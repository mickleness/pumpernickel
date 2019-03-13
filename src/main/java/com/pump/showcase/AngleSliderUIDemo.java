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
package com.pump.showcase;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;

import com.pump.inspector.InspectorGridBagLayout;
import com.pump.inspector.InspectorLayout;
import com.pump.plaf.AngleSliderUI;
import com.pump.plaf.AquaAngleSliderUI;

/**
 * This demos the <code>AngleSliderUI</code> class.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/AngleSliderUIDemo.png"
 * alt="A screenshot of the AngleSliderUIDemo.">
 */
public class AngleSliderUIDemo extends JPanel implements ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JPanel controls = new JPanel(new GridBagLayout());
	JComboBox<String> uiTypeComboBox = new JComboBox<>();
	JSlider angleSlider = new JSlider();
	JRadioButton stateEnabled = new JRadioButton("Enabled", true);
	JRadioButton stateDisabled = new JRadioButton("Disabled", false);
	JSlider sizeSlider = new JSlider(0, 100, 0);

	ActionListener actionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshAngleSlider();
		}

	};

	ChangeListener changeListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			refreshAngleSlider();
		}

	};

	public AngleSliderUIDemo() {
		super(new GridBagLayout());
		InspectorLayout layout = new InspectorGridBagLayout(controls);
		layout.addRow(new JLabel("Size:"), sizeSlider, true);
		layout.addRow(new JLabel("Style:"), uiTypeComboBox, false);
		layout.addRow(new JLabel("State:"), stateEnabled, stateDisabled);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		add(controls, c);
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		c.weighty = 1;
		c.anchor = GridBagConstraints.NORTH;
		add(angleSlider, c);

		ButtonGroup g1 = new ButtonGroup();
		g1.add(stateEnabled);
		g1.add(stateDisabled);

		stateEnabled.addActionListener(actionListener);
		stateDisabled.addActionListener(actionListener);
		sizeSlider.addChangeListener(changeListener);
		uiTypeComboBox.addActionListener(actionListener);

		uiTypeComboBox.addItem(AngleSliderUI.class.getSimpleName());
		uiTypeComboBox.addItem(AquaAngleSliderUI.class.getSimpleName());

		refreshAngleSlider();
	}

	private void refreshAngleSlider() {
		SliderUI ui = uiTypeComboBox.getSelectedIndex() == 0 ? new AngleSliderUI()
				: new AquaAngleSliderUI();
		angleSlider.setUI(ui);
		angleSlider.setEnabled(stateEnabled.isSelected());

		Dimension d = ui.getPreferredSize(angleSlider);
		int i = sizeSlider.getValue() + Math.max(d.width, d.height);
		angleSlider.setPreferredSize(new Dimension(i, i));
	}

	@Override
	public String getTitle() {
		return "AngleSliderUI Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new SliderUI to represent an angle.";
	}

	@Override
	public URL getHelpURL() {
		return AngleSliderUIDemo.class.getResource("angleSliderUIDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "angle", "degree", "paint", "slider" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { AngleSliderUI.class, AquaAngleSliderUI.class,
				JSlider.class };
	}
}