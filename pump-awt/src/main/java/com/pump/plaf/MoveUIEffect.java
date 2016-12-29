/*
 * @(#)MoveUIEffect.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
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
package com.pump.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.math.MathG;

public class MoveUIEffect extends UIEffect {

	ChangeListener changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			Component comp = getComponent();
			repaint(comp);
			float f = getProgress();
			int x = MathG.roundInt(startingBounds.x*(1-f)+finalBounds.x*f);
			int y = MathG.roundInt(startingBounds.y*(1-f)+finalBounds.y*f);
			int width = MathG.roundInt(startingBounds.width*(1-f)+finalBounds.width*f);
			int height = MathG.roundInt(startingBounds.height*(1-f)+finalBounds.height*f);
			comp.setBounds(x, y, width, height);
			repaint(comp);
		}
		
		/** This effect may be applied to invisible components,
		 * where other special configurations guarantee they
		 * still get painted (for example: see {@link com.bric.awt.RowLayout}).
		 * In this case we should manually invoke a repaint.
		 * <p>(When a component is visible, this will result in redundant
		 * repaint requests that should eventually be collapsed.)
		 */
		private void repaint(Component comp) {
			Container container = comp.getParent();
			if(container!=null) {
				Rectangle r = comp.getBounds();
				container.repaint(r.x, r.y, r.width, r.height);
			}
		}
	};
	
	final Rectangle startingBounds, finalBounds;
	
	public MoveUIEffect(JComponent comp, Rectangle newBounds) {
		this(comp, comp.getBounds(), newBounds);
	}
	
	public MoveUIEffect(JComponent comp, Rectangle startingBounds, Rectangle finalBounds) {
		super(comp, 100, 10);
		this.startingBounds = new Rectangle(startingBounds);
		this.finalBounds = new Rectangle(finalBounds);
		this.addChangeListener(changeListener);
	}

}
