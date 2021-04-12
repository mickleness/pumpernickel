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
