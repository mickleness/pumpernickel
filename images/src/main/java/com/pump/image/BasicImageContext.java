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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.Serial;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import com.pump.geom.PerspectiveTransform;

import com.pump.image.BasicImageContext.VariableWidthFunction.LineSegmentIntersectionException;

/**
 * This is a simple Java implementation of image transformations.
 * <p>
 * This is the result of 12 different potential optimizations.
 */
public class BasicImageContext extends ImageContext {
	final int width, height;
	final int[] data;
	final int stride;
	final BufferedImage bi;
	boolean disposed = false;
	ExecutorService executor = null;
	boolean internalExecutor = false;

	/**
	 * Create a Graphics3D context that paints to a destination image using 6
	 * threads.
	 * 
	 * @param bi
	 *            an RGB or ARGB image.
	 */
	public BasicImageContext(BufferedImage bi) {
		this(bi, 6);
		// TODO: in Java 1.8, use this:
		// executor = Executors.newWorkStealingPool(threads);
	}

	/**
	 * Create a Graphics3D context that paints to a destination image.
	 * 
	 * @param bi
	 *            an RGB or ARGB image.
	 * @param executor
	 *            an optional ExecutorService to dispatch tasks to.
	 */
	public BasicImageContext(BufferedImage bi, ExecutorService executor) {
		this(bi, 0);
		this.executor = executor;
		internalExecutor = false;
	}

	/**
	 * Create a Graphics3D context that paints to a destination image.
	 * 
	 * @param bi
	 *            an RGB or ARGB image.
	 * @param numberOfThreads
	 *            if positive then this is the number of threads used to render
	 *            tiles. If zero then calls to <code>drawImage</code> are not
	 *            multithreaded.
	 */
	public BasicImageContext(BufferedImage bi, int numberOfThreads) {
		int type = bi.getType();
		if (!(type == BufferedImage.TYPE_INT_ARGB || type == BufferedImage.TYPE_INT_RGB)) {
			throw new IllegalArgumentException(
					"only TYPE_INT_RGB and TYPE_INT_ARGB are supported");
		}
		this.bi = bi;
		width = bi.getWidth();
		height = bi.getHeight();
		stride = bi.getRaster().getWidth();
		data = getPixels(bi);
		if (numberOfThreads > 0) {
			executor = new ForkJoinPool(numberOfThreads);
			internalExecutor = true;
		}
	}

