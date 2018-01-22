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

import java.io.Serializable;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This map has a fixed capacity. This map includes a list of the priority of
 * elements. As you add or retrieve elements those elements are bumped to the
 * top of the priority list. If you have a capacity of 10 and you try to add an
 * 11th element: the element that was last interacted with will be dropped from
 * the table.
 * <p>
 * The current implementation of this class is not efficient, but because the
 * current intended use cases are for a small capacity (fewer than 20 elements)
 * the efficiency of the map operations is not a significant priority.
 */
public class FixedCapacityMap<K, V> implements Map<K, V>, Serializable {
	private static final long serialVersionUID = 1L;

	LinkedList<Entry<K, V>> entries = new LinkedList<>();
	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	int maxCapacity;

	/**
	 * Create a new FixedCapacityMap.
	 * 
	 * @param maxCapacity
	 *            the maximum number of elements this map should have at any
	 *            given. This "map" iterates over elements for all read/write
	 *            operations, so this number should be small (less than 20). If
	 *            you require a larger number: then it's time to redesign this
	 *            class.
	 */
	public FixedCapacityMap(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	@Override
	public int size() {
		lock.readLock().lock();
		try {
			return entries.size();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean isEmpty() {
		lock.readLock().lock();
		try {
			return entries.isEmpty();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean containsKey(Object key) {
		lock.readLock().lock();
		try {
			for (Entry<K, V> entry : entries) {
				if (Objects.equals(key, entry.getKey())) {
					return true;
				}
			}
			return false;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean containsValue(Object value) {
		lock.readLock().lock();
		try {
			for (Entry<K, V> entry : entries) {
				if (Objects.equals(value, entry.getValue())) {
					return true;
				}
			}
			return false;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public V get(Object key) {
		lock.readLock().lock();
		try {
			Iterator<Entry<K, V>> iter = entries.iterator();
			while (iter.hasNext()) {
				Entry<K, V> entry = iter.next();
				if (Objects.equals(key, entry.getKey())) {
					iter.remove();
					entries.add(entry);
					return entry.getValue();
				}
			}
			return null;
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public V put(K key, V value) {
		lock.writeLock().lock();
		try {
			Iterator<Entry<K, V>> iter = entries.iterator();
			while (iter.hasNext()) {
				Entry<K, V> entry = iter.next();
				if (Objects.equals(key, entry.getKey())) {
					iter.remove();
					entries.add(new SimpleEntry<K, V>(key, value));
					return entry.getValue();
				}
			}
			entries.add(new SimpleEntry<K, V>(key, value));
			while (entries.size() > maxCapacity) {
				entries.removeFirst();
			}
			return null;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public V remove(Object key) {
		lock.writeLock().lock();
		try {
			Iterator<Entry<K, V>> iter = entries.iterator();
			while (iter.hasNext()) {
				Entry<K, V> entry = iter.next();
				if (Objects.equals(key, entry.getKey())) {
					iter.remove();
					return entry.getValue();
				}
			}
			return null;
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		lock.writeLock().lock();
		try {
			entries.clear();
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public Set<K> keySet() {
		return new AbstractSet<K>() {

			@Override
			public Iterator<K> iterator() {
				final Iterator<Entry<K, V>> delegate = entries.iterator();
				return new Iterator<K>() {

					@Override
					public boolean hasNext() {
						lock.readLock().lock();
						try {
							return delegate.hasNext();
						} finally {
							lock.readLock().unlock();
						}
					}

					@Override
					public K next() {
						lock.readLock().lock();
						try {
							return delegate.next().getKey();
						} finally {
							lock.readLock().unlock();
						}
					}

					@Override
					public void remove() {
						lock.writeLock().lock();
						try {
							delegate.remove();
						} finally {
							lock.writeLock().unlock();
						}
					}
				};
			}

			@Override
			public int size() {
				lock.readLock().lock();
				try {
					return entries.size();
				} finally {
					lock.readLock().unlock();
				}
			}
		};
	}

	@Override
	public Collection<V> values() {
		return new AbstractSet<V>() {

			@Override
			public Iterator<V> iterator() {
				final Iterator<Entry<K, V>> delegate = entries.iterator();
				return new Iterator<V>() {

					@Override
					public boolean hasNext() {
						lock.readLock().lock();
						try {
							return delegate.hasNext();
						} finally {
							lock.readLock().unlock();
						}
					}

					@Override
					public V next() {
						lock.readLock().lock();
						try {
							return delegate.next().getValue();
						} finally {
							lock.readLock().unlock();
						}
					}

					@Override
					public void remove() {
						lock.writeLock().lock();
						try {
							delegate.remove();
						} finally {
							lock.writeLock().unlock();
						}
					}
				};
			}

			@Override
			public int size() {
				lock.readLock().lock();
				try {
					return entries.size();
				} finally {
					lock.readLock().unlock();
				}
			}
		};
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new AbstractSet<Entry<K, V>>() {

			@Override
			public Iterator<Entry<K, V>> iterator() {
				final Iterator<Entry<K, V>> delegate = entries.iterator();
				return new Iterator<Entry<K, V>>() {

					@Override
					public boolean hasNext() {
						lock.readLock().lock();
						try {
							return delegate.hasNext();
						} finally {
							lock.readLock().unlock();
						}
					}

					@Override
					public Entry<K, V> next() {
						lock.readLock().lock();
						try {
							return delegate.next();
						} finally {
							lock.readLock().unlock();
						}
					}

					@Override
					public void remove() {
						lock.writeLock().lock();
						try {
							delegate.remove();
						} finally {
							lock.writeLock().unlock();
						}
					}
				};
			}

			@Override
			public int size() {
				lock.readLock().lock();
				try {
					return entries.size();
				} finally {
					lock.readLock().unlock();
				}
			}
		};
	}
}