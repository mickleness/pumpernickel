package com.pump.text.html.css.border;

import com.pump.text.html.css.CssPropertyParser;

public class CssBorderRightStyleParser
		implements CssPropertyParser<CssBorderStyleValue> {

	public static final String PROPERTY_BORDER_RIGHT_STYLE = "border-right-style";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_RIGHT_STYLE;
	}

	@Override
	public CssBorderStyleValue parse(String value) {
		return new CssBorderStyleValue(value);
	}

}
