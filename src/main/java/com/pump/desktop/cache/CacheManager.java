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
package com.pump.desktop.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.pump.io.IOUtils;
import com.pump.util.JVM;

public class CacheManager {

	/**
	 * This returns or creates a cache directory for your current user. The
	 * returned file should exist and have write privileges.
	 * 
	 * @param appName
	 *            this is used as part (or all) of the directory name. For best
	 *            results this should be a qualified name like
	 *            "com.apple.GarageBand" instead of simply "GarageBand".
	 * @return the cache directory
	 * @throws IOException
	 *             if this directory couldn't be initialized correctly.
	 */
	public static File getCacheDirectory(String appName) throws IOException {
		File dir;
		if (JVM.isMac) {
			dir = new File(System.getProperty("user.home") + "/Library/Caches/"
					+ appName);
		} else if (JVM.isWindows) {
			dir = new File(System.getProperty("user.home")
					+ "\\Application Data\\" + appName);
		} else {
			dir = new File(System.getProperty("user.home") + File.separator
					+ appName);
		}
		validate(dir);
		return dir;
	}

	/**
	 * If a directory doesn't exist: create it, and make sure we have sufficient
	 * privileges.
	 * 
	 * @param dir
	 *            the directory to create/validate.
	 * @throws IOException
	 */
	private static final void validate(File dir) throws IOException {
		if (!dir.exists()) {
			if (!dir.mkdirs())
				throw new IOException("mkdirs failed for "
						+ dir.getAbsolutePath());
		}
		if (!dir.canRead())
			throw new IOException("insufficient privilege for "
					+ dir.getAbsolutePath());
		if (!dir.canWrite())
			throw new IOException("insufficient privilege for "
					+ dir.getAbsolutePath());
	}

	static private CacheManager GLOBAL;

	/**
	 * 
	 * @param appName
	 *            this is used as part (or all) of the directory name. For best
	 *            results this should be a qualified name like
	 *            "com.apple.GarageBand" instead of simply "GarageBand".
	 * @param version
	 *            a String representing the version of this application. This is
	 *            used to create a folder (so it should not contain slashes or
	 *            colons), and it lets us differentiate and clear cached
	 *            resources when the version changes.
	 * 
	 * @throws IOException
	 */
	public static synchronized void initialize(String appName, String version)
			throws IOException {
		if (GLOBAL != null)
			throw new IllegalStateException(
					"The CacheManager has already been initialized for "
							+ GLOBAL.appName);
		GLOBAL = new CacheManager(appName, version);
	}

	public static CacheManager get() {
		if (GLOBAL == null)
			throw new NullPointerException(
					"CacheManager.initialize() has not been called.");
		return GLOBAL;
	}

	protected String appName;
	protected File dir;
	protected File commonDir;
	protected File versionDir;

	/**
	 * 
	 * 
	 * @param appName
	 * @param version
	 *            this optional version name will be used to create a
	 *            version-specific folder within the cache. If this is non-null:
	 *            then all inactive directories (from previous versions) will be
	 *            deleted.
	 * @throws IOException
	 */
	public CacheManager(String appName, String version) throws IOException {
		this.appName = appName;
		dir = getCacheDirectory(appName);
		commonDir = new File(dir, "Common");
		validate(commonDir);

		if (version != null) {
			versionDir = new File(dir, version);
			validate(versionDir);

			File[] file = dir.listFiles();
			for (File f : file) {
				if (f.isDirectory()) {
					if (!(f.equals(commonDir) || f.equals(versionDir))) {
						// this is probably an old version. When the user
						// switches versions:
						// clear the old cache.
						IOUtils.delete(f);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param versioned
	 *            if true then the directory this returns is going to be unique
	 *            to the version this CacheManager was initialized/constructed
	 *            with. (So if this was constructed with a null version, this
	 *            scenario returns a null file.) If false then this directory
	 *            will be static across versions.
	 * @return a directory that should be persistent across sessions. This will
	 *         not be null (unless you asked for the versioned dir but didn't
	 *         construct this object with a version), but it may be empty
	 *         (either the user/system may choose to clear it, or it will be
	 *         empty the first time the app is run).
	 */
	public File getDirectory(boolean versioned) {
		if (versioned) {
			return versionDir;
		}
		return commonDir;
	}

	/**
	 * Return true if {@link #initialize(String, String)} has been called.
	 * 
	 * @return true if {@link #initialize(String, String)} has been called.
	 */
	public static boolean isInitialized() {
		return GLOBAL != null;
	}

	/**
	 * Uncache an object previously cached by calling
	 * {@link #cache(Object, File)}.
	 * 
	 * @param cachedObject
	 *            the file to uncache.
	 */
	public static Object uncache(File cachedObject) throws IOException,
			ClassNotFoundException {
		try (FileInputStream fileIn = new FileInputStream(cachedObject)) {
			try (GZIPInputStream zipIn = new GZIPInputStream(fileIn)) {
				try (ObjectInputStream objIn = new ObjectInputStream(zipIn)) {
					return objIn.readObject();
				}
			}
		}
	}

	/**
	 * Cache a file using a GZIP compressed ObjectOutputStream.
	 * 
	 * @param object
	 *            a Serializable object to cache.
	 * @param dest
	 *            the File destination to write to.
	 */
	public static void cache(Object object, File dest) throws IOException {
		if (!dest.exists())
			if (!dest.createNewFile())
				throw new IOException("createNewFile() failed for "
						+ dest.getAbsolutePath());
		try (FileOutputStream fileOut = new FileOutputStream(dest)) {
			try (GZIPOutputStream zipOut = new GZIPOutputStream(fileOut)) {
				try (ObjectOutputStream objOut = new ObjectOutputStream(zipOut)) {
					objOut.writeObject(object);
				}
			}
		}
	}

}