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
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.prefs.Preferences;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JFrame;

import com.pump.io.AdjacentFileOutputStream;
import com.pump.io.FileTree;
import com.pump.io.IOUtils;
import com.pump.swing.TextInputDialog;
import com.pump.swing.TextInputDialog.FilePathInputHandler;
import com.pump.swing.TextInputDialog.StringInputHandler;

/**
 * See instructions in {@link ReleaseApp}.
 */
public class BackupJars {

	public static void main(String[] args) throws IOException {
		final FileFilter fileFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		StringInputHandler handler = new FilePathInputHandler(fileFilter);
		String mavenDirPath = TextInputDialog
				.show(new JFrame(),
						"Select Directory",
						"What is the source directory?",
						"Please enter the root directory Maven jars are currently stored in.",
						"Directory Path", "Directory Path", null,
						Preferences.userNodeForPackage(BackupJars.class),
						"mavenPath", handler);
		if (mavenDirPath == null) {
			return;
		}
		File mavenDir = new File(mavenDirPath);
		Workspace workspace = new Workspace();
		String k = "com" + File.separator + "pump" + File.separator;
		Map<String, SortedMap<JarId, File>> jars = new HashMap<>();
		for (File file : new FileTree(mavenDir)) {
			String path = file.getAbsolutePath();
			int i = path.indexOf(k);
			if (i != -1 && (!file.getName().equals(".DS_STORE"))) {
				String remainder = path.substring(i);
				String z = workspace.getReleases().getDirectory()
						+ File.separator + remainder;
				File newFile = new File(z);

				if (file.isDirectory()) {
					newFile.mkdirs();
				} else {
					boolean result = IOUtils.copy(file, newFile, true);
					if (result) {
						System.out.println("Copied " + file.getAbsolutePath()
								+ " to " + newFile.getAbsolutePath());
					} else {
						System.out.println("Skipped " + file.getAbsolutePath()
								+ " to " + newFile.getAbsolutePath());
					}

					if (newFile.getName().endsWith(".jar")) {
						JarId id = new JarId(newFile);
						SortedMap<JarId, File> m = jars.get(id.artifactId);
						if (m == null) {
							m = new TreeMap<>();
							jars.put(id.artifactId, m);
						}
						m.put(id, newFile);
					}
				}
			}
		}

		try {
			buildShowcaseJar(jars);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Done");
		System.exit(0);
	}

	/**
	 * This method merges all the non-showcase jars into the showcase jar. This
	 * shouldn't be necessary if I fully understood how to merge jars with
	 * Maven, but... well... here we are.
	 */
	private static void buildShowcaseJar(
			Map<String, SortedMap<JarId, File>> jars) throws Exception {
		JarId showcaseJarId = jars.get("pump-showcase").lastKey();
		File showcaseJar = jars.remove("pump-showcase").get(showcaseJarId);

		long size = showcaseJar.length();
		for (Entry<String, SortedMap<JarId, File>> entry : jars.entrySet()) {
			JarId id = entry.getValue().lastKey();
			File jar = entry.getValue().get(id);
			System.out.println("Adding " + jar.getName() + " to "
					+ showcaseJar.getName());
			add(showcaseJar, jar);
		}
		System.out.println("Merged " + showcaseJar.getAbsolutePath() + " from "
				+ IOUtils.formatFileSize(size) + " to "
				+ IOUtils.formatFileSize(showcaseJar));
	}

	private static void add(File targetJar, File jarToAdd) throws Exception {
		try (OutputStream out = AdjacentFileOutputStream.create(targetJar)) {
			try (ZipOutputStream zipOut = new ZipOutputStream(out)) {
				addFile(zipOut, targetJar, true);
				addFile(zipOut, jarToAdd, false);
			}
		}
	}

	private static void addFile(ZipOutputStream zipOut, File jar,
			boolean includeMeta) throws IOException {
		try (InputStream in = new FileInputStream(jar)) {
			try (ZipInputStream zipIn = new ZipInputStream(in)) {
				ZipEntry e = zipIn.getNextEntry();
				while (e != null) {
					if (!e.isDirectory()) {
						boolean include = true;
						if (e.getName().startsWith("META-INF/")) {
							include = includeMeta;
						}
						if (include) {
							zipOut.putNextEntry(new ZipEntry(e.getName()));
							IOUtils.write(zipIn, zipOut);
						}
					}

					e = zipIn.getNextEntry();
				}
			}
		}
	}
}