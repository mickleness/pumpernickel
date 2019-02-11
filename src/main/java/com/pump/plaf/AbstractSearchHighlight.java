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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

import com.pump.graphics.TextOnlyGraphics2D;

/**
 * This renders a temporary effect somehow animating a selection in a
 * JTextComponent. This assumes the text component is in a JFrame, JApplet, or
 * other RootPaneContainer because it renders the animation with the help of the
 * LayeredPane.
 * <P>
 * This creates images of the selection when constructed, and renders those
 * images through JComponents. Once the image is drawn it cannot be modified,
 * but you can modify it's opacity and transform through the
 * <code>updateAnimation</code> method.
 * <P>
 * Also you can modify the duration of the animation by calling
 * <code>UIManager.put("SearchHighlight.duration",new Integer(millis))</code>,
 * or overriding the <code>getDuration()</code> method of this object.
 * <P>
 * This is automatically created by calling <code>TextSearch.highlight()</code>.
 */
public abstract class AbstractSearchHighlight {

	abstract class HighlightInfo {
		protected final JComponent jc;

		HighlightInfo(JComponent jc) {
			this.jc = jc;
		}

		abstract Rectangle[] createHighlightBounds();

		HighlightImage[] createHighlights() {
			Rectangle[] rects = createHighlightBounds();
			Insets insets = getHighlightInsets();
			List<HighlightImage> returnValue = new ArrayList<>(rects.length);
			for (int a = 0; a < rects.length; a++) {
				int imageWidth = rects[a].width + insets.left + insets.right;
				int imageHeight = rects[a].height + insets.top + insets.bottom;
				BufferedImage image = new BufferedImage(imageWidth,
						imageHeight, BufferedImage.TYPE_INT_ARGB);

				Graphics2D g = image.createGraphics();
				Rectangle highlightBounds = new Rectangle(insets.left,
						insets.top, rects[a].width, rects[a].height);
				paintHighlightBackground((Graphics2D) g.create(),
						highlightBounds);
				paintHighlightForeground((Graphics2D) g.create(),
						highlightBounds, rects[a]);

				g.dispose();

				Point center = new Point(rects[a].x + rects[a].width / 2,
						rects[a].y + rects[a].height / 2);
				Point imageCenter = new Point(insets.left + rects[a].width / 2,
						insets.top + rects[a].height / 2);
				HighlightImage highlight = new HighlightImage(image, center,
						imageCenter);
				returnValue.add(highlight);
			}
			return returnValue.toArray(new HighlightImage[returnValue.size()]);
		}
	}

	class TextHighlightInfo extends HighlightInfo {
		protected final int startIndex;
		protected final int endIndex;
		protected final JTextComponent jtc;

		TextHighlightInfo(int startIndex, int endIndex, JTextComponent jtc) {
			super(jtc);
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.jtc = jtc;
		}

		Rectangle[] createHighlightBounds() {
			Rectangle[] rects = getSelectionBounds(startIndex, endIndex, jtc);
			return rects;
		}
	}

	class TableHighlightInfo extends HighlightInfo {
		protected final int rowIndex;
		protected final int columnIndex;
		protected final JTable table;

		TableHighlightInfo(int rowIndex, int columnIndex, JTable table) {
			super(table);
			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
			this.table = table;
		}

		@Override
		Rectangle[] createHighlightBounds() {
			Rectangle rect = table.getCellRect(rowIndex, columnIndex, true);
			return new Rectangle[] { rect };
		}
	}

	protected HighlightInfo highlightInfo;
	protected JLayeredPane layeredPane;

	private static long idCtr = 0;
	protected final long id = idCtr++;

	protected static long currentID;

	/**
	 * Clears all current <code>AbstractSearchHighlights</code>.
	 * 
	 */
	public static void clearHighlights() {
		// just trick any visible highlights into thinking there's a
		// newer highlight that gets priority
		currentID = idCtr++;
	}

	long startTime = -1;
	ActionListener actionListener = new ActionListener() {
		boolean fadeOut = false;

		public void actionPerformed(ActionEvent e) {
			if (startTime == -1)
				startTime = System.currentTimeMillis();

			for (int a = 0; a < highlights.length; a++) {
				highlights[a].nudge();
			}

			long elapsed = System.currentTimeMillis() - startTime;
			float duration = getDuration();
			if (duration == 0)
				duration = 500; // the default

			if (id != currentID) // abort immediately, let the other one shine
				fadeOut = true;

			float fraction = (elapsed) / (duration);

			if (fraction < 1) {
				updateAnimation(highlights, fraction);
			} else {
				fadeOut = true;
			}

			if (fadeOut) {
				for (int a = 0; a < highlights.length; a++) {
					Number opacity = (Number) highlights[a]
							.getClientProperty("opacity");
					if (opacity == null)
						opacity = new Float(1);
					opacity = new Float(opacity.floatValue() - .25f);
					if (opacity.floatValue() > 0) {
						highlights[a].putClientProperty("opacity", opacity);
					} else {
						highlights[a].getParent().repaint(highlights[a].getX(),
								highlights[a].getY(), highlights[a].getWidth(),
								highlights[a].getHeight());
						highlights[a].getParent().remove(highlights[a]);
						timer.stop();
					}
				}
			}
		}
	};
	Timer timer = new Timer(10, actionListener);
	HighlightImage[] highlights;

