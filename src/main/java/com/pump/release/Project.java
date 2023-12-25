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
package com.pump.release;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.*;
import java.util.Map;

import com.pump.io.FileTreeIterator;
import com.pump.io.FileUtils;
import com.pump.io.IOUtils;
import com.pump.io.parser.java.JavaClassSummary;

public class Project {
	File projectDir;
	Class mainClass;
	String jarFileName;

	public Project(File projectDir, Class mainClass, String jarFileName) {
		this.projectDir = projectDir;
		this.mainClass = mainClass;
		this.jarFileName = jarFileName;

	}

	/**
	 * @param destSubdirectories the path to write this jar in, such as ["release", "jars", "v1.0"]
	 * @return the newly created jar file
	 */
	public File buildJar(boolean includeJavaSource, String... destSubdirectories)
			throws IOException {

		// TODO: this just dumps the existing compiled code in a jar
		// we need to replace this with logic that actually compiles
		// against the specified dependencies. This will let us stay
		// true to the intention of the dependency version, and it will
		// let us abort if we know there are compiler errors.

		File currentDir = projectDir;
		for (int a = 0; a < destSubdirectories.length; a++) {
			File newDir = new File(currentDir, destSubdirectories[a]);
			FileUtils.mkdirs(newDir);
			currentDir = newDir;
		}

		File jarFile = new File(currentDir, jarFileName);
		if (jarFile.exists()) {
			jarFile.delete();
		}
		FileUtils.createNewFile(jarFile);

		File classesDir = findClassesDir();

		if (classesDir == null || !classesDir.exists())
			throw new RuntimeException("\"target\\classes\" did not exist in "
					+ projectDir.getAbsolutePath());

		Manifest manifest = createManifest();
		Collection<String> entries = new HashSet<>();
		try (FileOutputStream fileOut = new FileOutputStream(jarFile)) {
			try (JarOutputStream jarOut = new JarOutputStream(fileOut,
					manifest)) {
				FileTreeIterator iter = new FileTreeIterator(classesDir);
				while (iter.hasNext()) {
					File f = iter.next();
					if ((!f.isDirectory()) && (!f.isHidden())) {
						String path = f.getAbsolutePath().substring(
								classesDir.getAbsolutePath().length() + 1);
						path = path.replace(File.separatorChar, '/');
						entries.add(path);
						jarOut.putNextEntry(new JarEntry(path));
						IOUtils.write(f, jarOut);
					}
				}

				if (includeJavaSource) {
					for (File javaSourceDir : getJavaSourceDirs()) {
						iter = new FileTreeIterator(javaSourceDir);
						while (iter.hasNext()) {
							File f = iter.next();
							if ((!f.isDirectory()) && (!f.isHidden())) {
								String path = f.getAbsolutePath().substring(
										javaSourceDir.getAbsolutePath().length()
												+ 1);
								path = path.replace(File.separatorChar, '/');
								entries.add(path);
								jarOut.putNextEntry(new JarEntry(path));
								IOUtils.write(f, jarOut);
							}
						}
					}
				}
			}
		}

		return jarFile;
	}

	/**
	 * This makes an educated guess what the root of the .class files is in this project
	 */
	protected File findClassesDir() {
		FileTreeIterator iter = new FileTreeIterator(projectDir, "class");

		Map<String, AtomicInteger> candidates = new HashMap<>();
		while (iter.hasNext()) {
			File classFile = iter.next();
			String path = classFile.getAbsolutePath();;
			if (path.contains(File.separator + "test" + File.separator))
				continue;
			int i1 = path.indexOf(File.separator + "com" + File.separator);
			int i2 = path.indexOf(File.separator + "java" + File.separator);
			int i3 = path.indexOf(File.separator + "javax" + File.separator);
			int i4 = path.indexOf(File.separator + "net" + File.separator);

			if (i1 == -1) i1 = Integer.MAX_VALUE;
			if (i2 == -1) i2 = Integer.MAX_VALUE;
			if (i3 == -1) i3 = Integer.MAX_VALUE;
			if (i4 == -1) i4 = Integer.MAX_VALUE;
			int i = Math.min(Math.min(i1, i2), Math.min(i3, i4));
			if (i != Integer.MAX_VALUE) {
				path = path.substring(0, i);
				AtomicInteger ctr = candidates.get(path);
				if (ctr == null) {
					ctr = new AtomicInteger(0);
					candidates.put(path, ctr);
				}
				ctr.incrementAndGet();
			}
		}

		String bestCandidate = null;
		int bestCandidateCtr = -1;
		for (Map.Entry<String, AtomicInteger> entry : candidates.entrySet()) {
			if (entry.getValue().get() > bestCandidateCtr) {
				bestCandidateCtr = entry.getValue().get();
				bestCandidate = entry.getKey();
			}
		}
		return new File(bestCandidate);
	}

	private Collection<File> getJavaSourceDirs() {
		Collection<File> returnValue = new HashSet<>();
		FileTreeIterator iter = new FileTreeIterator(projectDir, "java");
		while (iter.hasNext()) {
			File javaFile = iter.next();
			if (javaFile.getName().equals("module-info.java"))
				continue;

			try {
				String z = JavaClassSummary.getClassName(javaFile) + ".java";
				String x = javaFile.getAbsolutePath();
				x = x.substring(0, x.length() - z.length());
				if (!x.contains("/test/"))
					returnValue.add(new File(x));
			} catch (Exception e) {
				throw new RuntimeException(
						"Error processing " + javaFile.getAbsolutePath(), e);
			}
		}
		System.out
				.println("Identified java source directories: " + returnValue);
		return returnValue;
	}

	private Manifest createManifest() {
		Manifest manifest = new Manifest();
		Attributes attributes = manifest.getMainAttributes();
		attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		attributes.put(new Attributes.Name("Created-By"),
				Project.class.getName());
		if (mainClass != null) {
			attributes.put(Attributes.Name.MAIN_CLASS, mainClass.getName());
		}
		return manifest;
	}
}