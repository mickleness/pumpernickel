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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pump.icon.RefreshIcon;
import com.pump.image.ImageLoader;
import com.pump.image.pixel.Scaling;
import com.pump.inspector.Inspector;
import com.pump.reflect.Reflection;
import com.pump.swing.FontComboBox;
import com.pump.swing.JLink;
import com.pump.swing.JSwitchButton;
import com.pump.util.JVM;

public class JButtonDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	private static final String NONE = "none";

	public enum Horizontal {
		Right(SwingConstants.RIGHT), Left(SwingConstants.LEFT), Center(
				SwingConstants.CENTER), Leading(SwingConstants.LEADING), Trailing(
				SwingConstants.TRAILING);

		public static Horizontal valueOf(int constant) {
			for (Horizontal h : Horizontal.values()) {
				if (h.constant == constant)
					return h;
			}
			return null;
		}

		int constant;

		Horizontal(int constant) {
			this.constant = constant;
		}
	}

	public enum Vertical {
		Top(SwingConstants.TOP), Center(SwingConstants.CENTER), Bottom(
				SwingConstants.BOTTOM);

		public static Vertical valueOf(int constant) {
			for (Vertical v : Vertical.values()) {
				if (v.constant == constant)
					return v;
			}
			return null;
		}

		int constant;

		Vertical(int constant) {
			this.constant = constant;
		}
	}

	Map<String, Class> buttonTypeMap = new HashMap<>();
	JComboBox<String> buttonClassComboBox = new JComboBox<>();
	FontComboBox fontComboBox;
	JLabel fontDescriptor = new JLabel("");
	JComboBox<String> iconComboBox = new JComboBox<>();
	JTextField text = new JTextField("Name");
	JCheckBox paintBorderCheckbox = new JCheckBox("Border", true);
	JCheckBox paintContentCheckbox = new JCheckBox("Content", true);
	JCheckBox paintFocusCheckbox = new JCheckBox("Focus", true);
	JComboBox<Horizontal> horizontalAlignmentComboBox = new JComboBox<>(
			Horizontal.values());
	JComboBox<Horizontal> horizontalTextPositionComboBox = new JComboBox<>(
			Horizontal.values());
	JComboBox<Vertical> verticalAlignmentComboBox = new JComboBox<>(
			Vertical.values());
	JComboBox<Vertical> verticalTextPositionComboBox = new JComboBox<>(
			Vertical.values());
	JComboBox<String> aquaTypeComboBox;
	JComboBox<String> sizeVariantComboBox = new JComboBox<String>(new String[] {
			"regular", "small", "mini" });
	JComboBox<String> segmentPositionComboBox = new JComboBox<String>(
			new String[] { "only", "first", "middle", "last" });

	public JButtonDemo() {
		super(false, false, true);
		fontComboBox = new FontComboBox(new UIManagerFontFactory());
		Class[] buttonTypes = new Class[] { JButton.class, JToggleButton.class,
				JRadioButton.class, JCheckBox.class, JSwitchButton.class,
				JLink.class };
		for (Class buttonType : buttonTypes) {
			buttonTypeMap.put(buttonType.getSimpleName(), buttonType);
			buttonClassComboBox.addItem(buttonType.getSimpleName());
		}

		ActionListener actionRefreshListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshButton();
			}

		};

		Inspector inspector = createConfigurationInspector(300);
		inspector.addRow(new JLabel("Button Class:"), buttonClassComboBox);
		inspector.addRow(new JLabel("Font:"), fontComboBox);
		inspector.addRow(new JLabel(""), fontDescriptor);
		inspector.addRow(new JLabel("Icon:"), iconComboBox);
		inspector.addRow(new JLabel("Text:"), text, true);
		inspector.addRow(new JLabel("Horizontal Alignment:"),
				horizontalAlignmentComboBox);
		inspector.addRow(new JLabel("Horizontal Text Position:"),
				horizontalTextPositionComboBox);
		inspector.addRow(new JLabel("Vertical Alignment:"),
				verticalAlignmentComboBox);
		inspector.addRow(new JLabel("Vertical Text Position:"),
				verticalTextPositionComboBox);
		inspector.addRow(new JLabel("Paint:"), paintBorderCheckbox,
				paintContentCheckbox, paintFocusCheckbox);

		JButton dummyButton = new JButton();
		horizontalAlignmentComboBox.setSelectedItem(Horizontal
				.valueOf(dummyButton.getHorizontalAlignment()));
		horizontalTextPositionComboBox.setSelectedItem(Horizontal
				.valueOf(dummyButton.getHorizontalTextPosition()));
		verticalAlignmentComboBox.setSelectedItem(Vertical.valueOf(dummyButton
				.getVerticalAlignment()));
		verticalTextPositionComboBox.setSelectedItem(Vertical
				.valueOf(dummyButton.getVerticalTextPosition()));

		if (JVM.isMac) {
			inspector.addSeparator();
			// String[] aquaTypes = getAquaTypes();
			String[] aquaTypes = new String[] { NONE, "bevel", "capsule",
					"combobox", "comboboxEndCap", "comboboxInternal",
					"disclosure", "gradient", "help", "icon", "recessed",
					"round", "roundRect", "scrollColumnSizer", "segmented",
					"segmentedCapsule", "segmentedGradient",
					"segmentedRoundRect", "segmentedTextured",
					"segmentedTexturedRounded", "square", "text", "textured",
					"texturedRound", "toggle", "toolbar", "well" };
			aquaTypeComboBox = new JComboBox<String>(aquaTypes);
			inspector.addRow(new JLabel("Button Type:"), aquaTypeComboBox);
			inspector.addRow(new JLabel("Segment Position:"),
					segmentPositionComboBox);
			inspector.addRow(new JLabel("Size Variant:"), sizeVariantComboBox);

			aquaTypeComboBox.addActionListener(actionRefreshListener);
			sizeVariantComboBox.addActionListener(actionRefreshListener);
			segmentPositionComboBox.addActionListener(actionRefreshListener);
		}

		iconComboBox.addItem("None");
		iconComboBox.addItem("Thumbnail");
		iconComboBox.addItem("Refresh");

		buttonClassComboBox.addActionListener(actionRefreshListener);
		fontComboBox.addActionListener(actionRefreshListener);
		iconComboBox.addActionListener(actionRefreshListener);
		paintBorderCheckbox.addActionListener(actionRefreshListener);
		paintContentCheckbox.addActionListener(actionRefreshListener);
		paintFocusCheckbox.addActionListener(actionRefreshListener);
		horizontalAlignmentComboBox.addActionListener(actionRefreshListener);
		horizontalTextPositionComboBox.addActionListener(actionRefreshListener);
		verticalAlignmentComboBox.addActionListener(actionRefreshListener);
		verticalTextPositionComboBox.addActionListener(actionRefreshListener);

		text.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				refreshButton();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				refreshButton();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				refreshButton();
			}

		});

		// AbstractButton b = new JButton();
		// b.setIconTextGap(iconTextGap);
		// b.setMargin(m);

		paintBorderCheckbox
				.setToolTipText("This controls AbstractButton#setBorderPainted(boolean)");
		paintContentCheckbox
				.setToolTipText("This controls AbstractButton#setContentAreaFilled(boolean)");
		paintFocusCheckbox
				.setToolTipText("This controls AbstractButton#setFocusPainted(boolean)");

		refreshButton();
	}

	/**
	 * Return the different types of Aqua buttons we can render.
	 * <p>
	 * This method looks up a static map in com.apple.laf classes to identify
	 * its list of values.
	 */
	private static String[] getAquaTypes() {
		if (!JVM.isMac)
			return new String[] {};
		try {
			Class c = Class.forName("com.apple.laf.AquaButtonExtendedTypes");
			Map map = (Map) Reflection.invokeMethod(c, null, "getAllTypes");
			SortedSet<String> names = new TreeSet<>();
			names.addAll(map.keySet());
			return names.toArray(new String[names.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String[] {};
	}

	private AbstractButton lastButton;

	private void refreshButton() {
		try {
			Font font = fontComboBox.getSelectedFont();
			fontDescriptor.setText(" " + font.getName() + " "
					+ font.getSize2D());
			String buttonClass = (String) buttonClassComboBox.getSelectedItem();
			AbstractButton button = (AbstractButton) buttonTypeMap.get(
					buttonClass).newInstance();
			if (lastButton != null)
				button.setSelected(lastButton.isSelected());
			button.setFont(font);
			button.setText(text.getText());
			lastButton = button;

			if (iconComboBox.getSelectedIndex() == 1) {
				button.setIcon(new ImageIcon(getThumbnail()));
			} else if (iconComboBox.getSelectedIndex() == 2) {
				button.setIcon(new RefreshIcon(30));
			}

			Horizontal hAlign = (Horizontal) horizontalAlignmentComboBox
					.getSelectedItem();
			button.setHorizontalAlignment(hAlign.constant);

			Horizontal hTextPos = (Horizontal) horizontalTextPositionComboBox
					.getSelectedItem();
			button.setHorizontalTextPosition(hTextPos.constant);

			Vertical vAlign = (Vertical) verticalAlignmentComboBox
					.getSelectedItem();
			button.setVerticalAlignment(vAlign.constant);

			Vertical vTextPos = (Vertical) verticalTextPositionComboBox
					.getSelectedItem();
			button.setVerticalTextPosition(vTextPos.constant);

			button.setBorderPainted(paintBorderCheckbox.isSelected());
			button.setContentAreaFilled(paintContentCheckbox.isSelected());
			button.setFocusPainted(paintFocusCheckbox.isSelected());

			if (aquaTypeComboBox != null) {
				String buttonType = (String) aquaTypeComboBox.getSelectedItem();
				if (!NONE.equalsIgnoreCase(buttonType)) {
					button.putClientProperty("JButton.buttonType", buttonType);
				}

				if (buttonType.startsWith("segmented")) {
					segmentPositionComboBox.setEnabled(true);
					button.putClientProperty("JButton.segmentPosition",
							(String) segmentPositionComboBox.getSelectedItem());
				} else {
					segmentPositionComboBox.setEnabled(false);
				}
				button.putClientProperty("JComponent.sizeVariant",
						(String) sizeVariantComboBox.getSelectedItem());
			}

			examplePanel.removeAll();
			examplePanel.add(button);
		} catch (Exception e) {
			e.printStackTrace();
			examplePanel.removeAll();
			examplePanel.add(new JLabel("Error: see console"));
		} finally {
			examplePanel.revalidate();
		}
	}

	static BufferedImage thumbnail = null;

	private static BufferedImage getThumbnail() {
		if (thumbnail == null) {
			BufferedImage bi = ImageLoader.createImage(AlphaCompositeDemo.class
					.getResource("balloon.png"));
			thumbnail = Scaling.scaleProportionally(bi, new Dimension(30, 30));
		}
		return thumbnail;
	}

	@Override
	public String getTitle() {
		return "JButton Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates basic button configurations in Swing.";
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