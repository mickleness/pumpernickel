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

import java.lang.ref.WeakReference;
import java.util.Collection;
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
 */
public class Cache<K, V> {

	/**
	 * This is the pool of cached data and the limits imposed on that cached
	 * data.
	 * <p>
	 * There are two limits: the maximum number of elements allowed, and the
	 * maximum amount of time an element can stay in the cache.
	 * <p>
	 * The size limit is always required, although you can technically make it
	 * Integer.MAX_VALUE (if you aren't worried about memory exceptions).
	 * <p>
	 * The time limit is optional (you can pass in -1 to avoid using it). It is
	 * always enforced when the cache is consulted, and you have the option to
	 * also set up a timer to regularly check for expired elements. (The timer
	 * is useful if your cache may end up sitting untouched for long periods of
	 * time. But if you're going to constantly store/retrieve elements from the
	 * cache during its lifetime then you don't need the timer.)
	 */
	public static class CachePool {

		/**
		 * We have an optional static timer that periodically purges old data.
		 */
		static Timer timer;

		/**
		 * This task purges old data from the cache periodically, or it cancels
		 * itself the Cache that declared it is gc'ed.
		 */
		static class PurgeTimerTask extends TimerTask {
			WeakReference<CachePool> cachePoolRef;

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public PurgeTimerTask(CachePool pool) {
				cachePoolRef = new WeakReference(pool);
			}

			@Override
			public void run() {
				CachePool pool = cachePoolRef.get();
				if (pool == null) {
					cancel();
				} else {
					pool.purge();
				}
			}
		}

		long idCtr;

		/**
		 * All tickets this pool monitors, sorted from oldest to newest. So
		 * we'll always add new tickets to the tail, and remove tickets from the
		 * head. (And we'll also randomly remove tickets from the middle.)
		 */
		@SuppressWarnings("rawtypes")
		TreeSet<CacheTicket> allTickets = new TreeSet<>();

		/**
		 * The maximum number of tickets/elements allowed in the CachePool.
		 */
		int maxSize;

		/**
		 * The maximum number of milliseconds an element should stay in the
		 * CachePool, or -1 if we should never remove elements based on a time
		 * limit.
		 */
		long maxTime;

		/**
		 * Create a new CachePool.
		 * 
		 * @param maxSize
		 *            the maximum number of elements this pool will accept. When
		 *            this limit is reached: the oldest elements are
		 *            automatically purged.
		 *            <p>
		 *            If you don't want a maximum number of elements: you can
		 *            use Integer.MAX_VALUE. (Like any other data structure:
		 *            this approach will probably result in an OutOfMemoryError
		 *            if you really try to add two million elements.)
		 * @param maxTime
		 *            the maximum number of milliseconds an element can stay in
		 *            this cache. When an element expires: it is purged either
		 *            the next time this pool is used or based on the purging
		 *            timer (see next argument).
		 *            <p>
		 *            If you don't want elements to ever expire: you can set
		 *            this to -1.
		 * @param maxTimePurgeInterval
		 *            the number of milliseconds between regular purges of this
		 *            cache. If this is negative then no timer is set up.
		 */
		public CachePool(int maxSize, long maxTime, long maxTimePurgeInterval) {
			if (maxSize <= 0)
				throw new IllegalArgumentException("maxSize (" + maxSize
						+ ") must be greater than zero");

			this.maxSize = maxSize;
			this.maxTime = maxTime;

			if (maxTimePurgeInterval > 0 && maxTime > 0) {
				synchronized (CachePool.class) {
					if (timer == null) {
						timer = new Timer();
					}
				}
				TimerTask task = new PurgeTimerTask(this);
				timer.schedule(task, maxTimePurgeInterval);
			}
		}

		/**
		 * Clear this pool. This clears data in all the Caches that use this
		 * pool.
		 */
		@SuppressWarnings("rawtypes")
		public synchronized void clear() {
			Iterator<CacheTicket> iter = allTickets.iterator();
			while (iter.hasNext()) {
				CacheTicket t = iter.next();
				t.cache.keyToTickets.clear();
			}
			allTickets.clear();
			idCtr = Long.MIN_VALUE;
		}

