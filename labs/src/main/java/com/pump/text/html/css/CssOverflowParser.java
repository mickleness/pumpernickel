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

public class CssOverflowParser implements CssPropertyParser<CssOverflowValue> {

	public static final String PROPERTY_OVERFLOW = "overflow";

	@Override
	public String getPropertyName() {
		return PROPERTY_OVERFLOW;
	}

	@Override
	public CssOverflowValue parse(String cssString) {
		for (CssOverflowValue.Mode mode : CssOverflowValue.Mode.values()) {
			if (mode.name().equalsIgnoreCase(cssString))
				return new CssOverflowValue(cssString, mode);
		}
		throw new IllegalArgumentException(
				"Unsupported overflow \"" + cssString + "\"");
	}

}