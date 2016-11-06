/*
 * @(#)TransitionTabbedPane.java
 *
 * $Date: 2014-03-14 02:36:22 -0400 (Fri, 14 Mar 2014) $
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
package com.pump.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.image.transition.Transition;
import com.pump.image.transition.Transition2D;
import com.pump.image.transition.WipeTransition2D;

/** This is a <code>JTabbedPane</code> that applies a transition
 * every time the selected tab changes.
 * <P>By default this uses a wipe transition, but any <code>Transition2D</code>
 * can be applied if you override the <code>getTransition2D()</code> method.
 * <H3>Implementation</H3>
 * Each time a new component is added to this tabbed pane, it is enveloped
 * in a special container.  This container overrides its <code>paint()</code>
 * method, so when a transition should be applied only that transition is painted.
 * However, because it is only a container: <code>MouseEvents</code> will always
 * be delegated directly to the components (even though they may not appear in
 * their "real" location until the transition finishes).
 * <P>The special envelope container will be returned if you call:
 * <BR><code>myTabs.getComponentAt(index)</code>
 * <BR>So if you use this method to traverse your GUI, you should be aware that
 * the components you directly added aren't returned here in their original form.
 * But this is not a typical call for programmers to make, so I don't think this
 * will be a major problem.
 *
 */
