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
package com.pump.text.html.css;

import java.awt.Color;
import java.util.List;

import org.junit.Test;

import com.pump.image.shadow.ShadowAttributes;

import junit.framework.TestCase;

public class CssTextShadowParserTest extends TestCase {

	/**
	 * This tests a particular bug: The color can come at the beginning or end
	 * of the shadow description. But since our color parser can identify "0" as
	 * a color, then the shadow description "0 0 25px #0000cc" was read
	 * incorrectly. (We read the first "0" as a color, which meant the remaining
	 * terms "0 25px #0000cc" could not be converted to a ShadowAttributes.)
	 */
	@Test
	public void testColorOrder_1() {
		String css = "0 0 25px #0000CC";
		CssTextShadowParser p = new CssTextShadowParser();
		List<ShadowAttributes> attrs = p.parse(css);
		assertEquals(1, attrs.size());
		assertEquals(new Color(0x0000cc), attrs.get(0).getShadowColor());
		assertEquals(0f, attrs.get(0).getShadowXOffset());
		assertEquals(0f, attrs.get(0).getShadowYOffset());
		assertEquals(25f, attrs.get(0).getShadowKernelRadius());
	}

	@Test
	public void testColorOrder_2() {
		String css = "#0000CC 0 0 25px";
		CssTextShadowParser p = new CssTextShadowParser();
		List<ShadowAttributes> attrs = p.parse(css);
		assertEquals(1, attrs.size());
		assertEquals(new Color(0x0000cc), attrs.get(0).getShadowColor());
		assertEquals(0f, attrs.get(0).getShadowXOffset());
		assertEquals(0f, attrs.get(0).getShadowYOffset());
		assertEquals(25f, attrs.get(0).getShadowKernelRadius());
	}
}