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
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import com.pump.awt.Dimension2D;
import com.pump.image.ColorModelUtils;
import com.pump.image.ImageSize;

/**
 * This pixel iterator processes a <code>java.awt.Image</code> in a single pass
 * using its ImageProducer. The advantage of this class is that
 * it pipes all this information through the PixelIterator interface as it becomes
 * available so a buffer of the <i>entire</i> image is not kept in memory.
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
 * <i>single pass</i>. The <code>ImageProducer</code> can deliver pixels in any
 * order, so we don't really know until we get started if an <code>Image</code>
 * is compatible or not. An <code>ImageProducer</code> does offer hints, though,
 * so we can mostly know as soon as the first pixels arrive (towards the beginning
 * of file parsing) if the producer has offered to deliver pixels the way we need.
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
public class ImageProducerPixelIterator<T>
		implements PixelIterator<T>, AutoCloseable {

	/**
	 * The number of milliseconds threads wait before timing out while reading.
	 * By default this is 5000.
	 */
	public static long TIMEOUT_IN_PROCESS = 5_000;

	/**
	 * The number of milliseconds threads wait before timing out for
	 * construction. Note construction may be severely delayed, because Java's
	 * AWT classes only allow 4 threads at a time. By default this is 120,000 ms
	 * (2 minutes)
	 */
	public static long TIMEOUT_FOR_CONSTRUCTION = 120_000;

	/**
	 * This is an image type alternative that indicates we should return
	 * whatever is simplest/most expedient.
	 */
	public static int TYPE_DEFAULT = -888321;

	/**
	 * This exception indicates that the ImageProducer didn't offer pixels in a
	 * single top-down-left-right pass, or that it didn't advise us with hints
	 * that it was planning to.
	 * <p>
	 * If this occurs you can try the {@link com.pump.image.ImageLoader} class,
	 * which keeps a BufferedImage in memory as pixels made available.
	 * </p>
	 */
	public static class IncompatibleProducerException extends Exception {
		private static final long serialVersionUID = 1L;

		public IncompatibleProducerException(String msg) {
			super(msg);
		}
	}

	private static class ImageDescriptor {
		final int imgWidth, imgHeight, iteratorType;

		public ImageDescriptor(int imgWidth, int imgHeight, int iteratorType) {
			this.imgWidth = imgWidth;
			this.imgHeight = imgHeight;
			this.iteratorType = iteratorType;
		}
	}

	/**
	 * This object is passed from the ImageConsumer to the PixelIterator as it becomes available.
	 */
	private static class PixelPackage {
		final int x, y, w, h, offset, scanSize;
		final Object pixels;
		final ColorModel colorModel;

		public PixelPackage(int x, int y, int w, int h, ColorModel model, Object pixels, int offset, int scanSize) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.colorModel = model;
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
		 * 1. An ImageDescriptor
		 * 2. PixelPackages for incoming image data
		 * 3. A String if an error occurs and this consumer is aborting.
		 * 4. Boolean.TRUE if this consumer healthily finished.
		 */
		SynchronousQueue<Object> queue = new SynchronousQueue<>();

		int iteratorType;

		private boolean isSingleFrameHint, isSinglePassHint, isTopDownLeftRightHint;
		private long queueTimeoueMillis;

		/**
		 *
		 * @param producer
		 * @param iteratorType
		 * @param queueTimeoueMillis how long to wait in millis when pushing data to a queue.
		 */
		Consumer(ImageProducer producer, int iteratorType, long queueTimeoueMillis) {
			this.producer = producer;
			this.iteratorType = iteratorType;
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
					post(queue, closeStatus);
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
		private boolean post(SynchronousQueue<Object> queue, Object element) {
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
			if ( (hintFlags & ImageConsumer.SINGLEFRAME) > 0)
				isSingleFrameHint = true;
			if ( (hintFlags & ImageConsumer.SINGLEPASS) > 0)
				isSinglePassHint = true;
			if ( (hintFlags & ImageConsumer.TOPDOWNLEFTRIGHT) > 0)
				isTopDownLeftRightHint = true;
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

		private PixelPackage unsentPixels = null;

		private void _setPixels(int x, int y, int w, int h, ColorModel model,
				Object pixels, int offset, int scanSize) {
			if (closed)
				return;

			initialize(model);

			/**
			 * Sometimes images are decoded in multiple passes. (See PNG
			 * interlacing for an example.) In this case we would be within our
			 * rights to throw a NonSinglePassException, but since the ENTIRE
			 * block is going to be repeated a few more times, we can still make
			 * this work.
			 *
			 * TODO: is this still needed for PNGs?
			 */
			if (x == 0 && y == 0 && imgWidth.intValue() == w
					&& imgHeight.intValue() == h) {
				// TODO: should we replace or just supplement unsentPixels here?
				unsentPixels = new PixelPackage(x, y, w, h, model, pixels, offset, scanSize);
				return;
			}

			post(queue, new PixelPackage(x, y, w, h, model, pixels, offset, scanSize));
		}

		private boolean isInitialized = false;

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

				if (!(isSingleFrameHint && isSinglePassHint && isTopDownLeftRightHint)) {
					String error = "The ImageProducer did not promise to deliver data in a single top-down-left-right-pass, so it is incompatible with this class. You may want to try the ImageLoader class instead.";
					// TODO:
					//  1. add custom error
					//  2. add unit tests for this condition,
					//  3. check how construction flows and if error should be thrown
					close(error);
					throw new RuntimeException(error);
				}

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
					case BufferedImage.TYPE_INT_ARGB:
					case BufferedImage.TYPE_INT_ARGB_PRE:
					case BufferedImage.TYPE_INT_BGR:
					case BufferedImage.TYPE_INT_RGB:
						post(queue, new ImageDescriptor(w, h, iteratorType));
						break;
					default:
						close("unsupported iterator type: " + iteratorType);
						break;
				}
			} finally {
				isInitialized = true;
			}
		}

		@Override
		public void imageComplete(int status) {
			if (closed)
				return;

			if (unsentPixels != null) {
				post(queue, unsentPixels);
			}

			if (isInitialized == false) {
				String error = "imageComplete( " + status
						+ " ) was called before setPixels(...)";
				close(error);
				throw new RuntimeException(error);
			}
			post(queue, Boolean.TRUE);
		}
	}

	public static class Source<T> implements PixelIterator.Source<T> {
		private final Image image;
		private final int iteratorType;

		private Dimension size;

		public Source(Image image, int iteratorType) {
			this.image = Objects.requireNonNull(image);
			this.iteratorType = iteratorType;

			this.size = Objects.requireNonNull(ImageSize.get(image));
		}

		@Override
		public ImageProducerPixelIterator createPixelIterator() {
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
			final Consumer consumer = new Consumer(producer, iteratorType, 10_000);

			// TODO: use a thread pool of a fixed size to initiate these

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

			Object data = poll(consumer.queue, 100_000);
			if (data instanceof ImageDescriptor) {
				ImageDescriptor d = (ImageDescriptor) data;
				return new ImageProducerPixelIterator(d.imgWidth, d.imgHeight, ImageType.get(d.iteratorType), consumer);
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
	private static Object poll(SynchronousQueue<Object> queue, long timeoutMillis) {
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
	 * @return a <code>GenericImageSinglePassIterator</code> for the file
	 *         provided.
	 */
	public static ImageProducerPixelIterator get(File file,
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
	public static ImageProducerPixelIterator get(Image image,
			int iteratorType) {
		return new Source(image, iteratorType).createPixelIterator();
	}

	final int width, height;
	final ImageType type;
	final Consumer consumer;

	/**
	 * The number of rows we have processed. This value ranges from [0,
	 * height-1].
	 */
	int rowCtr = 0;

	private ImageProducerPixelIterator(int width, int height, ImageType type, Consumer consumer) {
		this.width = width;
		this.height = height;
		this.type = type;
		this.consumer = consumer;
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
		// TODO: what if ColorModel changes?
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