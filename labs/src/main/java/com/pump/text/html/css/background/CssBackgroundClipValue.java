/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.text.html.css.background;

import java.util.Objects;

import com.pump.text.html.css.AbstractCssValue;

/**
 * The background-clip CSS property sets whether an element's background extends
 * underneath its border box, padding box, or content box.
 */
public class CssBackgroundClipValue extends AbstractCssValue {

	public static final String PROPERTY_BACKGROUND_CLIP = "background-clip";

	public enum Mode {
		/**
		 * The background extends to the outside edge of the border (but
		 * underneath the border in z-ordering).
		 */
		BORDER_BOX("border-box"),

		/**
		 * The background extends to the outside edge of the padding. No
		 * background is drawn beneath the border.
		 */
		PADDING_BOX("padding-box"),

		/**
		 * The background is painted within (clipped to) the content box.
		 */
		CONTENT_BOX("content-box"),

		/**
		 * The background is painted within (clipped to) the foreground text.
		 * This is not a formal part of the CSS definition, but Mozilla/Firefox
		 * supports it.
		 */
		TEXT("text");

		String cssName;

		Mode(String cssName) {
			this.cssName = cssName;
		}

		@Override
		public String toString() {
			return cssName;
		}
	}

	String cssString;
	Mode mode;

	public CssBackgroundClipValue(String cssString, Mode mode) {
		Objects.requireNonNull(cssString);
		Objects.requireNonNull(mode);
		this.cssString = cssString;
		this.mode = mode;
	}

	public Mode getMode() {
		return mode;
	}

	@Override
	public String toCSSString() {
		return cssString;
	}
}