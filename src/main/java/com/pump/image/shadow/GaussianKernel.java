package com.pump.image.shadow;

import java.io.IOException;
import java.io.Serializable;

/**
 * This is a 1-dimension kernel for Gaussian blurs.
 */
public class GaussianKernel
		implements Comparable<GaussianKernel>, Serializable {
	private static final long serialVersionUID = 1L;

	private int[] data;
	private int sum;
	private int size;

	public GaussianKernel(int kernelSize) {
		initialize(kernelSize);
	}

	private void initialize(int kernelSize) {
		size = kernelSize;
		data = new int[2 * kernelSize + 1];
		double sigma = kernelSize / 3.0;
		double k = 1f / (2 * Math.PI * sigma * sigma);

		int y = data.length / 2;
		sum = 0;
		for (int b = 0; b < data.length; b++) {
			int x = b - kernelSize;

			double exp = -(x * x + y * y) / (2 * sigma * sigma);
			double z = k * Math.pow(Math.E, exp);
			data[b] = (int) (z * 1000000);
			sum += data[b];
		}
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GaussianKernel[ ");
		for (int a = 0; a < data.length; a++) {
			if (a != 0)
				sb.append(", ");
			sb.append(data[a]);
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public int compareTo(GaussianKernel o) {
		return Integer.compare(size, o.size);
	}

	@Override
	public int hashCode() {
		return size;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GaussianKernel))
			return false;
		GaussianKernel other = (GaussianKernel) obj;
		return compareTo(other) == 0;
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeInt(size);
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			initialize(in.readInt());
		} else {
			throw new IOException("unsupported internal version " + version);
		}
	}

}