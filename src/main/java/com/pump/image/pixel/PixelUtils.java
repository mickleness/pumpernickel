package com.pump.image.pixel;

import java.awt.image.BufferedImage;

/**
 * This contains static helper methods related to image/pixel types.
 */
public class PixelUtils {

	/**
	 * Returns <code>true</code> if the image type corresponds to an opaque
	 * color model.
	 * <p>
	 * This returns false for TYPE_BYTE_INDEXED to err on the side of caution.
	 * <P>
	 * If an unexpected image type is encountered, this throws an
	 * IllegalArgumentException.
	 * 
	 * @param imageType
	 *            a BufferedImage TYPE constant or a PixelIterator TYPE constant
	 */
	public static boolean isOpaque(int imageType) {

		switch (imageType) {

		// opaque:
		case ImageType.TYPE_3BYTE_RGB:
		case BufferedImage.TYPE_3BYTE_BGR:
		case BufferedImage.TYPE_BYTE_GRAY:
		case BufferedImage.TYPE_BYTE_BINARY:
		case BufferedImage.TYPE_INT_BGR:
		case BufferedImage.TYPE_INT_RGB:
		case BufferedImage.TYPE_USHORT_555_RGB:
		case BufferedImage.TYPE_USHORT_565_RGB:
		case BufferedImage.TYPE_USHORT_GRAY:
			return true;

		// may have alpha
		case BufferedImage.TYPE_BYTE_INDEXED:
			return false;

		// definitely has alpha

		case ImageType.TYPE_4BYTE_ARGB:
		case ImageType.TYPE_4BYTE_ARGB_PRE:
		case ImageType.TYPE_4BYTE_BGRA:
		case BufferedImage.TYPE_4BYTE_ABGR:
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_INT_ARGB_PRE:
			return false;

		default:
			throw new IllegalArgumentException(
					"Unrecognized image type: " + imageType);
		}
	}

	/**
	 * Return the number of array elements required to encode a pixel in a given
	 * format.
	 * <P>
	 * If an unexpected image type is encountered, this throws an
	 * IllegalArgumentException.
	 * 
	 * @param imageType
	 *            a BufferedImage TYPE constant or a PixelIterator TYPE constant
	 * @return the number of bytes required to encode a pixel. This is usually
	 *         between 1 and 4.
	 */
	public static int getPixelSize(int imageType) {
		switch (imageType) {

		// bytes:
		case ImageType.TYPE_3BYTE_RGB:
		case BufferedImage.TYPE_3BYTE_BGR:
			return 3;
		case BufferedImage.TYPE_BYTE_INDEXED:
		case BufferedImage.TYPE_BYTE_GRAY:
			return 1;
		case ImageType.TYPE_4BYTE_BGRA:
		case ImageType.TYPE_4BYTE_ARGB:
		case ImageType.TYPE_4BYTE_ARGB_PRE:
		case BufferedImage.TYPE_4BYTE_ABGR:
		case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			return 4;

		// ints:
		case BufferedImage.TYPE_INT_ARGB:
		case BufferedImage.TYPE_INT_ARGB_PRE:
		case BufferedImage.TYPE_INT_BGR:
		case BufferedImage.TYPE_INT_RGB:
			return 1;

		default:
			throw new IllegalArgumentException(
					"Unrecognized image type: " + imageType);
		}
	}

}
