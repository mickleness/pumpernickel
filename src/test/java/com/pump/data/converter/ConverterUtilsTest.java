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
package com.pump.data.converter;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.pump.awt.converter.Rectangle2DMapConverter;
import com.pump.awt.converter.RenderedImageMapConverter;
import com.pump.awt.converter.ShapeMapConverter;

import junit.framework.TestCase;

/**
 * Unit tests for the ConverterUtils class.
 */
public class ConverterUtilsTest extends TestCase {

	/**
	 * Confirm that getConverter() returns the Rectangle2DMapConverter for a
	 * rectangle. (It could also get confused with a ShapeMapConverter.)
	 */
	public void testGetConverter_multipleConverters_Rectangle() {
		BeanMapConverter<?> converter = ConverterUtils
				.getConverter(Rectangle.class);
		assertEquals(Rectangle2DMapConverter.class, converter.getClass());
	}

	/**
	 * Confirm that getConverter() returns the Rectangle2DMapConverter for a
	 * rectangle. (It could also get confused with a ShapeMapConverter.)
	 */
	public void testGetConverter_multipleConverters_Rectangle2D() {
		BeanMapConverter<?> converter = ConverterUtils
				.getConverter(Rectangle2D.class);
		assertEquals(Rectangle2DMapConverter.class, converter.getClass());
	}

	/**
	 * Confirm that getConverter() returns the Rectangle2DMapConverter for a
	 * rectangle. (It could also get confused with a ShapeMapConverter.)
	 */
	public void testGetConverter_multipleConverters_Rectangle2D_Float() {
		BeanMapConverter<?> converter = ConverterUtils
				.getConverter(Rectangle2D.Float.class);
		assertEquals(Rectangle2DMapConverter.class, converter.getClass());
	}

	/**
	 * Confirm that getConverter() returns the Rectangle2DMapConverter for a
	 * rectangle. (It could also get confused with a ShapeMapConverter.)
	 */
	public void testGetConverter_multipleConverter_Rectangle2D_Double() {
		BeanMapConverter<?> converter = ConverterUtils
				.getConverter(Rectangle2D.Double.class);
		assertEquals(Rectangle2DMapConverter.class, converter.getClass());
	}

	/**
	 * Also confirm we can get a ShapeMapConverter when appropriate.
	 */
	public void testGetConverter_Path2D() {
		BeanMapConverter<?> converter = ConverterUtils
				.getConverter(Path2D.class);
		assertEquals(ShapeMapConverter.class, converter.getClass());
	}

	/**
	 * Confirm a BufferedImage returns Image.
	 * <p>
	 * The BufferedImage is both a RenderedImage and an Image, so the
	 * ImageMapConverter and RenderedImageMapConverter may vie for it.
	 */
	public void testGetConverter_BufferedImage() {
		BeanMapConverter<?> converter = ConverterUtils
				.getConverter(BufferedImage.class);
		assertEquals(RenderedImageMapConverter.class, converter.getClass());
	}

	/**
	 * This confirms that {@link AWTSerializationUtils#equals(Object,Object)} is
	 * able to recognize the atoms from two GradientPaints as equal.
	 * <p>
	 * The GradientPaint is a good example because it does NOT implement its own
	 * equals method.
	 */
	public void testEquals_GradientPaint() {
		GradientPaint gp1 = new GradientPaint(50, 100, Color.red, 90, 100,
				Color.yellow);
		GradientPaint gp2 = new GradientPaint(50, 100, Color.red, 90, 100,
				Color.yellow);
		assertTrue(ConverterUtils.equals(gp1, gp2));

		// not strictly required for our unit test, but this highlights what we
		// offer vs what we get by default:
		assertFalse(gp1.equals(gp2));
	}

	/**
	 * This confirms that AlphaComposite can be given a meaningful serialization
	 * string.
	 */
	public void testToString_AlphaComposite() {
		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.DST_OVER,
				.25f);
		String str = ConverterUtils.toString(ac);
		assertEquals("AlphaComposite[ alpha=0.25, rule=4]", str);

		// not strictly required for our unit test, but this highlights what we
		// offer vs what we get by default:
		assertFalse(ac.toString().contains(".25"));
	}

	/**
	 * This confirms an anonymous inner class is given better name like "Shape".
	 */
	public void testToString_anonymousClassName() {
		Shape shape = new Shape() {
			Rectangle delegate = new Rectangle(0, 0, 100, 100);

			@Override
			public Rectangle getBounds() {
				return delegate.getBounds();
			}

			@Override
			public Rectangle2D getBounds2D() {
				return delegate.getBounds2D();
			}

			@Override
			public boolean contains(double x, double y) {
				return delegate.contains(x, y);
			}

			@Override
			public boolean contains(Point2D p) {
				return delegate.contains(p);
			}

			@Override
			public boolean intersects(double x, double y, double w, double h) {
				return delegate.intersects(x, y, w, h);
			}

			@Override
			public boolean intersects(Rectangle2D r) {
				return delegate.intersects(r);
			}

			@Override
			public boolean contains(double x, double y, double w, double h) {
				return delegate.contains(x, y, w, h);
			}

			@Override
			public boolean contains(Rectangle2D r) {
				return delegate.contains(r);
			}

			@Override
			public PathIterator getPathIterator(AffineTransform at) {
				return delegate.getPathIterator(at);
			}

			@Override
			public PathIterator getPathIterator(AffineTransform at,
					double flatness) {
				return delegate.getPathIterator(at, flatness);
			}

		};

		String str = ConverterUtils.toString(shape);
		assertTrue(str.startsWith("Shape["));
	}
}