/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.util;

public class EnumProperty<T> extends Property<T> {
	T[] values;

	public EnumProperty(String name, T[] values, T defaultValue) {
		super(name);
		this.values = values;
		for (int a = 0; a < values.length; a++) {
			if (values[a] == null)
				throw new NullPointerException(
						"none of the enum values may be null");
		}
		if (values.length == 0)
			throw new IllegalArgumentException("there were no values");
		setValue(defaultValue);
	}

	public T[] getValues() {
		return values;
	}

	@Override
	protected void validateValue(Object obj) {
		if (obj == null)
			throw new NullPointerException("the value must not be null");
		for (int a = 0; a < values.length; a++) {
			if (values[a].equals(obj))
				return;
		}
		throw new IllegalArgumentException("the value (\"" + obj
				+ "\") did not match any of the allowed values ("
				+ listValues() + ")");
	}

	protected String listValues() {
		if (values.length == 1)
			return values[0].toString();

		StringBuffer sb = new StringBuffer();
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