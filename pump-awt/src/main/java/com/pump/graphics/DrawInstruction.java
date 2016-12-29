/*
 * @(#)DrawInstruction.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
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
package com.pump.graphics;

import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/** This instruction paints the stroke of a shape.
* <P>The <code>paint()</code> method should render something equivalent to what
* these lines of code produce:
* <br><code>g = (Graphics2D)g.create();</code>
* <br><code>g.clip(getClipping());</code>
* <br><code>g.setTransform(getTransform());</code>
* <br><code>g.setStroke(getStroke());</code>
* <br><code>g.setPaint(getStrokePaint());</code>
* <br><code>g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));</code>
* <br><code>g.draw(getShape());</code>
 * 
**/
public interface DrawInstruction extends GraphicInstruction {
	/** The shape to paint. */
	public Shape getShape();
	/** The stroke to use. */
	public Stroke getStroke();
	/** The paint to use. */
	public Paint getStrokePaint();
	/** The AffineTransform to render the stroked shape through. */
	public AffineTransform getTransform();
	/** Whether the clippings (if it exists) intersects the stroked shape. */
	public boolean isClipped();
	/** Returns the optional clipping.  This is <i>not</i> relative to the <code>AffineTransform</code> used. */
	public Shape getClipping();
	/** Returns the opacity to use. */
	public float getOpacity();
}
