package com.pump.swing.popup;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSliderUI;

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
		if (component instanceof JSlider) {
			Rectangle thumb = getSliderThumbScreenRect((JSlider) component);
			if (thumb != null)
				return thumb;
		}

		Insets insets;
		if (component instanceof JComponent) {
			insets = ((JComponent) component).getInsets();
			insets.left = Math.min(10, insets.left);
			insets.right = Math.min(10, insets.right);
			insets.top = Math.min(10, insets.top);
			insets.bottom = Math.min(10, insets.bottom);
		} else {
			insets = new Insets(0,0,0,0);
		}

		Point p = component.getLocationOnScreen();
		Rectangle r = new Rectangle(p, component.getSize());
		r.x += insets.left;
		r.y += insets.top;
		r.width -= insets.left + insets.right;
		r.height -= insets.bottom + insets.top;
		return r;
	}

	protected Rectangle getSliderThumbScreenRect(JSlider slider) {
		BasicSliderUI sliderUI = slider.getUI() instanceof BasicSliderUI ? (BasicSliderUI) slider
				.getUI() : null;
		if (sliderUI == null)
			return null;

		try {
			Field f = BasicSliderUI.class.getDeclaredField("thumbRect");
			f.setAccessible(true);
			Rectangle thumbRect = (Rectangle) f.get(sliderUI);
			if (thumbRect == null || thumbRect.isEmpty())
				return null;

			thumbRect = new Rectangle(thumbRect);
			Point p = new Point(0, 0);
			SwingUtilities.convertPointToScreen(p, slider);
			thumbRect.x += p.x;
			thumbRect.y += p.y;
			return thumbRect;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

		// this... kinda worked. It derives the thumb location.
		// But it was still a little imprecise, and just
		// grabbing the thumbRect is simplest.

		// int value = slider.getValue();
		// if (slider.getOrientation() == JSlider.HORIZONTAL) {
		// float bestX = slider.getWidth() / 2;
		// int span = 0;
		// int bestDiff = Integer.MAX_VALUE;
		// for (int x = 0; x < slider.getWidth(); x++) {
		// int v = sliderUI.valueForXPosition(x);
		// int diff = (Math.abs(value - v));
		// if (diff < bestDiff) {
		// bestX = x;
		// span = 1;
		// bestDiff = diff;
		// } else if (diff == bestDiff) {
		// bestX = (bestX * span + x) / (++span);
		// }
		// }
		// return new Point((int) Math.round(bestX), slider.getHeight());
		// } else if (slider.getOrientation() == JSlider.VERTICAL) {
		// float bestY = slider.getHeight() / 2;
		// int span = 0;
		// int bestDiff = Integer.MAX_VALUE;
		// for (int y = 0; y < slider.getHeight(); y++) {
		// int v = ui.valueForYPosition(y);
		// int diff = (Math.abs(value - v));
		// if (diff < bestDiff) {
		// bestY = y;
		// span = 1;
		// bestDiff = diff;
		// } else if (diff == bestDiff) {
		// bestY = (bestY * span + y) / (++span);
		// }
		// }
		// return new Point(slider.getWidth(), (int) Math.round(bestY));
		// }
		// throw new RuntimeException("Unexpected orientation: "
		// + slider.getOrientation());

	}

}
