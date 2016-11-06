/*
 * @(#)FocusedBorder.java
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
package com.pump.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import javax.swing.border.Border;

import com.pump.plaf.PlafPaintUtils;

/** This will alternate between a focus ring of 3 pixels
 * and an arbitrary border you pass when constructing this
 * object.
 * 
 */
public class FocusedBorder implements Border {
	private final static Insets focusInsets = new Insets(3,3,3,3);
	final Border unfocusedBorder;
	
	/** Create a new <code>FocusedBorder</code>.
	 * 
	 * @param unfocusedBorder the border to paint when the focus ring will not be painted.
	 */
	public FocusedBorder(Border unfocusedBorder) {
		this.unfocusedBorder = unfocusedBorder;
	}

	public Insets getBorderInsets(Component c) {
		Insets newInsets = (Insets)focusInsets.clone();
		if(unfocusedBorder!=null) {
			Insets otherInsets = unfocusedBorder.getBorderInsets(c);
			newInsets.left = Math.max(newInsets.left, otherInsets.left);
			newInsets.top = Math.max(newInsets.top, otherInsets.top);
			newInsets.bottom = Math.max(newInsets.bottom, otherInsets.bottom);
			newInsets.right = Math.max(newInsets.right, otherInsets.right);
		}
		return newInsets;
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		Insets insets = getBorderInsets(c);
		if(c.hasFocus()) {
			Rectangle rect = new Rectangle(x+insets.left, y+insets.top,
					width-insets.left-insets.right-1,
					height-insets.top-insets.bottom-1);
			Graphics2D g2 = (Graphics2D)g.create();
			GeneralPath focusOnly = new GeneralPath(Path2D.WIND_EVEN_ODD);
			focusOnly.append(new Rectangle(x,y,width,height), false);
			focusOnly.append(new Rectangle(x+insets.left,y+insets.top,
					width-insets.left-insets.right,
					height-insets.top-insets.bottom), false);
			g2.clip(focusOnly);
			PlafPaintUtils.paintFocus(g2, rect, 3);
		} else if(unfocusedBorder!=null) {
			Insets borderInsets = unfocusedBorder.getBorderInsets(c);
			unfocusedBorder.paintBorder(c, g, x+insets.left-borderInsets.left, 
					y+insets.top-borderInsets.top, 
					width-insets.left-insets.right+borderInsets.left+borderInsets.right,
					height-insets.top-insets.bottom+borderInsets.top+borderInsets.bottom);
		}
		
	}

}
