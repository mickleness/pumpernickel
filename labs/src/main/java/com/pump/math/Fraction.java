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

import java.io.IOException;

public class Fraction extends Number implements Comparable<Fraction> {

	private static final long serialVersionUID = 1L;

	long numerator, denominator;

	public Fraction(long numerator, long denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(numerator) ^ Long.hashCode(denominator);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Fraction))
			return false;
		Fraction other = (Fraction) obj;
		return other.numerator == numerator && other.denominator == denominator;
	}

	/**
	 * Return true if this fraction represents an integer (whole number)
	 */
	public boolean isInteger() {
		long remainder = numerator % denominator;
		return remainder == 0;
	}

	public long getNumerator() {
		return numerator;
	}

	public long getDenominator() {
		return denominator;
	}

	@Override
	public String toString() {
		return numerator + "/" + denominator;
	}

	@Override
	public int compareTo(Fraction o) {
		long v1 = numerator * o.denominator;
		long v2 = o.numerator * denominator;
		return Long.compare(v1, v2);
	}

	@Override
	public int intValue() {
		return Math.round(floatValue());
	}

	@Override
	public long longValue() {
		return intValue();
	}

	@Override
	public float floatValue() {
		return (float) doubleValue();
	}

	@Override
	public double doubleValue() {
		return ((double) numerator) / ((double) denominator);
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeLong(numerator);
		out.writeLong(denominator);
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int internalVersion = in.readInt();
		if (internalVersion == 0) {
			numerator = in.readLong();
			denominator = in.readLong();
		} else {
			throw new IOException(
					"Unsupported internal version: " + internalVersion);
		}
	}

}