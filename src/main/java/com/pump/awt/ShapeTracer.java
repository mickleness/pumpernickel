package com.pump.awt;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.HashSet;

public class ShapeTracer {
	private static final byte EMPTY = 0;
	private static final byte OPAQUE_PROCESSED = 1;
	private static final byte OPAQUE_UNPROCESSED = 2;

	private static final byte DIRECTION_RIGHT = 0;
	private static final byte DIRECTION_UP = 1;
	private static final byte DIRECTION_DOWN = 2;
	private static final byte DIRECTION_LEFT = 3;
	private static final byte DIRECTION_UNDEFINED = 4;

	public Shape trace(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		int[] pixels = new int[width * height];
		img.getRGB(0, 0, width, height, pixels, 0, width);

		// our mask has includes a 1px empty border on all sides
		byte[] mask = new byte[(width + 2) * (height + 2)];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int alpha = (pixels[y * width + x] >> 24) & 0xff;
				if (alpha > 250) {
					mask[(y + 1) * (width + 2) + (x + 1)] = OPAQUE_UNPROCESSED;
				} else {
					mask[(y + 1) * (width + 2) + (x + 1)] = EMPTY;
				}
			}
		}

		Path2D path = new Path2D.Float(PathIterator.WIND_NON_ZERO);

		width += 2;
		height += 2;

		for (int y = 0; y < height; y++) {
			for (int x = 1; x < width; x++) {
				if (mask[y * width + x - 1] == EMPTY
						&& mask[y * width + x] == OPAQUE_UNPROCESSED) {
					traceMask(mask, width, height, x, y, path);
				}
			}
		}

		return path;
	}

	/**
	 * This writes a series of orthogonal points, and collapses adjacent runs of
	 * points into individual lines.
	 */
	static class PathWriter {
		Path2D dest;
		int currentX, currentY, lastCommittedX, lastCommittedY;
		byte direction = DIRECTION_UNDEFINED;
		Collection<Point> points = new HashSet<>();
		boolean closed = false;

		public PathWriter(Path2D dest, int startX, int startY) {
			this.dest = dest;
			points.add(new Point(startX, startY));
			dest.moveTo(startX, startY);
			lastCommittedX = currentX = startX;
			lastCommittedY = currentY = startY;
		}

		public boolean isClosed() {
			return closed;
		}

		/**
		 * 
		 * @param newX
		 * @param newY
		 */
		public void add(int newX, int newY) {
			if (closed)
				return;
			if (newX == currentX && newY == currentY) {
				return;
			}
			if (!points.add(new Point(newX, newY))) {
				if (currentX != lastCommittedX || currentY != lastCommittedY)
					dest.lineTo(currentX, currentY);
				dest.closePath();
				closed = true;
				return;
			}

			byte newDirection = DIRECTION_UNDEFINED;

			if (currentX + 1 == newX && currentY == newY) {
				newDirection = DIRECTION_RIGHT;
			} else if (currentX - 1 == newX && currentY == newY) {
				newDirection = DIRECTION_LEFT;
			} else if (currentX == newX && currentY + 1 == newY) {
				newDirection = DIRECTION_DOWN;
			} else if (currentX == newX && currentY - 1 == newY) {
				newDirection = DIRECTION_UP;
			} else {
				throw new RuntimeException(
						"Attempt to step to noncontiguous pixel: (" + currentX
								+ ", " + currentY + ") to (" + newX + ", "
								+ newY + ")");
			}

			if (direction != newDirection || direction == DIRECTION_UNDEFINED) {
				// we changed direction, so commit this line:
				dest.lineTo(currentX, currentY);
				lastCommittedX = currentX;
				lastCommittedY = currentY;
			}
			direction = newDirection;
			currentX = newX;
			currentY = newY;
			return;
		}
	}

	protected void traceMask(byte[] mask, int width, int height, int startX,
			int startY, Path2D path) {
		boolean leftSide = true;
		boolean topSide = true;
		PathWriter writer = new PathWriter(path, startX + (leftSide ? -1 : 0),
				startY + (topSide ? -1 : 0));
		int x = startX;
		int y = startY;
		byte direction = DIRECTION_RIGHT;
		while (!writer.isClosed()) {
			switch (direction) {
			case DIRECTION_RIGHT:
				if (mask[(y - 1) * width + x] != EMPTY) {
					// try counter-clockwise:
					direction = DIRECTION_UP;
					topSide = false;
					y--;
				} else if (mask[y * width + x + 1] != EMPTY) {
					// continue right
					x++;
				} else if (mask[(y + 1) * width + x] != EMPTY) {
					// try clockwise
					direction = DIRECTION_DOWN;
					leftSide = false;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					y++;
				} else {
					// reverse
					direction = DIRECTION_LEFT;
					leftSide = false;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					topSide = false;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					x--;
				}
				writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
				mask[y * width + x] = OPAQUE_PROCESSED;
				break;
			case DIRECTION_DOWN:
				if (mask[y * width + x + 1] != EMPTY) {
					// try counter-clockwise:
					direction = DIRECTION_RIGHT;
					leftSide = true;
					x++;
				} else if (mask[(y + 1) * width + x] != EMPTY) {
					// continue down
					y++;
				} else if (mask[y * width + x - 1] != EMPTY) {
					// try clockwise
					direction = DIRECTION_LEFT;
					topSide = false;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					x--;
				} else {
					// reverse
					direction = DIRECTION_UP;
					topSide = false;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					leftSide = true;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					y--;
				}
				writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
				mask[y * width + x] = OPAQUE_PROCESSED;
				break;
			case DIRECTION_LEFT:
				if (mask[(y + 1) * width + x] != EMPTY) {
					// try counter-clockwise:
					direction = DIRECTION_DOWN;
					topSide = true;
					y++;
				} else if (mask[y * width + x - 1] != EMPTY) {
					// continue left
					x--;
				} else if (mask[(y - 1) * width + x] != EMPTY) {
					// try clockwise
					direction = DIRECTION_UP;
					leftSide = true;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					y--;
				} else {
					// reverse
					direction = DIRECTION_RIGHT;
					leftSide = true;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					topSide = true;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					x++;
				}
				writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
				mask[y * width + x] = OPAQUE_PROCESSED;
				break;
			case DIRECTION_UP:
				if (mask[y * width + x - 1] != EMPTY) {
					// try counter-clockwise:
					direction = DIRECTION_LEFT;
					leftSide = false;
					x--;
				} else if (mask[(y - 1) * width + x] != EMPTY) {
					// continue up
					y--;
				} else if (mask[y * width + x + 1] != EMPTY) {
					// try clockwise
					direction = DIRECTION_RIGHT;
					topSide = true;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					x++;
				} else {
					// reverse
					direction = DIRECTION_DOWN;
					topSide = true;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					leftSide = false;
					writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
					y++;
				}
				writer.add(x + (leftSide ? -1 : 0), y + (topSide ? -1 : 0));
				mask[y * width + x] = OPAQUE_PROCESSED;
				break;
			default:
				// this should never happen
				throw new RuntimeException(
						"Unexpected direction: " + direction);
			}
		}
	}
}
