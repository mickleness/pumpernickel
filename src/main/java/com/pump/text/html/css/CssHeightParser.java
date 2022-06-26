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
package com.pump.text.html.css;

/**
 * This parses the "height" property into a CssDimensionValue.
 */
public class CssHeightParser implements CssPropertyParser<CssDimensionValue> {

	public static final String PROPERTY_HEIGHT = "height";

	@Override
	public String getPropertyName() {
		return PROPERTY_HEIGHT;
	}

	@Override
	public CssDimensionValue parse(String cssString) {
		return new CssDimensionValue(cssString);
	}

}