	/**
	 * Creates a new search highlight that captures the text from [startIndex,
	 * endIndex].
	 * 
	 * @param jtc
	 *            the text component being used.
	 * @param startIndex
	 *            the start index.
	 * @param endIndex
	 *            the end index.
	 */
	public AbstractSearchHighlight(JTextComponent jtc, int startIndex,
			int endIndex) {
		initialize(new TextHighlightInfo(startIndex, endIndex, jtc));
	}

	/**
	 * Creates a new search highlight that renders the table cell indicated.
	 * 
	 * @param table
	 *            the table component being used.
	 * @param rowIndex
	 *            the row index of the cell to highlight.
	 * @param columnIndex
	 *            the columnIndex of the cell to highlight.
	 */
	public AbstractSearchHighlight(JTable table, int rowIndex, int columnIndex) {
		initialize(new TableHighlightInfo(rowIndex, columnIndex, table));
	}

	private void initialize(HighlightInfo info) {
		highlightInfo = info;
		currentID = id;

		JRootPane rootPane = info.jc.getRootPane();
		layeredPane = rootPane.getLayeredPane();

		highlights = highlightInfo.createHighlights();
		for (HighlightImage highlight : highlights) {
			layeredPane.add(highlight, JLayeredPane.DRAG_LAYER);
		}
		updateAnimation(highlights, 0);

		timer.start();

	}

	/**
	 * This paints the text in the image.
	 * 
	 * @param g
	 *            the graphics to paint to.
	 * @param textRect
	 *            the rectangle in g the text is painted in.
	 * @param textRectInTextComponent
	 *            the rectangle relative to the text component.
	 */
	protected void paintHighlightForeground(Graphics2D g, Rectangle textRect,
			Rectangle textRectInTextComponent) {
		int tx = -textRectInTextComponent.x + textRect.x;
		int ty = -textRectInTextComponent.y + textRect.y;
		g.translate(tx, ty);
		g.clipRect(textRectInTextComponent.x, textRectInTextComponent.y,
				textRectInTextComponent.width, textRectInTextComponent.height);
		paintOnlyText(g);
	}

	/**
	 * This paints the background underneath the text in the image. This might
	 * be an opaque shape, for example.
	 * 
	 * @param g
	 *            the graphics to paint to.
	 * @param textRect
	 *            where the text will be rendered. A shape enclosing the text
	 *            should add a few pixels of padding around this rectangle.
	 */
	protected void paintHighlightBackground(Graphics2D g, Rectangle textRect) {
	}

	/**
	 * The padding added to the text rectangle to create the image.
	 */
	protected Insets getHighlightInsets() {
		return new Insets(3, 3, 3, 3);
	}

	/**
	 * This is constantly called while the animation is playing. It gives you an
	 * optional chance to modify the highlights a little bit.
	 * 
	 * @param highlights
	 *            the components to modify. You should change the client
	 *            property "opacity" (to be a number from [0, 1]) and
	 *            "transform" (to be an AffineTransform) to modify these
	 *            highlights.
	 * @param fraction
	 *            a fraction from [0, 1], indicating how progressed this
	 *            animation is. After <code>t = 1</code> this method is no
	 *            longer called and this highlight will fade out.
	 */
	protected abstract void updateAnimation(JComponent[] highlights,
			float fraction);

	/**
	 * Paints the text from the <code>JTextComponent</code> in the rectangle
	 * provided. You should clip this Graphics2D before calling this method,
	 * though.
	 */
	protected void paintOnlyText(Graphics2D g) {
		TextOnlyGraphics2D textOnlyG = new TextOnlyGraphics2D(g, Color.black);
		highlightInfo.jc.paint(textOnlyG);
		textOnlyG.dispose();
	}

	/**
	 * This returns all the rectangles needed to enclose the selection. Usually
	 * this is just one rectangle, but in cases of a line break this may be two.
	 */
	public static Rectangle[] getSelectionBounds(int startIndex, int endIndex,
			JTextComponent jtc) {
		try {
			List<Rectangle> rectangles = new ArrayList<Rectangle>();

			Rectangle nextRect = null;
			for (int a = startIndex; a < endIndex; a++) {
				Rectangle newRect = (nextRect == null) ? jtc.getUI()
						.modelToView(jtc, a, Position.Bias.Forward) : nextRect;
				nextRect = jtc.getUI().modelToView(jtc, a + 1,
						Position.Bias.Backward);
				if (nextRect.x > newRect.x) {
					// TODO: this is not tested against BIDI text
					newRect.width = nextRect.x - newRect.x;
				}

				boolean added = false;
				for (int b = 0; b < rectangles.size() && added == false; b++) {
					Rectangle r = rectangles.get(b);
					// give a little leeway:
					newRect.x -= 1;
					newRect.width += 2;
					newRect.y -= 1;
					newRect.height += 2;
					if (r.intersects(newRect)) {
						// (take leeway away...)
						newRect.x += 1;
						newRect.width -= 2;
						newRect.y += 1;
						newRect.height -= 2;
						r.add(newRect);
						added = true;
					}
				}
				if (!added)
					rectangles.add(newRect);
			}

			return rectangles.toArray(new Rectangle[rectangles.size()]);
		} catch (BadLocationException e) {
			IllegalArgumentException e2 = new IllegalArgumentException();
			e2.initCause(e);
			throw e2;
		}
	}

