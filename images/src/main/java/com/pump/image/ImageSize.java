/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import com.pump.image.jpeg.JPEGMetaData;
import com.pump.image.bmp.BmpDecoder;

/**
 * A collection of static methods to fetch the dimensions of an image.
 */
public class ImageSize {
	private static class Observer implements ImageObserver {
		AtomicInteger w, h;
		boolean error = false;

		Observer(AtomicInteger width, AtomicInteger height) {
			w = width;
			h = height;
		}

		public boolean imageUpdate(Image img, int infoflags, int x, int y,
				int width, int height) {
			synchronized (this) {
				if ((infoflags & ImageObserver.ERROR) > 0) {
					error = true;
				}
				w.set(Math.max(w.get(), x + width));
				h.set(Math.max(h.get(), y + height));
				notify();
			}
			return w.get() <= 0 || h.get() <= 0;
		}

		public void load() {
			while (true) {
				synchronized (this) {
					if (error)
						throw new RuntimeException(
								"an error occurred while retrieving the width and height");
					if (w.get() > 0 && h.get() > 0)
						return;
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}
			}
		}

	}

	/**
	 * Retrieves the dimensions of this image using <code>ImageIO</code> classes
	 * or an <code>ImageObserver</code>.
	 */
	public static Dimension get(File file) {
		if (file == null)
			throw new NullPointerException();
		try {
			String filename = file.getName().toLowerCase();
			if (filename.endsWith(".bmp")) {
				// our BGRA-encoded BMPs are incompatible with ImageIO's BGRA BMPs and vice versa,
				// so we can read certain BMP file headers without an error:
				try {
					return BmpDecoder.getSize(file);
				} catch(Exception e) {
					// intentionally empty
				}
			} else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
				return JPEGMetaData.getSize(file);
			}

			return getSizeUsingImageIO(file);
		} catch (Exception e) {
			try {
				Image image = Toolkit.getDefaultToolkit().createImage(
						file.getAbsolutePath());
				try {
					return get(image);
				} finally {
					image.flush();
				}
			} catch (Exception e2) {
				IllegalArgumentException e3 = new IllegalArgumentException(
						"could not fetch dimensions of "
								+ file.getAbsolutePath(), e2);
				e2.initCause(e);
				throw e3;
			}
		}
	}

	/**
	 * Retrieves the dimensions of this image using <code>ImageIO</code> classes
	 * or an <code>ImageObserver</code>.
	 * 
	 * @throws IllegalArgumentException
	 *             if the dimensions could not be retrieved.
	 */
	public static Dimension get(URL url) throws IllegalArgumentException {
		if (url == null)
			throw new NullPointerException();
		try {
			return getSizeUsingImageIO(url);
		} catch (Exception e) {
			try {
				Image image = Toolkit.getDefaultToolkit().createImage(url);
				try {
					return get(image);
				} finally {
					image.flush();
				}
			} catch (Exception e2) {
				IllegalArgumentException e3 = new IllegalArgumentException(
						"could not fetch dimensions of " + url, e2);
				e2.initCause(e);
				throw e3;
			}
		}
	}

	/**
	 * Retrieves the dimensions of this image using an
	 * <code>ImageObserver</code>.
	 * 
	 */
	public static Dimension get(Image image) {
		AtomicInteger width = new AtomicInteger(-1);
		AtomicInteger height = new AtomicInteger(-1);

		Observer observer = new Observer(width, height);

		int w = image.getWidth(observer);
		if (w != -1)
			observer.imageUpdate(image, 0, 0, 0, w, 0);
		int h = image.getHeight(observer);
		if (h != -1)
			observer.imageUpdate(image, 0, 0, 0, 0, h);

		observer.load();

		return new Dimension(observer.w.get(), observer.h.get());
	}

	private static Dimension getSizeUsingImageIO(File file)
			throws IOException {
		ImageInputStream iis = null;
		ImageReader reader = null;
		try {
			iis = new FileImageInputStream(file);
			Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
			if (!it.hasNext())
				return null;

			reader = it.next();
			reader.setInput(iis, true, true);

			Dimension d = new Dimension(reader.getWidth(0), reader.getHeight(0));
			if (d.width <= 0 || d.height <= 0)
				throw new RuntimeException("invalid dimensions: " + d.width
						+ "x" + d.height);
			return d;
		} finally {
			try {
				if (reader != null)
					reader.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (iis != null)
					iis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static Dimension getSizeUsingImageIO(URL url) throws IOException {
		InputStream in = null;
		ImageInputStream iis = null;
		ImageReader reader = null;
		try {
			in = url.openStream();
			iis = new MemoryCacheImageInputStream(in);
			Iterator<ImageReader> it = ImageIO.getImageReaders(iis);
			if (!it.hasNext())
				return null;

			reader = it.next();
			reader.setInput(iis, true, true);

			Dimension d = new Dimension(reader.getWidth(0), reader.getHeight(0));
			if (d.width <= 0 || d.height <= 0)
				throw new RuntimeException("invalid dimensions: " + d.width
						+ "x" + d.height);
			return d;
		} finally {
			try {
				if (reader != null)
					reader.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (iis != null)
					iis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				if (in != null)
					in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}