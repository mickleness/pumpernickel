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
package com.pump.swing.io;

import java.awt.Component;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIManager;

import com.pump.data.Key;
import com.pump.io.location.IOLocation;
import com.pump.plaf.KeyListenerNavigator;
import com.pump.plaf.LocationTreePaneUI;

/**
 * This shows a tree structure of IOLocations.
 */
public class LocationTreePane extends JComponent {

    public static Key<IOLocation[]> KEY_ROOTS = new Key<>(IOLocation[].class,
	    LocationTreePane.class.getName() + "#roots");

    private static final long serialVersionUID = 1L;

    private static final String uiClassID = "LocationTreePane";

    JTree tree;

    public LocationTreePane(IOLocation... roots) {
	tree = new JTree() {
	    private static final long serialVersionUID = 1L;

	    int recursionCtr = 0;

	    @Override
	    public String convertValueToText(Object value, boolean selected,
		    boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (recursionCtr > 0) {
		    // the default renderer call this method, so we need to
		    // abort for recursive calls
		    return super.convertValueToText(value, selected, expanded,
			    leaf, row, hasFocus);
		}
		recursionCtr++;
		try {
		    Component c = getCellRenderer()
			    .getTreeCellRendererComponent(this, value,
				    selected, expanded, leaf, row, hasFocus);
		    String str = KeyListenerNavigator.getText(c);
		    if (str != null)
			return str;
		    return super.convertValueToText(value, selected, expanded,
			    leaf, row, hasFocus);
		} finally {
		    recursionCtr--;
		}
	    }

	};
	setRoots(roots);
	updateUI();
    }

    public void setRoots(IOLocation[] roots) {
	Objects.requireNonNull(roots);
	KEY_ROOTS.putClientProperty(this, roots);
    }

    public IOLocation[] getRoots() {
	IOLocation[] returnValue = KEY_ROOTS.getClientProperty(this);
	if (returnValue == null) {
	    returnValue = new IOLocation[] {};
	}
	Arrays.copyOf(returnValue, returnValue.length);
	return returnValue;
    }

    @Override
    public void updateUI() {
	if (UIManager.getDefaults().get(uiClassID) == null) {
	    UIManager.getDefaults().put(uiClassID,
		    "com.pump.plaf.LocationTreePaneUI");
	}
	setUI((LocationTreePaneUI) UIManager.getUI(this));
    }

    @Override
    public String getUIClassID() {
	return uiClassID;
    }

    public JTree getTree() {
	return tree;
    }
}