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
package com.pump.image.pixel;

import com.pump.image.QBufferedImage;

import java.awt.image.*;
import java.util.Objects;

/**
 * This interfaces the <code>PixelIterator</code> model with
 * <code>BufferedImages</code>.
 * <p>
 * You cannot directly instantiate this class: use one of the static
 * <code>get(...)</code> methods to create <code>BufferedImageIterators</code>.
 */
abstract class BufferedImageIterator<T> implements PixelIterator<T> {

	/**
	 * Return true if we a given BufferedImage type is supported.
	 *
	 * @param type a BufferedImage.TYPE constant
	 */
	static boolean isSupportedType(int type) {
		switch (type) {
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
			case BufferedImage.TYPE_INT_BGR:
			case BufferedImage.TYPE_INT_RGB:
			case BufferedImage.TYPE_3BYTE_BGR:
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			case BufferedImage.TYPE_BYTE_GRAY:
			case BufferedImage.TYPE_BYTE_INDEXED:
				return true;
		}
		return false;
	}

	/**
	 * This is a PixelIteratorSource for BufferedImages.
	 */
	public static class Source implements PixelIterator.Source {
		private final BufferedImage bufferedImage;
		private final boolean isTopDown;

		public Source(BufferedImage bufferedImage) {
			this (bufferedImage, true);
		}

		public Source(BufferedImage bufferedImage, boolean isTopDown) {
			this.bufferedImage = Objects.requireNonNull(bufferedImage);
			this.isTopDown = isTopDown;
		}

		@Override
		public BufferedImageIterator createPixelIterator() {
			int type = bufferedImage.getType();
			if (type == BufferedImage.TYPE_INT_ARGB
					|| type == BufferedImage.TYPE_INT_ARGB_PRE
					|| type == BufferedImage.TYPE_INT_BGR
					|| type == BufferedImage.TYPE_INT_RGB
					|| type == BufferedImage.TYPE_3BYTE_BGR
					|| type == BufferedImage.TYPE_4BYTE_ABGR
					|| type == BufferedImage.TYPE_4BYTE_ABGR_PRE
					|| type == BufferedImage.TYPE_BYTE_GRAY) {
				if (getDataBufferScanline(bufferedImage, false) > 0) {
					return new BufferedImageIterator_FromDataBuffer(bufferedImage, isTopDown);
				}
				return new BufferedImageIterator_FromRaster(bufferedImage, isTopDown);
			} else if (type == BufferedImage.TYPE_BYTE_INDEXED) {
				return new BufferedImageIndexedIterator(bufferedImage, isTopDown);
			} else {
				throw new IllegalArgumentException(
						"unsupported image type: " + bufferedImage.getType());
			}
		}

		@Override
		public int getWidth() {
			return bufferedImage.getWidth();
		}

		@Override
		public int getHeight() {
			return bufferedImage.getHeight();
		}
	}

	/**
	 * Write a PixelIterator to a BufferedImage.
	 * 
	 * @param i
	 *            the pixel data
	 * @param dest
	 *            an optional image to write the image data to. If this is null a new image is created.
	 *            If this is non-null then the pixel data is written to this image. (And exceptions are thrown
	 *            if the incoming data doesn't match the destination image.)
	 * @return a QBufferedImage. If the `dest` argument was non-null but was not a QBufferedImage,
	 * 			  then a new QBufferedImage wrapper is created that points to the same underlying raster.
	 */
	public static QBufferedImage writeToImage(PixelIterator<?> i, BufferedImage dest) {
		return writeToImage(i, dest, 0, 0);
	}


