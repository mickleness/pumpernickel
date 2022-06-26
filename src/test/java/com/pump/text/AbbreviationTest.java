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
package com.pump.text;

import junit.framework.TestCase;

public class AbbreviationTest extends TestCase {
	
	public void testAbbreviations() {
		assertTrue( Abbreviation.isAbbreviation(null, "agri department", "agriculture dept", 5));
		
		// I used to live on this street!
		assertTrue( Abbreviation.isAbbreviation(null, "S D Msn Rd", "San Diego Mission Road", 5) );

		/** It'd be nice if this returned true, but for now "SD" is considered one word */
		assertFalse( Abbreviation.isAbbreviation(null, "SD Msn Rd", "San Diego Mission Road", 5) );

		//we check for prefixes/suffixes, so these should NOT be identified as abbreviations
		assertFalse( Abbreviation.isAbbreviation(null, "invasive", "noninvasive", 3));
	}
}