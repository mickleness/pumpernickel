/*
 * @(#)BasicLocationGroup.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
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
package com.pump.io.location;

import java.util.HashSet;
import java.util.Set;

public class BasicLocationGroup implements IOLocationGroup {
	long consumerCtr = 0;
	Set<Long> checkedOutKeys = new HashSet<Long>();

	public synchronized Object addConsumer() {
		Long returnValue = consumerCtr++;
		checkedOutKeys.add(returnValue);
		return returnValue;
		
	}

	public synchronized void releaseConsumer(Object key) {
		checkedOutKeys.remove(key);
	}
	
	public synchronized boolean isActive() {
		return checkedOutKeys.size()>0;
	}

}
