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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Constructor;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.TextUI;

import com.pump.graphics.TextOnlyGraphics2D;

/** This is an untouchable text field that sits on top
* of a parent <code>JTextField</code> providing a text prompt.
* 
* @see com.bric.swing.PromptSearchDemo
*/
public class TextFieldPrompt extends JTextField {
	private static final long serialVersionUID = 1L;

	ComponentAdapter componentListener = new ComponentAdapter() {
		@Override
		public void componentResized(ComponentEvent e) {
			updateBounds();
		}
	};

	HierarchyListener hierarchyListener = new HierarchyListener() {
		public void hierarchyChanged(HierarchyEvent e) {
			updateVisibility();
		}
	};
	
	FocusListener focusListener = new FocusListener() {
		public void focusGained(FocusEvent e) {
			updateVisibility();
		}
		public void focusLost(FocusEvent e) {
			updateVisibility();
		}
	};
	
	DocumentListener documentListener = new DocumentListener() {

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateVisibility();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			insertUpdate(e);
		}
		
	};
	
	PropertyChangeListener propertyListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			String n = evt.getPropertyName();
			if(n.equals("useSearchIcon") || n.equals("JTextField.variant")) {
				putClientProperty(n, evt.getNewValue());
			}
		}
	};

	/** Creates a new <code>TextFieldPrompt</code>.
	 * 
	 * @param parent the text field to add this prompt to.
	 * @param promptText the text to display as the prompt.  This can
	 * easily be controlled with <code>getText()</code> and <code>setText()</code>.
	 */
	public TextFieldPrompt(JTextField parent,String promptText) {
		this(parent,null,promptText);
	}
	
	/** Creates a new <code>TextFieldPrompt</code>.
	 * 
	 * @param parent the text field to add this prompt to.
	 * @param promptColor the color of this prompt text.
	 * @param promptText the text to display as the prompt.  This can
	 * easily be controlled with <code>getText()</code> and <code>setText()</code>.
	 */
	public TextFieldPrompt(JTextField parent,Color promptColor,String promptText) {
		super(promptText);
		
		if(promptColor==null)
			promptColor = Color.gray;
		
		parent.add(this);
		
		setFocusable(false);
		setEditable(false);
		setForeground(promptColor);
		setOpaque(false);
		
		addHierarchyListener(hierarchyListener);
		parent.addComponentListener(componentListener);
		addComponentListener(componentListener);
		parent.addFocusListener(focusListener);
		parent.getDocument().addDocumentListener(documentListener);
		parent.addPropertyChangeListener(propertyListener);

		putClientProperty( "useSearchIcon", parent.getClientProperty("useSearchIcon"));
		putClientProperty( "JTextField.variant", parent.getClientProperty("JTextField.variant"));
		
		updateBounds();
		try {
			try {
				TextUI ui = parent.getUI();
				Constructor<?> noArgConstructor = ui.getClass().getConstructor(new Class[] {});
				TextUI newUI = (TextUI)noArgConstructor.newInstance(new Object[] {});
				setUI(newUI);
				return;
			} catch(Throwable t) {}
	
			try {
				TextUI ui = parent.getUI();
				Constructor<?> noArgConstructor = ui.getClass().getConstructor(new Class[] { JTextField.class });
				TextUI newUI = (TextUI)noArgConstructor.newInstance(new Object[] { this });
				setUI(newUI);
			} catch(RuntimeException e) {
				throw e;
			} catch(Throwable t) {
				RuntimeException e2 = new RuntimeException();
				e2.initCause(t);
				throw e2;
			}
		} finally {
			updateVisibility();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint( new TextOnlyGraphics2D((Graphics2D)g, null) );
	}
	
	@Override
	public boolean contains(int x, int y) {
		return false;
	}
	
	private Runnable updateBoundsRunnable = new Runnable() {
		public void run() {
			updateBounds();
		}
	};
	private int adjustingBounds = 0;
	private void updateBounds() {
		if(SwingUtilities.isEventDispatchThread()==false) {
			SwingUtilities.invokeLater(updateBoundsRunnable);
			return;
		}
		if(adjustingBounds>0)
			return;
		
		adjustingBounds++;
		try {
			JTextField parent = (JTextField)getParent();
			setBounds(0,0,parent.getWidth(),parent.getHeight());
			updateVisibility();
		} finally {
			adjustingBounds--;
		}
	}
	
	private Runnable updateVisibilityRunnable = new Runnable() {
		public void run() {
			updateVisibility();
		}
	};

	private void updateVisibility() {
		if(SwingUtilities.isEventDispatchThread()==false) {
			SwingUtilities.invokeLater(updateVisibilityRunnable);
			return;
		}
		JTextField parent = (JTextField)getParent();
		boolean focused = parent.hasFocus();
		boolean empty = parent.getText().length()==0;
		setVisible( (!focused) && empty );
		if(isVisible())
			updateBounds();
	}
}