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
package com.pump.math;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is a Random implementation that is seeded by a String or a BigInteger.
 * <p>
 * This means you can give this object a complex seed of arbitrary length, but
 * this remains a deterministic pseudo-random number generator (unlike
 * SecureRandom).
 * <p>
 * This delegates to an array of Randoms seeded by 64-bit chunks of the original
 * master seed, so the complexity/variety of this Random is related to the
 * length/size of the master seed.
 */
public class KeyedRandom extends Random {
	private static final long serialVersionUID = 1L;

	protected final Random[] random;
	AtomicInteger ctr = new AtomicInteger(0);

	/**
	 * Create a new KeyedRandom.
	 * 
	 * @param seed
	 *            the string that acts as a seed. This is converted to a
	 *            BigInteger, which is broken into 64-bit chunks to act as a
	 *            series of seeds for other <code>java.util.Random</code>
	 *            objects.
	 */
	public KeyedRandom(CharSequence seed) {
		this(convertToBigInteger(seed));
	}

	private static BigInteger convertToBigInteger(CharSequence key) {
		BigInteger i = null;
		for (int a = 0; a < key.length(); a++) {
			char ch = key.charAt(a);
			if (i == null) {
				i = BigInteger.valueOf((int) ch);
			} else {
				i = i.shiftLeft(16).add(BigInteger.valueOf((int) ch));
			}
		}
		return i;
	}

	/**
	 * Create a new KeyedRandom.
	 * 
	 * @param seed
	 *            this is broken into 64-bit chunks to act as a series of seeds
	 *            for other <code>java.util.Random</code> objects.
	 */
	public KeyedRandom(BigInteger seed) {
		if (seed.compareTo(BigInteger.ONE) < 0)
			throw new IllegalArgumentException("seed (" + seed
					+ ") must be one or greater");
		List<Random> list = new ArrayList<Random>();
		while (seed.compareTo(BigInteger.ZERO) > 0) {
			long l = seed.longValue();
			list.add(new Random(l));
			seed = seed.shiftRight(64);
		}
		random = list.toArray(new Random[list.size()]);
	}

	protected Random[] getRandoms() {
		return random;
	}

	@Override
	public int nextInt(int bound) {
		int k = ctr.incrementAndGet() % random.length;
		return random[k].nextInt(bound);
	}

	@Override
	public long nextLong() {
		int k = ctr.incrementAndGet() % random.length;
		return random[k].nextLong();
	}

	@Override
	protected int next(int bits) {
		int k = ctr.incrementAndGet() % random.length;
		int b = 1 << bits;
		return random[k].nextInt(b);
	}
}