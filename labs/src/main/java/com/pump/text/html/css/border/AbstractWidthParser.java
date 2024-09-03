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

import java.util.HashMap;
import java.util.Map;

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssPropertyParser;

/**
 * This parser resolves "thin" to "1px", "medium" to "3px" and "thick" to "5px",
 * and any other value is parsed like a normal CssLength (such as "4px").
 */
public abstract class AbstractWidthParser
		implements CssPropertyParser<CssLength> {

	public static final String VALUE_THIN = "thin";
	public static final String VALUE_MEDIUM = "medium";
	public static final String VALUE_THICK = "thick";

	private static Map<String, Integer> widthMap = new HashMap<>();

	/**
	 * Set (or override) a value like "thin", "medium" or "thick"
	 */
	public static void setWidth(String widthName, int pixelValue) {
		synchronized (widthMap) {
			widthMap.put(widthName.toLowerCase(), pixelValue);
		}
	}

	static {
		setWidth(VALUE_THIN, 1);
		setWidth(VALUE_MEDIUM, 3);
		setWidth(VALUE_THICK, 5);
	}

	@Override
	public CssLength parse(String value) {
		String z = value.toLowerCase();
		String z2 = z.stripLeading();
		int deletedWhitespace = z.length() - z2.length();

		for (int a = 0; a < z2.length(); a++) {
			char ch = z2.charAt(a);
			if (Character.isWhitespace(ch)) {
				String cssWord = value.substring(deletedWhitespace,
						deletedWhitespace + a);
				return parseWord(cssWord);
			}
		}

		return parseWord(value);
	}

	private CssLength parseWord(String cssWord) {
		String lowerCaseCssWord = cssWord.toLowerCase();

		Integer lutValue;
		synchronized (widthMap) {
			lutValue = widthMap.get(lowerCaseCssWord);
		}
		if (lutValue == null) {
			return new CssLength(cssWord);
		} else {
			return new CssLength(cssWord, lutValue.intValue(), "px");
		}
	}
}