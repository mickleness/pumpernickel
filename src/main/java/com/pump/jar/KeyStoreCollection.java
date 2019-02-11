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

import com.pump.io.SuffixFilenameFilter;

public class KeyStoreCollection extends FileCollection {

	static FileFilter jksFileFilter = new SuffixFilenameFilter("jks", "p12");

	public KeyStoreCollection() {
		super("keystore-");
	}

	@Override
	public void process(File file) {
		if (jksFileFilter.accept(file) && file.exists())
			files.add(file);
	}

}