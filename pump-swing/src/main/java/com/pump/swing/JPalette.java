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
package com.pump.swing;

import java.awt.Color;
import java.util.Locale;
import java.util.Objects;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;

import com.pump.plaf.PaletteUI;

public class JPalette extends JComponent {
	private static final long serialVersionUID = 1L;

	public static class AccessibleColor extends Color {
		private static final long serialVersionUID = 1L;
		String name;

		public AccessibleColor(int rgb, String name) {
			super(rgb);
			Objects.requireNonNull(name);
			this.name = name;
		}

		public String getName(Locale locale) {
			// TODO: properly localize the name when requested
			return name;
		}

		public String toString() {
			return "AccessibleColor[red=" + getRed() + ", green=" + getGreen()
					+ ", blue=" + getBlue() + ", name="
					+ getName(Locale.getDefault()) + "]";
		}
	}

	/**
	 * Return a 9 column by 6 row color grid where the last two columns are
	 * white-to-black and a column of brown, and all remaining columns are a hue
	 * from light-to-dark.
	 * 
	 * @return
	 */
	public static Color[][] get54Colors() {
		return new Color[][] {
				{ new Color(0xffc3bf), new Color(0xffe3bf),
						new Color(0xf8ffbf), new Color(0xbfffc6),
						new Color(0xbff8ff), new Color(0xc6bfff),
						new Color(0xffbff8), new Color(0xffffff),
						new Color(0xfff2d5) },
				{ new Color(0xff7b73), new Color(0xffc173),
						new Color(0xefff73), new Color(0x73ff82),
						new Color(0x73efff), new Color(0x8273ff),
						new Color(0xff73ef), new Color(0xcccccc),
						new Color(0xd2c5aa) },
				{ new Color(0xff3226), new Color(0xff9f26),
						new Color(0xe7ff26), new Color(0x26ff3e),
						new Color(0x26e7ff), new Color(0x3e26ff),
						new Color(0xff26e7), new Color(0x999999),
						new Color(0xa49880) },
				{ new Color(0xf80e00), new Color(0xf88a00),
						new Color(0xdcf800), new Color(0x00f81c),
						new Color(0x00dcf8), new Color(0x1c00f8),
						new Color(0xf800dc), new Color(0x666666),
						new Color(0x776c55) },
				{ new Color(0xbf0b00), new Color(0xbf6a00),
						new Color(0xaabf00), new Color(0x00bf15),
						new Color(0x00aabf), new Color(0x1500bf),
						new Color(0xbf00aa), new Color(0x333333),
						new Color(0x493f2b) },
				{ new Color(0x4e0400), new Color(0x4e2b00),
						new Color(0x454e00), new Color(0x004e09),
						new Color(0x00454e), new Color(0x09004e),
						new Color(0x4e0045), new Color(0x000000),
						new Color(0x1c1200) } };
	}

