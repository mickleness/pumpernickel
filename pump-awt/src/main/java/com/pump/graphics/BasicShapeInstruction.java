/*
 * @(#)BasicShapeInstruction.java
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
import java.io.IOException;
import java.io.Serializable;

import com.pump.geom.ShapeStringUtils;

/** This paints a {@link FillInstruction} and then a {@link DrawInstruction} on top
 * of it.  This requires the shape, clipping, transform, and opacities of the fill
 * and the stroke be the same.
 *
 * It is technically possible that the stroke or fill be null, but it is
 * recommended to use the most appropriate instruction for each graphic
 * operation.
 * 
 */
public class BasicShapeInstruction extends BasicFillInstruction implements 
	DrawInstruction, Serializable {
	private static final long serialVersionUID = 1L;
	
	Stroke stroke;
	Paint strokePaint;
	
	/** Creates a new <code>BasicShapeInstruction</code>.
	 * 
	 * @param shape the shape to render.
	 * @param transform the transform to use.
	 * @param clipping the optional clipping (may be null).
	 * @param fillPaint the fill paint.
	 * @param strokePaint the stroke paint.
	 * @param stroke the stroke
	 * @param opacity the opacity [0,1].
	 */
	public BasicShapeInstruction(Shape shape, AffineTransform transform, Shape clipping, Paint fillPaint,Paint strokePaint,Stroke stroke,float opacity)
	{
		super(shape, transform, clipping, fillPaint, opacity);
		this.strokePaint = strokePaint;
		this.stroke = stroke;
	}
	
	/** Returns a ShapeInstruction if possible that embodies both argument instructions.
	 * If not possible (because the fill, transform, or clipping is a problem), then
	 * this returns null.
	 * 
	 * @param fillInstr
	 * @param drawInstr
	 * @return a new instruction representing both argument instructions, or null if
	 * it isn't possible to combine these instructions.
	 */
	public static BasicShapeInstruction merge(FillInstruction fillInstr, DrawInstruction drawInstr) {
		if(fillInstr==null || drawInstr==null)
			return null;
		
		Shape shape1 = fillInstr.getShape();
		Shape shape2 = drawInstr.getShape();
		String s1 = ShapeStringUtils.toString(shape1);
		String s2 = ShapeStringUtils.toString(shape2);
		if(s1.equals(s2)==false)
			return null;
		if(fillInstr.getTransform().equals(drawInstr.getTransform())==false)
			return null;
		boolean b1 = fillInstr.isClipped();
		boolean b2 = drawInstr.isClipped();
		if(b1!=b2)
			return null;
		if(b1 && b2) {
			Shape clip1 = fillInstr.getClipping();
			Shape clip2 = drawInstr.getClipping();
			s1 = ShapeStringUtils.toString(clip1);
			s2 = ShapeStringUtils.toString(clip2);
			if(s1.equals(s2)==false)
				return null;
		}
		return new BasicShapeInstruction(
				shape1,
				fillInstr.getTransform(),
				fillInstr.getClipping(),
				fillInstr.getFillPaint(),
				drawInstr.getStrokePaint(),
				drawInstr.getStroke(),
				fillInstr.getOpacity()
		);
	}
	
	/** Extracts an instruction that only fills this shape. */
	public FillInstruction getFillInstruction() {
		return new BasicFillInstruction(getShape(),getTransform(),getClipping(),getFillPaint(),getOpacity());
	}

	/** Extracts an instruction that only draws this shape. */
	public DrawInstruction getDrawInstruction() {
		return new BasicDrawInstruction(getShape(),getTransform(),getClipping(),getStrokePaint(),getStroke(),getOpacity());
	}

	/** Returns the stroke used. */
	public Stroke getStroke()
	{
		return stroke;
	}

	/** Returns the stroke paint used. */
	public Paint getStrokePaint()
	{
		return strokePaint;
	}

	/** Renders this instruction. */
	@Override
	public void paint(Graphics2D g)
	{
		g = (Graphics2D)g.create();
		if(clipping!=null)
			g.clip(getClipping());
		g.transform(getTransform());
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));
		Paint fill = getFillPaint();
		Shape shape = getShape();
		if(fill!=null) {
			g.setPaint(fill);
			g.fill(shape);
		}
		Paint strokePaint = getStrokePaint();
		Stroke stroke = getStroke();
		if(strokePaint!=null && stroke!=null) {
			g.setStroke(stroke);
			g.setPaint(strokePaint);
			g.draw(getShape());
		}
	}
	
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject( strokePaint );
		if(stroke instanceof BasicStroke) {
			out.writeObject( BasicDrawInstruction.toString((BasicStroke)stroke) );
		} else {
			out.writeObject( stroke );
		}
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		strokePaint = (Paint)in.readObject();
		Object strokeObject = in.readObject();
		if(strokeObject instanceof String) {
			stroke = BasicDrawInstruction.createBasicStroke( (String)strokeObject );
		} else {
			stroke = (Stroke)strokeObject;
		}
	}
}
