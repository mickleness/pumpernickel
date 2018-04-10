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
package com.pump.awt;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.HashMap;

import com.pump.blog.Blurb;
import com.pump.math.MathG;

/**
 * A linear gradient based on a <code>java.awt.TexturePaint</code>.
 * <p>
 * This goes to extra lengths to reduce color banding more than Java's default
 * gradient implementation, and is generally comparable in performance.
 * 
 * @see <a
 *      href="http://javagraphics.blogspot.com/2014/03/gradients-avoiding-color-banding.html">Gradients:
 *      Avoiding Color Banding</a>
 * @see <a
 *      href="http://javagraphics.blogspot.com/2009/11/gradients-boring-discussion.html">Gradients:
 *      a Boring Discussion</a>
 */
@Blurb(title = "Gradients: Avoiding Color Banding", releaseDate = "March 2014", summary = "This article explores the problem of color banding in linear gradients and presents a partial solution.", article = "http://javagraphics.blogspot.com/2014/03/gradients-avoiding-color-banding.html")
public class GradientTexturePaint implements Paint {

	/**
	 * This is an advanced option only used for testing/debugging.
	 * 
	 */
	protected static boolean seedingEnabled = true;

	public static enum Cycle {
		LOOP, NONE, TILE
	};

	/** This applies error diffusion to a gradient. */
	static class DiffusedTextureSource extends TextureSource {
		DiffusedTextureSource(Color[] colors, float[] fractions, double distance) {
			boundsCheck(colors, fractions);

			int[] argb = new int[colors.length];

			for (int a = 0; a < argb.length; a++) {
				argb[a] = colors[a].getRGB();
			}

			int size = MathG.ceilInt(distance);
			boolean hasAlpha = containsAlpha(colors);

			// express all channels as a float, we'll apply diffusion later:
			float[] alpha, red, green, blue;
			alpha = new float[size];
			red = new float[size];
			green = new float[size];
			blue = new float[size];

			int firstR = (argb[0] >> 16) & 0xff;
			int firstG = (argb[0] >> 8) & 0xff;
			int firstB = (argb[0] >> 0) & 0xff;
			int lastR = (argb[argb.length - 1] >> 16) & 0xff;
			int lastG = (argb[argb.length - 1] >> 8) & 0xff;
			int lastB = (argb[argb.length - 1] >> 0) & 0xff;
			int firstA, lastA;
			if (hasAlpha) {
				firstA = (argb[0] >> 24) & 0xff;
				lastA = (argb[0] >> 24) & 0xff;
			} else {
				firstA = 255;
				lastA = 255;
			}

			/**
			 * The width (in pixels) of the image we used to render this
			 * gradient. This isn't an exact science, but a small value (of less
			 * than 10) can easily reveal to the human eye where the tiling
			 * repeats, so I want to try to make that less visible
			 */
			int depth = 16;

			float[][] alphaE, redE, greenE, blueE;
			alphaE = new float[size][depth];
			redE = new float[size][depth];
			greenE = new float[size][depth];
			blueE = new float[size][depth];

			for (int x = 0; x < size; x++) {
				float f = (x) / (size - 1f);
				if (f <= fractions[0]) {
					red[x] = firstR;
					green[x] = firstG;
					blue[x] = firstB;
					alpha[x] = firstA;
				} else if (f >= fractions[fractions.length - 1]) {
					red[x] = lastR;
					green[x] = lastG;
					blue[x] = lastB;
					alpha[x] = lastA;
				} else {
					search: {
						for (int a = 0; a < fractions.length - 1; a++) {
							int a1, a2;
							if (hasAlpha) {
								a1 = (argb[a] >> 24) & 0xff;
								a2 = (argb[a + 1] >> 24) & 0xff;
							} else {
								a1 = 255;
								a2 = 255;
							}
							int r1 = (argb[a] >> 16) & 0xff;
							int g1 = (argb[a] >> 8) & 0xff;
							int b1 = (argb[a] & 0xff);
							int r2 = (argb[a + 1] >> 16) & 0xff;
							int g2 = (argb[a + 1] >> 8) & 0xff;
							int b2 = (argb[a + 1] & 0xff);
							if (f >= fractions[a] && f <= fractions[a + 1]) {
								float rel = (f - fractions[a])
										/ (fractions[a + 1] - fractions[a]);
								red[x] = r1 * (1 - rel) + r2 * rel;
								green[x] = g1 * (1 - rel) + g2 * rel;
								blue[x] = b1 * (1 - rel) + b2 * rel;
								alpha[x] = a1 * (1 - rel) + a2 * rel;
								break search;
							}
						}
						red[x] = lastR;
						green[x] = lastG;
						blue[x] = lastB;
						alpha[x] = lastA;
					}
				}
			}

			int[][] argbQ = new int[size][depth];

			// seed the array:
			int[][] kernel = new int[][] { { 1, 3, 5, 3, 1 },
					{ 3, 5, 7, 5, 3 }, { 5, 7, 0, 7, 5 }, { 3, 5, 7, 5, 3 },
					{ 1, 3, 5, 3, 1 } };
			double redCarryover = 0;
			double greenCarryover = 0;
			double blueCarryover = 0;
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < depth; x++) {
					if (isSeed(x, y)) {
						int r = (int) (red[y]);
						int g = (int) (green[y]);
						int b = (int) (blue[y]);
						int a = (int) (alpha[y]);

						redCarryover += Math.sqrt(red[y] - r);
						greenCarryover += Math.sqrt(green[y] - g);
						blueCarryover += Math.sqrt(blue[y] - b);
						int rk = (int) redCarryover;
						int gk = (int) greenCarryover;
						int bk = (int) blueCarryover;
						r += rk;
						g += gk;
						b += bk;
						redCarryover -= rk;
						greenCarryover -= gk;
						blueCarryover -= bk;

						if (r > 255)
							r = 255;
						if (g > 255)
							g = 255;
						if (b > 255)
							b = 255;

						argbQ[y][x] = (a << 24) + (r << 16) + (g << 8) + b;

						float newRedError = r - red[y];
						float newGreenError = g - green[y];
						float newBlueError = b - blue[y];
						float newAlphaError = a - alpha[y];
						float localSum = 0;
						for (int i = 0; i < kernel.length; i++) {
							for (int j = 0; j < kernel[i].length; j++) {
								int y2 = (y + i - kernel.length / 2 + size)
										% size;
								int x2 = (x + j - kernel[0].length / 2 + depth)
										% depth;
								if (!isSeed(x2, y2)) {
									localSum += kernel[i][j];
								}
							}
						}
						for (int i = 0; i < kernel.length; i++) {
							for (int j = 0; j < kernel[i].length; j++) {
								int y2 = (y + i - kernel.length / 2 + size)
										% size;
								int x2 = (x + j - kernel[0].length / 2 + depth)
										% depth;
								if (!isSeed(x2, y2)) {
									redE[y2][x2] -= newRedError * kernel[i][j]
											/ localSum;
									greenE[y2][x2] -= newGreenError
											* kernel[i][j] / localSum;
									blueE[y2][x2] -= newBlueError
											* kernel[i][j] / localSum;
									alphaE[y2][x2] -= newAlphaError
											* kernel[i][j] / localSum;
								}
							}
						}
					}
				}
			}

