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
package com.pump.desktop.temp;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Objects;
import java.util.Random;

import com.pump.io.FileUtils;
import com.pump.io.IOUtils;

/**
 * This is a session-based manager for temp files.
 * <p>
 * Every active session is designated a unique folder that temp files are stored
 * in. This folder is deleted either when the session ends (healthily), or the
 * next time {@link #initialize(String)} is called for the same application.
 * <p>
 * The default {@link java.io.File#createTempFile(String, String)} method is
 * nice, but it does not guarantee files will be purged over time. Even if you
 * remember to call {@link java.io.File#deleteOnExit()}, there isn't a mechanism
 * to make sure the file is deleted on relaunch. (That is: if lightning strikes
 * and app doesn't exit normally: the next best time to delete the file is when
 * the app is relaunched.)
 */
public class TempFileManager {
	private static TempFileManager manager;
	private static final String LOCK_FILE_NAME = "lock";

	/**
	 * Return true if {@link #initialize(String)} has been called and it is safe
	 * to call {@link #get()}.
	 */
	public static boolean isInitialized() {
		return manager != null;
	}

	/**
	 * Get the current TempFileManager. {@link #initialize(String)} must be
	 * called before this method or an exception is thrown.
	 * 
	 * @return the current TempFileManager.
	 */
	public synchronized static TempFileManager get() {
		if (manager == null)
			throw new NullPointerException(
					"initialize(..) has not been called.");
		return manager;
	}

	/**
	 * Initialize a static TempFileManager for the rest of this session.
	 * <p>
	 * Subsequent attempts to initialize this will return false and print a
	 * warning message to System.err.
	 * 
	 * @param appName
	 *            the application name. This is used in the file path of temp
	 *            files.
	 * 
	 * @return true if this call initialized a TempFileManager, false otherwise.
	 * 
	 * @throws IOException
	 */
	public synchronized static boolean initialize(String appName)
			throws IOException {
		if (appName == null)
			throw new NullPointerException("appName is required");
		if (manager != null) {
			if (manager.getApplicationName().equals(appName)) {
				return false;
			}
			System.err.println(
					"Warning: TempFileManager previously initialized as \""
							+ manager.getApplicationName()
							+ "\", now attempted to initialize as \"" + appName
							+ "\".");
			return false;
		}

		File dir = new File(System.getProperty("java.io.tmpdir"));
		File appDir = new File(dir, appName + "+TempFileMgr");
		FileUtils.mkdirs(appDir);

		Random random = new Random();
		int n = random.nextInt(0xFFFFFFF);
		File localDir = new File(appDir, Integer.toString(n));
		int attempts = 0;
		while (localDir.exists()) {
			n = random.nextInt(0xFFFFFFF);
			localDir = new File(appDir, Integer.toString(n));
			attempts++;
			if (attempts > 1000)
				throw new IOException(
						"Failed to initialize TempFileManager; see "
								+ appDir.getAbsolutePath());
		}
		FileUtils.mkdirs(localDir);
		File lockedFile = new File(localDir, LOCK_FILE_NAME);
		FileUtils.createNewFile(lockedFile);

		FileLock lock = IOUtils.getFileLock(lockedFile, true);

		// clean up past sessions, in case they died prematurely
		new CleanThread(appDir, localDir, null).start();

		// clean up this session -- later
		Runtime.getRuntime()
				.addShutdownHook(new CleanThread(appDir, null, lock));
		System.out.println(
				"Initialized temp directory as " + localDir.getAbsolutePath());

		manager = new TempFileManager(appName, localDir);

		return true;
	}

	private static boolean isLocked(File file) {
		try {
			FileLock lock = IOUtils.getFileLock(file, true);
			if (lock == null)
				return true;
			lock.close();
			return false;
		} catch (IOException e) {
			return true;
		}
	}

	private static class CleanThread extends Thread {
		File exemptFile;
		File appDir;
		FileLock lock;

		public CleanThread(File appDir, File exemptFile, FileLock lock) {
			this.appDir = appDir;
			this.exemptFile = exemptFile;
			this.lock = lock;
		}

		@Override
		public void run() {
			try {
				if (lock != null)
					lock.close();

				File[] children = appDir.listFiles();
				for (File child : children) {
					if (!child.equals(exemptFile)) {
						if (child.isDirectory()) {
							File lockFile = new File(child, LOCK_FILE_NAME);
							if ((!lockFile.exists()) || (!isLocked(lockFile))) {
								if (!IOUtils.delete(child))
									System.err.println(
											"IOUtils.delete(..) failed for "
													+ child.getAbsolutePath());
							} else {
								System.err.println("Active session detected: "
										+ child.getAbsolutePath());
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	String appName;
	File dir;

	private TempFileManager(String appName, File dir) {
		Objects.requireNonNull(appName);
		Objects.requireNonNull(dir);
		this.dir = dir;
		this.appName = appName;
	}

	protected String getApplicationName() {
		return appName;
	}

	/**
	 * Create a new temporary file that will be deleted either at the end of the
	 * current session or at the beginning of the next session.
	 * 
	 * @param prefix
	 *            an optional prefix
	 * @param ext
	 *            an optional file extension
	 * @return a new File
	 */
	public File createFile(String prefix, String ext) {
		if (ext == null)
			ext = ".tmp";
		if (!ext.startsWith("."))
			ext = "." + ext;
		String name = prefix + ext;
		return IOUtils.getUniqueFile(dir, name, true, true);
	}

	/**
	 * Return the directory this TempFileManager stores files in.
	 */
	public File getDirectory() {
		return dir;
	}
}