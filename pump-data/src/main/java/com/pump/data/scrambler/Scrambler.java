package com.pump.data.scrambler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import com.pump.io.ByteEncoder.DataListener;
import com.pump.io.ChainedByteEncoder;
import com.pump.math.KeyedRandom;

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
public class Scrambler {
	
	/** The provides a command-line tool to encode and decode files.
	 * 
	 * <code>Scrambler -src filepath -dst filepath -ext styx -pwd password -substitutions</code>
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String args[]) throws IOException {
		List<String> argList = new ArrayList<String>();
		for(String s : args) { 
			if(s.startsWith("-")) s = s.toLowerCase();
			argList.add(s);
		}
		
		String ext = "scrmblr";
		int i = argList.indexOf("-ext");
		if(i!=-1) {
			try {
				ext = argList.remove(i+1);
			} catch(IndexOutOfBoundsException e) {
				System.err.println("error: \"-ext\" was declared but no extension was identified.");
				System.exit(1);
			}
			argList.remove(i);
			
			//if the user prepended this with a period, delete it:
			if(ext.startsWith("."))
				ext = ext.substring(1);
		}
		
		File src = null;
		i = argList.indexOf("-src");
		if(i!=-1) {
			try {
				src = new File(argList.remove(i+1));
			} catch(IndexOutOfBoundsException e) {
				System.err.println("error: \"-src\" was declared but no source file was identified.");
				System.exit(1);
			}
			argList.remove(i);
			if(!src.exists()) {
				System.err.println("error: the source file does not exist ("+src.getAbsolutePath()+").");
				System.exit(1);
			} else if(!src.canRead()) {
				System.err.println("error: the source file can not be read ("+src.getAbsolutePath()+").");
				System.exit(1);
			}
		}
		
		String password = null;
		i = argList.indexOf("-pwd");
		if(i!=-1) {
			try {
				password = argList.remove(i+1);
			} catch(IndexOutOfBoundsException e) {
				System.err.println("error: \"-pwd\" was declared but no password was identified.");
				System.exit(1);
			}
			argList.remove(i);
		}
		
		File dst = null;
		i = argList.indexOf("-dst");
		if(i!=-1) {
			try {
				dst = new File(argList.remove(i+1));
			} catch(IndexOutOfBoundsException e) {
				System.err.println("error: \"-dst\" was declared but no destination file was identified.");
				System.exit(1);
			}
			argList.remove(i);
		}
		
		if(argList.size()>0) {
			int exitCode = argList.size()==1 && 
					(argList.get(0).equals("?") 
					 || argList.get(0).equals("-help")
					 || argList.get(0).equals("help") ) ? 0 : 1;
			
			if(exitCode==1) {
				System.err.println("Illegal option(s): "+argList);
			}
			System.out.println("Scrambler Manual:\n\n"+
				"Scrambler is an amateur/hackish encryption implementation. It is only intended for casual use, please "
				+ "consider using an industry standard (such as AES) for essential security needs. Note this tool does "
				+ "not distinguish encryption from decryption. (That is: one pass encrypts data, and another pass "
				+ "decrypts it.)\n\n"+
				"Usage: -src filepath [-dst filepath] [-ext scrmbl] [-pwd password]\n\n"+
				"Options:\n"+
				"\t-src: the source file you want to encode/decode.\n"+
				"\t-dst: the destination file you want to write data to.\n"+
				"\t-ext: if dst is omitted, then the source file will be renamed with this file extension. "
				+ "\"scrmblr\" is used by default.\n"+
				"\t-pwd: the password used to encrypt files. If this is not included then you will be prompted "
				+ "for a password (with echoing disabled) once the program begins.");
			System.exit(exitCode);
		}

		if(src==null) {
			System.err.println("error: the -src argument is required See \"-help\" for instructions.");
			System.exit(1);
		}
		
		if(password==null) {
			System.out.println("Password:");
			password = new String(System.console().readPassword());
		}
		
		if(src.isDirectory()) {
			System.err.println("error: the source file is a directory. This version of Scrambler does not yet support encoding directories.");
			System.exit(1);
		}
		if(dst!=null && dst.exists() && dst.isDirectory()) {
			System.err.println("error: the destination file is a directory. Directories are not yet supported (and the source file is not a directory, which is weird!)");
			System.exit(1);
		} else if(dst==null) {
			String s = src.getAbsolutePath();
			if(s.endsWith("."+ext)) {
				//it already resembles xyz.scrmblr
				String shortName = s.substring(0, s.length()-ext.length()-1);
				File file = new File(shortName);
				if(!file.exists()) {
					dst = file;
				}
			}
			if(dst==null) {
				dst = new File(s+"."+ext);
			}
		}

		System.out.println("Source file: "+src.getAbsolutePath());
		ByteEncoder encoder = new Scrambler(password).createEncoder();
		write(encoder, src, dst);
		System.out.println("Successfully encoded to: "+dst.getAbsolutePath());
	}
	
	public static abstract class MarkerRule {
		
		public static class Fixed extends MarkerRule {
			int marker;
			
			public Fixed(int marker) {
				this.marker = marker;
			}
			
			public boolean isMarker(int i) {
				return i==marker;
			}
			
			@Override
			public String toString() {
				return "["+marker+"]";
			}
		}

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
	
	public static void write(ByteEncoder encoder,File src,File dst) throws IOException {
		if(dst.exists()) {
			if(!dst.delete())
				throw new IOException("Unable to delete existing destination file: "+dst.getAbsolutePath());
		}
		if(!dst.createNewFile())
			throw new IOException("Unable to create empty destination file: "+dst.getAbsolutePath());
		try(InputStream in = new FileInputStream(src)) {
			try(final OutputStream out = new FileOutputStream(dst)) {
				final IOException[] writingProblem = new IOException[] { null };
				encoder.setListener(new DataListener() {

					@Override
					public void chunkAvailable(ByteEncoder encoder) {
						try {
							int[] array = encoder.pullImmediately();
							for(int k : array) {
								out.write(k);
							}
						} catch(IOException e) {
							writingProblem[0] = e;
						}
					}

					@Override
					public void encoderClosed(ByteEncoder encoder) {}
					
				});
				int k = in.read();
				while(k!=-1) {
					encoder.push(k);
					if(writingProblem[0]!=null) throw writingProblem[0];
					k = in.read();
				}
				encoder.close();

				int[] array = encoder.pullImmediately();
				if(array!=null) {
					for(int k2 : array) {
						out.write(k2);
					}
				}
			}
		}
	}
	
	protected static class Layer extends ByteEncoder {
		
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
		final SubstitutionModel substitutionModel;
		MarkerRule markerRule;
		int reorderCycle = 0;

		/** Create a Scrambler.
		 * 
		 * @param random the Random to use as data is being encoded. This may be null.
		 * @param oneCount the number of 1's in a byte that are required to flag it as a marker.
		 * @param substitutionModel the optional SubstitutionModel this object may
		 * apply to replace bytes.
		 */
		public Layer(int capacitySeed,MarkerRule markerRule,SubstitutionModel substitutionModel) {
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
		

		public CharacterSubstitutionModel(String chars) {
			this(chars.toCharArray());
		}

		private CharacterSubstitutionModel(CharacterSubstitutionModel other) {
			this.charMap = other.charMap;
		}

		@Override
		public CharacterSubstitutionModel clone() {
			return new CharacterSubstitutionModel(this);
		}
		
		public CharacterSubstitutionModel(char... chars) {
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
		
		public ByteSubstitutionModel() {
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
	
	static class ScramblerLayerFactory {
		
		int capacitySeed;
		MarkerRule rule;
		SubstitutionModel substitutionModel;
		
		ScramblerLayerFactory(int capacitySeed, MarkerRule rule, SubstitutionModel substitutionModel) {
			if(rule==null)
				throw new NullPointerException();
			if(substitutionModel==null)
				throw new NullPointerException();
			
			this.capacitySeed = capacitySeed;
			this.rule = rule;
			this.substitutionModel = substitutionModel;
		}
		public Layer createLayer() {
			return new Layer(capacitySeed, rule, substitutionModel.clone());
		}
	}

	protected List<ScramblerLayerFactory> layers = new ArrayList<>();

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

		List<Integer> capacitySeeds = new ArrayList<>(256+32);
		for(int a = 0; a<k.size(); a++) {
			capacitySeeds.add(random.nextInt(Layer.CAPACITY_MAX));
		}

		char[] charArray = characterSet == null ? null : characterSet.toString().toCharArray();
		SubstitutionModel substitutionModel = characterSet==null ? 
				new ByteSubstitutionModel() : 
				new CharacterSubstitutionModel(charArray);
				
		for(int a = 0; a<k.size(); a++) {
			layers.add(new ScramblerLayerFactory(capacitySeeds.get(a), k.get(a), substitutionModel.clone() ));
		}
		for(int a = k.size()-2; a>=0; a--) {
			layers.add(new ScramblerLayerFactory(capacitySeeds.get(a), k.get(a), substitutionModel.clone()));
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
			copy[a] = layers.get(a).createLayer();
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
