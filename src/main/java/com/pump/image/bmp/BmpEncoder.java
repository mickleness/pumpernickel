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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.pump.UserCancelledException;
import com.pump.image.ImageSize;
import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.ByteBGRAConverter;
import com.pump.image.pixel.ByteBGRConverter;
import com.pump.image.pixel.BytePixelIterator;
import com.pump.util.PushPullQueue;

/**
 * This is a set of static calls to write a simple BMP 2.x image, either in
 * 24-bit or 32-bit depending on whether the source image is opaque.
 *
 */
public class BmpEncoder {

	/**
	 * This reads a PNG, JPG or GIF file as a BufferedImage.
	 * <p>
	 * This method writes the image out to a temporary BMP file, and then reads
	 * it back using a BMP decoder.
	 * 
	 * TODO: rename this bizarre and deceiving method
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static BufferedImage read(File sourceFile) throws IOException {
		Dimension d = ImageSize.get(sourceFile);

		File tempBMPFile = null;
		Image image = null;
		try {
			image = Toolkit.getDefaultToolkit().createImage(
					sourceFile.getAbsolutePath());
			tempBMPFile = File.createTempFile("tempImage", ".bmp");
			writeImageUsingRandomAccess(image, tempBMPFile, d);
			return BmpDecoder.readImage(tempBMPFile);
		} finally {
			if (image != null)
				image.flush();
			if (tempBMPFile != null)
				tempBMPFile.delete();
		}
	}

	/**
	 * Write an image to a file. If the image argument is a
	 * <code>BufferedImage</code>: then this method calls
	 * <code>write(BufferedImage, OutputStream)</code>.
	 * <p>
	 * There is a known bug in Oracle's GIF decoder that can result in images
	 * with less than 256 giving incorrect image dimensions. (I have 2 sample
	 * images that reproduce this consistently, but I don't own the images so I
	 * can't distribute them.) The <code>read(File)</code> method avoids this
	 * problem by using <code>ImageIO</code> to get the dimensions of the source
	 * image, but since that's not an option with this method: the resulting BMP
	 * might be too large.
	 * 
	 * @param image
	 * @param dest
	 * @throws IOException
	 */
	public static void write(Image image, File dest) throws IOException {
		if (image instanceof BufferedImage) {
			BufferedImage bi = (BufferedImage) image;
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(dest);
				write(bi, out);
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return;
		}
		writeImageUsingRandomAccess(image, dest, null);
	}

	public static void write(BufferedImage image, OutputStream out)
			throws IOException {
		write(image, out, true);
	}

	public static void write(BufferedImage image, OutputStream out,
			boolean closeStreamOnCompletion) throws IOException {
		BytePixelIterator i;
		BufferedImage bi = image;
		BufferedImageIterator imageIter = BufferedImageIterator.get(bi, false);
		if (isOpaque(bi)) {
			i = new ByteBGRConverter(imageIter);
		} else {
			i = new ByteBGRAConverter(imageIter);
		}
		write(out, i, closeStreamOnCompletion);
	}

