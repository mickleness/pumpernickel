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
package com.pump.inspector;

import javax.swing.JComponent;
import javax.swing.JSeparator;

import com.pump.blog.Blurb;

/** This interface adds GUI elements to an inspector. 
 * <P>Currently this assumes the GUI is based in a left-to-right language,
 * although it would not be too hard to change this in the future.
 * <P>Each row/separator is added in a top-to-bottom order.
 * 
 */
@Blurb (
imageName = "Inspector.png",
title = "Layouts: Designing an Inspector",
releaseDate = "June 2009",
summary = "This article examines the notion of a well-designed inspector.\n"+
"<p>It uses <a href=\"https://javagraphics.java.net/doc/com/bric/inspector/InspectorLayout.html\">this interface</a> to "+
" layout the components, and it presents a few implementations "+
"of that interface (with varying degrees of success/hackery).",
article = "http://javagraphics.blogspot.com/2009/06/layouts-designing-inspector.html"
)
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