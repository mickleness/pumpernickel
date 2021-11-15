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
package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.StyleSheet.BoxPainter;

public class QBodyBlockView extends SwingBodyBlockView
		implements LegacyCssView {

	BoxPainter boxPainter;

	public QBodyBlockView(Element elem) {
		super(elem);
	}

	@Override
	public void paint(Graphics g, Shape allocation) {
		QViewRenderer.paint((Graphics2D) g, allocation, this, this,
				getStyleSheet(), boxPainter, true);
	}

	@Override
	public void paintLegacyCss2(Graphics g, Shape allocation) {
		super.paint(g, allocation);
	}

	@Override
	protected void setPropertiesFromAttributes() {
		super.setPropertiesFromAttributes();

		// what we really want is access to our super's BoxPainter,
		// but that's private so we'll create our own
		StyleSheet sheet = super.getStyleSheet();
		boxPainter = sheet.getBoxPainter(sheet.getViewAttributes(this));
	}
}