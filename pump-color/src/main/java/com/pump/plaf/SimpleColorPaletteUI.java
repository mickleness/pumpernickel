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
package com.pump.plaf;

import com.pump.swing.ColorPalette;

public class SimpleColorPaletteUI extends ColorPaletteUI {
	public final ColorSet colorSet;

	public SimpleColorPaletteUI() {
		this(new DefaultColors(true));
	}

	public SimpleColorPaletteUI(ColorSet colors) {
		colorSet = colors;
	}

	@Override
	protected ColorSet getColorSet(ColorPalette cp) {
		return colorSet;
	}
}