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
package com.pump.swing;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.pump.graphics.TextOnlyGraphics2D;
import com.pump.plaf.AbstractSearchHighlight;

/**
 * This is a sheet that sits over a <code>JTextComponent</code> and highlights
 * text.
 * <P>
 * You can call <code>setBackground()</code> to control the shadow this sheet
 * casts over the entire text component. By default the background is set to a
 * translucent black, but to make it transparent you can call: <br>
 * <code>sheet.setBackground(new Color(0x00ffffff,true)</code>
 *
 */
public class TextHighlightSheet extends JComponent {
	private static final long serialVersionUID = 1L;

	public static final int FIREFOX_PADDING = 1;

	protected int padding = 2;
	protected JTextComponent jtc;
	protected JLayeredPane layeredPane;
	protected boolean active;
	protected String searchPhrase = "";
	protected boolean matchCase;
	protected float opacity = 0;
	/** The fill color for the highlights. */
	protected Color highlightColor = Color.white;
	/** The border color for the highlights. */
	protected Color borderColor = new Color(100, 100, 100);
	protected boolean borderActive = true;

	ActionListener updater = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			if (active && layeredPane == null) {
				JRootPane rootPane = jtc.getRootPane();
				if (rootPane == null)
					return;

				layeredPane = rootPane.getLayeredPane();
				int layer = JLayeredPane.DRAG_LAYER.intValue() * 3 / 4
						+ JLayeredPane.POPUP_LAYER.intValue() * 1 / 4;
				layeredPane.add(TextHighlightSheet.this, new Integer(layer));
				makeHighlightsDirty();
			}

			if (layeredPane == null) {
				animator.stop();
				return;
			}

