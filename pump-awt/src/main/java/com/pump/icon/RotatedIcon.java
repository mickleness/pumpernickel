/*
 * @(#)RotatedIcon.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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
package com.pump.icon;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

import com.pump.math.MathG;

/** A rotated rendering of a given icon.
 * <P>This is (currently) intended for animation, so for
 * the sake of uniformity: this icon uses the same width and height
 * (calculated as the maximum that may be required for the transformed icon).
 * The source icon is always rendered in the center of this space.
 * <p>For example: if you have a 100x100 icon, then the RotatedIcon
 * wrapper will be 142x142 pixels (because it has to allow room for
 * the icon to rotate 45 degrees).
 */
public class RotatedIcon implements Icon {
	float rotation;
	Icon icon;
	int size;
	
	public RotatedIcon(Icon icon,float radians) {
		this.rotation = radians;
		this.icon = icon;
		
		int h = icon.getIconHeight();
		int w = icon.getIconWidth();
		double k = Math.sqrt(h*h+w*w);
		size = MathG.ceilInt(k);
	}

	public void paintIcon(Component c, Graphics g0, int x, int y) {
		Graphics2D g = (Graphics2D)g0.create();
		g.rotate(rotation, x + size/2, y + size/2);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		icon.paintIcon(c, g, 
				x + getIconWidth()/2 - icon.getIconWidth()/2, 
				y + getIconHeight()/2 - icon.getIconHeight()/2 );
		g.dispose();
	}

	public int getIconWidth() {
		return size;
	}

	public int getIconHeight() {
		return size;
	}
}
