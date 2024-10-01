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

	public enum FileOperation {
		/**
		 * This is associated with {@link File#createNewFile()}.
		 */
		NEW_FILE,

		/**
		 * This is associated with {@link File#mkdir()}.
		 */
		MKDIR,

		/**
		 * This is associated with {@link File#mkdirs()}.
		 */
		MKDIRS,

		/**
		 * This is associated with {@link File#delete()}.
		 */
		DELETE
	}

	/**
	 * This IOException identifies the {@link FileOperation} that failed.
	 */
	public static class FileException extends IOException {

		private final FileOperation operation;
		private final File file;

		public FileException(FileOperation operation,
							 File file,
							 String msg) {
			super(msg);
			this.file = file;
			this.operation = operation;
		}

		/**
		 * Return the File involved in this failed operation.
		 */
		public File getFile() {
			return file;
		}

		/**
		 * Return the exact FileOperation that failed.
		 * @return
		 */
		public FileOperation getFileOperation() {
			return operation;
		}
	}

	/**
	 * This calls File.mkdir(), and it throws an IOException if it fails.
	 *
	 * @return false if this File already exists as a directory. True
	 * if this successfully created the directory.
	 */
	public static boolean mkdir(File file) throws FileException {
		if (file.exists() && file.isDirectory())
			return false;

		if (!file.mkdir())
			throw new FileException(FileOperation.MKDIR,  file,
					"File.mkdir failed for " + file.getAbsolutePath());
		return true;
	}

	/**
	 * This calls File.createNewFile(), and it throws a FileException if it
	 * fails.
	 *
	 * @return false if the File already exists and is not a directory. True
	 * if this success creates the file.
	 */
	public static boolean createNewFile(File file) throws IOException {
		if (file.exists() && !file.isDirectory())
			return false;

		if (!file.createNewFile())
			throw new FileException( FileOperation.NEW_FILE, file,
					"File.createNewFile failed for " + file.getAbsolutePath());
		return true;
	}

	/**
	 * This calls File.mkdirs(), and it throws a FileException if it fails.
	 *
	 * @return false if this File already exists as a directory. True
	 * if this successfully created the directory.
	 */
	public static boolean mkdirs(File dir) throws FileException {
		if (dir.exists() && dir.isDirectory())
			return false;

		if (!dir.mkdirs())
			throw new FileException( FileOperation.MKDIRS, dir,
					"File.mkdirs failed for " + dir.getAbsolutePath());
		return true;
	}

	/**
	 * This calls File.delete(), and it throws a FileException if it fails.
	 *
	 * @return false if the File does not exist (so there is nothing to delete).
	 * True if this successfully deletes the file.
	 */
	public static boolean delete(File file) throws FileException {
		if (!file.exists())
			return false;

		if (!file.delete())
			throw new FileException( FileOperation.DELETE, file,
					"File.delete failed for " + file.getAbsolutePath());

		return true;
	}
}