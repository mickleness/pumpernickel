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

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * This iterator scales another iterator as it is being read.
 * <p>
 * You cannot directly instantiate this class, but you can use the static
 * <code>get()</code> methods or instantiate the two subclasses:
 * <code>IntScalingIterator</code> or <code>ByteScalingIterator</code>.
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2010/06/images-scaling-down.html">Images:
 *      Scaling Down</a>
 */
public abstract class ScalingIterator<T> implements PixelIterator<T> {

	/**
	 * Returns a <code>IntPixelIterator</code> scaled to a specific ratio.
	 * <p>
	 * If the ratio is 1.0: then this method returns the argument provided.
	 * Otherwise a new scaled iterator is returned.
	 * <p>
	 * If the image type of the source image is one of the four supported
	 * int-based types (ARGB, ARGB_PRE, RGB, or BGR) then the new scaled
	 * iterator will also use that type. Otherwise the new iterator will use
	 * ARGB or RGB (depending on whether the source image is opaque or not).
	 * 
	 * @param i
	 *            the incoming source image data.
	 * @param scaledRatio
	 *            the ratio to scale to, where 1.0 is 100%, .5 is 50%, etc.
	 * @return a scaled iterator.
	 */
	public static IntScalingIterator get(IntPixelIterator i,
			float scaledRatio) {
		return (IntScalingIterator) get((PixelIterator) i, scaledRatio);
	}

	/**
	 * Returns a <code>BytePixelIterator</code> scaled to a specific ratio.
	 * <p>
	 * If the ratio is 1.0: then this method returns the argument provided.
	 * Otherwise a new scaled iterator is returned.
	 * <p>
	 * If the image type of the source image is one of the four supported
	 * byte-based types (ABGR, ABGR_PRE, BGR, or GRAY) then the new scaled
	 * iterator will also use that type. Otherwise the new iterator will use
	 * ABGR or BGR (depending on whether the source image is opaque or not).
	 * 
	 * @param i
	 *            the incoming source image data.
	 * @param scaledRatio
	 *            the ratio to scale to, where 1.0 is 100%, .5 is 50%, etc.
	 * @return a scaled iterator.
	 */
	public static ByteScalingIterator get(BytePixelIterator i,
			float scaledRatio) {
		return (ByteScalingIterator) get((PixelIterator) i, scaledRatio);
	}

	/**
	 * Returns a <code>PixelIterator</code> scaled to a specific ratio.
	 * <p>
	 * If the ratio is 1.0: then this method returns the argument provided.
	 * Otherwise a new scaled iterator is returned.
	 * <p>
	 * If the image type of the source image is one of the eight supported
	 * types: then the new scaled iterator will also use that type. Otherwise
	 * the new iterator will use ABGR / BGR (for bytes) or ARGB / RGB (for
	 * ints).
	 * 
	 * @param i
	 *            the incoming source image data.
	 * @param scaledRatio
	 *            the ratio to scale to, where 1.0 is 100%, .5 is 50%, etc.
	 * @return a scaled iterator.
	 */
	public static PixelIterator get(PixelIterator i, float scaledRatio) {
		int newWidth = (int) (scaledRatio * i.getWidth());
		int newHeight = (int) (scaledRatio * i.getHeight());
		return get(i, newWidth, newHeight);
	}

	/**
	 * Checks the incoming image type against the 4 int types this class
	 * supports.
	 */
	protected static boolean isSupportedIntType(int type) {
		return (type == BufferedImage.TYPE_INT_ARGB
				|| type == BufferedImage.TYPE_INT_ARGB_PRE
				|| type == BufferedImage.TYPE_INT_BGR
				|| type == BufferedImage.TYPE_INT_RGB);
	}

	/**
	 * Checks the incoming image type against the 4 byte types this class
	 * supports.
	 */
	protected static boolean isSupportedByteType(int type) {
		return (type == BufferedImage.TYPE_3BYTE_BGR
				|| type == BufferedImage.TYPE_4BYTE_ABGR
				|| type == BufferedImage.TYPE_4BYTE_ABGR_PRE
				|| type == ImageType.TYPE_3BYTE_RGB
				|| type == ImageType.TYPE_4BYTE_ARGB
				|| type == ImageType.TYPE_4BYTE_ARGB_PRE
				|| type == BufferedImage.TYPE_BYTE_GRAY);
	}

	/**
	 * Returns a <code>IntPixelIterator</code> with a fixed width and height.
	 * <p>
	 * If the source image is already the width and height specified: then this
	 * method returns the argument provided. Otherwise a new scaled iterator is
	 * returned.
	 * <p>
	 * If the image type of the source image is one of the four supported
	 * int-based types (ARGB, ARGB_PRE, RGB, or BGR) then the new scaled
	 * iterator will also use that type. Otherwise the new iterator will use
	 * ARGB or RGB (depending on whether the source image is opaque or not).
	 * 
	 * @param i
	 *            the incoming source image data.
	 * @param newWidth
	 *            the new width.
	 * @param newHeight
	 *            the new height.
	 * @return an iterator that uses the width and height specified.
	 */
	public static PixelIterator<int[]> getFromInt(PixelIterator<int[]> i, int newWidth,
			int newHeight) {
		if (i.getWidth() == newWidth && i.getHeight() == newHeight)
			return i;
		int imageType = i.getType();
		if (isSupportedIntType(imageType) == false) {
			if (i.isOpaque()) {
				imageType = BufferedImage.TYPE_INT_RGB;
			} else {
				imageType = BufferedImage.TYPE_INT_ARGB;
			}
		}
		return new IntScalingIterator(i, imageType, newWidth, newHeight);
	}

