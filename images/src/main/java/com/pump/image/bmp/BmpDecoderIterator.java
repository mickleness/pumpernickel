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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.*;
import java.net.URL;
import java.util.Objects;

import com.pump.image.pixel.IndexedBytePixelIterator;
import com.pump.image.pixel.PixelIterator;
import com.pump.io.InputStreamSource;
import com.pump.io.URLInputStreamSource;
import com.pump.io.MeasuredInputStream;

/**
 * A PixelIterator that reads simple BMP images. You must instantiate
 * this object using {@link #get(InputStream)}, which will return a
 * BmpDecoderIterator that may or may not also be a IndexedBytePixelIterator.
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
public class BmpDecoderIterator implements PixelIterator<byte[]> {

	/**
	 * This {@link PixelIterator.Source} produces {@link BmpDecoderIterator BmpDecoderIterators} from a given URL.
	 */
	public static class Source implements PixelIterator.Source<byte[]> {
		private final InputStreamSource src;
		private int width = -1;
		private int height = -1;

		public Source(InputStreamSource src) {
			this.src = Objects.requireNonNull(src);
		}

		@Override
		public BmpDecoderIterator createPixelIterator() {
			try {
				BmpDecoderIterator returnValue = BmpDecoderIterator.get(src.createInputStream());
				if (width == -1) {
					width = returnValue.getWidth();
					height = returnValue.getHeight();
				}
				return returnValue;
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public int getWidth() {
			validateSize();
			return width;
		}

		@Override
		public int getHeight() {
			validateSize();
			return height;
		}

		private void validateSize() {
			if (width == -1) {
				try(InputStream in = src.createInputStream()) {
					Dimension d = BmpDecoder.getSize(src.createInputStream());
					width = d.width;
					height = d.height;
				} catch(IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Returns a <code>BmpDecoderIterator</code> from a <code>File</code>.
	 * 
	 * @throws BmpHeaderException
	 *             if this file does not appear to be a valid BMP image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BmpDecoderIterator get(File file) throws IOException {
		return get(file.toURI().toURL());
	}

	/**
	 * Returns a <code>BmpDecoderIterator</code> from a <code>URL</code>.
	 *
	 * @throws BmpHeaderException
	 *             if this file does not appear to be a valid BMP image.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BmpDecoderIterator get(URL url) throws IOException {
		return new Source(new URLInputStreamSource(url)).createPixelIterator();
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
				|| header.bitsPerPixel == 8 || header.bitsPerPixel == 24
				|| header.bitsPerPixel == 32))
			throw new IOException(
					"unsupported depth (" + header.bitsPerPixel + ")");

		if (header.colorModel != null) {
			return new BmpDecoderIndexedIterator(in, header.colorModel,
					header.width, header.height, header.bitsPerPixel,
					header.topDown);
		}

		if (header.planes != 1)
			throw new IOException("unsupported planes (" + header.planes + ")");

		if (header.compression != 0)
			throw new IOException(
					"unsupported compression (" + header.compression + ")");

		// we should already be pointing to the bitmap offset,
		// but in case the file format changes in the future
		// and (by some magical/well-architected coincidence)
		// we can still make sense of the image: let's defer
		// to the bitmapOffset field to point out the image data:
		in2.seek(header.bitmapOffset);

		return new BmpDecoderIterator(in, header.width, header.height,
				header.bitsPerPixel, header.topDown);
	}

	/**
	 * This specialized BmpDecoderIterator is for IndexColorModels.
	 */
	static class BmpDecoderIndexedIterator extends BmpDecoderIterator
			implements IndexedBytePixelIterator {
		IndexColorModel colorModel;

		private BmpDecoderIndexedIterator(InputStream in, IndexColorModel model,
				int width, int height, int depth, boolean topDown) {
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
		public void next(byte[] dest, int offset) {
			super.next(dest, offset);
			// unpack the data
			if (depth == 4) {
				for (int x = width / 2; x >= 0; x--) {
					byte k = dest[x + offset];
					if (2 * x + 1 < width)
						dest[2 * x + 1 + offset] = (byte) (k & 0x0f);
					if (2 * x < width)
						dest[2 * x + offset] = (byte) ((k >> 4) & 0x0f);
				}
			} else if (depth == 1) {
				for (int x = width / 8; x >= 0; x--) {
					byte k = dest[x + offset];
					for (int i = 7; i >= 0; i--) {
						if (8 * x + i < width)
							dest[8 * x + i + offset] = (byte) ((k >> (7 - i)) & 0x01);
					}
				}
			}
		}

		@Override
		protected void validateDepth() {
			// intentionally empty
		}
	}

	int width, height, depth;
	InputStream in;
	boolean closed;
	boolean topDown;
	int rowCtr;
	int scanline;

	private BmpDecoderIterator(InputStream in, int width, int height, int depth,
			boolean topDown) {
		this.in = in;
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.topDown = topDown;

		scanline = width * depth / 8;
		int r = scanline % 4;
		if (r != 0) {
			scanline = scanline + (4 - r);
		}

		rowCtr = 0;
		validateDepth();
	}

	/**
	 * This throws an exception if the {@link #depth} field is unsupported.
	 */
	protected void validateDepth() {
		if (!(depth == 32 || depth == 24)) {
			throw new IllegalArgumentException(
					"unsupported depth (" + depth + ")");
		}
	}

	@Override
	public void next(byte[] dest, int offset) {
		if (closed)
			throw new IllegalStateException("This BmpDecoderIterator is closed.");

		try {
			int read = in.readNBytes(dest, offset, scanline);
			if (read != scanline)
				throw new IOException("requested " + scanline+", but read " + read + " bytes");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			rowCtr++;
			checkComplete();
		}
	}

	@Override
	public void skip() {
		if (closed)
			return;

		try {
			in.skipNBytes(scanline);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			rowCtr++;
			checkComplete();
		}
	}

	private void checkComplete() {
		if (isDone()) {
			close();
		}
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getPixelSize() {
		return depth / 8;
	}

	@Override
	public int getType() {
		if (depth == 24)
			return BufferedImage.TYPE_3BYTE_BGR;
		return BufferedImage.TYPE_4BYTE_ABGR;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public boolean isDone() {
		return rowCtr == height;
	}

	@Override
	public boolean isTopDown() {
		return topDown;
	}

	@Override
	public void close() {
		if (!closed) {
			closed = true;
			try {
				in.close();
			} catch(RuntimeException e) {
				throw e;
			} catch(Exception e) {
				throw new ClosingException(e);
			}
			in = null;
		}
	}
}