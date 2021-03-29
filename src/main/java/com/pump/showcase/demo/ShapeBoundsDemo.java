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
package com.pump.showcase.demo;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.pump.geom.ShapeBounds;

/**
 * A simple test showing off the efficiency of {@link ShapeBounds}.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/ShapeBoundsDemo.png"
 * alt="A screenshot of the ShapeBoundsDemo.">
 **/
public class ShapeBoundsDemo extends ShowcaseChartDemo {
	private static final long serialVersionUID = 1L;

	private static final int SAMPLE_COUNT = 10;

	private static final String IMPLEMENTATION_SHAPE_BOUNDS = "ShapeBounds";
	private static final String IMPLEMENTATION_AREA = "Area";
	private static final String IMPLEMENTATION_FLATTENED_AREA = "Flattened Area";

	static class MeasurementRunnable extends TimeMemoryMeasurementRunnable {

		Path2D path;
		Path2D flattenedShape;
		Area area;

		public MeasurementRunnable(Map<String, Map<String, SampleSet>> data,
				String implementation, Path2D path) {
			super(data, null, implementation);
			this.path = path;
			flattenedShape = new Path2D.Float();
			flattenedShape.append(path.getPathIterator(null, .1f), false);
			area = new Area(path);
		}

		@Override
		protected void runSample() {
			for (int a = 0; a < 20; a++) {
				if (implementation.equals(IMPLEMENTATION_AREA)) {
					new Area(path).getBounds2D();
				} else if (implementation
						.equals(IMPLEMENTATION_FLATTENED_AREA)) {
					new Area(flattenedShape).getBounds2D();
				} else {
					ShapeBounds.getBounds(path);
				}
			}
		}
	}

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

	@Override
	protected Collection<Runnable> getMeasurementRunnables(
			Map<String, Map<String, SampleSet>> data) {
		String[] implementations = new String[] { IMPLEMENTATION_SHAPE_BOUNDS,
				IMPLEMENTATION_AREA, IMPLEMENTATION_FLATTENED_AREA };
		Path2D path = createPath();
		List<Runnable> returnValue = new ArrayList<>(
				SAMPLE_COUNT * implementations.length);
		for (String implementation : implementations) {
			Runnable r = new MeasurementRunnable(data, implementation, path);
			for (int sample = 0; sample < SAMPLE_COUNT; sample++) {
				returnValue.add(r);
			}
		}
		return returnValue;
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
}