/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
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

	public enum Unit {
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
		String marker = JPEGMarker.APP0_MARKER.getByteCode();
		listener.addProperty(marker, PROPERTY_VERSION_MAJOR, versionMajor);
		listener.addProperty(marker, PROPERTY_VERSION_MINOR, versionMinor);

		listener.addProperty(marker, PROPERTY_UNITS,
				Unit.values()[array[2] & 0xff]);

		int horizDensity = ((array[3] & 0xff) << 16) + (array[4] & 0xff);
		int vertDensity = ((array[5] & 0xff) << 16) + (array[6] & 0xff);
		listener.addProperty(marker, PROPERTY_HORIZONTAL_DENSITY, horizDensity);
		listener.addProperty(marker, PROPERTY_VERTICAL_DENSITY, vertDensity);

		int thumbnailWidth = array[7] & 0xff;
		int thumbnailHeight = array[8] & 0xff;
		listener.addProperty(marker, PROPERTY_THUMBNAIL_WIDTH, thumbnailWidth);
		listener.addProperty(marker, PROPERTY_THUMBNAIL_HEIGHT,
				thumbnailHeight);
		if (thumbnailWidth * thumbnailHeight > 0) {
			// This is probably a rare and old-fashioned way to embed a thumbnail,
			// but I'm not sure how to prove/quantify this claim.
			if (listener.isThumbnailAccepted(marker, thumbnailWidth,
					thumbnailHeight)) {
				byte[] dataByte;
				BufferedImage image;

				// We mod by 65536 because apparently APP0 segments can just keep running as long as they need to
				// to fill the given RGB table. I don't see this mentioned in the JFIF specs, but I observe it
				// in my unit tests.
				if (in.remainingMarkerLength == (thumbnailHeight * thumbnailWidth * 4) % 65536 ) {
					// The com.sun.imageio.plugins.jpeg.JPEGImageWriter implementation will write ARGB thumbnails
					// and include the alpha channels. I'm not sure if this is a common convention, but we should
					// support that choice since we ought to support Java-written JPEGs:
					dataByte = new byte[thumbnailHeight * thumbnailWidth * 4];
					in.readFully(dataByte, dataByte.length, true);
					image = new BufferedImage(thumbnailWidth,
							thumbnailHeight, BufferedImage.TYPE_4BYTE_ABGR);
				} else if (in.remainingMarkerLength == (thumbnailHeight * thumbnailWidth * 3) % 65536) {
					// this is the more traditional/predictable case:
					dataByte = new byte[thumbnailHeight * thumbnailWidth * 3];
					in.readFully(dataByte, dataByte.length, true);
					image = new BufferedImage(thumbnailWidth,
							thumbnailHeight, BufferedImage.TYPE_3BYTE_BGR);
				} else {
					throw new RuntimeException("Unexpected marker length. Thumbnail size: " + thumbnailWidth + "x" + thumbnailHeight + ", remaining bytes: " + in.remainingMarkerLength);
				}
				image.getRaster().setDataElements(0,0,thumbnailWidth,thumbnailHeight, dataByte);
				listener.addThumbnail(marker, image);
			}
		}
	}
}