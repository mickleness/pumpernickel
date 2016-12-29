/*
 * @(#)DoubleClickListener.java
 *
 * $Date: 2015-12-26 20:42:44 -0600 (Sat, 26 Dec 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;

/** When a double-click is detected, this puts a Runnable on the EDT queue.
 *
 */
public class DoubleClickListener extends MouseAdapter {
	
	protected Runnable runnable;
	
	/** When a double-click is observed a button is clicked.
	 * 
	 * @param button when a double-click is observed this button's <code>doClick()</code>
	 * method is invoked if it is enabled.
	 */
	public DoubleClickListener(final AbstractButton button) {
		this(new Runnable() {
			public void run() {
				if(button.isEnabled())
					button.doClick();
			}
		});
		if(button==null)
			throw new NullPointerException();
	}
	
	/** When a double-click is observed the argument Runnable is put on the EDT.
	 * 
	 * @param runnable a runnable to put in the EDT when a double-click is observed.
	 */
	public DoubleClickListener(Runnable runnable) {
		this.runnable = runnable;
		if(runnable==null)
			throw new NullPointerException();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getClickCount()>1)
		{
			SwingUtilities.invokeLater(runnable);
		}
	}

}
