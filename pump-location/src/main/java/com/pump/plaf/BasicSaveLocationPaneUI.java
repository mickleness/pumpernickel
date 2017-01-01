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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.icon.TriangleIcon;
import com.pump.swing.DialogFooter;
import com.pump.swing.io.LocationPane;
import com.pump.swing.io.LocationPaneDialog;

public abstract class BasicSaveLocationPaneUI extends SaveLocationPaneUI {
	public static final Icon DOWN_ICON = new TriangleIcon(SwingConstants.SOUTH, 10, 5);
	public static final Icon UP_ICON = new TriangleIcon(SwingConstants.NORTH, 10, 5);
	protected final JButton expandButton = new JButton(DOWN_ICON);
	protected final JSeparator separator = new JSeparator();
	
	private final String expandedKey = getClass().getName()+".isExpanded";
	protected final JPanel lowerPanel = new JPanel(new GridBagLayout());
	protected final JPanel upperPanel = new JPanel(new GridBagLayout());
	protected final JLabel saveLabel = new JLabel("Save As:");

	protected final DialogFooter footer;
	protected final JPanel accessoryPanel = new JPanel(new GridBagLayout());
	protected final JPanel accessoryDecoration = new JPanel(new GridBagLayout());
	protected String suffix = "";

	Runnable updateTextRunnable = new Runnable() {
		public void run() {
			suffix = (String)locationPane.getClientProperty("suffix");
			getNewFileName();
		}
	};
	Runnable updateAccessoryRunnable = new Runnable() {
		public void run() {
			updateAccessoryPanel();
		}
	};
	
