/*
 * @(#)ImageQuantization.java
 *
 * $Date: 2014-03-23 20:20:06 -0400 (Sun, 23 Mar 2014) $
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
import java.awt.image.IndexColorModel;

import com.pump.image.pixel.IndexedBytePixelIterator;


/** This is an algorithm that converts an image with thousands or millions
 * of colors into a simpler image with a fixed color palette.
 */
public abstract class ImageQuantization {
	

	protected abstract  static class AbstractIndexedBytePixelIterator implements IndexedBytePixelIterator {

		protected BufferedImage source;
		protected ColorLUT lut;
		protected IndexColorModel icm;
		
		AbstractIndexedBytePixelIterator(BufferedImage source, ColorLUT lut) {
			this.source = source;
			this.lut = lut;
			icm = lut.getIndexColorModel();
		}

		public int getType() {
			return BufferedImage.TYPE_BYTE_INDEXED;
		}

		public boolean isOpaque() {
			return icm.getTransparentPixel()<0;
		}

		public int getPixelSize() {
			return 1;
		}

		public boolean isTopDown() {
			return true;
		}

		public int getWidth() {
			return source.getWidth();
		}

		public int getHeight() {
			return source.getHeight();
		}

		public int getMinimumArrayLength() {
			return getWidth();
		}

		public IndexColorModel getIndexColorModel() {
			return icm;
		}
		
	}
	
	/** Create a copy of an image using a reduced color palette.
	 * <P>This uses the {@link BiasedMedianCutColorQuantization} and
	 * {@link #MEDIUM_DIFFUSION}.
	 * 
	 * @param src the image to reduce the palette of.
	 * @param maxColors the maximum number of allowed colors.
	 * @return a new reduced image.
	 */
	public static BufferedImage reduce(BufferedImage src,int maxColors) {
		ColorSet set = new ColorSet(src);
		BiasedMedianCutColorQuantization b = new BiasedMedianCutColorQuantization();
		set = b.createReducedSet(set, maxColors, false);
		ColorLUT lut = new ColorLUT( set.createIndexColorModel(false, false) );
		return MOST_DIFFUSION.createImage(src, lut);
	}
	
	/** This simply identifies the closest approximation of every color. No
	 * dithering or diffusion here. The diffusion algorithms will offer much
	 * better results.
	 */
	public static ImageQuantization NEAREST_NEIGHBOR = new NearestNeighborImageQuantization() {
		@Override
		public String toString() {
			return "NEAREST_NEIGBHOR";
		}
	};

	/** This uses a 2x2 kernel for error diffusion. Sometimes this is simply referred to as 
	 * "Two-dimensional error diffusion". The kernel is:
	 * <pre>[ [ 0, 2],
	 *  [ 1, 1] ]</pre>
	 *  <p>The designation as "simplest" is a naming oversimplification: error diffusion is
	 *  not limited to exactly 3 tiers ("simplest", "medium" and "most"). But for the sake
	 *  of casual comparison it seemed like a more helpful name.</p>
	 */
	public static ImageQuantization SIMPLEST_DIFFUSION = new ErrorDiffusionImageQuantization(new int[][] {{0, 2}, {1, 1}}) {
		@Override
		public String toString() {
			return "SIMPLEST_DIFFUSION";
		}
	};
	
	/** This uses a 2x3 kernel (Floyd and Steinberg) for error diffusion. The kernel is:
	 * <pre>[ [ 0, 0, 7],
	 *  [ 3, 5, 1] ]</pre>
	 *  <p>The designation as "medium" is a naming oversimplification: error diffusion is
	 *  not limited to exactly 3 tiers ("simplest", "medium" and "most"). But for the sake
	 *  of casual comparison it seemed like a more helpful name.</p>
	 */
	public static ImageQuantization MEDIUM_DIFFUSION = new ErrorDiffusionImageQuantization(new int[][] {{0, 0, 7}, {3, 5, 1}}) {
		@Override
		public String toString() {
			return "MEDIUM_DIFFUSION";
		}
	};
	
	/** This uses a 3x5 kernel (Bell Labs) for error diffusion. The kernel is:
	 * <pre>[ [ 0, 0, 0, 7, 5],
	 *  [ 3, 5, 7, 5, 3] ]
	 *  [ 1, 3, 5, 3, 1] ]</pre>
	 *  <p>The designation as "most" is a naming oversimplification: error diffusion is
	 *  not limited to exactly 3 tiers ("simplest", "medium" and "most"). But for the sake
	 *  of casual comparison it seemed like a more helpful name.</p>
	 */
	public static ImageQuantization MOST_DIFFUSION = new ErrorDiffusionImageQuantization(new int[][] {{0, 0, 0, 7, 5}, {3, 5, 7, 5, 3}, {1, 3, 5, 3, 1}}) {
		@Override
		public String toString() {
			return "MOST_DIFFUSION";
		}
	};
	
	/** Create a copy of the image argument using only the colors provided in the
	 * color look-up table argument.
	 * 
	 * @param source the image to downsample.
	 * @param colorLUT the new color table to use. This must be created from
	 * an IndexColorModel.
	 */
	public final BufferedImage createImage(BufferedImage source,ColorLUT colorLUT) {
		IndexColorModel icm = colorLUT.getIndexColorModel();
		if(icm==null) throw new NullPointerException();
		int width = source.getWidth();
		int height = source.getHeight();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, icm);
		byte[] row = new byte[width];
		IndexedBytePixelIterator iter = createImageData(source, colorLUT);
		int y = 0;
		while(!iter.isDone()) {
			iter.next(row);
			bi.getRaster().setDataElements(0,y,width,1,row);
			y++;
		}
		
		return bi;
	}

	
	/** Create a copy of the image argument using only the colors provided in the
	 * color look-up table argument.
	 * 
	 * @param source the image to downsample.
	 * @param colorLUT the new color table to use. This must be created from
	 * an IndexColorModel.
	 */
	public abstract IndexedBytePixelIterator createImageData(BufferedImage source,ColorLUT colorLUT);
}
