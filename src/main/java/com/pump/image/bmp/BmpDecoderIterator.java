/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.bmp;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.pump.image.pixel.BytePixelIterator;
import com.pump.image.pixel.IndexedBytePixelIterator;
import com.pump.image.pixel.PixelConverter;
import com.pump.io.MeasuredInputStream;

/**
 * A {@link com.pump.image.pixel.BytePixelIterator} that reads simple BMP
 * graphics. You cannot directly instantiate this object because a BMP image may
 * require a <code>BytePixelIterator</code> or a
 * <codE>IndexedBytePixelIterator</code> to decode correctly: so the
 * <code>get()</code> method returns the appropriate iterator for a given BMP
 * image.
 * <p>
 * FIXME: As of this writing: this BMP decoder is NOT fully functional. It has
 * been tested against 1, 4, 8, 24, and 32-bit images, but it does not support
 * any compression settings (including 16-bit encoded images). Also this has not
 * been tested against BMPs that are encoded from top-to-bottom.
 * <P>
 * This was written largely from this specification:
 * 
 * @see <a href="http://www.fileformat.info/format/bmp/egff.htm">Microsoft
 *      Windows Bitmap File Format Summary</a>
 */
public class BmpDecoderIterator implements BytePixelIterator {

	/**
	 * Returns a <code>BmpDecoderIterator</code> from a <code>File</code>.
	 * 
	 * @throws BmpHeaderException
	 *             if this file does not appear to be a valid BMP image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BmpDecoderIterator get(File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			return get(in);
		}
	}

	/**
	 * Returns a <code>BmpDecoderIterator</code> from an
	 * <code>InputStream</code>.
	 * 
	 * @throws BmpHeaderException
	 *             if this stream does not appear to be a valid BMP image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BmpDecoderIterator get(InputStream in) throws IOException {
		MeasuredInputStream in2 = new MeasuredInputStream(in);
		BmpHeader header = new BmpHeader(in2);

		if (!(header.bitsPerPixel == 1 || header.bitsPerPixel == 4
				|| header.bitsPerPixel == 8 || header.bitsPerPixel == 24 || header.bitsPerPixel == 32))
			throw new IOException("unsupported depth (" + header.bitsPerPixel
					+ ")");

		if (header.colorModel != null) {
			return new BmpDecoderIndexedIterator(in, header.colorModel,
					header.width, header.height, header.bitsPerPixel,
					header.topDown);
		}

		if (header.planes != 1)
			throw new IOException("unsupported planes (" + header.planes + ")");

		if (header.compression != 0)
			throw new IOException("unsupported compression ("
					+ header.compression + ")");

		// we should already be pointing to the bitmap offset,
		// but in case the file format changes in the future
		// and (by some magical/well-architected coincidence)
		// we can still make sense of the image: let's defer
		// to the bitmapOffset field to point out the image data:
		in2.seek(header.bitmapOffset);

		return new BmpDecoderIterator(in, header.width, header.height,
				header.bitsPerPixel, header.topDown);
	}

	static class BmpDecoderIndexedIterator extends BmpDecoderIterator implements
			IndexedBytePixelIterator {
		IndexColorModel colorModel;

		private BmpDecoderIndexedIterator(InputStream in,
				IndexColorModel model, int width, int height, int depth,
				boolean topDown) {
			super(in, width, height, depth, topDown);
			this.colorModel = model;
		}

		@Override
		public int getType() {
			return BufferedImage.TYPE_BYTE_INDEXED;
		}

		public IndexColorModel getIndexColorModel() {
			return colorModel;
		}

		@Override
		public int getMinimumArrayLength() {
			return Math.max(super.getMinimumArrayLength(), width);
		}

		@Override
		public void next(byte[] dest) {
			super.next(dest);
			// unpack the data
			if (depth == 4) {
				for (int x = width / 2; x >= 0; x--) {
					byte k = dest[x];
					if (2 * x + 1 < width)
						dest[2 * x + 1] = (byte) (k & 0x0f);
					if (2 * x < width)
						dest[2 * x] = (byte) ((k >> 4) & 0x0f);
				}
			} else if (depth == 1) {
				for (int x = width / 8; x >= 0; x--) {
					byte k = dest[x];
					for (int i = 7; i >= 0; i--) {
						if (8 * x + i < width)
							dest[8 * x + i] = (byte) ((k >> (7 - i)) & 0x01);
					}
				}
			}
		}
	}

	int width, height, depth;
	InputStream in;
	boolean topDown;
	int y;
	int scanline;

	private BmpDecoderIterator(InputStream in, int width, int height,
			int depth, boolean topDown) {
		this.in = in;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.topDown = topDown;

		scanline = width * depth / 8;
		if (!(depth == 32 || depth == 24)
				&& getClass().equals(BmpDecoderIterator.class)) { // if this is
																	// a
																	// BmpDecoderIndexedIterator
																	// we're
																	// under
																	// control
			throw new IllegalArgumentException("unsupported depth (" + depth
					+ ")");
		}

		int r = scanline % 4;
		if (r != 0) {
			scanline = scanline + (4 - r);
		}

		y = height - 1;
	}

	public boolean isOpaque() {
		return PixelConverter.isOpaque(getType());
	}

	public void next(byte[] dest) {
		try {
			read(in, dest, scanline);
		} catch (IOException e) {
			System.err.println("height = " + height);
			System.err.println("y = " + y);
			RuntimeException e2 = new RuntimeException();
			e2.initCause(e);
			throw e2;
		}
		y--;
	}

	private static void read(InputStream in, byte[] dest, int length)
			throws IOException {
		int k = 0;
		while (k < length) {
			int read = in.read(dest, k, length - k);
			if (read == -1)
				throw new EOFException("k = " + k + " length = " + length);
			k += read;
		}
	}

	public void skip() {
		try {
			skip(in, scanline);
		} catch (IOException e) {
			RuntimeException e2 = new RuntimeException();
			e2.initCause(e);
			throw e2;
		}
		y--;
	}

	private static void skip(InputStream in, int length) throws IOException {
		long k = 0;
		while (k < length) {
			long read = in.skip(length - k);
			if (read == -1)
				throw new EOFException();
			k += read;
		}
	}

	public int getHeight() {
		return height;
	}

	public int getMinimumArrayLength() {
		return scanline;
	}

	public int getPixelSize() {
		return depth / 8;
	}

	public int getType() {
		if (depth == 24)
			return BufferedImage.TYPE_3BYTE_BGR;
		return BufferedImage.TYPE_4BYTE_ABGR;
	}

	public int getWidth() {
		return width;
	}

	public boolean isDone() {
		return y == -1;
	}

	public boolean isTopDown() {
		return topDown;
	}
}