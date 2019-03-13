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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.pump.plaf.CircularProgressBarUI;

public abstract class ShowcaseChartDemo extends ShowcaseDemo {

	private static final long serialVersionUID = 1L;

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
		Map<String, Map<String, Long>> data;

		public InstallResults(Map<String, Map<String, Long>> data) {
			this.data = data;
		}

		@Override
		public void run() {
			BarChartRenderer r = new BarChartRenderer(data);
			BufferedImage bi = r.render(new Dimension(500, 1000));
			progressBar.setVisible(false);
			results.setVisible(true);
			results.add(new JLabel(new ImageIcon(bi)));
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
		progressBar.putClientProperty(
				CircularProgressBarUI.PROPERTY_TRANSITION, Boolean.TRUE);
		progressBar.putClientProperty(
				CircularProgressBarUI.PROPERTY_SPARK_ACTIVE, Boolean.TRUE);

		addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				isShowing = isShowing();
			}
		});

		Thread thread = new Thread(this.getClass().getSimpleName()) {
			public void run() {
				long i = 0;
				int[] limits = getCollectDataParamLimits();
				long iterationMax = 1;
				for (int a = 0; a < limits.length; a++) {
					iterationMax *= limits[a];
				}
				Map<String, Map<String, Long>> data = null;
				while (i < iterationMax) {
					if (!isShowing) {
						sleep();
					} else {
						try {
							int[] params = split(i, limits);
							data = collectData(params);
						} catch (Exception e) {
							/*
							 * If you find collectData is throwing an exception,
							 * instead catch that exception and store the value
							 * as BarChartRenderer.ERROR_CODE
							 */
							e.printStackTrace();
							return;
						}
						i++;
						SwingUtilities.invokeLater(new UpdateProgressRunnable(
								i, iterationMax));
					}
				}
				if (data == null)
					throw new NullPointerException("null data for "
							+ ShowcaseChartDemo.this.getClass().getName());

				System.out.println(ShowcaseChartDemo.this.getClass()
						.getSimpleName() + ":\n" + toHtml(data));
				Runnable installResults = new InstallResults(data);
				SwingUtilities.invokeLater(installResults);
			}

			private void sleep() {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					Thread.yield();
				}
			}
		};
		thread.start();
	}

	protected String toHtml(Map<String, Map<String, Long>> data) {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Map<String, Long>> entry : data.entrySet()) {
			sb.append(entry.getKey() + ":\n<table>\n");
			for (Entry<String, Long> entry2 : entry.getValue().entrySet()) {
				sb.append("\t<tr>\n");
				sb.append("\t\t\t<td>" + entry2.getKey() + "</td>\n");
				String v = entry2.getValue()
						.equals(BarChartRenderer.ERROR_CODE) ? "Error"
						: NumberFormat.getInstance().format(entry2.getValue());
				sb.append("\t\t\t<td>" + v + "</td>\n");
				sb.append("\t</tr>\n");
			}
			sb.append("</table>\n");
		}
		return sb.toString();
	}

	/**
	 * Split in iteration index into a series of parameters.
	 * <p>
	 * For example, if paramSizes is {2, 3, 2}, then:
	 * 
	 * <pre>
	 * iterationIndex = 0, return = {0, 0, 0}
	 * iterationIndex = 1, return = {1, 0, 0}
	 * iterationIndex = 2, return = {0, 1, 0}
	 * iterationIndex = 3, return = {1, 1, 0}
	 * iterationIndex = 4, return = {0, 2, 0}
	 * iterationIndex = 5, return = {1, 2, 0}
	 * iterationIndex = 6, return = {0, 0, 1}
	 * iterationIndex = 7, return = {1, 0, 1}
	 * iterationIndex = 8, return = {0, 1, 1}
	 * iterationIndex = 9, return = {1, 1, 1}
	 * iterationIndex = 10, return = {0, 2, 1}
	 * iterationIndex = 11, return = {1, 2, 1}
	 * iterationIndex = 12, return = null
	 * </pre>
	 * 
	 * @param iterationIndex
	 *            the value to split into parameters.
	 * @param paramSizes
	 *            a list of the maximum value of each parameter.
	 * @return the iteration index split into separate parameters, or null if
	 *         the iterationIndex is too large to split into parameters based on
	 *         paramSize
	 */
	private int[] split(long iterationIndex, int... paramSizes) {
		int[] returnValue = new int[paramSizes.length];
		for (int a = 0; a < paramSizes.length; a++) {
			returnValue[a] = (int) (iterationIndex % paramSizes[a]);
			iterationIndex /= paramSizes[a];
		}
		if (iterationIndex != 0)
			return null;

		return returnValue;
	}

	protected JPanel upperControls = new JPanel();
	protected JPanel lowerControls = new JPanel();

	/**
	 * Calculate the data to show in charts for this demo.
	 * <p>
	 * This method will be repeatedly called until all possible combinations of
	 * parameters have been passed in.
	 * 
	 * @throws Exception
	 */
	protected abstract Map<String, Map<String, Long>> collectData(int... params)
			throws Exception;

	protected abstract int[] getCollectDataParamLimits();
}