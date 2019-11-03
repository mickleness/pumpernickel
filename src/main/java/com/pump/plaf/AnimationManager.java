package com.pump.plaf;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * This helps with animations.
 */
public class AnimationManager {

	/**
	 * Tween between two colors.
	 * 
	 * @param c1
	 * @param c2
	 * @param f
	 * @return
	 */
	public static final Color tween(Color c1, Color c2, double f) {
		if (f <= 0)
			return c1;
		if (f >= 1)
			return c2;
		int r = (int) (c1.getRed() * (1 - f) + f * c2.getRed());
		int g = (int) (c1.getGreen() * (1 - f) + f * c2.getGreen());
		int b = (int) (c1.getBlue() * (1 - f) + f * c2.getBlue());
		int a = (int) (c1.getAlpha() * (1 - f) + f * c2.getAlpha());
		return new Color(r, g, b, a);
	}

	/** Tween between the two rectangles. */
	public static Rectangle2D tween(Rectangle2D r1, Rectangle2D r2, double f) {
		if (f < 0)
			return (Rectangle2D) r1.clone();
		if (f > 1)
			return (Rectangle2D) r2.clone();
		return new Rectangle2D.Double(r1.getX() * (1 - f) + r2.getX() * f,
				r1.getY() * (1 - f) + r2.getY() * f, r1.getWidth() * (1 - f)
						+ r2.getWidth() * f, r1.getHeight() * (1 - f)
						+ r2.getHeight() * f);
	}

	private static class Ticket extends
			AbstractMap.SimpleEntry<JComponent, String> {
		private static final long serialVersionUID = 1L;

		Ticket(JComponent jc, String propertyName) {
			super(jc, propertyName);
		}

		String getTargetProperty() {
			return getValue() + "-target";
		}

		String getStepSizeProperty() {
			return getValue() + "-stepSize";
		}

		/**
		 * Return true if a change occurred; false if no change was made.
		 * 
		 * @return
		 */
		boolean increment() {
			Number currentValue = (Number) getKey().getClientProperty(
					getValue());
			Number targetValue = (Number) getKey().getClientProperty(
					getTargetProperty());

			double c1 = currentValue.doubleValue();
			double t1 = targetValue.doubleValue();

			if (c1 == t1)
				return false;

			Number stepSize = (Number) getKey().getClientProperty(
					getStepSizeProperty());
			double s1 = stepSize.doubleValue();
			if (c1 < t1) {
				c1 = Math.min(c1 + s1, t1);
			} else if (c1 > t1) {
				c1 = Math.max(c1 - s1, t1);
			}

			getKey().putClientProperty(getValue(), c1);
			return true;
		}
	}

	static Map<Ticket, AnimationManager> managerByTicketMap = new HashMap<>();
	static Map<Integer, AnimationManager> managerByIntervalMap = new HashMap<>();

	/**
	 * Set a target client property, and if necessary initiate a looping timer
	 * to incrementally change the current value to reach the target.
	 * 
	 * @param component
	 *            the component the client property belongs to. At any point you
	 *            can call component.getClientProperty(propertyName) to get the
	 *            current value.
	 * @param propertyName
	 *            the property to set.
	 * @param targetValue
	 *            the target value to eventually set. We immediately switch to
	 *            the target value if the component is not showing or no
	 *            preexisting value is found.
	 * @param stepInterval
	 *            the number of milliseconds between each animation iteration.
	 * @param stepSize
	 *            the amount to change the value by with each iteration. For
	 *            example: if you're changing the target from zero to one, you
	 *            may want to set the stepSize to something like .2 so you'll
	 *            see 5 frames of animation.
	 * 
	 * @return the current value of the property.
	 */
	public static double setTargetProperty(JComponent component,
			String propertyName, double targetValue, int stepInterval,
			double stepSize) {
		Objects.requireNonNull(component);
		Objects.requireNonNull(propertyName);

		Number currentValue;
		synchronized (AnimationManager.class) {
			currentValue = (Number) component.getClientProperty(propertyName);
			if (currentValue == null || !component.isShowing()
					|| currentValue.doubleValue() == targetValue) {
				component.putClientProperty(propertyName, targetValue);
				return targetValue;
			}

			Ticket ticket = new Ticket(component, propertyName);
			component
					.putClientProperty(ticket.getTargetProperty(), targetValue);
			component.putClientProperty(ticket.getStepSizeProperty(), stepSize);
			AnimationManager manager = managerByTicketMap.get(ticket);
			if (manager == null || manager.timerInterval != stepInterval) {
				if (manager != null)
					manager.remove(ticket);
				manager = getAnimationManager(stepInterval);
				manager.add(ticket);
			} else {
				// there's already an active AnimationManager working on this
				// transition, so there's nothing we need to do here.
			}
		}

		return currentValue.doubleValue();
	}

	static AnimationManager getAnimationManager(int stepInterval) {
		synchronized (AnimationManager.class) {
			AnimationManager m = managerByIntervalMap.get(stepInterval);
			if (m == null) {
				m = new AnimationManager(stepInterval);
				managerByIntervalMap.put(stepInterval, m);
			}
			return m;
		}
	}

	int timerInterval;
	Timer timer;
	Collection<Ticket> tickets = new HashSet<>();
	ActionListener actionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (AnimationManager.class) {
				for (Ticket ticket : getTickets()) {
					if (!ticket.increment()) {
						remove(ticket);
					}
					ticket.getKey().repaint();
				}
			}
		}

	};

	/**
	 * Use the static
	 * {@link #setTargetProperty(JComponent, String, double, int, double)}
	 * method to initiate AnimationManagers.
	 */
	private AnimationManager(int timerInterval) {
		this.timerInterval = timerInterval;
		timer = new Timer(timerInterval, actionListener);
	}

	void remove(Ticket ticket) {
		synchronized (AnimationManager.class) {
			tickets.remove(ticket);
			managerByTicketMap.remove(ticket);
			if (tickets.size() == 0)
				timer.stop();
		}
	}

	Ticket[] getTickets() {
		synchronized (AnimationManager.class) {
			return tickets.toArray(new Ticket[tickets.size()]);
		}
	}

	void add(Ticket ticket) {
		synchronized (AnimationManager.class) {
			tickets.add(ticket);
			managerByTicketMap.put(ticket, this);
			if (!timer.isRunning())
				timer.start();
		}
	}
}
