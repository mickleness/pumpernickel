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
package com.pump.image.pixel;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This interfaces the <code>PixelIterator</code> model with
 * <code>BufferedImages</code>.
 * <p>
 * You cannot directly instantiate this class: use one of the static
 * <code>get(...)</code> methods to create <code>BufferedImageIterators</code>.
 *
 */
public abstract class BufferedImageIterator<T> implements PixelIterator<T> {
	static class RGBtoBGR implements BytePixelIterator {
		final BytePixelIterator bpi;

		RGBtoBGR(BytePixelIterator bpi) {
			this.bpi = bpi;
		}

		@Override
		public int getType() {
			return bpi.getType();
		}

		@Override
		public boolean isOpaque() {
			return bpi.isOpaque();
		}

		@Override
		public boolean isDone() {
			return bpi.isDone();
		}

		@Override
		public boolean isTopDown() {
			return bpi.isTopDown();
		}

		@Override
		public int getWidth() {
			return bpi.getWidth();
		}

		@Override
		public int getHeight() {
			return bpi.getHeight();
		}

		@Override
		public int getMinimumArrayLength() {
			return bpi.getMinimumArrayLength();
		}

		@Override
		public void skip() {
			bpi.skip();
		}

		@Override
		public void next(byte[] dest) {
			bpi.next(dest);
			int w = getWidth();
			for (int x = 0; x < w; x++) {
				byte t = dest[3 * x];
				dest[3 * x] = dest[3 * x + 2];
				dest[3 * x + 2] = t;
			}
		}
	}

	static class ARGBtoABGR implements BytePixelIterator {
		final BytePixelIterator bpi;

		ARGBtoABGR(BytePixelIterator bpi) {
			this.bpi = bpi;
		}

		@Override
		public int getType() {
			return bpi.getType();
		}

		@Override
		public boolean isOpaque() {
			return bpi.isOpaque();
		}

		@Override
		public boolean isDone() {
			return bpi.isDone();
		}

		@Override
		public boolean isTopDown() {
			return bpi.isTopDown();
		}

		@Override
		public int getWidth() {
			return bpi.getWidth();
		}

		@Override
		public int getHeight() {
			return bpi.getHeight();
		}

		@Override
		public int getMinimumArrayLength() {
			return bpi.getMinimumArrayLength();
		}

		@Override
		public void skip() {
			bpi.skip();
		}

		@Override
		public void next(byte[] dest) {
			bpi.next(dest);
			int w = getWidth();
			for (int x = 0; x < w; x++) {
				byte t = dest[4 * x];
				dest[4 * x] = dest[4 * x + 2];
				dest[4 * x + 2] = t;
			}
		}
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
			if (type == PixelIterator.TYPE_4BYTE_ARGB)
				imageType = BufferedImage.TYPE_4BYTE_ABGR;
			if (type == PixelIterator.TYPE_4BYTE_ARGB_PRE)
				imageType = BufferedImage.TYPE_4BYTE_ABGR_PRE;
			if (type == PixelIterator.TYPE_3BYTE_RGB)
				imageType = BufferedImage.TYPE_3BYTE_BGR;
			dest = new BufferedImage(w, h, imageType);
		}

