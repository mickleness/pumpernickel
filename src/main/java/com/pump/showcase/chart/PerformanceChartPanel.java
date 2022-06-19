package com.pump.showcase.chart;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.pump.plaf.CircularProgressBarUI;
import com.pump.showcase.demo.ShowcaseDemo;
import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.swing.ContextualMenuHelper;

/**
 * This is a panel for BarChartGenerators that examine performance.
 * <p>
 * This includes a "loading" panel with a a progress bar while the samples are
 * being collected.
 */
public class PerformanceChartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public final static String CHART_NAME_TIME = "Time";
	public final static String CHART_NAME_MEMORY = "Memory";

	private static final String CARD_LOADING = "loading";
	private static final String CARD_RESULTS = "results";

	private static final String PARAMETER_SAMPLE_INDEX = "sample index";
	public final static String PARAMETER_NAME = "name";

	static class PerformanceResult {
		List<Long> times = new ArrayList<>();
		List<Long> memories = new ArrayList<>();

		public synchronized void addTime(long time) {
			times.add(time);

		}

		public synchronized void addMemory(long memory) {
			memories.add(memory);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[ time = " + getMedianTime()
					+ " memory = " + getMedianMemory() + "]";
		}

		public synchronized long getMedianTime() {
			return getMedian(times);
		}

		public synchronized long getMedianMemory() {
			return getMedian(memories);
		}

		private synchronized long getMedian(List<Long> elements) {
			Collections.sort(elements);
			if (elements.size() > 0)
				return Long.valueOf(elements.get(elements.size() / 2));
			return -1;
		}
	}

	interface Listener {
		void progressUpdate(int completedIterations, int totalIterations);

		void complete(Map<Map<String, ?>, PerformanceResult> resultsMap);
	}

	static class WorkerTaskManager {

		class WorkerTaskGroup {

			final Listener listener;
			final PerformanceChartPanel panel;
			final ChartDataGenerator dataGenerator;
			final List<WorkerTask> tasks = new LinkedList<>();
			final Map<Map<String, ?>, PerformanceResult> resultMap = new HashMap<>();

			int recordCtr = 0;

			public WorkerTaskGroup(PerformanceChartPanel panel,
					ChartDataGenerator dataGenerator, Listener listener) {
				this.panel = panel;
				this.dataGenerator = dataGenerator;
				this.listener = listener;
			}

			public void record(WorkerTask task, long time, long usedMemory) {
				Map<String, Object> parameters = new HashMap<>();
				parameters.putAll(task.parameters);
				parameters.remove(PARAMETER_SAMPLE_INDEX);

				PerformanceResult result = resultMap.get(parameters);
				if (result == null) {
					result = new PerformanceResult();
					resultMap.put(parameters, result);
				}
				if (task.measureMemory && usedMemory > 0) {
					result.addMemory(usedMemory);
				}
				if (task.measureTime) {
					result.addTime(time);
				}

				recordCtr++;
				try {
					if (recordCtr == tasks.size()) {
						listener.complete(resultMap);
					} else {
						listener.progressUpdate(recordCtr, tasks.size());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			public Collection<? extends WorkerTask> getWorkerTasks() {
				return tasks;
			}

			public void addTask(WorkerTask task) {
				tasks.add(task);
			}

		}

		class WorkerTask implements Runnable {
			final WorkerTaskGroup group;
			final Map<String, ?> parameters;
			final boolean measureTime;
			final boolean measureMemory;

			public WorkerTask(WorkerTaskGroup group, Map<String, ?> parameters,
					boolean measureTime, boolean measureMemory) {
				this.parameters = parameters;
				this.measureTime = measureTime;
				this.measureMemory = measureMemory;
				this.group = group;
				group.addTask(this);
			}

			@Override
			public void run() {
				if (measureTime) {
					group.dataGenerator.setupTimedSample(parameters);
				} else if (measureMemory) {
					group.dataGenerator.setupMemorySample(parameters);
				}
				try {
					Runtime.getRuntime().gc();
					Runtime.getRuntime().runFinalization();
					Runtime.getRuntime().gc();
					Runtime.getRuntime().runFinalization();

					long time = System.currentTimeMillis();
					long freeMemory = Runtime.getRuntime().freeMemory();
					long totalMemory = Runtime.getRuntime().totalMemory();

					try {
						if (measureTime) {
							group.dataGenerator.runTimedSample(parameters);
						} else if (measureMemory) {
							group.dataGenerator.runMemorySample(parameters);
						}
					} catch (Throwable t) {
						t.printStackTrace();
					} finally {
						time = System.currentTimeMillis() - time;
						long usedMemory = freeMemory
								- Runtime.getRuntime().freeMemory();

						if (Runtime.getRuntime().totalMemory() != totalMemory)
							usedMemory = -1;

						group.record(this, time, usedMemory);
					}
				} finally {
					if (measureTime) {
						group.dataGenerator.tearDownTimedSample(parameters);
					} else if (measureMemory) {
						group.dataGenerator.tearDownMemorySample(parameters);
					}
				}
			}

		}

		static int threadCtr = 0;

		Thread workerThread;

		List<WorkerTask> tasks = new LinkedList<>();

		Runnable taskManagerRunnable = new Runnable() {

			@Override
			public void run() {
				while (true) {
					WorkerTask currentTask = null;
					synchronized (tasks) {
						if (tasks.isEmpty())
							return;
						currentTask = tasks.remove(0);
					}
					currentTask.run();
				}
			}
		};

		public void addTasks(PerformanceChartPanel panel,
				ChartDataGenerator dataGenerator, Cancellable cancellable,
				Listener listener) {

			WorkerTaskGroup group = new WorkerTaskGroup(panel, dataGenerator,
					listener);

			synchronized (tasks) {

				ChartDataGenerator.ExecutionMode mode = dataGenerator
						.getExecutionMode();
				if (mode == ChartDataGenerator.ExecutionMode.RECORD_TIME_AND_MEMORY_SIMULTANEOUSLY) {
					int sampleCount = Math.max(
							dataGenerator.getMemorySampleCount(),
							dataGenerator.getTimedSampleCount());
					createWorkerTasks(group, sampleCount,
							dataGenerator.getTimedParameters(), true, true);
				}
				if (mode == ChartDataGenerator.ExecutionMode.RECORD_TIME_AND_MEMORY_SEPARATELY
						|| mode == ChartDataGenerator.ExecutionMode.RECORD_TIME_ONLY) {
					createWorkerTasks(group,
							dataGenerator.getTimedSampleCount(),
							dataGenerator.getTimedParameters(), true, false);
				}
				if (mode == ChartDataGenerator.ExecutionMode.RECORD_TIME_AND_MEMORY_SEPARATELY
						|| mode == ChartDataGenerator.ExecutionMode.RECORD_MEMORY_ONLY) {
					createWorkerTasks(group,
							dataGenerator.getMemorySampleCount(),
							dataGenerator.getMemoryParameters(), false, true);
				}
				tasks.addAll(group.getWorkerTasks());

				checkWorkerThread();
			}
		}

		private void createWorkerTasks(WorkerTaskGroup group, int sampleCount,
				List<Map<String, ?>> parameterList, boolean recordTime,
				boolean recordMemory) {
			for (Map<String, ?> parameters : parameterList) {
				for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
					Map<String, Object> newParameters = new HashMap<>();
					newParameters.putAll(parameters);
					newParameters.put(PARAMETER_SAMPLE_INDEX,
							Integer.valueOf(sampleIndex));
					new WorkerTask(group, newParameters, recordTime,
							recordMemory);
				}
			}
		}

		/**
		 * Move all the tasks related to a PerformanceChartPanel to the front of
		 * the queue.
		 */
		public void bringToFront(PerformanceChartPanel panel) {
			synchronized (tasks) {
				Collection<WorkerTask> tasksToMoveToFront = new LinkedList<>();
				Iterator<WorkerTask> iter = tasks.iterator();
				while (iter.hasNext()) {
					WorkerTask t = iter.next();
					if (t.group.panel == panel) {
						iter.remove();
						tasksToMoveToFront.add(t);
					}
				}
				tasks.addAll(0, tasksToMoveToFront);
			}
		}

		/**
		 * Make sure our worker thread is alive; create a new one if needed.
		 */
		private void checkWorkerThread() {
			Thread t = workerThread;
			if (t == null || !t.isAlive()) {
				t = new Thread(taskManagerRunnable,
						"WorkerTaskManager-" + (threadCtr++));
				t.start();
				workerThread = t;
			}
		}

	}

	static WorkerTaskManager manager = new WorkerTaskManager();

	CardLayout cardLayout = new CardLayout();

	JPanel loadingPanel = new JPanel();
	JPanel resultsPanel = new JPanel();

	Cancellable workerThreadCancellable;
	Listener managerListener = new Listener() {

		@Override
		public void progressUpdate(int completedIterations,
				int totalIterations) {
			int percent = (int) (100 * completedIterations / totalIterations);
			progressBar.setValue(percent);
		}

		@Override
		public void complete(
				Map<Map<String, ?>, PerformanceResult> resultsMap) {
			populateResultsPanel(resultsMap);
			cardLayout.show(PerformanceChartPanel.this, CARD_RESULTS);
		}

	};
	JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0,
			100);

	AbstractAction copyAction_text, copyAction_html;
	List<Chart> charts = null;
	String name;

	/**
	 * @param name
	 *            A human-readable name for this chart. Something like
	 *            "ImageLoader Performance Results".
	 */
	public PerformanceChartPanel(String name) {
		setLayout(cardLayout);
		this.name = name;
		add(loadingPanel, CARD_LOADING);
		add(resultsPanel, CARD_RESULTS);

		setOpaque(false);
		loadingPanel.setOpaque(false);
		resultsPanel.setOpaque(false);

		addHierarchyListener(new HierarchyListener() {

			boolean wasShowing = false;

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				boolean isShowing = isShowing();
				if (isShowing && !wasShowing) {
					manager.bringToFront(PerformanceChartPanel.this);
				}
				wasShowing = isShowing;
			}

		});

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(9, 9, 9, 9);
		progressBar.setPreferredSize(new Dimension(100, 100));
		loadingPanel.add(progressBar, gbc);

		progressBar.setUI(new CircularProgressBarUI());

		copyAction_text = new AbstractAction("Copy as Text") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = BarChartRenderer.toText(charts);
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(new StringSelection(str), null);
			}

		};
		copyAction_html = new AbstractAction("Copy as HTML") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = BarChartRenderer.toHtml(charts);
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(new StringSelection(str), null);
			}

		};
	}

	protected void populateResultsPanel(
			Map<Map<String, ?>, PerformanceResult> resultsMap) {

		charts = new LinkedList<>();
		charts.add(new Chart(CHART_NAME_TIME));
		charts.add(new Chart(CHART_NAME_MEMORY));

		for (Map.Entry<Map<String, ?>, PerformanceResult> entry : resultsMap
				.entrySet()) {
			Map<String, ?> params = entry.getKey();
			String name = (String) params.get(PARAMETER_NAME);
			if (name == null)
				throw new NullPointerException(
						"PARAMETER_NAME must be defined in the parameters to create chart.");
			PerformanceResult r = entry.getValue();
			if (r.getMedianTime() > 0)
				charts.get(0).getSeriesData().add(
						new AbstractMap.SimpleEntry<>(name, r.getMedianTime()));
			if (r.getMedianMemory() > 0)
				charts.get(1).getSeriesData().add(new AbstractMap.SimpleEntry<>(
						name, r.getMedianMemory()));
		}

		resultsPanel.removeAll();
		resultsPanel.setLayout(new GridBagLayout());

		int width = 400;
		int height = 270;
		BarChartRenderer chartRenderer = new BarChartRenderer(charts);
		BarChartPanel p = new BarChartPanel(chartRenderer,
				new Dimension(width, height), new Dimension(width, height));
		resultsPanel.add(p);

		ContextualMenuHelper.add(resultsPanel, copyAction_text);
		ContextualMenuHelper.add(resultsPanel, copyAction_html);
		ShowcaseDemo.installExportJVGContextMenu(resultsPanel, resultsPanel,
				name);
	}

	public void reset(ChartDataGenerator generator) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				ContextualMenuHelper.clear(resultsPanel);
				charts = null;

				progressBar.setValue(0);
				cardLayout.show(PerformanceChartPanel.this, CARD_LOADING);

				if (workerThreadCancellable != null)
					workerThreadCancellable.cancel();

				workerThreadCancellable = new BasicCancellable();
				manager.addTasks(PerformanceChartPanel.this, generator,
						workerThreadCancellable, managerListener);
			}
		};
		if (SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			SwingUtilities.invokeLater(r);
		}
	}
}