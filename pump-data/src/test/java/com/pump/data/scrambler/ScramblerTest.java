package com.pump.data.scrambler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import junit.framework.TestCase;

import com.pump.TestingStrings;
import com.pump.data.scrambler.Scrambler.ReorderType;

public class ScramblerTest extends TestCase implements TestingStrings {

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

	public void testEncodeDecode() {
		for(String s : strings) {
			testEncodeDecode(s);
		}
	}

	private void testEncodeDecode(String string) {
		String key = "narwhal";
		//test both with and without substitutions
		for(boolean useByteEncoder : new boolean[] { true, false} ) {
			String encoded = Scrambler.encode(key, useByteEncoder, string);
			
			if(string.length()>10) //because a short word (like "odd") can be reshuffled only a few possible ways ("odd", "dod", "ddo")
				assertFalse("the encoded value was identical to the input: \""+string+"\"", string.equals(encoded));
			System.out.println(encoded);
			String decoded = Scrambler.encode(key, useByteEncoder, encoded);
			assertEquals(string, decoded);
		}
		
		//also use the character substitution model:
		Collection<Character> allChars = new TreeSet<>();
		for(int a = 0; a<string.length(); a++) {
			allChars.add(string.charAt(a));
		}
		StringBuilder charset = new StringBuilder();
		for(Character ch : allChars) {
			charset.append(ch);
		}

		String encoded = Scrambler.encode(key, charset.toString(), string);
		if(string.length()>10)
			assertFalse("the encoded value was identical to the input: \""+string+"\"", string.equals(encoded));
		System.out.println(encoded);
		String decoded = Scrambler.encode(key, charset.toString(), encoded);
		assertEquals(string, decoded);
	}
}
