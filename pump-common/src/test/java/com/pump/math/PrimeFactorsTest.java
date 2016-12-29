package com.pump.math;

import com.pump.math.MathException.NegativeException;

import junit.framework.TestCase;

public class PrimeFactorsTest extends TestCase {
	
	public void testGetPrimeNumbers() {
		int expected = 10;
		
		long[] primes = PrimeFactors.getPrimeNumbers(expected);
		assertEquals(expected, primes.length);
		assertEquals(2, primes[0]);
		assertEquals(3, primes[1]);
		assertEquals(5, primes[2]);
		assertEquals(7, primes[3]);
		assertEquals(11, primes[4]);
		assertEquals(13, primes[5]);
		assertEquals(17, primes[6]);
		assertEquals(19, primes[7]);
		assertEquals(23, primes[8]);
		assertEquals(29, primes[9]);
	}
	
	public void testGet() throws NegativeException {
		{
			try {
				PrimeFactors.get(-5);
				fail();
			} catch(NegativeException e) {
				//pass!
			}
		}
		
		{
			long[] factors = PrimeFactors.get(0);
			assertEquals(1, factors.length);
			assertEquals(1, factors[0]);
		}
		
		
		{
			long[] factors = PrimeFactors.get(2*2*2*2);
			assertEquals(4, factors.length);
			assertEquals(2, factors[0]);
			assertEquals(2, factors[1]);
			assertEquals(2, factors[2]);
			assertEquals(2, factors[3]);
		}
		
		{
			long[] factors = PrimeFactors.get(2*3*3*5*7);
			assertEquals(5, factors.length);
			assertEquals(2, factors[0]);
			assertEquals(3, factors[1]);
			assertEquals(3, factors[2]);
			assertEquals(5, factors[3]);
			assertEquals(7, factors[4]);
		}
		
		{
			long[] factors = PrimeFactors.get(7*23*29);
			assertEquals(3, factors.length);
			assertEquals(7, factors[0]);
			assertEquals(23, factors[1]);
			assertEquals(29, factors[2]);
		}
	}
}
