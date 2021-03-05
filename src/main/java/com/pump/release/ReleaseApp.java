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
import com.pump.io.IOUtils;
import com.pump.showcase.app.PumpernickelShowcaseApp;

/**
 * Instructions:
 * <ol>
 * <li>Run this app</li>
 * <li>Right-click pumpernickel/pom.xml and select "Run As...". Select "Maven
 * install"</li>
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

		String filename = "Pumpernickel.jar";
		Project project = new Project(dir, PumpernickelShowcaseApp.class,
				filename);
		File versionedJar = project.buildJar(true, "release", "jars",
				PumpernickelShowcaseApp.VERSION);
		File currentJar = new File(versionedJar.getParentFile().getParentFile(),
				filename);
		IOUtils.copy(versionedJar, currentJar, true);

		System.out.println("Created/updated " + versionedJar.getAbsolutePath());
		System.out.println("Created/updated " + currentJar.getAbsolutePath());

		System.exit(0);
	}
}