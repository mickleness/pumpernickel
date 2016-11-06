/*
 * @(#)IOSelectionModel.java
 *
 * $Date: 2016-01-30 18:40:21 -0500 (Sat, 30 Jan 2016) $
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


import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.io.location.IOLocation;

/** A selection model for <code>LocationPanes</code> and <code>LocationBrowsers</code>.
 */
public class IOSelectionModel {
	
	List<ChangeListener> changeListeners = new ArrayList<ChangeListener>();
	IOLocation[] selection = new IOLocation[] {};
	boolean allowMultipleSelection;
	
	public IOSelectionModel(boolean allowMultipleSelection) {
		this.allowMultipleSelection = allowMultipleSelection;
	}
	
	public boolean allowsMultipleSelection() {
		return allowMultipleSelection;
	}

	public void addChangeListener(ChangeListener l) {
		if(changeListeners.contains(l))
			return;
		changeListeners.add(l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		changeListeners.remove(l);
	}
	
	protected void fireChangeListeners() {
		for(int a = 0; a<changeListeners.size(); a++) {
			ChangeListener l = changeListeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(this));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public IOLocation[] getSelection() {
		IOLocation[] copy = new IOLocation[selection.length];
		System.arraycopy(selection, 0, copy, 0, selection.length);
		return copy;
	}
	
	public boolean setSelection(IOLocation newSelection) {
		return setSelection(new IOLocation[] {newSelection});
	}
	
	public boolean setSelection(IOLocation[] newSelection) {
		
		if(allowMultipleSelection==false && newSelection.length>1) {
			return setSelection(new IOLocation[] { newSelection[0] });
		}
		
		if(equals(selection,newSelection))
			return false;
		
		if(newSelection.length>0) {
			String parent = newSelection[0].getParentPath();
			if(parent==null) {
				for(int a = 1; a<newSelection.length; a++) {
					if(newSelection[a].getParentPath()!=null)
						throw new IllegalArgumentException("all elements in the selection must have the same parent");
				}
			} else {
				for(int a = 1; a<newSelection.length; a++) {
					if(newSelection[a].getParentPath().equals(parent)==false) {
						System.out.println(parent+" != "+newSelection[a].getParentPath());
						throw new IllegalArgumentException("all elements in the selection must have the same parent");
					}
				}
			}
		}

		IOLocation[] copy = new IOLocation[newSelection.length];
		System.arraycopy(newSelection, 0, copy, 0, newSelection.length);
		selection = copy;
		
		fireChangeListeners();
		return true;
	}
	
	protected static boolean equals(IOLocation[] array1,IOLocation[] array2) {
		if(array1.length!=array2.length)
			return false;
		return (contains(array1,array2) && contains(array2,array1));
	}
	
	protected static boolean contains(IOLocation[] bigger,IOLocation[] smaller) {
		for(int a = 0; a<smaller.length; a++) {
			boolean found = false;
			for(int b = 0; b<bigger.length && found==false; b++) {
				if(bigger[b].equals(smaller[a]))
					found = true;
			}
			if(found==false)
				return false;
		}
		return true;
	}
}
