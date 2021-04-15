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
