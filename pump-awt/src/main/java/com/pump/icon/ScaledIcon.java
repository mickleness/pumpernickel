/*
 * @(#)ScaledIcon.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
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
import java.awt.geom.AffineTransform;

import javax.swing.Icon;

import com.pump.geom.TransformUtils;

public class ScaledIcon implements Icon {
	int w, h;
	Icon icon;

	public ScaledIcon(Icon i, int w, int h) {
		this.icon = i;
		this.w = w;
		this.h = h;
	}

	public int getIconHeight() {
		return h;
	}

	public int getIconWidth() {
		return w;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		AffineTransform oldTransform = g2.getTransform();
		g2.transform(TransformUtils.createAffineTransform(x, y, x
				+ icon.getIconWidth(), y, x, y + icon.getIconHeight(), x, y, x
				+ w, y, x, y + h));
		icon.paintIcon(c, g2, x, y);
		g2.setTransform(oldTransform);
	}
}
