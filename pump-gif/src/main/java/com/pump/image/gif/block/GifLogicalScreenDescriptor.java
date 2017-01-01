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
package com.pump.image.gif.block;

import java.awt.Dimension;

public class GifLogicalScreenDescriptor extends GifBlock {
	byte[] b;

	public GifLogicalScreenDescriptor(Dimension d, int globalTableSize) {
		this(d.width, d.height, globalTableSize);
	}

	public GifLogicalScreenDescriptor(int w, int h, int globalTableSize) {
		b = new byte[7];
		setWidth(w);
		setHeight(h);
		b[4] = (byte) (0xFF);
		setGlobalColorTableSize(globalTableSize);
		setSorted(true);
		b[6] = 0;
	}

	/**
	 * Set the global color table size. Set this value to 0 or -1 to indicate
	 * that there is not global color table following this block.
	 * 
	 * @param globalTableSize
	 *            this must be a power of 2, that is not greater than 256. Use
	 *            zero or -1 to indicate that there is no global color table.
	 */
	public void setGlobalColorTableSize(int globalTableSize) {
		if (globalTableSize > 256)
			throw new IllegalArgumentException(
					"The size of the global color table (" + globalTableSize
							+ ") must not be greater than 256.");
		if (globalTableSize > 0) {
			int i = 0;
			int p = globalTableSize;
			while (p > 2) {
				p = p / 2;
				i++;
			}
			if (p != 2)
				throw new IllegalArgumentException(
						"The size of the global color table ("
								+ globalTableSize
								+ ") must be an exact power of 2.");
			b[4] = (byte) ((b[4] & 0xF8) + i);
			b[4] = (byte) ((0x80) + (b[4] & 0x7F)); // indicate we DO have a
													// global color table

		} else {
			b[4] = (byte) ((0 << 7) + (b[4] & 0x7F));
		}
	}

	public void setWidth(int w) {
		if (w < 1 || w > 65535)
			throw new IllegalArgumentException("The width (" + w
					+ ") must be between 1 and 65535");

		b[0] = (byte) (w % 256);
		b[1] = (byte) (w / 256);
	}

	public void setHeight(int h) {
		if (h < 1 || h > 65535)
			throw new IllegalArgumentException("The height (" + h
					+ ") must be between 1 and 65535");

		b[2] = (byte) (h % 256);
		b[3] = (byte) (h / 256);
	}

	/**
	 * Creates a logical screen descriptor block from a 7-byte array.
	 */
	protected GifLogicalScreenDescriptor(byte[] array) {
		this.b = array;
		if (b.length != 7)
			throw new IllegalArgumentException(
					"The logical screen descriptor for a GIF must be 7 bytes in length.");
	}

	/**
	 * @return the width, in pixels, of the Logical Screen where the images will
	 *         be rendered in the displaying device.
	 */
	public int getWidth() {
		return (b[0] & 0xFF) + (b[1] & 0xFF) * 256;
	}

	/**
	 * @return the height, in pixels, of the Logical Screen where the images
	 *         will be rendered in the displaying device.
	 */
	public int getHeight() {
		return (b[2] & 0xFF) + (b[3] & 0xFF) * 256;
	}

	/**
	 * @return <code>true</code>if a
	 *          {@link com.bric.image.gif.block.GifGlobalColorTable} follows this
	 *          block in the GIF file.
	 */
	public boolean hasGlobalColorTable() {
		return (b[4] & 0x80) > 0;
	}

	/**
	 * @return <code>true</code> if the
	 *          {@link com.bric.image.gif.block.GifGlobalColorTable} is sorted. If
	 *          <code>hasGlobalColorTable()</code> is <code>false</code>,
	 *          then this also returns <code>false</code>.
	 *          <P>
	 *          This is not really of modern use. The original GIF specification
	 *          states the following:
	 *          <P>
	 *          "If this is <code>true</code>, the Global Color Table is
	 *          sorted, in order of decreasing importance Typically, the order
	 *          would be decreasing frequency, with most frequent color first.
	 *          This assists a decoder, with fewer available colors, in choosing
	 *          the best subset of colors; the decoder may use an initial
	 *          segment of the table to render the graphic."
	 */
	public boolean isSorted() {
		if (hasGlobalColorTable() == false)
			return false;
		return (b[4] & 0x08) > 0;
	}

	/**
	 * This indicates whether the global color table (if it exists) is sorted by
	 * decreasing color frequency.
	 * 
	 * @param b
	 *            whether the global color table is sorted.
	 */
	public void setSorted(boolean b) {
		int k = 0;
		if (b)
			k = 1;

		this.b[4] = (byte) ((this.b[4] & 0xF7) + (k << 3));
	}

	/**
	 * This value represents the bits per pixel from the original source image,
	 * not the number of colors actually used in the graphic.
	 * <P>
	 * This value indicates the richness of the original palette, even if not
	 * every color from the whole palette is available on the source machine.
	 */
	public int getSourceColorResolution() {
		int i = b[4] >> 4;
		i = i & 0x03;
		i++;
		return i;
	}

	/**
	 * @return the number of entries in the global color table.
	 */
	public int getGlobalColorTableSize() {
		int i = (b[4] & 0x07);
		int p = 2;
		while (i > 0) {
			p = p * 2;
			i--;
		}
		return p;
	}

	public byte[] getBytes() {
		byte[] d = new byte[b.length];
		System.arraycopy(b, 0, d, 0, b.length);

		return d;
	}

	public int getByteCount() {
		return b.length;
	}

	/**
	 * Index into the {@link com.bric.image.gif.block.GifGlobalColorTable} for the
	 * Background Color. The Background Color is the color used for those pixels
	 * on the screen that are not covered by an image.
	 */
	public int getBackgroundColorIndex() {
		return (b[5] & 0xFF);
	}

	public void setBackgroundColorIndex(int i) {
		b[5] = (byte) i;
	}

	/**
	 * @return the ratio of horizontal to vertical pixels in the source image.
	 *         This must be a value between .25 (meaning there was a ratio of
	 *         1:4) or 4.0 (meaning there was a ratio of 4:1)
	 */
	public float getAspectRatio() {
		int i = b[6] & 0xff;
		if (i == 0)
			return 1;

		float f = (i + 15f) / 64f;
		return f;
	}
}