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
package com.pump.util;

import org.junit.Test;

import junit.framework.TestCase;

public class FloatPropertyTest extends TestCase {

	@Test
	public void testConstructor() {
		FloatProperty p1 = new FloatProperty("p1", 0f, 1f, 0f);
		FloatProperty p2 = new FloatProperty("p2", 0f, 1f, .5f);
		FloatProperty p3 = new FloatProperty("p3", 0f, 1f, 1f);

		FloatProperty p4 = new FloatProperty("p4", -1f, 0f, -1f);
		FloatProperty p5 = new FloatProperty("p5", -1f, 0f, .5f);
		FloatProperty p6 = new FloatProperty("p6", -1f, 0f, 0f);

		FloatProperty p7 = new FloatProperty("p7", 1f, 10f, 1f);
		FloatProperty p8 = new FloatProperty("p8", 1f, 10f, 5f);
		FloatProperty p9 = new FloatProperty("p9", 1f, 10f, 10f);

		FloatProperty p10 = new FloatProperty("p10", -10f, -1f, -1f);
		FloatProperty p11 = new FloatProperty("p11", -10f, -1f, -5f);
		FloatProperty p12 = new FloatProperty("p12", -10f, -1f, -10f);
	}
}