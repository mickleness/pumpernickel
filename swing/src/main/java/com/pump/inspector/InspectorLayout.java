/*
 * @(#)InspectorLayout.java
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
package com.pump.inspector;

import javax.swing.JComponent;
import javax.swing.JSeparator;

/** This interface adds GUI elements to an inspector. 
 * <P>Currently this assumes the GUI is based in a left-to-right language,
 * although it would not be too hard to change this in the future.
 * <P>Each row/separator is added in a top-to-bottom order.
 * 
 */
public interface InspectorLayout {
	/** Appends a new row containing only 1 object to this inspector.
	 * 
	 * @param component the component to add.
	 * @param alignment one of the SwingConstants values: LEFT, CENTER, RIGHT.
	 * @param stretchToFill whether to stretch this component to fill the space
	 * horizontally or not.
	 */
	public void addRow(JComponent component,int alignment,boolean stretchToFill);
	
	/** Appends a new row containing 2 objects to this inspector.
	 * 
	 * The identifier is right-aligned, and the control is left-aligned.
	 * 
	 * @param identifier the control on the left.  This should usually contain text.
	 * A <code>JLabel</code> or a <code>JCheckBox</code> is recommended.
	 * @param control any more complex control on the right.
	 * @param stretchControlToFill whether this control should stretch to fit
	 * the remaining width.
	 */
	public void addRow(JComponent identifier,JComponent control,boolean stretchControlToFill);
	
	/** Appends a new row containing 3 objects to this inspector.
	 * 
	 * The identifier is right-aligned.  The leftControl is
	 * right-aligned, and the rightControl is right-aligned against
	 * the far right margin of the inspector.
	 * 
	 * @param identifier the control on the left.  This should usually contain text.
	 * A <code>JLabel</code> or a <code>JCheckBox</code> is recommended.
	 * @param leftControl any other control.
	 * @param stretchToFill whether the <code>leftControl</code>
	 * should stretch to fit the remaining width.
	 * @param rightControl the element to add on the right.
	 */
	public void addRow(JComponent identifier,JComponent leftControl,boolean stretchToFill,JComponent rightControl);
	
	/** Appends a new separator to this inspector.
	 * 
	 */
	public JSeparator addSeparator();
	
	/** Appends a gap to this inspector.  All the rows should be their
	 * preferred/minimum height, but all vertical gaps will distribute
	 * the remaining vertical space evenly.
	 * 
	 */
	public void addGap();
	
	/** Removes all elements from this inspector, usually so elements
	 * can be re-added.
	 */
	public void clear();
}
