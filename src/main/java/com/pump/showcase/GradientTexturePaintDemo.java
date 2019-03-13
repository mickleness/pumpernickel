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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolTip;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.awt.GradientTexturePaint;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.plaf.PlafPaintUtils;
import com.pump.swing.JColorWell;
import com.pump.swing.JPopover;

/**
 * This small demo app features two horizontal gradients, and each shows below
 * it a zoomed-in image highlighting where pixels change.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/GradientTexturePaintDemo.png"
 * alt="A screenshot of the GradientTexturePaintDemo.">
 */
public class GradientTexturePaintDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	class GradientPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		protected GradientPanel() {
			setPreferredSize(new Dimension(300, 400));
		}

		protected void paintComponent(Graphics g0) {
			Graphics2D g = (Graphics2D) g0;

			int numberOfColors = numberOfColorsSlider.getValue();

			BufferedImage bi = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			{
				Graphics2D g2 = bi.createGraphics();
				Point2D p1 = new Point2D.Double(0, getHeight() / 4);
				Point2D p2 = new Point2D.Double(0, getHeight() * 3 / 4);

				Color c1 = new Color(100, 100, 100);
				Color c2 = new Color(100 + numberOfColors - 1,
						100 + numberOfColors - 1, 100 + numberOfColors - 1);

				Paint p;
				CycleMethod cycle = (CycleMethod) cycleComboBox
						.getSelectedItem();
				if (typeComboBox.getSelectedItem() == GradientType.GRADIENT_TEXTURE_PAINT) {
					p = new GradientTexturePaint(new Color[] { c1, c2 },
							new float[] { 0, 1 }, p1, p2, cycle);
				} else if (typeComboBox.getSelectedItem() == GradientType.GRADIENT_PAINT) {
					p = new GradientPaint(p1, c1, p2, c2);
				} else {
					p = new LinearGradientPaint(p1, p2, new float[] { 0, 1 },
							new Color[] { c1, c2 }, cycle);
				}
				g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
						RenderingHints.VALUE_COLOR_RENDER_SPEED);
				g2.setPaint(p);
				g2.fillRect(0, 0, getWidth(), getHeight());

				int x = getWidth() / 2;
				g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
						RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				g2.fillRect(0, 0, x, getHeight());
				g2.dispose();
			}

			int[] d = new int[getWidth()];
			for (int y = 0; y < getHeight(); y++) {
				bi.getRaster().getDataElements(0, y, d.length, 1, d);
				for (int x = 0; x < d.length; x++) {
					int gray = d[x] & 0xff;
					float f = (float) (gray - 100)
							/ (float) (numberOfColors - 1f);

					// I guess the diffusion makes us stray a little outside
					// [0,1]?
					f = Math.min(1, Math.max(0, f));

					Color c = PlafPaintUtils.tween(well1
							.getColorSelectionModel().getSelectedColor(), well2
							.getColorSelectionModel().getSelectedColor(), f);
					d[x] = c.getRGB();
				}
				bi.getRaster().setDataElements(0, y, d.length, 1, d);
			}

			g.drawImage(bi, 0, 0, null);
		}
	}

	JColorWell well1 = new JColorWell(new Color(0xffffdf));
	JColorWell well2 = new JColorWell(new Color(0x4f009f));
	JPanel controls = new JPanel();

	GradientPanel panel = new GradientPanel();
	JSlider numberOfColorsSlider = new JSlider(2, 100, 10);

	enum GradientType {
		GRADIENT_TEXTURE_PAINT, GRADIENT_PAINT, LINEAR_GRADIENT_PAINT
	}

	JComboBox<GradientType> typeComboBox = new JComboBox<>();
	JComboBox<CycleMethod> cycleComboBox = new JComboBox<>();
	JLabel unsupportedLabel = new JLabel("(Unsupported!)");

	public GradientTexturePaintDemo() {
		setLayout(new GridBagLayout());
		for (GradientType t : GradientType.values()) {
			typeComboBox.addItem(t);
		}
		for (CycleMethod t : CycleMethod.values()) {
			cycleComboBox.addItem(t);
		}

		InspectorGridBagLayout layout = new InspectorGridBagLayout(controls);
		layout.addRow(new JLabel("Color 1:"), well1, false, null);
		layout.addRow(new JLabel("Color 2:"), well2, false, null);
		layout.addRow(new JLabel("Colors:"), numberOfColorsSlider, false, null);
		layout.addRow(new JLabel("Gradient Type:"), typeComboBox, false, null);
		layout.addRow(new JLabel("Cycle:"), cycleComboBox, unsupportedLabel);

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		add(controls, c);

		JPanel graphicPanel = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weighty = 1;
		c.weightx = 1;
		c.gridy++;
		add(graphicPanel, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		graphicPanel.add(panel, c);
		c.gridy++;
		c.weighty = 0;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		graphicPanel.add(new JLabel("Quality Hints"), c);
		c.gridx++;
		graphicPanel.add(new JLabel("Speed Hints"), c);

		ChangeListener repaintListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				panel.repaint();
			}
		};

		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				unsupportedLabel
						.setVisible(typeComboBox.getSelectedItem() == GradientType.GRADIENT_PAINT);
				cycleComboBox.setEnabled(!unsupportedLabel.isVisible());
				panel.repaint();
			}
		};
		cycleComboBox.addActionListener(actionListener);
		typeComboBox.addActionListener(actionListener);

		actionListener.actionPerformed(null);

		new JPopover<JToolTip>(numberOfColorsSlider, new JToolTip(), false) {

			@Override
			protected void doRefreshPopup() {
				getContents().setTipText(
						numberOfColorsSlider.getValue() + " Colors");
				// this is only because we have the JToolTipDemo so colors
				// might change:
				getContents().updateUI();
			}
		};

		numberOfColorsSlider.addChangeListener(repaintListener);
		well1.getColorSelectionModel().addChangeListener(repaintListener);
		well2.getColorSelectionModel().addChangeListener(repaintListener);
	}

	@Override
	public String getTitle() {
		return "GradientTexturePaint Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a new alternative gradient paint that uses diffusion to reduce color banding.";
	}

	@Override
	public URL getHelpURL() {
		return GradientTexturePaintDemo.class
				.getResource("gradientTexturePaintDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "gradients", "diffusion", "paint" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { GradientTexturePaintDemo.class,
				TexturePaint.class, GradientPaint.class };
	}
}