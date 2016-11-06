/*
 * @(#)SuffixIOLocationFilter.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
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


public class SuffixIOLocationFilter extends IOLocationFilter {
	String[] suffixes;
	boolean acceptsDirectories;
	boolean acceptsHidden;
	boolean acceptsAliases;
	
	public SuffixIOLocationFilter(boolean acceptsDirectories,boolean acceptsHidden,boolean acceptsAliases,String... suffixes) {
		this.acceptsDirectories = acceptsDirectories;
		this.acceptsHidden = acceptsHidden;
		this.acceptsAliases = acceptsAliases;
		this.suffixes = new String[ suffixes.length ];
		for(int a = 0; a<suffixes.length; a++) {
			if(suffixes[a].startsWith(".")) {
				this.suffixes[a] = suffixes[a].toLowerCase();
			} else {
				this.suffixes[a] = "."+suffixes[a].toLowerCase();
			}
		}
	}

	@Override
	public IOLocation filter(IOLocation loc) {
		if(loc.isHidden() && (!acceptsHidden))
			return null;
		if(loc.isAlias() && (!acceptsAliases))
			return null;
		if(loc.isDirectory() && loc.isNavigable()) {
			if(acceptsDirectories)
				return loc;
			return null;
		}
		String name = loc.getName();
		if(suffixMatches(name))
			return loc;
		return null;
	}
	
	/** Return true if the path argument should be accepted
	 * by this suffix filter.
	 * 
	 */
	public boolean suffixMatches(String path) {
		path = path.toLowerCase();
		for(String suffix : suffixes) {
			if(path.endsWith(suffix))
				return true;
		}
		return false;
	}

}
