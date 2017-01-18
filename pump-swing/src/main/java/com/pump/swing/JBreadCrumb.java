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
package com.pump.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;

import com.pump.blog.Blurb;
import com.pump.plaf.BreadCrumbUI;

/** A navigation UI component to help present and navigate a tree-based
 * hierarchy.
 *<p>Here are two possible UI's depicting the same basic data:
 *<p><img src="https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/screenshot_breadcrumb_demo.png" alt="Sample Screenshots of JBreadCrumb">
 *
 * @param <T> the type of data used used to model this series of nodes.
 * @see #PATH_KEY
 */
@Blurb (
imageName = "JBreadCrumbDemo.png",
title = "Navigation: Bread Crumbs",
releaseDate = "January 2014",
summary = "A Swing implementation of collapsible bread crumbs.",
article = "http://javagraphics.blogspot.com/2014/01/navigation-breadcrumbs.html"
)
public class JBreadCrumb<T> extends JComponent {
	private static final long serialVersionUID = 1L;

	private static final String uiClassID = "BreadCrumbUI";
	
	/** Each bread crumb is represented by a JLabel, and this
	 * gives you the opportunity to format that label. For example,
	 * if this component navigated <code>java.io.Files</code>, then
	 * you should apply a <code>BreadCrumbFormatter</code> that
	 * invoked <code>label.setText(file.getName())</code> and <code>label.setIcon(fileIcon)</code>.
	 */
	public static interface BreadCrumbFormatter<T> {
		/** Format a label to accommodate the path node (crumb) provided.
		 * <p>For example: this might add specific icons, or change the font
		 * or opacity based on what this label is representing.
		 * 
		 * @param container the <code>JBreadCrumb</code> this is part of.
		 * @param label the label to be formatted.
		 * @param pathNode the node/crumb being represented.
		 * @param index the index of this node in the path.
		 */
		public void format(JBreadCrumb<T> container, JLabel label,T pathNode,int index);
	}
	
	/** Add a <code>PropertyChangeListener</code> for this key
	 * to be notified when the current path changes.
	 * 
	 */
	public static final String PATH_KEY = JBreadCrumb.class.getName()+".path";
	
	public static final String FORMATTER_KEY = JBreadCrumb.class.getName()+".formatter";
	
	List<NavigationListener<T>> listeners = new ArrayList<NavigationListener<T>>();

	public JBreadCrumb() {
		updateUI();
		setFormatter(new BreadCrumbFormatter<T>() {
			public void format(JBreadCrumb<T> container,JLabel label, T pathNode, int index) {
				label.setIcon(null);
				label.setText(pathNode.toString());
			}
		});
	}
	
	public JBreadCrumb(T... path) {
		this();
		setPath(path);
	}
	
	public void addNavigationListener(NavigationListener<T> l) {
		listeners.add(l);
	}
	
	public void removeNavigationListener(NavigationListener<T> l) {
		listeners.remove(l);
	}
	
	public List<NavigationListener<T>> getNavigationListeners() {
		return listeners;
	}
	
	/** Set the path this component displays.
	 * 
	 * @param path the path to display. This should not be null,
	 * or contain any null elements. 
	 */
	public void setPath(T... path) {
		if(path==null)
			throw new NullPointerException();
		for(T t : path) {
			if(t==null)
				throw new NullPointerException();
		}
		putClientProperty(PATH_KEY, path);
	}
	
	/** Return the current path of this component.
	 * This may be null if it has not been defined yet.
	 */
	public T[] getPath() {
		return (T[])getClientProperty(PATH_KEY);
	}
	
	/** Assign the <code>BreadCrumbFormatter</code> for this
	 * component.
	 */
	public void setFormatter(BreadCrumbFormatter<T> formatter) {
		if(formatter==null) throw new NullPointerException();
		putClientProperty(FORMATTER_KEY, formatter);
	}

	public BreadCrumbFormatter<T> getFormatter() {
		return (BreadCrumbFormatter<T>)getClientProperty(FORMATTER_KEY);
	}

    @Override
	public String getUIClassID() {
        return uiClassID;
    }
	
    @Override
	public void updateUI() {
    	if(UIManager.getDefaults().get(uiClassID)==null) {
    		UIManager.getDefaults().put(uiClassID, "com.pump.plaf.BreadCrumbUI");
    	}
    	setUI((BreadCrumbUI)UIManager.getUI(this));
    }
	
	public void setUI(BreadCrumbUI ui) {
        super.setUI(ui);
	}
	
	public BreadCrumbUI getUI() {
		return (BreadCrumbUI)ui;
	}
	
}