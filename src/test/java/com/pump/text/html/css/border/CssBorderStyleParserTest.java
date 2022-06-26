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

import junit.framework.TestCase;

/**
 * Tests related to the CssBorderStyleParser.
 */
public class CssBorderStyleParserTest extends TestCase {
	
	// I didn't set aside time for well-defined unit tests for all
	// the parsers, but I'll try to write new tests as specific
	// bugs come in.
	
	/**
	 * Test the CssBorderStyleParser's ability to parse 4 arguments.
	 */
	public void testFourArguments() {
		CssBorderStyleParser parser = new CssBorderStyleParser();
		List<CssBorderStyleValue> styles = parser
				.parse("double solid double solid");
		assertEquals(new CssBorderStyleValue("double"), styles.get(0));
		assertEquals(new CssBorderStyleValue("solid"), styles.get(1));
		assertEquals(new CssBorderStyleValue("double"), styles.get(2));
		assertEquals(new CssBorderStyleValue("solid"), styles.get(3));
	}
}