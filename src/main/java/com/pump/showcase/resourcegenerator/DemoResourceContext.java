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
package com.pump.showcase.resourcegenerator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.pump.io.AdjacentFileOutputStream;
import com.pump.io.IOUtils;

public class DemoResourceContext {

	/**
	 * Find a particular file in this workspace.
	 */
	public File getFile(String filepathSegment) {
		File dir = new File(System.getProperty("user.dir"));
		File file = findFile(dir, filepathSegment);
		if (file == null)
			throw new NullPointerException(
					"Could not locate \"" + filepathSegment + "\" in \""
							+ dir.getAbsolutePath() + "\"");
		return file;
	}

	/**
	 * Find a file in a directory (or its subdirectories) matching a given
	 * filename.
	 */
	public File findFile(File dir, String filepathSegment) {
		for (File child : dir.listFiles()) {
			if (child.getAbsolutePath().contains(filepathSegment))
				return child;
			if (child.isDirectory()) {
				File rv = findFile(child, filepathSegment);
				if (rv != null)
					return rv;
			}
		}
		return null;
	}

	/**
	 * A map of a directory to its original children.
	 */
	Map<File, File[]> indexedChildren = new HashMap<>();
	Collection<File> newFiles = new HashSet<>();

	/**
	 * This indexes all the files in a directory. After calling this method you
	 * should call {@link #createFileOutputStream(File)} any number of times,
	 * and then clean up by calling {@link #removeOldFiles(File)}.
	 */
	public void indexDirectory(File dir) {
		if (indexedChildren.containsKey(dir))
			return;
		indexedChildren.put(dir, dir.listFiles());
	}

	public AdjacentFileOutputStream createFileOutputStream(File file)
			throws IOException {
		newFiles.add(file);
		return AdjacentFileOutputStream.create(file);
	}

	public void removeOldFiles(File dir) {
		File[] oldChildren = indexedChildren.remove(dir);
		if (oldChildren == null)
			throw new RuntimeException(
					"This method should only be called once after indexDirectory(..)");
		for (File oldChild : oldChildren) {
			if (!newFiles.contains(oldChild)) {
				IOUtils.delete(oldChild);
				System.out.println("Deleting: " + oldChild.getAbsolutePath());
			}
		}

	}

	private ExecutorService service;
	private List<Future<?>> futures = new LinkedList<>();

	/**
	 * Add a Callable. See {@link #waitForExecutor()} to block until this and
	 * all other callables finish.
	 */
	public void queueCallable(Callable<?> callable) {
		if (service == null)
			service = Executors.newFixedThreadPool(6);
		futures.add(service.submit(callable));
	}

	/**
	 * Wait for all queues callables to finish.
	 */
	public void waitForExecutor() throws Exception {
		while (!futures.isEmpty()) {
			Future<?> future = futures.remove(0);
			future.get(10000, TimeUnit.HOURS);
		}
	}
}