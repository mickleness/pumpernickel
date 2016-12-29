/*
 * @(#)OpenLocationPane.java
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
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.pump.io.location.IOLocation;
import com.pump.plaf.LocationPaneUI;
import com.pump.plaf.OpenLocationPaneUI;
import com.pump.util.JVM;

public class OpenLocationPane extends LocationPane {
    private static final long serialVersionUID = 1L;

	private static final String uiClassID = "OpenLocationPaneUI";

	public static IOLocation[] showDialog(Frame parent,String[] suffixes,boolean multipleSelection,String title) {
		OpenLocationPane openPane = new OpenLocationPane(suffixes,multipleSelection);
		return openPane.showDialog(parent,title);
	}
	
	public IOLocation[] showDialog(Frame parent,String title) {
		final LocationPaneDialog d = new LocationPaneDialog(parent);
		if(title!=null)
			d.setTitle(title);
		
		d.setModal(true);
		
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		final IOLocation[][] returnValue = new IOLocation[1][];
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==getUI().getCommitButton()) {
					returnValue[0] = getSelectionModel().getSelection();
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
		if(returnValue[0]==null) { //remember this is an array, not an object
			//this will happen if the user clicks Cancel or otherwise exits the dialog.
			IOLocation[] emptyArray = new IOLocation[] {};
			getSelectionModel().setSelection(emptyArray);
			return emptyArray;
		}
		return returnValue[0];
	}

	public OpenLocationPane(String suffix) {
		this(new String[] {suffix},true);
	}
	
	public OpenLocationPane(String[] suffixes,boolean multipleSelection) {
		this(new LocationHistory(),new IOSelectionModel(multipleSelection),new GraphicCache());
		putClientProperty("suffixes",clone(suffixes));
	}
	
	private Object[] clone(Object[] array) {
		if(array==null) return array;
		Object[] copy = (Object[])Array.newInstance( array.getClass().getComponentType(), array.length);
		System.arraycopy(array,0,copy,0,array.length);
		return copy;
	}
	
	public OpenLocationPane(boolean allowMultipleSelection) {
		super(allowMultipleSelection);
	}
	
	public OpenLocationPane(LocationHistory directoryStack,IOSelectionModel selectionModel,GraphicCache graphicCache) {
		super(directoryStack,selectionModel,graphicCache);
	}
	
	public IOLocation[] getSelectedLocations() {
		return selectionModel.getSelection();
	}
	
	public IOLocation getSelectedLocation() {
		IOLocation[] selection = selectionModel.getSelection();
		if(selection.length>0)
			return selection[0];
		return null;
	}

    @Override
	public String getUIClassID() {
        return uiClassID;
    }
	
    @Override
	public void updateUI() {
    	if(UIManager.getDefaults().get(uiClassID)==null) {
    		UIManager.getDefaults().put(uiClassID, "com.pump.plaf.AquaOpenLocationPaneUI");
    		//UIManager.getDefaults().put(uiClassID, "com.pump.plaf.XPOpenLocationPaneUI");
    	}
    	try {
	    	String className = UIManager.getDefaults().getString(uiClassID);
	    	Class<?> classObject = Class.forName(className);
	    	Constructor<?> constructor = classObject.getConstructor(new Class[] { LocationPane.class });
	    	setUI( (OpenLocationPaneUI)constructor.newInstance(new Object[] {this} ) );
    	} catch(RuntimeException e) {
    		throw e;
    	} catch(Throwable t) {
    		RuntimeException e = new RuntimeException();
    		e.initCause(t);
    		throw e;
    	}
    }
	
	public void setUI(OpenLocationPaneUI ui) {
        super.setUI(ui);
	}
	
	public OpenLocationPaneUI getUI() {
		return (OpenLocationPaneUI)ui;
	}
	
	@Override
	public LocationPaneUI getLocationPaneUI() {
		return getUI();
	}
}
