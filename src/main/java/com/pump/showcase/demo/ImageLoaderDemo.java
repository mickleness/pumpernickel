package com.pump.showcase.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import com.pump.desktop.temp.TempFileManager;
import com.pump.image.ImageLoader;
import com.pump.image.ImageSize;
import com.pump.image.pixel.PixelIterator;
import com.pump.showcase.chart.ChartDataGenerator;
import com.pump.showcase.chart.PerformanceChartPanel;

public class ImageLoaderDemo extends ShowcaseResourceExampleDemo<File> {

	private static final long serialVersionUID = 1L;

	private static String PARAMETER_MODEL = "model";
	private static String MODEL_IMAGE_IO = "imageIO";
	private static String MODEL_MEDIA_TRACKER = "mediaTracker";
	private static String MODEL_IMAGE_LOADER = "imageLoader";

	class ImageLoaderChartDataGenerator implements ChartDataGenerator {
		final File file;

		// how many times we load the image to measure the time it takes
		// it's important this figure is small when the image is large (so the
		// samples don't take forever to collect), and large when the image is
		// small (so they can show statistically significant differences).
		final int loopCount;

		public ImageLoaderChartDataGenerator(File file) {
			this.file = file;

			Dimension imgSize = ImageSize.get(file);
			int area = imgSize.width * imgSize.height;

			if (area > 3000000) {
				loopCount = 10;
			} else if (area > 500000) {
				loopCount = 25;
			} else {
				loopCount = 100;
			}

		}

		@Override
		public ExecutionMode getExecutionMode() {
			return ExecutionMode.RECORD_TIME_AND_MEMORY_SEPARATELY;
		}

		@Override
		public int getTimedSampleCount() {
			return 10;
		}

		@Override
		public int getMemorySampleCount() {
			return 20;
		}

		@Override
		public List<Map<String, ?>> getTimedParameters() {
			List<Map<String, ?>> returnValue = new ArrayList<>();

			Map<String, Object> p1 = new HashMap<>();
			p1.put(PARAMETER_MODEL, MODEL_IMAGE_IO);
			p1.put(PerformanceChartPanel.PARAMETER_NAME, "ImageIO");
			returnValue.add(p1);

			Map<String, Object> p2 = new HashMap<>();
			p2.put(PARAMETER_MODEL, MODEL_MEDIA_TRACKER);
			p2.put(PerformanceChartPanel.PARAMETER_NAME, "MediaTracker");
			returnValue.add(p2);

			Map<String, Object> p3 = new HashMap<>();
			p3.put(PARAMETER_MODEL, MODEL_IMAGE_LOADER);
			p3.put(PerformanceChartPanel.PARAMETER_NAME, "ImageLoader");
			returnValue.add(p3);

			return returnValue;

		}

		@Override
		public List<Map<String, ?>> getMemoryParameters() {
			return getTimedParameters();
		}

		@Override
		public void runTimedSample(Map<String, ?> parameters) throws Exception {
			runSample(parameters, loopCount);
		}

		@Override
		public void runMemorySample(Map<String, ?> parameters)
				throws Exception {
			runSample(parameters, 1);
		}

		private void runSample(Map<String, ?> parameters, int loopCount)
				throws Exception {
			String model = (String) parameters.get(PARAMETER_MODEL);

			boolean multithreadTest = false;

			if (multithreadTest) {
				// TODO: figure out why this performs so differently.

				// when multithreadTest = true, my results resemble:

				// *** Time
				// ImageLoader = 577
				// ImageIO = 612
				// MediaTracker = 732

				// when multithreadTest = false, my results resemble:

				// *** Time
				// ImageLoader = 989
				// ImageIO = 1357
				// MediaTracker = 883

				if (MODEL_MEDIA_TRACKER.equals(model)) {
					MediaTracker mediaTracker = new MediaTracker(new Label());
					Image[] images = new Image[loopCount];
					for (int a = 0; a < loopCount; a++) {
						images[a] = Toolkit.getDefaultToolkit()
								.createImage(file.getAbsolutePath());
						mediaTracker.addImage(images[a], a);
					}
					mediaTracker.waitForAll();
					for (Image image : images) {
						image.flush();
					}
				} else {
					// the default Toolkit code uses 4 "Image Fetcher" threads,
					// so to compare performance we should have 4 threads too.
					ExecutorService executor = Executors.newFixedThreadPool(4);
					List<Future<?>> futures = new ArrayList<>(100);
					for (int a = 0; a < loopCount; a++) {
						final int z = a;
						futures.add(executor.submit(new Runnable() {
							public void run() {

								try {
									if (MODEL_IMAGE_IO.equals(model)) {
										ImageIO.read(file);
									} else if (MODEL_IMAGE_LOADER
											.equals(model)) {
										ImageLoader.createImage(file);
									}
								} catch (Exception e) {
									throw new RuntimeException(e);
								}

							}
						}));
					}
					executor.shutdown();
					for (Future<?> future : futures) {
						future.get();
					}
				}
			} else {
				for (int i = 0; i < loopCount; i++) {
					if (MODEL_IMAGE_IO.equals(model)) {
						ImageIO.read(file);
					} else if (MODEL_IMAGE_LOADER.equals(model)) {
						ImageLoader.createImage(file);
					} else if (MODEL_MEDIA_TRACKER.equals(model)) {
						MediaTracker mediaTracker = new MediaTracker(
								new Label());
						Image image = Toolkit.getDefaultToolkit()
								.createImage(file.getAbsolutePath());
						mediaTracker.addImage(image, i);
						mediaTracker.waitForAll();
						image.flush();
					}
				}
			}
		}

	}

	private static BufferedImage createSampleImage() {
		BufferedImage bi = new BufferedImage(800, 600,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		Random r = new Random(0);
		Color[] colors = new Color[] { Color.red, Color.gray, Color.green,
				Color.blue, Color.pink, Color.magenta, Color.cyan, Color.blue,
				Color.white };
		for (int a = 0; a < 100; a++) {
			Color color = colors[r.nextInt(colors.length)];
			g.setColor(color);
			float radius = r.nextInt(20) + 20;
			Ellipse2D circle = new Ellipse2D.Float(r.nextInt(800),
					r.nextInt(600), radius, radius);
			g.fill(circle);
		}
		g.dispose();
		return bi;
	}

	PerformanceChartPanel perfChartPanel;
	File sampleFile;

	public ImageLoaderDemo() throws Exception {
		super(File.class, false, "png", "jpg", "jpeg");

		perfChartPanel = new PerformanceChartPanel(
				"ImageLoader Performance Results");

		examplePanel.add(perfChartPanel);

	}

	@Override
	protected void setDefaultResourcePath() {
		try {
			if (sampleFile == null) {
				BufferedImage sampleImage = createSampleImage();
				sampleFile = TempFileManager.get().createFile("sampleImage",
						"png");
				ImageIO.write(sampleImage, "png", sampleFile);
			}
			resourcePathField.setText(sampleFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			super.setDefaultResourcePath();
		}
	}

	@Override
	public String getTitle() {
		return "ImageLoader Demo";
	}

	@Override
	public String getSummary() {
		return "The ImageLoader converts Images to BufferedImages.";
	}

	@Override
	public URL getHelpURL() {
		return VectorImageDemo.class.getResource("imageLoader.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "ImageLoader", "MediaTracker", "PixelIterator" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { ImageLoader.class, PixelIterator.class };
	}

	@Override
	protected void refreshFile(File file, String resourceStr) {
		ChartDataGenerator dataGenerator = new ImageLoaderChartDataGenerator(
				file);
		perfChartPanel.reset(dataGenerator);
	}

}