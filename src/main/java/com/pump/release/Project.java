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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;

public class Project {
	File projectDir;
	Class mainClass;
	String jarFileName;

	public Project(File projectDir, Class mainClass, String jarFileName) {
		this.projectDir = projectDir;
		this.mainClass = mainClass;
		this.jarFileName = jarFileName;

	}

	public File buildJar() throws IOException {

		// TODO: this just dumps the existing compiled code in a jar
		// we need to replace this with logic that actually compiles
		// against the specified dependencies. This will let us stay
		// true to the intention of the dependency version, and it will
		// let us abort if we know there are compiler errors.

		File releaseDir = new File(projectDir, "release");
		if (!releaseDir.exists() && !releaseDir.mkdirs())
			throw new IOException("mkdirs failed for "
					+ releaseDir.getAbsolutePath());

		File jarsDir = new File(releaseDir, "jars");
		if (!jarsDir.exists() && !jarsDir.mkdirs())
			throw new IOException("mkdirs failed for "
					+ jarsDir.getAbsolutePath());

		File jarFile = new File(jarsDir, jarFileName);
		if (jarFile.exists()) {
			jarFile.delete();
		}
		jarFile.createNewFile();

		File targetDir = new File(projectDir, "target");
		File classesDir = new File(targetDir, "classes");
		if (!classesDir.exists())
			throw new RuntimeException("\"target\\classes\" did not exist in "
					+ projectDir.getAbsolutePath());

		Manifest manifest = createManifest();
		Collection<String> entries = new HashSet<>();
		try (FileOutputStream fileOut = new FileOutputStream(jarFile)) {
			try (JarOutputStream jarOut = new JarOutputStream(fileOut, manifest)) {
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
			}
		}

		return jarFile;
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