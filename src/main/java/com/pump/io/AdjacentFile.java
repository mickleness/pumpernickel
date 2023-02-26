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

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.util.JVM;


/**
 * This helps manage a file adjacent to a target file. When {@link #close()} is called
 * the temporary adjacent file is renamed or deleted. This offers a couple of advantages
 * over simply replacing the target file:
 * <ul><li>If the data we write is exactly the same, then the temporary file is deleted and the original file
 * preserves all of its original meta information.</li>
 * <li>If writing the file is interrupted (either because of an exception or because the user cancelled), then the
 * original file is unchanged.</li></ul>
 * <p>
 * It's important to note that when {@link #close()} is invoked the target file will be replaced unless
 * {@link #cancel()} has already been invoked. So if an exception occurs (or anything that could corrupt
 * the saved file), then you must call {@link #cancel()}. You may want to structure your code so it resembles:
 * <pre>
 *     AdjacentFile adjFile = new AdjacentFile(targetFile);
 *     try {
 *         write(adjFile.createOutputStream());
 *     } catch(Exception e) {
 *         adjFile.cancel();
 *         throw e;
 *     }
 * </pre>
 */
public class AdjacentFile implements AutoCloseable, Cancellable {

	/**
	 * File.renameTo() may not return true even though the file is flagged to be
	 * renamed.
	 * <p>
	 * This method waits up to a fixed number of milliseconds to give the file
	 * system a chance to catch up and acknowledge the file is renamed.
	 * <p>
	 * After the delay: this may still return false even though the file is
	 * *going* to be renamed.
	 *
	 * @param fileToRename
	 *            the file to rename
	 * @param dest
	 *            the argument for File.renameTo()
	 * @param ms
	 *            the number of milliseconds to wait
	 * @return true if the file was renamed in under the milliseconds allotted.
	 */
	private static boolean renameTo(File fileToRename, File dest, long ms) {
		boolean renamed = fileToRename.renameTo(dest);
		long t = System.currentTimeMillis();
		while (!renamed && System.currentTimeMillis() - t < ms) {
			if (!fileToRename.exists()) {
				renamed = true;
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.yield();
				}
			}
		}

