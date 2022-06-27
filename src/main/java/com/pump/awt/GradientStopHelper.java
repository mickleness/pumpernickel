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
package com.pump.awt;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Arrays;

import com.pump.plaf.AnimationManager;

/**
 * This helps organize gradient stops.
 * <p>
 * You can add any number of stops between 0 and 1. You can add them in any
 * order, and this object will sort them.
 * </p>
 * <p>
 * If you add a stop outside of [0,1], then that stop will be replaced with a
 * stop at 0 or 1. For example, if you add a stop at 1 for white, and a stop at
 * -1 for black, then the -1 entry will be replaced with a gray at 0.
 * </p>
 */
public class GradientStopHelper {

	float[] stops = new float[0];
	Color[] colors = new Color[0];

	/**
	 * Convert this GradientStopHelper to a LinearGradientPaint.
	 * 
	 * @param x1
	 *            the x of the starting point.
	 * @param y1
	 *            the y of the starting point.
	 * @param x2
	 *            the x of the final point.
	 * @param y2
	 *            the y of the final point.
	 */
	public LinearGradientPaint toPaint(double x1, double y1, double x2,
			double y2) {
		if (stops.length == 0)
			return null;
		Point2D start = new Point2D.Double(x1, y1);
		Point2D end = new Point2D.Double(x2, y2);

		return new LinearGradientPaint(start, end, stops, colors);
	}

	/**
	 * Add a color stop for this gradient.
	 * 
	 * @param d
	 *            a fractional value on a scale of [0, 1]. This may be outside
	 *            of [0,1], but this GradientStopHelper will never store a stop
	 *            value outside of [0,1].
	 * @param c
	 *            the color to add.
	 */
	public void addStop(double d, Color c) {
		float f = (float) d;
		if (stops.length == 0) {
			stops = new float[] { f };
			colors = new Color[] { c };
			return;
		}
		int i = Arrays.binarySearch(stops, f);
		if (i >= 0) {
			colors[i] = c;
		} else {
			int insertionPoint = -(i + 1);

			float[] newStops = new float[stops.length + 1];
			Color[] newColors = new Color[colors.length + 1];
			System.arraycopy(stops, 0, newStops, 0, insertionPoint);
			System.arraycopy(colors, 0, newColors, 0, insertionPoint);
			newStops[insertionPoint] = f;
			newColors[insertionPoint] = c;
			System.arraycopy(stops, insertionPoint, newStops,
					insertionPoint + 1, stops.length - insertionPoint);
			System.arraycopy(colors, insertionPoint, newColors,
					insertionPoint + 1, stops.length - insertionPoint);

			// did we add a stop that is outside [0,1]? That's not allowed:
			if (newStops[0] < 0) {
				if (newStops[1] == 0)
					return;

				newColors[0] = AnimationManager.tween(newColors[0],
						newColors[1],
						-newStops[0] / (newStops[1] - newStops[0]));
				newStops[0] = 0;
			}

			if (newStops[newStops.length - 1] > 1) {
				if (newStops[newStops.length - 1] == 1)
					return;

				newColors[newStops.length - 1] = AnimationManager.tween(
						newColors[newColors.length - 2],
						newColors[newColors.length - 1],
						(1 - newStops[newStops.length - 2])
								/ (newStops[newStops.length - 1]
										- newStops[newStops.length - 2]));
				newStops[newStops.length - 1] = 1;
			}

			stops = newStops;
			colors = newColors;
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getClass().getSimpleName());
		sb.append("[");
		for (int i = 0; i < stops.length; i++) {
			if (i != 0)
				sb.append(", ");
			sb.append(DecimalFormat.getInstance().format(stops[i]));
			sb.append("=");
			sb.append(colors[i]);
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Return the number of stops.
	 */
	public int size() {
		return stops.length;
	}

	/**
	 * Return a specific Color.
	 * 
	 * @param i
	 *            the stop index
	 */
	public Color getColor(int i) {
		return colors[i];
	}

	/**
	 * Return a specific stop.
	 * 
	 * @param i
	 *            the stop index
	 */
	public float getStop(int i) {
		return stops[i];
	}

}