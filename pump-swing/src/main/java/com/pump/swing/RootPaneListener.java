/*
 * @(#)RootPaneListener.java
 *
 * $Date: 2016-01-30 19:07:08 -0500 (Sat, 30 Jan 2016) $
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
package com.pump.swing;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JRootPane;

/** This listener will be notified when the <code>JRootPane</code> ancestor
 * of a <code>JComponent</code> changes.
 */
public abstract class RootPaneListener {
	private static Object NULL_VALUE = new Object();
	private Map<JComponent, Object> rootPanes = new HashMap<>();
	private HierarchyListener hierarchyListener = new HierarchyListener() {
		
		public void hierarchyChanged(HierarchyEvent e) {
			JComponent jc = (JComponent)e.getSource();
			JRootPane realRootPane = jc.getRootPane();
			Object recordedRootPane = rootPanes.get(jc);
			if(recordedRootPane==NULL_VALUE) recordedRootPane = null;
			if(realRootPane!=recordedRootPane) {
				Object value = realRootPane;
				if(value==null) value = NULL_VALUE;
				rootPanes.put(jc, value);
				rootPaneChanged(jc, realRootPane);
			}
		}
		
	};
	
	/** This adds this listener to the argument <code>JComponent</code>.
	 * @param jc the component added to this hierarchy tree.
	 */
	public void add(JComponent jc) {
		jc.addHierarchyListener(hierarchyListener);
		Object value = jc.getRootPane();
		if(value==null)
			value = NULL_VALUE;
		rootPanes.put(jc, value);
	}
	
	public abstract void rootPaneChanged(JComponent jc,JRootPane rootPane);
}
