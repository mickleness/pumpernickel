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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.Timer;

import com.pump.plaf.button.ButtonState;

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

	public static class Ticket {
		JComponent component;
		String propertyName;
		Adjuster<?> adjuster;

		Ticket(JComponent component, String propertyName, Adjuster<?> adjuster) {
			this.component = component;	
			this.propertyName = propertyName;
			this.adjuster = adjuster;
		}

		public JComponent getComponent() {
			return component;
		}

		/**
		 * Return the property name used to store/retrieve the current value.
		 * This value will change during the animation.
		 */
		public String getPropertyName() {
			return propertyName;
		}

		public Adjuster<?> getAdjuster() {
			return adjuster;
		}
	}

	/**
	 * This adjusts (increments) a property until it reaches its target.
	 */
	public static abstract class Adjuster<T> {
		final long startTime, endTime, duration;
		T targetValue, initialValue;

		protected Adjuster(float duration, T targetValue) {
			startTime = System.currentTimeMillis();
			this.duration = (long) (1000 * duration);
			endTime = (long) (startTime + this.duration);
			this.targetValue = targetValue;
		}

		public void setInitialValue(T initialValue) {
			this.initialValue = initialValue;
		}

		public T getTargetValue() {
			return targetValue;
		}

		public boolean increment(Ticket ticket) {
			double elapsed = System.currentTimeMillis() - startTime;
			double fraction = elapsed / duration;
			increment(ticket, Math.min(1, fraction));
			return fraction < 1;
		}

		/**
		 * @param ticket the Ticket that includes the JComponent and client
		 * property that needs incrementing.
		 * @param fraction a value from [0, 1]
		 */
		public abstract void increment(Ticket ticket, double fraction);
	}

	/** 
	 * This animates towards a target ButtonState.Float value.
	 */
	public static class ButtonStateAdjuster extends Adjuster<ButtonState.Float> {

		public ButtonStateAdjuster(float targetTime,
				ButtonState.Float finalState) {
			super(targetTime, finalState);
		}

		@Override
		public void increment(Ticket ticket, double fraction) {
			ButtonState.Float s = new ButtonState.Float(
					(float) (initialValue.isEnabled() * (1 - fraction) + this.targetValue.isEnabled()
							* fraction), (float) (initialValue.isSelected()
							* (1 - fraction) + this.targetValue.isSelected()
							* fraction), (float) (initialValue.isPressed()
							* (1 - fraction) + this.targetValue.isPressed()
							* fraction), (float) (initialValue.isArmed()
							* (1 - fraction) + this.targetValue.isArmed()
							* fraction), (float) (initialValue.isRollover()
							* (1 - fraction) + this.targetValue.isRollover()
							* fraction));
			ticket.getComponent()
					.putClientProperty(ticket.getPropertyName(), s);
		}

	}

	/** 
	 * This animates towards a target Double value.
	 */
	public static class DoubleAdjuster extends Adjuster<Number> {

		public DoubleAdjuster(float targetTime, Number finalValue) {
			super(targetTime, finalValue);
		}

		@Override
		public void increment(Ticket ticket, double fraction) {
			double v = initialValue.doubleValue() * (1 - fraction)
					+ targetValue.doubleValue() * fraction;
			ticket.getComponent().putClientProperty(ticket.getPropertyName(),
					Double.valueOf(v));
		}

	}

	public static ButtonState.Float setTargetProperty(JComponent component,
			String propertyName, ButtonState.Float finalState, float duration) {
		return setTargetProperty(component, propertyName,
				new ButtonStateAdjuster(duration, finalState));
	}

	public static <T> T setTargetProperty(JComponent component,
			String propertyName, Adjuster<T> adjuster) {
		Objects.requireNonNull(component);
		Objects.requireNonNull(propertyName);
		Objects.requireNonNull(adjuster);

		T targetValue = adjuster.getTargetValue();
		AnimationManager animationManager = get();
		synchronized (animationManager) {
			T currentValue = (T) component.getClientProperty(propertyName);
			if (currentValue == null || currentValue.equals(targetValue)) {
				component.putClientProperty(propertyName, targetValue);
				return targetValue;
			}

			Ticket oldTicket = animationManager.getTicket(component,
					propertyName);
			if (oldTicket != null) {
				if (oldTicket.getAdjuster().targetValue
						.equals(adjuster.targetValue))
					return currentValue;

				animationManager.remove(oldTicket);
			}

			Ticket ticket = new Ticket(component, propertyName, adjuster);
			adjuster.setInitialValue(currentValue);
			animationManager.add(ticket);
			return currentValue;
		}
	}

	static AnimationManager singleton = new AnimationManager();

	public static AnimationManager get() {
		return singleton;
	}

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
	 *            the target value if no preexisting value is found.
	 * 
	 * @return the current value of the property.
	 */
	public static double setTargetProperty(JComponent component,
			String propertyName, double targetValue, float duration) {
		return setTargetProperty(component, propertyName,
				new DoubleAdjuster(duration, Double.valueOf(targetValue)))
				.doubleValue();
	}

	Timer timer;
	Map<JComponent, Map<String, Ticket>> ticketsByComponentAndProperty = new HashMap<>();
	int ticketCount = 0;
	ActionListener actionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (AnimationManager.this) {
				for (Ticket ticket : getTickets()) {
					if (!ticket.getAdjuster().increment(ticket)) {
						remove(ticket);
					}
					ticket.getComponent().repaint();
				}
			}
		}

	};

	private AnimationManager() {
		// 1000 / 25 = 40 fps
		timer = new Timer(25, actionListener);
	}

	synchronized Ticket getTicket(JComponent component, String propertyName) {
		Map<String, Ticket> ticketsByProperty = ticketsByComponentAndProperty
				.get(component);
		if (ticketsByProperty == null)
			return null;
		return ticketsByProperty.get(propertyName);
	}

	synchronized void remove(Ticket ticket) {
		Map<String, Ticket> ticketsByProperty = ticketsByComponentAndProperty
				.get(ticket.getComponent());
		if (ticketsByProperty != null) {
			if (ticketsByProperty.remove(ticket.getPropertyName()) != null) {
				ticketCount--;
				if (getTicketCount() == 0)
					timer.stop();
			}
		}
	}

	synchronized int getTicketCount() {
		return ticketCount;
	}

	synchronized List<Ticket> getTickets() {
		List<Ticket> tickets = new LinkedList<>();
		for (Map<String, Ticket> c : ticketsByComponentAndProperty.values()) {
			tickets.addAll(c.values());
		}
		return tickets;
	}

	synchronized void add(Ticket ticket) {
		Map<String, Ticket> ticketsByProperty = ticketsByComponentAndProperty
				.get(ticket.getComponent());
		if (ticketsByProperty == null) {
			ticketsByProperty = new HashMap<>();
			ticketsByComponentAndProperty.put(ticket.getComponent(),
					ticketsByProperty);
		}

		if (ticketsByProperty.put(ticket.getPropertyName(), ticket) == null) {
			ticketCount++;
			if (ticketCount == 1)
				timer.start();
		}
	}
}