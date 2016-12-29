/*
 * @(#)GifCommentExtension.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
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
