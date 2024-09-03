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
package com.pump.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This keeps a strong reference to keys and a weak reference to values.
 * <p>
 * Every time you interact with this object the {@link #purge()} is called to
 * clean up stale references. (In some cases you may want to also set up an
 * independent timer to also purge references.)
 * <p>
 * This map does not allow null keys or values.
 * 
 * @param <K>
 *            the key in a key/value pair. This maps keeps strong references to
 *            the keys.
 * @param <V>
 *            the value in a key/value pair. The map keeps weak references to
 *            the values.
 */
public class WeakValueMap<K, V> {

	/**
	 * This is the property attached to PropertyChangeEvents when the size of
	 * this map changes.
	 */
	public static final String PROPERTY_SIZE = WeakValueMap.class
			.getSimpleName() + "#size";

	/**
	 * This custom WeakReference also keeps track of our key.
	 *
	 * @param <K>
	 *            the key in a key/value pair. This object keeps a strong
	 *            reference to the key.
	 * @param <V>
	 *            the value in a key/value pair. This object is a weak reference
	 *            to the value.
	 */
	static class WeakValueMapValue<K, V> extends WeakReference<V> {
		protected K key;

		public WeakValueMapValue(K key, V referent, ReferenceQueue<? super V> q) {
			super(referent, q);
			this.key = key;
		}

	}

	protected List<PropertyChangeListener> propertyListeners = new ArrayList<>();
	protected Map<K, WeakValueMapValue<K, V>> map = new HashMap<>();

	/**
	 * All of our WeakValueReferences use this queue. This helps us efficiently
	 * track when elements are enqueued so we can purge stale data from our map.
	 */
	protected ReferenceQueue<? super V> queue = new ReferenceQueue<>();

	/**
	 * Add a PropertyChangeListener to this map. This is notified when the
	 * {@link #PROPERTY_SIZE} property changes.
	 */
	public void addPropertyListener(PropertyChangeListener l) {
		Objects.requireNonNull(l);
		synchronized (this) {
			propertyListeners.add(l);
		}
	}

	public void removePropertyListener(PropertyChangeListener l) {
		synchronized (this) {
			propertyListeners.remove(l);
		}
	}

	public V get(K key) {
		synchronized (this) {
			purge();
			WeakValueMapValue<K, V> ref = map.get(key);
			V value = ref == null ? null : ref.get();
			if (ref != null && value == null) {
				ref.enqueue();
				purge();
				return null;
			}
			return value;
		}
	}

	public int size() {
		synchronized (this) {
			purge();
			return map.size();
		}
	}

	public boolean containsKey(K key) {
		return get(key) != null;
	}

	public V put(K key, V value) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(value);
		int oldSize, newSize;
		V returnValue;
		synchronized (this) {
			purge();

			oldSize = map.size();
			WeakValueMapValue<K, V> newRef = new WeakValueMapValue<>(key,
					value, queue);
			WeakValueMapValue<K, V> oldRef = map.put(key, newRef);
			if (oldRef == null) {
				returnValue = null;
			} else {
				returnValue = oldRef.get();
				if (returnValue == null) {
					oldRef.enqueue();
					purge();
				}
			}
			newSize = map.size();
		}
		firePropertyEvent(PROPERTY_SIZE, oldSize, newSize);
		return returnValue;
	}

	/**
	 * Remove invalid references from this map.
	 * 
	 * @return the number of removed references.
	 */
	@SuppressWarnings("unchecked")
	public int purge() {
		int oldSize, newSize;
		int returnValue = 0;
		synchronized (this) {
			oldSize = map.size();
			WeakValueMapValue<K, V> ref = (WeakValueMapValue<K, V>) queue
					.poll();
			while (ref != null) {
				map.remove(ref.key);
				ref = (WeakValueMapValue<K, V>) queue.poll();
				returnValue++;
			}

			newSize = map.size();
		}
		firePropertyEvent(PROPERTY_SIZE, oldSize, newSize);
		return returnValue;
	}

	/**
	 * Fire PropertyChangeEvents to listeners.
	 */
	protected void firePropertyEvent(String propertyName, Object oldValue,
			Object newValue) {
		if (Objects.equals(oldValue, newValue))
			return;
		PropertyChangeListener[] array;
		synchronized (this) {
			array = propertyListeners
					.toArray(new PropertyChangeListener[propertyListeners
							.size()]);
		}
		for (PropertyChangeListener l : array) {
			l.propertyChange(new PropertyChangeEvent(this, propertyName,
					oldValue, newValue));
		}
	}

	/**
	 * Convert this map of weakly reference data to a regular java.util.Map with
	 * strong references.
	 */
	public Map<K, V> toMap() {
		synchronized (this) {
			purge();
			Map<K, V> returnValue = new HashMap<>(map.size());
			for (WeakValueMapValue<K, V> ref : map.values()) {
				K key = ref.key;
				V value = ref.get();
				if (value != null)
					returnValue.put(key, value);
			}
			return returnValue;
		}
	}

	/**
	 * Clear all the key/value pairs in this map.
	 */
	public void clear() {
		int oldSize;
		synchronized (this) {
			oldSize = size();
			if (oldSize == 0)
				return;

			for (WeakValueMapValue<K, V> ref : map.values()) {
				ref.enqueue();
			}
			while (true) {
				if (queue.poll() == null)
					break;
			}
			map.clear();

			// this shouldn't be necessary, but just to be thorough:
			queue = new ReferenceQueue<>();
		}

		firePropertyEvent(PROPERTY_SIZE, oldSize, 0);
	}

	@Override
	public String toString() {
		Map<K, V> map = toMap();
		return map.toString();
	}
}