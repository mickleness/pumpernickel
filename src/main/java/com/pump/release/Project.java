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

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.jar.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

	public File buildJar(boolean includeJavaSource, String... subdirectories)
			throws IOException {

		// TODO: this just dumps the existing compiled code in a jar
		// we need to replace this with logic that actually compiles
		// against the specified dependencies. This will let us stay
		// true to the intention of the dependency version, and it will
		// let us abort if we know there are compiler errors.

		File currentDir = projectDir;
		for (int a = 0; a < subdirectories.length; a++) {
			File newDir = new File(currentDir, subdirectories[a]);
			FileUtils.mkdirs(newDir);
			currentDir = newDir;
		}

		File jarFile = new File(currentDir, jarFileName);
		if (jarFile.exists()) {
			jarFile.delete();
		}
		FileUtils.createNewFile(jarFile);

		File targetDir = new File(projectDir, "target");
		File classesDir = new File(targetDir, "classes");
		if (!classesDir.exists())
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