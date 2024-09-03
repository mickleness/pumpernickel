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
package com.pump.plaf;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;

/**
 * When a double-click is detected, this puts a Runnable on the EDT queue.
 *
 */
public class DoubleClickListener extends MouseAdapter {

	protected Runnable runnable;

	/**
	 * When a double-click is observed a button is clicked.
	 * 
	 * @param button
	 *            when a double-click is observed this button's
	 *            <code>doClick()</code> method is invoked if it is enabled.
	 */
	public DoubleClickListener(final AbstractButton button) {
		this(new Runnable() {
			public void run() {
				if (button.isEnabled())
					button.doClick();
			}
		});
		if (button == null)
			throw new NullPointerException();
	}

	/**
	 * When a double-click is observed the argument Runnable is put on the EDT.
	 * 
	 * @param runnable
	 *            a runnable to put in the EDT when a double-click is observed.
	 */
	public DoubleClickListener(Runnable runnable) {
		this.runnable = runnable;
		if (runnable == null)
			throw new NullPointerException();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() > 1) {
			SwingUtilities.invokeLater(runnable);
		}
	}

}