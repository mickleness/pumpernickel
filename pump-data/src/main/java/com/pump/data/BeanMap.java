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

import java.util.HashMap;
import java.util.Map;

/**
 * This is a simple map container with get/set values that use {@link Key} objects.
 */
public class BeanMap {
	protected Map<String, Object> data = new HashMap<>();

	/**
	 * Create an empty BeanMap.
	 */
	public BeanMap() {}
	
	/**
	 * Create a BeanMap
	 * @param attributes the attributes to copy into this object.
	 */
	public BeanMap(Map<String, Object> attributes) {
		data.putAll(attributes);
	}

	/**
	 * Set a key/value pair in this object.
	 * 
	 * @param key the key to retrieve the value of.
	 * @param value the value to assign to the key.
	 * 
	 * @return the previous value the key mapped to.
	 */
	public <T> T set(Key<T> key, T value) {
		return key.put(data, value);
	}
	
	/**
	 * Apply all the values in the incoming map.
	 * 
	 * @param incomingData a series of key/value pairs to install in this object.
	 */
	public void putAll(Map<String, Object> incomingData) {
		data.putAll(incomingData);
	}
	
	/**
	 * Return the value associated with a key.
	 * 
	 * @param key the key to retrieve the value for.
	 * 
	 * @return the value associated with the provided key.
	 */
	public <T> T get(Key<T> key) {
		return key.get(data);
	}

	@Override
	public int hashCode() {
		return data.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BeanMap))
			return false;
		BeanMap other = (BeanMap)obj;
		return data.equals(other.data);
	}

	/**
	 * Return a copy of all the key/value pairs in this object.
	 */
	public Map<String, Object> getMap() {
		return new HashMap<>(data);
	}
}