	/**
	 * Return a 15 column by 10 row color grid where the last column is
	 * white-to-black and the other columns feature a hue from light-to-dark.
	 */
	public static Color[][] get150Colors() {
		return new Color[][] {
				{ new Color(0xffcfcf), new Color(0xffe4cf),
						new Color(0xfff9cf), new Color(0xf0ffcf),
						new Color(0xdcffcf), new Color(0xcfffd6),
						new Color(0xcfffeb), new Color(0xcffeff),
						new Color(0xcfeaff), new Color(0xcfd5ff),
						new Color(0xddcfff), new Color(0xf2cfff),
						new Color(0xffcff7), new Color(0xffcfe3),
						new Color(0xffffff) },
				{ new Color(0xffa2a1), new Color(0xffcaa1),
						new Color(0xfff3a1), new Color(0xe3ffa1),
						new Color(0xbaffa1), new Color(0xa1ffaf),
						new Color(0xa1ffd8), new Color(0xa1feff),
						new Color(0xa1d5ff), new Color(0xa1adff),
						new Color(0xbda1ff), new Color(0xe5a1ff),
						new Color(0xffa1f0), new Color(0xffa1c8),
						new Color(0xefefef) },
				{ new Color(0xff7573), new Color(0xffb173),
						new Color(0xffed73), new Color(0xd5ff73),
						new Color(0x99ff73), new Color(0x73ff89),
						new Color(0x73ffc5), new Color(0x73fdff),
						new Color(0x73c1ff), new Color(0x7385ff),
						new Color(0x9d73ff), new Color(0xd973ff),
						new Color(0xff73e9), new Color(0xff73ad),
						new Color(0xcfcfcf) },
				{ new Color(0xff4845), new Color(0xff9745),
						new Color(0xffe745), new Color(0xc7ff45),
						new Color(0x77ff45), new Color(0x45ff62),
						new Color(0x45ffb2), new Color(0x45fcff),
						new Color(0x45adff), new Color(0x455dff),
						new Color(0x7d45ff), new Color(0xcc45ff),
						new Color(0xff45e2), new Color(0xff4592),
						new Color(0xafafaf) },
				{ new Color(0xff1a17), new Color(0xff7e17),
						new Color(0xffe117), new Color(0xb9ff17),
						new Color(0x56ff17), new Color(0x17ff3b),
						new Color(0x17ff9f), new Color(0x17fcff),
						new Color(0x1798ff), new Color(0x1735ff),
						new Color(0x5d17ff), new Color(0xc017ff),
						new Color(0xff17db), new Color(0xff1777),
						new Color(0x8f8f8f) },
				{ new Color(0xfc0400), new Color(0xfc7000),
						new Color(0xfcdc00), new Color(0xb1fc00),
						new Color(0x45fc00), new Color(0x00fc28),
						new Color(0x00fc94), new Color(0x00f9fc),
						new Color(0x008dfc), new Color(0x0020fc),
						new Color(0x4c00fc), new Color(0xb800fc),
						new Color(0xfc00d5), new Color(0xfc0069),
						new Color(0x6f6f6f) },
				{ new Color(0xe80300), new Color(0xe86700),
						new Color(0xe8ca00), new Color(0xa2e800),
						new Color(0x3fe800), new Color(0x00e824),
						new Color(0x00e888), new Color(0x00e5e8),
						new Color(0x0081e8), new Color(0x001ee8),
						new Color(0x4600e8), new Color(0xa900e8),
						new Color(0xe800c4), new Color(0xe80060),
						new Color(0x4f4f4f) },
				{ new Color(0xbf0300), new Color(0xbf5500),
						new Color(0xbfa700), new Color(0x86bf00),
						new Color(0x34bf00), new Color(0x00bf1e),
						new Color(0x00bf70), new Color(0x00bdbf),
						new Color(0x006bbf), new Color(0x0019bf),
						new Color(0x3900bf), new Color(0x8b00bf),
						new Color(0xbf00a1), new Color(0xbf004f),
						new Color(0x2f2f2f) },
				{ new Color(0x820200), new Color(0x823a00),
						new Color(0x827100), new Color(0x5b8200),
						new Color(0x238200), new Color(0x008214),
						new Color(0x00824c), new Color(0x008082),
						new Color(0x004882), new Color(0x001182),
						new Color(0x270082), new Color(0x5f0082),
						new Color(0x82006e), new Color(0x820036),
						new Color(0x0f0f0f) },
				{ new Color(0x300100), new Color(0x301500),
						new Color(0x302a00), new Color(0x223000),
						new Color(0x0d3000), new Color(0x003008),
						new Color(0x00301c), new Color(0x003030),
						new Color(0x001b30), new Color(0x000630),
						new Color(0x0f0030), new Color(0x230030),
						new Color(0x300029), new Color(0x300014),
						new Color(0x000000) }

		};
	}

	/**
	 * The <a href="https://flatuicolors.com/palette/defo">Flat UI Palette
	 * v1</a> is a 6x4 color grid.
	 */
	public static AccessibleColor[][] getFlatUIColors() {
		return new AccessibleColor[][] {
				{ new AccessibleColor(0x1abc9c, "Turquoise"),
						new AccessibleColor(0x2ecc71, "Emerald"),
						new AccessibleColor(0x3498db, "Peter River"),
						new AccessibleColor(0x9b59b6, "Amethyst"),
						new AccessibleColor(0x34495e, "Wet Asphalt") },
				{ new AccessibleColor(0x16a085, "Green Sea"),
						new AccessibleColor(0x27ae60, "Nephritis"),
						new AccessibleColor(0x2980b9, "Belize Hole"),
						new AccessibleColor(0x8e44ad, "Wisteria"),
						new AccessibleColor(0x2c3e50, "Midnight Blue") },
				{ new AccessibleColor(0xf1c40f, "Sunflower"),
						new AccessibleColor(0xe67e22, "Carrot"),
						new AccessibleColor(0xe74c3c, "Alizarin"),
						new AccessibleColor(0xecf0f1, "Clouds"),
						new AccessibleColor(0x95a5a6, "Concrete") },
				{ new AccessibleColor(0xf39c12, "Orange"),
						new AccessibleColor(0xd35400, "Pumpkin"),
						new AccessibleColor(0xc0392b, "Pomegranate"),
						new AccessibleColor(0xbdc3c7, "Silver"),
						new AccessibleColor(0x7f8c8d, "Asbestos") } };
	}