		return renamed;
	}

	/**
	 * File.delete() may not return true even though the file is flagged for
	 * deletion.
	 * <p>
	 * This method waits up to a fixed number of milliseconds to give the file
	 * system a chance to catch up and acknowledge the file is deleted.
	 * <p>
	 * After the delay: this may still return false even though the file is
	 * *going* to be deleted.
	 *
	 * @param file
	 *            the file to delete
	 * @param ms
	 *            the number of milliseconds to wait
	 * @return true if the file was deleted in under the milliseconds allotted.
	 */
	private static boolean delete(File file, long ms) {
		boolean deleted = file.delete();
		long t = System.currentTimeMillis();
		while (!deleted && System.currentTimeMillis() - t < ms) {
			if (!file.exists()) {
				deleted = true;
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.yield();
				}
			}
		}
		return deleted;
	}

	private final File adjacentFile;
	private final File targetFile;
	private boolean closed = false;
	private final BasicCancellable cancellable = new BasicCancellable();

	/**
	 * Create an AdjacentFile.
	 *
	 * @param fileToWriteTo temporary file to write data to. When {@link #close()} is called this file is deleted (or renamed).
	 * @param targetFile the file we ultimately want to replace (unless we {@link #cancel()}.
	 */
	public AdjacentFile(File fileToWriteTo, File targetFile) {
		this.targetFile = Objects.requireNonNull(targetFile);
		this.adjacentFile = Objects.requireNonNull(fileToWriteTo);
	}

	/**
	 * Create an AdjacentFile using a default naming convention for the temporary file.
	 *
	 * @param targetFile the file we ultimately want to replace (unless we {@link #cancel()}.
	 */
	public AdjacentFile(File targetFile) throws IOException {
		this.targetFile = targetFile;
		String adjacentFilename = targetFile.getName();
		if (JVM.isMac) {
			// the "." makes files hidden, like .ds_store
			adjacentFilename = "." + adjacentFilename;
		} else {
			// this appears to be the convention for Word?
			adjacentFilename = "~" + adjacentFilename;
		}

		// an arbitrary max # of characters
		int max = 20;
		if (adjacentFilename.length() > max) {
			adjacentFilename = adjacentFilename.substring(0, max);
		}

		File adjacentFile = null;
		int ctr = 1;
		while (adjacentFile == null || adjacentFile.exists()) {
			adjacentFile = new File(targetFile.getParentFile(),
					adjacentFilename + (ctr++) + ".tmp");
		}
		FileUtils.createNewFile(adjacentFile);

		this.adjacentFile = adjacentFile;
	}

	/**
	 * Return the file this object expects changes to ultimately be in.
	 */
	public File getTargetFile() {
		return targetFile;
	}

	/**
	 * Return file that this object expects to temporarily write data to.
	 */
	public File getAdjacentFile() {
		return adjacentFile;
	}

	@Override
	public String toString() {
		return getClass().getName()+"[ targetFile = " + targetFile.getAbsolutePath() + " adjacentFile = " + adjacentFile + "]";
	}

	@Override
	public void cancel() {
		if (closed)
			return;

		cancellable.cancel();
	}

	@Override
	public boolean isCancelled() {
		return cancellable.isCancelled();
	}

	@Override
	public boolean isFinished() {
		return cancellable.isFinished();
	}

	@Override
	public void addCancelListener(ActionListener l) {
		cancellable.addCancelListener(l);
	}

	@Override
	public void addFinishListener(ActionListener l) {
		cancellable.addFinishListener(l);
	}

	@Override
	public void removeCancelListener(ActionListener l) {
		cancellable.removeCancelListener(l);
	}

	@Override
	public void removeFinishListener(ActionListener l) {
		cancellable.removeFinishListener(l);
	}

	/**
	 * If this object is not cancelled then this method will first check to see if the new file
	 * is an exact copy of the original file. If it is: then the new copy is deleted and the original
	 * is left unchanged. If it is modified: then the original file is deleted and the new file
	 * is renamed to replace teh original.
	 * <p>
	 * If this object has been cancelled: then this method deletes {@link #getAdjacentFile()}.
	 * </p>
	 */
	@Override
	public void close() throws IOException {
		if (closed)
			return;
		closed = true;

		if (cancellable.isCancelled()) {
			if (!delete(adjacentFile, 2500))
				throw new IOException("File.delete() failed for "
						+ adjacentFile.getAbsolutePath());
		} else {
			if (IOUtils.equals(adjacentFile, targetFile)) {
				if (!delete(adjacentFile, 2500))
					throw new IOException("File.delete() failed for "
							+ adjacentFile.getAbsolutePath());
			} else {
				if (!delete(targetFile, 2500))
					throw new IOException("File.delete() failed for "
							+ targetFile.getAbsolutePath());
				if (!renameTo(adjacentFile, targetFile, 2500))
					throw new IOException("File.renameTo() failed for "
							+ adjacentFile.getAbsolutePath() + " and "
							+ targetFile.getAbsolutePath());
			}
			cancellable.finish();
		}
	}

	/**
	 * Create the FileOutputStream for {@link #getAdjacentFile()} that will invoke {@link #close()} when
	 * the FileOutputStream closes.
	 */
	public FileOutputStream createOutputStream() throws FileNotFoundException {
		return new FileOutputStream(getAdjacentFile()) {
			@Override
			public void close() throws IOException {
				try {
					super.close();
				} finally {
					AdjacentFile.this.close();
				}
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				try {
					super.write(b, off, len);
				} catch(IOException | RuntimeException e) {
					cancel();
					throw e;
				}
			}

			@Override
			public void write(int b) throws IOException {
				try {
					super.write(b);
				} catch(IOException | RuntimeException e) {
					cancel();
					throw e;
				}
			}

			@Override
			public void write(byte[] b) throws IOException {
				try {
					super.write(b);
				} catch(IOException | RuntimeException e) {
					cancel();
					throw e;
				}
			}
		};
	}
}