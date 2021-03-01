package com.pump.text.html.css.background;

import java.awt.Dimension;
import java.util.Objects;

import com.pump.text.html.css.AbstractCssValue;
import com.pump.text.html.css.CssLength;

public class CssBackgroundSizeValue extends AbstractCssValue {

	public interface Calculator {

		/**
		 * Return the size of the image to paint.
		 * 
		 * @param canvasWidth
		 *            the width of the canvas we'll draw to.
		 * @param canvasHeight
		 *            the height of the canvas we'll draw to.
		 * @param imageSize
		 *            the optional image size to render. For example: a JPG has
		 *            a clear size, but a linear gradient will pass null.
		 * @return
		 */
		public Dimension getSize(int canvasWidth, int canvasHeight,
				Dimension imageSize);

	}

	/**
	 * Scales the image as large as possible without cropping or stretching the
	 * image.
	 */
	private static class ContainCalculator implements Calculator {

		@Override
		public Dimension getSize(int canvasWidth, int canvasHeight,
				Dimension imageSize) {
			if (imageSize == null)
				return new Dimension(canvasWidth, canvasHeight);
			float sx = ((float) canvasWidth) / ((float) imageSize.width);
			float sy = ((float) canvasHeight) / ((float) imageSize.height);
			float scale = Math.min(sx, sy);
			return new Dimension((int) (scale * imageSize.width),
					(int) (scale * imageSize.height));
		}

	}

	/**
	 * Scales the image as large as possible without stretching the image. If
	 * the proportions of the image differ from the element, it is cropped
	 * either vertically or horizontally so that no empty space remains.
	 */
	private static class CoverCalculator implements Calculator {

		@Override
		public Dimension getSize(int canvasWidth, int canvasHeight,
				Dimension imageSize) {
			if (imageSize == null)
				return new Dimension(canvasWidth, canvasHeight);
			float sx = ((float) canvasWidth) / ((float) imageSize.width);
			float sy = ((float) canvasHeight) / ((float) imageSize.height);
			float scale = Math.max(sx, sy);
			return new Dimension((int) (scale * imageSize.width),
					(int) (scale * imageSize.height));
		}

	}

	private static class AutoCalculator implements Calculator {

		@Override
		public Dimension getSize(int canvasWidth, int canvasHeight,
				Dimension imageSize) {
			if (imageSize != null)
				return new Dimension(imageSize.width, imageSize.height);
			return new Dimension(canvasWidth, canvasHeight);
		}

	}

	/**
	 * This always returns a fixed size. That size might be percent-based, so it
	 * may change based on the canvas width/height.
	 */
	public static class FixedSizeCalculator implements Calculator {
		private final CssLength width, height;

		public FixedSizeCalculator(CssLength width, CssLength height) {
			Objects.requireNonNull(width);
			Objects.requireNonNull(height);
			this.width = width;
			this.height = height;
		}

		@Override
		public Dimension getSize(int canvasWidth, int canvasHeight,
				Dimension imageSize) {
			int newWidth, newHeight;
			if (this.width.getUnit().equals("%")) {
				newWidth = (int) (canvasWidth * this.width.getValue() / 100);
			} else {
				newWidth = (int) (this.width.getValue());
			}

			if (this.height.getUnit().equals("%")) {
				newHeight = (int) (canvasHeight * this.height.getValue() / 100);
			} else {
				newHeight = (int) (this.height.getValue());
			}
			return new Dimension(newWidth, newHeight);
		}

	}

	public static class AutoWidthCalculator implements Calculator {
		private final CssLength height;

		public AutoWidthCalculator(CssLength height) {
			Objects.requireNonNull(height);
			this.height = height;
		}

		@Override
		public Dimension getSize(int canvasWidth, int canvasHeight,
				Dimension imageSize) {
			if (imageSize == null)
				return new Dimension(canvasWidth, canvasHeight);

			int newHeight;
			if (this.height.getUnit().equals("%")) {
				newHeight = (int) (canvasHeight * this.height.getValue() / 100);
			} else {
				newHeight = (int) (this.height.getValue());
			}

			int newWidth = imageSize.width * newHeight / imageSize.height;
			return new Dimension(newWidth, newHeight);
		}
	}

	public static class AutoHeightCalculator implements Calculator {
		private final CssLength width;

		public AutoHeightCalculator(CssLength width) {
			Objects.requireNonNull(width);
			this.width = width;
		}

		@Override
		public Dimension getSize(int width, int height, Dimension imageSize) {
			if (imageSize == null)
				return new Dimension(width, height);

			int newWidth;
			if (this.width.getUnit().equals("%")) {
				newWidth = (int) (width * this.width.getValue() / 100);
			} else {
				newWidth = (int) (this.width.getValue());
			}

			int newHeight = imageSize.height * newWidth / imageSize.width;
			return new Dimension(newWidth, newHeight);
		}

	}

	public static final Calculator CONTAIN = new ContainCalculator();
	public static final Calculator COVER = new CoverCalculator();
	public static final Calculator AUTO = new AutoCalculator();

	final String cssString;
	final Calculator calculator;

	public CssBackgroundSizeValue(String cssString, Calculator calculator) {
		Objects.requireNonNull(cssString);
		Objects.requireNonNull(calculator);
		this.cssString = cssString;
		this.calculator = calculator;
	}

	@Override
	public String toString() {
		return toCSSString();
	}

	@Override
	public String toCSSString() {
		return cssString;
	}

	public Calculator getCalculator() {
		return calculator;
	}

}
