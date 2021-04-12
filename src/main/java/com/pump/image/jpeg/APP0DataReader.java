/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.jpeg;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This reads an APP0 block and passes information to a JPEGMetaDataListener.
 */
class APP0DataReader {

	public static enum Unit {
		NONE, DOTS_PER_INCH, DOTS_PER_CM
	}

	public static final String PROPERTY_VERSION_MAJOR = "version major";
	public static final String PROPERTY_VERSION_MINOR = "version minor";
	public static final String PROPERTY_UNITS = "units";
	public static final String PROPERTY_HORIZONTAL_DENSITY = "horizontal density";
	public static final String PROPERTY_VERTICAL_DENSITY = "vertical density";
	public static final String PROPERTY_THUMBNAIL_WIDTH = "thumbnail width";
	public static final String PROPERTY_THUMBNAIL_HEIGHT = "thumbnail height";

	public static void read(JPEGMarkerInputStream in,
			JPEGMetaDataListener listener) throws IOException {
		// TODO: also support JFXX ("JFIF Extention")
		// http://en.wikipedia.org/wiki/JPEG_File_Interchange_Format
		// the problem is: I can't find a single file that uses this.
		byte[] array = new byte[9];
		if (in.readFully(array, 5) != 5)
			throw new IOException("APP0 expected to begin with \"JFIF_\".");
		if (array[0] != 74 || array[1] != 70 || array[2] != 73 || array[3] != 70
				|| array[4] != 0)
			throw new IOException("APP0 expected to begin with \"JFIF_\".");
		if (in.readFully(array, 9) != 9) {
			throw new IOException("APP0 expected to at least 9 bytes of data.");
		}
		int versionMajor = array[0] & 0xff;
		int versionMinor = array[1] & 0xff;
		listener.addProperty(JPEGMarkerInputStream.APP0_MARKER,
				PROPERTY_VERSION_MAJOR, versionMajor);
		listener.addProperty(JPEGMarkerInputStream.APP0_MARKER,
				PROPERTY_VERSION_MINOR, versionMinor);

		listener.addProperty(JPEGMarkerInputStream.APP0_MARKER, PROPERTY_UNITS,
				Unit.values()[array[2] & 0xff]);

		int horizDensity = ((array[3] & 0xff) << 16) + (array[4] & 0xff);
		int vertDensity = ((array[5] & 0xff) << 16) + (array[6] & 0xff);
		listener.addProperty(JPEGMarkerInputStream.APP0_MARKER,
				PROPERTY_HORIZONTAL_DENSITY, horizDensity);
		listener.addProperty(JPEGMarkerInputStream.APP0_MARKER,
				PROPERTY_VERTICAL_DENSITY, vertDensity);

		int thumbnailWidth = array[7] & 0xff;
		int thumbnailHeight = array[8] & 0xff;
		listener.addProperty(JPEGMarkerInputStream.APP0_MARKER,
				PROPERTY_THUMBNAIL_WIDTH, thumbnailWidth);
		listener.addProperty(JPEGMarkerInputStream.APP0_MARKER,
				PROPERTY_THUMBNAIL_HEIGHT, thumbnailHeight);
		if (thumbnailWidth * thumbnailHeight > 0) {
			if (listener.isThumbnailAccepted(JPEGMarkerInputStream.APP0_MARKER,
					thumbnailWidth, thumbnailHeight)) {
				// TODO: test this. I haven't found a single file that uses
				// an APP0 thumbnail, so this code has never been tested.
				byte[] dataByte = new byte[thumbnailWidth * 3];
				int[] dataInt = new int[thumbnailWidth];
				in.readFully(dataByte, dataByte.length);
				BufferedImage image = new BufferedImage(thumbnailWidth,
						thumbnailHeight, BufferedImage.TYPE_INT_RGB);
				for (int y = 0; y < thumbnailHeight; y++) {
					for (int x = 0; x < thumbnailWidth; x++) {
						int r = (dataByte[x * 3] & 0xff);
						int g = (dataByte[x * 3 + 1] & 0xff);
						int b = (dataByte[x * 3 + 2] & 0xff);
						dataInt[x] = (r << 16) + (g << 8) + (b);
					}
					image.getRaster().setDataElements(0, y, thumbnailWidth, 1,
							array);
				}
				listener.addThumbnail(JPEGMarkerInputStream.APP0_MARKER, image);
			}
		}
	}
}