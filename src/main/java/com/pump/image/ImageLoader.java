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
package com.pump.image;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.image.pixel.*;
import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;

/**
 * This class can convert an abstract <code>Image</code> into a
 * <code>MutableBufferedImage</code> with a specified image type.
 * <P>
 * It was written to replace the <code>MediaTracker</code>; on my Macs this
 * class is faster and more efficient. It has come to my attention that on Linux
 * this may not be the case (see my <a href=
 * "https://javagraphics.blogspot.com/2007/04/images-studying-mediatracker.html"
 * >blog</a> for more discussion.)
 * <P>
 * Also this class has the added advantage of always returning an image in a
 * certain format. Using other methods (such as ImageIO) may return an arbitrary
 * image type. (And this class doesn't require a <code>java.awt.Component</code>
 * to initialize; why does the MediaTracker do that? It's just a strange
 * animal.)
 * <p>
 * This class only loads the first image of an ImageProducer. So if there are
 * multiple frames: all frames after the first are ignored.
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2007/04/images-studying-mediatracker.html">Images:
 *      Studying MediaTracker</a>
 */
public class ImageLoader {
	private static boolean debug = false;

	/**
	 * This is an image type alternative that indicates we should return
	 * whatever is simplest/most expedient.
	 */
	public static int TYPE_DEFAULT = -888321;

	/**
	 * This code indicates our attempt to load the image was aborted because our
	 * Cancellable was activated.
	 */
	private static int STATUS_CANCELLABLE_CANCELLED = -1;

