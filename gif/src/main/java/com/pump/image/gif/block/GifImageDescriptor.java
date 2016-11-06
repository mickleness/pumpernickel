/*
 * @(#)GifImageDescriptor.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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
package com.pump.image.gif.block;

/**
 * There must be <code>GifImageDescriptor</code> for each
 * {@link com.bric.image.gif.block.GifImageDataBlock}. This block will precede the
 * image data, but there <i>may</i> be a local color table immediately
 * following this block.
 * <P>
 * The GIF file format specification describes this block as follows:
 * <P>
 * "Each image in the Data Stream is composed of an Image Descriptor, an
 * optional Local Color Table, and the image data. Each image must fit within
 * the boundaries of the Logical Screen, as defined in the Logical Screen
 * Descriptor.
 * <P>
 * The Image Descriptor contains the parameters necessary to process a table
 * based image. The coordinates given in this block refer to coordinates within
 * the Logical Screen, and are given in pixels. This block is a
 * Graphic-Rendering Block, optionally preceded by one or more Control blocks
 * such as the Graphic Control Extension, and may be optionally followed by a
 * Local Color Table; the Image Descriptor is always followed by the image data.
 * <P>
 * This block is REQUIRED for an image. Exactly one Image Descriptor must be
 * present per image in the Data Stream. An unlimited number of images may be
 * present per Data Stream."
 */
public class GifImageDescriptor extends GifGraphicRenderingBlock {
	byte[] b;

	/**
	 * Creates a GifImageDescriptor from an array of 9 bytes (these bytes omit
	 * the 'image separator' byte.
	 * 
	 * @param b
	 *            this is NOT cloned
	 */
	protected GifImageDescriptor(byte[] b) {
		this.b = b;
	}

	/**
	 * This creates a GifImageDescriptor from the info provided.
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param interlaced
	 * @param localColorTableSize
	 */
	public GifImageDescriptor(int x, int y, int w, int h, boolean interlaced,
			int localColorTableSize) {
		b = new byte[9];
		setX(x);
		setY(y);
		setWidth(w);
		setHeight(h);
		setInterlaced(interlaced);
		setLocalColorTableSize(localColorTableSize);
	}

	public byte[] getBytes() {
		byte[] d = new byte[b.length + 1];
		d[0] = 0x2C;
		System.arraycopy(b, 0, d, 1, b.length);
		return d;
	}

	public int getByteCount() {
		return b.length + 1;
	}

	/**
	 * @return the left x-coordinate of this image
	 *         <P>
	 *         The GIF file format specification verbosely explains this as
	 *         follows:
	 *         <P>
	 *         "Column number, in pixels, of the left edge of the image, with
	 *         respect to the left edge of the Logical Screen. Leftmost column
	 *         of the Logical Screen is 0."
	 */
	public int getX() {
		return (b[1] & 0xFF) * 256 + (b[0] & 0xFF);
	}

	public void setX(int x) {
		if (x < 0 || x > 65535)
			throw new IllegalArgumentException("The x-coordinate (" + x
					+ ") must be between 0 and 65535");

		b[0] = (byte) (x % 256);
		b[1] = (byte) (x / 256);
	}

	public void setBounds(int x, int y, int w, int h) {
		setX(x);
		setY(y);
		setWidth(w);
		setHeight(h);
	}

	public void setY(int y) {
		if (y < 0 || y > 65535)
			throw new IllegalArgumentException("The y-coordinate (" + y
					+ ") must be between 0 and 65535");

		b[2] = (byte) (y % 256);
		b[3] = (byte) (y / 256);
	}

	public void setWidth(int w) {
		if (w < 1 || w > 65535)
			throw new IllegalArgumentException("The width (" + w
					+ ") must be between 1 and 65535");

		b[4] = (byte) (w % 256);
		b[5] = (byte) (w / 256);
	}

	public String toString() {
		return "GifImageDescriptor[x=" + getX() + " y=" + getY() + " width="
				+ getWidth() + " height=" + getHeight()
				+ " local color table size=" + getLocalColorTableSize()
				+ " has local color table=" + hasLocalColorTable()
				+ " interlaced=" + isInterlaced()
				+ " is local color table sorted=" + isLocalColorTableSorted()
				+ "]";
	}

