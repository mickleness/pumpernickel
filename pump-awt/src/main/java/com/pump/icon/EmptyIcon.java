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

import javax.swing.Icon;

/** An empty icon, used for placeholders or stubs.
 * 
 */
public class EmptyIcon implements Icon {

	int w, h;
	
	public EmptyIcon(int width,int height) {
		w = width;
		h = height;
	}
	
	public int getIconHeight() {
		return h;
	}

	public int getIconWidth() {
		return w;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {}

}