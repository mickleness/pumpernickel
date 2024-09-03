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
package com.pump.geom;

import java.awt.geom.GeneralPath;

/**
 * This collects a series of points that would otherwise be a jagged polyline
 * and smooths out the data into an attractive curved shape.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Smoothing">Smoothing</a>
 * @see <a
 *      href="https://javagraphics.blogspot.com/2010/06/shapes-implementing-freehand-pencil.html">Shapes:
 *      Implementing a Freehand Pencil Tool</a>
 */
public interface MouseSmoothing {

	/**
	 * Add a point to be smoothed.
	 * 
	 * @param x
	 *            the x-coordinate.
	 * @param y
	 *            the y-coordinate.
	 * @param t
	 *            the time, in milliseconds. This is optional (it may be a
	 *            constant value), but some subclasses may use this information
	 *            to help define edges.
	 */
	public void add(float x, float y, long t);

	/**
	 * Removes all previously added points to reset this object.
	 */
	public void reset();

	/**
	 * Returns the shape this data represents when smoothed.
	 * <p>
	 * This is equivalent to calling:
	 * 
	 * <pre>
	 * GeneralPath path = new GeneralPath();
	 * getShape(path);
	 * return path;
	 * </pre>
	 * <p>
	 * It is possible that this MouseSmoothing is empty, and no shape data will
	 * be added.
	 */
	public GeneralPath getShape();

	/**
	 * Returns the shape this data represents when smoothed.
	 * <p>
	 * This will not empty the argument before appending data.
	 * <p>
	 * It is possible that this MouseSmoothing is empty, and no shape data will
	 * be added.
	 * 
	 * @param path
	 *            the <code>GeneralPath</code> to append shape data to.
	 */
	public void getShape(GeneralPath path);

	/**
	 * Returns whether this data set is empty.
	 * <p>
	 * This will be empty either when it is first constructed, or after
	 * <code>reset()</code> has been called.
	 */
	public boolean isEmpty();
}