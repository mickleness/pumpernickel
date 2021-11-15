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
package com.pump.text.html.css;

/**
 * This CssValue includes a generic {@link #getCreationToken()}.
 */
public abstract class AbstractCssValue implements CssValue {

	protected final CssValueCreationToken creationToken = new CssValueCreationToken();

	@Override
	public CssValueCreationToken getCreationToken() {
		return creationToken;
	}

}