package com.pump.data.scrambler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.junit.Test;

import com.pump.TestingStrings;
import com.pump.data.scrambler.Scrambler.ReorderType;

public class ScramblerTest extends TestCase implements TestingStrings {

	@Test
	public void testReorderReverse() {
		List<Integer> list = new ArrayList<Integer>();
		for(int a = 50; a<70; a++) {
			list.add(a);
		}
		int[] array = new int[list.size()];
		ReorderType.REVERSE.reorder(list, 0, list.size(), array, 0);
		int[] expected = new int[] { 69, 68, 67, 66, 65, 64, 63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50 };
		for(int a = 0; a<array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
	}

	@Test
	public void testReorderNormal() {
		List<Integer> list = new ArrayList<Integer>();
		for(int a = 50; a<70; a++) {
			list.add(a);
		}
		int[] array = new int[list.size()];
		ReorderType.NORMAL.reorder(list, 0, list.size(), array, 0);
		int[] expected = new int[] { 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69 };
		for(int a = 0; a<array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
	}

	@Test
	public void testReorderReversePair() {
		List<Integer> list = new ArrayList<Integer>();
		for(int a = 50; a<70; a++) {
			list.add(a);
		}
		int[] array = new int[list.size()];
		ReorderType.REVERSE_PAIRS.reorder(list, 0, list.size(), array, 0);
		int[] expected = new int[] { 51, 50, 53, 52, 55, 54, 57, 56, 59, 58, 61, 60, 63, 62, 65, 64, 67, 66, 69, 68 };
		for(int a = 0; a<array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
		
		//test it with an odd number of elements:
		list.clear();
		for(int a = 50; a<69; a++) {
			list.add(a);
		}
		array = new int[list.size()];
		ReorderType.REVERSE_PAIRS.reorder(list, 0, list.size(), array, 0);
		expected = new int[] { 51, 50, 53, 52, 55, 54, 57, 56, 59, 58, 61, 60, 63, 62, 65, 64, 67, 66, 68 };
		for(int a = 0; a<array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
	}
	
	@Test
	public void testAllReorderTypes() {
		for(ReorderType reorderType : ReorderType.values()) {
			testReorderType(reorderType);
		}
	}

	private void testReorderType(ReorderType reorderType) {
		for(int max = 15; max<25; max++) {
			List<Integer> srcList = new ArrayList<>();
			for(int a = 0; a<max; a++) {
				srcList.add(a);
			}
			int[] dest = new int[srcList.size()];
			int[] dest2 = new int[srcList.size()];
			reorderType.reorder(srcList, 0, srcList.size(), dest, 0);
			
			List<Integer> src2 = new ArrayList<>();
			for(int a = 0; a<dest.length; a++) {
				src2.add(dest[a]);
			}
			
			reorderType.reorder(src2, 0, srcList.size(), dest2, 0);

			List<Integer> src3 = new ArrayList<>();
			for(int a = 0; a<dest2.length; a++) {
				src3.add(dest2[a]);
			}
			
			assertEquals(srcList, src3);
		}
	}

	@Test
	public void testReorderCutDeck() {
		List<Integer> list = new ArrayList<Integer>();
		for(int a = 50; a<70; a++) {
			list.add(a);
		}
		int[] array = new int[list.size()];
		ReorderType.CUT_DECK.reorder(list, 0, list.size(), array, 0);
		int[] expected = new int[] { 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59 };
		for(int a = 0; a<array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
		
		//test it with an odd number of elements:
		list.clear();
		for(int a = 50; a<69; a++) {
			list.add(a);
		}
		array = new int[list.size()];
		ReorderType.CUT_DECK.reorder(list, 0, list.size(), array, 0);
		expected = new int[] { 59, 60, 61, 62, 63, 64, 65, 66, 67, 50, 51, 52, 53, 54, 55, 56, 57, 58, 68 };
		for(int a = 0; a<array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
	}

	@Test
	public void testReverseCutDeck() {
		List<Integer> list = new ArrayList<Integer>();
		for(int a = 50; a<70; a++) {
			list.add(a);
		}
		int[] array = new int[list.size()];
		ReorderType.REVERSE_CUT_DECK.reorder(list, 0, list.size(), array, 0);
		int[] expected = new int[] { 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 69, 68, 67, 66, 65, 64, 63, 62, 61, 60 };
		for(int a = 0; a<array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
		
		//test it with an odd number of elements:
		list.clear();
		for(int a = 50; a<69; a++) {
			list.add(a);
		}
		array = new int[list.size()];
		ReorderType.REVERSE_CUT_DECK.reorder(list, 0, list.size(), array, 0);
		expected = new int[] { 58, 57, 56, 55, 54, 53, 52, 51, 50, 68, 67, 66, 65, 64, 63, 62, 61, 60, 59 };
		for(int a = 0; a<array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
	}
	
	/** Make sure each ReorderType when applied twice restores data to its original state. */
	public void testReorderRepeat() {
		ReorderType[] types = Scrambler.ReorderType.values();
		for(ReorderType type : types) {
			for(String s : strings) {
				testReorderRepeat(type, s);
			}
		}
	}

	@Test
	private void testReorderRepeat(ReorderType type, String s) {
		List<Integer> list = new ArrayList<Integer>(s.length());
		for(int a = 0; a<s.length(); a++) {
			list.add( (int)s.charAt(a) );
		}
		int[] array = new int[list.size()];
		type.reorder(list, 0, list.size(), array, 0);
		
		List<Integer> list2 = new ArrayList<Integer>();
		int[] array2 = new int[list.size()];
		for(int a = 0; a<array.length; a++) {
			list2.add(array[a]);
		}
		type.reorder(list2, 0, list2.size(), array2, 0);
		for(int a = 0; a<s.length(); a++) {
			assertEquals( type+" failed for char #"+a, (int)s.charAt(a), array2[a] );
		}
	}

	@Test
	public void testEncodeDecode() throws Exception{
		for(String s : strings) {
			testEncodeDecodeUsingStrings(s);
			testEncodeDecodeUsingStreams(s);
		}
	}

	private void testEncodeDecodeUsingStreams(String string) throws IOException {
		String key = "narwhal";
		Scrambler scrambler = new Scrambler(key);
		byte[] unencodedData = string.getBytes(Charset.forName("UTF-8"));
		byte[] encodedData;
		try(ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			try(OutputStream scrambleOut = scrambler.createOutputStream(byteOut)) {
				scrambleOut.write(unencodedData);
			}
			encodedData = byteOut.toByteArray();
		}
		
		assertEquals(unencodedData.length, encodedData.length);
		assertFalse(equals(unencodedData, encodedData));
		
		byte[] finalUnencodedData;
		try(ByteArrayInputStream byteIn = new ByteArrayInputStream(encodedData)) {
			try(InputStream in = scrambler.createInputStream(byteIn)) {
				try(ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
					byte[] block = new byte[4096];
					int t = in.read(block);
					while(t!=-1) {
						buffer.write(block,0,t);
						t = in.read(block);
					}
					finalUnencodedData = buffer.toByteArray();
				}
			}
		}

		assertTrue(equals(unencodedData, finalUnencodedData));
	}

	private boolean equals(byte[] array1, byte[] array2) {
		if(array1.length!=array2.length)
			return false;
		for(int a = 0; a<array1.length; a++) {
			if(array1[a]!=array2[a])
				return false;
		}
		return true;
	}

	private void testEncodeDecodeUsingStrings(String string) {
		String key = "narwhal";

		//also use the bytes substitution model:
		{
			Scrambler scrambler = new Scrambler(key);
			String encoded = scrambler.encode(string);
			
			if(string.length()>10) //because a short word (like "odd") can be reshuffled only a few possible ways ("odd", "dod", "ddo")
				assertFalse("the encoded value was identical to the input: \""+string+"\"", string.equals(encoded));
			String decoded = scrambler.encode(encoded);
			assertEquals(string, decoded);
		}

		//also use the character substitution model:
		{
			Collection<Character> allChars = new TreeSet<>();
			for(int a = 0; a<string.length(); a++) {
				allChars.add(string.charAt(a));
			}
			StringBuilder charset = new StringBuilder();
			for(Character ch : allChars) {
				charset.append(ch);
			}
			Scrambler scrambler = new Scrambler(key, charset);

			String encoded = scrambler.encode(string);
			if(string.length()>10)
				assertFalse("the encoded value was identical to the input: \""+string+"\"", string.equals(encoded));
			String decoded = scrambler.encode(encoded);
			assertEquals(string, decoded);
		}
	}
	
	@Test
	public void testPredictability() {
		String password = "barbaric background eyes finishing solitary pitch";
		Scrambler scrambler = new Scrambler(password, "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
		List<String> encoded = new ArrayList<>();
		for(int a = 0; a<10; a++) {
			String s = "PRODUCT00000"+a;
			String s2 = scrambler.encode(s);
			encoded.add(s2);
		}
		
		for(int a = 0; a<encoded.size(); a++) {
			String s = encoded.get(a);
			for(int b = a+1; b<encoded.size(); b++) {
				int similar = countSimilarLetters(s, encoded.get(b));
				if(similar==s.length()-1) {
					fail(s+" and "+encoded.get(b)+" were too similar ("+
							scrambler.encode(s) +" vs "+
							scrambler.encode(encoded.get(b))+")");
				}
			}
			
			// make sure if we alter letters by guessing that we don't stumble back into a valid combination:
			for(int b = 0; b<s.length(); b++) {
				String pre = s.substring(0,b) + (char) (s.charAt(b) - 1) + s.substring(b+1);
				String actual = s;
				String post = s.substring(0,b) +(char) (s.charAt(b) + 1) + s.substring(b+1);
				
				try {
					pre = scrambler.encode(pre);
				} catch(Exception e) {}
				actual = scrambler.encode(actual);
				try {
					post = scrambler.encode(post);
				} catch(Exception e) {}
				
				assertFalse(pre.contains("PRODUCT"));
				assertTrue(actual.contains("PRODUCT"));
				assertFalse(post.contains("PRODUCT"));
			}
		}
	}

	private int countSimilarLetters(String s1, String s2) {
		int sum = 0;
		for(int a = 0; a<s1.length(); a++) {
			char ch1 = s1.charAt(a);
			char ch2 = s2.charAt(a);
			if(ch1==ch2)
				sum++;
		}
		return sum;
	}
}
