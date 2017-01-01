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
package com.pump.plaf;

import java.awt.Graphics2D;

import javax.swing.JComponent;

public abstract class PaintUIEffect extends UIEffect {
	

	public PaintUIEffect(JComponent comp, int totalDuration,
			int updateInterval) {
		super(comp, totalDuration, updateInterval);
	}

	/** Paints this effect.
	 * @param g the graphics to paint to.  This will be
	 * a copy of the original <code>Graphics2D</code>
	 * destination, so it is safe to clip, transform, 
	 * and composite this however you want to without
	 * restoring its original state.
	 */
	public abstract void paint(Graphics2D g);
	
	public abstract boolean isBackground();
}