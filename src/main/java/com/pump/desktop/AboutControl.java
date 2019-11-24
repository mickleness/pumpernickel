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
