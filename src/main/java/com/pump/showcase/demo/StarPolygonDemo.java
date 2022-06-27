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
package com.pump.showcase.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.geom.StarPolygon;
import com.pump.inspector.Inspector;
import com.pump.swing.popover.JPopover;

public class StarPolygonDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;
	int MAX = 100;

	JSlider pointsSlider = new ShowcaseSlider(3, 20, 5);
	JSlider outerRadiusSlider = new ShowcaseSlider(1, MAX, 50);
	JSlider innerRadiusSlider = new ShowcaseSlider(1, MAX, 19);

	JComponent preview = new JComponent() {
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			StarPolygon p = new StarPolygon(pointsSlider.getValue(),
					outerRadiusSlider.getValue(), innerRadiusSlider.getValue());
			Rectangle2D r = p.getBounds2D();
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setColor(Color.darkGray);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.translate(getWidth() / 2 - r.getCenterX(),
					getHeight() / 2 - r.getCenterY());
			g2.fill(p);
			g2.dispose();
		}
	};

	ChangeListener repaintListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			preview.repaint();
		}

	};

	public StarPolygonDemo() {
		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("Points:"), pointsSlider);
		inspector.addRow(new JLabel("Outer Radius:"), outerRadiusSlider);
		inspector.addRow(new JLabel("Inner Radius:"), innerRadiusSlider);
		JPopover.add(pointsSlider, " points");
		JPopover.add(outerRadiusSlider, " pixels");
		JPopover.add(innerRadiusSlider, " pixels");

		pointsSlider.addChangeListener(repaintListener);
		outerRadiusSlider.addChangeListener(repaintListener);
		innerRadiusSlider.addChangeListener(repaintListener);

		preview.setPreferredSize(new Dimension(MAX * 2, MAX * 2));
		examplePanel.add(preview);
	}

	@Override
	public String getTitle() {
		return "StarPolygon Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates the StarPolygon.";
	}

	@Override
	public URL getHelpURL() {
		return StarPolygonDemo.class.getResource("starPolygonDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "shape", "polygon", "star" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { StarPolygon.class };
	}

}