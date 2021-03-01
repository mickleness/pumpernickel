package com.pump.text.html.css.border;

import java.util.Objects;

import com.pump.text.html.css.AbstractCssValue;

public class CssBorderStyleValue extends AbstractCssValue {

	public enum Value {
		NONE, HIDDEN, DOTTED, DASHED, SOLID, DOUBLE, GROOVE, RIDGE, INSET, OUTSET;

		/**
		 * Return true for everything except NONE or HIDDEN.
		 */
		public boolean isVisible() {
			return !(this == NONE || this == HIDDEN);
		}
	}

	private final String cssString;
	private final Value value;

	public CssBorderStyleValue(String cssString) {
		this(Value.valueOf(cssString.toUpperCase()));
	}

	public CssBorderStyleValue(Value value) {
		this(value.name().toLowerCase(), value);
	}

	public CssBorderStyleValue(String cssString, Value value) {
		Objects.requireNonNull(cssString);
		Objects.requireNonNull(value);
		this.cssString = cssString;
		this.value = value;
	}

	public Value getValue() {
		return value;
	}

	@Override
	public String toString() {
		return toCSSString();
	}

	@Override
	public String toCSSString() {
		return cssString;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CssBorderStyleValue))
			return false;
		CssBorderStyleValue other = (CssBorderStyleValue) obj;
		return other.value.equals(value)
				&& Objects.equals(cssString, other.cssString);
	}
}
