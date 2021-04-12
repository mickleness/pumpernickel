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
