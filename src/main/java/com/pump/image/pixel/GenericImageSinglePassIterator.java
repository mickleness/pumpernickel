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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.pump.awt.Dimension2D;
import com.pump.image.ColorModelUtils;
import com.pump.util.PushPullQueue;

/**
 * This pixel iterator processes a <code>java.awt.Image</code> in a single pass
 * (either top-to-bottom or bottom-to-top). The advantage of this class is that
 * it pipes all this information through the iterator interface as it becomes
 * available: so a buffer of the <i>entire</i> image is not kept in memory.
 * <p>
 * This uses the <code>ImageConsumer</code>/<code>ImageProducer</code> model for
 * collecting image data.
 * <p>
 * If you combine this with the <code>ScalingIterator</code> class: then this
 * means you can pipe data from a large abstract image into a small thumbnail
 * without consuming too much memory. For example: I can create a thumbnail of a
 * 18967x13606 pixel JPEG in just a few seconds with a very low memory footprint
 * using this class.
 * <p>
 * The disadvantage to this class is it relies on being able to read images in a
 * <i>single pass</i>. There is no guarantee that the <code>ImageProducer</code>
 * will actually provide all the data in a single pass of consecutive rows.
 * (Unfortunately the <code>ImageConsumer</code> hint regarding single passes
 * appears to not be set correctly for many JPGs, so this object disregards that
 * flag as unreliable).
 * <p>
 * What this means is: unless you are 100% confident that your
 * <code>java.awt.Image</code> will be read in a single pass you need to wrap
 * this iterator in a thorough try/catch clause, and be prepared for the
 * possibility that it may fail and throw a <code>NonSinglePassException</code>.
 * <p>
 * Because this is piping data from the <code>ImageProducer</code> in another
 * thread: it is recommended that you call <code>next(...)</code> or
 * <code>skip()</code> until this iterator has no more pixel data. There are
 * some safeguards in place to recover the other thread (a 5-second timeout as
 * well as awareness of when this object is finalized), but those are unpleasant
 * safety nets. In many cases the AWT toolkit will only launch 4 "Image Fetcher"
 * threads at a time: if all 4 are hung for a minimum of 5 seconds, then <i>no
 * other images</i> can be processed through the AWT toolkit during that time.
 * 
 * <a href=
 * "https://javagraphics.blogspot.com/2011/05/images-scaling-jpegs-and-pngs.html"
 * >Images: Scaling JPEGs and PNGs</a>
 */
