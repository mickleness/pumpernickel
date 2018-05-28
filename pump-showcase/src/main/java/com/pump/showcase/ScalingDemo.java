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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.pump.awt.DemoPaintable;
import com.pump.image.ImageLoader;
import com.pump.image.pixel.PixelIterator;
import com.pump.image.pixel.Scaling;

/**
 * A demo app that shows off the <code>Scaling</code> class.
 *
 */
public class ScalingDemo extends ShowcaseChartDemo {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>
	 * Returns a thumbnail of a source image.
	 * </p>
	 * <p>
	 * The source and javadoc for this method are copied from
	 * GraphicsUtilities.java, licensed under LGPL. I want to compare this
	 * method against other methods in this class.
	 * 
	 * <p>
	 * This method offers a good trade-off between speed and quality. The result
	 * looks better than
	 * {@link #createThumbnailFast(java.awt.image.BufferedImage, int)} when the
	 * new size is less than half the longest dimension of the source image, yet
	 * the rendering speed is almost similar.
	 * </p>
	 *
	 * @param image
	 *            the source image
	 * @param newWidth
	 *            the width of the thumbnail
	 * @param newHeight
	 *            the height of the thumbnail
	 * @return a new compatible <code>BufferedImage</code> containing a
	 *         thumbnail of <code>image</code>
	 * @throws IllegalArgumentException
	 *             if <code>newWidth</code> is larger than the width of
	 *             <code>image</code> or if code>newHeight</code> is larger than
	 *             the height of
	 *             <code>image or if one the dimensions is not &gt; 0</code>
	 */
	private static BufferedImage createThumbnail(BufferedImage image,
			int newWidth, int newHeight) {
		int width = image.getWidth();
		int height = image.getHeight();

		if (newWidth >= width || newHeight >= height) {
			throw new IllegalArgumentException("newWidth and newHeight cannot"
					+ " be greater than the image" + " dimensions");
		} else if (newWidth <= 0 || newHeight <= 0) {
			throw new IllegalArgumentException("newWidth and newHeight must"
					+ " be greater than 0");
		}

		BufferedImage thumb = image;

		do {
			if (width > newWidth) {
				width /= 2;
				if (width < newWidth) {
					width = newWidth;
				}
			}

			if (height > newHeight) {
				height /= 2;
				if (height < newHeight) {
					height = newHeight;
				}
			}

			GraphicsConfiguration gc = GraphicsEnvironment
					.getLocalGraphicsEnvironment().getDefaultScreenDevice()
					.getDefaultConfiguration();
			BufferedImage temp = gc.createCompatibleImage(width, height);

			Graphics2D g2 = temp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.drawImage(thumb, 0, 0, temp.getWidth(), temp.getHeight(), null);
			g2.dispose();

			thumb = temp;
		} while (width != newWidth || height != newHeight);

		return thumb;
	}

	@Override
	public String getTitle() {
		return "Scaling Demo";
	}

	@Override
	public URL getHelpURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "scaling", "scale", "performance", "image" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { Scaling.class, PixelIterator.class };
	}

	private static final String LABEL_SCALED_INSTANCE = "Image.getScaledInstance()";
	private static final String LABEL_GRAPHICS_UTILITIES = "GraphicsUtilities";
	private static final String LABEL_SCALING = "Scaling";
	private static final String GROUP_TIME = "Time";
	private static final String GROUP_MEMORY = "Memory";
	private static final int SAMPLE_COUNT = 10;

	Map<String, Map<String, Long>> data;
	long[] timeSamples = new long[SAMPLE_COUNT];
	long[] memorySamples = new long[SAMPLE_COUNT];
	BufferedImage sampleImage;

	@Override
	protected Map<String, Map<String, Long>> collectData(int... params)
			throws Exception {
		if (data == null) {
			data = new LinkedHashMap<>();
			data.put(GROUP_TIME, new LinkedHashMap<String, Long>());
			data.put(GROUP_MEMORY, new LinkedHashMap<String, Long>());
		}
		if (sampleImage == null) {
			sampleImage = new BufferedImage(800, 600,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = sampleImage.createGraphics();
			Color[] colors = new Color[] { new Color(0xffeaa7),
					new Color(0x55efc4) };
			DemoPaintable.paint(g, sampleImage.getWidth(),
					sampleImage.getHeight(), colors, "BMP");
			g.dispose();
		}
		int sampleIndex = params[0];
		String label;
		Runnable runnable;
		switch (params[1]) {
		case 0:
			label = LABEL_SCALED_INSTANCE;
			runnable = new Runnable() {
				@Override
				public void run() {
					Image img = sampleImage.getScaledInstance(60, 80,
							Image.SCALE_SMOOTH);
					ImageLoader.createImage(img);
				}
			};
			break;
		case 1:
			label = LABEL_GRAPHICS_UTILITIES;
			runnable = new Runnable() {
				@Override
				public void run() {
					createThumbnail(sampleImage, 60, 80);
				}
			};
			break;
		default:
			label = LABEL_SCALING;
			runnable = new Runnable() {
				@Override
				public void run() {
					Scaling.scale(sampleImage, new Dimension(60, 80));
				}
			};
		}

		timeSamples[sampleIndex] = System.currentTimeMillis();
		memorySamples[sampleIndex] = Runtime.getRuntime().freeMemory();

		for (int z = 0; z < 10; z++) {
			runnable.run();
		}

		timeSamples[sampleIndex] = System.currentTimeMillis()
				- timeSamples[sampleIndex];
		memorySamples[sampleIndex] = memorySamples[sampleIndex]
				- Runtime.getRuntime().freeMemory();

		if (sampleIndex == SAMPLE_COUNT - 1) {
			Arrays.sort(timeSamples);
			Arrays.sort(memorySamples);
			data.get(GROUP_TIME)
					.put(label, timeSamples[timeSamples.length / 2]);
			data.get(GROUP_MEMORY).put(label,
					memorySamples[memorySamples.length / 2]);
		}

		return data;
	}

	@Override
	protected int[] getCollectDataParamLimits() {
		return new int[] { SAMPLE_COUNT, 3 };
	}

}