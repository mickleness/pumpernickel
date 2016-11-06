/*
 * @(#)FadedIcon.java
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

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

/** This applies a degree of translucency to an existing <code>Icon</code>.
 */
public class FadedIcon implements Icon {
	float alpha;
	Icon icon;

	public FadedIcon(Icon icon,float alpha) {
		if(alpha<0 || alpha>1)
			throw new IllegalArgumentException("alpha ("+alpha+") must be between [0, 1]");
		this.icon = icon;
		this.alpha = alpha;
	}

	public int getIconHeight() {
		return icon.getIconHeight();
	}

	public int getIconWidth() {
		return icon.getIconWidth();
	}

	public void paintIcon(Component c, Graphics g0, int x, int y) {
		Graphics2D g = (Graphics2D)g0;
		g.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		icon.paintIcon(c, g, x, y);
	}
}
