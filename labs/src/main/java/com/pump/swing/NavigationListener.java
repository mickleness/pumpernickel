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
package com.pump.swing;

public interface NavigationListener<T> {

	public enum ListSelectionType {
		SINGLE_CLICK, DOUBLE_CLICK, KEY
	};

	/**
	 * 
	 * @param type
	 *            the type of selection
	 * @param elements
	 *            the newly selected elements
	 * @return true if this listener consumed the event, false otherwise
	 */
	public boolean elementsSelected(ListSelectionType type, T... elements);
}