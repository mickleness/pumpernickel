package com.pump.showcase.demo;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public abstract class DemoResourceGenerator {
	public abstract void run() throws Exception;

	/**
	 * Find a particular file in this workspace.
	 */
	protected File getFile(String filename) {
		File dir = new File(System.getProperty("user.dir"));
		File file = findFile(dir, filename);
		if (file == null)
			throw new NullPointerException("Could not locate \"" + filename
					+ "\" in \"" + dir.getAbsolutePath() + "\"");
		return file;
	}

	/**
	 * Find a file in a directory (or its subdirectories) matching a given
	 * filename.
	 */
	protected File findFile(File dir, String filename) {
		for (File child : dir.listFiles()) {
			if (child.getName().equals(filename))
				return child;
			if (child.isDirectory()) {
				File rv = findFile(child, filename);
				if (rv != null)
					return rv;
			}
		}
		return null;
	}

	/**
	 * Split a String into several rows of text. This is intended to break up a
	 * large base64 block of text into multiple rows.
	 * 
	 * @param str
	 *            a large String
	 * @param charLimit
	 *            the maximum number of characters in a row
	 * @return a list of Strings that can be combined together to recreate the
	 *         input.
	 */
	protected List<String> splitRows(String str, int charLimit) {
		List<String> returnValue = new LinkedList<>();
		while (str.length() > 0) {
			String row = str.substring(0, Math.min(str.length(), charLimit));
			returnValue.add(row);
			str = str.substring(row.length());
		}
		return returnValue;
	}
}
