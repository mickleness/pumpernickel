/*
 * @(#)MouseSmoothing.java
 *
 * $Date: 2015-02-28 15:59:45 -0500 (Sat, 28 Feb 2015) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.geom;

import java.awt.geom.GeneralPath;

import com.pump.blog.Blurb;

/** This collects a series of points that would
 * otherwise be a jagged polyline and smooths
 * out the data into an attractive curved shape.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Smoothing">Smoothing</a>
 */
@Blurb (
filename = "MouseSmoothing",
title = "Shapes: Implementing a Freehand Pencil Tool",
releaseDate = "June 2010",
summary = "If the user drags the mouse over a <code>JComponent</code>, then you can collect a series of points. "+
"This project takes those points and replaces the line segments with attractive bezier curves.\n"+
"<p>This is one of three <a href=\"https://javagraphics.java.net/index.html#ShapeCreationPanelDemo\">ShapeCreationPanelUIs</a>.",
link = "http://javagraphics.blogspot.com/2010/06/shapes-implementing-freehand-pencil.html",
sandboxDemo = false
)
public interface MouseSmoothing {
	
	/** Add a point to be smoothed.
	 * 
	 * @param x the x-coordinate.
	 * @param y the y-coordinate.
	 * @param t the time, in milliseconds.  This is optional
	 * (it may be a constant value), but some subclasses
	 * may use this information to help define edges.
	 */
	public void add(float x,float y,long t);
	
	/** Removes all previously added points to reset this object.
	 */
	public void reset();
	
	/** Returns the shape this data represents when smoothed.
	 * <p>This is equivalent to calling:
	 * <pre>GeneralPath path = new GeneralPath();
	 * getShape(path);
	 * return path;</pre>
	 * <p>It is possible that this MouseSmoothing is empty,
	 * and no shape data will be added.
	 */
	public GeneralPath getShape();

	/** Returns the shape this data represents when smoothed.
	 * <p>This will not empty the argument before appending data.
	 * <p>It is possible that this MouseSmoothing is empty,
	 * and no shape data will be added.
	 * @param path the <code>GeneralPath</code> to append
	 * shape data to.
	 */
	public void getShape(GeneralPath path);
	
	/** Returns whether this data set is empty.
	 * <p>This will be empty either when it is first
	 * constructed, or after <code>reset()</code> has
	 * been called.
	 */
	public boolean isEmpty();
}
