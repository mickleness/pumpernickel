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

import java.util.Arrays;
import java.util.Map;

import com.pump.image.pixel.quantize.ColorSet.RGBChannelComparator;

/**
 * This is a <a href="http://en.wikipedia.org/wiki/Median_cut">median cut color
 * reduction algorithm</a>.
 * <P>
 * This treats colors as points in 3-dimensional RGB space. The list of points
 * is sorted by its red value. Then this list is split in half. Each half is
 * sorted its green value, and then those lists are cut in half. Each new half
 * is sorted by its blue value. Then cut in half again, and we start again: red,
 * green, and blue. In this way we isolate clusters of colors with similar
 * values. So if we want to reduce a color set to <i>k</i> colors, then we
 * simply identify <i>k</i>-many clusters, and take the average RGB value of
 * each cluster.
 * </P>
 * <P>
 * When we average the RGB values we take into account the frequency of each
 * color value. That is: if there are 100 occurrences of red of 1 occurrence of
 * orange in a cluster, then the red will carry 100 times more weight when we
 * calculate the average.
 *
 */
public class MedianCutColorQuantization extends ColorQuantization {
	private static RGBChannelComparator redComparator = new RGBChannelComparator(
			16, 8, 0);
	private static RGBChannelComparator greenComparator = new RGBChannelComparator(
			8, 0, 16);
	private static RGBChannelComparator blueComparator = new RGBChannelComparator(
			0, 16, 8);

	private static RGBChannelComparator[] comparators = new RGBChannelComparator[] {
			redComparator, greenComparator, blueComparator };

	@Override
	public ColorSet createReducedSet(ColorSet originalSet,
			int maximumColorCount, boolean retainOriginalIntegrity) {
		if (maximumColorCount < 2)
			throw new IllegalArgumentException("maximumColorCount ("
					+ maximumColorCount + ") must be 2 or greater");
		if (originalSet.getColorCount() <= maximumColorCount) {
			return originalSet;
		}
		ColorSet newGuy = new ColorSet();
		Map<Integer, Integer> frequencyMap = originalSet
				.getRGBtoFrequencyMap(false);
		Integer[] rgb = frequencyMap.keySet().toArray(
				new Integer[originalSet.tree.size()]);
		split(rgb, frequencyMap, 0, rgb.length, maximumColorCount, 0, newGuy);
		return newGuy;
	}

	private long split(Integer[] rgb, Map<Integer, Integer> rgbToFrequencyMap,
			int ctr, int len, int count, int comparatorIndex, ColorSet dest) {
		if (count == 1) {
			long r = 0;
			long g = 0;
			long b = 0;
			int pixelSum = 0;
			int k = ctr + len;
			for (int a = ctr; a < k; a++) {
				int z = rgbToFrequencyMap.get(rgb[a]);
				r += ((rgb[a] >> 16) & 0xff) * z;
				g += ((rgb[a] >> 8) & 0xff) * z;
				b += ((rgb[a] >> 0) & 0xff) * z;
				pixelSum += z;
			}
			r = r / pixelSum;
			g = g / pixelSum;
			b = b / pixelSum;
			dest.addColor((int) r, (int) g, (int) b, pixelSum);
			return pixelSum;
		}
		Arrays.sort(rgb, ctr, ctr + len, comparators[comparatorIndex]);
		comparatorIndex = (comparatorIndex + 1) % 3;
		int leftHalfCount = count / 2;
		int rightHalfCount = count - leftHalfCount;
		int leftLen = len / 2;
		long sum = split(rgb, rgbToFrequencyMap, ctr, leftLen, leftHalfCount,
				comparatorIndex, dest);
		sum += split(rgb, rgbToFrequencyMap, ctr + leftLen, len - leftLen,
				rightHalfCount, comparatorIndex, dest);
		return sum;
	}
}