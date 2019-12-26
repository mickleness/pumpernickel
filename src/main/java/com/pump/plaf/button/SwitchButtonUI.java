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
package com.pump.plaf.button;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicRadioButtonUI;

import com.pump.icon.AquaSwitchButtonIcon;
import com.pump.util.JVM;

public class SwitchButtonUI extends BasicRadioButtonUI {
	private final static String propertyPrefix = "SwitchButton" + ".";

	public static ComponentUI createUI(JComponent c) {
		return new SwitchButtonUI();
	}

	public static final String PROPERTY_ICON_RECT = SwitchButtonUI.class
			.getName() + "#iconRect";

	@Override
	public void installUI(JComponent c) {
		initializeUIManager();
		super.installUI(c);
		c.setOpaque(false);
		c.setBorder(new BasicBorders.MarginBorder());
	}

	/**
	 * Return the icon's rectangle, or null if it can't be determined.
	 * <p>
	 * This method relies on the icon to set the {@link PROPERTY_ICON_RECT} on
	 * the JComponent. This is a kludgy implementation, but I don't see a better
	 * alternative without being able to refactor the parent class to give us
	 * access to the layout of the button components.
	 * 
	 * @param c
	 * @return
	 */
	protected Rectangle getIconRect(JComponent c) {
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setClip(new Rectangle(0, 0, c.getWidth(), c.getHeight()));
		paint(g, c);
		Rectangle iconRect = (Rectangle) c
				.getClientProperty(PROPERTY_ICON_RECT);
		if (iconRect != null)
			iconRect = (Rectangle) iconRect.clone();
		return iconRect;
	}

	private void initializeUIManager() {
		for (String word : new String[] { "background", "border",
				"disabledText", "focusInputMap", "font", "foreground",
				"margin", "select", "textIconGap", "textShiftOffset" }) {
			String otherKey = "CheckBox." + word;
			String myKey = getPropertyPrefix() + word;
			if (UIManager.get(myKey) == null) {
				Object value = UIManager.get(otherKey);
				UIManager.put(myKey, value);
			}
		}

		String iconKey = getPropertyPrefix() + "icon";
		Icon icon = UIManager.getIcon(iconKey);
		if (icon == null) {
			if (JVM.isMac) {
				icon = new AquaSwitchButtonIcon();
			} else {
				icon = new AquaSwitchButtonIcon();
			}
			UIManager.put(iconKey, icon);
		}
	}

	@Override
	protected String getPropertyPrefix() {
		return propertyPrefix;
	}

	@Override
	protected BasicButtonListener createButtonListener(final AbstractButton b) {
		return new BasicButtonListener(b) {
			boolean clickedIcon = false;
			boolean changedSelectedState = false;

			@Override
			public void mousePressed(MouseEvent e) {
				Rectangle r = getIconRect(b);
				if (b.isEnabled() && r != null && r.contains(e.getPoint())) {
					clickedIcon = true;
					b.getModel().setArmed(true);
					b.getModel().setPressed(true);
					b.repaint();

					if (b.isRequestFocusEnabled())
						b.requestFocus();
					return;
				}
				super.mousePressed(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (clickedIcon) {
					mouseDraggedIcon(e);
					return;
				}
				super.mouseDragged(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (clickedIcon) {
					b.getModel().setArmed(false);
					b.getModel().setPressed(false);
					if (changedSelectedState) {
						mouseDraggedIcon(e);
					} else {
						b.setSelected(!b.isSelected());
					}
					clickedIcon = false;
					changedSelectedState = false;
					return;
				}
				super.mouseReleased(e);
			}

			private void mouseDraggedIcon(MouseEvent e) {
				Rectangle r = getIconRect(b);
				boolean newSelected = e.getX() > r.getCenterX();
				if (newSelected != b.isSelected()) {
					b.setSelected(newSelected);
					changedSelectedState = true;
				}
			}
		};
	}
}