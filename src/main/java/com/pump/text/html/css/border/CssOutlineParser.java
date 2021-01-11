package com.pump.text.html.css.border;

/**
 * The outline CSS shorthand property set all the outline properties in a single
 * declaration such as "thick double #32a1ce"
 */
public class CssOutlineParser extends CssBorderParser {

	public static final String PROPERTY_OUTLINE = "outline";

	@Override
	public String getPropertyName() {
		return PROPERTY_OUTLINE;
	}
}