	/** Return all the pixels in the argument in ARGB format. */
	protected int[] getPixels(BufferedImage bi) {
		if ((bi.getType() != BufferedImage.TYPE_INT_ARGB && bi.getType() != BufferedImage.TYPE_INT_RGB)
				|| !(bi.getRaster().getDataBuffer() instanceof DataBufferInt)) {
			BufferedImage tmp = bi;
			bi = new BufferedImage(tmp.getWidth(), tmp.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.drawImage(tmp, 0, 0, null);
			g.dispose();
		}

		DataBufferInt buf = (DataBufferInt) bi.getRaster().getDataBuffer();
		return buf.getData();
	}

	/** This calculates the left and right edges of a horizontal strip. */
	static abstract class HorizontalStripFunction {

		/**
		 * Return the left and right x-coordinates (inclusive) that need to be
		 * processed in a given row.
		 * 
		 * @param y
		 *            the row to process.
		 * @param dst
		 *            a 2-pixel array storing the left and right x-coordinates
		 *            (respectively)
		 */
		public abstract void getXEndpoints(int y, int[] dst);

		/**
		 * Create a similar new function that enforces a new min and max
		 * x-value. This does not replace any previous min/max values: it
		 * further constrains them.
		 */
		public abstract HorizontalStripFunction derive(int newMinX, int newMaxX);
	}

	/**
	 * Without doing anything clever: this returns the same left/right edges
	 * each time. (This effectively makes the pixels we iterate over a
	 * rectangle.)
	 */
	static class FixedWidthFunction extends HorizontalStripFunction {
		final int minX, maxX;

		FixedWidthFunction(int minX, int maxX) {
			this.minX = minX;
			this.maxX = maxX;
		}

		public void getXEndpoints(int y, int[] dst) {
			dst[0] = minX;
			dst[1] = maxX;
		}

		public HorizontalStripFunction derive(int newMinX, int newMaxX) {
			return new FixedWidthFunction(Math.max(newMinX, minX), Math.min(
					newMaxX, maxX));
		}
	}

	/**
	 * This chooses left/right edges that follow the contour of a quadrilateral.
	 * (It rounds a little bit to make sure all the relevant pixels will be
	 * covered.)
	 */
	static class VariableWidthFunction extends HorizontalStripFunction {
		final int minX, maxX;

		/** Sort Point2Ds in ascending y value. */
		static Comparator<Point2D> yComparator = (o1, o2) -> {
			if (o1.getY() < o2.getY()) {
				return -1;
			} else if (o1.getY() > o2.getY()) {
				return 1;
			} else return Double.compare(o1.getX(), o2.getX());
		};

		/** Sort Point2Ds in ascending y value. */
		static Comparator<Point2D> xComparator = (o1, o2) -> {
			if (o1.getX() < o2.getX()) {
				return -1;
			} else if (o1.getX() > o2.getX()) {
				return 1;
			} else return Double.compare(o1.getY(), o2.getY());
		};

		static class LineSegmentIntersectionException extends Exception {
			@Serial
			private static final long serialVersionUID = 1L;
		}

		/**
		 * The points that trace the left edge of the image, from top to bottom.
		 */
		final int[] leftX, leftY;

		/**
		 * The points that trace the right edge of the image, from top to
		 * bottom.
		 */
		final int[] rightX, rightY;

		private static boolean intersect(Point2D a1, Point2D a2, Point2D b1,
				Point2D b2) {
			return Line2D.linesIntersect(a1.getX(), a1.getY(), a2.getX(),
					a2.getY(), b1.getX(), b1.getY(), b2.getX(), b2.getY());
		}

		VariableWidthFunction(int minX, int maxX, Point2D topLeft,
				Point2D topRight, Point2D bottomRight, Point2D bottomLeft)
				throws LineSegmentIntersectionException {
			this.minX = minX;
			this.maxX = maxX;

			if (intersect(topLeft, bottomLeft, topRight, bottomRight))
				throw new LineSegmentIntersectionException();
			if (intersect(topLeft, topRight, bottomLeft, bottomRight))
				throw new LineSegmentIntersectionException();

			SortedSet<Point2D> horizontalList = new TreeSet<>(
					xComparator);
			horizontalList.add(topLeft);
			horizontalList.add(topRight);
			horizontalList.add(bottomLeft);
			horizontalList.add(bottomRight);
			double leftX = horizontalList.first().getX();
			double rightX = horizontalList.last().getX();

			SortedSet<Point> left = new TreeSet<>(yComparator);
			SortedSet<Point> right = new TreeSet<>(yComparator);

			Point2D[] path = new Point2D[] { topLeft, topRight, bottomRight,
					bottomLeft };
			for (int a = 0; a < path.length; a++) {
				int prev = (a - 1 + path.length) % path.length;
				int next = (a + 1 + path.length) % path.length;

				boolean bottomMostVertex = path[prev].getY() <= path[a].getY()
						&& path[next].getY() <= path[a].getY();
				boolean topMostVertex = path[prev].getY() >= path[a].getY()
						&& path[next].getY() >= path[a].getY();

				if (path[a].getX() == leftX) {
					addPoint2D(left, path[a], false);

					if (bottomMostVertex || topMostVertex) {
						if (path[prev].getX() <= path[a].getX()) {
							addPoint2D(left, path[prev], false);
						} else if (path[next].getX() <= path[a].getX()) {
							addPoint2D(left, path[next], false);
						}
					} else {
						addPoint2D(left, path[prev], false);
						addPoint2D(left, path[next], false);
					}
				}

				if (path[a].getX() == rightX) {
					addPoint2D(right, path[a], true);

					if (bottomMostVertex || topMostVertex) {
						if (path[prev].getX() >= path[a].getX()) {
							addPoint2D(right, path[prev], true);
						} else if (path[next].getX() >= path[a].getX()) {
							addPoint2D(right, path[next], true);
						}
					} else {
						addPoint2D(right, path[prev], true);
						addPoint2D(right, path[next], true);
					}
				}
			}

			left = removeRedundantYs(left, false);
			right = removeRedundantYs(right, true);

			this.leftX = new int[left.size()];
			this.leftY = new int[left.size()];
			store(left, this.leftX, this.leftY);

			this.rightX = new int[right.size()];
			this.rightY = new int[right.size()];
			store(right, this.rightX, this.rightY);
		}

		VariableWidthFunction(int minX, int maxX, int[] leftX, int[] leftY,
				int[] rightX, int[] rightY) {
			this.minX = minX;
			this.maxX = maxX;
			this.leftX = leftX;
			this.leftY = leftY;
			this.rightX = rightX;
			this.rightY = rightY;
		}

		public HorizontalStripFunction derive(int newMinX, int newMaxX) {
			int finalMin = Math.max(newMinX, minX);
			int finalMax = Math.min(newMaxX, maxX);
			return new VariableWidthFunction(finalMin, finalMax, leftX, leftY,
					rightX, rightY);
		}

		private SortedSet<Point> removeRedundantYs(Set<Point> points,
				boolean useGreaterValue) {
			SortedMap<Integer, Integer> map = new TreeMap<>();
			for (Point p : points) {
				Integer x = map.get(p.y);
				if (x == null) {
					map.put(p.y, p.x);
				} else if (useGreaterValue) {
					map.put(p.y, Math.max(p.x, x));
				} else {
					map.put(p.y, Math.min(p.x, x));
				}
			}
			SortedSet<Point> returnValue = new TreeSet<>(yComparator);
			for (Integer y : map.keySet()) {
				Integer x = map.get(y);
				returnValue.add(new Point(x, y));
			}
			return returnValue;
		}

		private void store(Set<Point> points, int[] xs, int[] ys) {
			Iterator<Point> pIter = points.iterator();
			int ctr = 0;
			while (pIter.hasNext()) {
				Point p = pIter.next();
				xs[ctr] = p.x;
				ys[ctr] = p.y;
				ctr++;
			}
		}

		private void addPoint2D(Set<Point> dest, Point2D p, boolean roundUp) {
			int y = (int) p.getY();
			int x = (int) (roundUp ? Math.ceil(p.getX()) : Math.floor(p.getX()));
			if (y == p.getY()) {
				dest.add(new Point(x, y));
			} else {
				dest.add(new Point(x, y));
				dest.add(new Point(x, y + 1));
			}
		}

		public void getXEndpoints(int y, int[] dst) {
			/*
			 * This doesn't use a binary search, but the list is only going to
			 * be about 6 units long.
			 */

			// calculate left edge (dst[0]):
			boolean leftEdge = false;
			for (int i = 0; i < leftX.length - 1 && (!leftEdge); i++) {
				int ymin = leftY[i];
				int ymax = leftY[i + 1];
				if (y >= ymin && y <= ymax) {
					int yrange = ymax - ymin;

					int xmin = leftX[i];
					int xmax = leftX[i + 1];
					int xrange = xmax - xmin;
					int x = xmin + xrange * (y - ymin) / yrange;
					dst[0] = x;
					leftEdge = true;
				}
			}
			if ((!leftEdge) || dst[0] < minX)
				dst[0] = minX;

			// calculate right edge (dst[1]):
			boolean rightEdge = false;
			for (int i = 0; i < rightX.length - 1 && (!rightEdge); i++) {
				int ymin = rightY[i];
				int ymax = rightY[i + 1];
				if (y >= ymin && y <= ymax) {
					int yrange = ymax - ymin;

					int xmin = rightX[i];
					int xmax = rightX[i + 1];
					int xrange = xmax - xmin;
					int x = xmin + xrange * (y - ymin) / yrange;
					dst[1] = x;
					rightEdge = true;
				}
			}
			if ((!rightEdge) || dst[1] > maxX)
				dst[1] = maxX;
		}
	}

	/**
	 * Draw an image to this Graphics3D.
	 * <p>
	 * This respects the interpolation rendering hints. When the interpolation
	 * hint is missing, this will also consult the antialiasing hint or the
	 * render hint. The bilinear hint is used by default.
	 * <p>
	 * This uses a source over composite.
	 * 
	 * @param img
	 *            the image to draw.
	 * @param topLeft
	 *            where the top-left corner of this image will be painted.
	 * @param topRight
	 *            where the top-right corner of this image will be painted.
	 * @param bottomRight
	 *            where the bottom-right corner of this image will be painted.
	 * @param bottomLeft
	 *            where the bottom-left corner of this image will be painted.
	 */
	public synchronized void drawImage(BufferedImage img, Point2D topLeft,
			Point2D topRight, Point2D bottomRight, Point2D bottomLeft) {
		if (disposed)
			throw new IllegalStateException("This context has been disposed.");

		// we possibly could optimize this one day by detecting if drawing Image
		// X will
		// collide with existing instructions already in the queue?
		processQueue();

		Point2D srcTopLeft = new Point2D.Double(0, 0);
		Point2D srcTopRight = new Point2D.Double(img.getWidth(), 0);
		Point2D srcBottomLeft = new Point2D.Double(0, img.getHeight());
		Point2D srcBottomRight = new Point2D.Double(img.getWidth(),
				img.getHeight());

		double minX = Math.min(Math.min(topLeft.getX(), topRight.getX()),
				Math.min(bottomLeft.getX(), bottomRight.getX()));
		double maxX = Math.max(Math.max(topLeft.getX(), topRight.getX()),
				Math.max(bottomLeft.getX(), bottomRight.getX()));
		double minY = Math.min(Math.min(topLeft.getY(), topRight.getY()),
				Math.min(bottomLeft.getY(), bottomRight.getY()));
		double maxY = Math.max(Math.max(topLeft.getY(), topRight.getY()),
				Math.max(bottomLeft.getY(), bottomRight.getY()));
		int minXi = (int) Math.floor(minX) - 1;
		int maxXi = (int) Math.ceil(maxX) + 1;
		int minYi = (int) Math.floor(minY) - 1;
		int maxYi = (int) Math.ceil(maxY) + 1;

		// bound everything from [0,limit)
		minXi = Math.max(0, Math.min(width - 1, minXi));
		maxXi = Math.max(0, Math.min(width - 1, maxXi));
		minYi = Math.max(0, Math.min(height - 1, minYi));
		maxYi = Math.max(0, Math.min(height - 1, maxYi));

		PerspectiveTransform pt = PerspectiveTransform.getQuadToQuad(
				topLeft.getX(), topLeft.getY(), topRight.getX(),
				topRight.getY(), bottomLeft.getX(), bottomLeft.getY(),
				bottomRight.getX(), bottomRight.getY(), srcTopLeft.getX(),
				srcTopLeft.getY(), srcTopRight.getX(), srcTopRight.getY(),
				srcBottomLeft.getX(), srcBottomLeft.getY(),
				srcBottomRight.getX(), srcBottomRight.getY());

		int[] otherPixels = getPixels(img);
		int oStride = img.getRaster().getWidth();
		int oWidth = img.getWidth();
		int oHeight = img.getHeight();
		boolean oHasAlpha = img.getColorModel().hasAlpha();

		HorizontalStripFunction stripFunction;
		try {
			stripFunction = new VariableWidthFunction(minXi, maxXi, topLeft,
					topRight, bottomRight, bottomLeft);
		} catch (LineSegmentIntersectionException e) {
			stripFunction = new FixedWidthFunction(minXi, maxXi);
		}

		Object interpolationHint = getInterpolationRenderingHint();

		if (executor != null) {
			int y = minYi;
			while (y <= maxYi) {
				int cy = y / 100;
				int h = Math.min((cy + 1) * 100, maxYi) - y;
				int x = minXi;
				while (x <= maxXi) {
					int cx = x / 100;
					int w = Math.min((cx + 1) * 100, maxXi) - x;
					TileInstructions i = new TileInstructions(x, y, w - 1,
							h - 1, interpolationHint, otherPixels, pt,
							oHasAlpha, oWidth, oHeight, oStride, stripFunction);
					synchronized (instructionQueue) {
						instructionQueue.add(i);
					}

					x = (cx + 1) * 100;
				}
				y = (cy + 1) * 100;
			}

			for (int a = 0; a < 4; a++) {
				executor.submit(new DrawTileRunnable());
			}
		} else {
			drawTile(minXi, minYi, maxXi, maxYi, interpolationHint,
					otherPixels, pt, oHasAlpha, oWidth, oHeight, oStride,
					stripFunction);
		}
	}

	static class TileInstructions {
		boolean active = false;
		int tileX, tileY, tileWidth, tileHeight, oWidth, oHeight, oStride;
		Object renderingHint;
		int[] otherPixels;
		PerspectiveTransform transform;
		boolean oHasAlpha;
		HorizontalStripFunction stripFunction;

		public TileInstructions(int x, int y, int w, int h, Object rh,
				int[] otherPixels, PerspectiveTransform pt, boolean oHasAlpha,
				int oWidth, int oHeight, int oStride,
				HorizontalStripFunction stripFunction) {
			this.tileX = x;
			this.tileY = y;
			this.tileWidth = w;
			this.tileHeight = h;
			this.renderingHint = rh;
			this.otherPixels = otherPixels;
			this.transform = pt;
			this.oHasAlpha = oHasAlpha;
			this.oWidth = oWidth;
			this.oHeight = oHeight;
			this.oStride = oStride;
			this.stripFunction = stripFunction.derive(tileX, tileX + tileWidth);
		}

		@Override
		public String toString() {
			return "TileInstructions[ x=" + tileX + ", y=" + tileY + ", w="
					+ tileWidth + ", h=" + tileHeight + ", hint="
					+ renderingHint + "]";
		}

	}

	final List<TileInstructions> instructionQueue = new LinkedList<>();

	class DrawTileRunnable implements Runnable {
		public void run() {
			processQueue();
		}
	}

	protected void drawTile(int minXi, int minYi, int maxXi, int maxYi,
			Object interpolationHint, int[] otherPixels,
			PerspectiveTransform pt, boolean oHasAlpha, int oWidth,
			int oHeight, int oStride, HorizontalStripFunction stripFunction) {

		double transformedX, transformedY;
		double m00, m01, m02, m10, m11, m12, m20, m21, m22, w;

		{
			double[][] matrix = new double[3][3];
			pt.getMatrix(matrix);
			m00 = matrix[0][0];
			m01 = matrix[0][1];
			m02 = matrix[0][2];
			m10 = matrix[1][0];
			m11 = matrix[1][1];
			m12 = matrix[1][2];
			m20 = matrix[2][0];
			m21 = matrix[2][1];
			m22 = matrix[2][2];
		}

		int[] xEndpoints = new int[2];

		if (RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
				.equals(interpolationHint)) {
			if (oHasAlpha) {
				for (int y = minYi; y <= maxYi; y++) {
					int yw = y * stride;
					stripFunction.getXEndpoints(y, xEndpoints);
					for (int x = xEndpoints[0]; x <= xEndpoints[1]; x++) {

						// transform (x,y) to (transformedX, transformedY):
						w = m20 * (double) x + m21 * (double) y + m22;
						transformedX = (m00 * (double) x + m01 * (double) y + m02) / w;
						transformedY = (m10 * (double) x + m11 * (double) y + m12) / w;

						int newX = (int) (transformedX + .5);
						int newY = (int) (transformedY + .5);
						if (newY >= 0 && newY < oHeight && newX >= 0
								&& newX < oWidth) {
							int src = otherPixels[newY * oStride + newX];
							int srcA = src >>> 24;
							if (srcA == 255) {
								data[yw + x] = src;
							} else if (srcA > 0) {
								int r = (src >> 16) & 0xff;
								int g = (src >> 8) & 0xff;
								int b = src & 0xff;
								int dst = data[yw + x];
								int dstA = (dst >> 24) & 0xff;
								int dstAX = (dstA) * (255 - srcA);
								int dstR = (dst >> 16) & 0xff;
								int dstG = (dst >> 8) & 0xff;
								int dstB = dst & 0xff;
								int srcAX = srcA * 255;
								int resA = srcAX + dstAX;

								if (resA != 0) {
									r = (r * srcAX + dstR * dstAX) / resA;
									g = (g * srcAX + dstG * dstAX) / resA;
									b = (b * srcAX + dstB * dstAX) / resA;
									data[yw + x] = (resA / 255 << 24)
											| ((r > 255) ? 0xff0000 : r << 16)
											| ((g > 255) ? 0xff00 : g << 8)
											| ((b > 255) ? 0xff : b);
								}
							}
						}
					}
				}
			} else {
				for (int y = minYi; y <= maxYi; y++) {
					int yw = y * stride;
					stripFunction.getXEndpoints(y, xEndpoints);
					for (int x = xEndpoints[0]; x <= xEndpoints[1]; x++) {

						// transform (x,y) to (transformedX, transformedY):
						w = m20 * (double) x + m21 * (double) y + m22;
						transformedX = (m00 * (double) x + m01 * (double) y + m02) / w;
						transformedY = (m10 * (double) x + m11 * (double) y + m12) / w;

						int newX = (int) (transformedX + .5);
						int newY = (int) (transformedY + .5);
						if (newY >= 0 && newY < oHeight && newX >= 0
								&& newX < oWidth) {
							data[yw + x] = otherPixels[newY * oStride + newX];
						}
					}
				}
			}
		} else {
			int windowLength;
			if (RenderingHints.VALUE_INTERPOLATION_BICUBIC
					.equals(interpolationHint)) {
				windowLength = 4;
			} else {
				windowLength = 2;
			}
			double windowLengthD = windowLength;
			double incr = 1.0 / (windowLengthD - 1.0);
			int windowArea = windowLength * windowLength;

			if (oHasAlpha) {
				for (int y = minYi; y <= maxYi; y++) {
					int yw = y * stride;
					stripFunction.getXEndpoints(y, xEndpoints);
					for (int x = xEndpoints[0]; x <= xEndpoints[1]; x++) {
						int samples = windowArea;
						int srcA = 0;
						int r = 0;
						int g = 0;
						int b = 0;
						for (double dx = 0; dx < windowLengthD; dx++) {
							for (double dy = 0; dy < windowLengthD; dy++) {
								double x2 = (double) x + dx * incr;
								double y2 = (double) y + dy * incr;

								// transform (x,y) to (transformedX,
								// transformedY):
								w = m20 * x2 + m21 * y2 + m22;
								transformedX = (m00 * x2 + m01 * y2 + m02) / w;
								transformedY = (m10 * x2 + m11 * y2 + m12) / w;

								int newX = (int) (transformedX - .00001);
								int newY = (int) (transformedY - .00001);
								if (newY >= 0 && newY < oHeight && newX >= 0
										&& newX < oWidth) {
									int opix = otherPixels[newY * oStride
											+ newX];
									srcA += (opix >> 24) & 0xff;
									r += opix & 0xff0000;
									g += opix & 0xff00;
									b += opix & 0xff;
								} else {
									samples--;
								}
							}
						}
						if (samples > 0) {
							srcA = srcA / samples;
							r = (r >>> 16) / samples;
							g = (g >>> 8) / samples;
							b = b / samples;
							if (srcA == 255) {
								data[yw + x] = 0xff000000 | (r << 16)
										| (g << 8) | b;
							} else if (srcA > 0) {
								int dst = data[yw + x];
								int dstAX = (dst >>> 24) * (255 - srcA);
								int dstR = (dst >> 16) & 0xff;
								int dstG = (dst >> 8) & 0xff;
								int dstB = dst & 0xff;
								int srcAX = srcA * 255;
								int resA = (srcAX + dstAX);

								if (resA != 0) {
									r = (r * srcAX + dstR * dstAX) / resA;
									g = (g * srcAX + dstG * dstAX) / resA;
									b = (b * srcAX + dstB * dstAX) / resA;
									data[yw + x] = (resA / 255 << 24)
											| ((r > 255) ? 0xff0000 : r << 16)
											| ((g > 255) ? 0xff00 : g << 8)
											| ((b > 255) ? 0xff : b);
								}
							}
						}
					}
				}
			} else {
				for (int y = minYi; y <= maxYi; y++) {
					int yw = y * stride;
					stripFunction.getXEndpoints(y, xEndpoints);
					for (int x = xEndpoints[0]; x <= xEndpoints[1]; x++) {

						int samples = windowArea;
						int srcA = 0;
						int r = 0;
						int g = 0;
						int b = 0;
						for (double dx = 0; dx < windowLengthD; dx++) {
							for (double dy = 0; dy < windowLengthD; dy++) {
								double x2 = (double) x + dx * incr;
								double y2 = (double) y + dy * incr;

								// transform (x,y) to (transformedX,
								// transformedY):
								w = m20 * x2 + m21 * y2 + m22;
								transformedX = (m00 * x2 + m01 * y2 + m02) / w;
								transformedY = (m10 * x2 + m11 * y2 + m12) / w;

								int newX = (int) (transformedX - .00001);
								int newY = (int) (transformedY - .00001);
								if (newY >= 0 && newY < oHeight && newX >= 0
										&& newX < oWidth) {
									int opix = otherPixels[newY * oStride
											+ newX];
									srcA += 255;
									r += opix & 0xff0000;
									g += opix & 0xff00;
									b += opix & 0xff;
								} else {
									samples--;
								}
							}
						}
						if (samples > 0) {
							srcA = srcA / samples;
							r = (r >>> 16) / samples;
							g = (g >>> 8) / samples;
							b = b / samples;
							if (srcA == 255) {
								data[yw + x] = 0xff000000 | (r << 16)
										| (g << 8) | b;
							} else if (srcA > 0) {
								int dst = data[yw + x];
								int dstAX = (dst >>> 24) * (255 - srcA);
								int dstR = (dst >> 16) & 0xff;
								int dstG = (dst >> 8) & 0xff;
								int dstB = dst & 0xff;
								int srcAX = srcA * 255;
								int resA = (srcAX + dstAX);

								if (resA != 0) {
									r = (r * srcAX + dstR * dstAX) / resA;
									g = (g * srcAX + dstG * dstAX) / resA;
									b = (b * srcAX + dstB * dstAX) / resA;
									data[yw + x] = (resA / 255 << 24)
											| ((r > 255) ? 0xff0000 : r << 16)
											| ((g > 255) ? 0xff00 : g << 8)
											| ((b > 255) ? 0xff : b);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This blocks until instructionQueue is empty.
	 */
	protected void processQueue() {
		while (true) {
			TileInstructions activeTile = null;
			synchronized (instructionQueue) {
				Iterator<TileInstructions> iter = instructionQueue.iterator();
				boolean skippedTile = false;
				while (iter.hasNext() && activeTile == null) {
					TileInstructions instr = iter.next();
					if (!instr.active) {
						instr.active = true;
						activeTile = instr;
					} else {
						skippedTile = true;
					}
				}

				if (activeTile == null) {
					if (skippedTile) {
						// we don't have anything to do in this thread,
						// but we should keep blocking until other threads
						// finish:
						try {
							instructionQueue.wait(1000);
						} catch (InterruptedException e) {
						}
					} else {
						// everyone is done, the queue is empty.
						return;
					}
				}
			}

			if (activeTile != null) {
				try {
					drawTile(activeTile.tileX, activeTile.tileY,
							activeTile.tileX + activeTile.tileWidth,
							activeTile.tileY + activeTile.tileHeight,
							activeTile.renderingHint, activeTile.otherPixels,
							activeTile.transform, activeTile.oHasAlpha,
							activeTile.oWidth, activeTile.oHeight,
							activeTile.oStride, activeTile.stripFunction);
				} finally {
					synchronized (instructionQueue) {
						instructionQueue.remove(activeTile);
						instructionQueue.notifyAll();
					}
				}
			}
		}
	}

	/**
	 * Commit all changes back to the BufferedImage this context paints to.
	 */
	@Override
	public synchronized void close() {
		processQueue();

		if (executor != null) {
			if (internalExecutor) {
				executor.shutdown();
				try {
					executor.awaitTermination(60, TimeUnit.MINUTES);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		disposed = true;
	}
}