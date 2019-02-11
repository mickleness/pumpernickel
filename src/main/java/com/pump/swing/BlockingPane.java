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

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;

public class BlockingPane extends JComponent implements MouseListener,
		MouseMotionListener {

	private static final long serialVersionUID = 1L;

	public BlockingPane() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	ComponentListener rootPaneComponentListener = new ComponentListener() {

		public void componentHidden(ComponentEvent e) {
		}

		public void componentMoved(ComponentEvent e) {
		}

		public void componentResized(ComponentEvent e) {
			Component c = e.getComponent();
			setBounds(0, 0, c.getWidth(), c.getHeight());
		}

		public void componentShown(ComponentEvent e) {
		}

	};

	private JRootPane rootPaneInUse = null;

	public void install(RootPaneContainer rpc) {
		if (getParent() != rpc.getLayeredPane())
			rpc.getLayeredPane().add(this, JLayeredPane.POPUP_LAYER);

		if (rootPaneInUse != null)
			rootPaneInUse.removeComponentListener(rootPaneComponentListener);

		rootPaneInUse = rpc.getRootPane();
		setBounds(0, 0, rootPaneInUse.getWidth(), rootPaneInUse.getHeight());
		setVisible(true);
		rootPaneInUse.addComponentListener(rootPaneComponentListener);
	}

	public void mouseDragged(MouseEvent e) {
		e.consume();
	}

	public void mouseMoved(MouseEvent e) {
		e.consume();
	}

	public void mouseClicked(MouseEvent e) {
		e.consume();
	}

	public void mouseEntered(MouseEvent e) {
		e.consume();
	}

	public void mouseExited(MouseEvent e) {
		e.consume();
	}

	public void mousePressed(MouseEvent e) {
		e.consume();
	}

	public void mouseReleased(MouseEvent e) {
		e.consume();
	}
}