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
package com.pump.geom.area;


public class RawCurveArrayList extends CurveList {

	protected RawCurveArrayList(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Removes all of the elements from this list.  The list will
	 * be empty after this call returns.
	 */
	protected void clear() {
		// Let gc do its work
		for (int i = 0; i < elementCount; i++)
			elementData[i] = null;

		elementCount = 0;
	}

	protected CurveX[] getArray() {
		return elementData;
	}
	
	protected CurveX[] getArray(int minArraySize) {
		ensureCapacity(minArraySize);
		return elementData;
	}
}