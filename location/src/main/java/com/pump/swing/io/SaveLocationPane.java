/*
 * @(#)SaveLocationPane.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
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
package com.pump.swing.io;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Constructor;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.pump.io.location.IOLocation;
import com.pump.plaf.LocationPaneUI;
import com.pump.plaf.SaveLocationPaneUI;
import com.pump.util.JVM;

public class SaveLocationPane extends LocationPane {
    private static final long serialVersionUID = 1L;

	private static final String uiClassID = "SaveLocationPaneUI";

	public static IOLocation showDialog(Frame parent,String suffix,String dialogTitle) {
		final SaveLocationPane savePane = new SaveLocationPane(suffix);
		return savePane.showDialog(parent,dialogTitle);
	}
	
	
	public IOLocation showDialog(Frame parent,String dialogTitle) {
		final LocationPaneDialog d = new LocationPaneDialog(parent);
		if(dialogTitle!=null)
			d.setTitle(dialogTitle);

		d.setModal(true);

		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		final IOLocation[] returnValue = new IOLocation[1];
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==getUI().getCommitButton()) {
					try {
						returnValue[0] = getSaveLocation();
					} catch(IOException e2) {
						e2.printStackTrace();
						returnValue[0] = null;
					}
				} else {
					returnValue[0] = null;
				}
			}
		};
		
		AbstractAction escapeAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				d.setVisible(false);
				returnValue[0] = null;
			}
		};

		d.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(escapeKey, escapeKey);
		d.getRootPane().getActionMap().put(escapeKey, escapeAction);
		
		if(JVM.isMac)
			d.getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(commandPeriodKey, escapeKey);

		
		getUI().getCommitButton().addActionListener(actionListener);
		getUI().getCancelButton().addActionListener(actionListener);
		d.getContentPane().add(this);
		d.pack();
		d.setLocationRelativeTo(parent);
		d.setVisible(true);
		if(returnValue[0]==null) {
			//this will happen if the user clicks Cancel or otherwise exits the dialog.
			getSelectionModel().setSelection(new IOLocation[] {});
			return null;
		}
		return returnValue[0];
	}

	public SaveLocationPane(String suffix) {
		this(new LocationHistory(),new IOSelectionModel(false),new GraphicCache());
		setSuffix(suffix);
	}
	
	public SaveLocationPane(LocationHistory locationHistory,IOSelectionModel selectionModel,GraphicCache graphicCache) {
		super(locationHistory,selectionModel,graphicCache);
	}
	
	public String getSaveName() {
		return getUI().getNewFileName();
	}
	
	public void setSaveName(String name) {
		getUI().setNewFileName(name);
	}
	
	/** Returns the optional suffix this save pane enforces.
	 * This may be null or an empty string.
	 * <p>You can add a PropertyChangeListener to this component
	 * for the "suffix" property to know when this changes.
	 */
	public String getSuffix() {
		return (String)getClientProperty("suffix");
	}
	
	/** Sets the suffix this save pane enforces.
	 */
	public void setSuffix(String suffix) {
		if(suffix.length()>0 && suffix.charAt(0)=='.')
			suffix = suffix.substring(1);
		putClientProperty("suffix",suffix);
	}
	
	public IOLocation getSaveLocation() throws IOException {
		return getUI().getSaveLocation();
	}

    @Override
	public String getUIClassID() {
        return uiClassID;
    }
	
    @Override
	public void updateUI() {
    	if(UIManager.getDefaults().get(uiClassID)==null) {
    		UIManager.getDefaults().put(uiClassID, "com.pump.plaf.AquaSaveLocationPaneUI");
    		//UIManager.getDefaults().put(uiClassID, "com.pump.plaf.XPSaveLocationPaneUI");
    	}
    	try {
	    	String className = UIManager.getDefaults().getString(uiClassID);
	    	Class<?> classObject = Class.forName(className);
	    	Constructor<?> constructor = classObject.getConstructor(new Class[] { SaveLocationPane.class });
	    	setUI( (SaveLocationPaneUI)constructor.newInstance(new Object[] {this} ) );
    	} catch(RuntimeException e) {
    		throw e;
    	} catch(Throwable t) {
    		RuntimeException e = new RuntimeException();
    		e.initCause(t);
    		throw e;
    	}
    }
	
	public void setUI(SaveLocationPaneUI ui) {
        super.setUI(ui);
	}
	
	public SaveLocationPaneUI getUI() {
		return (SaveLocationPaneUI)ui;
	}
	
	@Override
	public LocationPaneUI getLocationPaneUI() {
		return getUI();
	}
}
