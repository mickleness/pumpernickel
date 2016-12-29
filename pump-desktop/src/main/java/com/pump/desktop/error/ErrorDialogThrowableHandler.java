/*
 * @(#)ErrorDialogThrowableHandler.java
 *
 * $Date: 2015-09-13 14:46:53 -0400 (Sun, 13 Sep 2015) $
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
package com.pump.desktop.error;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class ErrorDialogThrowableHandler implements ThrowableHandler {
	
	List<ThrowableDescriptor> queue = new LinkedList<>();
	List<JComponent> leftComponents = new LinkedList<>();
	
	Runnable processQueue = new Runnable() {
		public void run() {
			ThrowableDescriptor[] throwables;
			synchronized(queue) {
				throwables = queue.toArray(new ThrowableDescriptor[queue.size()]);
				queue.clear();
			}
			if(throwables.length==0)
				return;
			
			ErrorDialog errorDialog = ErrorDialog.get();
			errorDialog.addThrowables(throwables);
			synchronized(leftComponents) {
				errorDialog.getFooter().setLeftComponents(leftComponents.toArray(new JComponent[leftComponents.size()]));
			}
			if(!errorDialog.isVisible()) {
				errorDialog.pack();
				errorDialog.setLocationRelativeTo(null);
				errorDialog.setVisible(true);
			}
		}
	};
	
	@Override
	public boolean processThrowable(ThrowableDescriptor throwable) {
		synchronized(queue) {
			queue.add( throwable );
		}
		ErrorManager.println(throwable.throwable);
		if(SwingUtilities.isEventDispatchThread()) {
			processQueue.run();
		} else {
			SwingUtilities.invokeLater(processQueue);
		}
		return true;
	}

	/** Add a component to the left side of the error dialog footer.
	 * 
	 */
	public void addLeftComponent(JComponent component)
	{
		synchronized(leftComponents) {
			leftComponents.add(component);
		}
	}

	public ThrowableDescriptor[] getThrowables() {
		ErrorDialog[] errorDialogs = ErrorDialog.getAll();
		List<ThrowableDescriptor> returnValue = new ArrayList<>();
		for(ErrorDialog d : errorDialogs) {
			returnValue.addAll(d.getThrowables());
		}
		return returnValue.toArray(new ThrowableDescriptor[returnValue.size()]);
	}
}
