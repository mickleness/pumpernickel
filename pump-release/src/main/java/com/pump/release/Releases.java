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
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;

/**
 * A set of jar files identified by {@link JarId} keys. This is derived
 * from the local file system and modified by build processes.
 */
public class Releases {
	File releaseProjectDir;
	Map<JarId, File> map = new HashMap<>();
	
	public Releases(File releaseProjectDir) {
		this.releaseProjectDir = releaseProjectDir;
		File comDir = new File(releaseProjectDir, "com");
		if(!comDir.exists())
			throw new RuntimeException("\"com\" not found in \"pump-release\".");
		File pumpDir = new File(comDir, "pump");
		if(!pumpDir.exists())
			throw new RuntimeException("\"com\\pump\" not found in \"pump-release\".");
		FileTreeIterator iter = new FileTreeIterator(pumpDir, "jar");
		while(iter.hasNext()) {
			File f = iter.next();
			String path = f.getAbsolutePath().substring(pumpDir.getAbsolutePath().length()+1);
			String artifactId = path.substring(0, path.indexOf(File.separator));
			path = path.substring(path.indexOf(File.separator)+1);
			String version = path.substring(0, path.indexOf(File.separator));
			JarId id = new JarId(artifactId, version);
			setJar(id, f);
		}
	}
	
	public File getDirectory() {
		return releaseProjectDir;
	}

	public File getJar(JarId jarId) {
		File f = map.get(jarId);
		if(f==Workspace.DELETED_FILE) {
			return null;
		}
		return f;
	}
	
	/**
	 * Assign a jar.
	 * 
	 * @param jarId the jar ID being assigned.
	 * @param file if null then this file is marked as removed/missing.
	 */
	public void setJar(JarId jarId, File file) {
		if(file==null)
			file = Workspace.DELETED_FILE;
		map.put(jarId, file);
	}

	/**
	 * Flush the releases map (mapping JarIds to jars) back to the file system.
	 * This only writes/copies files when changes are detected.
	 */
	public void save() throws IOException {
		File comDir = new File(releaseProjectDir, "com");
		if(!comDir.exists())
			throw new RuntimeException("\"com\" not found in \"pump-release\".");
		File pumpDir = new File(comDir, "pump");
		if(!pumpDir.exists())
			throw new RuntimeException("\"com\\pump\" not found in \"pump-release\".");
		
		for(Entry<JarId, File> entry : map.entrySet()) {
			File namedDir = new File(pumpDir, entry.getKey().artifactId);
			if(!namedDir.exists())
				namedDir.mkdirs();
			File versionDir = new File(namedDir, entry.getKey().version);
			if(!versionDir.exists())
				versionDir.mkdirs();
			String jarName = entry.getKey().artifactId+"-"+entry.getKey().version+".jar";
			File targetFile = new File(versionDir, jarName);
			
			File srcFile = entry.getValue();
			if(Workspace.DELETED_FILE.equals(srcFile)) {
				if(targetFile.exists()) {
					System.out.println("Deleted "+targetFile.getAbsolutePath());
					targetFile.delete();
				} else {
					System.out.println("Failed to create "+targetFile.getAbsolutePath());
				}
			} else {
				if(targetFile.exists() && IOUtils.zipEquals(targetFile, srcFile, ".*pom\\.properties"))
				{
					System.out.println("Skipped "+targetFile.getAbsolutePath()+" (no changes)");
					continue;
				}
				if(targetFile.exists()) {
					System.out.println("Replaced "+targetFile.getAbsolutePath());
				} else {
					System.out.println("Added "+targetFile.getAbsolutePath());
				}
				IOUtils.copy(srcFile, targetFile, true);
			}
		}
	}
}