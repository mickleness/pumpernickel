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