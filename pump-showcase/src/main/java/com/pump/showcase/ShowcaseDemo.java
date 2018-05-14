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
package com.pump.showcase;

import java.net.URL;

public interface ShowcaseDemo {
	/**
	 * This is the title of the demo panel.
	 */
	public String getTitle();

	/**
	 * Return the optional URL of a html resource to display for additional
	 * reading.
	 */
	public URL getHelpURL();

	/**
	 * Return relevant keywords to assist in searches.
	 */
	public String[] getKeywords();

	/**
	 * Return classes that are demonstrated to assist in searches.
	 */
	public Class<?>[] getClasses();

	/**
	 * Return true if a horizontal separator should be shown below the header.
	 * <p>
	 * Some demos that include a CollapsibleContainer may not need separators.
	 */
	public boolean isSeparatorVisible();
}