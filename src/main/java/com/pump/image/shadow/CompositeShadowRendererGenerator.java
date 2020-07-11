package com.pump.image.shadow;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import com.pump.image.shadow.CompositeShadowRenderer.Combo;
import com.pump.util.Cache;

/**
 * This generates the lookup table info for the CompositeShadowRenderer.
 * <p>
 * There's probably a lot that can be optimized here, but this is only intended
 * to be run once to generate the tables. This is not a public class.
 * <p>
 * (Also this generates a lot of files to help visually confirm the results.)
 */
class CompositeShadowRendererGenerator {

	static final int MAX = 120;

	static Cache<Integer, BufferedImage> blockCache = new Cache<>(2000);
	static Cache<String, BufferedImage> gaussianCache = new Cache<>(2000);

	private static GaussianShadowRenderer gaussianRenderer = new GaussianShadowRenderer();

	private static Callable evaluate(final Comparison[] comparisons,
			final int... radii) {
		return new Callable() {

			@Override
			public Object call() throws Exception {
				int sum = 0;
				for (int radius : radii) {
					sum += radius;
				}
				if (sum > 0) {
					Combo c = new Combo(radii);
					BufferedImage block;
					synchronized (blockCache) {
						block = blockCache.get(c.radiiSum);
						if (block == null) {
							block = new BufferedImage(c.radiiSum, c.radiiSum,
									BufferedImage.TYPE_INT_RGB);
							blockCache.put(c.radiiSum, block);
						}
					}

					int ctr = 0;
					int maxK = Math.min(MAX, 10 * c.radiiSum);
					for (float k = 1; k <= maxK; k += .1f, ctr++) {
						String key = c.radiiSum + "-" + k;
						BufferedImage t;
						synchronized (gaussianCache) {
							t = gaussianCache.get(key);
							if (t == null) {
								ShadowAttributes attr = new ShadowAttributes(k,
										1);
								t = gaussianRenderer.createShadow(block, attr);
								gaussianCache.put(key, t);
							}
						}
						Comparison comparison = new Comparison(c, t, k);
						if (comparisons[ctr] == null
								|| comparison.error < comparisons[ctr].error) {
							comparisons[ctr] = comparison;
						}
					}
				}
				return Void.TYPE;
			}

		};

	}

	static WeakHashMap<Combo, ARGBPixels> comboPixels = new WeakHashMap<>();

	private static ARGBPixels getPixels(Combo c) {
		ARGBPixels returnValue = comboPixels.get(c);
		if (returnValue == null) {
			returnValue = new ARGBPixels(3 * c.radiiSum + 1,
					3 * c.radiiSum + 1);

			FastShadowRenderer r = new FastShadowRenderer();
			int x = c.radiiSum;
			int y = c.radiiSum;

			for (int z1 = 0; z1 < c.radiiSum; z1++) {
				for (int z2 = 0; z2 < c.radiiSum; z2++) {
					returnValue.getPixels()[(y + z2) * returnValue.getWidth()
							+ (x + z1)] = 0xff000000;
				}
			}

			int w = c.radiiSum;
			int h = c.radiiSum;

			for (Integer radius : c.sortedRadii) {
				ShadowAttributes attr = new ShadowAttributes(radius, 1f);
				r.applyShadow(returnValue, x, y, w, h, attr);
				x -= radius;
				y -= radius;
				w += 2 * radius;
				h += 2 * radius;
			}

			comboPixels.put(c, returnValue);
		}
		return returnValue;
	}