public class TransitionTabbedPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	
	/** The images used to render a transition. */
	BufferedImage lastImage, newImage, backgroundImage;
	
	ChangeListener modelListener = new ChangeListener() {
		int lastSelectedIndex = 0;
		public void stateChanged(ChangeEvent e) {
			//the selected tab has changed
			int selectedIndex = getSelectedIndex();
			Component lastComponent = getComponentAt(lastSelectedIndex);
			Component newComponent = getComponentAt(selectedIndex);
			
			//if width/height is zero, this tabbed component is not really set up
			//yet
			if(lastComponent==null || lastComponent.getWidth()==0 ||
					lastComponent.getHeight()==0) return;
			if(newComponent==null || newComponent.getWidth()==0 ||
					newComponent.getHeight()==0) return;
			
			if(backgroundImage==null || backgroundImage.getWidth()!=lastComponent.getWidth() ||
					backgroundImage.getHeight()!=lastComponent.getHeight()) {
				backgroundImage = new BufferedImage( lastComponent.getWidth(),
						lastComponent.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics2D g = backgroundImage.createGraphics();
				g.setColor(getBackground());
				g.fillRect(0,0,backgroundImage.getWidth(),
						backgroundImage.getHeight());
				
				/** The Transition2D package only supports opaque images.
				 * So we have to paint the background behind this
				 * component correctly.
				 * To do this, we'll offset g, and call tabbedPane.paint(g),
				 * while using the 'paintingBlanks' variable to suspend actually
				 * painting the underlying components.  This all takes place in
				 * the AWT thread, and is generally "magic".  Or "voodoo".
				 */
				
				Point p = lastComponent.getLocation();
				g.translate(-p.x,-p.y);
				paintingBlanks = true;
				TransitionTabbedPane.this.paint(g);
				paintingBlanks = false;
				g.translate(p.x,p.y);
			}
			
			//prep the images:
			if(lastImage==null || lastImage.getWidth()!=lastComponent.getWidth() ||
					lastImage.getHeight()!=lastComponent.getHeight()) {
				lastImage = new BufferedImage(lastComponent.getWidth(),
						lastComponent.getHeight(),
						BufferedImage.TYPE_INT_RGB);
			}
			paint(lastComponent,lastImage);
			if(newImage==null || newImage.getWidth()!=newComponent.getWidth() ||
					newImage.getHeight()!=newComponent.getHeight()) {
				newImage = new BufferedImage(newComponent.getWidth(),
						newComponent.getHeight(),
						BufferedImage.TYPE_INT_RGB);
			}
			
			progress = .01f; //so we don't flicker with a repaint
			paint(newComponent,newImage);
			transition = getTransition2D(lastSelectedIndex,selectedIndex);
			transitionStartTime = System.currentTimeMillis();
			transitionTimer.start();
			lastSelectedIndex = selectedIndex;
		}
		
		
		/** Paint a Component in an image. */
		private void paint(Component c,BufferedImage bi) {
			Graphics2D g = bi.createGraphics();
			g.drawImage(backgroundImage,0,0,null);
			
			if(c instanceof TransitionReadyComponent) {
				TransitionReadyComponent t = (TransitionReadyComponent)c;
				t.component.paint(g);
			} else {
				//this really shouldn't happen?
				c.paint(g);
			}
		}
	};
	
	/** The duration, in milliseconds, to be used for a transition. */
	float transitionDuration = 750;
	
	/** The timer that updates the transition. */
	Timer transitionTimer = new Timer(25,new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			long time = System.currentTimeMillis();
			long elapsed = time-transitionStartTime;
			float progress = (elapsed)/transitionDuration;
			setTransitionProgress(progress);
			if(progress>1) {
				((Timer)e.getSource()).stop();
			}
		}	
	});
	
	/** This is a hack-ish approach that guarantees
	 * all the TransitionReadyComponents will not paint.
	 * This is used to capture the background of the tabbed
	 * pane accurately.
	 * <P>This is only briefly modified in the AWT thread, and should
	 * never result in the user seeing a blank tabbed pane.
	 */
	private static boolean paintingBlanks = false;
	
	/** The time stamp for when the last transition was started. */
	private long transitionStartTime;
	
	/** The progress (from 0 to 1) of the current transition. */
	private float progress;
	
	/** The current transition being used. */
	private Transition2D transition;

	public TransitionTabbedPane() {
		super();
		getModel().addChangeListener(modelListener);
	}

	public TransitionTabbedPane(int tabPlacement, int tabLayoutPolicy) {
		super(tabPlacement, tabLayoutPolicy);
		getModel().addChangeListener(modelListener);
	}

	public TransitionTabbedPane(int tabPlacement) {
		super(tabPlacement);
		getModel().addChangeListener(modelListener);
	}
	
	/** Returns the transition duration, in seconds.
	 * 
	 */
	public float getTransitionDuration() {
		return transitionDuration/1000f;
	}
	
	/** Sets the transition duration, in seconds. */
	public void setTransitionDuration(float f) {
		transitionDuration = f*1000;
	}
	
	/** This returns a wipe transition.
	 * <P>The wipe moves to the left if the old index is less than the
	 * new index, or right otherwise.
	 * <P>Subclasses can override this method to implement their own transitions.
	 * 
	 * @param prevSelectedIndex the initial index.
	 * @param newSelectedIndex the new/current index.
	 * @return the transition to be used to go from one index to another.
	 */
	protected Transition2D getTransition2D(int prevSelectedIndex,int newSelectedIndex) {
		if(prevSelectedIndex<newSelectedIndex) {
			return new WipeTransition2D(Transition.LEFT);
		}
		return new WipeTransition2D(Transition.RIGHT);
	}

	@Override
	public Component add(Component component, int index) {
		return super.add(wrapComponent(component), index);
	}

	@Override
	public void add(Component component, Object constraints, int index) {
		super.add(wrapComponent(component), constraints, index);
	}

	@Override
	public void add(Component component, Object constraints) {
		super.add(wrapComponent(component), constraints);
	}

	@Override
	public Component add(Component component) {
		return super.add(wrapComponent(component));
	}

	@Override
	public Component add(String title, Component component) {
		return super.add(title, wrapComponent(component));
	}

	@Override
	public void addTab(String title, Component component) {
		super.addTab(title, wrapComponent(component));
	}

	@Override
	public void addTab(String title, Icon icon, Component component, String tip) {
		super.addTab(title, icon, wrapComponent(component), tip);
	}

	@Override
	public void addTab(String title, Icon icon, Component component) {
		super.addTab(title, icon, wrapComponent(component));
	}
	
	@Override
	public void insertTab(String title, Icon icon, Component component,
			String tip, int index) {
		super.insertTab(title, icon, wrapComponent(component), tip, index);
	}

	/** Wraps the component in a TransitionReadyComponent. */
	private Component wrapComponent(Component c) {
		if(c instanceof TransitionReadyComponent)
			return c;
		return new TransitionReadyComponent(c);
	}
	
	/** Sets the transition progress.
	 */
	protected void setTransitionProgress(float f) {
		if(f<0) f = 0;
		if(f>1) f = 1;
		if(f==progress) return;
		progress = f;
		repaint();
	}
	
	/** This is the special container that renders the transition
	 * if appropriate.  
	 */
	class TransitionReadyComponent extends Container {
		private static final long serialVersionUID = 1L;
		
		Component component;
		
		public TransitionReadyComponent(Component c) {
			super();
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1; gbc.weighty = 1;
			this.add(c,gbc);
			
			component = c;
		}

		@Override
		public void paint(Graphics g0) {
			if(paintingBlanks) return;
			
			Graphics2D g = (Graphics2D)g0;
			if(transition!=null && progress>0 && progress<1 && lastImage!=null && newImage!=null) {
				transition.paint(g, lastImage, newImage, progress);
				return;
			}
			super.paint(g);
		}
	}
	
	/** This checks both the envelope container and the contents
	 * of the envelope for the component provided.
	 */
	@Override
	public int indexOfComponent(Component component) {
        for(int i = 0; i < getTabCount(); i++) {
            Component c = getComponentAt(i);
            if ((c != null && c.equals(component)) ||
                (c == null && c == component)) { 
                return i;
            }
            if(c instanceof TransitionReadyComponent) {
            	TransitionReadyComponent t = (TransitionReadyComponent)c;
            	if ((t.component.equals(component)) ||
                    (t.component == null && t.component == component)) { 
                    return i;
                }
            }
        }
        return -1; 
	}
}
