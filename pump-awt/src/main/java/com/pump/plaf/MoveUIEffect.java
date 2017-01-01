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