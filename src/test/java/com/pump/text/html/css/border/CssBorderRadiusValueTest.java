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
package com.pump.text.html.css.border;

import org.junit.Test;

import com.pump.text.html.css.CssLength;

import junit.framework.TestCase;

/**
 * Tests related to the CssBorderRadiusValue.
 */
public class CssBorderRadiusValueTest extends TestCase {

	@Test
	public void testTwoArguments() {
		CssBorderRadiusValue v = new CssBorderRadiusValue("50px 30%");
		assertEquals(new CssLength("50px"), v.getHorizontalValue());
		assertEquals(new CssLength("30%"), v.getVerticalValue());
	}
}