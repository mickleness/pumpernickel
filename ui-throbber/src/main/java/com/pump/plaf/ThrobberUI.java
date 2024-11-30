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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.plaf.ComponentUI;

import com.pump.swing.JThrobber;

/**
 * The ComponentUI for {@link com.pump.swing.JThrobber}.
 */
public class ThrobberUI extends ComponentUI {

	/**
	 * The ThrobberUI may be applied to multiple JThrobbers, but the
	 * ThrobberData is associated with a unique JThrobber/ThrobberUI pair.
	 */
	protected static class ThrobberData {
		JThrobber throbber;
		Color originalForeground = null;
		Timer timer;
		HierarchyListener hierarchyListener = new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				reevaluateTimer();
			}

		};

		ActionListener repaintActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				throbber.repaint();
				reevaluateTimer();
			}
		};

		PropertyChangeListener activeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				reevaluateTimer();
			}
		};

		public ThrobberData(JThrobber throbber) {
			Objects.requireNonNull(throbber);
			this.throbber = throbber;
		}

		protected void install() {
			Color foreground = throbber.getUI().painter.getPreferredForeground();
			if (foreground != null) {
				originalForeground = throbber.getForeground();
				throbber.setForeground(foreground);
			}
			throbber.addPropertyChangeListener(JThrobber.KEY_ACTIVE,
					activeListener);
			timer = new Timer(25,
					repaintActionListener);
			reevaluateTimer();
			throbber.addHierarchyListener(hierarchyListener);
		}

		protected void uninstall() {
			throbber.removePropertyChangeListener(JThrobber.KEY_ACTIVE,
					activeListener);
			throbber.removeHierarchyListener(hierarchyListener);
			timer.stop();
			timer = null;

			if (originalForeground != null)
				throbber.setForeground(originalForeground);
		}

		protected void reevaluateTimer() {
			if (timer != null) {
				if (throbber.isActive() && throbber.isShowing()) {
					if (!timer.isRunning())
						timer.start();
				} else {
					if (timer.isRunning())
						timer.stop();
				}
			}
		}
	}

	protected static String PROPERTY_THROBBER_DATA = ThrobberUI.class.getName()
			+ "#data";

	/**
	 * An optional client property to define the period of this animation (if
	 * the animation has a period).
	 */
	public static final String PERIOD_KEY = ThrobberUI.class.getName()
			+ ".period";

	/**
	 * An optional client property to slow the default period by a fixed
	 * interval.
	 */
	public static final String PERIOD_MULTIPLIER_KEY = ThrobberUI.class
			.getName() + ".period-multiplier";

	public static ComponentUI createUI(JComponent c) {
		return new ThrobberUI(new AquaThrobberPainter());
	}

	protected ThrobberPainter painter;

	public ThrobberUI(ThrobberPainter painter) {
		setPainter(painter);
	}

	public void setPainter(ThrobberPainter painter) {
		this.painter = Objects.requireNonNull(painter);
	}

	/**
	 * Return the period (in milliseconds) of this throbber.
	 * <p>
	 * This is a convenience method that takes the argument defaultPeriod and
	 * modifies it according to {@link #PERIOD_KEY} or
	 * {@link #PERIOD_MULTIPLIER_KEY}
	 * 
	 * @param jc
	 *            the component to inspect. If null then the default period is
	 *            immediately returned.
	 * @param defaultPeriod
	 *            the value to return if no customizations are defined..
	 */
	public int getPeriod(JComponent jc, int defaultPeriod) {
		if (jc != null) {
			Number n = (Number) jc.getClientProperty(PERIOD_KEY);
			if (n != null)
				return n.intValue();
			n = (Number) jc.getClientProperty(PERIOD_MULTIPLIER_KEY);
			if (n != null) {
				return (int) (defaultPeriod * n.floatValue() + .5);
			}
		}
		return defaultPeriod;
	}

	@Override
	public void paint(Graphics g, JComponent jc) {
		paint(g, jc, null);
	}

	/**
	 * 
	 * @param g0
	 * @param jc
	 *            the component may be null
	 * @param fixedFraction
	 *            an optional fixed fraction from [0, 1] representing the state
	 *            of this animation. If null: then this should be derived from
	 *            the current time.
	 */
	protected void paint(Graphics g0, JComponent jc, Float fixedFraction) {
		Graphics2D g = (Graphics2D) g0.create();
		try {
			paintBackground(g, jc);

			if (jc != null && !((JThrobber) jc).isActive())
				return;

			Color foregroundColor = jc.getForeground();

			float f;
			if (fixedFraction != null) {
				f = fixedFraction;
			} else {
				int p = getPeriod(jc, painter.getPreferredPeriod());
				f = System.currentTimeMillis() % p;
				f = f / ((float) p);
			}

			Dimension d = jc.getSize();
			painter.paint(g, f, Math.min(d.width, d.height), foregroundColor);
		} finally {
			g.dispose();
		}
	}

	/** Paint the background color, if the argument is opaque. */
	protected void paintBackground(Graphics2D g, JComponent jc) {
		if (jc != null && jc.isOpaque()) {
			g.setColor(jc.getBackground());
			g.fillRect(0, 0, jc.getWidth(), jc.getHeight());
		}
	}

	@Override
	public void installUI(final JComponent c) {
		super.installUI(c);
		ThrobberData data = getThrobberData((JThrobber) c);
		data.install();
	}

	protected ThrobberData getThrobberData(JThrobber throbber) {
		ThrobberData data = (ThrobberData) throbber
				.getClientProperty(PROPERTY_THROBBER_DATA);
		if (data == null) {
			data = new ThrobberData(throbber);
			throbber.putClientProperty(PROPERTY_THROBBER_DATA, data);
		}
		return data;
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return painter.getPreferredSize();
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		ThrobberData data = getThrobberData((JThrobber) c);
		data.uninstall();
		c.putClientProperty(PROPERTY_THROBBER_DATA, null);
	}
}