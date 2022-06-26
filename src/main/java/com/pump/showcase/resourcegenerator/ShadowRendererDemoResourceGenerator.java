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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.pump.graphics.vector.VectorImage;
import com.pump.image.shadow.ARGBPixels;
import com.pump.image.shadow.BoxShadowRenderer;
import com.pump.image.shadow.DoubleBoxShadowRenderer;
import com.pump.image.shadow.GaussianShadowRenderer;
import com.pump.image.shadow.ShadowAttributes;
import com.pump.image.shadow.ShadowRenderer;
import com.pump.showcase.chart.LineChartRenderer;
import com.pump.showcase.demo.ShadowRendererDemo;
import com.pump.showcase.demo.ShadowRendererDemo.OriginalGaussianShadowRenderer;

public class ShadowRendererDemoResourceGenerator extends DemoResourceGenerator {

	@Override
	public void run(DemoResourceContext context) throws Exception {
		Profiler profiler = new Profiler();
		profiler.run();
		LineChartRenderer renderer = new LineChartRenderer(
				profiler.results.data, "Kernel Radius",
				"Execution Time (ms) for 100 Renders");
		VectorImage img = new VectorImage();
		renderer.paint(img.createGraphics(), 600, 400);

		writeImage(img, "ShadowRendererDemo");
	}
}

/**
 * This compares the performance of different ShadowRenderers as the kernel
 * radius increases.
 * <p>
 * This class includes the UI and the comparison logic.
 */
class Profiler {

	static class ProfileResults {
		Map<String, SortedMap<Double, Double>> data = new TreeMap<>();

		public void store(ShadowRenderer renderer, float kernelSize,
				long time) {
			String name = getName(renderer);
			SortedMap<Double, Double> m = data.get(name);
			if (m == null) {
				m = new TreeMap<>();
				data.put(name, m);
			}
			m.put((double) kernelSize, (double) time);
		}

		private String getName(ShadowRenderer renderer) {
			if (renderer instanceof GaussianShadowRenderer)
				return "Optimized Gaussian Shadow Renderer";
			if (renderer instanceof OriginalGaussianShadowRenderer)
				return "Unoptimized Gaussian Shadow Renderer";

			String str = renderer.getClass().getSimpleName();
			StringBuilder sb = new StringBuilder();
			for (int a = 0; a < str.length(); a++) {
				char ch = str.charAt(a);
				if (Character.isUpperCase(ch) && sb.length() > 0) {
					sb.append(' ');
				}
				sb.append(ch);
			}
			return sb.toString();
		}

		public void printTable() {
			StringBuilder sb = new StringBuilder();
			sb.append("Kernel\t");
			for (String name : data.keySet()) {
				sb.append(name);
				sb.append("\t");
			}
			System.out.println(sb.toString().trim());

			SortedSet<Double> allKeys = new TreeSet<>();
			for (SortedMap<Double, Double> m : data.values()) {
				allKeys.addAll(m.keySet());
			}
			for (Double key : allKeys) {
				sb.delete(0, sb.length());
				sb.append(key.toString());
				sb.append("\t");
				for (String name : data.keySet()) {
					sb.append(data.get(name).get(key));
					sb.append("\t");
				}
				System.out.println(sb.toString().trim());
			}
		}
	}

	static class RunSample implements Runnable {
		ShadowRenderer renderer;
		ShadowAttributes attr;
		ARGBPixels srcPixels, dstPixels;
		ProfileResults profileResults;

		public RunSample(ProfileResults profileResults, ShadowRenderer renderer,
				ShadowAttributes attr, ARGBPixels srcPixels,
				ARGBPixels dstPixels) {
			this.renderer = renderer;
			this.attr = attr;
			this.dstPixels = dstPixels;
			this.srcPixels = srcPixels;
			this.profileResults = profileResults;
		}

		public void run() {
			long[] times = new long[6];
			for (int a = 0; a < times.length; a++) {

				times[a] = System.currentTimeMillis();
				for (int b = 0; b < 100; b++) {
					Arrays.fill(dstPixels.getPixels(), 0);
					renderer.createShadow(srcPixels, dstPixels,
							attr.getShadowKernelRadius(),
							attr.getShadowColor());
				}
				times[a] = System.currentTimeMillis() - times[a];
			}
			Arrays.sort(times);
			profileResults.store(renderer, attr.getShadowKernelRadius(),
					times[times.length / 2]);
		}
	}

	// frontload most expensive renderers first:
	Collection<ShadowRenderer> renderers = Arrays.asList(
			new OriginalGaussianShadowRenderer(), new GaussianShadowRenderer(),
			new DoubleBoxShadowRenderer(), new BoxShadowRenderer());

	ProfileResults results;

	public ProfileResults run() {
		if (results == null) {
			results = createResults();
		}
		return results;
	}

	protected ProfileResults createResults() {
		ProfileResults returnValue = new ProfileResults();
		profileRenderers(returnValue, renderers);
		return returnValue;
	}

	private void profileRenderers(ProfileResults profileResults,
			Collection<ShadowRenderer> renderers) {
		boolean outputResultsToConsole = false;

		try {
			BufferedImage srcImage = ShadowRendererDemo.createTestImage();
			ARGBPixels srcPixels = new ARGBPixels(srcImage);
			srcImage.getRaster().getDataElements(0, 0, srcPixels.getWidth(),
					srcPixels.getHeight(), srcPixels.getPixels());

			List<Runnable> runnables = new LinkedList<>();
			for (ShadowRenderer renderer : renderers) {
				float min = 0;
				float max = 25;
				// load max first so we front more expensive things at the
				// beginning of progress bar updates
				for (float kernelSize = max; kernelSize >= min; kernelSize -= .5f) {
					ShadowAttributes attr = new ShadowAttributes(0, 0,
							kernelSize, Color.black);
					int k = renderer.getKernel(attr.getShadowKernelRadius())
							.getKernelRadius();
					ARGBPixels dstPixels = new ARGBPixels(
							srcImage.getWidth() + 2 * k,
							srcImage.getHeight() + 2 * k);
					runnables.add(new RunSample(profileResults, renderer, attr,
							srcPixels, dstPixels));
				}
			}

			while (!runnables.isEmpty()) {
				Runnable runnable = runnables.remove(0);
				runnable.run();
			}

			if (outputResultsToConsole)
				profileResults.printTable();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}