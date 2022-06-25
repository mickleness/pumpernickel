package com.pump.awt;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Arrays;

import com.pump.plaf.AnimationManager;

public class GradientStopHelper {

	float[] stops = new float[0];
	Color[] colors = new Color[0];

	public Paint toPaint(double x1, double y1, double x2, double y2) {
		if (stops.length == 0)
			return null;
		Point2D start = new Point2D.Double(x1, y1);
		Point2D end = new Point2D.Double(x2, y2);

		return new LinearGradientPaint(start, end, stops, colors);
	}

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

	public int size() {
		return stops.length;
	}

	public Color getColor(int i) {
		return colors[i];
	}

	public float getStop(int i) {
		return stops[i];
	}

}