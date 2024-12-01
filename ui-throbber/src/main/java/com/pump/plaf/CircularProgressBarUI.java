/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.plaf;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;

/**
 * This ProgressBarUI renders as a circle. The rendering model used will stretch
 * to whatever the largest circle is that can be painted in the bounds provided.
 * You can call <code>myProgressBar.setPreferredSize(..)</code> to
 * create either a small or large arc. This UI can function at small sizes (like
 * 12x12), but the text become illegible so it shouldn't be used with
 * {@link javax.swing.JProgressBar#isStringPainted() isStringPainted()}.
 * <p>
 * Opinions on progress indicators are wide and varied (try googling "UX
 * progress indicator"). One <a
 * href="https://www.nngroup.com/articles/progress-indicators/">article</a>
 * summed up: <blockquote> The main guideline is to use a looped indicator for
 * delays of 2–9 seconds and a percent-done indicator for delays of 10 seconds
 * or more. </blockquote>
 * 
 * <p>
 * <h3>Determinate Behavior</h3>
 * <p>
 * For a determinate progress bar, this renders two complementary arcs using the
 * JProgressBar's foreground and background colors. For example, if
 * {@link javax.swing.JProgressBar#getPercentComplete()} is .25, then this
 * renders an arc from the 12:00 position clockwise to the 3:00 position using
 * the foreground color, and another clockwise arc from the 3:00 back to the
 * 12:00 position using the background color.
 * <p>
 * If {@link javax.swing.JProgressBar#isStringPainted() isStringPainted()}
 * returns true then this renders the percent complete inside the circle/arc.
 * The font is based on {@link javax.swing.JProgressBar#getFont()}, but the font
 * size will be automatically scaled to fit well within the arc. (That is: the
 * font family and style you assign is respected, but the font size is
 * calculated by the UI.)
 * <p>
 * <h3>Indeterminate Behavior</h3>
 * <p>
 * An indeterminate progress bar never renders text, and only renders animated
 * chasing partitioned arc. This is functionally equivalent to a
 * {@link com.pump.swing.JThrobber}.
 * <p>
 * Although the indeterminate UI doesn't render any text regardless of the
 * {@link javax.swing.JProgressBar#isStringPainted() isStringPainted()}
 * property, the presence of that property does alter the stroke thickness
 * (thick for no text, thin for text).
 * <p>
 * The indeterminate UI loops every 500 ms. <a href=
 * "https://medium.muz.li/how-progress-bars-or-loaders-impacts-the-user-experience-5082370f810b"
 * >This article</a> points out that indeterminate progress bars "are looped so
 * they should be a tad faster in terms of speed, which psychologically makes
 * user think data is getting loaded faster and makes them to have more
 * patience."
 * <p>
 * <h3>Optional Features</h3> This UI offers a few additional features you can
 * configure using client properties.
 * <p>
 * <h4>Pulse On Completion</h4> The {@link #PROPERTY_PULSE_COMPLETION_ACTIVE}
 * client property maps to a boolean. When this is true and the progress bar
 * reaches 100%, this UI pulses the thickness of the circle in a half-second
 * animation. This helps to draw attention to the progress bar (since it just
 * changed from "incomplete" to "complete") in a fun/celebratory way. If
 * undefined this is assumed to be true.
 * <h4>Spark Active</h4> The {@link #PROPERTY_SPARK_ACTIVE} client property maps
 * to a boolean. When this is true and two seconds have passed since the last
 * time the progress bar changed value: s small (20-degree) "spark" runs from
 * the edge of the foreground counter-clockwise to the origin. The intention
 * here is to offer some (any!) animation to reassure the user that the UI is
 * not frozen. In an ideal world all our progress bars will take less than a few
 * seconds, but if they must take a long time then we need to reassure the user
 * that we're still responsive and nothing is wrong. If undefined this is
 * assumed to be true.
 * <h4>Acceleration</h4> The {@link #PROPERTY_ACCELERATE} client property maps
 * to a boolean. When this is true this gives the illusion of slower progress
 * initially that accelerates as it approaches completion. (I recommend watching
 * a demo of this feature, because the text description is hard to explain.) If
 * the actual value of the progress bar is accelerating linearly from v=0 to
 * v=1, this renders the value of the progress bar as (v^2) or (v^3). So at 50%
 * we're rendering the progress bar as closer to 25%.
 * <p>
 * This idea is based on an idea put forward in <a href=
 * "https://www.smashingmagazine.com/2016/12/best-practices-for-animated-progress-indicators/"
 * >this article</a>: <blockquote>Keep in mind that perception can be just as
 * important as raw speed. In order to make a progress bar feel faster to users
 * you can start the progressive animation slower and allow it to move faster as
 * it approaches the end. This way, you give users a rapid sense of completion
 * time.</blockquote>
 * <p>
 * If you are unconvinced: imagine the opposite. Imagine a progress bar that,
 * whether real or imagined, slowed progress as the completion approached 100%.
 * This will frustrate users, but the opposite behavior (acceleration) will
 * pleasantly surprise users.
 * <p>
 * Because this renders a different progress value than what the JProgressBar
 * actually indicates, this should not be used with a painted String. If
 * undefined this property is assumed to be false.
 * <p>
 * <h4>Transitioning Values</h4>
 * <p>
 * The {@link #PROPERTY_TRANSITION} client property maps to a boolean. When this
 * is true every time the progress bar's value increases we'll launch a very
 * short animation to transition from the current value to the new value. This
 * is not useful if the progress bar moves in increments of 1, but if it moves
 * in increments of 5, 10, 20, etc. then this adds a more polished look to the
 * UI. If undefined this is assumed to be true.
 * <p>
 * <h4>Custom Stroke Width</h4>
 * <p>
 * The {@link #PROPERTY_STROKE_WIDTH} client property maps to a Number. When
 * this is not null this is the width of the stroke that is used to render the
 * arcs. When this is null a default value is calculated based on the size of
 * the circle/arc.
 */
