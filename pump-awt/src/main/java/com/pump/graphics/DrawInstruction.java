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