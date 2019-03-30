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
package com.pump.io.location;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.pump.UserCancelledException;
import com.pump.swing.BasicCancellable;
import com.pump.swing.Cancellable;
import com.pump.util.BasicReceiver;
import com.pump.util.Receiver;

/**
 * This is an <code>IOLocation</code> that caches most simple properties
 * (include children) for immediate return.
 * <p>
 * It's tempting to think that these calls should generally be fast, but we've
 * seen <code>java.io.File.exists()</code> stall for several seconds at a time
 * when the file system is experiencing network connectivity problems.
 * <p>
 * By caching all of these values: it becomes imperative that you invoke
 * {@link #flush()} at the appropriate times to clear cached data. For example:
 * after writing, deleting or renaming a file the data should be flushed. You
 * might also consider flushing cached data when a file navigation dialog is
 * reactivated (because the user might have just switched from the OS back to
 * the app after manipulating files). But the other 90% of the time: the cached
 * value should be reasonably trustworthy.
 */
public abstract class CachedLocation extends IOLocation {

	LoadChildrenTask loadChildrenTask;
	GetPathTask getPathTask;
	GetNameTask getNameTask;
	CanWriteTask canWriteTask;
	CanReadTask canReadTask;
	IsDirectoryTask isDirectoryTask;
	LengthTask lengthTask;
	ExistsTask existsTask;
	IsHiddenTask isHiddenTask;
	IsAliasTask isAliasTask;
	IsNavigableTask isNavigableTask;
	GetModificationDateTask getModificationDateTask;

	public CachedLocation() {
		flush();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String cs = getClass().getName();
		cs = cs.substring(cs.lastIndexOf('.') + 1);
		sb.append(cs);
		sb.append('[');
		sb.append("\"" + getPath() + "\"");
		sb.append(']');
		return sb.toString();
	}

	public synchronized void flush() {
		loadChildrenTask = new LoadChildrenTask();
		getPathTask = new GetPathTask();
		getNameTask = new GetNameTask();
		canWriteTask = new CanWriteTask();
		canReadTask = new CanReadTask();
		isDirectoryTask = new IsDirectoryTask();
		lengthTask = new LengthTask();
		existsTask = new ExistsTask();
		isHiddenTask = new IsHiddenTask();
		isAliasTask = new IsAliasTask();
		isNavigableTask = new IsNavigableTask();
		getModificationDateTask = new GetModificationDateTask();
		super.flush();
	}

	/**
	 * A cancellable that represents n-many other Cancellables. When all of
	 * these cancellables are cancelled: then this object is also cancelled.
	 */
	private static class MasterCancellable extends BasicCancellable {
		Set<Cancellable> allCancellables = new HashSet<Cancellable>();

