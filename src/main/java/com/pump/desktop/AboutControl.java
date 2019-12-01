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
package com.pump.desktop;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

public class AboutControl {
	
	private static String getText() {
		DesktopApplication app = DesktopApplication.get();
		if(app==null)
			return "About";
		return "About "+app.getSimpleName();
	}

	protected AbstractAction aboutAction = new AbstractAction(getText()) {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent evt) {

			DesktopApplication app = DesktopApplication.get();
			Runnable runnable = app==null ? null : app.getAboutRunnable();
			if(runnable!=null) {
				runnable.run();
			}
		}
	};

	protected JMenuItem aboutItem = new JMenuItem(aboutAction);

	public AboutControl() {
	}

	public AbstractAction getAction() {
		return aboutAction;
	}

	public JMenuItem getMenuItem() {
		return aboutItem;
	}
}