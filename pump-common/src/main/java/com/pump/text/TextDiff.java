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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

/**
 * This object can diff two Strings together to find the minimal amount of
 * changes between the two.
 * <p>
 * Note this is only intended for small strings, and depending on the similarity
 * and length of the Strings: this can be very expensive. Because it is
 * expensive: this object caches all requested diffs for cheap retrieval. So
 * periodically you may want to replace your {@code TextDiff} object to avoid
 * store large sets of Strings and diffs in memory.
 * <p>
 * This class can best be described by examples, so let's consider how this
 * diffs the two strings: "antigravity" and "gravity field". The simplest result
 * this identifies is: "[anti]gravity{ field}". A visualization of this result
 * resembles:
 * <p>
 * <img src=
 * "data:image/png;base64,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"
 * alt="[anti]gravity{ field}">
 * <p>
 * You can either follow the red arrows (branch A, using square brackets) to
 * create the first String, or you can follow the blue arrows (branch B, using
 * curly brackets) to create the second String.
 * <p>
 * Each of these columns is a different type of {@link TextDiff.DiffSegment}
 * object:
 * <ul>
 * <li>{@link TextDiff.SharedSegment}: clusters of letters that belong to both
 * of the Strings/branches. In the example above this is the middle column.</li>
 * <li>{@link TextDiff.SplitSegment}: clusters of letters that belong
 * exclusively to branch A or B. In the example above this is the leftmost and
 * rightmost column.</li>
 * </ul>
 * <p>
 * <p>
 * One of the reasons that calculating these diffs is so expensive is that there
 * may be several unusual ways to process this data. For example, this is
 * another solution to the example above: "[an]{gravi}t[igrav]{y f}i[ty]{eld}":
 * <p>
 * <img src=
 * "data:image/png;base64,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"
 * alt="[an]{gravi}t[igrav]{y f}i[ty]{eld}">
 * <p>
 * There are actually 38 solutions for this example. There are 74 solutions for
 * diffing "agri department" against "agriculture dept". But in both cases:
 * there is exactly one solution that is considered the simplest, so other
 * solutions are discarded.
 * <p>
 * Each solution is represented as a single {@code DiffSegment} object. A
 * {@code DiffSegment} is actually a linked list, and you can traverse the
 * {@code next()} and {@code previous()} methods to walk the list from beginning
 * to end.
 */
public class TextDiff {

	/** A designation of which branch (A or B) you're interested in examining. */
	public static enum Branch {
		/**
		 * The first possible way to read a DiffSegment graph. This is denoted
		 * with square brackets.
		 */
		A,
		/**
		 * The second possible way to read a DiffSegment graph. This is denoted
		 * with curly brackets.
		 */
		B;

		/**
		 * Returns the opposite branch.
		 * 
		 * @return the opposite branch.
		 */
		public Branch opposite() {
			for (Branch b : Branch.values()) {
				if (b != this) {
					return b;
				}
			}
			throw new RuntimeException("Unexpected condition");
		}
	};

	/**
	 * This represents a segment of a diff solution.
	 * <p>
	 * This abstract class has two subclasses: the {@code SharedSegment} is a
	 * String that is shared between both branch A and B, and the {code
	 * SplitSegment} is two Strings that deviate depending on which branch you're
	 * following.
	 * <p>
	 * This is illustrated in the {@link TextDiff} javadoc with diagrams. Each
	 * column in those diagrams is a DiffSegment object.
	 * <p>
	 * This object is a node in a linked list. Originally you are given the head
	 * of the list as the complete solution, and you can call {@code next()} to
	 * traverse to the tail of this list.
	 * <p>
	 * If this is put into a sorted data structure, these segments are sorted by
	 * complexity (with the least complex graph sorted first).
	 */
	public abstract static class DiffSegment implements Comparable<DiffSegment> {

		protected DiffSegment next;
		protected DiffSegment prev;
		protected int complexity = -1;

