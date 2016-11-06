/*
 * @(#)NavigationPanelUI.java
 *
 * $Date: 2016-02-29 21:18:28 -0500 (Mon, 29 Feb 2016) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.Callable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSpinnerUI;

/**
 * This is the abstract parent class for JSpinners intended to navigate a collection of objects/properties.
 * <p>
 * The following code will automatically format your panel to include a label
 * that reads "Page 5 of 10" or "Image 1 of 2", etc:
 * <pre>mySpinner.putClientProperty( 
 * 		NavigationPanelUI.PROPERTY_DESCRIPTOR, 
 * 		new NumberSpinnerDescriptor(mySpinner, "Page"));</pre>
 * 	
 */
public abstract class NavigationPanelUI extends BasicSpinnerUI {

	/** This maps to a Callable&lt;String&gt; used to describe this spinner in a label.
	 * 
	 */
	public static final String PROPERTY_DESCRIPTOR = "NavigationPanelUI.descriptor";
	
	/** This maps to a Boolean indicating whether the user should be able to drag this spinner around.
	 * 
	 */
	public static final String PROPERTY_DRAGGABLE = "NavigationPanelUI.draggable";
	
	protected static final String PROPERTY_LABEL_CHANGE_LISTENER = "NavigationPanelUI.labelChangeListener";
	protected static final String EDITOR_NAME = "Spinner.editor";
	protected static final String LABEL_NAME = "Spinner.label";
	protected static final String NEXT_BUTTON_NAME = "Spinner.nextButton";
	protected static final String PREV_BUTTON_NAME = "Spinner.previousButton";

	
	/** Translate a panel with the mouse drag.
	 * 
	 */
	MouseInputListener dragListener = new MouseInputAdapter() {
		Point lastPoint;
		
		@Override
		public void mousePressed(MouseEvent e) {
			if(isDraggable(spinner))
				lastPoint = e.getPoint();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			lastPoint = null;
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if(lastPoint!=null) {
				Point currentPoint = e.getPoint();
				Rectangle r = spinner.getBounds();
				int dx = currentPoint.x - lastPoint.x;
				int dy = currentPoint.y - lastPoint.y;
				r.x += dx;
				r.y += dy;
				spinner.setBounds(r);
				
				lastPoint = currentPoint;
				lastPoint.x -= dx;
				lastPoint.y -= dy;
			}
		}

	};
	
	protected static class UpdateLabelListener implements ChangeListener, PropertyChangeListener {
		protected JSpinner spinner;
		protected JLabel label;

		UpdateLabelListener(JSpinner spinner, JLabel label) {
			this.spinner = spinner;
			this.label = label;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			refreshLabel();
		}
		
		void refreshLabel() {
			if(spinner.getUI() instanceof NavigationPanelUI) {
				((NavigationPanelUI)spinner.getUI()).refreshLabel(spinner, label);
			} else {
				refreshLabelDefault(spinner, label);
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getPropertyName().equals("model")) {
				refreshLabel();
			}
		}	
	}

	/** 
	 * This is used to describe a spinner's value.
	 * <p>
	 * This default implementation just converts a spinner's value to a String (or converts a null value to an empty String).
	 */
	public static class SpinnerDescriptor implements Callable<String> {
		private JSpinner spinner;
		private SpinnerModel model;
		
		/** Create a new SpinnerDescriptor.
		 * 
		 * @param spinner the spinner this applies to.
		 */
		public SpinnerDescriptor(JSpinner spinner) {
			if(spinner==null) throw new NullPointerException();
			this.spinner = spinner;
		}
		
		/** Create a new DefaultDescriptor.
		 * 
		 * @param model the model to consult.
		 */
		public SpinnerDescriptor(SpinnerModel model) {
			if(model==null) throw new NullPointerException();
			this.model = model;
		}
		
		protected SpinnerModel getModel() {
			if(model!=null) {
				return model;
			}
			return spinner.getModel();
		}
		
