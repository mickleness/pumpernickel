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
package com.pump.image.pixel.quantize;

import java.awt.image.IndexColorModel;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is a tool to help reduce the time it takes to map an arbitrary color to
 * its closest match(es) in limited color set.
 * <p>
 * Internally: this breaks up the 3D color cube into hundreds of smaller
 * subcubes. So instead of searching through all possible colors every time we
 * require a match: we hone in on the exact subcube and iterate outward a cycle
 * or two.
 */
public class ColorLUT {
	public static class ColorNode implements Comparable<ColorNode> {
		/** The red component of a color (0-255) */
		public final int red;
		/** The green component of a color (0-255) */
		public final int green;
		/** The blue component of a color (0-255) */
		public final int blue;
		/**
		 * The optional index of this color in an <code>IndexColorModel</code>.
		 * If not available, then this will be -1.
		 */
		public final int index;

		ColorNode(int r, int g, int b, int index) {
			red = r;
			green = g;
			blue = b;
			this.index = index;
		}

		public int compareTo(ColorNode o) {
			if (red < o.red)
				return -1;
			if (red > o.red)
				return 1;
			if (green < o.green)
				return -1;
			if (green > o.green)
				return 1;
			if (blue < o.blue)
				return -1;
			if (blue > o.blue)
				return 1;
			if (index < o.index)
				return -1;
			if (index > o.index)
				return 1;
			return 0;
		}

		@Override
		public String toString() {
			return "(" + red + ", " + green + ", " + blue + ", " + index + ")";
		}
	}

	/** A subcube in the 3D color space. */
	private static class Cluster {
		final int minR, maxR, minG, maxG, minB, maxB;
		ColorNode[] nodes = new ColorNode[] {};

		Cluster(int minR, int minG, int minB, int range) {
			this.minR = minR;
			this.maxR = minR + range - 1;
			this.minG = minG;
			this.maxG = minG + range - 1;
			this.minB = minB;
			this.maxB = minB + range - 1;
		}

		@Override
		public int hashCode() {
			return minR << 16 + minG << 8 + minB;
		}

		public Match getMatch(Match bestMatch, int red, int green, int blue) {
			for (ColorNode n : nodes) {
				int dr = red - n.red;
				int dg = green - n.green;
				int db = blue - n.blue;
				int errorSquared = dr * dr + dg * dg + db * db;
				if (bestMatch == null || errorSquared < bestMatch.errorSquared) {
					bestMatch = new Match(n, errorSquared);
				}
			}
			return bestMatch;
		}

		public void getMatches(Set<Match> dest, int red, int green, int blue,
				int maxErrorSquared, boolean includeExact) {
			for (ColorNode n : nodes) {
				int dr = red - n.red;
				int dg = green - n.green;
				int db = blue - n.blue;
				int errorSquared = dr * dr + dg * dg + db * db;
				if (errorSquared <= maxErrorSquared) {
					if (errorSquared != 0 || includeExact) {
						dest.add(new Match(n, errorSquared));
					}
				}
			}
		}

		public void add(int red, int green, int blue, int index) {
			ColorNode n = new ColorNode(red, green, blue, index);
			ColorNode[] newArray = new ColorNode[nodes.length + 1];
			System.arraycopy(nodes, 0, newArray, 0, nodes.length);
			newArray[newArray.length - 1] = n;

			nodes = newArray;
		}
	}

	int divisions = 8;
	int span = 256 / divisions;
	Cluster[][][] subcubes = new Cluster[divisions][divisions][divisions];
	IndexColorModel indexColorModel;

	/** Create a ColorLUT where every color has an index of -1. */
	public ColorLUT(Integer[] rgb) {
		for (int a = 0; a < rgb.length; a++) {
			int r = (rgb[a] >> 16) & 0xff;
			int g = (rgb[a] >> 8) & 0xff;
			int b = (rgb[a]) & 0xff;
			addRGB(r, g, b, -1);
		}
	}

	/** Create a ColorLUT from a <code>IndexColorModel</code>. */
	public ColorLUT(IndexColorModel icm) {
		this.indexColorModel = icm;

		for (int a = 0; a < icm.getMapSize(); a++) {
			if (a != icm.getTransparentPixel()) {
				int red = icm.getRed(a);
				int green = icm.getGreen(a);
				int blue = icm.getBlue(a);
				addRGB(red, green, blue, a);
			}
		}
	}

