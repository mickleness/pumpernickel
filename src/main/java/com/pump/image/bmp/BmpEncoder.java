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
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.pump.UserCancelledException;
import com.pump.image.ImageSize;
import com.pump.image.pixel.BytePixelIterator;
import com.pump.image.pixel.ImageType;
import com.pump.util.PushPullQueue;

/**
 * This is a set of static calls to write a simple BMP 2.x image, either in
 * 24-bit or 32-bit depending on whether the source image is opaque.
 * <p>
 * These methods require a <code>java.awt.image.BufferedImage</code>. If you
 * only have a <code>java.awt.Image</code>: you can use the {@link com.pump.image.ImageLoader}
 * to convert a plain <code>Image</code> to a <code>BufferedImage</code>.
 * </p>
 */
public class BmpEncoder {

	/**
	 * Write an image to a file as a BMP.
	 *
	 * @throws IOException
	 */
	public static void write(BufferedImage image, File dest) throws IOException {
		try(FileOutputStream fileOut = new FileOutputStream(dest)) {
			write(image, fileOut);
		}
	}

	/**
	 * Write an image to an OutputStream as a BMP.
	 *
	 * @throws IOException
	 */
	public static void write(BufferedImage image, OutputStream out)
			throws IOException {
		write(image, out, true);
	}

	/**
	 * Write an image to an OutputStream as a BMP.
	 *
	 * @param closeStreamOnCompletion if true then this method calls {@link OutputStream#close()}, if false
	 *                                then this method leaves the OutputStream open.
	 */
	public static void write(BufferedImage image, OutputStream out,
			boolean closeStreamOnCompletion) throws IOException {
		BytePixelIterator i;
		if (image.getTransparency() == Transparency.OPAQUE) {
			i = ImageType.BYTE_BGR.createConverter(image);
		} else {
			i = ImageType.BYTE_BGRA.createConverter(image);
		}
		write(out, i, closeStreamOnCompletion);
	}

	protected static final int HEADER_SIZE = 26;

	/**
	 * Return the number of bytes in each row of the BMP.
	 */
	protected static int getScanlineSize(int width, int bytesPerPixel) {
		int scanLineSize = width * bytesPerPixel;
		int r = scanLineSize % 4;
		if (r != 0) {
			scanLineSize = scanLineSize + (4 - r);
		}
		return scanLineSize;
	}

	/**
	 * This will write a BMP header. This will write HEADER_SIZE many bytes.
	 * 
	 * @param destinationArray
	 *            the array to write the header in.
	 * @param arrayOffset
	 *            the offset to write in the array.
	 * @param width
	 *            the width of the image.
	 * @param height
	 *            the height of the image.
	 * @param bitsPerPixel
	 *            must be 24 or 32.
	 * @return the scan line size of this BMP. All rows are expected to be this
	 *         many bytes in length.
	 */
	protected static int writeHeader(byte[] destinationArray, int arrayOffset,
			int width, int height, int bitsPerPixel) {
		if (width <= 0)
			throw new IllegalArgumentException(
					"width (" + width + ") must be positive");
		if (height <= 0)
			throw new IllegalArgumentException(
					"height (" + height + ") must be positive");
		if (!(bitsPerPixel == 24 || bitsPerPixel == 32))
			throw new IllegalArgumentException(
					"bitsPerPixel (" + bitsPerPixel + ") must be 24 or 32");

		int scanLineSize = getScanlineSize(width, bitsPerPixel / 8);

		int fileSize = scanLineSize * height + HEADER_SIZE;

		// declare this file is a bitmap:
		destinationArray[0 + arrayOffset] = 'B';
		destinationArray[1 + arrayOffset] = 'M';
		// size of file:
		destinationArray[2 + arrayOffset] = (byte) ((fileSize >> 0) & 0xff);
		destinationArray[3 + arrayOffset] = (byte) ((fileSize >> 8) & 0xff);
		destinationArray[4 + arrayOffset] = (byte) ((fileSize >> 16) & 0xff);
		destinationArray[5 + arrayOffset] = (byte) ((fileSize >> 24) & 0xff);
		// reserved:
		destinationArray[6 + arrayOffset] = 0;
		destinationArray[7 + arrayOffset] = 0;
		destinationArray[8 + arrayOffset] = 0;
		destinationArray[9 + arrayOffset] = 0;
		// where the image data begins:
		destinationArray[10 + arrayOffset] = (byte) ((HEADER_SIZE >> 0) & 0xff);
		destinationArray[11 + arrayOffset] = (byte) ((HEADER_SIZE >> 8) & 0xff);
		destinationArray[12
				+ arrayOffset] = (byte) ((HEADER_SIZE >> 16) & 0xff);
		destinationArray[13
				+ arrayOffset] = (byte) ((HEADER_SIZE >> 24) & 0xff);

		// size of the remaining header (12)
		destinationArray[14 + arrayOffset] = (byte) ((12 >> 0) & 0xff);
		destinationArray[15 + arrayOffset] = (byte) ((12 >> 8) & 0xff);
		destinationArray[16 + arrayOffset] = (byte) ((12 >> 16) & 0xff);
		destinationArray[17 + arrayOffset] = (byte) ((12 >> 24) & 0xff);

		// width:
		destinationArray[18 + arrayOffset] = (byte) ((width >> 0) & 0xff);
		destinationArray[19 + arrayOffset] = (byte) ((width >> 8) & 0xff);

		// height:
		destinationArray[20 + arrayOffset] = (byte) ((height >> 0) & 0xff);
		destinationArray[21 + arrayOffset] = (byte) ((height >> 8) & 0xff);

		// planes: (1)
		destinationArray[22 + arrayOffset] = 1;
		destinationArray[23 + arrayOffset] = 0;

		destinationArray[24 + arrayOffset] = (byte) bitsPerPixel;
		destinationArray[25 + arrayOffset] = 0;

		return scanLineSize;
	}

	public static void write(OutputStream out, BytePixelIterator i)
			throws IOException {
		write(out, i, true);
	}

	public static void write(OutputStream out, BytePixelIterator i,
			boolean closeStreamOnCompletion) throws IOException {
		byte bitsPerPixel = (byte) (i.getPixelSize() * 8);

		byte[] scrap = new byte[Math.max(HEADER_SIZE,
				i.getMinimumArrayLength() + 8)];

		int scanLineSize = writeHeader(scrap, 0, i.getWidth(), i.getHeight(),
				bitsPerPixel);

		out.write(scrap, 0, HEADER_SIZE);

		while (i.isDone() == false) {
			// TODO: when we encode the BMP we found were swapping the blue and red channels twice.
			// Instead if we directly interacted with the DataBufferByte: we could avoid any swapping and
			// outperform ImageIO classes.
			// So maybe (?) a similar performance gain can be seen here: is the ByteInterleavedRaster swapping
			// color channels automatically for us?
			i.next(scrap);
			out.write(scrap, 0, scanLineSize);
		}
		if (closeStreamOnCompletion)
			out.close();
	}
}