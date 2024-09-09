/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.data.converter;

import java.io.Serializable;
import java.util.Map;

/**
 * This converts an object (a bean) into a java.util.Map and back again.
 * <p>
 * The map should use String keys, and all the values should be very common
 * object types like wrapped primitives, Strings, or other serializable objects.
 * The ConverterUtils class includes methods that help with these features.
 * <p>
 * A BeanMapConverter is expected to be serializable. And ideally this should be
 * trivial because a BeanMapConverter should be stateless.
 *
 * @param <B>
 *            the type of bean this supports.
 */
public interface BeanMapConverter<B> extends Serializable {

	/**
	 * Return the type of object this filter supports.
	 */
	Class<B> getType();

	/**
	 * Break an object down into a map of atom attributes. The atoms should all
	 * be serializable. They will often be wrapped primitives, Strings, or
	 * Lists/Maps. These primitives can be reconstituted by calling
	 * {@link #createFromAtoms(Map)}. You should not include any object that
	 * doesn't have a good <code>equals()</code> method (such as arrays).
	 * 
	 * @param object
	 *            the object to break into atoms.
	 * @return a map of atoms that {@link #createFromAtoms(Map)} can use to
	 *         rebuild the original object.
	 */
	Map<String, Object> createAtoms(B object);

	/**
	 * Recreate an object from a series of atoms created by
	 * {@link #createAtoms(Object)}.
	 * 
	 * @param atoms
	 *            a list of serializable objects previously created by
	 *            {@link #createAtoms(Object)}.
	 * @return a new instance of an Object rebuilt from the atoms.
	 */
	B createFromAtoms(Map<String, Object> atoms);
}