	public static boolean isOpaque(BufferedImage bi) {
		try {
			Method m = BufferedImage.class.getMethod("getTransparency",
					new Class[] {});
			Object returnValue = m.invoke(bi, new Object[] {});
			Field f = BufferedImage.class.getField("OPAQUE");
			return f.get(null).equals(returnValue);
		} catch (Throwable e) {
			// in earlier JVMs this will be a problem:
			int type = bi.getType();
			return (type == BufferedImage.TYPE_4BYTE_ABGR
					|| type == BufferedImage.TYPE_4BYTE_ABGR_PRE
					|| type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_ARGB_PRE);
		}
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
			throw new IllegalArgumentException("width (" + width
					+ ") must be positive");
		if (height <= 0)
			throw new IllegalArgumentException("height (" + height
					+ ") must be positive");
		if (!(bitsPerPixel == 24 || bitsPerPixel == 32))
			throw new IllegalArgumentException("bitsPerPixel (" + bitsPerPixel
					+ ") must be 24 or 32");

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
		destinationArray[12 + arrayOffset] = (byte) ((HEADER_SIZE >> 16) & 0xff);
		destinationArray[13 + arrayOffset] = (byte) ((HEADER_SIZE >> 24) & 0xff);

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
			i.next(scrap);
			out.write(scrap, 0, scanLineSize);
		}
		if (closeStreamOnCompletion)
			out.close();
	}

	/**
	 * In another draft of this project there was an alternative data model:
	 * keeping the pixel data in a large byte array in memory. It wouldn't be
	 * hard to reimplement that model based on the methods in this object if
	 * that ever becomes appropriate.
	 */
	private static class RandomAccessDataModel {
		RandomAccessFile randomAccessFile;

		// File file;

		RandomAccessDataModel(File file, int totalBytes, int width, int height,
				int bitsPerPixel) throws IOException {
			// this.file = file;
			randomAccessFile = new RandomAccessFile(file, "rw");
			randomAccessFile.setLength(totalBytes);

			byte[] header = new byte[HEADER_SIZE];
			writeHeader(header, 0, width, height, bitsPerPixel);
			randomAccessFile.write(header, 0, header.length);
		}

		public void write(int position, byte[] data, int dataOffset,
				int dataLength) throws IOException {
			randomAccessFile.seek(position + HEADER_SIZE);
			randomAccessFile.write(data, dataOffset, dataLength);

		}

		// public void read(int position,byte[] dest,int destOffset,int
		// destLength) throws IOException {
		// randomAccessFile.seek(position+HEADER_SIZE);
		// randomAccessFile.readFully(dest, destOffset, destLength);
		// }

		public void dispose() throws IOException {
			randomAccessFile.close();
		}
	}

	/**
	 * The number of milliseconds we will wait for a <code>java.awt.Image</code>
	 * to finish sending all its pixel data. The default value is 15,000 (15
	 * seconds).
	 */
	public static long LOAD_IMAGE_TIMEOUT = 15000;

	/**
	 * This is the consumer that listens for updates from the ImageProducer.
	 * These changes are then passed to the pixel iterator.
	 */
	private static class Consumer implements ImageConsumer {
		Integer width = null;
		Integer height = null;
		@SuppressWarnings("rawtypes")
		Map properties = new HashMap();
		Integer returnValue;
		PushPullQueue<Dimension> sizeQueue = new PushPullQueue<Dimension>();
		PushPullQueue<Integer> statusQueue = new PushPullQueue<Integer>();
		// An error that occurred.
		Throwable throwable;
		ImageProducer imageProducer;
		RandomAccessDataModel dataModel;
		File dest;
		int bytesPerPixel = -1;
		int scanlineSize;

		Consumer(ImageProducer imageProducer, File dest, Dimension imageSize) {
			this.imageProducer = imageProducer;
			this.dest = dest;
			if (imageSize != null) {
				setDimensions(imageSize.width, imageSize.height);
			}
		}

		public void setDimensions(int w, int h) {
			if (w >= 0 && width == null)
				width = new Integer(w);
			if (h >= 0 && height == null)
				height = new Integer(h);
		}

		private int getReturnValue() {
			if (returnValue != null)
				return returnValue.intValue();

			return statusQueue.pull(LOAD_IMAGE_TIMEOUT).intValue();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void setProperties(Hashtable props) {
			properties.putAll(props);
		}

		public void setColorModel(ColorModel model) {
		}

		public void setHints(int hintFlags) {
		}

		private void initDataModel(ColorModel colorModel) {
			if (dataModel != null)
				return;

			if (width == null || height == null) {
				RuntimeException e = new RuntimeException(
						"width and height were not initialized when pixel data was sent.");
				imageComplete(ImageConsumer.IMAGEERROR, e);
				return;
			}

			if (colorModel.hasAlpha()) {
				bytesPerPixel = 4;
			} else {
				bytesPerPixel = 3;
			}

			scanlineSize = getScanlineSize(width.intValue(), bytesPerPixel);
			Dimension d = new Dimension(width.intValue(), height.intValue());
			int totalBytes = scanlineSize * d.height + HEADER_SIZE;
			try {
				dataModel = new RandomAccessDataModel(dest, totalBytes,
						d.width, d.height, bytesPerPixel * 8);
				sizeQueue.push(d);
			} catch (Exception e) {
				imageComplete(ImageConsumer.IMAGEERROR, e);
			}

		}

		byte[] smallArray;
		int dy = 0;
		int dx = 0;

		public synchronized void setPixels(int x, int y, final int w,
				final int h, final ColorModel model, final byte[] pixels,
				final int offset, final int scanSize) {
			initDataModel(model);

			if (returnValue != null)
				throw new IllegalStateException();

			int inPixelSize = model.getPixelSize();
			int outPixelSize = bytesPerPixel;
			if (smallArray == null || smallArray.length < inPixelSize)
				smallArray = new byte[inPixelSize];
			if (scratchByteArray == null
					|| scratchByteArray.length < scanlineSize)
				scratchByteArray = new byte[scanlineSize];

			try {
				IndexColorModel indexColorModel = null;
				if (model instanceof IndexColorModel) {
					indexColorModel = (IndexColorModel) model;
					/**
					 * This addresses a bug with GIFs on my 10.5.8 machine where
					 * the abstract Image provides the wrong size:
					 */
					if (x + w > width.intValue() && w == width.intValue()
							&& dx == 0) {
						dx = -x;
						dy = -y;
					}
					x += dx;
					y += dy;
				}

				for (int j = 0; j < h; j++) {
					for (int k = 0; k < w; k++) {
						int rgb;
						if (indexColorModel != null) {
							int index = (pixels[j * scanSize + k]) & 0xff;
							if (index == indexColorModel.getTransparentPixel()) {
								rgb = 0x00000000;
							} else {
								rgb = indexColorModel.getRGB(index);
							}
						} else {
							System.arraycopy(pixels, j * scanSize + k,
									smallArray, 0, inPixelSize);
							rgb = model.getRGB(smallArray);
						}
						switch (bytesPerPixel) {
						case 3:
							scratchByteArray[3 * k + 0] = (byte) ((rgb >> 0) & 0xff);
							scratchByteArray[3 * k + 1] = (byte) ((rgb >> 8) & 0xff);
							scratchByteArray[3 * k + 2] = (byte) ((rgb >> 16) & 0xff);
							break;
						case 4:
							scratchByteArray[4 * k + 0] = (byte) ((rgb >> 0) & 0xff);
							scratchByteArray[4 * k + 1] = (byte) ((rgb >> 8) & 0xff);
							scratchByteArray[4 * k + 2] = (byte) ((rgb >> 16) & 0xff);
							scratchByteArray[4 * k + 3] = (byte) ((rgb >> 24) & 0xff);
							break;
						default:
							throw new RuntimeException(
									"unexpected condition: bytesPerPixel = "
											+ bytesPerPixel);
						}
					}
					dataModel.write((height.intValue() - 1 - (y + j))
							* scanlineSize + x * outPixelSize,
							scratchByteArray, 0, w * outPixelSize);
				}
			} catch (Exception e) {
				imageComplete(ImageConsumer.IMAGEERROR, e);
			}
		}

		byte[] scratchByteArray;

		public synchronized void setPixels(int x, int y, int w, int h,
				ColorModel model, int[] pixels, int offset, int scanSize) {
			initDataModel(model);

			if (returnValue != null)
				throw new IllegalStateException();

			if (scratchByteArray == null
					|| scratchByteArray.length < scanlineSize) {
				scratchByteArray = new byte[scanlineSize];
			}

			try {
				switch (bytesPerPixel) {
				case 3:
					for (int j = 0; j < h; j++) {
						for (int k = 0; k < w; k++) {
							int rgb = model.getRGB(pixels[x + k + offset + j
									* scanSize]);
							scratchByteArray[3 * k + 0] = (byte) ((rgb >> 0) & 0xff);
							scratchByteArray[3 * k + 1] = (byte) ((rgb >> 8) & 0xff);
							scratchByteArray[3 * k + 2] = (byte) ((rgb >> 16) & 0xff);
						}
						dataModel.write((height.intValue() - 1 - (y + j))
								* scanlineSize + x * bytesPerPixel,
								scratchByteArray, 0, w * bytesPerPixel);
					}
					return;
				case 4:
					for (int j = 0; j < h; j++) {
						for (int k = 0; k < w; k++) {
							int rgb = model.getRGB(pixels[x + k + offset + j
									* scanSize]);
							scratchByteArray[4 * k + 0] = (byte) ((rgb >> 0) & 0xff);
							scratchByteArray[4 * k + 1] = (byte) ((rgb >> 8) & 0xff);
							scratchByteArray[4 * k + 2] = (byte) ((rgb >> 16) & 0xff);
							scratchByteArray[4 * k + 3] = (byte) ((rgb >> 24) & 0xff);
						}
						dataModel.write((height.intValue() - 1 - (y + j))
								* scanlineSize + x * bytesPerPixel,
								scratchByteArray, 0, w * bytesPerPixel);
					}
					return;
				default:
					throw new RuntimeException(
							"unexpected condition: bytesPerPixel = "
									+ bytesPerPixel);
				}
			} catch (Exception e) {
				imageComplete(ImageConsumer.IMAGEERROR, e);
			}
		}

		public void imageComplete(int status) {
			Throwable throwable = null;
			if (status == ImageConsumer.IMAGEABORTED) {
				throwable = new UserCancelledException();
			} else if (status == ImageConsumer.IMAGEERROR) {
				throwable = new IOException(
						"An error occurred reading this image.");
			} else if (!(status == ImageConsumer.SINGLEFRAMEDONE || status == ImageConsumer.STATICIMAGEDONE)) {
				throwable = new IOException(
						"Unrecognized completion status code: " + status);
			}
			imageComplete(status, throwable);
		}

		private synchronized void imageComplete(int status, Throwable throwable) {
			if (returnValue == null) {
				try {
					dataModel.dispose();
				} catch (Exception e) {
					if (throwable == null) {
						throwable = e;
					} else {
						e.printStackTrace();
					}
				}

				this.throwable = throwable;
				if (sizeQueue.isEmpty() && (width == null || height == null)) {
					if (this.throwable == null) {
						this.throwable = new IOException(
								"Completion code ("
										+ status
										+ ") was sent before width and height were defined.");
					}
					sizeQueue.push(new Dimension(-1, -1));
				}
				returnValue = new Integer(status);
				// unless we're interested in other frames, we should stop
				// listening now:
				imageProducer.removeConsumer(this);
				statusQueue.push(returnValue);
			}
		}
	}

	/**
	 * 
	 * @param image
	 * @param dest
	 * @param imageSize
	 *            an optional argument for the size of the image. This is
	 *            provided because on my Mac 10.5.8 machine running Java 1.6:
	 *            the Toolkit-based GIF images with less than 256 can return
	 *            dimensions that are too large.
	 * @throws IOException
	 */
	private static void writeImageUsingRandomAccess(Image image, File dest,
			Dimension imageSize) throws IOException {
		final ImageProducer imageProducer = image.getSource();
		final Consumer consumer = new Consumer(imageProducer, dest, imageSize);
		Thread thread = new Thread("Produce/Consume Image "
				+ imageProducer.toString()) {
			@Override
			public void run() {
				imageProducer.startProduction(consumer);
			}
		};
		thread.start();
		int returnValue = consumer.getReturnValue();
		if (!(returnValue == ImageConsumer.SINGLEFRAMEDONE || returnValue == ImageConsumer.STATICIMAGEDONE)) {
			if (consumer.throwable instanceof IOException) {
				IOException e2 = new IOException(
						consumer.throwable.getMessage());
				e2.initCause(consumer.throwable);
				throw e2;
			} else if (consumer.throwable != null) {
				RuntimeException e2 = new RuntimeException(
						consumer.throwable.getMessage());
				e2.initCause(consumer.throwable);
				throw e2;
			} else {
				throw new IOException(
						"An error occurred processing this image.  Completion code: "
								+ returnValue);
			}
		}
	}
}