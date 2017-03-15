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
package com.pump.data;

import junit.framework.TestCase;

import org.junit.Test;

import com.pump.data.Scrambler.MarkerRule;

public class MarkerRuleTest extends TestCase {
	@Test
	public void testOneCount() {
		assertEquals( 0, MarkerRule.OneCount.getOneCount( Integer.parseInt("00000000", 2)));
		
		assertEquals( 1, MarkerRule.OneCount.getOneCount( Integer.parseInt("00000001", 2)));
		assertEquals( 1, MarkerRule.OneCount.getOneCount( Integer.parseInt("00000010", 2)));
		assertEquals( 1, MarkerRule.OneCount.getOneCount( Integer.parseInt("00000100", 2)));
		assertEquals( 1, MarkerRule.OneCount.getOneCount( Integer.parseInt("00001000", 2)));
		assertEquals( 1, MarkerRule.OneCount.getOneCount( Integer.parseInt("00010000", 2)));
		assertEquals( 1, MarkerRule.OneCount.getOneCount( Integer.parseInt("00100000", 2)));
		assertEquals( 1, MarkerRule.OneCount.getOneCount( Integer.parseInt("01000000", 2)));
		assertEquals( 1, MarkerRule.OneCount.getOneCount( Integer.parseInt("10000000", 2)));

		assertEquals( 2, MarkerRule.OneCount.getOneCount( Integer.parseInt("00000011", 2)));
		assertEquals( 2, MarkerRule.OneCount.getOneCount( Integer.parseInt("00001010", 2)));
		assertEquals( 2, MarkerRule.OneCount.getOneCount( Integer.parseInt("00000101", 2)));
		assertEquals( 2, MarkerRule.OneCount.getOneCount( Integer.parseInt("10001000", 2)));
		assertEquals( 2, MarkerRule.OneCount.getOneCount( Integer.parseInt("01010000", 2)));
		assertEquals( 2, MarkerRule.OneCount.getOneCount( Integer.parseInt("00100010", 2)));
		assertEquals( 2, MarkerRule.OneCount.getOneCount( Integer.parseInt("01000100", 2)));
		assertEquals( 2, MarkerRule.OneCount.getOneCount( Integer.parseInt("10010000", 2)));
	
		assertEquals( 8, MarkerRule.OneCount.getOneCount( Integer.parseInt("11111111", 2)));
	}

	@Test
	public void testBadOneCount() {
		try {
			assertEquals( 0, MarkerRule.OneCount.getOneCount( -1 ));
			fail();
		} catch(Exception e) {
			//pass!
		}

		try {
			assertEquals( 0, MarkerRule.OneCount.getOneCount( 256 ));
			fail();
		} catch(Exception e) {
			//pass!
		}
	}
}