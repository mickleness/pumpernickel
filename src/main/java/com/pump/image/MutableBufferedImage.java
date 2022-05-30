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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * This is a <code>BufferedImage</code> that offers a <code>setProperty()</code>
 * method.
 */
public class MutableBufferedImage extends BufferedImage {

	/**
	 * Return a map of all known properties of a BufferedImage.
	 */
	public static Map<String, Object> getProperties(BufferedImage bi) {
		Map<String, Object> returnValue = new HashMap<>();
		if (bi != null) {
			synchronized (bi) {
				String[] propNames = bi.getPropertyNames();
				if (propNames != null) {
					for (String key : propNames) {
						returnValue.put(key, bi.getProperty(key));
					}
				}
			}
		}
		return returnValue;
	}

	private static Hashtable<String, Object> getPropertiesHashtable(
			BufferedImage bi) {
		Map<String, Object> map = getProperties(bi);
		Hashtable<String, Object> returnValue = new Hashtable<>(map.size());
		returnValue.putAll(map);
		return returnValue;
	}

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

	/**
	 * Create a MutableBufferedImage that stores data in the same raster as the
	 * argument. If you modify this image or the argument: the other will also
	 * be modified.
	 */
	public MutableBufferedImage(BufferedImage bi) {
		this(bi.getColorModel(), bi.getRaster(), bi.isAlphaPremultiplied(),
				getPropertiesHashtable(bi));
	}

	@Override
	public synchronized Object getProperty(String name,
			ImageObserver observer) {
		if (extraProperties != null && extraProperties.containsKey(name)) {
			return extraProperties.get(name);
		}
		return super.getProperty(name, observer);
	}

	@Override
	public synchronized Object getProperty(String name) {
		if (extraProperties != null && extraProperties.containsKey(name)) {
			return extraProperties.get(name);
		}
		return super.getProperty(name);
	}

	@Override
	public synchronized String[] getPropertyNames() {
		Collection<String> returnValue = new LinkedHashSet<String>();
		String[] superNames = super.getPropertyNames();
		if (superNames != null)
			returnValue.addAll(Arrays.asList(superNames));
		if (extraProperties != null) {
			returnValue.addAll(extraProperties.keySet());
		}
		return returnValue.toArray(new String[0]);
	}

	/**
	 * Assign a property value.
	 */
	public synchronized void setProperty(String propertyName, Object value) {
		if (extraProperties == null)
			extraProperties = new HashMap<String, Object>();
		extraProperties.put(propertyName, value);
	}

	/**
	 * Assign multiple property values.
	 */
	public synchronized void setProperties(Map<String, Object> properties) {
		if (extraProperties == null)
			extraProperties = new HashMap<String, Object>();
		extraProperties.putAll(properties);
	}
}