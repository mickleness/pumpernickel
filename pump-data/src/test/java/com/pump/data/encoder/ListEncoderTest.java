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
package com.pump.data.encoder;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class ListEncoderTest extends TestCase {
	public void testStringList() {
		List<String> list;

		list = Arrays.asList("one", "two", "three");
		test(list, "\"one\",\"two\",\"three\"");

		list = Arrays.asList("inner,comma", "two", "three");
		test(list, "\"inner,comma\",\"two\",\"three\"");

		list = Arrays.asList("inner\"quote", "two", "three");
		test(list, "\"inner\\\"quote\",\"two\",\"three\"");
	}

	public void testIntList() {
		List<Integer> list;

		list = Arrays.asList(1, 2, 3);
		test(list, "1,2,3");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void test(List<?> list, String expectedEncoding) {
		ListEncoder encoder = new ListEncoder(list.get(0).getClass());
		assertEquals(expectedEncoding, encoder.encode(list));
		
		List clone = encoder.parse(expectedEncoding);
		assertEquals(list, clone);
		
	}
}