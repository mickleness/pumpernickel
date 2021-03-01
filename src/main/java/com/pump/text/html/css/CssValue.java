package com.pump.text.html.css;

/**
 * This is a parsed value from CSS data. This is not analogous to the non-public
 * CSS.CssValue class.
 */
public interface CssValue {
	String toCSSString();

	/**
	 * Return a CssValueCreationToken, which is basically a timestamp.
	 */
	CssValueCreationToken getCreationToken();
}
