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

import java.util.Objects;

import com.pump.text.html.css.AbstractCssValue;
import com.pump.text.html.css.CssColorValue;
import com.pump.text.html.css.CssLength;

public class CssBorderValue extends AbstractCssValue {
	String cssString;
	CssLength width;
	CssBorderStyleValue style;
	CssColorValue color;

	public CssBorderValue(String cssString, CssLength width,
			CssBorderStyleValue style, CssColorValue color) {
		Objects.requireNonNull(cssString);
		Objects.requireNonNull(width);
		Objects.requireNonNull(style);
		Objects.requireNonNull(color);
		this.cssString = cssString;
		this.width = width;
		this.style = style;
		this.color = color;
	}

	public CssLength getWidth() {
		return width;
	}

	public CssColorValue getColor() {
		return color;
	}

	public CssBorderStyleValue getStyle() {
		return style;
	}

	public String toString() {
		return toCSSString();
	}

	@Override
	public String toCSSString() {
		return cssString;
	}

}