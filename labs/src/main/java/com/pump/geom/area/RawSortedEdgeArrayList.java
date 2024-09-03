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
package com.pump.geom.area;

import java.util.Comparator;

public class RawSortedEdgeArrayList extends RawEdgeArrayList {

	Comparator<EdgeX> comparator;

	protected RawSortedEdgeArrayList(int initialCapacity,
			Comparator<EdgeX> comparator) {
		super(initialCapacity);
		this.comparator = comparator;
	}

	@Override
	protected void add(EdgeX element) {
		int min = 0;
		int max = elementCount;
		while (min != max) {
			int middle = (min + max) / 2;
			int k = comparator.compare(get(middle), element);
			if (k == 0) {
				min = middle + 1;
				max = middle + 1;
			} else if (k < 1) {
				min = middle + 1;
			} else {
				max = middle;
			}
		}

		ensureCapacity(elementCount + 1);
		if (elementCount - min > 0) {
			System.arraycopy(elementData, min, elementData, min + 1,
					elementCount - min);
		}
		elementData[min] = element;
		elementCount++;
	}
}