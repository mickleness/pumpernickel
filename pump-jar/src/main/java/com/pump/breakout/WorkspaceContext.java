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
package com.pump.breakout;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;
import com.pump.io.java.JavaClassSummary;

/**
 * This describes the java and jar files available in a workspace.
 */
public class WorkspaceContext {

	/** All the directories of resources we've added/searched. */
	protected Collection<File> sourcePaths = new HashSet<>();
	/** A map of jar filenames to the jar. */
	protected Map<String, File> fileNameToJar = new HashMap<>();
	/** A map of classnames to java files. */
	protected Map<String, File> classNameToFileMap = new HashMap<>();

	/** Create a new WorkspaceContext based on the current user.dir folder. */
	public WorkspaceContext() throws IOException {
		addSourcePath(new File(System.getProperty("user.dir")));
	}
	
	/** Create a new WorkspaceContext with an initial source path.
	 * 
	 * @param sourcePath the directory containing java/jar files.
	 */
	public WorkspaceContext(File sourcePath) throws IOException {
		addSourcePath(sourcePath);
	}
	
	/**
	 * Return a map of jar file names to jars.
	 */
	public synchronized Map<String, File> getJars() {
		return new HashMap<>(fileNameToJar);
	}
	
	/**
	 * Return a map of class names to Java file.
	 */
	public synchronized Map<String, File> getJavaFiles() {
		return new HashMap<>(classNameToFileMap);
	}

	/**
	 * Add a source path to this context.
	 * <p>
	 * This method exhaustively searches this directory for all .java and .jar files
	 */
	public synchronized void addSourcePath(File sourcePath) throws IOException {
		sourcePaths.add(sourcePath);
		FileTreeIterator iter = new FileTreeIterator(sourcePath, "java", "jar");
		while(iter.hasNext()) {
			File file = iter.next();
			if(file.getAbsolutePath().toLowerCase().endsWith("java")) {
				String name = JavaClassSummary.getClassName(file);
				File existingFile = classNameToFileMap.get(name);
				if(existingFile!=null && !IOUtils.equals(existingFile, file)) {
					throw new RuntimeException("The class name \""+name+"\" was defined in "+existingFile.getAbsolutePath()+" and "+file.getAbsolutePath()+".");
				}
				classNameToFileMap.put(name, file);
			} else if(file.getAbsolutePath().toLowerCase().endsWith("jar")) {
				File existingJar = fileNameToJar.get(file.getName());
				if(existingJar!=null && !IOUtils.equals(existingJar, file)) {
					throw new RuntimeException("The jar name \""+file.getName()+"\" was defined in "+existingJar+" and "+file.getAbsolutePath()+".");
				}
				fileNameToJar.put(file.getName(), file);
			}
		}
	}

	/**
	 * Return the java file associated with a class name, or null if that classname
	 * isn't available in this context.
	 */
	public synchronized File getJavaFile(String className) {
		return classNameToFileMap.get(className);
	}

	/**
	 * Return the classnames of java files this context contains.
	 */
	public Collection<String> getClassNames() {
		return Collections.unmodifiableCollection(classNameToFileMap.keySet());
	}

	/**
	 * Return the source paths this context uses.
	 */
	public Collection<File> getSourcePaths() {
		return new HashSet<>(sourcePaths);
	}
}