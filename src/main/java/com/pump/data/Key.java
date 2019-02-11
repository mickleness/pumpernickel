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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.prefs.Preferences;

import javax.swing.JComponent;

import com.pump.data.encoder.ValueEncoder;

/**
 * This describes a key/attribute and its associated generic type.
 * <p>
 * This also includes optional {@link BoundsChecker BoundsCheckers} which are
 * triggered in the {@link #validate(Object)} method.
 * <p>
 * Each <code>Key</code> is also capable of serializing/deserializing an object
 * as a String. The <code>Key</code> class itself is abstract, and each subclass
 * knows how to read/write data itself.
 * 
 * @param <T>
 *            the type of object this Key relates to.
 */
public class Key<T> implements CharSequence, Serializable {
	private static final long serialVersionUID = 1L;

	protected final String name;
	protected final Class<T> type;
	protected List<BoundsChecker<T>> checkers;
	protected T defaultValue;
	protected ValueEncoder<T> encoder;

	public Key(Class<T> type, String name) {
		this(type, name, null);
	}

	public Key(Class<T> type, String name, T defaultValue) {
		this(type, name, defaultValue, true);
	}

	public Key(Class<T> type, String name, T defaultValue, boolean canBeNull) {
		this(type, name, defaultValue, canBeNull, ValueEncoder
				.getDefaultEncoder(type));
	}

	@SuppressWarnings("unchecked")
	public Key(Class<T> type, String name, T defaultValue, boolean canBeNull,
			ValueEncoder<T> encoder) {
		if (type == null)
			throw new NullPointerException();
		if (name == null)
			throw new NullPointerException();
		if (name.length() == 0)
			throw new IllegalArgumentException();

		this.defaultValue = defaultValue;
		this.type = type;
		this.name = name;
		this.encoder = encoder;

		if (!canBeNull)
			addBoundsChecker(BoundsChecker.NOT_NULL);
	}

	public void addBoundsChecker(BoundsChecker<T> bc) {
		if (checkers == null)
			checkers = new ArrayList<>();

		checkers.add(bc);
	}

	public Class<T> getType() {
		return type;
	}

	@Override
	public int length() {
		return name.length();
	}

	@Override
	public char charAt(int index) {
		return name.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return name.subSequence(start, end);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Key))
			return false;
		Key<?> other = (Key<?>) obj;
		if (!name.equals(other.name))
			return false;
		if (!type.equals(other.type))
			return false;
		if (!Objects.equals(defaultValue, other.defaultValue))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	public List<BoundsChecker<T>> getBoundsCheckers() {
		ArrayList<BoundsChecker<T>> returnValue = new ArrayList<>();
		if (checkers != null) {
			for (BoundsChecker<T> b : checkers) {
				returnValue.add(b);
			}
		}
		return returnValue;
	}

	/**
	 * Validate the argument against all of this key's BoundsCheckers (if any).
	 * 
	 * @param value
	 *            the value to check. This may be null.
	 * 
	 * @throws IllegalArgumentException
	 *             , or any other RuntimeException that BoundsCheckers may
	 *             consider appropriate.
	 */
	public void validate(T value) {
		if (checkers != null) {
			for (BoundsChecker<T> checker : checkers) {
				checker.check(this, value);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T put(Map attributes, T value) {
		validate(value);

		if (value == null) {
			return (T) attributes.remove(toString());
		} else {
			return (T) attributes.put(toString(), value);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public T putClientProperty(JComponent jcomponent, T value) {
		validate(value);

		String str = toString();
		T returnValue = (T) jcomponent.getClientProperty(str);
		jcomponent.putClientProperty(str, value);

		return returnValue;
	}

	/**
	 * Return the value of this key based on the map provided, or
	 * {@link #getDefaultValue()} is the map doesn't contain the requested
	 * value.
	 * 
	 * @param attributes
	 *            the attributes to inspect.
	 * @return the value of this key based on the map provided.
	 */
	@SuppressWarnings({ "rawtypes" })
	public T get(Map attributes) {
		return get(attributes, true);
	}

	/**
	 * Return the value of this key based on the map provided.
	 * 
	 * @param attributes
	 *            the attributes to inspect.
	 * @param applyDefaultValue
	 *            if true and the map doesn't contain the key, then
	 *            {@link #getDefaultValue()} is returned.
	 * @return the value of this key based on the map provided.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T get(Map attributes, boolean applyDefaultValue) {
		T value = (T) attributes.get(toString());
		if (value == null && applyDefaultValue)
			value = getDefaultValue();
		return value;
	}

	/**
	 * Return the value of this key in a jcomponent's client properties, or
	 * {@link #getDefaultValue()} if the value is undefined.
	 * 
	 * @param jcomponent
	 *            component with the client properties to inspect
	 * @return the value of this key based on the map provided.
	 */
	public T getClientProperty(JComponent jcomponent) {
		return getClientProperty(jcomponent, true);
	}

	/**
	 * Return the value of this key in a jcomponent's client properties.
	 * 
	 * @param jcomponent
	 *            component with the client properties to inspect
	 * @param applyDefaultValue
	 *            if true and the map doesn't contain the key, then
	 *            {@link #getDefaultValue()} is returned.
	 * @return the value of this key based on the map provided.
	 */
	@SuppressWarnings({ "unchecked" })
	public T getClientProperty(JComponent jcomponent, boolean applyDefaultValue) {
		T value = (T) jcomponent.getClientProperty(toString());
		if (value == null && applyDefaultValue)
			value = getDefaultValue();
		return value;
	}

	/**
	 * Remove this key from the map provided.
	 * 
	 * @return the value this key mapped to before it was removed.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T remove(Map attributes) {
		return (T) attributes.remove(toString());
	}

	/**
	 * Returns the name of this key, which is equivalent to {@link #toString()}.
	 * 
	 * @return the name of this key, which is equivalent to {@link #toString()}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Store an attribute in a Preferences object.
	 * 
	 * @param prefs
	 *            the destination to store the object int.
	 * @param value
	 *            the value to write.
	 */
	public void set(Preferences prefs, T value) {
		validate(value);

		if (value == null) {
			prefs.remove(getName());
		} else {
			ValueEncoder<T> encoder = getEncoder();
			String encodedValue = encoder.encode(value);
			prefs.put(getName(), encodedValue);
		}
	}

	/**
	 * Check to see if a value must be serialized.
	 * 
	 * @param value
	 *            a value to validate
	 * @return true if the argument matches the default value. Note subclasses
	 *         may override this to identify approximate matches. (For example:
	 *         consider an AffineTransform where 1 value is within .00001 of an
	 *         identity transform.)
	 */
	public boolean isDefault(T value) {
		return Objects.equals(defaultValue, value);
	}

	/**
	 * 
	 * @return the default value for this key (may be null).
	 */
	public T getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Retrieve a value from a Preferences object, or return a default value.
	 * 
	 * @param prefs
	 *            the preferences object to consult.
	 * @param defaultValue
	 *            the default value if no other value is provided.
	 * @return either the stored value in the preferences, or the defaultValue
	 *         if the preferences don't include this key.
	 */
	public T get(Preferences prefs, T defaultValue) {
		String value = prefs.get(getName(), null);
		if (value == null) {
			return getDefaultValue();
		}

		return getEncoder().parse(value);
	}

	public ValueEncoder<T> getEncoder() {
		return encoder;
	}

	/**
	 * @return the generic parameters this attribute relies on. The default
	 *         implementation returns an empty array.
	 */
	public Key<?>[] getGenericParameters() {
		return new Key<?>[] {};
	}
}