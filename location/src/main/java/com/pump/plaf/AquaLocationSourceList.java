/*
 * @(#)AquaLocationSourceList.java
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
package com.pump.plaf;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.pump.io.location.IOLocation;
import com.pump.swing.io.GraphicCache;
import com.pump.swing.io.LocationHistory;

/** This is the source list of directories in open/save dialogs.
 * 
 */
public class AquaLocationSourceList extends JTree {
	private static final long serialVersionUID = 1L;

	final LocationHistory directoryStack;
	final GraphicCache graphicCache;
	final DefaultMutableTreeNode root;
	final DefaultMutableTreeNode devices = new DefaultMutableTreeNode("DEVICES");
	final DefaultMutableTreeNode places = new DefaultMutableTreeNode("PLACES");
	final DefaultTreeModel treeModel;
	
	TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
			TreePath treePath = getSelectionPath();
			if(treePath!=null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)treePath.getLastPathComponent();
				if(node instanceof LocationNode) {
					LocationNode l = (LocationNode)node;
					directoryStack.append(l.getLocation());
				}
			}
		}
	};
	
	ChangeListener directoryStackListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			IOLocation loc = directoryStack.getLocation();
			
			DefaultMutableTreeNode node = findNode( (TreeNode)getModel().getRoot(), loc);
			if(node==null) {
				setSelectionPath(null);
			} else {
				setSelectionPath( new TreePath(node.getPath()) );
			}
		}
	};
	
	protected static DefaultMutableTreeNode findNode(TreeNode node,Object userObject) {
		if(node instanceof DefaultMutableTreeNode) {
			Object obj = ((DefaultMutableTreeNode)node).getUserObject();
			if(obj==null && userObject==null)
				return (DefaultMutableTreeNode)node;
			if(obj.equals(userObject))
				return (DefaultMutableTreeNode)node;
		}
		for(int a = 0; a<node.getChildCount(); a++) {
			TreeNode child = node.getChildAt(a);
			DefaultMutableTreeNode hit = findNode(child, userObject);
			if(hit!=null) return hit;
		}
		return null;
	}
	
	public AquaLocationSourceList(LocationHistory directoryStack,GraphicCache graphicCache) {
		super(new DefaultTreeModel(new DefaultMutableTreeNode("root")));
		
		treeModel = (DefaultTreeModel)getModel();
		root = (DefaultMutableTreeNode)treeModel.getRoot();
		
		this.directoryStack = directoryStack;
		this.graphicCache = graphicCache;
		
		treeModel.insertNodeInto(devices, root, 0);
		treeModel.insertNodeInto(places, root, 1);
		addTreeSelectionListener(treeSelectionListener);
		directoryStack.addChangeListener(directoryStackListener);
		setExpandsSelectedPaths(true);
		setCellRenderer(new SourceListLocationRenderer( graphicCache, this));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setUI(new SourceListTreeUI(this));
		
		expandPath( new TreePath(devices.getPath()) );
		expandPath( new TreePath(places.getPath()) );
	}

	/** A <code>DefaultMutableTreeNode</code> that maps to a <code>IOLocation</code>. */
	class LocationNode extends DefaultMutableTreeNode {
		private static final long serialVersionUID = 1L;
		
		PropertyChangeListener cacheListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				IOLocation location = getLocation();
				if(evt.getPropertyName().equals(GraphicCache.ICON_PROPERTY) && evt.getSource().equals(location)) {
					Icon icon = graphicCache.requestIcon(location);
					if(icon!=null)
					setIcon( icon );
				}
			}
		};
		Icon icon = null;
		
		public LocationNode(IOLocation location) {
			super();
			setUserObject(location);
			graphicCache.addPropertyChangeListener(cacheListener);
			Icon icon = graphicCache.requestIcon(location);
			if(icon==null)
				icon = IOLocation.FOLDER_ICON;
			setIcon(icon);
		}
		
		public void setIcon(Icon i) {
			icon = i;
			TreePath path = new TreePath(this.getPath());
			Rectangle bounds = getUI().getPathBounds(AquaLocationSourceList.this, path);
			if(bounds!=null)
				repaint( bounds );
		}
		
		public Icon getIcon() {
			return icon;
		}
		
		public IOLocation getLocation() {
			return (IOLocation)getUserObject();
		}
		
	}
	
	public void add(IOLocation[] locations) {
		for(int a = 0; a<locations.length; a++) {
			IOLocation l = locations[a];
			LocationNode newNode = new LocationNode(l);
			
			String path = l.getPath();
			boolean isDevice = path.equals("file:/");
			
			if(path.startsWith("file:/Volumes/")) {
				String s = path.substring("file:/Volumes/".length());
				int i = s.indexOf(File.separator);
				if(i==-1 || i==s.length()-1) {
					isDevice = true;
				}
			}
			
			if(isDevice) {
				treeModel.insertNodeInto(newNode, devices, devices.getChildCount());
			} else {
				treeModel.insertNodeInto(newNode, places, places.getChildCount());
			}
		}
	}
	
	public boolean isEmpty() {
		return devices.getChildCount()==0 && places.getChildCount()==0;
	}
}
