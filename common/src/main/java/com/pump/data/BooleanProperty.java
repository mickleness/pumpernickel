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

public class BooleanProperty extends Property<Boolean> {

	public BooleanProperty(String propertyName) {
		this(propertyName, false);
	}

	public BooleanProperty(String propertyName, boolean defaultValue) {
		super(propertyName, defaultValue);
	}
}