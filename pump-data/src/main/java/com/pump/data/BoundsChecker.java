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

/** This can be used for bounds checking as values are about to be assigned.
 * It is an optional mechanism to automate IllegalArgumentExceptions.
 * 
 * @param <T> the type of argument this checks.
 */
public abstract class BoundsChecker<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * This BoundsChecker will throw an IllegalArgumentException if a candidate value is null.
	 */
	@SuppressWarnings({ "rawtypes" })
	public static BoundsChecker NOT_NULL = new BoundsChecker() {
		private static final long serialVersionUID = 1L;

		@Override
		public void check(Key key, Object t) throws IllegalArgumentException {
			if(t==null)
				throw new IllegalArgumentException("The key \""+key+"\" should not be null");
		}
		
	};

	/** Check that a potential value is an accepted value.
	 * @param key the key this value will be assigned to.
	 * @param t the value to check.
	 * 
	 * @throws IllegalArgumentException if the argument is somehow not an acceptable
	 * property value.
	 */
	public abstract void check(Key<T> key,T t) throws IllegalArgumentException;
}