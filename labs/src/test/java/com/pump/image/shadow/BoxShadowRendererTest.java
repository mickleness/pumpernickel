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
package com.pump.image.shadow;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.junit.Test;

import com.pump.showcase.demo.ShadowRendererDemo;
import com.pump.showcase.demo.ShadowRendererDemo.OriginalGaussianShadowRenderer;

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
			9, 2, 41, 16, 29, 255, 35, 
			42, 30, 24, 36, 10, 255, 17, 
			18, 43, 31, 4, 37, 255, 48, 
			38, 44, 5, 12, 19, 255, 25, 
			45, 20, 13, 0, 26, 255, 6, 
			7, 27, 47, 33, 21, 255, 39, 
			28, 34, 22, 8, 15, 255, 46
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(11, 11);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 7, 7, 2, 255, Color.black);
		renderer.new VerticalRenderer().run();

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

		// test opaque column
		assertEquals(51, dstPixels[7 + 0 * 11]);
		assertEquals(102, dstPixels[7 + 1 * 11]);
		assertEquals(153, dstPixels[7 + 2 * 11]);
		assertEquals(204, dstPixels[7 + 3 * 11]);
		assertEquals(255, dstPixels[7 + 4 * 11]);
		assertEquals(255, dstPixels[7 + 5 * 11]);
		assertEquals(255, dstPixels[7 + 6 * 11]);
		assertEquals(204, dstPixels[7 + 7 * 11]);
		assertEquals(153, dstPixels[7 + 8 * 11]);
		assertEquals(102, dstPixels[7 + 9 * 11]);
		assertEquals(51, dstPixels[7 + 10 * 11]);

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
	 * A variation of testVerticalBlur_7 that uses a fractional kernel.
	 * <p>
	 * This test addresses a bug. An early implementation of the blur produced
	 * an exception because using fractional kernels with opaque images.
	 * 
	 */
	@Test
	public void testVerticalBlur_7_fractionalKernel() {
		ARGBPixels srcImage = new ARGBPixels(7, 7);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
				9, 2, 41, 16, 29, 255, 35, 
				42, 30, 24, 36, 10, 255, 17, 
				18, 43, 31, 4, 37, 255, 48, 
				38, 44, 5, 12, 19, 255, 25, 
				45, 20, 13, 0, 26, 255, 6, 
				7, 27, 47, 33, 21, 255, 39, 
				28, 34, 22, 8, 15, 255, 46
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(11, 11);

		// test a fractional kernel where k = 1.5f
		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 7, 7, 2, edgeWeight,
				Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();

		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 11]);
		assertEquals(7, dstPixels[2 + 1 * 11]);
		assertEquals(15, dstPixels[2 + 2 * 11]);
		assertEquals(22, dstPixels[2 + 3 * 11]);
		assertEquals(31, dstPixels[2 + 4 * 11]);
		assertEquals(31, dstPixels[2 + 5 * 11]);
		assertEquals(28, dstPixels[2 + 6 * 11]);
		assertEquals(24, dstPixels[2 + 7 * 11]);
		assertEquals(14, dstPixels[2 + 8 * 11]);
		assertEquals(7, dstPixels[2 + 9 * 11]);
		assertEquals(3, dstPixels[2 + 10 * 11]);

		// test middle column
		assertEquals(2, dstPixels[5 + 0 * 11]);
		assertEquals(8, dstPixels[5 + 1 * 11]);
		assertEquals(13, dstPixels[5 + 2 * 11]);
		assertEquals(15, dstPixels[5 + 3 * 11]);
		assertEquals(15, dstPixels[5 + 4 * 11]);
		assertEquals(12, dstPixels[5 + 5 * 11]);
		assertEquals(13, dstPixels[5 + 6 * 11]);
		assertEquals(11, dstPixels[5 + 7 * 11]);
		assertEquals(10, dstPixels[5 + 8 * 11]);
		assertEquals(6, dstPixels[5 + 9 * 11]);
		assertEquals(1, dstPixels[5 + 10 * 11]);

		// test opaque column
		// (in an early draft this produced values > 255, which eventually
		// caused RTE's)
		assertEquals(31, dstPixels[7 + 0 * 11]);
		assertEquals(95, dstPixels[7 + 1 * 11]);
		assertEquals(159, dstPixels[7 + 2 * 11]);
		assertEquals(223, dstPixels[7 + 3 * 11]);
		assertEquals(255, dstPixels[7 + 4 * 11]);
		assertEquals(255, dstPixels[7 + 5 * 11]);
		assertEquals(255, dstPixels[7 + 6 * 11]);
		assertEquals(223, dstPixels[7 + 7 * 11]);
		assertEquals(159, dstPixels[7 + 8 * 11]);
		assertEquals(95, dstPixels[7 + 9 * 11]);
		assertEquals(31, dstPixels[7 + 10 * 11]);

		// test rightmost column
		assertEquals(4, dstPixels[8 + 0 * 11]);
		assertEquals(10, dstPixels[8 + 1 * 11]);
		assertEquals(19, dstPixels[8 + 2 * 11]);
		assertEquals(28, dstPixels[8 + 3 * 11]);
		assertEquals(27, dstPixels[8 + 4 * 11]);
		assertEquals(26, dstPixels[8 + 5 * 11]);
		assertEquals(29, dstPixels[8 + 6 * 11]);
		assertEquals(25, dstPixels[8 + 7 * 11]);
		assertEquals(22, dstPixels[8 + 8 * 11]);
		assertEquals(16, dstPixels[8 + 9 * 11]);
		assertEquals(5, dstPixels[8 + 10 * 11]);
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
			9, 2, 41, 16, 255, 23, 
			42, 30, 24, 36, 255, 3, 
			18, 43, 31, 4, 255, 11, 
			38, 44, 5, 12, 255, 49, 
			45, 20, 13, 0, 255, 32, 
			7, 27, 47, 33, 255, 14
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(10, 10);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 6, 6, 2, 255, Color.black);
		renderer.new VerticalRenderer().run();

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

		// test opaque column
		assertEquals(51, dstPixels[6 + 0 * 10]);
		assertEquals(102, dstPixels[6 + 1 * 10]);
		assertEquals(153, dstPixels[6 + 2 * 10]);
		assertEquals(204, dstPixels[6 + 3 * 10]);
		assertEquals(255, dstPixels[6 + 4 * 10]);
		assertEquals(255, dstPixels[6 + 5 * 10]);
		assertEquals(204, dstPixels[6 + 6 * 10]);
		assertEquals(153, dstPixels[6 + 7 * 10]);
		assertEquals(102, dstPixels[6 + 8 * 10]);
		assertEquals(51, dstPixels[6 + 9 * 10]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 6 rows and a fractional
	 * kernel
	 */
	@Test
	public void testVerticalBlur_6_fractionalKernel() {
		ARGBPixels srcImage = new ARGBPixels(6, 6);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 2, 41, 16, 255, 23, 
			42, 30, 24, 36, 255, 3, 
			18, 43, 31, 4, 255, 11, 
			38, 44, 5, 12, 255, 49, 
			45, 20, 13, 0, 255, 32, 
			7, 27, 47, 33, 255, 14
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(10, 10);

		// test a fractional kernel where k = 1.5f
		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 6, 6, 2, edgeWeight,
				Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();

		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 10]);
		assertEquals(7, dstPixels[2 + 1 * 10]);
		assertEquals(15, dstPixels[2 + 2 * 10]);
		assertEquals(22, dstPixels[2 + 3 * 10]);
		assertEquals(31, dstPixels[2 + 4 * 10]);
		assertEquals(31, dstPixels[2 + 5 * 10]);
		assertEquals(24, dstPixels[2 + 6 * 10]);
		assertEquals(17, dstPixels[2 + 7 * 10]);
		assertEquals(7, dstPixels[2 + 8 * 10]);
		assertEquals(0, dstPixels[2 + 9 * 10]);

		// test opaque column
		assertEquals(31, dstPixels[6 + 0 * 10]);
		assertEquals(95, dstPixels[6 + 1 * 10]);
		assertEquals(159, dstPixels[6 + 2 * 10]);
		assertEquals(223, dstPixels[6 + 3 * 10]);
		assertEquals(255, dstPixels[6 + 4 * 10]);
		assertEquals(255, dstPixels[6 + 5 * 10]);
		assertEquals(223, dstPixels[6 + 6 * 10]);
		assertEquals(159, dstPixels[6 + 7 * 10]);
		assertEquals(95, dstPixels[6 + 8 * 10]);
		assertEquals(31, dstPixels[6 + 9 * 10]);
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
			9, 2, 41, 255, 29, 
			42, 30, 24, 255, 10, 
			18, 43, 31, 255, 37, 
			38, 44, 5, 255, 19, 
			45, 20, 13, 255, 26
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(9, 9);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 5, 5, 2, 255, Color.black);
		renderer.new VerticalRenderer().run();

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

		// test opaque column
		assertEquals(51, dstPixels[5 + 0 * 9]);
		assertEquals(102, dstPixels[5 + 1 * 9]);
		assertEquals(153, dstPixels[5 + 2 * 9]);
		assertEquals(204, dstPixels[5 + 3 * 9]);
		assertEquals(255, dstPixels[5 + 4 * 9]);
		assertEquals(204, dstPixels[5 + 5 * 9]);
		assertEquals(153, dstPixels[5 + 6 * 9]);
		assertEquals(102, dstPixels[5 + 7 * 9]);
		assertEquals(51, dstPixels[5 + 8 * 9]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 5 rows and a fractional
	 * kernel.
	 */
	@Test
	public void testVerticalBlur_5_fractionalKernel() {
		ARGBPixels srcImage = new ARGBPixels(5, 5);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 2, 41, 255, 29, 
			42, 30, 24, 255, 10, 
			18, 43, 31, 255, 37, 
			38, 44, 5, 255, 19, 
			45, 20, 13, 255, 26
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(9, 9);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 5, 5, 2, edgeWeight,
				Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();

		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 9]);
		assertEquals(7, dstPixels[2 + 1 * 9]);
		assertEquals(15, dstPixels[2 + 2 * 9]);
		assertEquals(22, dstPixels[2 + 3 * 9]);
		assertEquals(31, dstPixels[2 + 4 * 9]);
		assertEquals(30, dstPixels[2 + 5 * 9]);
		assertEquals(23, dstPixels[2 + 6 * 9]);
		assertEquals(16, dstPixels[2 + 7 * 9]);
		assertEquals(5, dstPixels[2 + 8 * 9]);

		// test opaque column
		assertEquals(31, dstPixels[5 + 0 * 9]);
		assertEquals(95, dstPixels[5 + 1 * 9]);
		assertEquals(159, dstPixels[5 + 2 * 9]);
		assertEquals(223, dstPixels[5 + 3 * 9]);
		assertEquals(255, dstPixels[5 + 4 * 9]);
		assertEquals(223, dstPixels[5 + 5 * 9]);
		assertEquals(159, dstPixels[5 + 6 * 9]);
		assertEquals(95, dstPixels[5 + 7 * 9]);
		assertEquals(31, dstPixels[5 + 8 * 9]);
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
			9, 2, 255, 16, 
			42, 30, 255, 36, 
			18, 43, 255, 4, 
			38, 44, 255, 12
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(8, 8);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 4, 4, 2, 255, Color.black);
		renderer.new VerticalRenderer().run();

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

		// test opaque column
		assertEquals(51, dstPixels[4 + 0 * 8]);
		assertEquals(102, dstPixels[4 + 1 * 8]);
		assertEquals(153, dstPixels[4 + 2 * 8]);
		assertEquals(204, dstPixels[4 + 3 * 8]);
		assertEquals(204, dstPixels[4 + 4 * 8]);
		assertEquals(153, dstPixels[4 + 5 * 8]);
		assertEquals(102, dstPixels[4 + 6 * 8]);
		assertEquals(51, dstPixels[4 + 7 * 8]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 4 rows and a fractional
	 * kernel.
	 */
	@Test
	public void testVerticalBlur_4_fractionalKernel() {
		ARGBPixels srcImage = new ARGBPixels(4, 4);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 2, 255, 16, 
			42, 30, 255, 36, 
			18, 43, 255, 4, 
			38, 44, 255, 12
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(8, 8);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 4, 4, 2, edgeWeight,
				Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();

		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 8]);
		assertEquals(7, dstPixels[2 + 1 * 8]);
		assertEquals(15, dstPixels[2 + 2 * 8]);
		assertEquals(22, dstPixels[2 + 3 * 8]);
		assertEquals(25, dstPixels[2 + 4 * 8]);
		assertEquals(19, dstPixels[2 + 5 * 8]);
		assertEquals(11, dstPixels[2 + 6 * 8]);
		assertEquals(4, dstPixels[2 + 7 * 8]);

		// test opaque column
		assertEquals(31, dstPixels[4 + 0 * 8]);
		assertEquals(95, dstPixels[4 + 1 * 8]);
		assertEquals(159, dstPixels[4 + 2 * 8]);
		assertEquals(223, dstPixels[4 + 3 * 8]);
		assertEquals(223, dstPixels[4 + 4 * 8]);
		assertEquals(159, dstPixels[4 + 5 * 8]);
		assertEquals(95, dstPixels[4 + 6 * 8]);
		assertEquals(31, dstPixels[4 + 7 * 8]);
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
			9, 255, 41,
			42, 255, 24, 
			18, 255, 31
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(7, 7);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 3, 3, 2, 255, Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();

		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 7]);
		assertEquals(10, dstPixels[2 + 1 * 7]);
		assertEquals(13, dstPixels[2 + 2 * 7]);
		assertEquals(13, dstPixels[2 + 3 * 7]);
		assertEquals(13, dstPixels[2 + 4 * 7]);
		assertEquals(12, dstPixels[2 + 5 * 7]);
		assertEquals(3, dstPixels[2 + 6 * 7]);

		// test opaque column
		assertEquals(51, dstPixels[3 + 0 * 7]);
		assertEquals(102, dstPixels[3 + 1 * 7]);
		assertEquals(153, dstPixels[3 + 2 * 7]);
		assertEquals(153, dstPixels[3 + 3 * 7]);
		assertEquals(153, dstPixels[3 + 4 * 7]);
		assertEquals(102, dstPixels[3 + 5 * 7]);
		assertEquals(51, dstPixels[3 + 6 * 7]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 3 rows and a fractional
	 * kernel.
	 */
	@Test
	public void testVerticalBlur_3_fractionalKernel() {
		ARGBPixels srcImage = new ARGBPixels(3, 3);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 255, 41,
			42, 255, 24, 
			18, 255, 31
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(7, 7);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 3, 3, 2, edgeWeight,
				Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();

		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 7]);
		assertEquals(7, dstPixels[2 + 1 * 7]);
		assertEquals(15, dstPixels[2 + 2 * 7]);
		assertEquals(17, dstPixels[2 + 3 * 7]);
		assertEquals(16, dstPixels[2 + 4 * 7]);
		assertEquals(9, dstPixels[2 + 5 * 7]);
		assertEquals(2, dstPixels[2 + 6 * 7]);

		// test opaque column
		assertEquals(31, dstPixels[3 + 0 * 7]);
		assertEquals(95, dstPixels[3 + 1 * 7]);
		assertEquals(159, dstPixels[3 + 2 * 7]);
		assertEquals(191, dstPixels[3 + 3 * 7]);
		assertEquals(159, dstPixels[3 + 4 * 7]);
		assertEquals(95, dstPixels[3 + 5 * 7]);
		assertEquals(31, dstPixels[3 + 6 * 7]);
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
			9, 255,
			42, 255
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(6, 6);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 2, 2, 2, 255, Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();

		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 6]);
		assertEquals(10, dstPixels[2 + 1 * 6]);
		assertEquals(10, dstPixels[2 + 2 * 6]);
		assertEquals(10, dstPixels[2 + 3 * 6]);
		assertEquals(10, dstPixels[2 + 4 * 6]);
		assertEquals(8, dstPixels[2 + 5 * 6]);

		// test opaque column
		assertEquals(51, dstPixels[3 + 0 * 6]);
		assertEquals(102, dstPixels[3 + 1 * 6]);
		assertEquals(102, dstPixels[3 + 2 * 6]);
		assertEquals(102, dstPixels[3 + 3 * 6]);
		assertEquals(102, dstPixels[3 + 4 * 6]);
		assertEquals(51, dstPixels[3 + 5 * 6]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 2 rows and a fractional
	 * kernel.
	 */
	@Test
	public void testVerticalBlur_2_fractionalKernel() {
		ARGBPixels srcImage = new ARGBPixels(2, 2);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			9, 255,
			42, 255
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(6, 6);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 2, 2, 2, edgeWeight,
				Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();

		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 6]);
		assertEquals(7, dstPixels[2 + 1 * 6]);
		assertEquals(12, dstPixels[2 + 2 * 6]);
		assertEquals(12, dstPixels[2 + 3 * 6]);
		assertEquals(11, dstPixels[2 + 4 * 6]);
		assertEquals(5, dstPixels[2 + 5 * 6]);

		// test opaque column
		assertEquals(31, dstPixels[3 + 0 * 6]);
		assertEquals(95, dstPixels[3 + 1 * 6]);
		assertEquals(127, dstPixels[3 + 2 * 6]);
		assertEquals(127, dstPixels[3 + 3 * 6]);
		assertEquals(95, dstPixels[3 + 4 * 6]);
		assertEquals(31, dstPixels[3 + 5 * 6]);
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
				srcImage, dstImage, 0, 0, 2, 2, 1, 1, 2, 255, Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 5]);
		assertEquals(1, dstPixels[2 + 1 * 5]);
		assertEquals(1, dstPixels[2 + 2 * 5]);
		assertEquals(1, dstPixels[2 + 3 * 5]);
		assertEquals(1, dstPixels[2 + 4 * 5]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 1 row and a fractional
	 * kernel.
	 */
	@Test
	public void testVerticalBlur_1_fractionalKernel() {
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

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 1, 1, 2, edgeWeight,
				Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(1, dstPixels[2 + 0 * 5]);
		assertEquals(2, dstPixels[2 + 1 * 5]);
		assertEquals(2, dstPixels[2 + 2 * 5]);
		assertEquals(2, dstPixels[2 + 3 * 5]);
		assertEquals(1, dstPixels[2 + 4 * 5]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 1 opaque row.
	 */
	@Test
	public void testVerticalBlur_1_opaque() {
		ARGBPixels srcImage = new ARGBPixels(1, 1);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			255
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(5, 5);
		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 1, 1, 2, 255, Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(51, dstPixels[2 + 0 * 5]);
		assertEquals(51, dstPixels[2 + 1 * 5]);
		assertEquals(51, dstPixels[2 + 2 * 5]);
		assertEquals(51, dstPixels[2 + 3 * 5]);
		assertEquals(51, dstPixels[2 + 4 * 5]);
	}

	/**
	 * A variation of the testVerticalBlur_7 that uses 1 opaque row and a
	 * fractional kernel.
	 */
	@Test
	public void testVerticalBlur_1_opaque_fractionalKernel() {
		ARGBPixels srcImage = new ARGBPixels(1, 1);
		int[] srcPixels = srcImage.getPixels();
		// @formatter:off
		int[] srcPixelsCopy = new int[] {
			255
		};
		// @formatter:on
		System.arraycopy(srcPixelsCopy, 0, srcPixels, 0, srcPixels.length);
		for (int a = 0; a < srcPixels.length; a++) {
			srcPixels[a] = srcPixels[a] << 24;
		}

		ARGBPixels dstImage = new ARGBPixels(5, 5);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 1, 1, 2, edgeWeight,
				Color.black);
		renderer.new VerticalRenderer().run();

		int[] dstPixels = dstImage.getPixels();
		// test leftmost column
		assertEquals(31, dstPixels[2 + 0 * 5]);
		assertEquals(63, dstPixels[2 + 1 * 5]);
		assertEquals(63, dstPixels[2 + 2 * 5]);
		assertEquals(63, dstPixels[2 + 3 * 5]);
		assertEquals(31, dstPixels[2 + 4 * 5]);
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
			0, 0, 255, 255, 255, 255, 255, 255, 255, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 7, 7, 2, 255, Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[22] >> 24) & 0xff);
		assertEquals(2, (dstPixels[23] >> 24) & 0xff);
		assertEquals(10, (dstPixels[24] >> 24) & 0xff);
		assertEquals(13, (dstPixels[25] >> 24) & 0xff);
		assertEquals(19, (dstPixels[26] >> 24) & 0xff);
		assertEquals(22, (dstPixels[27] >> 24) & 0xff);
		assertEquals(28, (dstPixels[28] >> 24) & 0xff);
		assertEquals(20, (dstPixels[29] >> 24) & 0xff);
		assertEquals(17, (dstPixels[30] >> 24) & 0xff);
		assertEquals(11, (dstPixels[31] >> 24) & 0xff);
		assertEquals(7, (dstPixels[32] >> 24) & 0xff);

		// test middle row
		assertEquals(7, (dstPixels[55] >> 24) & 0xff);
		assertEquals(16, (dstPixels[56] >> 24) & 0xff);
		assertEquals(17, (dstPixels[57] >> 24) & 0xff);
		assertEquals(19, (dstPixels[58] >> 24) & 0xff);
		assertEquals(23, (dstPixels[59] >> 24) & 0xff);
		assertEquals(25, (dstPixels[60] >> 24) & 0xff);
		assertEquals(22, (dstPixels[61] >> 24) & 0xff);
		assertEquals(21, (dstPixels[62] >> 24) & 0xff);
		assertEquals(18, (dstPixels[63] >> 24) & 0xff);
		assertEquals(14, (dstPixels[64] >> 24) & 0xff);
		assertEquals(5, (dstPixels[65] >> 24) & 0xff);

		// test opaque row
		assertEquals(51, (dstPixels[88] >> 24) & 0xff);
		assertEquals(102, (dstPixels[89] >> 24) & 0xff);
		assertEquals(153, (dstPixels[90] >> 24) & 0xff);
		assertEquals(204, (dstPixels[91] >> 24) & 0xff);
		assertEquals(255, (dstPixels[92] >> 24) & 0xff);
		assertEquals(255, (dstPixels[93] >> 24) & 0xff);
		assertEquals(255, (dstPixels[94] >> 24) & 0xff);
		assertEquals(204, (dstPixels[95] >> 24) & 0xff);
		assertEquals(153, (dstPixels[96] >> 24) & 0xff);
		assertEquals(102, (dstPixels[97] >> 24) & 0xff);
		assertEquals(51, (dstPixels[98] >> 24) & 0xff);

		// no need to test more, horizontal passes are much simpler since data
		// is continuous.
	}

	/**
	 * Test the horizontal blur using the same data as the
	 * {@link #testVerticalBlur_7()} with a fractional kernel.
	 */
	@Test
	public void testHorizontalBlur_7_fractionalKernel() {
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
			0, 0, 255, 255, 255, 255, 255, 255, 255, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 7, 7, 2, edgeWeight,
				Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[22] >> 24) & 0xff);
		assertEquals(2, (dstPixels[23] >> 24) & 0xff);
		assertEquals(7, (dstPixels[24] >> 24) & 0xff);
		assertEquals(15, (dstPixels[25] >> 24) & 0xff);
		assertEquals(19, (dstPixels[26] >> 24) & 0xff);
		assertEquals(24, (dstPixels[27] >> 24) & 0xff);
		assertEquals(26, (dstPixels[28] >> 24) & 0xff);
		assertEquals(23, (dstPixels[29] >> 24) & 0xff);
		assertEquals(18, (dstPixels[30] >> 24) & 0xff);
		assertEquals(11, (dstPixels[31] >> 24) & 0xff);
		assertEquals(4, (dstPixels[32] >> 24) & 0xff);

		// test middle row
		assertEquals(4, (dstPixels[55] >> 24) & 0xff);
		assertEquals(15, (dstPixels[56] >> 24) & 0xff);
		assertEquals(21, (dstPixels[57] >> 24) & 0xff);
		assertEquals(23, (dstPixels[58] >> 24) & 0xff);
		assertEquals(22, (dstPixels[59] >> 24) & 0xff);
		assertEquals(20, (dstPixels[60] >> 24) & 0xff);
		assertEquals(23, (dstPixels[61] >> 24) & 0xff);
		assertEquals(24, (dstPixels[62] >> 24) & 0xff);
		assertEquals(20, (dstPixels[63] >> 24) & 0xff);
		assertEquals(12, (dstPixels[64] >> 24) & 0xff);
		assertEquals(3, (dstPixels[65] >> 24) & 0xff);

		// test opaque row
		assertEquals(31, (dstPixels[88] >> 24) & 0xff);
		assertEquals(95, (dstPixels[89] >> 24) & 0xff);
		assertEquals(159, (dstPixels[90] >> 24) & 0xff);
		assertEquals(223, (dstPixels[91] >> 24) & 0xff);
		assertEquals(255, (dstPixels[92] >> 24) & 0xff);
		assertEquals(255, (dstPixels[93] >> 24) & 0xff);
		assertEquals(255, (dstPixels[94] >> 24) & 0xff);
		assertEquals(223, (dstPixels[95] >> 24) & 0xff);
		assertEquals(159, (dstPixels[96] >> 24) & 0xff);
		assertEquals(95, (dstPixels[97] >> 24) & 0xff);
		assertEquals(31, (dstPixels[98] >> 24) & 0xff);

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
			0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 6, 6, 2, 255, Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[20] >> 24) & 0xff);
		assertEquals(2, (dstPixels[21] >> 24) & 0xff);
		assertEquals(10, (dstPixels[22] >> 24) & 0xff);
		assertEquals(13, (dstPixels[23] >> 24) & 0xff);
		assertEquals(19, (dstPixels[24] >> 24) & 0xff);
		assertEquals(22, (dstPixels[25] >> 24) & 0xff);
		assertEquals(21, (dstPixels[26] >> 24) & 0xff);
		assertEquals(13, (dstPixels[27] >> 24) & 0xff);
		assertEquals(10, (dstPixels[28] >> 24) & 0xff);
		assertEquals(4, (dstPixels[29] >> 24) & 0xff);

		// test opaque row of data
		assertEquals(51, (dstPixels[70] >> 24) & 0xff);
		assertEquals(102, (dstPixels[71] >> 24) & 0xff);
		assertEquals(153, (dstPixels[72] >> 24) & 0xff);
		assertEquals(204, (dstPixels[73] >> 24) & 0xff);
		assertEquals(255, (dstPixels[74] >> 24) & 0xff);
		assertEquals(255, (dstPixels[75] >> 24) & 0xff);
		assertEquals(204, (dstPixels[76] >> 24) & 0xff);
		assertEquals(153, (dstPixels[77] >> 24) & 0xff);
		assertEquals(102, (dstPixels[78] >> 24) & 0xff);
		assertEquals(51, (dstPixels[79] >> 24) & 0xff);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 6 rows and a fractional
	 * kernel
	 */
	@Test
	public void testHorizontalBlur_6_fractionalKernel() {
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
			0, 0, 255, 255, 255, 255, 255, 255, 0, 0, 
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 6, 6, 2, edgeWeight,
				Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[20] >> 24) & 0xff);
		assertEquals(2, (dstPixels[21] >> 24) & 0xff);
		assertEquals(7, (dstPixels[22] >> 24) & 0xff);
		assertEquals(15, (dstPixels[23] >> 24) & 0xff);
		assertEquals(19, (dstPixels[24] >> 24) & 0xff);
		assertEquals(24, (dstPixels[25] >> 24) & 0xff);
		assertEquals(22, (dstPixels[26] >> 24) & 0xff);
		assertEquals(15, (dstPixels[27] >> 24) & 0xff);
		assertEquals(9, (dstPixels[28] >> 24) & 0xff);
		assertEquals(2, (dstPixels[29] >> 24) & 0xff);

		// test opaque row of data
		assertEquals(31, (dstPixels[70] >> 24) & 0xff);
		assertEquals(95, (dstPixels[71] >> 24) & 0xff);
		assertEquals(159, (dstPixels[72] >> 24) & 0xff);
		assertEquals(223, (dstPixels[73] >> 24) & 0xff);
		assertEquals(255, (dstPixels[74] >> 24) & 0xff);
		assertEquals(255, (dstPixels[75] >> 24) & 0xff);
		assertEquals(223, (dstPixels[76] >> 24) & 0xff);
		assertEquals(159, (dstPixels[77] >> 24) & 0xff);
		assertEquals(95, (dstPixels[78] >> 24) & 0xff);
		assertEquals(31, (dstPixels[79] >> 24) & 0xff);
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
			0, 0, 255, 255, 255, 255, 255, 0, 0, 
		    0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 5, 5, 2, 255, Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[18] >> 24) & 0xff);
		assertEquals(2, (dstPixels[19] >> 24) & 0xff);
		assertEquals(10, (dstPixels[20] >> 24) & 0xff);
		assertEquals(13, (dstPixels[21] >> 24) & 0xff);
		assertEquals(19, (dstPixels[22] >> 24) & 0xff);
		assertEquals(17, (dstPixels[23] >> 24) & 0xff);
		assertEquals(17, (dstPixels[24] >> 24) & 0xff);
		assertEquals(9, (dstPixels[25] >> 24) & 0xff);
		assertEquals(5, (dstPixels[26] >> 24) & 0xff);

		// test opaque row of data
		assertEquals(51, (dstPixels[54] >> 24) & 0xff);
		assertEquals(102, (dstPixels[55] >> 24) & 0xff);
		assertEquals(153, (dstPixels[56] >> 24) & 0xff);
		assertEquals(204, (dstPixels[57] >> 24) & 0xff);
		assertEquals(255, (dstPixels[58] >> 24) & 0xff);
		assertEquals(204, (dstPixels[59] >> 24) & 0xff);
		assertEquals(153, (dstPixels[60] >> 24) & 0xff);
		assertEquals(102, (dstPixels[61] >> 24) & 0xff);
		assertEquals(51, (dstPixels[62] >> 24) & 0xff);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 5 rows and a fractional
	 * kernel.
	 */
	@Test
	public void testHorizontalBlur_5_fractionalKernel() {
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
			0, 0, 255, 255, 255, 255, 255, 0, 0, 
		    0, 0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 5, 5, 2, edgeWeight,
				Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[18] >> 24) & 0xff);
		assertEquals(2, (dstPixels[19] >> 24) & 0xff);
		assertEquals(7, (dstPixels[20] >> 24) & 0xff);
		assertEquals(15, (dstPixels[21] >> 24) & 0xff);
		assertEquals(19, (dstPixels[22] >> 24) & 0xff);
		assertEquals(21, (dstPixels[23] >> 24) & 0xff);
		assertEquals(16, (dstPixels[24] >> 24) & 0xff);
		assertEquals(9, (dstPixels[25] >> 24) & 0xff);
		assertEquals(3, (dstPixels[26] >> 24) & 0xff);

		// test opaque row of data
		assertEquals(31, (dstPixels[54] >> 24) & 0xff);
		assertEquals(95, (dstPixels[55] >> 24) & 0xff);
		assertEquals(159, (dstPixels[56] >> 24) & 0xff);
		assertEquals(223, (dstPixels[57] >> 24) & 0xff);
		assertEquals(255, (dstPixels[58] >> 24) & 0xff);
		assertEquals(223, (dstPixels[59] >> 24) & 0xff);
		assertEquals(159, (dstPixels[60] >> 24) & 0xff);
		assertEquals(95, (dstPixels[61] >> 24) & 0xff);
		assertEquals(31, (dstPixels[62] >> 24) & 0xff);
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
			0, 0, 255, 255, 255, 255, 0, 0, 
		    0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 4, 4, 2, 255, Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[16] >> 24) & 0xff);
		assertEquals(2, (dstPixels[17] >> 24) & 0xff);
		assertEquals(10, (dstPixels[18] >> 24) & 0xff);
		assertEquals(13, (dstPixels[19] >> 24) & 0xff);
		assertEquals(13, (dstPixels[20] >> 24) & 0xff);
		assertEquals(11, (dstPixels[21] >> 24) & 0xff);
		assertEquals(11, (dstPixels[22] >> 24) & 0xff);
		assertEquals(3, (dstPixels[23] >> 24) & 0xff);

		// test opaque row of data
		assertEquals(51, (dstPixels[40] >> 24) & 0xff);
		assertEquals(102, (dstPixels[41] >> 24) & 0xff);
		assertEquals(153, (dstPixels[42] >> 24) & 0xff);
		assertEquals(204, (dstPixels[43] >> 24) & 0xff);
		assertEquals(204, (dstPixels[44] >> 24) & 0xff);
		assertEquals(153, (dstPixels[45] >> 24) & 0xff);
		assertEquals(102, (dstPixels[46] >> 24) & 0xff);
		assertEquals(51, (dstPixels[47] >> 24) & 0xff);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 4 rows and a fractional
	 * kernel.
	 */
	@Test
	public void testHorizontalBlur_4_fractionalKernel() {
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
			0, 0, 255, 255, 255, 255, 0, 0, 
		    0, 0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 4, 4, 2, edgeWeight,
				Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[16] >> 24) & 0xff);
		assertEquals(2, (dstPixels[17] >> 24) & 0xff);
		assertEquals(7, (dstPixels[18] >> 24) & 0xff);
		assertEquals(15, (dstPixels[19] >> 24) & 0xff);
		assertEquals(15, (dstPixels[20] >> 24) & 0xff);
		assertEquals(14, (dstPixels[21] >> 24) & 0xff);
		assertEquals(9, (dstPixels[22] >> 24) & 0xff);
		assertEquals(2, (dstPixels[23] >> 24) & 0xff);

		// test opaque row of data
		assertEquals(31, (dstPixels[40] >> 24) & 0xff);
		assertEquals(95, (dstPixels[41] >> 24) & 0xff);
		assertEquals(159, (dstPixels[42] >> 24) & 0xff);
		assertEquals(223, (dstPixels[43] >> 24) & 0xff);
		assertEquals(223, (dstPixels[44] >> 24) & 0xff);
		assertEquals(159, (dstPixels[45] >> 24) & 0xff);
		assertEquals(95, (dstPixels[46] >> 24) & 0xff);
		assertEquals(31, (dstPixels[47] >> 24) & 0xff);
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
			0, 0, 255, 255, 255, 0, 0,
		    0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 3, 3, 2, 255, Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[14] >> 24) & 0xff);
		assertEquals(2, (dstPixels[15] >> 24) & 0xff);
		assertEquals(10, (dstPixels[16] >> 24) & 0xff);
		assertEquals(10, (dstPixels[17] >> 24) & 0xff);
		assertEquals(10, (dstPixels[18] >> 24) & 0xff);
		assertEquals(8, (dstPixels[19] >> 24) & 0xff);
		assertEquals(8, (dstPixels[20] >> 24) & 0xff);

		// test opaque row of data
		assertEquals(51, (dstPixels[28] >> 24) & 0xff);
		assertEquals(102, (dstPixels[29] >> 24) & 0xff);
		assertEquals(153, (dstPixels[30] >> 24) & 0xff);
		assertEquals(153, (dstPixels[31] >> 24) & 0xff);
		assertEquals(153, (dstPixels[32] >> 24) & 0xff);
		assertEquals(102, (dstPixels[33] >> 24) & 0xff);
		assertEquals(51, (dstPixels[34] >> 24) & 0xff);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 3 rows and a fractional
	 * kernel.
	 */
	@Test
	public void testHorizontalBlur_3_fractionalKernel() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(3, 3);

		ARGBPixels dstImage = new ARGBPixels(7, 7);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0,
			0, 0, 9, 2, 41, 0, 0, 
			0, 0, 42, 30, 24, 0, 0, 
			0, 0, 255, 255, 255, 0, 0,
		    0, 0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 3, 3, 2, edgeWeight,
				Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[14] >> 24) & 0xff);
		assertEquals(2, (dstPixels[15] >> 24) & 0xff);
		assertEquals(7, (dstPixels[16] >> 24) & 0xff);
		assertEquals(12, (dstPixels[17] >> 24) & 0xff);
		assertEquals(11, (dstPixels[18] >> 24) & 0xff);
		assertEquals(10, (dstPixels[19] >> 24) & 0xff);
		assertEquals(5, (dstPixels[20] >> 24) & 0xff);

		// test opaque row of data
		assertEquals(31, (dstPixels[28] >> 24) & 0xff);
		assertEquals(95, (dstPixels[29] >> 24) & 0xff);
		assertEquals(159, (dstPixels[30] >> 24) & 0xff);
		assertEquals(191, (dstPixels[31] >> 24) & 0xff);
		assertEquals(159, (dstPixels[32] >> 24) & 0xff);
		assertEquals(95, (dstPixels[33] >> 24) & 0xff);
		assertEquals(31, (dstPixels[34] >> 24) & 0xff);
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
			0, 0, 255, 255, 0, 0,
		    0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 2, 2, 2, 255, Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(3, (dstPixels[12] >> 24) & 0xff);
		assertEquals(6, (dstPixels[13] >> 24) & 0xff);
		assertEquals(6, (dstPixels[14] >> 24) & 0xff);
		assertEquals(6, (dstPixels[15] >> 24) & 0xff);
		assertEquals(6, (dstPixels[16] >> 24) & 0xff);
		assertEquals(2, (dstPixels[17] >> 24) & 0xff);

		// test opaque row of data
		assertEquals(51, (dstPixels[18] >> 24) & 0xff);
		assertEquals(102, (dstPixels[19] >> 24) & 0xff);
		assertEquals(102, (dstPixels[20] >> 24) & 0xff);
		assertEquals(102, (dstPixels[21] >> 24) & 0xff);
		assertEquals(102, (dstPixels[22] >> 24) & 0xff);
		assertEquals(51, (dstPixels[23] >> 24) & 0xff);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 2 rows and a fractional
	 * kernel.
	 */
	@Test
	public void testHorizontalBlur_2_fractionalKernel() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(2, 2);

		ARGBPixels dstImage = new ARGBPixels(6, 6);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0,
			0, 0, 19, 12, 0, 0, 
			0, 0, 255, 255, 0, 0,
		    0, 0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 2, 2, 2, edgeWeight,
				Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(2, (dstPixels[12] >> 24) & 0xff);
		assertEquals(6, (dstPixels[13] >> 24) & 0xff);
		assertEquals(7, (dstPixels[14] >> 24) & 0xff);
		assertEquals(7, (dstPixels[15] >> 24) & 0xff);
		assertEquals(5, (dstPixels[16] >> 24) & 0xff);
		assertEquals(1, (dstPixels[17] >> 24) & 0xff);

		// test opaque row of data
		assertEquals(31, (dstPixels[18] >> 24) & 0xff);
		assertEquals(95, (dstPixels[19] >> 24) & 0xff);
		assertEquals(127, (dstPixels[20] >> 24) & 0xff);
		assertEquals(127, (dstPixels[21] >> 24) & 0xff);
		assertEquals(95, (dstPixels[22] >> 24) & 0xff);
		assertEquals(31, (dstPixels[23] >> 24) & 0xff);
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
				srcImage, dstImage, 0, 0, 2, 2, 1, 1, 2, 255, Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[10] >> 24) & 0xff);
		assertEquals(1, (dstPixels[11] >> 24) & 0xff);
		assertEquals(1, (dstPixels[12] >> 24) & 0xff);
		assertEquals(1, (dstPixels[13] >> 24) & 0xff);
		assertEquals(1, (dstPixels[14] >> 24) & 0xff);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 1 row and a fractional
	 * kernel.
	 */
	@Test
	public void testHorizontalBlur_1_fractionalKernel() {
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

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 1, 1, 2, edgeWeight,
				Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(1, (dstPixels[10] >> 24) & 0xff);
		assertEquals(2, (dstPixels[11] >> 24) & 0xff);
		assertEquals(2, (dstPixels[12] >> 24) & 0xff);
		assertEquals(2, (dstPixels[13] >> 24) & 0xff);
		assertEquals(1, (dstPixels[14] >> 24) & 0xff);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 1 opaque row.
	 */
	@Test
	public void testHorizontalBlur_1_opaque() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(1, 1);

		ARGBPixels dstImage = new ARGBPixels(5, 5);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0,
			0, 0, 255, 0, 0, 
		    0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 1, 1, 2, 255, Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(51, (dstPixels[10] >> 24) & 0xff);
		assertEquals(51, (dstPixels[11] >> 24) & 0xff);
		assertEquals(51, (dstPixels[12] >> 24) & 0xff);
		assertEquals(51, (dstPixels[13] >> 24) & 0xff);
		assertEquals(51, (dstPixels[14] >> 24) & 0xff);
	}

	/**
	 * A variation of the testHorizontalBlur_7 that uses 1 opaque row and a
	 * fractional kernel.
	 */
	@Test
	public void testHorizontalBlur_1_opaque_fractionalKernel() {
		// we don't really use the source in this unit test
		ARGBPixels srcImage = new ARGBPixels(1, 1);

		ARGBPixels dstImage = new ARGBPixels(5, 5);
		// @formatter:off
		int[] dstPixelsCopy = new int[] {
		    0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0,
			0, 0, 255, 0, 0, 
		    0, 0, 0, 0, 0,
		    0, 0, 0, 0, 0
		};
		// @formatter:on
		int[] dstPixels = dstImage.getPixels();
		System.arraycopy(dstPixelsCopy, 0, dstPixels, 0, dstPixelsCopy.length);

		int edgeWeight = new BoxShadowRenderer().getEdgeWeight(1.5f);

		BoxShadowRenderer.Renderer renderer = new BoxShadowRenderer.Renderer(
				srcImage, dstImage, 0, 0, 2, 2, 1, 1, 2, edgeWeight,
				Color.black);
		renderer.new HorizontalRenderer().run();

		// test topmost row of data
		assertEquals(31, (dstPixels[10] >> 24) & 0xff);
		assertEquals(63, (dstPixels[11] >> 24) & 0xff);
		assertEquals(63, (dstPixels[12] >> 24) & 0xff);
		assertEquals(63, (dstPixels[13] >> 24) & 0xff);
		assertEquals(31, (dstPixels[14] >> 24) & 0xff);
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
		ShadowAttributes attr = new ShadowAttributes(0, 0, 15,
				new Color(0, 0, 0, 128));

		final GaussianKernel kernel = renderer1
				.getKernel(attr.getShadowKernelRadius());

		ShadowRenderer renderer2 = new OriginalGaussianShadowRenderer() {

			@Override
			public GaussianKernel getKernel(float kernelRadius) {
				return kernel;
			}
		};

		BufferedImage bi = ShadowRendererDemo.createTestImage();

		BufferedImage result1 = renderer1.createShadow(bi,
				attr.getShadowKernelRadius(), attr.getShadowColor());
		BufferedImage result2 = renderer2.createShadow(bi,
				attr.getShadowKernelRadius(), attr.getShadowColor());

		// I had set tolerance to 1 to get this to pass. I'm OK with
		// a smidge of rounding error. I looked at the BufferedImages and they
		// look identical.
		String msg = GaussianShadowRendererTest.equals(result1, result2, 1);
		assertTrue(msg, msg == null);
	}
}