package com.pump.text.html.css.border;

import com.pump.text.html.css.CssPropertyParser;

public class CssBorderBottomStyleParser
		implements CssPropertyParser<CssBorderStyleValue> {

	public static final String PROPERTY_BORDER_BOTTOM_STYLE = "border-bottom-style";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_BOTTOM_STYLE;
	}

	@Override
	public CssBorderStyleValue parse(String value) {
		return new CssBorderStyleValue(value);
	}

}
