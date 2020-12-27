package com.pump.text.html.css.border;

import com.pump.text.html.css.CssPropertyParser;

public class CssBorderTopStyleParser
		implements CssPropertyParser<CssBorderStyleValue> {

	public static final String PROPERTY_BORDER_TOP_STYLE = "border-top-style";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_TOP_STYLE;
	}

	@Override
	public CssBorderStyleValue parse(String value) {
		return new CssBorderStyleValue(value);
	}

}
