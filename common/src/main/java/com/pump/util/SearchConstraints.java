/*
 * @(#)SearchConstraints.java
 *
 * $Date: 2016-01-30 18:40:21 -0500 (Sat, 30 Jan 2016) $
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
package com.pump.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public abstract class SearchConstraints<T> implements Comparator<T> {
	public abstract boolean accepts(T obj);
	
	public void sort(List<T> unsortedList,List<T> destination) {
		List<T> solutions = new ArrayList<T>(unsortedList.size());
		for(int a = 0; a<unsortedList.size(); a++) {
			T v = unsortedList.get(a);
			if(accepts(v))
				solutions.add(v);
		}
		Collections.sort(solutions, this);
		
		if(destination instanceof ObservableList) {
			((ObservableList<T>)destination).setAll(solutions);
		} else {
			destination.clear();
			for(int a = 0; a<solutions.size(); a++) {
				destination.add(solutions.get(a));
			}
		}
	}
}
