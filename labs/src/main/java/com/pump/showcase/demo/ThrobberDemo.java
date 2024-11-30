/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.showcase.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.inspector.Inspector;
import com.pump.plaf.*;
import com.pump.swing.JColorWell;
import com.pump.swing.JThrobber;
import com.pump.swing.popover.JPopover;

/**
 * This panel shows three simple JThrobber implementations.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/ThrobberDemo.png"
 * alt="A screenshot of the ThrobberDemo.">
 */
public class ThrobberDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JComboBox<Class<? extends ThrobberPainter>> typeComboBox = new JComboBox<>();
	JSlider sizeSlider = new ShowcaseSlider(8, 100, 16);
	JSlider rateSlider = new ShowcaseSlider(50, 200, 100);
	JThrobber throbber = new JThrobber();
	JColorWell color = new JColorWell(new Color(100, 0, 120));
	JCheckBox colorCheckBox = new JCheckBox("Custom Foreground:");

	public ThrobberDemo() {
		JPopover.add(sizeSlider, " pixels");
		JPopover.add(rateSlider, "%");

		Inspector layout = new Inspector(configurationPanel);
		layout.addRow(new JLabel("Type:"), typeComboBox);
		layout.addRow(new JLabel("Size:"), sizeSlider);
		layout.addRow(new JLabel("Rate:"), rateSlider);
		layout.addRow(colorCheckBox, color);

		Dictionary<Integer, JLabel> dictionary = new Hashtable<Integer, JLabel>();
		dictionary.put(50, new JLabel("50%"));
		dictionary.put(100, new JLabel("100%"));
		dictionary.put(200, new JLabel("200%"));

		rateSlider.setPaintTicks(true);
		rateSlider.setPaintLabels(true);
		rateSlider.setMajorTickSpacing(50);
		rateSlider.setMinorTickSpacing(10);
		rateSlider.setLabelTable(dictionary);

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.insets = new Insets(10, 10, 10, 10);
		examplePanel.add(throbber, c);

		ChangeListener changeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshThrobber();
			}

		};
		rateSlider.addChangeListener(changeListener);
		sizeSlider.addChangeListener(changeListener);
		color.getColorSelectionModel().addChangeListener(changeListener);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshThrobber();
			}
		};

		colorCheckBox.addActionListener(actionListener);
		typeComboBox.addActionListener(actionListener);

		typeComboBox.addItem(AquaThrobberPainter.class);
		typeComboBox.addItem(ChasingArrowsThrobberPainter.class);
		typeComboBox.addItem(PulsingCirclesThrobberPainter.class);
		typeComboBox.addItem(CircularThrobberPainter.class);

		typeComboBox.setRenderer(
				new LabelCellRenderer<Class<? extends ThrobberPainter>>(typeComboBox,
						true) {

					@Override
					protected void formatLabel(
							Class<? extends ThrobberPainter> value) {
						label.setText(value.getSimpleName());
					}

				});
	}

	private void refreshThrobber() {
		Class<?> c = (Class<?>) typeComboBox.getSelectedItem();
		ThrobberUI ui;
		try {
			ThrobberPainter painter = (ThrobberPainter) c.newInstance();
			ui = new ThrobberUI(painter);
			throbber.setUI(ui);
			int size = sizeSlider.getValue();
			throbber.setPreferredSize(new Dimension(size, size));
			throbber.getParent().revalidate();
			Float m = 100f / ((float) rateSlider.getValue());
			throbber.putClientProperty(ThrobberUI.PERIOD_MULTIPLIER_KEY, m);

			color.setEnabled(colorCheckBox.isSelected());
			if (colorCheckBox.isSelected()) {
				Color f = color.getColorSelectionModel().getSelectedColor();
				throbber.setForeground(f);
			} else {
				// installing a new UI will automatically change the
				// foreground,
				// so if the checkbox is unselected we'll get the right
				// color.
			}
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getTitle() {
		return "JThrobber Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new Swing component that is more compact than an indeterminate JProgressBar.";
	}

	@Override
	public URL getHelpURL() {
		return ThrobberDemo.class.getResource("throbberDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "throbber", "indicator", "feedback", "progress",
				"waiting", "Swing", "indeterminate" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JThrobber.class, AquaThrobberPainter.class,
				ChasingArrowsThrobberPainter.class, PulsingCirclesThrobberPainter.class };
	}
}