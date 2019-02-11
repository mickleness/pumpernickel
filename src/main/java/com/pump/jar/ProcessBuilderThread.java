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
package com.pump.jar;

import java.util.List;

import com.pump.util.BufferedPipe;

class ProcessBuilderThread {
	String name;
	boolean outputCommand;
	/**
	 * If this process exits normally, then this Runnable will immediately be
	 * invoked.
	 */
	public Runnable postRunnable = null;
	public final ProcessBuilder processBuilder = new ProcessBuilder(
			new String[] {});

	public ProcessBuilderThread(String name, boolean outputCommand) {
		this.name = name;
		this.outputCommand = outputCommand;
	}

	private int ctr = 1;

	public void start(boolean blocking) {
		final String threadName = ctr == 1 ? "Execute " + name : "Execute "
				+ name + " " + (ctr++);
		Thread thread = new Thread(threadName) {

			protected String toString(ProcessBuilder pb) {
				List<String> list = pb.command();
				String returnValue = "";
				for (String s : list) {
					returnValue = returnValue + " " + s;
				}
				return returnValue.trim();
			}

			public void run() {
				try {
					if (outputCommand) {
						System.out.println(toString(processBuilder));
					}
					Process process = processBuilder.start();
					new BufferedPipe(process.getInputStream(), System.out, "\t");
					new BufferedPipe(process.getErrorStream(), System.err, "\t");
					int code = process.waitFor();
					if (code != 0) {
						System.err.println("\t" + threadName + " error: "
								+ code);
					} else {
						System.out.println("\t" + threadName + " complete");
						if (postRunnable != null)
							postRunnable.run();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		if (blocking) {
			thread.run();
		} else {
			thread.start();
		}
	}
}