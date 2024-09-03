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

import java.awt.Color;
import java.util.Objects;

/**
 * This is a <code>java.awt.Color</code> that was originally expressed as CSS.
 * The current CSS parser supports RGBA, HSLA, hex and named colors.
 */
public class CssColorValue extends Color implements CssValue {
	private static final long serialVersionUID = 1L;

	private final String cssString;

	protected final CssValueCreationToken creationToken = new CssValueCreationToken();

	public CssColorValue(String cssStr, int r, int g, int b, int a) {
		super(r, g, b, a);
		Objects.requireNonNull(cssStr);
		cssString = cssStr;
	}

	public CssColorValue(String cssStr, Color color) {
		this(cssStr, color.getRed(), color.getGreen(), color.getBlue(),
				color.getAlpha());
	}

	@Override
	public String toCSSString() {
		return cssString;
	}

	@Override
	public CssValueCreationToken getCreationToken() {
		return creationToken;
	}

}