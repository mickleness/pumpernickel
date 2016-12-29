/*
 * @(#)SubstringIterator.java
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
package com.pump.util;

import java.util.HashSet;
import java.util.Iterator;

/** This iterates over all possible substrings of a <code>java.lang.String</code>.
 * For example: the string "abcde" will yield: "abcde", "abcd", "bcde", "abc", "bcd",
 * "cde", "ab", "bc", "cd", "de", "a", "b", "c", "d", "e".
 * <p>There is also an option to return only unique substrings.  If this option
 * is active and the original string is "apple", then "p" will only be returned once
 * instead of twice.
 */
public class SubstringIterator implements Iterator<String> {
	String original;
	int pos;
	int len;
	boolean done = false;
	HashSet<String> previousValues;
	
	/** Create a <code>SubstringIterator</code> that returns
	 * every possible substring of the original text.
	 * @param text the text to break into substrings.
	 */
	public SubstringIterator(String text) {
		this(text, false);
	}
	
	/** Create a <code>SubstringIterator</code>.
	 * 
	 * @param text the text to break into substrings.
	 * @param onlyUniqueSubstrings if this is true then this
	 * iterator will never return the same substring twice.
	 * For example substrings of "maintain" may include two copies
	 * of "ain" if this boolean is false.
	 */
	public SubstringIterator(String text,boolean onlyUniqueSubstrings) {
		if(text==null) throw new NullPointerException();
		
		original = text;
		pos = 0;
		len = text.length();
		if(onlyUniqueSubstrings) {
			previousValues = new HashSet<String>();
		}
	}

	public boolean hasNext() {
		return !done;
	}
	
	/** Returns the length of the substring to be returned by
	 * <code>next()</code>.  This value decreases from the original
	 * string length to 1 as this iterator progresses.
	 */
	public int getCurrentLength() {
		return len;
	}

	public String next() {
		String returnValue = original.substring(pos, len+pos);
		if(previousValues!=null)
			previousValues.add(returnValue);
		iterate();
		return returnValue;
	}
	
	private void iterate() {
		while(!done) {
			pos++;
			if(pos+len>original.length()) {
				len--;
				pos = 0;
				if(len<=0)
					done = true;
				if(previousValues!=null)
					previousValues.clear();
			}
			
			if(previousValues!=null && (!done)) {
				String nextValue = original.substring(pos, len+pos);
				if(!previousValues.contains(nextValue)) {
					return;
				}
			} else {
				return;
			}
		}
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
