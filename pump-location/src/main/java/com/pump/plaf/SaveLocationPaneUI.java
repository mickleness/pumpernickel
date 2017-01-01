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
package com.pump.plaf;

import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.pump.io.location.IOLocation;
import com.pump.swing.io.LocationPane;
import com.pump.swing.io.SaveLocationPane;

public abstract class SaveLocationPaneUI extends LocationPaneUI {

	protected final JTextField saveField = new JTextField(20);
	
	public SaveLocationPaneUI(LocationPane locationPane) {
		super(locationPane);
	}

	/** This returns the IOLocation that data should be saved to.
	 * This should only be called after the commit button is pressed.
	 * @throws IOException if an IO problem occurs.
	 */
	public IOLocation getSaveLocation() throws IOException {
		String name = ((SaveLocationPane)locationPane).getSaveName();
		if(name==null || name.length()==0)
			return null;
		IOLocation newChild = locationPane.getLocationHistory().getLocation().getChild(name);
		return newChild;
	}
	
	public abstract String getNewFileName();
	
	public abstract void setNewFileName(String fileName);
	
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		selectName(true);
	}

	/** Selects the text in the text field.
	 * 
	 * @param invokeLater if true, then this task is wrapped in a
	 * runnable passed to <code>SwingUtilities.invokeLater()</code>.
	 */
	public void selectName(boolean invokeLater) {
		Runnable runnable = new Runnable() {
			public void run() {
				saveField.requestFocus();
				String s = saveField.getText();
				int i = s.lastIndexOf('.');
				if(i==-1) {
					saveField.select(0, s.length());
				} else {
					saveField.select(0,i);
				}
			}
		};
		if(SwingUtilities.isEventDispatchThread()) {
			if(invokeLater) {
				SwingUtilities.invokeLater(runnable);
			} else {
				runnable.run();
			}
		} else {
			SwingUtilities.invokeLater(runnable);
		}
	}
}