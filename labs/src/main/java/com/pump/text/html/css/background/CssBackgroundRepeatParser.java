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

import java.text.CharacterIterator;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.html.CSS;

import com.pump.text.html.css.CssListParser;
import com.pump.text.html.css.background.CssBackgroundRepeatValue.Mode;

public class CssBackgroundRepeatParser
		extends CssListParser<CssBackgroundRepeatValue> {

	@Override
	public String getPropertyName() {
		return CSS.Attribute.BACKGROUND_REPEAT.toString();
	}

	@Override
	protected int parseListElement(String cssString, int index,
			List<CssBackgroundRepeatValue> dest) {

		// the format syntax is a little funky:
		// <repeat-style> = repeat-x | repeat-y | [ repeat | space | round |
		// no-repeat ]{1,2}
		String s = cssString.substring(index);
		if (s.startsWith("repeat-x")) {
			dest.add(new CssBackgroundRepeatValue("repeat-x", Mode.REPEAT,
					Mode.NO_REPEAT));
			return index + "repeat-x".length();
		} else if (s.startsWith("repeat-y")) {
			dest.add(new CssBackgroundRepeatValue("repeat-y", Mode.NO_REPEAT,
					Mode.REPEAT));
			return index + "repeat-x".length();
		}
		Mode xKeyword = null;
		for (Mode k : Mode.values()) {
			if (s.startsWith(k.getKeyword())) {
				xKeyword = k;
				break;
			}
		}

		if (xKeyword == null)
			throw new IllegalArgumentException("Unsupported keyword \"" + s
					+ "\". Expected " + Arrays.asList(Mode.values()));

		int index2 = index + xKeyword.getKeyword().length();
		char ch = index2 < cssString.length() ? cssString.charAt(index2)
				: CharacterIterator.DONE;

		while (Character.isWhitespace(ch)) {
			index2++;
			ch = index2 < cssString.length() ? cssString.charAt(index2)
					: CharacterIterator.DONE;
		}

		if (ch == ',' || ch == CharacterIterator.DONE) {
			dest.add(new CssBackgroundRepeatValue(xKeyword.getKeyword(),
					xKeyword));
			return index2;
		}

		s = cssString.substring(index2);
		Mode yKeyword = null;
		for (Mode k : Mode.values()) {
			if (s.startsWith(k.getKeyword())) {
				yKeyword = k;
				break;
			}
		}

		if (yKeyword == null) {
			throw new IllegalArgumentException("Unsupported keyword \"" + s
					+ "\". Expected " + Arrays.asList(Mode.values()));
		}

		index2 += yKeyword.getKeyword().length();
		dest.add(new CssBackgroundRepeatValue(
				cssString.substring(index, index2), xKeyword, yKeyword));
		return index2;

	}

}