	/**
	 * Create an ARGB BufferedImage from a URL.
	 */
	public static MutableBufferedImage createImage(URL url) {
		return createImage(url, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * @param imageType
	 *            a constant like BufferedImage.TYPE_INT_ARGB, or
	 *            {@link #TYPE_DEFAULT}
	 */
	public static MutableBufferedImage createImage(URL url, int imageType) {
		if (url == null)
			throw new NullPointerException();
		Image img = null;
		try {
			img = Toolkit.getDefaultToolkit().createImage(url);
			return createImage(img, url.toString(), imageType);
		} catch (RuntimeException e) {
			System.err.println("url: " + url);
			if (img != null)
				img.flush();
			throw e;
		}
	}

	/**
	 * Create an ARGB BufferedImage from a File.
	 */
	public static MutableBufferedImage createImage(File file) {
		return createImage(file, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * @param imageType
	 *            a constant like BufferedImage.TYPE_INT_ARGB, or
	 *            {@link #TYPE_DEFAULT}
	 */
	public static MutableBufferedImage createImage(File file, int imageType) {
		Image i = Toolkit.getDefaultToolkit()
				.createImage(file.getAbsolutePath());
		try {
			return createImage(i, file.getAbsolutePath(), imageType);
		} finally {
			i.flush();
		}
	}

	/**
	 * Create an ARGB BufferedImage of an image.
	 */
	public static MutableBufferedImage createImage(Image i) {
		return createImage(i, BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * This returns a BufferedImage depicting the argument <code>i</code>.
	 * <P>
	 * Note that if <code>i</code> is already a BufferedImage, then it may
	 * immediately be returned and this method does NOT duplicate it.
	 * 
	 * @param i
	 * @param imageType
	 *            a constant like BufferedImage.TYPE_INT_ARGB, or
	 *            {@link #TYPE_DEFAULT}
	 * @return an ARGB BufferedImage identical to the argument.
	 */
	public static MutableBufferedImage createImage(Image i, int imageType) {
		if (i instanceof BufferedImage) {
			BufferedImage bi = (BufferedImage) i;
			int currentType = bi.getType();
			if (imageType == TYPE_DEFAULT)
				imageType = currentType;

			if (imageType == currentType) {
				if (bi instanceof MutableBufferedImage)
					return (MutableBufferedImage) bi;
				return new MutableBufferedImage(bi);
			}

			MutableBufferedImage newImage = new MutableBufferedImage(
					bi.getWidth(), bi.getHeight(), imageType);
			Graphics2D g = newImage.createGraphics();
			g.drawImage(bi, 0, 0, null);
			g.dispose();
			newImage.setProperties(MutableBufferedImage.getProperties(bi));
			return newImage;
		}
		return createImage(i, null, imageType);
	}

	protected static MutableBufferedImage createImage(Image i,
			String description, int imageType) {
		ImageLoader l = new ImageLoader(i.getSource(), null, null, description,
				imageType);
		// TODO: make ImageLoader (and other pixel classes) use MutableBufferedImage
		MutableBufferedImage bi = l.getImage();
		bi.setAccelerationPriority(i.getAccelerationPriority());
		return bi;
	}

	InnerImageConsumer consumer;
	Cancellable cancellable;
	ChangeListener changeListener;
	ImageProducer producer;
	float progress = 0;
	int destImageType;
	String description;

	int status = 0;
	Dimension size;

	/**
	 * A set of properties that we'll store in dest as soon as we initialize
	 * dest.
	 */
	Map<String, Object> pendingProperties;

	MutableBufferedImage dest;
	ImageType destType;

	/**
	 * This constructs an ImageLoader. As soon as an ImageLoader is constructed
	 * the <code>ImageProducer</code> is asked to start producing data. (However
	 * constructing this object will not block waiting on the image data.)
	 * 
	 * @param p
	 *            the source of this image
	 * @param c
	 *            an optional <code>Cancellable</code> object.
	 * @param changeListener
	 *            an optional <code>ChangeListener</code>. This will be notified
	 *            when a change occurs.
	 * @param description
	 *            an optional description that may be useful for debugging
	 * @param imageType
	 *            a constant like BufferedImage.TYPE_INT_ARGB, or
	 *            {@link #TYPE_DEFAULT}
	 */
	private ImageLoader(ImageProducer p, Cancellable c,
			ChangeListener changeListener, String description, int imageType) {
		cancellable = c == null ? new BasicCancellable() : c;
		producer = p;
		this.destImageType = imageType;
		this.description = description;
		this.changeListener = changeListener;
		consumer = new InnerImageConsumer();
		p.startProduction(consumer);
	}

	/**
	 * Returns a float from [0,1] <i>approximating</i> how much of the image is
	 * loaded. If your image were a text document, this is basically telling you
	 * where the text cursor was last placed. However the
	 * <code>ImageProducer</code> may make several different iterations over the
	 * image to deliver the complete image, so it is possible that this value
	 * will range from 0 to 1 a few different times.
	 * <P>
	 * (I wish this could be more precise, but I don't see how more precision is
	 * possible given the way the <code>ImageProducer</code>/
	 * <code>ImageConsumer</code> model works.)
	 * <P>
	 * Often this will be a straight-forward 1-pass system, but you may observe
	 * some images require multiple passes.
	 */
	public float getProgress() {
		return progress;
	}

	/**
	 * This indicates whether this <code>ImageLoader</code> is finished.
	 * <P>
	 * Unlike <code>getProgress()</code>, this is guaranteed to be 100%
	 * accurate.
	 */
	public boolean isDone() {
		return status != 0;
	}

	/**
	 * Returns the dimension of this image, or null if the dimensions are not
	 * yet known.
	 */
	public Dimension getSize() {
		if (size == null)
			return null;
		return new Dimension(size.width, size.height);
	}

	private void fireChangeListener() {
		if (changeListener != null) {
			try {
				changeListener.stateChanged(new ChangeEvent(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * This blocks until the image has finished loading, an error occurs, or the
	 * operation has been cancelled.
	 * 
	 * @return the image represented in the original ImageProducer, or
	 *         <code>null</code> if the operation was cancelled.
	 * @throws RuntimeException
	 *             if an error occurred while loading this image
	 */
	public MutableBufferedImage getImage() {
		int status = block();

		if (status == STATUS_CANCELLABLE_CANCELLED) {
			producer.removeConsumer(consumer);
			return null;
		} else if (status == ImageConsumer.IMAGEERROR) {
			throw new RuntimeException("An error occurred.");
		} else if (status == ImageConsumer.IMAGEABORTED) {
			throw new RuntimeException("The operation was aborted.");
		}

		return dest;
	}

	/**
	 * Blocks this thread until the image is finished loading (or cancelled, or
	 * aborted)
	 */
	private int block() {
		while (true) {
			synchronized (this) {
				if (status != 0)
					return status;
				try {
					wait();
				} catch (InterruptedException e) {
					// intentionally empty
				}
			}
		}
	}

	private void unblock(int newStatus) {
		synchronized (this) {
			if (status != 0)
				return;
			this.status = newStatus;

			producer.removeConsumer(consumer);
			this.notifyAll();
		}
		fireChangeListener();
	}

	class InnerImageConsumer implements ImageConsumer {

		public InnerImageConsumer() {
		}

		@Override
		public void imageComplete(int completionStatus) {
			if (debug) {
				System.err.println("imageComplete(): ");
				if ((completionStatus == IMAGEABORTED))
					System.err.println("\tIMAGEABORTED");
				if ((completionStatus == IMAGEERROR))
					System.err.println("\tIMAGEERROR");
				if ((completionStatus == SINGLEFRAMEDONE))
					System.err.println("\tSINGLEFRAMEDONE");
				if ((completionStatus == STATICIMAGEDONE))
					System.err.println("\tSTATICIMAGEDONE");
			}

			unblock(completionStatus);
		}

		@Override
		public void setColorModel(ColorModel cm) {
			if (debug)
				System.err.println("setColorModel( " + cm + " )");
			// the doc says this isn't really binding, so let's ignore it
		}

		@Override
		public synchronized void setDimensions(int w, int h) {
			if (debug)
				System.err.println("setDimensions(" + w + "," + h + ")");
			if (w <= 0)
				throw new IllegalArgumentException(
						"Width must be greater than zero.  (" + w + ")");
			if (h <= 0)
				throw new IllegalArgumentException(
						"Height must be greater than zero.  (" + h + ")");
			if (size != null) {
				// eh? already exists?

				if (size.width == w && size.height == h) {
					// weird, but harmless
					return;
				}

				if (dest != null) {
					throw new RuntimeException("An image of "
							+ (size.getWidth()) + "x" + size.getHeight()
							+ " was already created.  Illegal attempt to call setDimensions("
							+ w + "," + h + ")");
				}
			}
			size = new Dimension(w, h);
			fireChangeListener();
		}

		@Override
		public void setHints(int hints) {
			if (debug) {
				System.err.println("setHints():");
				if ((hints & COMPLETESCANLINES) > 0)
					System.err.println("\tCOMPLETESCANLINES");
				if ((hints & RANDOMPIXELORDER) > 0)
					System.err.println("\tSINGLEFRAME");
				if ((hints & SINGLEFRAME) > 0)
					System.err.println("\tSINGLEFRAME");
				if ((hints & SINGLEPASS) > 0)
					System.err.println("\tSINGLEPASS");
				if ((hints & TOPDOWNLEFTRIGHT) > 0)
					System.err.println("\tTOPDOWNLEFTRIGHT");
			}
		}

		private int lastImageType;
		private ColorModel lastCM = null;

		@Override
		public synchronized void setPixels(int x, int y, int w, int h,
				ColorModel colorModel, byte[] data, int offset, int scanSize) {
			if (debug) {
				System.err.println("setPixels(" + x + " ," + y + " ," + w + " ,"
						+ h + ", " + colorModel + ", ..., " + offset + ", "
						+ scanSize + ") (byte[])");
			}

			int inDataType = prepareSetPixels(colorModel);
			PixelIterator<?> srcIter = new BufferedBytePixelIterator(data, w, h,
					offset, scanSize, inDataType);
			setPixels(srcIter, x, y, w, h, colorModel);
		}

		@Override
		public synchronized void setPixels(int x, int y, int w, int h,
				ColorModel colorModel, int[] data, int offset, int scanSize) {
			if (debug) {
				System.err.println("setPixels(" + x + " ," + y + " ," + w + " ,"
						+ h + ", " + colorModel + ", ..., " + offset + ", "
						+ scanSize + ") (int[])");
			}

			int inDataType = prepareSetPixels(colorModel);
			PixelIterator<?> srcIter = new BufferedIntPixelIterator(data, w, h,
					offset, scanSize, inDataType);
			setPixels(srcIter, x, y, w, h, colorModel);
		}

		private int prepareSetPixels(ColorModel colorModel) {
			int colorModelImageType = colorModel == lastCM ? lastImageType
					: ColorModelUtils.getBufferedImageType(colorModel);

			lastCM = colorModel;
			lastImageType = colorModelImageType;

			if (dest == null) {
				if (destImageType == ImageLoader.TYPE_DEFAULT) {
					// we need to decide our BufferedImage's type:
					if (colorModelImageType == ColorModelUtils.TYPE_UNRECOGNIZED) {
						// we may get error downstream (or we may not),
						// but this is an OK guess right now:
						dest = new MutableBufferedImage(size.width, size.height,
								BufferedImage.TYPE_INT_ARGB);
					} else {
						dest = new MutableBufferedImage(size.width, size.height,
								colorModelImageType);
					}
				} else {
					dest = new MutableBufferedImage(size.width, size.height,
							destImageType);
				}
				destType = ImageType.get(dest.getType());

				if (pendingProperties != null) {
					dest.setProperties(pendingProperties);
					pendingProperties = null;
				}
			}
			return colorModelImageType;
		}

		private void setPixels(PixelIterator<?> srcIter, int x, int y, int w,
				int h, ColorModel colorModel) {

			if (cancellable.isCancelled()) {
				if (debug)
					System.err.println("the Cancellable was activated");
				unblock(STATUS_CANCELLABLE_CANCELLED);
				return;
			}

			if (size == null)
				throw new RuntimeException(
						"The dimensions of this image are not yet defined.  Cannot write image data until the dimensions of the image are known.");

			PixelIterator<?> dstIter = destType.createPixelIterator(srcIter);
			BufferedImageIterator.writeToImage(dstIter, dest, x, y);
			setProgress(x + w, y + h);
		}

		@Override
		public synchronized void setProperties(Hashtable<?, ?> p) {
			if (debug) {
				System.err.println("setProperties(): " + p);
			}

			if (dest != null) {
				for (Map.Entry<?, ?> entry : p.entrySet()) {
					String key = String.valueOf(entry.getKey());
					dest.setProperty(key, entry.getValue());
				}
			} else {
				if (pendingProperties == null)
					pendingProperties = new HashMap<>();

				for (Map.Entry<?, ?> entry : p.entrySet()) {
					String key = String.valueOf(entry.getKey());
					pendingProperties.put(key, entry.getValue());
				}
			}
		}
	}

	private void setProgress(int x, int y) {
		progress = ((float) (y * size.width + x))
				/ ((float) (size.width * size.height));
		fireChangeListener();
	}
}