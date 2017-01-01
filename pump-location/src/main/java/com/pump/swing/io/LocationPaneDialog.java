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
package com.pump.swing.io;

import java.awt.Frame;

import javax.swing.JDialog;

/** The dialog used when <code>OpenLocationPane</code> or
 * <code>SaveLocationPane</code> invoke <code>showDialog()</code>
 *
 */
public class LocationPaneDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	protected LocationPaneDialog(Frame parent) {
		super(parent);
	}
}