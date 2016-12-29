/*
 * @(#)HTMLPaintable.java
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
package com.pump.print;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JEditorPane;

import com.pump.awt.Paintable;

/** This renders HTML text as a <code>Paintable</code> object.
 *
 */
class HTMLPaintable implements Paintable {

	JEditorPane editorPane = new JEditorPane();
	int width, height;
	
	public HTMLPaintable() {
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);
		editorPane.setOpaque(false);
	}
	
	@Override
	public Object clone() {
		HTMLPaintable newGuy = new HTMLPaintable();
		newGuy.editorPane.setText(editorPane.getText());
		newGuy.width = width;
		newGuy.height = height;
		newGuy.lastHTMLText = lastHTMLText;
		return newGuy;
	}
	
	private String lastHTMLText = "";
	/** Sets the html text of this paintable, and a maximum width.
	 * The height is then calculated and determined, based on the text
	 * and the width provided.
	 * 
	 * @param htmlText the html text to use
	 * @param w the maximum width
	 * @return whether this call visually changed this object.
	 */
	public boolean define(String htmlText,int w) {
		width = w;
		
		if(lastHTMLText.equals(htmlText))
			return false;
		
		lastHTMLText = htmlText;
		
		if(htmlText.length()==0) {
			setHeight(0);
			editorPane.setText("");
		} else {
			editorPane.setText(htmlText);
			editorPane.setBounds(new Rectangle(0,0,w,1000000));
			setHeight(editorPane.getPreferredSize().height);
		}
		return true;
	}
	
	private void setHeight(int h) {
		if(h==height)
			return;
		height = h;
	}
	
	/** The height of this paintable, in pixels. */
	public int getHeight() {
		return height;
	}

	/** The width of this paintable, in pixels. */
	public int getWidth() {
		return width;
	}

	/** Paints this object. */
	public void paint(Graphics2D g) {
		editorPane.setBounds(new Rectangle(0,0,width,height));
		editorPane.paint(g);
	}
}
