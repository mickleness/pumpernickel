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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;

/** An icon that pads another icon with <code>Insets</code>. */
public class PaddedIcon implements Icon {
	final Icon icon;
	final Insets i;

	public PaddedIcon(Icon orig, int padding) {
		this(orig, new Insets(padding, padding, padding, padding));
	}

	public PaddedIcon(Icon orig, Insets i) {
		if(orig==null) throw new NullPointerException();
		if(i==null) throw new NullPointerException();
		this.icon = orig;
		this.i = i;
	}

	public int getIconHeight() {
		return icon.getIconHeight() + i.top + i.bottom;
	}

	public int getIconWidth() {
		return icon.getIconWidth() + i.left + i.right;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		icon.paintIcon(c, g, x+i.left, y+i.top);
	}

}