	/**
	 * Write a PixelIterator to a BufferedImage starting at a specific (x,y) offset.
	 *
	 * @param srcIter
	 *            the pixel data to iterate over and store in a BufferedImage.
	 * @param dest
	 *            an optional image to write the image data to. If this is null a new image is created.
	 *            If this is non-null then the pixel data is written to this image. (And exceptions are thrown
	 *            if the incoming data doesn't match the destination image.)
	 * @param x the x offset to start writing to in the dest image.
	 * @param y the y offset to start writing to in the dest image.
	 * @return a QBufferedImage. If the `dest` argument was non-null but was not a QBufferedImage,
	 * 			  then a new QBufferedImage wrapper is created that points to the same underlying raster.
	 */
	public static QBufferedImage writeToImage(PixelIterator<?> srcIter, BufferedImage dest, int x, int y) {

		int w = srcIter.getWidth();
		int h = srcIter.getHeight();

		QBufferedImage returnValue;

		if (dest != null) {
			if (dest.getType() != srcIter.getType())
				srcIter = ImageType.get(dest.getType()).createPixelIterator(srcIter);
			if (dest.getWidth() < x + w || dest.getHeight() < y + h)
				throw new IllegalArgumentException("size mismatch: "
						+ dest.getWidth() + "x" + dest.getHeight()
						+ " is too small for " + w + "x" + h + " that starts at (" + x + ", " + y+")");
			if (dest instanceof QBufferedImage) {
				returnValue = (QBufferedImage) dest;
			} else {
				returnValue = new QBufferedImage(dest);
			}
		} else if (srcIter instanceof IndexedBytePixelIterator) {
			IndexColorModel indexModel = ((IndexedBytePixelIterator) srcIter)
					.getIndexColorModel();
			returnValue = new QBufferedImage(w + x, h + y, BufferedImage.TYPE_BYTE_INDEXED,
					indexModel);
		} else {
			int type = srcIter.getType();
			type = getBufferedImageType(type);
			srcIter = ImageType.get(type).createPixelIterator(srcIter);
			returnValue = new QBufferedImage(w + x, h + y, type);
		}

		int dataBufferScanline = getDataBufferScanline(returnValue, false);

		boolean writeDirectToDataBuffer = dataBufferScanline != -1;
		if (writeDirectToDataBuffer) {
			int xOffset = (x + -returnValue.getRaster().getSampleModelTranslateX()) * srcIter.getPixelSize();
			y += -returnValue.getRaster().getSampleModelTranslateY();

			if (srcIter.isInt()) {
				DataBufferInt dataBuffer = (DataBufferInt) returnValue.getRaster().getDataBuffer();
				int[] imgData = dataBuffer.getData();
				int bufferOff = dataBuffer.getOffset();

				PixelIterator<int[]> ipi = (PixelIterator<int[]>) srcIter;
				if (srcIter.isTopDown()) {
					for (int y2 = 0; y2 < h; y2++) {
						ipi.next(imgData, bufferOff + (y + y2) * dataBufferScanline + xOffset);
					}
				} else {
					for (int y2 = h - 1; y2 >= 0; y2--) {
						ipi.next(imgData, bufferOff + (y + y2) * dataBufferScanline + xOffset);
					}
				}
			} else {
				DataBufferByte dataBuffer = (DataBufferByte) returnValue.getRaster().getDataBuffer();
				byte[] imgData = dataBuffer.getData();
				int bufferOff = dataBuffer.getOffset();

				PixelIterator<byte[]> bpi = (PixelIterator<byte[]>) srcIter;
				if (bpi.isTopDown()) {
					for (int y2 = 0; y2 < h; y2++) {
						bpi.next(imgData, bufferOff + (y + y2) * dataBufferScanline + xOffset);
					}
				} else {
					for (int y2 = h - 1; y2 >= 0; y2--) {
						bpi.next(imgData, bufferOff + (y + y2) * dataBufferScanline + xOffset);
					}
				}
			}
		} else {
			if (srcIter.isInt()) {
				PixelIterator<int[]> ipi = (PixelIterator<int[]>) srcIter;
				int[] row = new int[w * ipi.getPixelSize()];
				if (ipi.isTopDown()) {
					for (int y2 = 0; y2 < h; y2++) {
						ipi.next(row, 0);
						returnValue.getRaster().setDataElements(x, y + y2, w, 1, row);
					}
				} else {
					for (int y2 = h - 1; y2 >= 0; y2--) {
						ipi.next(row, 0);
						returnValue.getRaster().setDataElements(x, y + y2, w, 1, row);
					}
				}
			} else {
				PixelIterator<byte[]> bpi = (PixelIterator<byte[]>) srcIter;

				switch (bpi.getType()) {
					case BufferedImage.TYPE_3BYTE_BGR:
						bpi = ImageType.BYTE_RGB.createPixelIterator(bpi);
						break;
					case BufferedImage.TYPE_4BYTE_ABGR_PRE:
						bpi = ImageType.BYTE_RGBA_PRE.createPixelIterator(bpi);
						break;
					case BufferedImage.TYPE_4BYTE_ABGR:
						bpi = ImageType.BYTE_RGBA.createPixelIterator(bpi);
						break;
				}

				byte[] row = new byte[w * bpi.getPixelSize()];
				if (bpi.isTopDown()) {
					for (int y2 = 0; y2 < h; y2++) {
						bpi.next(row, 0);
						returnValue.getRaster().setDataElements(x, y + y2, w, 1, row);
					}
				} else {
					for (int y2 = h - 1; y2 >= 0; y2--) {
						bpi.next(row, 0);
						returnValue.getRaster().setDataElements(x, y + y2, w, 1, row);
					}
				}
			}
		}
		return returnValue;
	}

