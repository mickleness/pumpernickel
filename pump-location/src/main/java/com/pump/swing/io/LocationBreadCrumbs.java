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

import java.util.LinkedList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.io.location.IOLocation;
import com.pump.swing.JBreadCrumb;
import com.pump.swing.JBreadCrumb.BreadCrumbFormatter;
import com.pump.swing.NavigationListener;
import com.pump.swing.NavigationListener.ListSelectionType;

public class LocationBreadCrumbs extends JBreadCrumb<IOLocation> {
	private static final long serialVersionUID = 1L;

	public LocationBreadCrumbs() {
		setFormatter(new BreadCrumbFormatter<IOLocation>() {

			public void format(JBreadCrumb<IOLocation> container, JLabel label,
					IOLocation pathNode, int index) {
				label.setText(pathNode.getName());
				label.setIcon(pathNode.getIcon(null));
			}
			
		});
	}

	public LocationBreadCrumbs(final LocationHistory history) {
		this();
		history.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setPath(getPath(history.getLocation()));
			}
		});
		setPath(getPath(history.getLocation()));
		
		addNavigationListener(new NavigationListener<IOLocation>() {

			public boolean elementsSelected(
					NavigationListener.ListSelectionType type,
					IOLocation... elements) {
				if(ListSelectionType.DOUBLE_CLICK.equals(type)) {
					history.append( elements[0] );
					return true;
				}
				return false;
			}
			
		});
	}
	
	/** Create a tree path of parents ending with the location provided.
	 */
	protected IOLocation[] getPath(IOLocation loc) {
		List<IOLocation> path = new LinkedList<IOLocation>();
		IOLocation t = loc;
		while(true) {
			if(t==null || path.contains(t) || path.size()>50) 
				return path.toArray(new IOLocation[path.size()]);
			path.add(0, t);
			t = t.getParent();
		}
	}
}