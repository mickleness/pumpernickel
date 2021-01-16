package com.pump.text.html.css.border;

import java.util.Objects;

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssValue;

/**
 * <p>
 * With one value: <br>
 * the value is a &lt;length&gt; or a &lt;percentage&gt; denoting the radius of
 * the circle to use for the border in that corner.
 * <p>
 * With two values: <br>
 * the first value is a &lt;length&gt; or a &lt;percentage&gt; denoting the
 * horizontal semi-major axis of the ellipse to use for the border in that
 * corner. the second value is a &lt;length&gt; or a &lt;percentage&gt; denoting
 * the vertical semi-major axis of the ellipse to use for the border in that
 * corner.
 *
 */
public class CssBorderRadiusValue implements CssValue {
	CssLength horizontalValue, verticalValue;
	String cssString;

	public CssBorderRadiusValue(String cssString, CssLength horizontalValue,
			CssLength verticalValue) {
		Objects.requireNonNull(cssString);
		Objects.requireNonNull(horizontalValue);
		Objects.requireNonNull(verticalValue);

		this.cssString = cssString;
		this.horizontalValue = horizontalValue;
		this.verticalValue = verticalValue;
	}

	public CssBorderRadiusValue(CssLength horizontalValue,
			CssLength verticalValue) {
		Objects.requireNonNull(horizontalValue);
		Objects.requireNonNull(verticalValue);

		this.cssString = horizontalValue.toCSSString() + " "
				+ verticalValue.toCSSString();
		this.horizontalValue = horizontalValue;
		this.verticalValue = verticalValue;
	}

	public CssBorderRadiusValue(String cssString) {
		Objects.requireNonNull(cssString);
		this.cssString = cssString;

		String z = cssString.stripLeading();
		int leadingWhitespace = cssString.length() - z.length();

		String firstTerm = null;
		String secondTerm = null;
		for (int a = 0; a < z.length(); a++) {
			char ch = z.charAt(a);
			if (Character.isWhitespace(ch)) {
				if (firstTerm == null) {
					firstTerm = cssString.substring(leadingWhitespace,
							leadingWhitespace + a);
				}
			} else {
				if (firstTerm != null) {
					secondTerm = cssString.substring(leadingWhitespace,
							leadingWhitespace + a);
					break;
				}
			}
		}

		if (firstTerm == null) {
			horizontalValue = new CssLength(cssString);
			verticalValue = horizontalValue;
		} else {
			horizontalValue = new CssLength(firstTerm);
			verticalValue = new CssLength(secondTerm);
		}
	}

	public CssLength getHorizontalValue() {
		return horizontalValue;
	}

	public CssLength getVerticalValue() {
		return verticalValue;
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
