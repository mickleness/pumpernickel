package com.pump.showcase;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.lang.reflect.Field;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JScrollPane;

import com.pump.icon.AquaIcon;
import com.pump.plaf.LabelCellRenderer;
import com.pump.util.list.ObservableList;

public class AquaSystemIconDemo extends ShowcaseDemo {

	private static final long serialVersionUID = 1L;

	ObservableList<String> selectors = new ObservableList<>();
	JList<String> list = new JList(selectors.createUIView());
	Dimension maxConstrainingSize = new Dimension(48, 48);

	public AquaSystemIconDemo() {
		for (Field field : AquaIcon.class.getFields()) {
			if (field.getName().startsWith("SELECTOR_")) {
				try {
					String selector = (String) field.get(null);
					selectors.add(selector);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(list), c);

		list.setCellRenderer(new LabelCellRenderer<String>() {

			@Override
			protected void formatLabel(String value) {
				Icon icon = AquaIcon.getAquaIcon(value, maxConstrainingSize);
				label.setIcon(icon);
				label.setText("");
			}

		});
		list.setFixedCellHeight(maxConstrainingSize.width);
		list.setFixedCellHeight(maxConstrainingSize.height);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	}

	@Override
	public String getTitle() {
		return "Aqua SystemIcon Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates com.apple.laf.AquaIcon.SystemIcons.";
	}

	@Override
	public URL getHelpURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getKeywords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<?>[] getClasses() {
		// TODO Auto-generated method stub
		return null;
	}

}
