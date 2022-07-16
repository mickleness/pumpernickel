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
package com.pump.awt.converter;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.pump.data.Key;
import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for Colors.
 */
public class ColorMapConverter implements BeanMapConverter<Color> {

	private static final long serialVersionUID = 1L;

	/**
	 * This property defines the {@link Color#getRed}.
	 */
	public static final Key<Integer> PROPERTY_RED = new Key<>(Integer.class,
			"red");

	/**
	 * This property defines the {@link Color#getGreen}.
	 */
	public static final Key<Integer> PROPERTY_GREEN = new Key<>(Integer.class,
			"green");

	/**
	 * This property defines the {@link Color#getBlue}.
	 */
	public static final Key<Integer> PROPERTY_BLUE = new Key<>(Integer.class,
			"blue");

	/**
	 * This property defines the {@link Color#getAlpha}.
	 */
	public static final Key<Integer> PROPERTY_ALPHA = new Key<>(Integer.class,
			"alpha");

	@Override
	public Class<Color> getType() {
		return Color.class;
	}

	@Override
	public Map<String, Object> createAtoms(Color color) {
		Map<String, Object> atoms = new HashMap<>(4);
		PROPERTY_RED.put(atoms, color.getRed());
		PROPERTY_GREEN.put(atoms, color.getGreen());
		PROPERTY_BLUE.put(atoms, color.getBlue());
		PROPERTY_ALPHA.put(atoms, color.getAlpha());
		return atoms;
	}

	@Override
	public Color createFromAtoms(Map<String, Object> atoms) {
		int red = PROPERTY_RED.get(atoms);
		int green = PROPERTY_GREEN.get(atoms);
		int blue = PROPERTY_BLUE.get(atoms);
		int alpha = PROPERTY_ALPHA.get(atoms);
		return new Color(red, green, blue, alpha);
	}
}