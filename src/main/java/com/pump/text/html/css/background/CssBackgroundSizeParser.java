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
package com.pump.text.html.css.background;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssListParser;
import com.pump.text.html.css.CssParserUtils;
import com.pump.text.html.css.background.CssBackgroundSizeValue.AutoHeightCalculator;
import com.pump.text.html.css.background.CssBackgroundSizeValue.FixedSizeCalculator;

public class CssBackgroundSizeParser
		extends CssListParser<CssBackgroundSizeValue> {

	public static final String PROPERTY_BACKGROUND_SIZE = "background-size";

	private static final String CONTAIN = "contain";
	private static final String COVER = "cover";
	private static final String AUTO = "auto";
	private static final Collection<String> keywords = new HashSet<>(
			Arrays.asList(CONTAIN, COVER, AUTO));

	@Override
	public String getPropertyName() {
		return PROPERTY_BACKGROUND_SIZE;
	}

	@Override
	protected int parseListElement(String cssString, int index,
			List<CssBackgroundSizeValue> dest) {

		// formal syntax:
		// [ <length-percentage> | auto ]{1,2} | cover | contain

		int i = cssString.indexOf(',', index);
		String s = i == -1 ? cssString : cssString.substring(index, i);

		List<Object> words = CssParserUtils.parseLengthsAndStrings(s, keywords);

		if (words.size() == 1 && words.get(0).equals(CONTAIN)) {
			dest.add(new CssBackgroundSizeValue(s,
					CssBackgroundSizeValue.CONTAIN));
		} else if (words.size() == 1 && words.get(0).equals(COVER)) {
			dest.add(new CssBackgroundSizeValue(s,
					CssBackgroundSizeValue.COVER));
		} else if (words.size() == 1 && words.get(0).equals(AUTO)) {
			dest.add(
					new CssBackgroundSizeValue(s, CssBackgroundSizeValue.AUTO));
		} else if (words.size() == 2 && words.get(0).equals(AUTO)
				&& words.get(1).equals(AUTO)) {
			dest.add(
					new CssBackgroundSizeValue(s, CssBackgroundSizeValue.AUTO));
		} else if (words.size() == 2 && words.get(0) instanceof CssLength
				&& words.get(1).equals(AUTO)) {
			CssLength width = (CssLength) words.get(0);
			dest.add(new CssBackgroundSizeValue(s,
					new AutoHeightCalculator(width)));
		} else if (words.size() == 2 && words.get(0).equals(AUTO)
				&& words.get(1) instanceof CssLength) {
			CssLength height = (CssLength) words.get(1);
			dest.add(new CssBackgroundSizeValue(s,
					new AutoHeightCalculator(height)));
		} else if (words.size() == 2 && words.get(0) instanceof CssLength
				&& words.get(1) instanceof CssLength) {
			CssLength width = (CssLength) words.get(0);
			CssLength height = (CssLength) words.get(1);
			dest.add(new CssBackgroundSizeValue(s,
					new FixedSizeCalculator(width, height)));
		} else {
			throw new IllegalArgumentException(
					"Unsupported background size: \"" + s + "\"");
		}
		return index + s.length();

	}

}