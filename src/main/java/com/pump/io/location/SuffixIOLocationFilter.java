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
package com.pump.io.location;

public class SuffixIOLocationFilter extends IOLocationFilter {
	String[] suffixes;
	boolean acceptsDirectories;
	boolean acceptsHidden;
	boolean acceptsAliases;

	public SuffixIOLocationFilter(boolean acceptsDirectories,
			boolean acceptsHidden, boolean acceptsAliases, String... suffixes) {
		this.acceptsDirectories = acceptsDirectories;
		this.acceptsHidden = acceptsHidden;
		this.acceptsAliases = acceptsAliases;
		this.suffixes = new String[suffixes.length];
		for (int a = 0; a < suffixes.length; a++) {
			if (suffixes[a].startsWith(".")) {
				this.suffixes[a] = suffixes[a].toLowerCase();
			} else {
				this.suffixes[a] = "." + suffixes[a].toLowerCase();
			}
		}
	}

	@Override
	public IOLocation filter(IOLocation loc) {
		if (loc.isHidden() && (!acceptsHidden))
			return null;
		if (loc.isAlias() && (!acceptsAliases))
			return null;
		if (loc.isDirectory() && loc.isNavigable()) {
			if (acceptsDirectories)
				return loc;
			return null;
		}
		String name = loc.getName();
		if (suffixMatches(name))
			return loc;
		return null;
	}

	/**
	 * Return true if the path argument should be accepted by this suffix
	 * filter.
	 * 
	 */
	public boolean suffixMatches(String path) {
		path = path.toLowerCase();
		for (String suffix : suffixes) {
			if (path.endsWith(suffix))
				return true;
		}
		return false;
	}

}