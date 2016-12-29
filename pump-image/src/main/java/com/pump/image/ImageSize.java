/*
 * @(#)ImageSize.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
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

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import com.pump.math.MutableInteger;

/** A collection of static methods to fetch the dimensions of an image.
 */
public class ImageSize {
	private static class Observer implements ImageObserver {
		MutableInteger w, h;
		boolean error = false;
		Observer(MutableInteger width,MutableInteger height) {
			w = width;
			h = height;
		}
		public boolean imageUpdate(Image img, int infoflags, int x, int y,
				int width, int height) {
			synchronized(this) {
				if( (infoflags & ImageObserver.ERROR)>0) {
					error = true;
				}
				w.value = Math.max(w.value, x+width);
				h.value = Math.max(h.value, y+height);
				notify();
			}
			return false;
		}
		
		public void load() {
			while(true) {
				synchronized(this) {
					if(error)
						throw new RuntimeException("an error occurred while retrieving the width and height");
					if(w.value>0 && h.value>0)
						return;
					try {
						wait();
					} catch(InterruptedException e) {}
				}
			}
		}
		
	}

	/** Retrieves the dimensions of this image using
	 * <code>ImageIO</code> classes or an <code>ImageObserver</code>.
	 */
	public static Dimension get(File file) {
		if(file==null) throw new NullPointerException();
		try {
			Dimension size = getSizeUsingImageIO(file);
			return size;
		} catch(Exception e) {
			try {
				Image image = Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath());
				try {
					return get(image);
				} finally {
					image.flush();
				}
			} catch(Exception e2) {
				IllegalArgumentException e3 = new IllegalArgumentException("could not fetch dimensions of "+file.getAbsolutePath());
				e3.initCause(e2);
				e2.initCause(e);
				throw e3;
			}
		}
	}

	/** Retrieves the dimensions of this image using
	 * <code>ImageIO</code> classes or an <code>ImageObserver</code>.
	 * 
	 * @throws IllegalArgumentException if the dimensions
	 * could not be retrieved.
	 */
	public static Dimension get(URL url) throws IllegalArgumentException {
		if(url==null) throw new NullPointerException();
		try {
			Dimension size = getSizeUsingImageIO(url);
			return size;
		} catch(Exception e) {
			try {
				Image image = Toolkit.getDefaultToolkit().createImage(url);
				try {
					return get(image);
				} finally {
					image.flush();
				}
			} catch(Exception e2) {
				IllegalArgumentException e3 = new IllegalArgumentException("could not fetch dimensions of "+url);
				e3.initCause(e2);
				e2.initCause(e);
				throw e3;
			}
		}
	}
	
	/** Retrieves the dimensions of this image using an <code>ImageObserver</code>.
	 * 
	 */
	public static Dimension get(Image image) {
		MutableInteger width = new MutableInteger(-1);
		MutableInteger height = new MutableInteger(-1);
		
		Observer observer = new Observer(width, height);
		
		int w = image.getWidth(observer);
		if(w!=-1) observer.imageUpdate(image, 0, 0, 0, w, 0);
		int h = image.getHeight(observer);
		if(h!=-1) observer.imageUpdate(image, 0, 0, 0, 0, h);
		
		observer.load();
		
		return new Dimension(observer.w.value, observer.h.value);
	}
	
	private static Dimension getSizeUsingImageIO(File file) throws FileNotFoundException, IOException {
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
			if(d.width<=0 || d.height<=0)
				throw new RuntimeException("invalid dimensions: "+d.width+"x"+d.height);
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
			if(d.width<=0 || d.height<=0)
				throw new RuntimeException("invalid dimensions: "+d.width+"x"+d.height);
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
