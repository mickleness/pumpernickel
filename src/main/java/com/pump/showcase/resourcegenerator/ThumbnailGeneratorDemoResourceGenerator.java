package com.pump.showcase.resourcegenerator;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.pump.graphics.vector.VectorImage;
import com.pump.image.thumbnail.generator.BasicThumbnailGenerator;
import com.pump.image.thumbnail.generator.ThumbnailGenerator;
import com.pump.showcase.chart.BarChartRenderer;
import com.pump.showcase.demo.ThumbnailGeneratorDemo;

/**
 * This creates the JVG image of the chart showing the ThumbnailGenerators
 * relative performances.
 */
public class ThumbnailGeneratorDemoResourceGenerator
		extends DemoResourceGenerator {

	public void run(DemoResourceContext context) throws Exception {
		File srcFile = context.getFile("bridge3.jpg");
		System.out.println("Source file: " + srcFile.getAbsolutePath());

		Thread.sleep(3000);

		Map<String, Map<String, Long>> chartData = new HashMap<>();
		chartData.put("Time", new LinkedHashMap<>());
		for (ThumbnailGenerator gs : ThumbnailGeneratorDemo.GENERATORS) {
			if (gs instanceof BasicThumbnailGenerator)
				continue;

			System.gc();
			System.runFinalization();
			System.gc();
			System.runFinalization();
			long[] times = new long[10];
			try {
				for (int a = 0; a < times.length; a++) {
					times[a] = System.currentTimeMillis();
					for (int b = 0; b < 10; b++) {
						gs.createThumbnail(srcFile, -1);
					}
					times[a] = System.currentTimeMillis() - times[a];
				}
				Arrays.sort(times);
				long time = times[times.length / 2];
				System.out.println(gs.getClass().getSimpleName() + " " + time);
				chartData.get("Time").put(gs.getClass().getSimpleName(), time);
			} catch (Throwable t) {
				t.printStackTrace();
				System.out.println(gs.getClass().getSimpleName() + " failed");
			}
		}

		BarChartRenderer renderer = new BarChartRenderer(chartData);
		VectorImage vi = new VectorImage();
		renderer.paint(vi.createGraphics(), new Dimension(300, 100));

		boolean writeFiles = false;

		if (writeFiles) {
			File jvgFile = new File("ThumbnailGeneratorDemo.jvg");
			try (FileOutputStream fileOut = new FileOutputStream(jvgFile)) {
				vi.save(fileOut);
			}
			System.out.println("Chart: " + jvgFile.getAbsolutePath());
		}

		if (writeFiles) {
			// make a PNG version just for quick human reference:
			BufferedImage bi = renderer.paint(new Dimension(300, 100));
			File pngFile = new File("ThumbnailGeneratorDemo.png");
			ImageIO.write(bi, "png", pngFile);
			System.out.println("Chart: " + pngFile.getAbsolutePath());
		}

		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			vi.save(byteOut);
			byte[] bytes = byteOut.toByteArray();
			String str = new String(Base64.getEncoder().encode(bytes));
			System.out.println("Base64 encoding of jvg:");
			System.out.println(str);
		}
	}
}
