package com.pump.plaf.button.mixed;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Line2D;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.plaf.basic.BasicRadioButtonUI;

/**
 * This MixedStateUI is compatible with ButtonUIs that subclass BasicRadioButtonUI.
 */
public class BasicMixedStateUI extends MixedStateUI {

	/**
	 * This renders a diagonal tick above an existing icon.
	 */
	class TickIcon implements Icon {
		Icon parentIcon;

		TickIcon(Icon parentIcon) {
			Objects.requireNonNull(parentIcon);
			this.parentIcon = parentIcon;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics g2 = g.create();
			parentIcon.paintIcon(c, g2, x, y);
			g2.dispose();
			
			paintTick(g, x, y, getIconWidth(), getIconHeight());
		}

		@Override
		public int getIconWidth() {
			return parentIcon.getIconWidth();
		}

		@Override
		public int getIconHeight() {
			return parentIcon.getIconHeight();
		}

	};

	protected static void paintTick(Graphics g, int x, int y, int w, int h) {
		Graphics2D g2 = (Graphics2D) g.create();

		// Check both highlights and choose the better contrast:

		for (Color color : new Color[] { SystemColor.controlHighlight,
				SystemColor.controlDkShadow }) {
			float hsbDistance = getHSBDistance(SystemColor.window, color);
			if (hsbDistance > 1) {
				g2.setColor(color);
				break;
			}
		}
		g2.setStroke(new BasicStroke(2.1f));
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		int k = Math.min(w, h) / 5;
		int cx = x + w / 2;
		int cy = y + h / 2;
		g2.draw(new Line2D.Float(cx - k, cy - k, cx + k + 1, cy + k + 1));
		g2.dispose();
	}

	private static float getHSBDistance(Color c1, Color c2) {
		float[] f1 = new float[3];
		float[] f2 = new float[3];
		Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), f1);
		Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), f2);
		float v = (float) Math.sqrt((f1[0] - f2[0]) * (f1[0] - f2[0])
				+ (f1[1] - f2[1]) * (f1[1] - f2[1]) + (f1[2] - f2[2])
				* (f1[2] - f2[2]));
		return v;
	}
	
	protected final BasicRadioButtonUI buttonUI;
	protected final Icon defaultIcon;

	protected BasicMixedStateUI(JCheckBox checkBox) {
		super(checkBox);
		buttonUI = (BasicRadioButtonUI) checkBox.getUI();
		defaultIcon = buttonUI.getDefaultIcon();
	}

	@Override
	protected void doInstall() {
		TickIcon tickIcon = new TickIcon(defaultIcon);
		checkBox.setIcon(tickIcon);
	}

	@Override
	protected void doUninstall() {
		checkBox.setIcon(defaultIcon);
	}

}
