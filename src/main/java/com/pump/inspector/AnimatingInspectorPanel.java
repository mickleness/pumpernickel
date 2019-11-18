package com.pump.inspector;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPanel;

import com.pump.plaf.AnimationManager;
import com.pump.util.JVM;

/**
 * This panel animates changes to InspectorRowPanels' visibility.
 * <p>
 * Pass this object to the Inspector constructor.
 */
public class AnimatingInspectorPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final String PROPERTY_ANIMATING_HEIGHT = AnimatingInspectorPanel.class
			.getName() + "#animatingHeight";

	private static final String PROPERTY_ANIMATING_OPACITY = AnimatingInspectorPanel.class
			.getName() + "#animatingOpacity";

	PropertyChangeListener repaintPropertyChangeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			repaint();
		}

	};

	public AnimatingInspectorPanel() {
		setOpaque(false);
		addContainerListener(new ContainerListener() {

			@Override
			public void componentAdded(ContainerEvent e) {
				e.getChild().addPropertyChangeListener(
						PROPERTY_ANIMATING_HEIGHT,
						repaintPropertyChangeListener);
				e.getChild().addPropertyChangeListener(
						PROPERTY_ANIMATING_OPACITY,
						repaintPropertyChangeListener);
			}

			@Override
			public void componentRemoved(ContainerEvent e) {
				e.getChild().removePropertyChangeListener(
						PROPERTY_ANIMATING_HEIGHT,
						repaintPropertyChangeListener);
				e.getChild().removePropertyChangeListener(
						PROPERTY_ANIMATING_OPACITY,
						repaintPropertyChangeListener);
			}

		});
	}

	@Override
	public void paint(Graphics g) {
		Inspector i = (Inspector) getClientProperty(Inspector.PROPERTY_INSPECTOR);
		if (i == null) {
			super.paint(g);
			return;
		}

		// I don't understand exactly why, but when any descendant of these
		// RowPanels has double-buffering enabled it can screw up other 
		// (unrelated) repaints in the UI.

		setDoubleBuffered(this, false);

		g = (Graphics2D) getComponentGraphics(g.create());

		paintComponent(g);
		paintBorder(g);

		double y = 0;
		InspectorRowPanel[] rows = i.getRows();
		for (int a = 0; a < rows.length; a++) {
			InspectorRowPanel p = rows[a];

			Rectangle rect = p.getBounds();
			Graphics2D g2 = (Graphics2D) g.create();
			double h = AnimationManager.setTargetProperty(p,
					PROPERTY_ANIMATING_HEIGHT, rect.height, 20, 6);
			double targetOpacity = p.isVisible() ? 1 : 0;
			double opacity = AnimationManager.setTargetProperty(p,
					PROPERTY_ANIMATING_OPACITY, targetOpacity, 20, .1);
			g2.translate(rect.x, y);
			g2.clipRect(0, 0, getWidth(), (int) h);
			if(JVM.isMac) {
				
				// on Windows: changing the opacity results in a tiny subtle flicker when we
				// transition to fully opaque. It's as if a rendering hint is changing. Text
				// in a text field (including spinners) shifts a little bit.
				// I couldn't figure out how to prevent this, so for now I'll just turn it off.
				// (I tried changing the RenderingHints and using my own buffer, but no luck.)
				
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
						(float) opacity));
			}
			p.setShowing(true);
			p.paintComponent(g2);
			p.paintComponents(g2);
			p.setShowing(null);
			g2.dispose();

			y += h;
		}

		g.dispose();
	}

	/**
	 * Set double buffering for a component and all of its descendants.
	 */
	private static void setDoubleBuffered(JComponent jc, boolean b) {
		jc.setDoubleBuffered(b);
		for (Component c : jc.getComponents()) {
			if (c instanceof JComponent)
				setDoubleBuffered((JComponent) c, b);
		}
	}
}