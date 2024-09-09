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

import com.pump.image.QBufferedImage;
import com.pump.io.InputStreamSource;

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
	 * @param dest
	 * 			  an optional image to store the image data in
	 * @return the image, or <code>null</code> if this was not a valid image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static QBufferedImage read(File bmpFile, BufferedImage dest) throws IOException {
		if (bmpFile.length() == 0) {
			throw new IOException("the source image file is zero bytes ("+bmpFile.getAbsolutePath()+")");
		}
		try (InputStream in = new FileInputStream(bmpFile)) {
			return read(in, dest);
		}
	}

	/**
	 * Returns an image from the BMP data provided, or null if the input stream
	 * does not appear to be a valid BMP image.
	 * 
	 * @param in
	 *            BMP image data.
	 * @param dest
	 * 			  an optional image to store the image data in
	 * @return the image, or <code>null</code> if this was not a valid image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static QBufferedImage read(InputStream in, BufferedImage dest)
			throws IOException {
		try {
			// create a one-time Source just for this method:
			InputStreamSource src = () -> in;
			return new BmpDecoderIterator.Source(src).toBufferedImage(dest);
		} catch(RuntimeException e) {
			if (e.getCause() instanceof BmpHeaderException)
				return null;
			if (e.getCause() instanceof IOException)
				throw (IOException) e.getCause();
			throw e;
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
			try {
				BmpHeader header = new BmpHeader(fileIn);
				return new Dimension(header.width, header.height);
			} catch(BmpHeaderException bhe) {
				return null;
			}
		}
	}
}