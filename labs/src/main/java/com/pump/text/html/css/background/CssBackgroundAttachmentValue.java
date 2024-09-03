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

import javax.swing.text.html.CSS;

import com.pump.text.html.css.AbstractCssValue;

/**
 * The background-attachment CSS property sets whether a background image's
 * position is fixed within the viewport, or scrolls with its containing block.
 */
public class CssBackgroundAttachmentValue extends AbstractCssValue {

	// TODO: we support fixed/scroll, but not anything else

	public enum Mode {
		/**
		 * The background is fixed relative to the element itself and does not
		 * scroll with its contents. (It is effectively attached to the
		 * element's border.)
		 */
		SCROLL,
		/**
		 * The background is fixed relative to the viewport. Even if an element
		 * has a scrolling mechanism, the background doesn't move with the
		 * element. (This is not compatible with background-clip: text.)
		 */
		FIXED,
		/**
		 * The background is fixed relative to the element's contents. If the
		 * element has a scrolling mechanism, the background scrolls with the
		 * element's contents, and the background painting area and background
		 * positioning area are relative to the scrollable area of the element
		 * rather than to the border framing them.
		 */
		LOCAL, INHERIT, INITIAL, UNSET;
	}

	public static String PROPERTY_BACKGROUND_ATTACHMENT = CSS.Attribute.BACKGROUND_ATTACHMENT
			.toString();

	String cssValue;
	Mode mode;

	public CssBackgroundAttachmentValue(String cssValue, Mode mode) {
		Objects.requireNonNull(cssValue);
		Objects.requireNonNull(mode);
		this.mode = mode;
		this.cssValue = cssValue;
	}

	public Mode getMode() {
		return mode;
	}

	@Override
	public String toString() {
		return cssValue;
	}

	@Override
	public String toCSSString() {
		return cssValue;
	}

}