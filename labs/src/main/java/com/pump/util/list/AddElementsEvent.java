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
package com.pump.util.list;

import java.util.Collections;
import java.util.List;

import javax.swing.event.ListDataEvent;

/**
 * This event describes one or more consecutive elements being added to list.
 *
 * @param <T>
 */
public class AddElementsEvent<T> extends ListEvent<T> {

	private List<T> newElements;
	private int index;

	public AddElementsEvent(Object source, int index, List<T> newElements) {
		super(source);
		this.index = index;
		this.newElements = Collections.unmodifiableList(newElements);
	}

	public int getIndex() {
		return index;
	}

	public List<T> getNewElements() {
		return newElements;
	}

	@Override
	public void execute(List<T> list) {
		list.addAll(getIndex(), getNewElements());
	}

	@Override
	protected ListDataEvent createListDataEvent() {
		return new ListDataEvent(getSource(), ListDataEvent.INTERVAL_ADDED,
				getIndex(), getIndex() + getNewElements().size());
	}
}