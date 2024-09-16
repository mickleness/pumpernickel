/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.util;

/**
 * A simple interface for depositing an element.
 */
public interface Receiver<T> {

	/**
	 * Add elements to this receiver.
	 * 
	 * @param elements
	 *            the elements to add to this receiver.
	 */
	void add(T... elements);
}