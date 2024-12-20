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
package com.pump.swing;

import java.awt.Color;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;

import com.pump.blog.ResourceSample;
import com.pump.plaf.ColorWellUI;

/**
 * This is a rectangular panel used to render one color.
 * <P>
 * When the user interacts with this component (either by the mouse or keyboard)
 * he/she should usually be able to choose another color.
 * 
 * 
 * <!-- ======== START OF AUTOGENERATED SAMPLES ======== -->
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/samples/ColorWell/sample.png"
 * alt="new&#160;com.pump.swing.ColorWell(&#160;java.awt.Color.blue&#160;)">
 * <!-- ======== END OF AUTOGENERATED SAMPLES ======== -->
 * 
 * @see <a
 *      href="https://javagraphics.blogspot.com/2010/01/colors-good-gui-for-selecting-colors.html">Colors:
 *      a Good GUI for Selecting Colors</a>
 */
@ResourceSample(sample = { "new com.pump.swing.ColorWell( java.awt.Color.blue )" })
public class JColorWell extends JComponent {
	private static final long serialVersionUID = 1L;
	private static final String uiClassID = "ColorWellUI";

	public static final String KEY_COLOR_SELECTION_MODEL = JColorWell.class
			.getName() + "#colorSelectionModel";

	public JColorWell() {
		setColorSelectionModel(new DefaultColorSelectionModel());
		updateUI();
		setRequestFocusEnabled(true);
		setFocusable(true);
	}

	public JColorWell(Color initialColor) {
		this();
		getColorSelectionModel().setSelectedColor(initialColor);
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	@Override
	public void updateUI() {
		if (UIManager.getDefaults().get(uiClassID) == null) {
			UIManager.getDefaults().put(uiClassID,
					"com.pump.plaf.AquaColorWellUI");
		}
		setUI((ColorWellUI) UIManager.getUI(this));
	}

	public void setUI(ColorWellUI ui) {
		super.setUI(ui);
	}

	public ColorWellUI getUI() {
		return (ColorWellUI) ui;
	}

	public void setColorSelectionModel(ColorSelectionModel colorSelectionModel) {
		Objects.requireNonNull(colorSelectionModel);
		putClientProperty(KEY_COLOR_SELECTION_MODEL, colorSelectionModel);
	}

	public ColorSelectionModel getColorSelectionModel() {
		return (ColorSelectionModel) getClientProperty(KEY_COLOR_SELECTION_MODEL);
	}
}