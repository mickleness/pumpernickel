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

/**
 * This contains textual information which is not part of the actual graphics in
 * the GIF Data Stream. It is suitable for including comments about the
 * graphics, credits, descriptions or any other type of non-control and
 * non-graphic data.
 */
public class GifCommentExtension extends GifExtensionBlock {
	String s;

	public GifCommentExtension(String s) {
		setText(s);
	}

	/**
	 * @return the text this comment contains.
	 */
	public String getText() {
		return s;
	}

	/**
	 * Assigns the text this comment contians.
	 * 
	 * @param text
	 *            this must be ASCII-compatible. No characters may have an
	 *            integer value greater than 255.
	 */
	public void setText(String text) {
		for (int a = 0; a < text.length(); a++) {
			char c = text.charAt(a);
			if (c > 256)
				throw new IllegalArgumentException("The character \"" + c
						+ "\" is not ASCII-compatible.");
		}
		this.s = text;
	}

	public byte[] getBytes() {
		byte[] d = s.getBytes();
		byte[] b = new byte[2];
		b[0] = 0x21;
		b[1] = (byte) 0xFE;
		return concatenate(b, writeSubBlocks(d));
	}

	public int getByteCount() {
		return getBytes().length;
	}
}