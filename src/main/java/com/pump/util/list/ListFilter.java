package com.pump.util.list;

import javax.swing.event.ChangeListener;

/**
 * This helps filter a {@link ObservableList.UIMirror}.
 * <p>
 * For example: if you have a large list of Files, and you're presenting those
 * Files in a JList using a UIMirror: this filter can automatically help
 * eliminate files as they are transfered from the master list to the JList.
 *
 * @param <T>
 */
public interface ListFilter<T> {
	public boolean accept(T candidate);

	public void addChangeListener(ChangeListener changeListener);

	public void removeChangeListener(ChangeListener changeListener);

	public boolean isActive();
}