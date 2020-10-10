package com.pump.text.html.css;

import java.util.Objects;

/**
 * A value (a float) and a unit (String). The unit may be an empty string, but
 * it will not be empty.
 */
public class CssLength implements CssValue {
	private final String cssString;

	private float value;
	private String unit;

	/**
	 * Create a new CssLength
	 * 
	 * @param cssString
	 *            a String like "5px" or "5" or "50%"
	 */
	public CssLength(String cssString) {
		Objects.requireNonNull(cssString);
		this.cssString = cssString;

		StringBuilder sb = new StringBuilder();
		for (int a = cssString.length() - 1; a >= 0; a--) {
			char ch = cssString.charAt(a);
			if (Character.isLetter(ch) || ch == '%') {
				sb.insert(0, ch);
			} else {
				break;
			}
		}
		unit = sb.toString();
		value = Float.parseFloat(
				cssString.substring(0, cssString.length() - sb.length()));
	}

	/**
	 * Create a new CssLength.
	 * 
	 * @param value
	 *            the value of this length.
	 * @param unit
	 *            if this is null then this object will record an empty string
	 *            as the unit.
	 */
	public CssLength(float value, String unit) {
		cssString = value + unit;
		this.value = value;
		this.unit = unit == null ? "" : unit;
	}

	/**
	 * Create a new CssLength.
	 * 
	 * @param cssString
	 *            a String like "5px" or "5" or "50%"
	 * @param value
	 *            the value of this length.
	 * @param unit
	 *            if this is null then this object will record an empty string
	 *            as the unit.
	 */
	public CssLength(String cssString, float value, String unit) {
		Objects.requireNonNull(cssString);
		this.cssString = cssString;
		this.value = value;
		this.unit = unit == null ? "" : unit;

	}

	@Override
	public CssLength clone() {
		return new CssLength(cssString);
	}

	@Override
	public String toString() {
		return "CssLength[ " + toCSSString() + " ]";
	}

	@Override
	public String toCSSString() {
		return cssString;
	}

	public String getUnit() {
		return unit;
	}

	public float getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Float.hashCode(value) + unit.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CssLength))
			return false;
		CssLength other = (CssLength) obj;
		if (value != other.value)
			return false;
		if (!unit.equals(other.unit))
			return false;
		return true;
	}
}