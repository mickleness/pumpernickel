/*
 * @(#)ImageBounds.java
 *
 * $Date: 2014-04-14 02:05:51 -0400 (Mon, 14 Apr 2014) $
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
package com.pump.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.pump.reflect.Reflection;
import com.pump.util.ResourcePool;

public class ImageBounds {
	
	/** Returns the smallest rectangle enclosing the pixels in this
	 * image that are non-translucent.
	 * 
	 * 
	 * @param bi a TYPE_INT_ARGB image.
	 * @return the smallest rectangle enclosing the pixels in this
	 * image that are non-translucent.
	 */
	public static Rectangle getBounds(BufferedImage bi) {
		int type = bi.getType();
		if(type==BufferedImage.TYPE_INT_ARGB) {
			return getARGBBounds(bi);
		}
		throw new IllegalArgumentException("Illegal image type ("+Reflection.nameStaticField(BufferedImage.class, new Integer(type)));
	}
	
	static int THRESHOLD = 125;
	
	private static Rectangle getARGBBounds(BufferedImage bi) {
		int[] array = ResourcePool.get().getIntArray(bi.getWidth());
		try {
			int h = bi.getHeight();
			int w = bi.getWidth();
			int minX = -1;
			int maxX = -1;
			int minY = -1;
			int maxY = -1;
			
			findMinY : for(int y = 0; y<h; y++) {
				bi.getRaster().getDataElements(0, y, w, 1, array);
				for(int x = 0; x<w; x++) {
					int alpha = (array[x] >> 24) & 0xff;
					if(alpha>THRESHOLD) {
						minX = x;
						maxX = x;
						minY = y;
						break findMinY;
					}
				}
			}
			
			if(minY==-1)
				return null;
			
			findMaxY : for(int y = h-1; y>=0; y--) {
				bi.getRaster().getDataElements(0, y, w, 1, array);
				for(int x = 0; x<w; x++) {
					int alpha = (array[x] >> 24) & 0xff;
					if(alpha>THRESHOLD) {
						minX = (x<minX) ? x : minX;
						maxX = (x>maxX) ? x : maxX;
						maxY = y;
						break findMaxY;
					}
				}
			}
			
			for(int y = minY; y<=maxY; y++) {
				bi.getRaster().getDataElements(0, y, w, 1, array);
				minSearch : for(int x = 0; x<minX; x++) {
					int alpha = (array[x] >> 24) & 0xff;
					if(alpha>THRESHOLD) {
						minX = x;
						break minSearch;
					}
				}
				maxSearch : for(int x = w-1; x>maxX; x--) {
					int alpha = (array[x] >> 24) & 0xff;
					if(alpha>THRESHOLD) {
						maxX = x;
						break maxSearch;
					}
				}
			}
			
			return new Rectangle(minX, minY, maxX-minX+1, maxY-minY+1);
		} finally {
			ResourcePool.get().put(array);
		}
	}
}
