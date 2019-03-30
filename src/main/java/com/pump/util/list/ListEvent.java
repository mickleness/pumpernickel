package com.pump.util.list;

import java.util.List;

import javax.swing.event.ListDataEvent;

public abstract class ListEvent<T> {

	private Object source;

	public ListEvent(Object source) {
		this.source = source;
	}

	public abstract void execute(List<T> list);

	protected abstract ListDataEvent createListDataEvent();

	public Object getSource() {
		return source;
	}
}
