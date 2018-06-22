/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This is a specialized pattern matching mechanism for common wildcard
 * conventions. This class will hopefully be more efficient and transparent than
 * the existing <code>java.util.regex.Pattern</code> and
 * <code>java.util.regex.Matcher</code> classes. (This class will not be as
 * flexible/powerful, though.)
 * 
 * <p>
 * The following description of wildcards is copied directly from <a
 * href="http://www.linfo.org/wildcard.html"
 * >http://www.linfo.org/wildcard.html</a>. The naming conventions in this class
 * follow these descriptions:
 * 
 * <h3>Star Wildcard</h3>
 * <p>
 * Three types of wildcards are used with Linux commands. The most frequently
 * employed and usually the most useful is the star wildcard, which is the same
 * as an asterisk (*). The star wildcard has the broadest meaning of any of the
 * wildcards, as it can represent zero characters, all single characters or any
 * string.
 * <p>
 * As an example, the file command provides information about any filesystem
 * object (i.e., file, directory or link) that is provided to it as an argument
 * (i.e., input). Because the star wildcard represents every string, it can be
 * used as the argument for file to return information about every object in the
 * specified directory. Thus, the following would display information about
 * every object in the current directory (i.e., the directory in which the user
 * is currently working):
 * 
 * <P>
 * <code>file *</code>
 * 
 * <p>
 * If there are no matches, an error message is returned, such as *: can't stat
 * `*' (No such file or directory).. In the case of this example, the only way
 * that there would be no matches is if the directory were empty.
 * 
 * <p>
 * Wildcards can be combined with other characters to represent parts of
 * strings. For example, to represent any filesystem object that has a .jpg
 * filename extension, *.jpg would be used. Likewise, a* would represent all
 * objects that begin with a lower case (i.e., small) letter a.
 * 
 * <p>
 * As another example, the following would tell the ls command (which is used to
 * list files) to provide the names of all files in the current directory that
 * have an .html or a .txt extension:
 * 
 * <p>
 * <code>ls *.html *.txt</code>
 * 
 * <p>
 * Likewise, the following would tell the rm command (which is used to remove
 * files and directories) to delete all files in the current directory that have
 * the string xxx in their name:
 * 
 * <p>
 * <code>rm *xxx*</code>
 * 
 * 
 * <h3>Question Mark Wildcard</h3>
 * 
 * <p>
 * The question mark (?) is used as a wildcard character in shell commands to
 * represent exactly one character, which can be any single character. Thus, two
 * question marks in succession would represent any two characters in
 * succession, and three question marks in succession would represent any string
 * consisting of three characters.
 * 
 * <p>
 * Thus, for example, the following would return data on all objects in the
 * current directory whose names, inclusive of any extensions, are exactly three
 * characters in length:
 * 
 * <p>
 * <code>file ???</code>
 * 
 * <p>
 * And the following would provide data on all objects whose names are one, two
 * or three characters in length:
 * 
 * <p>
 * <code>file ? ?? ???</code>
 * 
 * <p>
 * As is the case with the star wildcard, the question mark wildcard can be used
 * in combination with other characters. For example, the following would
 * provide information about all objects in the current directory that begin
 * with the letter a and are five characters in length:
 * 
 * <p>
 * <code>file a????</code>
 * 
 * <p>
 * The question mark wildcard can also be used in combination with other
 * wildcards when separated by some other character. For example, the following
 * would return a list of all files in the current directory that have a
 * three-character filename extension:
 * 
 * <p>
 * <code>ls *.???</code>
 * 
 * 
 * <h3>Square Brackets Wildcard</h3>
 * 
 * <p>
 * The third type of wildcard in shell commands is a pair of square brackets,
 * which can represent any of the characters enclosed in the brackets. Thus, for
 * example, the following would provide information about all objects in the
 * current directory that have an x, y and/or z in them:
 * 
 * <p>
 * <code>file *[xyz]*</code>
 * 
 * <p>
 * And the following would list all files that had an extension that begins with
 * x, y or z:
 * 
 * <p>
 * <code>ls *.[xyz]*</code>
 * 
 * <p>
 * The same results can be achieved by merely using the star and question mark
 * wildcards. However, it is clearly more efficient to use the bracket wildcard.
 * 
 * <p>
 * When a hyphen is used between two characters in the square brackets wildcard,
 * it indicates a range inclusive of those two characters. For example, the
 * following would provide information about all of the objects in the current
 * directory that begin with any letter from a through f:
 * 
 * <p>
 * <code>file [a-f]*</code>
 * 
 * <p>
 * And the following would provide information about every object in the current
 * directory whose name includes at least one numeral:
 * 
 * <p>
 * <code>file *[0-9]*</code>
 * 
 * <p>
 * The use of the square brackets to indicate a range can be combined with its
 * use to indicate a list. Thus, for example, the following would provide
 * information about all filesystem objects whose names begin with any letter
 * from a through c or begin with s or t:
 * 
 * <p>
 * <code>file [a-cst]*</code>
 * 
 * <p>
 * Likewise, multiple sets of ranges can be specified. Thus, for instance, the
 * following would return information about all objects whose names begin with
 * the first three or the final three lower case letters of the alphabet:
 * 
 * <p>
 * <code>file [a-cx-z]*</code>
 * 
 * <p>
 * Sometimes it can be useful to have a succession of square bracket wildcards.
 * For example, the following would display all filenames in the current
 * directory that consist of jones followed by a three-digit number:
 * 
 * <p>
 * <code>ls jones[0-9][0-9][0-9]</code>
 */
public class WildcardPattern {

	/** An element of a WildcardPattern. */
	public static abstract class Placeholder {
	}

	/**
	 * This placeholder is used for a non-wildcard character.
	 */
	public final static class FixedCharacter extends Placeholder {
		/** The character this placeholder represents. */
		public final char ch;

		FixedCharacter(char ch) {
			this.ch = Character.toLowerCase(ch);
		}

		@Override
		public String toString() {
			return Character.toString(ch);
		}
	}

	/**
	 * The star wildcard has the broadest meaning of any of the wildcards, as it
	 * can represent zero characters, all single characters or any string.
	 */
	public final static class StarWildcard extends Placeholder {
		@Override
		public String toString() {
			return "*";
		}
	}

	/**
	 * The SquareBracketsWildcard is used to represent one of a finite set of
	 * characters.
	 */
	public final static class SquareBracketsWildcard extends Placeholder {
		/**
		 * This is made public for efficiency of access: under no circumstances
		 * should you change the contents of this array.
		 */
		private char[] ch;

		SquareBracketsWildcard(char[] ch) {
			for (int a = 0; a < ch.length; a++) {
				ch[a] = Character.toLowerCase(ch[a]);
			}
			Arrays.sort(ch);
			this.ch = ch;
		}

		/**
		 * @param ch
		 *            the character to search for
		 * @return true if this wildcard can be used to represent the argument.
		 */
		public boolean contains(char ch) {
			int i = Arrays.binarySearch(this.ch, ch);
			return (i >= 0 && i < this.ch.length);
		}

		/**
		 * @return a copy of the list of characters this wildcard can represent.
		 */
		public char[] getChars() {
			char[] copy = new char[ch.length];
			System.arraycopy(ch, 0, copy, 0, ch.length);
			return copy;
		}

		@Override
		public String toString() {
			return "[" + (new String(ch)) + "]";
		}
	}

	/**
	 * The QuestionMarkWildcard is used to represent exactly one character.
	 */
	public final static class QuestionMarkWildcard extends Placeholder {
		@Override
		public String toString() {
			return "?";
		}
	}

	final Placeholder[] placeholders;
	final String patternText;

	/**
	 * Create a WildcardPattern.
	 * 
	 * @param patternText
	 *            the text this pattern is created from.
	 */
	public WildcardPattern(CharSequence patternText) {
		this.patternText = patternText.toString();
		try {
			placeholders = parse(patternText);
		} catch (RuntimeException e) {
			System.err.println("constructor failed: WildcardPattern(\""
					+ patternText + "\")");
			throw e;
		}
	}

	/**
	 * Return the original pattern text used to construct this WildcardPattern.
	 * 
	 * @return the original pattern text used to construct this WildcardPattern.
	 */
	public String getPatternText() {
		return patternText;
	}

	private Placeholder[] parse(CharSequence s) {
		List<Placeholder> list = new ArrayList<>(s.length());
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '*') {
				// two consecutive wildcards are meaningless, and screw up the
				// unit tests
				if (list.size() > 0
						&& list.get(list.size() - 1) instanceof StarWildcard)
					continue;
				list.add(new StarWildcard());
			} else if (ch == '?') {
				list.add(new QuestionMarkWildcard());
			} else if (ch == '[') {
				i++;
				ch = s.charAt(i);
				StringBuffer sb = new StringBuffer();
				while (ch != ']') {
					sb.append(ch);
					i++;
					ch = s.charAt(i);
				}
				list.add(parseSquareBracketWildcard(sb));
			} else {
				list.add(new FixedCharacter(ch));
			}
		}
		return list.toArray(new Placeholder[list.size()]);
	}

	private SquareBracketsWildcard parseSquareBracketWildcard(StringBuffer in) {
		SortedSet<Character> chars = new TreeSet<Character>();
		char lastChar = (char) -1;
		for (int a = 0; a < in.length(); a++) {
			char ch = in.charAt(a);
			if (ch == '-') {
				if (lastChar == -1)
					throw new IllegalArgumentException("could not parse \""
							+ in + "\"");
				a++;
				if (a >= in.length())
					throw new IllegalArgumentException("could not parse \""
							+ in + "\"");
				ch = in.charAt(a);
				for (char k = lastChar; k <= ch; k++) {
					chars.add(k);
				}
			} else {
				chars.add(ch);
				lastChar = ch;
			}
		}
		char[] array = new char[chars.size()];
		Iterator<Character> iter = chars.iterator();
		for (int a = 0; a < array.length; a++) {
			array[a] = iter.next().charValue();
		}
		return new SquareBracketsWildcard(array);
	}

	/** @return the number of Placeholder objects in this pattern. */
	public int getPlaceholderCount() {
		return placeholders.length;
	}

	/**
	 * @param index
	 *            the index of the Placeholder to retrieve.
	 * @return a specific Placeholder object in this pattern.
	 */
	public Placeholder getPlaceholder(int index) {
		return placeholders[index];
	}

	/** @return a copied list of the Placeholders in this pattern. */
	public Placeholder[] getPlaceholders() {
		Placeholder[] copy = new Placeholder[placeholders.length];
		System.arraycopy(placeholders, 0, copy, 0, placeholders.length);
		return copy;
	}

	/**
	 * @param string
	 *            the text to check against.
	 * @return true if the argument complies with this pattern.
	 */
	public boolean matches(CharSequence string) {
		Objects.requireNonNull(string);
		return matches(string, 0, string.length() - 1, placeholders, 0,
				placeholders.length - 1);
	}

	boolean matches(CharSequence string, int stringMinIndex,
			int stringMaxIndex, Placeholder[] placeholders,
			int placeholderMinIndex, int placeholderMaxIndex) {

		// this can happen if we're matching against an empty String
		if (stringMaxIndex < stringMinIndex) {
			if (placeholders.length == 1
					&& placeholders[0] instanceof StarWildcard)
				return true;

			// empty string + empty WildcardPattern? I guess that passes...
			if (placeholderMaxIndex < placeholderMinIndex)
				return true;

			return false;
		}

		if (placeholderMaxIndex < placeholderMinIndex)
			return false;

		// iterate forward, consume everything except stars:
		for (; stringMinIndex <= stringMaxIndex; stringMinIndex++) {
			if (placeholderMinIndex > placeholderMaxIndex) {
				// we have a character and we've exhausted the pattern:
				return false;
			}

			Placeholder p = placeholders[placeholderMinIndex];
			if (p instanceof FixedCharacter) {
				FixedCharacter fc = (FixedCharacter) p;
				char ch = string.charAt(stringMinIndex);
				ch = Character.toLowerCase(ch);
				if (ch != fc.ch)
					return false;
				placeholderMinIndex++;
			} else if (p instanceof QuestionMarkWildcard) {
				placeholderMinIndex++;
			} else if (p instanceof SquareBracketsWildcard) {
				SquareBracketsWildcard sbw = (SquareBracketsWildcard) p;
				char ch = string.charAt(stringMinIndex);
				ch = Character.toLowerCase(ch);
				if (sbw.contains(ch) == false)
					return false;
				placeholderMinIndex++;
			} else {
				break;
			}

			if (stringMinIndex == stringMaxIndex) {
				if (placeholderMinIndex > placeholderMaxIndex) {
					// we exhausted the string and pattern together:
					return true;
				} else if (placeholderMinIndex == placeholderMaxIndex
						&& placeholders[placeholderMinIndex] instanceof StarWildcard) {
					// we exhausted the strig and the only remaining pattern is
					// a star
					return true;
				} else {
					// we exhausted the String, but there's still pattern
					// leftover (that isn't a star)
					return false;
				}
			}
		}

		// if we reached this point: that means we hit an asterisk scanning from
		// left-to-right. Now we'll scan from right-to-left:

		for (; stringMaxIndex >= stringMinIndex; stringMaxIndex--) {
			if (placeholderMaxIndex < placeholderMinIndex) {
				// we have a character and we've exhausted the pattern:
				return false;
			}

			Placeholder p = placeholders[placeholderMaxIndex];
			if (p instanceof FixedCharacter) {
				FixedCharacter fc = (FixedCharacter) p;
				char ch = string.charAt(stringMaxIndex);
				ch = Character.toLowerCase(ch);
				if (ch != fc.ch)
					return false;
				placeholderMaxIndex--;
			} else if (p instanceof QuestionMarkWildcard) {
				placeholderMaxIndex--;
			} else if (p instanceof SquareBracketsWildcard) {
				SquareBracketsWildcard sbw = (SquareBracketsWildcard) p;
				char ch = string.charAt(stringMaxIndex);
				ch = Character.toLowerCase(ch);
				if (sbw.contains(ch) == false)
					return false;
				placeholderMaxIndex--;
			} else {
				break;
			}

			if (placeholderMaxIndex < placeholderMinIndex
					&& stringMinIndex == stringMaxIndex) {
				// exact match, both the string and pattern are exhausted
				return true;
			}
		}

		// now we've hit an asterisk scanning from right-to-left.

		if (placeholderMaxIndex == placeholderMinIndex) {
			// there's only one asterisk, so that means anything/everything
			// passes.
			return true;
		}

		for (; stringMinIndex <= stringMaxIndex; stringMinIndex++) {
			if (matches(string, stringMinIndex, stringMaxIndex, placeholders,
					placeholderMinIndex + 1, placeholderMaxIndex))
				return true;
		}

		return false;
	}

	/**
	 * @return the maximum number of characters in this pattern. This may return
	 *         Integer.MAX_VALUE if this pattern contains an asterisk.
	 */
	public int getMaximumLength() {
		if (containsStarWildcard())
			return Integer.MAX_VALUE;
		return placeholders.length;
	}

	private Boolean containsStarWildcard;

	/**
	 * @return whether this pattern contains a StarWildcard.
	 *         <p>
	 *         The StarWildcard may be considerably more complicated to
	 *         evaluate, so other objects evaluating this pattern might often be
	 *         interested in knowing this.
	 */
	public boolean containsStarWildcard() {
		if (containsStarWildcard == null) {
			for (int a = 0; a < placeholders.length
					&& (containsStarWildcard == null); a++) {
				if (placeholders[a] instanceof StarWildcard) {
					containsStarWildcard = Boolean.TRUE;
				}
			}
			if (containsStarWildcard == null)
				containsStarWildcard = Boolean.FALSE;
		}
		return containsStarWildcard;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(placeholders.length);
		sb.append("WildcardPattern[ \"");
		for (int a = 0; a < placeholders.length; a++) {
			sb.append(placeholders[a]);
		}
		sb.append("\" ]");
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return patternText.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WildcardPattern))
			return false;
		WildcardPattern p = (WildcardPattern) obj;
		return p.getPatternText().equals(getPatternText());
	}

	/**
	 * Return all the elements of the argument that comply with this pattern.
	 * <p>
	 * This should be functionally equivalent to iterating over every element of
	 * the set and checking its compliance, but depending on the composition of
	 * this pattern: this method may be faster than if you iterate over all
	 * elements directly.
	 * 
	 * @param set
	 * @param string
	 * @return
	 */
	public SortedSet<String> getMatches(TreeSet<String> set) {
		SortedSet<String> returnValue = new TreeSet<>();
		Placeholder[] phs = getPlaceholders();

		StringBuilder constantPrefixBuilder = new StringBuilder();
		for (int a = 0; a < phs.length; a++) {
			if (phs[a] instanceof FixedCharacter) {
				constantPrefixBuilder.append(phs[a].toString());
			} else {
				break;
			}
		}
		String constantPrefix = constantPrefixBuilder.toString();

		for (String s : set.tailSet(constantPrefix, true)) {
			if (matches(s))
				returnValue.add(s);
			if (!s.startsWith(constantPrefix))
				break;
		}

		return returnValue;
	}
}