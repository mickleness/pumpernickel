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
package com.pump.showcase.demo;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.net.URL;
import java.util.*;

import com.pump.geom.ShapeBounds;
import com.pump.showcase.chart.ChartDataGenerator;
import com.pump.showcase.chart.PerformanceChartPanel;

/**
 * A simple test showing off the efficiency of {@link ShapeBounds}.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/ShapeBoundsDemo.png"
 * alt="A screenshot of the ShapeBoundsDemo.">
 **/
public class ShapeBoundsDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	PerformanceChartPanel chartPanel;
	
	enum Model {
		AREA("Area") {
			@Override
			public void run(Path2D.Double path) {
				new Area(path).getBounds2D();
			}
		},
		AREA_FLATTENED("Flattened Area") {
			@Override
			public void run(Path2D.Double path) {
				Path2D flatCopy = new Path2D.Double();
				flatCopy.append(path.getPathIterator(null, .01), false);
				new Area(flatCopy).getBounds2D();
			}
		},
		SHAPE_BOUNDS("ShapeBounds") {
			@Override
			public void run(Path2D.Double path) {
				ShapeBounds.getBounds(path);
			}
		},
		SHAPE_BOUNDS_FLATTENED("Flattened ShapeBounds") {
			@Override
			public void run(Path2D.Double path) {
				ShapeBounds.getBounds(path.getPathIterator(null, .01));
			}
		};

		String name;
		Model(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		public abstract void run(Path2D.Double path);
	}
	
	public ShapeBoundsDemo() {
		chartPanel = new PerformanceChartPanel("ShapeBounds Performance Results");
		ChartDataGenerator dataGenerator = new ChartDataGenerator() {
			@Override
			public ExecutionMode getExecutionMode() {
				return ExecutionMode.RECORD_TIME_ONLY;
			}

			@Override
			public int getTimedSampleCount() {
				return 10;
			}

			@Override
			public int getMemorySampleCount() {
				return 0;
			}

			@Override
			public List<Map<String, ?>> getTimedParameters() {
				List<Map<String,?>> returnValue = new ArrayList<>();
				for (Model model : Model.values()) {
					Map<String, Object> params = new HashMap<>();
					params.put(PerformanceChartPanel.PARAMETER_NAME, model.toString());
					params.put("model", model);
					returnValue.add(params);
				}
				return returnValue;
			}

			@Override
			public List<Map<String, ?>> getMemoryParameters() {
				return Collections.EMPTY_LIST;
			}

			@Override
			public void runTimedSample(Map<String, ?> parameters) {
				Model model = (Model) parameters.get("model");

				Path2D.Double p = createPath();
				for (int a = 0; a < 50; a++) {
					model.run(p);
				}
			}

			@Override
			public void runMemorySample(Map<String, ?> parameters) {
				throw new UnsupportedOperationException();
			}
		};
		chartPanel.reset(dataGenerator);

		add(chartPanel);
	}

	@Override
	public String getTitle() {
		return "ShapeBounds Demo";
	}

	@Override
	public String getSummary() {
		return "This compares the time required to calculate a shape's bounds using the com.pump.geom.ShapeBounds class and the Area class.\n\nThis demos the com.pump.geom.ShapeBounds's ability to calculate a shape's bounds against the java.awt.geom.Area.";
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