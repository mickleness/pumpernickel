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
package com.pump.image;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.media.jai.PerspectiveTransform;

import com.pump.math.MathG;

/**
 * Spatial-Coherence optimized implementation of ImageContext.
 * <p>
 * This implementation takes advantage of spatial coherence between pixels to
 * improve the rendering performance.
 * <p>
 * Instead of performing a perspective transform for each individual pixel, this
 * implementation only transforms the four corners of the image, and then uses
 * hyperbolic interpolation for the remaining pixels.
 * <p>
 * In order to take advantage of spacial coherence, the number of tiles should
 * be kept low, because only a small amount of spatial coherence is shared
 * between tiles. Therefore this implementation is best suited for systems with
 * a small number of processors.
 * <p>
 * This class only implements nearest neighbor interpolation. A higher quality
 * interpolation scheme can easily implemented by super sampling pixels from the
 * source image.
 *
 * @author Werner Randelshofer
 */
public class SCImageContext extends ImageContext {

	final int width, height;
	final int[] data;
	final int stride;
	final BufferedImage bi;
	boolean disposed = false;
	ExecutorService executor = null;

	/**
	 * Create a Graphics3D context that paints to a destination image using 6
	 * threads.
	 * 
	 * @param bi
	 *            an RGB or ARGB image.
	 */
	public SCImageContext(BufferedImage bi) {
		this(bi, 6);
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
	public SCImageContext(BufferedImage bi, int numberOfThreads) {
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
			// TODO: in Java 1.8, we can use a nicer API for creating a
			// ForkJoinPool instance:
			// executor = Executors.newWorkStealingPool(threads);
			executor = new ForkJoinPool(numberOfThreads);
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
		int[] p = buf.getData();
		return p;
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
	@Override
	public synchronized void drawImage(BufferedImage img, Point2D topLeft,
			Point2D topRight, Point2D bottomRight, Point2D bottomLeft) {
		if (disposed) {
			throw new IllegalStateException(
					"This image context has been disposed.");
		}

		Point2D srcTopLeft = new Point2D.Double(0, 0);
		Point2D srcTopRight = new Point2D.Double(img.getWidth(), 0);
		Point2D srcBottomLeft = new Point2D.Double(0, img.getHeight());
		Point2D srcBottomRight = new Point2D.Double(img.getWidth(),
				img.getHeight());

		// Compute the transformation matrix
		// ---------------------------
		float minX = minOf(topLeft.getX(), topRight.getX(), bottomLeft.getX(),
				bottomRight.getX());
		float maxX = maxOf(topLeft.getX(), topRight.getX(), bottomLeft.getX(),
				bottomRight.getX());
		float minY = minOf(topLeft.getY(), topRight.getY(), bottomLeft.getY(),
				bottomRight.getY());
		float maxY = maxOf(topLeft.getY(), topRight.getY(), bottomLeft.getY(),
				bottomRight.getY());
		int minXi = max(0, MathG.floorInt(minX) - 1);
		int maxXi = min(width, MathG.ceilInt(maxX) + 1);
		int minYi = max(0, MathG.floorInt(minY) - 1);
		int maxYi = min(height, MathG.ceilInt(maxY) + 1);
		PerspectiveTransform pt = PerspectiveTransform.getQuadToQuad(
				topLeft.getX(), topLeft.getY(), topRight.getX(),
				topRight.getY(), bottomLeft.getX(), bottomLeft.getY(),
				bottomRight.getX(), bottomRight.getY(), srcTopLeft.getX(),
				srcTopLeft.getY(), srcTopRight.getX(), srcTopRight.getY(),
				srcBottomLeft.getX(), srcBottomLeft.getY(),
				srcBottomRight.getX(), srcBottomRight.getY());

		Object interpolationHint = getInterpolationRenderingHint();

		// Gather some data about the source image
		// ---------------------------
		int[] srcPixels = getPixels(img);
		int srcStride = img.getRaster().getWidth();
		int srcWidth = img.getWidth();
		int srcHeight = img.getHeight();
		boolean srcHasAlpha = img.getColorModel().hasAlpha();

		// Take advantage of spatial coherence:
		// Part 1: Compute the perspective transformation for the four corners
		// of the source image. And sort them along the y-axis.
		// ---------------------------
		final Vertex[] v = new Vertex[4];
		for (int i = 0; i < v.length; i++) {
			v[i] = new Vertex();
		}
		float[] pDst = new float[3];
		v[0].load(transform(pt, 0, 0, pDst), 0, 0);
		v[1].load(transform(pt, srcWidth - 1, 0, pDst), width - 1, 0);
		v[2].load(transform(pt, 0, srcHeight - 1, pDst), 0, height - 1);
		v[3].load(transform(pt, srcWidth - 1, srcHeight - 1, pDst), width - 1,
				height - 1);
		Arrays.sort(v);

		// Take advantage of multiple processors:
		// XXX Tiling spoils spatial coherence. Consider using larger tiles.
		// ---------------------------
		if (executor != null) {
			LinkedList<DrawTileRunnable> list = new LinkedList<>();
			int y = minYi;
			while (y <= maxYi) {
				int cy = y / 100;
				int h = min((cy + 1) * 100, maxYi) - y;
				int x = minXi;
				while (x <= maxXi) {
					int cx = x / 100;
					int w = min((cx + 1) * 100, maxXi) - x;
					list.add(new DrawTileRunnable(v, cx << 8 + cy, x, y, w, h,
							interpolationHint, srcPixels, srcStride,
							srcHasAlpha));
					x = (cx + 1) * 100;
				}
				y = (cy + 1) * 100;
			}
			try {
				for (Future<Object> f : executor.invokeAll(list)) {
					f.get();
				}
			} catch (InterruptedException | ExecutionException ex) {
				// suppress
			}
		} else {
			drawTile(v, minXi, minYi, maxXi, maxXi, interpolationHint,
					srcPixels, srcStride, srcHasAlpha);
		}
	}

	private class DrawTileRunnable implements Callable<Object> {
		int id, tileX, tileY, tileWidth, tileHeight;
		int srcStride;
		int[] srcPixels;

		boolean srcHasAlpha;
		Vertex[] vertices;

		Object interpolationHint;

		public DrawTileRunnable(Vertex[] v, int id, int tx, int ty, int tw,
				int th, Object ih, int[] srcPixels, int srcStride,
				boolean srcHasAlpha) {

			this.vertices = v;
			this.id = id;

			this.tileX = tx;
			this.tileY = ty;
			this.tileWidth = tw;
			this.tileHeight = th;
			this.interpolationHint = ih;

			this.srcPixels = srcPixels;
			this.srcStride = srcStride;
			this.srcHasAlpha = srcHasAlpha;
		}

		@Override
		public String toString() {
			return "DrawTileRunnable[ id=" + id + ", x=" + tileX + ", y="
					+ tileY + ", w=" + tileWidth + ", h=" + tileHeight
					+ ", hint=" + interpolationHint + "]";
		}

		@Override
		public Object call() throws Exception {
			drawTile(vertices, max(0, tileX), max(0, tileY),
					min(width, tileX + tileWidth),
					min(height, tileY + tileHeight), interpolationHint,
					srcPixels, srcStride, srcHasAlpha);
			return null;
		}
	}

	private void drawTile(Vertex[] v, int minX, int minY, int maxX, int maxY,
			Object interpolationHint, int[] srcPixels, int srcStride,
			boolean srcHasAlpha) {
		// Take advantage of spatial coherence:
		// Part 2: We have the four corners of a perspective transformed
		// rectangle.
		// They are sorted along the y-axis.
		// We can render the transformed rectangle using three trapezoids,
		// by considering four different cases.
		// ---------------------------

		if (v[2].x < v[3].x) {
			if (v[1].x < v[3].x) {
				renderTrapezoid(v[0], v[3], v[0], v[1],
						max(v[0].getIntY(), minY), min(v[1].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
				renderTrapezoid(v[0], v[3], v[1], v[2],
						max(v[1].getIntY(), minY), min(v[2].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
				renderTrapezoid(v[2], v[3], v[1], v[2],
						max(v[3].getIntY(), minY), min(v[3].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
			} else {
				renderTrapezoid(v[0], v[2], v[0], v[1],
						max(v[0].getIntY(), minY), min(v[1].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
				renderTrapezoid(v[0], v[2], v[1], v[3],
						max(v[1].getIntY(), minY), min(v[2].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
				renderTrapezoid(v[2], v[3], v[1], v[3],
						max(v[2].getIntY(), minY), min(v[3].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
			}
		} else {
			if (v[1].x < v[3].x) {
				renderTrapezoid(v[0], v[1], v[0], v[2],
						max(v[0].getIntY(), minY), min(v[1].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
				renderTrapezoid(v[1], v[3], v[0], v[2],
						max(v[1].getIntY(), minY), min(v[2].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
				renderTrapezoid(v[1], v[3], v[2], v[3],
						max(v[2].getIntY(), minY), min(v[3].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
			} else {
				renderTrapezoid(v[0], v[3], v[0], v[1],
						max(v[0].getIntY(), minY), min(v[1].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
				renderTrapezoid(v[0], v[3], v[1], v[2],
						max(v[1].getIntY(), minY), min(v[2].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
				renderTrapezoid(v[0], v[3], v[2], v[3],
						max(v[2].getIntY(), minY), min(v[3].getIntY(), maxY),
						minX, maxX, srcPixels, srcStride, srcHasAlpha);
			}
		}
	}

	/**
	 * Renders a trapezoid from y=ymin to y<ymax.
	 * 
	 * <pre>
	 *      p1          p3
	 *      /            \
	 *     /==============\   + ymin
	 *    /================\  |
	 *   /==================\ + ymax
	 *  /                    \
	 * p2                    p4
	 * </pre>
	 * 
	 * @param p1
	 *            Top left vertex.
	 * @param p2
	 *            Bottom left vertex.
	 * @param p3
	 *            Top right vertex.
	 * @param p4
	 *            Bottom right vertex.
	 * @param ymin
	 *            Y min.
	 * @param ymax
	 *            Y max.
	 */
	private void renderTrapezoid(Vertex p1, Vertex p2, Vertex p3, Vertex p4,
			int ymin, int ymax, int xmin, int xmax, int[] otherPixels, int os,
			boolean oAlpha) {
		// Nothing to do if trapezoid is empty
		// ---------------------------
		if (ymin > ymax) {
			return;
		}

		// Take advantage of spatial coherence:
		// Part 3: Interpolate along the two vertical edges of the
		// trapezoid.
		// Edge A goes from p1 to p2.
		// Edge B goes from p3 to p4.
		// ---------------------------
		Vertex pA = new Vertex(); // vertex y-interpolated along edge A
		Vertex pB = new Vertex(); // vertex y-interpolated along edge B
		Vertex pX = new Vertex(); // vertex x-interpolated between pA and pB
		float invDA = 1f / (p2.y - p1.y); // y-distance of edge A
		float invDB = 1f / (p4.y - p3.y); // y-distance of edge B

		// Nothing to do if trapezoid is fully to the left or right of the tile
		// ---------------------------
		/*
		 * int x = interpolateX(p1, p2, (ymin - p1.y) * invDA); if (xmax < x &&
		 * xmax < interpolateX(p1, p2, (ymax - p1.y) * invDA) && xmax <
		 * interpolateX(p3, p4, (ymin - p3.y) * invDB) && xmax <
		 * interpolateX(p3, p4, (ymax - p3.y) * invDB)) { return; } else if
		 * (xmin > x && xmin > interpolateX(p1, p2, (ymax - p1.y) * invDA) &&
		 * xmin > interpolateX(p3, p4, (ymin - p3.y) * invDB) && xmin >
		 * interpolateX(p3, p4, (ymax - p3.y) * invDB)) { return; }
		 */

		// Nothing to do if trapezoid is on the left or the right of the tile
		// ---------------------------
		interpolateXWUV(p1, p2, (ymin - p1.y) * invDA, pA);
		if (pA.x > xmax) {
			// => edge A at top of tile is on the right of the tile
			interpolateXWUV(p1, p2, (ymax - p1.y) * invDA, pA);
			if (pA.x > xmax) {
				// => edge A at bottom of tile is on the right of the tile
				interpolateXWUV(p3, p4, (ymin - p3.y) * invDB, pB);
				if (pB.x > xmax) {
					// => edge B at top of tile is on the right of the tile
					interpolateXWUV(p3, p4, (ymax - p3.y) * invDB, pB);
					if (pB.x > xmax) {
						// => edge B at bottom of tile is on the right of tile
						return;
					}
				}
			}
		} else if (pA.x < xmin) {
			// => edge A at top of tile is on the left of the tile
			interpolateXWUV(p1, p2, (ymax - p1.y) * invDA, pA);
			if (pA.x < xmin) {
				// => edge A at bottom of tile on the left of the tile
				interpolateXWUV(p3, p4, (ymin - p3.y) * invDB, pB);
				if (pB.x < xmin) {
					// => edge B at bottom of tile is on the left of the tile
					interpolateXWUV(p3, p4, (ymax - p3.y) * invDB, pB);
					if (pB.x < xmin) {
						// => edge B at bottom of tile is on the left of the
						// tile
						return;
					}
				}
			}
		}

		// Rasterize trapezoid
		// ---------------------------
		for (int y = ymin, yw = ymin * width; y < ymax; y++, yw += width) {
			interpolateXWUV(p1, p2, (y - p1.y) * invDA, pA);
			interpolateXWUV(p3, p4, (y - p3.y) * invDB, pB);
			int xStart = pA.getIntX();
			int xEnd = pB.getIntX();

			if (xStart > xEnd) { // XXX This is an invariant - consider moving
									// it out of the loop!
				renderLine(max(xmin, xEnd), min(xmax, xStart), pA, pB, pX, yw,
						otherPixels, os, oAlpha);
			} else {
				renderLine(max(xmin, xStart), min(xmax, xEnd), pA, pB, pX, yw,
						otherPixels, os, oAlpha);
			}
		}
	}

	/**
	 * Renders a line from xStart to xEnd. This implementation is optimized,
	 * look at {@link #renderLineUnoptimized} for a simpler but slower
	 * implementation.
	 */
	private void renderLine(int xStart, int xEnd, Vertex pA, Vertex pB,
			Vertex pX, int yw, int[] srcPixels, int srcStride, boolean srcAlpha) {
		// Take advantage of spatial coherence:
		// Part 3: Interpolate horizontally between two points on the
		// edges of the trapezoid.

		float invDx = 1f / (pB.x - pA.x);
		float pAx = pA.x;
		float pAw = pA.w;
		float pAu = pA.u;
		float pAv = pA.v;
		float pBw = pB.w;
		float pBu = pB.u;
		float pBv = pB.v;
		float pXw, pXu, pXv;
		if (srcAlpha) {
			for (int x = xStart; x < xEnd; x++) {
				float a = (x - pAx) * invDx;
				{
					a = clamp(a);
					float invA = 1 - a;
					pXw = (invA * pAw + a * pBw);
					pXu = (invA * pAu + a * pBu);
					pXv = (invA * pAv + a * pBv);
				}
				plotA(yw + x, pXu, pXv, pXw, srcPixels, srcStride);
			}
		} else {
			for (int x = xStart; x < xEnd; x++) {
				float a = (x - pAx) * invDx;
				{
					a = clamp(a);
					float invA = 1 - a;
					pXw = (invA * pAw + a * pBw);
					pXu = (invA * pAu + a * pBu);
					pXv = (invA * pAv + a * pBv);
				}
				plot(yw + x, pXu, pXv, pXw, srcPixels, srcStride);
			}
		}
	}

	/** Renders a line from xStart to xEnd. */
	private void renderLineUnoptimized(int xStart, int xEnd, Vertex pA,
			Vertex pB, Vertex pX, int yw, int[] otherPixels, int srcStride,
			boolean srcHasAlpha) {
		// Take advantage of spatial coherence:
		// Part 3: Interpolate horizontally between two points on the
		// edges of the trapezoid.

		float invDx = 1f / (pB.x - pA.x);
		if (srcHasAlpha) {
			for (int x = xStart; x < xEnd; x++) {
				float a = (x - pA.x) * invDx;
				interpolateWUV(pA, pB, a, pX);
				plotA(yw + x, pX, otherPixels, srcStride);
			}
		} else {
			for (int x = xStart; x < xEnd; x++) {
				float a = (x - pA.x) * invDx;
				interpolateWUV(pA, pB, a, pX);
				plot(yw + x, pX, otherPixels, srcStride);
			}
		}
	}

	/** Plots a pixel without alpha compositing. */
	private void plot(int pos, Vertex p, int[] srcPixels, int srcStride) {
		int tx = p.w == 0 ? 0 : (int) (p.u / p.w);
		int ty = p.w == 0 ? 0 : (int) (p.v / p.w);
		int src = srcPixels[ty * srcStride + tx];
		//
		data[pos] = 0xff000000 | src;
	}

	/** Plots a pixel with alpha compositing. */
	private void plotA(int pos, Vertex p, int[] srcPixels, int srcStride) {
		int tx = p.w == 0 ? 0 : (int) (p.u / p.w);
		int ty = p.w == 0 ? 0 : (int) (p.v / p.w);
		int src = srcPixels[ty * srcStride + tx];
		//
		// SRC_OVER composition rule
		int srcA = src >>> 24;
		if (srcA == 255) {
			data[pos] = src;
		} else {
			int r = (src >> 16) & 0xff;
			int g = (src >> 8) & 0xff;
			int b = (src) & 0xff;
			int dst = data[pos];
			int dstAX = (dst >>> 24) * (255 - srcA);
			int dstR = (dst >> 16) & 0xff;
			int dstG = (dst >> 8) & 0xff;
			int dstB = dst & 0xff;
			int srcAX = srcA * 255;
			int resA = (srcAX + dstAX);

			if (resA != 0) {
				r = min(255, (r * srcAX + dstR * dstAX) / resA);
				g = min(255, (g * srcAX + dstG * dstAX) / resA);
				b = min(255, (b * srcAX + dstB * dstAX) / resA);
			}
			data[pos] = (resA / 255 << 24) | (r << 16) | (g << 8) | b;
		}
	}

	/** Plots a pixel without alpha compositing. */
	private void plot(int pos, float u, float v, float w, int[] otherPixels,
			int os) {
		int tx = w == 0 ? 0 : (int) (u / w);
		int ty = w == 0 ? 0 : (int) (v / w);
		int src = otherPixels[ty * os + tx];
		//
		data[pos] = 0xff000000 | src;
	}

	/** Plots a pixel with alpha compositing. */
	private void plotA(int pos, float u, float v, float w, int[] otherPixels,
			int os) {
		int tx = w == 0 ? 0 : (int) (u / w);
		int ty = w == 0 ? 0 : (int) (v / w);
		int src = otherPixels[ty * os + tx];
		//
		// SRC_OVER composition rule
		int srcA = src >>> 24;
		if (srcA == 255) {
			data[pos] = src;
		} else {
			int r = (src >> 16) & 0xff;
			int g = (src >> 8) & 0xff;
			int b = (src) & 0xff;
			int dst = data[pos];
			int dstAX = (dst >>> 24) * (255 - srcA);
			int dstR = (dst >> 16) & 0xff;
			int dstG = (dst >> 8) & 0xff;
			int dstB = dst & 0xff;
			int srcAX = srcA * 255;
			int resA = (srcAX + dstAX);

			if (resA != 0) {
				r = min(255, (r * srcAX + dstR * dstAX) / resA);
				g = min(255, (g * srcAX + dstG * dstAX) / resA);
				b = min(255, (b * srcAX + dstB * dstAX) / resA);
			}
			data[pos] = (resA / 255 << 24) | (r << 16) | (g << 8) | b;
		}
	}

	/**
	 *
	 * @param p1
	 * @param p2
	 * @param a
	 *            Fixed decimal.
	 * @param dest
	 */
	private void interpolateXWUV(Vertex p1, Vertex p2, float a, Vertex dest) {
		a = clamp(a);
		float invA = 1 - a;
		dest.x = (invA * p1.x + a * p2.x);
		// dest.y = (invA * p1.y + a * p2.y);
		dest.w = (invA * p1.w + a * p2.w);
		dest.u = (invA * p1.u + a * p2.u);
		dest.v = (invA * p1.v + a * p2.v);
	}

	private int interpolateX(Vertex p1, Vertex p2, float a) {
		a = clamp(a);
		float invA = 1 - a;
		return (int) (invA * p1.x + a * p2.x);
	}

	private void interpolateWUV(Vertex p1, Vertex p2, float a, Vertex dest) {
		a = clamp(a);
		float invA = 1 - a;
		dest.w = (invA * p1.w + a * p2.w);
		dest.u = (invA * p1.u + a * p2.u);
		dest.v = (invA * p1.v + a * p2.v);
	}

	/**
	 * Commit all changes back to the BufferedImage this context paints to.
	 */
	public synchronized void dispose() {
		if (executor != null) {
			executor.shutdown();
			try {
				executor.awaitTermination(60, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				// abort wait when interrupt occurs
			}
		}
		disposed = true;
	}

	/** Clamps {@code value} to 0 and 1. */
	private static float clamp(float value) {
		return value < 1f ? (value > 0f ? value : 0f) : 1f;
	}

	private static float minOf(double v1, double v2, double v3, double v4) {
		return (float) min(min(v1, v2), min(v3, v4));
	}

	private static float maxOf(double v1, double v2, double v3, double v4) {
		return (float) max(max(v1, v2), max(v3, v4));
	}

	/**
	 * Transforms a point x,y and computes [x/width,y/width,width] coordinates.
	 * 
	 * @param pt
	 * @param x
	 * @param y
	 * @param ptDst
	 * @return
	 */
	private static float[] transform(PerspectiveTransform pt, float x, float y,
			float[] ptDst) {
		double[][] m;
		try {
			m = pt.createInverse().getMatrix(new double[3][3]);
		} catch (Exception ex) {
			/**
			 * This could throw NoninvertibleTransformException, or one compiler
			 * strangely pointed out a possible CloneNotSupportedException
			 */
			InternalError newE = new InternalError();
			newE.initCause(ex);
			throw newE;
		}

		double w = (m[2][0] * x + m[2][1] * y + m[2][2]);
		double wInv = 1.0 / w;
		ptDst[0] = (float) ((m[0][0] * x + m[0][1] * y + m[0][2]) * wInv);
		ptDst[1] = (float) ((m[1][0] * x + m[1][1] * y + m[1][2]) * wInv);
		ptDst[2] = (float) w;
		return ptDst;
	}

	/**
	 * Holds a position on the target image and the corresponding position on
	 * the source image for hyperbolic interpolation.
	 * <p>
	 * Since a perspective transformation happens in a 3 dimensional space, the
	 * point on the target image has three coordinates: {@code x}, {@code y},
	 * {@code w}.
	 * <p>
	 * A point on the source image has two coordinates {@code u}, {@code v}.
	 * <p>
	 * The target position x, y, z can be correctly interpolated linearly.
	 * (Since the target image is flat, we don't really need {@code w}.)
	 * <p>
	 * The source position can not be interpolated linearly, because this would
	 * result in improper foreshortening. However, we can interpolate
	 * {@code 1/w}, {@code u/v} and {@code v/w} linearly.
	 */
	private final static class Vertex implements Comparable<Vertex> {

		/** The x-coordinate of the point on the target image. */
		public float x;
		/** The y-coordinate of the point on the target image. */
		public float y;
		/** The 1/w-coordinate of the point on the target image. */
		public float w;
		/** The u/w-coordinate of the point on the source image. */
		public float u;
		/** The v/w-coordinate of the point on the source image. */
		public float v;

		public void load(float[] xyw, int u, int v) {
			this.x = xyw[0];
			this.y = xyw[1];
			this.w = 1f / xyw[2];
			this.u = u / xyw[2];
			this.v = v / xyw[2];
		}

		public int getIntX() {
			return (int) x;
		}

		public int getIntY() {
			return (int) y;
		}

		@Override
		public int compareTo(Vertex that) {
			return (int) (this.y - that.y);
		}
	}

}