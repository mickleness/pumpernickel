/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.icon;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

import com.pump.blog.ResourceSample;

/**
 * One of three icons used to toggle views in file browsers/dialogs.
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/samples/ListIcon/sample.png"
 * alt="new&#160;com.pump.swing.resources.ListIcon(12,&#160;12)"> <!-- ========
 * END OF AUTOGENERATED SAMPLES ======== -->
 * 
 * @see ColumnIcon
 * @see TileIcon
 * @see StackIcon
 */
@ResourceSample(sample = { "new com.pump.swing.resources.ListIcon(12, 12)" })
public class ListIcon implements Icon {
	final int w, h;

	public ListIcon(int width, int height) {
		w = width;
		h = height;
	}

	public int getIconHeight() {
		return h;
	}

	public int getIconWidth() {
		return w;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(Color.darkGray);
		int dy = (h - 4 * (h / 4)) / 2;
		g.translate(0, dy);
		for (int myY = y; myY < y + h; myY += 4) {
			g.drawLine(x, myY, x + w, myY);
		}
		g.translate(0, -dy);
	}

}