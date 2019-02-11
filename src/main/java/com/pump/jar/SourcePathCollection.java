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
package com.pump.jar;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import com.pump.io.FileTreeIterator;
import com.pump.io.SuffixFilenameFilter;

public class SourcePathCollection extends FileCollection {

	static FileFilter javaFileFilter = new SuffixFilenameFilter("java");

	public SourcePathCollection() {
		super("sourcepath-");
	}

	protected static boolean isWorkspace(File file) {
		if (!file.isDirectory())
			return false;
		FileTreeIterator iter = new FileTreeIterator(file, "java");
		while (iter.hasNext()) {
			File javaFile = iter.next();
			File workspace = getWorkspaceForJavaFile(javaFile);
			if (file.equals(workspace))
				return true;
		}
		return false;
	}

	protected static File getWorkspaceFromParent(File file, List<File> dest) {
		if (file.isHidden())
			return null;

		if (file.isDirectory()) {
			File[] children = file.listFiles();
			String path = file.getAbsolutePath();
			for (int a = 0; a < children.length; a++) {
				File workspace = getWorkspaceFromParent(children[a], dest);
				if (workspace != null) {
					if (path.startsWith(workspace.getAbsolutePath())) {
						return workspace;
					} else {
						dest.add(workspace);
					}
				}
			}
			return null;
		}

		String path = file.getAbsolutePath().toLowerCase();
		if (path.endsWith(".java")) {
			return getWorkspaceForJavaFile(file);
		}
		return null;
	}

	/**
	 * Looks at the file path and the package to determine the root file for a
	 * workspace.
	 * 
	 * @param file
	 * @return the root file, or null if it could not be determined.
	 */
	private static File getWorkspaceForJavaFile(File file) {
		String packageName = JarWriter.getPackage(file);
		if (packageName == null) {
			return null;
		}

		String path = file.getAbsolutePath();
		packageName = packageName.replace(".", File.separator);
		String end = packageName + File.separator + file.getName();
		if (path.endsWith(end)) {
			// make sure it's the right case
			String rootPath = file.getAbsolutePath().substring(0,
					path.length() - end.length());
			File root = new File(rootPath);
			return root;
		}

		return null;
	}

	public void process(File file) {
		if (!file.exists())
			return;

		if (!file.isDirectory()) {
			if (javaFileFilter.accept(file)) {
				File workspace = getWorkspaceForJavaFile(file);
				if (workspace != null) {
					files.add(workspace);
				}
			}
		} else {
			if (isWorkspace(file)) {
				files.add(file);
			}
		}
	}
}