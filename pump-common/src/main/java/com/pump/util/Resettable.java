/*
 * @(#)Resettable.java
 *
 * $Date: 2014-11-27 01:55:25 -0500 (Thu, 27 Nov 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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


/** An interface for objects that can be reset to a starting state or condition. */
public interface Resettable {
	
	/** Resets this object to its original state. */
	public void reset();
}
