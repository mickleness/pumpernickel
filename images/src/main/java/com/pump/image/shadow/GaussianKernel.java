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
package com.pump.image.shadow;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * This is a 1-dimension kernel for Gaussian blurs.
 */
public class GaussianKernel
		implements Comparable<GaussianKernel>, Serializable {

	private static int[] tween(int[] array1, int[] array2, float fraction) {
		if (array1.length != array2.length)
			throw new IllegalArgumentException("array1.length = "
					+ array1.length + ", array2.length = " + array2.length);
		int[] returnValue = new int[array1.length];
		for (int a = 0; a < array1.length; a++) {
			returnValue[a] = (int) (array1[a] * (1 - fraction)
					+ array2[a] * fraction);
		}
		return returnValue;
	}

	/**
	 * Grow an int array so the left & right side are padded with zeroes.
	 */
	private static int[] grow(int[] array, int newArrayLength) {
		int[] returnValue = new int[newArrayLength];
		System.arraycopy(array, 0, returnValue,
				(returnValue.length - array.length) / 2, array.length);
		return returnValue;
	}

	@Serial
	private static final long serialVersionUID = 1L;

	private int[] data;
	private int sum;

	/**
	 * Create a kernel with a default bell-shaped distribution.
	 * 
	 * @param shadowKernelRadius
	 *            the radius of the kernel. The total length of this kernel will
	 *            be (2*radius + 1)
	 */
	public GaussianKernel(float shadowKernelRadius) {
		this(shadowKernelRadius, true);
	}

	/**
	 * Create a kernel with a default bell-shaped distribution.
	 * 
	 * @param kernelRadius
	 *            the radius of the kernel. The total length of this kernel will
	 *            be (2*radius + 1)
	 * @param condense
	 *            if true then leading/trailing zeroes will be purged. This
	 *            should always be true, but it is left as an option for
	 *            testing/comparison.
	 */
	public GaussianKernel(float kernelRadius, boolean condense) {
		initialize(kernelRadius, condense);
	}

	/**
	 * Create a kernel based on a specific int array.
	 * 
	 * @param kernelData
	 *            the entire kernel, which must be an odd number of elements.
	 */
	public GaussianKernel(int[] kernelData) {
		Objects.requireNonNull(kernelData);
		if (kernelData.length % 2 == 0)
			throw new IllegalArgumentException(
					"The kernel array must have an odd number of elements.");
		data = new int[kernelData.length];
		System.arraycopy(kernelData, 0, data, 0, data.length);
		updateSum();
	}

	private void updateSum() {
		sum = 0;
		for (int datum : data) {
			sum += datum;
		}
	}

	private void initialize(float kernelSize, boolean condense) {
		if (kernelSize < 0)
			throw new IllegalArgumentException(
					"kernel size (" + kernelSize + ") must be zero or greater");

		int lowerKernelBound = (int) kernelSize;
		int upperKernelBound = lowerKernelBound + 1;
		float fraction = kernelSize - lowerKernelBound;

		if (fraction == 0) {
			data = getKernel(lowerKernelBound);
		} else {
			int[] upperArray = getKernel(upperKernelBound);
			int[] lowerArray = grow(getKernel(lowerKernelBound),
					upperArray.length);

			data = tween(lowerArray, upperArray, fraction);
		}

		if (condense)
			data = condense(data);

		updateSum();
	}

	/**
	 * Remove leading/trailing zeroes from an array.
	 */
	private static int[] condense(int[] data) {
		int zeroCtr = 0;
		for (int a = 0; a < data.length / 2; a++) {
			if (data[a] == 0) {
				zeroCtr++;
			} else {
				break;
			}
		}
		if (zeroCtr == 0)
			return data;
		int[] newArray = new int[data.length - zeroCtr * 2];
		System.arraycopy(data, zeroCtr, newArray, 0, newArray.length);

		return newArray;
	}

	private int[] getKernel(int kernelSize) {
		if (kernelSize == 0) {
			return new int[] { 1 };
		}
		int[] returnValue = new int[2 * kernelSize + 1];
		double sigma = kernelSize / 3.0;
		double k = 1f / (2 * Math.PI * sigma * sigma);

		int y = returnValue.length / 2;
		for (int b = 0; b < returnValue.length; b++) {
			int x = b - kernelSize;

			double exp = -(x * x + y * y) / (2 * sigma * sigma);
			double z = k * Math.pow(Math.E, exp);
			returnValue[b] = (int) (z * 1000000);
		}
		return returnValue;
	}

	/**
	 * Return the cell values of this kernel.
	 */
	public int[] getArray() {
		int[] copy = new int[data.length];
		System.arraycopy(data, 0, copy, 0, data.length);
		return copy;
	}

	/**
	 * Return the sum of all values in this kernel.
	 */
	public int getArraySum() {
		return sum;
	}

	/**
	 * Return the kernel radius, which is <code>(getArray().length - 1)/2</code>
	 */
	public int getKernelRadius() {
		return (data.length - 1) / 2;
	}

	@Override
	public String toString() {
		return "GaussianKernel" +
				toString(data);
	}

	static String toString(int[] array) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ ");
		for (int a = 0; a < array.length; a++) {
			if (a != 0)
				sb.append(", ");
			sb.append(array[a]);
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int compareTo(GaussianKernel o) {
		int[] array1 = getArray();
		int[] array2 = o.getArray();
		int max = Math.max(array1.length, array2.length);
		if (max != array1.length)
			array1 = grow(array1, max);
		if (max != array2.length)
			array2 = grow(array2, max);
		for (int a = 0; a < max; a++) {
			int k = Integer.compare(array1[a], array2[a]);
			if (k != 0)
				return k;
		}
		return 0;

	}

	@Override
	public int hashCode() {
		int j = 0;
		for (int datum : data) {
			j = (j << 2) + datum;
		}
		return j;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GaussianKernel other))
			return false;
		return compareTo(other) == 0;
	}

	@Serial
	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeObject(data);
	}

	@Serial
	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			data = (int[]) in.readObject();
			updateSum();
		} else {
			throw new IOException("unsupported internal version " + version);
		}
	}
}