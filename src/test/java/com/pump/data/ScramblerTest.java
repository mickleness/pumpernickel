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
package com.pump.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.pump.TestingStrings;
import com.pump.data.Scrambler.ReorderType;

public class ScramblerTest extends TestCase implements TestingStrings {

	@Test
	public void testReorderReverse() {
		int[] list = new int[20];
		for (int a = 50; a < 70; a++) {
			list[a - 50] = a;
		}
		int[] array = new int[list.length];
		ReorderType.REVERSE.reorder(list, 0, list.length, array, 0);
		int[] expected = new int[] { 69, 68, 67, 66, 65, 64, 63, 62, 61, 60,
				59, 58, 57, 56, 55, 54, 53, 52, 51, 50 };
		for (int a = 0; a < array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
	}

	@Test
	public void testReorderNormal() {
		int[] list = new int[20];
		for (int a = 50; a < 70; a++) {
			list[a - 50] = a;
		}
		int[] array = new int[list.length];
		ReorderType.NORMAL.reorder(list, 0, list.length, array, 0);
		int[] expected = new int[] { 50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
				60, 61, 62, 63, 64, 65, 66, 67, 68, 69 };
		for (int a = 0; a < array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
	}

	@Test
	public void testReorderReversePair() {
		int[] list = new int[20];
		for (int a = 50; a < 70; a++) {
			list[a - 50] = a;
		}
		int[] array = new int[list.length];
		ReorderType.REVERSE_PAIRS.reorder(list, 0, list.length, array, 0);
		int[] expected = new int[] { 51, 50, 53, 52, 55, 54, 57, 56, 59, 58,
				61, 60, 63, 62, 65, 64, 67, 66, 69, 68 };
		for (int a = 0; a < array.length; a++) {
			assertEquals(expected[a], array[a]);
		}

		// test it with an odd number of elements:
		list = new int[19];
		for (int a = 50; a < 69; a++) {
			list[a - 50] = a;
		}
		array = new int[list.length];
		ReorderType.REVERSE_PAIRS.reorder(list, 0, list.length, array, 0);
		expected = new int[] { 51, 50, 53, 52, 55, 54, 57, 56, 59, 58, 61, 60,
				63, 62, 65, 64, 67, 66, 68 };
		for (int a = 0; a < array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
	}

	@Test
	public void testAllReorderTypes() {
		for (ReorderType reorderType : ReorderType.values()) {
			testReorderType(reorderType);
		}
	}

	private void testReorderType(ReorderType reorderType) {
		for (int max = 15; max < 25; max++) {
			int[] srcList = new int[max];
			for (int a = 0; a < max; a++) {
				srcList[a] = a;
			}
			int[] dest = new int[srcList.length];
			int[] dest2 = new int[srcList.length];
			reorderType.reorder(srcList, 0, srcList.length, dest, 0);

			int[] src2 = new int[dest.length];
			for (int a = 0; a < dest.length; a++) {
				src2[a] = dest[a];
			}

			reorderType.reorder(src2, 0, srcList.length, dest2, 0);

			int[] src3 = new int[dest2.length];
			for (int a = 0; a < dest2.length; a++) {
				src3[a] = dest2[a];
			}

			assertEquals(srcList.length, src3.length);
			for (int a = 0; a < srcList.length; a++) {
				assertEquals(srcList[a], src3[a]);
			}
		}
	}

	@Test
	public void testReorderCutDeck() {
		int[] list = new int[20];
		for (int a = 50; a < 70; a++) {
			list[a - 50] = a;
		}
		int[] array = new int[list.length];
		ReorderType.CUT_DECK.reorder(list, 0, list.length, array, 0);
		int[] expected = new int[] { 60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
				50, 51, 52, 53, 54, 55, 56, 57, 58, 59 };
		for (int a = 0; a < array.length; a++) {
			assertEquals(expected[a], array[a]);
		}

		// test it with an odd number of elements:
		list = new int[19];
		for (int a = 50; a < 69; a++) {
			list[a - 50] = a;
		}
		array = new int[list.length];
		ReorderType.CUT_DECK.reorder(list, 0, list.length, array, 0);
		expected = new int[] { 59, 60, 61, 62, 63, 64, 65, 66, 67, 50, 51, 52,
				53, 54, 55, 56, 57, 58, 68 };
		for (int a = 0; a < array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
	}

	@Test
	public void testReverseCutDeck() {
		int[] list = new int[20];
		for (int a = 50; a < 70; a++) {
			list[a - 50] = a;
		}
		int[] array = new int[list.length];
		ReorderType.REVERSE_CUT_DECK.reorder(list, 0, list.length, array, 0);
		int[] expected = new int[] { 59, 58, 57, 56, 55, 54, 53, 52, 51, 50,
				69, 68, 67, 66, 65, 64, 63, 62, 61, 60 };
		for (int a = 0; a < array.length; a++) {
			assertEquals(expected[a], array[a]);
		}

		// test it with an odd number of elements:
		list = new int[19];
		for (int a = 50; a < 69; a++) {
			list[a - 50] = a;
		}
		array = new int[list.length];
		ReorderType.REVERSE_CUT_DECK.reorder(list, 0, list.length, array, 0);
		expected = new int[] { 58, 57, 56, 55, 54, 53, 52, 51, 50, 68, 67, 66,
				65, 64, 63, 62, 61, 60, 59 };
		for (int a = 0; a < array.length; a++) {
			assertEquals(expected[a], array[a]);
		}
	}

	/**
	 * Make sure each ReorderType when applied twice restores data to its
	 * original state.
	 */
	public void testReorderRepeat() {
		ReorderType[] types = Scrambler.ReorderType.values();
		for (ReorderType type : types) {
			for (String s : strings) {
				testReorderRepeat(type, s);
			}
		}
	}

	@Test
	private void testReorderRepeat(ReorderType type, String s) {
		int[] list = new int[s.length()];
		for (int a = 0; a < s.length(); a++) {
			list[a] = (int) s.charAt(a);
		}
		int[] array = new int[list.length];
		type.reorder(list, 0, list.length, array, 0);

		int[] list2 = new int[array.length];
		int[] array2 = new int[list.length];
		for (int a = 0; a < array.length; a++) {
			list2[a] = array[a];
		}
		type.reorder(list2, 0, list2.length, array2, 0);
		for (int a = 0; a < s.length(); a++) {
			assertEquals(type + " failed for char #" + a, (int) s.charAt(a),
					array2[a]);
		}
	}

	@Test
	public void testEncodeDecode() throws Exception {
		for (String s : strings) {
			testEncodeDecode(s);
		}
	}

	private void testEncodeDecode(String string) throws IOException {
		String key = "narwhal";
		testEncodeDecodeData(new Scrambler(key), string);
		testEncodeDecodeData(new Scrambler(key, string), string);
	}

	private void testEncodeDecodeData(Scrambler scrambler, String string)
			throws IOException {
		byte[] unencodedData = string.getBytes(Charset.forName("UTF-8"));
		byte[] encodedData;
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			try (OutputStream scrambleOut = scrambler
					.createOutputStream(byteOut)) {
				scrambleOut.write(unencodedData);
			}
			encodedData = byteOut.toByteArray();
		}

		assertEquals(unencodedData.length, encodedData.length);
		assertFalse(equals(unencodedData, encodedData));

		byte[] finalUnencodedData;
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(encodedData)) {
			try (InputStream in = scrambler.createInputStream(byteIn)) {
				try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
					byte[] block = new byte[4096];
					int t = in.read(block);
					while (t != -1) {
						buffer.write(block, 0, t);
						t = in.read(block);
					}
					finalUnencodedData = buffer.toByteArray();
				}
			}
		}

		assertTrue(equals(unencodedData, finalUnencodedData));
	}

	private boolean equals(byte[] array1, byte[] array2) {
		if (array1.length != array2.length)
			return false;
		for (int a = 0; a < array1.length; a++) {
			if (array1[a] != array2[a])
				return false;
		}
		return true;
	}

	/**
	 * In this test we encode "PRODUCT000000"-"PRODUCT000010", and we make sure
	 * that the encoded Strings are not too similar.
	 * <p>
	 * This test triggered a complete refactor of the Scrambler architecture
	 * because some strings were easily "hackable". (That is: you could modify
	 * just 1 character and, when decoded, your String would still start with
	 * "PRODUCT000").
	 */
	@Test
	public void testPredictability() {
		String password = "barbaric background eyes finishing solitary pitch";
		Scrambler scrambler = new Scrambler(password,
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
		List<String> encoded = new ArrayList<>();
		for (int a = 0; a < 10; a++) {
			String s = "PRODUCT00000" + a;
			String s2 = scrambler.encode(s);
			encoded.add(s2);
		}

		for (int a = 0; a < encoded.size(); a++) {
			String s = encoded.get(a);
			for (int b = a + 1; b < encoded.size(); b++) {
				int similar = countSimilarLetters(s, encoded.get(b));
				if (similar == s.length() - 1) {
					fail(s + " and " + encoded.get(b) + " were too similar ("
							+ scrambler.encode(s) + " vs "
							+ scrambler.encode(encoded.get(b)) + ")");
				}
			}

			// make sure if we alter letters by guessing that we don't stumble
			// back into a valid combination:
			for (int b = 0; b < s.length(); b++) {
				String pre = s.substring(0, b) + (char) (s.charAt(b) - 1)
						+ s.substring(b + 1);
				String actual = s;
				String post = s.substring(0, b) + (char) (s.charAt(b) + 1)
						+ s.substring(b + 1);

				try {
					pre = scrambler.encode(pre);
				} catch (Exception e) {
				}
				actual = scrambler.encode(actual);
				try {
					post = scrambler.encode(post);
				} catch (Exception e) {
				}

				assertFalse(pre.contains("PRODUCT"));
				assertTrue(actual.contains("PRODUCT"));
				assertFalse(post.contains("PRODUCT"));
			}
		}
	}

	/**
	 * Count the number of letters in the exact same position between the two
	 * Strings
	 */
	private int countSimilarLetters(String s1, String s2) {
		int sum = 0;
		for (int a = 0; a < Math.min(s1.length(), s2.length()); a++) {
			char ch1 = s1.charAt(a);
			char ch2 = s2.charAt(a);
			if (ch1 == ch2)
				sum++;
		}
		return sum;
	}
}