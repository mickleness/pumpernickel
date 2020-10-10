package com.pump.text.html.css;

import java.util.LinkedList;
import java.util.List;

public class CssParserUtils {

	/**
	 * Split the argument into comma-separated terms, but also account for
	 * parentheses. So for ex: "rgb(0,0,0), rgb(5,5,5)" will produce two terms
	 * even though there are 6 commas.
	 * 
	 */
	public static List<String> splitCommaSeparatedList(String str,
			boolean trim) {
		List<String> returnValue = new LinkedList<>();
		StringBuilder sb = new StringBuilder();
		int parenthesesCtr = 0;
		for (int a = 0; a < str.length(); a++) {
			char ch = str.charAt(a);
			if (ch == '(') {
				parenthesesCtr++;
				sb.append(ch);
			} else if (ch == ')') {
				parenthesesCtr--;
				sb.append(ch);
			} else if (ch == ',' && parenthesesCtr == 0) {
				if (trim) {
					returnValue.add(sb.toString().trim());
				} else {
					returnValue.add(sb.toString());
				}
				sb.delete(0, sb.length());
			} else {
				sb.append(ch);
			}
		}
		if (trim) {
			returnValue.add(sb.toString().trim());
		} else {
			returnValue.add(sb.toString());
		}

		return returnValue;
	}

	/**
	 * Identify a closing parentheses, and optionally all the characters
	 * in-between.
	 * <p>
	 * This skips over nested parentheses. As of this writing it does not
	 * account for quotation marks and escaped characters.
	 * 
	 * @param str
	 *            the string to examine
	 * @param startingParentheses
	 *            the index in str of the starting parentheses
	 * @param sb
	 *            an optional StringBuilder to store the characters between
	 *            parentheses in.
	 * @return the index in str of the closing parentheses
	 */
	public static int getClosingParentheses(String str, int startingParentheses,
			StringBuilder sb) {
		int index = startingParentheses;
		char ch = str.charAt(index);
		if (ch != '(')
			throw new IllegalArgumentException("the character at index "
					+ startingParentheses + " should be (, but was " + ch);

		index++;
		int parenthesesCtr = 1;

		while (true) {
			if (index >= str.length())
				throw new IllegalArgumentException(
						"no closing parentheses found after "
								+ startingParentheses);

			ch = str.charAt(index);
			if (ch == '(') {
				parenthesesCtr++;
			} else if (ch == ')') {
				parenthesesCtr--;
				if (parenthesesCtr == 0)
					return index;
			}
			if (sb != null)
				sb.append(ch);

			index++;
		}

	}

}
