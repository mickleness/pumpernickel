/*
 * @(#)GifTrailerBlock.java
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
 * This 1-byte block identifies the end of a GIF file.
 * <P>It's value is simply <code>"0x3B"</code>.
 */
public class GifTrailerBlock extends GifBlock {

	public byte[] getBytes() {
		return new byte[] { 0x3B };
	}

	public int getByteCount() {
		return 1;
	}

}
