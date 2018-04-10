/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.util;

/**
 * A minimal interface for an object that stores key/value pairs. You can create
 * a Storage object that maps to a Hashtable, to particular Preferences, etc.
 */
public interface Storage {
	/**
	 * Stores a key/value pair.
	 * 
	 * @param key
	 *            the key to define
	 * @param value
	 *            the value to store
	 * @return <code>true</code> if this call changed the existing value of
	 *         <code>key</code>
	 */
	public boolean put(Object key, String value);

	/**
	 * Retrieves a key/value pair.
	 * 
	 * @param key
	 *            the key to consult
	 * @return the String associated with that key
	 */
	public String get(Object key);
}