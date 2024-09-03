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
package com.pump.job;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CancellationException;

import javax.swing.event.ChangeListener;

import com.pump.swing.Cancellable;
import com.pump.util.ObservableProperties;
import com.pump.util.ObservableProperties.Key;

/**
 * This is an abstract task usually intended to be delegated to a
 * {@link JobManager}.
 *
 */
public abstract class Job implements Runnable, Cancellable {
	public static final int PRIORITY_LOW = 0;
	public static final int PRIORITY_MEDIUM = 50;
	public static final int PRIORITY_HIGH = 100;

	public static final Key<Job[]> DEPENDENCIES = new Key<Job[]>(
			"dependencies", Job[].class);
	public static final Key<Boolean> CANCELLED = new Key<Boolean>("cancelled",
			Boolean.class);
	public static final Key<Boolean> FINISHED = new Key<Boolean>("finished",
			Boolean.class);
	public static final Key<String> DESCRIPTION = new Key<String>(
			"description", String.class);
	public static final Key<String> NAME = new Key<String>("name", String.class);
	public static final Key<String> NOTE = new Key<String>("note", String.class);
	public static final Key<Integer> PRIORITY = new Key<Integer>("priority",
			Integer.class);

	ObservableProperties properties = new ObservableProperties();

	/** Creates a medium-priority job. */
	public Job() {
		properties.set(CANCELLED, false);
		properties.set(FINISHED, false);
		properties.set(DESCRIPTION, "");
		properties.set(NOTE, "");
		properties.set(PRIORITY, PRIORITY_MEDIUM);
		setName("Untitled-" + System.identityHashCode(this));
	}

	/**
	 * Creates a Job with a specific priority.
	 */
	public Job(int priority) {
		this();
		setPriority(priority);
	}

	@Override
	public String toString() {
		return properties.get(NAME);
	}

	/**
	 * Returns a very short user-friendly description of this job. The
	 * description generally should not change, but the note should. This may be
	 * an empty string if no description is available.
	 * 
	 * @see #setDescription(String)
	 * @see #getNote()
	 */
	public String getDescription() {
		return properties.get(DESCRIPTION);
	}

	/**
	 * Assign a very short user-friendly description of this job. The
	 * description generally should not change, but the note should. This may be
	 * an empty string if no description is available, but it may not be null.
	 * 
	 * @see #getDescription()
	 * @see #setNote(String)
	 */
	public void setDescription(String s) {
		if (s == null)
			throw new NullPointerException();
		properties.set(DESCRIPTION, s);
	}

	/**
	 * Assign the name of this Job. This should be a short identifier (even
	 * shorting than the description).
	 */
	public void setName(String s) {
		if (s == null)
			throw new NullPointerException();
		properties.set(NAME, s);
	}

	/** Return the name of this Job. */
	public String getName() {
		return properties.get(NAME);
	}

	/**
	 * Returns a very short user-friendly summary of the current state of this
	 * job. This may be an empty string if no note is available.
	 * 
	 * @see #setNote(String)
	 * @see #getDescription()
	 */
	public String getNote() {
		return properties.get(NOTE);
	}

	/**
	 * Assign a very short user-friendly summary of the current state of this
	 * job. This may be an empty string if no note is available, but it may not
	 * be null.
	 * 
	 * @see #getNote()
	 * @see #setDescription(String)
	 */
	public void setNote(String s) {
		if (s == null)
			throw new NullPointerException();
		properties.set(NOTE, s);
	}

	public void setPriority(int priority) {
		properties.set(PRIORITY, priority);
	}

	public int getPriority() {
		return properties.get(PRIORITY);
	}

	/**
	 * If this is non-null, then the {@link JobManager} should immediately
	 * replace all jobs with the same replacement-id.
	 * <p>
	 * For example, if a job is initiated every time the user adjusts a JSlider
	 * and those jobs have the id "recalculate", then instead of queuing of
	 * queuing 500 jobs only one job will be enqueued. The JobManager may (at
	 * its discretion) also try to cancel an active job if its replacement id
	 * matches an incoming job's replacement id.
	 * 
	 * @return an identifying String, or null if this attribute isn't used.
	 */
	public String getReplacementId() {
		return null;
	}

	/**
	 * Adds a listener interested in description changes.
	 * 
	 * @param l
	 *            this listener will be notified if this job's description
	 *            changes.
	 */
	public synchronized void addDescriptionListener(ChangeListener l) {
		properties.addListener(ObservableProperties.DEFAULT, DESCRIPTION, l);
	}

