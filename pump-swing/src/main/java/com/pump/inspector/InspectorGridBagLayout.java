/*
 * @(#)InspectorGridBagLayout.java
 *
 * $Date: 2015-05-04 16:15:52 -0400 (Mon, 04 May 2015) $
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
package com.pump.inspector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/** An implementation of the <code>InspectorLayout</code> that uses a
 * <code>GridBagLayout</code>.
 */
public class InspectorGridBagLayout implements InspectorLayout {
	public static final boolean isMac = System.getProperty("os.name").toLowerCase().indexOf("mac")!=-1;
	
	Insets borderInsets = new Insets(0,0,0,0);
	int y = 0;
	JPanel panel;
	/** For the right side of the identifier. */
	int rightIdentifierInset = 3;
	/** For the left side of the control. */
	int leftControlInset = 3;
	float idAnchorFactor;
	int rowVerticalInset = 1;
	
	/** Creates a new <code>InspectorGridBagLayout</code> object.
	 * @param panel the panel to use.
	 */
	public InspectorGridBagLayout(JPanel panel) {
		this(panel, 4);
	}
	
	/** Creates a new <code>InspectorGridBagLayout</code> object.
	 * 
	 * @param panel the panel to use.
	 * @param idAnchorFactor this is used to anchor IDs either at the top
	 * or center of a row.  If you divide the height of the right hand side
	 * by the height of the left hand side and is less than this factor:
	 * the id is centered.  Otherwise the id is anchored at the top.
	 */
	public InspectorGridBagLayout(JPanel panel,float idAnchorFactor) {
		this.panel = panel;
		this.idAnchorFactor = idAnchorFactor;
		rightIdentifierInset = 3;
		leftControlInset = 3;
		borderInsets = new Insets(8,8,8,8);
		clear();
	}
	
	public void clear() {
		panel.removeAll();
		panel.setLayout(new GridBagLayout());
		y = 0;
		panel.revalidate();
	}

	public void addGap() {
		JPanel fluff = new JPanel();
		fluff.setOpaque(false);

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = y;
		c.gridx = 0;
		c.weightx = 0;
		c.weighty = 1;
		panel.add(fluff, c);
		
		y++;
	}

	public JSeparator addSeparator() {
		JSeparator separator = new JSeparator();

		GridBagConstraints c = new GridBagConstraints();
		c.gridy = y;
		c.gridx = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(separator, c);
		
		y++;
		
		return separator;
	}

	public void addRow(JComponent comp,int alignment,boolean stretch) {
		if(comp==null)
			throw new NullPointerException();
		addRow(comp, alignment, stretch, 0);
	}
	
	/**
	 * 
	 * @param comp
	 * @param alignment
	 * @param stretch
	 * @param weighty a value of zero means this component will be squished vertically
	 * to take up the least amount of space. On rare occasion components that should stretch
	 * (like scrollpanes) may need a value of 1.0 here instead.
	 */
	public void addRow(JComponent comp,int alignment,boolean stretch,double weighty) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = y;
		c.gridx = 0;
		c.weightx = 1;
		c.weighty = weighty;
		c.gridwidth = GridBagConstraints.REMAINDER;
		if(alignment==SwingConstants.LEFT) {
			c.anchor = GridBagConstraints.WEST;
		} else if(alignment==SwingConstants.CENTER) {
			c.anchor = GridBagConstraints.CENTER;
		} else if(alignment==SwingConstants.RIGHT) {
			c.anchor = GridBagConstraints.EAST;
		} else {
			throw new IllegalArgumentException("Alignment should be LEFT, RIGHT or CENTER from SwingConstants.");
		}
		if(weighty>0) {
			if(stretch) {
				c.fill = GridBagConstraints.BOTH;
			} else {
				c.fill = GridBagConstraints.VERTICAL;
			}
		} else {
			if(stretch) {
				c.fill = GridBagConstraints.HORIZONTAL;
			} else {
				c.fill = GridBagConstraints.NONE;
			}
		}
		c.insets.top = (y==0) ? borderInsets.top : rowVerticalInset;
		c.insets.bottom = rowVerticalInset;
		c.insets.left = borderInsets.left;
		c.insets.right = borderInsets.right;
		panel.add(comp, c);
		