	/**
	 * Returns a <code>BytePixelIterator</code> with a fixed width and height.
	 * <p>
	 * If the source image is already the width and height specified: then this
	 * method returns the argument provided. Otherwise a new scaled iterator is
	 * returned.
	 * <p>
	 * If the image type of the source image is one of the four supported
	 * byte-based types (ABGR, ABGR_PRE, BGR, or GRAY) then the new scaled
	 * iterator will also use that type. Otherwise the new iterator will use
	 * ABGR or BGR (depending on whether the source image is opaque or not).
	 * 
	 * @param i
	 *            the incoming source image data.
	 * @param newWidth
	 *            the new width.
	 * @param newHeight
	 *            the new height.
	 * @return an iterator that uses the width and height specified.
	 */
	private static PixelIterator<byte[]> getFromByte(PixelIterator<byte[]> i, int newWidth,
			int newHeight) {
		if (i.getWidth() == newWidth && i.getHeight() == newHeight)
			return i;
		int imageType = i.getType();
		if (isSupportedByteType(imageType) == false) {
			if (i.isOpaque()) {
				imageType = BufferedImage.TYPE_3BYTE_BGR;
			} else {
				imageType = BufferedImage.TYPE_4BYTE_ABGR;
			}
		}
		return new ByteScalingIterator(i, imageType, newWidth, newHeight);
	}

	/**
	 * Returns a <code>PixelIterator</code> with a fixed width and height.
	 * <p>
	 * If the source image is already the width and height specified: then this
	 * method returns the argument provided. Otherwise a new scaled iterator is
	 * returned.
	 * <p>
	 * If the image type of the source image is one of the eight supported
	 * types: then the new scaled iterator will also use that type. Otherwise
	 * the new iterator will use ABGR / BGR (for bytes) or ARGB / RGB (for
	 * ints).
	 * 
	 * @param i
	 *            the incoming source image data.
	 * @param newWidth
	 *            the new width.
	 * @param newHeight
	 *            the new height.
	 * @return an iterator that uses the width and height specified.
	 */
	public static <T> PixelIterator<T> get(PixelIterator<T> i, int newWidth,
			int newHeight) {
		ImageType imageType = ImageType.get(i.getType());
		if (imageType.isByte()) {
			return (PixelIterator<T>) getFromByte( (PixelIterator<byte[]>) i, newWidth, newHeight);
		} else if (imageType.isInt()) {
			return (PixelIterator<T>) getFromInt( (PixelIterator<int[]>) i, newWidth, newHeight);
		}
		throw new IllegalArgumentException(
				"Unsupported iterator: " + i.getClass().getName());
	}

	public static class ByteScalingIterator extends ScalingIterator<byte[]>
			implements BytePixelIterator {
		final int imageType;

		/**
		 * Create a new <code>ByteScalingIterator</code>.
		 * 
		 * @param i
		 *            the incoming image data to scale.
		 * @param newImageType
		 *            this must be one of the four byte-based pixel types this
		 *            object supports: BGR, ABGR, ABGR_PRE, or GRAY.
		 * @param newWidth
		 *            the width to scale to.
		 * @param newHeight
		 *            the height to scale to.
		 */
		public ByteScalingIterator(PixelIterator i, int newImageType,
				int newWidth, int newHeight) {
			super(i, i.getWidth(), i.getHeight(), newWidth, newHeight,
					i.isTopDown(), i.isOpaque());
			this.imageType = newImageType;

			if (isSupportedByteType(imageType) == false)
				throw new IllegalArgumentException(
						"the image type for this contructor must be TYPE_3BYTE_BGR, TYPE_4BYTE_ABGR, TYPE_4BYTE_ABGR_PRE or TYPE_BYTE_GRAY. (newImageType = "
								+ newImageType + ")");
		}

		@Override
		public void next(byte[] dest) {
			next(dest, null);
		}

		@Override
		public int getMinimumArrayLength() {
			int bytesPerPixel = getPixelSize();
			ImageType type = ImageType.get(imageType);
			int returnValue = dstW * bytesPerPixel;
			if (type.isByte()) {
				returnValue = Math.max(srcIterator.getMinimumArrayLength(), returnValue);
			}
			return returnValue;
		}

		@Override
		public int getType() {
			return imageType;
		}
	}

	public static class IntScalingIterator extends ScalingIterator<int[]>
			implements IntPixelIterator {
		final int imageType;

		/**
		 * Create a new <code>IntScalingIterator</code>.
		 * 
		 * @param i
		 *            the incoming image data to scale.
		 * @param newImageType
		 *            this must be one of the four int-based pixel types this
		 *            object supports: ARGB, ARGB_PRE, RGB or BGR.
		 * @param newWidth
		 *            the width to scale to.
		 * @param newHeight
		 *            the height to scale to.
		 */
		public IntScalingIterator(PixelIterator i, int newImageType,
				int newWidth, int newHeight) {
			super(i, i.getWidth(), i.getHeight(), newWidth, newHeight,
					i.isTopDown(), i.isOpaque());
			this.imageType = newImageType;

			if (isSupportedIntType(newImageType) == false)
				throw new IllegalArgumentException(
						"the image type for this constructor must be TYPE_INT_ARGB, TYPE_INT_ARGB_PRE, TYPE_INT_BGR, or TYPE_INT_RGB. (newImageType = "
								+ newImageType + ")");
		}

		@Override
		public void next(int[] dest) {
			next(null, dest);
		}

		@Override
		public int getMinimumArrayLength() {
			ImageType type = ImageType.get(getType());
			if (type.isInt()) {
				return Math.max(srcIterator.getMinimumArrayLength(), dstW);
			}
			return dstW;
		}

		@Override
		public int getPixelSize() {
			return 1;
		}

		@Override
		public int getType() {
			return imageType;
		}
	}

	final int srcW, srcH, dstW, dstH;
	final PixelIterator srcIterator;
	int dstY;
	int srcY = 0;

	static abstract class Row {
		/**
		 * This indicates the source row this Row object corresponds to. This is
		 * only used for upsampling.
		 */
		int marker = -1;

		/**
		 * Add the color components from the source row to this object.
		 */
		abstract void readColorComponents(int[] sourceArray, int type);

		/**
		 * Add the color components from the source row to this object.
		 */
		abstract void readColorComponents(byte[] sourceArray, int type);

		/**
		 * Write the color components from this object to the array provided.
		 */
		abstract void writeColorComponents(int[] destArray, int type);

		/**
		 * Write the color components from this object to the array provided.
		 */
		abstract void writeColorComponents(byte[] destArray, int type);

