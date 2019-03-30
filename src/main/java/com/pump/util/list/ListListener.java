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