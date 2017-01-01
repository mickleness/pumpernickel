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
package com.pump.graphics;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import com.pump.geom.ShapeBounds;
import com.pump.geom.ShapeStringUtils;

/** The default implementation of a {@link FillInstruction}.
 * 
 */
public class BasicFillInstruction implements FillInstruction, Serializable
{
	private static final long serialVersionUID = 1L;

	GeneralPath path = new GeneralPath();
	Paint paint;
	Shape clipping;
	AffineTransform transform;
	float opacity;
	GraphicsWriter parent;
	String source;
	
	/** Create a new <code>BasicFillInstruction</code>.
	 * 
	 * @param shape the shape to fill.
	 * @param transform the transform to view the shape through.
	 * @param clipping the optional clipping.
	 * @param p the paint to fill with.
	 * @param opacity the opacity.
	 */
	public BasicFillInstruction(Shape shape,AffineTransform transform,Shape clipping,Paint p,float opacity) {
		if(opacity<0 || opacity>1) throw new IllegalArgumentException("The opacity ("+opacity+") must be between [0,1].");
		path.append(shape.getPathIterator(null),true);
		paint = p;
		if(clipping!=null) {
			this.clipping = new Area(clipping);
		}
		this.transform = new AffineTransform(transform);
		this.opacity = opacity;
		
		source = GraphicsWriter.getCaller();
	}
	
	/** Returns the opacity [0,1]. */
	public float getOpacity() {
		return opacity;
	}
	
	/** Returns the paint used to fill this shape. */
	public Paint getFillPaint() {
		return paint;
	}
	
	/** Returns the optional clipping.  (May be null.)  Note this clipping is
	 * applied <i>before</i> the <code>AffineTransform</code>.
	 * 
	 */
	public Shape getClipping() {
		if(clipping==null)
			return null;
		if(clipping instanceof Rectangle) {
			return new Rectangle( (Rectangle)clipping );
		}
		if(clipping instanceof Rectangle2D) {
			Rectangle2D r = new Rectangle2D.Double();
			r.setFrame( (Rectangle2D)clipping );
			return r;
		}
		return new Area(clipping);
	}
	
	/** Returns the <code>AffineTransform</code> this shape is
	 * viewed through.
	 */
	public AffineTransform getTransform() {
		return new AffineTransform(transform);
	}
	
	/** Returns the untransformed, unclipped shape. */
	public Shape getShape() {
		return getShape(false,false);
	}
	
	/** Returns the shape this instruction renders.
	 * 
	 * @param includeTransform whether to transform the shape.
	 * @param includeClipping whether to clip the shape, if a clipping is
	 * provided.
	 * @return the shape this instruction renders.
	 */
	public Shape getShape(boolean includeTransform,boolean includeClipping) {
		if( (!includeTransform) && (!includeClipping) ) {
			GeneralPath copy = new GeneralPath(path);
			return copy;
		} else if( includeTransform && (!includeClipping)) {
			return transform.createTransformedShape(path);
		} else {
			if(clipping==null) {
				return getShape(includeTransform, false);
			}
			try {
				Area clipped = new Area(clipping);
				Area shape = new Area(path);
				if(includeClipping && (!includeTransform)) {
					shape.transform(transform.createInverse());
				} else {
					clipped.transform(transform);
				}
				clipped.intersect(shape);
				return clipped;
			} catch(NoninvertibleTransformException e) {
				RuntimeException e2 = new RuntimeException();
				e2.initCause(e);
				throw e2;
			}
		}
	}
	
	/** Returns the transformed bounds of this instruction. */
	public Rectangle2D getBounds() {
		if(clipping==null) {
			return ShapeBounds.getBounds(path,transform);
		}
	
		Area clipArea = new Area(clipping);
		Area shapeArea = new Area(path);
		shapeArea.transform(transform);
		clipArea.intersect(shapeArea);
		return clipArea.getBounds2D();
	}
	
	/** Renders this instruction. */
	public void paint(Graphics2D g) {
		g = (Graphics2D)g.create();
		if(clipping!=null)
			g.clip(clipping);
		g.transform(getTransform());
		g.setPaint(getFillPaint());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));
		g.fill(getShape());
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		if(clipping==null) {
			out.writeObject(null);
		} else {
			out.writeObject( ShapeStringUtils.toString(clipping) );
		}
		out.writeObject( paint );
		out.writeObject( ShapeStringUtils.toString(path) );
		out.writeObject( transform );
		out.writeFloat( opacity );
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		Object obj = in.readObject();
		if(obj instanceof String) {
			String str = (String)obj;
			clipping = new Area( ShapeStringUtils.createGeneralPath(str) );
		}
		paint = (Paint)in.readObject();
		String str = (String)in.readObject();
		path = ShapeStringUtils.createGeneralPath( str );
		transform = (AffineTransform)in.readObject();
		opacity = in.readFloat();
	}

	private transient Boolean isClipped;
	
	/** Return true if this instruction is affected by the current clipping.
	 * (Returns false if no clipping is present.)
	 */
	public boolean isClipped() {
		if(clipping==null) return false;
		if(isClipped==null) {
			Area shapeArea = new Area(path);
			shapeArea.transform(transform);
			Area clipArea = new Area(clipping);
			
			Area area = new Area(shapeArea);
			shapeArea.intersect(clipArea);
			
			isClipped = new Boolean( shapeArea.equals(area) );
		}
		return isClipped.booleanValue();
	}

	public void setParent(GraphicsWriter parent) {
		this.parent = parent;
	}
	
	public Enumeration<?> children() {
		return GraphicsWriter.EMPTY_ENUMERATION;
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	public int getChildCount() {
		return 0;
	}

	public int getIndex(TreeNode node) {
		return -1;
	}

	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return true;
	}

	public String getSource() {
		return source;
	}
}