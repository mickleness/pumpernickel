package com.pump.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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

	/**
	 * This client property maps to a Boolean indicating whether this UI should
	 * pulse when we reach 100%. If undefined this is assumed to be true.
	 */
	public static final String PROPERTY_PULSE_COMPLETION_ACTIVE = CircularProgressBarUI.class
			.getName() + "#pulseCompletion";
	/**
	 * This client property maps to a Boolean indicating whether this UI should
	 * paint a subtle "spark" animation when the JProgressBar doesn't show any
	 * activity for 2 seconds. If undefined this is assumed to be true.
	 */
	public static final String PROPERTY_SPARK_ACTIVE = CircularProgressBarUI.class
			.getName() + "#sparkActive";

	private static final String PROPERTY_SPARK_ANGLE = CircularProgressBarUI.class
			.getName() + "#sparkPosition";
	private static final String PROPERTY_STROKE_MULTIPLIER = CircularProgressBarUI.class
			.getName() + "#strokeMultiplier";

	/**
	 * The degrees the spark spans.
	 */
	private static final int SPARK_EXTENT = 20;

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

	/**
	 * When we first reach completion this listener initiates a .5 second
	 * animation to pulse the stroke width.
	 */
	ChangeListener pulseChangeListener = new ChangeListener() {
		boolean wasComplete = false;
		long pulseStartTime = -1;
		Timer pulseCompletionTimer = new Timer(10, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				long elapsed = System.currentTimeMillis() - pulseStartTime;
				float f = ((float) elapsed) / 500f;
				if (f >= 1) {
					progressBar.putClientProperty(PROPERTY_STROKE_MULTIPLIER,
							null);
					pulseCompletionTimer.stop();
				} else {
					double m = (1 + .3 * Math.sin(4 * Math.PI * f) * (1 - f));
					progressBar
							.putClientProperty(PROPERTY_STROKE_MULTIPLIER, m);
				}
			}

		});

		@Override
		public void stateChanged(ChangeEvent e) {
			// address the pulse completion:
			boolean isComplete = progressBar.getPercentComplete() == 1;
			if (isComplete) {
				if (!wasComplete && isPulseCompletionActive()) {
					pulseStartTime = System.currentTimeMillis();
					pulseCompletionTimer.start();
				}
			} else {
				pulseCompletionTimer.stop();
			}
			wasComplete = isComplete;
		}

		private boolean isPulseCompletionActive() {
			Boolean b = (Boolean) progressBar
					.getClientProperty(PROPERTY_PULSE_COMPLETION_ACTIVE);
			if (b == null)
				return true;
			return b;
		}
	};

	/**
	 * For determinate incomplete progress bars: every time the value changes we
	 * restart a 2-second timer to trigger the "spark" animation.
	 */
	ChangeListener sparkChangeListener = new ChangeListener() {
		long lastValueChangeTime = -1;
		Timer sparkInitiateTimer = new Timer(2000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				long elapsed = System.currentTimeMillis() - lastValueChangeTime;
				if (elapsed >= 2000 && isSparkActive()) {
					initiateSpark();
					lastValueChangeTime = System.currentTimeMillis();
				}
			}

			int sparkStartValue = -1;
			long sparkStartTime = -1;
			Timer moveSparkTimer = new Timer(5, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					long elapsed = System.currentTimeMillis() - sparkStartTime;
					int r = Math.min(progressBar.getWidth(),
							progressBar.getHeight()) / 2;
					float duration = (float) (250 + r * 2 + progressBar
							.getPercentComplete() * 300);
					float f = ((float) elapsed) / duration;
					if (f >= 1) {
						progressBar.putClientProperty(PROPERTY_SPARK_ANGLE,
								null);
						moveSparkTimer.stop();
					} else {
						int newValue = (int) (sparkStartValue * (1 - f));
						progressBar.putClientProperty(PROPERTY_SPARK_ANGLE,
								newValue);
					}
				}

			});

			private void initiateSpark() {
				int angle = sparkStartValue = (int) (progressBar
						.getPercentComplete() * 360 + SPARK_EXTENT);
				sparkStartTime = System.currentTimeMillis();
				progressBar.putClientProperty(PROPERTY_SPARK_ANGLE, angle);
				moveSparkTimer.restart();
			}

		});

		@Override
		public void stateChanged(ChangeEvent e) {
			// address the pulse completion:
			boolean isComplete = progressBar.getPercentComplete() == 1;
			if (!isComplete) {
				sparkInitiateTimer.restart();
			} else {
				sparkInitiateTimer.stop();
			}
		}

		private boolean isSparkActive() {
			Boolean b = (Boolean) progressBar
					.getClientProperty(PROPERTY_SPARK_ACTIVE);
			if (b == null)
				return true;
			return b;
		}
	};

	PropertyChangeListener repaintListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			progressBar.repaint();
		}

	};

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
		progressBar.addChangeListener(pulseChangeListener);
		progressBar.addChangeListener(sparkChangeListener);
		progressBar.addPropertyChangeListener(PROPERTY_STROKE_MULTIPLIER,
				repaintListener);
		progressBar.addPropertyChangeListener(PROPERTY_SPARK_ANGLE,
				repaintListener);
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
			Number multiplier = (Number) progressBar
					.getClientProperty(PROPERTY_STROKE_MULTIPLIER);
			if (multiplier != null)
				strokeWidth = strokeWidth * multiplier.floatValue();

			double extent = progressBar.getPercentComplete() * 360;
			paintArc(g, progressBar.getForeground(), centerX, centerY, 0,
					extent, radius - strokeWidth / 2, strokeWidth);
			paintArc(g, progressBar.getBackground(), centerX, centerY, extent,
					360 - extent, radius - strokeWidth / 2, strokeWidth);

			Number sparkAngle = (Number) progressBar
					.getClientProperty(PROPERTY_SPARK_ANGLE);
			if (sparkAngle != null) {
				int a1 = sparkAngle.intValue();
				int a2 = sparkAngle.intValue() - SPARK_EXTENT;
				int b1 = (int) Math.max(0, Math.min(extent, a1));
				int b2 = (int) Math.max(0, Math.min(extent, a2));
				if (b2 >= 0) {
					Color sparkColor = new Color(0xddffffff, true);
					paintArc(g, sparkColor, centerX, centerY, b1, b2 - b1,
							radius - strokeWidth / 2, strokeWidth);
				}
			}

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

	/**
	 * This paints a portion of the edge of the circle. Degrees are interpreted
	 * in a clockwise orientation from the top of the circle. (That is: 0
	 * degrees is the 12:00 position, 90 degrees is the 3:00 position, 180
	 * degrees is the 6:00 position, 270 degrees is the 9:00 position, and 360
	 * degrees is a complete arc back a the 12:00 position.)
	 * 
	 * @param g
	 *            the Graphics2D to paint to.
	 * @param color
	 *            the color to paint the arc
	 * @param centerX
	 *            the x-coordinate of the center of the circle
	 * @param centerY
	 *            the y-coordinate of the center of the circle
	 * @param startAngle
	 *            the angle where this arc begins in degrees
	 * @param extent
	 *            the number of degrees this arc covers
	 * @param radius
	 *            the radius of the circle
	 * @param strokeWidth
	 *            the stroke width
	 */
	private void paintArc(Graphics2D g, Color color, double centerX,
			double centerY, double startAngle, double extent, double radius,
			float strokeWidth) {
		g = (Graphics2D) g.create();
		g.transform(TransformUtils.flipHorizontal(centerX));
		g.rotate(-Math.PI / 2, centerX, centerY);
		g.setPaint(color);
		Arc2D progressArc = new Arc2D.Double(centerX - radius,
				centerY - radius, radius * 2, radius * 2, startAngle, extent,
				Arc2D.OPEN);
		g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10));
		g.draw(progressArc);
		g.dispose();
	}
}