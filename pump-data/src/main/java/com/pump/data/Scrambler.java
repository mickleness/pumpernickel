package com.pump.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.pump.io.ByteEncoder;
import com.pump.io.ChainedByteEncoder;
import com.pump.math.KeyedRandom;

/** 
 * This is a cipher that outputs data where the encoding and decoding algorithm
 * are identical. (That is: this object has no designation of "encoded" or "unencoded" data.
 * As long as the same Scrambler object is used: the same process converts A to B and then
 * B to A.)
 * <p>
 * Standard disclaimer: this may have its use cases, but it is no substitute for
 * industry-standard security if that's what you're looking for.
 * <p>
 * A Scrambler can be configured in one of two ways:
 * <ol><li>Without a character set. This object thinks and operates in bytes. This is useful
 * when you're encoding blocks of raw binary data.</li>
 * <li>With a character set. This object requires all incoming data to be part of this
 * character set, and it will only ever output data in this character set. This is useful
 * if you want to obfuscate well-formed Strings. For example you can scramble a series
 * of registration codes that resemble "PRODUCT001". (The character set you'll pass in will
 * be the complete alphabet and 10 digits.)</li></ol>
 * <p>
 * This object obfuscates your data in two ways:
 * <ol><li>A series of reordering <em>layers</em>. Each layer uses one or more maker characters.
 * When we reach two markers (or identify x-many chars past an orphaned marker, or we pass y-many
 * chars without any marker), we identify a <em>run</em>. This object alternates over a few
 * {@link ReorderType ReorderTypes} that rearrange this data in a completely reversible way.
 * This approach simply reorders your incoming data.</li>
 * <li>Additionally we use a {@link SubstitutionModel} to replace bytes. So if you execute
 * a Scrambler against "PRODUCT001" (using a whole alphabet), then the result may include
 * any A-Z letter. (When a character set is used, we use a {@link CharacterSubstitutionModel}.
 * Otherwise we use a {@link ByteSubstitutionModel}.) The substitution models are also fully
 * reversible.
 * </li></ol>
 * <p>
 * A single layer by itself doesn't sufficiently scramble your data, but this places
 * builds n-many layers together. Each layer is fully reversible, so if you pass
 * your input through A and then pass that output through A again, you'll get your
 * original input. Similarly if you pass your input through A, then B, then B, then A:
 * you'll end up with your original input. If you pass your input through A-B-C-B-A:
 * then you have something unique. But that unique thing can be decoded by similarly passing
 * through A-B-C-B-A again.
 * <p>
 * The number of layers this uses varies. For raw bytes, this uses about 300 layers
 * (one for each byte, plus a few extra for good measure). When you've supplied a character
 * set, this uses one layer for every unique letter in that character set.
 * <p>
 * Each Scrambler is also keyed using a password. This is used to generate pseudorandom
 * numbers used to shuffle the layers and make other decisions to mix up the results.
 */
public class Scrambler {
	
	/**
	 * This class determines whether a byte is a marker or not.
	 */
	public static abstract class MarkerRule {
		
		/**
		 * This MarkerRule uses a fixed byte as a marker. For example, if we
		 * designate 0x33 as a marker, then every call to <code>isMarker( 0x33 )</code>
		 * will return true.
		 */
		public static class Fixed extends MarkerRule {
			int marker;
			
			public Fixed(int marker) {
				this.marker = marker;
			}
			
			@Override
			public boolean isMarker(int i) {
				return i==marker;
			}
			
			@Override
			public String toString() {
				return "["+marker+"]";
			}
		}

		/**
		 * This rule converts a byte to binary and counts the number of ones.
		 * If a byte has the expect number of ones, then it is considered a marker.
		 * <p>
		 * So if this OneCount object expects two 1's, then the
		 * bytes like <code>00000011</code> and <code>10000001</code> will be
		 * identified as markers.
		 * 
		 */
		public static class OneCount extends MarkerRule {
			boolean[] bytes = new boolean[256];
			int oneCount;
			
			public OneCount(int oneCount) {
				this.oneCount = oneCount;
				if(oneCount<0 || oneCount>8)
					throw new IllegalArgumentException("The argument ("+oneCount+") should be between 0 and 8.");
				for(int a = 0; a<bytes.length; a++) {
					bytes[a] = getOneCount(a)==oneCount;
				}
			}
			
			public static int getOneCount(int i) {
				if(i<0 || i>255)
					throw new IllegalArgumentException("The argument ("+i+") must be between [0, 255].");
				
				int sum = 0;
				for(int a = 0; a<8; a++) {
					int j = i >> a;
					if(j%2==1)
						sum++;
				}
				return sum;
			}

			@Override
			public boolean isMarker(int i) {
				return bytes[i];
			}
			
			@Override
			public String toString() {
				return "{"+oneCount+"}";
			}
		}
		