		if (i instanceof IntPixelIterator) {
			IntPixelIterator ipi = (IntPixelIterator) i;
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
			BytePixelIterator bpi = (BytePixelIterator) i;

			/**
			 * BMPs are considered "BGR" data: the color components will be
			 * unloaded as array = {blue1, green1, red1, blue2, green2, red2,
			 * ...} However if we dump them into a BufferedImage with this type:
			 * they appear backwards. I don't know why this happens, but let's
			 * fix it here:
			 */
			if (type == BufferedImage.TYPE_3BYTE_BGR) {
				bpi = new RGBtoBGR(bpi);
			} else if (type == BufferedImage.TYPE_4BYTE_ABGR
					|| type == BufferedImage.TYPE_4BYTE_ABGR_PRE) {
				bpi = new ARGBtoABGR(bpi);
			}

			byte[] row = new byte[i.getMinimumArrayLength()];
			if (i.isTopDown()) {
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

	static class BufferedImageIntIterator extends BufferedImageIterator<int[]>
			implements IntPixelIterator {
		BufferedImageIntIterator(BufferedImage bi, boolean topDown) {
			super(bi, topDown);
			if (!(type == BufferedImage.TYPE_INT_ARGB
					|| type == BufferedImage.TYPE_INT_ARGB_PRE
					|| type == BufferedImage.TYPE_INT_BGR
					|| type == BufferedImage.TYPE_INT_RGB)) {
				throw new IllegalArgumentException("The image type "
						+ getTypeName(type) + " is not supported.");
			}
		}

		@Override
		public void next(int[] dest) {
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

	private static int getRealType(BufferedImage bi) {
		int describedType = bi.getType();
		if (describedType == BufferedImage.TYPE_3BYTE_BGR) {
			byte[] array = new byte[] { 100, 50, 10 };
			int r = bi.getColorModel().getRed(array);
			int g = bi.getColorModel().getGreen(array);
			int b = bi.getColorModel().getBlue(array);

			if (r == 100 && g == 50 && b == 10) {
				return TYPE_3BYTE_RGB;
			}
			return BufferedImage.TYPE_3BYTE_BGR;
		} else if (describedType == BufferedImage.TYPE_4BYTE_ABGR
				|| describedType == BufferedImage.TYPE_4BYTE_ABGR_PRE) {
			byte[] array = new byte[] { -128, 100, 50, 10 };
			int r = bi.getColorModel().getRed(array);
			int g = bi.getColorModel().getGreen(array);
			int b = bi.getColorModel().getBlue(array);

			if (r == 100 && g == 50 && b == 10) {
				if (describedType == BufferedImage.TYPE_4BYTE_ABGR) {
					return TYPE_4BYTE_ARGB;
				}
				return TYPE_4BYTE_ARGB_PRE;
			} else if (r == 128 && g == 100 && b == 50) {
				return TYPE_4BYTE_BGRA;
			}
			return describedType;
		}
		return describedType;
	}

	static class BufferedImageByteIterator extends BufferedImageIterator<byte[]>
			implements BytePixelIterator {
		int pixelSize;

		BufferedImageByteIterator(BufferedImage bi, boolean topDown) {
			super(bi, getRealType(bi), topDown);
			if (type == BufferedImage.TYPE_3BYTE_BGR
					|| type == PixelIterator.TYPE_3BYTE_RGB) {
				pixelSize = 3;
			} else if (type == BufferedImage.TYPE_4BYTE_ABGR
					|| type == BufferedImage.TYPE_4BYTE_ABGR_PRE
					|| type == PixelIterator.TYPE_4BYTE_BGRA
					|| type == PixelIterator.TYPE_4BYTE_ARGB
					|| type == PixelIterator.TYPE_4BYTE_ARGB_PRE) {
				pixelSize = 4;
			} else if (type == BufferedImage.TYPE_BYTE_GRAY
					|| type == BufferedImage.TYPE_BYTE_INDEXED) {
				pixelSize = 1;
			} else {
				throw new IllegalArgumentException("The image type "
						+ getTypeName(type) + " is not supported.");
			}
		}

		@Override
		public void next(byte[] dest) {
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

		@Override
		public int getPixelSize() {
			return pixelSize;
		}
	}

	static class BufferedImageIndexedByteIterator extends
			BufferedImageByteIterator implements IndexedBytePixelIterator {

		BufferedImageIndexedByteIterator(BufferedImage bi, boolean topDown) {
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

	/**
	 * Used for exceptions, this method retrieved the field name from
	 * BufferedImage whose constant matches the argument. For example, if you
	 * call: <BR>
	 * <code>getTypeName(myBufferedImage.getType());</code> <BR>
	 * ... then this may return the string "TYPE_INT_ARGB_PRE".
	 * <P>
	 * If the correct name cannot be determined (because it does not exist, or a
	 * security exception is thrown), then a string representation of the
	 * argument is returned.
	 * 
	 * @param type
	 *            a type of image
	 * @return a more human-readable description of the image type, hopefully.
	 */
	public static String getTypeName(int type) {
		try {
			String s = getTypeName(type, BufferedImage.class);
			if (s == null)
				s = getTypeName(type, PixelIterator.class);
			if (s != null)
				return s;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return Integer.toString(type);
	}

	private static String getTypeName(int type, Class t) throws Throwable {
		Field[] f = t.getFields();
		for (int a = 0; a < f.length; a++) {
			if ((f[a].getModifiers() & Modifier.STATIC) > 0
					&& f[a].getType() == Integer.TYPE) {
				if (((Number) f[a].get(null)).intValue() == type)
					return t.getSimpleName() + "." + f[a].getName();
			}
		}
		return null;
	}

	public static BufferedImageIterator<?> get(BufferedImage bi) {
		return get(bi, true);
	}

	public static BufferedImageIterator<?> get(BufferedImage bi,
			boolean topDown) {
		int type = bi.getType();
		if (type == BufferedImage.TYPE_INT_ARGB
				|| type == BufferedImage.TYPE_INT_ARGB_PRE
				|| type == BufferedImage.TYPE_INT_BGR
				|| type == BufferedImage.TYPE_INT_RGB) {
			return new BufferedImageIntIterator(bi, topDown);
		} else if (type == BufferedImage.TYPE_BYTE_INDEXED) {
			return new BufferedImageIndexedByteIterator(bi, topDown);
		} else if (type == BufferedImage.TYPE_3BYTE_BGR
				|| type == BufferedImage.TYPE_4BYTE_ABGR
				|| type == BufferedImage.TYPE_4BYTE_ABGR_PRE
				|| type == BufferedImage.TYPE_BYTE_GRAY) {
			return new BufferedImageByteIterator(bi, topDown);
		} else {
			throw new IllegalArgumentException(
					"unsupported image type: " + bi.getType());
		}
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