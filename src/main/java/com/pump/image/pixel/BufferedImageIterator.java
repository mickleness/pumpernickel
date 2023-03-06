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

import java.awt.image.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * This interfaces the <code>PixelIterator</code> model with
 * <code>BufferedImages</code>.
 * <p>
 * You cannot directly instantiate this class: use one of the static
 * <code>get(...)</code> methods to create <code>BufferedImageIterators</code>.
 *
 */
public abstract class BufferedImageIterator<T> implements PixelIterator<T> {

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
	 * I don't fully understand this phenomenon, but for TYPE_3BYTE_BGR images:
	 * the pixel data is actually ordered as RGB. It gets flipped in the
	 * ByteInterleavedRaster (see ByteInterleavedRaster#inOrder).
	 * <p>
	 * This method inspects the BufferedImage's color model and returns an image
	 * type code that reflects the order pixel channels will really be stored with.
	 * </p>
	 */
	// TODO: either remove or integrate with buffer-based iterator
	private static int getRealByteType(BufferedImage bi) {
		int describedType = bi.getType();
		if (describedType == BufferedImage.TYPE_3BYTE_BGR) {
			byte[] array = new byte[] { 10, 20, 30 };
			int r = bi.getColorModel().getRed(array);
			int g = bi.getColorModel().getGreen(array);
			int b = bi.getColorModel().getBlue(array);

			if (r == 10 && g == 20 && b == 30) {
				return ImageType.TYPE_3BYTE_RGB;
			} else if (b == 10 && g == 20 && r == 30) {
				return BufferedImage.TYPE_3BYTE_BGR;
			}
			throw new IllegalStateException("unrecognized state: " + Arrays.toString(array) + ", r = " + r + ", g = "+ g + ", b = " + b);
		} else if (describedType == BufferedImage.TYPE_4BYTE_ABGR
				|| describedType == BufferedImage.TYPE_4BYTE_ABGR_PRE) {
			byte[] array = new byte[] { 10, 20, 30, 40 };
			int r = bi.getColorModel().getRed(array);
			int g = bi.getColorModel().getGreen(array);
			int b = bi.getColorModel().getBlue(array);
			int a = bi.getColorModel().getAlpha(array);


			if (a == 10 && r == 20 && g == 30 && b == 40) {
				return ImageType.TYPE_4BYTE_ARGB;
			} else if (a == 10 && r == 20  && g == 30 && b == 40) {
				// TODO: what exactly would RGB values be here?
				return ImageType.TYPE_4BYTE_ARGB_PRE;
			} else if (b == 10 && g == 20 && r == 30 && a == 40) {
				return ImageType.TYPE_4BYTE_BGRA;
			} else if (r == 10 && g == 20 && b == 30 && a == 40) {
				return ImageType.TYPE_4BYTE_RGBA;
			}
			throw new IllegalStateException("unrecognized state: " + Arrays.toString(array) + ", r = " + r + ", g = "+ g + ", b = " + b + ", a = "+ a);
		}
		return describedType;
	}


	/**
	 * Creates a BufferedImage from a PixelIterator.
	 * 
	 * @param i
	 *            the pixel data
	 * @param dest
	 *            an optional image to write the image data to.
	 * @return a BufferedImage
	 */
	public static BufferedImage create(PixelIterator<?> i, BufferedImage dest) {
		int type = i.getType();

		int w = i.getWidth();
		int h = i.getHeight();

		if (dest != null) {
			if (dest.getType() != type)
				throw new IllegalArgumentException("types mismatch ("
						+ dest.getType() + "!=" + type + ")");
			if (dest.getWidth() < w)
				throw new IllegalArgumentException("size mismatch ("
						+ dest.getWidth() + "x" + dest.getHeight()
						+ " is too small for " + w + "x" + h + ")");
		} else if (i instanceof IndexedBytePixelIterator) {
			IndexColorModel indexModel = ((IndexedBytePixelIterator) i)
					.getIndexColorModel();
			dest = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_INDEXED,
					indexModel);
		} else {
			int imageType = type;
			if (type == ImageType.TYPE_4BYTE_ARGB || type == ImageType.TYPE_4BYTE_BGRA || type == ImageType.TYPE_4BYTE_RGBA)
				imageType = BufferedImage.TYPE_4BYTE_ABGR;
			if (type == ImageType.TYPE_4BYTE_RGBA_PRE)
				imageType = BufferedImage.TYPE_4BYTE_ABGR_PRE;
			if (type == ImageType.TYPE_3BYTE_RGB)
				imageType = BufferedImage.TYPE_3BYTE_BGR;
			dest = new BufferedImage(w, h, imageType);
		}

		if (i.isInt()) {
			PixelIterator<int[]> ipi = (PixelIterator<int[]>) i;
			int[] row = new int[i.getMinimumArrayLength()];
			if (i.isTopDown()) {
				for (int y = 0; y < h; y++) {
					ipi.next(row);
					dest.getRaster().setDataElements(0, y, w, 1, row);
				}
			} else {
				for (int y = h - 1; y >= 0; y--) {
					ipi.next(row);
					dest.getRaster().setDataElements(0, y, w, 1, row);
				}
			}
		} else {
			PixelIterator<byte[]> bpi = (PixelIterator<byte[]>) i;

			byte[] row = new byte[i.getMinimumArrayLength()];
			if (bpi.isTopDown()) {
				for (int y = 0; y < h; y++) {
					bpi.next(row);
					dest.getRaster().setDataElements(0, y, w, 1, row);
				}
			} else {
				for (int y = h - 1; y >= 0; y--) {
					bpi.next(row);
					dest.getRaster().setDataElements(0, y, w, 1, row);
				}
			}
		}
		return dest;
	}

	final BufferedImage bi;
	final int type;
	final boolean topDown;
	final int w, h;
	int y;

	private BufferedImageIterator(BufferedImage bi, boolean topDown) {
		this(bi, bi.getType(), topDown);
	}

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
		}

		@Override
		public void next(T dest) {
			if (topDown) {
				if (y >= h)
					throw new RuntimeException("end of data reached");
				bi.getRaster().getDataElements(0, y, w, 1, dest);
				y++;
			} else {
				if (y <= -1)
					throw new RuntimeException("end of data reached");
				bi.getRaster().getDataElements(0, y, w, 1, dest);
				y--;
			}
		}
	}

	// TODO implement / test
	static class BufferedImageByteIterator_FromDataBuffer extends BufferedImageIterator<byte[]> {

		BufferedImageByteIterator_FromDataBuffer(BufferedImage bi, int imageTypeCode, boolean topDown) {
			super(bi, imageTypeCode, topDown);
		}

		@Override
		public void next(byte[] dest) {

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
	public int getMinimumArrayLength() {
		return getWidth() * getPixelSize();
	}

	@Override
	public void skip() {
		if (topDown) {
			if (y >= h)
				throw new RuntimeException("end of data reached");
			y++;
		} else {
			if (y <= -1)
				throw new RuntimeException("end of data reached");
			y--;
		}
	}
}