			/**
			 * Use a 3x5 kernel (Bell Labs) for error diffusion:
			 * 
			 * <pre>
			 * [ [ 0, 0, 0, 7, 5],
			 *  [ 3, 5, 7, 5, 3] ]
			 *  [ 1, 3, 5, 3, 1] ]
			 * </pre>
			 */
			kernel = new int[][] { { 0, 0, 0, 7, 5 }, { 3, 5, 7, 5, 3 },
					{ 1, 3, 5, 3, 1 } };
			float kernelSum = getKernelSum(kernel);

			for (int y = 0; y < size; y++) {
				for (int x = 0; x < depth; x++) {
					if (!isSeed(x, y)) {
						int r = (int) (red[y] + redE[y][x]);
						int g = (int) (green[y] + greenE[y][x]);
						int b = (int) (blue[y] + blueE[y][x]);
						int a = (int) (alpha[y] + alphaE[y][x]);
						if (r > 255)
							r = 255;
						if (r < 0)
							r = 0;
						if (g > 255)
							g = 255;
						if (g < 0)
							g = 0;
						if (b > 255)
							b = 255;
						if (b < 0)
							b = 0;
						if (a > 255)
							a = 255;
						if (a < 0)
							a = 0;
						argbQ[y][x] = (a << 24) + (r << 16) + (g << 8) + b;

						float newRedError = r - red[y];
						float newGreenError = g - green[y];
						float newBlueError = b - blue[y];
						float newAlphaError = a - alpha[y];
						for (int i = 0; i < kernel.length; i++) {
							for (int j = 0; j < kernel[i].length; j++) {
								redE[(y + i) % size][(x + j - kernel[0].length
										/ 2 + depth)
										% depth] -= newRedError * kernel[i][j]
										/ kernelSum;
								greenE[(y + i) % size][(x + j
										- kernel[0].length / 2 + depth)
										% depth] -= newGreenError
										* kernel[i][j] / kernelSum;
								blueE[(y + i) % size][(x + j - kernel[0].length
										/ 2 + depth)
										% depth] -= newBlueError * kernel[i][j]
										/ kernelSum;
								alphaE[(y + i) % size][(x + j
										- kernel[0].length / 2 + depth)
										% depth] -= newAlphaError
										* kernel[i][j] / kernelSum;
							}
						}
					}
				}
			}