		/**
		 * Clears all the data in this pool from the given Cache.
		 */
		@SuppressWarnings("rawtypes")
		synchronized void clear(Cache cache) {
			Iterator<CacheTicket> iter = allTickets.iterator();
			while (iter.hasNext()) {
				CacheTicket t = iter.next();
				if (t.cache == cache)
					iter.remove();
			}
		}

		/**
		 * Return the next ticket ID.
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		synchronized long getNextTicketID() {
			if (idCtr == Long.MAX_VALUE) {
				// very rare, but possible on a server with a long uptime
				idCtr = Long.MIN_VALUE;

				// they're already sorted, but we want to reset their ID's to
				// start at MIN_VALUE:
				for (CacheTicket t : allTickets) {
					t.id = idCtr++;
				}

			}
			return idCtr++;
		}

		/**
		 * Purge old records from this Cache, if possible.
		 */
		@SuppressWarnings("rawtypes")
		synchronized void purge() {
			if (maxTime < 0) {
				return;
			}
			long t = System.currentTimeMillis();

			Iterator<CacheTicket> iter = allTickets.iterator();
			while (iter.hasNext()) {
				CacheTicket e = iter.next();
				long elapsed = t - e.timestamp;
				if (elapsed > maxTime) {
					iter.remove();
					e.cache.keyToTickets.remove(e.key);
				} else {
					return;
				}
			}
		}

		/**
		 * Add a ticket to this pool. If this pool is at capacity: adding a new
		 * ticket requires first removing the oldest ticket.
		 */
		@SuppressWarnings("rawtypes")
		synchronized void add(CacheTicket newTicket) {
			if (allTickets.size() >= maxSize) {
				CacheTicket oldestTicket = allTickets.pollFirst();
				oldestTicket.cache.keyToTickets.remove(oldestTicket.key);
			}
			allTickets.add(newTicket);
		}
	}

	/**
	 * This is meta information about a key/value pair, including: when it was
	 * added and which Cache it belongs to.
	 *
	 * @param <K>
	 * @param <V>
	 */
	@SuppressWarnings("rawtypes")
	static class CacheTicket<K, V> implements Comparable<CacheTicket> {
		long id;
		long timestamp = System.currentTimeMillis();
		final K key;
		V value;
		Cache<K, V> cache;

		@SuppressWarnings("unchecked")
		CacheTicket(Cache<?, ?> cache, K key, V value) {
			id = cache.cachePool.getNextTicketID();
			this.cache = (Cache) cache;
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			return timestamp + ", " + key + ", " + value + ", " + id;
		}

		@Override
		public int compareTo(CacheTicket t) {
			return Long.compare(id, t.id);
		}
	}

	/**
	 * This stores the key/value information in this Cache.
	 */
	Map<K, CacheTicket<K, V>> keyToTickets;
	CachePool cachePool;

	/**
	 * Create a new Cache that uses its own private CachePool that does not
	 * impose a time limit.
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
	 * Create a new Cache that uses its own private CachePool.
	 * 
	 * @param maxSize
	 *            the maximum number of elements this cache will accept. When
	 *            this limit is reached: the oldest elements are automatically
	 *            purged.
	 *            <p>
	 *            If you don't want a maximum number of elements: you can use
	 *            Integer.MAX_VALUE. (Like any other data structure: this
	 *            approach will probably result in an OutOfMemoryError if you
	 *            really try to add two million elements.)
	 * @param maxTime
	 *            the maximum number of milliseconds an element can stay in this
	 *            cache. When an element expires: it is purged either the next
	 *            time this cache is used or based on the purging timer (see
	 *            next argument).
	 *            <p>
	 *            If you don't want elements to ever expire: you can set this to
	 *            -1.
	 * @param maxTimePurgeInterval
	 *            the number of milliseconds between regular purges of this
	 *            cache. If this is negative then no timer is set up.
	 */
	public Cache(int maxSize, long maxTime, long maxTimePurgeInterval) {
		this(new CachePool(maxSize, maxTime, maxTimePurgeInterval));
	}

