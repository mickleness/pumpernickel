/*
 * @(#)TextBoxHighlightPainter.java
 *
 * $Date: 2015-12-26 20:42:44 -0600 (Sat, 26 Dec 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
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
package com.pump.text;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;

import com.pump.awt.TextBlock;

/** This combines a {@link com.bric.awt.TextBlock} and a 
 * {@link javax.swing.text.Highlighter.HighlightPainter}.
 * 
 */
public class TextBoxHighlightPainter implements HighlightPainter {

	protected TextBlock textBlock;
	protected Rectangle2D.Float currentStringBounds = new Rectangle2D.Float();
	protected boolean includeFill;
	protected float alpha = 1;
	
	public TextBoxHighlightPainter(float hue,boolean includeFill) {
		this.includeFill = includeFill;
		
		textBlock = new TextBlock(hue, "") {
			@Override
			protected Rectangle2D getStringBounds() {
				Rectangle2D r = new Rectangle2D.Float();
				r.setFrame(currentStringBounds);
				return r;
			}
		};
		textBlock.setCurveWidth(4);
		textBlock.setInsets(new Insets(0,0,0,0));
		textBlock.setTextInsets(new Insets(0,1,0,1));
		textBlock.setBorderPaint(new Color(0,0,0,25));
		textBlock.setBackgroundShadowColor(new Color(0,0,0,10));
	}
	
	public TextBoxHighlightPainter(float hue,boolean includeFill,float alpha) {
		this(hue, includeFill);
		this.alpha = alpha;
	}
	
	@Override
	public void paint(Graphics g0, int p0, int p1, Shape bounds, JTextComponent c) {
		Graphics2D g = (Graphics2D)g0.create();
		try {
			if(alpha!=1) {
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			}
			int length = c.getDocument().getLength();
			p0 = Math.min(Math.max(0, p0), length);
			p1 = Math.min(Math.max(0, p1), length);
			Rectangle2D rect = c.modelToView(p0);
			rect.add(c.modelToView(p1));
			if(!includeFill) {
				g.setPaint( textBlock.getBackground() );
				g.draw( rect );
			} else {
				currentStringBounds.setFrame(rect);
				textBlock.revalidate();
				g.translate( rect.getCenterX() - textBlock.getWidth()/2.0,
							rect.getCenterY() - textBlock.getHeight()/2.0 );
				textBlock.paint(g);
			}
			
		} catch(BadLocationException e) {
			throw new RuntimeException(e);
		} finally {
			g.dispose();
		}
	}

}
