package com.pump.text.html.css.background;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.pump.text.html.css.CssValue;

public class CssBackgroundRepeatValue implements CssValue {

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
					int imageLength) {
				List<Span> returnValue = new LinkedList<>();
				int j = 0;
				while (j < canvasLength) {
					returnValue.add(new Span(j, imageLength));
					j += imageLength;
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
					int imageLength) {
				// TODO: integrate background-position
				List<Span> returnValue = new LinkedList<>();

				int repetitions = canvasLength / imageLength;
				if (repetitions >= 2) {
					for (int r = 0; r < repetitions; r++) {
						returnValue.add(new Span(
								canvasStart + canvasLength * r / repetitions,
								imageLength));
					}
				} else {
					returnValue.add(new Span(
							canvasStart + canvasLength / 2 - imageLength / 2,
							imageLength));
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
					int imageLength) {
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
					int imageLength) {
				// TODO: consult background position
				return Collections.singletonList(new Span(
						canvasStart + canvasLength / 2 - imageLength / 2,
						imageLength));
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
				int imageLength);
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
