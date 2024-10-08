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
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;

import com.pump.blog.ResourceSample;

/**
 * A pause icon.
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/samples/PauseIcon/sample.png"
 * alt=
 * "new&#160;com.pump.swing.resources.PauseIcon(&#160;24,&#160;24,&#160;java.awt.Color.lightGray)"
 * > <!-- ======== END OF AUTOGENERATED SAMPLES ======== -->
 *
 * @see FirstIcon
 * @see LastIcon
 */
@ResourceSample(sample = { "new com.pump.swing.resources.PauseIcon( 24, 24, java.awt.Color.lightGray)" })
public class PauseIcon implements Icon {

	int width, height;
	Color color;

	public PauseIcon(int w, int h) {
		this(w, h, Color.darkGray);
	}

	public PauseIcon(int w, int h, Color color) {
		if (color == null)
			throw new NullPointerException();
		width = w;
		height = h;
		this.color = color;
	}

	public int getIconHeight() {
		return height;
	}

	public int getIconWidth() {
		return width;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		float barWidth = width * 2 / 5;
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(color);
		g2.fill(new Rectangle2D.Float(x, y, barWidth, height));
		g2.fill(new Rectangle2D.Float(x + width - barWidth, y, barWidth, height));
	}

}