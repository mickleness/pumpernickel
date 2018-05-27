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
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JTextPane;
import javax.swing.text.JTextComponent;

import com.pump.awt.DemoPaintable;
import com.pump.image.bmp.BmpDecoder;
import com.pump.image.bmp.BmpEncoder;
import com.pump.image.pixel.PixelIterator;

/**
 * This application prompts you for a BMP file and then executes a few tests.
 * <P>
 * The first test measures the time and memory used to create a thumbnail, and
 * the second test measures the time and memory used to write a file.
 * <p>
 * The console is printed to the screen using a BasicConsole to see the results.
 * <p>
 * Also the BMP image itself is displayed in a separate window so you can
 * visually confirm that the results are what you expect.
 */
public class BmpComparisonDemo extends ShowcaseChartDemo {
	private static final long serialVersionUID = 1L;

	static final int SAMPLE_COUNT = 10;

	JTextComponent textBox = new JTextPane();

	public BmpComparisonDemo() {
		textBox.setText("This demo compares this codebase's BmpEncoder and BmpDecoder with the equivalent ImageIO encoder and decoder.\n\nAs of this writing the pump classes significantly outperform ImageIO classes in speed. Regarding memory usage: encoding shows a significant improvement, but decoding is nearly identical.");
		textBox.setEditable(false);
		textBox.setOpaque(false);
		upperControls.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		upperControls.add(textBox, c);
	}

	class DataGenerator {
		Map<String, Map<String, Long>> data;
		BufferedImage sampleImage;
		File bmpFile;
		long[] sampleTimes = new long[SAMPLE_COUNT];
		long[] sampleMemory = new long[sampleTimes.length];
		String CREATE_THUMBNAIL_TIME = "Decode BMP (Time)";
		String CREATE_THUMBNAIL_MEMORY = "Decode BMP (Memory)";
		String ENCODE_IMAGE_TIME = "Encode BMP (Time)";
		String ENCODE_IMAGE_MEMORY = "Encode BMP (Memory)";
		String LABEL_IMAGEIO = "javax.imageio.ImageIO classes";
		String LABEL_PUMP = "com.pump.image.bmp classes";

		public DataGenerator() throws IOException {
			data = new LinkedHashMap<>();
			data.put(CREATE_THUMBNAIL_TIME, new HashMap<String, Long>());
			data.put(CREATE_THUMBNAIL_MEMORY, new HashMap<String, Long>());
			data.put(ENCODE_IMAGE_TIME, new HashMap<String, Long>());
			data.put(ENCODE_IMAGE_MEMORY, new HashMap<String, Long>());

			sampleImage = new BufferedImage(800, 600,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = sampleImage.createGraphics();
			Color[] colors = new Color[] { new Color(0xffeaa7),
					new Color(0x55efc4) };
			DemoPaintable.paint(g, sampleImage.getWidth(),
					sampleImage.getHeight(), colors, "BMP");
			g.dispose();

			bmpFile = File.createTempFile("sample", ".bmp");
			bmpFile.deleteOnExit();
			try (FileOutputStream out = new FileOutputStream(bmpFile)) {
				BmpEncoder.write(sampleImage, out);
			}
		}

		public Map<String, Map<String, Long>> iterate(int[] params)
				throws Exception {
			int sampleIndex = params[0];
			int testType = params[1];
			int implementationType = params[2];

			System.runFinalization();
			System.gc();
			System.runFinalization();
			System.gc();
			sampleTimes[sampleIndex] = System.currentTimeMillis();
			sampleMemory[sampleIndex] = Runtime.getRuntime().freeMemory();
			for (int a = 0; a < 30; a++) {
				if (testType == 0) {
					decodeBMP(implementationType == 1);
				} else {
					encodeBMP(implementationType == 1);
				}
			}

			sampleTimes[sampleIndex] = System.currentTimeMillis()
					- sampleTimes[sampleIndex];
			sampleMemory[sampleIndex] = sampleMemory[sampleIndex]
					- Runtime.getRuntime().freeMemory();
			if (sampleIndex == sampleTimes.length - 1) {
				Arrays.sort(sampleTimes);
				Arrays.sort(sampleMemory);
				long medianSampleTime = sampleTimes[sampleTimes.length / 2];
				long medianSampleMemory = sampleMemory[sampleMemory.length / 2];

				String groupLabelTime = testType == 0 ? CREATE_THUMBNAIL_TIME
						: ENCODE_IMAGE_TIME;
				String groupLabelMemory = testType == 0 ? CREATE_THUMBNAIL_MEMORY
						: ENCODE_IMAGE_MEMORY;
				String dataType = implementationType == 1 ? LABEL_PUMP
						: LABEL_IMAGEIO;

				data.get(groupLabelTime).put(dataType, medianSampleTime);
				data.get(groupLabelMemory).put(dataType, medianSampleMemory);
			}
			return data;
		}

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();

		private void encodeBMP(boolean usePumpClasses) throws Exception {
			bOut.reset();
			if (usePumpClasses) {
				BmpEncoder.write(sampleImage, bOut);
			} else {
				ImageIO.write(sampleImage, "bmp", bOut);
			}
		}

		private void decodeBMP(boolean usePumpClasses) throws Exception {
			try (InputStream in = new FileInputStream(bmpFile)) {
				if (usePumpClasses) {
					BmpDecoder.readImage(in);
				} else {
					ImageIO.read(in);
				}
			}
		}
	}

	DataGenerator dataGenerator;

	@Override
	protected int[] getCollectDataParamLimits() {
		return new int[] { SAMPLE_COUNT, 2, 2 };
	}

	@Override
	protected Map<String, Map<String, Long>> collectData(int... params)
			throws Exception {
		if (dataGenerator == null)
			dataGenerator = new DataGenerator();
		return dataGenerator.iterate(params);
	}

	@Override
	public String getTitle() {
		return "BmpEncoder, BmpDecoder Demo";
	}

	@Override
	public URL getHelpURL() {
		return BmpComparisonDemo.class.getResource("BmpComparisonDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "bmp", "image", "encoding", "decoding",
				"bufferedimage", "performance" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { BmpEncoder.class, BmpDecoder.class,
				PixelIterator.class };
	}
}