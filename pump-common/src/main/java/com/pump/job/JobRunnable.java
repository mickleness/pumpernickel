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
package com.pump.job;


/** This wraps a Runnable inside a <code>Job</code>.
 * When possible it is better to use a robust model (like a Job)
 * that offers the ability to cancel jobs, add listeners, and 
 * (through listeners) valuable UI feedback.
 */
public class JobRunnable extends Job {
	Runnable runnable;
	
	JobRunnable(Runnable r) {
		super();
		runnable = r;
	}

	@Override
	protected void runJob() {
		runnable.run();
	};
	
}