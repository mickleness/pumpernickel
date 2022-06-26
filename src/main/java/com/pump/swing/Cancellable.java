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

import java.awt.event.ActionListener;

/**
 * An interface for operations that can be cancelled.
 * <P>
 * This is useful when two different threads are involved in an operation:
 * Thread A wants to monitor an operation and Thread B is actually performing
 * it. If the user indicates they want to cancel the operation then Thread A
 * calls <code>cancel()</code>. Thread B should be checking the value of
 * <code>isCanceled()</code> regularly, and abort if it returns
 * <code>true</code> (maybe by throwing a <code>UserCanceledException</code>).
 * <P>
 * That is the primary use of this class. In some cases the opposite is also
 * useful: when Thread B completes an operation it should call
 * <code>finish()</code>, and Thread A can relay this status by regularly
 * checking <code>isFinished()</code>.
 */
public interface Cancellable {
	public void cancel();

	public boolean isCancelled();

	public boolean isFinished();

	/**
	 * You can also add a listener to be notified when <code>cancel()</code> is
	 * called. However if Thread A is opening a complex file, and Thread B is
	 * the event dispatch thread where <code>cancel()</code> is triggered: then
	 * this listener will either be notified on Thread B or perhaps Thread C:
	 * Thread A is still unaware the operation is canceled unless other measures
	 * are taken.
	 */
	public void addCancelListener(ActionListener l);

	/**
	 * You can also add a listener to be notified when this job finishes.
	 */
	public void addFinishListener(ActionListener l);

	public void removeCancelListener(ActionListener l);

	public void removeFinishListener(ActionListener l);
}