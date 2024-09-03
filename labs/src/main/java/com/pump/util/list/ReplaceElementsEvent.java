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
 * This event describes a replace-all event on a list.
 * <p>
 * See {@link ObservableList#setAll(java.util.Collection)}.
 *
 * @param <T>
 */
public class ReplaceElementsEvent<T> extends ListEvent<T> {

	private List<T> oldElements;
	private List<T> newElements;

	public ReplaceElementsEvent(Object source, List<T> oldElements,
			List<T> newElements) {
		super(source);
		this.oldElements = oldElements;
		this.newElements = newElements;
	}

	@Override
	public void execute(List<T> list) {
		list.clear();
		list.addAll(getNewElements());
	}

	public List<? extends T> getNewElements() {
		return Collections.unmodifiableList(newElements);
	}

	public List<? extends T> getOldElements() {
		return Collections.unmodifiableList(oldElements);
	}

	@Override
	protected ListDataEvent createListDataEvent() {
		return new ListDataEvent(getSource(), ListDataEvent.CONTENTS_CHANGED,
				0, newElements.size() - 1);
	}

}