		/**
		 * Return the next node in this list, or null if this is the tail node.
		 * 
		 * @return the next node in this list, or null if this is the tail node.
		 */
		public DiffSegment next() {
			return next;
		}

		/**
		 * Return the previous node in this list, or null if this is the head
		 * node.
		 * 
		 * @return the previous node in this list, or null if this is the head
		 *         node.
		 */
		public DiffSegment previous() {
			return prev;
		}

		/**
		 * Return the sum of the length of all {@code SharedSegments}.
		 * 
		 * @return the sum of the length of all {@code SharedSegments}.
		 */
		public int getSharedCharCount() {
			int sum = 0;
			DiffSegment t = this;
			while (t != null) {
				if (t instanceof SharedSegment) {
					sum += ((SharedSegment) t).text.length();
				}
				t = t.next;
			}
			return sum;
		}

		/**
		 * Return -1 if this graph is less complex than the argument.
		 * 
		 * @return -1 if this graph is less complex than the argument.
		 */
		@Override
		public int compareTo(DiffSegment o) {
			int myComplexity = getComplexity();
			int otherComplexity = o.getComplexity();
			if (myComplexity < otherComplexity) {
				return -1;
			} else if (myComplexity > otherComplexity) {
				return 1;
			}
			// it doesn't matter what we do here, as long as its
			// consistent. My first draft just used:
			// toString().compareTo(o.toString())
			// this worked, but was several times slower than the
			// approach below that helps cut the comparison short
			// much sooner.
			int type1 = this instanceof SharedSegment ? 1 : 0;
			int type2 = o instanceof SharedSegment ? 1 : 0;
			if (type1 == 1 && type2 == 0) {
				return -1;
			} else if (type1 == 0 && type2 == 1) {
				return 1;
			} else if (type1 == 1 && type2 == 1) {
				SharedSegment s1 = (SharedSegment) this;
				SharedSegment s2 = (SharedSegment) o;
				int k = s1.text.compareTo(s2.text);
				if (k != 0) {
					return k;
				}
			} else if (type1 == 0 && type2 == 0) {
				SplitSegment s1 = (SplitSegment) this;
				SplitSegment s2 = (SplitSegment) o;
				int k = s1.textA.compareTo(s2.textA);
				if (k != 0) {
					return k;
				}
				k = s1.textB.compareTo(s2.textB);
				if (k != 0) {
					return k;
				}
			}

			if (next == null && o.next == null) {
				return 0;
			} else if (next == null) {
				return -1;
			} else if (o.next == null) {
				return 1;
			} else {
				return next.compareTo(o.next);
			}
		}

		/**
		 * Clone this node and all its subsequent nodes.
		 */
		public DiffSegment clone() {
			return clone(false);
		}

		/**
		 * Clone this node and all its subsequent nodes.
		 * 
		 * @param swapBranches
		 *            if true then branch A and B should swap as we clone this
		 *            node.
		 */
		public abstract DiffSegment clone(boolean swapBranches);

		/**
		 * Return the text of this node.
		 * 
		 * @param branch
		 *            the branch of this node to consider. For a
		 *            {@code SharedSegment} this argument is ignored, but for a
		 *            {@code SplitSegment} this argument controls which String
		 *            is returned.
		 * @param appendSubsequentNodes
		 *            if true then this appends all subsequent nodes. This is
		 *            useful if just want the whole String of a branch. If you
		 *            need to study individual pieces more carefully then you
		 *            may want to pass false here.
		 * @return the text of this node. This returns the raw text with no
		 *         quotations or square/curly brackets.
		 */
		public abstract String getText(Branch branch,
				boolean appendSubsequentNodes);

		/**
		 * Return the tail of this linked list.
		 * 
		 * @return the tail of this linked list.
		 */
		public DiffSegment getTail() {
			DiffSegment t = this;
			while (t.next != null) {
				t = t.next;
			}
			return t;
		}

