package com.pump.text.html.css.border;

import java.util.List;

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssListParser;

/**
 * The border-width property may be specified using one, two, three, or four
 * values.
 * 
 * <ul>
 * <li>When one value is specified, it applies the same width to all four
 * sides.</li>
 * <li>When two values are specified, the first width applies to the top and
 * bottom, the second to the left and right.</li>
 * <li>When three values are specified, the first width applies to the top, the
 * second to the left and right, the third to the bottom.</li>
 * <li>When four values are specified, the widths apply to the top, right,
 * bottom, and left in that order (clockwise).</li>
 * </ul>
 */
public class CssBorderWidthParser extends CssListParser<CssLength> {

	public static final String VALUE_THIN = "thin";
	public static final String VALUE_MEDIUM = "medium";
	public static final String VALUE_THICK = "thick";

	public static final String PROPERTY_BORDER_WIDTH = "border-width";

	@Override
	public String getPropertyName() {
		return PROPERTY_BORDER_WIDTH;
	}

	@Override
	protected int parseListElement(String cssString, int index,
			List<CssLength> dest) {
		String substring1 = cssString.substring(index);
		String substring2 = substring1.stripLeading().toLowerCase();
		int deletedWhitespace = substring1.length() - substring2.length();
		if (substring2.startsWith(VALUE_THIN)) {
			String cssWord = substring1.substring(deletedWhitespace,
					deletedWhitespace + VALUE_THIN.length());
			dest.add(new CssLength(cssWord, 1, "px"));
			return index + cssWord.length() + deletedWhitespace;
		} else if (substring2.startsWith(VALUE_MEDIUM)) {
			String cssWord = substring1.substring(deletedWhitespace,
					deletedWhitespace + VALUE_MEDIUM.length());
			dest.add(new CssLength(cssWord, 2, "px"));
			return index + cssWord.length() + deletedWhitespace;
		} else if (substring2.startsWith(VALUE_THICK)) {
			String cssWord = substring1.substring(deletedWhitespace,
					deletedWhitespace + VALUE_THICK.length());
			dest.add(new CssLength(cssWord, 4, "px"));
			return index + cssWord.length() + deletedWhitespace;
		}

		for (int a = 0; a < substring2.length(); a++) {
			char ch = substring2.charAt(a);
			if (Character.isWhitespace(ch)) {
				String cssWord = substring1.substring(deletedWhitespace, a);
				dest.add(new CssLength(cssWord));
				return a;
			}
		}
		dest.add(new CssLength(substring1));
		return index + substring1.length();
	}
}
