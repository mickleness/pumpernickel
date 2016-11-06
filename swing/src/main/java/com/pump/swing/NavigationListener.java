/*
 * @(#)NavigationListener.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
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
package com.pump.swing;

public interface NavigationListener<T> {

	public enum ListSelectionType { SINGLE_CLICK, DOUBLE_CLICK, KEY };
	
	/**
	 * 
	 * @param type the type of selection
	 * @param elements the newly selected elements
	 * @return true if this listener consumed the event, false otherwise
	 */
	public boolean elementsSelected(ListSelectionType type, T... elements);
}
