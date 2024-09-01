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
package com.pump.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class BasicCancellable implements Cancellable {

	private boolean cancelled = false;
	private boolean finished = false;
	List<ActionListener> cancelListeners;
	List<ActionListener> finishListeners;

	@Override
	public void cancel() {
		if (cancelled)
			return;
		cancelled = true;
		fireCancelListeners();
	}

	public void finish() {
		if (finished)
			return;
		finished = true;
		fireFinishListeners();
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isFinished() {
		return finished;
	}

	protected void fireFinishListeners() {
		if (finishListeners == null)
			return;
		for (int a = 0; a < finishListeners.size(); a++) {
			ActionListener l = finishListeners.get(a);
			try {
				l.actionPerformed(new ActionEvent(this, 0, "finish"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void fireCancelListeners() {
		if (cancelListeners == null)
			return;
		for (int a = 0; a < cancelListeners.size(); a++) {
			ActionListener l = cancelListeners.get(a);
			try {
				l.actionPerformed(new ActionEvent(this, 0, "cancel"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addFinishListener(ActionListener l) {
		if (finishListeners == null)
			finishListeners = new ArrayList<ActionListener>();
		if (finishListeners.contains(l))
			return;
		finishListeners.add(l);
	}

	@Override
	public void addCancelListener(ActionListener l) {
		if (cancelListeners == null)
			cancelListeners = new ArrayList<ActionListener>();
		if (cancelListeners.contains(l))
			return;
		cancelListeners.add(l);
	}

	@Override
	public void removeFinishListener(ActionListener l) {
		if (finishListeners == null)
			return;
		finishListeners.remove(l);
	}

	@Override
	public void removeCancelListener(ActionListener l) {
		if (cancelListeners == null)
			return;
		cancelListeners.remove(l);
	}

}