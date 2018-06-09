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

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.tree.TreeNode;

/**
 * An instruction to paint something to a <code>Graphics2D</code>. This is part
 * of the {@link GraphicsWriter} model.
 * 
 */
public interface GraphicInstruction extends TreeNode {
	/** Renders this instruction. */
	public void paint(Graphics2D g);

	/**
	 * Returns the rectangle that is affected by this instruction.
	 */
	public Rectangle2D getBounds();

	/**
	 * Return true if a Point2D is rendered in this instruction.
	 * <p>
	 * By default this may return getBounds().contains(p), but shapes may
	 * override this to give more specific details. For example: if a circle is
	 * being rendered, then this method may return false for the corners of
	 * getBounds() but not the center.
	 */
	public boolean contains(Point2D p);

	/**
	 * Sets the parent of this tree node. This method
	 */
	public void setParent(GraphicsWriter parent);

	/**
	 * Returns a string for developers to recognize the source of this
	 * instruction. By default this returns a stack trace, but it may return
	 * "Unknown" if the <code>GraphicsWriter</code> debugging flag is false.
	 */
	public String getSource();

	GraphicInstruction getChildAt(int childIndex);
}