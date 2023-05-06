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

import com.pump.image.thumbnail.generator.BasicThumbnailGenerator;
import com.pump.image.thumbnail.generator.ScalingThumbnailGenerator;
import com.pump.image.thumbnail.generator.ThumbnailGenerator;
import com.pump.showcase.demo.ThumbnailGeneratorDemo;

/**
 * This creates the chart data showing the ThumbnailGenerators
 * relative performances.
 */
public class ThumbnailGeneratorDemoResourceGenerator
		extends DemoResourceGenerator {

	public void run(DemoResourceContext context) throws Exception {
		ScalingThumbnailGenerator.ALLOW_EMBEDDED_THUMBNAILS = false;
		try {
			File srcFile = context.getFile("bridge3.jpg");
			System.out.println("Source file: " + srcFile.getAbsolutePath());

			Thread.sleep(3000);

			StringBuilder sb = new StringBuilder();

			for (ThumbnailGenerator gs : ThumbnailGeneratorDemo.GENERATORS) {
				if (gs instanceof BasicThumbnailGenerator)
					continue;
				String seriesName = gs.getClass().getSimpleName();
				sb.append(seriesName + "\t");
			}
			sb.append("\n");

			for (ThumbnailGenerator gs : ThumbnailGeneratorDemo.GENERATORS) {
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
					sb.append(time + "\t");
				} catch (Throwable t) {
					t.printStackTrace();
					System.err.println(gs.getClass().getSimpleName() + " failed");
					return;
				}
			}
			sb.append("\n");
			System.out.println(sb);
		} finally {
			ScalingThumbnailGenerator.ALLOW_EMBEDDED_THUMBNAILS = true;
		}
	}
}