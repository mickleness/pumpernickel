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
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ButtonUI;

import com.pump.icon.RefreshIcon;
import com.pump.image.ImageLoader;
import com.pump.image.pixel.Scaling;
import com.pump.inspector.Inspector;
import com.pump.inspector.InspectorRowPanel;
import com.pump.plaf.BevelButtonUI;
import com.pump.plaf.GradientButtonUI;
import com.pump.plaf.QButtonUI;
import com.pump.plaf.QComboBoxUI;
import com.pump.plaf.RecessedButtonUI;
import com.pump.plaf.RetroButtonUI;
import com.pump.plaf.RoundRectButtonUI;
import com.pump.plaf.ShimmerPaintUIEffect;
import com.pump.plaf.SquareButtonUI;
import com.pump.plaf.ZoomIconPaintUIEffect;
import com.pump.reflect.Reflection;
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

	Map<String, Class> buttonUITypeMap = new HashMap<>();
	JComboBox<String> buttonUIClassComboBox = new JComboBox<>();
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
	JCheckBox shimmerCheckBox = new JCheckBox("Shimmer", false);
	JCheckBox zoomCheckBox = new JCheckBox("Zoom Icon", false);
	JCheckBox comboBoxCheckBox = new JCheckBox("JComboBox:", false);
	JCheckBox comboBoxSeparatorCheckBox = new JCheckBox("Separator", false);
	JCheckBox comboBoxPopDownCheckBox = new JCheckBox("Pop Down", false);
	JCheckBox customShapeCheckBox = new JCheckBox("Custom Shape:", false);
	JRadioButton customShapeCircleButton = new JRadioButton("Circle", true);
	JRadioButton customShapeDiamondButton = new JRadioButton("Diamond", false);
	Collection<InspectorRowPanel> aquaRows = new HashSet<>();
	Collection<InspectorRowPanel> qbuttonRows = new HashSet<>();
	InspectorRowPanel segmentPositionRow;

	public JButtonDemo() {
		super(false, false, true);
		JButton dummyButton = new JButton();

		List<Class> buttonUITypes = new ArrayList<>();

		LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
		for (LookAndFeelInfo lafInfo : lafs) {
			try {
				LookAndFeel laf = (LookAndFeel) Class.forName(
						lafInfo.getClassName()).newInstance();
				laf.initialize();
				UIDefaults defaults = laf.getDefaults();
				JButton testButton = new JButton("test");
				ButtonUI ui = (ButtonUI) defaults.getUI(testButton);
				try {
					testButton.setUI(ui);
					// only keep the UI if the call to setUI didn't throw an
					// exception:
					buttonUITypes.add(ui.getClass());
				} catch (Exception e) {
					// Nimbus throws an exception resembling:

					// @formatter:off
					// java.lang.ClassCastException: com.apple.laf.AquaLookAndFeel cannot be cast to javax.swing.plaf.nimbus.NimbusLookAndFeel
					// at javax.swing.plaf.nimbus.NimbusStyle.validate(NimbusStyle.java:250)
					// at javax.swing.plaf.nimbus.NimbusStyle.getValues(NimbusStyle.java:806)
					// at javax.swing.plaf.nimbus.NimbusStyle.getInsets(NimbusStyle.java:485)
					// at javax.swing.plaf.synth.SynthStyle.installDefaults(SynthStyle.java:913)
					// at javax.swing.plaf.synth.SynthLookAndFeel.updateStyle(SynthLookAndFeel.java:265)
					// at javax.swing.plaf.synth.SynthButtonUI.updateStyle(SynthButtonUI.java:79)
					// at javax.swing.plaf.synth.SynthButtonUI.installDefaults(SynthButtonUI.java:62)
					// at javax.swing.plaf.basic.BasicButtonUI.installUI(BasicButtonUI.java:88)
					// at javax.swing.JComponent.setUI(JComponent.java:666)
					// @formatter:on
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		buttonUITypes.add(BevelButtonUI.class);
		buttonUITypes.add(GradientButtonUI.class);
		buttonUITypes.add(RecessedButtonUI.class);
		buttonUITypes.add(RetroButtonUI.class);
		buttonUITypes.add(RoundRectButtonUI.class);
		buttonUITypes.add(SquareButtonUI.class);

		for (Class buttonUIType : buttonUITypes) {
			buttonUITypeMap.put(buttonUIType.getSimpleName(), buttonUIType);
			buttonUIClassComboBox.addItem(buttonUIType.getSimpleName());
		}
		buttonUIClassComboBox.setSelectedItem(dummyButton.getUI().getClass()
				.getSimpleName());

		ActionListener actionRefreshListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshButton();
			}

		};

		ButtonGroup g = new ButtonGroup();
		g.add(customShapeCircleButton);
		g.add(customShapeDiamondButton);

		Inspector inspector = createConfigurationInspector(300);
		inspector.addRow(new JLabel("ButtonUI:"), buttonUIClassComboBox);
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

		horizontalAlignmentComboBox.setSelectedItem(Horizontal
				.valueOf(dummyButton.getHorizontalAlignment()));
		horizontalTextPositionComboBox.setSelectedItem(Horizontal
				.valueOf(dummyButton.getHorizontalTextPosition()));
		verticalAlignmentComboBox.setSelectedItem(Vertical.valueOf(dummyButton
				.getVerticalAlignment()));
		verticalTextPositionComboBox.setSelectedItem(Vertical
				.valueOf(dummyButton.getVerticalTextPosition()));

		// String[] aquaTypes = getAquaTypes();
		String[] aquaTypes = new String[] { NONE, "bevel", "capsule",
				"combobox", "comboboxEndCap", "comboboxInternal", "disclosure",
				"gradient", "help", "icon", "recessed", "round", "roundRect",
				"scrollColumnSizer", "segmented", "segmentedCapsule",
				"segmentedGradient", "segmentedRoundRect", "segmentedTextured",
				"segmentedTexturedRounded", "square", "text", "textured",
				"texturedRound", "toggle", "toolbar", "well" };
		aquaTypeComboBox = new JComboBox<String>(aquaTypes);
		aquaRows.add(inspector.addRow(new JLabel("Button Type:"),
				aquaTypeComboBox));
		segmentPositionRow = inspector.addRow(new JLabel("Segment Position:"),
				segmentPositionComboBox);
		aquaRows.add(inspector.addRow(new JLabel("Size Variant:"),
				sizeVariantComboBox));
		qbuttonRows.add(inspector.addRow(new JLabel("Effects:"),
				shimmerCheckBox, zoomCheckBox));
		qbuttonRows.add(inspector.addRow(customShapeCheckBox,
				customShapeCircleButton, customShapeDiamondButton));
		qbuttonRows.add(inspector.addRow(comboBoxCheckBox,
				comboBoxPopDownCheckBox, comboBoxSeparatorCheckBox));

		customShapeCheckBox.addActionListener(actionRefreshListener);
		customShapeCircleButton.addActionListener(actionRefreshListener);
		customShapeDiamondButton.addActionListener(actionRefreshListener);
		comboBoxCheckBox.addActionListener(actionRefreshListener);
		comboBoxSeparatorCheckBox.addActionListener(actionRefreshListener);
		comboBoxPopDownCheckBox.addActionListener(actionRefreshListener);
		aquaTypeComboBox.addActionListener(actionRefreshListener);
		sizeVariantComboBox.addActionListener(actionRefreshListener);
		segmentPositionComboBox.addActionListener(actionRefreshListener);

		iconComboBox.addItem("None");
		iconComboBox.addItem("Thumbnail");
		iconComboBox.addItem("Refresh");

		buttonUIClassComboBox.addActionListener(actionRefreshListener);
		iconComboBox.addActionListener(actionRefreshListener);
		paintBorderCheckbox.addActionListener(actionRefreshListener);
		paintContentCheckbox.addActionListener(actionRefreshListener);
		paintFocusCheckbox.addActionListener(actionRefreshListener);
		horizontalAlignmentComboBox.addActionListener(actionRefreshListener);
		horizontalTextPositionComboBox.addActionListener(actionRefreshListener);
		verticalAlignmentComboBox.addActionListener(actionRefreshListener);
		verticalTextPositionComboBox.addActionListener(actionRefreshListener);
		shimmerCheckBox.addActionListener(actionRefreshListener);
		zoomCheckBox.addActionListener(actionRefreshListener);

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

		zoomCheckBox.setToolTipText("Zoom the icon when clicked.");
		shimmerCheckBox.setToolTipText("Shimmer the button on mouseover.");

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
			JComboBox comboBox = null;
			AbstractButton button = new JButton();

			String buttonUIClass = (String) buttonUIClassComboBox
					.getSelectedItem();
			ButtonUI buttonUI = (ButtonUI) buttonUITypeMap.get(buttonUIClass)
					.newInstance();
			button.setUI(buttonUI);

			if (lastButton != null)
				button.setSelected(lastButton.isSelected());
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

			boolean isAqua = buttonUIClass.toLowerCase().contains("aqua");
			boolean isQButton = QButtonUI.class.isInstance(button.getUI());
			for (InspectorRowPanel p : aquaRows) {
				p.setVisible(isAqua);
			}
			for (InspectorRowPanel p : qbuttonRows) {
				p.setVisible(isQButton);
			}
			segmentPositionRow.setVisible(isAqua || isQButton);
			if (isAqua) {
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
			} else if (isQButton) {
				if (shimmerCheckBox.isSelected())
					button.addMouseListener(ShimmerPaintUIEffect.mouseListener);
				if (zoomCheckBox.isSelected())
					button.addActionListener(ZoomIconPaintUIEffect.actionListener);
				zoomCheckBox.setEnabled(button.getIcon() != null);

				segmentPositionComboBox.setEnabled(true);
				button.putClientProperty(QButtonUI.HORIZONTAL_POSITION,
						(String) segmentPositionComboBox.getSelectedItem());

				if (comboBoxCheckBox.isSelected()) {
					comboBox = new JComboBox<String>(new String[] { "Item 1",
							"Item 2", "Item 3" });
					comboBox.setUI(((QButtonUI) button.getUI())
							.createComboBoxUI());

					comboBox.putClientProperty(QButtonUI.SHOW_SEPARATORS,
							comboBoxSeparatorCheckBox.isSelected());
					comboBox.putClientProperty(QComboBoxUI.IS_POP_DOWN_KEY,
							comboBoxPopDownCheckBox.isSelected());
				}

				customShapeCircleButton.setEnabled(customShapeCheckBox
						.isSelected());
				customShapeDiamondButton.setEnabled(customShapeCheckBox
						.isSelected());
				if (customShapeCheckBox.isSelected()) {
					Shape shape;
					if (customShapeCircleButton.isSelected()) {
						shape = new Ellipse2D.Float(0, 0, 20, 20);
					} else {
						GeneralPath diamond = new GeneralPath();
						diamond.moveTo(0, 0);
						diamond.lineTo(10, 10);
						diamond.lineTo(0, 20);
						diamond.lineTo(-10, 10);
						diamond.closePath();
						shape = diamond;
					}
					button.putClientProperty(QButtonUI.SHAPE, shape);
				}
			}

			examplePanel.removeAll();
			examplePanel.add(button);
			if (comboBox != null)
				examplePanel.add(comboBox);
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
		return new String[] { "button", "ux", "ui", "Swing" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JButton.class, QButtonUI.class };
	}

}