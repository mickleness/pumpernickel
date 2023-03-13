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
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.*;

import com.pump.awt.Dimension2D;
import com.pump.image.ColorModelUtils;
import com.pump.image.ImageSize;
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
public class GenericImageSinglePassIterator<T>
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
			// TODO: revisit these, I think we may be getting valid hints
//			System.out.println();
//			System.out.println("SINGLEFRAME: " + (hintFlags & ImageConsumer.SINGLEFRAME));
//			System.out.println("SINGLEPASS: " + (hintFlags & ImageConsumer.SINGLEPASS));
//			System.out.println("IMAGEERROR: " + (hintFlags & ImageConsumer.IMAGEERROR));
//			System.out.println("COMPLETESCANLINES: " + (hintFlags & ImageConsumer.COMPLETESCANLINES));
//			System.out.println("STATICIMAGEDONE: " + (hintFlags & ImageConsumer.STATICIMAGEDONE));
//			System.out.println("IMAGEABORTED: " + (hintFlags & ImageConsumer.IMAGEABORTED));
//			System.out.println("TOPDOWNLEFTRIGHT: " + (hintFlags & ImageConsumer.TOPDOWNLEFTRIGHT));
//			System.out.println("RANDOMPIXELORDER: " + (hintFlags & ImageConsumer.RANDOMPIXELORDER));
//			System.out.println("SINGLEFRAMEDONE: " + (hintFlags & ImageConsumer.SINGLEFRAMEDONE));
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
				iterator = new GenericImageSinglePassIterator(w, h,
						iteratorType, topDown);
				break;

			case BufferedImage.TYPE_INT_ARGB:
			case BufferedImage.TYPE_INT_ARGB_PRE:
			case BufferedImage.TYPE_INT_BGR:
			case BufferedImage.TYPE_INT_RGB:
				iterator = new GenericImageSinglePassIterator(w, h,
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
		public GenericImageSinglePassIterator createPixelIterator() {
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
	public static GenericImageSinglePassIterator get(Image image,
			int iteratorType) {
		return new Source(image, iteratorType).createPixelIterator();
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

	PushPullQueue<PixelPackage> incoming = new MyPushPullQueue(this);
	PushPullQueue<PixelPackage> outgoing = new MyPushPullQueue(this);
	private PixelPackage scratchPackage = new PixelPackage();

	/**
	 * 
	 * @param destArray
	 *            will be either an int[] or byte[]
	 *
	 * @throws NonSinglePassException
	 *             if the source image did not deliver the image in a single
	 *             pass.
	 */
	public synchronized void next(T destArray, int destArrayOffset) {
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

					int bytesPerPixel = getPixelSize();
					if (destArray instanceof byte[]) {
						byte[] byteArray = (byte[]) destArray;
						Arrays.fill(byteArray, destArrayOffset, destArrayOffset + width * bytesPerPixel, (byte) 0);
					} else {
						int[] intArray = (int[]) destArray;
						Arrays.fill(intArray, destArrayOffset, destArrayOffset + width * bytesPerPixel, 0);
					}

					// guarantee that in the finally block below we'll continue to reuse this pixel package:
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
					PixelIterator srcIter;
					int inType = BufferedImageIterator.getImageType(pixelPackage.colorModel);

					if (pixelPackage.colorModel.getTransferType() == DataBuffer.TYPE_BYTE) {
						srcIter = new BufferedBytePixelIterator((byte[]) pixelPackage.pixels,
								pixelPackage.w, 1,
								pixelPackage.offset + (pixelPackage.y * pixelPackage.scanSize + pixelPackage.x) * getPixelSize(),
								pixelPackage.w * getPixelSize(), inType);
					} else {
						srcIter = new BufferedIntPixelIterator((int[]) pixelPackage.pixels,
								pixelPackage.w, 1,
								pixelPackage.offset + (pixelPackage.y * pixelPackage.scanSize + pixelPackage.x) * getPixelSize(),
								pixelPackage.w * getPixelSize(), inType);
					}
					PixelIterator srcIter2 = ImageType.get(getType()).createPixelIterator(srcIter);
					srcIter2.next(destArray, destArrayOffset);

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
}