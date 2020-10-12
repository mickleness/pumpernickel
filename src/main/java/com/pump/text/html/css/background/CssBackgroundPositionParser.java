package com.pump.text.html.css.background;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.swing.text.html.CSS;

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssListParser;

public class CssBackgroundPositionParser
		extends CssListParser<CssBackgroundPositionValue> {

	private static Collection<String> keywords = new HashSet<>();
	static {
		keywords.add("left");
		keywords.add("right");
		keywords.add("center");
		keywords.add("top");
		keywords.add("bottom");
	}

	@Override
	public String getPropertyName() {
		return CSS.Attribute.BACKGROUND_POSITION.toString();
	}

	@Override
	protected int parseListElement(String cssString, int index,
			List<CssBackgroundPositionValue> dest) {
		// the formal syntax reads:
		// @formatter:off
		
		// [ left | center | right | top | bottom | <length-percentage> ] |
		// [ left | center | right | <length-percentage> ] [ top | center | bottom | <length-percentage> ] |
		// [ center | [ left | right ] <length-percentage>? ] && [ center | [ top | bottom ] <length-percentage>? ]
		
		// @formatter:on

		// ... however I noticed Chrome supports "bottom left", so really these
		// can come in any order. So that's how we'll try to interpret them:

		int i = cssString.indexOf(',', index);
		String s = i == -1 ? cssString : cssString.substring(index, i);

		List<Object> words = parseLengthsAndStrings(s);

		CssLength horizPosition = null;
		CssLength vertPosition = null;
		boolean isFromLeft = true;
		boolean isFromTop = true;

		outerLoop: while (horizPosition == null || vertPosition == null) {
			// first pick off the really clear terms
			Object[] wordsCopy = words.toArray();
			for (int a = 0; a < wordsCopy.length; a++) {
				if ("left".equals(wordsCopy[a])) {
					words.remove(wordsCopy[a]);
					if (a + 1 < wordsCopy.length
							&& wordsCopy[a + 1] instanceof CssLength) {
						words.remove(wordsCopy[a + 1]);
						CssLength l = (CssLength) wordsCopy[a + 1];
						if (vertPosition == null && words.isEmpty()) {
							vertPosition = l;
							horizPosition = new CssLength(0, "%");
						} else {
							horizPosition = l;
						}
					} else {
						horizPosition = new CssLength(0, "%");
					}
					continue outerLoop;
				} else if ("right".equals(wordsCopy[a])) {
					words.remove(wordsCopy[a]);
					if (a + 1 < wordsCopy.length
							&& wordsCopy[a + 1] instanceof CssLength) {
						words.remove(wordsCopy[a + 1]);
						CssLength l = (CssLength) wordsCopy[a + 1];
						if (vertPosition == null && words.isEmpty()) {
							vertPosition = l;
							horizPosition = new CssLength(100, "%");
						} else {
							horizPosition = l;
							isFromLeft = false;
						}
					} else {
						horizPosition = new CssLength(100, "%");
					}
					continue outerLoop;
				} else if ("top".equals(wordsCopy[a])) {
					words.remove(wordsCopy[a]);
					if (a + 1 < wordsCopy.length
							&& wordsCopy[a + 1] instanceof CssLength) {
						words.remove(wordsCopy[a + 1]);
						CssLength l = (CssLength) wordsCopy[a + 1];
						if (horizPosition == null && words.isEmpty()) {
							horizPosition = l;
							vertPosition = new CssLength(0, "%");
						} else {
							vertPosition = l;
						}
					} else {
						vertPosition = new CssLength(0, "%");
					}
					continue outerLoop;
				} else if ("bottom".equals(wordsCopy[a])) {
					words.remove(wordsCopy[a]);
					if (a + 1 < wordsCopy.length
							&& wordsCopy[a + 1] instanceof CssLength) {
						words.remove(wordsCopy[a + 1]);
						CssLength l = (CssLength) wordsCopy[a + 1];
						if (horizPosition == null && words.isEmpty()) {
							horizPosition = l;
							vertPosition = new CssLength(100, "%");
						} else {
							vertPosition = l;
							isFromTop = false;
						}
					} else {
						vertPosition = new CssLength(100, "%");
					}
					continue outerLoop;
				}
			}

			for (int a = 0; a < wordsCopy.length; a++) {
				if ("center".equals(wordsCopy[a]) && horizPosition == null) {
					words.remove(wordsCopy[a]);
					horizPosition = new CssLength(50, "%");
					continue outerLoop;
				} else if ("center".equals(wordsCopy[a])
						&& vertPosition == null) {
					words.remove(wordsCopy[a]);
					vertPosition = new CssLength(50, "%");
					continue outerLoop;
				}
			}

			for (int a = 0; a < wordsCopy.length; a++) {
				if (wordsCopy[a] instanceof CssLength
						&& horizPosition == null) {
					words.remove(wordsCopy[a]);
					horizPosition = (CssLength) wordsCopy[a];
					continue outerLoop;
				} else if (wordsCopy[a] instanceof CssLength
						&& vertPosition == null) {
					words.remove(wordsCopy[a]);
					vertPosition = (CssLength) wordsCopy[a];
					continue outerLoop;
				}
			}

			// if our string is simply "left", then we need to assume
			// vertPosition is 50%
			if (words.isEmpty() && vertPosition == null
					&& horizPosition != null) {
				vertPosition = new CssLength(50, "%");
				continue outerLoop;
			}

			// if our string is simply "top", then we need to assume
			// horizPosition is 50%
			if (words.isEmpty() && horizPosition == null
					&& vertPosition != null) {
				horizPosition = new CssLength(50, "%");
				continue outerLoop;
			}

			// we shouldn't reach this point
			throw new RuntimeException("Unrecognized position \"" + s + "\"");
		}

		if (!words.isEmpty()) {
			// we should have consumed all the words
			throw new RuntimeException("Unrecognized position \"" + s + "\"");
		}

		dest.add(new CssBackgroundPositionValue(s, horizPosition, isFromLeft,
				vertPosition, isFromTop));

		return index + s.length();
	}

	private List<Object> parseLengthsAndStrings(final String s) {
		List<Object> words = new ArrayList<>();
		String t = s;
		while (t.length() > 0) {
			int i = indexOfWhitespace(t);
			String word = i == -1 ? t : t.substring(0, i);
			word = word.toLowerCase();
			if (keywords.contains(word)) {
				words.add(word);
			} else {
				try {
					CssLength l = new CssLength(word);
					words.add(l);
				} catch (RuntimeException e) {
					// if it wasn't a length, it was a keyword ("left",
					// "center", etc)
					throw new RuntimeException(
							"unsupported position element \"" + word + "\"");
				}
			}
			t = t.substring(word.length()).trim();
		}
		return words;
	}

	private int indexOfWhitespace(String str) {
		for (int a = 0; a < str.length(); a++) {
			char ch = str.charAt(a);
			if (Character.isWhitespace(ch))
				return a;
		}
		return -1;
	}
}
