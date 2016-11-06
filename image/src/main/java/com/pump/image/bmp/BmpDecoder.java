/*
 * @(#)BmpDecoder.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
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
package com.pump.image.bmp;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.PixelIterator;
import com.pump.image.pixel.ScalingIterator;

public class BmpDecoder {

	/** Returns an image from the BMP file provided, or null if
	 * the file does not appear to be a valid BMP image.
	 * 
	 * @param bmpFile a BMP file.
	 * @return the image, or <code>null</code> if this was
	 * not a valid image.
	 * @throws IOException if an IO problem occurs.
	 */
	public static BufferedImage readImage(File bmpFile) throws IOException {
		return readImage(bmpFile, null);
	}

	/** Returns an image from the BMP file provided, or null if
	 * the file does not appear to be a valid BMP image.
	 * 
	 * @param bmpFile a BMP file.
	 * @param dst a destination to store the image in.  If this is
	 * non-null it must be the correct size to contain all
	 * of the image data.
	 * @return the image, or <code>null</code> if this was
	 * not a valid image.
	 * @throws IOException if an IO problem occurs.
	 */
	public static BufferedImage readImage(File bmpFile, BufferedImage dst)
			throws IOException {
		FileInputStream in = null;
		if (bmpFile == null) {
			throw new NullPointerException();
		} else if (bmpFile.length() == 0) {
			return null;
		}
		try {
			in = new FileInputStream(bmpFile);
			return readImage(in, dst);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}

	/** Returns an image from the BMP data provided, or null if
	 * the input stream does not appear to be a valid BMP image.
	 * 
	 * @param in BMP image data.
	 * @return the image, or <code>null</code> if this was
	 * not a valid image.
	 * @throws IOException if an IO problem occurs.
	 */
	public static BufferedImage readImage(InputStream in) throws IOException {
		return readImage(in, null);
	}

	/** Returns an image from the BMP data provided, or null if
	 * the input stream does not appear to be a valid BMP image.
	 * 
	 * @param in BMP image data.
	 * @param dst a destination to store the image in.  If this is
	 * non-null it must be the correct size to contain all
	 * of the image data.
	 * @return the image, or <code>null</code> if this was
	 * not a valid image.
	 * @throws IOException if an IO problem occurs.
	 */
	public static BufferedImage readImage(InputStream in,
			BufferedImage dst) throws IOException {
		try {
			BmpDecoderIterator iterator = BmpDecoderIterator.get(in);
			return BufferedImageIterator.create(iterator, dst);
		} catch(BmpHeaderException e) {
			return null;
		}
	}

	/** Returns the dimensions of a BMP.
	 * 
	 * @param file the file to retrieve the size of.
	 * @return the size of the BMP, or null if the file does
	 * not appear to be a valid BMP image.
	 * @throws IOException if an IO problem occurs.
	 */
	public static Dimension getSize(File file) throws IOException {
		FileInputStream in = null;
		in = new FileInputStream(file);
		return getSize(in);
	}

	/** Returns the dimensions of a BMP.
	 * 
	 * @param in a stream containing a BMP.  This will be closed
	 * when this method returns.
	 * @return the size of the BMP, or null if this input stream
	 * did not appear to be a valid BMP image.
	 * @throws IOException if an IO problem occurs.
	 */
	public static Dimension getSize(InputStream in) throws IOException {
		try {
			BmpHeader header = new BmpHeader(in);
			return new Dimension(header.width, header.height);
		} catch(BmpHeaderException e) {
			return null;
		}
	}

	/** Create a thumbnail of a BMP file.
	 * 
	 * @param bmpFile a BMP file.
	 * @param maxSize the maximum width and height of the thumbnail.
	 * If the image normally exceeds one (or both) of these dimensions,
	 * then the image data will be scaled down.
	 * <p>If the BMP image is smaller than <code>maxSize</code> it is
	 * <i>not</i> scaled up.  This argument is only used to scale <i>down</i>.
	 * @return a thumbnail of a BMP file.
	 * @throws IOException if an IO problem occurs.
	 */
	public static BufferedImage createThumbnail(File bmpFile,Dimension maxSize) throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(bmpFile);
			return createThumbnail(in, maxSize);
		} finally {
			try {
				if(in!=null)
					in.close();
			} catch(Exception e) {}
		}
	}

	/** Create a thumbnail of a BMP file.
	 * 
	 * @param bmpURL a BMP file.
	 * @param maxSize the maximum width and height of the thumbnail.
	 * If the image normally exceeds one (or both) of these dimensions,
	 * then the image data will be scaled down.
	 * <p>If the BMP image is smaller than <code>maxSize</code> it is
	 * <i>not</i> scaled up.  This argument is only used to scale <i>down</i>.
	 * @return a thumbnail of a BMP file.
	 * @throws IOException if an IO problem occurs.
	 */
	public static BufferedImage createThumbnail(URL bmpURL,Dimension maxSize) throws IOException {
		InputStream in = null;
		try {
			in = bmpURL.openStream();
			return createThumbnail(in, maxSize);
		} finally {
			try {
				if(in!=null)
					in.close();
			} catch(Exception e) {}
		}
	}

	/** Create a thumbnail of a BMP image.
	 * 
	 * @param bmp a BMP image.
	 * @param maxSize the maximum width and height of the thumbnail.
	 * If the image normally exceeds one (or both) of these dimensions,
	 * then the image data will be scaled down.
	 * <p>If the BMP image is smaller than <code>maxSize</code> it is
	 * <i>not</i> scaled up.  This argument is only used to scale <i>down</i>.
	 * @return a thumbnail of a BMP image.
	 * @throws IOException if an IO problem occurs.
	 */
	public static BufferedImage createThumbnail(InputStream bmp,Dimension maxSize) throws IOException {
		PixelIterator i = BmpDecoderIterator.get(bmp);
		int srcW = i.getWidth();
		int srcH = i.getHeight();

		float widthRatio = ((float)maxSize.width)/((float)srcW);
		float heightRatio = ((float)maxSize.height)/((float)srcH);
		float ratio = Math.min(widthRatio, heightRatio);
		
		if(ratio<1) {
			i = ScalingIterator.get(i, ratio);
		}
		return BufferedImageIterator.create( i, null );
	}

	/** Create a thumbnail of a BMP file.
	 * 
	 * @param bmpFile a BMP file.
	 * @param dest an image to store the thumbnail in.
	 * This must the size of the original image
	 * or smaller: it may not be larger.
	 * @throws IOException if an IO problem occurs.
	 */
	public static void createThumbnail(File bmpFile,BufferedImage dest) throws IOException {
		InputStream in = null;
		try {
			in = new FileInputStream(bmpFile);
			createThumbnail(in,dest);
		} finally {
			try {
				if(in!=null)
					in.close();
			} catch(Exception e) {}
		}
	}

	/** Create a thumbnail of a BMP image.
	 * 
	 * @param bmp a BMP image.
	 * @param dest an image to store the thumbnail in.
	 * @throws IOException if an IO problem occurs.
	 */
	public static void createThumbnail(InputStream bmp,BufferedImage dest) throws IOException {
		PixelIterator i = BmpDecoderIterator.get(bmp);
	
		i = ScalingIterator.get(i, dest.getWidth(), dest.getHeight());
		BufferedImageIterator.create( i, dest );
	}
}
