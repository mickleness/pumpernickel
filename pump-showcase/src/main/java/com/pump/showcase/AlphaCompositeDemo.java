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
package com.pump.showcase;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.blog.Blurb;
import com.pump.blog.ResourceSample;

/** This is a simple demo exploring the different types of <code>AlphaComposites</code>.
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p><img src="https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/samples/CompositeDemo/sample.png" alt="new&#160;com.bric.awt.CompositeDemo()">
 * <!-- ======== END OF AUTOGENERATED SAMPLES ======== -->
 */
@Blurb (
title = "AlphaComposites: Which Does What?",
releaseDate = "October 2009",
summary = "This is a very short article with a little hands-on demo of how the different <code><a href=\"http://download.oracle.com/javase/6/docs/api/java/awt/AlphaComposite.html\">AlphaComposite</a></code> types interact.\n"+
"<p>This doesn't necessarily teach anything or present new ideas; it's just a quick guide to consult if you need it.",
article = "http://javagraphics.blogspot.com/2009/10/alphacomposites-which-does-what.html",
imageName = "CompositeDemo.png",
javadocLink = true
)
@ResourceSample( sample="new com.bric.awt.CompositeDemo()" )
public class AlphaCompositeDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	
	JComboBox composites = new JComboBox();
	JSlider alpha = new JSlider(0,100,100);
	JSlider srcAlpha = new JSlider(0,100,100);
	JSlider dstAlpha = new JSlider(0,100,100);
	Map<String, Field> fieldsTable = new HashMap<String, Field>();
	JRadioButton useShapes = new JRadioButton("Use Shapes",true);
	JRadioButton useImages = new JRadioButton("Use Images");
	
	CompositePreview preview = new CompositePreview();
	
	public AlphaCompositeDemo() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 0; c.weighty = 0;
		c.insets = new Insets(3,3,3,3);
		c.anchor = GridBagConstraints.EAST;
		add(new JLabel("Composite:"),c);
		c.gridy++;
		add(new JLabel("Alpha:"),c);
		c.gridy++; c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(useShapes,c);
		c.gridy++;
		add(useImages,c);
		c.gridy++;
		add(new JSeparator(),c);
		c.gridy++; c.gridwidth = 1; c.fill = GridBagConstraints.NONE;
		add(new JLabel("Dest Alpha:"),c);
		c.gridy++;
		add(new JLabel("Source Alpha:"),c);
		c.gridx++; c.gridy = 0;
		c.weightx = 1; c.anchor = GridBagConstraints.WEST;
		add(composites,c);
		c.gridy++; c.fill = GridBagConstraints.HORIZONTAL;
		add(alpha,c);
		c.gridy+=4;
		add(dstAlpha,c);
		c.gridy++;
		add(srcAlpha,c);
		
		c.gridy++; c.gridx = 0; c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH; c.gridwidth = GridBagConstraints.REMAINDER;
		add(preview,c);
		
		Field[] fields = AlphaComposite.class.getFields();
		for(int a = 0; a<fields.length; a++) {
			boolean isStatic = ((fields[a].getModifiers() & Modifier.STATIC) > 0);
			if(fields[a].getType().equals(Integer.TYPE) && isStatic) {
				try {
					composites.addItem( fields[a].getName() );
					fieldsTable.put(fields[a].getName(), fields[a]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		composites.setSelectedItem("SRC_OVER");
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				preview.repaint();
			}
		};
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				preview.repaint();
			}
		};
		composites.addActionListener(actionListener);
		useShapes.addActionListener(actionListener);
		useImages.addActionListener(actionListener);
		alpha.addChangeListener(changeListener);
		srcAlpha.addChangeListener(changeListener);
		dstAlpha.addChangeListener(changeListener);
		
		ButtonGroup group = new ButtonGroup();
		group.add(useImages);
		group.add(useShapes);
		
		//for applets in Safari
		//setBackground(getContentPane(), Color.white);
	}
	
	private static void setBackground(Component c,Color color) {
		c.setBackground(color);
		if(c instanceof Container && !(c instanceof JComboBox)) {
			Container c2 = (Container)c;
			for(int a = 0; a<c2.getComponentCount(); a++) {
				setBackground(c2.getComponent(a), color);
			}
		}
	}
	
	class CompositePreview extends JPanel {
		private static final long serialVersionUID = 1L;
		
		boolean cleanDemo = false;
		Ellipse2D shape1 = new Ellipse2D.Float(0,0,200,200);
		Ellipse2D shape2 = new Ellipse2D.Float(100,0,200,200);
		BufferedImage image1 = new BufferedImage(300,200,BufferedImage.TYPE_INT_ARGB);
		BufferedImage image2 = new BufferedImage(300,200,BufferedImage.TYPE_INT_ARGB);
		TexturePaint checkerboard;
		
		BufferedImage image = new BufferedImage(300,200,BufferedImage.TYPE_INT_ARGB);
		public CompositePreview() {
			setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
			
			BufferedImage checkers = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
			Graphics2D g = checkers.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0,0,checkers.getWidth(), checkers.getHeight());
			g.setColor(Color.lightGray);
			g.fillRect(0,0,checkers.getWidth()/2,checkers.getHeight()/2);
			g.fillRect(checkers.getWidth()/2,checkers.getHeight()/2,checkers.getWidth()/2,checkers.getHeight()/2);
			g.dispose();
			checkerboard = new TexturePaint(checkers,new Rectangle(0,0,checkers.getWidth(),checkers.getHeight()));
		}
		
		public Composite getComposite() {
			try {
				String selectedItem = (String)composites.getSelectedItem();
				Field f = fieldsTable.get(selectedItem);
				int rule = f.getInt(null);
				float a = ((float)alpha.getValue())/100;
				return AlphaComposite.getInstance(rule, a);
			} catch(Throwable t) {
				t.printStackTrace();
				return AlphaComposite.SrcOver;
			}
		}
		
		@Override
		protected void paintComponent(Graphics g0) {
			super.paintComponent(g0);
			
			Graphics2D g2 = image.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setComposite(AlphaComposite.Clear);
			g2.fillRect(0,0,image.getWidth(),image.getHeight());
			g2.setComposite(AlphaComposite.SrcOver);
			int destAlpha = (int)(dstAlpha.getValue()*255f/100f);
			Paint destPaint = new GradientPaint(0,0,new Color(255,100,100,0),0,200,new Color(255,100,100,destAlpha));
			if(useImages.isSelected()) {
				Graphics2D g3 = image1.createGraphics();
				g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g3.setComposite(AlphaComposite.Clear);
				g3.fillRect(0, 0, image1.getWidth(), image1.getHeight());
				g3.setComposite(AlphaComposite.SrcOver);
				g3.setPaint(destPaint);
				g3.fill(shape1);
				g3.dispose();
				
				g2.drawImage(image1, 0, 0, null);
			} else {
				g2.setPaint(destPaint);
				g2.fill(shape1);
			}
			g2.setComposite(getComposite());
			int sa = (int)(srcAlpha.getValue()*255f/100f);
			Paint srcPaint = new GradientPaint(0,200,new Color(100,100,255,0),0,0,new Color(100,100,255,sa));
			if(useImages.isSelected()) {
				Graphics2D g3 = image2.createGraphics();
				g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g3.setComposite(AlphaComposite.Clear);
				g3.fillRect(0, 0, image2.getWidth(), image2.getHeight());
				g3.setComposite(AlphaComposite.SrcOver);
				g3.setPaint(srcPaint);
				g3.fill(shape2);
				g3.dispose();
				
				g2.drawImage(image2, 0, 0, null);
			} else {
				g2.setPaint(srcPaint);
				g2.fill(shape2);
			}
			g2.dispose();
			
			Graphics2D g = (Graphics2D)g0;
			if(cleanDemo==false) {
				g.setPaint(checkerboard);
			} else {
				g.setPaint(Color.white);
			}			
			g.fillRect(0,0,getWidth(),getHeight());

			Rectangle bounds = shape1.getBounds();
			bounds.add(shape2.getBounds());
			g.translate( getWidth() / 2 - bounds.width/2, getHeight()/2 - bounds.height/2);
			
			g.drawImage(image, 0, 0, null);
			if(cleanDemo==false) {
				g.setColor(Color.black);
				drawString(g,"DST",100,100);
				drawString(g,"SRC",200,100);
			}
			g.dispose();
		}
		
		private void drawString(Graphics2D g,String s,float centerX,float centerY) {
			Rectangle2D r = g.getFontMetrics().getStringBounds(s, g);
			g.setColor(Color.white);
			g.drawString(s, (float)(centerX-r.getWidth()/2), centerY+1f);
			g.setColor(Color.black);
			g.drawString(s, (float)(centerX-r.getWidth()/2), centerY);
		}
	}
}