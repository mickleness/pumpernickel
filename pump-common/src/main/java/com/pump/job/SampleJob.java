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

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/** A job that calculates a series of samples. This series is identified
 * by <code>longs</code>: so it can be so large that you should not store
 * the results in memory unless you know an instance of this class
 * is dealing with a limited amount of data.
 * 
 * <p>A <code>WeakReference</code> is kept of this job
 * so it can be retrieved by its ID (until garbage
 * collection consumes it).
 * 
 * @param <T>
 */
public abstract class SampleJob<T> {
	
	public static class OverflowException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public OverflowException() {}
		
		public OverflowException(String s) {
			super(s);
		}
		
	}
	
	public static class NoncontinuousException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public NoncontinuousException() {}
		
		public NoncontinuousException(String s) {
			super(s);
		}
		
	}
	
	/** A range of samples.
	 * <p>This class is a simple encapsulation of an initial sample index
	 * and the number of samples. The initial index is a <code>long</code>,
	 * but the amount is restricted to an <code>int</code> (so we can safely
	 * create an array of sample objects).
	 */
	public final static class Range implements Serializable, Comparable<Range> {
		private static final long serialVersionUID = 1L;

		public final long startingIndex;
		public final int amount;
		private final int hashCode;
		public Range(long index,int amount) {
			this.startingIndex = index;
			this.amount = amount;
			hashCode = (int)(index^(index>>>32)) + amount;
		}
		
		public Range add(Range r) throws OverflowException, NoncontinuousException {
			long indexA1 = startingIndex;
			long indexA2 = startingIndex+amount-1;

			long indexB1 = r.startingIndex;
			long indexB2 = r.startingIndex+r.amount-1;
			
			if( (indexA1<=indexB1 && indexB1<=indexA2) ||
				(indexA1<=indexB2 && indexB2<=indexA2) ||
				(indexA2==indexB1-1) ||
				(indexB2==indexA1-1) ) {
				long min = Math.min(indexA1, indexB1);
				long max = Math.max(indexA2, indexB2);
				long length = max - min + 1;
				if(length>Integer.MAX_VALUE)
					throw new OverflowException();
				return new Range(min, (int)length);
			}
			
			throw new NoncontinuousException();
		}
		
		public Range subtract(Range r) throws NoncontinuousException {
			long indexA1 = startingIndex;
			long indexA2 = startingIndex+amount-1;

			long indexB1 = r.startingIndex;
			long indexB2 = r.startingIndex+r.amount-1;
			
			if(indexA1<=indexB1 && indexB1<=indexA2) {
				if(indexA1<=indexB2 && indexB2<=indexA2) {
					throw new NoncontinuousException("This range ("+this+") contains the argument ("+r+"), so subtract is not possible.");
				}
				return new Range(indexA1, (int)(indexB1-indexA1+1));
			} else if(indexA1<=indexB2 && indexB2<=indexA2) {
				return new Range(indexB2+1, (int)(indexA2-(indexB2+1)+1 ));
			}
			return this;
		}
		
		public boolean intersects(Range r) {
			long indexA1 = startingIndex;
			long indexA2 = startingIndex+amount-1;

			long indexB1 = r.startingIndex;
			long indexB2 = r.startingIndex+r.amount-1;
			
			if( indexA1<=indexB1 && indexB1<=indexA2)
				return true;

			if( indexA1<=indexB2 && indexB2<=indexA2)
				return true;
			
			return false;
		}
		
		public boolean isAdjacent(Range r) {
			long indexA1 = startingIndex;
			long indexA2 = startingIndex+amount-1;

			long indexB1 = r.startingIndex;
			long indexB2 = r.startingIndex+r.amount-1;
			
			if( indexA2==indexB1-1)
				return true;

			if( indexB2==indexA1-1)
				return true;
			
			return false;
		}
		
		public int compareTo(Range o) {
			if(startingIndex<o.startingIndex)
				return -1;
			if(startingIndex>o.startingIndex)
				return 1;
			
			if(amount<o.amount)
				return -1;
			if(amount>o.amount)
				return 1;
			
			return 0;
		}
		@Override
		public int hashCode() {
			return hashCode;
		}
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Range))
				return false;
			return compareTo( (Range)obj )==0;
		}
		@Override
		protected Object clone() {
			return new Range(startingIndex, amount);
		}
		@Override
		public String toString() {
			return "Range[ index="+startingIndex+" length="+amount+"]";
		}
		
	}
	
	
	static Map<String, WeakReference<SampleJob<?>>> jobMap = new HashMap<String, WeakReference<SampleJob<?>>>();
	
	/** Return a SampleJob with a given ID.
	 * 
	 * @param id the ID the search for.
	 * @return the job, if it exists.
	 */
	public SampleJob<?> get(String id) {
		synchronized(jobMap) {
			WeakReference<SampleJob<?>> ref = jobMap.get(id);
			if(ref!=null) return ref.get();
			return null;
		}
	}
	
	final long creation = System.currentTimeMillis();
	final long sampleCount;
	final String id;
	final Map<String, String> properties = new HashMap<String, String>();
	
	/**
	 * 
	 * @param id a unique identifier for this job. This must
	 * be unique for the entire session.
	 * @param sampleCount the number of samples this job can calculate.
	 */
	public SampleJob(String id,long sampleCount) {
		if(sampleCount<0) throw new IllegalArgumentException("sampleCount ("+sampleCount+") must not be negative.");
		if(id==null)
			throw new NullPointerException();
		
		synchronized(jobMap) {
			if(jobMap.get(id)!=null)
				throw new IllegalArgumentException("the id \""+id+"\" was already claimed");
			jobMap.put(id, new WeakReference<SampleJob<?>>(this));
		}
		this.sampleCount = sampleCount;
		this.id = id;
	}
	
	/** The minimum sample index this job can calculate. */
	public final long getSampleCount() {
		return sampleCount;
	}

	/** Calculate a series of samples.
	 * 
	 * <p>This method may be asked to recreate redundant
	 * data. (The {@link com.bric.job.SampleJobExecutor} makes
	 * sure never to do this, but there is technically nothing
	 * wrong with it.)
	 * 
	 * <p>This method simply performs a few boundary checks
	 * and invokes {@link #doCalculate(long, long)}
	 * 
	 * @param range the range of samples to calculate.
	 * @return the samples corresponding to the indices provided.
	 */
	public final T[] calculate(Range range) {
		return calculate(range.startingIndex, range.amount);
	}
	
	/** Calculate a series of samples.
	 * 
	 * <p>This method may be asked to recreate redundant
	 * data. (The {@link com.bric.job.SampleJobExecutor} makes
	 * sure never to do this, but there is technically nothing
	 * wrong with it.)
	 * 
	 * <p>This method simply performs a few boundary checks
	 * and invokes {@link #doCalculate(long, long)}
	 * 
	 * @param sampleIndex the initial sample.
	 * @param length the number of samples to calculate.
	 * @return the samples corresponding to the indices provided.
	 */
	public final T[] calculate(long sampleIndex,int length) {
		if(sampleIndex<0) throw new IllegalArgumentException("sampleIndex ("+sampleIndex+") must not be negative");
		if(sampleIndex+length>sampleCount)
			throw new IllegalArgumentException("sampleIndex + length ("+sampleIndex+" + "+length+") must not exceed sampleCount ("+sampleCount+")");
		return doCalculate(sampleIndex, length);
	}

	/** Calculate a series of samples.
	 * 
	 * <p>Subclasses must override this method to generate data.
	 * Boundary checking is provided by the {@link #calculate(long, int)} method.
	 * 
	 * <p>This method may be asked to recreate redundant
	 * data. (The {@link com.bric.job.SampleJobExecutor} makes
	 * sure never to do this, but there is technically nothing
	 * wrong with it.)
	 * 
	 * @param sampleIndex the initial sample.
	 * @param length the number of samples to calculate.
	 * @return the samples corresponding to the indices provided.
	 */
	protected abstract T[] doCalculate(long sampleIndex,long length);
	
	public long getCreationDate() {
		return creation;
	}
	
	public final String getValue(String key) {
		return properties.get(key);
	}
	
	public String getID() {
		return id;
	}
	
	public final boolean setValue(String key,String value) {
		if(value==null) {
			String oldValue = properties.remove(key);
			return oldValue!=null;
		}
		String oldValue = properties.put(key, value);
		return !value.equals(oldValue);
	}
}