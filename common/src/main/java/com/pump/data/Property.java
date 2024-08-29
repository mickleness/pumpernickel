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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class Property<T> {
	final String name;

	T value;
	List<PropertyChangeListener> listeners;

	boolean isEnabled = true;
	boolean isUserAdjustable = true;

	public Property(String propertyName) {
		this(propertyName, null);
	}

	public Property(String propertyName, T initialValue) {
		name = propertyName;
		setValue(initialValue);
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public boolean isUserAdjustable() {
		return isUserAdjustable;
	}

	public void setEnabled(boolean b) {
		if (b == isEnabled)
			return;
		isEnabled = b;
		firePropertyChangeListeners(name + ".enabled", Boolean.valueOf(!b),
				Boolean.valueOf(b));
	}

	public void setUserAdjustable(boolean b) {
		if (b == isUserAdjustable)
			return;
		isUserAdjustable = b;
		firePropertyChangeListeners(name + ".adjustable", Boolean.valueOf(!b),
				Boolean.valueOf(b));
	}

	public final T getValue() {
		return value;
	}

	public final String getName() {
		return name;
	}

	public final boolean setValue(T obj) {
		validateValue(obj);
		if (obj == null && value == null)
			return false;
		if (obj != null && value != null) {
			if (obj.equals(value)) {
				return false;
			}
		}
		Object oldValue = value;
		value = obj;
		firePropertyChangeListeners(name, oldValue, value);
		return true;
	}

	/**
	 * Subclasses should override this method to throw an
	 * IllegalArgumentException if a value is inappropriate for this property.
	 * 
	 * @param value
	 */
	protected void validateValue(T value) {
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		if (listeners == null) {
			listeners = new ArrayList<PropertyChangeListener>();
		}
		if (listeners.contains(l))
			return;
		listeners.add(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.remove(l);
	}

	protected void firePropertyChangeListeners(String propertyName,
			Object oldValue, Object newValue) {
		if (listeners == null)
			return;
		for (int a = 0; a < listeners.size(); a++) {
			PropertyChangeListener l = listeners.get(a);
			try {
				l.propertyChange(new PropertyChangeEvent(this, propertyName,
						oldValue, newValue));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + name + "=" + value + " (enabled="
				+ isEnabled + ", " + isUserAdjustable + "=" + isUserAdjustable
				+ ")]";
	}
}