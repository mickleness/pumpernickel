package com.pump.text.html.css.border;

import com.pump.text.html.css.CssPropertyParser;

/**
 * The border-top-right-radius CSS property rounds the top-right corner of an
 * element by specifying the radius (or the radius of the semi-major and
 * semi-minor axes) of the ellipse defining the curvature of the corner.
 */
public class CssBorderTopRightRadiusParser
		implements CssPropertyParser<CssBorderRadiusValue> {

	public static final String PROPERTY_BORDER_TOP_RIGHT_RADIUS = "border-top-right-radius";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_TOP_RIGHT_RADIUS;
	}

	@Override
	public CssBorderRadiusValue parse(String cssString) {
		return new CssBorderRadiusValue(cssString);
	}

}
