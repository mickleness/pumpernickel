/*
 * @(#)ReverseBytePixelIterator.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
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
package com.pump.image.pixel;

import java.awt.image.BufferedImage;

/** This iterator swaps the order of color components.  So
 * if the components are stored in the order {r1, g1, b1, r2, g2, b2, ...}
 * then this will return arrays as {b1, g1, r1, b2, g2, r2, ...}.
 */
public class ReverseBytePixelIterator implements BytePixelIterator {

	BytePixelIterator i;
	final int bytesPerPixel, len, k;
	
	public ReverseBytePixelIterator(BytePixelIterator i) {
		this.i = i;

		bytesPerPixel = i.getPixelSize();
		len = i.getWidth()*bytesPerPixel;
		k = bytesPerPixel/2;
	}
	
	
	public void next(byte[] dest) {
		i.next(dest);
		if(bytesPerPixel==3) {
			for(int x = 0; x<len; x+=bytesPerPixel) {
				byte t = dest[x];
				dest[x] = dest[x+2];
				dest[x+2] = t;
			}
		} else if(bytesPerPixel==4) {
			for(int x = 0; x<len; x+=bytesPerPixel) {
				byte t = dest[x];
				dest[x] = dest[x+3];
				dest[x+3] = t;
				
				t = dest[x+1];
				dest[x+1] = dest[x+2];
				dest[x+2] = t;
			}
		} else if(bytesPerPixel==1) {
			return;
		} else {
			for(int x = 0; x<len; x+=bytesPerPixel) {
				for(int z = 0; z<k; z++) {
					byte t = dest[x+z];
					dest[x+z] = dest[x+bytesPerPixel-1-z];
					dest[x+bytesPerPixel-1-z] = t;
				}
			}
		}
	}

	public void skip() {
		i.skip();
	}

	public int getHeight() {
		return i.getHeight();
	}

	public int getMinimumArrayLength() {
		return i.getMinimumArrayLength();
	}

	public int getPixelSize() {
		return i.getPixelSize();
	}

	public int getType() {
		return BufferedImage.TYPE_CUSTOM;
	}

	public int getWidth() {
		return i.getWidth();
	}

	public boolean isDone() {
		return i.isDone();
	}

	public boolean isOpaque() {
		return i.isOpaque();
	}

	public boolean isTopDown() {
		return i.isTopDown();
	}
}
