/*
 * @(#)GifBlock.java
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

import java.io.IOException;
import java.io.OutputStream;

import com.pump.image.gif.GifConstants;

/**
 * A <code>GifBlock</code> is the most fundamental object a GIF can be broken
 * down into.
 * <P>
 * These blocks are all carefully detailed in the GIF file format specification,
 * and the <code>GifBlock</code>'s in this package [should] follow those
 * descriptions to the letter.
 */
public abstract class GifBlock implements GifConstants {
	public static final int IMAGE_DESCRIPTOR = 0;
	public static final int LOCAL_COLOR_TABLE = 1;
	public static final int IMAGE_DATA = 2;
	public static final int GRAPHIC_CONTROL_EXTENSION = 3;
	public static final int COMMENT_EXTENSION = 4;
	public static final int PLAIN_TEXT_EXTENSION = 5;
	public static final int APPLICATION_EXTENSION = 6;
	public static final int GLOBAL_COLOR_TABLE = 7;
	public static final int LOGICAL_SCREEN_DESCRIPTOR = 8;
	public static final int HEADER = 9;
	public static final int TRAILER = 10;

	/**
	 * This outputs the exact byte representation of this block.
	 * <P>
	 * A series of these byte arrays combined can create a GIF file.
	 */
	public abstract byte[] getBytes();

	public abstract int getByteCount();

	/**
	 * This concatenates two byte arrays together, in the order provided.
	 * 
	 * @param b1
	 *            an array of bytes
	 * @param b2
	 *            a second array of bytes
	 * @return a new array that contains <code>[b1] + [b2]</code>
	 */
	protected static byte[] concatenate(byte[] b1, byte[] b2) {
		byte[] t = new byte[b1.length + b2.length];
		System.arraycopy(b1, 0, t, 0, b1.length);
		System.arraycopy(b2, 0, t, b1.length, b2.length);
		return t;
	}

	/** Writes this GIF block to an output stream. 
	 * @throws IOException if an IO problem occurs.
	 */
	public void write(OutputStream out) throws IOException {
		byte[] b = getBytes();
		out.write(b, 0, b.length);
	}

	/**
	 * Creates a large block of data as a series of GIF sub-blocks, followed by
	 * a sub-block terminator of <code>0x00</code>.
	 * <P>
	 * A GIF sub-block is defined as follows: <BR>
	 * <code> &nbsp; &nbsp; 7 6 5 4 3 2 1 0  &nbsp; Field Name &nbsp; &nbsp; &nbsp; Type</code>
	 * <BR>
	 * <code> &nbsp; &nbsp; +---------------+</code> <BR>
	 * <code> &nbsp; 0  | &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; | Block Size &nbsp; &nbsp; &nbsp; Byte</code>
	 * <BR>
	 * <code> &nbsp; &nbsp; +---------------+</code> <BR>
	 * <code> &nbsp; 1  | &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |</code> <BR>
	 * <code> &nbsp; &nbsp; +- &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; -+</code> <BR>
	 * <code> &nbsp; 2  | &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |</code> <BR>
	 * <code> &nbsp; &nbsp; +- &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; -+</code> <BR>
	 * <code> &nbsp; 3  | &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |</code> <BR>
	 * <code> &nbsp; &nbsp; +- &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; -+</code> <BR>
	 * <code> &nbsp; &nbsp; | &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; | Data Values &nbsp; &nbsp; Byte</code>
	 * <BR>
	 * <code> &nbsp; &nbsp; +- &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; +</code>
	 * <BR>
	 * <code>up  &nbsp;| &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |</code> <BR>
	 * <code> &nbsp; &nbsp; +-   &nbsp;&nbsp;. . . .  &nbsp; -+</code> <BR>
	 * <code>to  &nbsp;| &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |</code> <BR>
	 * <code> &nbsp; &nbsp; +- &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; -+</code> <BR>
	 * <code> &nbsp; &nbsp; | &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |</code>
	 * <BR>
	 * <code> &nbsp; &nbsp; +- &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; -+</code> <BR>
	 * <code>255  | &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; |</code> <BR>
	 * <code> &nbsp; &nbsp; &nbsp;+---------------+</code> <BR>
	 * 
	 * @param data
	 *            a large chunk of data
	 * @return a series of sub-blocks, followed by a terminator byte
	 */
	protected static byte[] writeSubBlocks(byte[] data) {
		int numBlocks = data.length / 255;
		if (numBlocks * 255 == data.length) {
			// a perfect match! Woohoo!
		} else {
			numBlocks++;
		}
		byte[] newArray = new byte[numBlocks + data.length + 1];
		int dataOffset = 0;
		int newOffset = 0;
		int remainingBytes;
		for (int a = 0; a < numBlocks; a++) {
			remainingBytes = Math.min(data.length - dataOffset, 255);
			newArray[newOffset] = (byte) remainingBytes;
			newOffset++;
			System.arraycopy(data, dataOffset, newArray, newOffset,
					remainingBytes);
			newOffset += remainingBytes;
			dataOffset += remainingBytes;
		}
		newArray[newOffset] = 0; // block terminator
		return newArray;
	}
}
