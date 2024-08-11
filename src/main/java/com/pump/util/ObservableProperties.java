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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This manages multiple sets of properties, including listeners.
 * <p>
 * Eventually we might want to ramp this up to rival the {@link ObservableList}
 * in terms of synchronized modification safeguards and
 * <code>RecursiveListenerModificationExceptions</code>.
 * <p>
 * Notes this includes parameterized keys, and it is technically possible to
 * have two keys that use the same name but map to different types.
 */
public class ObservableProperties implements Serializable {
	private static final long serialVersionUID = 1L;

	public static class Edit {

	}

	private Set<Edit> activeEdits = new HashSet<>();
	private LinkedList<QueueElement> eventQueue = new LinkedList<>();

	/**
	 * Begin recording an edit that suspends all listeners until
	 * {@link #endEdit(Edit)} is called.
	 * <p>
	 * This is needed when it is dangerous for a listener to get an incomplete
	 * picture of the state of this object. For example if you are changing the
	 * VALUE key to 5 and the MAXIMUM key to 8, if a listener were notified
	 * after the first step and the MAXIMUM value was an older value (such as
	 * 3), then it might throw an exception because it assumes the VALUE is
	 * always less than the MAXIMUM.
	 * 
	 * @return a unique identifier to pass to <code>endEdit()</code> when your
	 *         changes are complete.
	 */
	public Edit beginEdit() {
		synchronized (activeEdits) {
			Edit newEdit = new Edit();
			activeEdits.add(newEdit);
			return newEdit;
		}
	}

	/**
	 * This must be called after {@link #beginEdit()} to reactive listeners for
	 * this object.
	 * 
	 * @param endedEdit
	 *            a token created by a call to {@link #beginEdit()}. When there
	 *            are no unresolved edits: all pending PropertyChangeEvents will
	 *            be flushed.
	 */
	public void endEdit(Edit endedEdit) {
		synchronized (activeEdits) {
			if (!activeEdits.remove(endedEdit)) {
				throw new IllegalStateException("This edit already ended.");
			}
			if (eventQueue.size() > 0) {
				clearEventQueue();
			}
		}
	}

	/**
	 * This represents a future PropertyChangeEvent.
	 */
	private static class QueueElement {
		PropertyGroup group;
		Key<?> key;
		Object oldValue;
		Object newValue;

		QueueElement(PropertyGroup group, Key<?> key, Object oldValue,
				Object newValue) {
			this.group = group;
			this.key = key;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}
	}

