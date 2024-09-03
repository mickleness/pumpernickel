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