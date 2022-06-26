package com.pump.awt;

import java.awt.Color;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

public class GradientStopHelperTest extends TestCase {
	//@formatter:off
	private List<Map.Entry<Float,Color>> createEntries() {
		return Arrays.asList(new AbstractMap.SimpleEntry<Float, Color>(0f, Color.red),
					new AbstractMap.SimpleEntry<Float, Color>(.1f, Color.orange),
					new AbstractMap.SimpleEntry<Float, Color>(.2f, Color.yellow),
					new AbstractMap.SimpleEntry<Float, Color>(.3f, Color.green),
					new AbstractMap.SimpleEntry<Float, Color>(.4f, Color.cyan),
					new AbstractMap.SimpleEntry<Float, Color>(.5f, Color.blue) );
	}//@formatter:on

	/**
	 * This tests that as we add stops all the elements are correctly sorted.
	 */
	public void testAddStop_sorted() {
		for (int randomSeed = 0; randomSeed < 10000; randomSeed++) {
			try {
				testAddStop_sorted(randomSeed);
			} catch (Throwable t) {
				System.err.println("random seed = " + randomSeed);
				throw t;
			}
		}
	}

	private void testAddStop_sorted(int randomSeed) {
		Random random = new Random(randomSeed);
		List<Map.Entry<Float, Color>> entries = createEntries();
		Collections.shuffle(entries, random);

		GradientStopHelper h = new GradientStopHelper();
		for (Map.Entry<Float, Color> e : entries) {
			h.addStop(e.getKey(), e.getValue());
		}

		List<Map.Entry<Float, Color>> sortedEntries = createEntries();
		for (int a = 0; a < h.size(); a++) {
			assertEquals(sortedEntries.get(a).getKey(), h.getStop(a));
			assertEquals(sortedEntries.get(a).getValue(), h.getColor(a));
		}
	}

	/**
	 * Make sure if a stop is before zero that we interpolate it and truncate
	 * the stops so they are within [0,1].
	 */
	public void testAddStop_beforeZero() {
		GradientStopHelper h = new GradientStopHelper();
		h.addStop(1, Color.white);
		h.addStop(-2, Color.black);
		assertEquals(0f, h.getStop(0));
		assertEquals(1f, h.getStop(1));
		assertEquals(new Color(170, 170, 170), h.getColor(0));
		assertEquals(Color.white, h.getColor(1));
	}

	/**
	 * Make sure if a stop is after one that we interpolate it and truncate the
	 * stops so they are within [0,1].
	 */
	public void testAddStop_afterOne() {
		GradientStopHelper h = new GradientStopHelper();
		h.addStop(0, Color.white);
		h.addStop(4, Color.black);
		assertEquals(0f, h.getStop(0));
		assertEquals(1f, h.getStop(1));
		assertEquals(Color.white, h.getColor(0));
		assertEquals(new Color(191, 191, 191), h.getColor(1));
	}

	/**
	 * Make sure if a stop is before zero and after one that we interpolate it
	 * and truncate the stops so they are within [0,1].
	 */
	public void testAddStop_beforeAndAfter() {
		GradientStopHelper h = new GradientStopHelper();
		h.addStop(-1, Color.white);
		h.addStop(4, Color.black);
		assertEquals(0f, h.getStop(0));
		assertEquals(1f, h.getStop(1));
		assertEquals(new Color(203, 203, 203), h.getColor(0));
		assertEquals(new Color(152, 152, 152), h.getColor(1));
	}

}
