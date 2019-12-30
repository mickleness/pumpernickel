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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.pump.swing.PartialLineBorder;

/**
 * This ScrollBarUI only shows the thumb (no buttons) used a subtle rounded
 * rectangle.
 * <p>
 * This resemble's Aqua's current scrollbars. (It is not meant to be an exact
 * replica though.)
 */
public class SubtleScrollBarUI extends BasicScrollBarUI {
	int thumbWidth = 8;
	private static final String PROPERTY_ROLLOVER_BOOLEAN = SubtleScrollBarUI.class
			.getName() + "#rollover";
	private static final String PROPERTY_ACTIVE_NUMBER = SubtleScrollBarUI.class
			.getName() + "#active";

	protected JButton createDecreaseButton(int orientation) {
		JButton b = new JButton();
		b.setPreferredSize(new Dimension(0, 0));
		b.setVisible(false);
		return b;
	}

	protected JButton createIncreaseButton(int orientation) {
		return createDecreaseButton(orientation);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		int k = thumbWidth + 3 * 2;
		return new Dimension(k, k);
	}

	@Override
	protected void paintThumb(Graphics g0, JComponent c, Rectangle thumbBounds) {
		Graphics2D g = (Graphics2D) g0.create();
		int alpha = 60;
		Number rollover = (Number) scrollbar
				.getClientProperty(PROPERTY_ACTIVE_NUMBER);
		if (rollover != null) {
			alpha += (int) (rollover.floatValue() * 60);
		}
		g.setColor(new Color(0, 0, 0, alpha));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int k = thumbWidth;
		g.fill(new RoundRectangle2D.Float(thumbBounds.x + thumbBounds.width / 2
				- thumbWidth / 2, thumbBounds.y, thumbWidth,
				thumbBounds.height, k, k));
		g.dispose();
	}

	@Override
	protected void paintTrack(Graphics g0, JComponent c, Rectangle trackBounds) {
		if (scrollbar.isOpaque()) {
			Graphics2D g = (Graphics2D) g0.create();
			paintBackground(g);
			g.dispose();
		}
		super.paintTrack(g0, c, trackBounds);
	}

	protected void paintBackground(Graphics2D g) {
		GradientPaint gp = new GradientPaint(0, 0, new Color(0xf8f8f8),
				scrollbar.getWidth(), 0, new Color(0xfefefe));
		g.setPaint(gp);
		g.fillRect(0, 0, scrollbar.getWidth(), scrollbar.getHeight());
	}

	@Override
	protected void paintDecreaseHighlight(Graphics g) {

	}

	@Override
	protected void paintIncreaseHighlight(Graphics g) {

	}

	PropertyChangeListener opaqueListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent e) {
			refreshBorder();
		}
	};

	PropertyChangeListener rolloverListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			refreshActive();
		}

	};

	FocusListener focusListener = new FocusListener() {

		@Override
		public void focusGained(FocusEvent e) {
			refreshActive();
		}

		@Override
		public void focusLost(FocusEvent e) {
			refreshActive();
		}

	};

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setOpaque(false);
		c.addPropertyChangeListener("opaque", opaqueListener);
		trackColor = new Color(trackColor.getRed(), trackColor.getGreen(),
				trackColor.getBlue(), 0);
		c.addPropertyChangeListener(PROPERTY_ROLLOVER_BOOLEAN, rolloverListener);
		c.addFocusListener(focusListener);
		refreshActive();
		refreshBorder();

	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removePropertyChangeListener(PROPERTY_ROLLOVER_BOOLEAN,
				rolloverListener);
	}

	protected boolean isRollover() {
		Boolean b = (Boolean) scrollbar
				.getClientProperty(PROPERTY_ROLLOVER_BOOLEAN);
		if (b == null)
			return false;
		return b;
	}

	protected void refreshActive() {
		boolean isRollover = isRollover();
		boolean isActive = isRollover || isDragging || scrollbar.hasFocus();
		int activeValue = isActive ? 1 : 0;
		AnimationManager.setTargetProperty(scrollbar, PROPERTY_ACTIVE_NUMBER,
				activeValue, .1f);
	}

	protected void refreshBorder() {
		Border b = new EmptyBorder(3, 3, 3, 3);
		if (scrollbar.isOpaque()) {
			b = new CompoundBorder(new PartialLineBorder(
					new Color(0, 0, 0, 30), 0, 1, 0, 0), b);
		}
		scrollbar.setBorder(b);
	}

	@Override
	protected TrackListener createTrackListener() {
		return new TrackListener() {

			@Override
			public void mouseExited(MouseEvent e) {
				scrollbar.putClientProperty(PROPERTY_ROLLOVER_BOOLEAN,
						Boolean.FALSE);
				super.mouseExited(e);
			}

			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				refreshActive();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				scrollbar.putClientProperty(PROPERTY_ROLLOVER_BOOLEAN,
						Boolean.TRUE);
				super.mouseEntered(e);
			}

		};
	}
}