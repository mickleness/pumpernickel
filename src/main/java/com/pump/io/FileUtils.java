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
}