		/**
		 * Write the color components from this object to the array provided,
		 * while tweening between this row and the next row.
		 * 
		 * @nextRow it is assumed this argument will be of the same class as
		 *          this object.
		 * @fraction the amount of this row vs the argument to use. When zero:
		 *           only this object is used, when one: only the argument is
		 *           used.
		 */
		abstract void writeColorComponents(Row nextRow, double fraction,
				int[] destArray, int type);

		/**
		 * Write the color components from this object to the array provided,
		 * while tweening between this row and the next row.
		 * 
		 * @nextRow it is assumed this argument will be of the same class as
		 *          this object.
		 * @fraction the amount of this row vs the argument to use. When zero:
		 *           only this object is used, when one: only the argument is
		 *           used.
		 */
		abstract void writeColorComponents(Row nextRow, double fraction,
				byte[] destArray, int type);

		abstract void interpolateXValues();

		abstract void clear();
	}

	class IntRow extends Row {
		int[] reds, greens, blues, alphas, sums;
		boolean isOpaque;
		int width;

		IntRow(int width, boolean isOpaque) {
			this.width = width;
			this.isOpaque = isOpaque;
			reds = new int[width];
			greens = new int[width];
			blues = new int[width];
			if (isOpaque == false)
				alphas = new int[width];
			sums = new int[width];
		}

		@Override
		void clear() {
			Arrays.fill(reds, 0, width, 0);
			Arrays.fill(greens, 0, width, 0);
			Arrays.fill(blues, 0, width, 0);
			Arrays.fill(sums, 0, width, 0);
			if (alphas != null)
				Arrays.fill(alphas, 0, width, 0);
		}

