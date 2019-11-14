package com.pump.inspector;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import com.pump.data.AbstractAttributeDataImpl;
import com.pump.data.Key;

/**
 * This is a bean describing the contents of an inspector row.
 */
public class InspectorRow extends AbstractAttributeDataImpl {
	private static final long serialVersionUID = 1L;
	
	/**
	 * This is copied from JComponent. This is a client property on
	 * an arbitrary JComponent that points to a JLabel.
	 * <p>
	 * We'll experiment with assigning it to other components
	 * (mostly JCheckBoxes) and see if it works well with accessibility
	 * software.
	 */
    static final String LABELED_BY_PROPERTY = "labeledBy";
	
	public static final Key<JComponent> PROPERTY_LEAD_COMPONENT = new Key<>(JComponent.class, InspectorRow.class.getName()+"#leadComponent");
	public static final Key<JComponent> PROPERTY_MAIN_COMPONENT = new Key<>(JComponent.class, InspectorRow.class.getName()+"#mainComponent");
	public static final Key<Float> PROPERTY_MAIN_COMPONENT_HORIZONTAL_WEIGHT =  new Key<>(Float.class, InspectorRow.class.getName()+"#mainComponentHorizontalWeight");
	public static final Key<Float> PROPERTY_ROW_VERTICAL_WEIGHT =  new Key<>(Float.class, InspectorRow.class.getName()+"#rowVerticalWeight");
	
	public InspectorRow(JComponent leadComponent,JComponent mainComponent, float mainComponentHorizontalWeight,float rowVerticalWeight) {
		setLeadComponent(leadComponent);
		setMainComponent(mainComponent);
		setMainComponentHorizontalWeight(mainComponentHorizontalWeight);
		setRowVerticalWeight(rowVerticalWeight);
		if(leadComponent instanceof JLabel && mainComponent!=null) {
			JLabel l = (JLabel) leadComponent;
			l.setLabelFor(mainComponent);
		} else if(leadComponent!=null && mainComponent!=null) {
			mainComponent.putClientProperty(LABELED_BY_PROPERTY, leadComponent);
		}
	}
	
	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		super.addPropertyChangeListener(propertyName, listener);
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);
	}
	
	/**
	 * Return lead optional lead component. This is usually a JLabel or a JCheckbox.
	 * This component will be right-aligned in the left-most column.
	 */
	public JComponent getLeadComponent() {
		return getAttribute(PROPERTY_LEAD_COMPONENT);
	}
	
	/**
	 * Return the optional main component. If the lead is non-null, then this occupies
	 * the second column. If the lead is null, then this occupies the entire
	 * width of the inspector.
	 */
	public JComponent getMainComponent() {
		return getAttribute(PROPERTY_MAIN_COMPONENT);
	}
	
	/**
	 * Return a float from [0,1] indicating how to distribute extra width for the main
	 * component. Usually this is either zero (indicating the main component gets its
	 * preferred width) or one (indicating the main component gets the remaining width).
	 */
	public float getMainComponentHorizontalWeight() {
		return getAttribute(PROPERTY_MAIN_COMPONENT_HORIZONTAL_WEIGHT);
	}
	
	/**
	 * Return a float from [0,1] indicating how to distribute extra height for this row.
	 * If an inspector is given more than its preferred height: this is used to determine
	 * which rows use the extra height. (If all rows have a vertical weight of zero:
	 * then the inspector is top-aligned with empty space at the bottom.)
	 */
	public float getRowVerticalWeight() {
		return getAttribute(PROPERTY_ROW_VERTICAL_WEIGHT);
	}
	
	public JComponent setLeadComponent(JComponent leadComponent) {
		return setAttribute(PROPERTY_LEAD_COMPONENT, leadComponent);
	}
	
	public JComponent setMainComponent(JComponent mainComponent) {
		return setAttribute(PROPERTY_MAIN_COMPONENT, mainComponent);
	}
	
	public Float setMainComponentHorizontalWeight(float mainComponentHorizontalWeight) {
		return setAttribute(PROPERTY_MAIN_COMPONENT_HORIZONTAL_WEIGHT, mainComponentHorizontalWeight);
	}
	
	public Float setRowVerticalWeight(float rowVerticalWeight) {
		return setAttribute(PROPERTY_ROW_VERTICAL_WEIGHT, rowVerticalWeight);
	}

	/**
	 * Return the non-null components in this InspectorRow.
	 */
	public List<JComponent> getComponents() {
		List<JComponent> returnValue = new ArrayList<>(3);
		JComponent lead = getLeadComponent();
		JComponent main = getMainComponent();
		if(lead!=null)
			returnValue.add(lead);
		if(main!=null)
			returnValue.add(main);
		
		return returnValue;
	}
}
