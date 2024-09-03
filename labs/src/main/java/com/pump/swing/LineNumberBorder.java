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
package com.pump.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

/**
 * This combines a {@code javax.swing.Border} with a {@link LineNumberRenderer}
 * to help paint the line numbers for a {@code JTextComponent}.
 * <p>
 * The advantage of offering line numbers in a border is: it can float outside
 * the viewport, so as the user scrolls horizontally the line numbers remain
 * visible.
 */
public class LineNumberBorder implements Border {

	protected LineNumberRenderer renderer;
	protected int width = 0;
	protected JScrollPane scrollPane;

	protected LineNumberBorder(JScrollPane scrollPane,
			JTextComponent textComponent) {
		this.scrollPane = scrollPane;
		renderer = new LineNumberRenderer(textComponent) {

			@Override
			protected void setMaximumWidth(int maxWidth) {
				width = maxWidth;
			}

			@Override
			protected float getWidth() {
				return width;
			}

			@Override
			protected void repaint() {
				JComponent t = jtc;
				while (t != null) {
					Border b = t.getBorder();
					if (containsThisBorder(b)) {
						t.repaint();
						return;
					}
					if (t.getParent() instanceof JComponent) {
						t = (JComponent) t.getParent();
					} else {
						return;
					}
				}
			}

			private boolean containsThisBorder(Border b) {
				if (b == LineNumberBorder.this) {
					return true;
				} else if (b instanceof CompoundBorder) {
					CompoundBorder cb = (CompoundBorder) b;
					return containsThisBorder(cb.getInsideBorder())
							|| containsThisBorder(cb.getOutsideBorder());
				}
				return false;
			}

		};
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		Graphics2D g2 = (Graphics2D) g.create();
		Point viewPosition = scrollPane.getViewport().getViewPosition();
		g2.clipRect(x, y, width, height);
		g2.translate(-viewPosition.x, -viewPosition.y);
		renderer.paintComponent(g2);
		g2.dispose();
	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(0, width, 0, 0);
	}

	@Override
	public boolean isBorderOpaque() {
		return false;
	}

	/**
	 * Add a new LineNumberBorder to a JScrollPane. Note this supplements the
	 * existing border without replacing it (using a {@code CompoundBorder}).
	 * 
	 * @param scrollPane
	 *            the scrollPane to modify the border of.
	 * @param textPane
	 *            the text component to map line numbers to. It is assumed (but
	 *            not enforced) that this text component is in the viewport of
	 *            this scrollpane.
	 */
	public static void install(final JScrollPane scrollPane, JTextPane textPane) {
		LineNumberBorder border = new LineNumberBorder(scrollPane, textPane);
		Border oldBorder = scrollPane.getBorder();
		scrollPane.setBorder(new CompoundBorder(border, oldBorder));
		scrollPane.getVerticalScrollBar().getModel()
				.addChangeListener(new ChangeListener() {

					@Override
					public void stateChanged(ChangeEvent e) {
						scrollPane.repaint();
					}
				});
	}

}