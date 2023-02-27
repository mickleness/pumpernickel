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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
                        scaledImage = Scaling.scale(rainbowImage, scaledWidth, scaledHeight);
                        Map<Color, Integer> colorMap = getColorMap(scaledImage);

						int redCount = getColorCount(colorMap, Color.red);
						int orangeCount = getColorCount(colorMap, Color.orange);
						int yellowCount = getColorCount(colorMap, Color.yellow);
						int greenCount = getColorCount(colorMap, Color.green);
						int cyanCount = getColorCount(colorMap, Color.cyan);
						int blueCount = getColorCount(colorMap, Color.blue);

						int totalPixels = scaledImage.getWidth()
								* scaledImage.getHeight();

						float redPercent = redCount * 100 / totalPixels;
						float orangePercent = orangeCount * 100 / totalPixels;
						float yellowPercent = yellowCount * 100 / totalPixels;
						float greenPercent = greenCount * 100 / totalPixels;
						float cyanPercent = cyanCount * 100 / totalPixels;
						float bluePercent = blueCount * 100 / totalPixels;

						try {
							// a little bit of detail may be antialiased away,
							// but
							// we should have 6 really clear stripes:
							assertTrue("red band missing: " + redPercent,
									redPercent > 10);

							try {
								assertTrue(
										"orange band missing: " + orangePercent,
										orangePercent > 10);
							} catch (AssertionFailedError e) {
								int skyBlueCount = getColorCount(colorMap,
										new Color(0, 200, 255));
								float skyBluePercent = skyBlueCount * 100
										/ totalPixels;
								if (skyBluePercent > 10) {
									System.err.println(
											"## the orange is probably failing because the red and blue color channels are swapped");

									scaledImage = Scaling.scale(rainbowImage,
											scaledWidth, scaledHeight);
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

	private int getColorCount(Map<Color, Integer> colorMap, Color c) {
		int returnValue = 0;
		for (Map.Entry<Color, Integer> entry : colorMap.entrySet()) {
			if (Math.abs(c.getRed() - entry.getKey().getRed()) < 10
					&& Math.abs(c.getGreen() - entry.getKey().getGreen()) < 10
					&& Math.abs(c.getBlue() - entry.getKey().getBlue()) < 10) {
				returnValue += entry.getValue().intValue();
			}
		}
		return returnValue;
	}

	private Map<Color, Integer> getColorMap(BufferedImage image) {
		Map<Color, Integer> map = new HashMap<>();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb = image.getRGB(x, y);
				Color c = new Color(rgb);
				Integer f = map.get(c);
				if (f == null) {
					map.put(c, Integer.valueOf(1));
				} else {
					map.put(c, Integer.valueOf(1 + f));
				}
			}
		}
		return map;
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