package com.pump.geom;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

/**
 * This is a simple star-shaped polygon. (According to wikipedia this is what's
 * known as a "simple concave isotoxal 2n-gon".)
 * <p>
 * You can customize the number of points (tips) of the star, and the two radii.
 * The first radius is always oriented directly upward, so the stars are all
 * horizontally symmetrical.
 * <p>
 * The star is always centered around (0,0), and at most it will fit inside a
 * rectangle that is (2 * maxRadius) x (2 * maxRadius) in size.
 */
public class StarPolygon extends AbstractShape
		implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	protected int numberOfPoints;
	protected float radius1, radius2;
	protected float centerX = 0;
	protected float centerY = 0;

	/**
	 * Create a traditional 5-tipped star centered at (0,0).
	 * 
	 * @param radius
	 *            the outer radius for the star.
	 */
	public StarPolygon(float radius) {
		this(5, radius, radius * .38f, 0, 0);
	}

	/**
	 * Create a star centered at (0, 0).
	 */
	public StarPolygon(int numberOfPoints, float radius1, float radius2) {
		this(numberOfPoints, radius1, radius2, 0, 0);
	}

	/**
	 * Create a customized star.
	 * 
	 * @param numberOfPoints
	 * @param radius1
	 * @param radius2
	 */
	public StarPolygon(int numberOfPoints, float radius1, float radius2,
			float centerX, float centerY) {
		setNumberOfPoints(numberOfPoints);
		setRadius1(radius1);
		setRadius2(radius2);
		setCenter(centerX, centerY);
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}

	public float getRadius1() {
		return radius1;
	}

	public float getRadius2() {
		return radius2;
	}

	public float getCenterX() {
		return centerX;
	}

	public float getCenterY() {
		return centerY;
	}

	public Point2D.Float getCenter() {
		return new Point2D.Float(centerX, centerY);
	}

	public void setNumberOfPoints(int numberOfPoints) {
		if (numberOfPoints < 3)
			throw new IllegalArgumentException("The number of points ("
					+ numberOfPoints + ") must be three or greater.");
		this.numberOfPoints = numberOfPoints;
	}

	public void setRadius1(float radius1) {
		if (radius1 < 0)
			throw new IllegalArgumentException(
					"The radius (" + radius1 + ") must be zero or greater.");
		this.radius1 = radius1;
	}

	public void setRadius2(float radius2) {
		if (radius2 < 0)
			throw new IllegalArgumentException(
					"The radius (" + radius2 + ") must be zero or greater.");
		this.radius2 = radius2;
	}

	public void setCenter(Point2D p) {
		setCenter((float) p.getX(), (float) p.getY());
	}

	public void setCenter(float centerX, float centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getRadius1(), getRadius2(), getNumberOfPoints());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof StarPolygon))
			return false;
		StarPolygon other = (StarPolygon) obj;
		if (getNumberOfPoints() != other.getNumberOfPoints())
			return false;
		if (getRadius1() != other.getRadius1())
			return false;
		if (getRadius2() != other.getRadius2())
			return false;
		return true;
	}

	@Override
	protected StarPolygon clone() {
		return new StarPolygon(getNumberOfPoints(), getRadius1(), getRadius2(),
				getCenterX(), getCenterY());
	}

	@Override
	public String toString() {
		return "StarPolygon[ numberOfPoints=" + getNumberOfPoints()
				+ ", radius1=" + radius1 + ", radius2=" + radius2 + "]";
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		Path2D path = createPath2D();
		return path.getPathIterator(at);
	}

	@Override
	public int getWindingRule() {
		return PathIterator.WIND_NON_ZERO;
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeInt(getNumberOfPoints());
		out.writeFloat(getRadius1());
		out.writeFloat(getRadius2());
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			setNumberOfPoints(in.readInt());
			setRadius1(in.readFloat());
			setRadius2(in.readFloat());
		} else {
			throw new IOException("unsupported internal version " + version);
		}
	}

	protected Path2D createPath2D() {
		float r1 = getRadius1();
		float r2 = getRadius2();
		int k = getNumberOfPoints();

		Point2D[] points = new Point2D[2 * k];
		for (int a = 0; a < k; a++) {
			double theta1 = 2 * Math.PI / k * a - Math.PI / 2;
			double theta2 = 2 * Math.PI / k * (a + .5) - Math.PI / 2;
			points[2 * a + 0] = new Point2D.Double(r1 * Math.cos(theta1),
					r1 * Math.sin(theta1));
			points[2 * a + 1] = new Point2D.Double(r2 * Math.cos(theta2),
					r2 * Math.sin(theta2));
		}
		Path2D p = new Path2D.Float();
		p.moveTo(points[0].getX(), points[0].getY());
		for (int a = 1; a < points.length; a++) {
			p.lineTo(points[a].getX(), points[a].getY());
		}
		p.closePath();
		p.transform(AffineTransform.getTranslateInstance(getCenterX(),
				getCenterY()));
		return p;
	}

}
