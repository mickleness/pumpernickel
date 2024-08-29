/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;

/**
 * A simple <code>FocusTraversalPolicy</code> object that delegates to another
 * object.
 *
 */
public class DelegateFocusTraversalPolicy extends FocusTraversalPolicy {
	FocusTraversalPolicy ftp;

	public DelegateFocusTraversalPolicy(FocusTraversalPolicy policy) {
		ftp = policy;
	}

	@Override
	public Component getComponentAfter(Container focusCycleRoot,
			Component component) {
		return ftp.getComponentAfter(focusCycleRoot, component);
	}

	@Override
	public Component getComponentBefore(Container focusCycleRoot,
			Component component) {
		return ftp.getComponentBefore(focusCycleRoot, component);
	}

	@Override
	public Component getDefaultComponent(Container focusCycleRoot) {
		return ftp.getDefaultComponent(focusCycleRoot);
	}

	@Override
	public Component getFirstComponent(Container focusCycleRoot) {
		return ftp.getFirstComponent(focusCycleRoot);
	}

	@Override
	public Component getLastComponent(Container focusCycleRoot) {
		return ftp.getLastComponent(focusCycleRoot);
	}
}