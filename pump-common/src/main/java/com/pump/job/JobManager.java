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
package com.pump.job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This manages a queue of {@link com.pump.job.Job} objects. Jobs are first
 * sorted by priority, and then handled on a FIFO basis. If a job is next in
 * line but is classified as being dependent on other (unfinished) jobs, then it
 * is skipped until those jobs are processed.
 * <p>
 * TODO: This does not resolve circular dependencies.
 * <p>
 * This relies on the <code>java.util.concurrent.Executor</code> and related
 * classes, but it offers more useful feedback to help keep UIs up-to-date.
 *
 */
public class JobManager {
	/**
	 * This offers a model for simple notification of what this JobManager is
	 * currently doing.
	 */
	public static interface Listener {
		/**
		 * This is called when an error occurs executing a job.
		 * <p>
		 * This is probably invoked on a thread that executes jobs, so this
		 * should not perform EDT work and should probably be very light
		 * (triggering other threads if necessary).
		 */
		public void jobError(JobManager manager, Job job, Throwable t);

		/**
		 * This is called before a job begins executing. When this is invoked:
		 * the active job count of the JobManager will have changed.
		 * <p>
		 * This is probably invoked on a thread that executes jobs, so this
		 * should not perform EDT work and should probably be very light
		 * (triggering other threads if necessary).
		 */
		public void jobAdded(JobManager manager, Job... job);

		/**
		 * This is called when a job finished executing. When this is invoked:
		 * the active job count of the JobManager will have changed.
		 * <p>
		 * This is probably invoked on a thread that executes jobs, so this
		 * should not perform EDT work and should probably be very light
		 * (triggering other threads if necessary).
		 */
		public void jobRemoved(JobManager manager, Job... job);

		/**
		 * This is called when a job is skipped over, either because it was
		 * cancelled or because it was removed from the pending job queue in
		 * this manager. When this is invoked: the pending count of the
		 * JobManager will have changed.
		 * <p>
		 * This is probably invoked on a thread that executes jobs, so this
		 * should not perform EDT work and should probably be very light
		 * (triggering other threads if necessary).
		 */
		public void jobSkipped(JobManager manager, Job... job);

		/**
		 * This is called when a job is added to the queue of pending jobs. When
		 * this is invoked: the pending job count of the JobManager will have
		 * changed.
		 * <p>
		 * This is probably invoked on the thread that added the job.
		 */
		public void jobQueued(JobManager manager, Job... job);
	}

	private static class ChangeListenerWrapper implements Listener {
		ChangeListener changeListener;

		ChangeListenerWrapper(ChangeListener cl) {
			changeListener = cl;
		}

		@Override
		public void jobAdded(JobManager m, Job... job) {
			changeListener.stateChanged(new ChangeEvent(m));
		}

		@Override
		public void jobError(JobManager m, Job job, Throwable throwable) {
			changeListener.stateChanged(new ChangeEvent(m));
		}

		@Override
		public void jobQueued(JobManager m, Job... job) {
			changeListener.stateChanged(new ChangeEvent(m));
		}

		@Override
		public void jobRemoved(JobManager m, Job... job) {
			changeListener.stateChanged(new ChangeEvent(m));
		}

		@Override
		public void jobSkipped(JobManager m, Job... job) {
			changeListener.stateChanged(new ChangeEvent(m));
		}
	}

	private static final int ERROR = -1;
	private static final int ADD = 0;
	private static final int REMOVE = 1;
	private static final int SKIP = 2;
	private static final int QUEUE = 3;

	private Runnable processQueueRunnable = new Runnable() {
		public void run() {
			while (true) {
				Job job = null;
				synchronized (active) {
					job = popHighestPriorityAvailableJob();
					if (job == null)
						return;
					active.add(job);
				}
				if (job != null) {
					int type = ERROR;
					Throwable throwable = null;
					try {
						if (!job.isCancelled()) {
							fireListeners(ADD, job);
							job.run();
							type = REMOVE;
						} else {
							type = SKIP;
						}
					} catch (Throwable t) {
						throwable = t;
					} finally {
						synchronized (active) {
							active.remove(job);
						}
						synchronized (queue) {
							String id = job.getReplacementId();
							if (id != null)
								jobsByReplacementId.remove(id);
						}
						if (throwable != null) {
							fireErrorListener(job, throwable);
						} else {
							fireListeners(type, job);
						}
					}
				}
			}
		}
	};

