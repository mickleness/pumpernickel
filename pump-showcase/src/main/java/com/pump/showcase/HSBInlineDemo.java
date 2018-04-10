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
package com.pump.showcase;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.util.Arrays;

import com.pump.util.JVM;

/**
 * This demonstrates the performance difference you see in a tight loop
 * iterating over pixels if you avoid making any external method calls.
 * <P>
 * On my computers (Mac OS 10.3.9 and Mac OS 10.4.9) I see about ta 20%
 * improvement in performance with the <code>filterQuickly</code> method than
 * with the <code>filterSlowly</code> method.
 * 
 */
public class HSBInlineDemo extends OutputDemo {

	private static final long serialVersionUID = 1L;

	public HSBInlineDemo() {
		super("Run...", false);
	}

	/**
	 * This shifts the hue of every pixel in src by calling Color.HSBtoRGB and
	 * Color.RGBtoHSB
	 */
	public static void filterWithoutInlining(BufferedImage src,
			BufferedImage dest, float hueShift) {
		int w = src.getWidth();
		int h = src.getHeight();
		int[] row = new int[w];
		int a, r, g, b, argb;
		float[] f = new float[3];
		for (int y = 0; y < h; y++) {
			src.getRaster().getDataElements(0, y, w, 1, row);
			for (int x = 0; x < w; x++) {
				argb = row[x];
				a = argb & 0xff000000;
				r = (argb >> 16) & 0xff;
				g = (argb >> 8) & 0xff;
				b = (argb >> 0) & 0xff;

				Color.RGBtoHSB(r, g, b, f);
				f[0] = (f[0] + hueShift) % 1;
				argb = Color.HSBtoRGB(f[0], f[1], f[2]);
				argb = (argb & 0xffffff) + a;
				row[x] = argb;
			}
			dest.getRaster().setDataElements(0, y, w, 1, row);
		}
	}

	/**
	 * This method does the same thing filterSlowly does, but it doesn't rely on
	 * any extra method calls. It shows about a 20% improvement in performance
	 * on my computer.
	 */
	public static void filterWithInlining(BufferedImage src,
			BufferedImage dest, float hueShift) {
		int w = src.getWidth();
		int h = src.getHeight();
		int[] row = new int[w];
		int a, r, g, b, argb;
		float hue, saturation, brightness;
		float redc, greenc, bluec;
		int cmax, cmin;
		float h2, f, p, q, t;
		for (int y = 0; y < h; y++) {
			src.getRaster().getDataElements(0, y, w, 1, row);
			for (int x = 0; x < w; x++) {
				argb = row[x];
				a = argb & 0xff000000;
				r = (argb >> 16) & 0xff;
				g = (argb >> 8) & 0xff;
				b = (argb >> 0) & 0xff;

				// this is copied and pasted from Color.RGBtoHSB:
				cmax = (r > g) ? r : g;
				if (b > cmax)
					cmax = b;
				cmin = (r < g) ? r : g;
				if (b < cmin)
					cmin = b;

				brightness = (cmax) / 255.0f;
				if (cmax != 0)
					saturation = ((float) (cmax - cmin)) / ((float) cmax);
				else
					saturation = 0;
				if (saturation == 0)
					hue = 0;
				else {
					redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
					greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
					bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
					if (r == cmax)
						hue = bluec - greenc;
					else if (g == cmax)
						hue = 2.0f + redc - bluec;
					else
						hue = 4.0f + greenc - redc;
					hue = hue / 6.0f;
					if (hue < 0)
						hue = hue + 1.0f;
				}
				// end of pasted code

				hue = (hue + hueShift) % 1;

				// the following is copied & pasted from Color.HSBtoRGB:
				if (saturation == 0) {
					r = g = b = (int) (brightness * 255.0f + 0.5f);
				} else {
					h2 = (hue - (int) hue) * 6.0f;
					f = h2 - (int) (h2);
					p = brightness * (1.0f - saturation);
					q = brightness * (1.0f - saturation * f);
					t = brightness * (1.0f - (saturation * (1.0f - f)));
					switch ((int) h2) {
					case 0:
						r = (int) (brightness * 255.0f + 0.5f);
						g = (int) (t * 255.0f + 0.5f);
						b = (int) (p * 255.0f + 0.5f);
						break;
					case 1:
						r = (int) (q * 255.0f + 0.5f);
						g = (int) (brightness * 255.0f + 0.5f);
						b = (int) (p * 255.0f + 0.5f);
						break;
					case 2:
						r = (int) (p * 255.0f + 0.5f);
						g = (int) (brightness * 255.0f + 0.5f);
						b = (int) (t * 255.0f + 0.5f);
						break;
					case 3:
						r = (int) (p * 255.0f + 0.5f);
						g = (int) (q * 255.0f + 0.5f);
						b = (int) (brightness * 255.0f + 0.5f);
						break;
					case 4:
						r = (int) (t * 255.0f + 0.5f);
						g = (int) (p * 255.0f + 0.5f);
						b = (int) (brightness * 255.0f + 0.5f);
						break;
					case 5:
						r = (int) (brightness * 255.0f + 0.5f);
						g = (int) (p * 255.0f + 0.5f);
						b = (int) (q * 255.0f + 0.5f);
						break;
					}
				}

				// end of pasted code

				argb = a + (r << 16) + (g << 8) + b;
				row[x] = argb;
			}
			dest.getRaster().setDataElements(0, y, w, 1, row);
		}
	}

	@Override
	public void run() {
		PrintStream out = console.createPrintStream(false);
		PrintStream err = console.createPrintStream(true);

		try {
			out.println(JVM.getProfile());
			out.println("\nThis creates a 4000x4000 pixel image of random pixels and then shifts the hue.");
			out.println("\nShifting the hue is traditionally done by calling Color.RGBtoHSB(..), and then");
			out.println("interacting with the HSB value of a pixel and converting it back to RGB values.");
			out.println("\nThis program tests what happens if instead of using the static call Color.RGBtoHSB(..)");
			out.println("we copy and paste that code into our loop. (That is: we're simply inlining the code and");
			out.println("removing a small amount overhead the JVM has to keep track of by entering a new method");
			out.println("16,000,000 times.)\n");
			int w = 4000;
			int h = 4000;
			BufferedImage src = new BufferedImage(w, h,
					BufferedImage.TYPE_INT_ARGB);
			int[] row = new int[src.getWidth()];
			for (int y = 0; y < src.getHeight(); y++) {
				for (int x = 0; x < src.getWidth(); x++) {
					row[x] = (int) ((0xffffffff) * Math.random());
				}
				src.getRaster().setDataElements(0, y, w, 1, row);
			}

			BufferedImage dest = new BufferedImage(src.getWidth(),
					src.getHeight(), BufferedImage.TYPE_INT_ARGB);
			long[] t1 = new long[5];
			long[] t2 = new long[t1.length];
			for (int a = 0; a < t1.length; a++) {
				long t = System.currentTimeMillis();
				filterWithoutInlining(src, dest, .5f);
				t = System.currentTimeMillis() - t;
				t1[a] = t;

				t = System.currentTimeMillis();
				filterWithInlining(src, dest, .5f);
				t = System.currentTimeMillis() - t;
				t2[a] = t;
				System.runFinalization();
				System.gc();
			}
			Arrays.sort(t1);
			Arrays.sort(t2);
			out.println("Median time using calls to the Color class: \t"
					+ t1[t1.length / 2] + " ms");
			out.println("Median time after inlining Color.RGBtoHSB: \t"
					+ t2[t2.length / 2] + " ms");
		} catch (Throwable t) {
			t.printStackTrace(err);
		}
	}

}