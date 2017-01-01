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

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/** This is a <code>BufferedImage</code> that offers a <code>setProperty()</code> method.
 *
 */
public class MutableBufferedImage extends BufferedImage {
	
	Map<String, Object> extraProperties = null;

	public MutableBufferedImage(ColorModel cm, WritableRaster r,
			boolean premultiplied, Hashtable<String, Object> properties) {
		super(cm, r, premultiplied, properties);
	}

	public MutableBufferedImage(int width, int height, int imageType,
			IndexColorModel cm) {
		super(width, height, imageType, cm);
	}

	public MutableBufferedImage(int width, int height, int imageType) {
		super(width, height, imageType);
	}

	@Override
	public synchronized Object getProperty(String name, ImageObserver observer) {
		if(extraProperties!=null) {
			Object value = extraProperties.get(name);
			if(value!=null)
				return value;
		}
		return super.getProperty(name, observer);
	}

	@Override
	public synchronized Object getProperty(String name) {
		if(extraProperties!=null) {
			Object value = extraProperties.get(name);
			if(value!=null)
				return value;
		}
		return super.getProperty(name);
	}

	@Override
	public synchronized String[] getPropertyNames() {
		ArrayList<String> list = new ArrayList<String>();
		String[] superNames = super.getPropertyNames();
		for(int a = 0; a<superNames.length; a++) {
			list.add(superNames[a]);
		}
		if(extraProperties!=null) {
			Iterator<String> e = extraProperties.keySet().iterator();
			while(e.hasNext()) {
				String key = e.next();
				list.add(key);
			}
		}
		return list.toArray(new String[list.size()]);
	}

	public synchronized void setProperty(String propertyName,Object value) {
		if(extraProperties==null)
			extraProperties = new HashMap<String, Object>();
		extraProperties.put(propertyName, value);
	}
}