	public BasicSaveLocationPaneUI(LocationPane locPane) {
		super(locPane);

		expandButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setExpanded(!isExpanded());
			}
		});
		accessoryDecoration.setUI(DecoratedPanelUI.createDarkRecessedUI());
		commitButton.setText("Save");
		cancelButton.setText("Cancel");
		newFolderButton.setText("New Folder");
		
		footer = new DialogFooter(getLeftFooterControls(), 
				new JComponent[] { commitButton, cancelButton}, false, commitButton);

		
		saveField.getDocument().addDocumentListener(new DocumentListener() {
			
			public void changedUpdate(DocumentEvent e) {
				//can't run this now: modifying a document during a document event throws exceptions
				SwingUtilities.invokeLater(updateTextRunnable);
				updateCommitButton();
			}

			public void insertUpdate(DocumentEvent e) {
				changedUpdate(e);
			}

			public void removeUpdate(DocumentEvent e) {
				changedUpdate(e);
			}
		});
		updateAccessoryPanel();
		
		locationPane.addPropertyChangeListener("suffix", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				SwingUtilities.invokeLater(updateTextRunnable);
			}
		});
		locationPane.addPropertyChangeListener(LocationPane.ACCESSORY_KEY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if(SwingUtilities.isEventDispatchThread()) {
					updateAccessoryRunnable.run();
				} else {
					SwingUtilities.invokeLater(updateAccessoryRunnable);
				}
			}
		});
		
		SwingUtilities.invokeLater(updateTextRunnable);
		SwingUtilities.invokeLater(updateAccessoryRunnable);
	}
	
	protected abstract void populateLowerPanel(JPanel p);
	protected abstract void populateUpperPanel(JPanel p);
	
	/** Returns controls to put on the left side of the dialog footer.
	 * <p>By default this returns the <code>newFolderButton</code>.
	 */
	protected JComponent[] getLeftFooterControls() {
		return new JComponent[] { newFolderButton };
	}

	/** Expands/contracts this pane.
	 * 
	 * @param b
	 */
	public void setExpanded(boolean b) {
		lowerPanel.setVisible(b);
		lowerPanel.setVisible(b);
		separator.setVisible(b);
		newFolderButton.setVisible(b);
		
		expandButton.setIcon(b ? UP_ICON : DOWN_ICON);
		Preferences.userNodeForPackage(BasicSaveLocationPaneUI.class).putBoolean(expandedKey, b );
	
		Window w = SwingUtilities.getWindowAncestor(locationPane);
		if(w instanceof LocationPaneDialog) {
			Point p = new Point(0,0);
			Point newPoint = new Point(0,0);
			SwingUtilities.convertPointToScreen(p, saveField);
			w.pack();
			SwingUtilities.convertPointToScreen(newPoint, saveField);
			w.setLocation(w.getX()+p.x-newPoint.x, w.getY()+p.y-newPoint.y);
		}
	}
	
	/** Returns true if this pane is expanded.
	 * 
	 */
	protected boolean isExpanded() {
		return lowerPanel.isVisible();
	}

	/** Updates the accessoryPanel so it contains the
	 * <code>SaveLocationPane</code> accessory.
	 */
	private void updateAccessoryPanel() {
		JComponent newAccessory = locationPane.getAccessory();
		Component oldAccessory = accessoryDecoration.getComponentCount()>0
			? accessoryDecoration.getComponent(0) : null;
		if(newAccessory!=oldAccessory) {
			accessoryPanel.removeAll();
			
			if(newAccessory!=null) {
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
				c.fill = GridBagConstraints.BOTH;
				accessoryDecoration.removeAll();
				accessoryDecoration.add( newAccessory, c );
				accessoryDecoration.validate();
				accessoryDecoration.revalidate();
				accessoryPanel.add( accessoryDecoration, c);
			}
			accessoryPanel.validate();
			accessoryPanel.setVisible(newAccessory!=null);

			repackIfNecessary();
		}
	}
	
	protected void repackIfNecessary() {
		Window window = SwingUtilities.getWindowAncestor(locationPane);
		if(window==null) return;
		
		window.pack();
		
		//TODO: make this a little smarter
		/*
		if(isExpanded()==false) {
			window.pack();
			return;
		}
		
		Dimension realSize = locationPane.getSize();
		Dimension preferredSize = locationPane.getPreferredSize();
		
		if(realSize.height<preferredSize.height) {
			int deltaHeight = preferredSize.height-realSize.height;
			Dimension d = window.getSize();
			d.height += deltaHeight;
			window.setSize(d);
		}*/
	}
	
	@Override
	public void setNewFileName(String newFileName) {
		//other listeners will impose the suffix here.
		saveField.setText(newFileName);
	}

	/** This fetches the current file name and updates the
	 * <code>saveField</code> text if necessary to
	 * conform to the currently assigned suffix.
	 */
	@Override
	public String getNewFileName() {
		String s = saveField.getText();
		String currentSuffix = getSuffix(s);
		
		if(suffix.equalsIgnoreCase(currentSuffix)) {
			if(s.equals("."+currentSuffix)) {
				saveField.setText("");
				saveField.setSelectionStart(0);
				saveField.setSelectionEnd(-1);
				return "";
			}
			return s;
		}

		int p1 = saveField.getSelectionStart();
		int p2 = saveField.getSelectionEnd();
		if(currentSuffix==null) {
			if(s.length()==0)
				return s;
			
			String newText = suffix.length()==0 ? s : s+"."+suffix;
			if(saveField.getText().equals(newText)==false)
				saveField.setText(newText);
			saveField.setSelectionStart(p1);
			saveField.setSelectionEnd(p2);
			return newText;
		}
		
		String base = s.substring(0,s.length()-currentSuffix.length()-1);
		if(base.length()==0) {
			saveField.setText("");
			saveField.setSelectionStart(0);
			saveField.setSelectionEnd(-1);
			return "";
		}
		
		int dot = base.length();
		String newText = suffix.length()==0 ? base : base+"."+suffix;
		saveField.setText(newText);
		if(p1>dot || p2>dot) {
			//this is bad: the user shouldn't be typing here
			
			//I'm not sure if this is the best response, though...
			saveField.setSelectionStart(dot);
			saveField.setSelectionEnd(-1);
			return newText;
		}
		
		saveField.setSelectionStart(p1);
		saveField.setSelectionEnd(p2);
		return newText;
	}
	
	/** Returns the suffix of a string, or null if no
	 * suffix is detected.
	 * @param s a string like "name.foo"
	 * @return the text that follows the last "." in the argument, or null
	 * if no "." is present.
	 */
	protected static String getSuffix(String s) {
		int i = s.lastIndexOf('.');
		if(i==-1)
			return null;
		return s.substring(i+1);
	}
	
	/** Updates the enabled state of the commit button.
	 * By default this method disables the commit button
	 * if the <code>saveField</code> is empty.
	 */
	protected void updateCommitButton() {
		commitButton.setEnabled(saveField.getText().length()>0);
	}
	
	@Override
	protected final void installGUI(JComponent panel) {
		panel.removeAll();
		panel.revalidate();
		panel.setLayout(new GridBagLayout());

		lowerPanel.removeAll();
		upperPanel.removeAll();
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(20,20,3,20); c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(upperPanel,c);
		
		c.gridy++; c.gridx = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1; c.insets = new Insets(3,0,3,0);
		panel.add(separator, c);
		c.insets = new Insets(3,3,3,3);
		c.gridy++; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
		panel.add(lowerPanel, c);
		
		c.gridy++; c.weighty = 0;
		panel.add(accessoryPanel, c);
		accessoryPanel.setBorder(new EmptyBorder(new Insets(5,20,5,20)));
		
		c.weighty = 0; c.gridy++; c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(footer, c);
		lowerPanel.setBorder(new EmptyBorder(new Insets(0,20,0,20)));
		
		updateCommitButton();
		
		populateLowerPanel(lowerPanel);
		populateUpperPanel(upperPanel);
		
		setExpanded( Preferences.userNodeForPackage(BasicSaveLocationPaneUI.class).getBoolean(expandedKey, true) );
	}
}