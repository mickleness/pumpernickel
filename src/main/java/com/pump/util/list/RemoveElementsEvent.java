package com.pump.util.list;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.event.ListDataEvent;

/**
 * This event describes removing several (possible non-consecutive) elements
 * from a list.
 *
 * @param <T>
 */
public class RemoveElementsEvent<T> extends ListEvent<T> {

	private TreeMap<Integer, T> removedElements;

	public RemoveElementsEvent(Object source,
			TreeMap<Integer, T> removedElements) {
		super(source);
		this.removedElements = removedElements;
	}

	/**
	 * Return a map of index-value pairs indicating which elements were removed
	 * and their original index.
	 */
	public SortedMap<Integer, T> getRemovedElements() {
		return Collections.unmodifiableSortedMap(removedElements);
	}

	@Override
	public void execute(List<T> list) {
		Iterator<Integer> iter = removedElements.descendingKeySet()
				.iterator();
		while (iter.hasNext()) {
			int index = iter.next();
			list.remove(index);
		}
	}

	@Override
	protected ListDataEvent createListDataEvent() {
		int minIndex = removedElements.firstKey();
		int maxIndex = removedElements.lastKey();
		// I'm not entirely sure how ListDataListeners expected to hear about
		// removals?
		// Is this one event sufficient?
		return new ListDataEvent(getSource(), ListDataEvent.INTERVAL_REMOVED,
				minIndex, maxIndex);
	}

}
