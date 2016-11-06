/*
 * @(#)StoredPaddingInfo.java
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
package com.pump.awt;

import java.awt.Component;
import java.awt.Insets;
import java.security.AccessControlException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;

/** This maintains a list of the padding of certain components.
 * This assumes, for example, that all JCheckBoxes using the
 * same look-and-feel will use the same padding. Whenever
 * a cached value is missing, this defers to the
 * more expensive <code>PaddingInfo.get()</code> method
 * and stores that value.
 */
public class StoredPaddingInfo extends PaddingInfo {
	public static final String CACHE_ACTIVE_KEY = "StoredPaddingInfo.cacheActive";
	
	static Properties properties = new Properties();
	
	/** May be null if launched from within an Applet with poor security/permissions. */
	private final static Preferences preferences = getPreferences();
	
	private static Preferences getPreferences() {
		try {
			return Preferences.userNodeForPackage(StoredPaddingInfo.class);
		} catch(AccessControlException e) {
			return null;
		}
	}
	
	public Insets get(Component jc) {
		String id = getID(jc);
		Insets i = null;
		if(id!=null) {
			i = getInsets(properties,id);
		}
		if(i==null) {
			i = super.get(jc);
			if(id!=null)
				putInsets(properties,id,i);
		}
		return i;
	}
	
	/** Returns a unique identifier for a component. */
	private static String getID(Component c) {
		if(c instanceof JPanel || c instanceof JToolBar) {
			//FIXME: improve this?  Maybe?
			return null;
		}
		
		/** This also refers to some properties defined in Apple Tech Note 2196.
		 * Those are checked on all platforms -- not just macs -- because some
		 * (especially the button segments?) may be integrated into other
		 * look-and-feels.
		 */
		
		StringBuffer id = new StringBuffer();

		id.append(System.getProperty("os.name"));
		if(c instanceof JSpinner) {
			JSpinner s = (JSpinner)c;
			id.append(s.getUI().getClass().getName()+" ");
			id.append(s.getEditor().getClass().getName()+" ");
		} else if(c instanceof JToolBar) {
			JToolBar t = (JToolBar)c;
			id.append(t.getUI().getClass().getName()+" ");
			id.append(t.getMargin()+" ");
			id.append(t.getOrientation()+" ");
			id.append(t.isBorderPainted()+" ");
			id.append(t.isFloatable()+" ");
		} else if(c instanceof JSlider) {
			JSlider s = (JSlider)c;
			//might be too complicated to guess at?
			if(s.getPaintLabels() && s.getLabelTable()!=null && s.getLabelTable().size()>0)
				return null;
			
			id.append(s.getUI().getClass().getName()+" ");
			id.append(s.getOrientation()+" ");
			id.append(s.getPaintTicks()+" ");
			id.append(s.getPaintTrack()+" ");
		} else if(c instanceof JComboBox) {
			JComboBox cb = (JComboBox)c;
			id.append(cb.getUI().getClass().getName()+" ");
			id.append(cb.isEditable()+" ");
			id.append(cb.getClientProperty("JComboBox.isSquare")+" ");
		} else if(c instanceof JProgressBar) {
			JProgressBar pb = (JProgressBar)c;
			//might be too complicated to guess at?
			if(pb.isStringPainted() && pb.getString()!=null && pb.getString().length()>0)
				return null;
			id.append(pb.getUI().getClass().getName()+" ");
			id.append(pb.isBorderPainted()+" ");
			id.append(pb.isIndeterminate()+" ");
			id.append(pb.getClientProperty("JProgressBar.style")+" ");
		} else if(c instanceof JList) {
			JList l = (JList)c;
			id.append(l.getUI().getClass().getName()+" ");
		} else if(c instanceof JLabel) {
			JLabel l = (JLabel)c;
			id.append(l.getUI().getClass().getName()+" ");
		} else if(c instanceof JScrollPane) {
			JScrollPane s = (JScrollPane)c;
			id.append(s.getUI().getClass().getName()+" ");
		} else if(c instanceof JScrollBar) {
			JScrollBar s = (JScrollBar)c;
			id.append(s.getUI().getClass().getName()+" ");
			id.append(s.getOrientation()+" ");
		} else if(c instanceof JSeparator) {
			JSeparator s = (JSeparator)c;
			id.append(s.getUI().getClass().getName()+" ");
			id.append(s.getOrientation()+" ");
		} else if(c instanceof AbstractButton) {
			AbstractButton b = (AbstractButton)c;
			id.append(b.getUI().getClass().getName()+" ");
			id.append(b.isContentAreaFilled()+" ");
			if(b instanceof JCheckBox) {
				JCheckBox cb = (JCheckBox)b;
				id.append(cb.isBorderPaintedFlat()+" ");
			}
			id.append(b.getClientProperty("JButton.buttonType")+" ");
			id.append(b.getClientProperty("JButton.segmentPosition")+" ");
		} else if(c instanceof JTextField) {
			JTextField tf = (JTextField)c;
			id.append(tf.getUI().getClass().getName()+" ");
			id.append(tf.getClientProperty("JTextField.variant")+" ");
		}
		
		if(c instanceof JComponent) {
			JComponent jc = (JComponent)c;
			id.append(jc.getClientProperty("JComponent.sizeVariant")+" ");
		}
		
		return id.toString();
	}
	
	private static void putInsets(Properties p,String id,Insets insets) {
		if(id==null)
			throw new NullPointerException();
		
		if(insets==null) {
			p.remove(id);
			return;
		}
		
		p.put(id, insets.top+" "+insets.left+" "+insets.bottom+" "+insets.right);
	}
	
	private static Insets getInsets(Properties p,String id) {
		String value = null;
		if(preferences!=null) {
			if(preferences.get(CACHE_ACTIVE_KEY, "true").toLowerCase().equals("false"))
				return null;
			
			if(preferences.get(id, null)!=null)
				value = preferences.get(id, null);
		}
		if(value==null && p.get(id)!=null)
			value = (String)p.getProperty(id);
		if(value==null) {
			return null;
		}
		StringTokenizer tokenizer = new StringTokenizer(value);
		String top = tokenizer.nextToken();
		String left = tokenizer.nextToken();
		String bottom = tokenizer.nextToken();
		String right = tokenizer.nextToken();
		
		if(top==null && left==null && right==null && bottom==null)
			return null;
		
		if(top==null || left==null || right==null || bottom==null) {
			System.err.println("top: "+top);
			System.err.println("left: "+left);
			System.err.println("bottom: "+bottom);
			System.err.println("right: "+right);
			throw new RuntimeException("Incomplete/corrupt dimensions.");
		}
		
		Insets insets = new Insets(
				Integer.parseInt(top),
				Integer.parseInt(left),
				Integer.parseInt(bottom),
				Integer.parseInt(right)
		);
		return insets;
	}
	
	
}
