/*
 * @(#)CombinationIterator.java
 *
 * $Date: 2015-12-26 20:42:44 -0600 (Sat, 26 Dec 2015) $
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** This iterates through every combination of a list of arrays. For example:
 * <pre>List&lt;String[]&gt; input = new ArrayList&lt;&gt;();
 *input.add(new String[] {"1", "2", "3"});
 *input.add(new String[] {"apple", "orange"});
 *input.add(new String[] {"X"});
 *CombinationIterator&lt;String&gt; iter = new CombinationIterator&lt;&gt;(input);
 *while(iter.hasNext()) {
 *	System.out.println(iter.next());
 *}</pre>
 * <p>This code will output this list:
 * <ul><li>"1", "apple", "X"</li>
 * <li>"1", "orange", "X"</li>
 * <li>"2", "apple", "X"</li>
 * <li>"2", "orange", "X"</li>
 * <li>"3", "apple", "X"</li>
 * <li>"3", "orange", "X"</li></ul>
 * <p>It helps to visualize this as a slot machine, where "1", "2", "3" are vertically stacked as
 * one place/slot in the machine. "apple", "orange" is the next slot, and "X" is always the 3rd slot.
 *
 * @param <T>
 */
public class CombinationIterator<T> implements Iterator<List<T>>
{

	List<T[]> masterList;
	int[] positions;

	public CombinationIterator(T[]... list) {
		masterList = new ArrayList<>();
		for(T[] element : list) {
			masterList.add(element);
		}
		positions = list.length==0 ? null : new int[list.length];
	}
	
	public CombinationIterator(List<T[]> list) {
		masterList = new ArrayList<>();
		masterList.addAll(list);
		positions = list.size()==0 ? null : new int[list.size()];
	}
	
	@Override
	public synchronized boolean hasNext()
	{
		return positions!=null;
	}

	@Override
	public synchronized List<T> next()
	{
		List<T> returnValue = new ArrayList<>();
		for(int a = 0; a<masterList.size(); a++) {
			returnValue.add( masterList.get(a)[positions[a]] );
		}
		
		incrementPositions();
		
		return returnValue;
	}
	
	protected void incrementPositions() {
		int index = positions.length-1;
		while(true) {
			positions[index]++;
			if(positions[index]==masterList.get(index).length) {
				positions[index] = 0;
				index--;
				if(index==-1) {
					positions = null;
					return;
				}
			} else {
				return;
			}
		}
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
	
}
