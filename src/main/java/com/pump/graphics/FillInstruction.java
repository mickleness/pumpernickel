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
import java.awt.geom.AffineTransform;

/**
 * This instruction paints the fill of a shape.
 * <P>
 * The <code>paint()</code> method should render something equivalent to what
 * these lines of code produce: <br>
 * <code>g = (Graphics2D)g.create();</code> <br>
 * <code>g.clip(getClipping());</code> <br>
 * <code>g.setTransform(getTransform());</code> <br>
 * <code>g.setPaint(getFillPaint());</code> <br>
 * <code>g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));</code>
 * <br>
 * <code>g.fill(getShape());</code>
 * 
 **/
public interface FillInstruction extends GraphicInstruction {
	/**
	 * Returns the optional clipping is <i>not</i> relative to the
	 * <code>AffineTransform</code> used.
	 */
	public Shape getClipping();

	/** Returns the transform to use. */
	public AffineTransform getTransform();

	/** Returns the shape to paint. */
	public Shape getShape();

	/** Returns the paint to use. */
	public Paint getFillPaint();

	/** Return true if the clipping intersects this shape. */
	public boolean isClipped();

	/** The opacity to use. */
	public float getOpacity();
}