		@Override
		public String call() {
			Object value = getModel().getValue();
			return value==null ? "" : value.toString();
		}
	}
	/** 
	 * This describes SpinnerNumberModels. Here we have a min and a max, so
	 * we can say something like: "Page 5 of 10".
	 * To do this you'll have to specify the word "Page" during construction.
	 * <p>
	 * However if we just have a generic SpinnerModel: then there's no telling where
	 * the min/max is. In that case this just converts the spinner's current value to
	 * a String.
	 */
	public static class NumberSpinnerDescriptor extends SpinnerDescriptor {
		protected String word;
		protected int incr;
		
		/** Create a new NumberSpinnerDescriptor.
		 * 
		 * @param spinner the spinner this applies to.
		 * @param word in the expression "Page 5 of 10" this argument is "Page"
		 */
		public NumberSpinnerDescriptor(JSpinner spinner,String word) {
			this(spinner, word, 0);
		}
		
		/** Create a new DefaultDescriptor.
		 * 
		 * @param numberModel the number model to consult.
		 * @param word in the expression "Page 5 of 10" this argument is "Page"
		 */
		public NumberSpinnerDescriptor(SpinnerNumberModel numberModel,String word) {
			this(numberModel, word, 0);
		}
		
		/** Create a new DefaultDescriptor.
		 * 
		 * @param spinner the spinner this applies to.
		 * @param word in the expression "Page 5 of 10" this argument is "Page"
		 * @param incr an optional amount to increment spinner values by.
		 * For example if a model ranges from [0, 99] and this value is 1,
		 * then the text will present this model as ranging from [1,100]
		 */
		public NumberSpinnerDescriptor(JSpinner spinner,String word,int incr) {
			super(spinner);
			this.word = word;
			this.incr = incr;
		}
		
		/** Create a new DefaultDescriptor.
		 * 
		 * @param numberModel the number model to consult.
		 * @param word in the expression "Page 5 of 10" this argument is "Page"
		 * @param incr an optional amount to increment spinner values by.
		 * For example if a model ranges from [0, 99] and this value is 1,
		 * then the text will present this model as ranging from [1,100]
		 */
		public NumberSpinnerDescriptor(SpinnerNumberModel numberModel,String word,int incr) {
			super(numberModel);
			if(word==null)
				throw new NullPointerException();
			this.word = word;
			this.incr = incr;
		}
		
		@Override
		public String call() {
			SpinnerNumberModel numberModel = (SpinnerNumberModel)getModel();
			Number max = (Number)numberModel.getMaximum();
			if(incr!=0) {
				return word+" "+add(numberModel.getNumber(), incr)+" of "+add(max, incr);
			}
			return word+" "+(numberModel.getValue())+" of "+max;
		}
		
		private static Number add(Number value,int incr) {
			if(value instanceof Integer) {
				return Integer.valueOf(value.intValue() + incr);
			}
			return Double.valueOf(value.doubleValue() + incr);
		}
	}
	
	protected JLabel createLabel() {
		JLabel editor = new JLabel("");
		editor.setName(LABEL_NAME);
		return editor;
	}
	
	protected boolean isDraggable(JSpinner spinner) {
		Boolean b = (Boolean)spinner.getClientProperty(PROPERTY_DRAGGABLE);
		if(b==null)
			return false;
		return b.booleanValue();
	}

	/** Refresh a label to describe the contents of a spinner.
	 * The consults the {@link #PROPERTY_DESCRIPTOR} client property for the spinner,
	 * and if it is defined then it is used to format the label text. If that property
	 * is not defined, then the label is basically set to spinner.getValue().toString().
	 * 
	 * @param spinner the spinner to consult.
	 * @param label the label to update.
	 */
	public void refreshLabel(JSpinner spinner, JLabel label) {
		if(label==null)
			return;
		
		Callable<String> callable = (Callable<String>)spinner.getClientProperty(PROPERTY_DESCRIPTOR);
		if(callable==null) {
			refreshLabelDefault(spinner, label);
		} else {
			try {
				String text = callable.call();
				label.setText(text);
			} catch(Exception e) {
				label.setText(e.getMessage());
			}
		}
	}

