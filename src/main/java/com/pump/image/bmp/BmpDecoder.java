/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.bmp;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.pump.image.QBufferedImage;
import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.PixelIterator;
import com.pump.image.pixel.ScalingIterator;

/**
 * This is a collection of static methods to help decode BMP images.
 */
public class BmpDecoder {

	/**
	 * Returns an image from the BMP file provided, or null if the file does not
	 * appear to be a valid BMP image.
	 * 
	 * @param bmpFile
	 *            a BMP file.
	 * @return the image, or <code>null</code> if this was not a valid image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static QBufferedImage read(File bmpFile) throws IOException {
		return read(bmpFile, null);
	}

	/**
	 * Returns an image from the BMP file provided, or null if the file does not
	 * appear to be a valid BMP image.
	 * 
	 * @param bmpFile
	 *            a BMP file.
	 * @param dst
	 *            a destination to store the image in. If this is non-null it
	 *            must be the correct size to contain all of the image data.
	 * @return the image, or <code>null</code> if this was not a valid image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static QBufferedImage read(File bmpFile, BufferedImage dst)
			throws IOException {
		if (bmpFile.length() == 0) {
			throw new IOException("the source image file is zero bytes ("+bmpFile.getAbsolutePath()+")");
		}
		try (InputStream in = new FileInputStream(bmpFile)) {
			return read(in, dst);
		}
	}

	/**
	 * Returns an image from the BMP data provided, or null if the input stream
	 * does not appear to be a valid BMP image.
	 * 
	 * @param in
	 *            BMP image data.
	 * @return the image, or <code>null</code> if this was not a valid image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BufferedImage read(InputStream in) throws IOException {
		return read(in, null);
	}

	/**
	 * Returns an image from the BMP data provided, or null if the input stream
	 * does not appear to be a valid BMP image.
	 * 
	 * @param in
	 *            BMP image data.
	 * @param dst
	 *            a destination to store the image in. If this is non-null it
	 *            must be the correct size to contain all of the image data.
	 * @return the image, or <code>null</code> if this was not a valid image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static QBufferedImage read(InputStream in, BufferedImage dst)
			throws IOException {
		try {
			BmpDecoderIterator iterator = BmpDecoderIterator.get(in);
			return BufferedImageIterator.writeToImage(iterator, dst);
		} catch (BmpHeaderException e) {
			return null;
		}
	}

	/**
	 * Returns the dimensions of a BMP.
	 * 
	 * @param file
	 *            the file to retrieve the size of.
	 * @return the size of the BMP, or null if the file does not appear to be a
	 *         valid BMP image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static Dimension getSize(File file) throws IOException {
		try (FileInputStream fileIn = new FileInputStream(file)) {
			return getSize(fileIn);
		}
	}

	/**
	 * Returns the dimensions of a BMP.
	 * 
	 * @param in
	 *            a stream containing a BMP. This will be closed when this
	 *            method returns.
	 * @return the size of the BMP, or null if this input stream did not appear
	 *         to be a valid BMP image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static Dimension getSize(InputStream in) throws IOException {
		try {
			BmpHeader header = new BmpHeader(in);
			return new Dimension(header.width, header.height);
		} catch (BmpHeaderException e) {
			return null;
		}
	}

	/**
	 * Create a thumbnail of a BMP file.
	 *
	 * @param bmpFile
	 *            a BMP file.
	 * @param maxSize
	 *            the maximum width and height of the thumbnail. If the image
	 *            normally exceeds one (or both) of these dimensions, then the
	 *            image data will be scaled down.
	 *            <p>
	 *            If the BMP image is smaller than <code>maxSize</code> it is
	 *            <i>not</i> scaled up. This argument is only used to scale
	 *            <i>down</i>.
	 * @return a thumbnail of a BMP file.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BufferedImage createThumbnail(File bmpFile, Dimension maxSize)
			throws IOException {
		try (InputStream in = new FileInputStream(bmpFile)) {
			return createThumbnail(in, maxSize);
		}
	}

	/**
	 * Create a thumbnail of a BMP file.
	 *
	 * @param bmpURL
	 *            a BMP file.
	 * @param maxSize
	 *            the maximum width and height of the thumbnail. If the image
	 *            normally exceeds one (or both) of these dimensions, then the
	 *            image data will be scaled down.
	 *            <p>
	 *            If the BMP image is smaller than <code>maxSize</code> it is
	 *            <i>not</i> scaled up. This argument is only used to scale
	 *            <i>down</i>.
	 * @return a thumbnail of a BMP file.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BufferedImage createThumbnail(URL bmpURL, Dimension maxSize)
			throws IOException {
		try (InputStream in = bmpURL.openStream()) {
			return createThumbnail(in, maxSize);
		}
	}

	/**
	 * Create a thumbnail of a BMP image.
	 *
	 * @param bmp
	 *            a BMP image.
	 * @param maxSize
	 *            the maximum width and height of the thumbnail. If the image
	 *            normally exceeds one (or both) of these dimensions, then the
	 *            image data will be scaled down.
	 *            <p>
	 *            If the BMP image is smaller than <code>maxSize</code> it is
	 *            <i>not</i> scaled up. This argument is only used to scale
	 *            <i>down</i>.
	 * @return a thumbnail of a BMP image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BufferedImage createThumbnail(InputStream bmp,
												Dimension maxSize) throws IOException {
		PixelIterator i = BmpDecoderIterator.get(bmp);
		int srcW = i.getWidth();
		int srcH = i.getHeight();

		float widthRatio = ((float) maxSize.width) / ((float) srcW);
		float heightRatio = ((float) maxSize.height) / ((float) srcH);
		float ratio = Math.min(widthRatio, heightRatio);

		if (ratio < 1) {
			int scaledWidth = Math.max(1, Math.round(ratio * srcW));
			int scaledHeight = Math.max(1, Math.round(ratio * srcH));
			i = new ScalingIterator(i, scaledWidth, scaledHeight);
		}
		return BufferedImageIterator.writeToImage(i, null);
	}

	/**
	 * Create a thumbnail of a BMP file.
	 *
	 * @param bmpFile
	 *            a BMP file.
	 * @param dest
	 *            an image to store the thumbnail in. This must the size of the
	 *            original image or smaller: it may not be larger.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static void createThumbnail(File bmpFile, BufferedImage dest)
			throws IOException {
		try (InputStream in = new FileInputStream(bmpFile)) {
			createThumbnail(in, dest);
		}
	}

	/**
	 * Create a thumbnail of a BMP image.
	 *
	 * @param bmp
	 *            a BMP image.
	 * @param dest
	 *            an image to store the thumbnail in.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static void createThumbnail(InputStream bmp, BufferedImage dest)
			throws IOException {
		PixelIterator i = BmpDecoderIterator.get(bmp);
		i = new ScalingIterator(i, dest.getWidth(), dest.getHeight());
		BufferedImageIterator.writeToImage(i, dest);
	}
}