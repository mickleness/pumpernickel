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
package com.pump.text.html.css.border;

import java.util.List;

import org.junit.Test;

import com.pump.text.html.css.CssColorValue;

import junit.framework.TestCase;

public class CssBorderColorParserTest extends TestCase {
	@Test
	public void testFourArguments() {
		CssBorderColorParser parser = new CssBorderColorParser();
		List<CssColorValue> colors = parser.parse("red orange yellow green");
		assertEquals("red", colors.get(0).toCSSString());
		assertEquals("orange", colors.get(1).toCSSString());
		assertEquals("yellow", colors.get(2).toCSSString());
		assertEquals("green", colors.get(3).toCSSString());
	}

	@Test
	public void testOneArgument() {
		CssBorderColorParser parser = new CssBorderColorParser();
		List<CssColorValue> colors = parser.parse("blue");
		assertEquals("blue", colors.get(0).toCSSString());
	}
}