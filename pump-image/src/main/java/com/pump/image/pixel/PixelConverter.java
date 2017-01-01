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
package com.pump.image.pixel;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

/**
 * This is the abstract base class for most of the converters in this package.
 * 
 */
public abstract class PixelConverter implements PixelIterator {
	private final PixelIterator i;
	final BytePixelIterator byteIterator;
	final IntPixelIterator intIterator;
	final int originalType;
	final int width;
	final IndexColorModel indexModel;

	/**
	 * Returns <code>true</code> if the image type corresponds to an opaque
	 * color model.
	 * <P>
	 * If an unexpected image type is encountered, this returns true to err on
	 * the side of caution.
	 */
	public static boolean isOpaque(int imageType) {
		if (imageType == BufferedImage.TYPE_3BYTE_BGR
				|| imageType == PixelIterator.TYPE_3BYTE_RGB
				|| imageType == BufferedImage.TYPE_BYTE_BINARY
				|| imageType == BufferedImage.TYPE_BYTE_GRAY
				|| imageType == BufferedImage.TYPE_INT_BGR
				|| imageType == BufferedImage.TYPE_INT_RGB
				|| imageType == BufferedImage.TYPE_USHORT_555_RGB
				|| imageType == BufferedImage.TYPE_USHORT_565_RGB
				|| imageType == BufferedImage.TYPE_USHORT_GRAY)
			return true;
		return false;
	}

	public PixelConverter(PixelIterator i) {
		this.i = i;
		originalType = i.getType();
		width = i.getWidth();

		if (i instanceof IndexedBytePixelIterator) {
			IndexedBytePixelIterator ibpi = (IndexedBytePixelIterator)i;
			byteIterator = ibpi;
			indexModel = ibpi.getIndexColorModel();
			intIterator = null;
		} else if (i instanceof BytePixelIterator) {
			byteIterator = (BytePixelIterator) i;
			indexModel = null;
			intIterator = null;
		} else if (i instanceof IntPixelIterator) {
			intIterator = (IntPixelIterator) i;
			byteIterator = null;
			indexModel = null;
		} else {
			throw new IllegalArgumentException(
					"the converted iterator must be a BytePixelIterator or an IntPixelIterator (not a "
							+ i.getClass().getName() + ")");
		}
	}

	public boolean isOpaque() {
		return isOpaque(getType());
	}

	public int getHeight() {
		return i.getHeight();
	}

	public int getWidth() {
		return width;
	}

	public boolean isDone() {
		return i.isDone();
	}

	public boolean isTopDown() {
		return i.isTopDown();
	}

}