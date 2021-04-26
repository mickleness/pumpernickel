package com.pump.showcase.chart;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Given a span/range of numbers this identifies the tickmarks that should be
 * used.
 */
public class BasicTickLabeler {

	/**
	 * 
	 * @param min
	 *            The minimum of the range to express
	 * @param max
	 *            the maximum of the range to express
	 * @return a map of values and their String representations. The chart
	 *         should render tickmarks at the Double values provided, and it
	 *         should display the accompanying String at each tick. The Strings
	 *         will all share the same number of decimal places.
	 */
	public SortedMap<Double, String> getLabeledTicks(double min, double max) {
		SortedSet<BigDecimal> t = getTicks(min, max);
		int leftMaxDigits = 0;
		int rightMaxDigits = 0;
		for (BigDecimal d : t) {
			String str = d.toPlainString();
			leftMaxDigits = Math.max(leftMaxDigits, getLeftDigits(str));
			rightMaxDigits = Math.max(rightMaxDigits, getRightDigits(str));
		}

		StringBuilder pattern = new StringBuilder();
		int ctr = 0;
		for (int a = 0; a < leftMaxDigits; a++) {
			if (ctr == 3) {
				ctr = 0;
				pattern.insert(0, ',');
			}
			ctr++;
			pattern.insert(0, "#");
		}

		if (rightMaxDigits > 0) {
			pattern.append(".");
			for (int a = 0; a < rightMaxDigits; a++) {
				pattern.append("0");
			}
		}

		DecimalFormat format = new DecimalFormat(pattern.toString());
		SortedMap<Double, String> returnValue = new TreeMap<>();

		for (BigDecimal d : t) {
			returnValue.put(d.doubleValue(), format.format(d.doubleValue()));
		}

		return returnValue;
	}

	private int getRightDigits(String str) {
		int i = str.indexOf('.');
		if (i == -1)
			return 0;
		return str.length() - (i + 1);
	}

	private int getLeftDigits(String str) {
		int i = str.indexOf('.');
		if (i == -1)
			return str.length();
		return i;
	}

	protected SortedSet<BigDecimal> getTicks(double min, double max) {

		BigDecimal maxBD = BigDecimal.valueOf(max);

		int roundedUpPowerOf10 = (int) (Math.round(Math.log10(max) + .5) + .5);
		BigDecimal maxScale = BigDecimal.ONE
				.scaleByPowerOfTen(roundedUpPowerOf10);

		SortedSet<BigDecimal> divBy20 = getTicksForDivisor(20, maxBD, maxScale);
		SortedSet<BigDecimal> divBy10 = getTicksForDivisor(10, maxBD, maxScale);
		SortedSet<BigDecimal> divBy5 = getTicksForDivisor(5, maxBD, maxScale);
		SortedSet<BigDecimal> divBy4 = getTicksForDivisor(4, maxBD, maxScale);

		int s20 = divBy20.size();
		int s10 = divBy10.size();
		int s5 = divBy5.size();
		int s4 = divBy4.size();

		if (s20 >= 5 && s20 <= 6)
			return divBy20;
		if (s10 >= 5 && s10 <= 6)
			return divBy10;
		if (s5 >= 5 && s5 <= 6)
			return divBy5;
		if (s5 >= 5 && s4 <= 6)
			return divBy4;

		if (s20 >= 4 && s20 <= 6)
			return divBy20;
		if (s10 >= 4 && s10 <= 6)
			return divBy10;
		if (s5 >= 4 && s5 <= 6)
			return divBy5;

		return divBy4;
	}

	private SortedSet<BigDecimal> getTicksForDivisor(int divisor,
			BigDecimal targetValue, BigDecimal maxScale) {
		SortedSet<BigDecimal> returnValue = new TreeSet<>();
		returnValue.add(BigDecimal.ZERO);

		BigDecimal v = maxScale.divide(new BigDecimal(divisor));

		int k = 0;
		while (true) {
			BigDecimal newValue = v.multiply(new BigDecimal(k++));
			if (returnValue.last().compareTo(targetValue) >= 0)
				break;
			returnValue.add(newValue);
		}
		return returnValue;
	}

}
