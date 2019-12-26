package com.pump.plaf.button;

import java.util.Objects;

import javax.swing.ButtonModel;

/**
 * This is an immutable snapshot of a ButtonModel's state.
 */
public class ButtonState {
	public static class Boolean extends ButtonState {
		boolean isArmed, isEnabled, isPressed, isRollover, isSelected;

		/**
		 * Create a ButtonState.
		 */
		public Boolean(boolean enabled, boolean selected, boolean pressed,
				boolean armed, boolean rollover) {
			isArmed = armed;
			isEnabled = enabled;
			isPressed = pressed;
			isRollover = rollover;
			isSelected = selected;
		}

		/**
		 * Create a ButtonState that captures a ButtonModel's current state.
		 */
		public Boolean(ButtonModel model) {
			isArmed = model.isArmed();
			isEnabled = model.isEnabled();
			isPressed = model.isPressed();
			isRollover = model.isRollover();
			isSelected = model.isSelected();
		}

		@Override
		public int hashCode() {
			return Objects.hash(isArmed(), isEnabled(), isPressed(),
					isRollover(), isSelected());
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ButtonState.Boolean))
				return false;
			ButtonState.Boolean other = (ButtonState.Boolean) obj;
			if (isArmed() != other.isArmed())
				return false;
			if (isEnabled() != other.isEnabled())
				return false;
			if (isPressed() != other.isPressed())
				return false;
			if (isRollover() != other.isRollover())
				return false;
			if (isSelected() != other.isSelected())
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "ButtonState[isArmed=" + isArmed() + ", isEnabled="
					+ isEnabled() + ", isPressed=" + isPressed()
					+ ", isRollover=" + isRollover() + ", isSelected="
					+ isSelected() + "]";
		}

		public boolean isArmed() {
			return isArmed;
		}

		public boolean isEnabled() {
			return isEnabled;
		}

		public boolean isPressed() {
			return isPressed;
		}

		public boolean isRollover() {
			return isRollover;
		}

		public boolean isSelected() {
			return isSelected;
		}
	}

	public static class Float extends ButtonState {
		float isArmed, isEnabled, isPressed, isRollover, isSelected;

		/**
		 * Create a ButtonState.
		 */
		public Float(float enabled, float selected, float pressed, float armed,
				float rollover) {
			isArmed = armed;
			isEnabled = enabled;
			isPressed = pressed;
			isRollover = rollover;
			isSelected = selected;
		}

		/**
		 * Create a ButtonState that captures a ButtonModel's current state.
		 */
		public Float(ButtonModel model) {
			isArmed = model.isArmed() ? 1 : 0;
			isEnabled = model.isEnabled() ? 1 : 0;
			isPressed = model.isPressed() ? 1 : 0;
			isRollover = model.isRollover() ? 1 : 0;
			isSelected = model.isSelected() ? 1 : 0;
		}

		public Float(Boolean b) {
			isArmed = b.isArmed() ? 1 : 0;
			isEnabled = b.isEnabled() ? 1 : 0;
			isPressed = b.isPressed() ? 1 : 0;
			isRollover = b.isRollover() ? 1 : 0;
			isSelected = b.isSelected() ? 1 : 0;
		}

		@Override
		public int hashCode() {
			return Objects.hash(isArmed(), isEnabled(), isPressed(),
					isRollover(), isSelected());
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ButtonState.Boolean))
				return false;
			ButtonState.Float other = (ButtonState.Float) obj;
			if (isArmed() != other.isArmed())
				return false;
			if (isEnabled() != other.isEnabled())
				return false;
			if (isPressed() != other.isPressed())
				return false;
			if (isRollover() != other.isRollover())
				return false;
			if (isSelected() != other.isSelected())
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "ButtonState[isArmed=" + isArmed() + ", isEnabled="
					+ isEnabled() + ", isPressed=" + isPressed()
					+ ", isRollover=" + isRollover() + ", isSelected="
					+ isSelected() + "]";
		}

		public float isArmed() {
			return isArmed;
		}

		public float isEnabled() {
			return isEnabled;
		}

		public float isPressed() {
			return isPressed;
		}

		public float isRollover() {
			return isRollover;
		}

		public float isSelected() {
			return isSelected;
		}
	}
}
