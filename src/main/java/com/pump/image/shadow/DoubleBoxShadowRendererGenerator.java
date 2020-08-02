package com.pump.image.shadow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import com.pump.desktop.logging.SessionLog;
import com.pump.geom.StarPolygon;
import com.pump.geom.TransformUtils;
import com.pump.image.shadow.DoubleBoxShadowRenderer.Combo;

/**
 * This generates the lookup table info for the DoubleBoxShadowRenderer.
 * <p>
 * There's probably a lot that can be optimized here, but this is only intended
 * to be run once to generate the tables. This is not a public class.
 * <p>
 * (Also this generates a lot of files to help visually confirm the results.)
 */
class DoubleBoxShadowRendererGenerator
		implements Callable<DoubleBoxShadowRendererGenerator.Results> {
	private static GaussianShadowRenderer gaussianRenderer = new GaussianShadowRenderer();
	private static final Color SHADOW_COLOR = new Color(0, 0, 0, 192);

	private static final BufferedImage star = createStar();
	private static final ARGBPixels starPixels = new ARGBPixels(star, true);
	private static final BigDecimal gaussianIncr = BigDecimal.ONE
			.divide(BigDecimal.valueOf(10L));
	private static final BigDecimal gaussianMax = BigDecimal.valueOf(100);

	// this is arbitrary, the most we can support is 1/255
	private static final BigDecimal fastIncr = BigDecimal.ONE
			.divide(BigDecimal.valueOf(256));
	private static final BigDecimal fastMax = BigDecimal.valueOf(20);

	public static void main(String[] args) throws Exception {
		SessionLog.initialize("DoubleBoxShadowRenderer", 10);

		// I tried multithreading, but that led to memory errors. There's really
		// no hurry for this one-time cost, so single-thread seems simplest for
		// now:

		for (BigDecimal gaussianRadius = gaussianIncr; gaussianRadius
				.compareTo(gaussianMax) <= 0; gaussianRadius = gaussianRadius
						.add(gaussianIncr)) {
			DoubleBoxShadowRendererGenerator generator = new DoubleBoxShadowRendererGenerator(
					gaussianRadius);
			System.out.println(generator.call().output());
		}

		System.out.println("done");
		System.exit(0);
	}

	BigDecimal gaussianRadius;
	BufferedImage gaussianShadowImage;

	DoubleBoxShadowRendererGenerator(BigDecimal gaussianRadius) {
		this.gaussianRadius = gaussianRadius;
	}

	private static final DecimalFormat format = new DecimalFormat("#.0");

	static class Results implements Comparable<Results> {
		long error = Long.MAX_VALUE;
		Combo combo = null;
		ARGBPixels pixels = null;
		BigDecimal value, incr;
		BigDecimal gaussianRadius;
		BufferedImage gaussianShadowImage;

		public Results(long error, Combo combo, ARGBPixels pixels,
				BigDecimal value, BigDecimal incr, BigDecimal gaussianRadius,
				BufferedImage gaussianShadowImage) {
			this.error = error;
			this.combo = combo;
			this.pixels = pixels;
			this.value = value;
			this.incr = incr;
			this.gaussianRadius = gaussianRadius;
			this.gaussianShadowImage = gaussianShadowImage;
		}

		@Override
		public int compareTo(Results o) {
			return Long.compare(error, o.error);
		}

		String output() throws Exception {
			String name = "k" + gaussianRadius;
			ImageIO.write(gaussianShadowImage, "png", new File(name + ".png"));
			ImageIO.write(pixels.createBufferedImage(true), "png",
					new File(name + "-" + combo.sortedRadii + ".png"));

			StringBuilder sb = new StringBuilder();
			sb.append(format.format(gaussianRadius));
			sb.append("," + error);
			for (int a = 0; a < combo.sortedRadii.size(); a++) {
				sb.append("," + combo.sortedRadii.get(a));
			}

			return sb.toString();
		}
	}

	public Results call() {
		try {
			ShadowAttributes attr = new ShadowAttributes(0, 0,
					gaussianRadius.floatValue(), SHADOW_COLOR);
			gaussianShadowImage = gaussianRenderer.createShadow(star, attr);

			Results results = getBestFit(fastIncr, fastMax, BigDecimal.ONE);
			while (results.incr.compareTo(fastIncr) >= 0) {
				BigDecimal newIncr = results.incr.divide(new BigDecimal(2));
				results = getBestFit(
						results.value.subtract(
								results.incr.multiply(BigDecimal.valueOf(2))),
						results.value.add(
								results.incr.multiply(BigDecimal.valueOf(2))),
						newIncr);
			}

			return results;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	private Results getBestFit(BigDecimal min, BigDecimal max, BigDecimal incr)
			throws Exception {
		if (min.compareTo(fastIncr) <= 0)
			min = fastIncr;

		Results bestResults = null;
		for (BigDecimal v1 = min; v1.compareTo(max) <= 0; v1 = v1.add(incr)) {
			for (BigDecimal v2 = v1; v2.compareTo(max) <= 0; v2 = v2
					.add(incr)) {
				// for (BigDecimal v3 = v2; v3.compareTo(max) <= 0; v3 =
				// v3.add(incr)) {
				Combo combo = new Combo(v1.floatValue(), v2.floatValue()); // ),
																			// v3.floatValue());
				int width = 2 * combo.radiiSum + starPixels.getWidth();
				int height = 2 * combo.radiiSum + starPixels.getHeight();
				ARGBPixels shadowPixels = new ARGBPixels(width, height);
				combo.createShadow(starPixels, shadowPixels, SHADOW_COLOR);
				long error = getError(gaussianShadowImage,
						shadowPixels.createBufferedImage(true));
				Results results = new Results(error, combo, shadowPixels, v1,
						incr, gaussianRadius, gaussianShadowImage);
				if (bestResults == null || results.compareTo(bestResults) < 0) {
					bestResults = results;
				}
				// }
			}
		}

		return bestResults;
	}

	private static long getError(BufferedImage bi1, BufferedImage bi2) {
		long sum = 0;

		int w1 = bi1.getWidth();
		int h1 = bi1.getHeight();
		int w2 = bi2.getWidth();
		int h2 = bi2.getHeight();

		int maxW = Math.max(w1, w2);
		int maxH = Math.max(h1, h2);

		int dx1 = maxW / 2 - bi1.getWidth() / 2;
		int dy1 = maxH / 2 - bi1.getHeight() / 2;

		int dx2 = maxW / 2 - bi2.getWidth() / 2;
		int dy2 = maxH / 2 - bi2.getHeight() / 2;

		int[] row1 = new int[w1];
		int[] row2 = new int[w2];

		for (int y = 0; y < maxH; y++) {
			int y1 = y - dy1;
			int y2 = y - dy2;

			boolean row1valid;
			if (y1 >= 0 && y1 < h1) {
				bi1.getRaster().getDataElements(0, y1, w1, 1, row1);
				row1valid = true;
			} else {
				row1valid = false;
			}

			boolean row2valid;
			if (y2 >= 0 && y2 < h2) {
				bi2.getRaster().getDataElements(0, y2, w2, 1, row2);
				row2valid = true;
			} else {
				row2valid = false;
			}

			for (int x = 0; x < maxW; x++) {
				int x1 = x - dx1;
				int x2 = x - dx2;

				int argb1, argb2;

				if (row1valid && x1 >= 0 && x1 < w1) {
					argb1 = row1[x1];
				} else {
					argb1 = 0;
				}

				if (row2valid && x2 >= 0 && x2 < w2) {
					argb2 = row2[x2];
				} else {
					argb2 = 0;
				}

				int a1 = (argb1 >> 24) & 0xff;
				int a2 = (argb2 >> 24) & 0xff;
				int error = a1 - a2;
				sum += error * error;
			}
		}

		return sum;
	}

	/**
	 * Create an image of a star. This is the image we test shadows against.
	 */
	private static BufferedImage createStar() {
		BufferedImage starImage = new BufferedImage(400, 400,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = starImage.createGraphics();
		StarPolygon p = new StarPolygon(1);
		g.setColor(Color.black);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setTransform(TransformUtils.createAffineTransform(p.getBounds2D(),
				new Rectangle(10, 10, 380, 380)));
		g.fill(p);

		// punch a whole in the center:
		g.setComposite(AlphaComposite.Clear);
		AffineTransform tx = AffineTransform.getRotateInstance(.4, 200, 200);
		tx.concatenate(TransformUtils.createAffineTransform(p.getBounds2D(),
				new Rectangle(100, 100, 200, 200)));
		g.setTransform(tx);
		g.fill(p);

		g.dispose();
		return starImage;
	}
}