		y++;
	}

	public void addRow(JComponent identifier, JComponent control,boolean stretchControlToFill) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = y;
		c.gridx = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		if(control!=null && identifier!=null && 
				control.getPreferredSize().height>identifier.getPreferredSize().height*idAnchorFactor) {
			c.anchor = GridBagConstraints.NORTHEAST;
		} else {
			c.anchor = GridBagConstraints.EAST;
		}
		c.fill = GridBagConstraints.NONE;
		c.insets.top = (y==0) ? borderInsets.top : rowVerticalInset;
		c.insets.bottom = rowVerticalInset;
		c.insets.left = borderInsets.left;
		c.insets.right = rightIdentifierInset;
		if(identifier!=null)
			panel.add(identifier, c);
		
		c.gridx++;
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1;
		if(stretchControlToFill) {
			c.fill = GridBagConstraints.HORIZONTAL;
		} else {
			c.fill = GridBagConstraints.NONE;
		}
		c.insets.left = leftControlInset;
		c.insets.right = borderInsets.right;
		
		if(control!=null)
			panel.add(control, c);
		
		y++;
	}

	public void addRow(JComponent identifier, JComponent leftControl,boolean stretchLeftControl,
			JComponent rightControl) {
		if(rightControl==null) {
			//don't wrap this in an extra panel if we don't need to
			addRow(identifier,leftControl,stretchLeftControl);
			return;
		}
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = y;
		c.gridx = 0;
		c.weightx = 0;
		c.weighty = 0;
		c.gridwidth = 1;
		int rhs = 0;
		if(leftControl!=null && rightControl!=null) {
			rhs = Math.max(leftControl.getPreferredSize().height, rightControl.getPreferredSize().height);
		} else if(leftControl!=null) {
			rhs = leftControl.getPreferredSize().height;
		} else if(rightControl!=null) {
			rhs = rightControl.getPreferredSize().height;
		}
		if(identifier!=null && rhs>identifier.getPreferredSize().height*idAnchorFactor) {
			c.anchor = GridBagConstraints.NORTHEAST;
		} else {
			c.anchor = GridBagConstraints.EAST;
		}
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.NONE;
		c.insets.top = (y==0) ? borderInsets.top : rowVerticalInset;
		c.insets.bottom = rowVerticalInset;
		c.insets.left = borderInsets.left;
		c.insets.right = rightIdentifierInset;
		if(identifier!=null)
			panel.add(identifier, c);
		
		if(leftControl!=null && rightControl!=null) {
			JPanel row = new JPanel();
			row.setLayout(new GridBagLayout());
			
			c.gridx++;
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			c.insets.left = leftControlInset;
			c.insets.right = borderInsets.right;
			panel.add(row,c);
			
			c.gridx = 0;
			c.insets.left = 0;
			c.insets.top = 0;
			c.insets.bottom = 0;
			c.fill = (stretchLeftControl) ?
					GridBagConstraints.HORIZONTAL :
					GridBagConstraints.NONE ;
			if(leftControl!=null)
				row.add(leftControl, c);

			c.gridx++;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.EAST;
			c.weightx = 0;
			c.insets.left = leftControlInset;
			if(rightControl!=null)
				row.add(rightControl, c);
			
			row.setOpaque(false);
		} else if(leftControl!=null && rightControl==null) {
			c.gridx++;
			c.anchor = GridBagConstraints.WEST;
			c.fill = stretchLeftControl ?
					GridBagConstraints.HORIZONTAL :
					GridBagConstraints.NONE;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1;
			c.insets.left = leftControlInset;
			c.insets.right = borderInsets.right;
			panel.add(leftControl,c);
		} else if(leftControl==null && rightControl!=null) {
			c.gridx++;
			c.anchor = GridBagConstraints.EAST;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1;
			c.insets.left = leftControlInset;
			c.insets.right = borderInsets.right;
			panel.add(rightControl,c);
		}
		
		y++;
	}
	
	public int getRowVerticalInset() {
		return rowVerticalInset;
	}
	
	public void setRowVerticalInset(int i) {
		rowVerticalInset = i;
	}
}
