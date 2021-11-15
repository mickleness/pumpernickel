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
package com.pump.text.html.css;

/**
 * This parses "margin-left", "margin-top", "margin-right" or "margin-bottom".
 */
public class CssSingleMarginParser
		implements CssPropertyParser<CssMarginValue> {

	public static final String PROPERTY_MARGIN_LEFT = "margin-left";
	public static final String PROPERTY_MARGIN_RIGHT = "margin-right";
	public static final String PROPERTY_MARGIN_TOP = "margin-top";
	public static final String PROPERTY_MARGIN_BOTTOM = "margin-bottom";

	String property;

	/**
	 * @param property
	 *            PROPERTY_MARGIN_LEFT, PROPERTY_MARGIN_RIGHT,
	 *            PROPERTY_MARGIN_TOP or PROPERTY_MARGIN_BOTTOM.
	 */
	public CssSingleMarginParser(String property) {
		if (!(PROPERTY_MARGIN_LEFT.equals(property)
				|| PROPERTY_MARGIN_RIGHT.equals(property)
				|| PROPERTY_MARGIN_TOP.equals(property)
				|| PROPERTY_MARGIN_BOTTOM.equals(property)))
			throw new IllegalArgumentException(
					"unsupported property name \"" + property + "\"");
		this.property = property;
	}

	@Override
	public String getPropertyName() {
		return property;
	}

	@Override
	public CssMarginValue parse(String value) {
		return new CssMarginValue(value);
	}
}