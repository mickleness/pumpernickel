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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.image.pixel.BufferedIntPixelIterator;
import com.pump.image.pixel.IntPixelIterator;
import com.pump.image.pixel.converter.IntARGBConverter;
import com.pump.image.pixel.converter.IntRGBConverter;
import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.util.Warnings;

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
		try {
			return createImage(Toolkit.getDefaultToolkit().createImage(url),
					url.toString(), imageType);
		} catch (RuntimeException e) {
			System.err.println("url: " + url);
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
		return createImage(i, file.getAbsolutePath(), imageType);
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
		return l.getImage();
	}

	InnerImageConsumer consumer;
	Cancellable cancellable;
	List<ChangeListener> listeners;
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
	 *            when a change occurs. It can be added here in the constructor
	 *            because loading the image begins immediately; depending on how
	 *            <code>ImageProducer.startProduction</code> is implemented this
	 *            <i>may</i> be a blocking call so the
	 *            <code>ChangeListener</code> needs to be added before the
	 *            loading begins
	 * @param description
	 *            an optional description that may be useful for debugging
	 * @param imageType
	 *            a constant like BufferedImage.TYPE_INT_ARGB, or
	 *            {@link #TYPE_DEFAULT}
	 */
	public ImageLoader(ImageProducer p, Cancellable c,
			ChangeListener changeListener, String description, int imageType) {
		cancellable = c == null ? new BasicCancellable() : c;
		producer = p;
		this.destImageType = imageType;
		this.description = description;
		addChangeListener(changeListener);
		consumer = new InnerImageConsumer();
		p.startProduction(consumer);
	}

	/**
	 * Adds a ChangeListener to this loader. This listener will be notified
	 * either when the image is fully loaded/created, or when
	 * <code>getProgress()</code> changes value.
	 */
	public void addChangeListener(ChangeListener l) {
		if (l == null)
			return;

		if (listeners == null)
			listeners = new ArrayList<ChangeListener>();
		if (listeners.contains(l))
			return;
		listeners.add(l);
	}

	/**
	 * Removes a ChangeListener from this loader.
	 */
	public void removeChangeListener(ChangeListener l) {
		if (listeners == null)
			return;
		listeners.remove(l);
	}

	/**
	 * Returns a float from [0,1] <i>approximating</i> how much of the image is
	 * loaded. If your image were a text document, this is basically telling you
	 * where the text cursor was last placed. However the
	 * <code>ImageProducer</code> may make several different iterations over the
	 * image to deliver the complete image, so it is completely possible that
	 * this value will range from 0 to 1 a few different times.
	 * <P>
	 * I wish this could be more precise, but I don't see how more precision is
	 * possible given the way the <code>ImageProducer</code>/
	 * <code>ImageConsumer</code> model works.
	 * <P>
	 * For the most part, this will be a straight-forward 1-pass system. If you
	 * limit yourself to certain types of images (like PNGs) this is probably
	 * the case.
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

	/** Fires all change listeners */
	protected void fireChangeListeners() {
		if (listeners == null)
			return;
		for (int a = 0; a < listeners.size(); a++) {
			ChangeListener l = listeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(this));
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
		fireChangeListeners();
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
		}

		@Override
		public void setDimensions(int w, int h) {
			try {
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
					if (size.width == w && size.height == h)
						return;
					if (dest != null) {
						throw new RuntimeException("An image of "
								+ (size.getWidth()) + "x" + size.getHeight()
								+ " was already created.  Illegal attempt to call setDimensions("
								+ w + "," + h + ")");
					}
				}
				size = new Dimension(w, h);
				fireChangeListeners();
			} catch (RuntimeException e) {
				System.err.println("setDimensions( " + w + ", " + h + " )");
				System.err.println(description);
				throw e;
			} catch (Error e) {
				System.err.println("setDimensions( " + w + ", " + h + " )");
				System.err.println(description);
				throw e;
			}
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
		private int[] rowInt = null;
		private byte[] rowByte = null;

		@Override
		public void setPixels(int x, int y, int w, int h, ColorModel cm,
				byte[] data, int offset, int scanSize) {
			setPixels(x, y, w, h, cm, null, data, offset, scanSize);
		}

		@Override
		public void setPixels(int x, int y, int w, int h, ColorModel colorModel,
				int[] data, int offset, int scanSize) {
			setPixels(x, y, w, h, colorModel, data, null, offset, scanSize);
		}

		/**
		 * Either intData or byteData must be non-null
		 */
		private void setPixels(int x, int y, int w, int h,
				ColorModel colorModel, int[] intData, byte[] byteData,
				int offset, int scanSize) {
			try {
				if (intData == null && byteData == null)
					throw new NullPointerException();
				if (intData != null && byteData != null)
					throw new IllegalArgumentException();

				if (cancellable.isCancelled()) {
					if (debug)
						System.err.println("the Cancellable was activated");
					producer.removeConsumer(this);
					unblock(STATUS_CANCELLABLE_CANCELLED);
					return;
				}

				if (debug) {
					if (intData != null) {
						System.err.println("setPixels(" + x + " ," + y + " ,"
								+ w + " ," + h + ", " + colorModel + ", ..., "
								+ offset + ", " + scanSize + ") (int[])");
					} else {
						System.err.println("setPixels(" + x + " ," + y + " ,"
								+ w + " ," + h + ", " + colorModel + ", ..., "
								+ offset + ", " + scanSize + ") (byte[])");
					}
				}

				if (size == null)
					throw new RuntimeException(
							"The dimensions of this image are not yet defined.  Cannot write image data until the dimensions of the image are known.");

				int colorModelImageType = colorModel == lastCM ? lastImageType
						: ColorModelUtils.getBufferedImageType(colorModel);

				lastCM = colorModel;
				lastImageType = colorModelImageType;

				if (dest == null) {
					if (destImageType == ImageLoader.TYPE_DEFAULT) {
						// we need to decide our BufferedImage's type:
						if (colorModelImageType == ColorModelUtils.TYPE_UNRECOGNIZED) {
							// we may get error downstream (or we may not), but
							// this is an OK guess right now:
							dest = new MutableBufferedImage(size.width,
									size.height, BufferedImage.TYPE_INT_ARGB);
						} else {
							dest = new MutableBufferedImage(size.width,
									size.height, colorModelImageType);
						}
					} else {
						dest = new MutableBufferedImage(size.width, size.height,
								destImageType);
					}
				}

				if (intData != null) {
					writePixels(intData, colorModelImageType, x, y, w, h,
							offset, scanSize, colorModel);
				} else {
					// TODO: implement byte support
					// writePixels(byteData, colorModelImageType, x, y, w, h,
					// offset, scanSize, colorModel);

					// TODO: reimplement

					// if (cm == lastCM && indexed != null) {
					// int argb;
					// byte k = 0;
					// int k2 = 0;
					// for (int n = y; n < y + h; n++) {
					// for (int m = x; m < x + w; m++) {
					// k = data[(n - y) * scanSize + (m - x) + offset];
					// if (k >= 0) {
					// k2 = k;
					// } else {
					// k2 = k + 256;
					// }
					// argb = indexed[k2];
					// row[m - x] = argb;
					// }
					// dest.getRaster().setDataElements(x, n, w, 1, row);
					// }
					// } else {
					// int transIndex = (cm instanceof IndexColorModel)
					// ? ((IndexColorModel) cm).getTransparentPixel()
					// : -1;
					//
					// int argb;
					// for (int n = y; n < y + h; n++) {
					// for (int m = x; m < x + w; m++) {
					// byte k = data[(n - y) * scanSize + (m - x)
					// + offset];
					// int k2 = k & 0xff;
					// if (k2 == transIndex) {
					// argb = 0;
					// } else {
					// argb = cm.getRGB(k2);
					// }
					// row[m - x] = argb;
					// }
					// dest.getRaster().setDataElements(x, n, w, 1, row);
					// }
					// }
				}

				setProgress(x + w, y + h);
			} catch (RuntimeException e) {
				System.err.println("setPixels(" + x + " ," + y + " ," + w + " ,"
						+ h + ", " + colorModel + ", ..., " + offset + ", "
						+ scanSize + ")");
				System.err.println(description);
				throw e;
			} catch (Error e) {
				System.err.println("setPixels(" + x + " ," + y + " ," + w + " ,"
						+ h + ", " + colorModel + ", ..., " + offset + ", "
						+ scanSize + ")");
				System.err.println(description);
				throw e;
			}
		}

		private void writePixels(int[] data, int dataType, int x, int y, int w,
				int h, int offset, int scanSize, ColorModel colorModel) {

			boolean writePixelsDirectly = dest.getType() == dataType;

			// attempt #1: can we just write rows of data directly into our
			// image?

			if ((dest.getType() == BufferedImage.TYPE_INT_ARGB
					|| dest.getType() == BufferedImage.TYPE_INT_ARGB_PRE)
					&& dataType == BufferedImage.TYPE_INT_RGB) {
				// we have opaque RGB pixels coming in, so we can just add
				// an opaque alpha channel to every pixel:
				for (int q = y; q < y + h; q++) {
					int i = offset + (q - y) * scanSize;
					int i_end = i + w;
					for (; i < i_end; i++) {
						data[i] = 0xff000000 | (data[i] & 0xffffff);
					}
				}
				writePixelsDirectly = true;
			} else if (dest.getType() == BufferedImage.TYPE_INT_ARGB
					&& dataType == BufferedImage.TYPE_INT_ARGB_PRE) {
				// our incoming is ARGB_PRE, and we want to convert to ARGB
				for (int q = y; q < y + h; q++) {
					int i = offset + (q - y) * scanSize;
					int i_end = i + w;
					for (; i < i_end; i++) {
						int alpha1 = data[i] & 0xff000000;
						int alpha2 = (alpha1 >> 24);

						if (alpha2 != 0 && alpha2 != -1) {
							// convert to [0,255]
							alpha2 = alpha2 & 0xff;

							int r = (data[i] >> 8) & 0xff00;
							int g = data[i] & 0xff00;
							int b = (data[i] << 8) & 0xff00;

							// can we ever exceed 255 due to rounding error?
							// let's use Math.min() to avoid any risk
							r = Math.min(255, r / alpha2);
							g = Math.min(255, g / alpha2);
							b = Math.min(255, b / alpha2);

							data[i] = alpha1 | (r << 16) | (g << 8) | b;
						}
					}
				}
				writePixelsDirectly = true;
			} else if (dest.getType() == BufferedImage.TYPE_INT_ARGB_PRE
					&& dataType == BufferedImage.TYPE_INT_ARGB) {
				// our incoming is ARGB, and we want to convert to ARGB_PRE
				for (int q = y; q < y + h; q++) {
					int i = offset + (q - y) * scanSize;
					int i_end = i + w;
					for (; i < i_end; i++) {
						int alpha1 = data[i] & 0xff000000;
						int alpha2 = alpha1 >> 24;

						if (alpha2 == 0) {
							data[i] = 0;
						} else if (alpha2 == -1) {
							// intentionally empty, opaque pixel
						} else {
							// convert to [0,255]
							alpha2 = alpha2 & 0xff;

							int r = ((data[i] & 0xff0000) * alpha2) >> 8;
							int g = ((data[i] & 0xff00) * alpha2) >> 8;
							int b = ((data[i] & 0xff) * alpha2) >> 8;

							data[i] = alpha1 | r | g | b;
						}
					}
				}
				writePixelsDirectly = true;
			}

			if (writePixelsDirectly) {
				if (offset == 0 && scanSize == w) {
					dest.getRaster().setDataElements(x, y, w, h, data);
				} else {
					if (rowInt == null || rowInt.length < w)
						rowInt = new int[w];

					for (int n = y; n < y + h; n++) {
						int arrayOffset = (n - y) * scanSize - x + offset;
						System.arraycopy(data, arrayOffset, rowInt, 0, w);
						dest.getRaster().setDataElements(x, n, w, 1, rowInt);
					}
				}
				return;
			}

			// attempt #2: can we use a PixelConverter?

			IntPixelIterator intConverterIter = null;
			switch (dest.getType()) {
			case BufferedImage.TYPE_INT_ARGB:
				IntPixelIterator dataIter1 = new BufferedIntPixelIterator(data,
						w, h, offset, scanSize, dataType);
				intConverterIter = new IntARGBConverter(dataIter1);
				break;
			case BufferedImage.TYPE_INT_RGB:
				IntPixelIterator dataIter2 = new BufferedIntPixelIterator(data,
						w, h, offset, scanSize, dataType);
				intConverterIter = new IntRGBConverter(dataIter2);
				break;
			}

			if (intConverterIter != null) {
				if (rowInt == null || rowInt.length < intConverterIter
						.getMinimumArrayLength())
					rowInt = new int[intConverterIter.getMinimumArrayLength()];
				while (!intConverterIter.isDone()) {
					intConverterIter.next(rowInt);
					dest.getRaster().setDataElements(x, y, w, 1, rowInt);
					y++;
				}
				return;
			}

			// attempt #3 -- last resort: use the colorModel.getRGB

			for (int n = y; n < y + h; n++) {
				for (int m = x; m < x + w; m++) {
					// TODO: this will fail for bytes
					int argb = colorModel.getRGB(
							data[(n - y) * scanSize + (m - x) + offset]);
					dest.setRGB(x, y, argb);
				}
			}

			// if we see this message: we should add a condition above to
			// address whatever is happening
			Warnings.println(
					"ImageLoader#writePixels(..) is writing pixel data inefficiently.",
					10000);
		}

		@Override
		public void setProperties(Hashtable<?, ?> p) {
			try {
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

			} catch (RuntimeException e) {
				System.err.println(description);
				throw e;
			} catch (Error e) {
				System.err.println(description);
				throw e;
			}
		}
	}

	private void setProgress(int x, int y) {
		progress = ((float) (y * size.width + x))
				/ ((float) (size.width * size.height));
		fireChangeListeners();
	}
}