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
package com.pump.showcase.app;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

/**
 * This threads runs when the showcase app first launches. It is responsible for
 * loading all the showcase demos.
 */
public class DemoLoadThread extends Thread {
	ShowcaseDemoInfo[] demoElements;

	public DemoLoadThread(ShowcaseDemoInfo[] demoElements) {
		super("ShowcaseApp-loading-thread");
		this.demoElements = demoElements;
	}

	@Override
	public void run() {
		for (ShowcaseDemoInfo sdi : demoElements) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						sdi.getDemo();
					}
				});
			} catch (InvocationTargetException | InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
}