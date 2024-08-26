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

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.pump.image.ColorModelUtils;
import com.pump.image.QBufferedImage;

/**
 * This uses the ImageProducer / ImageConsumer model to iterate over all the
 * rows in an image.
 * <p>
 * If the ImageProducer promises to deliver the pixels in a certain order (via
 * the {@link ImageConsumer#setHints(int)} method) then this pipes pixels
 * efficiently from the ImageProducer to whatever is consuming this iterator.
 * Otherwise this has to buffer the entire image in memory, and attempts to read
 * the first row of data from this PixelIterator will block until that buffer is
 * fully loaded.
 * </p>
 *
 * @param <T>
 */
class ImageProducerPixelIterator<T> implements PixelIterator<T> {

	/**
	 * This exception is thrown if {@link ImageConsumer#imageComplete(int)} is
	 * called with {@link ImageConsumer#IMAGEERROR}.
	 */
	public static class ImageProducerException extends RuntimeException {
		public ImageProducerException(String msg) {
			super(msg);
		}
	}

	/**
	 * This exception is thrown if {@link ImageConsumer#imageComplete(int)} is
	 * called with {@link ImageConsumer#IMAGEABORTED}.
	 */
	public static class ImageProducerAbortedException extends RuntimeException {
		public ImageProducerAbortedException(String msg) {
			super(msg);
		}
	}

	/**
	 * This exception is thrown in both the production and consumer threads if
	 * the producer timed out while trying to post new data. This probably means
	 * the consumer was either orphaned or it took too long to pull data from
	 * the incoming queue.
	 */
	public static class ImageProducerTimedOutException
			extends RuntimeException {
		public ImageProducerTimedOutException(String msg) {
			super(msg);
		}
	}

	/**
	 * This exception is thrown if the ImageProducer stops posting image updates
	 * after {@link #TIMEOUT_MILLIS} milliseconds. This may indicate an IO
	 * difficulty (like a slow network connection), or it may indicate the
	 * production thread has somehow aborted.
	 */
	public static class ImageProducerUnresponsiveException
			extends RuntimeException {
		public ImageProducerUnresponsiveException(String msg) {
			super(msg);
		}
	}

	/**
	 * The number of milliseconds before push/pull operations between threads
	 * timeout.
	 */
	public static long TIMEOUT_MILLIS = 10_000;

	private static final String IMAGE_CONSUMER_COMPLETE_VIA_ERROR = "The ImageProducer failed with an error.";
	private static final String IMAGE_CONSUMER_COMPLETE_VIA_ABORTED = "The ImageProducer aborted.";
	private static final String IMAGE_PRODUCER_TIMED_OUT_POSTING_DATA = "The ImageProducer thread aborted because it timed out while attempting to post new information.";

	private static final String PIXEL_ITERATOR_CLOSED = "The PixelIterator closed.";

	/**
	 * This object is the first packet of info passed from the ImageConsumer to
	 * the PixelIterator
	 */
	private static class ImageDescriptor {
		final int imgWidth, imgHeight;
		final ImageType imageType;
		final boolean isOptimized;

		public ImageDescriptor(int imgWidth, int imgHeight, ImageType imageType,
				boolean isOptimized) {
			this.imgWidth = imgWidth;
			this.imgHeight = imgHeight;
			this.imageType = imageType;
			this.isOptimized = isOptimized;
		}
	}

	/**
	 * This object is passed from the ImageConsumer to the PixelIterator as it
	 * becomes available.
	 */
	private static class PixelPackage {
		final int x, y, w, h, offset, scanSize;
		final Object pixels;
		final ImageType imageType;

		PixelPackage(int x, int y, int w, int h, Object pixels, int offset,
				int scanSize, ImageType imageType) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.pixels = pixels;
			this.offset = offset;
			this.scanSize = scanSize;
			this.imageType = imageType;
		}

