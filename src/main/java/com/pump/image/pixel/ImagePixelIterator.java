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
import java.awt.image.*;
import java.io.Closeable;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import com.pump.awt.Dimension2D;
import com.pump.image.ColorModelUtils;
import com.pump.image.ImageSize;

/**
 * This PixelIterator iterates over a <code>java.awt.Image</code>.
 * <p>
 * If the underlying ImageProducer is producing this data in a single pass,
 * then this object only buffers a few rows of pixels at a time in memory.
 * If the ImageProducer requires multiple passes, then this object will
 * maintain an offscreen int or byte array to store all the pixel data
 * and this iterator won't let you start iterating over the image until
 * the entire image is buffered.
 * </p>
 * 
 * <a href=
 * "https://javagraphics.blogspot.com/2011/05/images-scaling-jpegs-and-pngs.html"
 * >Images: Scaling JPEGs and PNGs</a>
 */
public class ImagePixelIterator<T>
		implements PixelIterator<T>, AutoCloseable {

	// TODO: add static methods to create MutableBufferedImages, then delete ImageLoader.
	// Make sure new methods don't allocate new int[]/byte[] if our ImageConsumer has
	// already buffered *everything* into memory and it comes all at once.

	// TODO: review ImageLoader demo. The default jpg may not show much perf advantage, but I could grab a jpg
	// of my hd that should gains like in the write-up

	// TODO: write unit tests for this class, including lots of variations about how
	// pixel data can arrive.

	/**
	 * This is an image type alternative that indicates we should return
	 * whatever is simplest/most expedient.
	 */
	public static int TYPE_DEFAULT = -888321;

	private static class ImageDescriptor {
		final int imgWidth, imgHeight;
		final ImageType imageType;
		final boolean isOptimized;

		public ImageDescriptor(int imgWidth, int imgHeight, ImageType imageType, boolean isOptimized) {
			this.imgWidth = imgWidth;
			this.imgHeight = imgHeight;
			this.imageType = imageType;
			this.isOptimized = isOptimized;
		}
	}

	/**
	 * This object is passed from the ImageConsumer to the PixelIterator as it becomes available.
	 */
	private static class PixelPackage {
		final int x, y, w, h, offset, scanSize;
		final Object pixels;

		public PixelPackage(int x, int y, int w, int h, Object pixels, int offset, int scanSize) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.pixels = pixels;
			this.offset = offset;
			this.scanSize = scanSize;
		}
	}

	/**
	 * This is the ImageConsumer that listens for updates from the ImageProducer.
	 * This operates in its own thread.
	 */
	private static class Consumer implements ImageConsumer, Closeable {
		final ImageProducer producer;
		Integer imgWidth;
		Integer imgHeight;

		boolean closed;

		/**
		 * This Consumer pushes data to this queue as it becomes available. This
		 * may push 4 things:
		 * 1. An ImageDescriptor that precedes the first PixelPackage
		 * 2. PixelPackages for incoming image data
		 * 3. A String if an error occurs and this consumer is aborting.
		 * 4. Boolean.TRUE if this consumer healthily finished.
		 * <p>
		 * This queue has a limited capacity because there should be another
		 * thread actively ready data from this queue.
		 *
		 * TODO: test scenario where PixelIterator is orphaned
		 * </p>
		 */
		ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<>(10);

		ImageType pixelType;
		/**
		 * This is an int[] or byte[] which is only used for unoptimized image production.
		 * That is: this is only used when we have to load the entire image into memory to
		 * start iterating over pixels.
		 */
		Object buffer;

		private boolean isSinglePassHint, isTopDownLeftRightHint, isCompleteScanLineHint;
		private long queueTimeoueMillis;
		private boolean isInitialized = false;

		/**
		 * @param pixelType if null then the PixelIterator's type will be derived
		 *                  based on some of the first incoming pixel data.
		 * @param queueTimeoueMillis how long to wait in millis when pushing data to a queue.
		 */
		Consumer(ImageProducer producer, ImageType pixelType, long queueTimeoueMillis) {
			this.producer = producer;
			this.pixelType = pixelType;
			this.queueTimeoueMillis = queueTimeoueMillis;
		}

		@Override
		public void close() {
			close("aborted");
		}

		private void close(Object closeStatus) {
			if (!closed) {
				try {
					producer.removeConsumer(this);
					post(closeStatus);
				} finally {
					closed = true;
				}
			}
		}

		/**
		 * Post an element to a queue, or return false. This will return false if either this
		 * consumer has already called {@link #close(Object)}, or if this method exceeded the timeout
		 * and nobody received our data.
		 */
		private boolean post(Object element) {
			if (closed)
				return false;

			long start = System.currentTimeMillis();
			long waitTime = queueTimeoueMillis;
			while (true) {
				try {
					if (queue.offer(element, waitTime, TimeUnit.MILLISECONDS))
						return true;
				} catch (InterruptedException e) {
					// do nothing
				}
				waitTime = queueTimeoueMillis - (System.currentTimeMillis() - start);
				if (waitTime < 0)
					return false;
			}
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
			// intentionally empty
		}

		@Override
		public void setColorModel(ColorModel model) {
			// intentionally empty. The API explicitly says this is a guideline, not a guarantee
		}

		@Override
		public void setHints(int hintFlags) {
			if ( (hintFlags & ImageConsumer.SINGLEPASS) > 0)
				isSinglePassHint = true;
			if ( (hintFlags & ImageConsumer.TOPDOWNLEFTRIGHT) > 0)
				isTopDownLeftRightHint = true;
			if ( (hintFlags & ImageConsumer.COMPLETESCANLINES) > 0)
				isCompleteScanLineHint = true;
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

		private void _setPixels(int x, int y, int w, int h, ColorModel model,
				Object pixels, int offset, int scanSize) {
			if (closed)
				return;

			initialize(model);

			if (buffer != null) {
				PixelIterator iter = new ArrayPixelIterator(pixels,
						w, h, offset, scanSize, ColorModelUtils.getBufferedImageType(model));
				iter = pixelType.createPixelIterator(iter);
				while (!iter.isDone()) {
					iter.next(buffer, (y * imgWidth + x) * pixelType.getSampleCount());
					y++;
				}
				return;
			}

			int modelType = ColorModelUtils.getBufferedImageType(model);
			if (modelType != pixelType.getCode()) {
				// TODO: convert data to pixelType
			}

			post(new PixelPackage(x, y, w, h, pixels, offset, scanSize));
		}

		/**
		 * This is called when setPixels(...) is called, and if a
		 * ImageProducerPixelIterator hasn't been constructed yet: then we
		 * construct it here (or throw an error).
		 */
		private void initialize(ColorModel colorModel) {
			if (isInitialized)
				return;

			try {
				if (imgWidth == null || imgHeight == null) {
					String error = "pixel data was sent but the dimensions were undefined";
					close(error);
					throw new RuntimeException(error);
				}

				int w = imgWidth.intValue();
				int h = imgHeight.intValue();

				if (pixelType == null) {
					// we're supposed to assign it from first sample of pixels:
					int i = ColorModelUtils.getBufferedImageType(colorModel);
					pixelType = ImageType.get(i);

					// i might be ColorModelUtils.TYPE_UNRECOGNIZED
					if (pixelType == null) {
						pixelType = ImageType.INT_ARGB;
					}
				}

				// TODO: can we not require isCompleteScanLineHint? or does that ever come up in testing?
				boolean optimized = isTopDownLeftRightHint && isSinglePassHint && isCompleteScanLineHint;

				ImageDescriptor imageDescriptor;
				switch (pixelType.getCode()) {
					case BufferedImage.TYPE_3BYTE_BGR:
					case BufferedImage.TYPE_4BYTE_ABGR:
					case BufferedImage.TYPE_4BYTE_ABGR_PRE:
					case ImageType.TYPE_3BYTE_RGB:
					case ImageType.TYPE_4BYTE_ARGB:
					case ImageType.TYPE_4BYTE_ARGB_PRE:
					case BufferedImage.TYPE_BYTE_GRAY:
					case BufferedImage.TYPE_INT_ARGB:
					case BufferedImage.TYPE_INT_ARGB_PRE:
					case BufferedImage.TYPE_INT_BGR:
					case BufferedImage.TYPE_INT_RGB:
						imageDescriptor = new ImageDescriptor(w, h, pixelType, optimized);
						break;
					default:
						String error = "unsupported iterator type: " + pixelType;
						close(error);
						throw new RuntimeException(error);
				}

				if (!optimized) {
					if (pixelType.isInt()) {
						buffer = new int[imageDescriptor.imgWidth * imageDescriptor.imgHeight * pixelType.getSampleCount()];
					} else {
						buffer = new byte[imageDescriptor.imgWidth * imageDescriptor.imgHeight * pixelType.getSampleCount()];
					}
				}

				post(imageDescriptor);
			} finally {
				isInitialized = true;
			}
		}

		@Override
		public void imageComplete(int status) {
			if (closed)
				return;

			if (isInitialized == false) {
				String error = "imageComplete( " + status
						+ " ) was called before setPixels(...)";
				close(error);
				throw new RuntimeException(error);
			}

			if (buffer != null) {
				post(new PixelPackage(0, 0, imgWidth, imgHeight, buffer, 0, imgWidth * pixelType.getSampleCount()));
			}

			if (status == ImageConsumer.IMAGEERROR) {
				String error = "The ImageProducer failed with an error.";
				close(error);
				throw new RuntimeException(error);
			} else if (status == ImageConsumer.IMAGEABORTED) {
				String error = "The ImageProducer aborted.";
				close(error);
				throw new RuntimeException(error);
			}

			post(Boolean.TRUE);
		}
	}

	public static class Source<T> implements PixelIterator.Source<T> {
		private final Image image;
		private final ImageType iteratorType;

		private Dimension size;

		public Source(Image image, int iteratorType) {
			this.image = Objects.requireNonNull(image);

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
			this.iteratorType = ImageType.get(iteratorType);

			this.size = Objects.requireNonNull(ImageSize.get(image));
		}

		@Override
		public ImagePixelIterator createPixelIterator() {
			final ImageProducer producer = image.getSource();
			final Consumer consumer = new Consumer(producer, iteratorType, 10_000);

			// TODO: use a thread pool of a fixed size to initiate these

			// ImageProducer.startProduction often starts its own thread, but it's
			// not required to. Sometimes in my testing a BufferedImage would make
			// this a blocking call. So to be safe this call should be in its
			// own thread:
			Thread productionThread = new Thread(
					"ImagePixelIterator: Production Thread") {
				@Override
				public void run() {
					producer.startProduction(consumer);
				}
			};
			productionThread.start();

			Object data = poll(consumer.queue, 100_000);
			if (data instanceof ImageDescriptor) {
				ImageDescriptor d = (ImageDescriptor) data;
				return new ImagePixelIterator(d.imgWidth, d.imgHeight, d.imageType, d.isOptimized, consumer);
			} else if (data instanceof String) {
				throw new RuntimeException((String) data);
			} else if (data instanceof Boolean) {
				// when we receive Boolean.TRUE that should signal a healthy completion, so... what just happened?
				throw new RuntimeException();
			} else {
				// what happened here?
				throw new RuntimeException(String.valueOf(data));
			}
		}

		@Override
		public int getWidth() {
			return size.width;
		}

		@Override
		public int getHeight() {
			return size.height;
		}
	}

	/**
	 * Pull an element from this queue, or return null if we timed out.
	 */
	private static Object poll(ArrayBlockingQueue<Object> queue, long timeoutMillis) {
		long start = System.currentTimeMillis();
		long waitTime = timeoutMillis;
		while (true) {
			try {
				Object v = queue.poll(waitTime, TimeUnit.MILLISECONDS);
				if (v != null)
					return v;
			} catch (InterruptedException e) {
				// do nothing
			}
			waitTime = timeoutMillis - (System.currentTimeMillis() - start);
			if (waitTime < 0)
				return false;
		}
	}

	/**
	 * Returns a <code>ImageProducerPixelIterator</code> that is either a
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
	 * @return a <code>ImagePixelIterator</code> for the file
	 *         provided.
	 */
	public static ImagePixelIterator get(File file,
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
		PixelIterator iter = (PixelIterator<int[]>) get(image, type);
		if (iter == null)
			return null;
		Dimension currentSize = new Dimension(iter.getWidth(),
				iter.getHeight());
		if (currentSize.width <= maxSize.width
				&& currentSize.height <= maxSize.height) {
			return BufferedImageIterator.writeToImage(iter, null);
		}
		Dimension newSize = Dimension2D.scaleProportionally(currentSize,
				maxSize);
		PixelIterator scalingIter = new ScalingIterator(iter, newSize.width,
				newSize.height);
		return BufferedImageIterator.writeToImage(scalingIter, null);
	}

	/**
	 * Returns a <code>ImagePixelIterator</code> that is either a
	 * <code>IntPixelIterator</code> or a <code>BytePixelIterator</code>.
	 * 
	 * @param image
	 *            the image to iterate over.
	 * @param iteratorType
	 *            one of these 8 BufferedImage types: TYPE_INT_ARGB,
	 *            TYPE_INT_ARGB_PRE, TYPE_INT_RGB, TYPE_INT_BGR, TYPE_3BYTE_BGR,
	 *            TYPE_BYTE_GRAY, TYPE_4BYTE_ABGR, TYPE_4BYTE_ABGR_PRE.
	 * @return a <code>ImagePixelIterator</code> for the image
	 *         provided.
	 */
	public static ImagePixelIterator get(Image image,
			int iteratorType) {
		return new Source(image, iteratorType).createPixelIterator();
	}

	final int width, height;
	final ImageType type;
	final boolean isOptimized;
	final Consumer consumer;

	/**
	 * The number of rows we have processed. This value ranges from [0,
	 * height-1].
	 */
	int rowCtr = 0;

	private ImagePixelIterator(int width, int height, ImageType type, boolean isOptimized, Consumer consumer) {
		this.width = width;
		this.height = height;
		this.type = type;
		this.consumer = consumer;
		this.isOptimized = isOptimized;
	}

	/**
	 * An optimized ImagePixelIterator streams pixel data from the ImageProducer as it
	 * becomes available. An unoptimized ImagePixelIterator waits until the ImageProducer
	 * has produced all the pixel data (stored in a large buffer) and then iterates
	 * over that data all it once.
	 */
	public boolean isOptimized() {
		return isOptimized;
	}

	/**
	 * Returns the pixel type of this iterator. This will be one of these 8
	 * BufferedImage types: TYPE_INT_ARGB, TYPE_INT_ARGB_PRE, TYPE_INT_RGB,
	 * TYPE_INT_BGR, TYPE_3BYTE_BGR, TYPE_BYTE_GRAY, TYPE_4BYTE_ABGR,
	 * TYPE_4BYTE_ABGR_PRE.
	 */
	@Override
	public int getType() {
		return type.getCode();
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
		return true;
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

	/** Skip the next row of data. */
	@Override
	public void skip() {
		// we can't tell the consumer we want to skip, but we can ignore the data
		next(null, 0, true);
	}

	/**
	 * @param destArray
	 *            will be either an int[] or byte[]
	 */
	public synchronized void next(T destArray, int destArrayOffset) {
		next(destArray, destArrayOffset, false);
	}

	PixelPackage unfinishedPixelPackage = null;
	int unfinishedPixelPackageY;
	public void next(T destArray, int destArrayOffset, boolean skip) {
		if (unfinishedPixelPackage != null) {
			next_fromUnfinishedPixelPackage(destArray, destArrayOffset, skip);
			return;
		}
		Object data = poll(consumer.queue, 10_000);
		if (data instanceof String) {
			String str = (String) data;
			rowCtr = height;
			throw new RuntimeException(str);
		} else if (data instanceof Boolean) {
			if (isDone())
				throw new IllegalStateException();
			rowCtr = height;
			throw new RuntimeException("unexpected end of image reached at y = " + rowCtr);
		} else if (data instanceof PixelPackage) {
			unfinishedPixelPackage = (PixelPackage) data;
			unfinishedPixelPackageY = unfinishedPixelPackage.y;
			next_fromUnfinishedPixelPackage(destArray, destArrayOffset, skip);
		}
	}

	private void next_fromUnfinishedPixelPackage(T destArray, int destArrayOffset, boolean skip) {
		if (!skip) {
			System.arraycopy(unfinishedPixelPackage.pixels,
					unfinishedPixelPackage.offset + unfinishedPixelPackageY * unfinishedPixelPackage.scanSize + (unfinishedPixelPackage.x) * type.getSampleCount(),
					destArray, destArrayOffset, unfinishedPixelPackage.w * type.getSampleCount());
		}
		rowCtr++;
		unfinishedPixelPackageY++;
		if (unfinishedPixelPackageY == unfinishedPixelPackage.y + unfinishedPixelPackage.h)
			unfinishedPixelPackage = null;
	}

	@Override
	public void close() throws Exception {
		consumer.close();
	}
}