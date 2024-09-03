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
package com.pump.util;

import java.util.Iterator;

/**
 * This is an iterator that can be be set up in a try-with-resources block and
 * has an optional {@link #getProgress()} method.
 *
 * @param <T>
 */
public interface CloseableMeasurableIterator<T> extends AutoCloseable,
		Iterator<T> {

	/**
	 * Return the progress (as a fraction from [0,1]) of the amount read, or a
	 * negative number to indicate the progress is indeterminate.
	 */
	public float getProgress();
}