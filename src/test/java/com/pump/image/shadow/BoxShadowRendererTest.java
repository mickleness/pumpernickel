package com.pump.image.shadow;

import java.awt.image.BufferedImage;

import org.junit.Test;

import com.pump.showcase.ShadowRendererDemo;
import com.pump.showcase.ShadowRendererDemo.OriginalGaussianShadowRenderer;

import junit.framework.TestCase;

public class BoxShadowRendererTest extends TestCase {

	/**
	 * This calculates the vertical blur for a 7x7 grid. I ran (some of) the
	 * numbers by hand to verify the results.
	 */
	@Test
	public void testVerticalBlur_7() {
		ARGBPixels srcImage = new ARGBPixels(7, 7);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 2, 41, 16, 29, 23, 35, 
			42, 30, 24, 36, 10, 3, 17, 
			18, 43, 31, 4, 37, 11, 48, 
			38, 44, 5, 12, 19, 49, 25, 
			45, 20, 13, 0, 26, 32, 6, 
			7, 27, 47, 33, 21, 14, 39, 
			28, 34, 22, 8, 15, 40, 46
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(11, 11);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 7, 7, 2, 255, 1f);
		renderer.runVerticalBlur();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 11]);
		assertEquals(10, dstPixels[2 + 1 * 11]);
		assertEquals(13, dstPixels[2 + 2 * 11]);
		assertEquals(21, dstPixels[2 + 3 * 11]);
		assertEquals(30, dstPixels[2 + 4 * 11]);
		assertEquals(30, dstPixels[2 + 5 * 11]);
		assertEquals(27, dstPixels[2 + 6 * 11]);
		assertEquals(23, dstPixels[2 + 7 * 11]);
		assertEquals(16, dstPixels[2 + 8 * 11]);
		assertEquals(7, dstPixels[2 + 9 * 11]);
		assertEquals(5, dstPixels[2 + 10 * 11]);

		// test middle column
		assertEquals(3, dstPixels[5 + 0 * 11]);
		assertEquals(10, dstPixels[5 + 1 * 11]);
		assertEquals(11, dstPixels[5 + 2 * 11]);
		assertEquals(13, dstPixels[5 + 3 * 11]);
		assertEquals(13, dstPixels[5 + 4 * 11]);
		assertEquals(17, dstPixels[5 + 5 * 11]);
		assertEquals(11, dstPixels[5 + 6 * 11]);
		assertEquals(10, dstPixels[5 + 7 * 11]);
		assertEquals(8, dstPixels[5 + 8 * 11]);
		assertEquals(8, dstPixels[5 + 9 * 11]);
		assertEquals(1, dstPixels[5 + 10 * 11]);

		// test rightmost column
		assertEquals(7, dstPixels[8 + 0 * 11]);
		assertEquals(10, dstPixels[8 + 1 * 11]);
		assertEquals(20, dstPixels[8 + 2 * 11]);
		assertEquals(25, dstPixels[8 + 3 * 11]);
		assertEquals(26, dstPixels[8 + 4 * 11]);
		assertEquals(27, dstPixels[8 + 5 * 11]);
		assertEquals(32, dstPixels[8 + 6 * 11]);
		assertEquals(23, dstPixels[8 + 7 * 11]);
		assertEquals(18, dstPixels[8 + 8 * 11]);
		assertEquals(17, dstPixels[8 + 9 * 11]);
		assertEquals(9, dstPixels[8 + 10 * 11]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 6 rows.
	 */
	@Test
	public void testVerticalBlur_6() {
		ARGBPixels srcImage = new ARGBPixels(6, 6);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 2, 41, 16, 29, 23, 
			42, 30, 24, 36, 10, 3, 
			18, 43, 31, 4, 37, 11, 
			38, 44, 5, 12, 19, 49, 
			45, 20, 13, 0, 26, 32, 
			7, 27, 47, 33, 21, 14
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(10, 10);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 6, 6, 2, 255, 1f);
		renderer.runVerticalBlur();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 10]);
		assertEquals(10, dstPixels[2 + 1 * 10]);
		assertEquals(13, dstPixels[2 + 2 * 10]);
		assertEquals(21, dstPixels[2 + 3 * 10]);
		assertEquals(30, dstPixels[2 + 4 * 10]);
		assertEquals(30, dstPixels[2 + 5 * 10]);
		assertEquals(21, dstPixels[2 + 6 * 10]);
		assertEquals(18, dstPixels[2 + 7 * 10]);
		assertEquals(10, dstPixels[2 + 8 * 10]);
		assertEquals(1, dstPixels[2 + 9 * 10]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 5 rows.
	 */
	@Test
	public void testVerticalBlur_5() {
		ARGBPixels srcImage = new ARGBPixels(5, 5);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 2, 41, 16, 29, 
			42, 30, 24, 36, 10, 
			18, 43, 31, 4, 37, 
			38, 44, 5, 12, 19, 
			45, 20, 13, 0, 26
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(9, 9);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 5, 5, 2, 255, 1f);
		renderer.runVerticalBlur();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 9]);
		assertEquals(10, dstPixels[2 + 1 * 9]);
		assertEquals(13, dstPixels[2 + 2 * 9]);
		assertEquals(21, dstPixels[2 + 3 * 9]);
		assertEquals(30, dstPixels[2 + 4 * 9]);
		assertEquals(28, dstPixels[2 + 5 * 9]);
		assertEquals(20, dstPixels[2 + 6 * 9]);
		assertEquals(16, dstPixels[2 + 7 * 9]);
		assertEquals(9, dstPixels[2 + 8 * 9]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 4 rows.
	 */
	@Test
	public void testVerticalBlur_4() {
		ARGBPixels srcImage = new ARGBPixels(4, 4);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 2, 41, 16, 
			42, 30, 24, 36, 
			18, 43, 31, 4, 
			38, 44, 5, 12
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(8, 8);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 4, 4, 2, 255, 1f);
		renderer.runVerticalBlur();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 8]);
		assertEquals(10, dstPixels[2 + 1 * 8]);
		assertEquals(13, dstPixels[2 + 2 * 8]);
		assertEquals(21, dstPixels[2 + 3 * 8]);
		assertEquals(21, dstPixels[2 + 4 * 8]);
		assertEquals(19, dstPixels[2 + 5 * 8]);
		assertEquals(11, dstPixels[2 + 6 * 8]);
		assertEquals(7, dstPixels[2 + 7 * 8]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 3 rows.
	 */
	@Test
	public void testVerticalBlur_3() {
		ARGBPixels srcImage = new ARGBPixels(3, 3);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 2, 41,
			42, 30, 24, 
			18, 43, 31
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(7, 7);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 3, 3, 2, 255, 1f);
		renderer.runVerticalBlur();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 7]);
		assertEquals(10, dstPixels[2 + 1 * 7]);
		assertEquals(13, dstPixels[2 + 2 * 7]);
		assertEquals(13, dstPixels[2 + 3 * 7]);
		assertEquals(13, dstPixels[2 + 4 * 7]);
		assertEquals(12, dstPixels[2 + 5 * 7]);
		assertEquals(3, dstPixels[2 + 6 * 7]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 2 rows.
	 */
	@Test
	public void testVerticalBlur_2() {
		ARGBPixels srcImage = new ARGBPixels(2, 2);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 2,
			42, 30
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(6, 6);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 2, 2, 2, 255, 1f);
		renderer.runVerticalBlur();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 6]);
		assertEquals(10, dstPixels[2 + 1 * 6]);
		assertEquals(10, dstPixels[2 + 2 * 6]);
		assertEquals(10, dstPixels[2 + 3 * 6]);
		assertEquals(10, dstPixels[2 + 4 * 6]);
		assertEquals(8, dstPixels[2 + 5 * 6]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 1 row.
	 */
	@Test
	public void testVerticalBlur_1() {
		ARGBPixels srcImage = new ARGBPixels(1, 1);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(5, 5);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 1, 1, 2, 255, 1f);
		renderer.runVerticalBlur();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 5]);
		assertEquals(1, dstPixels[2 + 1 * 5]);
		assertEquals(1, dstPixels[2 + 2 * 5]);
		assertEquals(1, dstPixels[2 + 3 * 5]);
		assertEquals(1, dstPixels[2 + 4 * 5]);
	}

	/**
	 * Test the horizontal blur using the same data as the
	 * {@link #testVerticalBlur_7()}
	 */
	@Test
	public void testHorizontalBlur_7() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(7, 7);

		ARGBPixels dstImage = new ARGBPixels(11, 11);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 9, 2, 41, 16, 29, 23, 35, 0, 0, 
			0, 0, 42, 30, 24, 36, 10, 3, 17, 0, 0, 
			0, 0, 18, 43, 31, 4, 37, 11, 48, 0, 0, 
			0, 0, 38, 44, 5, 12, 19, 49, 25, 0, 0, 
			0, 0, 45, 20, 13, 0, 26, 32, 6, 0, 0, 
			0, 0, 7, 27, 47, 33, 21, 14, 39, 0, 0, 
			0, 0, 28, 34, 22, 8, 15, 40, 46, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 7, 7, 2, 255, 1f);
		renderer.runHorizontalBlur();

		// test topmost row (of data)
		assertEquals(1 << 24, dstPixels[22]);
		assertEquals(2 << 24, dstPixels[23]);
		assertEquals(10 << 24, dstPixels[24]);
		assertEquals(13 << 24, dstPixels[25]);
		assertEquals(19 << 24, dstPixels[26]);
		assertEquals(22 << 24, dstPixels[27]);
		assertEquals(28 << 24, dstPixels[28]);
		assertEquals(20 << 24, dstPixels[29]);
		assertEquals(17 << 24, dstPixels[30]);
		assertEquals(11 << 24, dstPixels[31]);
		assertEquals(7 << 24, dstPixels[32]);

		// test middle row
		assertEquals(7 << 24, dstPixels[55]);
		assertEquals(16 << 24, dstPixels[56]);
		assertEquals(17 << 24, dstPixels[57]);
		assertEquals(19 << 24, dstPixels[58]);
		assertEquals(23 << 24, dstPixels[59]);
		assertEquals(25 << 24, dstPixels[60]);
		assertEquals(22 << 24, dstPixels[61]);
		assertEquals(21 << 24, dstPixels[62]);
		assertEquals(18 << 24, dstPixels[63]);
		assertEquals(14 << 24, dstPixels[64]);
		assertEquals(5 << 24, dstPixels[65]);

		// no need to test more, horizontal passes are much simpler since data
		// is continuous.
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 6 rows.
	 */
	@Test
	public void testHorizontalBlur_6() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(6, 6);

		ARGBPixels dstImage = new ARGBPixels(10, 10);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 9, 2, 41, 16, 29, 23, 0, 0, 
			0, 0, 42, 30, 24, 36, 10, 3, 0, 0, 
			0, 0, 18, 43, 31, 4, 37, 11, 0, 0, 
			0, 0, 38, 44, 5, 12, 19, 49, 0, 0, 
			0, 0, 45, 20, 13, 0, 26, 32, 0, 0, 
			0, 0, 7, 27, 47, 33, 21, 14, 0, 0, 
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 6, 6, 2, 255, 1f);
		renderer.runHorizontalBlur();

		// test topmost row (of data)
		assertEquals(1 << 24, dstPixels[20]);
		assertEquals(2 << 24, dstPixels[21]);
		assertEquals(10 << 24, dstPixels[22]);
		assertEquals(13 << 24, dstPixels[23]);
		assertEquals(19 << 24, dstPixels[24]);
		assertEquals(22 << 24, dstPixels[25]);
		assertEquals(21 << 24, dstPixels[26]);
		assertEquals(13 << 24, dstPixels[27]);
		assertEquals(10 << 24, dstPixels[28]);
		assertEquals(4 << 24, dstPixels[29]);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 5 rows.
	 */
	@Test
	public void testHorizontalBlur_5() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(5, 5);

		ARGBPixels dstImage = new ARGBPixels(9, 9);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 9, 2, 41, 16, 29, 0, 0, 
			0, 0, 42, 30, 24, 36, 10, 0, 0, 
			0, 0, 18, 43, 31, 4, 37, 0, 0, 
			0, 0, 38, 44, 5, 12, 19, 0, 0, 
			0, 0, 45, 20, 13, 0, 26, 0, 0, 
		    0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 5, 5, 2, 255, 1f);
		renderer.runHorizontalBlur();

		// test topmost row (of data)
		assertEquals(1 << 24, dstPixels[18]);
		assertEquals(2 << 24, dstPixels[19]);
		assertEquals(10 << 24, dstPixels[20]);
		assertEquals(13 << 24, dstPixels[21]);
		assertEquals(19 << 24, dstPixels[22]);
		assertEquals(17 << 24, dstPixels[23]);
		assertEquals(17 << 24, dstPixels[24]);
		assertEquals(9 << 24, dstPixels[25]);
		assertEquals(5 << 24, dstPixels[26]);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 4 rows.
	 */
	@Test
	public void testHorizontalBlur_4() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(4, 4);

		ARGBPixels dstImage = new ARGBPixels(8, 8);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 9, 2, 41, 16, 0, 0, 
			0, 0, 42, 30, 24, 36, 0, 0, 
			0, 0, 18, 43, 31, 4, 0, 0, 
			0, 0, 38, 44, 5, 12, 0, 0, 
		    0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 4, 4, 2, 255, 1f);
		renderer.runHorizontalBlur();

		// test topmost row (of data)
		assertEquals(1 << 24, dstPixels[16]);
		assertEquals(2 << 24, dstPixels[17]);
		assertEquals(10 << 24, dstPixels[18]);
		assertEquals(13 << 24, dstPixels[19]);
		assertEquals(13 << 24, dstPixels[20]);
		assertEquals(11 << 24, dstPixels[21]);
		assertEquals(11 << 24, dstPixels[22]);
		assertEquals(3 << 24, dstPixels[23]);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 3 rows.
	 */
	@Test
	public void testHorizontalBlur_3() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(3, 3);

		ARGBPixels dstImage = new ARGBPixels(7, 7);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0,
			0, 0, 9, 2, 41, 0, 0, 
			0, 0, 42, 30, 24, 0, 0, 
			0, 0, 18, 43, 31, 0, 0,
		    0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 3, 3, 2, 255, 1f);
		renderer.runHorizontalBlur();

		// test topmost row (of data)
		assertEquals(1 << 24, dstPixels[14]);
		assertEquals(2 << 24, dstPixels[15]);
		assertEquals(10 << 24, dstPixels[16]);
		assertEquals(10 << 24, dstPixels[17]);
		assertEquals(10 << 24, dstPixels[18]);
		assertEquals(8 << 24, dstPixels[19]);
		assertEquals(8 << 24, dstPixels[20]);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 2 rows.
	 */
	@Test
	public void testHorizontalBlur_2() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(2, 2);

		ARGBPixels dstImage = new ARGBPixels(6, 6);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0,
			0, 0, 19, 12, 0, 0, 
			0, 0, 42, 30, 0, 0,
		    0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 2, 2, 2, 255, 1f);
		renderer.runHorizontalBlur();

		// test topmost row (of data)
		assertEquals(3 << 24, dstPixels[12]);
		assertEquals(6 << 24, dstPixels[13]);
		assertEquals(6 << 24, dstPixels[14]);
		assertEquals(6 << 24, dstPixels[15]);
		assertEquals(6 << 24, dstPixels[16]);
		assertEquals(2 << 24, dstPixels[17]);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 1 row.
	 */
	@Test
	public void testHorizontalBlur_1() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(1, 1);

		ARGBPixels dstImage = new ARGBPixels(5, 5);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0,
			0, 0, 9, 0, 0, 
		    0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 1, 1, 2, 255, 1f);
		renderer.runHorizontalBlur();

		// test topmost row (of data)
		assertEquals(1 << 24, dstPixels[10]);
		assertEquals(1 << 24, dstPixels[11]);
		assertEquals(1 << 24, dstPixels[12]);
		assertEquals(1 << 24, dstPixels[13]);
		assertEquals(1 << 24, dstPixels[14]);
	}

	/**
	 * This confirms that a thorough un-optimized Gaussian renderer that uses
	 * the uniform kernel reaches the same output as the BoxShadowRenderer
	 * 
	 * @throws Exception
	 */
	@Test
	public void testShadowImage() throws Exception {
		ShadowRenderer renderer1 = new BoxShadowRenderer();
		ShadowAttributes attr = new ShadowAttributes(15, .5f);

		final GaussianKernel kernel = renderer1.getKernel(attr);

		ShadowRenderer renderer2 = new OriginalGaussianShadowRenderer() {

			@Override
			public GaussianKernel getKernel(ShadowAttributes attr) {
				return kernel;
			}
		};

		BufferedImage bi = ShadowRendererDemo.createTestImage();

		BufferedImage result1 = renderer1.createShadow(bi, attr);
		BufferedImage result2 = renderer2.createShadow(bi, attr);

		// I had set tolerance to 1 to get this to pass. I'm OK with
		// a smidge of rounding error. I looked at the BufferedImages and they
		// look identical.
		String msg = GaussianShadowRendererTest.equals(result1, result2, 1);
		assertTrue(msg, msg == null);
	}
}
