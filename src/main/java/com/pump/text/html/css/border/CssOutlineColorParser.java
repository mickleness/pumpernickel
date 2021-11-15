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
package com.pump.text.html.css.border;

import com.pump.text.html.css.CssColorParser;

/**
 * The outline-color CSS property sets the color of an element's outline.
 */
public class CssOutlineColorParser extends CssColorParser {

	public static final String PROPERTY_OUTLINE_COLOR = "outline-color";

	public CssOutlineColorParser() {
		super(PROPERTY_OUTLINE_COLOR);
	}
}