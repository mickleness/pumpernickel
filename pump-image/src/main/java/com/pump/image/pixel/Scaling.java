/*
 * @(#)Scaling.java
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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.pump.image.ImageSize;
import com.pump.image.bmp.BmpDecoderIterator;

/** This contains a few static methods for scaling BufferedImages
 * using the {@link com.bric.image.pixel.ScalingIterator}.
 * 
 * @see com.bric.geom.Dimension2D.scaleProportionally
 * @see <a href="http://javagraphics.blogspot.com/2010/06/images-scaling-down.html">http://javagraphics.blogspot.com/2010/06/images-scaling-down.html</a>
 */
public class Scaling {

	/** Scales the source image into the destination.
	 * 
	 * @param source the source image.
	 * @param dest the destination image.  This must be
	 * smaller than the source image or an exception
	 * will be thrown.  Also this need to be of
	 * type <code>BufferedImage.TYPE_INT_ARGB</code>.
	 */
	public static void scale(BufferedImage source,BufferedImage dest) {
		scale(source, dest, new Dimension(dest.getWidth(), dest.getHeight()));
	}

	/** Scales the source image to a new, smaller size.
	 * 
	 * @param source the source image.
	 * @param w the new width. This must
	 * be less than the width of the source image, or an
	 * exception will be thrown.
	 * @param h the new height. This must
	 * be less than the height of the source image, or an
	 * exception will be thrown.
	 * @return a new scaled image of type <code>BufferedImage.TYPE_INT_ARGB</code>.
	 */
	public static BufferedImage scale(BufferedImage source,int w,int h) {
		return scale(source, null, new Dimension(w, h));
	}

	/** Scales the source image to a new, smaller size.
	 * 
	 * @param source the source image.
	 * @param destSize the size of the new image.  This must
	 * be smaller than the size of the source image, or an
	 * exception will be thrown.
	 * @return a new scaled image of type <code>BufferedImage.TYPE_INT_ARGB</code>
	 * or <code>BufferedImage.TYPE_INT_RGB</code>.
	 */
	public static BufferedImage scale(BufferedImage source,Dimension destSize) {
		return scale(source, null, destSize);
	}

	/** Scales the source image file to a new, smaller size.
	 * 
	 * @param source the source image file.
	 * @param preferredType <code>TYPE_INT_RGB</code>, <code>TYPE_INT_ARGB</code>, <code>TYPE_3BYTE_BGR</code>, <code>TYPE_4BYTE_ABGR</code>.
	 * @param destSize the size of the new image.
	 * @return a new scaled image of type <code>BufferedImage.TYPE_INT_ARGB</code>
	 * or <code>BufferedImage.TYPE_INT_RGB</code>.
	 */
	public static BufferedImage scale(File source,int preferredType,Dimension destSize) {
		//NOTE: this method mirrors scale(URL, ...), so when you modify one: modify the other
		String pathLower = source.getAbsolutePath().toLowerCase();
		if(pathLower.endsWith(".bmp")) {
			try {
				PixelIterator iter = BmpDecoderIterator.get(source);
				PixelIterator scalingIter = destSize==null ? iter : ScalingIterator.get(iter, destSize.width, destSize.height);
				PixelIterator finalIter = scalingIter;
				if(preferredType==BufferedImage.TYPE_INT_ARGB || preferredType==BufferedImage.TYPE_INT_ARGB_PRE) {
					finalIter = new IntARGBConverter(scalingIter);
				} else if(preferredType==BufferedImage.TYPE_INT_RGB) {
					finalIter = new IntRGBConverter(scalingIter);
				} else if(preferredType==BufferedImage.TYPE_3BYTE_BGR) {
					finalIter = new ByteBGRConverter(scalingIter);
				} else if(preferredType==BufferedImage.TYPE_4BYTE_ABGR) {
					finalIter = new ByteBGRAConverter(scalingIter);
				} else {
					throw new IllegalArgumentException("unrecognized type: "+preferredType);
				}
				BufferedImage image = BufferedImageIterator.create(finalIter, null);
				return image;
			} catch(IOException e) {
				return null;
			}
		}
		Image image = Toolkit.getDefaultToolkit().createImage(source.getAbsolutePath());
		try {
			return scale( image, null, destSize);
		} finally {
			if(image!=null)
				image.flush();
		}
	}

