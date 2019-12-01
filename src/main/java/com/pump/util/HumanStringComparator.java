package com.pump.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This comparator sorts Strings like a human reader would.
 * <p>
 * For example: "9" should come before "10", because they are read as integers.
 * {@link String#compareTo(String)} does not sort them in that order, though
 * (unless you rename the strings as "09" and "10".)
 */
public class HumanStringComparator implements Comparator<String> {

	abstract static class ComparatorToken implements
			Comparable<ComparatorToken> {
		String text;

		ComparatorToken(String text) {
			this.text = text;
		}

		public abstract int getOrder();

		@Override
		public String toString() {
			return text;
		}

		@Override
		public int compareTo(ComparatorToken other) {
			int z1 = getOrder();
			int z2 = other.getOrder();
			int z = Integer.compare(z1, z2);
			if (z != 0)
				return z;
			return compareSameToken(other);
		}

		abstract int compareSameToken(ComparatorToken other);
	}

	/**
	 * All whitespace tokens are treated as equal. For example: "\t" equals "  "
	 */
	static class WhitespaceToken extends ComparatorToken {
		WhitespaceToken(String text) {
			super(text);
		}

		@Override
		public int getOrder() {
			return 0;
		}

		@Override
		int compareSameToken(ComparatorToken other) {
			return 0;
		}
	}

	static class TextToken extends ComparatorToken {
		TextToken(String text) {
			// no case sensitivity. (The dictionary isn't separated into
			// "upper case T" and "lower case T" sections.)
			super(text.toLowerCase());
		}

		@Override
		public int getOrder() {
			return 1;
		}

		@Override
		int compareSameToken(ComparatorToken other) {
			return toString().compareTo(other.toString());
		}
	}

	static class NumberToken extends ComparatorToken {
		double value;

		NumberToken(String text) {
			super(text);
			value = Double.parseDouble(text);
		}

		@Override
		public int getOrder() {
			return 2;
		}

		@Override
		int compareSameToken(ComparatorToken other) {
			NumberToken n = (NumberToken) other;
			return Double.compare(value, n.value);
		}
	}

	Cache<String, ComparatorToken[]> tokenCache = new Cache<>(10000, 1000, 1000);
	boolean useStringComparatorAsBackup;

	public HumanStringComparator() {
		this(true);
	}

	public HumanStringComparator(boolean useStringComparatorAsBackup) {
		this.useStringComparatorAsBackup = useStringComparatorAsBackup;
	}

	ComparatorToken[] parse(String str) {
		List<ComparatorToken> returnValue = new ArrayList<>(str.length() / 2);
		char[] c = str.toCharArray();
		StringBuilder everythingElse = new StringBuilder();
		for (int a = 0; a < c.length; a++) {
			if (Character.isWhitespace(c[a])) {
				if (everythingElse.length() > 0) {
					returnValue.add(new TextToken(everythingElse.toString()));
					everythingElse.delete(0, everythingElse.length());
				}
				StringBuilder sb = new StringBuilder();
				sb.append(c[a]);
				while (a + 1 < c.length && Character.isWhitespace(c[a + 1])) {
					a++;
					sb.append(c[a]);
				}
				returnValue.add(new WhitespaceToken(sb.toString()));
			} else if (Character.isDigit(c[a]) || c[a] == '.') {
				// we're mostly looking for: "#.#"
				// (Let's assume scientific notation is beyond the scope of our
				// current needs.)

				if (c[a] == '.') {
					int trailingClusterLength = getDigitClusterLength(c, a + 1);
					if (trailingClusterLength == 0) {
						// abort, we don't consume this period
						everythingElse.append(c[a]);
					} else {
						if (everythingElse.length() > 0) {
							returnValue.add(new TextToken(everythingElse
									.toString()));
							everythingElse.delete(0, everythingElse.length());
						}

						returnValue.add(new NumberToken(str.substring(a, a
								+ trailingClusterLength + 1)));
						a += trailingClusterLength;
					}
				} else {
					if (everythingElse.length() > 0) {
						returnValue
								.add(new TextToken(everythingElse.toString()));
						everythingElse.delete(0, everythingElse.length());
					}

					int leadingClusterLength = getDigitClusterLength(c, a);
					if (a + leadingClusterLength == c.length
							|| c[a + leadingClusterLength] != '.') {
						// we consumed a cluster a digits with no decimal place
						returnValue.add(new NumberToken(str.substring(a, a
								+ leadingClusterLength)));
						a += leadingClusterLength - 1;
					} else {
						int trailingClusterLength = getDigitClusterLength(c, a
								+ leadingClusterLength + 1);
						if (trailingClusterLength == 0) {
							// this period is not a decimal place, consume our
							// leading digits but nothing else
							returnValue.add(new NumberToken(str.substring(a, a
									+ leadingClusterLength)));
							a += leadingClusterLength - 1;
						} else {
							// we consumed a number with leading and trailing
							// digits:
							returnValue.add(new NumberToken(str.substring(a, a
									+ 1 + leadingClusterLength
									+ trailingClusterLength)));
							a += leadingClusterLength + trailingClusterLength;
						}
					}
				}
			} else {
				everythingElse.append(c[a]);
			}
		}

		if (everythingElse.length() > 0) {
			returnValue.add(new TextToken(everythingElse.toString()));
		}

		return returnValue.toArray(new ComparatorToken[returnValue.size()]);

	}

	int getDigitClusterLength(char[] chars, int index) {
		int length = 0;
		while (index < chars.length && Character.isDigit(chars[index])) {
			length++;
			index++;
		}
		return length;
	}

	ComparatorToken[] getTokens(String str) {
		ComparatorToken[] returnValue = tokenCache.get(str);
		if (returnValue == null) {
			returnValue = parse(str);
			tokenCache.put(str, returnValue);
		}
		return returnValue;
	}

	/**
	 * This method first consults {@link #humanCompare(String, String)} and then
	 * (if that returns zero) will default to {@link String#compareTo(String)}.
	 */
	@Override
	public int compare(String o1, String o2) {
		int k = humanCompare(o1, o2);
		if (k != 0)
			return k;

		return o1.compareTo(o2);
	}

	/**
	 * This evaluates whether two Strings look functionally the same from a
	 * human reader's perspective.
	 */
	public int humanCompare(String str1, String str2) {
		ComparatorToken[] tokens1 = getTokens(str1);
		ComparatorToken[] tokens2 = getTokens(str2);

		int m = Math.max(tokens1.length, tokens2.length);
		for (int a = 0; a < m; a++) {
			ComparatorToken t1 = a < tokens1.length ? tokens1[a] : null;
			ComparatorToken t2 = a < tokens2.length ? tokens2[a] : null;

			if (t1 == null) {
				return -1;
			} else if (t2 == null) {
				return 1;
			} else {
				int k = t1.compareTo(t2);
				if (k != 0)
					return k;
			}
		}
		return 0;
	}
}