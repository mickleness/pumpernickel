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