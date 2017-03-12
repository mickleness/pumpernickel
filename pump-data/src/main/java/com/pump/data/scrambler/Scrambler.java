package com.pump.data.scrambler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

	/** Reencode a String using a Scrambler.
	 * 
	 * @param key an optional key to guide the random number generation.
	 * @param charset a set of characters to use for character substitution.
	 * @param s the String to reencode
	 * @return the String data after passing through a Scrambler.
	 */
	public static String encode(String key,String charset,String s) {
		Scrambler scrambler = new Scrambler(key, charset );
		return ByteEncoder.encode(scrambler.createEncoder(), s);
	}
	
	static class ScramblerLayerFactory {
		
		int capacitySeed;
		ScramblerMarkerRule rule;
		ScramblerSubstitutionModel substitutionModel;
		
		ScramblerLayerFactory(int capacitySeed, ScramblerMarkerRule rule, ScramblerSubstitutionModel substitutionModel) {
			if(rule==null)
				throw new NullPointerException();
			if(substitutionModel==null)
				throw new NullPointerException();
			
			this.capacitySeed = capacitySeed;
			this.rule = rule;
			this.substitutionModel = substitutionModel;
		}
		public ScramblerLayer createLayer() {
			return new ScramblerLayer(capacitySeed, rule, substitutionModel.clone());
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
		List<ScramblerMarkerRule> k = new ArrayList<>(256 + 32);
		for(int a = 0; a<256; a++) {
			k.add(new ScramblerMarkerRule.Fixed(a));
		}
		for(int a = 0; a<32; a++) {
			k.add(new ScramblerMarkerRule.OneCount(a%8));
		}
		Random random = key==null ? new Random(0) : new KeyedRandom(key);
		Collections.shuffle(k, random);

		List<Integer> capacitySeeds = new ArrayList<>(256+32);
		for(int a = 0; a<k.size(); a++) {
			capacitySeeds.add(random.nextInt(ScramblerLayer.CAPACITY_MAX));
		}
		
		List<ScramblerSubstitutionModel> substitutionModels = new ArrayList<>();
		
		char[] charArray = characterSet == null ? null : characterSet.toString().toCharArray();
		
		for(int a = 0; a<k.size(); a++) {
			ScramblerSubstitutionModel substitutionModel = characterSet==null ? 
					new ByteSubstitutionModel() : 
					new CharacterSubstitutionModel(charArray);
			substitutionModels.add(substitutionModel);
			layers.add(new ScramblerLayerFactory(capacitySeeds.get(a), k.get(a), substitutionModels.get(a)));
		}
		for(int a = k.size()-2; a>=0; a--) {
			ScramblerSubstitutionModel substitutionModel = substitutionModels.get(a);
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
		ScramblerLayer[] copy = new ScramblerLayer[layers.size()];
		for(int a = 0; a<layers.size(); a++) {
			copy[a] = layers.get(a).createLayer();
		}
		ChainedByteEncoder chainedEncoders = new ChainedByteEncoder(copy);
		return chainedEncoders;
		
	}

	public String encode(String string) {
		return ByteEncoder.encode(createEncoder(), string);
	}
}
