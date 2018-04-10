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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

/**
 * This is a PanelUI with a two-color vertical gradient.
 * 
 */
public class GradientPanelUI extends PanelUI {
	Color[] colors;
	float[] fractions;

	public GradientPanelUI(Color color1, Color color2) {
		fractions = new float[] { 0, 1 };
		colors = new Color[] { color1, color2 };
	}

	int cachedHeight = -11111;
	LinearGradientPaint paint;

	@Override
	public void paint(Graphics g0, JComponent c) {
		int width = c.getWidth();
		int height = c.getHeight();
		if (height != cachedHeight) {
			paint = new LinearGradientPaint(0, 0, 0, height, fractions, colors);
			cachedHeight = height;
		}
		Graphics2D g = (Graphics2D) g0;
		g.setPaint(paint);
		g.fillRect(0, 0, width, height);
	}
}