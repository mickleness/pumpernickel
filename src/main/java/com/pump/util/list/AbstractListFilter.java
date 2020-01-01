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
package com.pump.util.list;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class AbstractListFilter<T> implements ListFilter<T> {

	private List<ChangeListener> changeListeners = new ArrayList<>();
	private boolean active = true;
	private UncaughtExceptionHandler uncaughtExceptionHandler;

	protected AbstractListFilter() {
	}

	protected AbstractListFilter(boolean active) {
	}

	@Override
	public void addChangeListener(ChangeListener changeListener) {
		changeListeners.add(changeListener);
	}

	@Override
	public void removeChangeListener(ChangeListener changeListener) {
		changeListeners.remove(changeListener);
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		if (this.active == active)
			return;
		this.active = active;
		fireChangeListeners();
	}

	/**
	 * Fire all ChangeListeners.
	 * <p>
	 * This should be called when the filter has fundamentally changed what
	 * it may accept. For example: if this filter is based on text in a text field,
	 * then every time that text field is changed we should call this method
	 */
	public void fireChangeListeners() {
		for (ChangeListener changeListener : changeListeners
				.toArray(new ChangeListener[changeListeners.size()])) {
			try {
				changeListener.stateChanged(new ChangeEvent(this));
			} catch (Exception e) {
				UncaughtExceptionHandler u = getUncaughtExceptionHandler();
				if (u != null) {
					u.uncaughtException(Thread.currentThread(), e);
				} else {
					e.printStackTrace();
				}
			}
		}
	}

	public UncaughtExceptionHandler getUncaughtExceptionHandler() {
		return uncaughtExceptionHandler;
	}

	public void setUncaughtExceptionHandler(
			UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;
	}

}