package com.pump.icon.button;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.AbstractButton;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.plaf.PlafPaintUtils;

/**
 * This connects an AbstractButton with a ButtonIcon and manages the required
 * listeners and animations.
 */
public class ButtonIconManager {

	/** The duration of animations in milliseconds. */
	long animationDuration = 100;

	/** The timestamp of when the last animation started. */
	long animationStart;

	/**
	 * When this is not null there is an animation in progress. We nullify this
	 * when we conclude an animation.
	 */
	Map<String, Color> animationStartColors;
	Map<String, Color> animationEndColors;

	ActionListener actionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			Map<String, Color> current = getCurrentColors();
			display(current);
			if (current.equals(animationEndColors)) {
				animationStartColors = null;
				animationStart = -1;
				timer.stop();
			}
		}

	};
	Timer timer = new Timer(10, actionListener);

	FocusListener buttonFocusListener = new FocusListener() {

		@Override
		public void focusGained(FocusEvent e) {
			buttonChangeListener.stateChanged(null);
		}

		@Override
		public void focusLost(FocusEvent e) {
			buttonChangeListener.stateChanged(null);
		}

	};

	ChangeListener buttonChangeListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			ButtonState state = new ButtonState(button);
			Map<String, Color> colorMap = colors.getColors(state);

			if (animationEndColors == null) {
				animationEndColors = colorMap;
				display(animationEndColors);
				return;
			}

			// If we're still aiming for the same set of colors, don't change
			// anything. Either an animation is in progress, we're already
			// rendering the correct finished product.
			if (colorMap.equals(animationEndColors)) {
				return;
			}

			animationStartColors = getCurrentColors();
			animationEndColors = colorMap;
			animationStart = System.currentTimeMillis();
			display(animationStartColors);
			timer.restart();
		}

	};

	AbstractButton button;
	ButtonIconColors colors;
	ButtonIcon icon;

	/**
	 * 
	 * @param button
	 *            the button to listen to (for rollover, focus changes, enabled
	 *            changes, etc.), and to assign the icon of.
	 * @param icon
	 *            the ButtonIcon to use. This icon is assigned to the button,
	 *            and this manager constantly calls
	 *            {@link ButtonIcon#setColors(Map)} and repaints the button as
	 *            needed.
	 * @param colors
	 *            this translates button state information into colors.
	 */
	public ButtonIconManager(AbstractButton button, ButtonIcon icon,
			ButtonIconColors colors) {
		Objects.requireNonNull(button);
		Objects.requireNonNull(icon);
		Objects.requireNonNull(colors);
		this.button = button;
		this.colors = colors;
		this.icon = icon;
		button.setIcon(icon);
		button.addPropertyChangeListener(AbstractButton.ICON_CHANGED_PROPERTY,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						// uninstall: we are now obsolete
						ButtonIconManager.this.button
								.removePropertyChangeListener(
										AbstractButton.ICON_CHANGED_PROPERTY,
										this);
						ButtonIconManager.this.button
								.removeChangeListener(buttonChangeListener);
						ButtonIconManager.this.button
								.removeFocusListener(buttonFocusListener);
					}

				});
		button.setRolloverEnabled(true);
		button.addFocusListener(buttonFocusListener);
		button.addChangeListener(buttonChangeListener);
		buttonChangeListener.stateChanged(null);
	}

	private Map<String, Color> getCurrentColors() {
		long time = System.currentTimeMillis();
		long elapsed = time - animationStart;
		float f = ((float) elapsed) / ((float) animationDuration);
		if (f > 1)
			f = 1;

		return tweenColors(animationStartColors, animationEndColors, f);
	}

	private Map<String, Color> tweenColors(
			Map<String, Color> animationStartColors,
			Map<String, Color> animationEndColors, float f) {
		if (f <= 0)
			return animationStartColors;
		if (f >= 1)
			return animationEndColors;

		Map<String, Color> returnValue = new HashMap<>();
		for (String key : animationStartColors.keySet()) {
			Color c1 = animationStartColors.get(key);
			Color c2 = animationEndColors.get(key);
			Color c = PlafPaintUtils.tween(c1, c2, f);
			returnValue.put(key, c);
		}
		return returnValue;
	}

	private void display(Map<String, Color> current) {
		icon.setColors(current);
		button.repaint();
	}
}
