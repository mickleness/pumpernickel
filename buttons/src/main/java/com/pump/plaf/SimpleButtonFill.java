/*
 * @(#)SimpleButtonFill.java
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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JToggleButton;

public abstract class SimpleButtonFill extends ButtonFill {
	
	protected static final Color shadowHighlight = new Color(255,255,255,120);
	/** Returns a translucent white. */
	@Override
	public Color getShadowHighlight(AbstractButton button) {
		return shadowHighlight;
	}

	/** This fill is the darkest shade for this button.
	 * This is used for <code>JToggleButtons</code> to depict
	 * a selected state. If this is null, then
	 * <code>getDarkerFill()</code> may be used instead.
	 * @param fillRect the rectangle the fill applies to.
	 * @return the paint to use when a toggle button is selected.
	 */
	public abstract Paint getDarkestFill(Rectangle fillRect);

	/** This fill is slightly darker than the normal fill.
	 * Depending on whether the button being rendered is
	 * a <code>JToggleButton</code>: this may be the fill
	 * used for the pressed state or (in non-toggle-buttons)
	 * the selected state.
	 * @param fillRect the rectangle the fill applies to.
	 * @return the paint to use when this button is pressed or
	 * selected.
	 */
	public abstract Paint getDarkerFill(Rectangle fillRect);

	/** The rollover fill of this button, or null if there is
	 * no special fill for rollovers.  If this is null, then
	 * <code>getDarkestFill()</code> may be used instead.
	 * @param fillRect the rectangle the fill applies to.
	 * @return the paint to use when this button is rolled over.
	 */
	public abstract Paint getRolloverFill(Rectangle fillRect);
	
	/** The normal fill of this button.  Unlike the other <code>getter()</code>
	 * methods in this class: this cannot return null.
	 * @param fillRect the rectangle the fill applies to.
	 * @return the paint to use when this button is in its normal state.
	 */
	public abstract Paint getNormalFill(Rectangle fillRect);

	@Override
	public Paint getFill(AbstractButton button, Rectangle fillRect) {
		ButtonModel model = button.getModel();
		if(button instanceof JToggleButton) {
			if(model.isArmed() || FilledButtonUI.isSpacebarPressed(button)) {
				Paint darkestFill = getDarkestFill(fillRect);
				if(darkestFill!=null)
					return darkestFill;

				Paint darkerFill = getDarkerFill(fillRect);
				if(darkerFill!=null)
					return darkerFill;
			}
			if(model.isSelected()) {
				Paint darkerFill = getDarkerFill(fillRect);
				if(darkerFill!=null)
					return darkerFill;
				
				Paint darkestFill = getDarkestFill(fillRect);
				if(darkestFill!=null)
					return darkestFill;
			}
			if(FilledButtonUI.isRollover(button)) {
				Paint rolloverFill = getRolloverFill(fillRect);
				if(rolloverFill!=null)
					return rolloverFill;
			}
		} else {
			if(model.isSelected() || model.isArmed() || FilledButtonUI.isSpacebarPressed(button)) {
				Paint darkerFill = getDarkerFill(fillRect);
				if(darkerFill!=null)
					return darkerFill;

				Paint darkestFill = getDarkestFill(fillRect);
				if(darkestFill!=null)
					return darkestFill;
			}
			if(FilledButtonUI.isRollover(button)) {
				Paint rolloverFill = getRolloverFill(fillRect);
				if(rolloverFill!=null)
					return rolloverFill;
			}
		}
		Paint normalFill = getNormalFill(fillRect);
		if(normalFill==null)
			throw new NullPointerException("The getNormalFill() method cannot return null.");
		return normalFill;
	}

}
