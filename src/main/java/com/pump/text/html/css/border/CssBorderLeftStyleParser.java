package com.pump.text.html.css.border;

import com.pump.text.html.css.CssPropertyParser;

public class CssBorderLeftStyleParser
		implements CssPropertyParser<CssBorderStyleValue> {

	public static final String PROPERTY_BORDER_LEFT_STYLE = "border-left-style";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_LEFT_STYLE;
	}

	@Override
	public CssBorderStyleValue parse(String value) {
		return new CssBorderStyleValue(value);
	}

}