public abstract class GenericImageSinglePassIterator<T>
		implements PixelIterator<T> {

	/**
	 * The number of milliseconds threads wait before timing out while reading.
	 * By default this is 5000.
	 */
	public static long TIMEOUT_IN_PROCESS = 5000;

	/**
	 * The number of milliseconds threads wait before timing out for
	 * construction. Note construction may be severely delayed, because Java's
	 * AWT classes only allow 4 threads at a time. By default this is 120,000.
	 */
	public static long TIMEOUT_FOR_CONSTRUCTION = 120000;

	/**
	 * This is an image type alternative that indicates we should return
	 * whatever is simplest/most expedient.
	 */
	public static int TYPE_DEFAULT = -888321;

	/**
	 * This exception indicates that the source image did not provide all the
	 * pixel data in a single pass. (That is: it might have provided row 1, 3,
	 * and then row 2. The <code>GenericImageSinglePassIterator</code> requires
	 * this data be delivered in consecutive rows.)
	 * <p>
	 * If this occurs, I recommend trying the
	 * {@link com.pump.image.bmp.BmpEncoder} class. This can write an image to a
	 * <code>RandomAccessFile</code> in multiple arbitrary passes as a BMP, and
	 * then the {@link com.pump.image.bmp.BmpDecoder} can read the final image
	 * back in a {@link com.pump.image.pixel.PixelIterator}.
	 */
	public static class NonSinglePassException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NonSinglePassException(String msg) {
			super(msg);
		}
	}

	/**
	 * This object is the intermediate between the two threads controlling this
	 * iterator. This has 4 different uses: 1. When the ImageProducer first
	 * receives this, it is a request (and an empty vessel) 2. Then this object
	 * is passed back to the iterator as pixel data 3. Then this object is
	 * passed back to the ImageProducer as an acknowledgment that everything
	 * went well. 4. At any of the previous 3 stages: if the "error" field is
	 * defined then an exception should be thrown using that string as the
	 * exception description.
	 */
	private static class PixelPackage {
		int x, y, w, h, offset, scanSize;
		Object pixels;
		ColorModel colorModel;
		boolean finished = false;
		String error;

		synchronized void deliver(int x, int y, int w, int h,
				ColorModel colorModel, Object pixels, int offset,
				int scanSize) {
			error = null;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.colorModel = colorModel;
			this.pixels = pixels;
			this.offset = offset;
			this.scanSize = scanSize;
		}

		synchronized void acknowledge(String error) {
			this.error = error;
			this.x = -1;
			this.y = -1;
			this.w = -1;
			this.h = -1;
			this.colorModel = null;
			this.pixels = null;
			this.offset = -1;
			this.scanSize = -1;
		}
	}

	private static class GenericImageSinglePassByteIterator
			extends GenericImageSinglePassIterator<byte[]>
			implements BytePixelIterator {

		public GenericImageSinglePassByteIterator(int width, int height,
				int type, boolean topDown) {
			super(width, height, type, topDown);
		}

		@Override
		void fillEmptyRow(Object dest) {
			int bytesPerPixel = getPixelSize();
			byte[] destArray = (byte[]) dest;
			for (int x = 0; x < width * bytesPerPixel; x++) {
				destArray[x] = 0;
			}
		}

		/**
		 * Determine if the argument uses 3 BGR bytes for storage.
		 */
		private boolean isBGR(ColorModel model) {
			if (model.getTransferType() != DataBuffer.TYPE_BYTE
					|| model.getPixelSize() != 24)
				return false;
			// There has to be a better way to do this, but I don't know what
			// that is...
			if (scratchArray == null || scratchArray.length < 3) {
				scratchArray = new byte[3];
			}
			scratchArray[0] = (byte) (255);
			scratchArray[1] = (byte) (0);
			scratchArray[2] = (byte) (0);
			if (model.getRGB(scratchArray) != 0xffff0000) {
				return false;
			}
			scratchArray[0] = (byte) (0);
			scratchArray[1] = (byte) (255);
			scratchArray[2] = (byte) (0);
			if (model.getRGB(scratchArray) != 0xff00ff00) {
				return false;
			}
			scratchArray[0] = (byte) (0);
			scratchArray[1] = (byte) (0);
			scratchArray[2] = (byte) (255);
			if (model.getRGB(scratchArray) != 0xff0000ff) {
				return false;
			}
			return true;
		}

		/**
		 * Determine if the argument uses 4 ABGR bytes for storage.
		 */
		private boolean isABGR(ColorModel model) {
			if (model.getTransferType() != DataBuffer.TYPE_BYTE
					|| model.getPixelSize() != 32)
				return false;
			// There has to be a better way to do this, but I don't know what
			// that is...
			if (scratchArray == null || scratchArray.length < 4) {
				scratchArray = new byte[4];
			}
			scratchArray[0] = (byte) (255);
			scratchArray[1] = (byte) (255);
			scratchArray[2] = (byte) (0);
			scratchArray[3] = (byte) (0);
			if (model.getRGB(scratchArray) != 0xffff0000) {
				return false;
			}
			scratchArray[0] = (byte) (0x88);
			scratchArray[1] = (byte) (255);
			scratchArray[2] = (byte) (0);
			scratchArray[3] = (byte) (0);
			if (model.getRGB(scratchArray) != 0x8800ff00) {
				return false;
			}
			scratchArray[0] = (byte) (0x22);
			scratchArray[1] = (byte) (255);
			scratchArray[2] = (byte) (0);
			scratchArray[3] = (byte) (0);
			if (model.getRGB(scratchArray) != 0x220000ff) {
				return false;
			}
			return true;
		}

		/**
		 * Determine if the argument uses one gray byte for storage.
		 */
		private boolean isGray(ColorModel model) {
			if (model.getTransferType() != DataBuffer.TYPE_BYTE
					|| model.getPixelSize() != 8)
				return false;
			// There has to be a better way to do this, but I don't know what
			// that is...
			if (scratchArray == null || scratchArray.length < 1) {
				scratchArray = new byte[1];
			}
			scratchArray[0] = (byte) (255);
			if (model.getRGB(scratchArray) != 0xffffffff) {
				return false;
			}
			scratchArray[0] = (byte) (0x88);
			if (model.getRGB(scratchArray) != 0xff888888) {
				return false;
			}
			scratchArray[0] = (byte) (0x00);
			if (model.getRGB(scratchArray) != 0xff000000) {
				return false;
			}
			return true;
		}

		private byte[] scratchArray;

		@Override
		void populate(Object dest, Object pixels, int offset, int x, int width,
				ColorModel colorModel) {
			byte[] destArray = (byte[]) dest;
			try {
				int bitsPerPixel = colorModel.getPixelSize();
				if (colorModel.getTransferType() == DataBuffer.TYPE_BYTE) {
					byte[] pixelData = (byte[]) pixels;
					int bytesPerPixel = (bitsPerPixel + 7) / 8;
					if (scratchArray == null
							|| scratchArray.length < bytesPerPixel) {
						scratchArray = new byte[bytesPerPixel];
					}

					if (type == BufferedImage.TYPE_3BYTE_BGR
							&& bytesPerPixel == 3 && isBGR(colorModel)) {
						System.arraycopy(pixelData, offset + 3 * x, destArray,
								3 * x, width * 3);
					} else if ((type == BufferedImage.TYPE_4BYTE_ABGR_PRE
							|| type == BufferedImage.TYPE_4BYTE_ABGR)
							&& bytesPerPixel == 4 && isABGR(colorModel)) {
						System.arraycopy(pixelData, offset + 4 * x, destArray,
								4 * x, width * 4);
					} else if (type == BufferedImage.TYPE_BYTE_GRAY
							&& bytesPerPixel == 1 && isGray(colorModel)) {
						System.arraycopy(pixelData, offset + x, destArray, x,
								width);
					} else {
						for (int myX = x; myX < x + width; myX++) {
							for (int k = 0; k < bytesPerPixel; k++) {
								scratchArray[k] = pixelData[offset
										+ myX * bytesPerPixel + k];
							}
							int rgb = colorModel.getRGB(scratchArray);
							if (type == BufferedImage.TYPE_3BYTE_BGR) {
								destArray[3 * myX
										+ 0] = (byte) ((rgb >> 16) & 0xff);
								destArray[3 * myX
										+ 1] = (byte) ((rgb >> 8) & 0xff);
								destArray[3 * myX
										+ 2] = (byte) ((rgb >> 0) & 0xff);
							} else if (type == BufferedImage.TYPE_4BYTE_ABGR
									|| type == BufferedImage.TYPE_4BYTE_ABGR) {
								destArray[4 * myX
										+ 0] = (byte) ((rgb >> 24) & 0xff);
								destArray[4 * myX
										+ 1] = (byte) ((rgb >> 16) & 0xff);
								destArray[4 * myX
										+ 2] = (byte) ((rgb >> 8) & 0xff);
								destArray[4 * myX
										+ 3] = (byte) ((rgb >> 0) & 0xff);
							} else if (type == ImageType.TYPE_3BYTE_RGB) {
								destArray[3 * myX
										+ 0] = (byte) ((rgb >> 0) & 0xff);
								destArray[3 * myX
										+ 1] = (byte) ((rgb >> 8) & 0xff);
								destArray[3 * myX
										+ 2] = (byte) ((rgb >> 16) & 0xff);
							} else if (type == ImageType.TYPE_4BYTE_ARGB
									|| type == ImageType.TYPE_4BYTE_ARGB_PRE) {
								destArray[4 * myX
										+ 0] = (byte) ((rgb >> 24) & 0xff);
								destArray[4 * myX
										+ 1] = (byte) ((rgb >> 0) & 0xff);
								destArray[4 * myX
										+ 2] = (byte) ((rgb >> 8) & 0xff);
								destArray[4 * myX
										+ 3] = (byte) ((rgb >> 16) & 0xff);
							} else if (type == BufferedImage.TYPE_BYTE_GRAY) {
								int c1 = (rgb >> 16) & 0xff;
								int c2 = (rgb >> 8) & 0xff;
								int c3 = (rgb >> 0) & 0xff;
								destArray[myX] = (byte) ((c1 + c2 + c3) / 3);
							}
						}
					}
				} else if (colorModel
						.getTransferType() == DataBuffer.TYPE_INT) {
					int[] pixelData = (int[]) pixels;
					if (type == BufferedImage.TYPE_3BYTE_BGR) {
						for (int myX = x; myX < x + width; myX++) {
							int rgb = colorModel
									.getRGB(pixelData[myX + offset]);
							destArray[3 * myX
									+ 0] = (byte) ((rgb >> 16) & 0xff);
							destArray[3 * myX + 1] = (byte) ((rgb >> 8) & 0xff);
							destArray[3 * myX + 2] = (byte) ((rgb >> 0) & 0xff);
						}
					} else if (type == BufferedImage.TYPE_4BYTE_ABGR
							|| type == BufferedImage.TYPE_4BYTE_ABGR) {
						for (int myX = x; myX < x + width; myX++) {
							int rgb = colorModel
									.getRGB(pixelData[myX + offset]);
							destArray[4 * myX
									+ 0] = (byte) ((rgb >> 24) & 0xff);
							destArray[4 * myX
									+ 1] = (byte) ((rgb >> 16) & 0xff);
							destArray[4 * myX + 2] = (byte) ((rgb >> 8) & 0xff);
							destArray[4 * myX + 3] = (byte) ((rgb >> 0) & 0xff);
						}
					} else if (type == ImageType.TYPE_3BYTE_RGB) {
						for (int myX = x; myX < x + width; myX++) {
							int rgb = colorModel
									.getRGB(pixelData[myX + offset]);
							destArray[3 * myX + 0] = (byte) ((rgb >> 0) & 0xff);
							destArray[3 * myX + 1] = (byte) ((rgb >> 8) & 0xff);
							destArray[3 * myX
									+ 2] = (byte) ((rgb >> 16) & 0xff);
						}
					} else if (type == ImageType.TYPE_4BYTE_ARGB
							|| type == ImageType.TYPE_4BYTE_ARGB_PRE) {
						for (int myX = x; myX < x + width; myX++) {
							int rgb = colorModel
									.getRGB(pixelData[myX + offset]);
							destArray[4 * myX
									+ 0] = (byte) ((rgb >> 24) & 0xff);
							destArray[4 * myX + 1] = (byte) ((rgb >> 0) & 0xff);
							destArray[4 * myX + 2] = (byte) ((rgb >> 8) & 0xff);
							destArray[4 * myX
									+ 3] = (byte) ((rgb >> 16) & 0xff);
						}
					} else if (type == BufferedImage.TYPE_BYTE_GRAY) {
						for (int myX = x; myX < x + width; myX++) {
							int rgb = colorModel
									.getRGB(pixelData[myX + offset]);
							int c1 = (rgb >> 16) & 0xff;
							int c2 = (rgb >> 8) & 0xff;
							int c3 = (rgb >> 0) & 0xff;
							destArray[myX] = (byte) ((c1 + c2 + c3) / 3);
						}
					}
				} else {
					// I don't think this should ever happen?
					// Because this is all coming from setPixels(...) methods,
					// which
					// either pass int[] or byte[] to represent the data...
					throw new IllegalArgumentException(
							"only byte and int based ColorModels are supported. (colorModel = "
									+ colorModel + ")");
				}
			} catch (RuntimeException e) {
				throw e;
			}
		}

		/**
		 * 
		 * @throws NonSinglePassException
		 *             if the source image did not deliver the image in a single
		 *             pass.
		 */
		@Override
		public void next(byte[] dest) {
			processNextRow(dest);
		}
	}

	private static class GenericImageSinglePassIntIterator extends
			GenericImageSinglePassIterator<int[]> implements IntPixelIterator {

		public GenericImageSinglePassIntIterator(int width, int height,
				int type, boolean topDown) {
			super(width, height, type, topDown);
		}

		/**
		 * Flip the bytes for int elements in an array.
		 * 
		 * @param array
		 *            an array with elements to flip.
		 * @param offset
		 *            the offset of the array to begin flipping
		 * @param length
		 *            the number of array elements to flip
		 * @param constantMask
		 *            a mask of the values that should not change
		 * @param byteIndex1
		 *            the index of the first byte to swap
		 * @param byteIndex2
		 *            the index of the second byte to swap
		 */
		static void flipBytes(int[] array, int offset, int length,
				int constantMask, int byteIndex1, int byteIndex2) {
			for (int i = 0; i < length; i++) {
				int k = array[i + offset];
				int byte1 = (k >> byteIndex1 * 8) & 0xff;
				int byte2 = (k >> byteIndex2 * 8) & 0xff;
				k = (k & constantMask) + (byte1 << byteIndex2 * 8);
				k = (k & constantMask) + (byte2 << byteIndex1 * 8);
				array[i + offset] = k;
			}
		}

		@Override
		void fillEmptyRow(Object dest) {
			int[] destArray = (int[]) dest;
			for (int x = 0; x < width; x++) {
				destArray[x] = 0;
			}
		}

		private byte[] scratchArray;

		@Override
		void populate(Object dest, Object pixels, int offset, int x, int width,
				ColorModel colorModel) {
			int[] destArray = (int[]) dest;
			try {
				int bitsPerPixel = colorModel.getPixelSize();
				if (colorModel.getTransferType() == DataBuffer.TYPE_BYTE) {
					byte[] pixelData = (byte[]) pixels;
					int bytesPerPixel = (bitsPerPixel + 7) / 8;
					if (scratchArray == null
							|| scratchArray.length < bytesPerPixel) {
						scratchArray = new byte[bytesPerPixel];
					}
					for (int myX = 0; myX < width; myX++) {
						for (int k = 0; k < bytesPerPixel; k++) {
							scratchArray[k] = pixelData[offset
									+ myX * bytesPerPixel + k];
						}
						int rgb = colorModel.getRGB(scratchArray);
						destArray[myX + x] = rgb;
					}
					if (type == BufferedImage.TYPE_INT_BGR) {
						// flip bytes B and R, keeping A and G constant:
						flipBytes(destArray, x, width, 0xff00ff00, 0, 2);
					}
				} else if (colorModel
						.getTransferType() == DataBuffer.TYPE_INT) {
					int[] pixelData = (int[]) pixels;
					DirectColorModel dcm = null;
					if (colorModel instanceof DirectColorModel) {
						dcm = (DirectColorModel) colorModel;
					}
					if (type == BufferedImage.TYPE_INT_RGB && dcm != null
							&& dcm.getRedMask() == 0xff0000
							&& dcm.getGreenMask() == 0xff00
							&& dcm.getBlueMask() == 0xff) {
						System.arraycopy(pixelData, offset + x, dest, x, width);
					} else if ((type == BufferedImage.TYPE_INT_ARGB
							|| type == BufferedImage.TYPE_INT_ARGB_PRE)
							&& dcm != null && dcm.getRedMask() == 0xff0000
							&& dcm.getGreenMask() == 0xff00
							&& dcm.getBlueMask() == 0xff
							&& dcm.getAlphaMask() == 0xff000000) {
						System.arraycopy(pixelData, offset + x, dest, x, width);
					} else if ((type == BufferedImage.TYPE_INT_ARGB
							|| type == BufferedImage.TYPE_INT_ARGB_PRE)
							&& dcm != null && dcm.getRedMask() == 0xff0000
							&& dcm.getGreenMask() == 0xff00
							&& dcm.getBlueMask() == 0xff
							&& dcm.getAlphaMask() == 0x00) {
						for (int a = 0; a < width; a++) {
							destArray[x + a] = 0xff000000
									+ (pixelData[offset + x + a] & 0xffffff);
						}
					} else if (type == BufferedImage.TYPE_INT_BGR && dcm != null
							&& dcm.getRedMask() == 0xff
							&& dcm.getGreenMask() == 0xff00
							&& dcm.getBlueMask() == 0xff0000) {
						System.arraycopy(pixelData, offset + x, dest, x, width);
					} else if (type == BufferedImage.TYPE_INT_BGR && dcm != null
							&& dcm.getRedMask() == 0xff0000
							&& dcm.getGreenMask() == 0xff00
							&& dcm.getBlueMask() == 0xff) {
						// we want BGR, but we have RGB:
						System.arraycopy(pixelData, offset + x, dest, x, width);
						flipBytes(destArray, x, width, 0xff00ff00, 0, 2);
					} else {
						for (int myX = x; myX < x + width; myX++) {
							int rgb = colorModel.getRGB(pixelData[myX]);
							destArray[myX] = rgb;
						}
						// Of the 4 types we support, this is the only one that
						// needs special attention here:
						if (type == BufferedImage.TYPE_INT_BGR) {
							flipBytes(destArray, x, width, 0xff00ff00, 0, 2);
						}
					}
				} else {
					// I don't think this should ever happen?
					// Because this is all coming from setPixels(...) methods,
					// which
					// either pass int[] or byte[] to represent the data...
					throw new IllegalArgumentException(
							"only byte and int based ColorModels are supported. (colorModel = "
									+ colorModel + ")");
				}
			} catch (RuntimeException e) {
				throw e;
			}
		}

		/**
		 * 
		 * @throws NonSinglePassException
		 *             if the source image did not deliver the image in a single
		 *             pass.
		 */
		@Override
		public void next(int[] dest) {
			processNextRow(dest);
		}
	}

	static class MyPushPullQueue extends PushPullQueue<PixelPackage> {
		WeakReference<GenericImageSinglePassIterator> ref;

		public MyPushPullQueue(GenericImageSinglePassIterator iter) {
			ref = new WeakReference<GenericImageSinglePassIterator>(iter);
		}

		@Override
		protected void iteratePush() {
			GenericImageSinglePassIterator iter = ref.get();
			if (iter == null || iter.isDone())
				throw new CompletedException();
		}

		@Override
		protected void iteratePull() {
			GenericImageSinglePassIterator iter = ref.get();
			if (iter == null || iter.isDone())
				throw new CompletedException();
		}
	}

	static class CompletedException extends RuntimeException {
		private static final long serialVersionUID = 1L;

	}

	/**
	 * This is the consumer that listens for updates from the ImageProducer.
	 * These changes are then passed to the pixel iterator.
	 */
	private static class Consumer implements ImageConsumer {
		final ImageProducer producer;
		Integer imgWidth = null;
		Integer imgHeight = null;
		@SuppressWarnings("rawtypes")
		Map properties = new HashMap();
		PushPullQueue<PixelPackage> outgoing;
		PushPullQueue<PixelPackage> incoming;
		boolean listening = true;

		/**
		 * This is used only once to define the GenericImageSinglePassIterator,
		 * or a String representing an error in creating it.
		 */
		PushPullQueue<Object> incomingIteratorOrError = new PushPullQueue<Object>();

		int iteratorType;

		Consumer(ImageProducer producer, int iteratorType) {
			this.producer = producer;
			this.iteratorType = iteratorType;
		}

		@Override
		public void setDimensions(int width, int height) {
			imgWidth = Integer.valueOf(width);
			imgHeight = Integer.valueOf(height);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void setProperties(
				@SuppressWarnings("rawtypes") Hashtable props) {
			properties.putAll(props);
		}

		@Override
		public void setColorModel(ColorModel model) {
			// meh. The API explicitly says this is a guideline, not a guarantee
		}

		@Override
		public void setHints(int hintFlags) {
			/**
			 * In my experience these hints are unreliable. A core part of
			 * developing this class involves testing JPEGs: they never appeared
			 * to support the flag for single-pass, but they were actually
			 * single-passes in the end. So I don't trust this flag at all.
			 * 
			 * This class will end up throwing an exception if the iterator
			 * tries to go back to a row it already finished.
			 */
		}

		@Override
		public synchronized void setPixels(int x, int y, int w, int h,
				ColorModel model, byte[] pixels, int offset, int scanSize) {
			_setPixels(x, y, w, h, model, pixels, offset, scanSize);
		}

		@Override
		public synchronized void setPixels(int x, int y, int w, int h,
				ColorModel model, int[] pixels, int offset, int scanSize) {
			_setPixels(x, y, w, h, model, pixels, offset, scanSize);
		}

		private void processCachedPixels() {
			if (cachedPixelPackage != null) {
				processPixels(cachedPixelPackage.x, cachedPixelPackage.y,
						cachedPixelPackage.w, cachedPixelPackage.h,
						cachedPixelPackage.colorModel,
						cachedPixelPackage.pixels, cachedPixelPackage.offset,
						cachedPixelPackage.scanSize);
				cachedPixelPackage = null;
			}
		}

		private PixelPackage cachedPixelPackage = null;

		private void _setPixels(int x, int y, int w, int h, ColorModel model,
				Object pixels, int offset, int scanSize) {
			if (!listening)
				return;

			if (!isInitialized) {
				boolean isTopDown;
				if (y == 0) {
					isTopDown = true;
				} else if (y == imgHeight - 1) {
					isTopDown = false;
				} else {
					throw new RuntimeException("Cannot identify isTopDown. y = "
							+ y + ", imgHeight = " + imgHeight);
				}
				initialize(isTopDown, model);
			}

			/**
			 * Sometimes images are decoded in multiple passes. Google PNG
			 * interlacing for an example. In this case we would be within our
			 * rights to throw a NonSinglePassException, but since the ENTIRE
			 * block is going to be repeated a few more times, we can still make
			 * this work.
			 * 
			 */
			if (x == 0 && y == 0 && imgWidth.intValue() == w
					&& imgHeight.intValue() == h) {
				cachedPixelPackage = new PixelPackage();
				cachedPixelPackage.deliver(x, y, w, h, model, pixels, offset,
						scanSize);
				return;
			}

			processCachedPixels();
			processPixels(x, y, w, h, model, pixels, offset, scanSize);
		}

		private void processPixels(int x, int y, int w, int h, ColorModel model,
				Object pixels, int offset, int scanSize) {
			try {
				/**
				 * 1. First we receive an empty (or rewritable) package for a
				 * request. 2. Then we pass our contents along to the outgoing
				 * queue. 3. Then we wait for acknowledgment, indicating that
				 * the client is done.
				 */
				PixelPackage pixelPackage = incoming.pull(TIMEOUT_IN_PROCESS);
				if (pixelPackage.error != null)
					throw new RuntimeException(pixelPackage.error);

				pixelPackage.deliver(x, y, w, h, model, pixels, offset,
						scanSize);
				outgoing.push(pixelPackage, TIMEOUT_IN_PROCESS);

				pixelPackage = incoming.pull(TIMEOUT_IN_PROCESS);
				if (pixelPackage.error != null)
					throw new RuntimeException(pixelPackage.error);

			} catch (CompletedException e) {
				// for whatever reason (it's not our business):
				// our client isn't interested in receiving more data.
				// This might be an error, or it might be that the
				// client was finalized, or skipped rows.
				producer.removeConsumer(this);
				listening = false;
			}
		}

		private boolean isInitialized = false;

		/**
		 * This is called when setPixels(...) is called, and if a
		 * GenericImageSinglePassIterator hasn't been defined yet: then we
		 * define it here.
		 * 
		 * @param topDown
		 *            whether this data is readable as top-to-bottom data vs
		 *            bottom-to-top.
		 */
		private void initialize(boolean topDown, ColorModel colorModel) {
			if (imgWidth == null || imgHeight == null) {
				String error = "pixel data was sent but the dimensions were undefined";
				incomingIteratorOrError.push(error);
				throw new RuntimeException(error);
			}

			GenericImageSinglePassIterator iterator;
			int w = imgWidth.intValue();
			int h = imgHeight.intValue();

			if (iteratorType == TYPE_DEFAULT) {
				iteratorType = ColorModelUtils.getBufferedImageType(colorModel);
				if (iteratorType == ColorModelUtils.TYPE_UNRECOGNIZED)
					iteratorType = BufferedImage.TYPE_INT_ARGB;
			}

			switch (iteratorType) {
			case BufferedImage.TYPE_3BYTE_BGR:
			case BufferedImage.TYPE_4BYTE_ABGR:
			case BufferedImage.TYPE_4BYTE_ABGR_PRE:
			case ImageType.TYPE_3BYTE_RGB:
			case ImageType.TYPE_4BYTE_ARGB:
			case ImageType.TYPE_4BYTE_ARGB_PRE:
			case BufferedImage.TYPE_BYTE_GRAY:
				iterator = new GenericImageSinglePassByteIterator(w, h,
						iteratorType, topDown);
				break;

			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
			case BufferedImage.TYPE_INT_BGR:
			case BufferedImage.TYPE_INT_RGB:
				iterator = new GenericImageSinglePassIntIterator(w, h,
						iteratorType, topDown);
				break;
			default:
				throw new RuntimeException(
						"unsupported iterator type: " + iteratorType);
			}
			this.incoming = iterator.outgoing;
			this.outgoing = iterator.incoming;
			incomingIteratorOrError.push(iterator);
			isInitialized = true;
		}

		@Override
		public void imageComplete(int status) {

			processCachedPixels();

			// unless we're interested in other frames, we should stop listening
			// now:
			producer.removeConsumer(this);

			if (isInitialized == false) {
				String error = "imageComplete( " + status
						+ " ) was called before setPixels(...)";
				incomingIteratorOrError.push(error);
				throw new RuntimeException(error);
			}
			if (listening) {
				synchronized (incoming) {
					if (!incoming.isEmpty()) {
						incoming.pull(TIMEOUT_IN_PROCESS);
					}
					PixelPackage pixelPackage = new PixelPackage();
					pixelPackage.finished = true;
					outgoing.push(pixelPackage);
				}
			}

			listening = false;
		}

		private GenericImageSinglePassIterator getPixelIterator() {
			Object incoming = incomingIteratorOrError
					.pull(TIMEOUT_FOR_CONSTRUCTION);
			if (incoming instanceof String) {
				throw new RuntimeException((String) incoming);
			}
			GenericImageSinglePassIterator iterator = (GenericImageSinglePassIterator) incoming;
			return iterator;
		}
	}

	/**
	 * Returns a <code>GenericImageSinglePassIntIterator</code>.
	 * 
	 * @param file
	 *            <code>Toolkit.createImage(filePath)</code> is used to create
	 *            the <code>java.awt.Image</code>, so the supported image types
	 *            are JPG, PNG and GIF.
	 * @param iteratorType
	 *            one of these 4 BufferedImage types: TYPE_INT_ARGB,
	 *            TYPE_INT_ARGB_PRE, TYPE_INT_RGB, TYPE_INT_BGR.
	 * @return a <code>GenericImageSinglePassIntIterator</code> for the file
	 *         provided.
	 */
	public static GenericImageSinglePassIntIterator getIntIterator(File file,
			int iteratorType) {
		return (GenericImageSinglePassIntIterator) get(file, iteratorType);
	}

	/**
	 * Returns a <code>GenericImageSinglePassByteIterator</code>.
	 * 
	 * @param file
	 *            <code>Toolkit.createImage(filePath)</code> is used to create
	 *            the <code>java.awt.Image</code>, so the supported image types
	 *            are JPG, PNG and GIF.
	 * @param iteratorType
	 *            one of these 4 BufferedImage types: TYPE_3BYTE_BGR,
	 *            TYPE_BYTE_GRAY, TYPE_4BYTE_ABGR, TYPE_4BYTE_ABGR_PRE.
	 * @return a <code>GenericImageSinglePassByteIterator</code> for the file
	 *         provided.
	 */
	public static GenericImageSinglePassByteIterator getByteIterator(File file,
			int iteratorType) {
		return (GenericImageSinglePassByteIterator) get(file, iteratorType);
	}

	/**
	 * Returns a <code>GenericImageSinglePassIterator</code> that is either a
	 * <code>IntPixelIterator</code> or a <code>BytePixelIterator</code>.
	 * 
	 * @param file
	 *            <code>Toolkit.createImage(filePath)</code> is used to create
	 *            the <code>java.awt.Image</code>, so the supported image types
	 *            are JPG, PNG and GIF.
	 * @param iteratorType
	 *            one of these 8 BufferedImage types: TYPE_INT_ARGB,
	 *            TYPE_INT_ARGB_PRE, TYPE_INT_RGB, TYPE_INT_BGR, TYPE_3BYTE_BGR,
	 *            TYPE_BYTE_GRAY, TYPE_4BYTE_ABGR, TYPE_4BYTE_ABGR_PRE.
	 * @return a <code>GenericImageSinglePassIterator</code> for the file
	 *         provided.
	 */
	public static GenericImageSinglePassIterator get(File file,
			int iteratorType) {
		Image image = Toolkit.getDefaultToolkit()
				.createImage(file.getAbsolutePath());
		if (image == null)
			throw new IllegalArgumentException(
					"The toolkit could not create an image for "
							+ file.getAbsolutePath());
		return get(image, iteratorType);
	}

	/**
	 * Create a scaled image from a URL.
	 * <p>
	 * If the graphic is already smaller than the maximum size you request: then
	 * the graphic is returned at its original size.
	 * 
	 * @param url
	 *            the graphic to create a thumbnail of.
	 * @param maxSize
	 *            the largest bounds of the thumbnail. For example: if the
	 *            graphic is 1024x768, and you pass a maximum bounds of 120x120:
	 *            then the resulting thumbnail will be 120x90.
	 */
	public static BufferedImage createScaledImage(URL url, Dimension maxSize) {
		Image image = Toolkit.getDefaultToolkit().createImage(url);
		boolean isJPEG = url.toString().toLowerCase().endsWith(".jpg")
				|| url.toString().toLowerCase().endsWith(".jpeg");
		int type = isJPEG ? BufferedImage.TYPE_INT_RGB
				: BufferedImage.TYPE_INT_ARGB;
		IntPixelIterator iter = (IntPixelIterator) get(image, type);
		if (iter == null)
			return null;
		Dimension currentSize = new Dimension(iter.getWidth(),
				iter.getHeight());
		if (currentSize.width <= maxSize.width
				&& currentSize.height <= maxSize.height) {
			return BufferedImageIterator.create(iter, null);
		}
		Dimension newSize = Dimension2D.scaleProportionally(currentSize,
				maxSize);
		PixelIterator scalingIter = ScalingIterator.get(iter, newSize.width,
				newSize.height);
		return BufferedImageIterator.create(scalingIter, null);
	}

	/**
	 * Returns a <code>GenericImageSinglePassIterator</code> that is either a
	 * <code>IntPixelIterator</code> or a <code>BytePixelIterator</code>.
	 * 
	 * @param image
	 *            the image to iterate over.
	 * @param iteratorType
	 *            one of these 8 BufferedImage types: TYPE_INT_ARGB,
	 *            TYPE_INT_ARGB_PRE, TYPE_INT_RGB, TYPE_INT_BGR, TYPE_3BYTE_BGR,
	 *            TYPE_BYTE_GRAY, TYPE_4BYTE_ABGR, TYPE_4BYTE_ABGR_PRE.
	 * @return a <code>GenericImageSinglePassIterator</code> for the image
	 *         provided.
	 */
	public static GenericImageSinglePassIterator get(Image image,
			int iteratorType) {
		if (!(iteratorType == TYPE_DEFAULT
				|| iteratorType == BufferedImage.TYPE_INT_ARGB
				|| iteratorType == BufferedImage.TYPE_INT_ARGB_PRE
				|| iteratorType == BufferedImage.TYPE_INT_RGB
				|| iteratorType == BufferedImage.TYPE_INT_BGR
				|| iteratorType == BufferedImage.TYPE_3BYTE_BGR
				|| iteratorType == BufferedImage.TYPE_BYTE_GRAY
				|| iteratorType == BufferedImage.TYPE_4BYTE_ABGR
				|| iteratorType == BufferedImage.TYPE_4BYTE_ABGR_PRE)) {
			throw new IllegalArgumentException(
					"illegal iterator type: " + iteratorType);
		}
		final ImageProducer producer = image.getSource();
		final Consumer consumer = new Consumer(producer, iteratorType);
		// ImageProducer.startProduction often starts its own thread, but it's
		// not required to. Sometimes in my testing a BufferedImage would make
		// this a blocking call. So to be safe this call should be in its
		// own thread:
		Thread productionThread = new Thread(
				"GenericImageSinglePassIterator: Production Thread") {
			@Override
			public void run() {
				producer.startProduction(consumer);
			}
		};
		productionThread.start();
		return consumer.getPixelIterator();
	}

	final int width, height, type;
	final boolean topDown;
	/**
	 * The number of rows we have processed. This value ranges from [0,
	 * height-1]. This is a counter, so the value of topDown does not affect
	 * this value.
	 */
	int rowCtr = 0;

	private GenericImageSinglePassIterator(int width, int height, int type,
			boolean topDown) {
		this.width = width;
		this.height = height;
		this.type = type;
		this.topDown = topDown;
	}

	/**
	 * Returns the pixel type of this iterator. This will be one of these 8
	 * BufferedImage types: TYPE_INT_ARGB, TYPE_INT_ARGB_PRE, TYPE_INT_RGB,
	 * TYPE_INT_BGR, TYPE_3BYTE_BGR, TYPE_BYTE_GRAY, TYPE_4BYTE_ABGR,
	 * TYPE_4BYTE_ABGR_PRE.
	 */
	@Override
	public int getType() {
		return type;
	}

	/**
	 * If false then there is still pixel data to process by calling
	 * <code>next(...)</code>.
	 * 
	 */
	@Override
	public boolean isDone() {
		return rowCtr == height;
	}

	/**
	 * Whether this data is processed top-down or bottom-up.
	 * 
	 */
	@Override
	public boolean isTopDown() {
		return topDown;
	}

	/**
	 * The width of the image we're iterating over.
	 * 
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/**
	 * The height of the image we're iterating over.
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * The length to create arrays to pass to the <code>next(...)</code> method.
	 */
	@Override
	public int getMinimumArrayLength() {
		return getWidth() * getPixelSize();
	}

	/** Skip the next row of data. */
	@Override
	public void skip() {
		/**
		 * This method does nothing, because it's in the next() method that we
		 * assess what we've been handed and what goes in the destination array.
		 */
		rowCtr++;
		if (isDone()) {
			// wake everyone up, and then any
			// pending push/pull commands will throw
			// an AbortedException
			synchronized (outgoing) {
				outgoing.notifyAll();
			}
			synchronized (incoming) {
				incoming.notifyAll();
			}
		}
	}

	@Override
	protected synchronized void finalize() {
		while (!isDone()) {
			skip();
		}
	}

	PushPullQueue<PixelPackage> incoming = new MyPushPullQueue(this);
	PushPullQueue<PixelPackage> outgoing = new MyPushPullQueue(this);
	private PixelPackage scratchPackage = new PixelPackage();

	/**
	 * 
	 * @param destArray
	 *            will be either an int[] or byte[]
	 */
	synchronized void processNextRow(Object destArray) {
		while (true) {
			synchronized (outgoing) {
				if (incoming.isEmpty()) {
					outgoing.push(scratchPackage, TIMEOUT_IN_PROCESS);
				}
			}
			PixelPackage pixelPackage = incoming.pull(TIMEOUT_IN_PROCESS);
			if (pixelPackage.error != null)
				throw new RuntimeException(pixelPackage.error);

			int pixelPackageRowCtr = pixelPackage.y;
			if (topDown == false) {
				pixelPackageRowCtr = height - pixelPackage.y - 1;
			}

			try {
				if (pixelPackage.finished) {
					rowCtr++;
					fillEmptyRow(destArray);
					// guarantee that in the finally
					// block below we'll continue to
					// reuse this pixel package:
					pixelPackage.h = 1;
					return;
				} else if (pixelPackageRowCtr < rowCtr) {
					// skip this row
					pixelPackage.y++;
					pixelPackage.h--;
					pixelPackage.offset += pixelPackage.scanSize;
				} else if (pixelPackageRowCtr > rowCtr) {
					String error = "The iterator needed to process row "
							+ rowCtr + ", but was given row " + pixelPackage.y;
					pixelPackage.error = error;
					throw new NonSinglePassException(error);
				} else if (pixelPackage.y == rowCtr) {
					populate(destArray, pixelPackage.pixels,
							pixelPackage.offset, pixelPackage.x, pixelPackage.w,
							pixelPackage.colorModel);
					rowCtr++;
					pixelPackage.y++;
					pixelPackage.h--;
					pixelPackage.offset += pixelPackage.scanSize;
					return;
				}
			} catch (RuntimeException e) {
				pixelPackage.h = 0;
				pixelPackage.error = e.getMessage() + "";
				throw e;
			} finally {
				if (pixelPackage.h == 0) {
					// we are done with this package, so
					// acknowledge it to ask for another
					pixelPackage.acknowledge(pixelPackage.error);
					try {
						outgoing.push(pixelPackage, TIMEOUT_IN_PROCESS);
					} catch (CompletedException e) {
						// this is thrown when isDone() is true,
						// so it's not really a problem.
					}
				} else {
					// we're going to read the next row later,
					// so put this back on the queue.
					incoming.push(pixelPackage);
				}
			}
		}
	}

	abstract void fillEmptyRow(Object destArray);

	abstract void populate(Object destArray, Object pixels, int offset, int x,
			int width, ColorModel colorModel);
}