		public abstract boolean isMarker(int i);
	}
	
	/**
	 * This is one layer of encoders in a {@link ChainedByteEncoder}
	 * responsible for partially obfuscating byte data.
	 */
	protected static class Layer extends ByteEncoder {
		
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
		final SubstitutionModel substitutionModel;
		MarkerRule markerRule;
		int reorderCycle = 0;

		/** Create a Layer.
		 * 
		 * @param random the Random to use as data is being encoded. This may be null.
		 * @param oneCount the number of 1's in a byte that are required to flag it as a marker.
		 * @param substitutionModel the optional SubstitutionModel this object may
		 * apply to replace bytes.
		 */
		public Layer(MarkerRule markerRule,SubstitutionModel substitutionModel) {
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
	
	public interface SubstitutionModel {
		
		/** This alters bytes (ranging from [0,255]) so the data is transformed.
		 * This is invoked on bytes inside a run if this Scrambler was constructed
		 * with "allowSubstitutions" set to true.
		 * <p>
		 * The tricky thing about rearranging bytes is the Scrambler class
		 * relies on the number of 1's/0's in a byte, so that number may not
		 * change. For example: the value "00110000" may be transformed
		 * into "00001100", but it can't be transformed into "11001111".
		 * 
		 * @param array the array containing bytes to alter
		 * @param arrayOffset the first element in the array to alter
		 * @param length the number of elements in the array to alter
		 */
		public void applySubstitutions(MarkerRule markerRule,int[] array, int arrayOffset, int length);
		
		public SubstitutionModel clone();
	}
	
	/**
	 * This model assumes you have a fixed set of characters you plan on working with.
	 * <p>
	 * This is a great fit if you want to obfuscate a registration code (where you are
	 * sure you understand the original character set), but it is not appropriate if
	 * arbitrary data might be input.
	 */
	protected static class CharacterSubstitutionModel implements SubstitutionModel {
		Map<Integer, List<Integer>> charMap = new HashMap<>();
		

		public CharacterSubstitutionModel(Random random, String chars) {
			this(random, chars.toCharArray());
		}

		private CharacterSubstitutionModel(CharacterSubstitutionModel other) {
			this.charMap = other.charMap;
		}

		@Override
		public CharacterSubstitutionModel clone() {
			return new CharacterSubstitutionModel(this);
		}
		
		public CharacterSubstitutionModel(Random random, char... chars) {
			charMap = new HashMap<>();
			for(int a = 0; a<chars.length; a++) {
				int i = (int)chars[a];
				if(i<0 || i>=256)
					throw new RuntimeException("character index "+a+" ("+chars[a]+") cannot be represented as an int within [0,255].");
				int ones = countOnes(i);
				
				List<Integer> k = charMap.get(ones);
				if(k==null) {
					k = new ArrayList<>();
					charMap.put(ones, k);
				}
				if(!k.contains(i)) {
					k.add(i);
				}
			}
			
			for(List<Integer> value : charMap.values()) {
				Collections.shuffle(value, random);
			}
		}
		
		public static int countOnes(int k) {
			String s = Integer.toBinaryString(k);
			int sum = 0;
			for(int a = 0; a<s.length(); a++) {
				if(s.charAt(a)=='1')
					sum++;
			}
			return sum;
		}
		
		protected int[] getSections(int sectionCount,int total) {
			int[] returnValue = new int[sectionCount];
			int charCount = total;
			for(int a = 0; a<returnValue.length; a++) {
				returnValue[a] = charCount / (sectionCount - a);
				charCount -= returnValue[a];
			}
			return returnValue;
		}

		@Override
		public void applySubstitutions(MarkerRule markerRule,int[] array, int arrayOffset, int length) {
			for(int a = 0; a<length; a++) {
				int oldValue = array[arrayOffset + a];
				int ones = countOnes(oldValue);
				
				List<Integer> candidates = charMap.get(ones);
				Integer position = candidates.indexOf(oldValue);
				if(position==-1)
					throw new IllegalArgumentException("The byte "+oldValue+" ("+((char)oldValue)+") was not included in the original characters used to created this CharacterSubstitutionModel.");
				
				
				int newPos = candidates.size() - 1 - position;
				int newValue = candidates.get(newPos);
				if(!markerRule.isMarker(newValue))
					array[arrayOffset + a] = newValue;
			}
		}
	}
	
	/**
	 * This SubstitutionModel treats all data as bytes.
	 * <p>
	 * This may make data "ugly", because a letter that has a simple
	 * ASCII representation like 'A' might get mutated to strange punctuation
	 * or a backspace (ASCII 8).
	 */
	protected static class ByteSubstitutionModel implements SubstitutionModel {

		private static int[] reverseByteLUT;
		
		private int runCtr = 0;
		
		public ByteSubstitutionModel(Random random) {
			runCtr = random.nextInt(3);
			
			if(reverseByteLUT==null) {
				reverseByteLUT = new int[256];
				//this is a little kludgy, but it's a one-time expense:
				for(int a = 0; a<reverseByteLUT.length; a++) {
					String s = Integer.toBinaryString(a);
					while(s.length()<8) {
						s = "0"+s;
					}
					StringBuffer reverse = new StringBuffer();
					for(int b = s.length()-1; b>=0; b--) {
						reverse.append(s.charAt(b));
					}
					reverseByteLUT[a] = Integer.parseInt( reverse.toString(), 2 );
				}
			}
		}
		
		private ByteSubstitutionModel(ByteSubstitutionModel other) {
			this.runCtr = other.runCtr;
		}
		
		@Override
		public ByteSubstitutionModel clone() {
			return new ByteSubstitutionModel(this);
		}
		
		@Override
		public void applySubstitutions(MarkerRule markerRule, int[] array, int arrayOffset, int length) {
			
			runCtr = (runCtr+1)%3;
			switch(runCtr) {
				case 0:
					//reverse the middle two bits: 00011000
					for(int a = 0; a<length; a++) {
						int d = array[a+arrayOffset];
						int newValue = (d & 0xE7) + (reverseByteLUT[d] & 0x18);
						if(!markerRule.isMarker(newValue))
							array[a+arrayOffset] = newValue;
					}
					break;
				case 1:
					//reverse the middle four bits: 00111100
					for(int a = 0; a<length; a++) {
						int d = array[a+arrayOffset];
						int newValue = (d & 0xC3) + (reverseByteLUT[d] & 0x3C);
						if(!markerRule.isMarker(newValue))
							array[a+arrayOffset] = newValue;
					}
					break;
				default:
					//reverse the middle six bits: 01111110
					for(int a = 0; a<length; a++) {
						int d = array[a+arrayOffset];
						int newValue = (d & 0x81) + (reverseByteLUT[d] & 0x7E);
						if(!markerRule.isMarker(newValue))
							array[a+arrayOffset] = newValue;
					}
					break;
			}
		}
	}
	
	protected enum RunType { NO_MARKER, STARTING_MARKER, BOTH_MARKERS };
	
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

	protected List<MarkerRule> layers = new ArrayList<>();
	protected SubstitutionModel substitutionModel;

	public Scrambler(CharSequence key) {
		this(key, null);
	}

	/** Create a complex encoder based on a passkey.
	 * <p>This actually creates dozens of Scrambler instances and
	 * chains them together.
	 * 
	 * @param key an optional key to guide the random number generation.
	 * @param substitutionModel an optional (but recommended) substitution model to further
	 * scramble data.
	 * @return the complex encoder based on the key.
	 */
	public Scrambler(CharSequence key,CharSequence characterSet) {
		List<MarkerRule> k = new ArrayList<>(256 + 32);
		
		if(characterSet==null) {
			for(int a = 0; a<256; a++) {
				k.add(new MarkerRule.Fixed(a));
			}
			for(int a = 0; a<32; a++) {
				k.add(new MarkerRule.OneCount(a%8));
			}
		} else {
			Set<Integer> covered = new HashSet<>();
			for(int a = 0; a<characterSet.length(); a++) {
				int ch = characterSet.charAt(a);
				if(covered.add(ch))
					k.add(new MarkerRule.Fixed(ch));
			}
			for(int a = 0; a<8; a++) {
				k.add(new MarkerRule.OneCount(a%8));
			}
		}
		Random random = key==null ? new Random(0) : new KeyedRandom(key);
		Collections.shuffle(k, random);

		char[] charArray = characterSet == null ? null : characterSet.toString().toCharArray();
		substitutionModel = characterSet==null ? 
				new ByteSubstitutionModel(random) : 
				new CharacterSubstitutionModel(random, charArray);
				
		for(int a = 0; a<k.size(); a++) {
			layers.add(k.get(a));
		}
		for(int a = k.size()-2; a>=0; a--) {
			layers.add(k.get(a));
		}
	}
	
	public InputStream createInputStream(InputStream in) throws IOException {
		return createEncoder().createInputStream(in);
	}
	
	public OutputStream createOutputStream(OutputStream in) {
		return createEncoder().createOutputStream(in);
	}
	
	protected ChainedByteEncoder createEncoder() {
		Layer[] copy = new Layer[layers.size()];
		for(int a = 0; a<layers.size(); a++) {
			copy[a] = new Layer(layers.get(a), substitutionModel.clone());
		}
		ChainedByteEncoder chainedEncoders = new ChainedByteEncoder(copy);
		return chainedEncoders;
		
	}

	public String encode(String string) {
		return createEncoder().encode(string);
	}

	public String encode(String string,Charset charset) {
		return createEncoder().encode(string, charset);
	}
}
