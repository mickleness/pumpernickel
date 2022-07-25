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
package com.pump.showcase.chart;

import com.pump.graphics.vector.VectorGraphics2D;
import com.pump.graphics.vector.VectorImage;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicPanelUI;

/**
 * A panel that displays a BarChartRenderer
 */
public class BarChartPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	static class BarChartPanelUI extends BasicPanelUI {
		@Override
		public void paint(Graphics g, JComponent c) {
			super.paint(g, c);

			BarChartPanel p = (BarChartPanel) c;
			p.renderer.paint((Graphics2D) g, p.getSize());
		}
	}

	BarChartRenderer renderer;

	public BarChartPanel(BarChartRenderer renderer, int width) {
		this.renderer = Objects.requireNonNull(renderer);
		setUI(new BarChartPanelUI());

		// identify the height we need to render:
		VectorImage img = new VectorImage();
		VectorGraphics2D g = img.createGraphics();
		renderer.paint(g, new Dimension(1000, 1000));
		g.dispose();
		Rectangle2D r = img.getBounds();
		int height = (int)(r.getHeight() + .5);
		setPreferredSize(new Dimension(width, height));

		setOpaque(false);
	}
}