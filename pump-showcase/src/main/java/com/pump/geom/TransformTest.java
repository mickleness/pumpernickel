/*
 * @(#)TransformTest.java
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
package com.pump.geom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.pump.inspector.InspectorGridBagLayout;

public class TransformTest extends BasicTestElement {

	JLabel typeLabel = new JLabel("Type:");
	JComboBox type = new JComboBox();
	JLabel repeatLabel = new JLabel("Repeat:");
	JSpinner repeat = new JSpinner(new SpinnerNumberModel(5,1,1000,1));
	JLabel additionsLabel = new JLabel("Additions:");
	JSpinner additions = new JSpinner(new SpinnerNumberModel(100,1,1000,1));
	PrintStream printStream;
	
	public TransformTest(PrintStream stream) {
		printStream = stream;
		type.addItem("All");
		type.addItem("Linear");
		type.addItem("Quadratic");
		type.addItem("Cubic");
	}
	
	@Override
	public void addControls(InspectorGridBagLayout layout) {
		layout.addRow(typeLabel, type, false);
		layout.addRow(additionsLabel, additions, false);
		layout.addRow(repeatLabel, repeat, false);
	}

	@Override
	public void doTest() {
		int failures = 0;
		int additions = ((Number)TransformTest.this.additions.getValue()).intValue();
		try {
			long[] time = new long[((Number)repeat.getValue()).intValue() ];
			for(int a = 0; a<time.length; a++) {
				time[a] = System.currentTimeMillis();
				AreaX sum = new AreaX();
				for(int b = 0; b<additions; b++) {
					if(cancelled) return;
					
					float percent = ((float)a)/((float)time.length);
					percent = percent + 1f/(time.length)*(b)/(additions);
					progress.setValue( (int)(percent*(progress.getMaximum()-progress.getMinimum()))+progress.getMinimum() );
					
					AreaX newGuy = new AreaX(getShape(a*time.length+b));
					AffineTransform t = getTransform(a*time.length+b);
					newGuy.transform(t);
					sum.add(newGuy);
				}
				time[a] = System.currentTimeMillis() - time[a];
				
				clear(image1);
				clear(image2);
				
				Rectangle rect = sum.getBounds().getBounds();
				RectangularTransform overall = new RectangularTransform(rect,
						new Rectangle(0,0,image1.getWidth(),image1.getHeight()));
				
				Graphics2D g = image1.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.transform(overall.createAffineTransform());
				g.setColor(Color.black);
				for(int b = 0; b<additions; b++) {
					GeneralPath mini = getShape(a*time.length+b);
					mini.transform( getTransform(a*time.length+b) );
					g.fill(mini);
				}
				g.dispose();
				
				g = image2.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.transform(overall.createAffineTransform());
				g.setColor(Color.black);
				g.fill(sum);
				g.dispose();
				
				if(equals(image1, image2, printStream)==false) {
					failures++;
					printStream.println("failed for a = "+a+" (repeat = "+repeat.getValue()+", additions = "+additions+")");
				}
				
			}
			Arrays.sort(time);
			printStream.println("Median time: "+time[time.length/2]+" ms");
		} finally {
			if(cancelled) {
				printStream.println("cancelled");
			} else {
				printStream.println("failures: "+failures);
			}
		}
	}

	@Override
	public String getDescription() {
		return "Test the performance and accuracy of AreaX.transform for AffineTransforms " +
		"that don't rotate/shear.";
	}

	@Override
	public String getName() {
		return "Transform Test";
	}

	Random random = new Random();
	
	protected AffineTransform getTransform(int randomSeed) {
		random.setSeed(randomSeed);
		AffineTransform t = new AffineTransform( 
				random.nextDouble()*4-2, 0, 
				0, random.nextDouble()*4-2, 
				random.nextDouble()*50, random.nextDouble()*50);
		return t;
	}
	
	protected GeneralPath getShape(int randomSeed) {
		random.setSeed(randomSeed);
		GeneralPath path = new GeneralPath();
		path.moveTo( 100*random.nextFloat(), 100*random.nextFloat());
		for(int a = 0; a<3; a++) {
			int k;
			if(type.getSelectedIndex()==0) {
				k = random.nextInt(3);
			} else if(type.getSelectedIndex()==1) {
				k = 0;
			} else if(type.getSelectedIndex()==2) {
				k = 1;
			} else {
				k = 2;
			}
			
			if(k==0) {
				path.lineTo( 100*random.nextFloat(), 100*random.nextFloat() );
			} else if(k==1) {
				path.quadTo( 100*random.nextFloat(), 100*random.nextFloat(),
						100*random.nextFloat(), 100*random.nextFloat() );
			} else {
				path.curveTo( 100*random.nextFloat(), 100*random.nextFloat(),
						100*random.nextFloat(), 100*random.nextFloat(),
						100*random.nextFloat(), 100*random.nextFloat() );
			}
		}
		return path;
	}

}
