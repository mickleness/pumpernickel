package com.pump.swing.popup;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;

import javax.swing.JComponent;

/**
 * This PopupTarget points to a component's bounds.
 */
public class BasicPopupTarget implements PopupTarget {
	Component component;

	public BasicPopupTarget(Component c) {
		Objects.requireNonNull(c);
		this.component = c;
	}

	@Override
	public Rectangle getScreenBounds() {
		if (!component.isShowing())
			return null;

		Insets insets;
		if (component instanceof JComponent) {
			insets = ((JComponent) component).getInsets();
			insets.left = Math.min(10, insets.left);
			insets.right = Math.min(10, insets.right);
			insets.top = Math.min(10, insets.top);
			insets.bottom = Math.min(10, insets.bottom);
		} else {
			insets = new Insets(0, 0, 0, 0);
		}

		Point p = component.getLocationOnScreen();
		Rectangle r = new Rectangle(p, component.getSize());
		r.x += insets.left;
		r.y += insets.top;
		r.width -= insets.left + insets.right;
		r.height -= insets.bottom + insets.top;
		return r;
	}

}
