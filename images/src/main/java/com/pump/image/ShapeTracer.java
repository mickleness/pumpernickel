/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.Serial;

/**
 * This converts a region of pixels into a <code>java.awt.Shape</code>. The
 * shape is intended to be blocky/pixelated; if you want to smooth the data out
 * you'll have to smooth the shape out separately.
 * <p>
 * This could be used for flood fill painter, a magic wand selection, or any
 * other feature that requires creating a mask from an image.
 */
public class ShapeTracer {

	/**
	 * This collapses collinear horizontal and vertical points. For example, if
	 * you add (0,0), (1,0) and (2,0) to this polygon, then it will only record
	 * (0,0) and (2,0).
	 * <p>
	 * (This object is expected only ever receive orthogonally collinear points,
	 * so that's all it's designed to collapse.)
	 */
	static class CollapsedPolygon extends Polygon {
		@Serial
		private static final long serialVersionUID = 1L;

		@Override
		public void addPoint(int x, int y) {
			if (npoints > 0 && xpoints[npoints - 1] == x
					&& ypoints[npoints - 1] == y)
				return;
			if (npoints > 1) {
				int xMinus2 = xpoints[npoints - 2];
				int yMinus2 = ypoints[npoints - 2];
				int xMinus1 = xpoints[npoints - 1];
				int yMinus1 = ypoints[npoints - 1];
				if (xMinus2 == xMinus1 && xMinus1 == x) {
					// we have a vertical line
					if (yMinus2 < yMinus1 && yMinus1 < y) {
						ypoints[npoints - 1] = y;
						invalidate();
						return;
					} else if (y < yMinus1 && yMinus1 < yMinus2) {
						ypoints[npoints - 1] = y;
						invalidate();
						return;
					}
				} else if (yMinus2 == yMinus1 && yMinus1 == y) {
					// we have a horizonal line
					if (xMinus2 < xMinus1 && xMinus1 < x) {
						xpoints[npoints - 1] = x;
						invalidate();
						return;
					} else if (x < xMinus1 && xMinus1 < xMinus2) {
						xpoints[npoints - 1] = x;
						invalidate();
						return;
					}
				}
			}
			super.addPoint(x, y);
		}

	}

	/**
	 * This mask indicates a pixel should be included in the resulting shape.
	 */
	protected static final int PIXEL_INCLUDE = 1;

	/**
	 * This mask indicates a pixel value has a top edge we need to trace.
	 */
	private static final int EDGE_TOP = 2;

	/**
	 * This mask indicates a pixel value has a left edge we need to trace.
	 */
	private static final int EDGE_LEFT = 4;

	/**
	 * Create a shape based on an image.
	 * <p>
	 * The default implementation traces mostly opaque pixels, but you can
	 * modify this by overriding {@link #createPixelMask(int[], int, int)}.
	 * 
	 * @param img
	 *            the image to trace zero or more shapes from.
	 * @return a shape outline parts of the images provided.
	 */
	public Shape trace(BufferedImage img) {
		// add 1 px to the right/bottom edge
		int width = img.getWidth() + 1;
		int height = img.getHeight() + 1;
		int[] pixels = new int[width * height];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, width);

		createPixelMask(pixels, width, height);

