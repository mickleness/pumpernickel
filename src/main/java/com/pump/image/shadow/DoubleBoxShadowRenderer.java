/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.shadow;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This mimics a {@link GaussianShadowRenderer} by applying two iterations of a
 * {@link BoxShadowRenderer}. This class is preloaded with information about how
 * to best combine BoxShadowRenderers to resemble a GaussianShadowRenderer for
 * kernel radii of up to 100 pixels.
 * <p>
 * Usually a "double box" or "triple box" refers to applying a box blur in one
 * dimension x-many times, and then apply the box blur in the opposite dimension
 * x-many times. Instead this class applies one complete box blur (in both
 * dimensions) before applying the second. I think (?) this lets us apply only
 * two iterations to get sufficiently close to a gaussian blur.
 * <p>
 * Warning: the kernel returned by {@link #getKernel(ShadowAttributes)} is an
 * approximation. No single kernel will exactly describe the effects of this
 * renderer.
 */
public class DoubleBoxShadowRenderer implements ShadowRenderer {

	static TreeMap<Number, Combo> lookupTable = new TreeMap<>();

	static {
		try (InputStream in = DoubleBoxShadowRenderer.class
				.getResourceAsStream("DoubleBoxShadowRendererTable.csv")) {
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(in, "UTF-8"))) {
				String s = br.readLine();
				while (s != null) {
					String[] terms = s.split(",");
					Number radius = Float.parseFloat(terms[0]);
					Combo combo = new Combo(Float.parseFloat(terms[2]),
							Float.parseFloat(terms[3]));
					lookupTable.put(radius, combo);

					s = br.readLine();
				}
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static class Combo implements Comparable<Combo> {

		List<Float> sortedRadii;
		int radiiSum;

		Combo(float... fastKernelRadii) {
			radiiSum = 0;
			sortedRadii = new ArrayList<>(fastKernelRadii.length);
			for (float radius : fastKernelRadii) {
				if (radius != 0)
					sortedRadii.add(radius);
				radiiSum += (int) (Math.ceil(radius) + .5);

			}
			Collections.sort(sortedRadii);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Combo[ ");
			sb.append("radii = {");
			sb.append(sortedRadii.get(0));
			for (int a = 1; a < sortedRadii.size(); a++) {
				sb.append(",");
				sb.append(sortedRadii.get(a));
			}
			sb.append("}]");
			return sb.toString();
		}

		@Override
		public int compareTo(Combo o) {
			int max = Math.max(sortedRadii.size(), o.sortedRadii.size());
			for (int a = 0; a < max; a++) {
				Float v1 = a < sortedRadii.size() ? sortedRadii.get(a) : -1;
				Float v2 = a < o.sortedRadii.size() ? o.sortedRadii.get(a) : -1;
				int k = v1.compareTo(v2);
				if (k != 0)
					return k;
			}
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Combo))
				return false;
			Combo other = (Combo) obj;
			return compareTo(other) == 0;
		}

		@Override
		public int hashCode() {
			return sortedRadii.hashCode();
		}

		public ARGBPixels createShadow(ARGBPixels srcImage,
				ARGBPixels destImage, Color shadowColor) {
			Color opaqueShadowColor = new Color(shadowColor.getRed(),
					shadowColor.getGreen(), shadowColor.getBlue(), 255);

			int dstWidth = srcImage.getWidth() + 2 * radiiSum;
			int dstHeight = srcImage.getHeight() + 2 * radiiSum;

			if (destImage == null) {
				destImage = new ARGBPixels(dstWidth, dstHeight);
			} else {
				if (destImage.getWidth() < dstWidth)
					throw new IllegalArgumentException(
							"The destination width (" + destImage.getWidth()
									+ ") must be " + dstWidth + " or greater");
				if (destImage.getHeight() < dstHeight)
					throw new IllegalArgumentException(
							"The destination height (" + destImage.getHeight()
									+ ") must be " + dstHeight + " or greater");
			}

			BoxShadowRenderer r = new BoxShadowRenderer();
			int x = radiiSum;
			int y = radiiSum;
			int width = srcImage.getWidth();
			int height = srcImage.getHeight();

			for (int a = 0; a < sortedRadii.size(); a++) {
				Color currentShadowColor = opaqueShadowColor;
				if (a == sortedRadii.size() - 1)
					currentShadowColor = shadowColor;

				if (a == 0) {
					r.createShadow(srcImage, destImage, x, y,
							sortedRadii.get(a), currentShadowColor);
				} else {
					r.applyShadow(destImage, x, y, width, height,
							sortedRadii.get(a), currentShadowColor);
				}

				int p = (int) (Math.ceil(sortedRadii.get(a)) + .5);

				x -= p;
				y -= p;
				width += 2 * p;
				height += 2 * p;
			}

			return destImage;
		}
	}

	@Override
	public ARGBPixels createShadow(ARGBPixels srcImage, ARGBPixels destImage,
			float kernelRadius, Color shadowColor) {

		Combo combo = getCombo(kernelRadius);
		if (combo == null) {
			GaussianShadowRenderer r = new GaussianShadowRenderer();
			return r.createShadow(srcImage, destImage, kernelRadius,
					shadowColor);
		}

		return combo.createShadow(srcImage, destImage, shadowColor);
	}

	private Combo getCombo(float kernelRadius) {
		Map.Entry<Number, Combo> floorEntry = lookupTable
				.floorEntry(kernelRadius);
		if (floorEntry == null) {
			return null;
		}
		Map.Entry<Number, Combo> ceilEntry = lookupTable
				.ceilingEntry(kernelRadius);
		if (ceilEntry == null) {
			return null;
		}
		return floorEntry.getValue();
	}

	@Override
	public GaussianKernel getKernel(float kernelRadius) {

		Combo combo = getCombo(kernelRadius);
		if (combo == null) {
			GaussianShadowRenderer r = new GaussianShadowRenderer();
			return r.getKernel(kernelRadius);
		}

		double[] kernel = createCombinedKernel(combo);
		int[] intKernel = new int[kernel.length];
		for (int a = 0; a < kernel.length; a++) {
			intKernel[a] = (int) (0x10000 * kernel[a]);
		}
		return new GaussianKernel(intKernel);
	}

	private double[] createCombinedKernel(Combo combo) {
		double[] t = new double[] { 1 };
		for (float fastKernelRadius : combo.sortedRadii) {
			double[] k = createSingleKernel(fastKernelRadius);
			t = distributeKernel(t, k);
		}
		return t;
	}

	private double[] distributeKernel(double[] data, double[] kernel) {
		int r = (kernel.length - 1) / 2;
		double[] returnValue = new double[data.length + 2 * r];

		for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {
			for (int kernelIndex = 0; kernelIndex < kernel.length; kernelIndex++) {
				returnValue[dataIndex + kernelIndex] += data[dataIndex]
						* kernel[kernelIndex];
			}
		}

		return returnValue;
	}

	private double[] createSingleKernel(float fastKernelRadius) {
		BoxShadowRenderer r = new BoxShadowRenderer();
		GaussianKernel k = r.getKernel(fastKernelRadius);
		int[] z = k.getArray();
		double sum = 0;
		for (int e : z) {
			sum += e;
		}
		double[] returnValue = new double[z.length];
		for (int a = 0; a < z.length; a++) {
			returnValue[a] = ((double) z[a]) / sum;
		}
		return returnValue;
	}
}