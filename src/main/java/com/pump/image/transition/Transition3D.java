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
package com.pump.image.transition;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import com.pump.image.ImageContext;
import com.pump.math.function.PolynomialFunction;

/**
 * This provides a few static tools to help make 3D transitions.
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2014/05/images-3d-transitions-and.html">Images:
 *      3D Transforms and Transitions</a>
 */
public abstract class Transition3D extends AbstractTransition {

	public abstract static class Point3D {
		public static class Double extends Point3D {
			double x, y, z;

			public Double(double x, double y, double z) {
				this.x = x;
				this.y = y;
				this.z = z;
			}

			public double getX() {
				return x;
			}

			public double getY() {
				return y;
			}

			public double getZ() {
				return z;
			}

			public void setLocation(double x, double y, double z) {
				this.x = x;
				this.y = y;
				this.z = z;
			}

		}

		public abstract double getX();

		public abstract double getY();

		public abstract double getZ();

		public abstract void setLocation(double x, double y, double z);

		@Override
		public String toString() {
			return "(" + getX() + ", " + getY() + ", " + getZ() + ")";
		}
	}

	public static class Quadrilateral3D {
		Point3D topLeft, topRight, bottomRight, bottomLeft;

		public Quadrilateral3D(double topLeftX, double topLeftY,
				double topLeftZ, double topRightX, double topRightY,
				double topRightZ, double bottomRightX, double bottomRightY,
				double bottomRightZ, double bottomLeftX, double bottomLeftY,
				double bottomLeftZ) {
			topLeft = new Point3D.Double(topLeftX, topLeftY, topLeftZ);
			topRight = new Point3D.Double(topRightX, topRightY, topRightZ);
			bottomRight = new Point3D.Double(bottomRightX, bottomRightY,
					bottomRightZ);
			bottomLeft = new Point3D.Double(bottomLeftX, bottomLeftY,
					bottomLeftZ);
		}

		@Override
		public String toString() {
			return "Quadrilateral3D[ " + topLeft + ", " + topRight + ", "
					+ bottomRight + ", " + bottomLeft + "]";
		}

		public Quadrilateral2D toQuadrilateral2D(double width, double height) {
			BasicProjection p = new BasicProjection(width, height);
			return new Quadrilateral2D(p.transform(topLeft),
					p.transform(topRight), p.transform(bottomRight),
					p.transform(bottomLeft));
		}
	}

	public static class Quadrilateral2D {
		Point2D topLeft, topRight, bottomRight, bottomLeft;

		public Quadrilateral2D(Point2D topLeft, Point2D topRight,
				Point2D bottomRight, Point2D bottomLeft) {
			this(topLeft.getX(), topLeft.getY(), topRight.getX(),
					topRight.getY(), bottomRight.getX(), bottomRight.getY(),
					bottomLeft.getX(), bottomLeft.getY());
		}

		public Quadrilateral2D(double topLeftX, double topLeftY,
				double topRightX, double topRightY, double bottomRightX,
				double bottomRightY, double bottomLeftX, double bottomLeftY) {
			topLeft = new Point2D.Double(topLeftX, topLeftY);
			topRight = new Point2D.Double(topRightX, topRightY);
			bottomRight = new Point2D.Double(bottomRightX, bottomRightY);
			bottomLeft = new Point2D.Double(bottomLeftX, bottomLeftY);
		}

		@Override
		public String toString() {
			return "Quadrilateral2D[ " + topLeft + ", " + topRight + ", "
					+ bottomRight + ", " + bottomLeft + "]";
		}

		public boolean isFlippedHorizontally() {
			double centerRightX = (bottomRight.getX() + topRight.getX()) / 2.0;
			double centerRightY = (bottomRight.getY() + topRight.getY()) / 2.0;
			double theta = Math.atan2(topRight.getY() - bottomRight.getY(),
					topRight.getX() - bottomRight.getX());

			AffineTransform tx = new AffineTransform();
			tx.rotate(theta);
			tx.translate(-centerRightX, -centerRightY);
			if (tx.transform(topLeft, null).getY() <= 0)
				return true;
			if (tx.transform(bottomLeft, null).getY() <= 0)
				return true;
			return false;
		}

		public Shape toShape() {
			Path2D p = new Path2D.Double();
			p.moveTo(topLeft.getX(), topLeft.getY());
			p.lineTo(topRight.getX(), topRight.getY());
			p.lineTo(bottomRight.getX(), bottomRight.getY());
			p.lineTo(bottomLeft.getX(), bottomLeft.getY());
			p.closePath();
			return p;
		}
	}

	public static class BasicProjection {
		double w, h;
		PolynomialFunction pf = PolynomialFunction
				.createFit(new double[] { -500, 1.5 }, new double[] { 0, 1 });

		public BasicProjection(double width, double height) {
			w = width;
			h = height;
		}

		public Point2D transform(Point3D p) {
			return transform(p.getX(), p.getY(), p.getZ());
		}

		public Point2D transform(double x, double y, double z) {
			// based on:
			// http://stackoverflow.com/questions/519106/projecting-a-3d-point-to-a-2d-screen-coordinate

			x = x - w / 2.0;
			y = y - h / 2.0;

			double screenX = 0d, screenY = 0d;

			// Camera is defined in XAML as:
			// <Viewport3D.Camera>
			// <PerspectiveCamera Position="0,0,800" LookDirection="0,0,-1" />
			// </Viewport3D.Camera>

			// Translate input point using camera position
			double inputX = x - 0; // cam.Position.X;
			double inputY = y - 0; // cam.Position.Y;
			double inputZ = z - w; // cam.Position.Z;

			double aspectRatio = w / h;

			// Apply projection to X and Y
			screenX = inputX / (-inputZ * Math.tan(Math.PI * 1 / 4));

			screenY = (inputY * aspectRatio)
					/ (-inputZ * Math.tan(Math.PI * 1 / 4));

			// Convert to screen coordinates
			screenX = screenX * w;

			screenY = screenY * h;

			return new Point2D.Double(screenX + w / 2.0, screenY + h / 2.0);
		}
	}

	protected Quadrilateral2D paint(Graphics2D g, int width, int height,
			BufferedImage img, Quadrilateral3D quad3D,
			boolean skipIfFlippedHorizontally) {
		Quadrilateral2D quad2 = quad3D.toQuadrilateral2D(width, height);

		if (skipIfFlippedHorizontally && quad2.isFlippedHorizontally()) {
			return null;
		}

		BufferedImage scratchImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		ImageContext context = ImageContext.create(scratchImage);
		try {
			context.setRenderingHints(g.getRenderingHints());
			context.drawImage(img, quad2.topLeft, quad2.topRight,
					quad2.bottomRight, quad2.bottomLeft);
		} finally {
			context.dispose();
		}

		g.drawImage(scratchImage, 0, 0, null);
		return quad2;

	}

	/**
	 * Flush all z-coordinates with zero.
	 */
	protected static void flushZCoordinateWithSurface(Point3D... points) {
		double maxZ = 0;
		for (Point3D p : points) {
			maxZ = Math.max(maxZ, p.getZ());
		}
		for (Point3D p : points) {
			p.setLocation(p.getX(), p.getY(), p.getZ() - maxZ);
		}
	}
}