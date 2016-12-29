/*
 * @(#)NearestNeighborImageQuantization.java
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
package com.pump.image.pixel.quantize;

import java.awt.image.BufferedImage;

import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.IndexedBytePixelIterator;
import com.pump.image.pixel.IntARGBConverter;


/** This implements image quantization using a nearest-neighbor approach.
 * <p>This was largely developed as a baseline to test against. Unless you
 * have a specific reason for seeking this: I strongly recommend using
 * {@link ErrorDiffusionImageQuantization} instead.
 */
public class NearestNeighborImageQuantization extends ImageQuantization {
	
	/** The pixel iterator that implements the nearest neighbor image quantization. */
	protected static class NearestNeighborIndexedBytePixelIterator extends AbstractIndexedBytePixelIterator {

		int[] incomingRow;
		IntARGBConverter iter;
		protected int y = 0;
		
		NearestNeighborIndexedBytePixelIterator(BufferedImage source, ColorLUT lut) {
			super(source, lut);
			incomingRow = new int[getWidth()];
			iter = new IntARGBConverter(BufferedImageIterator.get(source));
		}

		public void skip() {
			iter.skip();
			y++;
		}
		
		public void next(byte[] dest) {
			iter.next(incomingRow);
			
			if(isOpaque()) {
				
				for(int x = 0; x<iter.getWidth(); x++) {
					int r = (incomingRow[x] >> 16) & 0xff;
					int g = (incomingRow[x] >> 8) & 0xff;
					int b = (incomingRow[x] >> 0) & 0xff;
					
					int index = lut.getIndexMatch(r, g, b);
					
					dest[x] = (byte)( index );
				}
			} else {
				int t = icm.getTransparentPixel();
				for(int x = 0; x<iter.getWidth(); x++) {
					int index;
					int a = (incomingRow[x] >> 24) & 0xff;
					if(a<128) {
						index = t;
					} else {
						int r = (incomingRow[x] >> 16) & 0xff;
						int g = (incomingRow[x] >> 8) & 0xff;
						int b = (incomingRow[x] >> 0) & 0xff;
						
						index = lut.getIndexMatch(r, g, b);
					}
					
					dest[x] = (byte)( index );
				}
			}
			y++;
		}

		public boolean isDone() {
			return y == getHeight();
		}
	}
	
	
	@Override
	public IndexedBytePixelIterator createImageData(BufferedImage source,
			ColorLUT colorLUT) {
		return new NearestNeighborIndexedBytePixelIterator(source, colorLUT);
	}
}
