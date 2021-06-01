package com.pump.awt;

import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;

public class ShapeTracer {
	private static final int PIXEL_OPAQUE = 1;
	private static final int EDGE_TOP = 2;
	private static final int EDGE_LEFT = 4;

	private static final int DIRECTION_GREATER = 1;
	private static final int DIRECTION_LESSER = 2;

	public Shape trace(BufferedImage img) {
		// add 1 px to the right/bottom edge
		int width = img.getWidth() + 1;
		int height = img.getHeight() + 1;
		int[] pixels = new int[width * height];
		img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, width);

		for (int k = 0; k < pixels.length; k++) {
			int alpha = (pixels[k] >> 24) & 0xff;
			if (alpha > 250) {
				pixels[k] = PIXEL_OPAQUE;
			} else {
				pixels[k] = 0;
			}
		}

		int k = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++, k++) {
				if ((pixels[k] & PIXEL_OPAQUE) == 1) {
					if (x - 1 >= 0 && (pixels[k - 1] & PIXEL_OPAQUE) == 0) {
						pixels[k] += EDGE_LEFT;
					}
					if (x + 1 < width && (pixels[k + 1] & PIXEL_OPAQUE) == 0) {
						pixels[k + 1] += EDGE_LEFT;
					}

					if (y - 1 >= 0 && (pixels[k - width] & PIXEL_OPAQUE) == 0) {
						pixels[k] += EDGE_TOP;
					}
					if (y + 1 < height
							&& (pixels[k + width] & PIXEL_OPAQUE) == 0) {
						pixels[k + width] += EDGE_TOP;
					}
				}
			}
		}

		Path2D path = new Path2D.Float(PathIterator.WIND_NON_ZERO);

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

	private Polygon tracePath(int[] pixels, int width, int height, int startX,
			int startY) {
		Polygon returnValue = new Polygon();
		int x = startX;
		int y = startY;
		int edge = EDGE_TOP;
		int direction = DIRECTION_GREATER;
		while (true) {
			if (edge == EDGE_TOP) {
				if (direction == DIRECTION_GREATER) {
					returnValue.addPoint(x, y);
					returnValue.addPoint(x + 1, y);
				} else {
					returnValue.addPoint(x + 1, y);
					returnValue.addPoint(x, y);
				}
			} else if (edge == EDGE_LEFT) {
				if (direction == DIRECTION_GREATER) {
					returnValue.addPoint(x, y);
					returnValue.addPoint(x, y + 1);
				} else {
					returnValue.addPoint(x, y + 1);
					returnValue.addPoint(x, y);
				}
			}
			pixels[y * width + x] ^= edge;

			if (edge == EDGE_TOP) {
				if (direction == DIRECTION_GREATER) {
					// on a top edge, we just scanned from left-to-right:
					if (y - 1 >= 0 && x + 1 < width
							&& (pixels[(y - 1) * width + x + 1]
									& EDGE_LEFT) > 0) {
						// turn counterclockwise
						y--;
						x++;
						edge = EDGE_LEFT;
						direction = DIRECTION_LESSER;
					} else if (x + 1 < width
							&& (pixels[y * width + x + 1] & EDGE_TOP) > 0) {
						// go straight
						x++;
						edge = EDGE_TOP;
						direction = DIRECTION_GREATER;
					} else if (x + 1 < width
							&& (pixels[y * width + x + 1] & EDGE_LEFT) > 0) {
						// turn clockwise
						x++;
						edge = EDGE_LEFT;
						direction = DIRECTION_GREATER;
					} else {
						return returnValue;
					}
				} else {
					// on a top edge, we just scanned from right-to-left:
					if ((pixels[y * width + x] & EDGE_LEFT) > 0) {
						// turn counterclockwise
						edge = EDGE_LEFT;
						direction = DIRECTION_GREATER;
					} else if (x - 1 >= 0
							&& (pixels[y * width + x - 1] & EDGE_TOP) > 0) {
						// go straight
						x--;
						edge = EDGE_TOP;
						direction = DIRECTION_LESSER;
					} else if (y - 1 >= 0
							&& (pixels[(y - 1) * width + x] & EDGE_LEFT) > 0) {
						// turn clockwise
						y--;
						edge = EDGE_LEFT;
						direction = DIRECTION_LESSER;
					} else {
						return returnValue;
					}
				}
			} else if (edge == EDGE_LEFT) {
				if (direction == DIRECTION_GREATER) {
					// on a left edge, we just scanned from top-to-bottom:
					if (y + 1 < height
							&& (pixels[(y + 1) * width + x] & EDGE_TOP) > 0) {
						// turn counterclockwise
						y++;
						edge = EDGE_TOP;
						direction = DIRECTION_GREATER;
					} else if (y + 1 < height
							&& (pixels[(y + 1) * width + x] & EDGE_LEFT) > 0) {
						// go straight
						y++;
						edge = EDGE_LEFT;
						direction = DIRECTION_GREATER;
					} else if (x - 1 >= 0 && y + 1 < height
							&& (pixels[(y + 1) * width + x - 1]
									& EDGE_TOP) > 0) {
						// turn clockwise
						y++;
						x--;
						edge = EDGE_TOP;
						direction = DIRECTION_LESSER;
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
						direction = DIRECTION_LESSER;
					} else if (y - 1 >= 0
							&& (pixels[(y - 1) * width + x] & EDGE_LEFT) > 0) {
						// go straight
						y--;
						edge = EDGE_LEFT;
						direction = DIRECTION_LESSER;
					} else if ((pixels[y * width + x] & EDGE_TOP) > 0) {
						// turn clockwise
						edge = EDGE_TOP;
						direction = DIRECTION_GREATER;
					} else {
						return returnValue;
					}
				}
			}
		}
	}
}