		/**
		 * Clone this node and append one character to both branches.
		 * 
		 * @param ch
		 *            the character to append.
		 * @return the new node that represents this node + the argument.
		 */
		public DiffSegment append(char ch) {
			DiffSegment clone = clone();
			DiffSegment tail = clone.getTail();
			if (tail instanceof SharedSegment) {
				SharedSegment oldTail = (SharedSegment) tail;
				SharedSegment newTail = new SharedSegment(oldTail.text + ch);

				DiffSegment nextToLast = oldTail.prev;
				if (nextToLast != null) {
					nextToLast.next = newTail;
					newTail.prev = nextToLast;
				} else {
					clone = newTail;
				}
			} else {
				SharedSegment newTail = new SharedSegment(ch);
				tail.next = newTail;
				newTail.prev = tail.next;
			}
			return clone;
		}

		/**
		 * Clone this node and append the two Strings provided to each
		 * respective branch.
		 * <p>
		 * Warning: some splits are considered "unnecessary", and are rejected.
		 * When an unnecessary split is detected this method returns null. For
		 * example the diff "[h]{h}ello" is rejected, because when the first
		 * characters of a split are the same: we have a redundant/unnecessary
		 * split.
		 * 
		 * @param strA
		 *            the String to append to branch A.
		 * @param strB
		 *            the String to append to branch B.
		 * @return the new node that represents this node + the arguments or
		 *         null if this split is not allowed.
		 */
		public DiffSegment append(String strA, String strB) {
			identifyRedundantOp: {
				DiffSegment origTail = getTail();
				if (origTail instanceof SplitSegment) {
					SplitSegment tail = (SplitSegment) origTail;
					char chA, chB;
					if (tail.textA.length() == 0 && strA.length() > 0) {
						chA = strA.charAt(0);
					} else if (tail.textA.length() > 0) {
						chA = tail.textA.charAt(0);
					} else {
						break identifyRedundantOp;
					}
					if (tail.textB.length() == 0 && strB.length() > 0) {
						chB = strB.charAt(0);
					} else if (tail.textB.length() > 0) {
						chB = tail.textB.charAt(0);
					} else {
						break identifyRedundantOp;
					}
					if (chA == chB) {
						return null;
					}
				}
			}

			DiffSegment clone = clone();
			DiffSegment tail = clone.getTail();
			if (tail instanceof SplitSegment) {
				SplitSegment oldTail = (SplitSegment) tail;
				SplitSegment newTail = new SplitSegment(oldTail.textA + strA,
						oldTail.textB + strB);

				DiffSegment nextToLast = oldTail.prev;
				if (nextToLast != null) {
					nextToLast.next = newTail;
					newTail.prev = nextToLast;
				} else {
					clone = newTail;
				}
			} else {
				SplitSegment newTail = new SplitSegment(strA, strB);
				tail.next = newTail;
				newTail.prev = tail.next;
			}
			return clone;
		}

		/**
		 * Return the complexity of this segment. Lower values are considered
		 * simpler and therefore "better" when pruning solutions.
		 * 
		 * @return the complexity of this segment.
		 */
		public int getComplexity() {
			if (complexity == -1) {
				complexity = calculateComplexity();
			}
			return complexity;
		}

		protected abstract int calculateComplexity();