	public void setHeight(int h) {
		if (h < 1 || h > 65535)
			throw new IllegalArgumentException("The height (" + h
					+ ") must be between 1 and 65535");

		b[6] = (byte) (h % 256);
		b[7] = (byte) (h / 256);
	}

	/**
	 * @return the upper y-coordinate of this image
	 *         <P>
	 *         The GIF file format specification verbosely explains this as
	 *         follows:
	 *         <P>
	 *         "Row number, in pixels, of the top edge of the image with respect
	 *         to the top edge of the Logical Screen. Top row of the Logical
	 *         Screen is 0."
	 */
	public int getY() {
		return (b[3] & 0xFF) * 256 + (b[2] & 0xFF);
	}

	/**
	 * @return the width of the image in pixels.
	 */
	public int getWidth() {
		return (b[5] & 0xFF) * 256 + (b[4] & 0xFF);
	}

	/**
	 * @return the height of the image in pixels.
	 */
	public int getHeight() {
		return (b[7] & 0xFF) * 256 + (b[6] & 0xFF);
	}

	/**
	 * @return <code>true</code> if this block is followed by a
	 *         {@link com.bric.image.gif.block.GifLocalColorTable}.
	 */
	public boolean hasLocalColorTable() {
		int i = b[8] & 0xFF;
		if ((i & 0x80) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * @return <code>true</code> if the image is interlaced. An image is
	 *         interlaced in a four-pass interlace pattern, described in
	 *         Appendix E of the GIF file format specification.
	 *         <P>
	 *         See {@link com.bric.image.gif.block.GifInterlace}.
	 */
	public boolean isInterlaced() {
		int i = b[8] & 0xFF;
		if ((i & 0x40) > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Indicates whether the upcoming image is interlaced.
	 * 
	 * @param b
	 *            whether interlacing is active or not.
	 */
	public void setInterlaced(boolean b) {
		int k = 0;
		if (b)
			k = 0x40;
		this.b[8] = (byte) ((this.b[8] & 0xF7) + k);
	}

	/**
	 * @return <code>true</code> if there is a
	 *         {@link com.bric.image.gif.block.GifLocalColorTable} following this
	 *         block, and that color table is sorted in order of decreasing
	 *         importance.
	 */
	public boolean isLocalColorTableSorted() {
		return ((b[8] & 0x20) > 0);
	}

	public void setLocalColorTableSorted(boolean b) {
		if (b) {
			this.b[8] = (byte) ((this.b[8] & 0xDF) + 0x20);
		} else {
			this.b[8] = (byte) ((this.b[8] & 0xDF));
		}
	}

	/**
	 * Assigns the local color table size for this block. If you pass 0 or -1 to
	 * this method, this effectively turns off the local color table.
	 * <P>
	 * If <code>i</code> is 0 or -1, subsequent calls
	 * to <code>hasLocalColorTable()</code> will return <code>false</code>.
	 * @param size must be a power of 2 between 2 and 256,
	 * or else -1 or 0 to indicate that there is no local
	 * color table.
	 */
	public void setLocalColorTableSize(int size) {
		if (size == 0 || size == -1) {
			// turn it off
			b[8] = (byte) (b[8] & 0x7F);
		} else {
			int i = 0;
			int p = size;
			while (p > 2) {
				p = p / 2;
				i++;
			}
			if (p != 2)
				throw new IllegalArgumentException(
						"The size of the global color table (" + size
								+ ") must be an exact power of 2.");
			b[8] = (byte) ((b[8] & 0xF8) + i);

			// make sure it's on:
			b[8] = (byte) ((b[8] & 0x7F) + 0x80);
		}
	}

	/**
	 * @return the number of colors in the following
	 *         {@link com.bric.image.gif.block.GifLocalColorTable}.
	 */
	public int getLocalColorTableSize() {
		int i = b[8] & 0xFF;
		i = (i & 0x07);
		int p = 2;
		while (i > 0) {
			p = p * 2;
			i--;
		}
		return p;
	}
}
