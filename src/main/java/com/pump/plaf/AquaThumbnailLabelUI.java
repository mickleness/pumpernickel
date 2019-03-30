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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JLabel;

public class AquaThumbnailLabelUI extends ThumbnailLabelUI {

	@Override
	protected void calculateGeometry(JLabel label) {
		super.calculateGeometry(label);
		iconRect.y += 4;
		iconRect.height = 64;
		iconRect.width = 64;
		iconRect.x = label.getWidth() / 2 - 32;

		textRect.y = 72 + label.getIconTextGap();
	}

	@Override
	protected int getViewWidth(JLabel label) {
		return label.getWidth() - 8;
	}

	@Override
	public void paint(Graphics g0, JComponent c) {
		JLabel label = (JLabel) c;
		calculateGeometry(label);
		super.paint(g0, c);
	}

	RoundRectangle2D roundRect = new RoundRectangle2D.Float();
	Color selectedIconBackground = new Color(0xCCCCCC);
	Color selectedTextBackground = new Color(0x3875D7);

	@Override
	protected void paintIcon(Graphics2D g, JLabel label, boolean isSelected,
			boolean isIndicated) {
		if (isSelected) {
			roundRect.setRoundRect(iconRect.x - 4, iconRect.y - 4,
					iconRect.width + 8, iconRect.height + 8, 12, 12);
			g.setColor(selectedIconBackground);
			g.fill(roundRect);
		}
		super.paintIcon(g, label, isSelected, isIndicated);
	}

	@Override
	protected void paintText(Graphics2D g, JLabel label, String text,
			boolean isSelected, boolean isIndicated) {
		if (isSelected) {
			roundRect.setRoundRect(textRect.x - 4, textRect.y,
					textRect.width + 8, textRect.height, 16, 16);
			g.setColor(selectedTextBackground);
			g.fill(roundRect);
			g.setColor(Color.white);
		} else {
			g.setColor(Color.black);
		}
		super.paintText(g, label, label.getText(), isSelected, isIndicated);
	}

}