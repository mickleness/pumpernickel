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
package com.pump.text.html.css.border;

import com.pump.text.html.css.CssPropertyParser;

/**
 * The border-top-left-radius CSS property rounds the top-left corner of an
 * element by specifying the radius (or the radius of the semi-major and
 * semi-minor axes) of the ellipse defining the curvature of the corner.
 */
public class CssBorderTopLeftRadiusParser
		implements CssPropertyParser<CssBorderRadiusValue> {

	public static final String PROPERTY_BORDER_TOP_LEFT_RADIUS = "border-top-left-radius";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_TOP_LEFT_RADIUS;
	}

	@Override
	public CssBorderRadiusValue parse(String cssString) {
		return new CssBorderRadiusValue(cssString);
	}
}