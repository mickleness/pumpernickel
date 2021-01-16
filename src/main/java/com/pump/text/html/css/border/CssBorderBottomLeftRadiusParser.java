package com.pump.text.html.css.border;

import com.pump.text.html.css.CssPropertyParser;

/**
 * The border-bottom-left-radius CSS property rounds the bottom-left corner of
 * an element by specifying the radius (or the radius of the semi-major and
 * semi-minor axes) of the ellipse defining the curvature of the corner.
 */
public class CssBorderBottomLeftRadiusParser
		implements CssPropertyParser<CssBorderRadiusValue> {

	public static final String PROPERTY_BORDER_BOTTOM_LEFT_RADIUS = "border-bottom-left-radius";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_BOTTOM_LEFT_RADIUS;
	}

	@Override
	public CssBorderRadiusValue parse(String cssString) {
		return new CssBorderRadiusValue(cssString);
	}

}
