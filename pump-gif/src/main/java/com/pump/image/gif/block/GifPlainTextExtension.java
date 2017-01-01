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

import java.nio.charset.Charset;

/**
 * Rarely used in practice, this is a
 * {@link com.bric.image.gif.block.GifGraphicRenderingBlock} that displays mono-spaced text.
 * Unfortunately, this was designed in 1990, and only appears to support ASCII
 * 7-bit text: so no international characters.
 * <P>
 * The GIF file format specification describes this block as follows:
 * <P>
 * "The Plain Text Extension contains textual data and the parameters necessary
 * to render that data as a graphic, in a simple form.
 * <P>
 * The textual data will be encoded with the 7-bit printable ASCII characters.
 * Text data are rendered using a grid of character cells defined by the
 * parameters in the block fields. Each character is rendered in an individual
 * cell. The textual data in this block is to be rendered as mono-spaced
 * characters, one character per cell, with a best fitting font and size.
 */
public class GifPlainTextExtension extends GifGraphicRenderingBlock {
	byte[] data;
	byte[] text;

	protected GifPlainTextExtension(byte[] data, byte[] text) {
		this.data = data;
		this.text = text;
	}

	public byte[] getBytes() {
		byte[] d = new byte[data.length + 3];
		d[0] = 0x21;
		d[1] = 0x01;
		d[2] = 12;
		System.arraycopy(data, 0, d, 0, data.length);
		return d;
	}

	public int getByteCount() {
		return data.length + 3;
	}

	/** @return the left edge, in pixels, of this text box */
	public int getX() {
		return (data[0] & 0xFF) * 256 + (data[1] & 0xFF);
	}

	/** @return the top edge, in pixels, of this text box */
	public int getY() {
		return (data[2] & 0xFF) * 256 + (data[3] & 0xFF);
	}

	/** @return the width, in pixels, of this text box */
	public int getWidth() {
		return (data[4] & 0xFF) * 256 + (data[5] & 0xFF);
	}

	/** @return the height, in pixels, of this text box */
	public int getHeight() {
		return (data[6] & 0xFF) * 256 + (data[7] & 0xFF);
	}

	/** @return the width, in pixels, of each character. */
	public int getCharacterCellWidth() {
		return (data[8] & 0xFF);
	}

	/** @return the height, in pixels, of each character. */
	public int getCharacterCellHeight() {
		return (data[9] & 0xFF);
	}

	/**
	 * @return the index in the global color table that the text should be
	 *         rendered in.
	 */
	public int getForegroundColorIndex() {
		return (data[10] & 0xFF);
	}

	/**
	 * @return the index in the global color table that the background of each
	 *         cell should be rendered in.
	 */
	public int getBackgroundColorIndex() {
		return (data[11] & 0xFF);
	}

	/** @return the text this block should render */
	public String getText() {
		return new String(text, Charset.availableCharsets().get("US-ASCII"));
	}
}