	/**
	 * Create a new Cache.
	 * 
	 * @param cachePool
	 *            the CachePool that regulates how many elements to store and
	 *            how long to store them.
	 */
	public Cache(CachePool cachePool) {
		Objects.requireNonNull(cachePool);
		this.cachePool = cachePool;

		keyToTickets = new HashMap<>(Integer.min(1000, cachePool.maxSize));
	}

	/**
	 * Return the CachePool this Cache uses.
	 * <p>
	 * Multiple Caches may refer to the same pool.
	 */
	public CachePool getCachePool() {
		return cachePool;
	}

	/**
	 * Remove all the elements in this Cache.
	 */
	public void clear() {
		synchronized (cachePool) {
			keyToTickets.clear();
			cachePool.clear(this);
		}
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
	@SuppressWarnings("unchecked")
	public V get(K key) {
		Objects.requireNonNull(key);
		synchronized (cachePool) {
			cachePool.purge();

			if (keyToTickets.isEmpty())
				return null;

			CacheTicket<K, V> mostRecentTicket = cachePool.allTickets.last();
			if (mostRecentTicket != null && mostRecentTicket.cache == this
					&& mostRecentTicket.key.equals(key)) {
				mostRecentTicket.timestamp = System.currentTimeMillis();
				return mostRecentTicket.value;
			} else {
				CacheTicket<K, V> oldTicket = keyToTickets.get(key);
				if (oldTicket == null) {
					return null;
				}

				CacheTicket<K, V> newTicket = new CacheTicket<>(this, key, null);
				keyToTickets.put(key, newTicket);

				cachePool.allTickets.remove(oldTicket);
				cachePool.allTickets.add(newTicket);

				newTicket.value = oldTicket.value;
				return newTicket.value;
			}
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
	@SuppressWarnings("unchecked")
	public synchronized V put(K key, V newValue) {
		synchronized (cachePool) {
			cachePool.purge();

			CacheTicket<K, V> mostRecentTicket = cachePool.allTickets.isEmpty() ? null
					: cachePool.allTickets.last();
			if (mostRecentTicket != null && mostRecentTicket.cache == this
					&& mostRecentTicket.key.equals(key)) {
				mostRecentTicket.timestamp = System.currentTimeMillis();
				V returnValue = mostRecentTicket.value;
				mostRecentTicket.value = newValue;
				return returnValue;
			}

			CacheTicket<K, V> newTicket = new CacheTicket<>(this, key, newValue);
			CacheTicket<K, V> oldTicket = keyToTickets.put(key, newTicket);
			if (oldTicket != null) {
				cachePool.allTickets.remove(oldTicket);
			}

			cachePool.add(newTicket);

			return oldTicket == null ? null : oldTicket.value;
		}
	}

	/**
	 * Return all the keys in this Cache.
	 * <p>
	 * Note a Cache may drop elements at seemingly random times, so just because
	 * an element is in this collection does not guarantee that a call to
	 * retrieve that key will return non-null.
	 */
	public synchronized Collection<K> getKeys() {
		synchronized (cachePool) {
			cachePool.purge();

			Collection<K> keys = new HashSet<>();
			keys.addAll(keyToTickets.keySet());
			return keys;
		}
	}

	/**
	 * Return the number of key/value pairs in this Cache.
	 */
	public synchronized int size() {
		synchronized (cachePool) {
			cachePool.purge();

			return keyToTickets.size();
		}
	}

	/**
	 * Create a Map representing all the data in this Cache.
	 */
	public synchronized Map<K, V> toMap() {
		synchronized (cachePool) {
			cachePool.purge();

			Map<K, V> map = new HashMap<>();
			for (Entry<K, CacheTicket<K, V>> entry : keyToTickets.entrySet()) {
				map.put(entry.getKey(), entry.getValue().value);
			}
			return map;
		}
	}

	@Override
	public int hashCode() {
		return toMap().hashCode();
	}

	@SuppressWarnings("rawtypes")
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
