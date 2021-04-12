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
