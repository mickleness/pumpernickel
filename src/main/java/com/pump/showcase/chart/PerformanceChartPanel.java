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
package com.pump.showcase.chart;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.function.Function;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.pump.math.Fraction;
import com.pump.plaf.CircularProgressBarUI;
import com.pump.showcase.app.ShowcaseDemoInfo;
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

	private static final String CARD_LOADING = "loading";
	private static final String CARD_RESULTS = "results";

	private static final String PARAMETER_SAMPLE_INDEX = "sample index";
	public final static String PARAMETER_NAME = "name";

	public final static String PARAMETER_CHART_NAME = "chart name";

	static class PerformanceResult {
		List<Long> times = new ArrayList<>();

		public synchronized void addTime(long time) {
			times.add(time);
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[ time = " + getMedianTime() + "]";
		}

		public synchronized long getMedianTime() {
			return getMedian(times);
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

			public void record(WorkerTask task, long time) {
				Map<String, Object> parameters = new HashMap<>();
				parameters.putAll(task.parameters);
				parameters.remove(PARAMETER_SAMPLE_INDEX);

				PerformanceResult result = resultMap.get(parameters);
				if (result == null) {
					result = new PerformanceResult();
					resultMap.put(parameters, result);
				}
				result.addTime(time);

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
			final Map<String, Object> parameters;
			final Cancellable cancellable;

			public WorkerTask(WorkerTaskGroup group, Map<String, Object> parameters,
					Cancellable cancellable) {
				this.parameters = parameters;
				this.group = group;
				group.addTask(this);
				this.cancellable = cancellable;
			}

			@Override
			public void run() {
				if (cancellable.isCancelled())
					return;

				group.dataGenerator.setupSample(parameters);

				try {
					Runtime.getRuntime().gc();

					long time = System.currentTimeMillis();

					try {
						group.dataGenerator.runSample(parameters);
					} catch (Throwable t) {
						t.printStackTrace();
					} finally {
						time = System.currentTimeMillis() - time;
						group.record(this, time);
					}
				} finally {
					group.dataGenerator.tearDownSample(parameters);
				}
			}

		}

		static int threadCtr = 0;

		Thread workerThread;

		final List<WorkerTask> tasks = new LinkedList<>();

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
			ShowcaseDemoInfo i = getShowcaseDemoInfo(panel);
			if (i != null)
				i.setLoadingProgress(new Fraction(0, 1));

			synchronized (tasks) {
				WorkerTaskGroup group = createWorkerTaskGroup(panel, dataGenerator, 5, cancellable, listener);
				tasks.addAll(group.getWorkerTasks());
				checkWorkerThread();
			}
		}

		private ShowcaseDemoInfo getShowcaseDemoInfo(JComponent jc) {
			while (jc != null) {
				if (jc instanceof ShowcaseDemo) {
					return ((ShowcaseDemo)jc).getDemoInfo();
				}
				
				if (jc.getParent() instanceof JComponent) {
					jc = (JComponent) jc.getParent();
				} else {
					break;
				}
			}
			return null;
		}

		private WorkerTaskGroup createWorkerTaskGroup(PerformanceChartPanel panel, ChartDataGenerator dataGenerator, int sampleCount,
													  Cancellable cancellable, Listener listener) {
			Listener multiListener = new Listener() {

				@Override
				public void progressUpdate(int completedIterations, int totalIterations) {
					listener.progressUpdate(completedIterations, totalIterations);
					ShowcaseDemoInfo i = getShowcaseDemoInfo(panel);
					if (i != null)
						i.setLoadingProgress(new Fraction(completedIterations, totalIterations));
				}

				@Override
				public void complete(Map<Map<String, ?>, PerformanceResult> resultsMap) {
					listener.complete(resultsMap);
					ShowcaseDemoInfo i = getShowcaseDemoInfo(panel);
					if (i != null)
						i.setLoadingProgress(new Fraction(1, 1));
				}

			};

			WorkerTaskGroup group = new WorkerTaskGroup(panel, dataGenerator,
					multiListener);

			for (Map<String, ?> parameters : dataGenerator.getParameters()) {
				for (int sampleIndex = 0; sampleIndex < sampleCount; sampleIndex++) {
					Map<String, Object> newParameters = new HashMap<>();
					newParameters.putAll(parameters);
					newParameters.put(PARAMETER_SAMPLE_INDEX,
							Integer.valueOf(sampleIndex));
					if (!newParameters.containsKey(PARAMETER_CHART_NAME))
						newParameters.put(PARAMETER_CHART_NAME, "Time");

					new WorkerTask(group, newParameters, cancellable);
				}
			}
			return group;
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

	JTextArea resultsLowerTextArea = new JTextArea();

	AbstractAction copyAction_text, copyAction_html;
	Map<String, Chart> chartMap;
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

		resultsLowerTextArea.setOpaque(false);
		resultsLowerTextArea.setEditable(false);
		resultsLowerTextArea.setWrapStyleWord(true);
		resultsLowerTextArea.setLineWrap(true);
		resultsLowerTextArea.setFont( new Font("default", 0, resultsLowerTextArea.getFont().getSize()) );

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
				String str = BarChartRenderer.toText(chartMap.values());
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(new StringSelection(str), null);
			}

		};
		copyAction_html = new AbstractAction("Copy as HTML") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = BarChartRenderer.toHtml(chartMap.values());
				Toolkit.getDefaultToolkit().getSystemClipboard()
						.setContents(new StringSelection(str), null);
			}

		};

		setChartDescription("");
	}

	protected void populateResultsPanel(
			Map<Map<String, ?>, PerformanceResult> resultsMap) {
		chartMap = new LinkedHashMap<>();
		for (Map.Entry<Map<String, ?>, PerformanceResult> entry : resultsMap
				.entrySet()) {
			Map<String, ?> params = entry.getKey();
			String name = (String) params.get(PARAMETER_NAME);
			if (name == null)
				throw new NullPointerException(
						"PARAMETER_NAME must be defined in the parameters to create chart.");
			PerformanceResult r = entry.getValue();

			String chartName = (String) params.get(PARAMETER_CHART_NAME);
			Chart chart = chartMap.get(chartName);
			if (chart == null) {
				chart = new Chart(chartName);
				chartMap.put(chartName, chart);
				chart.setValueFormatter(new Function<Number, String>() {
					DecimalFormat format = new DecimalFormat("0.0");

					@Override
					public String apply(Number t) {
						if (t.doubleValue() == 0)
							return "0 s";
						return format.format(t.doubleValue() / 1000.0) + " s";
					}
				});
			}
			long time = r.getMedianTime();
			chart.getSeriesData().add(
					new AbstractMap.SimpleEntry<>(name, time));
		}

		resultsPanel.removeAll();
		resultsPanel.setLayout(new GridBagLayout());

		List<Chart> chartList = new ArrayList<>(chartMap.values());
		BarChartRenderer chartRenderer = new BarChartRenderer(chartList);
		BarChartPanel p = new BarChartPanel(chartRenderer, 450);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.insets = new Insets(3, 3, 3, 3);

		resultsPanel.add(p, gbc);
		gbc.gridy++;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets.top += 6;
		resultsPanel.add(resultsLowerTextArea, gbc);

		ContextualMenuHelper.clear(resultsPanel);
		ContextualMenuHelper.add(resultsPanel, copyAction_text);
		ContextualMenuHelper.add(resultsPanel, copyAction_html);
		ShowcaseDemo.installExportJVGContextMenu(resultsPanel, p,
				name);
	}

	public void reset(ChartDataGenerator generator) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				ContextualMenuHelper.clear(resultsPanel);
				chartMap = null;

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

	/**
	 * Set an optional block of text below the graph.
	 */
	public void setChartDescription(String chartDescription) {
		if (chartDescription == null)
			chartDescription = null;
		resultsLowerTextArea.setText(chartDescription);
		resultsLowerTextArea.setVisible(!chartDescription.isEmpty());
	}
}