	/** Scales the source image file to a new size.
	 * 
	 * @param source the source image file.
	 * @param preferredType <code>TYPE_INT_RGB</code>, <code>TYPE_INT_ARGB</code>, <code>TYPE_3BYTE_BGR</code>, <code>TYPE_4BYTE_ABGR</code>.
	 * @param destSize the size of the new image.
	 * @return a new scaled image of type <code>BufferedImage.TYPE_INT_ARGB</code>
	 * or <code>BufferedImage.TYPE_INT_RGB</code>.
	 */
	public static BufferedImage scale(URL source,int preferredType,Dimension destSize) {
		//NOTE: this method mirrors scale(File, ...), so when you modify one: modify the other
		String pathLower = source.toString().toLowerCase();
		if(pathLower.endsWith(".bmp")) {
			InputStream in = null;
			try {
				in = source.openStream();
				PixelIterator iter = BmpDecoderIterator.get(in);
				PixelIterator scalingIter = destSize==null ? iter : ScalingIterator.get(iter, destSize.width, destSize.height);
				PixelIterator finalIter = scalingIter;
				if(preferredType==BufferedImage.TYPE_INT_ARGB || preferredType==BufferedImage.TYPE_INT_ARGB_PRE) {
					finalIter = new IntARGBConverter(scalingIter);
				} else if(preferredType==BufferedImage.TYPE_INT_RGB) {
					finalIter = new IntRGBConverter(scalingIter);
				} else if(preferredType==BufferedImage.TYPE_3BYTE_BGR) {
					finalIter = new ByteBGRConverter(scalingIter);
				} else if(preferredType==BufferedImage.TYPE_4BYTE_ABGR) {
					finalIter = new ByteBGRAConverter(scalingIter);
				} else {
					throw new IllegalArgumentException("unrecognized type: "+preferredType);
				}
				BufferedImage image = BufferedImageIterator.create(finalIter, null);
				return image;
			} catch(IOException e) {
				return null;
			} finally {
				try {
					if(in!=null)
						in.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		Image image = Toolkit.getDefaultToolkit().createImage(source);
		try {
			return scale( image, null, destSize);
		} finally {
			if(image!=null)
				image.flush();
		}
	}

	/** Scales the source image into the dest.
	 * 
	 * @param source the source image.  This may not be null.
	 * @param dest the destination image.  If non-null: this image must
	 * be at least <code>destSize</code> pixels in size or an
	 * exception will be thrown.  If this is null: an image will
	 * be created that is <code>destSize</code> pixels.
	 * <p>This argument can be the same as the
	 * <code>source</code> argument.  This may save some memory
	 * allocation, but it will permanently alter the
	 * source image.
	 * <p>Also this need to be of
	 * type <code>BufferedImage.TYPE_INT_ARGB</code>.
	 * @param destSize the dimensions to write to.  It is guaranteed
	 * that these pixels will be replaced in the dest image.
	 * @return the <code>dest</code> argument, or a new image if no
	 * <code>dest</code> argument was provided.
	 */
	public static BufferedImage scale(BufferedImage source,BufferedImage dest,Dimension destSize) {
		if(destSize==null && dest!=null) {
			destSize = new Dimension(dest.getWidth(), dest.getHeight());
		}

		if(source==null) {
			throw new NullPointerException("no source image");
		} else if(destSize==null) {
			throw new NullPointerException("no dest size");
		} else if(dest==null) {
			if(source.getColorModel().hasAlpha()) {
				dest = new BufferedImage(destSize.width, destSize.height, BufferedImage.TYPE_INT_ARGB);
			} else {
				dest = new BufferedImage(destSize.width, destSize.height, BufferedImage.TYPE_INT_RGB);
			}
		}

		PixelIterator pi = ScalingIterator.get(
				BufferedImageIterator.get(source),
				destSize.width, destSize.height
				);
		if(pi instanceof BytePixelIterator) {
			pi = new IntARGBConverter(pi);
		}
		IntPixelIterator i = (IntPixelIterator)pi;
		int[] row = new int[i.getMinimumArrayLength()];
		if(i.isTopDown()) {
			for(int y = 0; y<destSize.height; y++) {
				i.next(row);
				dest.getRaster().setDataElements(0, y, destSize.width, 1, row);
			}
		} else {
			for(int y = destSize.height-1; y>=0; y--) {
				i.next(row);
				dest.getRaster().setDataElements(0, y, destSize.width, 1, row);
			}
		}
		return dest;
	}


	/** Scales the source image into the dest.
	 * 
	 * @param source the source image.  This may not be null.
	 * @param dest the destination image.  If non-null: this image must
	 * be at least <code>destSize</code> pixels in size or an
	 * exception will be thrown.  If this is null: an image will
	 * be created that is <code>destSize</code> pixels.
	 * <p>This argument can be the same as the
	 * <code>source</code> argument.  This may save some memory
	 * allocation, but it will permanently alter the
	 * source image.
	 * <p>Also this need to be of
	 * type <code>BufferedImage.TYPE_INT_ARGB</code>.
	 * @param destSize the dimensions to write to. It is guaranteed
	 * that these pixels will be replaced in the dest image. If this is null
	 * then the image will not be scaled.
	 * @return the <code>dest</code> argument, or a new image if no
	 * <code>dest</code> argument was provided.
	 */
	public static BufferedImage scale(Image source,BufferedImage dest,Dimension destSize) {
		if(source instanceof BufferedImage) {
			return scale( (BufferedImage)source, dest, destSize);
		}

		if(destSize==null && dest!=null) {
			destSize = new Dimension(dest.getWidth(), dest.getHeight());
		}
		Dimension sourceSize = ImageSize.get(source);

		if(source==null) {
			throw new NullPointerException("no source image");
		} else if(destSize!=null && destSize.width>sourceSize.width) {
			throw new IllegalArgumentException("dest width ("+destSize.width+") must be less than source width ("+sourceSize.width+")");
		} else if(destSize!=null && destSize.height>sourceSize.height) {
			throw new IllegalArgumentException("dest height ("+destSize.height+") must be less than source height ("+sourceSize.height+")");
		} else if(destSize!=null && dest!=null && destSize.width>dest.getWidth()) {
			throw new IllegalArgumentException("dest width ("+destSize.width+") must not exceed the destination image width ("+dest.getWidth()+")");
		} else if(destSize!=null && dest!=null && destSize.height>dest.getHeight()) {
			throw new IllegalArgumentException("dest height ("+destSize.height+") must not exceed the destination image height ("+dest.getHeight()+")");
		}

		int destType = dest!=null ? dest.getType() : BufferedImage.TYPE_INT_ARGB;
		PixelIterator iter = GenericImageSinglePassIterator.get(source, destType);
		PixelIterator scalingIter = destSize==null ? iter : ScalingIterator.get(iter, destSize.width, destSize.height);
		return BufferedImageIterator.create(scalingIter, null);
	}

	/** Scales the source image proportionally to a new, smaller size.
	 * <p>The new image will either have a width of <code>maxWidth</code>
	 * or a height of <code>maxHeight</code> (or both).
	 * 
	 * @param image the source image to scale.
	 * @param maxWidth the maximum width the scaled image can be.
	 * @param maxHeight the maximum height the scaled image can be.
	 * @return a new scaled image.
	 */
	public static BufferedImage scaleProportionally(BufferedImage image,int maxWidth,int maxHeight) {
		float widthRatio = ((float)maxWidth)/((float)image.getWidth());
		float heightRatio = ((float)maxHeight)/((float)image.getHeight());
		float ratio = Math.min(widthRatio, heightRatio);

		int w = (int)(ratio*image.getWidth());
		int h = (int)(ratio*image.getHeight());
		return scale(image, w, h);
	}

	/** Scales the source image proportionally to a new, smaller size.
	 * <p>The new image will either have a height of <code>maxWidth</code>
	 * or a height of <code>maxHeight</code> (or both).
	 * 
	 * @param image the source image to scale.
	 * @param maxSize the maximum dimensions the scaled image can be.
	 * @return a new scaled image.
	 */
	public static BufferedImage scaleProportionally(BufferedImage image,Dimension maxSize) {
		return scaleProportionally(image, maxSize.width, maxSize.height);
	}
}
