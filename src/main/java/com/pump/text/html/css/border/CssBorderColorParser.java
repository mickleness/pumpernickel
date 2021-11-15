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

		String substring1 = cssString.substring(index);
		String substring2 = substring1.trim();
		int leadingWhitespace = substring1.length() - substring2.length();

		for (int i = 0; i < substring2.length(); i++) {
			char ch = substring2.charAt(i);
			if (Character.isWhitespace(ch)) {
				String firstTerm = substring2.substring(0, i);
				CssColorValue v = p.parse(firstTerm);
				dest.add(v);
				return index + i + leadingWhitespace;
			}
		}

		CssColorValue v = p.parse(substring2);
		dest.add(v);

		String z = v.toCSSString();
		return index + leadingWhitespace + z.length();
	}

}