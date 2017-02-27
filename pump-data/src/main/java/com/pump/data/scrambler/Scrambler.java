package com.pump.data.scrambler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.pump.io.ByteEncoder;
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
public class Scrambler extends ByteEncoder {
	
	/** The provides a command-line tool to encode and decode files.
	 * 
	 * <code>Scrambler -src filepath -dst filepath -ext styx -pwd password -substitutions</code>
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String args[]) throws IOException {
		for(int a = 0; a<10; a++) {
			String s = "PRODUCT00000"+a;
			System.out.println(s+" -> "+Scrambler.encode("zanzibar", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", s));
		}
		System.out.println( Scrambler.encode("zanzibar", "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", "D9L0ODD8DDQCH"));
		
		/*List<String> argList = new ArrayList<String>();
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
		
		boolean substitutions = argList.remove("-substitutions");
		

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
				"Usage: -src filepath [-dst filepath] [-ext scrmbl] [-pwd password] [-substitutions]\n\n"+
				"Options:\n"+
				"\t-src: the source file you want to encode/decode.\n"+
				"\t-dst: the destination file you want to write data to.\n"+
				"\t-ext: if dst is omitted, then the source file will be renamed with this file extension. "
				+ "\"scrmblr\" is used by default.\n"+
				"\t-pwd: the password used to encrypt files. If this is not included then you will be prompted "
				+ "for a password (with echoing disabled) once the program begins.\n"+
				"\t-substitutions: this flag will activate substitutions. By default the Scrambler only rearranges "
				+ "bytes, but if this is activated then it also alters bytes for added security.");
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
		ByteEncoder encoder = createEncoder(password, substitutions);
		write(encoder, src, dst);
		System.out.println("Successfully encoded to: "+dst.getAbsolutePath());*/
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
	
	public static interface SubstitutionModel {
		
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
		public void applySubstitutions(int[] array, int arrayOffset, int length);
		
		public SubstitutionModel clone();
		
		public SubstitutionModel nextLayer();
	}
	
	/**
	 * This model assumes you have a fixed set of characters you plan on working with.
	 * <p>
	 * This is a great fit if you want to obfuscate a registration code (where you are
	 * sure you understand the original character set), but it is not appropriate if
	 * arbitrary data might be input.
	 */
	public static class CharacterSubstitutionModel implements SubstitutionModel {
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
		
		private CharacterSubstitutionModel(Map<Integer, List<Integer>> charMap) {
			this.charMap = charMap;
		}

		public CharacterSubstitutionModel nextLayer() {
			Map<Integer, List<Integer>> nextMap = new HashMap<>();
			for(Entry<Integer, List<Integer>> entry : charMap.entrySet()) {
				List<Integer> list = entry.getValue();
				List<Integer> copy = new ArrayList<>();
				copy.addAll(list);
				Integer e = copy.remove(0);
				copy.add(e);
				nextMap.put(entry.getKey(), copy);
			}
			return new CharacterSubstitutionModel(nextMap);
		}
		
