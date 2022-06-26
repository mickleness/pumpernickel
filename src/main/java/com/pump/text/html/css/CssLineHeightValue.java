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
package com.pump.text.html.css;

/**
 * The line-height CSS property sets the height of a line box. It's commonly
 * used to set the distance between lines of text. On block-level elements, it
 * specifies the minimum height of line boxes within the element. On
 * non-replaced inline elements, it specifies the height that is used to
 * calculate line box height.
 * <p>
 * If this is the keyword "normal": then {@link #VALUE_NORMAL} is used.
 * <P>
 * If this is a unitless length: then that length is a multiplier for the
 * element's font size.
 * <p>
 * If this is a percentage: then that percent applies to the element's font
 * size.
 * <p>
 * If this is a value with any other unit: that is taken as the literal size of
 * the HTML element. (Using "em" produces unexpected results.)
 * <p>
 * For some reason the normal CssPropertyParser model doesn't pick up on this
 * property. But that's OK, because there's a
 * {@link javax.swing.text.html.CSS.Attribute#LINE_HEIGHT} property we can read
 * instead. If that value is non-null we can pass its toString() value to this
 * object's constructor.
 */
public class CssLineHeightValue extends AbstractCssValue {

	/**
	 * This is the multiplier used when the "line-height" property is "normal".
	 * By default it is 1.2.
	 */
	public static float VALUE_NORMAL = 1.2f;

	String cssString;
	boolean isNormal;
	CssLength value;

	public CssLineHeightValue(String cssString) {
		this.cssString = cssString;
		isNormal = "normal".equalsIgnoreCase(cssString);
		if (isNormal) {
			value = new CssLength(VALUE_NORMAL, "");
		} else {
			value = new CssLength(cssString);
		}
	}

	public CssLength getValue() {
		return value;
	}

	public boolean isNormal() {
		return isNormal;
	}

	@Override
	public String toCSSString() {
		return cssString;
	}
}