public class CircularProgressBarUI extends BasicProgressBarUI {

	/*
	 * Looking for something simpler? Check out:
	 * https://java-swing-tips.blogspot.com/2014/06/how-to-create-circular-progress.html
	 */

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
	/**
	 * This client property maps to a Boolean indicating whether this UI should
	 * render the progress bar's completion in an accelerated graph. If
	 * undefined this is assumed to be false.
	 */
	public static final String PROPERTY_ACCELERATE = CircularProgressBarUI.class
			.getName() + "#accelerate";

	/**
	 * This client property maps to a Boolean indicating whether this UI should
	 * animate transitions between different progress bar values. For example,
	 * if you jump from a progress bar value of 5 to 15, if this is true then
	 * the UI will animate in-between values. If undefined this is assumed to be
	 * true.
	 */
	public static final String PROPERTY_TRANSITION = CircularProgressBarUI.class
			.getName() + "#transition";

	/**
	 * This client property maps to a PainterThrobber that is used to paint when
	 * the JProgressBar is set to indeterminate mode.
	 */
	public static final String PROPERTY_THROBBER_PAINTER = CircularProgressBarUI.class
			.getName() + "#throbberPainter";
	/**
	 * This client property maps to a Number indicating the stroke width the
	 * arcs should use. By default this UI calculates a "reasonable" stroke
	 * width based on the radius of the circle, but this property lets you
	 * override that default.
	 */
	public static final String PROPERTY_STROKE_WIDTH = CircularProgressBarUI.class
			.getName() + "#strokeWidth";

	private static final String PROPERTY_LAST_RENDERED_VALUE = CircularProgressBarUI.class
			.getName() + "#lastRenderedValue";
	private static final String PROPERTY_SPARK_ANGLE = CircularProgressBarUI.class
			.getName() + "#sparkPosition";
	private static final String PROPERTY_STROKE_MULTIPLIER = CircularProgressBarUI.class
			.getName() + "#strokeMultiplier";

	/**
	 * The degrees the spark spans.
	 */
	private static final int SPARK_EXTENT = 20;

	private static final ThrobberPainter DEFAULT_THROBBER_PAINTER = new CircularThrobberPainter();

	/**
	 * Create an AffineTransform that flips everything horizontally around a
	 * given x-value.
	 */
	private static AffineTransform flipHorizontal(double x) {
		AffineTransform tx = new AffineTransform();
		tx.translate(x, 0);
		tx.scale(-1, 1);
		tx.translate(-x, 0);
		return tx;
	}

