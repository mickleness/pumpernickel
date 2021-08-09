package com.pump.showcase.resourcegenerator;

import java.awt.Dimension;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
		writeImage(vi, "ThumbnailGeneratorDemo");
	}
}
