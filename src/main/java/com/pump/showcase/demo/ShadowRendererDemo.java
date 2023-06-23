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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.geom.Spiral2D;
import com.pump.geom.StarPolygon;
import com.pump.image.shadow.ARGBPixels;
import com.pump.image.shadow.BoxShadowRenderer;
import com.pump.image.shadow.DoubleBoxShadowRenderer;
import com.pump.image.shadow.GaussianKernel;
import com.pump.image.shadow.GaussianShadowRenderer;
import com.pump.image.shadow.ShadowAttributes;
import com.pump.image.shadow.ShadowRenderer;
import com.pump.inspector.Inspector;
import com.pump.swing.JColorWell;
import com.pump.swing.popover.JPopover;

public class ShadowRendererDemo extends ShowcaseExampleDemo {

	/**
	 * For testing and comparison purposes: this is the original unoptimized
	 * Gaussian shadow renderer.
	 */
	public static class OriginalGaussianShadowRenderer
			implements ShadowRenderer {

		@Override
		public ARGBPixels createShadow(ARGBPixels src, ARGBPixels dst,
				float kernelRadius, Color shadowColor) {
			GaussianKernel kernel = getKernel(kernelRadius);
			int k = kernel.getKernelRadius();

			int shadowSize = k * 2;

			int srcWidth = src.getWidth();
			int srcHeight = src.getHeight();

			int dstWidth = srcWidth + shadowSize;
			int dstHeight = srcHeight + shadowSize;

			if (dst == null)
				dst = new ARGBPixels(dstWidth, dstHeight);

			if (dst.getWidth() != dstWidth)
				throw new IllegalArgumentException(
						dst.getWidth() + " != " + dstWidth);
			if (dst.getHeight() != dstHeight)
				throw new IllegalArgumentException(
						dst.getWidth() + " != " + dstWidth);

			int[] dstBuffer = dst.getPixels();
			int[] srcBuffer = src.getPixels();

			int[] opacityLookup = new int[256];

			{
				int rgb = shadowColor.getRGB() & 0xffffff;
				int alpha = shadowColor.getAlpha();
				for (int a = 0; a < opacityLookup.length; a++) {
					int newAlpha = (int) (a * alpha / 255);
					opacityLookup[a] = (newAlpha << 24) + rgb;
				}
			}

			int x1 = k;
			int x2 = k + srcWidth;

			int[] kernelArray = kernel.getArray();
			int kernelSum = kernel.getArraySum();

			// vertical pass:
			for (int dstX = x1; dstX < x2; dstX++) {
				int srcX = dstX - k;
				for (int dstY = 0; dstY < dstHeight; dstY++) {
					int srcY = dstY - k;
					int g = srcY - k;
					int w = 0;
					for (int j = 0; j < kernelArray.length; j++) {
						int kernelY = g + j;
						if (kernelY >= 0 && kernelY < srcHeight) {
							int argb = srcBuffer[srcX + kernelY * srcWidth];
							int alpha = argb >>> 24;
							w += alpha * kernelArray[j];
						}
					}
					w = w / kernelSum;
					dstBuffer[dstY * dstWidth + dstX] = w;
				}
			}

			// horizontal pass:
			int[] row = new int[dstWidth];
			for (int dstY = 0; dstY < dstHeight; dstY++) {
				System.arraycopy(dstBuffer, dstY * dstWidth, row, 0,
						row.length);
				for (int dstX = 0; dstX < dstWidth; dstX++) {
					int w = 0;
					for (int j = 0; j < kernelArray.length; j++) {
						int kernelX = dstX - k + j;
						if (kernelX >= 0 && kernelX < dstWidth) {
							w += row[kernelX] * kernelArray[j];
						}
					}
					w = w / kernelSum;
					dstBuffer[dstY * dstWidth + dstX] = opacityLookup[w];
				}
			}

			return dst;

		}

		@Override
		public GaussianKernel getKernel(float kernelRadius) {
			return new GaussianKernel(kernelRadius, false);
		}
	}

