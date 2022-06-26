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
package com.pump.io.location;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.pump.UserCancelledException;
import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.util.BasicReceiver;
import com.pump.util.Receiver;

/**
 * This represents an archive and all of its children.
 * <P>
 * This abstract superclass manages the tree structure and loading logic for
 * subclasses. The loading is executed in a separated thread, but when there are
 * no entities waiting for information about an archive: that thread is
 * cancelled.
 * <P>
 * Both the root node (which doesn't have an <code>ArchiveEntry</code>) and all
 * child nodes (which do) are represented with this class. This design choice
 * introduces some inner complexity, but it was to simplify developer's
 * interaction with this class. (That is: now they can just check
 * <code>if(x instanceof TarArchiveLocation)</code> without worrying about
 * whether an <code>IOLocation</code> might really resemble
 * <code>ArchiveLocation$NodeSubclass</code>.)
 * 
 * @param <T>
 *            an object describing each archive entry. For example: a
 *            <code>ZipEntry</code> or <code>TarEntry</code>.
 */
public abstract class ArchiveLocation<T> extends IOLocation {

	/** This will be null for the root node. */
	private T entry;

	/** The root of this archive; it may be <code>this</code>. */
	protected final ArchiveLocation<T> root;

	/**
	 * For the root node, this points to the archive. For all other nodes this
	 * is null.
	 */
	protected final IOLocation archive;

	private IOLocation parent;
	protected List<ArchiveLocation<T>> children = new ArrayList<ArchiveLocation<T>>();

	/**
	 * The constructor for the root node.
	 * 
	 * @param archive
	 *            the resource this archive refers to. For example: this may be
	 *            a "*.tar" or "*.zip" file.
	 * 
	 **/
	protected ArchiveLocation(IOLocation archive) {
		if (archive == null)
			throw new NullPointerException();

		entry = null;
		root = this;
		this.archive = archive;
	}

	/**
	 * The constructor for all non-root nodes.
	 * 
	 * @param root
	 *            the root node.
	 * @param entry
	 *            the entry-specific information this node represents.
	 */
	protected ArchiveLocation(ArchiveLocation<T> root, T entry) {
		if (root == null)
			throw new NullPointerException();
		if (entry == null)
			throw new NullPointerException();

		this.entry = entry;
		this.root = root;
		this.archive = null;
	}

	@Override
	public boolean isNavigable() {
		if (this == root) {
			return true;
		}
		return isDirectory();
	}

	/**
	 * Return a description of this archive entry. This will return null for the
	 * root container.
	 */
	public final T getArchiveEntry() {
		return entry;
	}

	@Override
	public final String getPath() {
		if (this == root) {
			return archive.getPath();
		}
		return root.getPath() + "$" + getArchivePath();
	}

	@Override
	public final String getName() {
		if (this == root) {
			return archive.getName();
		}
		char ch = getSeparatorChar();
		String archivePath = getArchivePath();
		int i = archivePath.lastIndexOf(ch);
		if (i == archivePath.length() - 1 && isDirectory()) {
			i = archivePath.lastIndexOf(ch, i - 1);
		}
		String name = archivePath.substring(i + 1);

		// if the directory ends with a "/", then chop it off:
		if (name.endsWith("" + getSeparatorChar()) && name.length() > 1) {
			name = name.substring(0, name.length() - 1);
		}

		return name;
	}

	/**
	 * Returns false.
	 * 
	 */
	@Override
	public final boolean canWrite() {
		return false;
	}

	@Override
	public final IOLocation getParent() {
		return parent;
	}

