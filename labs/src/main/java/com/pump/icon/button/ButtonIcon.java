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
package com.pump.icon.button;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Icon;

import com.pump.geom.RectangularTransform;
import com.pump.data.Property;

/**
 * This is an icon that can change colors based on a set of input variables
 * (rollover, enabled, pressed, etc.).
 * <p>
 * If you use the constructor that accepts a button, then this is all
 * automatically handled for you. Whenever a visual change is appropriate a
 * 100ms animation will fade in the new color scheme.
 */
public abstract class ButtonIcon implements Icon {
	Dimension transformedSize;

	Map<String, Color> colors;
	/**
	 * This may be null, depending on whether this was constructed using an
	 * AbstractButton or not.
	 */
	protected ButtonIconManager buttonIconManager;

	/**
	 * Create a new ButtonIcon that is not automatically bound to a button.
	 * <p>
	 * When you use this constructor: you must call {@link #setColors(Map)}
	 * before rendering this icon.
	 */
	public ButtonIcon() {
	}

	/**
	 * Create a new ButtonIcon and configure a new {@link ButtonIconManager} to
	 * coordinate all the animations and repaints when the button state changes.
	 * 
	 * @param button
	 *            the button this ButtonIcon should be associated with.
	 * @param colors
	 *            this translates the button's state into a set of colors used
	 *            to render this icon.
	 * @param properties
	 *            an optional set of properties that will be stored in each
	 *            ButtonState. This lets you add custom attributes that are not
	 *            already defined a ButtonState.
	 */
	public ButtonIcon(AbstractButton button, ButtonIconColors colors,
			Property... properties) {
		buttonIconManager = new ButtonIconManager(button, this, colors,
				properties);
	}

	@Override
	public void paintIcon(Component c, Graphics g0, int x, int y) {
		Graphics2D g = (Graphics2D) g0.create();
		try {
			g.translate(x, y);
			g.transform(createTransform());
			paintIcon(c, g);
		} finally {
			g.dispose();
		}
	}

	/**
	 * Set the colors used in {@link #paintIcon(Component, Graphics2D)}.
	 * 
	 * @param colors
	 *            the new colors to paint with.
	 */
	public void setColors(Map<String, Color> colors) {
		this.colors = colors;
	}

	/**
	 * Return the current set of colors to paint with.
	 */
	public Map<String, Color> getColors() {
		return new HashMap<>(colors);
	}

	/**
	 * Paint this icon using {@link #getColors()} at the default sieze (that is,
	 * paint it as if it should fit within {@link #getDefaultIconWidth()} and
	 * {@link #getDefaultIconHeight()}).
	 */
	protected abstract void paintIcon(Component c, Graphics2D g);

	private AffineTransform createTransform() {
		if (transformedSize == null)
			return new AffineTransform();
		Rectangle oldSize = new Rectangle(0, 0, getDefaultIconWidth(),
				getDefaultIconHeight());
		Rectangle newSize = new Rectangle(0, 0, transformedSize.width,
				transformedSize.height);
		return new RectangularTransform(oldSize, newSize).createAffineTransform();
	}

	@Override
	public int getIconWidth() {
		if (transformedSize != null)
			return transformedSize.width;
		return getDefaultIconWidth();
	}

	@Override
	public int getIconHeight() {
		if (transformedSize != null)
			return transformedSize.height;
		return getDefaultIconHeight();
	}

	/**
	 * Set the size of this icon. When painting a scaling AffineTransform is
	 * applied so this icon will fit within the requested size.
	 * 
	 * @param size
	 *            if null then this icon returns to its default size.
	 */
	public void setSize(Dimension size) {
		transformedSize = size;
	}

	/**
	 * The {@link #paintIcon(Component, Graphics2D)} method should paint the
	 * icon using this width in mind. If {@link #setSize(Dimension)} has been
	 * called then the Graphics2D that we're painting to may be scaled.
	 */
	protected abstract int getDefaultIconWidth();

	/**
	 * The {@link #paintIcon(Component, Graphics2D)} method should paint the
	 * icon using this height in mind. If {@link #setSize(Dimension)} has been
	 * called then the Graphics2D that we're painting to may be scaled.
	 */
	protected abstract int getDefaultIconHeight();
}