		int k = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++, k++) {
				if ((pixels[k] & PIXEL_INCLUDE) == 1) {
					if (x - 1 >= 0 && (pixels[k - 1] & PIXEL_INCLUDE) == 0
							|| x == 0) {
						pixels[k] += EDGE_LEFT;
					}
					if (x + 1 < width && (pixels[k + 1] & PIXEL_INCLUDE) == 0) {
						pixels[k + 1] += EDGE_LEFT;
					}

					if (y - 1 >= 0 && (pixels[k - width] & PIXEL_INCLUDE) == 0
							|| y == 0) {
						pixels[k] += EDGE_TOP;
					}
					if (y + 1 < height
							&& (pixels[k + width] & PIXEL_INCLUDE) == 0) {
						pixels[k + width] += EDGE_TOP;
					}
				}
			}
		}

		Path2D path = new Path2D.Float(PathIterator.WIND_EVEN_ODD);

		k = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++, k++) {
				int v = pixels[k];
				if ((v & EDGE_TOP) != 0) {
					Polygon p = tracePath(pixels, width, height, x, y);
					path.append(p, false);
				}
			}
		}

		return path;
	}

	/**
	 * This replaces every ARGB pixel value with a value of PIXEL_INCLUDE or
	 * zero.
	 * <p>
	 * The default implementation sets every pixel with an alpha value greater
	 * than 250 to PIXEL_INCLUDE and sets every other pixel to zero.
	 * <p>
	 * This method is a hook for subclasses to customize exactly which pixels
	 * are traced.
	 * 
	 * @param pixels
	 *            the ARGB pixel data this object is responsible for converting
	 *            into shapes.
	 * @param width
	 *            the width of the ARGB image. Note this is 1 pixel wider than
	 *            the image passed to {@link #trace(BufferedImage)} (the extra
	 *            column is empty).
	 * @param height
	 *            the height of the ARGB image. Note this is 1 pixel taller than
	 *            the image passed to {@link #trace(BufferedImage)} (the extra
	 *            row is empty).
	 */
	protected void createPixelMask(int[] pixels, int width, int height) {
		for (int k = 0; k < pixels.length; k++) {
			int alpha = (pixels[k] >> 24) & 0xff;
			if (alpha > 250) {
				pixels[k] = PIXEL_INCLUDE;
			} else {
				pixels[k] = 0;
			}
		}
	}

	/**
	 * Trace a path from start to finish. The starting pixel (x,y) must have a
	 * EDGE_TOP flag. As the path is traced (which constructs the Polygon return
	 * value) this method also unsets the EDGE_TOP and EDGE_LEFT flags from each
	 * pixel it iterates over.
	 * 
	 * @param pixels
	 *            the image data. This method relies on each pixel having a
	 *            combination of EDGE_TOP and EDGE_LEFT flags set correctly.
	 * @param width
	 *            the width of the image represented by "pixels"
	 * @param height
	 *            the height of the image represented by "pixels"
	 * @param startX
	 *            the initial x-value to start tracing at
	 * @param startY
	 *            the initial y-value to start tracing at
	 * @return a Polygon representing the traced path.
	 */
	private Polygon tracePath(int[] pixels, int width, int height, int startX,
			int startY) {
		Polygon returnValue = new CollapsedPolygon();
		int x = startX;
		int y = startY;
		int edge = EDGE_TOP;
		boolean increment = true;
		while (true) {
			if (edge == EDGE_TOP) {
				if (increment) {
					returnValue.addPoint(x, y);
					returnValue.addPoint(x + 1, y);
				} else {
					returnValue.addPoint(x + 1, y);
					returnValue.addPoint(x, y);
				}
			} else if (edge == EDGE_LEFT) {
				if (increment) {
					returnValue.addPoint(x, y);
					returnValue.addPoint(x, y + 1);
				} else {
					returnValue.addPoint(x, y + 1);
					returnValue.addPoint(x, y);
				}
			}
			pixels[y * width + x] ^= edge;

			if (edge == EDGE_TOP) {
				if (increment) {
					// on a top edge, we just scanned from left-to-right:
					if (y - 1 >= 0 && x + 1 < width
							&& (pixels[(y - 1) * width + x + 1]
									& EDGE_LEFT) > 0) {
						// turn counterclockwise
						y--;
						x++;
						edge = EDGE_LEFT;
						increment = false;
					} else if (x + 1 < width
							&& (pixels[y * width + x + 1] & EDGE_TOP) > 0) {
						// go straight
						x++;
						edge = EDGE_TOP;
						increment = true;
					} else if (x + 1 < width
							&& (pixels[y * width + x + 1] & EDGE_LEFT) > 0) {
						// turn clockwise
						x++;
						edge = EDGE_LEFT;
						increment = true;
					} else {
						return returnValue;
					}
				} else {
					// on a top edge, we just scanned from right-to-left:
					if ((pixels[y * width + x] & EDGE_LEFT) > 0) {
						// turn counterclockwise
						edge = EDGE_LEFT;
						increment = true;
					} else if (x - 1 >= 0
							&& (pixels[y * width + x - 1] & EDGE_TOP) > 0) {
						// go straight
						x--;
						edge = EDGE_TOP;
						increment = false;
					} else if (y - 1 >= 0
							&& (pixels[(y - 1) * width + x] & EDGE_LEFT) > 0) {
						// turn clockwise
						y--;
						edge = EDGE_LEFT;
						increment = false;
					} else {
						return returnValue;
					}
				}
			} else if (edge == EDGE_LEFT) {
				if (increment) {
					// on a left edge, we just scanned from top-to-bottom:
					if (y + 1 < height
							&& (pixels[(y + 1) * width + x] & EDGE_TOP) > 0) {
						// turn counterclockwise
						y++;
						edge = EDGE_TOP;
						increment = true;
					} else if (y + 1 < height
							&& (pixels[(y + 1) * width + x] & EDGE_LEFT) > 0) {
						// go straight
						y++;
						edge = EDGE_LEFT;
						increment = true;
					} else if (x - 1 >= 0 && y + 1 < height
							&& (pixels[(y + 1) * width + x - 1]
									& EDGE_TOP) > 0) {
						// turn clockwise
						y++;
						x--;
						edge = EDGE_TOP;
						increment = false;
					} else {
						return returnValue;
					}
				} else {
					// on a left edge, we just scanned from bottom-to-top:
					if (x - 1 >= 0
							&& (pixels[y * width + x - 1] & EDGE_TOP) > 0) {
						// turn counterclockwise
						x--;
						edge = EDGE_TOP;
						increment = false;
					} else if (y - 1 >= 0
							&& (pixels[(y - 1) * width + x] & EDGE_LEFT) > 0) {
						// go straight
						y--;
						edge = EDGE_LEFT;
						increment = false;
					} else if ((pixels[y * width + x] & EDGE_TOP) > 0) {
						// turn clockwise
						edge = EDGE_TOP;
						increment = true;
					} else {
						return returnValue;
					}
				}
			}
		}
	}
}