	/**
	 * Identify the DataBuffer scanline, if possible. It's possible this value may not be identifiable,
	 * so callers need to be ready to accept a return value of -1. This also returns -1 if there are
	 * multiple buffer banks. (I have yet to find a clear explanation of when/how multiple banks occur
	 * in the wild? If I understood this better maybe I could support it better?)
	 *
	 * @param bi the image to analyze
	 * @param throwException if true then we throw an exception instead of returning -1
	 * @return the DataBuffer scanline, or -1 if it can't be safely determined.
	 */
	private static int getDataBufferScanline(BufferedImage bi, boolean throwException) {
		ImageType type = ImageType.get(bi.getType());
		DataBuffer dataBuffer = bi.getRaster().getDataBuffer();
		int arrayLength;
		if (dataBuffer instanceof DataBufferInt) {
			arrayLength = ((DataBufferInt) dataBuffer).getData().length;
		} else if (dataBuffer instanceof DataBufferByte) {
			arrayLength = ((DataBufferByte) dataBuffer).getData().length;
		} else {
			if (throwException)
				throw new IllegalArgumentException("Unsupported DataBuffer: "+ dataBuffer.getClass().getName());
			return -1;
		}

		if (dataBuffer.getNumBanks() != 1)
			throw new IllegalArgumentException("Unsupported number of banks: "+ dataBuffer.getNumBanks());
		int dataBufferOffset = dataBuffer.getOffset();
		WritableRaster r = bi.getRaster();
		while (r.getParent() != null) {
			r = r.getWritableParent();
		}
		int rasterWidth = r.getWidth();
		int rasterHeight = r.getHeight();
		if (dataBufferOffset + rasterWidth * rasterHeight * type.getSampleCount() != arrayLength) {
			if (throwException)
				throw new IllegalArgumentException("Unsupported array: length = " + arrayLength + ", offset = " + dataBufferOffset + ", buffer size = " + dataBuffer.getSize());
			return -1;
		}
		return rasterWidth * ImageType.get(bi.getType()).getSampleCount();
	}

