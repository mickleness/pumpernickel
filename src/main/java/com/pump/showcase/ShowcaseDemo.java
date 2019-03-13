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

import javax.swing.JPanel;

public abstract class ShowcaseDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * This is the title of the demo panel.
	 */
	public abstract String getTitle();

	/**
	 * Return text explaining this demo that appears immediately below the
	 * title.
	 * <p>
	 * Whenever possible this should begin with "This demonstrates" or
	 * "This compares". It is OK to include paragraphs of text, but preferably
	 * the first paragraph should be a very short/simple 1-2 sentence summary.
	 */
	public abstract String getSummary();

	/**
	 * Return the optional URL of a html resource to display for additional
	 * reading.
	 */
	public abstract URL getHelpURL();

	/**
	 * Return relevant keywords to assist in searches.
	 */
	public abstract String[] getKeywords();

	/**
	 * Return classes that are demonstrated to assist in searches.
	 */
	public abstract Class<?>[] getClasses();
}