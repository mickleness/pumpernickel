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

public class CssBorderTopColorParser extends CssColorParser {

	public static final String PROPERTY_BORDER_TOP_COLOR = "border-top-color";

	public CssBorderTopColorParser() {
		super(PROPERTY_BORDER_TOP_COLOR);
	}
}