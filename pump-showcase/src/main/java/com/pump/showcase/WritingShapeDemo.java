/*
 * @(#)WritingShapeDemo.java
 *
 * $Date: 2015-04-04 22:28:47 -0400 (Sat, 04 Apr 2015) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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
package com.pump.showcase;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.pump.animation.writing.WritingShape;
import com.pump.animation.writing.WritingStroke;
import com.pump.blog.Blurb;

/** A simple demo of the WritingShape class that paints a smiley face.
 * @see <a href="http://javagraphics.blogspot.com/2014/11/text-handwriting-text-effect.html">Text: Handwriting Text Effect</a>
 */
@Blurb (
filename = "WritingShape",
title = "Shapes: Animating Drawings",
releaseDate = "TBA",
summary = "The <code>WritingShape</code> animates a drawing over time.",
scrapped = "This is part of the unpublished <code>WritingFont</code> project.",
sandboxDemo = true
)
public class WritingShapeDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	
	static WritingShape createSmileyFace() {
		WritingShape shape = new WritingShape();
		shape.getStrokes().add(new WritingStroke(0, new Ellipse2D.Float(.02f, .02f, .96f, .96f)));
		shape.getStrokes().add(new WritingStroke(1, new Arc2D.Float(
				.25f, .45f, .5f, .40f, 180f, 180f, Arc2D.CHORD )));
		shape.getStrokes().add(new WritingStroke(.2f, new Line2D.Float( .3f, .33f, .3f, .33f) ));
		shape.getStrokes().add(new WritingStroke(.2f, new Line2D.Float( .7f, .33f, .7f, .33f) ));
		shape.getStrokes().add(new WritingStroke(2, null));
		return shape;
	}
	
	JPanel preview = new JPanel() {
		private static final long serialVersionUID = 1L;

		WritingShape smileyFace = createSmileyFace();
		RenderingHints qualityHints;
		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			float beatsPerSecond = bpsSlider.getValue() / 10f;
			float pixelsPerSecond = ppsSlider.getValue() / 10f;
			float duration = smileyFace.getDuration(beatsPerSecond, pixelsPerSecond);
			
			float time = (System.currentTimeMillis() % (long)(1000 * duration) );
			time /= 1000f;
			
			if(qualityHints==null) {
				qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				qualityHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			}
			
			((Graphics2D)g).setRenderingHints(qualityHints);
			((Graphics2D)g).setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			smileyFace.paint(g, 
					new Rectangle(0,0,getWidth(),getHeight()),
					time, 
					beatsPerSecond, 
					pixelsPerSecond);
		}
	};
	
	JPanel controls = new JPanel(new GridBagLayout());
	JSlider ppsSlider = new JSlider(1, 100);
	JSlider bpsSlider = new JSlider(1, 100);
	
	public WritingShapeDemo() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 0; c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		setLayout(new GridBagLayout());
		add(controls, c);
		c.gridy++; c.weighty = 1;
		add(preview, c);
		preview.setPreferredSize(new Dimension(240, 240));

		c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 0; c.weighty = 0;
		c.insets = new Insets(3,3,3,3); c.anchor = GridBagConstraints.EAST;
		controls.add(new JLabel("Pixels Per Second:"), c);
		c.gridy++;
		controls.add(new JLabel("Beats Per Second:"), c);
		c.anchor = GridBagConstraints.WEST; c.gridx++; c.gridy = 0;
		controls.add(ppsSlider, c);
		c.gridy++;
		controls.add(bpsSlider, c);
		
		Timer timer = new Timer(10, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				preview.repaint();
			}
		});
		timer.start();
	}
	
}
