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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.image.ImageLoader;
import com.pump.inspector.Inspector;
import com.pump.plaf.PlafPaintUtils;
import com.pump.swing.popover.JPopover;

/**
 * This is a simple demo exploring the different types of
 * <code>AlphaComposites</code>.
 * 
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/AlphaCompositeDemo.png"
 * alt="A screenshot of the AlphaCompositeDemo.">
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2009/10/alphacomposites-which-does-what.html">AlphaComposites:
 *      Which Does What?</a>
 */
public class AlphaCompositeDemo extends ShowcaseExampleDemo {

	private static final long serialVersionUID = 1L;

	BufferedImage backgroundImage = ImageLoader.createImage(
			AlphaCompositeDemo.class.getResource("balloon-background.png"));
	BufferedImage foregroundImage = ImageLoader
			.createImage(AlphaCompositeDemo.class.getResource("balloon.png"));

	JComboBox composites = new JComboBox();
	JSlider alpha = new ShowcaseSlider(0, 100, 100);
	JSlider srcAlpha = new ShowcaseSlider(0, 100, 100);
	JSlider dstAlpha = new ShowcaseSlider(0, 100, 100);
	Map<String, Field> fieldsTable = new HashMap<String, Field>();
	JRadioButton useShapes = new JRadioButton("Use Shapes", true);
	JRadioButton useImages = new JRadioButton("Use Images");

	CompositePreview preview = new CompositePreview();

	public AlphaCompositeDemo() {
		JPopover.add(alpha, "%");
		JPopover.add(dstAlpha, "%");
		JPopover.add(srcAlpha, "%");

		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("Composite:"), composites, false);
		inspector.addRow(new JLabel("Alpha:"), alpha, false);
		inspector.addRow(null, useShapes, false);
		inspector.addRow(null, useImages, false);
		inspector.addSeparator();
		inspector.addRow(new JLabel("Dest Alpha:"), dstAlpha, false);
		inspector.addRow(new JLabel("Source Alpha:"), srcAlpha, false);

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 0;
		c.gridx = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(18, 18, 18, 18);
		preview.setPreferredSize(new Dimension(200, 100));
		examplePanel.add(preview, c);

		Field[] fields = AlphaComposite.class.getFields();
		for (int a = 0; a < fields.length; a++) {
			boolean isStatic = ((fields[a].getModifiers()
					& Modifier.STATIC) > 0);
			if (fields[a].getType().equals(Integer.TYPE) && isStatic) {
				try {
					composites.addItem(fields[a].getName());
					fieldsTable.put(fields[a].getName(), fields[a]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		composites.setSelectedItem("SRC_OVER");
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				preview.repaint();
			}
		};
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				preview.repaint();
			}
		};
		composites.addActionListener(actionListener);
		useShapes.addActionListener(actionListener);
		useImages.addActionListener(actionListener);
		alpha.addChangeListener(changeListener);
		srcAlpha.addChangeListener(changeListener);
		dstAlpha.addChangeListener(changeListener);

		ButtonGroup group = new ButtonGroup();
		group.add(useImages);
		group.add(useShapes);

		preview.setBorder(new LineBorder(Color.gray));

