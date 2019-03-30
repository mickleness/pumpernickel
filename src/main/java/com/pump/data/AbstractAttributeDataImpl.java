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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class provides the functionality of an AttributeData, but most of the
 * methods are protected.
 * <p>
 * The AttributeDataImpl will make these methods public, but some other
 * subclasses may want to more tightly control exposure to some methods. For
 * example: maybe external classes cannot call get/set for any arbitrary
 * property. In some subclasses we can control with explicit methods exactly
 * which attributes can be set.
 */
public class AbstractAttributeDataImpl implements Serializable {
	private static final long serialVersionUID = 1L;

	private static class PropertyChangeListenerDescriptor {
		PropertyChangeListener listener;
		String propertyName;

		PropertyChangeListenerDescriptor(PropertyChangeListener listener,
				String propertyName) {
			this.listener = listener;
			this.propertyName = propertyName;
		}

		boolean accepts(String propertyName) {
			return this.propertyName == null
					|| this.propertyName.equals(propertyName);
		}
	}

	protected Map<String, Object> data = new HashMap<>();
	private transient List<PropertyChangeListenerDescriptor> listeners;
	private transient ReadWriteLock lock;

	/**
	 * Create an empty AttributeDataImpl.
	 */
	public AbstractAttributeDataImpl() {
		initialize();
	}

	/**
	 * Create a AttributeDataImpl.
	 * 
	 * @param attributes
	 *            the attributes to copy into this object.
	 */
	public AbstractAttributeDataImpl(Map<String, Object> attributes) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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

	protected void addPropertyChangeListener(String propertyName,
			PropertyChangeListener pcl) {
		synchronized (listeners) {
			listeners.add(new PropertyChangeListenerDescriptor(pcl,
					propertyName));
		}
	}

	protected void addPropertyChangeListener(PropertyChangeListener pcl) {
		synchronized (listeners) {
			listeners.add(new PropertyChangeListenerDescriptor(pcl, null));
		}
	}

	protected void removePropertyChangeListener(PropertyChangeListener pcl) {
		synchronized (listeners) {
			Iterator<PropertyChangeListenerDescriptor> iter = listeners
					.iterator();
			while (iter.hasNext()) {
				PropertyChangeListenerDescriptor d = iter.next();
				if (d.listener == pcl) {
					iter.remove();
					return;
				}
			}
		}
	}

	protected ReadWriteLock getAttributeLock() {
		return lock;
	}

	protected <T> T setAttribute(Key<T> key, T value) {
		getAttributeLock().writeLock().lock();
		try {
			T oldValue = key.put(data, value);
			if (value == null && data.get(key.toString()) == null) {
				data.remove(key.toString());
			}
			if (!Objects.equals(value, oldValue))
				firePropertyChangeListeners(key.getName(), oldValue, value);
			return oldValue;
		} finally {
			getAttributeLock().writeLock().unlock();
		}
	}

	protected Object setAttribute(String key, Object value) {
		getAttributeLock().writeLock().lock();
		try {
			Object oldValue;
			if (value == null) {
				oldValue = data.get(key);
				data.remove(key);
			} else {
				oldValue = data.put(key, value);
			}
			if (!Objects.equals(value, oldValue))
				firePropertyChangeListeners(key, oldValue, value);
			return oldValue;
		} finally {
			getAttributeLock().writeLock().unlock();
		}
	}

	protected void firePropertyChangeListeners(String propertyName,
			Object oldValue, Object newValue) {
		PropertyChangeListenerDescriptor[] listenerArray;
		synchronized (listeners) {
			listenerArray = listeners
					.toArray(new PropertyChangeListenerDescriptor[listeners
							.size()]);
		}
		for (PropertyChangeListenerDescriptor pcld : listenerArray) {
			try {
				if (pcld.accepts(propertyName)) {
					pcld.listener.propertyChange(new PropertyChangeEvent(this,
							propertyName, oldValue, newValue));
				}
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

	protected void putAllAttributes(Map<String, Object> incomingData,
			boolean completeReplace) {
		getAttributeLock().writeLock().lock();
		try {
			for (Entry<String, Object> entry : incomingData.entrySet()) {
				Object oldValue = data.put(entry.getKey(), entry.getValue());
				if (!Objects.equals(oldValue, entry.getValue()))
					firePropertyChangeListeners(entry.getKey(), oldValue,
							entry.getValue());
			}
			if (completeReplace) {
				Collection<String> z = new HashSet<>(data.keySet());
				z.removeAll(incomingData.keySet());
				for (String unusedKey : z) {
					setAttribute(new Key<String>(String.class, unusedKey), null);
				}
			}
		} finally {
			getAttributeLock().writeLock().unlock();
		}
	}

	protected <T> T getAttribute(Key<T> key) {
		getAttributeLock().readLock().lock();
		try {
			return key.get(data);
		} finally {
			getAttributeLock().readLock().unlock();
		}
	}

	protected Object getAttribute(String key) {
		getAttributeLock().readLock().lock();
		try {
			return data.get(key);
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
		if (obj instanceof AbstractAttributeDataImpl) {
			return equals((AbstractAttributeDataImpl) obj);
		}
		if (obj instanceof AttributeData) {
			return equals((AttributeData) obj);
		}
		return false;
	}

	protected boolean equals(AttributeData other) {
		getAttributeLock().readLock().lock();
		other.getAttributeLock().readLock().lock();
		try {
			return getAttributeMap().equals(other.getAttributeMap());
		} finally {
			other.getAttributeLock().readLock().unlock();
			getAttributeLock().readLock().unlock();
		}
	}

	protected boolean equals(AbstractAttributeDataImpl other) {
		getAttributeLock().readLock().lock();
		other.getAttributeLock().readLock().lock();
		try {
			return getAttributeMap().equals(other.getAttributeMap());
		} finally {
			other.getAttributeLock().readLock().unlock();
			getAttributeLock().readLock().unlock();
		}
	}

	protected int getAttributeCount() {
		getAttributeLock().readLock().lock();
		try {
			return data.size();
		} finally {
			getAttributeLock().readLock().unlock();
		}
	}

	protected String[] getAttributes() {
		getAttributeLock().readLock().lock();
		try {
			return data.keySet().toArray(new String[data.size()]);
		} finally {
			getAttributeLock().readLock().unlock();
		}
	}

	protected void clearAttributes() {
		getAttributeLock().writeLock().lock();
		try {
			data.clear();
		} finally {
			getAttributeLock().writeLock().unlock();
		}
	}

	protected Map<String, Object> getAttributeMap() {
		getAttributeLock().readLock().lock();
		try {
			Map<String, Object> returnValue = new HashMap<>();
			for (Entry<String, Object> entry : data.entrySet()) {
				if (entry.getValue() != null)
					returnValue.put(entry.getKey(), entry.getValue());
			}
			return returnValue;
		} finally {
			getAttributeLock().readLock().unlock();
		}
	}
}