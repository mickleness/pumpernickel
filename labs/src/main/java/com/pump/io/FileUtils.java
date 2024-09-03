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
package com.pump.io;

import java.io.File;
import java.io.IOException;

/**
 * This offers a few static methods the correspond to java.io.File methods.
 */
public class FileUtils {

	/**
	 * This calls File.mkdir(), and it throws an IOException if it fails.
	 */
	public static boolean mkdir(File file) throws IOException {
		if (file.exists() && file.isDirectory())
			return false;
		if (!file.mkdir())
			throw new IOException(
					"File.mkdir failed for " + file.getAbsolutePath());
		return true;
	}

	/**
	 * This calls File.createNewFile(), and it throws an IOException if it
	 * fails.
	 */
	public static boolean createNewFile(File file) throws IOException {
		if (!file.createNewFile())
			throw new IOException(
					"File.createNewFile failed for " + file.getAbsolutePath());
		return true;
	}

	/**
	 * This calls File.mkdirs(), and it throws an IOException if it fails.
	 */
	public static boolean mkdirs(File dir) throws IOException {
		if (dir.exists() && dir.isDirectory())
			return false;
		if (!dir.mkdirs())
			throw new IOException(
					"File.mkdirs failed for " + dir.getAbsolutePath());
		return true;
	}

	/**
	 * This calls File.delete(), and it throws an IOException if it fails.
	 */
	public static boolean delete(File file) throws IOException {
		if (!file.exists())
			return false;

		if (!file.delete())
			throw new IOException(
					"File.delete failed for " + file.getAbsolutePath());

		return true;
	}
}