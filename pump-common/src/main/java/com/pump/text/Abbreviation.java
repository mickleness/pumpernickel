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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.pump.text.TextDiff.DiffSegment;

/**
 * Static helper methods related to abbreviation.
 *
 */
public class Abbreviation {

	/**
	 * Return true if it is reasonable to assume strA and strB are equivalent
	 * because of the use of abbreviations. This assumes the Strings are
	 * expressed in English.
	 * <p>
	 * For example if this compares "agri department" with "agriculture dept" it
	 * will return true. Note neither String is strictly an abbreviation of the
	 * other, but if you split them into individual words: it can be said that
	 * each word is an abbreviation of the other.
	 * <p>
	 * This strips out small helper words (like "a", "the", "of") in this
	 * comparison, and looks for common prefixes and suffixes (while "important"
	 * is a subset of "unimportant", the former shouldn't be considered an
	 * abbreviation of the latter).
	 * <p>
	 * This method is case insensitive, may also strip out some punctuation
	 * characters.
	 * 
	 * @param diff
	 *            an optional TextDiff. Each TextDiff object caches results, so
	 *            if you have frequent (and finite) Strings to compare, you may
	 *            want to reuse the same TextDiff object. Otherwise one is
	 *            created and destroyed.
	 * @param strA
	 *            the first string to compare.
	 * @param strB
	 *            the second string to compare.
	 * @return
	 */
	public static boolean isAbbreviation(TextDiff diff, String strA,
			String strB, int requiredSharedChars) {
		if (diff == null)
			diff = new TextDiff();

		List<String> wordsA = replaceWords(getMeaningfulWords(strA
				.toLowerCase()));
		List<String> wordsB = replaceWords(getMeaningfulWords(strB
				.toLowerCase()));
		if (wordsA.size() != wordsB.size()) {
			return false;
		}
		// first do a fast pass to abort:
		for (int a = 0; a < wordsA.size(); a++) {
			String wordA = wordsA.get(a);
			String wordB = wordsB.get(a);
			if (wordA.equals(wordB)) {
				continue;
			}
			if (wordA.length() == wordB.length()) {
				return false;
			}
		}

		// slower pass for scrutiny
		int sharedChars = 0;
		for (int a = 0; a < wordsA.size(); a++) {
			String wordA = wordsA.get(a);
			String wordB = wordsB.get(a);
			if (wordA.equals(wordB)) {
				continue;
			} else {
				DiffSegment solution = diff.getSubset(wordA, wordB);
				if (solution != null
						&& (!solution.containsSplit(SUFFIXES_AND_PREFIXES))) {
					sharedChars += solution.getSharedCharCount();
				} else {
					solution = diff.getSubset(wordB, wordA);
					if (solution != null
							&& (!solution.containsSplit(SUFFIXES_AND_PREFIXES))) {
						sharedChars += solution.getSharedCharCount();
					} else {
						return false;
					}
				}
			}
		}

		return sharedChars > requiredSharedChars;
	}

	private static List<String> replaceWords(List<String> words) {
		for (int a = 0; a < words.size(); a++) {
			String word = words.get(a);
			String replacement = REPLACEMENT_WORDS.get(word);
			if (replacement != null)
				words.set(a, replacement);
		}
		return words;
	}

	/**
	 * A collection of small (filler) words you can strip away from
	 * titles/sentences without losing much meaning/context.
	 */
	public static final Collection<String> SMALL_WORDS = Arrays.asList("in",
			"of", "a", "an", "the", "for", "with");

	public static final Map<String, String> REPLACEMENT_WORDS = Collections
			.unmodifiableMap(createReplacementWordMap());

	public static final Collection<String> SUFFIXES = Arrays.asList("able",
			"ible", "al", "ial", "ed", "en", "er", "est", "ful", "ic", "ing",
			"ion", "tion", "ation", "ition", "ity", "ty", "ive", "ative",
			"itive", "less", "ly", "ment", "ness", "ous", "eous", "ious", "y");
	public static final Collection<String> PREFIXES = Arrays.asList("anti",
			"de", "dis", "en", "em", "fore", "in", "im", "il", "ir", "inter",
			"mid", "mis", "non", "over", "pre", "re", "semi", "sub", "super",
			"trans", "un", "under");

	public static final Collection<String> SUFFIXES_AND_PREFIXES = createSuffixesAndPrefixes();

	/**
	 * This collapses white space, identifies alphanumeric chars, and removes
	 * small words like "a", "of" or "the" (words you wouldn't capitalize in a
	 * book title).
	 * 
	 * @param str
	 *            the characters to extract words from.
	 * @return the list of words extracted from the argument.
	 */
	public static List<String> getMeaningfulWords(CharSequence str) {
		List<String> returnValue = new ArrayList<>();
		StringBuffer sb = new StringBuffer();
		for (int a = 0; a < str.length(); a++) {
			char ch = str.charAt(a);
			if (Character.isWhitespace(ch)) {
				if (sb.length() > 0) {
					returnValue.add(sb.toString());
					sb.delete(0, sb.length());
				}
			} else if (Character.isLetterOrDigit(ch) || ch == '&') {
				sb.append(ch);
			}
		}
		if (sb.length() > 0) {
			returnValue.add(sb.toString());
		}

		returnValue.removeAll(SMALL_WORDS);

		return returnValue;
	}

	private static Collection<String> createSuffixesAndPrefixes() {
		HashSet<String> returnValue = new HashSet<>();
		returnValue.addAll(SUFFIXES);
		returnValue.addAll(PREFIXES);
		return returnValue;
	}

	private static Map<String, String> createReplacementWordMap() {
		Map<String, String> returnValue = new HashMap<>();
		returnValue.put("&", "and");
		returnValue.put("1", "one");
		returnValue.put("2", "two");
		returnValue.put("3", "three");
		returnValue.put("4", "four");
		returnValue.put("5", "five");
		returnValue.put("6", "six");
		returnValue.put("7", "seven");
		returnValue.put("8", "eight");
		returnValue.put("9", "nine");
		returnValue.put("10", "ten");
		returnValue.put("1st", "first");
		returnValue.put("2nd", "second");
		returnValue.put("3rd", "third");
		returnValue.put("4th", "fourth");
		returnValue.put("5th", "fifth");
		returnValue.put("6th", "sixth");
		returnValue.put("7th", "seventh");
		returnValue.put("8th", "eighth");
		returnValue.put("9th", "ninth");
		returnValue.put("10th", "tenth");
		return returnValue;
	}
}