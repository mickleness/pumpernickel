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
package com.pump.text.html.css.border;

import java.util.List;

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssPropertyParser;

public class CssBorderLeftWidthParser implements CssPropertyParser<CssLength> {

	public static final String PROPERTY_BORDER_LEFT_WIDTH = "border-left-width";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_LEFT_WIDTH;
	}

	@Override
	public CssLength parse(String cssString) {
		List<CssLength> list = new CssBorderWidthParser().parse(cssString);
		if (list.size() != 1)
			throw new IllegalArgumentException(
					"unable to parse \"" + cssString + "\"");
		return list.get(0);
	}
}