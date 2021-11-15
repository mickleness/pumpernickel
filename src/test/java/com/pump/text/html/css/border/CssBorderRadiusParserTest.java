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

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

public class CssBorderRadiusParserTest extends TestCase {
	@Test
	public void testEightArgumentsNoSpace() {
		String str = "95% 4% 92% 5%/4% 95% 6% 95%";
		CssBorderRadiusParser p = new CssBorderRadiusParser();
		List<CssBorderRadiusValue> v = p.parse(str);
		assertEquals(4, v.size());
		assertEquals(new CssBorderRadiusValue("95% 4%"), v.get(0));
		assertEquals(new CssBorderRadiusValue("4% 95%"), v.get(1));
		assertEquals(new CssBorderRadiusValue("92% 6%"), v.get(2));
		assertEquals(new CssBorderRadiusValue("5% 95%"), v.get(3));
	}

	@Test
	public void testEightArgumentsWithSpace() {
		String str = "95% 4% 92% 5% / 4% 95% 6% 95%";
		CssBorderRadiusParser p = new CssBorderRadiusParser();
		List<CssBorderRadiusValue> v = p.parse(str);
		assertEquals(4, v.size());
		assertEquals(new CssBorderRadiusValue("95% 4%"), v.get(0));
		assertEquals(new CssBorderRadiusValue("4% 95%"), v.get(1));
		assertEquals(new CssBorderRadiusValue("92% 6%"), v.get(2));
		assertEquals(new CssBorderRadiusValue("5% 95%"), v.get(3));
	}
}