	/** Flushes all QueueElements from the eventQueue. */
	protected void clearEventQueue() {
		synchronized (eventQueue) {
			while (eventQueue.size() > 0) {
				QueueElement q = eventQueue.removeFirst();

				Listener[] listenerArray;
				synchronized (q.group.listeners) {
					listenerArray = q.group.listeners
							.toArray(new Listener[q.group.listeners.size()]);
				}

				// TODO: consolidate QueueElements so we only include the latest
				// change
				for (Listener l : listenerArray) {
					boolean keysMatch = l.key == null || l.key.equals(q.key);
					if (keysMatch) {
						try {
							l.pcl.propertyChange(new PropertyChangeEvent(
									ObservableProperties.this, q.key.keyName,
									q.oldValue, q.newValue));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	/** Wrap an ChangeListener in a PropertyChangeListener. */
	private static class ChangeListenerWrapper implements
			PropertyChangeListener {
		ChangeListener changeListener;

		ChangeListenerWrapper(ChangeListener l) {
			changeListener = l;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			changeListener.stateChanged(new ChangeEvent(evt.getSource()));
		}
	}

	/** Wrap an ActionListener in a PropertyChangeListener. */
	private static class ActionListenerWrapper implements
			PropertyChangeListener {
		ActionListener actionListener;

		ActionListenerWrapper(ActionListener l) {
			actionListener = l;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			actionListener.actionPerformed(new ActionEvent(evt.getSource(), 0,
					evt.getPropertyName()));
		}
	}

	/**
	 * This can be used for bounds checking as values are about to be assigned.
	 * It is an optional mechanism to automate IllegalArgumentExceptions.
	 * 
	 * @param <T>
	 *            the type of argument this checks.
	 */
	public abstract static class BoundsChecker<T> implements Serializable {
		private static final long serialVersionUID = 1L;

		/**
		 * Check that a potential value is an accepted value.
		 * 
		 * @param t
		 *            the value to check.
		 * @param key
		 *            the key this value will be assigned to.
		 * 
		 * @throws IllegalArgumentException
		 *             if the argument is somehow not an acceptable property
		 *             value.
		 */
		public abstract void check(T t, Key<T> key)
				throws IllegalArgumentException;
	}

	/** This throws a NullPointerException if the value is null. */
	@SuppressWarnings("rawtypes")
	public static class NonNullBoundsChecker extends BoundsChecker implements
			Serializable {
		private static final long serialVersionUID = 1L;

		public void check(Object t, Key key) throws IllegalArgumentException {
			if (t == null)
				throw new NullPointerException(key.getKeyName()
						+ " cannot be null");
		}
	}

	/**
	 * This makes sure a value is within a set of candidates.
	 */
	public static class SetBoundsChecker<T> extends BoundsChecker<T> {
		private static final long serialVersionUID = 1L;

		Collection<T> candidates;

		public SetBoundsChecker(T... candidates) {
			this(Arrays.asList(candidates));
		}

		public SetBoundsChecker(Collection<T> candidates) {
			Objects.requireNonNull(candidates);
			this.candidates = candidates;
		}

		@Override
		public void check(T t, Key<T> key) throws IllegalArgumentException {
			if (!candidates.contains(t))
				throw new IllegalArgumentException("The value \"" + t
						+ "\" is not allowed. Only these values are allowed: "
						+ candidates);
		}

	}

	/**
	 * This applies bound checking for numeric values.
	 */
	public static class NumberBoundsChecker extends BoundsChecker<Number> {
		private static final long serialVersionUID = 1L;

		final Number min, max;
		final boolean includeMin, includeMax;

		/**
		 * 
		 * @param min
		 *            the minimum accepted value.
		 * @param max
		 *            the maximum accepted value.
		 * @param includeMin
		 *            if true then this applies a "greater than or equal to"
		 *            check. If false then this simply applies a "greater than"
		 *            check, so if someone tries to use a value that is exactly
		 *            equal to the minimum: it will fail.
		 * @param includeMax
		 *            if true then this applies a "less than or equal to" check.
		 *            If false then this simply applies a "less than" check, so
		 *            if someone tries to use a value that is exactly equal to
		 *            the maximum: it will fail.
		 */
		public NumberBoundsChecker(Number min, Number max, boolean includeMin,
				boolean includeMax) {
			this.min = min;
			this.max = max;
			this.includeMin = includeMin;
			this.includeMax = includeMax;
		}

		/** @return the maximum of this NumberBoundsChecker. */
		public Number getMin() {
			return min;
		}

		/** @return the minimum of this NumberBoundsChecker. */
		public Number getMax() {
			return max;
		}

		@Override
		public void check(Number t, Key<Number> key)
				throws IllegalArgumentException {
			boolean bad = (includeMin && t.doubleValue() < min.doubleValue())
					|| ((!includeMin) && t.doubleValue() <= min.doubleValue())
					|| (includeMax && t.doubleValue() > max.doubleValue())
					|| ((!includeMax) && t.doubleValue() >= max.doubleValue());
			if (bad)
				throw new IllegalArgumentException("the value \""
						+ key.toString() + "\" (" + t + ") must be within "
						+ toString());
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			if (includeMin) {
				sb.append('[');
			} else {
				sb.append('(');
			}
			sb.append(min);
			sb.append(", ");
			sb.append(max);
			if (includeMax) {
				sb.append(']');
			} else {
				sb.append(')');
			}
			return sb.toString();
		}

	};

	/**
	 * A key for a key/value pair. The reason keys are not simply
	 * <code>Strings</code> is that these objects have a parameterized type: so
	 * casting is not necessary.
	 * <p>
	 * You can technically have two keys with the same name and different types
	 * in the same property set.
	 * <p>
	 * It is recommended (but not necessary) that keys be stored as public
	 * static fields.
	 *
	 * @param <T>
	 *            the type of value this key should map to.
	 */
	public static class Key<T> implements Serializable, Comparable<Key<?>> {
		private static final long serialVersionUID = 1L;

		final String keyName;
		final Class<T> type;
		final BoundsChecker<T> checker;

		/**
		 * Create a new Key.
		 * 
		 * @param keyName
		 *            the name of this key.
		 * @param type
		 *            the parameterized type of this key. (Is there a way to
		 *            ascertain this by reflection? Stating this here seems
		 *            redundant, but necessary.)
		 */
		public Key(String keyName, Class<T> type) {
			this(keyName, type, null);
		}

		/**
		 * Create a new Key.
		 * 
		 * @param keyName
		 *            the name of this key.
		 * @param type
		 *            the parameterized type of this key. (Is there a way to
		 *            ascertain this by reflection? Stating this here seems
		 *            redundant, but necessary.)
		 * @param checker
		 *            an optional {@link BoundsChecker} to throw an exception if
		 *            someone attempts to assign a value that isn't allowed.
		 */
		public Key(String keyName, Class<T> type, BoundsChecker checker) {
			this.keyName = keyName;
			this.type = type;
			this.checker = checker;
		}

		/**
		 * @return the class of the object this key requires.
		 */
		public Class<T> getType() {
			return type;
		}

		/**
		 * Retrieve this key from a map of values or return the default value
		 * provided if the key is not identified.
		 * <p>
		 * Note the key name in the map does not have to be exact. For example
		 * if the map was generated via
		 * {@link com.pump.util.ObservableProperties#getMap(boolean, boolean, String...)}
		 * then in might have text prepended or appended to the technical key
		 * name. As soon as a partial match is identified that matches this key
		 * name (that also is consistent with the type required by this Key):
		 * then that value is returned. For this reason: key names should err on
		 * the side of being more specific/thorough than not.
		 * 
		 * @param map
		 *            the map to check against.
		 * @param defaultValue
		 *            the optional default value if the map doesn't contain the
		 *            key.
		 * @return the value of the key provided, or the default value if no
		 *         value is identified.
		 */
		public T get(Map<String, Object> map, T defaultValue) {
			Object v = map.get(keyName);
			if (v != null && type.isInstance(v)) {
				return (T) v;
			}
			synchronized (map) {
				Iterator<String> keys = map.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					if (key.toLowerCase().contains(keyName.toLowerCase())) {
						v = map.get(key);
						if (v != null && type.isInstance(v)) {
							return (T) v;
						}
					}
				}
			}
			return defaultValue;
		}

		/**
		 * Create a new Key with a numeric {@link BoundsChecker}.
		 * 
		 * @param keyName
		 *            the name of this key.
		 * @param type
		 *            the parameterized type of this key. (Is there a way to
		 *            ascertain this by reflection? Stating this here seems
		 *            redundant, but necessary.)
		 * @param min
		 *            the minimum accepted value.
		 * @param max
		 *            the maximum accepted value.
		 * @param includeMin
		 *            if true then this applies a "greater than or equal to"
		 *            check. If false then this simply applies a "greater than"
		 *            check, so if someone tries to use a value that is exactly
		 *            equal to the minimum: it will fail.
		 * @param includeMax
		 *            if true then this applies a "less than or equal to" check.
		 *            If false then this simply applies a "less than" check, so
		 *            if someone tries to use a value that is exactly equal to
		 *            the maximum: it will fail.
		 */
		public Key(String keyName, Class<T> type, Number min, Number max,
				boolean includeMin, boolean includeMax) {
			this(keyName, type, new NumberBoundsChecker(min, max, includeMin,
					includeMax));
		}

		/**
		 * @return true if a PropertyChangeEvent relates to this Key.
		 * @param evt
		 *            the evt to check against.
		 */
		public boolean matches(PropertyChangeEvent evt) {
			return evt.getPropertyName().equals(keyName)
					|| evt.getPropertyName().endsWith("#" + keyName);
		}

		/** @return the optional BoundsChecker for this key; this may be null. */
		public BoundsChecker<T> getBoundsChecker() {
			return checker;
		}

		@Override
		public int hashCode() {
			return keyName.hashCode();
		}

		@Override
		public boolean equals(Object t) {
			if (t instanceof Key<?>) {
				Key<?> other = (Key<?>) t;
				return compareTo(other) == 0;
			}
			return false;
		}

		/**
		 * Returns the original key name associated with this key.
		 * 
		 * @return the original key name associated with this key.
		 */
		public String getKeyName() {
			return keyName;
		}

		/**
		 * The key name property.
		 * 
		 */
		@Override
		public String toString() {
			return keyName;
		}

		public int compareTo(Key<?> o) {
			int c = keyName.compareTo(o.keyName);
			if (c == 0) {
				if (type == null && o.type == null)
					return 0;
				if (type == null)
					return -1;
				if (o.type == null)
					return 1;

				if (type.isAssignableFrom(o.type)
						|| o.type.isAssignableFrom(type)) {
					// this is OK
				} else {
					c = type.getName().compareTo(o.type.getName());
				}
			}
			return c;
		}
	}

	@Override
	public String toString() {
		return getMap(true, true, (String[]) null).toString();
	}

	private static class Listener {
		final Key<?> key;
		final PropertyChangeListener pcl;
		final Object id;

		Listener(Key<?> key, PropertyChangeListener pcl, Object id) {
			this.key = key;
			this.pcl = pcl;
			this.id = id;
		}
	}

	private class PropertyGroup implements Serializable {
		private static final long serialVersionUID = 1L;

		Map<Key<?>, Object> map = new HashMap<Key<?>, Object>();

		transient List<Listener> listeners;

		boolean addPropertyChangeListener(Key<?> key,
				PropertyChangeListener pcl, Object identifier) {
			if (listeners == null)
				listeners = new ArrayList<Listener>();
			synchronized (listeners) {
				for (int a = 0; a < listeners.size(); a++) {
					Listener l = listeners.get(a);
					if (l.id == identifier && l.pcl == pcl
							&& equals(l.key, key)) {
						return false;
					} else if (l.id == identifier && equals(l.key, key)) {
						throw new IllegalArgumentException(
								"This identifier is already declared: "
										+ identifier);
					}
				}

				listeners.add(new Listener(key, pcl, identifier));
			}
			return true;
		}

		boolean removePropertyChangeListener(Key<?> key, Object identifier) {
			if (listeners == null)
				return false;
			synchronized (listeners) {
				for (int a = 0; a < listeners.size(); a++) {
					Listener l = listeners.get(a);
					if (l.id == identifier && equals(l.key, key)) {
						listeners.remove(a);
						return true;
					}
				}
			}
			return false;
		}

		void clear() {
			Key<?>[] keys = map.keySet().toArray(new Key[map.size()]);
			for (Key<?> key : keys) {
				set(key, null, false);
			}
		}

		<T> T get(Key<T> key) {
			return (T) map.get(key);
		}

		<T> T set(Key<T> key, T newValue, boolean applyChecker) {
			if (key.checker != null && applyChecker) {
				key.checker.check(newValue, key);
			}

			T oldValue = get(key);
			if (newValue == null) {
				map.remove(key);
			} else {
				map.put(key, newValue);
			}

			fireListeners(key, oldValue, newValue);

			return oldValue;
		}

		void fireListeners(Key<?> key, Object oldValue, Object newValue) {
			if (equals(oldValue, newValue) || listeners == null)
				return;

			synchronized (eventQueue) {
				eventQueue.add(new QueueElement(this, key, oldValue, newValue));
			}

			synchronized (activeEdits) {
				if (activeEdits.size() == 0) {
					clearEventQueue();
				}
			}
		}

		/**
		 * Copied from Objects.equals() for Java 1.6 compatibility: Returns
		 * {@code true} if the arguments are equal to each other and
		 * {@code false} otherwise. Consequently, if both arguments are
		 * {@code null}, {@code true} is returned and if exactly one argument is
		 * {@code null}, {@code false} is returned. Otherwise, equality is
		 * determined by using the {@link Object#equals equals} method of the
		 * first argument.
		 *
		 * @param a
		 *            an object
		 * @param b
		 *            an object to be compared with {@code a} for equality
		 * @return {@code true} if the arguments are equal to each other and
		 *         {@code false} otherwise
		 * @see Object#equals(Object)
		 */
		public boolean equals(Object a, Object b) {
			return (a == b) || (a != null && a.equals(b));
		}
	}

	/**
	 * The string "default". This is recommended for a group of properties that
	 * represent vital elements of an object, and are serializable.
	 */
	public static final String DEFAULT = "default";

	/**
	 * The string "transient". This is recommended for a group of properties
	 * that are not meant to be serialized (cached indices, renderings, etc.).
	 */
	public static final String TRANSIENT = "transient";

	/** All possible PropertyGroups */
	Map<String, PropertyGroup> groupMap = new HashMap<String, PropertyGroup>();

	/** Possible group names. If null: then all group names are allowed. */
	final Set<String> possibleGroupNames;

	/** Create a new ObservableProperties that will accept any group ID. */
	public ObservableProperties() {
		this((String[]) null);
	}

	/** Clone this ObservableProperties. */
	public ObservableProperties(ObservableProperties p) {
		this(p.possibleGroupNames == null ? (String[]) null
				: p.possibleGroupNames.toArray(new String[p.possibleGroupNames
						.size()]));
		for (String group : p.groupMap.keySet()) {
			PropertyGroup pg = p.groupMap.get(group);
			for (Key key : pg.map.keySet()) {
				Object value = pg.map.get(key);
				set(group, key, value);
			}
		}
	}

	/**
	 * Create a new ObservableProperties with a limited set of possible group
	 * IDs.
	 * 
	 * @param groupNames
	 *            the group IDs this object allows. (If null: then all group IDs
	 *            are allowed.) If non-null: then attempting to access a group
	 *            ID not in this set will result in an exception.
	 */
	public ObservableProperties(String... groupNames) {
		if (groupNames == null) {
			possibleGroupNames = null;
		} else {
			possibleGroupNames = new HashSet<String>();
			for (String t : groupNames) {
				possibleGroupNames.add(t);
			}
		}
	}

	/**
	 * @return all the group names currently in use. (This is not related to the
	 *         Strings passed during construction that limit the possible group
	 *         names.)
	 */
	public synchronized SortedSet<String> getGroupNames() {
		TreeSet<String> returnValue = new TreeSet<String>();
		for (String groupID : groupMap.keySet()) {
			returnValue.add(groupID);
		}
		return returnValue;
	}

	/**
	 * Add a PropertyChangeListener for {@link #DEFAULT} properties.
	 * 
	 * @param pcl
	 *            the listener to add.
	 */
	public synchronized void addListener(PropertyChangeListener pcl) {
		addListener(DEFAULT, pcl);
	}

	/**
	 * Remove a PropertyChangeListener for {@link #DEFAULT} properties.
	 * 
	 * @param pcl
	 *            the listener to remove.
	 */
	public synchronized void removeListener(PropertyChangeListener pcl) {
		removeListener(DEFAULT, pcl);
	}

	/**
	 * Add a PropertyChangeListener for a given group of properties.
	 * 
	 * @param propertyGroup
	 *            the group of properties this relates to. For example:
	 *            {@link #DEFAULT} or {@link #TRANSIENT}.
	 * @param pcl
	 *            the listener to add.
	 */
	public synchronized void addListener(String propertyGroup,
			PropertyChangeListener pcl) {
		addListener(propertyGroup, null, pcl);
	}

	/**
	 * Add a PropertyChangeListener for a key in a given group of properties.
	 * 
	 * @param propertyGroup
	 *            the group of properties this relates to. For example:
	 *            {@link #DEFAULT} or {@link #TRANSIENT}.
	 * @param key
	 *            an optional key that limits what this listener which notified
	 *            about. If null: then this listener will be notified for all
	 *            changes.
	 * @param pcl
	 *            the listener to add.
	 */
	public synchronized void addListener(String propertyGroup, Key<?> key,
			PropertyChangeListener pcl) {
		PropertyGroup group = getGroup(propertyGroup);
		group.addPropertyChangeListener(key, pcl, pcl);
	}

	/**
	 * Add a ChangeListener for a key in a given group of properties.
	 * 
	 * @param propertyGroup
	 *            the group of properties this relates to. For example:
	 *            {@link #DEFAULT} or {@link #TRANSIENT}.
	 * @param key
	 *            an optional key that limits what this listener which notified
	 *            about. If null: then this listener will be notified for all
	 *            changes.
	 * @param changeListener
	 *            the ChangeListener to be notified
	 */
	public synchronized void addListener(String propertyGroup, Key<?> key,
			ChangeListener changeListener) {
		PropertyGroup group = getGroup(propertyGroup);
		group.addPropertyChangeListener(key, new ChangeListenerWrapper(
				changeListener), changeListener);
	}

	/**
	 * Add an ActionListener for a key in a given group of properties.
	 * 
	 * @param propertyGroup
	 *            the group of properties this relates to. For example:
	 *            {@link #DEFAULT} or {@link #TRANSIENT}.
	 * @param key
	 *            an optional key that limits what this listener which notified
	 *            about. If null: then this listener will be notified for all
	 *            changes.
	 * @param actionListener
	 *            the ActionListener to be notified.
	 */
	public synchronized void addListener(String propertyGroup, Key<?> key,
			ActionListener actionListener) {
		PropertyGroup group = getGroup(propertyGroup);
		group.addPropertyChangeListener(key, new ActionListenerWrapper(
				actionListener), actionListener);
	}

	/**
	 * Return all the PropertyChangeListeners that listen for changes to DEFAULT
	 * properties.
	 */
	public synchronized PropertyChangeListener[] getPropertyListeners() {
		return getPropertyListeners(DEFAULT);
	}

	/**
	 * Return all the PropertyChangeListeners that listen for changes to a given
	 * group of properties.
	 * 
	 * @param propertyGroup
	 *            the group of properties this relates to. For example:
	 *            {@link #DEFAULT} or {@link #TRANSIENT}.
	 */
	public synchronized PropertyChangeListener[] getPropertyListeners(
			String propertyGroup) {
		PropertyGroup group = getGroup(propertyGroup);
		if (group.listeners == null)
			return new PropertyChangeListener[] {};

		synchronized (group.listeners) {
			int ctr = 0;
			for (Listener l : group.listeners) {
				if (l.pcl instanceof ActionListenerWrapper)
					continue;
				if (l.pcl instanceof ChangeListenerWrapper)
					continue;
				ctr++;
			}
			PropertyChangeListener[] returnValue = new PropertyChangeListener[ctr];
			ctr = 0;
			for (Listener l : group.listeners) {
				if (l.pcl instanceof ActionListenerWrapper)
					continue;
				if (l.pcl instanceof ChangeListenerWrapper)
					continue;
				returnValue[ctr++] = l.pcl;
			}
			return returnValue;
		}
	}

	/**
	 * Remove a ChangeListener for a given group of properties.
	 * 
	 * @param propertyGroup
	 *            the group this listener belongs to.
	 * @param key
	 *            the key this listener relates to.
	 * @param changeListener
	 *            the listener to remove.
	 */
	public synchronized void removeListener(String propertyGroup, Key<?> key,
			ChangeListener changeListener) {
		PropertyGroup group = getGroup(propertyGroup);
		group.removePropertyChangeListener(key, changeListener);
	}

	/**
	 * Remove an ActionListener for a given group of properties.
	 * 
	 * @param propertyGroup
	 *            the group this listener belongs to.
	 * @param key
	 *            the key this listener relates to.
	 * @param actionListener
	 *            the listener to remove.
	 */
	public synchronized void removeListener(String propertyGroup, Key<?> key,
			ActionListener actionListener) {
		PropertyGroup group = getGroup(propertyGroup);
		group.removePropertyChangeListener(key, actionListener);
	}

	/**
	 * Remove a PropertyChangeListener for a given group of properties.
	 * 
	 * @param propertyGroup
	 *            the group this listener belongs to.
	 * @param pcl
	 *            the listener to remove.
	 */
	public synchronized void removeListener(String propertyGroup,
			PropertyChangeListener pcl) {
		removeListener(propertyGroup, null, pcl);
	}

	/**
	 * Remove a PropertyChangeListener for a given group of properties.
	 * 
	 * @param propertyGroup
	 *            the group this listener belongs to.
	 * @param key
	 *            the key this listener relates to.
	 * @param pcl
	 *            the listener to remove.
	 */
	public synchronized void removeListener(String propertyGroup, Key<?> key,
			PropertyChangeListener pcl) {
		PropertyGroup group = getGroup(propertyGroup);
		group.removePropertyChangeListener(key, pcl);
	}

	/**
	 * Set a property in a given property group.
	 * 
	 * @param propertyGroup
	 *            the property group to consult. (Recommended values are DEFAULT
	 *            or TRANSIENT.) This cannot be null. Each ObservableProperties
	 *            object may be constructed with a finite set of acceptable
	 *            property groups: if that is the case for this object and this
	 *            group ID is not part of this set, then an exception is thrown.
	 * @param key
	 *            the key to set.
	 * @param value
	 *            the value to set. If null: then this key is removed.
	 * @return the previous value.
	 */
	public synchronized <T> T set(String propertyGroup, Key<T> key, T value) {
		if (propertyGroup == null) {
			System.err
					.println("The propertyGroup cannot be null. When in doubt, use DEFAULT.");
			throw new NullPointerException();
		}
		PropertyGroup group = getGroup(propertyGroup);
		return group.set(key, value, true);
	}

	/**
	 * Return a property from a given property group.
	 * 
	 * @param propertyGroup
	 *            the property group to consult. (Recommended values are DEFAULT
	 *            or TRANSIENT.) This cannot be null. Each ObservableProperties
	 *            object may be constructed with a finite set of acceptable
	 *            property groups: if that is the case for this object and this
	 *            group ID is not part of this set, then an exception is thrown.
	 * @param key
	 *            the key to retrieve.
	 * @return the current value.
	 */
	public synchronized <T> T get(String propertyGroup, Key<T> key) {
		if (propertyGroup == null) {
			System.err
					.println("The propertyGroup cannot be null. When in doubt, use DEFAULT.");
			throw new NullPointerException("propertyGroup cannot be null");
		}
		PropertyGroup group = getGroup(propertyGroup);
		return group.get(key);
	}

	/**
	 * @return a condensed map of all group IDs. This will include all group IDs
	 *         and prepend them to keys.
	 */
	public synchronized Map<String, Object> getMap() {
		return getMap(true, true, (String[]) null);
	}

	/**
	 * @return all the keys associated with a property group.
	 * @param propertyGroup
	 *            the group of keys to return.
	 */
	public synchronized Set<Key<?>> keys(String propertyGroup) {
		PropertyGroup g = groupMap.get(propertyGroup);
		if (g == null) {
			g = new PropertyGroup();
		}
		return g.map.keySet();
	}

	/**
	 * Return all keys
	 */
	public synchronized Set<Key<?>> keys() {
		Set<Key<?>> keys = new HashSet<>();
		for (PropertyGroup g : groupMap.values()) {
			keys.addAll(g.map.keySet());
		}
		return keys;
	}

	/**
	 * @return a condensed map of all group IDs.
	 * @param prependGroupID
	 *            if true, then the groupID will be prepended to all keys. It is
	 *            highly recommended that this be true, because if this is false
	 *            and different groups have similar sounding keys: then those
	 *            groups cannot be safely condensed to a single map and
	 *            exception will be thrown.
	 * @param identifyClassNames
	 *            if true then keys will be uniquely identified by including
	 *            their class name.
	 * @param groupIDs
	 *            an optional list of group IDs to poll. If null: then all group
	 *            IDs are included. For example: you might want to only return
	 *            the DEFAULT group, at which point it would be safe to pass
	 *            <code>false</code> for <code>prependGroupID</code>.
	 * 
	 **/
	public synchronized Map<String, Object> getMap(boolean prependGroupID,
			boolean identifyClassNames, String... groupIDs) {
		TreeMap<String, Object> returnValue = new TreeMap<String, Object>();
		for (String groupID : groupMap.keySet()) {
			if (groupIDs == null || contains(groupIDs, groupID)) {
				PropertyGroup group = groupMap.get(groupID);
				for (Key<?> key : group.map.keySet()) {
					if (prependGroupID) {
						if (identifyClassNames) {
							if (key.type != null) {
								returnValue.put(
										groupID + "-" + key.type.getName()
												+ "#" + key.toString(),
										group.map.get(key));
							} else {
								returnValue.put(groupID + "#" + key.toString(),
										group.map.get(key));
							}
						} else {
							Object oldValue = returnValue.put(groupID + "#"
									+ key.toString(), group.map.get(key));
							if (oldValue != null) {
								throw new IllegalStateException(
										"the key \""
												+ key
												+ "\" was defined in with multiple classes, so this data cannot be condensed to a single Map unless identifyClassNames is set to true.");
							}
						}
					} else {
						if (identifyClassNames) {
							Object oldValue = returnValue.put(
									key.type.getName() + "-" + key.toString(),
									group.map.get(key));
							if (oldValue != null) {
								throw new IllegalStateException(
										"the key \""
												+ key
												+ "\" was defined in multiple groups, so these groups cannot be condensed to a single Map unless prependGroupID is set to true.");
							}
						} else {
							Object oldValue = returnValue.put(key.toString(),
									group.map.get(key));
							if (oldValue != null) {
								throw new IllegalStateException(
										"the key \""
												+ key
												+ "\" was defined in multiple groups or with multiple classes, so this data cannot be condensed to a single Map unless prependGroupID and/or identifyClassNames is set to true.");
							}
						}
					}
				}
			}
		}
		return returnValue;
	}

	/** Return true if an array contains an element. */
	private static <T> boolean contains(T[] array, T element) {
		for (T s : array) {
			if (s.equals(element))
				return true;
		}
		return false;
	}

	/**
	 * Retrieve a property in the DEFAULT property group.
	 * 
	 * @param key
	 *            the key to get.
	 * @return the current value.
	 */
	public synchronized <T> T get(Key<T> key) {
		return get(DEFAULT, key);
	}

	/**
	 * Assign a property to the DEFAULT property group.
	 * 
	 * @param key
	 *            the key to retrieve.
	 * @param value
	 *            the new value of the key
	 * @return the previous value.
	 */
	public synchronized <T> T set(Key<T> key, T value) {
		return set(DEFAULT, key, value);
	}

	/** Clear the DEFAULT group. */
	public synchronized void clear() {
		clear(DEFAULT);
	}

	/**
	 * Clear everything in a particular property group.
	 * 
	 * @param groupID
	 *            the id of the group to clear
	 */
	public synchronized void clear(String groupID) {
		PropertyGroup group = groupMap.get(groupID);
		if (group != null)
			group.clear();
	}

	/**
	 * Return a PropertyGroup. This will create one if necessary, or throw an
	 * exception if this ID is not allowed.
	 */
	private synchronized PropertyGroup getGroup(String groupID) {
		PropertyGroup group = groupMap.get(groupID);
		if (group == null) {

			if (possibleGroupNames != null
					&& (!possibleGroupNames.contains(groupID))) {
				throw new IllegalArgumentException("the group \"" + groupID
						+ "\" is not included in this ObservableProperties.");
			}

			group = new PropertyGroup();
			groupMap.put(groupID, group);
		}
		return group;
	}

	/**
	 * Return all the properties in a particular category of this
	 * ObservableProperties.
	 * 
	 * @param groupName
	 *            the name of the group (such as DEFAULT or TRANSIENT)
	 * @return all the properties in the group provided. This will be an empty
	 *         map if the group name is unused.
	 */
	public Map<Key<?>, Object> getMap(String groupName) {
		Map<Key<?>, Object> returnValue = new HashMap<>();
		PropertyGroup pg = getGroup(groupName);
		if (pg != null) {
			returnValue.putAll(pg.map);
		}
		return returnValue;
	}

	@Override
	public int hashCode() {
		return getMap().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ObservableProperties))
			return false;
		ObservableProperties p = (ObservableProperties) obj;
		return p.getMap().equals(getMap());
	}

}