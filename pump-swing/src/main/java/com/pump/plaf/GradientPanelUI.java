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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

/**
 * This is a PanelUI with a two-color vertical gradient.
 * 
 */
public class GradientPanelUI extends PanelUI {
	Color[] colors;
	float[] fractions;

	GradientPaint gradientPaint;

	public GradientPanelUI(Color color1, Color color2) {
		Objects.requireNonNull(color1);
		Objects.requireNonNull(color2);
		fractions = new float[] { 0, 1 };
		colors = new Color[] { color1, color2 };
	}

	public GradientPanelUI(GradientPaint paint) {
		Objects.requireNonNull(paint);
		gradientPaint = paint;
	}

	int cachedHeight = -11111;
	LinearGradientPaint cachedPaint;

	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D) g0;
		int width = c.getWidth();
		int height = c.getHeight();
		if (gradientPaint != null) {
			g.setPaint(gradientPaint);
		} else {
			if (height != cachedHeight) {
				cachedPaint = new LinearGradientPaint(0, 0, 0, height,
						fractions, colors);
				cachedHeight = height;
			}
			g.setPaint(cachedPaint);
		}
		g.fillRect(0, 0, width, height);
	}
}