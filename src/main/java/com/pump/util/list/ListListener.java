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

/**
 * This listener identifies specific list operations with separate
 * notifications.
 * <p>
 * (You can also use a ChangeListener or ArrayListener for a more generic
 * notification about list changes.)
 * 
 * @param <T>
 */
public interface ListListener<T> {
	public void elementsAdded(AddElementsEvent<T> event);

	public void elementsRemoved(RemoveElementsEvent<T> event);

	public void elementChanged(ChangeElementEvent<T> event);

	public void elementsReplaced(ReplaceElementsEvent<T> event);
}