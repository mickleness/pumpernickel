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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.plaf.ButtonUI;

public class XPDirectoryControls extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected static final ImageIcon BACK_ICON = new ImageIcon(XPDirectoryControls.class.getResource("xp_back.png"));
	protected static final ImageIcon UP_ICON = new ImageIcon(XPDirectoryControls.class.getResource("xp_up_directory.png"));
	protected static final ImageIcon NEW_FOLDER_ICON = new ImageIcon(XPDirectoryControls.class.getResource("xp_new_folder.png"));
	protected static final ImageIcon LIST_ICON = new ImageIcon(XPDirectoryControls.class.getResource("xp_view.png"));
	
	protected final JButton listButton = new JButton(LIST_ICON);

	public XPDirectoryControls(LocationPaneUI paneUI) {
		super(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 0; c.weighty = 0;
		add(paneUI.comboBox, c);
		c.gridx++;
		add(paneUI.backButton, c);
		c.gridx++;
		add(paneUI.upButton, c);
		c.gridx++;
		add(paneUI.newFolderButton, c);
		c.gridx++;
		add(listButton);
		
		JPanel fluff = new JPanel();
		fluff.setOpaque(false);
		c.gridx++; c.weightx = 1;
		add(fluff, c);
		
		Dimension d = paneUI.comboBox.getPreferredSize();
		d.width = 525;
		paneUI.comboBox.setPreferredSize(d);
		
		paneUI.backButton.setIcon(BACK_ICON);
		paneUI.upButton.setIcon(UP_ICON);
		paneUI.newFolderButton.setIcon(NEW_FOLDER_ICON);

		paneUI.backButton.setText("");
		paneUI.upButton.setText("");
		paneUI.newFolderButton.setText("");
		listButton.setText("");
		
		ButtonUI ui = new XPSubtleButtonUI();
		paneUI.backButton.setUI(ui);
		paneUI.upButton.setUI(ui);
		paneUI.newFolderButton.setUI(ui);
		listButton.setUI(ui);
	}
}