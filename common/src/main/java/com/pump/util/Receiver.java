/*
 * @(#)Receiver.java
 *
 * $Date: 2015-12-26 20:42:44 -0600 (Sat, 26 Dec 2015) $
 *
 * Copyright (c) 2013 by Jeremy Wood.
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


/** A simple interface for depositing an element.
 */
public interface Receiver<T> {
	
	/** Add elements to this receiver.
	 * 
	 * @param elements the elements to add to this receiver.
	 */
	public void add(T... elements);
}
