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
package com.pump.release;

import java.io.File;

import com.pump.desktop.temp.TempFileManager;

/**
 * Instructions:
 * <ol>
 * <li>Run this app</li>
 * <li>Right-click pumpernickel/pom.xml and select "Run As...". Select
 * "Maven install"</li>
 * <li>Run "BackupJars" app.</li>
 * </ol>
 */
public class ReleaseApp {
	public static void main(String[] args) throws Exception {
		TempFileManager.initialize("Pumpernickel-Prep");
		ReleaseApp release = new ReleaseApp();
		release.run();
	}

	public void run() throws Exception {
		System.out.println("Running UpdateSourceCodeHeader:");
		UpdateSourceCodeHeader headerUpdater = new UpdateSourceCodeHeader();
		File dir = new File(System.getProperty("user.dir"));
		headerUpdater.run(dir);
		System.exit(0);
	}
}