		/**
		 * Create a QBufferedImage of all pixels in this PixelPackage.
		 */
		QBufferedImage createBufferedImage(int y) {
			int arrayOffset = offset + y * scanSize;

			WritableRaster raster = null;
			if (imageType.getColorModel() instanceof DirectColorModel) {
				DirectColorModel dcm = (DirectColorModel) imageType
						.getColorModel();
				DataBuffer d = new DataBufferInt((int[]) pixels, h * scanSize,
						arrayOffset);

				int[] bandmasks;
				if (dcm.hasAlpha()) {
					bandmasks = new int[4];
					bandmasks[3] = dcm.getAlphaMask();
				} else {
					bandmasks = new int[3];
				}
				bandmasks[0] = dcm.getRedMask();
				bandmasks[1] = dcm.getGreenMask();
				bandmasks[2] = dcm.getBlueMask();

				raster = Raster.createPackedRaster(d, w, h, scanSize, bandmasks,
						new Point(0, 0));
			} else if (imageType
					.getColorModel() instanceof ComponentColorModel) {
				int[] bandOffsets = null;
				switch (imageType.getCode()) {
				case BufferedImage.TYPE_3BYTE_BGR:
					bandOffsets = new int[] { 2, 1, 0 };
					break;
				case BufferedImage.TYPE_4BYTE_ABGR_PRE:
				case BufferedImage.TYPE_4BYTE_ABGR:
					bandOffsets = new int[] { 3, 2, 1, 0 };
					break;
				case BufferedImage.TYPE_BYTE_GRAY:
					bandOffsets = new int[] { 0 };
					break;
				}

				if (bandOffsets != null) {
					DataBuffer d = new DataBufferByte((byte[]) pixels,
							h * scanSize, arrayOffset);
					raster = Raster.createInterleavedRaster(d, w, h, scanSize,
							imageType.getSampleCount(), bandOffsets,
							new Point(0, 0));
				}
			}

			if (raster == null)
				throw new NullPointerException(
						"Unsupported image type: " + imageType);

			return new QBufferedImage(imageType.getColorModel(), raster,
					imageType.getColorModel().isAlphaPremultiplied(),
					new Hashtable<>());
		}
	}

	/**
	 * This is the ImageConsumer that listens for updates from the
	 * ImageProducer. This operates in its own thread.
	 */
	private static class Consumer implements ImageConsumer {

		private final ImageProducer producer;

		Integer imgWidth;
		Integer imgHeight;

		AtomicBoolean isClosed = new AtomicBoolean(false);

		/**
		 * This Consumer pushes data to this queue as it becomes available. This
		 * may push 4 things: 1. An ImageDescriptor that precedes the first
		 * PixelPackage 2. PixelPackages for incoming image data 3. A String if
		 * an error occurs and this consumer is aborting. 4. Boolean.TRUE if
		 * this consumer healthily finished.
		 * <p>
		 * This queue has a limited capacity because there should be another
		 * thread actively ready data from this queue.
		 */
		ArrayBlockingQueue<Object> queue = new ArrayBlockingQueue<>(10);

		ImageType pixelType;

		/**
		 * This is an int[] or byte[] which is only used for unoptimized image
		 * production. That is: this is only used when we have to load the
		 * entire image into memory to start iterating over pixels.
		 */
		Object buffer;

		private final Collection<Thread> pixelProductionThreads = Collections
				.synchronizedSet(new HashSet<>());

		private boolean isSinglePassHint, isTopDownLeftRightHint,
				isCompleteScanLineHint;
		private long queueTimeoueMillis;
		private boolean isInitialized = false;

		/**
		 * @param pixelType
		 *            if null then the PixelIterator's type will be derived
		 *            based on some of the first incoming pixel data.
		 * @param queueTimeoueMillis
		 *            how long to wait in millis when pushing data to a queue.
		 */
		Consumer(ImageProducer producer, ImageType pixelType,
				long queueTimeoueMillis) {
			this.producer = producer;
			this.pixelType = pixelType;
			this.queueTimeoueMillis = queueTimeoueMillis;
		}

		private void close(Object closeStatus, boolean purgeQueuedImageData) {
			if (isClosed.compareAndSet(false, true)) {
				producer.removeConsumer(this);

				if (purgeQueuedImageData)
					queue.removeIf(o -> o instanceof ImageDescriptor
							|| o instanceof PixelPackage);

				synchronized (pixelProductionThreads) {
					for (Thread thread : pixelProductionThreads) {
						if (thread != Thread.currentThread())
							thread.interrupt();
					}
				}

				post(closeStatus);
			}
		}

		/**
		 * Post an element to a queue, or return false. This will return false
		 * if either this consumer has already called <code>close(..)</code>> or
		 * if this method exceeded the timeout and nobody received our data.
		 */
		private boolean post(Object element) {
			long start = System.currentTimeMillis();
			long waitTime = queueTimeoueMillis;
			while (true) {
				if (isClosed.get()) {
					if (element instanceof PixelPackage
							|| element instanceof ImageDescriptor) {
						return false;
					}
				}
				try {
					if (queue.offer(element, waitTime, TimeUnit.MILLISECONDS)) {
						return true;
					}
				} catch (InterruptedException e) {
					// do nothing, we'll iterate again and other offer (again)
					// or abort
				}

				waitTime = queueTimeoueMillis
						- (System.currentTimeMillis() - start);
				if (waitTime < 0 && !isClosed.get()) {
					close(IMAGE_PRODUCER_TIMED_OUT_POSTING_DATA, true);
					throw new ImageProducerTimedOutException(
							IMAGE_PRODUCER_TIMED_OUT_POSTING_DATA);
				}
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
			// intentionally empty. The API explicitly says this is a guideline,
			// not a guarantee
		}

		@Override
		public void setHints(int hintFlags) {
			if ((hintFlags & ImageConsumer.SINGLEPASS) > 0)
				isSinglePassHint = true;
			if ((hintFlags & ImageConsumer.TOPDOWNLEFTRIGHT) > 0)
				isTopDownLeftRightHint = true;
			if ((hintFlags & ImageConsumer.COMPLETESCANLINES) > 0)
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
			if (isClosed.get())
				return;

			Thread currentThread = Thread.currentThread();
			pixelProductionThreads.add(currentThread);
			try {
				initialize(model);

				if (buffer != null) {
					PixelIterator iter = new ArrayPixelIterator(pixels, w, h,
							offset, scanSize,
							ColorModelUtils.getBufferedImageType(model));
					iter = pixelType.createPixelIterator(iter);
					while (!iter.isDone()) {
						iter.next(buffer, (y * imgWidth + x)
								* pixelType.getSampleCount());
						y++;
					}
					return;
				}

				int modelType = ColorModelUtils.getBufferedImageType(model);
				Object pixelPackageArray;
				if (modelType == pixelType.getCode()) {
					if (pixels instanceof int[]) {
						pixelPackageArray = ((int[]) pixels).clone();
					} else if (pixels instanceof byte[]) {
						pixelPackageArray = ((byte[]) pixels).clone();
					} else {
						throw new IllegalStateException(
								pixels.getClass().getName());
					}
				} else {
					PixelIterator iter = new ArrayPixelIterator(pixels, w, h,
							offset, scanSize,
							ColorModelUtils.getBufferedImageType(model));
					iter = pixelType.createPixelIterator(iter);
					pixelPackageArray = readAll(iter);
					offset = 0;
					scanSize = iter.getWidth() * pixelType.getSampleCount();
				}

				post(new PixelPackage(x, y, w, h, pixelPackageArray, offset,
						scanSize, pixelType));
			} finally {
				pixelProductionThreads.remove(currentThread);
			}
		}

		private Object readAll(PixelIterator iter) {
			ImageType iterType = ImageType.get(iter.getType());
			Object array;
			if (iterType.isByte()) {
				array = new byte[iter.getWidth() * iter.getPixelSize()
						* iter.getHeight()];
			} else {
				array = new int[iter.getWidth() * iter.getPixelSize()
						* iter.getHeight()];
			}

			int offset = 0;
			while (!iter.isDone()) {
				iter.next(array, offset);
				offset += iter.getWidth() * pixelType.getSampleCount();
			}
			return array;
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
					close(error, false);
					return;
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

				// TODO: can we not require isCompleteScanLineHint? or does this
				// case come up in real-world testing?
				boolean optimized = isTopDownLeftRightHint && isSinglePassHint
						&& isCompleteScanLineHint;

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
					imageDescriptor = new ImageDescriptor(w, h, pixelType,
							optimized);
					break;
				default:
					String error = "unsupported iterator type: " + pixelType;
					close(error, false);
					return;
				}

				if (!optimized) {
					if (pixelType.isInt()) {
						buffer = new int[imageDescriptor.imgWidth
								* imageDescriptor.imgHeight
								* pixelType.getSampleCount()];
					} else {
						buffer = new byte[imageDescriptor.imgWidth
								* imageDescriptor.imgHeight
								* pixelType.getSampleCount()];
					}
				}

				post(imageDescriptor);
			} finally {
				isInitialized = true;
			}
		}

		@Override
		public void imageComplete(int status) {
			if (isClosed.get())
				return;

			if (status == ImageConsumer.IMAGEERROR) {
				close(IMAGE_CONSUMER_COMPLETE_VIA_ERROR, false);
				return;
			} else if (status == ImageConsumer.IMAGEABORTED) {
				close(IMAGE_CONSUMER_COMPLETE_VIA_ABORTED, false);
				return;
			}

			if (isInitialized == false) {
				String error = "imageComplete( " + status
						+ " ) was called before setPixels(...)";
				close(error, false);
				return;
			}

			if (buffer != null) {
				post(new PixelPackage(0, 0, imgWidth, imgHeight, buffer, 0,
						imgWidth * pixelType.getSampleCount(), pixelType));
			}

			close(Boolean.TRUE, false);
		}
	}

	/**
	 * Pull an element from this queue, or return null if we timed out.
	 */
	private static Object poll(ArrayBlockingQueue<Object> queue,
			long timeoutMillis, boolean allowFailedPoll) {
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
			if (waitTime < 0) {
				if (allowFailedPoll)
					return null;
				return Boolean.FALSE;
			}
		}
	}

	private static int THREAD_CTR = 0;

	final int width, height;
	final ImageType type;
	final boolean isOptimized;
	final Consumer consumer;

	/**
	 * The number of rows we have processed. This value ranges from [0,
	 * height-1].
	 */
	int rowCtr = 0;
	boolean isClosed = false;

	/**
	 * @param errorDescriptor
	 *            an optional String embedded in RuntimeExceptions if an error
	 *            comes up setting this iterator up. For example: if this image
	 *            is coming from a specific file then this should refer to the
	 *            file path. This way an exception identifies exactly which file
	 *            was being read.
	 */
	ImageProducerPixelIterator(ImageProducer imageProducer,
			ImageType<T> requestedOutputType, String errorDescriptor) {
		Objects.requireNonNull(imageProducer,
				errorDescriptor == null ? "null ImageProducer"
						: "null ImageProducer for " + errorDescriptor);

		consumer = new Consumer(imageProducer, requestedOutputType,
				TIMEOUT_MILLIS);

		// ImageProducer.startProduction often starts its own thread, but it's
		// not required to. To be extra safe we're going to wrap the call to
		// start
		// production in a separate thread. Most of the time this is useless and
		// this will instead activate *a different* thread (IIRC the Toolkit has
		// a thread pool of 4 threads that help load images), but that's really
		// out
		// of our control.
		//
		// Note the BufferedImage's ImageProducer (the OffScreenImageSource)
		// is NOT multithreaded; if somehow that is ever passed to this
		// constructor:
		// bad things (hopefully long timeouts) will happen. This class is
		// intended
		// to interact with ToolkitImages, though. There's a separate
		// class/branch that
		// should be in charge of BufferedImages.

		Runnable productionRunnable = () -> imageProducer
				.startProduction(consumer);
		Thread thread = new Thread(productionRunnable,
				"ImageProducerPixelIterator-thread-" + (THREAD_CTR++));
		thread.start();

		Object data = poll(consumer.queue, 100_000, false);
		if (data instanceof ImageDescriptor) {
			ImageDescriptor d = (ImageDescriptor) data;
			width = d.imgWidth;
			height = d.imgHeight;
			isOptimized = d.isOptimized;
			type = d.imageType;
		} else if (data instanceof String) {
			throwException((String) data);

			// this won't be reached; it's just to make the compiler happy:
			width = height = 0;
			isOptimized = false;
			type = null;
		} else if (data instanceof Boolean) {
			// when we receive Boolean.TRUE that should signal a healthy
			// completion, so... what just happened?
			throw new RuntimeException();
		} else {
			// what happened here?
			throw new RuntimeException(String.valueOf(data));
		}
	}

	/**
	 * This throws a RuntimeException with a given message. It may throw a
	 * specialized subclass like ImageProducerException or
	 * ImageProducerAbortedException based on the message string.
	 */
	private void throwException(String msg) {
		if (IMAGE_CONSUMER_COMPLETE_VIA_ERROR.equals(msg)) {
			throw new ImageProducerException(IMAGE_CONSUMER_COMPLETE_VIA_ERROR);
		} else if (IMAGE_PRODUCER_TIMED_OUT_POSTING_DATA.equals(msg)) {
			throw new ImageProducerTimedOutException(
					IMAGE_PRODUCER_TIMED_OUT_POSTING_DATA);
		} else if (IMAGE_CONSUMER_COMPLETE_VIA_ABORTED.equals(msg)) {
			throw new ImageProducerAbortedException(
					IMAGE_CONSUMER_COMPLETE_VIA_ABORTED);
		}

		throw new RuntimeException((String) msg);
	}

	/**
	 * An optimized ImagePixelIterator streams pixel data from the ImageProducer
	 * as it becomes available. An unoptimized ImagePixelIterator waits until
	 * the ImageProducer has produced all the pixel data (stored in a large
	 * buffer) and then iterates over that data all it once.
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
		if (rowCtr < height)
			return false;

		// one last check (without waiting) to see if there's an error condition
		// after all pixel data is read:
		if (unfinishedPixelPackage == null)
			pollNextPixelPackage(0);

		return true;
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
		// we can't tell the consumer we want to skip, but we can ignore the
		// data
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

	private void next(T destArray, int destArrayOffset, boolean skip) {
		if (isClosed)
			throw new ClosedException();

		if (unfinishedPixelPackage == null) {
			pollNextPixelPackage(TIMEOUT_MILLIS);
		}
		next_fromUnfinishedPixelPackage(destArray, destArrayOffset, skip);

		if (isDone())
			close();
	}

	/**
	 * Prepares the {@link #unfinishedPixelPackage} field with the next unread
	 * PixelPackage, or throws a RuntimeException if the producer thread has an
	 * error or times out.
	 */
	private void pollNextPixelPackage(long timeoutMillis) {
		Object data = poll(consumer.queue, timeoutMillis, timeoutMillis == 0);
		if (data instanceof String) {
			String str = (String) data;
			rowCtr = height;

			if (PIXEL_ITERATOR_CLOSED.equals(str)) {
				// This means someone called pixelIterator.close(). We're OK;
				// there's else nothing to poll.
				return;
			}

			throwException(str);
		} else if (data instanceof Boolean) {
			if (rowCtr == height && Boolean.TRUE.equals(data)) {
				// we're finished, and we don't expect anymore incoming data
				return;
			}

			// make sure isDone() returns true
			rowCtr = height;

			if (Boolean.FALSE.equals(data)) {
				// this indicates our poll timed out; the producer may be
				// dead/aborted/unreachable
				throw new ImageProducerUnresponsiveException(
						"the image consumer is unresponsive; y = " + rowCtr);
			} else {
				// this indicates the producer thinks it passed all the required
				// info
				throw new RuntimeException(
						"unexpected end of image reached; y = " + rowCtr);
			}
		} else if (data instanceof PixelPackage) {
			unfinishedPixelPackage = (PixelPackage) data;
			unfinishedPixelPackageY = unfinishedPixelPackage.y;
		}
	}

	private void next_fromUnfinishedPixelPackage(T destArray,
			int destArrayOffset, boolean skip) {
		if (!skip) {
			System.arraycopy(unfinishedPixelPackage.pixels,
					unfinishedPixelPackage.offset
							+ (unfinishedPixelPackageY
									- unfinishedPixelPackage.y)
									* unfinishedPixelPackage.scanSize
							+ unfinishedPixelPackage.x * type.getSampleCount(),
					destArray, destArrayOffset,
					unfinishedPixelPackage.w * type.getSampleCount());
		}
		rowCtr++;
		unfinishedPixelPackageY++;
		if (unfinishedPixelPackageY == unfinishedPixelPackage.y
				+ unfinishedPixelPackage.h)
			unfinishedPixelPackage = null;
	}

	@Override
	public void close() {
		// make sure isDone() returns true
		rowCtr = height;
		isClosed = true;

		consumer.close(PIXEL_ITERATOR_CLOSED, true);
	}
}