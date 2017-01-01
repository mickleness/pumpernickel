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