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
package com.pump.util;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This iterates over all the possible subsets of an initial set of elements,
 * starting with the smallest and building up to the largest. That is: this
 * identifies all the possible combinations of elements of a set (but it does
 * not address possible permutations or repetition of elements).
 *
 * @param <E>
 *            the type of element iterated over
 */
public class SubsetIterator<E> implements Iterator<Set<E>> {
	final Set<E> elements;
	final int minSetSize, maxSetSize;
	final Comparator<E> comparator;

	int currentSetSize;

	/**
	 * The sets of currentSetSize.
	 * 
	 */
	Iterator<Set<E>> returnValues = null;

	/**
	 * Create an iterator to iterate over all subsets of the argument provided.
	 * 
	 * If the elements provided are a <code>SortedSet</code>, then the
	 * <code>Comparator</code> used for that set is used to sort results of this
	 * class.
	 * 
	 * @param includeEmptySet
	 *            if true then the first value this returns will be an empty
	 *            set. If false then then first value this returns will have 1
	 *            element. (If false and <code>elements</code> is empty: an
	 *            exception will be thrown.)
	 * @param elements
	 *            the elements to create subsets of. Note this class continues
	 *            to reference this argument, so you should not modify this set
	 *            once this iterator is constructed.
	 */
	public SubsetIterator(Set<E> elements, boolean includeEmptySet) {
		this(elements, getComparator(elements), includeEmptySet ? 0 : 1,
				elements.size());
	}

	@SuppressWarnings("unchecked")
	private static <T> Comparator<T> getComparator(Set<T> elements) {
		if (elements instanceof SortedSet)
			return (Comparator<T>) ((SortedSet<T>) elements).comparator();
		return null;
	}

	/**
	 * 
	 * @param elements
	 *            the elements to create subsets of. Note this class continues
	 *            to reference this argument, so you should not modify this set
	 *            once this iterator is constructed.
	 * @param comparator
	 *            an optional comparator to sort the returned results.
	 * @param minSetSize
	 *            the smallest subset size to return. This may be [zero, max].
	 * @param maxSetSize
	 *            the largest subset size to return. This must be larger than
	 *            the minimum, and not larger than <code>elements.size()</code>.
	 */
	public SubsetIterator(Set<E> elements, Comparator<E> comparator,
			int minSetSize, int maxSetSize) {
		this.minSetSize = minSetSize;
		this.maxSetSize = maxSetSize;
		this.elements = elements;
		this.comparator = comparator;
		if (this.elements == null)
			throw new NullPointerException();
		if (minSetSize < 0)
			throw new IllegalArgumentException("minSetSize (" + minSetSize
					+ ") cannot be less than zero");
		if (minSetSize > maxSetSize)
			throw new IllegalArgumentException("minSetSize (" + minSetSize
					+ ") cannot be greater than maxSetSize (" + maxSetSize
					+ ")");
		if (maxSetSize > elements.size())
			throw new IllegalArgumentException("maxSetSize (" + maxSetSize
					+ ") cannot be greater than elements.size() ("
					+ elements.size() + ")");

		setCurrentSize(minSetSize);
	}

	private synchronized void setCurrentSize(int currentSize) {
		this.currentSetSize = currentSize;

		returnValues = new FixedSizeIterator(currentSetSize);
	}

	/**
	 * This is a hard-to-read class that provides each subset one-at-a-time.
	 * <p>
	 * The easiest way to understand this class is probably to understand it's
	 * derivation: it was originally implemented as a simple recursive method
	 * that identified all the subsets of a fixed size at once:
	 * 
	 * <pre>
	 * private void calculateSubsets(List&lt;Set&lt;E&gt;&gt; container,
	 * 		Set&lt;E&gt; allRemainingElements, Set&lt;E&gt; currentSet) {
	 * 	if (currentSet.size() == currentSetSize) {
	 * 		container.add(currentSet);
	 * 		return;
	 * 	}
	 * 
	 * 	Set&lt;E&gt; smallerRemainingElements = newSet();
	 * 	smallerRemainingElements.addAll(allRemainingElements);
	 * 
	 * 	Iterator&lt;E&gt; iter = allRemainingElements.iterator();
	 * 	while (iter.hasNext()) {
	 * 		Set&lt;E&gt; set = newSet();
	 * 		set.addAll(currentSet);
	 * 		E e = iter.next();
	 * 		smallerRemainingElements.remove(e);
	 * 		if (!set.contains(e)) {
	 * 			set.add(e);
	 * 			calculateSubsets(container, smallerRemainingElements, set);
	 * 		}
	 * 	}
	 * }
	 * </pre>
	 *
	 * <p>
	 * This class emulates that method with a stack of
	 * <code>RecursionNodes</code>.
	 */
	class FixedSizeIterator implements Iterator<Set<E>> {
		int capacity;
		Set<E> next;
		LinkedList<RecursionNode> stack = new LinkedList<RecursionNode>();

		private FixedSizeIterator(int capacity) {
			this.capacity = capacity;
			stack.add(new RecursionNode(elements, newSet()));
			queueNext();
		}

		public boolean hasNext() {
			return next != null;
		}

		public Set<E> next() {
			Set<E> returnValue = next;
			queueNext();
			return returnValue;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		private void queueNext() {
			next = null;
			while (stack.size() > 0) {
				RecursionNode n = stack.peekLast();
				if (n.currentSet.size() == capacity) {
					next = n.currentSet;
					stack.removeLast();
					return;
				}

				RecursionNode newChild = n.iterate();
				if (newChild != null) {
					stack.add(newChild);
				} else {
					stack.removeLast();
				}
			}
		}

	}

	class RecursionNode {
		Set<E> allRemainingElements;
		Set<E> currentSet;
		Set<E> smallerRemainingElements = newSet();

		private RecursionNode(Set<E> allRemainingElements, Set<E> currentSet) {
			this.allRemainingElements = allRemainingElements;
			this.currentSet = currentSet;
			smallerRemainingElements.addAll(allRemainingElements);
		}

		Iterator<E> iter;

		RecursionNode iterate() {
			if (iter == null) {
				iter = allRemainingElements.iterator();
			}

			Set<E> set = newSet();
			set.addAll(currentSet);

			while (true) {
				if (iter.hasNext() == false)
					return null;

				E e = iter.next();
				smallerRemainingElements.remove(e);
				if (!set.contains(e)) {
					set.add(e);
					return new RecursionNode(smallerRemainingElements, set);
				}
			}
		}
	}

	public synchronized boolean hasNext() {
		return currentSetSize < maxSetSize || returnValues.hasNext();
	}

	private Boolean useTreeSets = null;

	private Set<E> newSet() {
		if (comparator != null)
			return new TreeSet<E>(comparator);

		/*
		 * Unfortunately (I think?) we can't really know until runtime if we our
		 * elements are Comparable<E>, so we'll have to test to be safe:
		 */
		if (useTreeSets == null) {
			try {
				TreeSet<E> test = new TreeSet<E>();
				test.addAll(elements);
				useTreeSets = Boolean.TRUE;
			} catch (Exception e) {
				useTreeSets = Boolean.FALSE;
			}
		}

		if (useTreeSets)
			return new TreeSet<E>();

		return new HashSet<E>();
	}

	public synchronized Set<E> next() {
		if (returnValues.hasNext() == false) {
			if (currentSetSize + 1 <= maxSetSize) {
				setCurrentSize(currentSetSize + 1);
			} else {
				return null;
			}
		}
		return returnValues.next();
	}

	/** Throws an UnsupportedOperationException. */
	public void remove() {
		throw new UnsupportedOperationException();
	}

}