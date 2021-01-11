package com.pump.text.html.css.border;

/**
 * The CSS outline-width property sets the thickness of an element's outline. An
 * outline is a line that is drawn around an element, outside the border.
 */
public class CssOutlineWidthParser extends AbstractWidthParser {

	public static final String PROPERTY_OUTLINE_WIDTH = "outline-width";

	@Override
	public String getPropertyName() {
		return PROPERTY_OUTLINE_WIDTH;
	}
}
