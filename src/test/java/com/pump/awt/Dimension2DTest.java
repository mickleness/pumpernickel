package com.pump.awt;

import java.awt.Dimension;

import org.junit.Test;

import junit.framework.TestCase;

public class Dimension2DTest extends TestCase {

	@Test
	public void testScaleProportionally_exactMatch() {
		Dimension s = Dimension2D.scaleProportionally(new Dimension(400, 300),
				new Dimension(80, 60), true);
		assertEquals(80, s.width);
		assertEquals(60, s.height);
	}

	@Test
	public void testScaleProportionally_landscape() {
		Dimension s = Dimension2D.scaleProportionally(new Dimension(400, 200),
				new Dimension(80, 60), true);
		assertEquals(80, s.width);
		assertEquals(40, s.height);
	}

	@Test
	public void testScaleProportionally_portrait() {
		Dimension s = Dimension2D.scaleProportionally(new Dimension(200, 400),
				new Dimension(80, 60), true);
		assertEquals(30, s.width);
		assertEquals(60, s.height);
	}

	@Test
	public void testScaleProportionally_scaleUp_returnNull() {
		Dimension s = Dimension2D.scaleProportionally(new Dimension(16, 16),
				new Dimension(128, 128), true);
		assertNull(s);
	}

	@Test
	public void testScaleProportionally_scaleUp_returnNonNull() {
		Dimension s = Dimension2D.scaleProportionally(new Dimension(16, 16),
				new Dimension(128, 128), false);
		assertEquals(128, s.width);
		assertEquals(128, s.height);
	}

}
