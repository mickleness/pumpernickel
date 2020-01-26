package com.pump.showcase;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JList;
import javax.swing.JScrollPane;

import com.pump.util.list.ObservableList;

public abstract class ShowcaseIconDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	protected ObservableList<String> images = new ObservableList<>();
	protected JList<String> list = new JList<>(images.createUIView());
	protected Dimension maxConstrainingSize = new Dimension(48, 48);

	public ShowcaseIconDemo() {

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(list), c);

		list.setFixedCellHeight(maxConstrainingSize.width);
		list.setFixedCellHeight(maxConstrainingSize.height);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(0);
	}
}