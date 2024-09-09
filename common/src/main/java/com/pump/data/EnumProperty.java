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
package com.pump.data;

public class EnumProperty<T> extends Property<T> {
	T[] values;

	public EnumProperty(String name, T[] values, T defaultValue) {
		super(name, defaultValue);
		this.values = values;
		for (T t : values) {
			if (t == null)
				throw new NullPointerException(
						"none of the enum values may be null");
		}
		if (values.length == 0)
			throw new IllegalArgumentException("there were no values");
		validateValue(getValue());
	}

	public T[] getValues() {
		return values;
	}

	@Override
	protected void validateValue(Object obj) {

		if (values == null) {
			// this will happen high up in the constructor
			return;
		}

		if (obj == null)
			throw new NullPointerException("the value must not be null");
		for (T t : values) {
			if (t.equals(obj))
				return;
		}
		throw new IllegalArgumentException("the value (\"" + obj
				+ "\") did not match any of the allowed values (" + listValues()
				+ ")");
	}

	protected String listValues() {
		if (values.length == 1)
			return values[0].toString();

		StringBuilder sb = new StringBuilder();
		for (int a = 0; a < values.length; a++) {
			if (a > 0) {
				if (a == values.length - 1) {
					sb.append(" and ");
				} else {
					sb.append(", ");
				}
			}
			sb.append(values[a]);
		}
		return sb.toString();
	}
}