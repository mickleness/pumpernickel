/*
 * @(#)RawSortedEdgeArrayList.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.geom.area;

import java.util.Comparator;

public class RawSortedEdgeArrayList extends RawEdgeArrayList {

	Comparator<EdgeX> comparator;
	
	protected RawSortedEdgeArrayList(int initialCapacity,Comparator<EdgeX> comparator) {
		super(initialCapacity);
		this.comparator = comparator;
	}


	@Override
	protected void add(EdgeX element) {
		int min = 0;
		int max = elementCount;
		while(min!=max) {
			int middle = (min+max)/2;
			int k = comparator.compare(get(middle), element);
			if(k==0) {
				min = middle+1;
				max = middle+1;
			} else if(k<1) {
				min = middle+1;
			} else {
				max = middle;
			}
		}

		ensureCapacity(elementCount + 1);
		if(elementCount-min>0) {
			System.arraycopy(elementData, min, elementData, min + 1, elementCount - min);
		}
		elementData[min] = element;
		elementCount++;
	}
}
