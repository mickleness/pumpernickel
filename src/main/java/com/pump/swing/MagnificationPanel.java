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
package com.pump.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.TexturePaint;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import com.pump.plaf.PlafPaintUtils;

/**
 * A panel that offers a magnified view of another <code>Component</code> based
 * on the mouse location.
 */
public class MagnificationPanel extends JComponent {
	private static final long serialVersionUID = 1L;

	private static final String PIXELATED_KEY = MagnificationPanel.class
			.getName() + ".pixelated";

	MouseInputAdapter mouseListener = new MouseInputAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			refresh();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			refresh();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			refresh();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			refresh();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			refresh();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			refresh();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			refresh();
		}
	};

	int pixelSize;
	Component zoomedComponent;
	BufferedImage scratchImage;
	JTextArea textArea = new JTextArea();

	TexturePaint checkers;

	/**
	 * 
	 * @param zoomedComponent
	 *            the component to zoom in on.
	 * @param visiblePixelWidth
	 *            the number of pixels to show horizontally (this is used to
	 *            compute the preferred size)
	 * @param visiblePixelHeight
	 *            the number of pixels to show vertically (this is used to
	 *            compute the preferred size)
	 * @param pixelSize
	 *            the magnification of each pixel (4, 8, etc.)
	 */
	public MagnificationPanel(Component zoomedComponent, int visiblePixelWidth,
			int visiblePixelHeight, int pixelSize) {
		this.pixelSize = pixelSize;
		this.zoomedComponent = zoomedComponent;
		addMouseListeners(zoomedComponent);
		addMouseListeners(this);
		setPreferredSize(new Dimension(visiblePixelWidth * pixelSize,
				visiblePixelHeight * pixelSize));

		addPropertyChangeListener(PIXELATED_KEY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				repaint();
			}
		});

		textArea.setEditable(false);
		textArea.setFocusable(false);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setFont(new Font("Default", 0, 16));

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int componentWidth = getWidth();
				int componentHeight = getHeight();
				int imageWidth = (int) Math.ceil(((double) componentWidth)
						/ ((double) MagnificationPanel.this.pixelSize));
				int imageHeight = (int) Math.ceil(((double) componentHeight)
						/ ((double) MagnificationPanel.this.pixelSize));
				int oldImageWidth = scratchImage == null ? 0 : scratchImage
						.getWidth();
				int oldImageHeight = scratchImage == null ? 0 : scratchImage
						.getHeight();
				if (imageWidth > oldImageWidth || imageHeight > oldImageHeight) {
					scratchImage = new BufferedImage(imageWidth, imageHeight,
							BufferedImage.TYPE_INT_ARGB);
					refresh();
				}
			}
		});

		textArea.setBorder(new EmptyBorder(20, 20, 20, 20));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		add(textArea, c);

		checkers = PlafPaintUtils.getCheckerBoard(Math.max(4, pixelSize / 2),
				Color.white, new Color(238, 238, 238));
	}

	private ContainerListener containerListener = new ContainerListener() {

		public void componentAdded(ContainerEvent e) {
			addMouseListeners(e.getComponent());
		}

		public void componentRemoved(ContainerEvent e) {
			removeMouseListeners(e.getComponent());
		}
	};

	/**
	 * Add MouseListeners to a component and its children. If children have a
	 * MouseListener for mouse-moved events, then this component won't otherwise
	 * receive mouse-moved events (and therefore it won't repaint correctly).
	 * <p>
	 * A common instance of this problem is simply the presence of tooltips
	 * (which require a MouseListener).
	 */
	private void addMouseListeners(Component c) {
		c.addMouseMotionListener(mouseListener);
		c.addMouseListener(mouseListener);
		if (c instanceof Container) {
			Container c2 = (Container) c;
			c2.addContainerListener(containerListener);
			for (Component child : c2.getComponents()) {
				addMouseListeners(child);
			}
		}
	}

	private void removeMouseListeners(Component c) {
		c.removeMouseMotionListener(mouseListener);
		c.removeMouseListener(mouseListener);
		if (c instanceof Container) {
			Container c2 = (Container) c;
			c2.removeContainerListener(containerListener);
			for (Component child : c2.getComponents()) {
				addMouseListeners(child);
			}
		}
	}

	/**
	 * Set instructional text that is displayed when no image data is in use.
	 */
	public void setInstruction(String text) {
		if (text == null)
			text = "";
		textArea.setText(text);
	}

	/**
	 * Return true if the zoomed view is meant to be pixelated.
	 */
	public boolean isPixelated() {
		Boolean b = (Boolean) getClientProperty(PIXELATED_KEY);
		if (b == null)
			return true;
		return b;
	}

	/**
	 * Define whether the zoomed view is meant to be pixelated. By default this
	 * attribute is true, but if false then the component is painted through an
	 * AffineTransform to achieve the zoom. If the underlying component uses
	 * vector graphics, then this panel will demonstrate that. (If the
	 * underlying component uses pixelated images, then this property will make
	 * no difference.)
	 */
	public void setPixelated(boolean b) {
		putClientProperty(PIXELATED_KEY, b);
	}

	protected Point getMouseLoc() {
		// I observed a null PointerInfo after unplugging a second monitor
		PointerInfo pointerInfo = MouseInfo.getPointerInfo();
		if (pointerInfo == null)
			return null;
		Point p = pointerInfo.getLocation();
		SwingUtilities.convertPointFromScreen(p, zoomedComponent);
		return p;
	}

	/** Regenerate the zoomed image data and repaint this panel. */
	public void refresh() {
		if (scratchImage == null)
			return;
		Point mouseLoc = getMouseLoc();
		if (mouseLoc == null)
			return;

		Graphics2D g = scratchImage.createGraphics();
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, scratchImage.getWidth(), scratchImage.getHeight());
		g.setComposite(AlphaComposite.SrcOver);
		g.translate(-mouseLoc.x + scratchImage.getWidth() / 2, -mouseLoc.y
				+ scratchImage.getHeight() / 2);
		zoomedComponent.paint(g);

		boolean inside = mouseLoc.x >= 0 && mouseLoc.y >= 0
				&& mouseLoc.x < zoomedComponent.getWidth()
				&& mouseLoc.y < zoomedComponent.getHeight();
		textArea.setVisible(!inside);

		repaint();
	}

	@Override
	protected void paintComponent(Graphics g0) {
		super.paintComponent(g0);

		if (!textArea.isVisible()) {
			Graphics2D g = (Graphics2D) g0.create();
			g.setPaint(checkers);
			g.fillRect(0, 0, getWidth(), getHeight());
			if (isPixelated()) {
				if (scratchImage != null)
					g.drawImage(scratchImage, 0, 0, scratchImage.getWidth()
							* pixelSize, scratchImage.getHeight() * pixelSize,
							0, 0, scratchImage.getWidth(),
							scratchImage.getHeight(), null);
			} else {
				g.scale(pixelSize, pixelSize);
				Point mouseLoc = getMouseLoc();
				if (mouseLoc == null)
					return;

				g.translate(-mouseLoc.x + scratchImage.getWidth() / 2,
						-mouseLoc.y + scratchImage.getHeight() / 2);

				boolean resetToDoubleBuffered = false;
				if (zoomedComponent instanceof JComponent) {
					resetToDoubleBuffered = ((JComponent) zoomedComponent)
							.isDoubleBuffered();
					((JComponent) zoomedComponent).setDoubleBuffered(false);
				}
				zoomedComponent.paint(g);
				if (resetToDoubleBuffered) {
					((JComponent) zoomedComponent).setDoubleBuffered(true);
				}
			}
			g.dispose();
		} else {
			g0.setColor(Color.white);
			g0.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}