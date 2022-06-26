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
package com.pump.text.html.css.border;

import java.util.List;

import com.pump.text.html.css.CssListParser;

/**
 * The border-style property may be specified using one, two, three, or four
 * values.
 * <ul>
 * <li>When one value is specified, it applies the same style to all four
 * sides.</li>
 * <li>When two values are specified, the first style applies to the top and
 * bottom, the second to the left and right.</li>
 * <li>When three values are specified, the first style applies to the top, the
 * second to the left and right, the third to the bottom.</li>
 * <li>When four values are specified, the styles apply to the top, right,
 * bottom, and left in that order (clockwise).</li>
 * </ul>
 */
public class CssBorderStyleParser extends CssListParser<CssBorderStyleValue> {

	public static final String PROPERTY_BORDER_STYLE = "border-style";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_STYLE;
	}

	@Override
	protected int parseListElement(String cssString, int index,
			List<CssBorderStyleValue> dest) {
		String substring1 = cssString.substring(index);
		String substring2 = substring1.stripLeading().toLowerCase();
		int deletedWhitespace = substring1.length() - substring2.length();

		for (int a = 0; a < substring2.length(); a++) {
			char ch = substring2.charAt(a);
			if (Character.isWhitespace(ch)) {
				String cssWord = substring1.substring(deletedWhitespace, a);
				dest.add(new CssBorderStyleValue(cssWord));
				return index + a;
			}
		}
		dest.add(new CssBorderStyleValue(substring1));
		return index + substring1.length();
	}
}