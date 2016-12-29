/*
 * @(#)SampleJobExecutor.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
 *
 * Copyright (c) 2013 by Jeremy Wood.
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
package com.pump.job;

import java.util.LinkedList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/** This executes a {@link com.bric.job.SampleJob} across
 * a given number of threads until all samples are processed,
 * an error occurs, or <code>abort()</code> is invoked.
 * 
 * You need to extend this class and override
 * <code>processResults()</code> and <code>processThrowable()</code>
 * to make sense of the job results.
 */
public abstract class SampleJobExecutor<T> {
	
	public enum State {
		ACTIVE, ABORTED, ERROR, FINISHED
	}
	
	State state = State.ACTIVE;
	final long sampleCount;
	final SampleJob<T> job;
	final int sampleIncrement;
	long startingIndex;
	int activeThreads;
	List<ChangeListener> stateListeners = new LinkedList<ChangeListener>();
	final Thread[] threads;
	
	Runnable threadRunnable = new Runnable() {
		public void run() {
			try {
				while(State.ACTIVE.equals(state)) {
					long startingIndex;
					int sampleCount;
					synchronized(SampleJobExecutor.this) {
						startingIndex = SampleJobExecutor.this.startingIndex;
						long remainingSamples = SampleJobExecutor.this.sampleCount - startingIndex;
						
						sampleCount = sampleIncrement;
						if(remainingSamples<sampleCount)
							sampleCount = (int)remainingSamples;
						
						if(sampleCount==0)
							return;
						SampleJobExecutor.this.startingIndex += sampleCount;
					}
					try {
						T[] results = job.calculate(startingIndex, sampleCount);
						processResults(startingIndex, sampleCount, results);
					} catch(Throwable t) {
						setState(State.ERROR);
						processThrowable(startingIndex, sampleCount, t);
					}
				}
			} finally {
				activeThreads--;
				if(activeThreads==0) {
					//if we exited normally: set the state to a healthy FINISHED
					synchronized(SampleJobExecutor.this) {
						if(State.ACTIVE.equals(state)) {
							setState(State.FINISHED);
						}
					}
				}
			}
		}
	};
	
	/** Start executing a job.
	 * 
	 * @param job the job to execute.
	 * @param threadCount the number of threads to use.
	 * @param sampleIncrement the number of samples requested
	 * at a time. This must be an <code>int</code> (instead of a <code>long</code>)
	 * because we'll create an array of this size.
	 */
	public SampleJobExecutor(SampleJob<T> job,int threadCount,int sampleIncrement) {
		this.job = job;
		this.sampleCount = job.getSampleCount();
		this.sampleIncrement = sampleIncrement;
		this.startingIndex = 0;
		this.activeThreads = threadCount;
		
		ThreadGroup threadGroup = new ThreadGroup(job.toString());
		threads = new Thread[threadCount];
		for(int a = 0; a<threadCount; a++) {
			threads[a] = new Thread(threadGroup, threadRunnable, "thread-"+a);
		}
	}
	
	/** Start all the threads this executor manages.
	 * 
	 * @param join if <code>false</code> then this
	 * method returns immediately. If <code>true</code>
	 * then this method blocks until the execution is complete.
	 */
	public void start(boolean join) {
		for(int a = 0; a<threads.length; a++) {
			threads[a].start();
		}
		
		if(join)
			join();
	}
	
	
	/** Return true if this changed the state to ABORTED.
	 * This method may be ignored if the state is already FINISHED or ERROR.
	 */
	public boolean abort() {
		synchronized(SampleJobExecutor.this) {
			if(State.FINISHED.equals(state) || State.ERROR.equals(state))
				return false;
			return setState(State.ABORTED);
		}
	}
	
	/** Set the state.
	 * 
	 * @param state the new state.
	 * @return true if a change occurred.
	 */
	protected boolean setState(State state) {
		if(state==null) throw new NullPointerException();
		
		synchronized(SampleJobExecutor.this) {
			if(this.state.equals(state)) return false;
			this.state = state;
		}
		
		fireStateListeners();
		
		return true;
	}
	
	protected void fireStateListeners() {
		for(ChangeListener l : stateListeners) {
			try {
				l.stateChanged(new ChangeEvent(this));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Add a listener to be notified when the state changes.
	 * 
	 */
	public void addStateListener(ChangeListener l) {
		if(!stateListeners.contains(l))
			stateListeners.add(l);
	}
	
	/** Remove a listener.
	 * 
	 */
	public void removeStateListener(ChangeListener l) {
		stateListeners.remove(l);
	}
	
	/** Blocks until the job is fully executed, aborted,
	 * or an error occurs.
	 * 
	 * <P> (That is: when this
	 * method returns the <code>getState()</code> method
	 * is guaranteed to not return <code>State.ACTIVE</code>.)
	 */
	public void join() {
		boolean repeat = true;
		while(repeat) {
			repeat = false;
			for(int a = 0; a<threads.length; a++) {
				try {
					threads[a].join();
				} catch(InterruptedException e) {
					repeat = true;
				}
			}
		}
	}
	
	/** Process a series of results.
	 * 
	 * 
	 * @param index the initial index.
	 * The zereoth index of the <code>results</code> array
	 * is this index in the larger data model.
	 * @param length the number of results. The <code>results</code>
	 * array is always this size. (Or if it is larger: then the array
	 * is being recycled and you can ignore results after that.)
	 * @param results the results.
	 */
	protected abstract void processResults(long index,int length,T[] results);
	
	/** Process an error that occurred when we tried to get
	 * a series of results.
	 * 
	 * The default implementation of this class just outputs
	 * a little information (including the stacktrace) to
	 * System.err.
	 * 
	 * @param index the initial requested index.
	 * @param length the number of results requested.
	 * @param throwable the error intercepted.
	 */
	protected void processThrowable(long index,long length,Throwable throwable) {
		System.err.println("An error occurred processing samples ["+index+", "+(index+length)+")");
		throwable.printStackTrace();
	}
}
