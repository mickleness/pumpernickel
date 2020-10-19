package com.pump.text.html.css.background;

import java.util.Arrays;

import com.pump.text.html.css.CssPropertyParser;

public class CssBackgroundClipParser
		implements CssPropertyParser<CssBackgroundClipValue> {

	@Override
	public String getPropertyName() {
		return CssBackgroundClipValue.PROPERTY_BACKGROUND_CLIP;
	}

	@Override
	public CssBackgroundClipValue parse(String cssString) {
		String s = cssString.toLowerCase();
		for (CssBackgroundClipValue.Mode mode : CssBackgroundClipValue.Mode
				.values()) {
			if (s.startsWith(mode.toCSSString())) {
				return new CssBackgroundClipValue(cssString, mode);
			}
		}
		throw new IllegalArgumentException(
				"Unsupported keyword \"" + s + "\". Expected "
						+ Arrays.asList(CssBackgroundClipValue.Mode.values()));
	}

}
