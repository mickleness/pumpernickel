package com.pump.text.html.css;

/**
 * This CssValue includes a generic {@link #getCreationToken()}.s
 */
public abstract class AbstractCssValue implements CssValue {

	protected final CssValueCreationToken creationToken = new CssValueCreationToken();

	@Override
	public CssValueCreationToken getCreationToken() {
		return creationToken;
	}

}
