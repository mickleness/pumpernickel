package com.pump.text.html.css;

import java.util.List;

/**
 * This parses the "margin" property.
 */
public class CssMultiMarginParser extends CssListParser<CssMarginValue> {

	public static final String PROPERTY_MARGIN = "margin";

	@Override
	public String getPropertyName() {
		return PROPERTY_MARGIN;
	}

	@Override
	protected int parseListElement(String cssString, int index,
			List<CssMarginValue> dest) {
		StringBuilder sb = new StringBuilder();
		int returnValue = index;
		for (int a = index; a < cssString.length(); a++) {
			returnValue = a + 1;
			char ch = cssString.charAt(a);
			if (Character.isWhitespace(ch)) {
				break;
			}
			sb.append(ch);
		}

		dest.add(new CssMarginValue(sb.toString()));

		return returnValue;
	}

}
