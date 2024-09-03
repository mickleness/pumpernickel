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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.pump.text.html.css.AbstractCssValue;
import com.pump.text.html.css.CssLength;

public class CssBackgroundRepeatValue extends AbstractCssValue {

	public static class Span {
		public int position, length;

		public Span(int position, int length) {
			this.position = position;
			this.length = length;
		}

		@Override
		public String toString() {
			return "Span[" + position + ", " + length + "]";
		}
	}

	public enum Mode {
		/**
		 * The image is repeated as much as needed to cover the whole background
		 * image painting area. The last image will be clipped if it doesn't
		 * fit.
		 */
		REPEAT {

			@Override
			public List<Span> getSpans(int canvasStart, int canvasLength,
					int imageLength, CssLength position,
					boolean positionFromStart) {

				List<Span> returnValue = new LinkedList<>();
				Span originalSpan = NO_REPEAT.getSpans(canvasStart,
						canvasLength, imageLength, position, positionFromStart)
						.get(0);
				returnValue.add(originalSpan);

				for (int j = originalSpan.position
						+ originalSpan.length; j < canvasStart
								+ canvasLength; j += imageLength) {
					returnValue.add(new Span(j, imageLength));
				}

				for (int j = originalSpan.position - imageLength; j
						+ imageLength >= canvasStart; j -= imageLength) {
					returnValue.add(new Span(j, imageLength));
				}

				return returnValue;
			}
		},

		/**
		 * The image is repeated as much as possible without clipping. The first
		 * and last images are pinned to either side of the element, and
		 * whitespace is distributed evenly between the images. The
		 * background-position property is ignored unless only one image can be
		 * displayed without clipping. The only case where clipping happens
		 * using space is when there isn't enough room to display one image.
		 */
		SPACE {
			@Override
			public List<Span> getSpans(int canvasStart, int canvasLength,
					int imageLength, CssLength position,
					boolean positionFromStart) {
				List<Span> returnValue = new LinkedList<>();

				int repetitions = canvasLength / imageLength;
				if (repetitions >= 2) {
					for (int r = 0; r < repetitions; r++) {
						returnValue.add(new Span(
								canvasStart + canvasLength * r / repetitions,
								imageLength));
					}
				} else {
					return NO_REPEAT.getSpans(canvasStart, canvasLength,
							imageLength, position, positionFromStart);
				}

				return returnValue;
			}
		},

		/**
		 * As the allowed space increases in size, the repeated images will
		 * stretch (leaving no gaps) until there is room (space left >= half of
		 * the image width) for another one to be added. When the next image is
		 * added, all of the current ones compress to allow room. Example: An
		 * image with an original width of 260px, repeated three times, might
		 * stretch until each repetition is 300px wide, and then another image
		 * will be added. They will then compress to 225px.
		 */
		ROUND {
			@Override
			public List<Span> getSpans(int canvasStart, int canvasLength,
					int imageLength, CssLength position,
					boolean positionFromStart) {
				// this formula is from
				// https://www.w3.org/TR/css-backgrounds-3/#propdef-background-size
				float imageLengthPrime = ((float) canvasLength / Math
						.round((float) canvasLength / (float) imageLength));

				// use floats to prevent rounding errors from accumulating
				float k = canvasStart;
				List<Span> returnValue = new LinkedList<>();
				while (k < canvasLength) {
					int start = (int) k;
					int end = (int) (k + imageLengthPrime);
					returnValue.add(new Span(start, end - start));
					k += imageLengthPrime;
				}

				return returnValue;
			}
		},

		/**
		 * The image is not repeated (and hence the background image painting
		 * area will not necessarily be entirely covered). The position of the
		 * non-repeated background image is defined by the background-position
		 * CSS property.
		 */
		NO_REPEAT("no-repeat") {
			@Override
			public List<Span> getSpans(int canvasStart, int canvasLength,
					int imageLength, CssLength position,
					boolean positionFromStart) {
				if (position == null) {
					return Collections.singletonList(new Span(
							canvasStart + canvasLength / 2 - imageLength / 2,
							imageLength));
				}
				if ("%".equals(position.getUnit())) {
					int canvasLengthP = canvasLength - imageLength;
					float percent = positionFromStart ? position.getValue()
							: 100 - position.getValue();
					int pos = (int) (canvasLengthP * percent / 100);
					return Collections
							.singletonList(new Span(pos, imageLength));
				} else if ("px".equals(position.getUnit())) {
					int pos;
					if (positionFromStart) {
						pos = (int) (position.getValue());
					} else {
						pos = (int) (canvasLength - imageLength
								- position.getValue());
					}
					return Collections
							.singletonList(new Span(pos, imageLength));
				}
				throw new RuntimeException(
						"unsupported position unit: " + position);
			}
		};

		String keywordText;

		Mode() {
			keywordText = name().toLowerCase();
		}

		Mode(String keywordText) {
			this.keywordText = keywordText;
		}

		public String getKeyword() {
			return keywordText;
		}

		public abstract List<Span> getSpans(int canvasStart, int canvasLength,
				int imageLength, CssLength position, boolean positionFromStart);
	}

	private final String cssString;
	private Mode horizontalMode, verticalMode;

	public CssBackgroundRepeatValue(String cssString, Mode mode) {
		this(cssString, mode, mode);
	}

	public CssBackgroundRepeatValue(String cssString, Mode horizontalMode,
			Mode verticalMode) {
		Objects.requireNonNull(cssString);
		Objects.requireNonNull(horizontalMode);
		Objects.requireNonNull(horizontalMode);

		this.cssString = cssString;
		this.horizontalMode = horizontalMode;
		this.verticalMode = verticalMode;
	}

	public CssBackgroundRepeatValue(Mode mode) {
		this(mode.getKeyword(), mode);
	}

	public Mode getHorizontalMode() {
		return horizontalMode;
	}

	public Mode getVerticalMode() {
		return verticalMode;
	}

	@Override
	public String toCSSString() {
		return cssString;
	}

	@Override
	public String toString() {
		return "CssBackgroundRepeatValue[ " + toCSSString() + " ]";
	}
}