	/**
	 * The <a href="https://www.materialui.co/metrocolors">Metro Colors</a> are
	 * a 5x4 grid based on "flat design and modern colors". Metro is the
	 * nickname of MDL (Microsoft Design Language).
	 */
	public static AccessibleColor[][] getMetroColors() {
		return new AccessibleColor[][] {
				{ new AccessibleColor(0xa4c400, "Lime"),
						new AccessibleColor(0x60a917, "Green"),
						new AccessibleColor(0x008a00, "Emerald"),
						new AccessibleColor(0x00aba9, "Teal"),
						new AccessibleColor(0x1ba1e2, "Cyan") },
				{ new AccessibleColor(0x0050ef, "Cobalt"),
						new AccessibleColor(0x6a00ff, "Indigo"),
						new AccessibleColor(0xaa00ff, "Violet"),
						new AccessibleColor(0xf472d0, "Pink"),
						new AccessibleColor(0xd80073, "Magenta") },
				{ new AccessibleColor(0xa20025, "Crimson"),
						new AccessibleColor(0xe51400, "Red"),
						new AccessibleColor(0xfa6800, "Orange"),
						new AccessibleColor(0xf0a30a, "Amber"),
						new AccessibleColor(0xe3c800, "Yellow") },
				{ new AccessibleColor(0x825a2c, "Brown"),
						new AccessibleColor(0x6d8764, "Olive"),
						new AccessibleColor(0x647687, "Steel"),
						new AccessibleColor(0x76608a, "Mauve"),
						new AccessibleColor(0xa0522d, "Sienna") } };
	}

	/**
	 * The <a href="https://fluentcolors.com/">Fluent Design Colors</a> are an
	 * 8x6 color grid described as:
	 * <p>
	 * <blockquote>Fluent Design System (codenamed Project Neon), officially
	 * unveiled as Microsoft Fluent Design System, is a design language
	 * developed in 2017 by Microsoft. Fluent Design is a revamp of Microsoft
	 * Design Language 2 that will include guidelines for the designs and
	 * interactions used within software designed for all Windows 10 devices and
	 * platforms.
	 * 
	 * The system is based on five key components: Light, Depth, Motion,
	 * Material, and Scale. The new design language will include more prominent
	 * use of motion, depth, and translucency effects. The transition to Fluent
	 * is a long-term project with no specific target for completion, but
	 * elements of the new design language have been incorporated into selected
	 * apps since the Creators Update. It will have wider usage in the Fall
	 * Creators Update, but Microsoft has stated that the design system will not
	 * be finished within the timeframe of Fall Creators Update.
	 * 
	 * Microsoft announced Fluent Design on 11 May 2017, at the Build conference
	 * for developers.</blockquote>
	 */
	public static Color[][] getFluentColors() {
		return new Color[][] {
				{ new Color(0xFFB900), new Color(0xE74856),
						new Color(0x0078D7), new Color(0x0099BC),
						new Color(0x7A7574), new Color(0x767676) },
				{ new Color(0xFF8C00), new Color(0xE81123),
						new Color(0x0063B1), new Color(0x2D7D9A),
						new Color(0x5D5A58), new Color(0x4C4A48) },
				{ new Color(0xF7630C), new Color(0xEA005E),
						new Color(0x8E8CD8), new Color(0x00B7C3),
						new Color(0x68768A), new Color(0x69797E) },
				{ new Color(0xCA5010), new Color(0xC30052),
						new Color(0x6B69D6), new Color(0x038387),
						new Color(0x515C6B), new Color(0x4A5459) },
				{ new Color(0xDA3B01), new Color(0xE3008C),
						new Color(0x8764B8), new Color(0x00B294),
						new Color(0x567C73), new Color(0x647C64) },
				{ new Color(0xEF6950), new Color(0xBF0077),
						new Color(0x744DA9), new Color(0x018574),
						new Color(0x486860), new Color(0x525E54) },
				{ new Color(0xD13438), new Color(0xC239B3),
						new Color(0xB146C2), new Color(0x00CC6A),
						new Color(0x498205), new Color(0x847545) },
				{ new Color(0xFF4343), new Color(0x9A0089),
						new Color(0x881798), new Color(0x10893E),
						new Color(0x107C10), new Color(0x7E735F) }, };
	}

