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
package com.pump.swing;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.job.JobManager;

/**
 * This helps associate any number of throbbers with activity.
 * <p>
 * Call {@link #createToken()} to activate all possible throbbers, and then
 * after some time call {@link #returnToken(Object)} to return the token.
 * <p>
 * Your usage should resemble:
 * 
 * <pre>
 * Thread myThread = new Thread() {
 * 	public void run() {
 * 		Object token = throbberManager.createToken();
 * 		try {
 * 			// do complicated task
 * 		} finally {
 * 			throbberManager.returnToken(token);
 * 		}
 * 	}
 * };
 * </pre>
 * <p>
 * Any JThrobbers this object creates will automatically toggle their visibility
 * based on whether any tokens have been checked out.
 * 
 */
public class ThrobberManager {
	public static class Token {
		WeakReference<ThrobberManager> manager;
		boolean active = true;

		Token(ThrobberManager m) {
			manager = new WeakReference<>(m);
		}

		public ThrobberManager getManager() {
			return manager.get();
		}
	}

	private static class UpdateVisibilityListener implements ChangeListener {
		WeakReference<ThrobberManager> managerRef;
		WeakReference<JThrobber> throbberRef;

		public UpdateVisibilityListener(ThrobberManager throbberManager,
				JThrobber throbber) {
			managerRef = new WeakReference<>(throbberManager);
			throbberRef = new WeakReference<>(throbber);
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			JThrobber t = throbberRef.get();
			ThrobberManager m = managerRef.get();
			if (t != null && m != null) {
				t.setVisible(m.isActive());
			}
		}

	}

	protected int activeCount = 0;
	protected List<ChangeListener> listeners = new ArrayList<>();

	/**
	 * Create a ThrobberManager.
	 */
	public ThrobberManager() {
	}

	/**
	 * Create a ThrobberManager that joins with the JobManager provided.
	 * <p>
	 * This is a convenience method for creating an empty constructor and then
	 * calling {@link #join(JobManager)}.
	 */
	public ThrobberManager(JobManager jobManager) {
		this();
		join(jobManager);
	}

	/**
	 * This combines this ThrobberManager with the JobManager provided. When the
	 * JobManager is active: this ThrobberManager will also be active. You may
	 * continue to independently use {@link #createToken()} to further add
	 * activity to this manager. (For that matter, you may also join this
	 * manager to multiple JobManagers if you want to.)
	 * 
	 * @param jobManager
	 *            the JobManager to listen to.
	 */
	public void join(final JobManager jobManager) {
		final Token[] activeToken = new Token[1];
		jobManager.addChangeListener(new ChangeListener() {

			@Override
			public synchronized void stateChanged(ChangeEvent e) {
				boolean isActive = jobManager.isActive();
				if (isActive) {
					if (activeToken[0] == null) {
						activeToken[0] = createToken();
					}
				} else {
					if (activeToken[0] != null) {
						returnToken(activeToken[0]);
						activeToken[0] = null;
					}
				}
			}
		});
	}

	/**
	 * Create a new JThrobber that will automatically toggle its visibility
	 * based on whether any entity has borrowed tokens against this manager.
	 */
	public JThrobber createThrobber() {
		JThrobber t = new JThrobber();
		UpdateVisibilityListener l = new UpdateVisibilityListener(this, t);
		addChangeListener(l);
		l.stateChanged(null);

		return t;
	}

	/**
	 * Return true if any threads have borrowed tokens against this manager.
	 */
	public boolean isActive() {
		synchronized (this) {
			return activeCount > 0;
		}
	}

	/**
	 * Create (borrow) a new token from this manager.
	 * 
	 * @return the object that should be eventually passed to
	 *         {@link #returnToken(Object)}.
	 */
	public Token createToken() {
		boolean fireListeners;
		Token returnValue;
		synchronized (this) {
			fireListeners = incrementActiveCount(1);
			returnValue = new Token(this);
		}

		if (fireListeners) {
			fireChangeListeners();
		}

		return returnValue;
	}

	private void fireChangeListeners() {
		ChangeListener[] l;
		synchronized (listeners) {
			l = listeners.toArray(new ChangeListener[listeners.size()]);
		}
		for (ChangeListener k : l) {
			try {
				k.stateChanged(new ChangeEvent(this));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Add a ChangeListener to be notified when the number of borrowed tokens
	 * changes. This is only notified when the first token is borrowed or the
	 * last token is returned. (That is: this only distinguishes between
	 * "active" and "inactive". It doesn't notify listeners if a 2nd token is
	 * borrowed, because then it only changes from "active" to "active".)
	 * 
	 * @param cl
	 */
	public void addChangeListener(ChangeListener cl) {
		synchronized (listeners) {
			listeners.add(cl);
		}
	}

	/**
	 * Return an object previously created via {@link #createToken()}
	 * 
	 * @param token
	 *            an object previously created via {@link #createToken()}.
	 */
	public void returnToken(Token token) {
		if (token.getManager() != this)
			throw new IllegalArgumentException();
		if (!token.active)
			throw new IllegalStateException("This token was already returned.");
		boolean fireListeners;
		synchronized (this) {
			token.active = false;
			fireListeners = incrementActiveCount(-1);
		}

		if (fireListeners) {
			fireChangeListeners();
		}
	}

	private boolean incrementActiveCount(int incr) {
		boolean showing1 = activeCount == 0;
		activeCount += incr;
		boolean showing2 = activeCount == 0;
		return showing1 != showing2;
	}

}