		private int countOnes(int k) {
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
		public void applySubstitutions(int[] array, int arrayOffset, int length) {
			for(int a = 0; a<length; a++) {
				int oldValue = array[arrayOffset + a];
				int ones = countOnes(oldValue);
				
				List<Integer> candidates = charMap.get(ones);
				Integer position = candidates.indexOf(oldValue);
				if(position==-1)
					throw new IllegalArgumentException("The byte "+oldValue+" ("+((char)oldValue)+") was not included in the original characters used to created this CharacterSubstitutionModel.");
				
				
				int newPos = candidates.size() - 1 - position;
				int newValue = candidates.get(newPos);
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
	public static class ByteSubstitutionModel implements SubstitutionModel {

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
		public void applySubstitutions(int[] array, int arrayOffset, int length) {
			
			runCtr = (runCtr+1)%3;
			switch(runCtr) {
				case 0:
					//reverse the middle two bits: 00011000
					for(int a = 0; a<length; a++) {
						int d = array[a+arrayOffset];
						array[a+arrayOffset] = (d & 0xE7) + (reverseByteLUT[d] & 0x18);
					}
					break;
				case 1:
					//reverse the middle four bits: 00111100
					for(int a = 0; a<length; a++) {
						int d = array[a+arrayOffset];
						array[a+arrayOffset] = (d & 0xC3) + (reverseByteLUT[d] & 0x3C);
					}
					break;
				default:
					//reverse the middle six bits: 01111110
					for(int a = 0; a<length; a++) {
						int d = array[a+arrayOffset];
						array[a+arrayOffset] = (d & 0x81) + (reverseByteLUT[d] & 0x7E);
					}
					break;
			}
		}

		@Override
		public SubstitutionModel nextLayer() {
			//TODO: improve this?
			return clone();
		}
	}
	
	private static ReorderType[] reorderTypes = new ReorderType[] { ReorderType.CUT_DECK, ReorderType.REVERSE_CUT_DECK, ReorderType.REVERSE, ReorderType.REVERSE_PAIRS };
	private class Run {
		RunType type = null;
		List<Integer> bytes = new ArrayList<Integer>();
		
		private void reset(RunType type) {
			this.type = type;
			bytes.clear();
		}
		
		private int[] encode() {
			int[] array = new int[bytes.size()];
			ReorderType reorderType = random!=null ?
					reorderTypes[ random.nextInt(reorderTypes.length) ] :
					reorderTypes[ (reorderCycle++)%reorderTypes.length ];
			int arrayOffset;
			int length;
			if(RunType.BOTH_MARKERS.equals(type)) {
				array[0] = bytes.get(0);
				array[array.length-1] = bytes.get(bytes.size()-1);
				length = bytes.size()-2;
				arrayOffset = 1;
			} else if (RunType.NO_MARKER.equals(type)) {
				arrayOffset = 0;
				length = bytes.size();
			} else {
				array[0] = bytes.get(0);
				length = bytes.size()-1;
				arrayOffset = 1;
			}
			reorderType.reorder(bytes, arrayOffset, length, array, arrayOffset);
			if(substitutionModel!=null) {
				substitutionModel.applySubstitutions(array, arrayOffset, length);
			}
			return array;
		}
	}
	
	int capacity = 15;
	Random random;
	final Run currentRun = new Run();
	final SubstitutionModel substitutionModel;
	int oneCount;
	int reorderCycle = 0;
	boolean[] markerLUT;
	
	/** Create a Scrambler that uses four 1's as the definition of a marker and does not allow substitutions.
	 * 
	 * @param random the Random to use as data is being encoded. This may be null.
	 */
	public Scrambler(Random random) {
		this(random, 4, false);
	}

	/** Create a Scrambler.
	 * 
	 * @param random the Random to use as data is being encoded. This may be null.
	 * @param oneCount the number of 1's in a byte that are required to flag it as a marker.
	 * @param allowSubstitutions if false then this only ever rearranges bytes without
	 * altering them. If true then this transforms bytes so the output may contain bytes not
	 * present in the original.
	 */
	public Scrambler(Random random,int oneCount,boolean allowSubstitutions) {
		this(random, oneCount, allowSubstitutions ? new ByteSubstitutionModel() : null);
	}

	/** Create a Scrambler.
	 * 
	 * @param random the Random to use as data is being encoded. This may be null.
	 * @param oneCount the number of 1's in a byte that are required to flag it as a marker.
	 * @param substitutionModel the optional SubstitutionModel this object may
	 * apply to replace bytes.
	 */
	public Scrambler(Random random,int oneCount,SubstitutionModel substitutionModel) {
		this.oneCount = oneCount;
		this.substitutionModel = substitutionModel;
		this.random = random;
		resetRun();
		markerLUT = new boolean[256];
		for(int a = 0; a<markerLUT.length; a++) {
			markerLUT[a] = isMarker(a);
		}
	}
	
	@Override
	public synchronized void push(int b) {
		if(closed) throw new IllegalStateException("This Scrambler has already been closed.");
		
		boolean completesRun = false;
		if(markerLUT[b]) {
			if(RunType.NO_MARKER.equals(currentRun.type)) {
				pushChunk(currentRun.encode());
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
		currentRun.bytes.add(b);
		if(completesRun || currentRun.bytes.size()==capacity) {
			pushChunk(currentRun.encode());
			resetRun();
		}
	}
	
	private void resetRun() {
		currentRun.reset(null);
		if(random!=null) {
			capacity = 2 + random.nextInt(50);
		}
	}
	
	/** Determine whether a byte is a marker.
	 * Note this is assumed to be static through the lifetime of this
	 * Scrambler. (It is used to create a LUT when this Scrambler
	 * is first constructed.)
	 * 
	 * @param b a byte (represented as a [0,255] integer).
	 * @return true if the byte is considered a marker.
	 */
	protected boolean isMarker(int b) {
		int ones = 0;
		int z = b;
		for(int s = 0; s<8; s++) {
			if( ((z >> s) & 0x01)!=0) {
				ones++;
			}
		}
		return ones==oneCount;
	}
	
	
	protected void flush() {
		if(currentRun.type!=null) {
			pushChunk(currentRun.encode());
			resetRun();
		}
	}

	/** Create a complex encoder based on a passkey.
	 * <p>This actually creates dozens of Scrambler instances and
	 * chains them together.
	 * 
	 * @param key an optional key to guide the random number generation.
	 * @param allowSubstitutions if true then characters will be transformed as well as rearranged.
	 * @return the complex encoder based on the key.
	 */
	public static ByteEncoder createEncoder(String key,boolean allowSubstitutions) {
		return createEncoder(key, allowSubstitutions ? new ByteSubstitutionModel() : null);
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
	public static ByteEncoder createEncoder(String key,SubstitutionModel substitutionModel) {
		List<Integer> k = new ArrayList<Integer>();
		Random random = key==null ? new Random(0) : new KeyedRandom(key);
		for(int a = 0; a<100; a++) {
			int v = random.nextInt(8);
			while(k.size()>0 && k.get(k.size()-1)==v) {
				v = random.nextInt(8);
			}
			k.add( v );
		}
		List<Scrambler> encoders = new ArrayList<Scrambler>();
		SubstitutionModel currentLayer = substitutionModel;
		List<SubstitutionModel> substitutionModels = new ArrayList<>();
		
		
		for(int a = 0; a<k.size(); a++) {
			currentLayer = currentLayer==null ? null : currentLayer.nextLayer();
			substitutionModels.add(currentLayer);
			Random r = key==null ? new Random(a) : new KeyedRandom(key+a);
			encoders.add(new Scrambler(r, k.get(a), substitutionModels.get(a)) );
		}
		for(int a = k.size()-2; a>=0; a--) {
			currentLayer = substitutionModels.get(a);
			currentLayer = currentLayer==null ? null : currentLayer.clone();
			Random r = key==null ? new Random(a) : new KeyedRandom(key+a);
			encoders.add(new Scrambler(r, k.get(a), currentLayer) );
		}
		
		Scrambler[] series = encoders.toArray(new Scrambler[encoders.size()]);
		
		ChainedByteEncoder chain = new ChainedByteEncoder(series);
		return chain;
	}

	/** Reencode a String using a Scrambler.
	 * 
	 * @param key an optional key to guide the random number generation.
	 * @param allowSubstitutions if true then characters will be transformed as well as rearranged.
	 * @param s the String to reencode
	 * @return the String data after passing through a Scrambler.
	 */
	public static String encode(String key,boolean allowSubstitutions,String s) {
		ByteEncoder encoder = createEncoder(key, allowSubstitutions);
		return encode(encoder, s);
	}


	/** Reencode a String using a Scrambler.
	 * 
	 * @param key an optional key to guide the random number generation.
	 * @param charset a set of characters to use for character substitution.
	 * @param s the String to reencode
	 * @return the String data after passing through a Scrambler.
	 */
	public static String encode(String key,String charset,String s) {
		ByteEncoder encoder = createEncoder(key, new CharacterSubstitutionModel(charset.toCharArray()) );
		return encode(encoder, s);
	}
}
