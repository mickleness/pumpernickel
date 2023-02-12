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
package com.pump.util;

import java.awt.Point;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This creates a "square spiral" of points that traverse right, down, left, up,
 * etc. This guarantees increasing coverage radiating out from an initial point,
 * but because it is boxy (and not circular) the distances of the incoming
 * points are not always in increasing order.
 */
public class PointIterator implements Iterator<Point> {

	/**
	 * This identifies the point the PointIterator will return for a given
	 * iteration.
	 * <p>
	 * That is: in this example p1 and p2 are always equal: <code>
	 * PointIterator iter = new PointIterator(0, 0, 1_000_000);
	 * int ctr = 0;
	 * while (iter.hasNext()) {
	 *   Point p1 = iter.next();
	 *   Point p2 = PointIterator.getPoint(ctr);
	 *   ctr++;
	 * }
	 * </code>
	 */
	public static Point getPoint(int iteration) {
		double sqrt = Math.sqrt(iteration);
		int q = (int) Math.floor(sqrt);

		int j = iteration - q * q;

		if (q % 2 == 1) {
			if (j <= q)
				return new Point(1 + (q - 1) / 2, -(q - 1) / 2 + j);

			return new Point(1 + (q - 1) / 2 - (j - q), (q - 1) / 2 + 1);
		} else {
			if (j <= q)
				return new Point(-q / 2, q / 2 - j);
			return new Point(-q / 2 + (j - q), -q / 2);
		}
	}

	private static final int RIGHT = 0;
	private static final int DOWN = 1;
	private static final int LEFT = 2;
	private static final int UP = 3;

	private int iterations;
	private int x, y;

	int dir = UP;
	int stepCtr = 0;
	int stepMax = 0;

	public PointIterator(int x, int y, int iterations) {
		this.x = x;
		this.y = y + 1;
		this.iterations = iterations;
	}

	@Override
	public boolean hasNext() {
		return iterations > 0;
	}

	@Override
	public Point next() {
		iterations--;
		if (iterations < 0)
			throw new NoSuchElementException();

		if (dir == RIGHT) {
			x++;
		} else if (dir == DOWN) {
			y++;
		} else if (dir == LEFT) {
			x--;
		} else if (dir == UP) {
			y--;
		}

		stepCtr++;
		if (stepCtr >= stepMax) {
			stepCtr = 0;
			dir = (dir + 1) % 4;
			if (dir % 2 == 0)
				stepMax++;
		}

		return new Point(x, y);
	}

}