	public final boolean setParent(IOLocation newParent) {
		if (this == root) {
			if (Objects.equals(parent, newParent))
				return false;
			this.parent = newParent;
			return true;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public final IOLocation getChild(String name) throws IOException {
		int processedChildLimit = -1;
		Cancellable cancellable = new BasicCancellable();
		root.getLoadingThread().addConsumer(cancellable);
		try {
			while (true) {
				while (processedChildLimit < children.size()) {
					ArchiveLocation<T> child = children
							.get(++processedChildLimit);
					if (child.getName().equals(name))
						return child;
				}
				if (root.isLoaded(250)) {
					if (processedChildLimit == children.size())
						return null;
				}
			}
		} finally {
			root.getLoadingThread().removeConsumer(cancellable);
		}
	}

	@Override
	public final void mkdir() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public final URL getURL() {
		try {
			if (this == root) {
				return archive.getURL();
			} else {
				String ext = root.archive.getExtension();
				return new URL(ext + ":" + root.archive.getURL().toString()
						+ "$" + getArchivePath());
			}
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public IOLocation setName(String s) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public OutputStream createOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete() throws IOException {
		if (this == root) {
			archive.delete();
			return;
		}
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean exists() {
		return root.archive.exists();
	}

	@Override
	public boolean isAlias() {
		return false;
	}

	@Override
	public boolean canRead() {
		return root.archive.canRead();
	}

	@Override
	public IOLocation[] listChildren(Receiver<IOLocation> receiver,
			BasicCancellable cancellable) {
		int processedChildCount = 0;
		if (cancellable == null)
			cancellable = new BasicCancellable();
		if (receiver == null)
			receiver = new BasicReceiver<IOLocation>();
		root.getLoadingThread().addConsumer(cancellable);
		try {
			while (true) {
				if (cancellable.isCancelled())
					throw new UserCancelledException();

				while (processedChildCount < children.size()) {
					ArchiveLocation<T> child = children
							.get(processedChildCount);
					receiver.add(child);

					processedChildCount++;
				}
				if (root.isLoaded(250)) {
					if (processedChildCount == children.size()) {
						cancellable.finish();
						return children
								.toArray(new IOLocation[children.size()]);
					}
				}
			}
		} finally {
			root.getLoadingThread().removeConsumer(cancellable);
		}
	}

	@Override
	public String toString() {
		String s = getClass().getName();
		int i = s.lastIndexOf('.');
		s = s.substring(i + 1) + "[ path=\"" + getPath() + "\"]";
		return s;
	}

	private long creatorThreadID = -1;

	static long threadCtr = 0;

	private class LoadingThread extends Thread {

		List<Cancellable> cancellables = new ArrayList<Cancellable>();
		final long id;

		/** When the cancellables were first emptied */
		long emptyCancellablesTimeStamp = -1;

		/**
		 * All the directories we created by resorting to
		 * <code>createDirectory()</code>.
		 * 
		 */
		Set<ArchiveLocation<T>> madeUpDirectories = new HashSet<ArchiveLocation<T>>();

		LoadingThread(long id) {
			super("ArchiveLocation-LoadingThread-" + id);
			this.id = id;
		}

		void addConsumer(Cancellable c) {
			synchronized (ArchiveLocation.this) {
				cancellables.add(c);
			}
		}

		void removeConsumer(Cancellable c) {
			synchronized (ArchiveLocation.this) {
				cancellables.remove(c);
			}
		}

		public void run() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Receiver<T> archiveReceiver = new Receiver() {
				public void add(Object... elements) {
					checkCancellation();
					for (Object element : elements) {
						T t = (T) element;
						ArchiveLocation loc = createArchiveLocation(
								ArchiveLocation.this, t);
						loc.creatorThreadID = id;
						place(loc);
					}
				}
			};
			try {
				listArchiveEntries(archiveReceiver);
			} catch (IOException e) {
				// TODO: process exceptions better
				e.printStackTrace();
			}
		}

		/**
		 * This makes sure we still have consumers who are interested in
		 * cataloging this data. If 3 seconds pass and no entity is listening to
		 * this thread, then we throw a UserCancelledException.
		 * 
		 */
		private void checkCancellation() {
			synchronized (ArchiveLocation.this) {
				int activeConsumerCount = 0;
				Iterator<Cancellable> iter = cancellables.iterator();
				while (iter.hasNext()) {
					Cancellable c = iter.next();
					if (c.isCancelled()) {
						iter.remove();
					} else {
						activeConsumerCount++;
					}
				}

				if (activeConsumerCount == 0) {
					if (emptyCancellablesTimeStamp == -1) {
						emptyCancellablesTimeStamp = System.currentTimeMillis();
					} else {
						long elapsedTime = System.currentTimeMillis()
								- emptyCancellablesTimeStamp;
						if (elapsedTime > 3000 && (!root.locGroup.isActive())) {

							/*
							 * If we're cancelling: then we contain incomplete
							 * information. We'll have to load again from
							 * scratch:
							 */
							ArchiveLocation.this.loadingThread = null;

							/*
							 * However: do not clear the children. The user may
							 * still navigate whatever-we-loaded on a second
							 * attempt while we wait to catch up.
							 */

							throw new UserCancelledException();
						}
					}
				} else {
					emptyCancellablesTimeStamp = -1;
				}
			}
		}

		private void place(ArchiveLocation<T> newElement) {
			String archivePath = newElement.getArchivePath();
			String[] path = split(archivePath, getSeparatorChar());

			if (path[path.length - 1].length() == 0) {
				String[] trimmedPath = new String[path.length - 1];
				System.arraycopy(path, 0, trimmedPath, 0, path.length - 1);
				path = trimmedPath;
			}

			ArchiveLocation<T> parent = ArchiveLocation.this;
			String pathSoFar = "";
			identifyChild: for (int a = 0; a < path.length - 1; a++) {
				pathSoFar = pathSoFar + path[a] + getSeparatorChar();

				ArchiveLocation<T> existingDir = getChild(parent, path[a]);
				if (existingDir != null) {
					parent = existingDir;
					continue identifyChild;
				}

				ArchiveLocation<T> newDirectory = createArchiveLocation(root,
						createDirectory(pathSoFar));
				newDirectory.creatorThreadID = id;
				madeUpDirectories.add(newDirectory);

				parent.children.add(newDirectory);
				newDirectory.parent = parent;

				parent = newDirectory;
			}

			ArchiveLocation<T> existingNode = getChild(parent,
					path[path.length - 1]);
			if (existingNode != null) {
				if (madeUpDirectories.remove(existingNode)) {
					/*
					 * In this unlikely scenario: we had to create the directory
					 * already to place a file, but we didn't have an archive
					 * entry for it at the time. Now we have a legit entry
					 * describing the directory. So swap out the entry of the
					 * existing child node with the more accurate one. (The
					 * incoming one will have more accurate meta info.)
					 */
					existingNode.entry = newElement.entry;
				} else {
					/*
					 * If this was created by a previous LoadingThread: that's
					 * OK. (That just means the last thread was aborted.)
					 * Otherwise: the archive contains duplicate entries and
					 * that's weird.
					 */
					if (newElement.creatorThreadID != existingNode.creatorThreadID) {
						existingNode.creatorThreadID = newElement.creatorThreadID;
						return;
					}
					throw new RuntimeException("duplicate entry: " + newElement);
				}
			} else {
				parent.children.add(newElement);
				newElement.parent = parent;
			}
		}

		private ArchiveLocation<T> getChild(ArchiveLocation<T> parent,
				String name) {
			for (int a = 0; a < parent.children.size(); a++) {
				String currentName = parent.children.get(a).getName();
				if (currentName.endsWith("" + getSeparatorChar()))
					currentName = currentName.substring(0,
							currentName.length() - 1);
				if (currentName.equals(name)) {
					return parent.children.get(a);
				}
			}
			return null;
		}
	}

	/**
	 * This is invoked when the root node is cataloging the contents of an
	 * archive and it comes across "abc/foo.jpg" before there is a directory
	 * titled "abc". We'd much rather have a separate archive entry for the
	 * "abc" directory (so we can correctly define all the meta info), but this
	 * runner-up approach still gets the job done.
	 * 
	 * @param path
	 *            the full archive path name of a directory we need to catalog a
	 *            file.
	 * @return an archive entry representing that path name.
	 */
	protected abstract T createDirectory(String path);

	private LoadingThread loadingThread = null;

	/**
	 * Returns the LoadingThread for a root node. This will create and start the
	 * LoadingThread if it does not already exist.
	 */
	private LoadingThread getLoadingThread() {
		if (this != root)
			throw new RuntimeException(
					"only the root should have a loading thread");

		synchronized (this) {
			if (loadingThread == null) {
				loadingThread = new LoadingThread(threadCtr++);
				loadingThread.start();
			}
		}
		return loadingThread;
	}

	/**
	 * Return true if the root is fully loaded.
	 * 
	 * @param millisToBlock
	 *            an optional amount of milliseconds to block while we wait for
	 *            the root to fully load. If this argument is positive: then
	 *            this method should return either when the root is fully loaded
	 *            or when this number of milliseconds have passed -- whichever
	 *            is shorter.
	 * @return true if the root is fully loaded.
	 */
	private boolean isLoaded(long millisToBlock) {
		if (this != root) {
			return root.isLoaded(millisToBlock);
		}

		Thread loadingThread = getLoadingThread();

		try {
			loadingThread.join(millisToBlock);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return !loadingThread.isAlive();
	}

	/**
	 * Return the character that archive path names treat as a separator.
	 */
	protected abstract char getSeparatorChar();

	/**
	 * List all the archive entries.
	 * <p>
	 * This is only invoked on the root ArchiveLocation.
	 * 
	 * @param receiver
	 *            the object to give new archive entries to.
	 */
	protected abstract void listArchiveEntries(Receiver<T> receiver)
			throws IOException;

	protected abstract ArchiveLocation<T> createArchiveLocation(
			ArchiveLocation<T> root, T t);

	private static String[] split(String path, char ch) {
		if (path == null)
			throw new NullPointerException();
		List<String> segments = new ArrayList<String>();
		while (true) {
			int i = path.indexOf(ch);
			if (i == -1) {
				segments.add(path);
				return segments.toArray(new String[segments.size()]);
			}
			String lhs = path.substring(0, i);
			String rhs = path.substring(i + 1);
			segments.add(lhs);
			path = rhs;
		}
	}

	/**
	 * Return the path within the archive to this resource, or null if this is
	 * the root of this archive.
	 */
	protected abstract String getArchivePath();

	@Override
	public final boolean isDirectory() {
		if (this == root) {
			return archive.isDirectory();
		}
		return isDirectory(entry);
	}

	protected abstract boolean isDirectory(T t);

	@Override
	public final long getModificationDate() throws IOException {
		if (this == root) {
			return archive.getModificationDate();
		}
		return getModificationDate(entry);
	}

	protected abstract long getModificationDate(T t);

	@Override
	public final boolean isHidden() {
		if (this == root) {
			return archive.isHidden();
		}
		return isHidden(entry);
	}

	protected abstract boolean isHidden(T t);

	@Override
	public final long length() throws IOException {
		if (this == root) {
			return archive.length();
		}
		return length(entry);
	}

	protected abstract long length(T t);

	@Override
	public final InputStream createInputStream() throws IOException {
		if (this == root) {
			return archive.createInputStream();
		}
		return root.createInputStream(entry);
	}

	protected abstract InputStream createInputStream(T t) throws IOException;

	private IOLocationGroup locGroup = new BasicLocationGroup();

	@Override
	public IOLocationGroup getGroup() {
		if (this != root) {
			return root.getGroup();
		}
		return locGroup;
	}
}