package com.pump.util.list;

import java.util.List;

import javax.swing.event.ListDataEvent;

/**
 * This event describes one element in a list being replaced.
 *
 * @param <T>
 */
public class ChangeElementEvent<T> extends ListEvent<T> {

	private int index;
	private T oldElement, newElement;

	public ChangeElementEvent(Object source, int index, T oldElement,
			T newElement) {
		super(source);
		this.index = index;
		this.oldElement = oldElement;
		this.newElement = newElement;
	}

	public int getIndex() {
		return index;
	}

	public T getOldElement() {
		return oldElement;
	}

	public T getNewElement() {
		return newElement;
	}

	@Override
	public void execute(List<T> list) {
		list.set(getIndex(), getNewElement());
	}

	@Override
	protected ListDataEvent createListDataEvent() {
		return new ListDataEvent(getSource(), ListDataEvent.CONTENTS_CHANGED,
				getIndex(), getIndex());
	}

}
