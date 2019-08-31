package com.pump.util;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

/**
 * This resembles a key/value pair map, except pairs will be purged based on
 * either how long they've been in this cache or based on the capacity of this
 * cache.
 * <p>
 * When the cache capacity is reached: the last key/value pair that was
 * stored/retrieved will be dropped. For example: if you store elements A-D in a
 * 4-element Cache, and you constantly retrieve element A (and nothing else),
 * then if you add an element E: B will be dropped.
 */
public class Cache<K, V> {

	/**
	 * This is used to monitor information about when a key/value pair was last
	 * accessed.
	 *
	 * @param <K>
	 * @param <V>
	 */
	static class CacheTicket<K, V> {
		long id;
		long timestamp = System.currentTimeMillis();
		final K key;
		V value;

		CacheTicket(Cache<?, ?> owner, K key, V value) {
			id = owner.getNextTicketID();
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			return timestamp + ", " + key + ", " + value + ", " + id;
		}
	}

	static Comparator<CacheTicket<?, ?>> activityComparator = new Comparator<CacheTicket<?, ?>>() {

		@Override
		public int compare(CacheTicket<?, ?> o1, CacheTicket<?, ?> o2) {
			return Long.compare(o1.id, o2.id);
		}

	};

	/**
	 * We have an optional static timer that periodically purges old data.
	 */
	static Timer timer;

	/**
	 * This task purges old data from the cache periodically, or it cancels
	 * itself the Cache that declared it is gc'ed.
	 */
	static class PurgeTimerTask extends TimerTask {
		WeakReference<Cache<?, ?>> cacheRef;

		public PurgeTimerTask(Cache<?, ?> cache) {
			cacheRef = new WeakReference<>(cache);
		}

		@Override
		public void run() {
			Cache<?, ?> cache = cacheRef.get();
			if (cache == null) {
				cancel();
			} else {
				cache.purge();
			}
		}
	}

	long idCtr;

	/**
	 * This stores the key/value information in this Cache.
	 */
	Map<K, CacheTicket<K, V>> keyToTickets;
	/**
	 * This stores the meta information regarding when a key/value pair was last
	 * accessed.
	 */
	TreeSet<CacheTicket<K, V>> orderedByActivityTickets = new TreeSet<>(
			activityComparator);

	/**
	 * The maximum number of key/value pairs this Cache should hold.
	 */
	int maxSize;

	/**
	 * The maximum number of milliseconds a key/value pair should exist in this
	 * cache.
	 */
	long maxTime;

	/**
	 * Create a new Cache that does not impose a time limit.
	 * 
	 * @param maxSize
	 *            the maximum number of elements this cache will accept. When
	 *            this limit is reached: the oldest elements are automatically
	 *            purged.
	 */
	public Cache(int maxSize) {
		this(maxSize, -1, -1);
	}

	/**
	 * Create a new Cache.
	 * 
	 * @param maxSize
	 *            the maximum number of elements this cache will accept. When
	 *            this limit is reached: the oldest elements are automatically
	 *            purged.
	 *            <p>
	 *            If you don't want a maximum number of elements: you can use
	 *            Integer.MAX_VALUE. Like any other Map: this may result in an
	 *            OutOfMemoryError.
	 * @param maxTime
	 *            the maximum number of milliseconds an element can stay in this
	 *            cache. When an element expires: it is purged either the next
	 *            time this cache is invoked or based on the purging interval.
	 *            <p>
	 *            If you don't want elements to ever expire: you can set this to
	 *            -1.
	 * @param maxTimePurgeInterval
	 *            the number of milliseconds between regular purges of this
	 *            cache. If this is negative then no timer is set up.
	 */
	public Cache(int maxSize, long maxTime, long maxTimePurgeInterval) {
		if (maxSize < 0)
			throw new IllegalArgumentException();

		keyToTickets = new HashMap<>(Integer.min(1000, maxSize));
		this.maxSize = maxSize;
		this.maxTime = maxTime;
		clear();

		if (maxTimePurgeInterval > 0) {
			synchronized (Cache.class) {
				if (timer == null) {
					timer = new Timer();
				}
			}
			TimerTask task = new PurgeTimerTask(this);
			timer.schedule(task, maxTimePurgeInterval);
		}
	}

	/**
	 * Remove all the elements in this Cache.
	 */
	public synchronized void clear() {
		keyToTickets.clear();
		orderedByActivityTickets.clear();
		idCtr = Long.MIN_VALUE;
	}

	/**
	 * Return the next ticket ID.
	 */
	long getNextTicketID() {
		if (idCtr == Long.MAX_VALUE) {
			// very rare, but possible on a server with a long uptime
			idCtr = Long.MIN_VALUE;

			// they're already sorted, but we want to reset their ID's to
			// start at MIN_VALUE:
			for (CacheTicket<K, V> t : orderedByActivityTickets) {
				t.id = idCtr++;
			}

		}
		return idCtr++;
	}

