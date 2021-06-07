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
package com.pump.showcase.demo;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

import com.pump.showcase.app.ShowcaseDemoInfo;

public abstract class ShowcaseDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	private ShowcaseDemoInfo showcaseDemoInfo;

	/**
	 * This is the title of the demo panel.
	 */
	public abstract String getTitle();

	/**
	 * Return text explaining this demo that appears immediately below the
	 * title.
	 * <p>
	 * Whenever possible this should begin with "This demonstrates" or "This
	 * compares". It is OK to include paragraphs of text, but preferably the
	 * first paragraph should be a very short/simple 1-2 sentence summary.
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

	/**
	 * Return a list of Runnables that should be invoked in a separate thread
	 * before this demo is shown to the user. This is used for demos that need
	 * to run thousands of timed comparisons to create charts.
	 */
	@SuppressWarnings("unchecked")
	public List<Runnable> getInitializationRunnables() {
		return Collections.EMPTY_LIST;
	}

	/**
	 * Assign the ShowcaseDemoInfo that created this object.
	 */
	public void setDemoInfo(ShowcaseDemoInfo showcaseDemoInfo) {
		if (this.showcaseDemoInfo != null)
			throw new IllegalArgumentException(
					"This method should only be called once.");
		this.showcaseDemoInfo = showcaseDemoInfo;
	}

	/**
	 * Return the ShowcaseDemoInfo that created this object.
	 */
	public ShowcaseDemoInfo getDemoInfo() {
		return showcaseDemoInfo;
	}
}