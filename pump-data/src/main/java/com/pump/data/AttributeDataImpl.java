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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This is a simple map container with get/set values that use {@link Key}
 * objects.
 */
public class AttributeDataImpl implements AttributeData {
	private static final long serialVersionUID = 1L;

	protected Map<String, Object> data = new HashMap<>();
	private transient List<PropertyChangeListener> listeners;
	private transient ReadWriteLock lock;

	/**
	 * Create an empty AttributeDataImpl.
	 */
	public AttributeDataImpl() {
		initialize();
	}

	/**
	 * Create a AttributeDataImpl.
	 * 
	 * @param attributes
	 *            the attributes to copy into this object.
	 */
	public AttributeDataImpl(Map<String, Object> attributes) {
		this();
		data.putAll(attributes);
	}

	private void initialize() {
		listeners = new ArrayList<>();
		lock = new ReentrantReadWriteLock();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
		out.writeObject(data);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		initialize();
		int version = in.readInt();
		if (version > 0) {
			throw new IOException("unsupported serialization version: "
					+ version);
		}
		data = (Map) in.readObject();
	}

	@Override
	public void addAttributePropertyChangeListener(PropertyChangeListener pcl) {
		synchronized (listeners) {
			listeners.add(pcl);
		}
	}

	@Override
	public void removeAttributePropertyChangeListener(PropertyChangeListener pcl) {
		synchronized (listeners) {
			listeners.remove(pcl);
		}
	}

	@Override
	public ReadWriteLock getAttributeLock() {
		return lock;
	}

	@Override
	public <T> T setAttribute(Key<T> key, T value) {
		getAttributeLock().writeLock().lock();
		try {
			T oldValue = key.put(data, value);
			if (!Objects.equals(value, oldValue))
				firePropertyChangeListeners(key.getName(), oldValue, value);
			return oldValue;
		} finally {
			getAttributeLock().writeLock().unlock();
		}
	}

	protected void firePropertyChangeListeners(String propertyName,
			Object oldValue, Object newValue) {
		PropertyChangeListener[] listenerArray;
		synchronized (listeners) {
			listenerArray = listeners
					.toArray(new PropertyChangeListener[listeners.size()]);
		}
		for (PropertyChangeListener pcl : listenerArray) {
			try {
				pcl.propertyChange(new PropertyChangeEvent(this, propertyName,
						oldValue, newValue));
			} catch (Exception e) {
				handleUncaughtListenerException(e, propertyName, oldValue,
						newValue);
			}
		}
	}

	protected void handleUncaughtListenerException(Exception e,
			String propertyName, Object oldValue, Object newValue) {
		e.printStackTrace();
	}

	@Override
	public void putAllAttributes(Map<String, Object> incomingData) {
		getAttributeLock().writeLock().lock();
		try {
			for (Entry<String, Object> entry : incomingData.entrySet()) {
				Object oldValue = data.put(entry.getKey(), entry.getValue());
				if (!Objects.equals(oldValue, entry.getValue()))
					firePropertyChangeListeners(entry.getKey(), oldValue,
							entry.getValue());
			}
		} finally {
			getAttributeLock().writeLock().unlock();
		}
	}

	@Override
	public <T> T getAttribute(Key<T> key) {
		getAttributeLock().readLock().lock();
		try {
			return key.get(data);
		} finally {
			getAttributeLock().readLock().unlock();
		}
	}

	@Override
	public int hashCode() {
		lock.readLock().lock();
		try {
			return data.hashCode();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AttributeData))
			return false;
		AttributeData other = (AttributeData) obj;

		getAttributeLock().readLock().lock();
		other.getAttributeLock().readLock().lock();
		try {
			if (other instanceof AttributeDataImpl) {
				AttributeDataImpl b = (AttributeDataImpl) other;
				return data.equals(b.data);
			}
			return data.equals(other.getAttributeMap());
		} finally {
			other.getAttributeLock().readLock().unlock();
			getAttributeLock().readLock().unlock();
		}
	}

	@Override
	public int getAttributeCount() {
		getAttributeLock().readLock().lock();
		try {
			return data.size();
		} finally {
			getAttributeLock().readLock().unlock();
		}
	}

	@Override
	public String[] getAttributes() {
		getAttributeLock().readLock().lock();
		try {
			return data.keySet().toArray(new String[data.size()]);
		} finally {
			getAttributeLock().readLock().unlock();
		}
	}

	@Override
	public void clearAttributes() {
		getAttributeLock().writeLock().lock();
		try {
			data.clear();
		} finally {
			getAttributeLock().writeLock().unlock();
		}
	}

	@Override
	public Map<String, Object> getAttributeMap() {
		getAttributeLock().readLock().lock();
		try {
			return new HashMap<>(data);
		} finally {
			getAttributeLock().readLock().unlock();
		}
	}
}