package com.pump.plaf.button;

import java.awt.BasicStroke;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Line2D;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicRadioButtonUI;

/**
 * This controls the mixed state appearance of a JCheckBox.
 */
public class MixedCheckBoxState {
	private static final String PROPERTY_MIXED = MixedCheckBoxState.class
			.getName() + "#mixedActive";
	private static final String PROPERTY_REGISTERED = MixedCheckBoxState.class
			.getName() + "#registered";

	/**
	 * This renders a diagonal tick on an existing icon.
	 */
	static class TickIcon implements Icon {
		Icon parentIcon;

		TickIcon(Icon parentIcon) {
			this.parentIcon = parentIcon;
		}
		
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics g2 = g.create();
			parentIcon.paintIcon(c, g2, x, y);
			g2.dispose();
			
			if(c instanceof JComponent) {
				JComponent jc = (JComponent) c;
				boolean paintTick = Boolean.TRUE.equals(jc.getClientProperty(PROPERTY_MIXED));
				if(jc instanceof AbstractButton && ((AbstractButton)jc).isSelected())
					paintTick = false;
				if(paintTick) {
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
	

	private static void register(final JCheckBox checkBox) {
		if (checkBox.getClientProperty(PROPERTY_REGISTERED) == null) {
			checkBox.putClientProperty(PROPERTY_REGISTERED, true);
			if(checkBox.getUI() instanceof BasicRadioButtonUI) {
				BasicRadioButtonUI ui = (BasicRadioButtonUI) checkBox.getUI();
				Icon defaultIcon = ui.getDefaultIcon();
				TickIcon tickIcon = new TickIcon(defaultIcon);
				checkBox.setIcon(tickIcon);
			}
		}
	}

	protected static void paintTick(Graphics g, int x, int y, int w, int h) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(SystemColor.controlDkShadow);
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

	/**
	 * Return true if a checkbox has a mixed state value assigned.
	 */
	public static boolean isMixedState(JCheckBox checkBox) {
		register(checkBox);
		return Boolean.TRUE.equals(checkBox.getClientProperty(PROPERTY_MIXED));
	}

	/**
	 * Assign a mixed state icon to a JCheckBox. This icon is rendered on top of an unselected checkbox icon.
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