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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

public class AquaColorWellUI extends ColorWellUI {

	public static ComponentUI createUI(JComponent jc) {
		return new AquaColorWellUI();
	}

	@Override
	public Dimension getMaximumSize(JComponent c) {
		Border border = c.getBorder();
		Insets i = border.getBorderInsets(c);
		return new Dimension(40 + i.left + i.right, 40 + i.top + i.bottom);
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		Border border = c.getBorder();
		Insets i = border.getBorderInsets(c);
		return new Dimension(11 + i.left + i.right, 11 + i.top + i.bottom);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Border border = c.getBorder();
		Insets i = border.getBorderInsets(c);
		return new Dimension(49 + i.left + i.right, 11 + i.top + i.bottom);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setBorder(new AquaColorWellBorder());
	}

	static class AquaColorWellBorder implements Border {

		public Insets getBorderInsets(Component c) {
			return new Insets(6, 6, 6, 6);
		}

		public boolean isBorderOpaque() {
			return true;
		}

		static Color fill = new Color(0xF3F3F3);
		static Color border = new Color(0xB0B0B0);
		static Color borderSelected = new Color(0x575757);

		static Color[] normalGradient = new Color[] {
				new Color(0x00000000, true), new Color(0x03000000, true) };

		static Color[] selectedGradient = new Color[] {
				new Color(0x00000000, true), new Color(0x00000000, true) };

		static Color[] selectedFillGradient = new Color[] {
				new Color(0xB0B0B0), new Color(0xC4C4C4) };

		public void paintBorder(Component c, Graphics g0, int x, int y,
				int width, int height) {

			Graphics2D g = (Graphics2D) g0;

			boolean selected = c.hasFocus();
			if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				Boolean pressed = (Boolean) jc
						.getClientProperty("mousePressed");
				if (pressed == null)
					pressed = Boolean.FALSE;
				if (pressed.booleanValue())
					selected = true;
			}

			GeneralPath body = new GeneralPath(Path2D.WIND_EVEN_ODD);
			body.moveTo(x, y);
			body.lineTo(x + width, y);
			body.lineTo(x + width, y + height);
			body.lineTo(x, y + height);
			body.closePath();
			body.moveTo(x + 5, y + 5);
			body.lineTo(x + width - 5, y + 5);
			body.lineTo(x + width - 5, y + height - 5);
			body.lineTo(x + 5, y + height - 5);
			body.closePath();

			if (!selected) {
				g.setColor(fill);
			} else {
				Paint paint = PlafPaintUtils.getVerticalGradient(
						"aquaSelectedColorWellFill", height, y, new float[] {
								0, 1 }, selectedFillGradient);
				g.setPaint(paint);
			}
			g.fill(body);

			if (!selected) {
				g.setColor(border);
			} else {
				g.setColor(borderSelected);
			}
			g.drawLine(x, y, x + width - 1, y);
			g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
			g.drawLine(x, y, x, y + height - 1);
			g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);

			g.drawLine(x + 5, y + 5, x + width - 1 - 5, y + 5);
			g.drawLine(x + 5, y + height - 1 - 5, x + width - 1 - 5, y + height
					- 1 - 5);
			g.drawLine(x + 5, y + 5, x + 5, y + height - 1 - 5);
			g.drawLine(x + width - 1 - 5, y + 5, x + width - 1 - 5, y + height
					- 1 - 5);

			// side shades:
			if (!selected) {
				g.setPaint(PlafPaintUtils.getVerticalGradient("aquaColorWell",
						height, y, new float[] { 0, 1 }, normalGradient));
			} else {
				g.setPaint(PlafPaintUtils.getVerticalGradient(
						"aquaSelectedColorWell", height, y,
						new float[] { 0, 1 }, selectedGradient));
			}
			g.drawLine(x + 1, y + 1, x + 1, y + height - 2);
			g.drawLine(x + width - 2, y + 1, x + width - 2, y + height - 2);
		}

	}
}