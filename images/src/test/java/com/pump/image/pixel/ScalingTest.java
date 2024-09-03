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
package com.pump.image.pixel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class ScalingTest extends TestCase {
	static Color[] colors = new Color[] { Color.red, Color.orange, Color.yellow,
			Color.green, Color.cyan, Color.blue };

	static int[] imageTypes = new int[] { BufferedImage.TYPE_INT_ARGB,
			BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_3BYTE_BGR,
			BufferedImage.TYPE_4BYTE_ABGR, BufferedImage.TYPE_INT_RGB,
			BufferedImage.TYPE_INT_ARGB_PRE };

	static double[] imageScales = new double[] { 0.05, 0.1, 0.15, .2, .25, .3,
			.35, .4, .45, .5, .6, .7, .8, .9, .99, 1, 1.05, 1.1, 1.2, 1.5, 2, 3,
			4 };

	/**
	 * Test that an image with 6 bands is being scaled correctly.
	 * <p>
	 * This test is derived from a real-world failure in which the thumbnail
	 * appeared to be stretched 200% horizontally.
	 * </p>
	 */
	public void testColorBands() throws Throwable {
		List<Throwable> errors = new LinkedList<>();

		for (boolean isHorizontal : new boolean[] { true, false }) {
			for (int imageType : imageTypes) {
				BufferedImage rainbowImage = createRainbowImage(1000, 1000, imageType, isHorizontal);
				for (double scale : imageScales) {

					System.out.println("Testing image type = "
							+ ImageType.get(imageType) + ", image scale: "
							+ scale + ", isHorizontal: " + isHorizontal);

                    BufferedImage scaledImage = null;
                    try {
                        int scaledWidth = (int)(rainbowImage.getWidth() * scale);
                        int scaledHeight = (int)(rainbowImage.getHeight() * scale);
                        scaledImage = Scaling.scale(rainbowImage, new Dimension(scaledWidth, scaledHeight), null, null);

						RainbowColorProfile profile = new RainbowColorProfile(scaledImage);

						int totalPixels = scaledImage.getWidth()
								* scaledImage.getHeight();

						float redPercent = profile.redCount * 100 / totalPixels;
						float orangePercent = profile.orangeCount * 100 / totalPixels;
						float yellowPercent = profile.yellowCount * 100 / totalPixels;
						float greenPercent = profile.greenCount * 100 / totalPixels;
						float cyanPercent = profile.cyanCount * 100 / totalPixels;
						float bluePercent = profile.blueCount * 100 / totalPixels;

						try {
							// a little bit of detail may be antialiased away,
							// but we should have 6 really clear stripes:
							assertTrue("red band missing: " + redPercent,
									redPercent > 10);

							try {
								assertTrue(
										"orange band missing: " + orangePercent,
										orangePercent > 10);
							} catch (AssertionFailedError e) {
								int skyBlueCount = profile.skyBlueCount;
								float skyBluePercent = skyBlueCount * 100
										/ totalPixels;
								if (skyBluePercent > 10) {
									System.err.println(
											"## the orange is probably failing because the red and blue color channels are swapped");

									scaledImage = Scaling.scale(rainbowImage, new Dimension(scaledWidth, scaledHeight), null, null);
								}
								throw e;
							}

							assertTrue("yellow band missing: " + yellowPercent,
									yellowPercent > 10);
							assertTrue("green band missing: " + greenPercent,
									greenPercent > 10);
							assertTrue("cyan band missing: " + cyanPercent,
									cyanPercent > 10);
							assertTrue("blue band missing: " + bluePercent,
									bluePercent > 10);
						} catch (Throwable t) {
							throw t;
						}
					} catch (Throwable e) {
						errors.add(e);
						e.printStackTrace();
					}
				}
			}
		}

		if (!errors.isEmpty())
			throw errors.get(0);
	}

	static class RainbowColorProfile {
		int redCount, orangeCount, yellowCount, greenCount, cyanCount, blueCount;

		int skyBlueCount;

		RainbowColorProfile(BufferedImage bi) {
			PixelIterator iter = ImageType.INT_RGB.createPixelIterator(new ImagePixelIterator(bi));
			int[] row = new int[bi.getWidth()];
			while (!iter.isDone()) {
				iter.next(row, 0);
				for (int x = 0; x < row.length; x++) {
					int rgb = row[x];
					int red = (rgb >> 16) & 0xff;
					int green = (rgb >> 8) & 0xff;
					int blue = rgb & 0xff;

					if (Math.abs(red - 255) < 10 && Math.abs(green - 0) < 10 && Math.abs(blue - 0) < 10) {
						redCount++;
					} else if (Math.abs(red - 255) < 10 && Math.abs(green - 200) < 10 && Math.abs(blue - 0) < 10) {
						orangeCount++;
					} else if (Math.abs(red - 255) < 10 && Math.abs(green - 255) < 10 && Math.abs(blue - 0) < 10) {
						yellowCount++;
					} else if (Math.abs(red - 0) < 10 && Math.abs(green - 255) < 10 && Math.abs(blue - 0) < 10) {
						greenCount++;
					} else if (Math.abs(red - 0) < 10 && Math.abs(green - 255) < 10 && Math.abs(blue - 255) < 10) {
						cyanCount++;
					} else if (Math.abs(red - 0) < 10 && Math.abs(green - 0) < 10 && Math.abs(blue - 255) < 10) {
						blueCount++;
					} else if (Math.abs(red - 0) < 10 && Math.abs(green - 200) < 10 && Math.abs(blue - 255) < 10) {
						// this is an error condition where red and blue get switched; it's helpful if we
						// can identify and mention this.
						skyBlueCount++;
					}
				}
			}
		}
	}

    public static BufferedImage createRainbowImage(int width, int height, int imageType, boolean isHorizontal) {
        BufferedImage bi = new BufferedImage(width, height, imageType);

		Graphics2D g = bi.createGraphics();
		int x = 0;
		for (int a = 0; a < colors.length; a++) {
			int endX = bi.getWidth() * (a + 1) / colors.length;
			if (a == colors.length - 1) {
				endX = bi.getWidth();
			}
			g.setColor(colors[a]);
			if (!isHorizontal) {
				g.fillRect(x, 0, endX - x, bi.getHeight());
			} else {
				g.fillRect(0, x, bi.getWidth(), endX - x);
			}

			x = endX;
		}
		g.dispose();

		return bi;
	}

}