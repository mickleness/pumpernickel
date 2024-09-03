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
package com.pump.swing.popup;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;

import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSliderUI;

import com.pump.graphics.vector.VectorImage;

/**
 * This PopupTarget points to the thumb of a JSlider. Currently this only
 * supports a slider that uses a BasicSliderUI.
 */
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
		BasicSliderUI sliderUI = slider.getUI() instanceof BasicSliderUI
				? (BasicSliderUI) slider.getUI()
				: null;
		if (sliderUI == null)
			throw new UnsupportedOperationException("This UI is unsupported "
					+ slider.getUI().getClass().getName());

		VectorImage img = new VectorImage();
		Graphics2D g = img.createGraphics();
		sliderUI.paintThumb(g);
		g.dispose();

		return img.getBounds().getBounds();

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