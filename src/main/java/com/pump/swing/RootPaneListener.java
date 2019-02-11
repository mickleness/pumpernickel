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
package com.pump.swing;

import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JRootPane;

/**
 * This listener will be notified when the <code>JRootPane</code> ancestor of a
 * <code>JComponent</code> changes.
 */
public abstract class RootPaneListener {
	private static Object NULL_VALUE = new Object();
	private Map<JComponent, Object> rootPanes = new HashMap<>();
	private HierarchyListener hierarchyListener = new HierarchyListener() {

		public void hierarchyChanged(HierarchyEvent e) {
			JComponent jc = (JComponent) e.getSource();
			JRootPane realRootPane = jc.getRootPane();
			Object recordedRootPane = rootPanes.get(jc);
			if (recordedRootPane == NULL_VALUE)
				recordedRootPane = null;
			if (realRootPane != recordedRootPane) {
				Object value = realRootPane;
				if (value == null)
					value = NULL_VALUE;
				rootPanes.put(jc, value);
				rootPaneChanged(jc, realRootPane);
			}
		}

	};

	/**
	 * This adds this listener to the argument <code>JComponent</code>.
	 * 
	 * @param jc
	 *            the component added to this hierarchy tree.
	 */
	public void add(JComponent jc) {
		jc.addHierarchyListener(hierarchyListener);
		Object value = jc.getRootPane();
		if (value == null)
			value = NULL_VALUE;
		rootPanes.put(jc, value);
	}

	public abstract void rootPaneChanged(JComponent jc, JRootPane rootPane);
}