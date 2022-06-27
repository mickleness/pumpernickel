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