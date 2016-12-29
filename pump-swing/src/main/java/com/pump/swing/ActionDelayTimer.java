/*
 * @(#)ActionDelayTimer.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/** This timer can delay executing a task until a condition stabilizes.
 * <p>For example: suppose you want to display a warning when a condition is
 * consistently met. This condition must be consistently met for 2 seconds,
 * and then <code>setState(true)</code> will be called. If at any point in
 * those 2 seconds the method <code>checkCondition()</code> is polled and
 * returns <code>false</code>: then the timer resets and we wait for another
 * 2 seconds of uninterrupted success. Likewise once <code>setState(true)</code>
 * has been invoked: this timer will wait for 2 seconds of interrupted failed
 * conditions to invoke <code>setState(false)</code>.
 * <p>The intention here is to avoid annoying the user with constant flickering
 * feedback.
 * <p>It is assumed the default state is false (that is, pretend that
 * <code>setState(false)</code> has already been invoked once).
 *
 */
public abstract class ActionDelayTimer extends Timer {

	private static final long serialVersionUID = 1L;
	boolean lastObservedState = false;
	long lastObservedTime = -1;
	long waitingPeriod = 2000;
	
	boolean currentEnforcedState = false;
	
	private ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			long current = System.currentTimeMillis();
			long elapsed = current - lastObservedTime;
			if(checkCondition()) {
				if(currentEnforcedState) {
					//the condition is true, and that's the most recently enforced state:
					lastObservedState = true;
					lastObservedTime = current;
				} else if(lastObservedState) {
					//the condition is true, the last enforced state is false, but the last observed state is true:
					if(elapsed>waitingPeriod) {
						currentEnforcedState = true;
						setState(true);
					}
				} else {
					//the condition is true, the last enforced state is false, and the last observed state is false:
					lastObservedState = true;
					lastObservedTime = current;
				}
			} else {
				if(!currentEnforcedState) {
					//the condition is false, and that's the most recently enforced state:
					lastObservedState = false;
					lastObservedTime = current;
				} else if(!lastObservedState) {
					//the condition is false, the last enforced state is true, but the last observed state is false:
					if(elapsed>waitingPeriod) {
						currentEnforcedState = false;
						setState(false);
					}
				} else {
					//the condition is false, the last enforced state is true, and the last observed state is true:
					lastObservedState = false;
					lastObservedTime = current;
				}
			}
		}
	};

	/**
	 * 
	 * @param waitingPeriod the number of milliseconds that the condition must be constant
	 * before <code>setState(..)</code> is called.
	 */
	public ActionDelayTimer(long waitingPeriod) {
		super((int)(waitingPeriod/10), null);
		this.waitingPeriod = waitingPeriod;
		addActionListener(actionListener);
	}
	
	public abstract boolean checkCondition();
	
	public abstract void setState(boolean b);
}