	private static PropertyChangeListener repaintListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			HighlightImage src = (HighlightImage) evt.getSource();
			if (evt.getPropertyName().equals("transform")) {
				src.nudge();
				src.repaint();
			}
		}
	};

	AncestorListener ancestorListener = new AncestorListener() {

		public void ancestorMoved(AncestorEvent event) {
			for (int a = 0; a < highlights.length; a++) {
				highlights[a].nudge();
			}
		}

		public void ancestorAdded(AncestorEvent event) {
		}

		public void ancestorRemoved(AncestorEvent event) {
		}
	};

	protected float getDuration() {
		return UIManager.getInt("SearchHighlight.duration");
	}

	/**
	 * Returns the area relative to "reference" that is visible of component.
	 * Mostly this is concerned with clipping to parent viewports.
	 */
	public static Rectangle getClipping(JComponent component,
			JComponent reference) {
		// clip to any possible viewports the text component
		// might be nestled in
		JComponent jc = component;
		Rectangle clipping = new Rectangle();
		Rectangle result = SwingUtilities
				.convertRectangle(
						component,
						new Rectangle(0, 0, component.getWidth(), component
								.getHeight()), reference);

		while (jc != null) {
			if (jc instanceof JViewport) {
				Point topLeft = SwingUtilities
						.convertPoint(jc, 0, 0, reference);
				Point bottomRight = SwingUtilities.convertPoint(jc,
						jc.getWidth(), jc.getHeight(), reference);

				clipping.x = topLeft.x;
				clipping.y = topLeft.y;
				clipping.width = bottomRight.x - clipping.x;
				clipping.height = bottomRight.y - clipping.y;

				result = result.intersection(clipping);
			}
			if (jc.getParent() instanceof JComponent) {
				jc = (JComponent) jc.getParent();
			} else {
				jc = null;
			}
		}
		return result;
	}

	class HighlightImage extends JComponent {
		private static final long serialVersionUID = 1L;

		Point center, imageCenter;
		BufferedImage image;

		/**
		 * Creates a new HighlightImage.
		 * 
		 * @param image
		 *            the image to render
		 * @param center
		 *            the center, relative to the JTextComponent
		 * @param imageCenter
		 *            the center, relative to the BufferedImage
		 */
		public HighlightImage(BufferedImage image, Point center,
				Point imageCenter) {
			this.image = image;
			this.center = center;
			this.imageCenter = imageCenter;
			addPropertyChangeListener("transform", repaintListener);
			addPropertyChangeListener("opacity", repaintListener);

			highlightInfo.jc.addAncestorListener(ancestorListener);
			nudge();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			Rectangle clipping = getClipping(highlightInfo.jc, this);
			if (clipping.width == 0 || clipping.height == 0)
				return;

			g.clipRect(clipping.x, clipping.y, clipping.width, clipping.height);

			Point2D absCenter = SwingUtilities.convertPoint(highlightInfo.jc,
					center.x, center.y, this);
			g2.translate(absCenter.getX(), absCenter.getY());
			AffineTransform transform = (AffineTransform) getClientProperty("transform");
			if (transform != null)
				g2.transform(transform);
			g2.translate(-imageCenter.x, -imageCenter.y);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);

			Number opacity = (Number) getClientProperty("opacity");
			if (opacity != null) {
				g2.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, opacity.floatValue()));
			}

			g2.drawImage(image, 0, 0, null);
		}

		/**
		 * Repositions this component.
		 */
		protected void nudge() {
			Point topLeft = SwingUtilities.convertPoint(highlightInfo.jc, 0, 0,
					layeredPane);

			GeneralPath path = new GeneralPath();
			path.moveTo(0, 0);
			path.lineTo(image.getWidth(), 0);
			path.lineTo(image.getWidth(), image.getHeight());
			path.lineTo(0, image.getHeight());
			path.closePath();

			AffineTransform transform = AffineTransform.getTranslateInstance(
					-imageCenter.x, -imageCenter.y);
			AffineTransform hTransform = (AffineTransform) getClientProperty("transform");
			if (hTransform != null) {
				transform.concatenate(hTransform);
			}
			path.transform(transform);
			Rectangle bounds = path.getBounds();
			setBounds(center.x + topLeft.x - bounds.width / 2, center.y
					+ topLeft.y - bounds.height / 2, bounds.width,
					bounds.height);
		}
	}
}