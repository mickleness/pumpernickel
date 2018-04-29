package com.pump.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicProgressBarUI;

import com.pump.geom.TransformUtils;

/**
 * This ProgressBarUI renders as a circle, as if a slice of a pie chart
 * transitioned from 0% to 100%.
 * <p>
 * There's another simpler implementation of this concept <a href=
 * "https://java-swing-tips.blogspot.com/2014/06/how-to-create-circular-progress.html"
 * >here</a>.
 */
public class CircularProgressBarUI extends BasicProgressBarUI {
	public static Color COLOR_DEFAULT_FOREGROUND = getDefaultForegroundColor();
	public static Color COLOR_DEFAULT_BACKGROUND = getDefaultBackgroundColor();

	private static Color getDefaultForegroundColor() {
		Color c = UIManager.getColor("controlHighlight");
		if (c == null)
			c = new Color(0x3b5cfc);
		return c;
	}

	private static Color getDefaultBackgroundColor() {
		Color c = UIManager
				.getColor("TextComponent.selectionBackgroundInactive");
		if (c == null)
			c = new Color(0xdcdcdc);
		return c;
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension d = super.getPreferredSize(c);
		int v = Math.max(d.width, d.height);
		d.setSize(v, v);
		return d;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setForeground(COLOR_DEFAULT_FOREGROUND);
		c.setBackground(COLOR_DEFAULT_BACKGROUND);
		c.setOpaque(false);
	}

	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);

		Insets i = progressBar.getInsets();

		int x = 0;
		int y = 0;
		int width = c.getWidth();
		int height = c.getHeight();
		x += i.left;
		y += i.top;
		width -= i.left + i.right;
		height -= i.top + i.bottom;

		int diameter = Math.min(width, height);
		int radius = diameter / 2;
		int centerX = x + width / 2;
		int centerY = y + height / 2;

		float strokeWidth = ((float) diameter) / 10f + 1;
		if (!progressBar.isStringPainted())
			strokeWidth *= 2;

		if (!progressBar.isIndeterminate()) {
			double extent = progressBar.getPercentComplete() * 360;
			paintArc(g, progressBar.getForeground(), centerX, centerY, 0,
					extent, radius - strokeWidth / 2, strokeWidth);
			paintArc(g, progressBar.getBackground(), centerX, centerY, extent,
					360 - extent, radius - strokeWidth / 2, strokeWidth);

			if (progressBar.isStringPainted()) {
				Font font = progressBar.getFont();
				font = font.deriveFont(((float) radius) / 2f);
				PlafPaintUtils.paintCenteredString(g, progressBar.getString(),
						font, centerX, centerY);
			}
		} else {
			for (int degree = 0; degree < 360; degree += 60) {
				Color color = progressBar.getForeground();

				float k = ((float) (System.currentTimeMillis() % 1000)) / 1000f
						+ ((float) degree) / 360;
				k = k % 1;
				int alpha = (int) (255 - 255 * k);
				color = new Color(color.getRed(), color.getGreen(),
						color.getBlue(), alpha);
				int z = degree + (int) ((1 - k) * 30);
				paintArc(g, color, centerX, centerY, z, 30, radius
						- strokeWidth * k / 2 - strokeWidth / 2, strokeWidth
						* ((1 - k) / 4 + .75f));
			}
		}
	}

	@Override
	protected void setAnimationIndex(int newValue) {
		super.setAnimationIndex(newValue);
		// this is a hackish way to get constant repaints, but it works:
		progressBar.repaint();
	}

	private void paintArc(Graphics2D g, Color color, double centerX,
			double centerY, double startAngle, double endAngle, double radius,
			float strokeWidth) {
		g = (Graphics2D) g.create();
		g.transform(TransformUtils.flipHorizontal(centerX));
		g.rotate(-Math.PI / 2, centerX, centerY);
		g.setPaint(color);
		Arc2D progressArc = new Arc2D.Double(centerX - radius,
				centerY - radius, radius * 2, radius * 2, startAngle, endAngle,
				Arc2D.OPEN);
		g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10));
		g.draw(progressArc);
		g.dispose();
	}
}