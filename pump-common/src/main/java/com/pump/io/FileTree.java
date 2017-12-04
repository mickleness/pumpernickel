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
package com.pump.io;

import java.io.File;
import java.io.FileFilter;

/**
 * This is an Iterable wrapper for the {@link FileTreeIterator} class.
 */
public class FileTree implements Iterable<File> {
	
	File dir;
	FileFilter filter;
	
	/**
	 * Create a new FileTree.
	 * 
	 * @param dir the directory to traverse.
	 */
	public FileTree(File dir) {
		this.dir = dir;
	}

	/**
	 * Create a new FileTree.
	 * 
	 * @param dir the directory to traverse.
	 * @param filter the optional FileFilter to apply.
	 */
	public FileTree(File dir,FileFilter filter) {
		this.dir = dir;
		this.filter = filter;
	}

	/**
	 * Create a new FileTree.
	 * 
	 * @param dir the directory to traverse.
	 * @param extensions a series of file extensions to filter for.
	 */
	public FileTree(File dir,String... extensions) {
		this(dir, new SuffixFilenameFilter(extensions));
	}

	@Override
	public FileTreeIterator iterator() {
		return new FileTreeIterator(dir, filter);
	}

}