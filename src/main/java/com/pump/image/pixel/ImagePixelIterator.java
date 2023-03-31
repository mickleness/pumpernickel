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

import java.awt.*;
import java.awt.image.*;
import java.io.Closeable;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

import com.pump.image.ColorModelUtils;
import com.pump.image.ImageSize;
import com.pump.image.MutableBufferedImage;

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

	/**
	 * This exception is thrown if {@link ImageConsumer#imageComplete(int)} is called with {@link ImageConsumer#IMAGEERROR}.
	 */
	public static class ImageProducerException extends RuntimeException {
		public ImageProducerException(String msg) {
			super(msg);
		}
	}

	/**
	 * This exception is thrown if {@link ImageConsumer#imageComplete(int)} is called with {@link ImageConsumer#IMAGEABORTED}.
	 */
	public static class ImageProducerAbortedException extends RuntimeException {
		public ImageProducerAbortedException(String msg) {
			super(msg);
		}
	}

	/**
	 * This exception is thrown in both the production and consumer threads if the producer timed out
	 * while trying to post new data. This probably means the consumer was either orphaned or it
	 * took too long to pull data from the incoming queue.
	 */
	public static class ImageProducerTimedOutException extends RuntimeException {
		public ImageProducerTimedOutException(String msg) {
			super(msg);
		}
	}

	/**
	 * This exception is thrown if the ImageProducer stops posting image updates after {@link #TIMEOUT_MILLIS} milliseconds.
	 * This may indicate an IO difficulty (like a slow network connection), or it may indicate the production thread
	 * has somehow aborted.
	 */
	public static class ImageProducerUnresponsiveException extends RuntimeException {
		public ImageProducerUnresponsiveException(String msg) {
			super(msg);
		}
	}

	/**
	 * This manages the threads use start ImageProducer production in.
	 */
	static ThreadPoolExecutor PRODUCTION_EXECUTOR = new ThreadPoolExecutor(0, 6,
			0L,TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

	/**
	 * The number of milliseconds before push/pull operations between threads timeout.
	 */
	public static long TIMEOUT_MILLIS = 10_000;

	// TODO: review ImageLoader demo. The default jpg may not show much perf advantage, but I could grab a jpg
	// of my hd that should gains like in the write-up

	private static final String IMAGE_CONSUMER_COMPLETE_VIA_ERROR = "The ImageProducer failed with an error.";
	private static final String IMAGE_CONSUMER_COMPLETE_VIA_ABORTED = "The ImageProducer aborted.";
	private static final String IMAGE_PRODUCER_TIMED_OUT_POSTING_DATA = "The ImageProducer thread aborted because it timed out while attempting to post new information.";

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
		final ImageType imageType;

		PixelPackage(int x, int y, int w, int h, Object pixels, int offset, int scanSize, ImageType imageType) {
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
		 * Create a MutableBufferedImage of all pixels in this PixelPackage.
		 */
		MutableBufferedImage createBufferedImage(int y) {
			int arrayOffset = offset + y * scanSize;

			WritableRaster raster = null;
			if (imageType.getColorModel() instanceof DirectColorModel) {
				DirectColorModel dcm = (DirectColorModel) imageType.getColorModel();
				DataBuffer d = new DataBufferInt( (int[]) pixels, h * scanSize, arrayOffset);

				int[] bandmasks;
				if (dcm.hasAlpha()) {
					bandmasks = new int[4];
					bandmasks[3] = dcm.getAlphaMask();
				}
				else {
					bandmasks = new int[3];
				}
				bandmasks[0] = dcm.getRedMask();
				bandmasks[1] = dcm.getGreenMask();
				bandmasks[2] = dcm.getBlueMask();

				raster = Raster.createPackedRaster(d, w, h, scanSize, bandmasks, new Point(0,0));
			} else if (imageType.getColorModel() instanceof ComponentColorModel) {
				int[] bandOffsets = null;
				switch (imageType.getCode()) {
					case BufferedImage.TYPE_3BYTE_BGR:
						bandOffsets = new int[] {2, 1, 0};
						break;
					case BufferedImage.TYPE_4BYTE_ABGR_PRE:
					case BufferedImage.TYPE_4BYTE_ABGR:
						bandOffsets = new int[] {3, 2, 1, 0};
						break;
					case BufferedImage.TYPE_BYTE_GRAY:
						bandOffsets = new int[] {0};
						break;
				}

				if (bandOffsets != null) {
					DataBuffer d = new DataBufferByte((byte[]) pixels, h * scanSize, arrayOffset);
					raster = Raster.createInterleavedRaster(d, w, h, scanSize,
							imageType.getSampleCount(), bandOffsets, new Point(0, 0));
				}
			}

			if (raster == null)
				throw new NullPointerException("Unsupported image type: " + imageType);

			return new MutableBufferedImage(imageType.getColorModel(), raster, imageType.getColorModel().isAlphaPremultiplied(), new Hashtable<>());
		}
	}

	/**
	 * This is the ImageConsumer that listens for updates from the ImageProducer.
	 * This operates in its own thread.
	 */
	private static class Consumer implements ImageConsumer, Closeable {
		private final ImageProducer producer;
		private final boolean allowOptimization;

		Integer imgWidth;
		Integer imgHeight;

		boolean closed, isClosing;

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
		 * @param allowOptimization if false then this consumer MUST create a large int[] or byte[] buffer
		 *                          and populate it before passing any information back to the ImagePixelIterator.
		 *                          If true then this consumer MAY stream pixel data back one row at a time,
		 *                          if possible.
		 */
		Consumer(ImageProducer producer, ImageType pixelType, long queueTimeoueMillis, boolean allowOptimization) {
			this.producer = producer;
			this.pixelType = pixelType;
			this.queueTimeoueMillis = queueTimeoueMillis;
			this.allowOptimization = allowOptimization;
		}

		@Override
		public void close() {
			close("aborted");
		}

		private void close(Object closeStatus) {
			if (!closed) {
				isClosing = true;
				try {
					producer.removeConsumer(this);
					post(closeStatus);
				} finally {
					isClosing = false;
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
				if (waitTime < 0) {
					if (!isClosing) {
						close(IMAGE_PRODUCER_TIMED_OUT_POSTING_DATA);
						throw new ImageProducerTimedOutException(IMAGE_PRODUCER_TIMED_OUT_POSTING_DATA);
					}
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
			Object pixelPackageArray;
			if (modelType == pixelType.getCode()) {
				if (pixels instanceof int[] intArray) {
					pixelPackageArray = intArray.clone();
				} else if (pixels instanceof byte[] byteArray) {
					pixelPackageArray = byteArray.clone();
				} else {
					throw new IllegalStateException(pixels.getClass().getName());
				}
			} else {
				PixelIterator iter = new ArrayPixelIterator(pixels,
						w, h, offset, scanSize, ColorModelUtils.getBufferedImageType(model));
				iter = pixelType.createPixelIterator(iter);
				pixelPackageArray = readAll(iter);
				offset = 0;
				scanSize = iter.getWidth() * pixelType.getSampleCount();
			}

			post(new PixelPackage(x, y, w, h, pixelPackageArray, offset, scanSize, pixelType));
		}

		private Object readAll(PixelIterator iter) {
			ImageType iterType = ImageType.get(iter.getType());
			Object array;
			if (iterType.isByte()) {
				array = new byte[iter.getWidth() * iter.getPixelSize() * iter.getHeight()];
			} else {
				array = new int[iter.getWidth() * iter.getPixelSize() * iter.getHeight()];
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
					close(error);
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

				// TODO: can we not require isCompleteScanLineHint? or does this case come up in real-world testing?
				boolean optimized = allowOptimization && isTopDownLeftRightHint && isSinglePassHint && isCompleteScanLineHint;

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
						return;
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

			if (status == ImageConsumer.IMAGEERROR) {
				close(IMAGE_CONSUMER_COMPLETE_VIA_ERROR);
				return;
			} else if (status == ImageConsumer.IMAGEABORTED) {
				close(IMAGE_CONSUMER_COMPLETE_VIA_ABORTED);
				return;
			}

			if (isInitialized == false) {
				String error = "imageComplete( " + status
						+ " ) was called before setPixels(...)";
				close(error);
				return;
			}

			if (buffer != null) {
				post(new PixelPackage(0, 0, imgWidth, imgHeight, buffer, 0, imgWidth * pixelType.getSampleCount(), pixelType));
			}

			close(Boolean.TRUE);
		}
	}

	public static class Source<T> implements PixelIterator.Source<T> {
		private final Image image;
		private final ImageType iteratorType;

		private Dimension size;

		private String errorDescriptor;

		public Source(File file, ImageType imageType) {
			this( Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath()), imageType, file.getAbsolutePath());
		}

		public Source(URL url, ImageType imageType) {
			this( Toolkit.getDefaultToolkit().createImage(url), imageType, url.toString());
		}

		public Source(Image image, ImageType imageType) {
			this(image, imageType, null);
		}

		private Source(Image image, ImageType imageType, String errorDescriptor) {
			this.image = Objects.requireNonNull(image);
			this.errorDescriptor = errorDescriptor;

			if (!(imageType == null
					|| imageType.getCode() == BufferedImage.TYPE_INT_ARGB
					|| imageType.getCode() == BufferedImage.TYPE_INT_ARGB_PRE
					|| imageType.getCode() == BufferedImage.TYPE_INT_RGB
					|| imageType.getCode() == BufferedImage.TYPE_INT_BGR
					|| imageType.getCode() == BufferedImage.TYPE_3BYTE_BGR
					|| imageType.getCode() == BufferedImage.TYPE_BYTE_GRAY
					|| imageType.getCode() == BufferedImage.TYPE_4BYTE_ABGR
					|| imageType.getCode() == BufferedImage.TYPE_4BYTE_ABGR_PRE)) {
				throw new IllegalArgumentException(
						"illegal iterator type: " + imageType);
			}
			this.iteratorType = imageType;
		}

		@Override
		public MutableBufferedImage createBufferedImage() {
			// providing our own implementation saves us some memory allocation (and time):
			// just use the int[]/byte[] provided in a PixelPackage to create a DataBuffer
			// for a BufferedImage.
			try (ImagePixelIterator iter = createPixelIterator(false)) {
				iter.pollNextPixelPackage(TIMEOUT_MILLIS);
				return iter.unfinishedPixelPackage.createBufferedImage(iter.unfinishedPixelPackageY);
			}
		}

		@Override
		public ImagePixelIterator createPixelIterator() {
			return createPixelIterator(true);
		}

		private ImagePixelIterator createPixelIterator(boolean allowOptimization) {
			ImagePixelIterator returnValue = new ImagePixelIterator(image.getSource(), iteratorType, allowOptimization, errorDescriptor);
			if (size == null) {
				size = new Dimension(returnValue.getWidth(), returnValue.getHeight());
			}
			return returnValue;
		}

		@Override
		public int getWidth() {
			if (size == null) {
				size = ImageSize.get(image);
			}
			return size.width;
		}

		@Override
		public int getHeight() {
			if (size == null) {
				size = ImageSize.get(image);
			}
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

	public static BufferedImage createBufferedImage(File file) {
		return createBufferedImage(file, null);
	}

	public static MutableBufferedImage createBufferedImage(File file, ImageType<?> imageType) {
		Image image = Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath());
		return createBufferedImage(image, imageType, file.getAbsolutePath());
	}

	public static MutableBufferedImage createBufferedImage(Image image) {
		return createBufferedImage(image, null);
	}

	public static MutableBufferedImage createBufferedImage(Image image, ImageType<?> imageType) {
		return createBufferedImage(image, imageType, null);
	}

	public static MutableBufferedImage createBufferedImage(Image image, ImageType<?> imageType, String errorDescription) {
		if (image instanceof MutableBufferedImage) {
			return (MutableBufferedImage) image;
		} else if (image instanceof BufferedImage) {
			return new MutableBufferedImage( (BufferedImage) image);
		}
		return new Source(image, imageType, errorDescription).createBufferedImage();
	}

	public static MutableBufferedImage createBufferedImage(URL resource) {
		return createBufferedImage(resource, null);
	}

	public static MutableBufferedImage createBufferedImage(URL url, ImageType<?> imageType) {
		Image image = Toolkit.getDefaultToolkit().createImage(url);
		return createBufferedImage(image, imageType, url.toString());
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

	public ImagePixelIterator(URL url, ImageType<T> outputType) {
		this(Toolkit.getDefaultToolkit()
				.createImage(url), outputType, url.toString());
	}

	/**
	 * @param file
	 *            <code>Toolkit.createImage(filePath)</code> is used to create
	 *            the <code>java.awt.Image</code>, so the supported image types
	 *            are JPG, PNG and GIF.
	 * @param outputType
	 */
	public ImagePixelIterator(File file, ImageType<T> outputType) {
		this(Toolkit.getDefaultToolkit()
				.createImage(file.getAbsolutePath()), outputType, file.getAbsolutePath());
	}

	/**
	 * Create a new ImagePixelIterator based on an Image's {@link Image#getSource() ImageProducer}.
	 *
	 * @param image
	 * @param outputType the optional ImageType this pixel data should output as. If this is null
	 *                   then a default is chosen based on the incoming pixel data.
	 */
	public ImagePixelIterator(Image image, ImageType<T> outputType) {
		this(image == null ? null : image.getSource(), outputType, true, null);
	}

	/**
	 * @param errorDescriptor an optional String embedded in RuntimeExceptions if an error comes up
	 *                        setting this iterator up. For example: if this image is coming from a specific
	 *                        file then this should refer to the file path. This way an exception identifies
	 *                        exactly which file was being read.
	 */
	private ImagePixelIterator(Image image, ImageType<T> outputType, String errorDescriptor) {
		this(image == null ? null : image.getSource(), outputType, true, errorDescriptor);
	}

	/**
	 * Create a new ImagePixelIterator based on an ImageProducer.
	 *
	 * @param imageProducer the ImageProducer that will provide pixel data.
	 * @param outputType the optional ImageType this pixel data should output as. If this is null
	 *                   then a default is chosen based on the incoming pixel data.
	 */
	public ImagePixelIterator(ImageProducer imageProducer, ImageType<T> outputType) {
		this(imageProducer, outputType, true, null);
	}

	/**
	 * @param errorDescriptor an optional String embedded in RuntimeExceptions if an error comes up
	 *                        setting this iterator up. For example: if this image is coming from a specific
	 *                        file then this should refer to the file path. This way an exception identifies
	 *                        exactly which file was being read.
	 */
	private ImagePixelIterator(ImageProducer imageProducer, ImageType<T> requestedOutputType, boolean allowOptimization, String errorDescriptor) {
		Objects.requireNonNull(imageProducer, errorDescriptor == null ? "null ImageProducer" : "null ImageProducer for " + errorDescriptor);

		consumer = new Consumer(imageProducer, requestedOutputType, TIMEOUT_MILLIS, allowOptimization);

		// ImageProducer.startProduction often starts its own thread, but it's
		// not required to. This *cannot* be a blocking call, so we must wrap it
		// in a different thread to be safe.

		Semaphore startedProductionSemaphore = new Semaphore(1);
		startedProductionSemaphore.acquireUninterruptibly();
		Runnable productionRunnable = new Runnable() {
			public void run() {
				startedProductionSemaphore.release();

				// we don't know if startProduction(..) is going to launch
				// a new thread or not.
				imageProducer.startProduction(consumer);
			}
		};
		PRODUCTION_EXECUTOR.execute(productionRunnable);

		// don't start the clock on our timeout window until
		// we're sure our Runnable had a chance to start:
		startedProductionSemaphore.acquireUninterruptibly();

		Object data = poll(consumer.queue, 100_000);
		if (data instanceof ImageDescriptor) {
			ImageDescriptor d = (ImageDescriptor) data;
			width = d.imgWidth;
			height = d.imgHeight;
			isOptimized = d.isOptimized;
			type = d.imageType;
		} else if (data instanceof String) {
			throwException( (String) data);

			// this won't be reached; it's just to make the compiler happy:
			width = height = 0;
			isOptimized = false;
			type = null;
		} else if (data instanceof Boolean) {
			// when we receive Boolean.TRUE that should signal a healthy completion, so... what just happened?
			throw new RuntimeException();
		} else {
			// what happened here?
			throw new RuntimeException(String.valueOf(data));
		}
	}

	/**
	 * This throws a RuntimeException with a given message. It may throw a specialized subclass
	 * like ImageProducerException or ImageProducerAbortedException based on the message string.
	 */
	private void throwException(String msg) {
		if (IMAGE_CONSUMER_COMPLETE_VIA_ERROR.equals(msg)) {
			throw new ImageProducerException(IMAGE_CONSUMER_COMPLETE_VIA_ERROR);
		} else if (IMAGE_PRODUCER_TIMED_OUT_POSTING_DATA.equals(msg)) {
			throw new ImageProducerTimedOutException(IMAGE_PRODUCER_TIMED_OUT_POSTING_DATA);
		} else if (IMAGE_CONSUMER_COMPLETE_VIA_ABORTED.equals(msg)) {
			throw new ImageProducerAbortedException(IMAGE_CONSUMER_COMPLETE_VIA_ABORTED);
		}

		throw new RuntimeException((String) msg);
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
		if (rowCtr < height)
			return false;

		// one last check (without waiting) to see if there's an error condition after all pixel data is read:
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
	private void next(T destArray, int destArrayOffset, boolean skip) {
		if (unfinishedPixelPackage != null) {
			next_fromUnfinishedPixelPackage(destArray, destArrayOffset, skip);
			return;
		}
		pollNextPixelPackage(TIMEOUT_MILLIS);
		next_fromUnfinishedPixelPackage(destArray, destArrayOffset, skip);
	}

	/**
	 * Prepares the {@link #unfinishedPixelPackage} field with the next unread PixelPackage,
	 * or throws a RuntimeException if the producer thread has an error or times out.
	 */
	private void pollNextPixelPackage(long timeoutMillis) {
		Object data = poll(consumer.queue, timeoutMillis);
		if (data instanceof String) {
			String str = (String) data;
			rowCtr = height;
			throwException(str);
		} else if (data instanceof Boolean) {
			if (rowCtr == height && Boolean.TRUE.equals(data)) {
				// we're finished, and we don't expect anymore incoming data
				return;
			}
			rowCtr = height;
			if (Boolean.FALSE.equals(data)) {
				// this indicates our poll timed out; the producer may be dead/aborted/unreachable
				throw new ImageProducerUnresponsiveException("the image consumer is unresponsive; y = " + rowCtr);
			} else {
				// this indicates the producer thinks it passed all the required info
				throw new RuntimeException("unexpected end of image reached; y = " + rowCtr);
			}
		} else if (data instanceof PixelPackage) {
			unfinishedPixelPackage = (PixelPackage) data;
			unfinishedPixelPackageY = unfinishedPixelPackage.y;
		}
	}

	private void next_fromUnfinishedPixelPackage(T destArray, int destArrayOffset, boolean skip) {
		if (!skip) {
			System.arraycopy(unfinishedPixelPackage.pixels,
					unfinishedPixelPackage.offset + (unfinishedPixelPackageY - unfinishedPixelPackage.y) * unfinishedPixelPackage.scanSize + unfinishedPixelPackage.x * type.getSampleCount(),
					destArray, destArrayOffset, unfinishedPixelPackage.w * type.getSampleCount());
		}
		rowCtr++;
		unfinishedPixelPackageY++;
		if (unfinishedPixelPackageY == unfinishedPixelPackage.y + unfinishedPixelPackage.h)
			unfinishedPixelPackage = null;
	}

	@Override
	public void close() {
		consumer.close();
	}
}