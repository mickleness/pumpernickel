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
package com.pump.util;


/** A simple interface for depositing an element.
 */
public interface Receiver<T> {
	
	/** Add elements to this receiver.
	 * 
	 * @param elements the elements to add to this receiver.
	 */
	public void add(T... elements);
}