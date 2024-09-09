/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.data;

import java.io.Serial;
import java.util.Objects;

/**
 * This BoundsChecker makes sure a value is within a bounded range.
 */
public class ComparableBoundsChecker<T extends Comparable>
		extends BoundsChecker<T> {
	@Serial
	private static final long serialVersionUID = 1L;

	T minValue, maxValue;
	boolean includeMin, includeMax;

	public ComparableBoundsChecker(T minValue, T maxValue, boolean includeMin,
			boolean includeMax) {
		Objects.requireNonNull(minValue);
		Objects.requireNonNull(maxValue);
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.includeMin = includeMin;
		this.includeMax = includeMax;
	}

	public T getMinValue() {
		return minValue;
	}

	public T getMaxValue() {
		return maxValue;
	}

	public boolean isIncludeMin() {
		return includeMin;
	}

	public boolean isIncludeMax() {
		return includeMax;
	}

	@Override
	public void check(Key<T> key, T t) throws IllegalArgumentException {
		int k1 = minValue.compareTo(t);
		if (k1 > 0 || (k1 == 0 && !isIncludeMin()))
			throw new IllegalArgumentException(key.getName()
					+ " must be between " + getMinValue() + " and "
					+ getMaxValue() + ". Illegal value: " + t);

		int k2 = t.compareTo(maxValue);
		if (k2 > 0 || (k2 == 0 && !isIncludeMax()))
			throw new IllegalArgumentException(key.getName()
					+ " must be between " + getMinValue() + " and "
					+ getMaxValue() + ". Illegal value: " + t);

	}

	@Override
	public int hashCode() {
		return Objects.hash(getMinValue(), getMaxValue(), isIncludeMin(),
				isIncludeMax());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ComparableBoundsChecker other))
			return false;
		if (!getMinValue().equals(other.getMinValue()))
			return false;
		if (!getMaxValue().equals(other.getMaxValue()))
			return false;
		if (isIncludeMin() != other.isIncludeMin())
			return false;
		if (isIncludeMax() != other.isIncludeMax())
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (isIncludeMin()) {
			sb.append("[");
		} else {
			sb.append("(");
		}
		sb.append(getMinValue());
		sb.append(", ");
		sb.append(getMaxValue());
		if (isIncludeMax()) {
			sb.append("]");
		} else {
			sb.append(")");
		}
		return sb.toString();
	}

}