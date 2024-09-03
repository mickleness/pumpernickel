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
package com.pump.text.html.css;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CssParserUtils {

	/**
	 * Split the argument into comma-separated terms, but also account for
	 * parentheses. So for ex: "rgb(0,0,0), rgb(5,5,5)" will produce two terms
	 * even though there are 6 commas.
	 * 
	 * @param trim
	 *            if true then the elements in the returned list are trimmed
	 */
	public static List<String> splitCommaSeparatedList(String str,
			boolean trim) {
		return splitList(str, ',', trim);
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

	/**
	 * Return the first index of whitespace in a String.
	 */
	public static int indexOfWhitespace(String str, int i) {
		for (int a = i; a < str.length(); a++) {
			char ch = str.charAt(a);
			if (Character.isWhitespace(ch))
				return a;
		}
		return -1;
	}

	/**
	 * Return a List of Strings and CssLengths separated by whitespace.
	 * 
	 * @param s
	 *            the String to parse, which will be compared to keywords with
	 *            case insensitivity.
	 * @param allowedKeywords
	 *            a set of lowercase keywords that may be returned as Strings
	 *            Every space-separated String not in this collection is
	 *            converted to a CssLength
	 */
	public static List<Object> parseLengthsAndStrings(final String s,
			Collection<String> allowedKeywords) {
		List<Object> words = new ArrayList<>();
		String t = s;
		while (t.length() > 0) {
			int i = CssParserUtils.indexOfWhitespace(t, 0);
			String word = i == -1 ? t : t.substring(0, i);
			word = word.toLowerCase();

			// consider "5%/4%", where "/" is a keyword. In this case
			// whitespace isn't separating our words, but we want to
			// parse this into "5%", "/" and "4%". This should break up
			// those words (in two different passes):
			for (String allowedKeyword : allowedKeywords) {
				if (word.startsWith(allowedKeyword)) {
					word = allowedKeyword;
					break;
				}
				int j = word.indexOf(allowedKeyword);
				if (j != -1) {
					word = word.substring(0, j);
					break;
				}
			}

			if (allowedKeywords.contains(word)) {
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

	/**
	 * Split the argument into space-separated terms. For example: "4mm ridge
	 * rgba(170, 50, 220, .6)" is converted into "4mm" "ridge" and "rgba(170,
	 * 50, 220, .6)".
	 * 
	 * @param trim
	 *            if true then the elements in the returned list are trimmed
	 */
	public static List<String> splitSpaceSeparatedList(String str,
			boolean trim) {
		return splitList(str, ' ', trim);
	}

	public static List<String> splitList(String str, char separator,
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
			} else if (ch == separator && parenthesesCtr == 0) {
				if (sb.length() > 0) {
					if (trim) {
						returnValue.add(sb.toString().trim());
					} else {
						returnValue.add(sb.toString());
					}
				}
				sb.delete(0, sb.length());
			} else {
				sb.append(ch);
			}
		}
		if (sb.length() > 0) {
			if (trim) {
				returnValue.add(sb.toString().trim());
			} else {
				returnValue.add(sb.toString());
			}
		}

		return returnValue;
	}

}