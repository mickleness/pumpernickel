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
package com.pump.swing.toolbar;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;


/** <P>This listens to the parent window and makes important
 * changes to the child window to best emulate a "sheet-like"
 * dialog.
 * <P>If the parent is resized: then the dialog needs to
 * be repositioned and the modal cover needs to be resized.
 * <P>When the parent window is dragged, this drags the other window
 * an identical (+dx, +dy).
 * <P>This class is flawed, though, because on Mac the
 * componentResized events are coalesced together, and therefore
 * the second window moves in spurts and jumps.  But I don't see
 * how else to implement this without actual sheets.
 */
class FakeSheetWindowListener implements ComponentListener {
	Point lastLocation;
	Window window1, window2;
	JComponent modalCover, dialogAnchor;
	public FakeSheetWindowListener(Window window1,Window window2,JComponent dialogAnchor,JComponent modalCover) {
		lastLocation = window1.getLocation();
		this.window1 = window1;
		this.window2 = window2;
		this.modalCover = modalCover;
		this.dialogAnchor = dialogAnchor;
	}
	
	public void componentHidden(ComponentEvent e) {}

	public void componentMoved(ComponentEvent e) {
		Point newLocation = window1.getLocation();
		
		translate(window2,newLocation.x-lastLocation.x,
				newLocation.y-lastLocation.y);
		
		lastLocation = newLocation;
	}
	
	/** Translate a window (+dx, +dy) */
	public static void translate(Window w,int dx,int dy) {
		Point p = w.getLocation();
		p.x += dx;
		p.y += dy;
		w.setLocation(p);
	}

	public void componentResized(ComponentEvent e) {
		modalCover.setSize(window1.getSize());
		repositionDialog();
	}
	
	protected void repositionDialog() {
		Point topLeft = new Point(0,0);
		topLeft = SwingUtilities.convertPoint(dialogAnchor, topLeft, window1);
		int x = window1.getX() - window2.getWidth()/2 + dialogAnchor.getWidth()/2 + topLeft.x;
		int y = topLeft.y+dialogAnchor.getHeight()+1+window1.getY();
		Rectangle optionsBounds = new Rectangle( x, y,
				window2.getWidth(),window2.getHeight());
		SwingUtilities.convertRectangle(dialogAnchor, optionsBounds, window1);
		window2.setBounds(optionsBounds);
	}

	public void componentShown(ComponentEvent e) {
	}
}