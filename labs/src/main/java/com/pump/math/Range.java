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
import java.io.Serializable;
import java.util.Objects;

/**
 * This represents a range of values which may or may not be bounded on either 
 * side. For example this could represent "x < 10" or "4 <= x <= 6" or "0 <= x".
 * <p>
 * Each range includes an optional min and max value. If the min is null then
 * it is assumed to be negative infinity. For example: "x < 10" is equivalent to
 * "-infinity < x < 10". Similarly if the max is null then the max is assumed to
 * be positive infinity. (If both the min and max are null then this range has no 
 * limitations and {@link #contains(Comparable) will always return true.)
 * <p>
 * If a min or a max is defined there is an additional boolean to indicate whether
 * this range includes that boundary or not. For example: "x < 10" (which does not 
 * include 10) is different from "x <= 10" (which does include 10).
 *
 * @param <T>
 */
public class Range<T extends Comparable<T>> implements Serializable {
	private static final long serialVersionUID = 1L;

	protected T min, max;
	protected boolean includeMin, includeMax;

	/**
	 * Create a new Range.
	 * 
	 * @param min
	 *            the minimum of this Range. Null is interpreted as negative
	 *            infinity.
	 * @param max
	 *            the maximum of this Range. Null is interpreted as positive
	 *            infinity.
	 * @param includeMin
	 *            if min is non-null, then this is the expected value of calling
	 *            <code>contains(min)</code>
	 * @param includeMax
	 *            if max is non-null, then this is the expected value of calling
	 *            <code>contains(max)</code>
	 */
	public Range(T min, T max, boolean includeMin, boolean includeMax) {
		this.min = min;
		this.max = max;
		this.includeMin = includeMin;
		this.includeMax = includeMax;
	}

	/**
	 * @return The minimum value of this range, or null if there is no minimum
	 *         value.
	 */
	public T getMin() {
		return min;
	}

	/**
	 * @return The maximum value of this range, or null if there is no maximum
	 *         value.
	 */
	public T getMax() {
		return max;
	}

	/**
	 * @return true if the minimum is included in this range.
	 */
	public boolean isIncludeMin() {
		return min == null ? false : includeMin;
	}

	/**
	 * @return true if the maximum is included in this range.
	 */
	public boolean isIncludeMax() {
		return max == null ? false : includeMax;
	}

	/**
	 * Return true if the argument intersects this range.
	 * 
	 * @param other
	 *            the range the compare against
	 * @return true if the argument intersects this range.
	 */
	public boolean intersects(Range<T> other) {
		Objects.requireNonNull(other);
		return and(other) != null;
	}

	/**
	 * Create a Range, or return null if the parameters created an invalid Range
	 * like "10 < x < 10".
	 */
	private Range<T> createRange(T min, T max, boolean includeMin,
			boolean includeMax) {
		if (min != null && max != null && min.compareTo(max) == 0) {
			if (!includeMin || !includeMax)
				return null;
		}
		return new Range<T>(min, max, includeMin, includeMax);
	}

	/**
	 * Return the intersection between this Range and the argument, or null if
	 * no intersection exists.
	 */
	public Range<T> and(Range<T> other) {
		T min = getMin();
		T max = getMax();
		T otherMin = other.getMin();
		T otherMax = other.getMax();

		// if either is an infinite range, return the other
		if (min == null && max == null)
			return other;
		if (otherMin == null && otherMax == null)
			return this;

		if (min != null && max != null) {
			if (otherMin != null && otherMax != null) {
				int k1 = max.compareTo(otherMin);
				if (k1 < 0)
					return null;
				if (k1 == 0 && !(isIncludeMax() || other.isIncludeMin()))
					return null;

				int k2 = otherMax.compareTo(min);
				if (k2 < 0)
					return null;
				if (k2 == 0 && !(other.isIncludeMax() || isIncludeMin()))
					return null;

				T newMin = max(min, otherMin);
				T newMax = min(max, otherMax);
				return createRange(newMin, newMax,
						contains(newMin) && other.contains(newMin),
						contains(newMax) && other.contains(newMax));
			} else if (otherMin == null && otherMax != null) {
				T newMax = min(max, otherMax);
				return createRange(min, newMax, isIncludeMin(),
						contains(newMax) && other.contains(newMax));
			} else if (otherMin != null && otherMax == null) {
				T newMin = max(min, otherMin);
				return createRange(newMin, max,
						contains(newMin) && other.contains(newMin),
						isIncludeMax());
			}
		} else if (min == null && max != null) {
			if (otherMin != null) {
				int k = max.compareTo(otherMin);
				if (k < 0)
					return null;
				if (k == 0 && !(isIncludeMax() || other.isIncludeMin()))
					return null;
			}

			T newMax = otherMax == null ? max : min(max, otherMax);
			return createRange(otherMin, newMax, other.isIncludeMin(),
					contains(newMax) && other.contains(newMax));
		} else if (min != null && max == null) {
			if (otherMax != null) {
				int k = otherMax.compareTo(min);
				if (k < 0)
					return null;
				if (k == 0 && !(isIncludeMin() || other.isIncludeMax()))
					return null;
			}

			T newMin = otherMin == null ? min : max(min, otherMin);
			return createRange(newMin, otherMax,
					contains(newMin) && other.contains(newMin),
					other.isIncludeMax());
		}

		// this should be unreachable
		throw new IllegalStateException();
	}