	public static BufferedImage createTestImage() {
		BufferedImage bi = new BufferedImage(300, 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.white);
		g.setStroke(new BasicStroke(4));
		g.draw(new Ellipse2D.Float(-10, -10, 20, 20));
		g.draw(new Ellipse2D.Float(bi.getWidth() - 10, bi.getHeight() - 10, 20,
				20));
		g.draw(new Ellipse2D.Float(bi.getWidth() - 10, -10, 20, 20));
		g.draw(new Ellipse2D.Float(-10, bi.getHeight() - 10, 20, 20));

		StarPolygon star = new StarPolygon(40);
		star.setCenter(50, 50);
		g.setColor(new Color(0x1BE7FF));
		g.fill(star);

		BufferedImage textureBI = new BufferedImage(20, 60,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = textureBI.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		for (int z = 0; z < 500; z++) {
			g2.setStroke(new BasicStroke(8));
			g2.setColor(new Color(0xFF5714));
			g2.drawLine(-100 + z * 20, 100, 100 + z * 20, -100);
			g2.setStroke(new BasicStroke(10));
			g2.setColor(new Color(0x6EEB83));
			g2.drawLine(200 - z * 20, 100, 0 - z * 20, -100);
		}
		g2.dispose();
		Rectangle r = new Rectangle(0, 0, textureBI.getWidth(),
				textureBI.getHeight());
		g.setPaint(new TexturePaint(textureBI, r));
		Shape roundRect = new RoundRectangle2D.Float(110, 10, 80, 80, 40, 40);
		g.fill(roundRect);

		Spiral2D spiral = new Spiral2D(250, 50, 20, 2, 0, 0, true);
		g.setStroke(new BasicStroke(10));
		g.setColor(new Color(0xE8AA14));
		g.draw(spiral);
		return bi;
	}

	private static final long serialVersionUID = 1L;

	/**
	 * The maximum dx & dy offset for the shadow
	 */
	private final static int MAX_OFFSET = 25;

	JComboBox<String> rendererComboBox = new JComboBox<>();
	JSpinner kernelSizeSpinner = new JSpinner(
			new SpinnerNumberModel(5f, 0f, 25f, .5f));
	JSlider opacitySlider = new ShowcaseSlider(1, 100, 50);
	JSlider xOffsetSlider = new ShowcaseSlider(-MAX_OFFSET, MAX_OFFSET, 10);
	JSlider yOffsetSlider = new ShowcaseSlider(-MAX_OFFSET, MAX_OFFSET, 10);
	JColorWell colorWell = new JColorWell(Color.black);

	BufferedImage srcImage;

	ActionListener refreshActionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			refreshExample();
		}

	};

	ChangeListener refreshChangeListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			refreshExample();
		}

	};

	public ShadowRendererDemo() {
		super();
		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("Renderer:"), rendererComboBox);
		inspector.addRow(new JLabel("Kernel Size:"), kernelSizeSpinner);
		inspector.addRow(new JLabel("X:"), xOffsetSlider);
		inspector.addRow(new JLabel("Y:"), yOffsetSlider);
		inspector.addRow(new JLabel("Color:"), colorWell);
		inspector.addRow(new JLabel("Opacity:"), opacitySlider);

		rendererComboBox.addItem("Box");
		rendererComboBox.addItem("Double Box");
		rendererComboBox.addItem("Gaussian");

		// use Double Box as default:
		rendererComboBox.setSelectedIndex(1);

		JPopover.add(opacitySlider, "%");
		JPopover.add(xOffsetSlider, " pixels");
		JPopover.add(yOffsetSlider, " pixels");

		rendererComboBox.addActionListener(refreshActionListener);
		kernelSizeSpinner.addChangeListener(refreshChangeListener);
		opacitySlider.addChangeListener(refreshChangeListener);
		xOffsetSlider.addChangeListener(refreshChangeListener);
		yOffsetSlider.addChangeListener(refreshChangeListener);
		colorWell.getColorSelectionModel()
				.addChangeListener(refreshChangeListener);

		((DefaultEditor) kernelSizeSpinner.getEditor()).getTextField()
				.setColumns(4);

		refreshExample();
	}

	protected void refreshExample() {
		examplePanel.removeAll();
		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(3, 3, 3, 3);
		examplePanel.add(new JLabel(new ImageIcon(createShadowedImage())), c);
		c.gridy++;
		c.anchor = GridBagConstraints.WEST;
		examplePanel.revalidate();
		examplePanel.repaint();
	}

	private BufferedImage createShadowedImage() {
		if (srcImage == null)
			srcImage = createTestImage();

		Dimension size = new Dimension(srcImage.getWidth(),
				srcImage.getHeight());
		SpinnerNumberModel model = (SpinnerNumberModel) kernelSizeSpinner
				.getModel();
		Number max = (Number) model.getMaximum();
		size.width += 2 * max.intValue() + 2 * MAX_OFFSET;
		size.height += 2 * max.intValue() + 2 * MAX_OFFSET;

		BufferedImage returnValue = new BufferedImage(size.width, size.height,
				BufferedImage.TYPE_INT_ARGB);
		ShadowRenderer renderer;
		if (rendererComboBox.getSelectedIndex() == 0) {
			renderer = new BoxShadowRenderer();
		} else if (rendererComboBox.getSelectedIndex() == 1) {
			renderer = new DoubleBoxShadowRenderer();
		} else {
			renderer = new GaussianShadowRenderer();
		}

		float opacity = (float) (opacitySlider.getValue()) / 100f;
		Color color = colorWell.getColorSelectionModel().getSelectedColor();
		color = new Color(color.getRed(), color.getGreen(), color.getBlue(),
				(int) (255 * opacity));

		Number k1 = (Number) kernelSizeSpinner.getValue();
		int dx = xOffsetSlider.getValue();
		int dy = yOffsetSlider.getValue();
		ShadowAttributes attr = new ShadowAttributes(dx, dy, k1.floatValue(),
				color);
		Graphics2D g = returnValue.createGraphics();
		renderer.paint(g, srcImage,
				returnValue.getWidth() / 2 - srcImage.getWidth() / 2,
				returnValue.getHeight() / 2 - srcImage.getHeight() / 2, attr);
		g.dispose();

		return returnValue;
	}

	@Override
	public String getTitle() {
		return "Shadow Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a couple of options for rendering shadows.";
	}

	@Override
	public URL getHelpURL() {
		return ShadowRendererDemo.class.getResource("shadowRendererDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "shadow", "gaussian", "blur", "image" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { ShadowRenderer.class, DoubleBoxShadowRenderer.class,
				BoxShadowRenderer.class, GaussianShadowRenderer.class };
	}

}