	/**
	 * Purge old records from this Cache, if possible.
	 */
	public synchronized void purge() {
		if (maxTime < 0) {
			return;
		}
		long t = System.currentTimeMillis();

		Iterator<CacheTicket<K, V>> iter = orderedByActivityTickets.iterator();
		while (iter.hasNext()) {
			CacheTicket<K, V> e = iter.next();
			long elapsed = t - e.timestamp;
			if (elapsed > maxTime) {
				iter.remove();
				keyToTickets.remove(e.key);
			} else {
				return;
			}
		}
	}

	/**
	 * Remove a key from this cache.
	 * 
	 * @param key
	 *            the key to remove
	 * @return the value this key was previously mapped to, or null if this key
	 *         was undefined.
	 */
	public synchronized V remove(K key) {
		Objects.requireNonNull(key);
		purge();

		if (orderedByActivityTickets.isEmpty())
			return null;

		CacheTicket<K, V> oldTicket = keyToTickets.get(key);
		if (oldTicket == null) {
			return null;
		}

		keyToTickets.remove(key);

		orderedByActivityTickets.remove(oldTicket);
		return oldTicket.value;
	}

	/**
	 * Return the value associated with a key.
	 * <p>
	 * Calling this method moves the key/value pair to the top of the
	 * most-recently-accessed list.
	 * 
	 * @param key
	 *            the key to retrieve.
	 * @return the value associated with a key.
	 */
	public synchronized V get(K key) {
		Objects.requireNonNull(key);
		purge();

		if (orderedByActivityTickets.isEmpty())
			return null;

		CacheTicket<K, V> mostRecentTicket = orderedByActivityTickets.last();
		if (mostRecentTicket != null && mostRecentTicket.key.equals(key)) {
			mostRecentTicket.timestamp = System.currentTimeMillis();
			return mostRecentTicket.value;
		} else {
			CacheTicket<K, V> oldTicket = keyToTickets.get(key);
			if (oldTicket == null) {
				return null;
			}

			CacheTicket<K, V> newTicket = new CacheTicket<>(this, key, null);
			keyToTickets.put(key, newTicket);

			orderedByActivityTickets.remove(oldTicket);
			orderedByActivityTickets.add(newTicket);

			newTicket.value = oldTicket.value;
			return newTicket.value;
		}
	}

	/**
	 * Store a key/value pair.
	 * <p>
	 * This key/value pair is moved to the top of the most-recently-accessed
	 * list.
	 * 
	 * @param key
	 *            the key used to store a value.
	 * @param newValue
	 *            the value to store.
	 * @return
	 */
	public synchronized V put(K key, V newValue) {
		purge();

		CacheTicket<K, V> mostRecentTicket = orderedByActivityTickets.isEmpty() ? null
				: orderedByActivityTickets.last();
		if (mostRecentTicket != null && mostRecentTicket.key.equals(key)) {
			mostRecentTicket.timestamp = System.currentTimeMillis();
			V returnValue = mostRecentTicket.value;
			mostRecentTicket.value = newValue;
			return returnValue;
		}

		CacheTicket<K, V> newTicket = new CacheTicket<>(this, key, newValue);
		CacheTicket<K, V> oldTicket = keyToTickets.put(key, newTicket);
		if (oldTicket != null) {
			orderedByActivityTickets.remove(oldTicket);
		}

		if (orderedByActivityTickets.size() >= maxSize) {
			Iterator<CacheTicket<K, V>> t = orderedByActivityTickets.iterator();
			CacheTicket<K, V> oldestTicket = t.next();
			t.remove();
			keyToTickets.remove(oldestTicket.key);
		}
		orderedByActivityTickets.add(newTicket);

		return oldTicket == null ? null : oldTicket.value;
	}

	/**
	 * Return all the keys in this Cache.
	 * <p>
	 * Note a Cache may drop elements at seemingly random times, so just because
	 * an element is in this collection does not guarantee that a call to
	 * retrieve that key will return non-null.
	 */
	public synchronized Collection<K> getKeys() {
		purge();

		Collection<K> keys = new HashSet<>();
		keys.addAll(keyToTickets.keySet());
		return keys;
	}

	/**
	 * Return the number of key/value pairs in this Cache.
	 */
	public synchronized int size() {
		purge();

		return keyToTickets.size();
	}

	/**
	 * Create a Map representing all the data in this Cache.
	 */
	public synchronized Map<K, V> toMap() {
		purge();

		Map<K, V> map = new HashMap<>();
		for (Entry<K, CacheTicket<K, V>> entry : keyToTickets.entrySet()) {
			map.put(entry.getKey(), entry.getValue().value);
		}
		return map;
	}

	@Override
	public int hashCode() {
		return toMap().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Cache) {
			Cache other = (Cache) obj;
			return toMap().equals(other.toMap());
		} else if (obj instanceof Map) {
			return toMap().equals((Map) obj);
		}
		return false;
	}

	@Override
	public String toString() {
		return toMap().toString();
	}

	/**
	 * Return true if this Cache contains a given Key.
	 * <p>
	 * Note a Cache may drop elements at seemingly random times, so just because
	 * this returns true does not guarantee that a call to retrieve that key
	 * will return non-null.
	 */
	public boolean contains(K key) {
		return get(key) != null;
	}
}
