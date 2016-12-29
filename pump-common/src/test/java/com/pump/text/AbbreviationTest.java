/*
 * @(#)AbbreviationTest.java
 *
 * $Date$
 *
 * Copyright (c) 2016 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
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
