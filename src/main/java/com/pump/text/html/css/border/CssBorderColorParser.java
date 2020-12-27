package com.pump.text.html.css.border;

import java.util.List;

import com.pump.text.html.css.CssColorParser;
import com.pump.text.html.css.CssColorValue;
import com.pump.text.html.css.CssListParser;

/**
 * The border-color property may be specified using one, two, three, or four
 * values.
 * <ul>
 * <li>When one value is specified, it applies the same color to all four
 * sides.</li>
 * <li>When two values are specified, the first color applies to the top and
 * bottom, the second to the left and right.</li>
 * <li>When three values are specified, the first color applies to the top, the
 * second to the left and right, the third to the bottom.</li>
 * <li>When four values are specified, the colors apply to the top, right,
 * bottom, and left in that order (clockwise).</li>
 * </ul>
 */
public class CssBorderColorParser extends CssListParser<CssColorValue> {

	public static final String PROPERTY_BORDER_COLOR = "border-color";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_COLOR;
	}

	@Override
	protected int parseListElement(String cssString, int index,
			List<CssColorValue> dest) {
		CssColorParser p = new CssColorParser();
		String substring = cssString.substring(index);
		CssColorValue v = p.parse(substring);
		dest.add(v);

		String z = v.toCSSString();
		return cssString.indexOf(z, index) + z.length();
	}

}
