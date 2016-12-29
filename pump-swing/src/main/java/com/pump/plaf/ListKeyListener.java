/*
 * @(#)ComboBoxKeyListener.java
 *
 * $Date: 2015-11-03 08:53:55 -0500 (Tue, 03 Nov 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
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
package com.pump.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.text.JTextComponent;

/** This listens for KeyEvents and automatically selects the combobox items that start
 * with a given letter, if possible.
*
*/
public class ListKeyListener extends KeyAdapter {

	JComboBox comboBox;
	StringBuffer typedText = new StringBuffer();
	ListCellRenderer renderer;
	JList list;
	long lastKeyPress = -1;
	
	public ListKeyListener(JList list) {
		if(list==null)
			throw new NullPointerException();
		this.list = list;
	}
	
	public ListKeyListener(JComboBox comboBox) {
		if(comboBox==null)
			throw new NullPointerException();
		this.comboBox = comboBox;
		//TODO: use a ListDataListener to cache info about which items map to a given first letter
		//TODO: if the user presses "S" and then "S" again: if no matches are found for "SS" then cycle
		//through hits that do start with S.
		//TODO: add option to invoke popup and show selection. In cases where the combobox functions more
		//like a button and selecting something causes major changes, we shouldn't rush to make that selection
		//but we should help the user out.
	}

	public ListCellRenderer getCellRenderer() {
		if(comboBox!=null) {
			return comboBox.getRenderer();
		} else if(list!=null) {
			return list.getCellRenderer();
		}
		throw new IllegalStateException();
	}
	
	public ListModel getListModel() {
		if(comboBox!=null) {
			return comboBox.getModel();
		} else if(list!=null) {
			return list.getModel();
		}
		throw new IllegalStateException();
	}
	
	public int getSelectedIndex() {
		if(comboBox!=null) {
			return comboBox.getSelectedIndex();
		} else if(list!=null) {
			return list.getSelectedIndex();
		}
		throw new IllegalStateException();
	}
	
	public void setSelectedIndex(int newIndex) {
		if(comboBox!=null) {
			comboBox.setSelectedIndex(newIndex);
			return;
		} else if(list!=null) {
			list.setSelectedIndex(newIndex);
			return;
		}
		throw new IllegalStateException();
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		ListCellRenderer renderer = getCellRenderer();
		if(e.getWhen() - lastKeyPress>500) {
			typedText.delete(0, typedText.length());
		}
		lastKeyPress = e.getWhen();
		typedText.append(e.getKeyChar());
		
		List<Integer> itemsStartingWithText = new ArrayList<Integer>();
		
		ListModel model = getListModel();
		for(int a = 0; a<model.getSize(); a++) {
			Object element = model.getElementAt(a);
			Component rendered = renderer.getListCellRendererComponent(list, element, a, false, false);
			String text = getText(rendered);
			if(text==null)
				return;
			text = text.toUpperCase();
			
			if(text.startsWith(typedText.toString().toUpperCase())) {
				itemsStartingWithText.add(a);
			}
		}
		
		int selIndex = getSelectedIndex();
		
		int indexOf = itemsStartingWithText.indexOf(selIndex);
		if(indexOf==-1) {
			if(itemsStartingWithText.size()>0) {
				setSelectedIndex( itemsStartingWithText.get(0) );
			}
		} else {
			int newIndex = (indexOf+1)%itemsStartingWithText.size();
			setSelectedIndex(itemsStartingWithText.get(newIndex)	);
		}
	}

	private String getText(Component component)
	{
		if(component instanceof JTextComponent)
			return ((JTextComponent)component).getText();
		if(component instanceof JLabel)
			return ((JLabel)component).getText();
		if(!(component instanceof Container))
			return null;
		Container container = (Container)component;
		for(int a = 0; a<container.getComponentCount(); a++) {
			String tc = getText(container.getComponent(a));
			if(tc!=null) return tc;
		}
		return null;
	}
	
}
