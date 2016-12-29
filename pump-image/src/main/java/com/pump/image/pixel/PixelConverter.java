/*
 * @(#)PixelConverter.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
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