	private static void refreshLabelDefault(JSpinner spinner, JLabel label) {
		Object value = spinner.getModel().getValue();
		if(value==null) {
			value = "";
		}
		label.setText( value.toString() );
	}
	
	protected JLabel label;
	
	private ChangeListener valueListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			SpinnerModel model = spinner.getModel();
			updateEnabledState(spinner.isEnabled(), model.getPreviousValue()!=null, model.getNextValue()!=null);
		}
	};
	
	protected void updateEnabledState(boolean isSpinnerEnabled,boolean hasPreviousValue,boolean hasNextValue) {
		getComponent(spinner, NEXT_BUTTON_NAME).setEnabled(isSpinnerEnabled && hasNextValue);
		getComponent(spinner, PREV_BUTTON_NAME).setEnabled(isSpinnerEnabled && hasPreviousValue);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		spinner.removeChangeListener( valueListener );
		UpdateLabelListener  changeListener = (UpdateLabelListener)c.getClientProperty(PROPERTY_LABEL_CHANGE_LISTENER);
		((JSpinner)c).removeChangeListener(changeListener);
		((JSpinner)c).removePropertyChangeListener(changeListener);
		c.putClientProperty(PROPERTY_LABEL_CHANGE_LISTENER, null);
		c.removeMouseListener(dragListener);
		c.removeMouseMotionListener(dragListener);
	}

	protected static Component getComponent(Container parent, String componentName) {
		for(int a = 0; a<parent.getComponentCount(); a++) {
			if(parent.getComponent(a).getName().equals(componentName)) {
				return parent.getComponent(a);
			}
		}
		return null;
	}

	public JLabel getLabel() {
		return label;
	}
	
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		
		spinner.addChangeListener( valueListener );
		label = createLabel();
		maybeAdd(label, "Label");
		UpdateLabelListener changeListener = new UpdateLabelListener( (JSpinner)c, label);
		changeListener.refreshLabel();
		c.putClientProperty(PROPERTY_LABEL_CHANGE_LISTENER, changeListener);
		((JSpinner)c).addChangeListener(changeListener);
		((JSpinner)c).addPropertyChangeListener(changeListener);
		c.addMouseListener(dragListener);
		c.addMouseMotionListener(dragListener);
		c.setBorder(null);
	}
	
    public static ComponentUI createUI(JComponent c) {
        return new LargeNavigationPanelUI();
    }
	
    /** If a component is non-null, then add it to this Spinner.
     * 
     * @param componentToAdd the component to add
     * @param constraintName the name of the constraint this component is added with.
     */
    protected void maybeAdd(Component componentToAdd, String constraintName) {
        if (componentToAdd != null) {
            spinner.add(componentToAdd, constraintName);
        }
    }

    /**
     * Suspend all listeners, then adjust the value max, then resume all listeners and fire them exactly once.
     * 
     * @param spinner
     * @param newValue
     * @param newMax
     */
	public static void setValue(JSpinner spinner, int newValue, int newMax) {
		if(newValue>newMax)
			throw new IllegalArgumentException(newValue+">"+newMax);
		SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
		
		ChangeListener[] listeners = spinner.getListeners(ChangeListener.class);
		try {
			for(ChangeListener l : listeners) {
				spinner.removeChangeListener(l);
			}
			if(newValue>((Number)model.getMaximum()).doubleValue()) {
				model.setMaximum(newMax);
				model.setValue(newValue);
			} else {
				model.setValue(newValue);
				model.setMaximum(newMax);
			}
		} finally {
			for(ChangeListener l : listeners) {
				spinner.addChangeListener(l);
			}
			for(ChangeListener l : listeners) {
				l.stateChanged(new ChangeEvent(spinner));
			}
		}
	}
}
