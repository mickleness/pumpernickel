package com.pump.icon.button;

import java.awt.Color;
import java.util.Map;

/**
 * This translate ButtonState information into a map of colors.
 */
public interface ButtonIconColors {

	/**
	 * Return a map of colors based on the state provided.
	 */
	public Map<String, Color> getColors(ButtonState state);

}
