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
package com.pump.text.html.css.image;

import java.util.List;

import junit.framework.TestCase;

public class CssImageParserTest extends TestCase {

	/**
	 * This is from a known failure where two adjacent positions caused a
	 * runtime exception.
	 */
	public void testGradient() {
		String css = "repeating-linear-gradient(90deg, transparent, transparent 50px, rgba(255, 127, 0, 0.25) 50px, rgba(255, 127, 0, 0.25) 56px, transparent 56px, transparent 63px, rgba(255, 127, 0, 0.25) 63px, rgba(255, 127, 0, 0.25) 69px, transparent 69px, transparent 116px, rgba(255, 206, 0, 0.25) 116px, rgba(255, 206, 0, 0.25) 166px)";
		CssImageParser p = new CssImageParser();
		List<CssImageValue> list = p.parse(css);
		assertEquals(1, list.size());
	}
}