		public void add(final Cancellable cancellable) {
			synchronized (cancellable) {
				if (cancellable.isCancelled())
					return;
				cancellable.addCancelListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						allCancellables.remove(cancellable);
						if (allCancellables.size() == 0)
							cancel();
					}
				});
				allCancellables.add(cancellable);
			}
		}
	}

	static int threadCtr = 0;

	/**
	 * The thread used to load children.
	 * <p>
	 * This becomes tricky when you consider multithreaded requests and
	 * cancellations. This is the only cached value in this class that supports
	 * cancellation, so it is the only task that gets its own thread.
	 * <P>
	 * For example: Thread A might first request children, so
	 * <code>doListChildren()</code> needs to be invoked. But then if Thread B
	 * also requests children, and Thread A cancels its request: then we should
	 * continue listing children (unless Thread B also cancels). This can only
	 * be achieved in a way that quickly returns control to Thread A by starting
	 * a helper thread.
	 */
	class LoadChildrenTask extends Thread {
		MasterCancellable masterCancellable = new MasterCancellable();
		BasicReceiver<IOLocation> masterReceiver = new BasicReceiver<IOLocation>();
		boolean started = false;
		boolean finished = false;

		public LoadChildrenTask() {
			super("LoadChildrenTask-" + (threadCtr++));
		}

		public void run() {
			try {
				doListChildren(masterReceiver, masterCancellable);
				finished = true;
			} catch (UserCancelledException e) {
				flush();
			}
		}

		IOLocation[] join(Receiver<IOLocation> receiver,
				BasicCancellable cancellable) {
			synchronized (masterReceiver) {
				masterReceiver.add(receiver, true);
				masterCancellable.add(cancellable);

				if (!started) {
					start();
					started = true;
				}
			}

			while (!finished) {
				try {
					join(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (cancellable.isCancelled())
					throw new UserCancelledException();
			}

			return masterReceiver.toArray(new IOLocation[masterReceiver
					.getSize()]);
		}
	}

	/**
	 * This method starts a separate thread that gathers and caches the children
	 * of this location. Subsequent calls to listChildren() will return
	 * immediately, unless <code>flush()</code> has cleared the cached data of
	 * this <code>IOLocation</code>.
	 * 
	 * <p>
	 * TODO: take into account the last modified time.
	 * 
	 * @see #doListChildren(Receiver, Cancellable)
	 */
	@Override
	public final IOLocation[] listChildren(Receiver<IOLocation> receiver,
			BasicCancellable cancellable) {
		if (cancellable == null)
			cancellable = new BasicCancellable();
		if (receiver == null)
			receiver = new BasicReceiver<IOLocation>();
		return loadChildrenTask.join(receiver, cancellable);
	}

	/**
	 * List the children of this location. This is invoked by
	 * <code>listChildren()</code>, but this value is cached for future use
	 * until <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #listChildren(Receiver, BasicCancellable)
	 */
	protected abstract void doListChildren(Receiver<IOLocation> receiver,
			Cancellable cancellable);

	/**
	 * Return the path of this location. This is invoked by
	 * <code>getPath()</code>, but this value is cached for future use until
	 * <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #getPath()
	 */
	protected abstract String doGetPath();

	/**
	 * Return the name of this location. This is invoked by
	 * <code>getName()</code>, but this value is cached for future use until
	 * <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #getName()
	 */
	protected abstract String doGetName();

	/**
	 * Return whether this location can be written to. This is invoked by
	 * <code>canWrite()</code>, but this value is cached for future use until
	 * <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #canWrite()
	 */
	protected abstract boolean doCanWrite();

	/**
	 * Return whether this location can be read. This is invoked by
	 * <code>canRead()</code>, but this value is cached for future use until
	 * <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #canRead()
	 */
	protected abstract boolean doCanRead();

	/**
	 * Return whether this location is a directory. This is invoked by
	 * <code>isDirectory()</code>, but this value is cached for future use until
	 * <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #isDirectory()
	 */
	protected abstract boolean doIsDirectory();

	/**
	 * Return whether this location is hidden. This is invoked by
	 * <code>isHidden()</code>, but this value is cached for future use until
	 * <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #isHidden()
	 */
	protected abstract boolean doIsHidden();

	/**
	 * Return whether this location is an alias. This is invoked by
	 * <code>isAlias</code>, but this value is cached for future use until
	 * <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #isAlias()
	 */
	protected abstract boolean doIsAlias();

	/**
	 * Return whether this location is navigable. This is invoked by
	 * <code>isNavigable()</code>, but this value is cached for future use until
	 * <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #isNavigable()
	 */
	protected abstract boolean doIsNavigable();

	/**
	 * Return whether this location exists. This is invoked by
	 * <code>exists()</code>, but this value is cached for future use until
	 * <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #exists()
	 */
	protected abstract boolean doExists();

	/**
	 * Return the file length of this location. This is invoked by
	 * <code>length()</code>, but this value is cached for future use until
	 * <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #length()
	 */
	protected abstract long doLength() throws IOException;

	/**
	 * Return the last modification date of this location. This is invoked by
	 * <code>getModificationDate()</code>, but this value is cached for future
	 * use until <code>flush()</code> is called.
	 * 
	 * @see #flush()
	 * @see #getModificationDate()
	 */
	protected abstract long doGetModificationDate() throws IOException;

	private class GetPathTask {
		String path = null;

		String join() {
			synchronized (this) {
				if (path == null)
					path = doGetPath();
			}
			return path;
		}
	}

	private class GetNameTask {
		String name = null;

		String join() {
			synchronized (this) {
				if (name == null)
					name = doGetName();
			}
			return name;
		}
	}

	private class CanWriteTask {
		Boolean canWrite = null;

		Boolean join() {
			synchronized (this) {
				if (canWrite == null)
					canWrite = doCanWrite();
			}
			return canWrite;
		}
	}

	private class CanReadTask {
		Boolean canRead = null;

		Boolean join() {
			synchronized (this) {
				if (canRead == null)
					canRead = doCanRead();
			}
			return canRead;
		}
	}

	private class IsDirectoryTask {
		Boolean isDirectory = null;

		Boolean join() {
			synchronized (this) {
				if (isDirectory == null)
					isDirectory = doIsDirectory();
			}
			return isDirectory;
		}
	}

	private class IsHiddenTask {
		Boolean isHidden = null;

		Boolean join() {
			synchronized (this) {
				if (isHidden == null)
					isHidden = doIsHidden();
			}
			return isHidden;
		}
	}

	private class IsAliasTask {
		Boolean isAlias = null;

		Boolean join() {
			synchronized (this) {
				if (isAlias == null)
					isAlias = doIsAlias();
			}
			return isAlias;
		}
	}

	private class IsNavigableTask {
		Boolean isNavigable = null;

		Boolean join() {
			synchronized (this) {
				if (isNavigable == null)
					isNavigable = doIsNavigable();
			}
			return isNavigable;
		}
	}

	private class ExistsTask {
		Boolean exists = null;

		Boolean join() {
			synchronized (this) {
				if (exists == null)
					exists = doExists();
			}
			return exists;
		}
	}

	private class LengthTask {
		Long length = null;
		IOException e;

		long join() throws IOException {
			synchronized (this) {
				if (length == null && e == null) {
					try {
						length = doLength();
					} catch (IOException e) {
						this.e = e;
					}
				}
			}
			if (e != null)
				throw e;
			return length;
		}
	}

	private class GetModificationDateTask {
		Long modificationDate = null;
		IOException e;

		long join() throws IOException {
			synchronized (this) {
				if (modificationDate == null && e == null) {
					try {
						modificationDate = doGetModificationDate();
					} catch (IOException e) {
						this.e = e;
					}
				}
			}
			if (e != null)
				throw e;
			return modificationDate;
		}
	}

	/**
	 * Return this location's path. This method originally calls
	 * <code>doGetPath()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doGetPath()
	 */
	@Override
	public final String getPath() {
		return getPathTask.join();
	}

	/**
	 * Return this location's name. This method originally calls
	 * <code>doGetName()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doGetName()
	 */
	@Override
	public final String getName() {
		return getNameTask.join();
	}

	/**
	 * Return whether this location can be written to. This method originally
	 * calls <code>doCanWrite()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doCanWrite()
	 */
	@Override
	public final boolean canWrite() {
		return canWriteTask.join();
	}

	/**
	 * Return whether this location is a directory. This method originally calls
	 * <code>doIsDirectory()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doIsDirectory()
	 */
	@Override
	public final boolean isDirectory() {
		return isDirectoryTask.join();
	}

	/**
	 * Return this location's length. This method originally calls
	 * <code>doLength()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doLength()
	 */
	@Override
	public final long length() throws IOException {
		return lengthTask.join();
	}

	/**
	 * Return this location exists. This method originally calls
	 * <code>doExists()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doExists()
	 */
	@Override
	public final boolean exists() {
		return existsTask.join();
	}

	/**
	 * Return this location's modification date. This method originally calls
	 * <code>doGetModificationDate()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doGetModificationDate()
	 */
	@Override
	public final long getModificationDate() throws IOException {
		return getModificationDateTask.join();
	}

	/**
	 * Return whether this location is hidden. This method originally calls
	 * <code>doIsHidden()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doGetPath()
	 */
	@Override
	public final boolean isHidden() {
		return isHiddenTask.join();
	}

	/**
	 * Return whether this location is an alias. This method originally calls
	 * <code>doIsAlias()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doIsAlias()
	 */
	@Override
	public final boolean isAlias() {
		return isAliasTask.join();
	}

	/**
	 * Return whether this location is navigable. This method originally calls
	 * <code>doIsNavigable()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doIsNavigable()
	 */
	@Override
	public final boolean isNavigable() {
		return isNavigableTask.join();
	}

	/**
	 * Return whether this location can be read. This method originally calls
	 * <code>doCanRead()</code>, but this value is then cached until
	 * <code>flush()</code> is invoked.
	 * 
	 * @see #doCanRead()
	 */
	@Override
	public final boolean canRead() {
		return canReadTask.join();
	}
}