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
package com.pump.awt.text;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.pump.awt.CalligraphyPathWriter;
import com.pump.awt.Scribbler;
import com.pump.geom.GeneralPathWriter;
import com.pump.geom.MeasuredShape;

/** This reveals text from left-to-right with a kind of hand-written scrawl-like look:
 * <p><img src="https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/outline-text-effect.gif" alt="outline text effect demo"></p>
 */
public class OutlineTextEffect implements TextEffect {
	protected static final Color DEFAULT_FILL = new Color(0,100,200);
	protected static final Color DEFAULT_STROKE = Color.black;
	Color fill, stroke;
	int width, height;
	Font font;
	String text;
	
	List<BlockLetter> blockLetters = new ArrayList<BlockLetter>();
	float textWidth = 0;
	float textHeight = 0;
	float angle = (float)(Math.PI/4);

	public OutlineTextEffect(Font font,String text, int width,int height ) {
		this(font, text, width, height, DEFAULT_FILL, DEFAULT_STROKE);
	}
	
	public OutlineTextEffect(Font font,String text, int width,int height,Color fill, Color stroke ) {
		this.font = font;
		this.text = text;
		this.width = width;
		this.height = height;
		this.fill = fill;
		this.stroke = stroke;
		
		FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
		for(int a = 0; a<text.length(); a++) {
			char c = text.charAt(a);
			if(Character.isWhitespace(c)==false) {
				BlockLetter l = new BlockLetter.Simple(c, font, OutlineTextEffect.this.stroke);
				l.setDepth(3);
				l.put("x", new Float(textWidth));
				textHeight = Math.max(l.getDepth(), textHeight);
				blockLetters.add( l );
				
				GeneralPath blockShadow = new GeneralPath();
				GeneralPathWriter dest1 = new GeneralPathWriter(blockShadow);
				CalligraphyPathWriter cpw = new CalligraphyPathWriter(angle, 0, l.depth, dest1, null);
				cpw.write(l.outline);
				l.put("shadowArea", blockShadow);
			}
			Rectangle2D r = font.getStringBounds(c+"", frc);
			textWidth += (float)r.getWidth();
		}

		for(int a = 0; a<blockLetters.size(); a++) {
			BlockLetter l = (BlockLetter)blockLetters.get(a);
			MeasuredShape[] charSubpaths = MeasuredShape.getSubpaths( l.outline );
			l.put("charSubpaths", charSubpaths);
			GeneralPath blockShadow = (GeneralPath)l.get("shadowArea");
			MeasuredShape[] shadowSubpaths = MeasuredShape.getSubpaths( blockShadow );
			l.put("shadowSubpaths", shadowSubpaths);
		}

		
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	Random random = new Random();
	public void paint(Graphics2D g0, float fraction) {
		g0.setPaint(stroke);
		g0.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		random.setSeed(0);
		for(int a = 0; a<blockLetters.size(); a++) {
			BlockLetter l = (BlockLetter)blockLetters.get(a);
			
			float x = ((Number)l.get("x")).floatValue();
			float y = height/2+textHeight/2;
			x = width/2-textWidth/2+x;
			float xFraction = (a)/((float)blockLetters.size()-1);
			
			Graphics2D g = (Graphics2D)g0.create();
			g.translate(x, y+(5*random.nextDouble()-2.5));

			float shadowFraction = (fraction-.5f)/.5f;
			if(shadowFraction<0) shadowFraction = 0;
			g.setStroke(new BasicStroke(.5f));
			MeasuredShape[] shadowSubpaths = (MeasuredShape[])l.get("shadowSubpaths");
			for(int b = 0; b<shadowSubpaths.length; b++) {
				Shape shape = shadowSubpaths[b].getShape(0, shadowFraction);
				shape = Scribbler.create(shape, 1, 1, a*100);
				g.draw( shape );
			}
			
			if(( xFraction+fraction)/2<=fraction) {
				Shape fill = Scribbler.create(l.outline, 1.5f, 3.5f, a);
				g.setColor(OutlineTextEffect.this.fill);
				g.fill( fill );
			}
			
			float mainFraction = fraction/.7f;
			if(mainFraction>1) mainFraction = 1;
			g.setStroke(new BasicStroke(1));
			MeasuredShape[] charSubpaths = (MeasuredShape[])l.get("charSubpaths");
			
			for(int b = 0; b<charSubpaths.length; b++) {
				Shape shape = charSubpaths[b].getShape(0, mainFraction);
				Shape border = Scribbler.create(shape, 1, 3, a*100);
				g.setColor(Color.black);
				g.draw( border );
			}
			g.dispose();
		}
	}
}