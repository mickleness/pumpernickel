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
package com.pump.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

public class PartitionIteratorTest extends TestCase {
	public void test2Partitions() {
		List<String> list = Arrays.asList("A", "B", "C", "D");
		PartitionIterator<String> iter = new PartitionIterator<>(list, 2, 0);
		Collection<List<List<String>>> results = new HashSet<>();
		while(iter.hasNext()) {
			List<List<String>> k = iter.next();
			results.add(k);
		}
		
		assertTrue(results.contains( Arrays.asList( Arrays.asList(), Arrays.asList("A", "B", "C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A"), Arrays.asList("B", "C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B"), Arrays.asList("C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B", "C"), Arrays.asList("D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B", "C", "D"), Arrays.asList() )));
		assertEquals(5, results.size());
	}
	
	public void test2PartitionsMin1() {
		List<String> list = Arrays.asList("A", "B", "C", "D");
		PartitionIterator<String> iter = new PartitionIterator<>(list, 2, 1);
		Collection<List<List<String>>> results = new HashSet<>();
		while(iter.hasNext()) {
			List<List<String>> k = iter.next();
			results.add(k);
		}
		
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A"), Arrays.asList("B", "C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B"), Arrays.asList("C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B", "C"), Arrays.asList("D") )));
		
		assertEquals(3, results.size());
	}
	
	public void test3Partitions() {
		List<String> list = Arrays.asList("A", "B", "C", "D");
		PartitionIterator<String> iter = new PartitionIterator<>(list, 3, 0);
		Collection<List<List<String>>> results = new HashSet<>();
		while(iter.hasNext()) {
			List<List<String>> k = iter.next();
			results.add(k);
		}
		
		assertTrue(results.contains( Arrays.asList( Arrays.asList(), Arrays.asList(), Arrays.asList("A", "B", "C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList(), Arrays.asList("A"), Arrays.asList("B", "C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList(), Arrays.asList("A", "B"), Arrays.asList("C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList(), Arrays.asList("A", "B", "C"), Arrays.asList("D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList(), Arrays.asList("A", "B", "C", "D"), Arrays.asList() )));

		assertTrue(results.contains( Arrays.asList( Arrays.asList("A"), Arrays.asList(), Arrays.asList("B", "C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A"), Arrays.asList("B"), Arrays.asList("C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A"), Arrays.asList("B", "C"), Arrays.asList("D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A"), Arrays.asList("B", "C", "D"), Arrays.asList() )));

		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B"), Arrays.asList(), Arrays.asList("C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B"), Arrays.asList("C"), Arrays.asList("D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B"), Arrays.asList("C", "D"), Arrays.asList() )));

		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B", "C"), Arrays.asList(), Arrays.asList("D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B", "C"), Arrays.asList("D"), Arrays.asList() )));

		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B", "C", "D"), Arrays.asList(), Arrays.asList() )));
		
		assertEquals(15, results.size());
	}
	
	public void test3PartitionsMin1() {
		List<String> list = Arrays.asList("A", "B", "C", "D");
		PartitionIterator<String> iter = new PartitionIterator<>(list, 3, 1);
		Collection<List<List<String>>> results = new HashSet<>();
		while(iter.hasNext()) {
			List<List<String>> k = iter.next();
			results.add(k);
		}
		
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A"), Arrays.asList("B"), Arrays.asList("C", "D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A"), Arrays.asList("B", "C"), Arrays.asList("D") )));
		assertTrue(results.contains( Arrays.asList( Arrays.asList("A", "B"), Arrays.asList("C"), Arrays.asList("D") )));

		
		assertEquals(3, results.size());
	}

}