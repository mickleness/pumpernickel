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
import javax.swing.SwingConstants;

import com.pump.blog.ResourceSample;

/**
 * An icon to navigate to the beginning of a timeline or animation.
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/samples/FirstIcon/sample.png"
 * alt=
 * "new&#160;com.pump.swing.resources.FirstIcon(&#160;2,&#160;24,&#160;24,&#160;java.awt.Color.lightGray)"
 * > <!-- ======== END OF AUTOGENERATED SAMPLES ======== -->
 * 
 * @see LastIcon
 * @see PauseIcon
 */
@ResourceSample(sample = { "new com.pump.swing.resources.FirstIcon( 2, 24, 24, java.awt.Color.lightGray)" })
public class FirstIcon implements Icon {
	TriangleIcon triangleIcon;
	BarIcon barIcon;

	public FirstIcon(int barWidth, int width, int height, Color color) {
		triangleIcon = new TriangleIcon(SwingConstants.WEST, width - 2
				* barWidth, height, color);
		barIcon = new BarIcon(barWidth, height, color);
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		barIcon.paintIcon(c, g, x, y);
		triangleIcon.paintIcon(c, g, x + barIcon.getIconWidth() * 2, y);
	}

	public int getIconWidth() {
		return barIcon.getIconWidth() * 2 + triangleIcon.getIconWidth();
	}

	public int getIconHeight() {
		return Math.max(barIcon.getIconHeight(), triangleIcon.getIconHeight());
	}

	static class BarIcon implements Icon {
		Color color;
		int width, height;

		public BarIcon(int width, int height, Color color) {
			this.width = width;
			this.height = height;
			this.color = color;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {
			g.setColor(color);
			g.fillRect(x, y, width, height);
		}

		public int getIconWidth() {
			return width;
		}

		public int getIconHeight() {
			return height;
		}
	}
}