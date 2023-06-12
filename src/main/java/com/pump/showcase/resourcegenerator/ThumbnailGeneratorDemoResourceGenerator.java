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
package com.pump.showcase.resourcegenerator;

import java.io.File;
import java.util.*;

import com.pump.image.thumbnail.generator.*;
import com.pump.showcase.demo.ThumbnailGeneratorDemo;

/**
 * This creates the chart data showing the ThumbnailGenerators
 * relative performances.
 */
public class ThumbnailGeneratorDemoResourceGenerator
		extends DemoResourceGenerator {

	public void run(DemoResourceContext context) throws Exception {
		File srcFile = context.getFile("IMG-20171107-WA0002.jpg");
		System.out.println("Source file: " + srcFile.getAbsolutePath());

		Thread.sleep(3000);

		List<ThumbnailGenerator> generators = new ArrayList<>();
		generators.addAll(Arrays.asList(ThumbnailGeneratorDemo.GENERATORS));
		generators.add(new ScalingThumbnailGenerator(false));
		generators.add(new ImageIOThumbnailGenerator(false));

		SortedSet<Long> sortedTimes = new TreeSet<>();
		Map<ThumbnailGenerator, Long> timeMap = new HashMap<>();

		for (ThumbnailGenerator gs : generators) {
			if (gs instanceof BasicThumbnailGenerator)
				continue;

			System.gc();
			System.gc();
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
				sortedTimes.add(time);
				timeMap.put(gs, time);
			} catch (Throwable t) {
				t.printStackTrace();
				System.err.println(gs.getClass().getSimpleName() + " failed");
				return;
			}
		}

		StringBuilder row1 = new StringBuilder();
		StringBuilder row2 = new StringBuilder();
		for (Long time : sortedTimes) {
			for (Map.Entry<ThumbnailGenerator, Long> entry : timeMap.entrySet()) {
				if (entry.getValue().equals(time)) {
					row1.append(toString(entry.getKey()) + "\t");
					row2.append(entry.getValue() + "\t");
				}
			}
		}
		System.out.println(row1);
		System.out.println(row2);

	}

	private String toString(ThumbnailGenerator gs) {
		if (gs instanceof ScalingThumbnailGenerator) {
			ScalingThumbnailGenerator stg = (ScalingThumbnailGenerator) gs;
			if (stg.isAllowEmbeddedThumbnails())
				return "ScalingThumbnailGenerator (Thumbnails)";
			return "ScalingThumbnailGenerator (No Thumbnails)";
		}
		if (gs instanceof ImageIOThumbnailGenerator) {
			ImageIOThumbnailGenerator itg = (ImageIOThumbnailGenerator) gs;
			if (itg.isAllowDownsampling())
				return "ImageIOThumbnailGenerator (Subsampling)";
			return "ImageIOThumbnailGenerator (No Subsampling)";
		}
		return gs.getClass().getSimpleName();
	}
}