			BufferedImage image = new BufferedImage(size, depth,
					BufferedImage.TYPE_INT_ARGB);
			for (int x = 0; x < size; x++) {
				image.getRaster().setDataElements(x, 0, 1, depth, argbQ[x]);
			}
			texturePaint = new TexturePaint(image, new Rectangle(0, 0, size,
					depth));
		}

		private static boolean isSeed(int x, int y) {
			if (!seedingEnabled)
				return false;
			return x % 2 == 0 && y % 2 == 0;
		}

		private static int getKernelSum(int[][] kernel) {
			int kernelSum = 0;
			for (int[] i : kernel) {
				for (int j : i) {
					kernelSum += j;
				}
			}
			return kernelSum;
		}
	}

	/**
	 * This is the simplest texture source, but it may introduce color banding
	 * if similar colors are stretched over a long distance.
	 */
	static class SimpleTextureSource extends TextureSource {
		SimpleTextureSource(Color[] colors, float[] fractions, double distance) {
			int[] argb = new int[colors.length];

			for (int a = 0; a < argb.length; a++) {
				argb[a] = colors[a].getRGB();
			}

			int size = MathG.ceilInt(distance);
			int[] row = new int[size];
			boolean hasAlpha = containsAlpha(colors);
			int imageType = hasAlpha ? BufferedImage.TYPE_INT_ARGB
					: BufferedImage.TYPE_INT_RGB;
			BufferedImage image = new BufferedImage(row.length, 1, imageType);
			for (int x = 0; x < row.length; x++) {
				float f = (x) / (row.length - 1f);
				if (f <= fractions[0]) {
					row[x] = argb[0];
				} else if (f >= fractions[fractions.length - 1]) {
					row[x] = argb[colors.length - 1];
				} else {
					search: {
						for (int a = 0; a < fractions.length - 1; a++) {
							int a1 = (argb[a] >> 24) & 0xff;
							int r1 = (argb[a] >> 16) & 0xff;
							int g1 = (argb[a] >> 8) & 0xff;
							int b1 = (argb[a] & 0xff);
							int a2 = (argb[a + 1] >> 24) & 0xff;
							int r2 = (argb[a + 1] >> 16) & 0xff;
							int g2 = (argb[a + 1] >> 8) & 0xff;
							int b2 = (argb[a + 1] & 0xff);
							if (f >= fractions[a] && f <= fractions[a + 1]) {
								float rel = (f - fractions[a])
										/ (fractions[a + 1] - fractions[a]);
								row[x] = ((int) (a1 * (1 - rel) + a2 * rel) << 24)
										+ ((int) (r1 * (1 - rel) + r2 * rel) << 16)
										+ ((int) (g1 * (1 - rel) + g2 * rel) << 8)
										+ ((int) (b1 * (1 - rel) + b2 * rel));
								break search;
							}
						}
						row[x] = argb[colors.length - 1];
					}
				}
			}
			image.getRaster().setDataElements(0, 0, size, 1, row);
			texturePaint = new TexturePaint(image, new Rectangle(0, 0, size, 1));
		}
	}

	/**
	 * An abstract supplier for the TexturePaint this gradient delegates to.
	 */
	static abstract class TextureSource {
		TexturePaint texturePaint;
	}

	/**
	 * Make sure that colors and fractions are teh same size, that fractions is
	 * sorted and within [0,1]
	 * 
	 * @param colors
	 * @param fractions
	 */
	private static void boundsCheck(Color[] colors, float[] fractions) {
		if (colors.length != fractions.length)
			throw new IllegalArgumentException("The size of the colors array ("
					+ colors.length
					+ ") must equal the size of the fractions array ("
					+ fractions.length + ")");
		for (int a = 0; a < fractions.length; a++) {
			if (fractions[a] < 0 || fractions[a] > 1)
				throw new IllegalArgumentException(
						"At least one fractional value was not between [0,1]. (fractions["
								+ a + "] = " + fractions[a] + ")");
			if (a != 0) {
				if (fractions[a - 1] > fractions[a])
					throw new IllegalArgumentException(
							"At least one fractional value was less than the previous value. (fractions["
									+ (a - 1) + "] = " + fractions[a - 1]
									+ ", fractions[" + (a) + "] = "
									+ fractions[a] + ")");
			}
		}
	}

	/** Clone an array. */
	private static Color[] clone(Color[] c) {
		Color[] copy = new Color[c.length];
		System.arraycopy(c, 0, copy, 0, c.length);
		return copy;
	}

	/** Clone an array. */
	private static float[] clone(float[] c) {
		float[] copy = new float[c.length];
		System.arraycopy(c, 0, copy, 0, c.length);
		return copy;
	}

	/**
	 * Return true if any of these colors are not opaque.
	 * 
	 * @param colors
	 *            the colors to check for alpha components
	 * @return true if any of the colors in the argument are not fully opaque.
	 */
	protected static boolean containsAlpha(Color[] colors) {
		for (int a = 0; a < colors.length; a++) {
			int argb = colors[a].getRGB();
			int alpha = (argb & 0xff000000) >> 24;
			if (alpha != 255) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return true if this combination of colors and fractions should apply
	 * error diffusion prevent <a
	 * href="http://en.wikipedia.org/wiki/Colour_banding">color banding</a>.
	 * 
	 * @param colors
	 *            the colors to use in this gradient.
	 * @param fractions
	 *            the fractions to use in this gradient. These must be ascending
	 *            fractions from [0,1]. Each fraction corresponds to a color.
	 * @param x1
	 *            the x-value of the first coordinate.
	 * @param y1
	 *            the y-value of the first coordinate.
	 * @param x2
	 *            the x-value of the second coordinate.
	 * @param y2
	 *            the y-value of the second coordinate.
	 * @return true if the gradient the arguments described should apply error
	 *         diffusion to prevent color banding.
	 * 
	 * @see <a
	 *      href="http://javagraphics.blogspot.com/2014/03/gradients-avoiding-color-banding.html">Gradients:
	 *      Avoiding Color Banding</a>
	 */
	public static boolean needsDiffusion(Color[] colors, float[] fractions,
			float x1, float y1, float x2, float y2) {
		boundsCheck(colors, fractions);
		double totalDistance = Point2D.distance(x1, y1, x2, y2);
		for (int a = 0; a < colors.length - 1; a++) {
			double fractionRange = (fractions[a + 1] - fractions[a]);
			double segmentDistance = fractionRange * totalDistance;

			int r1 = colors[a].getRed();
			int g1 = colors[a].getGreen();
			int b1 = colors[a].getBlue();
			int r2 = colors[a + 1].getRed();
			int g2 = colors[a + 1].getGreen();
			int b2 = colors[a + 1].getBlue();
			int redDelta = Math.abs(r1 - r2);
			int greenDelta = Math.abs(g1 - g2);
			int blueDelta = Math.abs(b1 - b2);
			int maxDelta = Math.max(redDelta, Math.max(greenDelta, blueDelta));

			if (segmentDistance > maxDelta)
				return true;
		}
		return false;
	}

	protected Color[] colors;

	protected Cycle cycle;

	protected float[] fractions;

	protected boolean hasAlpha = false;

	protected boolean needsDiffusion;

	protected AffineTransform transform;

	protected float x1, y1, x2, y2;

	transient SimpleTextureSource simpleSource;
	transient DiffusedTextureSource diffusedSource;

	/**
	 * Create a 2-color gradient using <code>Cycle.NONE</code>.
	 * 
	 * @param x1
	 *            the x-coordinate of first point
	 * @param y1
	 *            the y-coordinate of first point
	 * @param c1
	 *            the first color
	 * @param x2
	 *            the x-coordinate of second point
	 * @param y2
	 *            the y-coordinate of second point
	 * @param c2
	 *            the second color
	 */
	public GradientTexturePaint(float x1, float y1, Color c1, float x2,
			float y2, Color c2) {
		this(new Color[] { c1, c2 }, new float[] { 0, 1 }, x1, y1, x2, y2,
				Cycle.NONE);
	}

	/**
	 * Create a 2-color gradient using <code>Cycle.NONE</code>.
	 * 
	 * @param p1
	 *            the first point
	 * @param c1
	 *            the first color
	 * @param x2
	 *            the second point
	 * @param c2
	 *            the second color
	 */
	public GradientTexturePaint(Point2D p1, Color c1, Point2D p2, Color c2) {
		this(new Color[] { c1, c2 }, new float[] { 0, 1 }, (float) p1.getX(),
				(float) p1.getY(), (float) p2.getX(), (float) p2.getY(),
				Cycle.NONE);
	}

	/**
	 * Creates a new <code>GradientTexturePaint</code>.
	 * 
	 * @param colors
	 *            the colors to use in this gradient.
	 * @param fractions
	 *            the fractions to use in this gradient. These must be ascending
	 *            fractions from [0,1]. Each fraction corresponds to a color.
	 * @param x1
	 *            the x-value of the first coordinate.
	 * @param y1
	 *            the y-value of the first coordinate.
	 * @param x2
	 *            the x-value of the second coordinate.
	 * @param y2
	 *            the y-value of the second coordinate.
	 * @param cycle
	 *            TILE is recommended for best performance.
	 */
	public GradientTexturePaint(Color[] colors, float[] fractions, float x1,
			float y1, float x2, float y2, Cycle cycle) {
		boundsCheck(colors, fractions);

		this.cycle = cycle;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;

		float dy = y2 - y1;
		float dx = x2 - x1;
		float distance = (float) Math.sqrt(dx * dx + dy * dy);
		float angle = (float) Math.atan2(dy, dx);

		hasAlpha = containsAlpha(colors);
		needsDiffusion = needsDiffusion(colors, fractions, x1, y1, x2, y2);

		// handle looping:

		if (cycle == Cycle.LOOP) {
			distance = 2 * distance;
			x2 = (float) (x1 + distance * Math.cos(angle));
			y2 = (float) (y1 + distance * Math.sin(angle));
			int newArraySize = colors.length * 2
					- (fractions[fractions.length - 1] < 1 ? 0 : 1);
			Color[] cycledColors = new Color[newArraySize];
			float[] cycledFractions = new float[newArraySize];
			for (int a = 0; a < colors.length; a++) {
				cycledColors[a] = colors[a];
				cycledColors[cycledColors.length - 1 - a] = colors[a];
				cycledFractions[a] = fractions[a] * .5f;
				cycledFractions[cycledFractions.length - 1 - a] = (.5f - fractions[a] * .5f) + .5f;
			}
			colors = cycledColors;
			fractions = cycledFractions;
		}

		this.colors = clone(colors);
		this.fractions = clone(fractions);

		transform = new AffineTransform();
		transform.translate(x1, y1);
		transform.rotate(angle);
	}

	/**
	 * Creates a new <code>GradientTexturePaint</code>.
	 * 
	 * @param colors
	 *            the colors to use in this gradient.
	 * @param fractions
	 *            the fractions to use in this gradient. These must be ascending
	 *            fractions from [0,1]. Each fraction corresponds to a color.
	 * @param p1
	 *            the first point.
	 * @param p2
	 *            the second point.
	 * @param cycle
	 *            TILE is recommended for best performance.
	 */
	public GradientTexturePaint(Color[] colors, float[] fractions, Point2D p1,
			Point2D p2, Cycle cycle) {
		this(colors, fractions, (float) p1.getX(), (float) p1.getY(),
				(float) p2.getX(), (float) p2.getY(), cycle);
	}

	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
			Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {

		// this is necessary on Java 1.4 to avoid a NullPointerException
		// from:
		// java.awt.TexturePaintContext.getContext(TexturePaintContext.java:57)
		if (hints == null)
			hints = new RenderingHints(
					new HashMap<RenderingHints.Key, Object>());

		boolean useDiffusion = RenderingHints.VALUE_DITHER_ENABLE.equals(hints
				.get(RenderingHints.KEY_DITHERING))
				|| RenderingHints.VALUE_DITHER_DEFAULT.equals(hints
						.get(RenderingHints.KEY_DITHERING))
				|| RenderingHints.VALUE_COLOR_RENDER_QUALITY.equals(hints
						.get(RenderingHints.KEY_COLOR_RENDERING))
				|| RenderingHints.VALUE_RENDER_QUALITY.equals(hints
						.get(RenderingHints.KEY_RENDERING));

		TextureSource source;
		if (useDiffusion && needsDiffusion) {
			if (diffusedSource == null)
				diffusedSource = new DiffusedTextureSource(colors, fractions,
						Point2D.distance(x1, y1, x2, y2));
			source = diffusedSource;
		} else {
			if (simpleSource == null)
				simpleSource = new SimpleTextureSource(colors, fractions,
						Point2D.distance(x1, y1, x2, y2));
			source = simpleSource;
		}

		AffineTransform newTransform = new AffineTransform(xform);
		newTransform.concatenate(transform);

		/**
		 * From what I can tell: The edges will appear much less pixelated if
		 * the interpolation is set to BILINEAR or BICUBIC. Because this is
		 * probably what the developer has in mind when (s)he turns on
		 * antialiasing, we'll make that switch here for them.
		 */
		if (RenderingHints.VALUE_ANTIALIAS_ON.equals(hints
				.get(RenderingHints.KEY_ANTIALIASING))) {
			// I'm not sure if "hints" is a shared object or if we should make a
			// clone.
			// To be safe, clone it:
			RenderingHints copy = new RenderingHints(
					RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			copy.putAll(hints);
			hints = copy;
			hints.put(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}

		PaintContext context = source.texturePaint.createContext(cm,
				deviceBounds, userBounds, newTransform, hints);
		if (cycle == Cycle.NONE) {
			context = new CoveredContext(context, x1, y1, colors[0].getRGB(),
					x2, y2, colors[colors.length - 1].getRGB(), xform);
		}
		return context;
	}

	/**
	 * Return the colors in this gradient. Each color corresponds to a value in
	 * {link #getFractions()}.
	 * 
	 * @return the colors in this gradient.
	 */
	public Color[] getColors() {
		return clone(colors);
	}

	/**
	 * Return the fractions in this gradient. Each fraction corresponds to a
	 * value in {link #getColors()}.
	 * 
	 * @return the fractions in this gradient.
	 */
	public float[] getFractions() {
		return clone(fractions);
	}

	/**
	 * Return <code>Transparency.OPAQUE</code> if all the colors in this
	 * gradient are opaque, otherwise this returns
	 * <code>Transparency.TRANSLUCENT</code>.
	 * 
	 * @return the transparency of this gradient.
	 */
	public int getTransparency() {
		return hasAlpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE;
	}
}