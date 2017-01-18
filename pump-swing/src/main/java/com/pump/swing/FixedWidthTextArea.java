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
package com.pump.swing;

import java.awt.Dimension;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.pump.blog.Blurb;


/** This is a JTextArea with a fixed width.  Its preferred height will adjust depending
 * on the text inside it.
 *
 * @deprecated see com.bric.awt.TextSize
 */
@Deprecated
@Blurb (
title = "Text: Text Height and GUI Layout",
releaseDate = "June 2008",
summary = "This walks through how to find the preferred height of a block of text, "+
		"and provides a GUI component to display text with a fixed width.",
article = "http://javagraphics.blogspot.com/2008/06/text-height-gui-layout-and-text-boxes.html")
public class FixedWidthTextArea extends JTextArea {
	private static final long serialVersionUID = 1L;
	
	public static final boolean isXP = System.getProperty("os.name").toLowerCase().indexOf("xp")!=-1;
	public static final boolean isMac = System.getProperty("os.name").toLowerCase().indexOf("mac")!=-1;

	int fixedWidth = -1;
	Dimension cachedSize = null;
	private final DocumentListener docListener = new DocumentListener() {

		public void changedUpdate(DocumentEvent e)
		{
			cachedSize = null;
		}

		public void insertUpdate(DocumentEvent e)
		{
			changedUpdate(e);
		}

		public void removeUpdate(DocumentEvent e)
		{
			changedUpdate(e);
		}
		
	};
	
	public FixedWidthTextArea(String text,int fixedWidth)
	{
		super(text);
		setFixedWidth(fixedWidth);
	}

	public FixedWidthTextArea(int fixedWidth)
	{
		super();
		setFixedWidth(fixedWidth);
	}

	public FixedWidthTextArea(Document doc,int fixedWidth)
	{
		super(doc);
		setFixedWidth(fixedWidth);
	}
	
	@Override
	public void setDocument(Document d) {
		Document oldDocument = getDocument();
		if(oldDocument!=null) oldDocument.removeDocumentListener(docListener);
		super.setDocument(d);
		d.addDocumentListener(docListener);
		cachedSize = null;
	}
	
	public void setFixedWidth(int i) {
		if(i==fixedWidth) return;
		fixedWidth = i;
		cachedSize = null;
	}

	@Override
	public Dimension getPreferredSize() {
		if(cachedSize==null) {
			Map<Attribute, Object> attributes = new HashMap<>();
			attributes.put( TextAttribute.FONT, getFont());
			String text = getText();
			
			/** It is crucial this be accurate!  I used to have it
			 * always true/true, and XP sometimes failed because of it.
			 */
			FontRenderContext frc = isMac ? 
					new FontRenderContext(new AffineTransform(),true,true) :
						new FontRenderContext(new AffineTransform(),false,false);
					
			String[] paragraphs = getParagraphs(text);
			int rows = 0;
			for(int a = 0; a<paragraphs.length; a++) {
				int textLength = paragraphs[a].length();
				if(isWhiteSpace(paragraphs[a])) {
					rows++;
				} else {
					AttributedString attrString = new AttributedString( paragraphs[a], attributes);
	
					LineBreakMeasurer lbm = new LineBreakMeasurer(attrString.getIterator(),frc);
				
					int pos = 0;
					while(pos<textLength) {
						pos = lbm.nextOffset(fixedWidth);
						lbm.setPosition(pos);
						rows++;
					}
				}
			}
			int extra = 0;
			if(isXP) { //allow for descents
				extra = (int)(getFont().getLineMetrics("g", frc).getDescent()+1);
			}
			cachedSize = new Dimension(fixedWidth, rows*getRowHeight()+extra);
		}
		return new Dimension(cachedSize);
	}
	
	private boolean isWhiteSpace(String s) {
		for(int a = 0; a<s.length(); a++) {
			if(Character.isWhitespace(s.charAt(a))==false)
				return false;
		}
		return true;
	}
	
	private String[] getParagraphs(String s) {
		int index = 0;
		List<String> list = new ArrayList<String>();
		while(index<s.length()) {
			int i1 = s.indexOf('\n',index);
			int i2 = s.indexOf('\r',index);
			int i;
			if(i1==-1 && i2!=-1) {
				i = i2;
			} else if(i1!=-1 && i2==-1) {
				i = i1;
			} else {
				i = Math.min(i1,i2);
			}
			if(i==-1) {
				list.add(s.substring(index));
				index = s.length();
			} else {
				list.add(s.substring(index,i));
				i++;
				index = i;
			}
		}
		return list.toArray(new String[list.size()]);
	}
	
	@Override
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
	
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
}