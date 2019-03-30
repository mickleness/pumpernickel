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

	protected void fireChangeListeners() {
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
