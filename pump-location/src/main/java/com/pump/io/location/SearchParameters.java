/*
 * @(#)SearchParameters.java
 *
 * $Date: 2014-03-27 03:50:51 -0400 (Thu, 27 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
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
package com.pump.io.location;


public class SearchParameters {
	final String input;
	
	/** Create a new SearchParameters object.
	 * @param input the search phrase the user submitted.
	 */
	public SearchParameters(String input) {
		this.input = input.toLowerCase();
	}
	
	/** Return the original search phrase the user submitted. */
	public String getInput() {
		return input;
	}
	
	/** Checks to see if a location matches the search criteria.
	 * 
	 * @param location the location to check the relevance of.
	 * @return zero or less for locations that do not match the search parameters.
	 * Otherwise the higher the value the more relevant the result.
	 */
	public float getRelevance(IOLocation location) {
		String name = location.getName();
		//TODO: make this more clever.
		//This should support quotes, AND/OR operators, for example.
		if( name.toLowerCase().indexOf(input)!=-1 ) {
		
			/** For now relevance is associated with the inverse of the number of parents.
			 * So if a hit is found that's 1 folder deep, and another is found that is 20 folders
			 * deep: the closer one is more relevant.
			 */
			float parentCount = getParentCount(location);
			return 1f/(parentCount+1);
		}
		return 0;
	}
	
	private static int getParentCount(IOLocation loc) {
		return loc.getTreePath(true).getPathCount()-1;
	}
}