		/**
		 * Return true if branch x is a subset of the opposite branch.
		 * <p>
		 * For example if string A is "underwater" and string B is "water", then
		 * the diff of these will be "[under]water". So calling
		 * {@code myDiff.isSubset(Branch.B)} will return true, because the B
		 * branch ("water") is a subset of the A branch ("underwater").
		 * <p>
		 * However if string A is "underwater" and string B is "watered", then
		 * the diff will be "[under]water{ed}". Here calling
		 * {@code isSubset(..)} for either branch returns false, because neither
		 * is a subset of the other.
		 * <p>
		 * The examples above are trivial because they cluster all the words
		 * together, but the utility of this method has to do with
		 * abbreviations. For example given the strings "association" and
		 * "assn": the second branch is considered a subset of the first, even
		 * though the diff resembles "ass[ociatio]n".
		 * <p>
		 * As a word of caution: this is helpful at identifying abbreviations,
		 * but it can also be misleading. For example "ne" is a subset of
		 * "northeast" (as expected), but "ne" is <i>also</i> a subset of
		 * "northwest". Similarly "intl" is a subset of "international", but it
		 * is also a subset of "intelligence". And "cm" is a subset of
		 * "centimeter", but it also a subset of "camp".
		 * 
		 * @param b
		 *            the branch that might be a subset of the opposite branch.
		 * @return true if the argument branch is a subset of the opposite
		 *         branch.
		 */
		public boolean isSubset(Branch x) {
			DiffSegment t = this;
			int droppedCharCount = 0;
			while (t != null) {
				if (t instanceof SplitSegment) {
					SplitSegment s = (SplitSegment) t;
					String text = s.getText(x, false);
					if (text.equals("")) {
						droppedCharCount += s.getText(x.opposite(), false)
								.length();
					} else {
						return false;
					}
				}
				t = t.next;
			}
			return droppedCharCount > 0;
		}

		/**
		 * Return true if this graph contains a split where one of the words is
		 * in the argument provided.
		 * 
		 * @param values
		 *            a collection of Strings to check against.
		 * @return true if this graph contains a split that contains text from
		 *         the argument.
		 */
		public boolean containsSplit(Collection<String> values) {
			DiffSegment t = this;
			while (t != null) {
				if (t instanceof SplitSegment) {
					SplitSegment s = (SplitSegment) t;
					if (values.contains(s.textA) || values.contains(s.textB)) {
						return true;
					}
				}
				t = t.next;
			}
			return false;
		}
	}

	/** This segment represents the same text for both branches. */
	public static class SharedSegment extends DiffSegment {

		/** The text this segment represents. */
		public final String text;

		private SharedSegment(char ch) {
			this(Character.toString(ch));
		}

		@Override
		public int hashCode() {
			return text.hashCode();
		}

