/*
 * @(#)Cancellable.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.swing;

import java.awt.event.ActionListener;

/** An interface for operations that can be cancelled.
 * <P>This is useful when two different threads are involved in
 * an operation: Thread A wants to monitor an operation and Thread B
 * is actually performing it.  If the user indicates they want to
 * cancel the operation then Thread A calls <code>cancel()</code>.
 * Thread B should be checking the value of <code>isCanceled()</code>
 * regularly, and abort if it returns <code>true</code> (maybe by
 * throwing a <code>UserCanceledException</code>).
 * <P>That is the primary use of this class.  In some cases the
 * opposite is also useful: when Thread B completes an operation it
 * should call <code>finish()</code>, and Thread A can relay this
 * status by regularly checking <code>isFinished()</code>.
 */
public interface Cancellable {
  public void cancel();
  public boolean isCancelled();
  public boolean isFinished();
  
  /** You can also add a listener to be notified when <code>cancel()</code>
   * is called.  However if Thread A is opening a complex file, and Thread B
   * is the event dispatch thread where <code>cancel()</code> is triggered:
   * then this listener will either be notified on Thread B or perhaps
   * Thread C: Thread A is still unaware the operation is canceled unless
   * other measures are taken.
   */
  public void addCancelListener(ActionListener l);

  /** You can also add a listener to be notified when this job finishes.
   */
  public void addFinishListener(ActionListener l);
  public void removeCancelListener(ActionListener l);
  public void removeFinishListener(ActionListener l);
}
