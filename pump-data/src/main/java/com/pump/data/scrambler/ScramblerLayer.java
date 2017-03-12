package com.pump.data.scrambler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pump.io.ByteEncoder;

/** 
 * This is a cipher that reencodes data using a key. The encoding algorithm is identical
 * to the decoding algorithm, and it can use a piped stream of data.
 * <p>
 * A single Scrambler instance in isolation offers a small change that the human eye can
 * easily decipher (or at least recognize), but the static methods in this class layer
 * dozens of Scramblers on top of each other so the result is unrecognizable.
 * <p>
 * This encoder operates by looking for <em>marker</em> bytes. We'll eventually encounter
 * one of three conditions:
 * <ul><li>We found two marker bytes (one to open and one to close)
 * so we can look at the run of bytes between them.</li>
 * <li>We've read i-consecutive bytes without encountering any markers, so we'll look at those
 * i-many bytes.</li>
 * <li>We found one marker, and then we read j-many consecutive bytes without finding a
 * matching closing marker, so we'll look at those j-many bytes.</li>/ul>
 * <p>
 * When we evaluate a run of bytes we use a {@link ReorderType} to rearrange those bytes.
 * These methods are guaranteed to be reversible, so if you pass data through a single
 * Scrambler object and then pass it through the same Scrambler object: your data is
 * restored to its original condition.
 * <p>
 * There is also an optional {@link SubstitutionModel} you can apply to this run of bytes.
 * A substitution model is basically a lookup code that can be further used to obfuscate data.
 * Everything described up to this point only rearranges existing data, but this can alter the
 * bytes.
 * <h3>Layering</h3>
 * If you apply one Scrambler ("X") twice, then your output is identical to your input. Similarly if you
 * run Scramblers X, Y, Y, X or X, Y, Z, Z, Y, X: then your data is unchanged. But if you use an
 * odd number of transformations then the data stays encrypted. For example: X, Y, Z, Y, X.
 * <p>
 * When you use one of the static methods in this class you'll layer approximately 100
 * Scramblers together, so the results should be sufficiently unrecognizable.
 * <p>
 * <h3>Usage</h3>
 * <p>
 * When I first started playing with this idea: I had hoped to use it for security. But
 * that was a pipe dream, and in reality: you should never use anything except standard
 * Java security/encryption classes to encrypt data.
 * <p>
 * However over the years this has ended up being useful in a couple of scenarios:
 * <ul><li>One employer insisted we share unique identifiers with customers but at the
 * same time we should NOT reveal the actual UID our database used. This class let
 * us scramble the UID in an easily reversible way; consumers received a unique identifier
 * that we could make sense of in future sessions, but if a malicious consumer tried to
 * execute SQL using that UID they would fail.</li>
 * <li>Registration codes. Suppose you just want to encode "PRODUCT000001", "PRODUCT000002", 
 * "PRODUCT000003", etc. This provides a simple/reversible model to encode those values.</li></ul>
 * 
 * 
 * @see #createEncoder(String, boolean)
 */
public class ScramblerLayer extends ByteEncoder {
	
	private enum RunType { NO_MARKER, STARTING_MARKER, BOTH_MARKERS };
	
	/** This is a way of reordering bytes that when invoked twice results in the
	 * original data.
	 */
	protected enum ReorderType {

		/** "ABCDEF" is encoded as "ABCDEF" */
		NORMAL() {
			@Override
			protected void reorder(List<Integer> srcList, int srcPos,
					int length, int[] dest, int destPos) {
				for(int a = 0; a<length; a++) {
					dest[destPos+a] = srcList.get(srcPos+a);
				}
			}
		},

		/** "ABCDEF" is encoded as "FEDCBA" */
		REVERSE() {
			@Override
			protected void reorder(List<Integer> srcList, int srcPos,
					int length, int[] dest, int destPos) {
				for(int a = 0; a<length; a++) {
					dest[destPos+a] = srcList.get(length-1-a+srcPos);
				}
			}
		},
		
		/** "ABCDEF" is encoded as "BA"+"DC"+"FE" */
		REVERSE_PAIRS() {
			@Override
			protected void reorder(List<Integer> srcList, int srcPos,
					int length, int[] dest, int destPos) {
				for(int a = 0; a<length; a+=2) {
					if(a+1<length) {
						dest[destPos+a] = srcList.get(srcPos+a+1);
						dest[destPos+a+1] = srcList.get(srcPos+a);
					} else {
						dest[destPos+a] = srcList.get(srcPos+a);
					}
				}
			}
		},
		
		/** "ABCDEF" is encoded as "DEF"+"ABC" */
		CUT_DECK() {
			@Override
			protected void reorder(List<Integer> srcList, int srcPos,
					int length, int[] dest, int destPos) {
				int split = length/2;
				dest[destPos+length-1] = srcList.get(srcPos+length-1);
				for(int a = 0; a<split; a++) {
					dest[destPos+a] = srcList.get(srcPos+a+split);
					dest[destPos+a+split] = srcList.get(srcPos+a);
				}
			}
		},

