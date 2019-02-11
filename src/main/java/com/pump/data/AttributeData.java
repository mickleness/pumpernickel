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
package com.pump.data;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

public interface AttributeData extends Serializable {
	public void addAttributePropertyChangeListener(PropertyChangeListener pcl);

	public void clearAttributes();

	/**
	 * Return the value associated with a key.
	 * 
	 * @param key
	 *            the key to retrieve the value for.
	 * 
	 * @return the value associated with the provided key.
	 */
	public <T> T getAttribute(Key<T> key);

	public String[] getAttributes();

	public ReadWriteLock getAttributeLock();

	/**
	 * Return a copy of all the key/value pairs in this object.
	 */
	public Map<String, Object> getAttributeMap();

	/**
	 * Apply all the values in the incoming map.
	 * 
	 * @param incomingData
	 *            a series of key/value pairs to install in this object.
	 * @param completeReplace
	 *            if true then this call also removes other attributes.. If
	 *            false then this call can only add attributes.
	 */
	public void putAllAttributes(Map<String, Object> incomingData,
			boolean completeReplace);

	public void removeAttributePropertyChangeListener(PropertyChangeListener pcl);

	/**
	 * Set a key/value pair in this object.
	 * 
	 * @param key
	 *            the key to retrieve the value of.
	 * @param value
	 *            the value to assign to the key.
	 * 
	 * @return the previous value the key mapped to.
	 */
	public <T> T setAttribute(Key<T> key, T value);

	public int getAttributeCount();
}