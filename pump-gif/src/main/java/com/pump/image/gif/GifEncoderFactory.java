/*
 * @(#)GifEncoderFactory.java
 *
 * $Date: 2014-04-15 20:18:16 -0400 (Tue, 15 Apr 2014) $
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
package com.pump.image.gif;


/** A factory to create a {@link com.bric.image.gif.GifEncoder}.
 */
public class GifEncoderFactory {
	private static GifEncoderFactory factory = new GifEncoderFactory();
	
	/** Return the static factory. */
	public static GifEncoderFactory get() {
		return factory;
	}
	
	/** Set the static factory. */
	public static void set(GifEncoderFactory newFactory) {
		if(newFactory==null)
			throw new NullPointerException();
		factory = newFactory;
	}
	
	/** Create a GifEncoder. */
	public GifEncoder createEncoder() {
		return new BasicGifEncoder();
	}
}
