/*
 * @(#)GifHeaderBlock.java
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
 * This is the first block in of data in a GIF file. This parser only supports a
 * header of "GIF89a" or "GIF87a".
 */
public class GifHeaderBlock extends GifBlock {
	byte[] b;

	/** Creates a "GIF89a" header */
	public GifHeaderBlock() {
		this.b = new byte[] { 'G', 'I', 'F', '8', '9', 'a' };
	}

	/**
	 * This constructs a <code>GifHeaderBlock</code> from 6 bytes. This throws
	 * exceptions if the array does not read "GIF89a" or "GIF87a".
	 * 
	 * @param b
	 *            this array is not cloned, it is referenced directly.
	 */
	protected GifHeaderBlock(byte[] b) {
		if (b.length != 6)
			throw new IllegalArgumentException(
					"A GIF header must be 6 bytes.  Illegal array length: "
							+ b.length);
		this.b = b;
		if (!(b[0] == 'G' && b[1] == 'I' && b[2] == 'F')) {
			throw new IllegalArgumentException(
					"The header of this input stream does not begin with \"GIF\".");
		}
		if (b[3] == '8' && b[4] == '9' && b[5] == 'a')
			return; // all's well
		if (b[3] == '8' && b[4] == '7' && b[5] == 'a')
			return; // we support this too

		System.err.println((b[3] & 0xFF) + ", " + (b[4] & 0xFF) + ", "
				+ (b[5] & 0xFF));
		String s = new String(b, 3, 3);
		throw new IllegalArgumentException(
				"Unrecognized GIF file format: \""
						+ s
						+ "\".  This decoder only supports \"89a\" or \"87a\" encoded GIF.");
	}

	public byte[] getBytes() {
		return b;
	}

	public int getByteCount() {
		return getBytes().length;
	}
}
