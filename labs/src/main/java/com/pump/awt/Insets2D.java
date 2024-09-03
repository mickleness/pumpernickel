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
package com.pump.awt;

import java.awt.Insets;
import java.io.Serializable;

/**
 * This is analogous to <code>java.awt.Insets</code>, except this is backed by
 * doubles instead of ints.
 *
 */
public class Insets2D implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	public double top, left, bottom, right;

	/**
	 * Creates and initializes a new {@code Insets2D} object with the specified
	 * top, left, bottom, and right insets.
	 * 
	 * @param top
	 *            the inset from the top.
	 * @param left
	 *            the inset from the left.
	 * @param bottom
	 *            the inset from the bottom.
	 * @param right
	 *            the inset from the right.
	 */
	public Insets2D(double top, double left, double bottom, double right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	/**
	 * Set top, left, bottom, and right to the specified values
	 *
	 * @param top
	 *            the inset from the top.
	 * @param left
	 *            the inset from the left.
	 * @param bottom
	 *            the inset from the bottom.
	 * @param right
	 *            the inset from the right.
	 * @since 1.5
	 */
	public void set(double top, double left, double bottom, double right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	/**
	 * Checks whether two insets objects are equal. Two instances of
	 * {@code Insets2D} are equal if the four integer values of the fields
	 * {@code top}, {@code left}, {@code bottom}, and {@code right} are all
	 * equal.
	 * 
	 * @return {@code true} if the two insets are equal; otherwise
	 *         {@code false}.
	 * @since 1.1
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Insets2D) {
			Insets2D insets = (Insets2D) obj;
			return ((top == insets.top) && (left == insets.left)
					&& (bottom == insets.bottom) && (right == insets.right));
		}
		if (obj instanceof Insets) {
			Insets insets = (Insets) obj;
			return ((top == insets.top) && (left == insets.left)
					&& (bottom == insets.bottom) && (right == insets.right));
		}
		return false;
	}

	/**
	 * Returns the hash code for this Insets2D.
	 *
	 * @return a hash code for this Insets2D.
	 */
	public int hashCode() {
		return Double.hashCode(top + bottom) + Double.hashCode(left + right);
	}

	/**
	 * Returns a string representation of this {@code Insets} object. This
	 * method is intended to be used only for debugging purposes, and the
	 * content and format of the returned string may vary between
	 * implementations. The returned string may be empty but may not be
	 * {@code null}.
	 *
	 * @return a string representation of this {@code Insets2D} object.
	 */
	public String toString() {
		return getClass().getName() + "[top=" + top + ",left=" + left
				+ ",bottom=" + bottom + ",right=" + right + "]";
	}

	/**
	 * Create a copy of this object.
	 * 
	 * @return a copy of this {@code Insets2D} object.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError(e);
		}
	}
}