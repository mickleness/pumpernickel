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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.pump.plaf.CircularProgressBarUI;
import com.pump.showcase.chart.BarChartRenderer;

public abstract class ShowcaseChartDemo extends ShowcaseDemo {

	private static final long serialVersionUID = 1L;

	/**
	 * This is a Number that is based on the median value of several numbers.
	 * <p>
	 * The intended use case is to record several measurements, add them to this
	 * object, and then treat this object like a single Number measurement.
	 * 
	 */
	protected static class SampleSet extends Number {
		private static final long serialVersionUID = 1L;

		List<Number> samples = new LinkedList<>();
		Number median = null;

		public void addSample(Number number) {
			samples.add(number);
			median = null;
		}

		public Number getMedian() {
			if (median == null) {
				median = calculateMedian();
			}
			return median;
		}

		@Override
		public synchronized String toString() {
			// sorts everything
			getMedian();
			return "SampleSet[ " + samples + "]";
		}

		private synchronized Number calculateMedian() {
			Collections.sort((List) samples);
			int index = samples.size() / 2;
			Iterator<Number> iter = samples.iterator();
			while (iter.hasNext()) {
				Number value = iter.next();
				if (index == 0)
					return value;
				index--;
			}

			// this should only happen when samples is empty
			return 0;
		}

		@Override
		public int intValue() {
			return getMedian().intValue();
		}

		@Override
		public long longValue() {
			return getMedian().longValue();
		}

		@Override
		public float floatValue() {
			return getMedian().floatValue();
		}

		@Override
		public double doubleValue() {
			return getMedian().doubleValue();
		}
	}

	protected abstract static class TimeMemoryMeasurementRunnable
			implements Runnable {

		protected Map<String, Map<String, SampleSet>> data;
		protected String operation, implementation;

		public TimeMemoryMeasurementRunnable(
				Map<String, Map<String, SampleSet>> data, String operation,
				String implementation) {
			this.data = data;
			this.operation = operation;
			this.implementation = implementation;
		}

		public void run() {
			String prefix = operation == null ? "" : operation + " ";
			String categoryTime = prefix + "Time";
			String categoryMemory = prefix + "Memory";

			Map<String, SampleSet> timeData = data.get(categoryTime);
			if (timeData == null) {
				timeData = new LinkedHashMap<>();
				data.put(categoryTime, timeData);
			}
			Map<String, SampleSet> memoryData = data.get(categoryMemory);
			if (memoryData == null) {
				memoryData = new LinkedHashMap<>();
				data.put(categoryMemory, memoryData);
			}

			SampleSet timeSampleSet = timeData.get(implementation);
			if (timeSampleSet == null) {
				timeSampleSet = new SampleSet();
				timeData.put(implementation, timeSampleSet);
			}
			SampleSet memorySampleSet = memoryData.get(implementation);
			if (memorySampleSet == null) {
				memorySampleSet = new SampleSet();
				memoryData.put(implementation, memorySampleSet);
			}

			System.runFinalization();
			System.gc();
			System.runFinalization();
			System.gc();

			long time = System.currentTimeMillis();
			long memory = Runtime.getRuntime().freeMemory();

			runSample();

			time = System.currentTimeMillis() - time;
			memory = memory - Runtime.getRuntime().freeMemory();

			timeSampleSet.addSample(time);
			memorySampleSet.addSample(memory);
		}

		protected abstract void runSample();
	}

	class UpdateProgressRunnable implements Runnable {
		float fraction;

		public UpdateProgressRunnable(long value, long max) {
			fraction = ((float) value) / ((float) max);
		}

		@Override
		public void run() {
			int range = progressBar.getMaximum() - progressBar.getMinimum();
			int v = (int) (progressBar.getMinimum() + fraction * range);
			progressBar.setIndeterminate(false);
			progressBar.setValue(v);
		}

	}

	class InstallResults implements Runnable {
		Map<String, Map<String, SampleSet>> data;

