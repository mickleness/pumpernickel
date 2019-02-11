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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pump.io.location.IOLocation;
import com.pump.swing.io.SaveLocationPane;

public class AquaSaveLocationPaneUI extends BasicSaveLocationPaneUI {

    private AquaOpenLocationPaneUI openPane;

    private JLabel whereLabel = new JLabel("Where:");
    private JComboBox whereComboBox = new JComboBox();

    public AquaSaveLocationPaneUI(SaveLocationPane p) {
	super(p);

    }

    private AquaOpenLocationPaneUI getOpenPane() {
	if (openPane == null)
	    openPane = new AquaOpenLocationPaneUI(locationPane);
	return openPane;
    }

    @Override
    protected JComponent[] getLeftFooterControls() {
	return new JComponent[] { newFolderButton };
    }

    @Override
    public IOLocation getDefaultDirectory() {
	return getOpenPane().getDefaultDirectory();
    }

    @Override
    protected String getNewFolderName() {
	return getOpenPane().getNewFolderName();
    }

    @Override
    public void setExpanded(boolean b) {
	super.setExpanded(b);
	whereLabel.setVisible(!b);
	whereComboBox.setVisible(!b);
    }

    @Override
    protected void populateLowerPanel(JPanel lower) {
	getOpenPane().installGUI(lower);
    }

    @Override
    protected void populateUpperPanel(JPanel upper) {
	upper.setLayout(new GridBagLayout());

	GridBagConstraints c = new GridBagConstraints();
	c.gridx = 0;
	c.gridy = 0;
	c.anchor = GridBagConstraints.EAST;
	c.insets = new Insets(3, 3, 3, 3);
	upper.add(saveLabel, c);
	c.gridy++;
	upper.add(whereLabel, c);
	c.gridy--;
	c.gridx++;
	c.anchor = GridBagConstraints.WEST;
	upper.add(saveField, c);
	c.gridy++;
	upper.add(whereComboBox, c);
	c.gridy--;
	c.gridx++;
	c.fill = GridBagConstraints.NONE;
	upper.add(expandButton, c);
    }

}