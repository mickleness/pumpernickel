/*
 * @(#)Storage.java
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


/** A minimal interface for an object that stores key/value pairs.
 * You can create a Storage object that maps to a Hashtable, to
 * particular Preferences, etc.
 */
public interface Storage
{
	/** Stores a key/value pair.
	 * 
	 * @param key the key to define
	 * @param value the value to store
	 * @return <code>true</code> if this call changed the existing value of <code>key</code>
	 */
	public boolean put(Object key,String value);
	
	/** Retrieves a key/value pair.
	 * 
	 * @param key the key to consult
	 * @return the String associated with that key
	 */
	public String get(Object key);
}
