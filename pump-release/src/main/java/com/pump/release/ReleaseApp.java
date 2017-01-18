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

import com.pump.desktop.temp.TempFileManager;

public class ReleaseApp {
	public static void main(String[] args) throws Exception {
		TempFileManager.initialize("Pumpernickel-Prep");
		ReleaseApp release = new ReleaseApp();
		release.run();
	}
	
	public void run() throws Exception {
		Workspace workspace = new Workspace();

		System.out.println();
		System.out.println("Running UpdateSourceCodeHeader:");
		UpdateSourceCodeHeader headerUpdater = new UpdateSourceCodeHeader();
		headerUpdater.run(workspace.getDirectory());

		System.out.println();
		System.out.println("Running BlurbIndexBuilder:");
		BlurbIndexBuilder blurbIndexBuilder = new BlurbIndexBuilder();
		blurbIndexBuilder.run(workspace);

		System.out.println();
		System.out.println("Running JavadocBuilder:");
		JavadocBuilder javadocBuilder = new JavadocBuilder();
		javadocBuilder.run(workspace);
		
		System.out.println();
		System.out.println("Running FavenReleaseBuilder:");
		FavenReleaseBuilder releaseBuilder = new FavenReleaseBuilder();
		releaseBuilder.run(workspace.getProjects().values(), workspace.getReleases());
		
		System.out.println();
		System.out.println("Done.");
	}
}