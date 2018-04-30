package com.pump.showcase;

import java.net.URL;

public interface ShowcaseDemo {
	/**
	 * This is the title of the demo panel.
	 */
	public String getTitle();

	/**
	 * Return the optional URL of a html resource to display for additional reading.
	 */
	public URL getHelpURL();
}