		@Override
		void writeColorComponents(byte[] destArray, int type) {
			switch (type) {
			case BufferedImage.TYPE_3BYTE_BGR:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					destArray[k2++] = (byte) (blues[x] / sums[x]);
					destArray[k2++] = (byte) (greens[x] / sums[x]);
					destArray[k2++] = (byte) (reds[x] / sums[x]);
				}
				break;
			case ImageType.TYPE_3BYTE_RGB:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					destArray[k2++] = (byte) (reds[x] / sums[x]);
					destArray[k2++] = (byte) (greens[x] / sums[x]);
					destArray[k2++] = (byte) (blues[x] / sums[x]);
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					destArray[k2++] = isOpaque ? -127
							: (byte) (alphas[x] / sums[x]);
					destArray[k2++] = (byte) (blues[x] / sums[x]);
					destArray[k2++] = (byte) (greens[x] / sums[x]);
					destArray[k2++] = (byte) (reds[x] / sums[x]);
				}
				break;
			case ImageType.TYPE_4BYTE_ARGB:
			case ImageType.TYPE_4BYTE_ARGB_PRE:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					destArray[k2++] = isOpaque ? -127
							: (byte) (alphas[x] / sums[x]);
					destArray[k2++] = (byte) (reds[x] / sums[x]);
					destArray[k2++] = (byte) (greens[x] / sums[x]);
					destArray[k2++] = (byte) (blues[x] / sums[x]);
				}
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				for (int x = 0; x < dstW; x++) {
					int r = reds[x];
					int g = greens[x];
					int b = blues[x];
					destArray[x] = (byte) ((r + g + b) / (3 * sums[x]));
				}
				break;
			default:
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}
		}

		@Override
		void readColorComponents(byte[] sourceArray, int type) {
			int incr = scaleX < .25 ? 2 : 1;
			int kIncr;
			switch (type) {
			case BufferedImage.TYPE_3BYTE_BGR:
				kIncr = 3 * incr - 3;
				for (int x = 0, k2 = 0; x < srcW; x += incr, k2 += kIncr) {
					int k = srcXLUT[x];
					blues[k] += sourceArray[k2++] & 0xff;
					greens[k] += sourceArray[k2++] & 0xff;
					reds[k] += sourceArray[k2++] & 0xff;
					sums[k]++;
				}
				break;
			case ImageType.TYPE_3BYTE_RGB:
				kIncr = 3 * incr - 3;
				for (int x = 0, k2 = 0; x < srcW; x += incr, k2 += kIncr) {
					int k = srcXLUT[x];
					reds[k] += sourceArray[k2++] & 0xff;
					greens[k] += sourceArray[k2++] & 0xff;
					blues[k] += sourceArray[k2++] & 0xff;
					sums[k]++;
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				kIncr = 4 * incr - 4;
				for (int x = 0, k2 = 0; x < srcW; x += incr, k2 += kIncr) {
					int k = srcXLUT[x];
					if (alphas != null)
						alphas[k] += isOpaque ? 255 : sourceArray[k2] & 0xff;
					k2++;
					blues[k] += sourceArray[k2++] & 0xff;
					greens[k] += sourceArray[k2++] & 0xff;
					reds[k] += sourceArray[k2++] & 0xff;
					sums[k]++;
				}
				break;
			case ImageType.TYPE_4BYTE_BGRA:
				kIncr = 4 * incr - 4;
				for (int x = 0, k2 = 0; x < srcW; x += incr, k2 += kIncr) {
					int k = srcXLUT[x];
					reds[k] += sourceArray[k2++] & 0xff;
					greens[k] += sourceArray[k2++] & 0xff;
					blues[k] += sourceArray[k2++] & 0xff;
					if (alphas != null) {
						alphas[k] += isOpaque ? 255 : sourceArray[k2] & 0xff;
					}
					k2++;
					sums[k]++;
				}
				break;
			case ImageType.TYPE_4BYTE_ARGB:
			case ImageType.TYPE_4BYTE_ARGB_PRE:
				kIncr = 4 * incr - 4;
				for (int x = 0, k2 = 0; x < srcW; x += incr, k2 += kIncr) {
					int k = srcXLUT[x];
					if (alphas != null)
						alphas[k] += isOpaque ? 255 : sourceArray[k2] & 0xff;
					k2++;
					reds[k] += sourceArray[k2++] & 0xff;
					greens[k] += sourceArray[k2++] & 0xff;
					blues[k] += sourceArray[k2++] & 0xff;
					sums[k]++;
				}
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				for (int x = 0; x < srcW; x += incr) {
					int k = srcXLUT[x];
					int v = sourceArray[x] & 0xff;
					reds[k] += v;
					sums[k]++;
				}
				break;
			default:
				// types BYTE_ABGR and INT_ARGB_PRE aren't supporte yet.
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}
		}

		@Override
		void readColorComponents(int[] sourceArray, int type) {
			int incr = scaleX < .25 ? 2 : 1;
			switch (type) {
			case BufferedImage.TYPE_INT_RGB:
				for (int x = 0; x < srcW; x += incr) {
					int k = srcXLUT[x];
					reds[k] += (sourceArray[x] >> 16) & 0xff;
					greens[k] += (sourceArray[x] >> 8) & 0xff;
					blues[k] += (sourceArray[x]) & 0xff;
					sums[k]++;
				}
				break;
			case BufferedImage.TYPE_INT_BGR:
				for (int x = 0; x < srcW; x += incr) {
					int k = srcXLUT[x];
					reds[k] += (sourceArray[x]) & 0xff;
					greens[k] += (sourceArray[x] >> 8) & 0xff;
					blues[k] += (sourceArray[x] >> 16) & 0xff;
					sums[k]++;
				}
				break;
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				for (int x = 0; x < srcW; x += incr) {
					int k = srcXLUT[x];
					if (alphas != null)
						alphas[k] += isOpaque ? 255
								: (sourceArray[x] >> 24) & 0xff;
					reds[k] += (sourceArray[x] >> 16) & 0xff;
					greens[k] += (sourceArray[x] >> 8) & 0xff;
					blues[k] += (sourceArray[x]) & 0xff;
					sums[k]++;
				}
				break;
			default:
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}
		}

		@Override
		void writeColorComponents(int[] destArray, int type) {
			switch (type) {
			case BufferedImage.TYPE_INT_RGB:
				for (int x = 0; x < width; x++) {
					destArray[x] = ((reds[x] / sums[x]) << 16)
							| ((greens[x] / sums[x]) << 8)
							| ((blues[x] / sums[x]));
				}
				break;
			case BufferedImage.TYPE_INT_BGR:
				for (int x = 0; x < width; x++) {
					destArray[x] = ((reds[x] / sums[x]))
							| ((greens[x] / sums[x]) << 8)
							| ((blues[x] / sums[x]) << 16);
				}
				break;
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				for (int x = 0; x < width; x++) {
					int alpha = isOpaque ? 255 : alphas[x] / sums[x];
					destArray[x] = ((alpha) << 24) | ((reds[x] / sums[x]) << 16)
							| ((greens[x] / sums[x]) << 8)
							| ((blues[x] / sums[x]));
				}
				break;
			default:
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}
		}

		@Override
		void interpolateXValues() {
			int lastRed = 0;
			int lastGreen = 0;
			int lastBlue = 0;
			int lastAlpha = 0;
			int newRed, newGreen, newBlue, newAlpha;
			for (int x = 0; x < width; x++) {
				switch (sums[x]) {
				case 0:
					if (x > 0) {
						lastRed = reds[x - 1] / sums[x - 1];
						lastGreen = greens[x - 1] / sums[x - 1];
						lastBlue = blues[x - 1] / sums[x - 1];
						lastAlpha = isOpaque ? 255
								: alphas[x - 1] / sums[x - 1];
					}
					int startX = x;
					int span = 1;
					while (x < sums.length && sums[x] == 0) {
						x++;
						span++;
					}
					if (x < sums.length) {
						newAlpha = isOpaque ? 255 : alphas[x] / sums[x];
						newRed = reds[x] / sums[x];
						newGreen = greens[x] / sums[x];
						newBlue = blues[x] / sums[x];
					} else {
						// erm, hopefully this isn't happening, but just in
						// case.
						newAlpha = lastAlpha;
						newRed = lastRed;
						newGreen = lastGreen;
						newBlue = lastBlue;
					}

					if (x == 0) {
						// and hopefully this isn't happening either
						lastRed = newRed;
						lastGreen = newGreen;
						lastBlue = newBlue;
						lastAlpha = newAlpha;
					}

					for (int k = startX; k < x; k++) {
						int f = k - startX + 1;
						if (alphas != null)
							alphas[k] = isOpaque ? 255
									: ((lastAlpha * (span - f) + newAlpha * f)
											/ span);
						reds[k] = ((lastRed * (span - f) + newRed * f) / span);
						greens[k] = ((lastGreen * (span - f) + newGreen * f)
								/ span);
						blues[k] = ((lastBlue * (span - f) + newBlue * f)
								/ span);
						sums[k] = 1;
					}
					break;
				}
			}
		}

		@Override
		void writeColorComponents(Row nextRow, double fraction, int[] destArray,
				int type) {
			if (fraction > 1 || fraction < 0)
				throw new IllegalArgumentException(
						"fraction (" + fraction + ") must be within [0,1]");
			IntRow next = (IntRow) nextRow;

			int mult1 = (int) ((1 - fraction) * 255);
			int mult2 = 255 - mult1;
			switch (type) {
			case BufferedImage.TYPE_INT_RGB:
				for (int x = 0; x < width; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;
					destArray[x] = (r << 16) | (g << 8) | (b);
				}
				break;
			case BufferedImage.TYPE_INT_BGR:
				for (int x = 0; x < width; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;
					destArray[x] = (b << 16) | (g << 8) | (r);
				}
				break;
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				for (int x = 0; x < width; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;
					int a = isOpaque ? 255
							: (alphas[x] * m1 + next.alphas[x] * m2) >> 8;
					destArray[x] = (a << 24) | (r << 16) | (g << 8) | (b);
				}
				break;
			default:
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}

		}

		@Override
		void writeColorComponents(Row nextRow, double fraction,
				byte[] destArray, int type) {
			if (fraction > 1 || fraction < 0)
				throw new IllegalArgumentException(
						"fraction (" + fraction + ") must be within [0,1]");
			IntRow next = (IntRow) nextRow;

			int mult1 = (int) ((1 - fraction) * 255);
			int mult2 = 255 - mult1;

			switch (type) {
			case BufferedImage.TYPE_3BYTE_BGR:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;

					destArray[k2++] = (byte) (b);
					destArray[k2++] = (byte) (g);
					destArray[k2++] = (byte) (r);
				}
				break;
			case ImageType.TYPE_3BYTE_RGB:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;

					destArray[k2++] = (byte) (r);
					destArray[k2++] = (byte) (g);
					destArray[k2++] = (byte) (b);
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;

					int a = isOpaque ? 255
							: (alphas[x] * m1 + next.alphas[x] * m2) >> 8;
					destArray[k2++] = (byte) (a);
					destArray[k2++] = (byte) (b);
					destArray[k2++] = (byte) (g);
					destArray[k2++] = (byte) (r);
				}
				break;
			case ImageType.TYPE_4BYTE_ARGB:
			case ImageType.TYPE_4BYTE_ARGB_PRE:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;

					int a = isOpaque ? 255
							: (alphas[x] * m1 + next.alphas[x] * m2) >> 8;

					destArray[k2++] = (byte) (a);
					destArray[k2++] = (byte) (r);
					destArray[k2++] = (byte) (g);
					destArray[k2++] = (byte) (b);
				}
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				for (int x = 0; x < dstW; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;

					destArray[x] = (byte) ((r + g + b) / 3);
				}
				break;
			default:
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}
		}
	}

	class ShortRow extends Row {
		short[] reds, greens, blues, alphas, sums;
		boolean isOpaque;
		int width;

		ShortRow(int width, boolean isOpaque) {
			this.width = width;
			this.isOpaque = isOpaque;
			reds = new short[width];
			greens = new short[width];
			blues = new short[width];
			if (isOpaque == false)
				alphas = new short[width];
			sums = new short[width];
		}

		@Override
		void clear() {
			Arrays.fill(reds, 0, width, (short) 0);
			Arrays.fill(greens, 0, width, (short) 0);
			Arrays.fill(blues, 0, width, (short) 0);
			Arrays.fill(sums, 0, width, (short) 0);
			if (alphas != null)
				Arrays.fill(alphas, 0, width, (short) 0);
		}

		@Override
		void writeColorComponents(byte[] destArray, int type) {
			switch (type) {
			case BufferedImage.TYPE_3BYTE_BGR:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					destArray[k2++] = (byte) (blues[x] / sums[x]);
					destArray[k2++] = (byte) (greens[x] / sums[x]);
					destArray[k2++] = (byte) (reds[x] / sums[x]);
				}
				break;
			case ImageType.TYPE_3BYTE_RGB:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					destArray[k2++] = (byte) (reds[x] / sums[x]);
					destArray[k2++] = (byte) (greens[x] / sums[x]);
					destArray[k2++] = (byte) (blues[x] / sums[x]);
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					destArray[k2++] = isOpaque ? -127
							: (byte) (alphas[x] / sums[x]);
					destArray[k2++] = (byte) (blues[x] / sums[x]);
					destArray[k2++] = (byte) (greens[x] / sums[x]);
					destArray[k2++] = (byte) (reds[x] / sums[x]);
				}
				break;
			case ImageType.TYPE_4BYTE_ARGB:
			case ImageType.TYPE_4BYTE_ARGB_PRE:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					destArray[k2++] = isOpaque ? -127
							: (byte) (alphas[x] / sums[x]);
					destArray[k2++] = (byte) (reds[x] / sums[x]);
					destArray[k2++] = (byte) (greens[x] / sums[x]);
					destArray[k2++] = (byte) (blues[x] / sums[x]);
				}
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				for (int x = 0; x < dstW; x++) {
					int r = reds[x];
					int g = greens[x];
					int b = blues[x];
					destArray[x] = (byte) ((r + g + b) / (3 * sums[x]));
				}
				break;
			default:
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}
		}

		@Override
		void readColorComponents(byte[] sourceArray, int type) {
			int incr = scaleX < .25 ? 2 : 1;
			int kIncr;
			switch (type) {
			case BufferedImage.TYPE_3BYTE_BGR:
				kIncr = 3 * incr - 3;
				for (int x = 0, k2 = 0; x < srcW; x += incr, k2 += kIncr) {
					int k = srcXLUT[x];
					blues[k] += sourceArray[k2++] & 0xff;
					greens[k] += sourceArray[k2++] & 0xff;
					reds[k] += sourceArray[k2++] & 0xff;
					sums[k]++;
				}
				break;
			case ImageType.TYPE_3BYTE_RGB:
				kIncr = 3 * incr - 3;
				for (int x = 0, k2 = 0; x < srcW; x += incr, k2 += kIncr) {
					int k = srcXLUT[x];
					reds[k] += sourceArray[k2++] & 0xff;
					greens[k] += sourceArray[k2++] & 0xff;
					blues[k] += sourceArray[k2++] & 0xff;
					sums[k]++;
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				kIncr = 4 * incr - 4;
				for (int x = 0, k2 = 0; x < srcW; x += incr, k2 += kIncr) {
					int k = srcXLUT[x];
					if (alphas != null)
						alphas[k] += isOpaque ? 255 : sourceArray[k2] & 0xff;
					k2++;
					blues[k] += sourceArray[k2++] & 0xff;
					greens[k] += sourceArray[k2++] & 0xff;
					reds[k] += sourceArray[k2++] & 0xff;
					sums[k]++;
				}
				break;
			case ImageType.TYPE_4BYTE_BGRA:
				kIncr = 4 * incr - 4;
				for (int x = 0, k2 = 0; x < srcW; x += incr, k2 += kIncr) {
					int k = srcXLUT[x];
					reds[k] += sourceArray[k2++] & 0xff;
					greens[k] += sourceArray[k2++] & 0xff;
					blues[k] += sourceArray[k2++] & 0xff;
					if (alphas != null) {
						alphas[k] += isOpaque ? 255 : sourceArray[k2] & 0xff;
					}
					k2++;
					sums[k]++;
				}
				break;
			case ImageType.TYPE_4BYTE_ARGB:
			case ImageType.TYPE_4BYTE_ARGB_PRE:
				kIncr = 4 * incr - 4;
				for (int x = 0, k2 = 0; x < srcW; x += incr, k2 += kIncr) {
					int k = srcXLUT[x];
					if (alphas != null)
						alphas[k] += isOpaque ? 255 : sourceArray[k2] & 0xff;
					k2++;
					reds[k] += sourceArray[k2++] & 0xff;
					greens[k] += sourceArray[k2++] & 0xff;
					blues[k] += sourceArray[k2++] & 0xff;
					sums[k]++;
				}
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				for (int x = 0; x < srcW; x += incr) {
					int k = srcXLUT[x];
					int v = sourceArray[x] & 0xff;
					reds[k] += v;
					sums[k]++;
				}
				break;
			default:
				// types BYTE_ABGR and INT_ARGB_PRE aren't supporte yet.
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}
		}

		@Override
		void readColorComponents(int[] sourceArray, int type) {
			int incr = scaleX < .25 ? 2 : 1;
			switch (type) {
			case BufferedImage.TYPE_INT_RGB:
				for (int x = 0; x < srcW; x += incr) {
					int k = srcXLUT[x];
					reds[k] += (sourceArray[x] >> 16) & 0xff;
					greens[k] += (sourceArray[x] >> 8) & 0xff;
					blues[k] += (sourceArray[x]) & 0xff;
					sums[k]++;
				}
				break;
			case BufferedImage.TYPE_INT_BGR:
				for (int x = 0; x < srcW; x += incr) {
					int k = srcXLUT[x];
					reds[k] += (sourceArray[x]) & 0xff;
					greens[k] += (sourceArray[x] >> 8) & 0xff;
					blues[k] += (sourceArray[x] >> 16) & 0xff;
					sums[k]++;
				}
				break;
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				for (int x = 0; x < srcW; x += incr) {
					int k = srcXLUT[x];
					if (alphas != null)
						alphas[k] += isOpaque ? 255
								: (sourceArray[x] >> 24) & 0xff;
					reds[k] += (sourceArray[x] >> 16) & 0xff;
					greens[k] += (sourceArray[x] >> 8) & 0xff;
					blues[k] += (sourceArray[x]) & 0xff;
					sums[k]++;
				}
				break;
			default:
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}
		}

		@Override
		void writeColorComponents(int[] destArray, int type) {
			switch (type) {
			case BufferedImage.TYPE_INT_RGB:
				for (int x = 0; x < width; x++) {
					destArray[x] = ((reds[x] / sums[x]) << 16)
							| ((greens[x] / sums[x]) << 8)
							| ((blues[x] / sums[x]));
				}
				break;
			case BufferedImage.TYPE_INT_BGR:
				for (int x = 0; x < width; x++) {
					destArray[x] = ((reds[x] / sums[x]))
							| ((greens[x] / sums[x]) << 8)
							| ((blues[x] / sums[x]) << 16);
				}
				break;
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				for (int x = 0; x < width; x++) {
					int alpha = isOpaque ? 255 : alphas[x] / sums[x];
					destArray[x] = (alpha << 24) | ((reds[x] / sums[x]) << 16)
							| ((greens[x] / sums[x]) << 8)
							| ((blues[x] / sums[x]));
				}
				break;
			default:
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}
		}

		@Override
		void interpolateXValues() {
			int lastRed = 0;
			int lastGreen = 0;
			int lastBlue = 0;
			int lastAlpha = 0;
			int newRed, newGreen, newBlue, newAlpha;
			for (int x = 0; x < width; x++) {
				switch (sums[x]) {
				case 0:
					if (x > 0) {
						lastRed = reds[x - 1] / sums[x - 1];
						lastGreen = greens[x - 1] / sums[x - 1];
						lastBlue = blues[x - 1] / sums[x - 1];
						lastAlpha = isOpaque ? 255
								: alphas[x - 1] / sums[x - 1];
					}
					int startX = x;
					int span = 1;
					while (x < sums.length && sums[x] == 0) {
						x++;
						span++;
					}
					if (x < sums.length) {
						newAlpha = isOpaque ? 255 : alphas[x] / sums[x];
						newRed = reds[x] / sums[x];
						newGreen = greens[x] / sums[x];
						newBlue = blues[x] / sums[x];
					} else {
						// erm, hopefully this isn't happening, but just in
						// case.
						newAlpha = lastAlpha;
						newRed = lastRed;
						newGreen = lastGreen;
						newBlue = lastBlue;
					}

					if (x == 0) {
						// and hopefully this isn't happening either
						lastRed = newRed;
						lastGreen = newGreen;
						lastBlue = newBlue;
						lastAlpha = newAlpha;
					}

					for (int k = startX; k < x; k++) {
						int f = k - startX + 1;
						if (alphas != null)
							alphas[k] = isOpaque ? 255
									: (short) ((lastAlpha * (span - f)
											+ newAlpha * f) / span);
						reds[k] = (short) ((lastRed * (span - f) + newRed * f)
								/ span);
						greens[k] = (short) ((lastGreen * (span - f)
								+ newGreen * f) / span);
						blues[k] = (short) ((lastBlue * (span - f)
								+ newBlue * f) / span);
						sums[k] = 1;
					}
					break;
				}
			}
		}

		@Override
		void writeColorComponents(Row nextRow, double fraction, int[] destArray,
				int type) {
			if (fraction > 1 || fraction < 0)
				throw new IllegalArgumentException(
						"fraction (" + fraction + ") must be within [0,1]");
			ShortRow next = (ShortRow) nextRow;

			int mult1 = (int) ((1 - fraction) * 255);
			int mult2 = 255 - mult1;

			switch (type) {
			case BufferedImage.TYPE_INT_RGB:
				for (int x = 0; x < width; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;
					destArray[x] = (r << 16) | (g << 8) | (b);
				}
				break;
			case BufferedImage.TYPE_INT_BGR:
				for (int x = 0; x < width; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;
					destArray[x] = (b << 16) | (g << 8) | (r);
				}
				break;
			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
				for (int x = 0; x < width; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;
					int a = isOpaque ? 255
							: (alphas[x] * m1 + next.alphas[x] * m2) >> 8;
					destArray[x] = (a << 24) | (r << 16) | (g << 8) | (b);
				}
				break;
			default:
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}

		}

		@Override
		void writeColorComponents(Row nextRow, double fraction,
				byte[] destArray, int type) {
			if (fraction > 1 || fraction < 0)
				throw new IllegalArgumentException(
						"fraction (" + fraction + ") must be within [0,1]");
			ShortRow next = (ShortRow) nextRow;

			int mult1 = (int) ((1 - fraction) * 255);
			int mult2 = 255 - mult1;

			switch (type) {
			case BufferedImage.TYPE_3BYTE_BGR:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;

					destArray[k2++] = (byte) (b);
					destArray[k2++] = (byte) (g);
					destArray[k2++] = (byte) (r);
				}
				break;
			case ImageType.TYPE_3BYTE_RGB:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;

					destArray[k2++] = (byte) (r);
					destArray[k2++] = (byte) (g);
					destArray[k2++] = (byte) (b);
				}
				break;
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;

					int a = isOpaque ? 255
							: (alphas[x] * m1 + next.alphas[x] * m2) >> 8;
					destArray[k2++] = (byte) (a);
					destArray[k2++] = (byte) (b);
					destArray[k2++] = (byte) (g);
					destArray[k2++] = (byte) (r);
				}
				break;
			case ImageType.TYPE_4BYTE_ARGB:
			case ImageType.TYPE_4BYTE_ARGB_PRE:
				for (int x = 0, k2 = 0; x < dstW; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;

					int a = isOpaque ? 255
							: (alphas[x] * m1 + next.alphas[x] * m2) >> 8;

					destArray[k2++] = (byte) (a);
					destArray[k2++] = (byte) (r);
					destArray[k2++] = (byte) (g);
					destArray[k2++] = (byte) (b);
				}
				break;
			case BufferedImage.TYPE_BYTE_GRAY:
				for (int x = 0; x < dstW; x++) {
					int m1 = mult1 / sums[x];
					int m2 = mult2 / sums[x];

					int r = (reds[x] * m1 + next.reds[x] * m2) >> 8;
					int g = (greens[x] * m1 + next.greens[x] * m2) >> 8;
					int b = (blues[x] * m1 + next.blues[x] * m2) >> 8;

					destArray[x] = (byte) ((r + g + b) / 3);
				}
				break;
			default:
				throw new RuntimeException(
						"unexpected condition: the type should have been converted when this object was constructed");
			}
		}
	}

	Row row;
	/** This is only used when upsampling vertically */
	Row row2;
	/** This maps src x-coordinates to dst x-coordinates. */
	int[] srcXLUT;
	final double scaleX, scaleY;
	final boolean topDown, isOpaque;
	final ImageType srcType;

	private ScalingIterator(PixelIterator<?> srcIterator, int srcW, int srcH,
			int scaledWidth, int scaledHeight, boolean topDown,
			boolean isOpaque) {
		srcType = ImageType.get(srcIterator.getType());
		if (srcType.isByte()
				&& (!isSupportedByteType(srcIterator.getType()))) {
			if (srcIterator.isOpaque()) {
				srcIterator = ImageType.BYTE_BGR.createPixelIterator(srcIterator);
			} else {
				srcIterator = ImageType.BYTE_BGRA.createPixelIterator(srcIterator);
			}
		} else if (srcType.isInt()
				&& (!isSupportedIntType(srcIterator.getType()))) {
			if (srcIterator.isOpaque()) {
				srcIterator = ImageType.INT_RGB.createPixelIterator(srcIterator);
			} else {
				srcIterator = ImageType.INT_ARGB.createPixelIterator(srcIterator);
			}
		}
		this.srcIterator = srcIterator;
		this.srcW = srcW;
		this.srcH = srcH;
		this.topDown = topDown;
		this.isOpaque = isOpaque;
		dstW = scaledWidth;
		dstH = scaledHeight;
		dstY = 0;
		srcY = 0;

		scaleY = ((double) dstH) / ((double) srcH);
		scaleX = ((double) dstW) / ((double) srcW);

		srcXLUT = new int[srcW];
		for (int srcX = 0; srcX < srcW; srcX++) {
			srcXLUT[srcX] = (int) (srcX * scaleX);
			if (srcX == srcXLUT.length - 1) {
				srcXLUT[srcX] = (dstW - 1);
			}
		}

		/**
		 * If the x and y scale factor are .1, then we'll dump 100 source pixels
		 * (separated into separate RGB channels) into 1 dest element. When this
		 * value crosses a threshold: we should switch from shorts to ints. If
		 * we don't then we'll suffer arithmetic overflows, and the color
		 * channels will look funky.
		 */
		int capacity = (int) Math.ceil(1.0 / scaleX)
				* (int) Math.ceil(1.0 / scaleY);
		if (capacity > 0xff) {
			row = new IntRow(dstW, isOpaque);
			if (scaleY > 1) // upsampling requires two Rows
				row2 = new IntRow(dstW, isOpaque);
		} else {
			row = new ShortRow(dstW, isOpaque);
			if (scaleY > 1)
				row2 = new ShortRow(dstW, isOpaque);
		}
		/**
		 * TODO: improve this calculation. This is a slight oversimplification:
		 * because in other places when a scale factor (x or y) is less than
		 * .25, then we start intentionally skipping source pixels. The
		 * threshold *would* be 0xff if we didn't skip anything. Since we're
		 * skipping data: we could afford to make this higher. But I don't know
		 * exactly how high...
		 */
	}

	protected void flush() {
		skipRemainingRows();
		row = null;
		srcXLUT = null;
		scratchByteArray = null;
		scratchIntArray = null;
	}

	@Override
	public int getHeight() {
		return dstH;
	}

	@Override
	public int getWidth() {
		return dstW;
	}

	@Override
	public boolean isDone() {
		boolean returnValue = dstY >= dstH;
		return returnValue;
	}

	@Override
	public void skip() {
		srcY++;
		if (isDone())
			flush();
	}

	protected void skipRemainingRows() {
		while (srcIterator.isDone() == false) {
			srcIterator.skip();
		}
		dstY = dstH;
		srcY = srcH;
	}

	@Override
	public boolean isTopDown() {
		return topDown;
	}

	@Override
	public boolean isOpaque() {
		return isOpaque;
	}

	private int[] scratchIntArray;
	private byte[] scratchByteArray;

	/**
	 * This is called by both the int and byte subclasses, so exactly one of
	 * these arguments will always be null. But so much of the logic is the same
	 * it made sense to put it all in one method.
	 * 
	 * @param incomingByteArray
	 * @param incomingIntArray
	 */
	void next(byte[] incomingByteArray, int[] incomingIntArray) {
		if (scaleY <= 1) {
			nextDownsample(incomingByteArray, incomingIntArray);
		} else {
			nextUpsample(incomingByteArray, incomingIntArray);
		}

		dstY++;
		if (isDone())
			flush();
	}

	/**
	 * This is called when scaleY>1, and we have to read two rows from the
	 * source image and interpolate between them to write two or more rows in
	 * the destination image.
	 * <P>
	 * Exactly one of the arguments will be null, depending on whether this data
	 * should be written with ints or bytes.
	 */
	void nextUpsample(byte[] incomingByteArray, int[] incomingIntArray) {
		/**
		 * I took the path of least resistance in writing this upsampling
		 * method: We tween ALL rows (except the first row). The result is a
		 * very smooth upward scaling, but the performance could be slightly
		 * improved if we introduced rounding. But the rounding would actually
		 * be a little bit more work, and would result in a lower image
		 * quality... so I'm not going to implement it for now.
		 */
		double sy = (dstH - 1.0) / (srcH - 1.0);
		double srcPosition = (dstY) / sy;
		int srcY0 = (int) Math.floor(srcPosition);
		int srcY1 = (int) Math.ceil(srcPosition);
		double srcFraction = srcPosition - srcY0;

		if (srcY < srcY0) {
			while (srcY < srcY0) {
				srcIterator.skip();
				srcY++;
			}
		}

		if (row2.marker == srcY0) {
			Row swap = row;
			row = row2;
			row2 = swap;
		}

		if (row.marker < srcY0) {
			row.clear();
			nextSourceRow(row, incomingByteArray, incomingIntArray);
			row.marker = srcY0;
			if (scaleX > 1)
				row.interpolateXValues();
		}

		boolean writeOnlyOneRow = false;

		if (srcY0 == srcY1) {
			// in the first iteration: both srcY0 and srcY1
			// will equal zero:
			writeOnlyOneRow = true;
		} else if (srcY >= srcH) {
			// given how we skip rows this can happen for the last row.
			// it'd be great to fix this, but for now just repeat the last
			// row of pixel data.
			// TODO: revisit this, examine how last row renders.
			// (hint: start by leaving writeOnlyOneRow false, and then
			// resolve the unit test failures that follow)
			writeOnlyOneRow = true;
		}

		if (writeOnlyOneRow) {
			if (incomingIntArray != null) {
				row.writeColorComponents(incomingIntArray, getType());
			} else {
				row.writeColorComponents(incomingByteArray, getType());
			}
		} else {
			// in every normal iteration we'll want to compare the two rows:
			if (row2.marker < srcY1) {
				row2.clear();
				nextSourceRow(row2, incomingByteArray, incomingIntArray);
				row2.marker = srcY1;
				if (scaleX > 1)
					row2.interpolateXValues();
			}

			if (incomingIntArray != null) {
				row.writeColorComponents(row2, srcFraction, incomingIntArray,
						getType());
			} else {
				row.writeColorComponents(row2, srcFraction, incomingByteArray,
						getType());
			}
		}
	}

	/**
	 * This is called when scaleY<=1, and we have to collapse one or more rows
	 * of the source image into the destination image.
	 * <P>
	 * Exactly one of the arguments will be null, depending on whether this data
	 * should be written with ints or bytes.
	 */
	void nextDownsample(byte[] incomingByteArray, int[] incomingIntArray) {
		int srcY0 = (int) ((dstY) / scaleY);
		int srcY1 = (int) (((dstY + 1)) / scaleY);
		if (srcY1 != srcY0)
			srcY1--;

		if (srcY < srcY0) {
			while (srcY < srcY0) {
				srcIterator.skip();
				srcY++;
			}
		}

		while (srcY <= srcY1) {
			nextSourceRow(row, incomingByteArray, incomingIntArray);

			if (scaleY < .25 && srcY < srcY1) {
				srcY++;
				srcIterator.skip();
			}
		}
		if (scaleX > 1)
			row.interpolateXValues();

		if (incomingIntArray != null) {
			row.writeColorComponents(incomingIntArray, getType());
		} else {
			row.writeColorComponents(incomingByteArray, getType());
		}
		row.clear();
	}

	/**
	 * Reads one row of pixel data from the source into the Row argument
	 * provided.
	 * 
	 * @param row
	 *            the row to collect the incoming pixel data.
	 * @param incomingByteArray
	 *            an optional byte array. If this is null and a byte array is
	 *            needed: the <code>scratchByteArray</code> will be used.
	 * @param incomingIntArray
	 *            an optional byte array. If this is null and an int array is
	 *            needed: the <code>scratchIntArray</code> will be used.
	 */
	void nextSourceRow(Row row, byte[] incomingByteArray,
			int[] incomingIntArray) {
		srcY++;
		if (srcType.isInt()) {
			int[] intArray = incomingIntArray;
			if (intArray == null) {
				// the source is ints, but we were provided
				// a byte array to write to:
				if (scratchIntArray == null)
					scratchIntArray = new int[srcIterator
							.getMinimumArrayLength()];
				intArray = scratchIntArray;
			}
			srcIterator.next(intArray);
			row.readColorComponents(intArray, srcIterator.getType());
		} else {
			byte[] byteArray = incomingByteArray;
			if (byteArray == null) {
				// the source is bytes, but we were provided
				// an int array to write to:
				if (scratchByteArray == null)
					scratchByteArray = new byte[srcIterator
							.getMinimumArrayLength()];
				byteArray = scratchByteArray;
			}
			srcIterator.next(byteArray);
			row.readColorComponents(byteArray, srcIterator.getType());
		}
	}
}