	private final static Comparator<Integer> reverseIntComparator = new Comparator<Integer>() {

		public int compare(Integer o1, Integer o2) {
			if (o1.intValue() < o2.intValue()) {
				return 1;
			} else if (o1.intValue() > o2.intValue()) {
				return -1;
			}
			return 0;
		}

	};

	private final TreeMap<Integer, List<Job>> queue = new TreeMap<Integer, List<Job>>(
			reverseIntComparator);
	private final Set<Job> active = new HashSet<Job>();
	private final int threadCount;
	private final Map<String, Job> jobsByReplacementId = new HashMap<>();

	transient List<Listener> listeners;
	transient ExecutorService service;

	public JobManager(int threadCount) {
		this.threadCount = threadCount;
	}

	/** Return the next Job that should be executed. */
	private Job popHighestPriorityAvailableJob() {
		synchronized (queue) {
			Iterator<Integer> keyIter = queue.keySet().iterator();
			while (keyIter.hasNext()) {
				Integer key = keyIter.next();
				List<Job> list = queue.get(key);
				for (int a = 0; a < list.size(); a++) {
					Job job = list.get(a);
					if (isEligibleToStart(job)) {
						list.remove(a);
						if (list.size() == 0) {
							queue.remove(key);
						}
						return job;
					}
				}
			}
			return null;
		}
	}

	/**
	 * Return true if a job is eligible to run. If there are any dependencies
	 * declared in this Job, they must be finished before this job can start.
	 */
	private boolean isEligibleToStart(Job job) {
		Job[] dependencies = job.getDependencies();
		for (Job d : dependencies) {
			/*
			 * Let cancelled jobs run. The Job.run() method checks for this
			 * condition and will throw an exception. This way: the
			 * JobManager.Listener will receive feedback that a job started,
			 * even if it was destined to crash and fail.
			 */
			boolean ok = d.isCancelled() || d.isFinished();
			if (!ok) {
				return false;
			}
		}
		return true;
	}

	/** Add a Listener to this JobManager. */
	public synchronized boolean addListener(Listener l) {
		if (listeners != null && listeners.contains(l))
			return false;
		if (listeners == null)
			listeners = new ArrayList<Listener>();
		if (l instanceof ChangeListenerWrapper) {
			ChangeListenerWrapper incoming = (ChangeListenerWrapper) l;
			// ugh, we have to do more exhaustive search:
			for (Listener l2 : listeners) {
				if (l2 instanceof ChangeListenerWrapper) {
					ChangeListenerWrapper existing = (ChangeListenerWrapper) l2;
					if (existing.changeListener == incoming.changeListener)
						return false;
				}
			}
		}
		listeners.add(l);
		return true;
	}

	/** Remove a Listener to this JobManager. */
	public synchronized boolean removeListener(Listener l) {
		if (listeners == null)
			return false;
		if (l instanceof ChangeListenerWrapper) {
			ChangeListenerWrapper incoming = (ChangeListenerWrapper) l;
			// ugh, we have to do more exhaustive search:
			for (int a = 0; a < listeners.size(); a++) {
				if (listeners.get(a) instanceof ChangeListenerWrapper) {
					ChangeListenerWrapper existing = (ChangeListenerWrapper) listeners
							.get(a);
					if (existing.changeListener == incoming.changeListener) {
						listeners.remove(a);
						return true;
					}
				}
			}
		}
		return listeners.remove(l);
	}

	/** Add one or more a Job to the job queue. */
	public void addJob(Job... jobs) {
		synchronized (queue) {
			for (Job job : jobs) {
				if (job.isFinished()) {
					System.err
							.println("JobManager warning: the job \""
									+ job.getName()
									+ "\" was classified as finished, but it is being resubmitted. Resetting status to unfinished.");
					job.properties.set(Job.FINISHED, false);
				}
				if (job.isCancelled()) {
					System.err
							.println("JobManager warning: the job \""
									+ job.getName()
									+ "\" was classified as cancelled, but it is being resubmitted. Resetting status to uncancelled.");
					job.properties.set(Job.CANCELLED, false);
				}
			}
			for (Job job : jobs) {
				String replacementId = job.getReplacementId();
				if (replacementId != null) {
					Job oldJob = jobsByReplacementId.get(replacementId);
					if (oldJob != null)
						oldJob.cancel();
					jobsByReplacementId.put(replacementId, job);
				}
				Integer key = new Integer(job.getPriority());
				List<Job> list = queue.get(key);
				if (list == null) {
					list = new LinkedList<Job>();
					queue.put(key, list);
				}
				list.add(job);
				if (service == null)
					service = Executors.newFixedThreadPool(threadCount);
				service.execute(processQueueRunnable);
			}
		}
		fireListeners(QUEUE, jobs);
	}

