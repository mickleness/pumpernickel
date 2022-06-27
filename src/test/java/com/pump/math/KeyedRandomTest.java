/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.TestCase;

import com.pump.TestingStrings;

public class KeyedRandomTest extends TestCase implements TestingStrings {

	/**
	 * This makes sure the KeyedRandom creates an array of Randoms with the
	 * expected length.
	 */
	public void testRandomCount() {
		BigInteger key = new BigInteger(largeNumber);
		KeyedRandom random = new KeyedRandom(key);
		int k = (int) (Math.ceil(((double) key.bitLength()) / 64.0) + .5);
		int j = random.getRandoms().length;
		assertTrue("unexpected inner breakdown of Random array (" + k + "!="
				+ j + ")", k == j);
	}

	/**
	 * This makes sure a KeyedRandom is distributing random doubles relatively
	 * equally.
	 * 
	 */
	public void testDistribution() {
		testDistribution(new KeyedRandom(new BigInteger(largeNumber)));
		testDistribution(new KeyedRandom(largeNumber));
		testDistribution(new KeyedRandom("helloworld"));
		testDistribution(new KeyedRandom("was it a rat I saw"));
		testDistribution(new KeyedRandom(
				"Is it better to have loved and lost than never to have loved at all?"));
	}

	private void testDistribution(KeyedRandom random) {
		SortedMap<Integer, MutableLong> map = new TreeMap<Integer, MutableLong>();
		for (long a = 0; a < 0xffff; a++) {
			double v = random.nextDouble();
			int i = (int) (v * 100);
			MutableLong z = map.get(i);
			if (z == null) {
				z = new MutableLong(1);
				map.put(i, z);
			} else {
				z.value++;
			}
		}
		BigInteger median = getMedian(map.values());
		BigInteger diffs = BigInteger.ZERO;
		for (MutableLong l : map.values()) {
			BigInteger diff = BigInteger.valueOf(l.value).subtract(median);
			BigInteger square = diff.pow(2);
			diffs = diffs.add(square);
		}

		double ratio = new BigDecimal(median).divide(
				BigDecimal.valueOf(2).pow(16).divide(BigDecimal.valueOf(100)))
				.doubleValue();
		assertTrue(ratio + " needs to be close to 1.0", ratio > .99
				&& ratio < 1.01);
		BigInteger stdDev = diffs.divide(BigInteger.valueOf(map.size()));
		assertTrue(isLong(stdDev));
		double sqrt = Math.sqrt(stdDev.longValue());
		assertTrue("standard deviation (" + stdDev + ") is too high",
				sqrt < 2000);
	}

	private boolean isLong(BigInteger stdDev) {
		try {
			long l = stdDev.longValue();
			BigInteger copy = BigInteger.valueOf(l);
			if (!copy.equals(stdDev))
				throw new ArithmeticException();
			return true;
		} catch (ArithmeticException e) {
			return false;
		}
	}

	private BigInteger getMedian(Collection<MutableLong> values) {
		BigInteger sum = BigInteger.ZERO;
		for (MutableLong l : values) {
			sum = sum.add(BigInteger.valueOf(l.value));
		}
		sum = sum.divide(BigInteger.valueOf(values.size()));
		return sum;
	}
}