			layeredPane.repaint(getX(), getY(), getWidth(), getHeight());
			if (active && searchPhrase.length() > 0) {
				setVisible(true);
				if (opacity == 1) {
					animator.stop();
				} else {
					opacity = (float) Math.min(1, opacity + .3);
				}
			} else {
				if (opacity == 0) {
					setVisible(false);
					animator.stop();
				} else {
					opacity = (float) Math.max(0, opacity - .3);
				}
			}
			layeredPane.repaint(getX(), getY(), getWidth(), getHeight());
		}
	};
	Timer animator = new Timer(50, updater);

	AncestorListener ancestorListener = new AncestorListener() {

		public void ancestorAdded(AncestorEvent event) {
			makeHighlightsDirty();
		}

		public void ancestorMoved(AncestorEvent event) {
			makeHighlightsDirty();
		}

		public void ancestorRemoved(AncestorEvent event) {
			makeHighlightsDirty();
		}
	};

	DocumentListener docListener = new DocumentListener() {

		@Override
		public void changedUpdate(DocumentEvent e) {
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			makeHighlightsDirty();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			insertUpdate(e);
		}

	};

	public TextHighlightSheet(JTextComponent textComponent) {
		jtc = textComponent;
		jtc.addAncestorListener(ancestorListener);
		setBackground(new Color(0, 0, 0, 50));
		setHighlightColor(Color.white);
		setForeground(Color.black);
		jtc.getDocument().addDocumentListener(docListener);
	}

	/**
	 * Turns this sheet on/off.
	 * 
	 */
	public void setActive(boolean b) {
		try {
			if (active == b)
				return;

			active = b;
		} finally {
			if (active) {
				makeHighlightsDirty();
			}
			animator.start();
		}
	}

	/**
	 * Assigns the search phrase this sheet highlights. (Note an empty phrase is
	 * not highlighted.)
	 */
	public void setSearchPhrase(String s) {
		if (s == null)
			s = "";
		if (s.equals(searchPhrase))
			return;

		searchPhrase = s;
		makeHighlightsDirty();
		animator.start();
	}

	/**
	 * Whether the search phrase has to match in case sensitivity.
	 * 
	 */
	public boolean isMatchCase() {
		return matchCase;
	}

	/**
	 * Controls whether the search phrase has to match in case sensitivity.
	 * 
	 */
	public void setMatchCase(boolean b) {
		if (matchCase == b)
			return;
		matchCase = b;
		makeHighlightsDirty();
	}

	boolean dirty = false;

	/** Call this when the highlights need to recalculate. */
	protected void makeHighlightsDirty() {
		dirty = true;
		SwingUtilities.invokeLater(updateRunnable);
	}

	protected GeneralPath shadow = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
	Area highlightShape = new Area();
	Area clippingShape = new Area();
	private Runnable updateRunnable = new Runnable() {
		public void run() {
			if (dirty) {
				dirty = false;
				updateHighlights();
			}
		}
	};

	protected void updateHighlights() {
		if (active == false || layeredPane == null)
			return;

		if (SwingUtilities.isEventDispatchThread() == false) {
			SwingUtilities.invokeLater(updateRunnable);
			return;
		}

		Rectangle visibleArea = AbstractSearchHighlight.getClipping(jtc,
				layeredPane);
		setBounds(visibleArea);

		try {
			highlightShape.reset();
			clippingShape.reset();

			if (searchPhrase.length() == 0)
				return;

			Point upperLeft = new Point(0, 0);
			Point lowerRight = new Point(getWidth(), getHeight());

			upperLeft = SwingUtilities.convertPoint(this, upperLeft, jtc);
			lowerRight = SwingUtilities.convertPoint(this, lowerRight, jtc);

			int startIndex = jtc.viewToModel(upperLeft);
			int endIndex = jtc.viewToModel(lowerRight);
			startIndex = Math.max(0, startIndex - searchPhrase.length());
			endIndex = Math.min(jtc.getDocument().getLength(), endIndex
					+ searchPhrase.length());

			int index = startIndex;
			int[] array = new int[2];
			while (index < endIndex) {
				if (SwingSearch.find(jtc, searchPhrase, true, matchCase, index,
						array)) {
					Rectangle[] rects = AbstractSearchHighlight
							.getSelectionBounds(array[0], array[1], jtc);
					for (int a = 0; a < rects.length; a++) {
						rects[a].x -= upperLeft.x;
						rects[a].y -= upperLeft.y;

						clippingShape.add(new Area(rects[a]));

						rects[a].x -= padding;
						rects[a].y -= padding;
						rects[a].width += 2 * padding;
						rects[a].height += 2 * padding;
						highlightShape.add(new Area(rects[a]));
					}
					index = array[1];
				} else {
					break;
				}
			}

			int w = getWidth();
			int h = getHeight();

			shadow.reset();

			shadow.moveTo(0, 0);
			shadow.lineTo(w, 0);
			shadow.lineTo(w, h);
			shadow.lineTo(0, h);
			shadow.closePath();

			shadow.append(highlightShape, false);

		} finally {
			repaint();
		}
	}

	/** Sets the color that is used to fill the highlight shapes. */
	public void setHighlightColor(Color c) {
		if (highlightColor.equals(c))
			return;
		highlightColor = c;
		repaint();
	}

	/** The color that is used to fill the highlight shapes. */
	public Color getHighlightColor() {
		return highlightColor;
	}

	/** Controls whether a 1-pixel border (and light shadow) are drawn. */
	public void setBorderActive(boolean b) {
		if (borderActive == b)
			return;
		borderActive = b;
		repaint();
	}

	/** Whether a 1-pixel border (and light shadow) are drawn. */
	public boolean isBorderActive() {
		return borderActive;
	}

	/**
	 * The number of pixels the selected text is padded with when the highlight
	 * outlines are drawn.
	 */
	public int getPadding() {
		return padding;
	}

	/**
	 * Controls the number of pixels the selected text is padded with when the
	 * highlight outlines are drawn.
	 */
	public void setPadding(int i) {
		if (padding == i)
			return;
		padding = i;
		makeHighlightsDirty();
	}

	private static BasicStroke shadowStroke = new BasicStroke(3,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
	private static BasicStroke borderStroke = new BasicStroke(1);
	Point lastTopleft = new Point(-1, -1);

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Point topLeft = new Point(0, 0);
		topLeft = SwingUtilities.convertPoint(this, topLeft, jtc);

		if (topLeft.equals(lastTopleft) == false) {
			// sometimes, for whatever reason: updateHighlights() isn't
			// called before a repaint comes in. To be fair:
			// updateHighlights WILL be called, but a split-second later, which
			// may result in the highlights jumping around on the page
			// in front of the user ever so briefly.
			// So instead here we may manually call updateHighlights() if we
			// have evidence that this component has moved.
			updateHighlights();
		}
		lastTopleft.setLocation(topLeft);

		Graphics2D g2 = (Graphics2D) g;
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				opacity));
		Color background = getBackground();
		if (background != null) {
			g2.setColor(background);
			g2.fill(shadow);
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);

		if (isBorderActive()) {
			g2.translate(0, 1f);
			g2.setColor(new Color(0, 0, 0, 40));
			g2.setStroke(shadowStroke);
			g2.draw(highlightShape);
			g2.translate(0, -1f);
		}

		g2.setColor(getHighlightColor());
		g2.fill(highlightShape);

		if (isBorderActive()) {
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
					RenderingHints.VALUE_STROKE_NORMALIZE);
			g2.setColor(borderColor);
			g2.setStroke(borderStroke);
			g2.draw(highlightShape);
		}

		g2.clip(clippingShape);
		g2.translate(0, -topLeft.y);

		TextOnlyGraphics2D g3 = new TextOnlyGraphics2D(g2, getForeground());
		jtc.paint(g3);
		g3.dispose();
	}
}