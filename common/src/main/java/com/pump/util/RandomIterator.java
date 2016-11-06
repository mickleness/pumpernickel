/*
 * @(#)RandomIterator.java
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/** This is a never-ending iterator that randomly roams over
 * several elements.
 * <P>More specifically: this shuffles all the original elements
 * into a list, and then iterates over that shuffled order.  Once
 * the end is reached, the list is re-shuffled until the first element
 * is not the last element returned.  This way every element will at
 * least get reached once before we randomize everything again.
 */
public class RandomIterator<T> implements Iterator<T> {

    Object[] list;
    int ctr = 0;
    
    public RandomIterator(T[] array) {
    	this.list = Arrays.copyOf(array, array.length);
        randomize(list);
    }
    
    public RandomIterator(List<T> v) {
    	list = v.toArray();
    	randomize(list);
    }
    
    public boolean hasNext() {
        return true;
    }

    public T next() {
        @SuppressWarnings("unchecked")
		T value = (T)list[ctr];
        ctr++;
        if(ctr==list.length) {
            //we're at the end!
            randomize(list);
            //make sure we don't repeat the same thing twice:
            while(list[0]==value && list.length>1) {
                randomize(list);
            }
            ctr = 0;
        }
        return value;
    }
    
    /** Randomizes the order of this list */
    public static void randomize(Object[] list) {
        for(int a = 0; a<list.length; a++) {
            int i = (int)(list.length*Math.random());
            
            Object swap = list[i];
            list[i] = list[a];
            list[a] = swap;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
