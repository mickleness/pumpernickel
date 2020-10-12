package com.pump.text.html.css.background;

import java.util.Objects;

import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssValue;

public class CssBackgroundPositionValue implements CssValue {

	private final String cssString;

	CssLength horizontalPosition, verticalPosition;
	boolean isFromLeft, isFromTop;

	public CssBackgroundPositionValue(String cssString,
			CssLength horizontalPosition, CssLength verticalPosition) {
		this(cssString, horizontalPosition, true, verticalPosition, true);
	}

	/**
	 * 
	 * @param cssString
	 * @param horizontalPosition
	 * @param isFromLeft
	 *            true if the horizontal position is relative to the left edge,
	 *            which is the default. For example the position "right 10%"
	 *            means "10% of the width away from the right edge".
	 * @param verticalPosition
	 * @param isFromTop
	 *            true if the vertical position is relative to the top edge,
	 *            which is the default. For example the position "bottom 10%"
	 *            means "10% of the height away from the bottom edge".
	 */
	public CssBackgroundPositionValue(String cssString,
			CssLength horizontalPosition, boolean isFromLeft,
			CssLength verticalPosition, boolean isFromTop) {
		Objects.requireNonNull(cssString);
		Objects.requireNonNull(horizontalPosition);
		Objects.requireNonNull(verticalPosition);

		validate(horizontalPosition);
		validate(verticalPosition);

		this.cssString = cssString;
		this.horizontalPosition = horizontalPosition;
		this.verticalPosition = verticalPosition;
		this.isFromLeft = isFromLeft;
		this.isFromTop = isFromTop;
	}

	private void validate(CssLength pos) {
		if (!(pos.getUnit().equals("%") || pos.getUnit().equals("px")
				|| pos.getUnit().isEmpty()))
			throw new IllegalArgumentException(
					"Unsupported length \"" + pos.toCSSString() + "\"");
	}

	@Override
	public int hashCode() {
		return (horizontalPosition.hashCode() << 8)
				+ verticalPosition.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CssBackgroundPositionValue))
			return false;
		CssBackgroundPositionValue other = (CssBackgroundPositionValue) obj;

		CssBackgroundPositionValue a = normalize();
		CssBackgroundPositionValue b = other.normalize();

		if (!Objects.equals(a.horizontalPosition, b.horizontalPosition))
			return false;
		if (!Objects.equals(a.verticalPosition, b.verticalPosition))
			return false;
		if (a.isFromLeft != b.isFromLeft)
			return false;
		if (a.isFromTop != b.isFromTop)
			return false;

		return true;
	}

	/**
	 * If possible convert "isFromLeft" and "isFromTop" from false to true.
	 * <p>
	 * If this is not possible (because the positions are not percentages), then
	 * this method returns this object.
	 */
	private CssBackgroundPositionValue normalize() {
		CssLength newHorizontalPosition = horizontalPosition;
		boolean newIsFromLeft = isFromLeft;
		if (!isFromLeft && "%".equals(horizontalPosition.getUnit())) {
			newHorizontalPosition = new CssLength(
					100 - horizontalPosition.getValue(), "%");
			newIsFromLeft = true;
		}

		CssLength newVerticalPosition = verticalPosition;
		boolean newIsFromTop = isFromTop;
		if (!isFromTop && "%".equals(verticalPosition.getUnit())) {
			newVerticalPosition = new CssLength(
					100 - verticalPosition.getValue(), "%");
			newIsFromTop = true;
		}

		if (newIsFromLeft != isFromLeft || newIsFromTop != isFromTop) {
			return new CssBackgroundPositionValue(cssString,
					newHorizontalPosition, newIsFromLeft, newVerticalPosition,
					newIsFromTop);
		}
		return this;
	}

	@Override
	public String toString() {
		return toCSSString();
	}

	public boolean isHorizontalPositionFromLeft() {
		return isFromLeft;
	}

	public boolean isVerticalPositionFromTop() {
		return isFromTop;
	}

	public CssLength getHorizontalPosition() {
		return horizontalPosition;
	}

	public CssLength getVerticalPosition() {
		return verticalPosition;
	}

	@Override
	public String toCSSString() {
		return cssString;
	}

}
