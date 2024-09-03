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
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * This is a minimal implementation of the AttributeData interface that is
 * backed with a HashMap.
 */
public class AttributeDataImpl extends AbstractAttributeDataImpl implements
		AttributeData {
	private static final long serialVersionUID = 1L;

	@Override
	public ReadWriteLock getAttributeLock() {
		return super.getAttributeLock();
	}

	@Override
	public <T> T setAttribute(Key<T> key, T value) {
		return super.setAttribute(key, value);
	}

	@Override
	public void handleUncaughtListenerException(Exception e,
			String propertyName, Object oldValue, Object newValue) {
		super.handleUncaughtListenerException(e, propertyName, oldValue,
				newValue);
	}

	@Override
	public void putAllAttributes(Map<String, Object> incomingData,
			boolean completeReplace) {
		super.putAllAttributes(incomingData, completeReplace);
	}

	@Override
	public <T> T getAttribute(Key<T> key) {
		return super.getAttribute(key);
	}

	@Override
	public void clearAttributes() {
		super.clearAttributes();
	}

	@Override
	public int getAttributeCount() {
		return super.getAttributeCount();
	}

	@Override
	public String[] getAttributes() {
		return super.getAttributes();
	}

	@Override
	public Map<String, Object> getAttributeMap() {
		return super.getAttributeMap();
	}

	@Override
	public void addAttributePropertyChangeListener(String propertyName,
			PropertyChangeListener pcl) {
		super.addPropertyChangeListener(propertyName, pcl);
	}

	@Override
	public void addAttributePropertyChangeListener(PropertyChangeListener pcl) {
		super.addPropertyChangeListener(pcl);
	}

	@Override
	public void removeAttributePropertyChangeListener(PropertyChangeListener pcl) {
		super.removePropertyChangeListener(pcl);
	}
}