	/**
	 * If the argument is not a BufferedImage TYPE constant, then this identifies the closest matching
	 * BufferedImage TYPE constant. For example: {@link ImageType#TYPE_4BYTE_RGBA_PRE} uses 4 bytes and
	 * is premultiplied, so it will be converted to {@link BufferedImage#TYPE_4BYTE_ABGR_PRE}.
	 */
	public static int getBufferedImageType(int imageType) {
		ImageType t = ImageType.get(imageType);
		if (t.isBufferedImageType())
			return imageType;

		if (t.isInt()) {
			if (t.getColorModel().hasAlpha()) {
				if (t.getColorModel().isAlphaPremultiplied()) {
					return BufferedImage.TYPE_INT_ARGB_PRE;
				} else {
					return BufferedImage.TYPE_INT_ARGB;
				}
			} else {
				return BufferedImage.TYPE_INT_RGB;
			}
		} else {
			if (t.getColorModel().hasAlpha()) {
				if (t.getColorModel().isAlphaPremultiplied()) {
					return BufferedImage.TYPE_4BYTE_ABGR_PRE;
				} else {
					return BufferedImage.TYPE_4BYTE_ABGR;
				}
			} else {
				return BufferedImage.TYPE_3BYTE_BGR;
			}
		}
	}

	final BufferedImage bi;
	final int type;
	final boolean topDown;
	final int w, h;
	int y;

	boolean isClosed = false;

	private BufferedImageIterator(BufferedImage bi, boolean topDown) {
		this(bi, bi.getType(), topDown);
	}

	/**
	 * @param type the image type of `bi`, which isn't always {@link BufferedImage#getType()}. If we're
	 *             relying on the Raster: in some cases we convert one byte type to another to reflect that
	 *             the Raster reorders the bytes.
	 */
	private BufferedImageIterator(BufferedImage bi, int type, boolean topDown) {
		this.type = type;
		this.bi = bi;
		this.topDown = topDown;
		w = bi.getWidth();
		h = bi.getHeight();
		if (topDown) {
			y = 0;
		} else {
			y = h - 1;
		}
	}

	/**
	 * This uses getRaster().getDataElements() to fetch rows of pixel data.
	 */
	static class BufferedImageIterator_FromRaster<T> extends BufferedImageIterator<T> {

		/**
		 * The BufferedImage types TYPE_3BYTE_BGR and 4BYTE_ARGB are
		 * actually encoded as the name suggests, but getRaster().getDataElements()
		 * reverses the order. So they become RGB and RGBA.
		 */
		private static int getRasterReturnType(BufferedImage bi) {
			int type = bi.getType();
			switch (type) {
				case BufferedImage.TYPE_3BYTE_BGR:
					return ImageType.TYPE_3BYTE_RGB;
				case BufferedImage.TYPE_4BYTE_ABGR:
					return ImageType.TYPE_4BYTE_RGBA;
				case BufferedImage.TYPE_4BYTE_ABGR_PRE:
					return ImageType.TYPE_4BYTE_RGBA_PRE;
				default:
					return type;
			}
		}

		final int scanline;

		BufferedImageIterator_FromRaster(BufferedImage bi, boolean topDown) {
			super(bi, getRasterReturnType(bi), topDown);
			if (!(type == BufferedImage.TYPE_INT_ARGB
					|| type == BufferedImage.TYPE_INT_ARGB_PRE
					|| type == BufferedImage.TYPE_INT_BGR
					|| type == BufferedImage.TYPE_INT_RGB
					|| type == BufferedImage.TYPE_BYTE_GRAY
					|| type == BufferedImage.TYPE_3BYTE_BGR
					|| type == ImageType.TYPE_3BYTE_RGB
					|| type == ImageType.TYPE_4BYTE_RGBA
					|| type == ImageType.TYPE_4BYTE_RGBA_PRE)) {
				throw new IllegalArgumentException("The image type "
						+ ImageType.toString(type) + " is not supported.");
			}
			scanline = bi.getWidth() * ImageType.get(getType()).getSampleCount();
		}

		private Object spareArray;

