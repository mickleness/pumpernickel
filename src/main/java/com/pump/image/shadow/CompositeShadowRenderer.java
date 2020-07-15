package com.pump.image.shadow;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This mimics a {@link GaussianShadowRenderer} by applying two iterations
 * of a {@link FastShadowRenderer}. This class is preloaded with information
 * about how to best combine FastShadowRenderers to resemble a
 * GaussianShadowRenderer for kernel radii of up to 100 pixels.
 * <p>
 * Warning: the kernel returned by {@link #getKernel(ShadowAttributes)} is an
 * approximation. No single kernel will exactly describe the effects of this renderer.
 */
public class CompositeShadowRenderer implements ShadowRenderer {

	static TreeMap<Number, Combo> lookupTable = new TreeMap<>();

	static {
		try(InputStream in = CompositeShadowRenderer.class.getResourceAsStream("CompositeShadowRendererTable.csv")) {
			try(BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
				String s = br.readLine();
				while(s!=null) {
					String[] terms = s.split(",");
					Number radius= Float.parseFloat(terms[0]);
					Combo combo = new Combo( Float.parseFloat(terms[1]), Float.parseFloat(terms[2]));
					lookupTable.put(radius, combo);
					
					s = br.readLine();
				}
			}
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception e) {
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
				radiiSum += (int)(Math.ceil(radius)+.5);

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

		public ARGBPixels createShadow(ARGBPixels srcImage, ARGBPixels destImage, float shadowOpacity) {

			
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

			FastShadowRenderer r = new FastShadowRenderer();
			int x = radiiSum;
			int y = radiiSum;
			int width = srcImage.getWidth();
			int height = srcImage.getHeight();

			for (int a = 0; a < sortedRadii.size(); a++) {
				ShadowAttributes attr2 = new ShadowAttributes(
						sortedRadii.get(a), 1);
				if (a == sortedRadii.size() - 1)
					attr2.setShadowOpacity(shadowOpacity);

				if (a == 0) {
					r.createShadow(srcImage, destImage, x, y, attr2);
				} else {
					r.applyShadow(destImage, x, y, width, height, attr2);
				}

				int p = (int)(Math.ceil(sortedRadii.get(a)) + .5);
				
				x -= p;
				y -= p;
				width += 2*p;
				height += 2*p;
			}

			return destImage;
		}
	}

	@Override
	public ARGBPixels createShadow(ARGBPixels srcImage, ARGBPixels destImage,
			ShadowAttributes attr) {
		float kernelRadius = attr.getShadowKernelRadius();
		
		Map.Entry<Number, Combo> entry = lookupTable.floorEntry(kernelRadius);
		if (entry==null) {
			GaussianShadowRenderer r = new GaussianShadowRenderer();
			return r.createShadow(srcImage, destImage, attr);
		}

		Combo combo = entry.getValue();

		return combo.createShadow(srcImage, destImage, attr.getShadowOpacity());
	}

	@Override
	public GaussianKernel getKernel(ShadowAttributes attr) {
		float kernelRadius = attr.getShadowKernelRadius();
		
		Map.Entry<Number, Combo> entry = lookupTable.floorEntry(kernelRadius);
		if (entry==null) {
			GaussianShadowRenderer r = new GaussianShadowRenderer();
			return r.getKernel(attr);
		}
		
		Combo combo = entry.getValue();

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
		FastShadowRenderer r = new FastShadowRenderer();
		ShadowAttributes attr = new ShadowAttributes(fastKernelRadius, 1f);
		GaussianKernel k = r.getKernel(attr);
		int[] z = k.getArray();
		double sum = 0;
		for(int e : z) {
			sum += e;
		}
		double[] returnValue = new double[z.length];
		for(int a = 0; a<z.length; a++) {
			returnValue[a] = ((double)z[a])/sum;
		}
		return returnValue;
	}
}