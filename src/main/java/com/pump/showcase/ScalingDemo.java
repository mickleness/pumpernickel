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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.pump.geom.TransformUtils;
import com.pump.image.ImageLoader;
import com.pump.image.pixel.PixelIterator;
import com.pump.image.pixel.Scaling;

/**
 * This demonstrates the efficiency of the <code>Scaling</code> class.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/ScalingDemo.png"
 * alt="A screenshot of the ScalingDemo.">
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

	Callable<BufferedImage> SCALED_INSTANCE_ICON = new Callable<BufferedImage>() {
		@Override
		public BufferedImage call() throws Exception {
			Image img = sampleImage.getScaledInstance(80, 60,
					Image.SCALE_SMOOTH);
			return ImageLoader.createImage(img);
		}
	};

	Callable<BufferedImage> GRAPHICS_UTILITIES_ICON = new Callable<BufferedImage>() {
		@Override
		public BufferedImage call() throws Exception {
			return createThumbnail(sampleImage, 80, 60);
		}
	};

	Callable<BufferedImage> TRANSFORM_ICON = new Callable<BufferedImage>() {
		@Override
		public BufferedImage call() throws Exception {
			BufferedImage bi = new BufferedImage(80, 60,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bi.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
					RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY);
			g.transform(TransformUtils.createAffineTransform(new Dimension(
					sampleImage.getWidth(), sampleImage.getHeight()),
					new Dimension(bi.getWidth(), bi.getHeight())));
			g.drawImage(sampleImage, 0, 0, null);
			g.dispose();
			return bi;
		}
	};

	Callable<BufferedImage> SCALING_ICON = new Callable<BufferedImage>() {
		@Override
		public BufferedImage call() throws Exception {
			return Scaling.scale(sampleImage, new Dimension(80, 60));
		}
	};

	JLabel graphicsUilitiesLabel, scaledInstanceLabel, scalingLabel,
			transformLabel;

	public ScalingDemo() {
		upperControls.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;

		try {
			graphicsUilitiesLabel = new JLabel("GraphicsUtilities", null,
					SwingConstants.CENTER);
			scaledInstanceLabel = new JLabel("Image.getScaledInstance", null,
					SwingConstants.CENTER);
			scalingLabel = new JLabel("Scaling", null, SwingConstants.CENTER);
			transformLabel = new JLabel("Transform", null,
					SwingConstants.CENTER);

			transformLabel
					.setToolTipText("<html>This uses a scaling AffineTransform to render the original image at a smaller size.</html>");
			graphicsUilitiesLabel
					.setToolTipText("<html>This approach repeatedly renders the image up to 50% smaller until the target size is reached.<p>This resolves the pixelated appearance that you see when you use a plain<br>AffineTransform.<p>This uses Romain Guy's GraphicsUtilities class.</html>");
			scaledInstanceLabel
					.setToolTipText("<html>This uses <code>Image.getScaledInstance(...)</code> to created a scaled image, and then<br>uses the <code>ImageLoader</code> to make sure the image data is available.</html>");
			scalingLabel
					.setToolTipText("<html>This uses the Pumpernickel <code>com.pump.image.pixel.Scaling</code> class<br>to iterate over the source image and produce a scaled image.</html>");

			lowerControls.setLayout(new GridBagLayout());
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.NONE;

			for (JLabel label : new JLabel[] { transformLabel,
					graphicsUilitiesLabel, scaledInstanceLabel, scalingLabel }) {
				lowerControls.add(label, c);
				label.setHorizontalTextPosition(JLabel.CENTER);
				label.setVerticalTextPosition(JLabel.BOTTOM);
				c.gridx++;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Thread loadThread = new Thread() {
			Image graphicsUtilitiesImage, scaledInstanceImage, scalingImage,
					transformImage;

			@Override
			public void run() {
				sampleImage = ImageLoader.createImage(ImageLoader.class
						.getResource("bridge3.jpg"));
				try {
					graphicsUtilitiesImage = GRAPHICS_UTILITIES_ICON.call();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							graphicsUilitiesLabel.setIcon(new ImageIcon(
									graphicsUtilitiesImage));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					scaledInstanceImage = SCALED_INSTANCE_ICON.call();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							scaledInstanceLabel.setIcon(new ImageIcon(
									scaledInstanceImage));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					scalingImage = SCALING_ICON.call();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							scalingLabel.setIcon(new ImageIcon(scalingImage));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					transformImage = TRANSFORM_ICON.call();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							transformLabel
									.setIcon(new ImageIcon(transformImage));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		loadThread.start();
	}

	@Override
	public String getTitle() {
		return "Scaling Demo";
	}

	@Override
	public String getSummary() {
		return "This compares the time and memory required by different scaling implementations.";
	}

	@Override
	public URL getHelpURL() {
		return ScalingDemo.class.getResource("scalingDemo.html");
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
	private static final String LABEL_TRANSFORM = "Transform";
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
		int sampleIndex = params[0];
		String label;
		Callable<BufferedImage> callable;
		switch (params[1]) {
		case 0:
			label = LABEL_SCALED_INSTANCE;
			callable = SCALED_INSTANCE_ICON;
			break;
		case 1:
			label = LABEL_GRAPHICS_UTILITIES;
			callable = GRAPHICS_UTILITIES_ICON;
			break;
		case 2:
			label = LABEL_TRANSFORM;
			callable = TRANSFORM_ICON;
			break;
		default:
			label = LABEL_SCALING;
			callable = SCALING_ICON;
		}

		timeSamples[sampleIndex] = System.currentTimeMillis();
		memorySamples[sampleIndex] = Runtime.getRuntime().freeMemory();

		for (int z = 0; z < 3; z++) {
			callable.call();
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
		return new int[] { SAMPLE_COUNT, 4 };
	}

}