		useShapes.setToolTipText("Draw one shape on top of another.");
		useImages.setToolTipText("Draw one image on top of another.");
	}

	class CompositePreview extends JPanel {
		private static final long serialVersionUID = 1L;

		TexturePaint checkerboard = PlafPaintUtils.getCheckerBoard(16,
				Color.white, Color.lightGray);

		public Composite getComposite() {
			try {
				String selectedItem = (String) composites.getSelectedItem();
				Field f = fieldsTable.get(selectedItem);
				int rule = f.getInt(null);
				float a = ((float) alpha.getValue()) / 100;
				return AlphaComposite.getInstance(rule, a);
			} catch (Throwable t) {
				t.printStackTrace();
				return AlphaComposite.SrcOver;
			}
		}

		@Override
		protected void paintComponent(Graphics g0) {
			super.paintComponent(g0);

			Graphics2D g = (Graphics2D) g0.create();
			g.setPaint(checkerboard);
			g.fillRect(0, 0, getWidth(), getHeight());

			int destAlphaValue = (int) (dstAlpha.getValue() * 255f / 100f);
			int srcAlphaValue = (int) (srcAlpha.getValue() * 255f / 100f);

			BufferedImage img;
			if (useImages.isSelected()) {
				img = createImageSample(destAlphaValue, srcAlphaValue);
			} else {
				img = createShapeSample(destAlphaValue, srcAlphaValue);
			}
			g.drawImage(img, getWidth() / 2 - img.getWidth() / 2,
					getHeight() / 2 - img.getHeight() / 2, null);

			g.dispose();
		}

		private void drawString(Graphics2D g, String s, float centerX,
				float centerY) {
			Rectangle2D r = g.getFontMetrics().getStringBounds(s, g);
			g.setColor(Color.white);
			g.drawString(s, (float) (centerX - r.getWidth() / 2), centerY + 1f);
			g.setColor(Color.black);
			g.drawString(s, (float) (centerX - r.getWidth() / 2), centerY);
		}

		private BufferedImage createImageSample(int destAlphaValue,
				int srcAlphaValue) {
			BufferedImage bi = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setComposite(AlphaComposite.SrcOver);

			BufferedImage myBackground = applyAlpha(backgroundImage,
					destAlphaValue);
			BufferedImage myForeground = applyAlpha(foregroundImage,
					srcAlphaValue);

			g.drawImage(myBackground,
					getWidth() * 2 / 3 - myForeground.getWidth() / 4,
					getHeight() / 2 - myBackground.getHeight() / 4,
					myBackground.getWidth() / 2, myBackground.getHeight() / 2,
					null);
			g.setComposite(getComposite());
			g.drawImage(myForeground,
					getWidth() * 1 / 3 - myForeground.getWidth() / 4,
					getHeight() / 2 - myForeground.getHeight() / 4,
					myForeground.getWidth() / 2, myForeground.getHeight() / 2,
					null);

			return bi;
		}

		private BufferedImage applyAlpha(BufferedImage img, int alpha) {
			BufferedImage bi = new BufferedImage(img.getWidth(),
					img.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0, 0, getWidth(), getHeight());
			float f = alpha;
			f /= 255f;
			g.setComposite(
					AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f));
			g.drawImage(img, 0, 0, null);
			g.dispose();
			return bi;
		}

		private BufferedImage createShapeSample(int destAlphaValue,
				int srcAlphaValue) {
			BufferedImage bi = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setComposite(AlphaComposite.SrcOver);

			Ellipse2D destShape = new Ellipse2D.Float(25, 0, 100, 100);
			Ellipse2D srcShape = new Ellipse2D.Float(75, 0, 100, 100);

			Paint destPaint = new GradientPaint(0, 0,
					new Color(255, 100, 100, 0), 0, 200,
					new Color(255, 100, 100, destAlphaValue));
			g.setPaint(destPaint);
			g.fill(destShape);

			Paint srcPaint = new GradientPaint(0, 200,
					new Color(100, 100, 255, 0), 0, 0,
					new Color(100, 100, 255, srcAlphaValue));
			g.setComposite(getComposite());
			g.setPaint(srcPaint);
			g.fill(srcShape);

			g.setComposite(AlphaComposite.SrcOver);
			g.setColor(Color.black);
			drawString(g, "DST", 85 - 30, 50);
			drawString(g, "SRC", 115 + 30, 50);

			return bi;
		}
	}

	@Override
	public String getTitle() {
		return "AlphaComposite Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates how each AlphaComposite renders.";
	}

	@Override
	public URL getHelpURL() {
		return AlphaCompositeDemo.class.getResource("alphaCompositeDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "AlphaComposite", "Graphics2D", "Graphics",
				"painting" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { AlphaComposite.class };
	}
}