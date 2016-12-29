/*
 * @(#)GraphicInstruction.java
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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.tree.TreeNode;

/** An instruction to paint something to a <code>Graphics2D</code>. 
 * This is part of the {@link GraphicsWriter} model.
 * 
 */
public interface GraphicInstruction extends TreeNode
{
	/** Renders this instruction. */
	public void paint(Graphics2D g);
	
	/** Returns the rectangle that is affected by this instruction.
	 */
	public Rectangle2D getBounds();
	
	/** Sets the parent of this tree node.
	 * This method 
	 */
	public void setParent(GraphicsWriter parent);
	
	/** Returns a string for developers to recognize the source
	 * of this instruction.  By default this returns a stack trace,
	 * but it may return "Unknown" if the <code>GraphicsWriter</code>
	 * debugging flag is false.
	 */
	public String getSource();
}
