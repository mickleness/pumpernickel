/*
 * @(#)GifLoopingApplicationExtension.java
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

import java.util.Arrays;

/**
 * This is a special block that indicates how many times a GIF should loop.
 * <P>
 * This is not defined in the GIF file format, but has become so standard that
 * it might as well be. This is just an application extension with the header of
 * "NETSCAPE2.0". The body of this block indicates how many loops should occur;
 * note that in most cases this value will be <code>LOOPS_FOREVER</code>.
 */
public class GifLoopingApplicationExtension extends GifApplicationExtension {
	protected static final byte[] NETSCAPE_HEADER = new byte[] { 0x4e, 0x45,
			0x54, 0x53, 0x43, 0x41, 0x50, 0x45, 0x32, 0x2e, 0x30 };

	public static final int MAX_LOOP_COUNT = 65535;

	/**
	 * Creates a looping extension.
	 * 
	 * @param loopCount
	 *            the number of times this should loop. If this is greater than
	 *            65535, then this value is changed to <code>LOOP_FOREVER</code>.
	 */
	public GifLoopingApplicationExtension(int loopCount) {
		this(NETSCAPE_HEADER, writeLoopCount(loopCount));
		if (loopCount > 65535)
			loopCount = 0;
		if (loopCount < 0 || loopCount > 65535)
			throw new IllegalArgumentException(
					"The looping value must be between 0 and 65535  Illegal value = "
							+ loopCount + ".");
	}

	/**
	 * Writes the number loops as a 3-byte block of data.
	 * 
	 * @param i
	 *            the number of loops
	 * @return data that can go directly into a GIF file.
	 */
	private static byte[] writeLoopCount(int i) {
		if (i > 65535)
			i = 0;
		byte b1 = (byte) (i % 256);
		byte b2 = (byte) (i / 256);

		byte[] b = new byte[] { 0x01, b1, b2 };
		return b;
	}

	/**
	 * This constructor is not recommended for this object, since the bytes have
	 * to match up perfectly for this to be a valid looping block.
	 * 
	 * @param header
	 *            This must be "NETSCAPE2.0"
	 * @param data
	 *            This must be 0x01 followed by 2 bytes, representing an
	 *            unsigned int.
	 */
	protected GifLoopingApplicationExtension(byte[] header, byte[] data) {
		super(header, data);
		if (Arrays.equals(header, NETSCAPE_HEADER) == false)
			throw new IllegalArgumentException(
					"This is not a looping extension, the application code should read \"NETSCAPE2.0\".");
		if (data.length != 3)
			throw new IllegalArgumentException(
					"A looping extension must have a data block of 3 bytes.  (Illegal value of "
							+ data.length + ")");
		if (data[0] != 0x01)
			throw new IllegalArgumentException(
					"This block requires the first data byte be 0x01.  Any other value is not used for looping.");
	}

	public int getLoopCount() {
		int i = (data[2] & 0xFF) * 256 + (data[1] & 0xFF);
		return i;
	}

	public void setLoopCount(int i) {
		if (i > 65535 || i < 0)
			throw new IllegalArgumentException(
					"This value must be between 0 and 65535.");
		data[2] = (byte) (i / 256);
		data[1] = (byte) (i % 256);
	}
}