	/**
	 * Return the combination between this Range and the argument, or null if
	 * that combination cannot be expressed as a single Range. For example:
	 * "0 < x < 10" and "100 < x < 200" cannot be combined into one unified
	 * Range because of the gap (10-90) between the two Ranges.
	 */
	public Range<T> or(Range<T> other) {
		T min = getMin();
		T max = getMax();
		T otherMin = other.getMin();
		T otherMax = other.getMax();

		// if either is an infinite range, return an infinite range
		if (min == null && max == null)
			return this;
		if (otherMin == null && otherMax == null)
			return other;

		if (min != null && max != null) {
			if (otherMin != null && otherMax != null) {
				int k1 = max.compareTo(otherMin);
				if (k1 < 0)
					return null;
				if (k1 == 0 && !(isIncludeMax() || other.isIncludeMin()))
					return null;

				int k2 = otherMax.compareTo(min);
				if (k2 < 0)
					return null;
				if (k2 == 0 && !(other.isIncludeMax() || isIncludeMin()))
					return null;

				T newMin = min(min, otherMin);
				T newMax = max(max, otherMax);
				return createRange(newMin, newMax,
						contains(newMin) || other.contains(newMin),
						contains(newMax) || other.contains(newMax));
			} else if (otherMin == null && otherMax != null) {
				T newMax = max(max, otherMax);
				return createRange(null, newMax, false, contains(newMax)
						|| other.contains(newMax));
			} else if (otherMin != null && otherMax == null) {
				T newMin = min(min, otherMin);
				return createRange(newMin, null,
						contains(newMin) || other.contains(newMin), false);
			}
		} else if (min == null && max != null) {
			if (otherMin != null) {
				int k = max.compareTo(otherMin);
				if (k < 0)
					return null;
				if (k == 0 && !(isIncludeMax() || other.isIncludeMin()))
					return null;
			}

			T newMax = otherMax == null ? null : max(max, otherMax);
			return createRange(null, newMax, false, newMax != null
					&& (contains(newMax) || other.contains(newMax)));
		} else if (min != null && max == null) {
			if (otherMax != null) {
				int k = otherMax.compareTo(min);
				if (k < 0)
					return null;
				if (k == 0 && !(isIncludeMin() || other.isIncludeMax()))
					return null;
			}

			T newMin = otherMin == null ? null : min(min, otherMin);
			return createRange(newMin, null, newMin != null
					&& (contains(newMin) || other.contains(newMin)), false);
		}

		// this should be unreachable
		throw new IllegalStateException();
	}

	/**
	 * Return true if this Range contains the value provided.
	 */
	public boolean contains(T value) {
		T min = getMin();
		T max = getMax();

		if (min == null && max == null)
			return true;
		if (min == null && max != null) {
			int k = value.compareTo(max);
			if (k < 0 || (k == 0 && isIncludeMax()))
				return true;
		} else if (min != null && max == null) {
			int k = min.compareTo(value);
			if (k < 0 || (k == 0 && isIncludeMin()))
				return true;
		} else {
			int k1 = min.compareTo(value);
			if (k1 < 0 || (k1 == 0 && isIncludeMin())) {
				int k2 = value.compareTo(max);
				if (k2 < 0 || (k2 == 0 && isIncludeMax()))
					return true;
			}
		}

		return false;
	}

	private T min(T x, T y) {
		int k = x.compareTo(y);
		if (k < 0)
			return x;
		return y;
	}

	private T max(T x, T y) {
		int k = x.compareTo(y);
		if (k > 0)
			return x;
		return y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getMin(), getMax());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Range))
			return false;
		Range<?> other = (Range<?>) obj;
		if (!Objects.equals(getMin(), other.getMin()))
			return false;
		if (!Objects.equals(getMax(), other.getMax()))
			return false;
		if (getMin() != null && isIncludeMin() != other.isIncludeMin())
			return false;
		if (getMax() != null && isIncludeMax() != other.isIncludeMax())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return toString("x");
	}

	/**
	 * Return a String representation of this Range that refers to the variable
	 * name provided. The default {@link #toString()} implementation uses "x",
	 * so it may produce values like "x < 50". This method let's you replace "x"
	 * with any identifying String.
	 */
	public String toString(String varName) {
		if (getMin() == null && getMax() == null)
			return "undefined";

		StringBuilder sb = new StringBuilder();
		if (getMin() != null) {
			sb.append(getMin().toString());
			sb.append(isIncludeMin() ? " <= " : " < ");
		}
		sb.append(varName);
		if (getMax() != null) {
			sb.append(isIncludeMax() ? " <= " : " < ");
			sb.append(getMax().toString());
		}
		return sb.toString();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
		out.writeObject(getMin());
		out.writeObject(getMax());
		out.writeBoolean(isIncludeMin());
		out.writeBoolean(isIncludeMax());
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			min = (T) in.readObject();
			max = (T) in.readObject();
			includeMin = in.readBoolean();
			includeMax = in.readBoolean();
		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}
}