	private static final String uiClassID = "PaletteUI";

	public static final String PROPERTY_CELL_WIDTH = JPalette.class.getName()
			+ "#cellWidth";
	public static final String PROPERTY_CELL_HEIGHT = JPalette.class.getName()
			+ "#cellHeight";
	/**
	 * This client property resolves to a matrix of Colors.
	 */
	public static final String PROPERTY_COLORS = JPalette.class.getName()
			+ "#colors";

	public static final String PROPERTY_SELECTION_MODEL = JPalette.class
			.getName() + "#selectionModel";

	public JPalette() {
		this(getFlatUIColors());
	}

	public JPalette(Color[][] colors) {
		this(colors, 10);
	}

	public JPalette(Color[][] colors, int cellSize) {
		setColorSelectionModel(new DefaultColorSelectionModel());
		setColors(colors);
		setCellSize(cellSize);
		updateUI();
	}

	public void setColors(Color[][] colors) {
		Objects.requireNonNull(colors);
		putClientProperty(PROPERTY_COLORS, clone(colors));
	}

	public Color[][] getColors() {
		Color[][] colors = (Color[][]) getClientProperty(PROPERTY_COLORS);
		if (colors == null)
			return new Color[][] {};
		return clone(colors);
	}

	private Color[][] clone(Color[][] c) {
		Color[][] r = new Color[c.length][];
		for (int a = 0; a < c.length; a++) {
			r[a] = new Color[c[a].length];
			System.arraycopy(c[a], 0, r[a], 0, c[a].length);
		}
		return r;
	}

	public void setColorSelectionModel(ColorSelectionModel model) {
		Objects.requireNonNull(model);
		putClientProperty(PROPERTY_SELECTION_MODEL, model);
	}

	public ColorSelectionModel getColorSelectionModel() {
		return (ColorSelectionModel) getClientProperty(PROPERTY_SELECTION_MODEL);
	}

	public void updateUI() {
		if (UIManager.getDefaults().get(uiClassID) == null) {
			UIManager.getDefaults().put(uiClassID, "com.pump.plaf.PaletteUI");
		}
		setUI((PaletteUI) UIManager.getUI(this));
	}

	public PaletteUI getUI() {
		return (PaletteUI) ui;
	}

	public void setUI(PaletteUI ui) {
		super.setUI(ui);
	}

	/**
	 * Returns a string that specifies the name of the L&amp;F class that
	 * renders this component.
	 *
	 * @return "PanelUI"
	 * @see JComponent#getUIClassID
	 * @see UIDefaults#getUI
	 * @beaninfo expert: true description: A string that specifies the name of
	 *           the L&amp;F class.
	 */
	public String getUIClassID() {
		return uiClassID;
	}

	/**
	 * Set the cell width and height to the same value.
	 * 
	 * @param cellSize
	 *            the new cell width and height.
	 */
	public void setCellSize(int cellSize) {
		setCellWidth(cellSize);
		setCellHeight(cellSize);
	}

	public void setCellWidth(int cellWidth) {
		if (cellWidth < 1)
			throw new IllegalArgumentException("cellWidth (" + cellWidth
					+ ") must be positive");
		putClientProperty(PROPERTY_CELL_WIDTH, cellWidth);
	}

	public void setCellHeight(int cellHeight) {
		if (cellHeight < 1)
			throw new IllegalArgumentException("cellHeight (" + cellHeight
					+ ") must be positive");
		putClientProperty(PROPERTY_CELL_HEIGHT, cellHeight);
	}

	public int getCellWidth() {
		return (Integer) getClientProperty(PROPERTY_CELL_WIDTH);
	}

	public int getCellHeight() {
		return (Integer) getClientProperty(PROPERTY_CELL_HEIGHT);
	}

	@Override
	public AccessibleContext getAccessibleContext() {
		if (accessibleContext == null) {
			accessibleContext = new AccessibleJPalette();
		}
		return accessibleContext;
	}

	protected class AccessibleJPalette extends AccessibleJComponent {
		private static final long serialVersionUID = 1L;

		public AccessibleRole getAccessibleRole() {
			return AccessibleRole.COLOR_CHOOSER;
		}
	}

}