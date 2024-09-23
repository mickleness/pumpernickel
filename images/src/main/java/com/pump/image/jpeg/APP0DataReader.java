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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
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
			throw new IOException("APP0 expected to begin with \"JFIF_\" or \"JFXX_\".");
		if (array[0] == 74 && array[1] == 70 && array[2] == 73 && array[3] == 70 && array[4] == 0) {
			readAPP0JFIF(in, listener);
		} else if (array[0] == 74 && array[1] == 70 && array[2] == 88 && array[3] == 88 && array[4] == 0) {
			readAPP0JFIFExtension(in, listener);
		} else {
			// we may just instead skip this condition? Does this ever come up in the real world?
			throw new IOException("APP0 expected to begin with \"JFIF_\", or \"JFXX_\", not \"" + (new String(array, 0, 5)) + "\".");
		}
	}

	private static void readAPP0JFIF(JPEGMarkerInputStream in,
									 JPEGMetaDataListener listener) throws IOException {
		byte[] array = new byte[9];
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
				BufferedImage image = readUncompressedImage(in, thumbnailWidth, thumbnailHeight);
				listener.addThumbnail(marker, image);
			}
		}
	}

	private static void readAPP0JFIFExtension(JPEGMarkerInputStream in,
									 JPEGMetaDataListener listener) throws IOException {
		// this is based on https://www.w3.org/Graphics/JPEG/jfif3.pdf

		String marker = JPEGMarker.APP0_MARKER.getByteCode();
		int extensionCode = in.read();
		if (extensionCode == 16) {
			// this is the only extension code I've tested against:

			byte[] thumbnailBytes = new byte[in.remainingMarkerLength];
			in.readFully(thumbnailBytes, thumbnailBytes.length);
			BufferedImage thumbnail = ImageIO.read(new ByteArrayInputStream(thumbnailBytes));
			if (listener.isThumbnailAccepted(marker, thumbnail.getWidth(), thumbnail.getHeight()))
				listener.addThumbnail(marker, thumbnail);
		} else if (extensionCode == 17) {
			// warning: this is untested.

			int thumbnailWidth = in.read() & 0xff;
			int thumbnailHeight = in.read() & 0xff;
			if (listener.isThumbnailAccepted(marker, thumbnailWidth, thumbnailHeight)) {

				int[] palette = new int[256];
				for (int a = 0; a < 256; a++) {
					palette[a] = ((in.read() & 0xff) << 0) + ((in.read() & 0xff) << 8) + ((in.read() & 0xff) << 16);
				}
				byte[] pixels = new byte[thumbnailWidth * thumbnailHeight];
				in.readFully(pixels, pixels.length);

				BufferedImage thumbnail = new BufferedImage(thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);
				int[] row = new int[thumbnailWidth];
				for (int y = 0; y < thumbnailHeight; y++) {
					for (int x = 0; x < thumbnailWidth; x++) {
						int pixelIndex = y * thumbnailWidth + x;
						int colorIndex = pixels[pixelIndex] & 0xff;
						row[x] = palette[colorIndex];
					}
					thumbnail.getRaster().setDataElements(0, y, thumbnailWidth, 1, row);
				}
				listener.addThumbnail(marker, thumbnail);
			}
		} else if (extensionCode == 19) {
			// warning: this is untested.

			int thumbnailWidth = in.read() & 0xff;
			int thumbnailHeight = in.read() & 0xff;
			if (listener.isThumbnailAccepted(marker, thumbnailWidth, thumbnailHeight)) {
				BufferedImage thumbnail = readUncompressedImage(in, thumbnailWidth, thumbnailHeight);
				listener.addThumbnail(marker, thumbnail);
			}
		} else {
			// if this is a JFIF v1.02 file I think the 3 extension codes above are the only officially
			// defined codes
			throw new UnsupportedOperationException("Unsupported extension code: " + extensionCode);
		}
	}

	private static BufferedImage readUncompressedImage(JPEGMarkerInputStream in, int thumbnailWidth, int thumbnailHeight) throws IOException {
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
		return image;
	}
}