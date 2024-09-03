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
package com.pump.image.pixel.quantize;

/**
 * This is an algorithm that converts a large <code>ColorSet</code> to a smaller
 * one.
 * <p>
 * The new set is not required to be a subset: it could contain new colors that
 * are the average/approximations of entries in the original set.
 */
public abstract class ColorQuantization {
	/**
	 * Create a reduced ColorSet.
	 * 
	 * @param originalSet
	 *            the large color set to reduce.
	 * @param maximumColorCount
	 *            the number of colors to reduce to.
	 * @param retainOriginalIntegrity
	 *            if true then the argument <code>originalSet</code> should not
	 *            be modified. If false: then this method may change the
	 *            original color set at its discretion. (In some algorithms:
	 *            this may save a lot of time.)
	 * @return a <code>ColorSet</code> that conforms to maximumColorCount. (This
	 *         method may return the <code>originalSet</code> argument if it was
	 *         already small enough.)
	 */
	public abstract ColorSet createReducedSet(ColorSet originalSet,
			int maximumColorCount, boolean retainOriginalIntegrity);
}