package com.pump.text.html.css;

import java.util.Objects;

/**
 * As of this writing we only support "visible" or "hidden".
 */
public class CssOverflowValue extends AbstractCssValue {
	public enum Mode {
		/**
		 * Default. The overflow is not clipped. The content renders outside the
		 * element's box
		 */
		VISIBLE,
		/**
		 * The overflow is clipped, and the rest of the content will be
		 * invisible
		 */
		HIDDEN,
		/**
		 * The overflow is clipped, and a scrollbar is added to see the rest of
		 * the content
		 */
		SCROLL,
		/** Similar to scroll, but it adds scrollbars only when necessary */
		AUTO, INHERIT
	}

	private final String cssString;
	private final Mode mode;

	public CssOverflowValue(String cssString, Mode mode) {
		Objects.requireNonNull(cssString);
		Objects.requireNonNull(mode);
		this.cssString = cssString;
		this.mode = mode;
	}

	public Mode getMode() {
		return mode;
	}

	@Override
	public String toString() {
		return toCSSString();
	}

	@Override
	public String toCSSString() {
		return cssString;
	}
}
