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
package com.pump.inspector;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.util.BooleanProperty;
import com.pump.util.EnumProperty;
import com.pump.util.FloatProperty;
import com.pump.util.IntProperty;
import com.pump.util.Property;

public class PropertyInspector extends JPanel {
	private static final long serialVersionUID = 1L;

	InspectorLayout layout;
	Property[] properties = new Property[0];

	public PropertyInspector() {
		layout = new InspectorGridBagLayout(this);
	}

	public void clear() {
		layout.clear();
	}

	public void setPropertiesEnabled(boolean b) {
		for (int a = 0; a < properties.length; a++) {
			if (properties[a].isUserAdjustable())
				properties[a].setEnabled(b);
		}
	}

	public void setProperties(Property[] properties) {
		layout.clear();
		this.properties = properties; // TODO: clone?
		for (int a = 0; a < properties.length; a++) {
			PropertyEditor editor;
			if (properties[a] instanceof BooleanProperty) {
				BooleanProperty bp = (BooleanProperty) properties[a];
				editor = new BooleanPropertyEditor(bp);
			} else if (properties[a] instanceof IntProperty) {
				IntProperty ip = (IntProperty) properties[a];
				editor = new IntPropertyEditor(ip);
			} else if (properties[a] instanceof FloatProperty) {
				FloatProperty fp = (FloatProperty) properties[a];
				editor = new FloatPropertyEditor(fp);
			} else if (properties[a] instanceof EnumProperty) {
				EnumProperty ep = (EnumProperty) properties[a];
				editor = new EnumPropertyEditor(ep);
			} else {
				throw new RuntimeException("Unsupported property: "
						+ properties[a]);
			}
			editor.install(layout);
		}
	}
}

abstract class PropertyEditor {
	public final Property p;
	private final Runnable updateRunnable = new Runnable() {
		public void run() {
			update();
		}
	};
	private PropertyChangeListener listener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			SwingUtilities.invokeLater(updateRunnable);
		}
	};

	public PropertyEditor(Property p) {
		this.p = p;
		p.addPropertyChangeListener(listener);
	}

	public abstract void install(InspectorLayout layout);

	public abstract void update();

}

class BooleanPropertyEditor extends PropertyEditor {
	JLabel idLabel = new JLabel();
	JLabel valueLabel = new JLabel();
	JPanel idContainer = new JPanel(new GridBagLayout());
	JPanel valueContainer = new JPanel(new GridBagLayout());

	JCheckBox box = new JCheckBox();
	private int adjusting = 0;

	public BooleanPropertyEditor(BooleanProperty p) {
		super(p);
		box.setText(p.getName());
		box.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (adjusting > 0)
					return;
				adjusting++;
				try {
					BooleanPropertyEditor.this.p.setValue(box.isSelected() ? Boolean.TRUE
							: Boolean.FALSE);
				} finally {
					adjusting--;
				}
			}

		});
		update();
	}

	@Override
	public void install(InspectorLayout layout) {
		idLabel.setText(p.getName() + ":");
		layout.addRow(idContainer, valueContainer, false);
	}

	@Override
	public void update() {
		if (adjusting > 0)
			return;
		adjusting++;
		try {
			box.setSelected(((BooleanProperty) p).getValue());
		} finally {
			adjusting--;
		}

		idLabel.setEnabled(p.isEnabled());
		valueLabel.setEnabled(p.isEnabled());
		box.setEnabled(p.isEnabled());

		if (p.isUserAdjustable() && box.getParent() != idContainer) {
			install(box, idContainer);
			install(null, valueContainer);
		} else if (p.isUserAdjustable() == false
				&& idLabel.getParent() != valueContainer) {
			install(idLabel, idContainer);
			install(valueLabel, valueContainer);
		}
		if (valueLabel.isShowing())
			valueLabel.setText(p.getValue().toString());
	}

	private void install(JComponent comp, JPanel parent) {
		parent.removeAll();
		if (comp != null) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			parent.add(comp, c);
		}
		parent.revalidate();
	}
}

class IntPropertyEditor extends PropertyEditor {
	JSpinner spinner;
	JLabel label = new JLabel();
	JPanel valueContainer = new JPanel(new GridBagLayout());
	JLabel value = new JLabel();

	private int adjusting = 0;

