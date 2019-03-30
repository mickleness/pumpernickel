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
package com.pump.io.location;

public class SearchParameters {
	final String input;

	/**
	 * Create a new SearchParameters object.
	 * 
	 * @param input
	 *            the search phrase the user submitted.
	 */
	public SearchParameters(String input) {
		this.input = input.toLowerCase();
	}

	/** Return the original search phrase the user submitted. */
	public String getInput() {
		return input;
	}

	/**
	 * Checks to see if a location matches the search criteria.
	 * 
	 * @param location
	 *            the location to check the relevance of.
	 * @return zero or less for locations that do not match the search
	 *         parameters. Otherwise the higher the value the more relevant the
	 *         result.
	 */
	public float getRelevance(IOLocation location) {
		String name = location.getName();
		// TODO: make this more clever.
		// This should support quotes, AND/OR operators, for example.
		if (name.toLowerCase().indexOf(input) != -1) {

			/**
			 * For now relevance is associated with the inverse of the number of
			 * parents. So if a hit is found that's 1 folder deep, and another
			 * is found that is 20 folders deep: the closer one is more
			 * relevant.
			 */
			float parentCount = getParentCount(location);
			return 1f / (parentCount + 1);
		}
		return 0;
	}

	private static int getParentCount(IOLocation loc) {
		return loc.getTreePath(true).getPathCount() - 1;
	}
}