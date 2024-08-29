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
package com.pump.plaf.button;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.TreeSet;

import com.pump.awt.ColorUtils;
import com.pump.plaf.AnimationManager;

public abstract class SimpleButtonFill extends ButtonFill {

	public static class VerticalGradientMaker {
		private final float[] fractions;
		private final Color[] colors;
		private final String name;

		/**
		 * @param name an optional name used in debugging
		 */
		public VerticalGradientMaker(float[] fractions, Color[] colors, String name) {
			if (fractions.length != colors.length)
				throw new IllegalArgumentException("fractions.length = "+ fractions.length+", colors.length = "+ colors.length);

			this.name = name;
			this.fractions = Objects.requireNonNull(fractions);
			this.colors = Objects.requireNonNull(colors);
		}

		@Override
		public String toString() {
			return "VerticalGradientMaker[ name = \"" + name + "\", colors = " + Arrays.asList(colors) + "]";
		}

		public Paint createPaint(int y1, int y2) {
			if (colors.length == 1)
				return colors[0];
			return new LinearGradientPaint(0, y1, 0, y2, fractions, colors);
		}

		public VerticalGradientMaker tween(VerticalGradientMaker otherFill, double tweenFraction) {
			if (tweenFraction <= 0)
				return this;
			if (tweenFraction >= 1)
				return otherFill;
			TreeSet<Float> allFractions = new TreeSet<>();
			for(float f : fractions) {
				allFractions.add(f);
			}
			for(float f : otherFill.fractions) {
				allFractions.add(f);
			}
			float[] newFractions = new float[allFractions.size()];
			Color[] newColors = new Color[allFractions.size()];
			int ctr = 0;
			for (Float f : allFractions) {
				newFractions[ctr] = f;
				newColors[ctr] = ColorUtils.tween(getColor(f), otherFill.getColor(f), tweenFraction);
				ctr++;
			}
			int percent = (int)(100 * tweenFraction);
			String newName = name+" -> " + otherFill.name+" (" + percent + "%)";
			return new VerticalGradientMaker(newFractions, newColors, newName);
		}

		public Color getColor(float f) {
			if (f < fractions[0] )
				return colors[0];
			for (int i = 0; i < fractions.length - 1; i++) {
				if (f >= fractions[i] && f < fractions[i + 1]) {
					float span = fractions[i + 1] - fractions[i];
					float elapsed = (f - fractions[i]) / span;
					return ColorUtils.tween(colors[i], colors[i + 1], elapsed);
				}
			}
			return colors[colors.length - 1];
		}
	}


	protected static final Color shadowHighlight = new Color(255, 255, 255, 120);

	/** Returns a translucent white. */
	@Override
	public Color getShadowHighlight(ButtonState.Float button) {
		return shadowHighlight;
	}

	/**
	 * This fill is the darkest shade for this button. This is used for
	 * <code>JToggleButtons</code> to depict a selected state. If this is null,
	 * then <code>getDarkerFill()</code> may be used instead.
	 * 
	 * @param fillRect
	 *            the rectangle the fill applies to.
	 * @return the paint to use when a toggle button is selected.
	 */
	public abstract VerticalGradientMaker getDarkestFill(Rectangle fillRect);

	/**
	 * The normal fill of this button. Unlike the other <code>getter()</code>
	 * methods in this class: this cannot return null.
	 * 
	 * @param fillRect
	 *            the rectangle the fill applies to.
	 * @return the paint to use when this button is in its normal state.
	 */
	public abstract VerticalGradientMaker getNormalFill(Rectangle fillRect);

	@Override
	public Paint getFill(ButtonState.Float state, Rectangle fillRect) {
		VerticalGradientMaker currentFill;
		VerticalGradientMaker normalFill = getNormalFill(fillRect);
		double darkestFraction = Math.max(state.isArmed(), Math.max(state.isSelected() * .7f, state.isRollover() * .4));
		if (darkestFraction > 0) {
			VerticalGradientMaker darkestFill = getDarkestFill(fillRect);
			currentFill = normalFill.tween(darkestFill, darkestFraction);
		} else {
			currentFill = normalFill;
		}
		return currentFill.createPaint(fillRect.y, fillRect.y + fillRect.height);
	}
}