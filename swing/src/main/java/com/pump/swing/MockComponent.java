/*
 * @(#)MockComponent.java
 *
 * $Date: 2014-05-09 09:15:15 -0400 (Fri, 09 May 2014) $
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
package com.pump.swing;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

/** This component stores an image of another component, and displays only
 * that image.
 * <P>This is useful as a proxy/substitute for existing components that
 * already have a permanent home in the UI.
 */
public class MockComponent extends JComponent {
	private static final long serialVersionUID = 1L;
	
	BufferedImage image;
	
	/** This creates a MockComponent that displays the image provided.
	 * A shallow reference to this image is used, so you should not
	 * modify the argument image.
	 * 
	 * @param bi
	 */
	public MockComponent(BufferedImage bi) {
		this.image = bi;
		Dimension d = new Dimension(bi.getWidth(), bi.getHeight());
		setSize(d);
		setPreferredSize(d);
	}
	
	/** Creates a MockComponent that resembles the argument component.
	 * <P>Note this method will traverse c and its subcomponents and may
	 * temporarily change properties of inner components: such as the focused
	 * state, the visibility, etc.
	 * <P>The goal is of this component is not to mirror the exact state of
	 * a component, but rather to provide a sample image of this component in
	 * its plain, unmodified, unused state.
	 * 
	 * @param c
	 */
	public MockComponent(JComponent c) {
		Dimension preferredSize = c.getPreferredSize();
		Dimension currentSize = c.getSize();
		
		Dimension d = new Dimension(Math.max(preferredSize.width, currentSize.width),
				Math.max(preferredSize.height, currentSize.height));
		
		if(currentSize.width==0 || currentSize.height==0) {
			//if the component isn't visible yet
			c.setSize(d);
			c.doLayout();
		}
		
		storeState(c);

		image = new BufferedImage(d.width,d.height,BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = image.createGraphics();
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, d.width, d.height);
		g.setComposite(AlphaComposite.SrcOver);

		c.paint(g);
		g.dispose();
		setPreferredSize(d);
		setMinimumSize(d);
		setMaximumSize(d);
		setOpaque(c.isOpaque());
		setName(c.getName());
		setToolTipText(c.getToolTipText());

		restoreState(c);
	}
	
	public BufferedImage getBufferedImage() {
		return image;
	}
	
	private static String WAS_SELECTED = "MockComponent.wasSelected";
	private static String WAS_FOCUS_PAINTED = "MockComponent.wasFocusPainted";
	private static String WAS_ENABLED = "MockComponent.wasEnabled";
	private static String WAS_VISIBLE = "MockComponent.wasVisible";
	
	/** Temporarily massage this component so it is visible, enabled,
	 * unselected, unfocused, etc.
	 */
	private void storeState(JComponent c) {
		if(c instanceof AbstractButton) {
			AbstractButton b = (AbstractButton)c;
			b.putClientProperty(WAS_SELECTED, new Boolean(b.isSelected()));
			b.putClientProperty(WAS_FOCUS_PAINTED, new Boolean(b.isSelected()));
			b.setSelected(false);
			b.setFocusPainted(false);
		}
		if(c.isEnabled()==false) {
			c.putClientProperty(WAS_ENABLED, new Boolean(c.isEnabled()));
			c.setEnabled(true);
		}
		if(c.isVisible()==false) {
			c.putClientProperty(WAS_VISIBLE, new Boolean(c.isVisible()));
			c.setVisible(true);
		}
		for(int a = 0; a<c.getComponentCount(); a++) {
			if(c.getComponent(a) instanceof JComponent) {
				storeState( (JComponent)c.getComponent(a) );
			}
		}
	}
	
	/** Restore this component back to its original goodness. */
	private void restoreState(JComponent c) {
		if(c instanceof AbstractButton) {
			AbstractButton b = (AbstractButton)c;
			if(b.getClientProperty(WAS_SELECTED)!=null) {
				b.setSelected( ((Boolean)b.getClientProperty(WAS_SELECTED)).booleanValue() );
				b.putClientProperty( WAS_SELECTED, null);
			}
			if(b.getClientProperty(WAS_FOCUS_PAINTED)!=null) {
				b.setFocusPainted( ((Boolean)b.getClientProperty(WAS_FOCUS_PAINTED)).booleanValue() );
				b.putClientProperty( WAS_FOCUS_PAINTED, null);
			}
		}
		if(c.getClientProperty(WAS_ENABLED)!=null) {
			c.setEnabled( ((Boolean)c.getClientProperty(WAS_ENABLED)).booleanValue() );
			c.putClientProperty( WAS_ENABLED, null);
		}
		if(c.getClientProperty(WAS_VISIBLE)!=null) {
			c.setVisible( ((Boolean)c.getClientProperty(WAS_VISIBLE)).booleanValue() );
			c.putClientProperty( WAS_VISIBLE, null);
		}
		for(int a = 0; a<c.getComponentCount(); a++) {
			if(c.getComponent(a) instanceof JComponent) {
				restoreState( (JComponent)c.getComponent(a) );
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(image, getWidth()/2-image.getWidth()/2, getHeight()/2-image.getHeight()/2, null);
	}
}
