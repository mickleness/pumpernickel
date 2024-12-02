/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
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
	void addAttributePropertyChangeListener(String propertyName,
			PropertyChangeListener pcl);

	void addAttributePropertyChangeListener(PropertyChangeListener pcl);

	void clearAttributes();

	/**
	 * Return the value associated with a key.
	 * 
	 * @param key
	 *            the key to retrieve the value for.
	 * 
	 * @return the value associated with the provided key.
	 */
	<T> T getAttribute(Key<T> key);

	String[] getAttributes();

	ReadWriteLock getAttributeLock();

	/**
	 * Return a copy of all the key/value pairs in this object.
	 */
	Map<String, Object> getAttributeMap();

	/**
	 * Apply all the values in the incoming map.
	 * 
	 * @param incomingData
	 *            a series of key/value pairs to install in this object.
	 * @param completeReplace
	 *            if true then this call also removes other attributes.. If
	 *            false then this call can only add attributes.
	 */
	void putAllAttributes(Map<String, Object> incomingData,
			boolean completeReplace);

	void removeAttributePropertyChangeListener(PropertyChangeListener pcl);

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
	<T> T setAttribute(Key<T> key, T value);

	int getAttributeCount();
}