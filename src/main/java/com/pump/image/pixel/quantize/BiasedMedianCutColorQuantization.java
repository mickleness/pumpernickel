/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.pixel.quantize;

import java.util.Arrays;
import java.util.Map;

import com.pump.image.pixel.quantize.ColorSet.FrequencyComparator;

/**
 * This is a modified <code>MedianCutColorQuantization</code>.
 * <p>
 * Before applying the median cut algorithm: this sorts all colors by frequency.
 * Then, given a fixed threshold, it evaluates which colors occur so frequently
 * that they should be preserved exactly as-is.
 * <p>
 * For example: if the color white makes up 15% of the pixels, and you pass a
 * threshold such as .1 (10%), then the color white will be preserved in the
 * reduced palette.
 * <p>
 * Without this precaution: important colors may still be averaged with other
 * colors in a cluster.
 * <p>
 * (Previous revisions of this class include attempts to offer additional biases
 * (based on human perception of color), but those experiments were abandoned
 * after no discernible improvement was made.)
 * 
 */
public class BiasedMedianCutColorQuantization extends ColorQuantization {

	final float pureColorThreshold;

	/**
	 * Create a <code>BiasedMedianCutColorQuantization</code> with a threshold
	 * of .1 (10%).
	 * 
	 */
	public BiasedMedianCutColorQuantization() {
		this(.1f);
	}

	/**
	 * Create a <code>BiasedMedianCutColorQuantization</code> with a fixed
	 * threshold.
	 * 
	 * @param pureColorThreshold
	 *            a fractional value (from 0-1) representing the cut-off point
	 *            for a color to be considered so important that it is added to
	 *            the finished set as-is. A recommended value for this is around
	 *            .1 (10%).
	 */
	public BiasedMedianCutColorQuantization(float pureColorThreshold) {
		this.pureColorThreshold = pureColorThreshold;
	}

	@Override
	public ColorSet createReducedSet(ColorSet originalSet,
			int maximumColorCount, boolean retainOriginalIntegrity) {
		Map<Integer, Integer> frequencyMap = originalSet
				.getRGBtoFrequencyMap(false);
		long pixelCount = originalSet.getPixelCount();

		ColorSet newGuy = new ColorSet();

		/*
		 * Process all the colors that occur above the pureColorThreshold:
		 */
		ColorSet remainingColors;
		if (pureColorThreshold > 0) {
			Integer[] rgb = frequencyMap.keySet().toArray(
					new Integer[frequencyMap.size()]);
			FrequencyComparator comparator = new FrequencyComparator(
					originalSet, false);
			Arrays.sort(rgb, comparator);

			if (retainOriginalIntegrity) {
				remainingColors = new ColorSet();
				for (int a = 0; a < rgb.length; a++) {
					int k = frequencyMap.get(rgb[a]);
					double frequencyFraction = ((double) k)
							/ ((double) pixelCount);
					if (frequencyFraction >= pureColorThreshold) {
						newGuy.addColor((rgb[a] >> 16) & 0xff,
								(rgb[a] >> 8) & 0xff, (rgb[a] >> 0) & 0xff, k);
					} else {
						remainingColors.addColor((rgb[a] >> 16) & 0xff,
								(rgb[a] >> 8) & 0xff, (rgb[a] >> 0) & 0xff, k);
					}
				}
			} else {
				remainingColors = originalSet;
				identifyThresholdColors: for (int a = 0; a < rgb.length; a++) {
					int k = frequencyMap.get(rgb[a]);
					double frequencyFraction = ((double) k)
							/ ((double) pixelCount);
					if (frequencyFraction >= pureColorThreshold) {
						newGuy.addColor((rgb[a] >> 16) & 0xff,
								(rgb[a] >> 8) & 0xff, (rgb[a] >> 0) & 0xff, k);
						frequencyMap.remove(rgb[a]);
					} else {
						break identifyThresholdColors;
					}
				}
			}
		} else {
			remainingColors = originalSet;
		}

		MedianCutColorQuantization m = new MedianCutColorQuantization();
		newGuy.addColors(m.createReducedSet(remainingColors, maximumColorCount
				- newGuy.getColorCount(), remainingColors == originalSet
				&& retainOriginalIntegrity));

		return newGuy;
	}
}