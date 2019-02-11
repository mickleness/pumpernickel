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
package com.pump.swing.toolbar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * This is a component that represents padding in the CustomizedToolbar.
 *
 */
class SpaceComponent extends JComponent {
	private static final long serialVersionUID = 1L;
	public static final int SPACE_COMPONENT_WIDTH = 32;

	CustomizedToolbar toolbar;
	boolean showBorder = false;
	boolean paintArrows = false;
	PropertyChangeListener propertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			updateBorder();
		}
	};

	/**
	 * Creates a new SpaceComponent
	 * 
	 * @param tb
	 *            the toolbar this gap is associated with. This component
	 *            listens to the toolbar for a specific property: when that
	 *            property is changed, this component may choose to paint itself
	 *            (such as when borders/arrows are necessary).
	 * @param paintArrows
	 *            whether the arrows should be painted or not.
	 */
	public SpaceComponent(CustomizedToolbar tb, boolean paintArrows) {
		toolbar = tb;
		setPreferredSize(new Dimension(SPACE_COMPONENT_WIDTH,
				toolbar.minimumHeight));
		setSize(getPreferredSize());
		updateBorder();
		tb.addPropertyChangeListener(CustomizedToolbar.DIALOG_ACTIVE,
				propertyListener);
		this.paintArrows = paintArrows;
	}

	private void updateBorder() {
		Boolean b = (Boolean) toolbar
				.getClientProperty(CustomizedToolbar.DIALOG_ACTIVE);
		if (b == null)
			b = Boolean.FALSE;
		showBorder = b.booleanValue();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (showBorder) {

			boolean darkBackground = CustomizedToolbar
					.isDarkBackground(SwingUtilities.getWindowAncestor(this));

			boolean contains = contains(toolbar, this);
			boolean lightBorder = contains && darkBackground;

			paintSpace(g, lightBorder, paintArrows, getWidth(), getHeight());
		}
	}

	/**
	 * Paints the visual elements of a SpaceComponent.
	 * 
	 * @param g
	 *            the Graphics to draw to
	 * @param lightBorder
	 *            whether the border should be light or dark.
	 * @param drawArrows
	 *            whether arrows should be painted
	 * @param w
	 *            the width to paint
	 * @param h
	 *            the height to paint
	 */
	protected static void paintSpace(Graphics g, boolean lightBorder,
			boolean drawArrows, int w, int h) {
		Graphics2D g2 = (Graphics2D) g;
		if (lightBorder) {
			g2.setColor(new Color(255, 255, 255, 80));
		} else {
			g2.setColor(new Color(0, 0, 0, 80));
		}
		g2.setStroke(new BasicStroke(1));
		g2.drawRect(0, 0, w - 1, h - 1);

		GeneralPath path = new GeneralPath();
		if (drawArrows) {
			g2.setColor(new Color(0, 0, 0, 180));

			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			path.reset();
			path.moveTo(0, h / 2 + .5f);
			path.lineTo(6, h / 2 - 2);
			path.lineTo(6, h / 2 + 3);
			path.lineTo(0, h / 2 + .5f);
			g2.fill(path);

			path.reset();
			path.moveTo(w - 1, h / 2 + .5f);
			path.lineTo(w - 7, h / 2 - 2);
			path.lineTo(w - 7, h / 2 + 3);
			path.lineTo(w - 1, h / 2 + .5f);
			g2.fill(path);

			g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
					BasicStroke.JOIN_BEVEL, 10, new float[] { 1, 2 }, 0));
			path.reset();
			path.moveTo(7, h / 2);
			path.lineTo(w - 8, h / 2);
			g2.draw(path);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}
	}

	private boolean contains(Container container, Component component) {
		for (int a = 0; a < container.getComponentCount(); a++) {
			Component innerComponent = container.getComponent(a);

			if (innerComponent == component) {
				return true;
			}
			if (innerComponent instanceof Container) {
				if (contains((Container) innerComponent, component))
					return true;
			}
		}
		return false;
	}
}