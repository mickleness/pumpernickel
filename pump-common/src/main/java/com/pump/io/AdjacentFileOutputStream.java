package com.pump.io;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.util.JVM;

/**
 * This writes file data to a file <i>next to</i> the file you want ultimately
 * want to replace, and then when you close this stream we swap out the two
 * files (if they are different).
 * <p>
 * This offers a few advantages: 1. If an error comes up in writing the file,
 * the original file is not damaged. To really properly account for this though
 * you should set up code that resembles:
 * 
 * <pre>
 * try(AdjacentFileOutputStream out = AdjacentFileOutputStream.create(myFile) {
 *   try {
 *     writeFile(out);
 *   } catch(Exception e) {
 *     out.cancel();
 *     throw e;
 *   }
 * }
 * </pre>
 * 
 * If you don't call {@link #cancel()}, then when {@link #close()} is
 * automatically called: the files are still swapped. So if an exception occurs
 * and your file is only half-written, you want to avoid that. 2. If the new
 * file is identical to the original, the new file is deleted. This keeps the
 * modification date and any other meta info about the original file in tact.
 */
public class AdjacentFileOutputStream extends FileOutputStream implements
		Cancellable {
	public static AdjacentFileOutputStream create(File targetFile)
			throws IOException {
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
		if (!adjacentFile.createNewFile())
			throw new IOException("File.createNewFile() failed for "
					+ adjacentFile.getAbsolutePath());
		return new AdjacentFileOutputStream(adjacentFile, targetFile);
	}

	boolean closed = false;
	File targetFile, fileToWriteTo;
	BasicCancellable cancellable = new BasicCancellable();

	private AdjacentFileOutputStream(File fileToWriteTo, File targetFile)
			throws FileNotFoundException {
		super(fileToWriteTo);
		this.fileToWriteTo = fileToWriteTo;
		this.targetFile = targetFile;
	}

	@Override
	public synchronized void close() throws IOException {
		if (closed)
			return;
		closed = true;
		super.close();

		if (cancellable.isCancelled()) {
			if (!delete(fileToWriteTo, 2500))
				throw new IOException("File.delete() failed for "
						+ fileToWriteTo.getAbsolutePath());
		} else {
			if (IOUtils.equals(fileToWriteTo, targetFile)) {
				if (!delete(fileToWriteTo, 2500))
					throw new IOException("File.delete() failed for "
							+ fileToWriteTo.getAbsolutePath());
			} else {
				if (!delete(targetFile, 2500))
					throw new IOException("File.delete() failed for "
							+ targetFile.getAbsolutePath());
				if (!renameTo(fileToWriteTo, targetFile, 2500))
					throw new IOException("File.renameTo() failed for "
							+ fileToWriteTo.getAbsolutePath() + " and "
							+ targetFile.getAbsolutePath());
			}
			cancellable.finish();
		}
	}

	/**
	 * File.renameTo() may not return true even though the file is flagged to be
	 * renamed.
	 * <p>
	 * This method waits up to a fixed number of milliseconds to give the file
	 * system a chance to catch up and acknowledge the file is renamed.
	 * <p>
	 * After the delay: this may still return false even though the file is
	 * *going* to be benamed.
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

	@Override
	public void cancel() {
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
}
