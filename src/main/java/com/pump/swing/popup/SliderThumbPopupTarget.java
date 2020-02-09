package com.pump.swing.popup;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.Objects;

import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSliderUI;

public class SliderThumbPopupTarget implements PopupTarget {
	JSlider slider;

	public SliderThumbPopupTarget(JSlider slider) {
		Objects.requireNonNull(slider);
		this.slider = slider;
		getLocalThumbScreenRect();
	}

	/**
	 * Return the bounds of the slider thumb relative to the JSliders coordinate
	 * system.
	 */
	protected Rectangle getLocalThumbScreenRect() {
		BasicSliderUI sliderUI = slider.getUI() instanceof BasicSliderUI ? (BasicSliderUI) slider
				.getUI() : null;
		if (sliderUI == null)
			throw new UnsupportedOperationException("This UI is unsupported "
					+ slider.getUI().getClass().getName());

		try {
			Field f = BasicSliderUI.class.getDeclaredField("thumbRect");
			f.setAccessible(true);
			Rectangle thumbRect = (Rectangle) f.get(sliderUI);
			if (thumbRect == null)
				throw new NullPointerException();
			if (thumbRect.isEmpty())
				throw new IllegalArgumentException();

			thumbRect = new Rectangle(thumbRect);
			return thumbRect;
		} catch (NoSuchFieldException | SecurityException
				| IllegalAccessException e) {
			throw new UnsupportedOperationException(e);
		}

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

	/**
	 * Return the bounds of the slider thumb relative to the screen.
	 */
	@Override
	public Rectangle getScreenBounds() {
		Rectangle rect = getLocalThumbScreenRect();
		Point p = new Point(0, 0);
		SwingUtilities.convertPointToScreen(p, slider);
		rect.x += p.x;
		rect.y += p.y;
		return rect;
	}
}
