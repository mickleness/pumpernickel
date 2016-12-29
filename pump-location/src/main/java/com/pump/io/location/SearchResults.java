/*
 * @(#)SearchResults.java
 *
 * $Date: 2014-03-27 03:50:51 -0400 (Thu, 27 Mar 2014) $
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
package com.pump.io.location;


public interface SearchResults {
	
	/** This may return null. */
	public IOLocation getSearchDirectory();
	
	/** Return the text the user is searching by. */
	public String getSearchText();
}
