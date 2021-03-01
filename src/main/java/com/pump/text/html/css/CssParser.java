package com.pump.text.html.css;

import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This parses simple CSS rules. It does not parse rules that start with "@".
 * <p>
 * This exists because javax.swing.text.html.StyleSheet.CssParser is not public
 * and it drops properties it doesn't recognize as CSS.Attributes.
 */
public class CssParser {

	/**
	 * One or more selectors that include a map of key/value attributes.
	 * <p>
	 * For example: the CSS represented as "h1, h2 {color:red}" includes two
	 * selectors ("h1", "h2") and a one-element map (color/red).
	 */
	public static class Rule {
		List<String> selectors;
		Map<String, String> properties;

		public Rule(List<String> selectors) {
			this.selectors = selectors;
			// order matters for BorderRenderingConfiguration, see QHtmlTests
			properties = new LinkedHashMap<>();
		}

		public List<String> getSelectors() {
			return selectors;
		}

		public Map<String, String> getProperties() {
			return properties;
		}

		@Override
		public int hashCode() {
			return Objects.hash(selectors, properties);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Rule))
				return false;
			Rule other = (Rule) obj;
			if (!Objects.equals(selectors, other.selectors))
				return false;
			if (!Objects.equals(properties, other.properties))
				return false;
			return true;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int a = 0; a < selectors.size(); a++) {
				sb.append(selectors.get(a));
				sb.append(" ");
			}
			sb.append("{");
			Iterator<Map.Entry<String, String>> iter = properties.entrySet()
					.iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entry = iter.next();
				sb.append(entry.getKey() + ": " + entry.getValue());
				if (iter.hasNext()) {
					sb.append("; ");
				}
			}
			sb.append("}");
			return sb.toString();
		}
	}

	/**
	 * Return a series of Rules parsed from the given String.
	 */
	public List<Rule> parseRules(String str) {
		List<Rule> returnValue = new ArrayList<>();
		int index = 0;
		index = skipWhiteSpaceAndComments(str, index);
		List<String> selectors = new ArrayList<>();
		StringBuilder unfinishedSelector = new StringBuilder();
		while (true) {
			char ch = index < str.length() ? str.charAt(index)
					: CharacterIterator.DONE;
			if (ch == ',') {
				if (unfinishedSelector.length() > 0) {
					selectors.add(unfinishedSelector.toString().trim());
					unfinishedSelector.delete(0, unfinishedSelector.length());
				} else {
					throw new RuntimeException("Empty selector");
				}
				index = skipWhiteSpaceAndComments(str, index + 1);
				continue;
			} else if (ch == '{') {
				if (unfinishedSelector.length() > 0) {
					selectors.add(unfinishedSelector.toString().trim());
					unfinishedSelector.delete(0, unfinishedSelector.length());
				}
				if (selectors.isEmpty())
					throw new RuntimeException(
							"No selectors detected before opening curly bracket.");
				Rule rule = new Rule(selectors);
				returnValue.add(rule);
				index = parseRule(str, index + 1, rule);
				selectors = new ArrayList<>();
			} else if (ch == '\\') {
				index = parseEscapedChar(str, index, unfinishedSelector);
			} else if (ch == CharacterIterator.DONE) {
				return returnValue;
			} else {
				unfinishedSelector.append(ch);
			}
			index++;
		}
	}

	private int skipWhiteSpaceAndComments(String str, int index) {
		while (true) {
			char ch = index < str.length() ? str.charAt(index)
					: CharacterIterator.DONE;
			char next = index + 1 < str.length() ? str.charAt(index + 1)
					: CharacterIterator.DONE;
			if (ch == CharacterIterator.DONE)
				return index;

			if (ch == '/' && next == '*') {
				int commentEnd = str.indexOf("*/", index);
				index = commentEnd + 2;
			} else if (Character.isWhitespace(ch)) {
				index++;
			} else {
				return index;
			}
		}
	}

	/**
	 * This is called when the cursor points just after a "{" in a rule
	 * definition
	 * 
	 * @return the cursor position just after the "}" concluded a rule
	 *         definition
	 */
	private int parseRule(String str, int index, Rule rule) {
		index = skipWhiteSpaceAndComments(str, index);
		StringBuilder propertyKey = new StringBuilder();
		while (true) {
			char ch = index < str.length() ? str.charAt(index)
					: CharacterIterator.DONE;
			if (ch == ':') {
				index = parseRuleProperty(str, index + 1, rule,
						propertyKey.toString());
				index = skipWhiteSpaceAndComments(str, index);
				propertyKey.delete(0, propertyKey.length());
				continue;
			} else if (ch == '}') {
				if (propertyKey.length() > 0) {
					throw new RuntimeException("The key \"" + propertyKey
							+ "\" did not include a colon-separated value.");
				}
				return index + 1;
			} else if (ch == CharacterIterator.DONE) {
				throw new RuntimeException("Unexpected EOF");
			} else {
				propertyKey.append(ch);
			}

			index++;
		}
	}

	/**
	 * 
	 * @param str
	 *            the string that contains the Rule we're trying to parse
	 * @param index
	 *            the position just after the ":" in a key/value pair
	 * @param rule
	 *            the rule to add the property to
	 * @param propertyKey
	 *            the key of the property to add
	 * @return the position just after a ";", or just before "}"
	 */
	private int parseRuleProperty(String str, int index, Rule rule,
			String propertyKey) {
		StringBuilder propertyValue = new StringBuilder();
		while (true) {
			char ch = index < str.length() ? str.charAt(index)
					: CharacterIterator.DONE;
			if (ch == '"' || ch == '\'') {
				propertyValue.append(ch);
				index++;

				char closingQuotationMark = ch;
				boolean insideQuotation = true;
				while (insideQuotation) {
					ch = index < str.length() ? str.charAt(index)
							: CharacterIterator.DONE;
					if (ch == '\\') {
						index = parseEscapedChar(str, index, propertyValue);
						index++;
					} else if (ch == closingQuotationMark) {
						insideQuotation = false;
						propertyValue.append(ch);
					} else if (ch == CharacterIterator.DONE) {
						throw new RuntimeException(
								"Unexpected EOF inside quotation mark");
					} else {
						propertyValue.append(ch);
						index++;
					}
				}
			} else if (ch == ';') {
				rule.getProperties().put(propertyKey,
						propertyValue.toString().trim());
				return index + 1;
			} else if (ch == '}') {
				rule.getProperties().put(propertyKey,
						propertyValue.toString().trim());
				return index;
			} else if (ch == '\\') {
				index = parseEscapedChar(str, index, propertyValue);
			} else if (ch == CharacterIterator.DONE) {
				throw new RuntimeException("Unexpected EOF");
			} else {
				propertyValue.append(ch);
			}
			index++;
		}
	}

	/**
	 * Parse a backslash encoded character.
	 * 
	 * @param str
	 *            the String to read from
	 * @param index
	 *            the character index after the first backslash
	 * @param dest
	 *            the StringBuilder to deposit the decoded char into
	 * @return the index in the String to resume reading
	 */
	private int parseEscapedChar(String str, int index, StringBuilder dest) {

		char next1 = index + 1 < str.length() ? str.charAt(index + 1)
				: CharacterIterator.DONE;
		char next2 = index + 2 < str.length() ? str.charAt(index + 2)
				: CharacterIterator.DONE;
		char next3 = index + 3 < str.length() ? str.charAt(index + 3)
				: CharacterIterator.DONE;
		char next4 = index + 4 < str.length() ? str.charAt(index + 4)
				: CharacterIterator.DONE;
		char next5 = index + 5 < str.length() ? str.charAt(index + 5)
				: CharacterIterator.DONE;
		char next6 = index + 6 < str.length() ? str.charAt(index + 6)
				: CharacterIterator.DONE;

		boolean hex1 = isHex(next1);
		boolean hex2 = isHex(next2);
		boolean hex3 = isHex(next3);
		boolean hex4 = isHex(next4);
		boolean hex5 = isHex(next5);
		boolean hex6 = isHex(next6);

		if (hex1 && hex2 && hex3 && hex4 && hex5 && hex6) {
			StringBuilder hex = new StringBuilder();
			hex.append(next1);
			hex.append(next2);
			hex.append(next3);
			hex.append(next4);
			hex.append(next5);
			hex.append(next6);
			int k = Integer.parseInt(hex.toString(), 16);
			dest.append((char) k);
			index = index + 6;
		} else if (hex1 && hex2 && hex3 && hex4 && hex5 && !hex6) {
			StringBuilder hex = new StringBuilder();
			hex.append(next1);
			hex.append(next2);
			hex.append(next3);
			hex.append(next4);
			hex.append(next5);
			int k = Integer.parseInt(hex.toString(), 16);
			dest.append((char) k);
			index = index + 5;
			if (next6 == ' ')
				index++;
		} else if (hex1 && hex2 && hex3 && hex4 && !hex5) {
			StringBuilder hex = new StringBuilder();
			hex.append(next1);
			hex.append(next2);
			hex.append(next3);
			hex.append(next4);
			int k = Integer.parseInt(hex.toString(), 16);
			dest.append((char) k);
			index = index + 4;
			if (next5 == ' ')
				index++;
		} else if (hex1 && hex2 && hex3 && !hex4) {
			StringBuilder hex = new StringBuilder();
			hex.append(next1);
			hex.append(next2);
			hex.append(next3);
			int k = Integer.parseInt(hex.toString(), 16);
			dest.append((char) k);
			index = index + 3;
			if (next4 == ' ')
				index++;
		} else if (hex1 && hex2 && !hex3) {
			StringBuilder hex = new StringBuilder();
			hex.append(next1);
			hex.append(next2);
			int k = Integer.parseInt(hex.toString(), 16);
			dest.append((char) k);
			index = index + 2;
			if (next3 == ' ')
				index++;
		} else if (hex1 && !hex2) {
			StringBuilder hex = new StringBuilder();
			hex.append(next1);
			int k = Integer.parseInt(hex.toString(), 16);
			dest.append((char) k);
			index = index + 1;
			if (next2 == ' ')
				index++;
		} else if (!hex1) {
			dest.append(next1);
			index = index + 1;
		}
		return index;
	}

	private boolean isHex(char ch) {
		if (Character.isDigit(ch))
			return true;
		ch = Character.toLowerCase(ch);
		if (ch >= 'a' && ch <= 'f')
			return true;
		return false;
	}

	/**
	 * Parse a semicolon-separated list of properties, like "color:red;
	 * text-shadow:none"
	 */
	public Map<String, String> parseDeclaration(String decl) {
		String rule = "ignore { " + decl + " }";
		return parseRules(rule).get(0).properties;
	}

}