	public IntPropertyEditor(IntProperty p) {
		super(p);
		SpinnerNumberModel model = new SpinnerNumberModel(p.getValue()
				.intValue(), p.getMin(), p.getMax(), 1);
		spinner = new JSpinner(model);
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (adjusting > 0)
					return;
				adjusting++;
				try {
					IntPropertyEditor.this.p.setValue(spinner.getValue());
				} finally {
					adjusting--;
				}
			}
		});
		update();
	}

	@Override
	public void install(InspectorLayout layout) {
		label.setText(p.getName() + ":");
		layout.addRow(label, valueContainer, false);
	}

	@Override
	public void update() {
		if (adjusting > 0)
			return;
		adjusting++;
		try {
			spinner.setValue(p.getValue());
		} finally {
			adjusting--;
		}

		label.setEnabled(p.isEnabled());
		spinner.setEnabled(p.isEnabled());
		value.setEnabled(p.isEnabled());

		if (p.isUserAdjustable() && spinner.getParent() != valueContainer) {
			install(spinner, valueContainer);
		} else if (p.isUserAdjustable() == false
				&& value.getParent() != valueContainer) {
			install(value, valueContainer);
		}
		if (value.isShowing())
			value.setText(p.getValue().toString());
	}

	private void install(JComponent comp, JPanel parent) {
		parent.removeAll();
		if (comp != null) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			parent.add(comp, c);
		}
		parent.revalidate();
	}
}

class FloatPropertyEditor extends PropertyEditor {
	JSpinner spinner;
	JLabel label = new JLabel();
	JPanel valueContainer = new JPanel(new GridBagLayout());
	JLabel value = new JLabel();
	private int adjusting = 0;

	public FloatPropertyEditor(FloatProperty p) {
		super(p);
		SpinnerNumberModel model = new SpinnerNumberModel(p.getValue()
				.floatValue(), p.getMin(), p.getMax(), 1);
		spinner = new JSpinner(model);
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (adjusting > 0)
					return;
				adjusting++;
				try {
					Number n = (Number) spinner.getValue();
					FloatPropertyEditor.this.p.setValue(new Float(n
							.floatValue()));
				} finally {
					adjusting--;
				}
			}
		});
		update();
	}

	@Override
	public void install(InspectorLayout layout) {
		label.setText(p.getName() + ":");
		layout.addRow(label, valueContainer, false);
	}

	@Override
	public void update() {
		if (adjusting > 0)
			return;
		adjusting++;
		try {
			spinner.setValue(p.getValue());
		} finally {
			adjusting--;
		}

		label.setEnabled(p.isEnabled());
		spinner.setEnabled(p.isEnabled());
		value.setEnabled(p.isEnabled());

		if (p.isUserAdjustable() && spinner.getParent() != valueContainer) {
			install(spinner, valueContainer);
		} else if (p.isUserAdjustable() == false
				&& value.getParent() != valueContainer) {
			install(value, valueContainer);
		}
		if (value.isShowing())
			value.setText(p.getValue().toString());
	}

	private void install(JComponent comp, JPanel parent) {
		parent.removeAll();
		if (comp != null) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			parent.add(comp, c);
		}
		parent.revalidate();
	}
}

class EnumPropertyEditor extends PropertyEditor {
	JComboBox comboBox;
	JLabel label = new JLabel();
	JPanel valueContainer = new JPanel(new GridBagLayout());
	JLabel value = new JLabel();
	private int adjusting = 0;

	public EnumPropertyEditor(EnumProperty p) {
		super(p);

		comboBox = new JComboBox(p.getValues());

		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (adjusting > 0)
					return;
				adjusting++;
				try {
					EnumPropertyEditor.this.p.setValue(comboBox
							.getSelectedItem());
				} finally {
					adjusting--;
				}
			}
		});

		update();
	}

	@Override
	public void install(InspectorLayout layout) {
		label.setText(p.getName() + ":");
		layout.addRow(label, valueContainer, false);
	}

	@Override
	public void update() {
		if (adjusting > 0)
			return;
		adjusting++;
		try {
			comboBox.setSelectedItem(p.getValue());
		} finally {
			adjusting--;
		}

		label.setEnabled(p.isEnabled());
		comboBox.setEnabled(p.isEnabled());
		value.setEnabled(p.isEnabled());

		if (p.isUserAdjustable() && comboBox.getParent() != valueContainer) {
			install(comboBox, valueContainer);
		} else if (p.isUserAdjustable() == false
				&& value.getParent() != valueContainer) {
			install(value, valueContainer);
		}
		if (value.isShowing())
			value.setText(p.getValue().toString());
	}

	private void install(JComponent comp, JPanel parent) {
		parent.removeAll();
		if (comp != null) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			parent.add(comp, c);
		}
		parent.revalidate();
	}
}