	/**
	 * Adds a listener interested in note changes.
	 * 
	 * @param l
	 *            this listener will be notified if this job's note changes.
	 */
	public synchronized void addNoteListener(ChangeListener l) {
		properties.addListener(ObservableProperties.DEFAULT, NOTE, l);
	}

	/**
	 * Adds a listener interested in cancel events.
	 * 
	 * @param l
	 *            this listener will be notified if this job is cancelled.
	 */
	public synchronized void addCancelListener(ActionListener l) {
		properties.addListener(ObservableProperties.DEFAULT, CANCELLED, l);
	}

	/**
	 * Adds a listener interested in finish events.
	 * 
	 * @param l
	 *            this listener will be notified when this job is finished.
	 */
	public void addFinishListener(ActionListener l) {
		properties.addListener(ObservableProperties.DEFAULT, FINISHED, l);
	}

	public void cancel() {
		properties.set(CANCELLED, true);
	}

	public boolean isCancelled() {
		return properties.get(CANCELLED);
	}

	public boolean isFinished() {
		return properties.get(FINISHED);
	}

	public synchronized void removeDescriptionListener(ChangeListener l) {
		properties.removeListener(ObservableProperties.DEFAULT, DESCRIPTION, l);
	}

	public synchronized void removeCancelListener(ActionListener l) {
		properties.removeListener(ObservableProperties.DEFAULT, CANCELLED, l);
	}

	public void removeFinishListener(ActionListener l) {
		properties.removeListener(ObservableProperties.DEFAULT, FINISHED, l);
	}

	/**
	 * This method is responsible for doing the real work in this Job. This
	 * method should constantly poll <code>isCancelled()</code>: if this job has
	 * been cancelled, this method should return immediately or throw a
	 * UserCancelledException.
	 * <P>
	 * Subclasses are also encouraged to call <code>setDescription()</code> when
	 * during this method to give the user cues about what is happening.
	 * <P>
	 * This method is not responsible for interacting with the
	 * <code>finish</code> property: that flag will be changed (and listeners
	 * notified) when this method returns.
	 * 
	 */
	protected abstract void runJob() throws Exception;

	public final void run() {
		try {
			for (Job dependency : getDependencies()) {
				if (dependency.isCancelled())
					throw new CancellationException(
							"the job \""
									+ getName()
									+ "\" was cancelled because it relied on the job \""
									+ dependency.getName()
									+ "\", which was previously cancelled");
				if (!dependency.isFinished())
					throw new IllegalStateException("the job \"" + getName()
							+ "\" should not be initiated until the job \""
							+ dependency.getName() + "\" is finished.");
			}
			runJob();
		} catch (CancellationException e) {
			System.err.println("Job: Cancelled \"" + getName() + "\"");
			throw e;
		} catch (RuntimeException e) {
			System.err.println("Job: RuntimeException executing \"" + getName()
					+ "\"");
			throw e;
		} catch (Error e) {
			System.err.println("Job: Error executing \"" + getName() + "\"");
			throw e;
		} catch (Exception e) {
			System.err
					.println("Job: Exception executing \"" + getName() + "\"");
			throw new RuntimeException(e);
		} finally {
			properties.set(FINISHED, true);
		}
	}

	/**
	 * Define a list of other Jobs this Job is dependent on. This Job should not
	 * be executed until all of these jobs are finished.
	 */
	public void setDependencies(List<? extends Job> list) {
		setDependencies(list.toArray(new Job[list.size()]));
	}

	/**
	 * Define a list of other Jobs this Job is dependent on. This Job should not
	 * be executed until all of these jobs are finished.
	 */
	public void setDependencies(Job... array) {
		if (array == null)
			array = new Job[] {};
		properties.set(DEPENDENCIES, array);
	}

	/**
	 * Adds new job dependencies to this Job.
	 * 
	 * @param list
	 */
	public void addDependencies(List<? extends Job> list) {
		addDependencies(list.toArray(new Job[list.size()]));
	}

	/**
	 * Adds new job dependencies to this Job.
	 * 
	 * @param array
	 */
	public void addDependencies(Job... array) {
		Job[] d = getDependencies();
		Job[] newArray = new Job[d.length + array.length];
		System.arraycopy(d, 0, newArray, 0, d.length);
		System.arraycopy(array, 0, newArray, d.length, array.length);
		setDependencies(newArray);
	}

	/**
	 * Return a list of jobs that must be completed before this job executes.
	 */
	public Job[] getDependencies() {
		Job[] r = properties.get(DEPENDENCIES);
		if (r == null)
			return new Job[] {};
		return r;
	}
}