		public InstallResults(Map<String, Map<String, SampleSet>> data) {
			this.data = data;
		}

		@Override
		public void run() {
			if (!SwingUtilities.isEventDispatchThread()) {
				// the first time this is run it's called from our worker
				// thread:
				SwingUtilities.invokeLater(this);
				return;
			}
			//
			Map<String, Map<String, Long>> dataCopy = convertSampleSetToLong(
					data);
			BarChartRenderer r = new BarChartRenderer(dataCopy);
			BufferedImage bi = r.render(new Dimension(500, 1000));
			progressBar.setVisible(false);
			results.setVisible(true);
			results.add(new JLabel(new ImageIcon(bi)));
		}

		/**
		 * Convert our data from SampleSets to Longs. If we did a major refactor
		 * we could remove this method, but this accommodates some legacy code
		 * for now.
		 */
		private Map<String, Map<String, Long>> convertSampleSetToLong(
				Map<String, Map<String, SampleSet>> d) {
			Map<String, Map<String, Long>> returnValue = new LinkedHashMap<>();
			for (Entry<String, Map<String, SampleSet>> outerEntry : d
					.entrySet()) {
				Map<String, Long> entryCopy = new LinkedHashMap<>();
				for (Entry<String, SampleSet> innerEntry : outerEntry.getValue()
						.entrySet()) {
					entryCopy.put(innerEntry.getKey(),
							innerEntry.getValue().longValue());
				}
				returnValue.put(outerEntry.getKey(), entryCopy);
			}
			return returnValue;
		}

	}

	JProgressBar progressBar = new JProgressBar();
	JPanel results = new JPanel();
	boolean isShowing = false;

	public ShowcaseChartDemo() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		add(upperControls, c);
		c.gridy++;
		c.weighty = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(10, 10, 10, 10);
		add(progressBar, c);
		c.insets = new Insets(3, 3, 3, 3);
		add(results, c);
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 0;
		add(lowerControls, c);

		results.setVisible(false);
		progressBar.setPreferredSize(new Dimension(70, 70));
		progressBar.setIndeterminate(true);
		progressBar.setUI(new CircularProgressBarUI());
		progressBar.setStringPainted(true);
		progressBar.putClientProperty(CircularProgressBarUI.PROPERTY_TRANSITION,
				Boolean.TRUE);
		progressBar.putClientProperty(
				CircularProgressBarUI.PROPERTY_SPARK_ACTIVE, Boolean.TRUE);

		addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				isShowing = isShowing();
			}
		});
	}

	protected String toHtml(Map<String, Map<String, Long>> data) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Map<String, Long>> entry : data.entrySet()) {
			sb.append(entry.getKey() + ":\n<table>\n");
			for (Entry<String, Long> entry2 : entry.getValue().entrySet()) {
				sb.append("\t<tr>\n");
				sb.append("\t\t\t<td>" + entry2.getKey() + "</td>\n");
				String v = entry2.getValue().equals(BarChartRenderer.ERROR_CODE)
						? "Error"
						: NumberFormat.getInstance().format(entry2.getValue());
				sb.append("\t\t\t<td>" + v + "</td>\n");
				sb.append("\t</tr>\n");
			}
			sb.append("</table>\n");
		}
		return sb.toString();
	}

	protected JPanel upperControls = new JPanel();
	protected JPanel lowerControls = new JPanel();

	@Override
	public final List<Runnable> getInitializationRunnables() {
		List<Runnable> returnValue = new LinkedList<>();

		Map<String, Map<String, SampleSet>> data = new HashMap<>();
		returnValue.addAll(getMeasurementRunnables(data));

		returnValue.add(new InstallResults(data));

		return returnValue;
	}

	/**
	 * Return a collection of Runnables that will be used to measure data.
	 * 
	 * @param data
	 *            the data to present to the user. Initially this will be empty;
	 *            the runnables this method creates should populate it.
	 */
	protected abstract Collection<Runnable> getMeasurementRunnables(
			Map<String, Map<String, SampleSet>> data);

}