package com.pump.desktop;

import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import com.pump.data.Key;


/** 
 * This frame remembers the last location it was positioned and reopened to the same position.
 */
public class PositionedFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public static final Key<Rectangle> KEY_BOUNDS = new Key<Rectangle>(Rectangle.class, "frame-bounds");

	protected Preferences prefs = Preferences.userNodeForPackage(getClass());
	
	private boolean shownYet = false;
	private boolean saveBoundsInPrefs;
	
	public PositionedFrame(boolean restoreBounds) {
		this("", restoreBounds);
	}
	
	public PositionedFrame(String title,boolean restoreBounds) {
		super(title);
		saveBoundsInPrefs = restoreBounds;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				shownYet = true;
			}
		});
		
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				saveWindowLocation();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				saveWindowLocation();
			}
			
			private void saveWindowLocation() {
				if(saveBoundsInPrefs)
					KEY_BOUNDS.set(prefs, PositionedFrame.this.getBounds());
			}
			
		});
	}
	
	@Override
	public void pack() {
		if(saveBoundsInPrefs && (!shownYet) ) {
			Rectangle bounds = KEY_BOUNDS.get(prefs, null);
			if(bounds!=null) {
				setBounds(bounds);
				return;
			}
		}
		super.pack();
	}
}
