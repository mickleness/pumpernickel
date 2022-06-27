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
package com.pump.plaf.button;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Line2D;
import java.util.Objects;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicRadioButtonUI;

import com.pump.reflect.Reflection;
import com.pump.util.JVM;

/**
 * This controls the mixed state appearance of a JCheckBox.
 */
public class MixedCheckBoxState {
	private static final String PROPERTY_MIXED = MixedCheckBoxState.class
			.getName() + "#mixedActive";
	private static final String PROPERTY_REGISTERED = MixedCheckBoxState.class
			.getName() + "#registered";

	/**
	 * This renders a diagonal tick above an existing icon.
	 */
	static class TickIcon implements Icon {
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

			if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				boolean paintTick = Boolean.TRUE.equals(jc
						.getClientProperty(PROPERTY_MIXED));
				if (jc instanceof AbstractButton
						&& ((AbstractButton) jc).isSelected())
					paintTick = false;
				if (paintTick) {
					paintTick(g, x, y, getIconWidth(), getIconHeight());
				}
			}
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

	/**
	 * This is a hacky implementation that renders the diagonal tick on top of
	 * Aqua's checkbox painter
	 */
	static class AquaTickIcon implements Icon {
		Dimension dimension;
		Border aquaBorder;

		public AquaTickIcon(Dimension dimension, Border aquaBorder) {
			this.aquaBorder = aquaBorder;
			this.dimension = dimension;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics g2 = g.create();
			Reflection
					.invokeMethod(aquaBorder.getClass(), aquaBorder,
							"paintButton", c, g2, x, y, getIconWidth(),
							getIconHeight());
			g2.dispose();

			if (c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				boolean paintTick = Boolean.TRUE.equals(jc
						.getClientProperty(PROPERTY_MIXED));
				if (jc instanceof AbstractButton
						&& ((AbstractButton) jc).isSelected())
					paintTick = false;
				if (paintTick) {
					paintTick(g, x, y, getIconWidth(), getIconHeight());
				}
			}
		}

		@Override
		public int getIconWidth() {
			return dimension.width;
		}

		@Override
		public int getIconHeight() {
			return dimension.height;
		}
	}

	private static void register(final JCheckBox checkBox) {
		if (checkBox.getClientProperty(PROPERTY_REGISTERED) == null) {
			checkBox.putClientProperty(PROPERTY_REGISTERED, true);
			ButtonUI ui = checkBox.getUI();
			Font font = checkBox.getFont();
			try {
				if (ui instanceof BasicRadioButtonUI) {
					BasicRadioButtonUI rui = (BasicRadioButtonUI) checkBox
							.getUI();
					Icon defaultIcon = rui.getDefaultIcon();
					TickIcon tickIcon = new TickIcon(defaultIcon);
					checkBox.setIcon(tickIcon);
				} else if (ui.getClass().getName()
						.equals("com.apple.laf.AquaButtonCheckBoxUI")) {
					try {
						// Aqua's "Painters" are borders that paint the innards
						// of buttons/components.
						Border aquaBorder = (Border) Reflection.invokeMethod(
								ui.getClass(), ui, "getPainter");

						// this icon is actually empty, but its dimensions are
						// valid:
						Icon i = (Icon) Reflection.invokeMethod(ui.getClass(),
								ui, "getDefaultIcon", checkBox);
						Dimension dimension = new Dimension(i.getIconWidth(),
								i.getIconHeight());

						AquaTickIcon tickIcon = new AquaTickIcon(dimension,
								aquaBorder);
						checkBox.setIcon(tickIcon);
					} catch (RuntimeException e) {
						// the Aqua implementation is hacky; maybe someday these
						// hacks will break
						throw new RuntimeException(
								"The AquaButtonCheckBoxUI is no longer compatible with this mixed state renderer.",
								e);
					}
				} else {
					throw new RuntimeException(
							"This ButtonUI is not compatible with this mixed state renderer. "
									+ ui.getClass().getName());
				}
			} finally {
				// on Mac changing the icon can change the font size
				checkBox.setFont(font);
			}
		}
	}

	protected static void paintTick(Graphics g, int x, int y, int w, int h) {
		Graphics2D g2 = (Graphics2D) g.create();

		// on Macs we want the rich dark blue of controlHighlight,
		// but on Windows controlDkShadow is better. Let's check both
		// and pick one that has sufficient contrast

		g2.setColor(SystemColor.controlText);

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
		if (JVM.isMac) {
			// this make the rendering symmetrical; I'm not sure why platforms
			// vary
			g2.draw(new Line2D.Float(cx - k, cy - k, cx + k, cy + k));
		} else {
			g2.draw(new Line2D.Float(cx - k, cy - k, cx + k + 1, cy + k + 1));
		}
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

	/**
	 * Return true if a checkbox has a mixed state value assigned.
	 */
	public static boolean isMixedState(JCheckBox checkBox) {
		register(checkBox);
		return Boolean.TRUE.equals(checkBox.getClientProperty(PROPERTY_MIXED));
	}

	/**
	 * Assign a mixed state icon to a JCheckBox. This icon is rendered on top of
	 * an unselected checkbox icon.
	 * 
	 * @return true if a change occurred, false otherwise.
	 */
	public static boolean setMixed(JCheckBox checkBox, boolean mixedState) {
		register(checkBox);
		if (mixedState) {
			Boolean oldValue = (Boolean) checkBox
					.getClientProperty(PROPERTY_MIXED);
			if (Boolean.TRUE.equals(oldValue)) {
				return false;
			} else {
				checkBox.putClientProperty(PROPERTY_MIXED, mixedState);
			}
		} else {
			Boolean oldValue = (Boolean) checkBox
					.getClientProperty(PROPERTY_MIXED);
			if (oldValue == null) {
				return false;
			} else {
				checkBox.putClientProperty(PROPERTY_MIXED, null);
			}
		}
		checkBox.repaint();
		return true;
	}
}