		@Override
		protected int calculateComplexity() {
			int c = text.length();
			if (next != null) {
				c += next.getComplexity();
			}
			return c;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SharedSegment)) {
				return false;
			}
			SharedSegment other = (SharedSegment) obj;
			if (!text.equals(other.text)) {
				return false;
			} else if (next != null && other.next != null) {
				return next.equals(other.next);
			} else if (next != null || other.next != null) {
				return false;
			}
			return true;
		}

		private SharedSegment(String text) {
			if (text == null)
				throw new NullPointerException();
			this.text = text;
		}

		@Override
		public String getText(Branch branch, boolean appendSubsequentNodes) {
			String returnValue = text;
			if (appendSubsequentNodes && next != null) {
				returnValue += next.getText(branch, appendSubsequentNodes);
			}
			return returnValue;
		}

		@Override
		public String toString() {
			String returnValue = text;
			if (next != null) {
				returnValue += next.toString();
			}
			return returnValue;
		}

		@Override
		public SharedSegment clone(boolean swapBranches) {
			SharedSegment clone = new SharedSegment(text);
			if (next != null) {
				clone.next = next.clone(swapBranches);
				clone.next.prev = clone;
			}
			return clone;
		}
	}

	/**
	 * This segment represents two unique Strings depending on whether you're
	 * reading branch A or B.
	 */
	public static class SplitSegment extends DiffSegment {

		/** The text that should be applied to branch A. */
		public final String textA;

		/** The text that should be applied to branch B. */
		public final String textB;

		private SplitSegment(String textA, String textB) {
			if (textA == null)
				throw new NullPointerException();
			if (textB == null)
				throw new NullPointerException();
			if (textA.length() == 0 && textB.length() == 0)
				throw new IllegalArgumentException(
						"At least one branch must have text.");
			this.textA = textA;
			this.textB = textB;

		}

		@Override
		protected int calculateComplexity() {
			int c = textA.length() + textB.length() + 1;
			if (next != null) {
				c += next.getComplexity();
			}
			return c;
		}

		@Override
		public int hashCode() {
			return textA.hashCode() ^ textB.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof SplitSegment)) {
				return false;
			}
			SplitSegment other = (SplitSegment) obj;
			if (!textA.equals(other.textA)) {
				return false;
			} else if (!textB.equals(other.textB)) {
				return false;
			} else if (next != null && other.next != null) {
				return next.equals(other.next);
			} else if (next != null || other.next != null) {
				return false;
			}
			return true;
		}

		@Override
		public SplitSegment clone(boolean swapBranches) {
			SplitSegment clone;
			if (swapBranches) {
				clone = new SplitSegment(textB, textA);
			} else {
				clone = new SplitSegment(textA, textB);
			}
			if (next != null) {
				clone.next = next.clone(swapBranches);
				clone.next.prev = clone;
			}
			return clone;
		}

		@Override
		public String getText(Branch branch, boolean appendSubsequentNodes) {
			if (branch == null)
				throw new NullPointerException();

			String returnValue;
			if (branch == Branch.A) {
				returnValue = textA;
			} else {
				returnValue = textB;
			}
			if (appendSubsequentNodes && next != null) {
				returnValue += next.getText(branch, appendSubsequentNodes);
			}
			return returnValue;
		}

		@Override
		public String toString() {
			String returnValue;
			if (textA.length() == 0 && textB.length() == 0) {
				// weird, but let's not make a stink here
				returnValue = "";
			} else if (textA.length() == 0) {
				returnValue = "{" + textB + "}";
			} else if (textB.length() == 0) {
				returnValue = "[" + textA + "]";
			} else {
				returnValue = "[" + textA + "]{" + textB + "}";
			}
			if (next != null) {
				returnValue += next.toString();
			}
			return returnValue;
		}
	}

	static class Key {
		final String strA, strB;
		final boolean includeAllSolutions;

		Key(String strA, String strB, boolean includeAllSolutions) {
			this.strA = strA;
			this.strB = strB;
			this.includeAllSolutions = includeAllSolutions;
		}

		@Override
		public int hashCode() {
			return strA.hashCode() + strB.hashCode()
					+ (includeAllSolutions ? 1 : 0);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Key)) {
				return false;
			}
			Key other = (Key) obj;
			return strA.equals(other.strA) && strB.equals(other.strB)
					&& includeAllSolutions == other.includeAllSolutions;
		}

		@Override
		public String toString() {
			return "\"" + strA + "\" \"" + strB + "\" includeAllSolutions="
					+ includeAllSolutions;
		}
	}

	protected Map<Key, Collection<DiffSegment>> cachedResults = new HashMap<>();

	public TextDiff() {
	}

	/**
	 * Return the simplest graphs explaining the diff between strA and strB.
	 * <p>
	 * If this query is already cached, then that cached value is immediately
	 * returned. Otherwise this method calculates the diff and caches the
	 * results in this object for future retrieval.
	 * 
	 * @param strA
	 *            the first String/branch to compare.
	 * @param strB
	 *            the second String/branch to compare.
	 * @return the simplest graphs explaining the diff between strA and strB.
	 */
	public DiffSegment[] getSolutions(String strA, String strB) {
		return getSolutions(strA, strB, false);
	}

	/**
	 * Return the graphs explaining the diff between strA and strB.
	 * <p>
	 * If this query is already cached, then that cached value is immediately
	 * returned. Otherwise this method calculates the diff and caches the
	 * results in this object for future retrieval.
	 * 
	 * @param strA
	 *            the first String/branch to compare.
	 * @param strB
	 *            the second String/branch to compare.
	 * @param includeAllSolutions
	 *            if true then this includes ALL solutions, including overly
	 *            complex solutions. If false (which is recommended) then this
	 *            method only includes the simplest solutions. If all solutions
	 *            are included: this query can be several times slower.
	 * @return the graphs explaining the diff between strA and strB.
	 */
	public DiffSegment[] getSolutions(String strA, String strB,
			boolean includeAllSolutions) {
		boolean reverse = strA.compareTo(strB) > 0;
		String tA, tB;
		if (reverse) {
			tA = strB;
			tB = strA;
		} else {
			tA = strA;
			tB = strB;
		}
		Key key = new Key(tA, tB, includeAllSolutions);
		Collection<DiffSegment> solutions = cachedResults.get(key);
		if (solutions == null) {
			solutions = includeAllSolutions ? new TreeSet<DiffSegment>()
					: new HashSet<DiffSegment>();
			calculateSolutions(solutions, null, key, 0, 0, new Complexity());
			cachedResults.put(key, solutions);
		}

		DiffSegment[] returnValue = new DiffSegment[solutions.size()];
		if (reverse) {
			int a = 0;
			for (DiffSegment s : solutions) {
				returnValue[a] = s.clone(true);
				a++;
			}
		} else {
			solutions.toArray(returnValue);
		}
		return returnValue;
	}

	/**
	 * Return one of the simplest graphs explaining the diff between strA and
	 * strB.
	 * <p>
	 * If this query is already cached, then that cached value is immediately
	 * returned. Otherwise this method calculates the diff and caches the
	 * results in this object for future retrieval.
	 * 
	 * @param strA
	 *            the first String/branch to compare.
	 * @param strB
	 *            the second String/branch to compare.
	 * @return one of the simplest graphs explaining the diff between strA and
	 *         strB.
	 */
	public DiffSegment getSolution(String strA, String strB) {
		boolean reverse = strA.compareTo(strB) > 0;
		String tA, tB;
		if (reverse) {
			tA = strB;
			tB = strA;
		} else {
			tA = strA;
			tB = strB;
		}
		Key key = new Key(tA, tB, false);
		Collection<DiffSegment> solutions = cachedResults.get(key);
		if (solutions == null) {
			solutions = new HashSet<>();
			calculateSolutions(solutions, null, key, 0, 0, new Complexity());
			cachedResults.put(key, solutions);
		}

		DiffSegment returnValue = solutions.iterator().next();
		if (reverse) {
			returnValue = returnValue.clone(true);
		}

		return returnValue;
	}

	/**
	 * This is a trivial wrapper for an int value representing the minimum
	 * complexity of a query.
	 */
	private static class Complexity {
		public int value = Integer.MAX_VALUE;
	}

	/**
	 * This searches for ways to diff strA and strB, and when a solution is
	 * found this calls
	 * {@link #processSolution(Collection,DiffSegment,Key,Complexity)}.
	 * 
	 * @param results
	 *            the object to store results in
	 * @param path
	 *            the base (starting) path to append data to.
	 * @param key
	 *            the key (which includes strA and strB) that describes this
	 *            query.
	 * @param indexA
	 *            the index of strA to begin reading.
	 * @param indexB
	 *            the index of strB to begin reading.
	 * @param minComplexity
	 *            a wrapper for the minimum complexity detected during this
	 *            scan. If we don't have to return all solutions then as branch
	 *            attempts pass this complexity they immediately abort.
	 */
	private void calculateSolutions(Collection<DiffSegment> results,
			DiffSegment path, Key key, int indexA, int indexB,
			Complexity minComplexity) {

		if ((!key.includeAllSolutions) && path != null
				&& path.getComplexity() > minComplexity.value)
			return;

		if (key.strA.length() - indexA == 0 && key.strB.length() - indexB == 0) {
			processSolution(results, path, key, minComplexity);
		} else if (key.strA.length() - indexA == 0
				|| key.strB.length() - indexB == 0) {
			String remainingA = key.strA.subSequence(indexA, key.strA.length())
					.toString();
			String remainingB = key.strB.subSequence(indexB, key.strB.length())
					.toString();
			if (path == null) {
				processSolution(results, new SplitSegment(remainingA,
						remainingB), key, minComplexity);
			} else {
				DiffSegment copy = path.append(remainingA, remainingB);
				if (copy != null)
					processSolution(results, copy, key, minComplexity);
			}
		} else {
			char ch1 = key.strA.charAt(indexA);
			char ch2 = key.strB.charAt(indexB);
			// great! create a special branch with this shared character:
			if (ch1 == ch2) {
				DiffSegment newPath;
				if (path == null) {
					newPath = new SharedSegment(ch1);
				} else {
					newPath = path.append(ch1);
				}

				calculateSolutions(results, newPath, key, indexA + 1,
						indexB + 1, minComplexity);
			}

			// take ch1 and create a special branch:
			{
				DiffSegment newPath;
				if (path == null) {
					newPath = new SplitSegment(Character.toString(ch1), "");
				} else {
					newPath = path.append(Character.toString(ch1), "");
				}
				if (newPath != null) {
					calculateSolutions(results, newPath, key, indexA + 1,
							indexB, minComplexity);
				}
			}

			// take ch2 and create a special branch:
			{
				DiffSegment newPath;
				if (path == null) {
					newPath = new SplitSegment("", Character.toString(ch2));
				} else {
					newPath = path.append("", Character.toString(ch2));
				}
				if (newPath != null) {
					calculateSolutions(results, newPath, key, indexA,
							indexB + 1, minComplexity);
				}
			}
		}
	}

	/**
	 * This chooses to record the results in the collection provided or discard
	 * it.
	 * <p>
	 * If includeAllSolutions is true, then this solution is always recorded.
	 * Otherwise this solution is only recorded if the complexity is the lowest
	 * complexity yet detected.
	 * 
	 * @param results
	 *            the collection to store the solution in if this method decides
	 *            to keep it.
	 * @param solution
	 *            the new solution to process.
	 * @param key
	 *            the key describing the query being made.
	 */
	protected void processSolution(Collection<DiffSegment> results,
			DiffSegment solution, Key key, Complexity minComplexity) {
		boolean isValid = true;
		DiffSegment tail = solution.getTail();
		if (tail instanceof SplitSegment) {
			SplitSegment tailSplit = (SplitSegment) tail;

			if (tailSplit.textA.length() > 0 && tailSplit.textB.length() > 0) {
				char chA = tailSplit.textA.charAt(tailSplit.textA.length() - 1);
				char chB = tailSplit.textB.charAt(tailSplit.textB.length() - 1);
				if (chA == chB) {
					isValid = false;
				}
			}
		}
		if (!isValid)
			return;

		int complexity = solution.getComplexity();
		if (key.includeAllSolutions) {
			results.add(solution);
		} else {
			if ((!key.includeAllSolutions) && complexity < minComplexity.value) {
				results.clear();
				minComplexity.value = complexity;
			}
			if (complexity == minComplexity.value) {
				results.add(solution);
			}
		}
	}

	/**
	 * Return true if the smaller phrase is a subset of the bigger phrase. For
	 * example "cm" is a subset of "centimeter" and "camp".
	 * 
	 * @param smallerPhrase
	 *            the smaller of the two phrases (the one that might be an
	 *            abbreviation)
	 * @param biggerPhrase
	 *            the larger of the two phrases
	 * @return the first solution that diffs these two phrases with the first as
	 *         a subset as the second.
	 */
	public DiffSegment getSubset(String smallerPhrase, String biggerPhrase) {
		DiffSegment[] array = getSolutions(smallerPhrase, biggerPhrase);
		for (DiffSegment s : array) {
			if (s.isSubset(Branch.A))
				return s;
		}
		return null;
	}
}