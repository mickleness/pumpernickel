/*
 * @(#)BasicDrawInstruction.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.graphics;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.swing.tree.TreeNode;

import com.pump.geom.ShapeBounds;
import com.pump.geom.ShapeStringUtils;

/** The default implementation of a {@link DrawInstruction}.
 * 
 */
public class BasicDrawInstruction implements DrawInstruction, Serializable
{
	private static final long serialVersionUID = 1L;

	GeneralPath path = new GeneralPath();
	Paint paint;
	Shape clipping;
	Stroke stroke;
	AffineTransform transform;
	float opacity;
	String source;
	GraphicsWriter parent;
	
	/** Create a new <code>BasicDrawInstruction</code>.
	 * 
	 * @param shape the shape to render.
	 * @param t the transform to render through.
	 * @param clipping the optional clipping.
	 * @param p the paint to use.
	 * @param s the stroke to use.
	 * @param opacity the opacity [0,1].
	 */
	public BasicDrawInstruction(Shape shape,AffineTransform t,Shape clipping,Paint p,Stroke s,float opacity) {
		if(opacity<0 || opacity>1) throw new IllegalArgumentException("The opacity ("+opacity+") must be between [0,1].");
		path.append(shape.getPathIterator(null),true);
		paint = p;
		stroke = s;
		transform = new AffineTransform(t);
		if(clipping!=null) {
			this.clipping = new Area(clipping);
		}
		this.opacity = opacity;
		
		source = GraphicsWriter.getCaller();
	}
	
	/** Returns the opacity [0,1]. */
	public float getOpacity() {
		return opacity;
	}
	
	/** Converts this to a <code>BasicFillInstruction</code>. */
	public BasicFillInstruction convertToFillInstruction() {
		return new BasicFillInstruction(
				stroke.createStrokedShape(path), 
				transform, 
				clipping, 
				paint,
				opacity
		);
	}
	
	/** Returns the stroke. */
	public Stroke getStroke() {
		return stroke;
	}
	
	/** Returns the stroke paint. */
	public Paint getStrokePaint() {
		return paint;
	}
	
	
	private transient Boolean isClipped;
	/** Return true if this instruction is affected by the current clipping.
	 * (Returns false if no clipping is present.)
	 */
	public boolean isClipped() {
		if(clipping==null) return false;
		if(isClipped==null) {
			Stroke currentStroke = stroke;
			if(currentStroke instanceof BasicStroke) {
				BasicStroke bs = (BasicStroke)currentStroke;
				currentStroke = new BasicStroke(bs.getLineWidth(),
						BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);
			}
			Area strokeArea = new Area(currentStroke.createStrokedShape(path));
			strokeArea.transform(transform);
			Area clipArea = new Area(clipping);
			
			strokeArea.subtract(clipArea);
			
			Rectangle2D r = strokeArea.getBounds2D();
			isClipped = new Boolean( r.getWidth()>.00001 && r.getHeight()>.00001 );
		}
		return isClipped.booleanValue();
	}
	
	/** Returns the untransformed, unclipped shape this instruction uses. */
	public Shape getShape() {
		return getShape(false);
	}
	
	/** Returns the unclipped shape this instruction uses.
	 * 
	 * @param useTransform whether to transform this shape or not.
	 * @return the unclipped shape.
	 */
	public Shape getShape(boolean useTransform) {
		if(useTransform) {
			return transform.createTransformedShape(path);
		}
		return path;
	}

	/** Renders this instruction. */
	public void paint(Graphics2D g) {
		g = (Graphics2D)g.create();
		if(clipping!=null)
			g.clip(getClipping());
		g.transform(getTransform());
		g.setStroke(getStroke());
		g.setPaint(getStrokePaint());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));
		g.draw(getShape());
	}
	
	/** Returns the <code>AffineTransform</code> this instruction uses. */
	public AffineTransform getTransform() {
		return new AffineTransform(transform);
	}
	
	/** Returns the area affected by this instruction.  This
	 * takes into account the clipping and transform.
	 */
	public Rectangle2D getBounds() {
		if(clipping==null) {
			return ShapeBounds.getBounds(stroke.createStrokedShape(path),transform);
		}

		Area clipArea = new Area(clipping);
		Area strokeArea = new Area(stroke.createStrokedShape(path));
		strokeArea.transform(transform);
		clipArea.intersect(strokeArea);
		return clipArea.getBounds2D();
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		if(clipping==null) {
			out.writeObject(null);
		} else {
			out.writeObject( ShapeStringUtils.toString(clipping) );
		}
		out.writeObject( paint );
		out.writeObject( ShapeStringUtils.toString(path) );
		if(stroke instanceof BasicStroke) {
			out.writeObject( toString((BasicStroke)stroke) );
		} else {
			out.writeObject( stroke );
		}
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
		Object strokeObject = in.readObject();
		if(strokeObject instanceof String) {
			stroke = createBasicStroke( (String)strokeObject );
		} else {
			stroke = (Stroke)strokeObject;
		}
		transform = (AffineTransform)in.readObject();
		opacity = in.readFloat();
	}
	
	public static String toString(BasicStroke stroke) {
		//TODO: fix this method, and createBasicStroke()
		return "BasicStroke[ width = "+stroke.getLineWidth()+" ]";
	}
	
	public static BasicStroke createBasicStroke(String s) {
		StringTokenizer st = new StringTokenizer(s);
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			try {
				float f = Float.parseFloat(token);
				return new BasicStroke(f);
			} catch(NumberFormatException e) {}
		}
		return new BasicStroke(1);
	}

	/** Returns the clipping this instruction uses.  (May be null.)
	 * This clipping is applied <i>before</i> the <code>AffineTransform</code>.
	 */
	public Shape getClipping()
	{
		if(clipping==null)
			return null;
		return new GeneralPath(clipping);
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