		@Override
		public void next(T dest, int offset) {
			if (isClosed)
				throw new ClosedException();

			if (y >= h || y <= -1)
				throw new RuntimeException("end of data reached");

			if (offset == 0) {
				bi.getRaster().getDataElements(0, y, w, 1, dest);
			} else {
				if (spareArray == null) {
					if (ImageType.get(getType()).isInt()) {
						spareArray = new int[scanline];
					} else {
						spareArray = new byte[scanline];
					}
				}
				// yikes, this feels especially wasteful. This is why this we prefer _fromDataBuffer over this class
				bi.getRaster().getDataElements(0, y, w, 1, spareArray);
				System.arraycopy(spareArray, 0, dest, offset, scanline);
			}

			y += topDown ? 1 : -1;

			if (isDone())
				close();
		}
	}

	/**
	 * This iterates over pixels by directly accessing the DataBuffer
	 *
	 * You should only construct this if {@link #getDataBufferScanline(BufferedImage, boolean)} returns a positive value.
	 */
	static class BufferedImageIterator_FromDataBuffer<T> extends BufferedImageIterator<T> {
		private final Object srcData;
		private final int dataBufferOffset;
		private final int scanline;
		private final int rowLength, subimageY, subimageX;

		BufferedImageIterator_FromDataBuffer(BufferedImage bi, boolean topDown) {
			super(bi, bi.getType(), topDown);

			scanline = getDataBufferScanline(bi, true);
			dataBufferOffset = bi.getRaster().getDataBuffer().getOffset();
			if (bi.getRaster().getDataBuffer() instanceof DataBufferInt) {
				srcData = ((DataBufferInt)bi.getRaster().getDataBuffer()).getData();
			} else {
				srcData = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
			}
			rowLength = bi.getWidth() * getPixelSize();
			subimageX = -bi.getRaster().getSampleModelTranslateX();
			subimageY = -bi.getRaster().getSampleModelTranslateY();
		}

		@Override
		public void next(T dest, int destOffset) {
			if (isClosed)
				throw new ClosedException();

			System.arraycopy(srcData,
					dataBufferOffset + scanline * (y + subimageY) + subimageX,
					dest, destOffset, rowLength);

			if (topDown) {
				y++;
			} else {
				y--;
			}

			if (isDone())
				close();
		}
	}

	static class BufferedImageIndexedIterator extends
			BufferedImageIterator_FromRaster<byte[]> implements IndexedBytePixelIterator {

		BufferedImageIndexedIterator(BufferedImage bi, boolean topDown) {
			super(bi, topDown);
		}

		@Override
		public IndexColorModel getIndexColorModel() {
			return ((IndexColorModel) bi.getColorModel());
		}
	}

	@Override
	public int getHeight() {
		return h;
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public int getWidth() {
		return w;
	}

	@Override
	public boolean isDone() {
		if (topDown)
			return y >= h;
		return y < 0;
	}

	@Override
	public boolean isTopDown() {
		return topDown;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()+"[ image type = "+ImageType.toString(getType())+", width = " + getWidth() + ", height = "+ getHeight()+", isTopDown() = " + isTopDown() + "]";
	}

	public static BufferedImageIterator<?> create(BufferedImage bi) {
		return create(bi, true);
	}

	public static BufferedImageIterator<?> create(BufferedImage bi, boolean topDown) {
		return new Source(bi, topDown).createPixelIterator();
	}

	@Override
	public void skip() {
		if (isClosed)
			throw new ClosedException();

		if (topDown) {
			if (y >= h)
				throw new RuntimeException("end of data reached");
			y++;
		} else {
			if (y <= -1)
				throw new RuntimeException("end of data reached");
			y--;
		}

		if (isDone())
			close();
	}

	/**
	 * There is no need to explicitly close a BufferedImageIterator. There is
	 * nothing to close/cleanup. But once this is called: other methods
	 * will throw CloseExceptions.
	 */
	@Override
	public void close() {
		isClosed = true;
	}
}