	/**
	 * Paint a String centered at a given (x,y) coordinate.
	 */
	private static void paintCenteredString(Graphics2D g, String str, Font font,
										   int centerX, int centerY) {
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		Rectangle2D r = fm.getStringBounds(str, g);
		float x = (float) (centerX - r.getWidth() / 2);
		float y = (float) (centerY - r.getHeight() / 2 - r.getY());
		g.drawString(str, x, y);
	}

	private static boolean isAqua() {
		return "Aqua".equals(UIManager.getLookAndFeel().getID());
	}

	private static Color getDefaultForegroundColor() {
		// weird: why is ProgressBar.foreground in Aqua black? That's no good.
		String propertyName = isAqua() ? "controlHighlight"
				: "ProgressBar.foreground";
		Color c = UIManager.getColor(propertyName);
		if (c == null)
			c = new Color(0x3b5cfc);
		return c;
	}

	private static Color getDefaultBackgroundColor() {
		String propertyName = isAqua() ? "TextComponent.selectionBackgroundInactive"
				: "ProgressBar.shadow";
		Color c = UIManager.getColor(propertyName);
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
		final Timer pulseCompletionTimer = new Timer(10, new ActionListener() {

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
		final Timer sparkInitiateTimer = new Timer(2000, new ActionListener() {

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
			final Timer moveSparkTimer = new Timer(5, new ActionListener() {

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

	PropertyChangeListener repaintListener = evt -> progressBar.repaint();

	private final Runnable repaintRunnable = () -> progressBar.repaint();

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
		progressBar.addPropertyChangeListener(PROPERTY_ACCELERATE,
				repaintListener);
		progressBar.addPropertyChangeListener(PROPERTY_STROKE_WIDTH,
				repaintListener);
		progressBar.setBorder(null);
	}

	@Override
	public void uninstallUI(JComponent c) {
		progressBar.removeChangeListener(pulseChangeListener);
		progressBar.removeChangeListener(sparkChangeListener);
		progressBar.removePropertyChangeListener(PROPERTY_STROKE_MULTIPLIER,
				repaintListener);
		progressBar.removePropertyChangeListener(PROPERTY_SPARK_ANGLE,
				repaintListener);
		progressBar.removePropertyChangeListener(PROPERTY_ACCELERATE,
				repaintListener);
		progressBar.removePropertyChangeListener(PROPERTY_STROKE_WIDTH,
				repaintListener);
		super.uninstallUI(c);
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

		float strokeWidth = getStrokeWidth(diameter);

		if (progressBar.isIndeterminate()) {
			paintIndeterminate(g, radius, strokeWidth, centerX, centerY);
		} else {
			paintDeterminate(g, radius, strokeWidth, centerX, centerY);
		}
	}

	/**
	 * Paint the indeterminate state of this UI.
	 * <p>
	 * This integrates System.currentTimeMillis() into its calculations, so
	 * every invocation will be slightly different.
	 */
	protected void paintIndeterminate(Graphics2D g, int radius,
			float strokeWidth, int centerX, int centerY) {
		ThrobberPainter p = (ThrobberPainter) progressBar.getClientProperty(PROPERTY_THROBBER_PAINTER);
		if (p == null)
			p = DEFAULT_THROBBER_PAINTER;
		Rectangle r = new Rectangle(0, 0, progressBar.getWidth(), progressBar.getHeight());
		p.paint(g, r, null, progressBar.getForeground());
	}

	/**
	 * Paint the determinate state of this UI.
	 * <p>
	 * This doesn't rely on System.currentTimeMillis(), but it can invoke
	 * <code>progressBar.repaint()</code> if the transition property is active,
	 * so subsequent invocations may produce different results.
	 */
	protected void paintDeterminate(Graphics2D g, int radius,
			float strokeWidth, int centerX, int centerY) {
		Number multiplier = (Number) progressBar
				.getClientProperty(PROPERTY_STROKE_MULTIPLIER);
		if (multiplier != null)
			strokeWidth = strokeWidth * multiplier.floatValue();

		strokeWidth = Math.min(strokeWidth, radius);

		double v = progressBar.getPercentComplete();
		if (isActive(PROPERTY_TRANSITION, true)) {
			Number lastRenderedValue = (Number) progressBar
					.getClientProperty(PROPERTY_LAST_RENDERED_VALUE);
			if (lastRenderedValue != null
					&& v > lastRenderedValue.doubleValue()) {
				double oldV = v;
				if (v > lastRenderedValue.doubleValue()) {
					v = Math.min(lastRenderedValue.doubleValue() + .0025, v);
				} else if (v < lastRenderedValue.doubleValue()) {
					v = Math.max(lastRenderedValue.doubleValue() - .0025, v);
				}
				if (v != oldV) {
					SwingUtilities.invokeLater(repaintRunnable);
				}
			}
		}
		progressBar.putClientProperty(PROPERTY_LAST_RENDERED_VALUE, v);
		if (isActive(PROPERTY_ACCELERATE, false)) {
			v = Math.pow(v, 2.5);
		}
		double extent = v * 360;
		paintArc(g, progressBar.getForeground(), centerX, centerY, 0, extent,
				radius - strokeWidth / 2, strokeWidth, true);
		paintArc(g, progressBar.getBackground(), centerX, centerY, extent,
				360 - extent, radius - strokeWidth / 2, strokeWidth, true);

		Number sparkAngle = (Number) progressBar
				.getClientProperty(PROPERTY_SPARK_ANGLE);
		if (sparkAngle != null) {
			int a1 = sparkAngle.intValue();
			int a2 = sparkAngle.intValue() - SPARK_EXTENT;
			int b1 = (int) Math.max(0, Math.min(extent, a1));
			int b2 = (int) Math.max(0, Math.min(extent, a2));
			if (b2 >= 0) {
				Color sparkColor = new Color(0xddffffff, true);
				paintArc(g, sparkColor, centerX, centerY, b1, b2 - b1, radius
						- strokeWidth / 2, strokeWidth, true);
			}
		}

		if (progressBar.isStringPainted()) {
			Font font = progressBar.getFont();
			font = font.deriveFont(((float) radius) / 2f);
			paintCenteredString(g, progressBar.getString(),
					font, centerX, centerY);
		}
	}

	/**
	 * Return the stroke width.
	 * <P>
	 * This first checks to see if the user has defined a custom width, and if
	 * not the width is derived based on the diameter.
	 */
	protected float getStrokeWidth(int diameter) {
		Number n = (Number) progressBar
				.getClientProperty(PROPERTY_STROKE_WIDTH);
		float f;
		if (n == null) {
			f = ((float) diameter) / 10f + 1;
			if (!progressBar.isStringPainted())
				f *= 2;
		} else {
			f = n.floatValue();
		}
		return f;
	}

	/**
	 * Return true if a property with the given name is active.
	 * 
	 * @param defaultValue
	 *            the default state of this property if the client property is
	 *            null.
	 */
	protected boolean isActive(String propertyName, boolean defaultValue) {
		Boolean b = (Boolean) progressBar.getClientProperty(propertyName);
		if (b == null)
			b = defaultValue;
		return b;
	}

	@Override
    protected Rectangle getBox(Rectangle r) {
    	if(r==null)
    		r = new Rectangle();
    	r.x = 0;
    	r.y = 0;
    	r.width = progressBar.getWidth();
    	r.height = progressBar.getHeight();
    	return r;
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
	static void paintArc(Graphics2D g, Color color, double centerX,
			double centerY, double startAngle, double extent, double radius,
			float strokeWidth, boolean flatEdge) {
		g = (Graphics2D) g.create();
		g.transform(flipHorizontal(centerX));
		g.rotate(-Math.PI / 2, centerX, centerY);
		g.setPaint(color);
		Arc2D progressArc = new Arc2D.Double(centerX - radius,
				centerY - radius, radius * 2, radius * 2, startAngle, extent,
				Arc2D.OPEN);
		int cap = flatEdge ? BasicStroke.CAP_BUTT : BasicStroke.CAP_ROUND;
		g.setStroke(new BasicStroke(strokeWidth, cap,
				BasicStroke.JOIN_MITER, 10));
		g.draw(progressArc);
		g.dispose();
	}
}