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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.pump.math.MathException.NegativeException;

/** Static methods related to prime factors. */
public class PrimeFactors {
	static Map<Long, long[]> primeFactorLUT = new HashMap<>();
	
	/** Return the first several prime numbers. 
	 * 
	 * @param arrayLength the number of prime numbers to return.
	 * @return an array of the first several prime numbers.
	 */
	public static long[] getPrimeNumbers(int arrayLength) {
		Set<Long> primes = new HashSet<>();
		long a = 2;
		while(primes.size()<arrayLength) {
			try {
				long[] factors = get(a);
				for(int z = 0; z<factors.length && primes.size()<arrayLength; z++) {
					primes.add(factors[z]);
				}
			} catch(NegativeException e) {
				//this won't happen
				throw new RuntimeException(e);
			}
			a++;
		}
		long[] returnValue = new long[primes.size()];
		Iterator<Long> iter = primes.iterator();
		int ctr = 0;
		while(iter.hasNext()) {
			returnValue[ctr++] = iter.next();
		};
		Arrays.sort(returnValue);
		return returnValue;
	}
	
	/** Return the prime factors of a number.
	 * This caches data to a static look-up table.
	 * 
	 * @param i a non-negative long value
	 * @return the prime factors for the argument
	 * @throws NegativeException if the argument is negative
	 */
	public static long[] get(long i) throws NegativeException {
		if(i==0) return new long[] { 1L };
		if(i<0) throw new NegativeException();
		
		long[] results = primeFactorLUT.get(i);
		if(results==null) {
			long max = (long)(Math.sqrt(i))+1;
			for(int a = 2; a<max; a++) {
				if(i%a==0) {
					long[] others = get(i/a);
					results = new long[others.length+1];
					results[0] = a;
					System.arraycopy(others, 0, results, 1, others.length);
					primeFactorLUT.put(i, results);
					return results;
				}
			}
			results = new long[] { i };
			primeFactorLUT.put(i, results);
		}
		return results;
	}
}