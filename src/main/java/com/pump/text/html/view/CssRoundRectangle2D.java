package com.pump.text.html.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.pump.text.html.css.border.CssBorderRadiusValue;

/**
 * A rounded rectangle that lets you customize each corner. This is used to
 * render a CSS "border-radius" property.
 */
public class CssRoundRectangle2D extends RectangularShape
		implements Serializable {
	private static final long serialVersionUID = 1L;

	// ArcIterator.btan(Math.PI/2)
	private static final double CtrlVal = 0.5522847498307933;

	private Rectangle2D frame;
	private CssBorderRadiusValue topLeftRadius, topRightRadius,
			bottomRightRadius, bottomLeftRadius;

	private transient Path2D path;
	private transient boolean isRectangle;

	public CssRoundRectangle2D(Rectangle2D frame, CssBorderRadiusValue topLeft,
			CssBorderRadiusValue topRight, CssBorderRadiusValue bottomRight,
			CssBorderRadiusValue bottomLeft) {
		this.topLeftRadius = topLeft;
		this.topRightRadius = topRight;
		this.bottomRightRadius = bottomRight;
		this.bottomLeftRadius = bottomLeft;
		this.frame = new Rectangle2D.Double();
		setFrame(frame);
	}

	@Override
	public CssRoundRectangle2D clone() {
		return new CssRoundRectangle2D(getFrame(), topLeftRadius,
				topRightRadius, bottomRightRadius, bottomLeftRadius);
	}

	@Override
	public int hashCode() {
		return frame.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CssRoundRectangle2D))
			return false;
		CssRoundRectangle2D other = (CssRoundRectangle2D) obj;
		if (!other.frame.equals(frame))
			return false;
		if (!topLeftRadius.equals(other.topLeftRadius))
			return false;
		if (!topRightRadius.equals(other.topRightRadius))
			return false;
		if (!bottomRightRadius.equals(other.bottomRightRadius))
			return false;
		if (!bottomLeftRadius.equals(other.bottomLeftRadius))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CssRoundRectangle2D[ " + frame.getX() + ", " + frame.getY()
				+ ", " + frame.getWidth() + ", " + frame.getHeight()
				+ ", topLeftRadius = " + topLeftRadius.toCSSString()
				+ ", topRightRadius = " + topRightRadius.toCSSString()
				+ ", bottomRightRadius = " + bottomRightRadius.toCSSString()
				+ ", bottomLeftRadius = " + bottomLeftRadius.toCSSString()
				+ "]";
	}

	private void resetShape() {
		float topLeftRadiusHoriz, topLeftRadiusVert, topRightRadiusHoriz,
				topRightRadiusVert, bottomLeftRadiusHoriz, bottomLeftRadiusVert,
				bottomRightRadiusHoriz, bottomRightRadiusVert;

		if (topLeftRadius != null) {
			topLeftRadiusHoriz = topLeftRadius.getHorizontalValue()
					.getValue(getWidth());
			topLeftRadiusVert = topLeftRadius.getVerticalValue()
					.getValue(getHeight());
		} else {
			topLeftRadiusHoriz = 0;
			topLeftRadiusVert = 0;
		}

		if (topRightRadius != null) {
			topRightRadiusHoriz = topRightRadius.getHorizontalValue()
					.getValue(getWidth());
			topRightRadiusVert = topRightRadius.getVerticalValue()
					.getValue(getHeight());
		} else {
			topRightRadiusHoriz = 0;
			topRightRadiusVert = 0;
		}

		if (bottomLeftRadius != null) {
			bottomLeftRadiusHoriz = bottomLeftRadius.getHorizontalValue()
					.getValue(getWidth());
			bottomLeftRadiusVert = bottomLeftRadius.getVerticalValue()
					.getValue(getHeight());
		} else {
			bottomLeftRadiusHoriz = 0;
			bottomLeftRadiusVert = 0;
		}

		if (bottomRightRadius != null) {
			bottomRightRadiusHoriz = bottomRightRadius.getHorizontalValue()
					.getValue(getWidth());
			bottomRightRadiusVert = bottomRightRadius.getVerticalValue()
					.getValue(getHeight());
		} else {
			bottomRightRadiusHoriz = 0;
			bottomRightRadiusVert = 0;
		}

		isRectangle = topLeftRadiusHoriz == 0 && topLeftRadiusVert == 0
				&& topRightRadiusHoriz == 0 && topRightRadiusVert == 0
				&& bottomLeftRadiusHoriz == 0 && bottomLeftRadiusVert == 0
				&& bottomRightRadiusHoriz == 0 && bottomRightRadiusVert == 0;

		topLeftRadiusHoriz = (float) Math.min(topLeftRadiusHoriz,
				frame.getWidth() / 2);
		topRightRadiusHoriz = (float) Math.min(topRightRadiusHoriz,
				frame.getWidth() / 2);
		bottomLeftRadiusHoriz = (float) Math.min(bottomLeftRadiusHoriz,
				frame.getWidth() / 2);
		bottomRightRadiusHoriz = (float) Math.min(bottomRightRadiusHoriz,
				frame.getWidth() / 2);

		topLeftRadiusVert = (float) Math.min(topLeftRadiusVert,
				frame.getHeight() / 2);
		topRightRadiusVert = (float) Math.min(topRightRadiusVert,
				frame.getHeight() / 2);
		bottomLeftRadiusVert = (float) Math.min(bottomLeftRadiusVert,
				frame.getHeight() / 2);
		bottomRightRadiusVert = (float) Math.min(bottomRightRadiusVert,
				frame.getHeight() / 2);

		path = new Path2D.Float();
		path.moveTo(frame.getMinX() + topLeftRadiusHoriz, frame.getMinY());
		path.lineTo(frame.getMaxX() - topRightRadiusHoriz, frame.getMinY());
		path.curveTo(frame.getMaxX() - topRightRadiusHoriz * (1 - CtrlVal),
				frame.getMinY(), frame.getMaxX(),
				frame.getMinY() + topRightRadiusVert * (1 - CtrlVal),
				frame.getMaxX(), frame.getMinY() + topRightRadiusVert);
		path.lineTo(frame.getMaxX(), frame.getMaxY() - bottomRightRadiusVert);
		path.curveTo(frame.getMaxX(),
				frame.getMaxY() - bottomRightRadiusVert * (1 - CtrlVal),
				frame.getMaxX() - bottomRightRadiusHoriz * (1 - CtrlVal),
				frame.getMaxY(), frame.getMaxX() - bottomRightRadiusHoriz,
				frame.getMaxY());
		path.lineTo(frame.getMinX() + bottomLeftRadiusHoriz, frame.getMaxY());
		path.curveTo(frame.getMinX() + bottomLeftRadiusHoriz * (1 - CtrlVal),
				frame.getMaxY(), frame.getMinX(),
				frame.getMaxY() - bottomLeftRadiusVert * (1 - CtrlVal),
				frame.getMinX(), frame.getMaxY() - bottomRightRadiusVert);
		path.lineTo(frame.getMinX(), frame.getMinY() + topLeftRadiusVert);
		path.curveTo(frame.getMinX(),
				frame.getMinY() + topLeftRadiusVert * (1 - CtrlVal),
				frame.getMinX() + topLeftRadiusHoriz * (1 - CtrlVal),
				frame.getMinY(), frame.getMinX() + topLeftRadiusHoriz,
				frame.getMinY());
		path.closePath();
	}

	public boolean isRectangle() {
		return isRectangle;
	}

	@Override
	public Rectangle2D getBounds2D() {
		return (Rectangle2D) frame.clone();
	}

	@Override
	public boolean contains(double x, double y) {
		return path.contains(x, y);
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return path.intersects(x, y, w, h);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return path.contains(x, y, w, h);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return path.getPathIterator(at);
	}

	@Override
	public double getX() {
		return frame.getX();
	}

	@Override
	public double getY() {
		return frame.getY();
	}

	@Override
	public double getWidth() {
		return frame.getWidth();
	}

	@Override
	public double getHeight() {
		return frame.getHeight();
	}

	@Override
	public boolean isEmpty() {
		return frame.isEmpty();
	}

	@Override
	public void setFrame(double x, double y, double w, double h) {
		frame.setFrame(x, y, w, h);
		resetShape();
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(0);
		out.writeDouble(frame.getX());
		out.writeDouble(frame.getY());
		out.writeDouble(frame.getWidth());
		out.writeDouble(frame.getHeight());
		out.writeObject(topLeftRadius);
		out.writeObject(topRightRadius);
		out.writeObject(bottomRightRadius);
		out.writeObject(bottomLeftRadius);
	}

	private void readObject(ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int internalVersion = in.readInt();
		if (internalVersion == 0) {
			frame = new Rectangle2D.Double(in.readDouble(), in.readDouble(),
					in.readDouble(), in.readDouble());
			topLeftRadius = (CssBorderRadiusValue) in.readObject();
			topRightRadius = (CssBorderRadiusValue) in.readObject();
			bottomRightRadius = (CssBorderRadiusValue) in.readObject();
			bottomLeftRadius = (CssBorderRadiusValue) in.readObject();
		} else {
			throw new IOException(
					"unsupported internal version: " + internalVersion);
		}

		resetShape();
	}

}