	/** Return all jobs that are currently executing. */
	public Job[] getActiveJobs() {
		synchronized (active) {
			return active.toArray(new Job[active.size()]);
		}
	}

	/** Return all jobs that are waiting to execute, in order of execution. */
	public Job[] getPendingJobs() {
		List<Job> list = new ArrayList<Job>();
		synchronized (queue) {
			Iterator<Integer> iter = queue.descendingKeySet().iterator();
			while (iter.hasNext()) {
				Integer key = iter.next();
				List<Job> jobs = queue.get(key);
				list.addAll(jobs);
			}
		}
		return list.toArray(new Job[list.size()]);
	}

	/**
	 * Returns true if there are any jobs currently being processed.
	 */
	public boolean isActive() {
		synchronized (active) {
			return active.size() > 0;
		}
	}

	/**
	 * Adds a ChangeListener to this JobManager. This is notified of the same
	 * events as a {@link Listener}; it's just more generic.
	 * 
	 * @param l
	 *            this listener will be notified when that state of this manager
	 *            changes (active/inactive) and when jobs finish (or stop)
	 *            running.
	 */
	public boolean addChangeListener(ChangeListener l) {
		return addListener(new ChangeListenerWrapper(l));
	}

	/**
	 * Remove a ChangeListener from this JobManager.
	 */
	public boolean removeChangeListener(ChangeListener l) {
		return removeListener(new ChangeListenerWrapper(l));
	}

	/**
	 * Returns the number of jobs in this JobManager.
	 * 
	 * @param includeActiveJobs
	 *            if true then this also includes the number of active jobs. If
	 *            false then this only includes pending jobs that are not
	 *            currently being processed. (This is no way accounts for
	 *            completed jobs.)
	 * @return if false then this includes only the pending jobs that have not
	 *         started. If true then this returns the pending jobs + the current
	 *         job.
	 */
	public int getJobCount(boolean includeActiveJobs) {
		int sum = 0;
		synchronized (queue) {
			Iterator<Integer> i = queue.keySet().iterator();
			while (i.hasNext()) {
				List<Job> list = queue.get(i.next());
				sum += list.size();
			}
			if (includeActiveJobs) {
				synchronized (active) {
					sum += active.size();
				}
			}
		}
		return sum;
	}

	/**
	 * Removes a pending job, triggering a <code>jobSkipped</code> notification.
	 * This has no effect if the job is already in progress.
	 * 
	 * @return true if the job was removed/skipped, false if the job was not
	 *         found in the pending queue. (It might already be executing, or
	 *         have otherwise finished.)
	 */
	public boolean removeJob(Job job) {
		boolean returnValue = false;
		try {
			synchronized (queue) {
				Iterator<Integer> i = queue.keySet().iterator();
				while (i.hasNext() && (!returnValue)) {
					List<Job> list = queue.get(i.next());
					if (list.remove(job)) {
						returnValue = true;
					}
				}
			}
			return returnValue;
		} finally {
			// invoke this outside of the sync lock
			if (returnValue) {
				fireListeners(SKIP, job);
			}
		}
	}

	private synchronized void fireErrorListener(Job job, Throwable t) {
		if (listeners == null)
			return;

		for (Listener l : listeners) {
			try {
				l.jobError(JobManager.this, job, t);
			} catch (Throwable t2) {
				t2.printStackTrace();
			}
		}
	}

	private synchronized void fireListeners(int type, Job... job) {
		if (listeners == null)
			return;

		for (Listener l : listeners) {
			try {
				if (type == ADD) {
					l.jobAdded(this, job);
				} else if (type == REMOVE) {
					l.jobRemoved(this, job);
				} else if (type == SKIP) {
					l.jobSkipped(this, job);
				} else if (type == QUEUE) {
					l.jobQueued(this, job);
				} else {
					throw new RuntimeException("Unrecognized type: " + type);
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	/**
	 * Remove all pending jobs.
	 * 
	 * @param cancelActiveJobs
	 *            if true then this additionally cancels every active job.
	 */
	public boolean removeAllJobs(boolean cancelActiveJobs) {
		synchronized (queue) {
			Iterator<Integer> i = queue.keySet().iterator();
			while (i.hasNext()) {
				List<Job> list = queue.get(i.next());
				for (Job job : list) {
					removeJob(job);
				}
			}

			if (cancelActiveJobs) {
				synchronized (active) {
					for (Job j : active) {
						j.cancel();
					}
				}
			}
		}
		return false;
	}
}