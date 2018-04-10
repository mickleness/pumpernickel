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
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

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
				}
			}
		}
		System.out.println("Done");
		System.exit(0);
	}
}