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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/** This is an alternative model to display/hide popups.
 * <p>Many browsers consistently render popups in the wrong
 * location: they take the (x, y) of the applet but display the
 * popup menu relative to the browser window instead of the
 * applet -- so it is displaced up and to the left.
 * <p>This was specifically designed with JPopupMenus and tooltips
 * in mind.
 *
 */
public class AppletPopupFactory extends PopupFactory {
	
	private static boolean initialized = false;
	public static void initialize() {
		if(initialized)
			return;
		try {
			PopupFactory oldFactory = PopupFactory.getSharedInstance();
			PopupFactory.setSharedInstance(new AppletPopupFactory(oldFactory));
		} finally {
			initialized = true;
		}
	}
	
	public static Component[] getHierarchy(Component c) {
		List<Component> hierarchy = new LinkedList<Component>();
		while(c!=null) {
			hierarchy.add(c);
			c = c.getParent();
		}
		return hierarchy.toArray(new Component[hierarchy.size()]);
	}

	static class AppletPopup extends Popup {

		class MyBlockingPane extends BlockingPane {
			private static final long serialVersionUID = 1L;

			MyBlockingPane() {}
			
			@Override
			public void mousePressed(MouseEvent e) {
				AppletPopup.this.hide();
				super.mouseClicked(e);
			}
			
			protected void paintComponent(Graphics g0) {
				super.paintComponent(g0);

				Graphics2D g = (Graphics2D)g0;

				Rectangle rect = component.getBounds();

				if(backgroundColor!=null) {
					//paint a shadow:
					Graphics2D g2 = (Graphics2D)g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
					g2.setColor(new Color(0,0,0,4));
					for(int a = 0; a<10; a++) {
						g2.setStroke(new BasicStroke(a+1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
						g2.drawRect( rect.x, rect.y+a/2, rect.width, rect.height );
					}
					g2.dispose();
					
					//paint the background color:
					g.setColor(backgroundColor);
					g.fillRect(rect.x-1, rect.y-1, rect.width+1, rect.height+1);
				}
				if(borderColor!=null) {
					g.setColor(borderColor);
					g.drawRect(rect.x-1, rect.y-1, rect.width+1, rect.height+1);
				}
			}
		};


		AbstractAction hideAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				hide();
			}				
		};

		final JApplet applet;
		final JComponent component;
		final Component owner;
		final int x, y;
		BlockingPane background;
		Color borderColor = Color.gray;
		Color backgroundColor = null;
		
		boolean transferredContents = false;

		public AppletPopup(JApplet applet, Component owner, JComponent component, int x,int y) {
			if(applet==null) throw new NullPointerException();
			
			this.applet = applet;
			this.component = component;
			this.x = x;
			this.y = y;
			this.owner = owner;
			
			/* For some bizarre reason: the LineBorder doesn't show up like
			 * it should. So we extract it and let the blocking pane paint it
			 * instead.
			 * 
			 */
			if(component.getBorder() instanceof LineBorder) {
				borderColor = ((LineBorder)component.getBorder()).getLineColor();
				component.setBorder(null);
			}

			/** Extract the background color to paint under the popup.
			 * This is custom-designed to work with Mac's and the AquaComboBoxUI.
			 */
			Component[] t = component.getComponents();
			Component j = null;
			for(int a = 0; a<t.length && j==null; a++) {
				if(!(t[a] instanceof Box.Filler)) {
					j = t[a];
				}
				if(j instanceof JScrollPane) {
					j = ((JScrollPane)j).getViewport().getComponent(0);
				}
			}
			if(j!=null)
				backgroundColor = j.getBackground();
		}

		@Override
		public void hide() {
			Container parent = component.getParent();
			if(parent!=null)
				parent.remove(component);
			background.setVisible(false);
		}

		@Override
		public void show() {
			if(component==null) return;
			
			Dimension size = component.getPreferredSize();
			
			if(owner instanceof JComponent) {
				JComponent jc = (JComponent)owner;
				
				InputMap inputMap = jc.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "hidePopup");
				jc.getActionMap().put("hidePopup", hideAction);
			}
			
			if(component.getParent()!=applet.getLayeredPane()) {
				applet.getLayeredPane().add( component , JLayeredPane.POPUP_LAYER);
			}
			background = new MyBlockingPane();
			background.install(applet);
			
			//calculate and constrain the (x,y) location:
			Point convertedPoint = new Point(x,y);
			SwingUtilities.convertPointFromScreen(convertedPoint, applet);
			int myX = convertedPoint.x;
			int myY = convertedPoint.y;
			
			if( myY < 0 ) {
				myY = 0;
			}
			if( myY + size.height > applet.getHeight()-1 ) {
				myY = Math.max(0, applet.getHeight() - size.height - 1);
			}
			if( myX < 0 ) {
				myX = 0;
			}
			if( myX + size.width > applet.getWidth()-1 ) {
				myX = Math.max(0, applet.getWidth() - size.width - 1);
			}
			
			component.setBounds( myX, myY, size.width, size.height);
			component.validate();
		}
	}

	PopupFactory oldFactory;
	
	/**
	 * 
	 * @param oldFactory we defer to this factory when we don't have to
	 * use the fake popup model.
	 */
	AppletPopupFactory(PopupFactory oldFactory) {
		this.oldFactory = oldFactory;
	}
	
	@Override
	public Popup getPopup(Component owner, Component contents, int x, int y)
			throws IllegalArgumentException {
		Component[] hierarchy = getHierarchy(owner);
		for(int a = hierarchy.length-1; a>=0; a--) {
			if(hierarchy[a] instanceof JApplet && 
					contents instanceof JComponent) {
				return new AppletPopup( (JApplet)hierarchy[a], owner, (JComponent)contents, x, y);
				
			/* Unfortunately we can't simply check against a java.awt.Frame,
			 * because applets can be embedded in a 
			 * sun.plugin2.main.client.PluginEmbeddedFrame.
			 */
			//} else if(hierarchy[a] instanceof Frame) {
			} else if(hierarchy[a] instanceof JFrame) {
				return oldFactory.getPopup(owner, contents, x, y);
			}
		}
		return oldFactory.getPopup(owner, contents, x, y);
	}

}