package com.pump.text.html.css;

import java.util.ArrayList;
import java.util.List;

public abstract class CssListParser<T> implements CssPropertyParser<List<T>> {

	@Override
	public List<T> parse(String cssString) {
		List<T> returnValue = new ArrayList<>();
		int index = 0;

		// consume leading whitespace
		while (index < cssString.length()
				&& Character.isWhitespace(cssString.charAt(index))) {
			index++;
		}

		while (index < cssString.length()) {
			index = parseListElement(cssString, index, returnValue);

			// consume trailing whitespace
			while (index < cssString.length()
					&& Character.isWhitespace(cssString.charAt(index))) {
				index++;
			}

			// consume possible comma
			if (index < cssString.length() && cssString.charAt(index) == ',') {
				index++;
			}

			// consume leading whitespace
			while (index < cssString.length()
					&& Character.isWhitespace(cssString.charAt(index))) {
				index++;
			}
		}
		return returnValue;
	}

	/**
	 * Parse exactly one element T from a String starting at a certain index.
	 * 
	 * @param cssString
	 *            the complete incoming CSS String, which may include elements
	 *            previously parsed and elements that should not be parsed yet.
	 * @param index
	 *            the position in the String to parse an element from
	 * @param dest
	 *            the list to add the parsed element to.
	 * @return the position after index where this method stopped parsing. This
	 *         should either point to a comma (indicating a new element should
	 *         be parsed) or the end of the String (indicating the entire list
	 *         of elements has been parsed).
	 */
	protected abstract int parseListElement(String cssString, int index,
			List<T> dest);

}
