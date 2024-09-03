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
package com.pump.plaf;

public class HueDistribution {
	float[] x;
	float[] y;

	public HueDistribution(float[] keyPoints) {
		x = new float[keyPoints.length];
		for (int a = 0; a < keyPoints.length; a++) {
			x[a] = (a) / ((float) keyPoints.length - 1);
		}
		y = keyPoints;
	}

	public float evaluate(float xValue) {
		try {
			return evaluate(x, y, xValue);
		} catch (RuntimeException e) {
			System.err.println(this);
			throw e;
		}
	}

	public float evaluateInverse(float yValue) {
		try {
			return evaluate(y, x, yValue);
		} catch (RuntimeException e) {
			System.err.println(this);
			throw e;
		}
	}

	@Override
	public String toString() {
		return "HueDistribution[ x = " + toString(x) + ", y = " + toString(y)
				+ " ]";
	}

	private static String toString(float[] array) {
		StringBuffer sb = new StringBuffer();
		sb.append("[ ");
		for (int a = 0; a < array.length; a++) {
			if (a != 0) {
				sb.append(", ");
			}
			sb.append(Float.toString(array[a]));
		}
		sb.append(" ]");
		return sb.toString();
	}

	protected static float evaluate(float[] x, float[] y, float xValue) {
		if (xValue < x[0])
			throw new IllegalArgumentException("xValue (" + xValue
					+ ") is less than the minimum x (" + (x[0]) + ")");
		if (xValue > x[x.length - 1])
			throw new IllegalArgumentException("xValue (" + xValue
					+ ") is greater than the maximum x (" + (x[x.length - 1])
					+ ")");

		for (int a = 0; a < x.length - 1; a++) {
			if (x[a] <= xValue && xValue <= x[a + 1]) {
				float t = (xValue - x[a]) / (x[a + 1] - x[a]);
				return y[a] + t * (y[a + 1] - y[a]);
			}
		}

		throw new IllegalArgumentException("xValue (" + xValue
				+ ") did not appear inside range [" + x[0] + ", "
				+ x[x.length - 1] + "]");
	}
}