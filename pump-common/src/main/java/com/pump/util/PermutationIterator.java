/*
 * @(#)PermutationIterator.java
 *
 * $Date: 2016-01-23 15:29:41 -0500 (Sat, 23 Jan 2016) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

/** This creates all the possible permutations of a collection of objects.
 * For example, given the elements "A", "B", "C", this will return all 6 possible ways to rearrange
 * these letters: ABC, ACB, BAC, BCA, CAB, CBA.
 * 
 * @param <T>
 */
public class PermutationIterator<T> implements Iterator<T[]>
{
	static class Column<T> {
		int i;
		T[] values;

		@SuppressWarnings("unchecked")
		Column(Class<T> type,int capacity) {
			values = (T[])Array.newInstance(type, capacity);
		}
	}
	
	protected final T[] elements;
	protected final Column<T>[] columns;
	protected final Class<T> elementType;
	protected boolean hasNext = true;
	
	/** Create a PermutationIterator.
	 * 
	 * @param allElements all the elements this iterator will provide permutations of.
	 * Note just walking through all possible permutations of a 10-element list
	 * involves 3,628,800 iterations, so this gets very expensive very fast. Less than 8
	 * elements, though, and you should probably be OK.
	 */
	@SuppressWarnings("unchecked")
	public PermutationIterator(Class<T> type,Collection<T> allElements) {
		elementType = type;
		elements = (T[])Array.newInstance(type, allElements.size());
		allElements.toArray(elements);
		
		for(int a = 0; a<elements.length; a++) {
			for(int b = a+1; b<elements.length; b++) {
				if(elements[a]==elements[b]) {
					throw new IllegalArgumentException("This iterator doesn't support redundant references yet.");
				}
			}
		}
		
		allElements.toArray(elements);
		columns = new Column[elements.length];
		for(int a = 0; a<columns.length; a++) {
			columns[a] = new Column<>(type, columns.length-a);
		}
		
		for(int a = 0; a<elements.length; a++) {
			columns[0].values[a] = elements[a];
		}
		populateColumns(1);
	}
	
	private void populateColumns(int index) {
		while(index<columns.length) {
			int ctr = 0;
			columns[index].i = 0;
			for(int a = 0; a<elements.length; a++) {
				boolean match = false;
				for(int stableColumnIndex = 0; stableColumnIndex<index && (!match); stableColumnIndex++) {
					Column<T> c = columns[stableColumnIndex];
					match = c.values[c.i]==elements[a];
				}
				
				if(!match) {
					columns[index].values[ctr] = elements[a];
					ctr++;
				}
			}
			index++;
		}
	}

	@Override
	public boolean hasNext()
	{
		return hasNext;
	}

	@Override
	public T[] next()
	{
		return next(null);
	}

	/**
	 * Calculate the next permutation sequence and store it in the array provided.
	 * <p>
	 * For large/demanding iterations, reusing the target array may save you a little
	 * bit of performance.
	 * 
	 * @param dest the array to store the permutation sequence in.
	 * @return the argument array.
	 */
	@SuppressWarnings("unchecked")
	public T[] next(T[] dest) {
		if(dest==null) {
			dest = (T[])Array.newInstance(elementType, elements.length);
		} else if(dest.length!=elements.length) {
			throw new IllegalArgumentException("The destination array (length "+dest.length+") is not the same size as the original list of elements (length "+elements.length+")");
		}
		//first define the value:
		for(int a = 0; a<columns.length; a++) {
			dest[a] = columns[a].values[ columns[a].i ];
		}
		
		//now increment the iterator for future use:
		int j = columns.length-1;
		while(j>=0) {
			if(columns[j].i+1 < columns[j].values.length) {
				columns[j].i++;
				populateColumns(j+1);
				return dest;
			} else {
				j--;
			}
		}
		hasNext = false;
		return dest;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