	public static void main(String[] args) throws Exception {
		ExecutorService executor = Executors.newFixedThreadPool(10);

		{
			Comparison[] comparisonTwos = new Comparison[MAX * 10];
			Collection<Callable<?>> tasks = new HashSet<>();
			for (int j1 = 0; j1 < 33; j1++) {
				for (int j2 = j1; j2 < 33; j2++) {
					tasks.add(evaluate(comparisonTwos, j1, j2));
				}
			}
			executor.invokeAll(tasks);
			output(comparisonTwos, "lutTwos");
		}

		{
			Comparison[] comparisonThrees = new Comparison[MAX * 10];
			Collection<Callable<?>> tasks = new HashSet<>();
			for (int j1 = 0; j1 < 33; j1++) {
				for (int j2 = j1; j2 < 33; j2++) {
					for (int j3 = j2; j3 < 33; j3++) {
						tasks.add(evaluate(comparisonThrees, j1, j2, j3));
					}
				}
			}
			executor.invokeAll(tasks);
			output(comparisonThrees, "lutThrees");
		}

		{
			Comparison[] comparisonFours = new Comparison[MAX * 10];
			Collection<Callable<?>> tasks = new HashSet<>();
			for (int j1 = 0; j1 < 33; j1++) {
				for (int j2 = j1; j2 < 33; j2++) {
					for (int j3 = j2; j3 < 33; j3++) {
						for (int j4 = j3; j4 < 33; j4++) {
							tasks.add(
									evaluate(comparisonFours, j1, j2, j3, j4));
						}
					}
				}
			}
			executor.invokeAll(tasks);
			output(comparisonFours, "lutFours");
		}

		System.out.println("done");
	}

	private static void output(Comparison[] comparisons, String fieldName)
			throws Exception {
		DecimalFormat format = new DecimalFormat("#.0");
		for (Comparison c : comparisons) {
			if (c != null) {
				String name = "k" + c.gaussianRadius;
				ImageIO.write(c.gaussianImg, "png", new File(name + ".png"));
				ImageIO.write(getPixels(c.combo).createBufferedImage(), "png",
						new File(name + "-" + c.combo.sortedRadii + ".png"));

				StringBuilder sb = new StringBuilder();
				sb.append(fieldName + ".put( Float.valueOf("
						+ format.format(c.gaussianRadius) + "f), new Combo(");
				sb.append(c.combo.sortedRadii.get(0));
				for (int a = 1; a < c.combo.sortedRadii.size(); a++) {
					sb.append(", " + c.combo.sortedRadii.get(a));
				}
				sb.append("));");
				System.out.println(sb.toString());
			}
		}
	}

	static class Comparison {
		float gaussianRadius;
		RenderedImage gaussianImg;
		Combo combo;
		long error;

		public Comparison(Combo combo, BufferedImage gaussianImg,
				float gaussianRadius) {
			this.gaussianRadius = gaussianRadius;
			this.gaussianImg = gaussianImg;
			this.combo = combo;

			ARGBPixels comboPixels = getPixels(combo);
			error = getError(comboPixels.createBufferedImage(true),
					gaussianImg);
		}

		private long getError(BufferedImage bi1, BufferedImage bi2) {
			long sum = 0;

			int w1 = bi1.getWidth();
			int h1 = bi1.getHeight();
			int w2 = bi2.getWidth();
			int h2 = bi2.getHeight();

			int maxW = Math.max(w1, w2);
			int maxH = Math.max(h1, h2);

			int yHalf = maxH / 2;
			int xHalf = maxW / 2;
			int[] row1 = new int[bi1.getWidth()];
			int[] row2 = new int[bi2.getWidth()];
			for (int y = 0; y < yHalf; y++) {
				boolean row1Defined, row2Defined;

				int y1 = h1 / 2 + y;
				int y2 = h2 / 2 + y;
				if (y1 < bi1.getHeight()) {
					bi1.getRaster().getDataElements(0, y1, bi1.getWidth(), 1,
							row1);
					row1Defined = true;
				} else {
					row1Defined = false;
				}
				if (y2 < bi2.getHeight()) {
					bi2.getRaster().getDataElements(0, y2, bi2.getWidth(), 1,
							row2);
					row2Defined = true;
				} else {
					row2Defined = false;
				}
				for (int x = 0; x < xHalf; x++) {
					int argb1 = 0;
					int argb2 = 0;

					int x1 = w1 / 2 + x;
					int x2 = w2 / 2 + x;

					if (row1Defined && x1 < row1.length) {
						argb1 = row1[x1];
					}

					if (row2Defined && x2 < row2.length) {
						argb2 = row2[x2];
					}

					int a1 = (argb1 >> 24) & 0xff;
					int a2 = (argb2 >> 24) & 0xff;
					int error = a1 - a2;
					sum += error * error;
				}
			}
			return sum;
		}
	}
}
