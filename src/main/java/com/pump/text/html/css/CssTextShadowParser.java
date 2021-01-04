package com.pump.text.html.css;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.pump.image.shadow.ShadowAttributes;

/**
 * This parses the "text-shadow" CSS attribute.
 * <p>
 * This currently assumes all the units are pixels.
 */
public class CssTextShadowParser
		implements CssPropertyParser<List<ShadowAttributes>> {

	public static final String PROPERTY_TEXT_SHADOW = "text-shadow";

	@Override
	public String getPropertyName() {
		return PROPERTY_TEXT_SHADOW;
	}

	/**
	 * Parse shadow attributes described as a CSS String
	 * 
	 * @param textShadowCSS
	 *            such as "2px 2px 5px #000000; 3px 3px 10px #FF0000"
	 * @return a series ShadowAttributes. If the argument is "none" then an
	 *         empty array is returned. Also this method can throw a
	 *         RuntimeException if the String cannot be parsed.
	 */
	@Override
	public List<ShadowAttributes> parse(String textShadowCSS) {
		List<String> shadowTerms = CssParserUtils
				.splitCommaSeparatedList(textShadowCSS, true);
		List<ShadowAttributes> attributes = new LinkedList<>();
		for (String shadowTerm : shadowTerms) {
			if (shadowTerm.equalsIgnoreCase("none")) {
				// do nothing
			} else {
				attributes.add(parseShadowAttributes(shadowTerm));
			}
		}
		return attributes;
	}

	private ShadowAttributes parseShadowAttributes(String textShadowCSS) {
		List<String> terms = getCSSTerms(textShadowCSS);
		if (terms.size() >= 2 && terms.size() <= 4) {
			// the specs say the color "can be specified either before or after
			// the offset"

			Iterator<String> termIter = terms.iterator();
			CssColorParser colorParser = new CssColorParser();

			// If unspecified, the color's value is left up to the user agent:
			Color color = Color.black;

			while (termIter.hasNext()) {
				String term = termIter.next();
				try {
					color = colorParser.parse(term, false);
					termIter.remove();
					break;
				} catch (Exception e) {
					// do nothing
				}
			}

			int dx = parseInt(terms.get(0));
			int dy = parseInt(terms.get(1));
			int radius = 0;

			if (terms.size() == 3) {
				try {
					radius = parseInt(terms.get(2));
				} catch (RuntimeException e) {
					throw e;
				}
			}
			return new ShadowAttributes(dx, dy, radius, color);
		} else {
			// we do not currently support spread or other attributes
			throw new IllegalArgumentException("Unsupported number of terms ("
					+ terms.size() + ") in \"" + textShadowCSS + "\"");
		}
	}

	private int parseInt(String str) {
		StringBuilder sb = new StringBuilder();
		for (int a = 0; a < str.length(); a++) {
			char ch = str.charAt(a);
			if (Character.isDigit(ch) || ch == '-') {
				sb.append(ch);
			} else {
				break;
			}
		}
		return Integer.parseInt(sb.toString());
	}

	/**
	 * Split a string by whitespace into separate terms.
	 */
	private List<String> getCSSTerms(String str) {
		List<String> returnValue = new ArrayList<>();
		StringBuilder currentTerm = new StringBuilder();
		for (int a = 0; a < str.length(); a++) {
			char ch = str.charAt(a);
			if (Character.isWhitespace(ch)) {
				if (currentTerm.length() > 0) {
					returnValue.add(currentTerm.toString());
					currentTerm.delete(0, currentTerm.length());
				}
			} else {
				currentTerm.append(ch);
			}
		}

		if (currentTerm.length() > 0) {
			returnValue.add(currentTerm.toString());
		}

		return returnValue;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof CssTextShadowParser;
	}
}
