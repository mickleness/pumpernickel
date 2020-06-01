package com.pump.image.shadow;

public class GaussianKernel {
	public final int[] data;
	public final int sum;

	public GaussianKernel(int kernelSize) {
		data = new int[2 * kernelSize + 1];
		double sigma = kernelSize / 3.0;
		double k = 1f / (2 * Math.PI * sigma * sigma);

		int y = data.length / 2;
		int s = 0;
		for (int b = 0; b < data.length; b++) {
			int x = b - kernelSize;

			double exp = -(x * x + y * y) / (2 * sigma * sigma);
			double z = k * Math.pow(Math.E, exp);
			data[b] = (int) (z * 1000000);
			s += data[b];
		}
		sum = s;
	}
}