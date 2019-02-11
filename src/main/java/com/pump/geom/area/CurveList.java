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
package com.pump.geom.area;

abstract class CurveList {

	private static CurveX[] emptyCurves = new CurveX[0];

	/**
	 * The array buffer into which the components of the list are stored. The
	 * capacity of the list is the length of this array buffer, and is at least
	 * large enough to contain all the list elements.
	 * <p>
	 * This array must be of type <code>type</code>.
	 * <p>
	 * Any array elements following the last element in the list are null.
	 */
	protected CurveX[] elementData;

	/**
	 * The number of valid components in this list. Components
	 * <tt>elementData[0]</tt> through <tt>elementData[elementCount-1]</tt> are
	 * the actual items.
	 */
	protected int elementCount;

	private static final long serialVersionUID = 1;

	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param type
	 *            the type of the array this list uses.
	 * @param initialCapacity
	 *            the initial capacity of the list.
	 * @exception IllegalArgumentException
	 *                if the specified initial capacity is negative
	 */
	protected CurveList(int initialCapacity) {
		if (initialCapacity == 0) {
			this.elementData = emptyCurves;
		} else {
			this.elementData = new CurveX[initialCapacity];
		}
	}

	/**
	 * Increases the capacity of this list, if necessary, to ensure that it can
	 * hold at least the number of components specified by the minimum capacity
	 * argument.
	 *
	 * <p>
	 * If the current capacity of this list is less than <tt>minCapacity</tt>,
	 * then its capacity is increased by replacing its internal data array, kept
	 * in the field <tt>elementData</tt>, with a larger one. The size of the new
	 * data array will be the old size plus <tt>capacityIncrement</tt>, unless
	 * the value of <tt>capacityIncrement</tt> is less than or equal to zero, in
	 * which case the new capacity will be twice the old capacity; but if this
	 * new size is still smaller than <tt>minCapacity</tt>, then the new
	 * capacity will be <tt>minCapacity</tt>.
	 *
	 * @param minCapacity
	 *            the desired minimum capacity.
	 */
	protected void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			Object oldData[] = elementData;
			int newCapacity = (oldCapacity * 2);
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			if (newCapacity < 4)
				newCapacity = 4;

			elementData = new CurveX[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, elementCount);
		}
	}

	/**
	 * Returns the number of components in this list.
	 *
	 * @return the number of components in this list.
	 */
	protected int size() {
		return elementCount;
	}

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index
	 *            index of the element to return.
	 * @return object at the specified index
	 * @exception ArrayIndexOutOfBoundsException
	 *                index is out of range (index &lt; 0 || index &gt;=
	 *                size()).
	 */
	protected CurveX get(int index) {
		if (index >= elementCount)
			throw new ArrayIndexOutOfBoundsException(index);

		return elementData[index];
	}

	/**
	 * Tests if this list has no components.
	 *
	 * @return <code>true</code> if and only if this list has no components,
	 *         that is, its size is zero; <code>false</code> otherwise.
	 */
	protected boolean isEmpty() {
		return elementCount == 0;
	}

	/**
	 * Appends the specified element to the end of this list.
	 *
	 * @param o
	 *            element to be appended to this list.
	 */
	protected void add(CurveX o) {
		ensureCapacity(elementCount + 1);
		elementData[elementCount++] = o;
	}
}