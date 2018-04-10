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
package com.pump.awt;

import java.awt.Stroke;

/**
 * This is a <code>Stroke</code> that modifies or sits on top of another
 * <code>Stroke</code>.
 * 
 * This model is especially convenient when you design a GUI to manipulate the
 * properties of your <code>Stroke</code>.
 * 
 */
public interface FilteredStroke extends Stroke {

	/** @return the underlying stroke being filtered. */
	public Stroke getStroke();

	/**
	 * Similar to <code>Font.deriveFont()</code>, this makes a stroke similar to
	 * this object, except the underlying <code>Stroke</code> this stroke
	 * filters is replaced.
	 * 
	 * @param s
	 *            the new underlying stroke to use.
	 * @return a new stroke that is built on top of <code>s</code>
	 */
	public FilteredStroke deriveStroke(Stroke s);
}