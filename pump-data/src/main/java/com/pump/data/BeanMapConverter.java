package com.pump.data;

import java.util.Map;

/**
 * This converts a map of key/value pairs into a bean object and back again.
 * 
 * @param <K> the type of bean identifier
 * @param <B> the type of bean wrapper object this converter creates/reads. 
 * In its simplest form, a BeanConverter may simply interact the map of key/value pairs, so B may just be
 * <code>Map</code>. But if you want to create a separate object for a <code>Person</code>
 * or <code>ProductOrder</code> or <code>Address</code>: this interface lets you do that
 * (so long as there are distinguishing characteristics in either the bean ID or the
 * map of data that will inform you which type of object to use).
 */
public interface BeanMapConverter<K, B> {
	
	/**
	 * Create a bean representation from a collection key/value pairs, the bean id,
	 * and the branch that is creating this object.
	 * <p>
	 * This needs to be a relatively light/fast call, because it may be consulted
	 * frequently.
	 * 
	 * @param source the branch that supplied this data.
	 * @param beanId the id of the bean requested.
	 * @param beanData a map of key/value pairs that represent this bean's data.
	 * 
	 * @return an object representing this bean.
	 */
	public B createBean(K beanId, Map<String, Object> beanData);
	
	/**
	 * Convert a bean object into a map of key/value pairs.
	 * 
	 * @param beanId the id of the bean argument.
	 * @param beanData the object from which we'll extract key/value pairs.
	 * @return a map of key/value pairs that represent that data in
	 * this bean.
	 */
	public Map<String, Object> getData(K beanId,B beanData);
}
