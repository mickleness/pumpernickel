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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.pump.util.list.ObservableList;

public abstract class SearchConstraints<T> implements Comparator<T> {
	public abstract boolean accepts(T obj);

	public void sort(List<T> unsortedList, List<T> destination) {
		List<T> solutions = new ArrayList<T>(unsortedList.size());
		for (int a = 0; a < unsortedList.size(); a++) {
			T v = unsortedList.get(a);
			if (accepts(v))
				solutions.add(v);
		}
		Collections.sort(solutions, this);

		if (destination instanceof ObservableList) {
			((ObservableList<T>) destination).setAll(solutions);
		} else {
			destination.clear();
			for (int a = 0; a < solutions.size(); a++) {
				destination.add(solutions.get(a));
			}
		}
	}
}