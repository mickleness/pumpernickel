package com.pump.icon.button;

import java.io.Serializable;

import javax.swing.AbstractButton;

/**
 * This provides a snapshot of a button's state (is it pressed, enabled,
 * selected, focused, etc.). This should capture all the information that will
 * be used by a {@link ButtonIconColors} object to assign colors to render a
 * {@link ButtonIcon}.
 */
public class ButtonState implements Serializable {
	private static final long serialVersionUID = 1L;

	final boolean armed, enabled, focusOwner, pressed, rollover, selected;

	public ButtonState(AbstractButton button) {
		this(button.getModel().isArmed(), button.getModel().isEnabled(), button
				.isFocusOwner(), button.getModel().isPressed(), button
				.getModel().isRollover(), button.getModel().isSelected());
	}

	public ButtonState(boolean armed, boolean enabled, boolean focusOwner,
			boolean pressed, boolean rollover, boolean selected) {
		this.armed = armed;
		this.enabled = enabled;
		this.focusOwner = focusOwner;
		this.pressed = pressed;
		this.rollover = rollover;
		this.selected = selected;
	}

	@Override
	public int hashCode() {
		int k = 0;
		if (isArmed())
			k += 1;
		if (isEnabled())
			k += 2;
		if (isFocusOwner())
			k += 4;
		if (isPressed())
			k += 8;
		if (isRollover())
			k += 16;
		if (isSelected())
			k += 32;
		return k;
	}

	public boolean isArmed() {
		return armed;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isFocusOwner() {
		return focusOwner;
	}

	public boolean isPressed() {
		return pressed;
	}

	public boolean isRollover() {
		return rollover;
	}

	public boolean isSelected() {
		return selected;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ButtonState))
			return false;
		ButtonState other = (ButtonState) obj;
		return hashCode() == other.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ButtonState[ ");
		if (isArmed())
			sb.append("armed ");
		if (isEnabled())
			sb.append("enabled ");
		if (isFocusOwner())
			sb.append("focusOwner ");
		if (isPressed())
			sb.append("pressed ");
		if (isRollover())
			sb.append("rollover ");
		if (isSelected())
			sb.append("selected ");
		sb.append("]");
		return sb.toString();
	}

}
