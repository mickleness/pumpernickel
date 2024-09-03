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
 * This parses the "width" property into a CssDimensionValue.
 */
public class CssWidthParser implements CssPropertyParser<CssDimensionValue> {

	public static final String PROPERTY_WIDTH = "width";

	@Override
	public String getPropertyName() {
		return PROPERTY_WIDTH;
	}

	@Override
	public CssDimensionValue parse(String cssString) {
		return new CssDimensionValue(cssString);
	}

}