		/** "ABCDEF" is encoded as "CBA"+"FED" */
		REVERSE_CUT_DECK() {
			@Override
			protected void reorder(List<Integer> srcList, int srcPos,
					int length, int[] dest, int destPos) {
				int split = length/2;
				for(int a = 0; a<split; a++) {
					dest[destPos+a] = srcList.get(srcPos+split-1-a);
				}
				for(int a = split; a<length; a++) {
					dest[destPos+a] = srcList.get(srcPos+length-1-a+split);
				}
			}
		};
		
		/** Reorder a series of integers.
		 * 
		 * @param srcList the integers to reorder
		 * @param srcPos the index in the srcList to begin enumeration options
		 * @param length the number of elements to transfer/reorder
		 * @param dest the array to store the data in
		 * @param destPos the first index in the dest array to write to
		 */
		protected abstract void reorder(List<Integer> srcList,int srcPos,int length,int[] dest,int destPos);
	};
	

	private static ReorderType[] reorderTypes = new ReorderType[] { ReorderType.CUT_DECK, ReorderType.REVERSE_CUT_DECK, ReorderType.REVERSE, ReorderType.REVERSE_PAIRS };
	private class Run {
		RunType type = null;
		int[] data = new int[100];
		int length = 0;
		
		private void reset(RunType type) {
			this.type = type;
			length = 0;
		}
		
		private int[] encode() {
			ReorderType reorderType = reorderTypes[ (reorderCycle++)%reorderTypes.length ];
			int arrayOffset;
			int l;
			if(RunType.BOTH_MARKERS.equals(type)) {
				l = length-2;
				arrayOffset = 1;
			} else if (RunType.NO_MARKER.equals(type)) {
				arrayOffset = 0;
				l = length;
			} else {
				l = length-1;
				arrayOffset = 1;
			}
			List<Integer> bytes = new ArrayList<>(length);
			for(int a = 0; a<length; a++) {
				bytes.add(data[a]);
			}
			reorderType.reorder(bytes, arrayOffset, l, data, arrayOffset);
			if(substitutionModel!=null) {
				substitutionModel.applySubstitutions(markerRule, data, arrayOffset, l);
			}
			return data;
		}
	}
	
	int capacity;
	final Run currentRun = new Run();
	final ScramblerSubstitutionModel substitutionModel;
	ScramblerMarkerRule markerRule;
	int reorderCycle = 0;

	/** Create a Scrambler.
	 * 
	 * @param random the Random to use as data is being encoded. This may be null.
	 * @param oneCount the number of 1's in a byte that are required to flag it as a marker.
	 * @param substitutionModel the optional SubstitutionModel this object may
	 * apply to replace bytes.
	 */
	public ScramblerLayer(int capacitySeed,ScramblerMarkerRule markerRule,ScramblerSubstitutionModel substitutionModel) {
		if(markerRule==null)
			throw new NullPointerException();
		this.markerRule = markerRule;
		this.substitutionModel = substitutionModel;
		resetRun();
	}
	
	@Override
	public synchronized void push(int b) throws IOException {
		if(closed) throw new IllegalStateException("This Scrambler has already been closed.");
		
		boolean completesRun = false;
		if(markerRule.isMarker(b)) {
			if(RunType.NO_MARKER.equals(currentRun.type)) {
				pushChunk(currentRun.encode(), currentRun.length);
				resetRun();
			}
			
			if(currentRun.type==null) {
				currentRun.reset( RunType.STARTING_MARKER );
			} else if(RunType.STARTING_MARKER.equals(currentRun.type)) {
				currentRun.type = RunType.BOTH_MARKERS;
				completesRun = true;
			}
		} else {
			if(currentRun.type==null) {
				currentRun.reset( RunType.NO_MARKER );
			}
		}
		currentRun.data[currentRun.length++] = b;
		if(completesRun || currentRun.length==capacity) {
			pushChunk(currentRun.encode(), currentRun.length);
			resetRun();
		}
	}
	
	private synchronized void pushChunk(int[] data, int length) throws IOException {
		if(data.length==length) {
			super.pushChunk(data);
		} else {
			int[] copy = new int[length];
			System.arraycopy(data, 0, copy, 0, length);
			super.pushChunk(copy);
		}
	}

	private void resetRun() {
		currentRun.reset(null);
		capacity = (capacity + CAPACITY_INCR) % CAPACITY_MAX + 2;
	}
	
	protected static int CAPACITY_MAX = 60;
	protected static int CAPACITY_INCR = 11;
	
	protected void flush() throws IOException {
		if(currentRun.type!=null) {
			pushChunk(currentRun.encode(), currentRun.length);
			resetRun();
		}
	}
}
