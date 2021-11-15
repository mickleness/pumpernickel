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
package com.pump.showcase.app;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.pump.math.Fraction;
import com.pump.showcase.demo.ShowcaseDemo;

/**
 * This threads runs when the showcase app first launches. It is responsible for
 * loading all the showcase demos.
 * <p>
 * Here "loading" means first instantiating (which by itself is a non-trivial
 * cost), and after everything is instantiated we execute any additional tasks
 * (see {@link ShowcaseDemo#getInitializationRunnables()}).
 */
public class DemoLoadThread extends Thread {

	static class LoadingTask {
		ShowcaseDemoInfo demoInfo;
		Runnable runnable;

		LoadingTask(ShowcaseDemoInfo demoInfo, Runnable runnable) {
			this.demoInfo = demoInfo;
			this.runnable = runnable;
		}
	}

	ShowcaseDemoInfo[] demoElements;
	List<LoadingTask> pendingTasks = Collections
			.synchronizedList(new LinkedList<LoadingTask>());

	public DemoLoadThread(ShowcaseDemoInfo[] demoElements) {
		super("ShowcaseApp-loading-thread");
		this.demoElements = demoElements;
	}

	@Override
	public void run() {

		// step 1: instantiate all the demos
		for (ShowcaseDemoInfo sdi : demoElements) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						ShowcaseDemo demo = sdi.getDemo();
						List<Runnable> runnables = demo
								.getInitializationRunnables();
						if (runnables.isEmpty()) {
							sdi.setLoadingProgress(new Fraction(1, 1));
						} else {
							sdi.setLoadingProgress(
									new Fraction(0, runnables.size()));
							synchronized (pendingTasks) {
								for (Runnable runnable : runnables) {
									pendingTasks.add(
											new LoadingTask(sdi, runnable));
								}
							}
						}

					}
				});
			} catch (InvocationTargetException | InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		// step 2: execute all other tasks related to loading every demo:
		while (!pendingTasks.isEmpty()) {
			LoadingTask nextTask = pendingTasks.remove(0);
			Fraction fraction = nextTask.demoInfo.getLoadingProgress();
			try {
				nextTask.runnable.run();
			} catch (Throwable t) {
				t.printStackTrace();
				nextTask.demoInfo.addLoadingException(t);
			} finally {
				fraction = new Fraction(fraction.getNumerator() + 1,
						fraction.getDenominator());
				nextTask.demoInfo.setLoadingProgress(fraction);
			}
		}
	}

	/**
	 * Request that a demo be prioritized so it will finish loading first.
	 * <p>
	 * For ex: the user selects the QButton demo, then that demo's
	 * initialization runnables will be executed before anything else.
	 */
	public void request(ShowcaseDemoInfo demo) {
		synchronized (pendingTasks) {
			List<LoadingTask> urgentTasks = new LinkedList<>();
			Iterator<LoadingTask> iter = pendingTasks.iterator();
			while (iter.hasNext()) {
				LoadingTask task = iter.next();
				if (task.demoInfo == demo) {
					iter.remove();
					urgentTasks.add(task);
				}
			}
			pendingTasks.addAll(0, urgentTasks);
		}
	}
}