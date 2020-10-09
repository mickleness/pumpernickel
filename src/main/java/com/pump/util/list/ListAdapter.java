package com.pump.util.list;

/**
 * An abstract adapter class for receiving list events. The methods in this
 * class are empty. This class exists as convenience for creating listener
 * objects.
 */
public class ListAdapter<T> implements ListListener<T> {

	@Override
	public void elementsAdded(AddElementsEvent<T> event) {
		// intentionally empty
	}

	@Override
	public void elementsRemoved(RemoveElementsEvent<T> event) {
		// intentionally empty
	}

	@Override
	public void elementChanged(ChangeElementEvent<T> event) {
		// intentionally empty
	}

	@Override
	public void elementsReplaced(ReplaceElementsEvent<T> event) {
		// intentionally empty
	}

}
