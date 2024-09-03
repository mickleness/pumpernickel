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

import java.util.Objects;

public class CssMarginValue extends AbstractCssValue {

	String cssString;
	boolean isAuto;
	CssLength length;

	public CssMarginValue(String cssString) {
		Objects.requireNonNull(cssString);
		this.cssString = cssString;
		isAuto = "auto".equalsIgnoreCase(cssString);
		if (!isAuto)
			length = new CssLength(cssString);
	}

	public boolean isAuto() {
		return isAuto;
	}

	public CssLength getLength() {
		return length;
	}

	@Override
	public String toCSSString() {
		return cssString;
	}

}