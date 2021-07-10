package com.pump.io;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pump.data.AbstractAttributeDataImpl;
import com.pump.data.Key;
import com.pump.swing.Cancellable;

public class DirectoryCopyTask extends AbstractAttributeDataImpl
		implements Cancellable {

	private static final long serialVersionUID = 1L;

	static final Key<File> KEY_SOURCE_DIRECTORY = new Key<>(File.class,
			"srcDir");
	static final Key<File> KEY_DEST_DIRECTORY = new Key<>(File.class,
			"destDir");
	static final Key<Boolean> KEY_DELETE_EXTRA_DEST_FILES = new Key<>(
			Boolean.class, "deleteExtraDestFiles");
	static final Key<Boolean> KEY_CANCELLED = new Key<>(Boolean.class,
			"cancelled");
	static final Key<Boolean> KEY_FINISHED = new Key<>(Boolean.class,
			"finished");
	static final Key<Integer> KEY_TOTAL_FILES_TO_COPY = new Key<>(Integer.class,
			"totalFileCount");
	static final Key<Integer> KEY_CURRENT_FILES_COPIED = new Key<>(
			Integer.class, "currentFileCount");
	static final Key<Long> KEY_CURRENT_BYTES_COPIED = new Key<>(Long.class,
			"currentBytesCopied");
	static final Key<File> KEY_CURRENT_FILE = new Key<>(File.class,
			"currentFile");
	static final Key<Long> KEY_TOTAL_BYTES_TO_COPY = new Key<>(Long.class,
			"totalBytesCopied");
	static final Key<Throwable> KEY_THROWABLE = new Key<>(Throwable.class,
			"throwable");

	static class WorkerRunnable implements Runnable {
		DirectoryCopyTask task;
		File src, dst;
		Map<File, Long> filesToCopy = new HashMap<>();

		public WorkerRunnable(DirectoryCopyTask task) {
			this.task = task;
			src = task.getAttribute(KEY_SOURCE_DIRECTORY);
			dst = task.getAttribute(KEY_DEST_DIRECTORY);
		}

		@Override
		public void run() {
			try {
				index();
				copyFiles();
			} catch (IOException e) {
				task.setThrowable(e);
				task.cancel();
			}
		}

		private void copyFiles() throws IOException {
			String srcPath = src.getAbsolutePath();
			String dstPath = dst.getAbsolutePath();
			for (Entry<File, Long> entry : filesToCopy.entrySet()) {
				File srcFile = entry.getKey();
				String filePath = srcFile.getAbsolutePath();
				if (filePath.startsWith(srcPath)) {
					String relPath = filePath.substring(srcPath.length());
					String destFilePath = dstPath + relPath;
					File destFile = new File(destFilePath);
					IOUtils.copy(srcFile, destFile);
					task.incrementProgress(entry.getValue(), entry.getKey());
				}
			}
		}

		/**
		 * Prepares this job. Populate filesToCopy and update the # of files and
		 * bytes to copy.
		 */
		private void index() throws IOException {
			if (task.getAttribute(KEY_DELETE_EXTRA_DEST_FILES)) {
				indexAndDelete(src, dst);
			} else {
				index(src, dst);
			}
		}

		private void index(File src, File dst) throws IOException {
			for (File child : src.listFiles()) {
				File dstChild = new File(dst, child.getName());
				if (child.isDirectory()) {
					if (dstChild.exists()) {
						if (!dstChild.isDirectory()) {
							throw new IOException("the file "
									+ dstChild.getAbsolutePath()
									+ " exists as a file, but this operation expects a directory");
						}
					} else {
						FileUtils.mkdir(dstChild);
					}
					index(child, dstChild);
				} else {
					if (dstChild.getName().equals(".DS_Store"))
						continue;

					Collection<String> terms = Arrays.asList(
							"BufferedAnimationPanel.java",
							"edit2_control_zoom_normal.png",
							"edit2_control_zoom_normal@2x.png",
							"tooltip_back_bottom.png",
							"tooltip_back_bottom@2x.png",
							"tooltip_back_bottom_arrow.png",
							"tooltip_back_bottom_arrow@2x.png",
							"tooltip_back_bottom_left.png",
							"tooltip_back_bottom_left@2x.png",
							"tooltip_back_bottom_right.png",
							"tooltip_back_bottom_right@2x.png",
							"tooltip_back_left.png", "tooltip_back_left@2x.png",
							"tooltip_back_left_arrow.png",
							"tooltip_back_left_arrow@2x.png",
							"tooltip_back_middle.png",
							"tooltip_back_middle@2x.png",
							"tooltip_back_right.png",
							"tooltip_back_right@2x.png",
							"tooltip_back_right_arrow.png",
							"tooltip_back_right_arrow@2x.png",
							"tooltip_back_top_arrow.png",
							"tooltip_back_top_arrow@2x.png", "trayIcon.png",
							"frame_icon.png", "Equations.java");
					if (terms.contains(dstChild.getName()))
						continue;

					if (dstChild.exists()) {
						if (IOUtils.equals(child, dstChild)) {
							continue;
						}
						// if we use indexAndDelete we can delete/overwrite dst
						// files, but this operation never deletes data
						// If we ever add a way to prompt the user with q's,
						// this is a good candidate
						throw new IOException("a file named " + child.getName()
								+ " already exists at "
								+ dst.getAbsolutePath());
					}
					enqueue(child);
				}
			}
		}

		private Map<String, File> getFiles(File dir) {
			Map<String, File> returnValue = new HashMap<>();
			for (File child : dir.listFiles()) {
				returnValue.put(child.getName(), child);
			}
			return returnValue;
		}

		private void enqueue(File srcFile) {
			long size = srcFile.length();
			task.incrementWorkload(size);
			filesToCopy.put(srcFile, size);
		}

		private void indexAndDelete(File src, File dst) throws IOException {
			Map<String, File> srcFiles = getFiles(src);
			Map<String, File> dstFiles = getFiles(dst);

			for (Entry<String, File> srcEntry : srcFiles.entrySet()) {
				File dstFile = dstFiles.remove(srcEntry.getKey());
				if (dstFile == null) {
					enqueue(srcEntry.getValue());
				} else if (dstFile.isDirectory()) {
					// if we reach this point: maybe we should add a way to
					// prompt the user for feedback? this could be very harmful.
					throw new IOException("will not delete directory "
							+ dstFile.getAbsolutePath() + " to copy file "
							+ srcEntry.getValue().getName());
				} else if (IOUtils.equals(srcEntry.getValue(), dstFile)) {
					// continue;
				} else {
					// the files are different
					if (!dstFile.delete())
						throw new IOException("File.delete() failed for "
								+ dstFile.getAbsolutePath());
					enqueue(srcEntry.getValue());
				}
			}

			for (File dstFile : dstFiles.values()) {
				if (!dstFile.delete())
					throw new IOException("File.delete() failed for "
							+ dstFile.getAbsolutePath());
			}
		}
	}

	transient List<ActionListener> cancelListeners = new LinkedList<>();
	transient List<ActionListener> finishListeners = new LinkedList<>();
	transient Thread workerThread;

	public DirectoryCopyTask(File srcDir, File dstDir,
			boolean deleteExtraDestFiles) {
		if (!srcDir.isDirectory()) {
			throw new IllegalArgumentException("srcDir must be a directory ("
					+ srcDir.getAbsolutePath() + ")");
		}
		if (dstDir.exists() && !dstDir.isDirectory()) {
			throw new IllegalArgumentException("dstDir must be a directory ("
					+ dstDir.getAbsolutePath() + ")");
		}
		setAttribute(KEY_CURRENT_FILES_COPIED, 0);
		setAttribute(KEY_TOTAL_FILES_TO_COPY, 0);
		setAttribute(KEY_CURRENT_BYTES_COPIED, 0L);
		setAttribute(KEY_TOTAL_BYTES_TO_COPY, 0L);
		setAttribute(KEY_FINISHED, Boolean.FALSE);
		setAttribute(KEY_CANCELLED, Boolean.FALSE);
		setAttribute(KEY_SOURCE_DIRECTORY, srcDir);
		setAttribute(KEY_DEST_DIRECTORY, dstDir);
		setAttribute(KEY_DELETE_EXTRA_DEST_FILES, deleteExtraDestFiles);
		workerThread = new Thread(new WorkerRunnable(this));
		workerThread.start();

		addPropertyChangeListener(KEY_CANCELLED.getName(),
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						for (ActionListener l : cancelListeners
								.toArray(new ActionListener[0])) {
							try {
								l.actionPerformed(
										new ActionEvent(DirectoryCopyTask.this,
												0, "cancelled"));
							} catch (Exception e) {
								Thread.currentThread()
										.getUncaughtExceptionHandler()
										.uncaughtException(
												Thread.currentThread(), e);
							}
						}
					}

				});

		addPropertyChangeListener(KEY_FINISHED.getName(),
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						for (ActionListener l : finishListeners
								.toArray(new ActionListener[0])) {
							try {
								l.actionPerformed(new ActionEvent(
										DirectoryCopyTask.this, 0, "finished"));
							} catch (Exception e) {
								Thread.currentThread()
										.getUncaughtExceptionHandler()
										.uncaughtException(
												Thread.currentThread(), e);
							}
						}
					}

				});

	}

	public Throwable getThrowable() {
		return getAttribute(KEY_THROWABLE);
	}

	void setThrowable(Throwable t) {
		setAttribute(KEY_THROWABLE, t);
	}

	void incrementProgress(long fileSize, File copiedFile) {
		getAttributeLock().writeLock().lock();
		try {
			int fileCount = getAttribute(KEY_CURRENT_FILES_COPIED);
			long byteCount = getAttribute(KEY_CURRENT_BYTES_COPIED);

			setAttribute(KEY_CURRENT_FILE, copiedFile);
			setAttribute(KEY_CURRENT_FILES_COPIED, fileCount + 1);
			setAttribute(KEY_CURRENT_BYTES_COPIED, byteCount + fileSize);
		} finally {
			getAttributeLock().writeLock().unlock();
		}
	}

	void incrementWorkload(long fileSize) {
		getAttributeLock().writeLock().lock();
		try {
			int fileCount = getAttribute(KEY_TOTAL_FILES_TO_COPY);
			long byteCount = getAttribute(KEY_TOTAL_BYTES_TO_COPY);

			setAttribute(KEY_TOTAL_FILES_TO_COPY, fileCount + 1);
			setAttribute(KEY_TOTAL_BYTES_TO_COPY, byteCount + fileSize);
		} finally {
			getAttributeLock().writeLock().unlock();
		}
	}

	@Override
	public void cancel() {
		setAttribute(KEY_CANCELLED, true);
	}

	@Override
	public boolean isCancelled() {
		return Boolean.TRUE.equals(getAttribute(KEY_CANCELLED));
	}

	@Override
	public boolean isFinished() {
		return Boolean.TRUE.equals(getAttribute(KEY_FINISHED));
	}

	@Override
	public void addCancelListener(ActionListener l) {
		cancelListeners.add(l);
	}

	@Override
	public void addFinishListener(ActionListener l) {
		finishListeners.add(l);
	}

	@Override
	public void removeCancelListener(ActionListener l) {
		cancelListeners.remove(l);

	}

	@Override
	public void removeFinishListener(ActionListener l) {
		finishListeners.remove(l);
	}

	/**
	 * Block until this task is complete.
	 */
	public void join() throws Exception {
		workerThread.join();
		Throwable t = getThrowable();
		if (t instanceof Exception)
			throw (Exception) t;
		if (t instanceof Error)
			throw (Error) t;
	}

}
