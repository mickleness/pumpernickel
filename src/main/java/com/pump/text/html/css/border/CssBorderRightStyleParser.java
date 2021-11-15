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

import com.pump.text.html.css.CssPropertyParser;

public class CssBorderRightStyleParser
		implements CssPropertyParser<CssBorderStyleValue> {

	public static final String PROPERTY_BORDER_RIGHT_STYLE = "border-right-style";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_RIGHT_STYLE;
	}

	@Override
	public CssBorderStyleValue parse(String value) {
		return new CssBorderStyleValue(value);
	}

}