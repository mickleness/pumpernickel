package com.pump.text.html.css;

import java.util.Objects;

/**
 * This value identifies a width or height.
 * <p>
 * Currently we support "max-content", "min-content", "auto" and a fixed length.
 */
public class CssDimensionValue extends AbstractCssValue {

	public enum Type {
		/**
		 * The browser will calculate and select a width for the specified
		 * element.
		 */
		AUTO("auto"),

		/**
		 * The intrinsic preferred width.
		 * <p>
		 * Some views (such as a header tag) may normally want to extend to fill
		 * 100% of the available space. But if you give an h1 tag a width of
		 * "max-content": then it will only extend take up as much space as it
		 * needs to render the characters.
		 * 
		 */
		MAX_CONTENT("max-content"),

		/**
		 * The intrinsic minimum width.
		 * <p>
		 * Like the MAX_CONTENT value this may help you reduce the width of
		 * text. But if MAX_CONTENT resolves to "100px" to fit the words "LOREM
		 * IPSUM", then MIN_CONTENT will resolve to about "60px", because it
		 * knows it can break "LOREM" and "IPSUM" into two separate lines of
		 * text. So the returned value is the width of the longer word.
		 */
		MIN_CONTENT("min-content"),

		/**
		 * If this Type is used, then {@link CssDimensionValue#getLength()} will
		 * return a non-null value.
		 */
		LENGTH(null);

		String cssName;

		Type(String cssName) {
			this.cssName = cssName;
		}

		/**
		 * This returns the CSS name of this type, or for LENGTH this returns
		 * null.
		 */
		public String toCSSString() {
			return cssName;
		}
	}

	private String cssString;
	private CssLength length;
	private Type type;

	public CssDimensionValue(String cssString) {
		Objects.requireNonNull(cssString);
		this.cssString = cssString;

		for (Type type : Type.values()) {
			if (type.cssName != null
					&& type.cssName.equalsIgnoreCase(cssString)) {
				this.type = type;
				break;
			}
		}

		if (type == null && cssString.startsWith("fit-content("))
			throw new IllegalArgumentException(
					"the fit-content function is not supported");

		if (type == null && cssString.startsWith("fit-content"))
			throw new IllegalArgumentException(
					"the fit-content keyword is not supported");

		if (type == null) {
			length = new CssLength(cssString);
			type = Type.LENGTH;
		}
	}

	public Type getType() {
		return type;
	}

	/**
	 * If {@link getType()} returns Type.LENGTH, then this method returns the
	 * length of this width. Otherwise this method returns null.
	 */
	public CssLength getLength() {
		return length;
	}

	@Override
	public String toCSSString() {
		return cssString;
	}
}