	/**
	 * Returns the IndexColorModel used to create this <code>ColorLUT</code>, or
	 * <code>null</code> if this was not created with an
	 * <code>IndexColorModel</code>.
	 */
	public IndexColorModel getIndexColorModel() {
		return indexColorModel;
	}

	private void addRGB(int red, int green, int blue, int index) {
		int i1 = red / span;
		int i2 = green / span;
		int i3 = blue / span;
		if (subcubes[i1][i2][i3] == null) {
			subcubes[i1][i2][i3] = new Cluster(i1 * span, i2 * span, i3 * span,
					span - 1);
		}
		subcubes[i1][i2][i3].add(red, green, blue, index);
	}

	/** A response to a query for approximate matches. */
	public static class Match implements Comparable<Match> {
		/** The color this match identified. */
		public final ColorNode node;

		/** The square of the error between this node and the requested color. */
		public final int errorSquared;

		Match(ColorNode node, int errorSquared) {
			this.node = node;
			this.errorSquared = errorSquared;
		}

		public int compareTo(Match o) {
			if (errorSquared < o.errorSquared)
				return -1;
			if (errorSquared > o.errorSquared)
				return 1;
			return node.compareTo(o.node);
		}
	}

	public int getIndexMatch(int red, int green, int blue) {
		Match match = getMatch(red, green, blue);
		return match.node.index;
	}

	private static Set<Match> scratchMatches = new TreeSet<Match>();

	public Match[] getSomeMatches(int red, int green, int blue,
			int maxErrorSquared, boolean includeExact) {
		int i1 = red / span;
		int i2 = green / span;
		int i3 = blue / span;

		synchronized (scratchMatches) {
			scratchMatches.clear();
			if (subcubes[i1][i2][i3] != null) {
				subcubes[i1][i2][i3].getMatches(scratchMatches, red, green,
						blue, maxErrorSquared, includeExact);
			}

			return scratchMatches.toArray(new Match[scratchMatches.size()]);
		}
	}

	private static Set<Cluster> outerRing = new HashSet<Cluster>();

	public Match getMatch(int red, int green, int blue) {
		int i1 = red / span;
		int i2 = green / span;
		int i3 = blue / span;
		Match bestMatch = null;

		if (subcubes[i1][i2][i3] != null) {
			bestMatch = subcubes[i1][i2][i3].getMatch(null, red, green, blue);
		}

		/*
		 * As you can see: this starts with a cell and iterates outwards with a
		 * cube. This might fail to identify the best match in fringe cases
		 * (consider a square embedded inside a circle), but it should be
		 * generally accurate.
		 */

		for (int d = 1; d <= divisions; d++) {
			if (bestMatch != null && bestMatch.errorSquared == 0)
				return bestMatch;
			boolean lastSweep = bestMatch != null;
			synchronized (outerRing) {
				collectPlane(outerRing, i1, i2, i3 + d, -d, d, -d, d, 0, 0);
				collectPlane(outerRing, i1, i2, i3 - d, -d, d, -d, d, 0, 0);
				collectPlane(outerRing, i1, i2 + d, i3, -d, d, 0, 0, -d, d);
				collectPlane(outerRing, i1, i2 - d, i3, -d, d, 0, 0, -d, d);
				collectPlane(outerRing, i1 + d, i2, i3, 0, 0, -d, d, -d, d);
				collectPlane(outerRing, i1 - d, i2, i3, 0, 0, -d, d, -d, d);

				for (Cluster c : outerRing) {
					bestMatch = c.getMatch(bestMatch, red, green, blue);
				}
				outerRing.clear();
			}
			if (lastSweep)
				return bestMatch;
		}
		if (bestMatch != null)
			return bestMatch;

		throw new RuntimeException("unexpected condition: regressed "
				+ divisions + " times with no matches");
	}

	private void collectPlane(Set<Cluster> dest, int i1, int i2, int i3,
			int i1a, int i1b, int i2a, int i2b, int i3a, int i3b) {
		for (int i = i1 + i1a; i <= i1 + i1b; i++) {
			for (int j = i2 + i2a; j <= i2 + i2b; j++) {
				for (int k = i3 + i3a; k <= i3 + i3b; k++) {
					if (i >= 0 && i < divisions && j >= 0 && j < divisions
							&& k >= 0 && k < divisions) {
						Cluster c = subcubes[i][j][k];
						if (c != null)
							dest.add(c);
					}
				}
			}
		}
	}
}