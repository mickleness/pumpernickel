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

import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.pump.data.converter.BeanMapConverter;

/**
 * This is a BeanMapConverter for RenderingHints.
 * <p>
 * The map used here is a map of rendering hints and their values represented as
 * Strings.
 * <p>
 * This only addresses Java's default RenderingHints. If you add custom
 * keys/values then you need to modify {@link #ALL_RENDERING_HINT_KEYS} and
 * {@link #ALL_RENDERING_HINT_VALUES}.
 */
public class RenderingHintsMapConverter
		implements BeanMapConverter<RenderingHints> {

	private static final long serialVersionUID = 1L;

	/**
	 * This is a collection of all known rendering hints. Each hint should have
	 * a unique toString() output. This is mutable so you can add custom keys to
	 * it if needed.
	 */
	public static final Collection<RenderingHints.Key> ALL_RENDERING_HINT_KEYS = new HashSet<>(
			Arrays.asList(RenderingHints.KEY_ALPHA_INTERPOLATION,
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.KEY_COLOR_RENDERING,
					RenderingHints.KEY_DITHERING,
					RenderingHints.KEY_FRACTIONALMETRICS,
					RenderingHints.KEY_INTERPOLATION,
					RenderingHints.KEY_RENDERING,
					RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.KEY_TEXT_LCD_CONTRAST));

	/**
	 * This is a collection of all known rendering hint values. Each value
	 * should have a unique toString() output. This is mutable so you can add
	 * custom values to it if needed.
	 */
	public static final Collection<Object> ALL_RENDERING_HINT_VALUES = new HashSet<>(
			Arrays.asList(RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,
					RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED,
					RenderingHints.VALUE_ANTIALIAS_DEFAULT,
					RenderingHints.VALUE_ANTIALIAS_OFF,
					RenderingHints.VALUE_ANTIALIAS_ON,
					RenderingHints.VALUE_COLOR_RENDER_DEFAULT,
					RenderingHints.VALUE_COLOR_RENDER_QUALITY,
					RenderingHints.VALUE_COLOR_RENDER_SPEED,
					RenderingHints.VALUE_DITHER_DEFAULT,
					RenderingHints.VALUE_DITHER_DISABLE,
					RenderingHints.VALUE_DITHER_ENABLE,
					RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT,
					RenderingHints.VALUE_FRACTIONALMETRICS_OFF,
					RenderingHints.VALUE_FRACTIONALMETRICS_ON,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
					RenderingHints.VALUE_RENDER_DEFAULT,
					RenderingHints.VALUE_RENDER_QUALITY,
					RenderingHints.VALUE_RENDER_SPEED,
					RenderingHints.VALUE_STROKE_DEFAULT,
					RenderingHints.VALUE_STROKE_NORMALIZE,
					RenderingHints.VALUE_STROKE_PURE,
					RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
					RenderingHints.VALUE_TEXT_ANTIALIAS_GASP,
					RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR,
					RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB,
					RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR,
					RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB,
					RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON));

	public static Object getValue(String valueName) {
		for (Object value : ALL_RENDERING_HINT_VALUES) {
			if (value.toString().equals(valueName))
				return value;
		}

		// If you reach this exception: you may need to adjust
		// ALL_RENDERING_HINT_VALUES.
		// It's public & static so you can easily add new keys.
		// Just make sure you new value has a unique toString() method.
		throw new IllegalArgumentException(
				"Unsupported value name: " + valueName);
	}

	public static Key getKey(String keyName) {
		for (RenderingHints.Key key : ALL_RENDERING_HINT_KEYS) {
			if (key.toString().equals(keyName))
				return key;
		}

		// If you reach this exception: you may need to adjust
		// ALL_RENDERING_HINT_KEYS.
		// It's public & static so you can easily add new keys.
		// Just make sure your new key has a unique toString() method.
		throw new IllegalArgumentException("Unsupported key name: " + keyName);
	}

	@Override
	public Class<RenderingHints> getType() {
		return RenderingHints.class;
	}

	@Override
	public Map<String, Object> createAtoms(RenderingHints hints) {
		Map<String, Object> atoms = new HashMap<>(hints.size());

		for (Entry<Object, Object> entry : hints.entrySet()) {
			atoms.put(entry.getKey().toString(), entry.getValue().toString());
		}
		return atoms;
	}

	@Override
	public RenderingHints createFromAtoms(Map<String, Object> atoms) {
		RenderingHints returnValue = new RenderingHints(
				new HashMap<RenderingHints.Key, Object>());
		for (Entry<String, Object> atomEntry : atoms.entrySet()) {
			Key key = getKey((String) atomEntry.getKey());
			Object value = getValue((String) atomEntry.getValue());
			returnValue.put(key, value);
		}
		return returnValue;
	}
}