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
package com.pump.desktop;

import java.text.NumberFormat;

import com.pump.thread.EventDispatchThreadMonitor.Listener;
import com.pump.thread.ThreadProfiler;

/**
 * This turns on a ThreadProfiler while the EDT is unresponsive.
 *
 */
class EventDispatchThreadProfilerListener implements Listener {

	ThreadProfiler profiler;

	@Override
	public void becameUnresponsive(Thread eventDispatchThread,
			long unresponsiveMillis, long lastSuccessfulPingMillis) {
		synchronized (this) {
			if (profiler == null) {
				profiler = new ThreadProfiler();
				profiler.setActive(true);

				System.out.println(
						"The event dispatch thread was unresponsive for approximately "
								+ NumberFormat.getInstance()
										.format(unresponsiveMillis)
								+ " millis, so I'm starting a ThreadProfiler.");
			} else {
				// I'm not sure if this condition is possible, but
				// just in case
			}
		}
	}

	@Override
	public void becameResponsive(Thread eventDispatchThread,
			long lastSuccessfulPingMillis, long unresponsiveMillis) {
		synchronized (this) {
			if (profiler != null) {
				profiler.setActive(false);
				String output = profiler.getOutput();
				System.out.println(
						"The event dispatch thread recovered. Here is the output of "
								+ NumberFormat.getInstance()
										.format(unresponsiveMillis)
								+ " milliseconds of profiling activity:\n"
								+ output);
				profiler = null;
			}
		}

	}

}