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
import java.util.HashMap;
import java.util.Map;

import com.pump.release.Project.MissingPomException;

public class Workspace {
	/** A placeholder object a file that should be deleted/missing. */
	static File DELETED_FILE = new File("deleted file");
	
	File pumpernickelDir = new File(System.getProperty("user.dir")).getParentFile();
	Map<JarId, Project> projects = new HashMap<>();
	File releaseDir;
	Releases releases;
	
	public Workspace() {
		releaseDir = new File(pumpernickelDir, "pump-release");
		if(!releaseDir.exists())
			throw new RuntimeException("\"pump-release\" directory not found.");

		releases = new Releases(releaseDir);
		
		for(File child : pumpernickelDir.listFiles()) {
			if(child.isDirectory() && child.getName().startsWith("pump-")) {
				try {
					Project p = new Project(releases, child);
					projects.put(p.getJarId(), p);
				} catch(MissingPomException e) {
					if(!child.equals(releaseDir)) {
						e.printStackTrace();
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Return the Releases object used to manage jar releases.
	 */
	public Releases getReleases() {
		return releases;
	}
	
	/**
	 * Return a copy of all the available Projects keyed by their {@link JarId}.
	 */
	public Map<JarId, Project> getProjects() {
		return new HashMap<>(projects);
	}

	/**
	 * Return the master "Pumpernickel" directory that contains all the subprojects.
	 */
	public File getDirectory() {
		return pumpernickelDir;
	}
	
	
}