package com.pump.data;

import java.io.Serializable;

/** This can be used for bounds checking as values are about to be assigned.
 * It is an optional mechanism to automate IllegalArgumentExceptions.
 * 
 * @param <T> the type of argument this checks.
 */
public abstract class BoundsChecker<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/** Check that a potential value is an accepted value.
	 * @param key the key this value will be assigned to.
	 * @param t the value to check.
	 * 
	 * @throws IllegalArgumentException if the argument is somehow not an acceptable
	 * property value.
	 */
	public abstract void check(Key<T> key,T t) throws IllegalArgumentException;
}