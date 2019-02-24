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

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.pump.geom.ShapeBounds;

/**
 * A simple test showing off the efficiency of {@link ShapeBounds}.
 *
 **/
public class ShapeBoundsDemo extends ShowcaseChartDemo {
	private static final long serialVersionUID = 1L;
	int SAMPLE_COUNT = 10;

	@Override
	public String getTitle() {
		return "ShapeBounds Demo";
	}

	@Override
	public String getSummary() {
		return "This compares the time and memory required to calculate a shape's bounds using the com.pump.geom.ShapeBounds class and the Area class.\n\nThis demos the com.pump.geom.ShapeBounds's ability to calculate a shape's bounds against the java.awt.geom.Area.\n\n(This also considers the return value of Path2D#getBounds(), but that's inaccurate so it doesn't really count...)";
	}

	@Override
	public URL getHelpURL() {
		return ShapeBoundsDemo.class.getResource("shapeBoundsDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "performance", "boundary", "rectangle" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { ShapeBounds.class, Area.class, Shape.class,
				PathIterator.class };
	}

	String GROUP_TIME = "Time";
	String GROUP_MEMORY = "Memory";
	long[] sampleTimes = new long[SAMPLE_COUNT];
	long[] sampleMemory = new long[SAMPLE_COUNT];
	Map<String, Map<String, Long>> data;

	@Override
	protected Map<String, Map<String, Long>> collectData(int... params)
			throws Exception {
		if (data == null) {
			data = new HashMap<>();
			data.put(GROUP_MEMORY, new HashMap<String, Long>());
			data.put(GROUP_TIME, new HashMap<String, Long>());
		}

		int sampleIndex = params[0];
		int testType = params[1];

		System.runFinalization();
		System.gc();
		System.runFinalization();
		System.gc();
		sampleTimes[sampleIndex] = System.currentTimeMillis();
		sampleMemory[sampleIndex] = Runtime.getRuntime().freeMemory();

		Path2D path = createPath();
		Path2D flattenedShape = new Path2D.Float();
		flattenedShape.append(path.getPathIterator(null, .1f), false);
		Area area = new Area(path);
		// this is our gold standard we compare against:
		Rectangle2D expectedBounds = area.getBounds2D();
		Rectangle2D actualBounds = null;
		String typeName = null;
		for (int a = 0; a < 20; a++) {
			switch (testType) {
			case 0:
				typeName = "Area";
				actualBounds = new Area(path).getBounds2D();
				break;
			case 1:
				typeName = "Area (flattened)";
				actualBounds = new Area(flattenedShape).getBounds2D();
				break;
			case 2:
				typeName = "ShapeBounds";
				actualBounds = ShapeBounds.getBounds(path);
				break;
			case 3:
				typeName = "Path2D";
				actualBounds = new Path2D.Float(path).getBounds2D();
				break;
			default:
				throw new IllegalStateException("unexpected type: " + testType);
			}
		}

		if (!equivalent(actualBounds, expectedBounds)) {
			sampleTimes[sampleIndex] = BarChartRenderer.ERROR_CODE;
			sampleMemory[sampleIndex] = BarChartRenderer.ERROR_CODE;
		} else {
			sampleTimes[sampleIndex] = System.currentTimeMillis()
					- sampleTimes[sampleIndex];
			sampleMemory[sampleIndex] = sampleMemory[sampleIndex]
					- Runtime.getRuntime().freeMemory();
		}

		if (sampleIndex == sampleTimes.length - 1) {
			Arrays.sort(sampleTimes);
			Arrays.sort(sampleMemory);

			data.get(GROUP_TIME).put(typeName,
					sampleTimes[sampleTimes.length / 2]);
			data.get(GROUP_MEMORY).put(typeName,
					sampleMemory[sampleMemory.length / 2]);
		}
		return data;
	}

	@Override
	protected int[] getCollectDataParamLimits() {
		return new int[] { SAMPLE_COUNT, 4 };
	}

	private Path2D.Double createPath() {
		Random r = new Random(0);
		int numberOfSegments = 20;
		Path2D.Double p = new Path2D.Double();
		p.moveTo(1000 * r.nextFloat(), 1000 * r.nextFloat());
		for (int b = 0; b < numberOfSegments; b++) {
			p.curveTo(1000 * r.nextFloat(), 1000 * r.nextFloat(),
					1000 * r.nextFloat(), 1000 * r.nextFloat(),
					1000 * r.nextFloat(), 1000 * r.nextFloat());
		}
		p.closePath();
		return p;
	}

	private static boolean equivalent(Rectangle2D r1, Rectangle2D r2) {
		double tolerance = .001;
		if (Math.abs(r1.getMinX() - r2.getMinX()) > tolerance)
			return false;
		if (Math.abs(r1.getMaxX() - r2.getMaxX()) > tolerance)
			return false;
		if (Math.abs(r1.getMinY() - r2.getMinY()) > tolerance)
			return false;
		if (Math.abs(r1.getMaxY() - r2.getMaxY()) > tolerance)
			return false;
		return true;
	}
}