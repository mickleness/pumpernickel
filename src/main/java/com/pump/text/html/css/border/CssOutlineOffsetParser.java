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

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssPropertyParser;

/**
 * The outline-offset CSS property sets the amount of space between an outline
 * and the edge or border of an element.
 */
public class CssOutlineOffsetParser implements CssPropertyParser<CssLength> {

	public static final String PROPERTY_OUTLINE_OFFSET = "outline-offset";

	@Override
	public String getPropertyName() {
		return PROPERTY_OUTLINE_OFFSET;
	}

	@Override
	public CssLength parse(String value) {
		return new CssLength(value);
	}
}