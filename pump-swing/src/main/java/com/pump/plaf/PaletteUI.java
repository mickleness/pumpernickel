package com.pump.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;

import com.pump.awt.Scribbler;
import com.pump.swing.FocusedBorder;
import com.pump.swing.JPalette;
import com.pump.swing.JPalette.AccessibleColor;

public class PaletteUI extends ComponentUI {

	/**
	 * This JPalette client property can resolve to
	 * {@link #PROPERTY_HIGHLIGHT_GRID} or {@link #VALUE_HIGHLIGHT_SCRIBBLE}
	 */
	public static final String PROPERTY_HIGHLIGHT = PaletteUI.class.getName()
			+ "#highlight";

	/**
	 * This value for the {@link #PROPERTY_HIGHLIGHT} key indicates a light
	 * scribbled border should be sketched between all the colored cells.
	 */
	public static final Object VALUE_HIGHLIGHT_SCRIBBLE = PaletteUI.class
			.getName() + "#highlightScribble";

	/**
	 * This value for the {@link #PROPERTY_HIGHLIGHT} key indicates a light
	 * beveled highlight/shadow should be painted on all colored cells.
	 */
	public static final Object VALUE_HIGHLIGHT_BEVEL = PaletteUI.class
			.getName() + "#highlightGrid";

	/**
	 * This JPalette client property resolves to a Fields object.
	 */
	private static final String PROPERTY_FIELDS = PaletteUI.class.getName()
			+ "#fields";

	public static ComponentUI createUI(JComponent c) {
		return new PaletteUI();
	}

	class PaletteLayoutManager implements LayoutManager {
		String PROPERTY_CELL = PaletteLayoutManager.class.getName() + "#cell";

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			JPalette p = (JPalette) parent;
			return p.getUI().getPreferredSize(p);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			JPalette p = (JPalette) parent;
			return p.getUI().getMinimumSize(p);
		}

		@Override
		public void layoutContainer(Container parent) {
			JPalette p = (JPalette) parent;
			Color[][] paletteLayout = p.getColors();

			List<JToggleButton> cells = getCells(parent);

			Insets i = p.getInsets();

			int height = p.getHeight() - i.left - i.right;
			int width = p.getWidth() - i.top - i.bottom;

			boolean buttonStatesDirty = false;
			int y = i.top;
			for (int row = 0; row < paletteLayout.length; row++) {
				int x = i.left;
				int y2 = i.top + (row + 1) * height / paletteLayout.length;
				for (int col = 0; col < paletteLayout[0].length; col++) {
					int x2 = i.left + (col + 1) * width
							/ paletteLayout[0].length;
					if (paletteLayout[row][col] != null) {
						JToggleButton cell;
						if (cells.size() == 0) {
							cell = createCell();
							p.add(cell);
						} else {
							cell = cells.remove(0);
						}
						boolean dirtyForeground = !Objects.equals(
								cell.getForeground(), paletteLayout[row][col]);
						if (dirtyForeground) {
							cell.setForeground(paletteLayout[row][col]);
							buttonStatesDirty = true;
						}
						cell.setBounds(x, y, x2 - x, y2 - y);

						String hexName = Integer
								.toHexString(paletteLayout[row][col].getRGB());
						while (hexName.length() < 6)
							hexName = "0" + hexName;
						if (hexName.length() == 8)
							hexName = hexName.substring(2);
						hexName = "0x" + hexName.toUpperCase();

						String desc;
						if (paletteLayout[row][col] instanceof AccessibleColor) {
							AccessibleColor a = (AccessibleColor) paletteLayout[row][col];
							desc = hexName + " \""
									+ a.getName(Locale.getDefault()) + "\"";
						} else {
							desc = hexName;
						}
						cell.setToolTipText(desc);
						cell.getAccessibleContext().setAccessibleName(desc);
					}
					x = x2;
				}
				y = y2;
			}
			while (cells.size() > 0) {
				p.remove(cells.remove(0));
			}

			if (buttonStatesDirty) {
				getFields(p, true).refreshSelectedStates();
			}
		}

		ActionListener cellActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JToggleButton button = (JToggleButton) e.getSource();
				JPalette p = (JPalette) button.getParent();
				if (p.isRequestFocusEnabled())
					p.requestFocus();
				button.setSelected(true);
				p.getColorSelectionModel().setSelectedColor(
						button.getForeground());
			}
		};

		private JToggleButton createCell() {
			JToggleButton newCell = new JToggleButton();
			newCell.addMouseListener(mouseListener);
			newCell.addMouseMotionListener(mouseListener);
			newCell.setContentAreaFilled(false);
			newCell.setBorder(null);
			newCell.setRequestFocusEnabled(false);
			newCell.setBorderPainted(false);
			newCell.setOpaque(false);
			newCell.putClientProperty(PROPERTY_CELL, Boolean.TRUE);
			newCell.addActionListener(cellActionListener);
			return newCell;
		}

		private LinkedList<JToggleButton> getCells(Container parent) {
			LinkedList<JToggleButton> cells = new LinkedList<>();
			for (int a = 0; a < parent.getComponentCount(); a++) {
				Component c = parent.getComponent(a);
				if (c instanceof JToggleButton) {
					JToggleButton b = (JToggleButton) c;
					Boolean j = (Boolean) b.getClientProperty(PROPERTY_CELL);
					if (j != null && j.booleanValue()) {
						cells.add((JToggleButton) c);
					}
				}
			}
			return cells;
		}

	}

	PropertyChangeListener propertyRepaintListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			((Component) evt.getSource()).repaint();
		}

	};

	PropertyChangeListener propertyLayoutListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			relayoutCells((JPalette) evt.getSource());
		}

	};

	MouseInputAdapter mouseListener = new MouseInputAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			Component c = e.getComponent();
			JPalette palette;
			Point palettePoint = e.getPoint();
			if (c instanceof JToggleButton) {
				palette = (JPalette) c.getParent();
				palettePoint = SwingUtilities.convertPoint(c, palettePoint,
						palette);
			} else {
				palette = (JPalette) c;
			}
			if (palette.isRequestFocusEnabled())
				palette.requestFocus();

			Insets i = palette.getInsets();
			palettePoint.x = Math.max(i.left + 1, Math.min(palette.getWidth()
					- i.right - i.left - 1, palettePoint.x));
			palettePoint.y = Math.max(i.top + 1, Math.min(palette.getHeight()
					- i.top - i.bottom - 1, palettePoint.y));

			Component c2 = SwingUtilities.getDeepestComponentAt(palette,
					palettePoint.x, palettePoint.y);
			if (c2 != null && c2 instanceof JToggleButton) {
				Color f = c2.getForeground();
				palette.getColorSelectionModel().setSelectedColor(f);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			mousePressed(e);
		}
	};

	FocusListener focusListener = new FocusListener() {

		@Override
		public void focusGained(FocusEvent e) {
			((JPalette) e.getSource()).repaint();
		}

		@Override
		public void focusLost(FocusEvent e) {
			((JPalette) e.getSource()).repaint();
		}

	};

	KeyListener keyListener = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {
			int dx = 0;
			int dy = 0;
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				dx = -1;
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				dx = 1;
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				dy = -1;
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				dy = 1;
			}

			if (dx != 0 || dy != 0) {
				e.consume();
				JPalette palette = (JPalette) e.getComponent();
				nudge(palette, dx, dy);
			}
		}
	};

	protected Border getDefaultBorder() {
		return new EmptyBorder(2, 2, 2, 2);
		// return new BevelBorder(BevelBorder.LOWERED, Color.lightGray,
		// Color.darkGray);
	}

	static class Fields {
		JPalette palette;

		ChangeListener colorSelectionListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshSelectedStates();
			}
		};

		/**
		 * Iterate over every button in this palette and update its selected
		 * state based on whether it matches the currently selected color.
		 */
		protected void refreshSelectedStates() {
			Color c = palette.getColorSelectionModel().getSelectedColor();
			for (Component z : palette.getComponents()) {
				JToggleButton b = (JToggleButton) z;
				Color f = b.getForeground();
				if (f == null) {
					b.setSelected(false);
				} else {
					b.setSelected(f.equals(c));
				}
			}
		}

		PropertyChangeListener colorSelectionModelListener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				ColorSelectionModel oldModel = (ColorSelectionModel) evt
						.getOldValue();
				ColorSelectionModel newModel = (ColorSelectionModel) evt
						.getNewValue();
				if (oldModel != null)
					oldModel.removeChangeListener(colorSelectionListener);
				if (newModel != null)
					newModel.addChangeListener(colorSelectionListener);
			}
		};

		public Fields(JPalette p) {
			palette = p;
		}

		public void install() {
			palette.getColorSelectionModel().addChangeListener(
					colorSelectionListener);
			palette.addPropertyChangeListener(
					JPalette.PROPERTY_SELECTION_MODEL,
					colorSelectionModelListener);
		}

		public void uninstall() {
			palette.getColorSelectionModel().removeChangeListener(
					colorSelectionListener);
			palette.removePropertyChangeListener(
					JPalette.PROPERTY_SELECTION_MODEL,
					colorSelectionModelListener);
		}
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.addPropertyChangeListener(JPalette.PROPERTY_COLORS,
				propertyLayoutListener);
		c.addPropertyChangeListener(PaletteUI.PROPERTY_HIGHLIGHT,
				propertyRepaintListener);
		c.setLayout(new PaletteLayoutManager());
		c.setRequestFocusEnabled(true);
		c.addMouseListener(mouseListener);
		c.addFocusListener(focusListener);
		c.addKeyListener(keyListener);
		c.setFocusable(true);
		Fields fields = getFields((JPalette) c, true);
		fields.install();
		c.setBorder(new FocusedBorder(getDefaultBorder()));
		relayoutCells((JPalette) c);
	}

	protected void relayoutCells(JPalette c) {
		c.revalidate();
		c.repaint();
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removePropertyChangeListener(JPalette.PROPERTY_COLORS,
				propertyLayoutListener);
		c.removePropertyChangeListener(PaletteUI.PROPERTY_HIGHLIGHT,
				propertyRepaintListener);
		c.removeMouseListener(mouseListener);
		c.removeFocusListener(focusListener);
		c.removeKeyListener(keyListener);
		Fields fields = getFields((JPalette) c, false);
		if (fields != null)
			fields.uninstall();
		c.putClientProperty(PROPERTY_FIELDS, null);
	}

	private Fields getFields(JPalette p, boolean createIfMissing) {
		Fields f = (Fields) p.getClientProperty(PROPERTY_FIELDS);
		if (f == null && createIfMissing) {
			f = new Fields(p);
			p.putClientProperty(PROPERTY_FIELDS, f);
		}
		return f;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		JPalette p = (JPalette) c;
		paintBackground((Graphics2D) g.create(), p);
		paintGrid((Graphics2D) g.create(), p);
		paintHighlights((Graphics2D) g.create(), p);
	}

	protected void paintBackground(Graphics2D g, JPalette p) {
		if (p.isOpaque()) {
			g.setColor(p.getBackground());
			g.fillRect(0, 0, p.getWidth(), p.getHeight());
		}
	}

	protected void paintGrid(Graphics2D g, JPalette p) {
		for (Component c : p.getComponents()) {
			Color f = c.getForeground();
			g.setColor(f);
			Rectangle r = c.getBounds();
			g.fillRect(r.x, r.y, r.width, r.height);
			JToggleButton b = (JToggleButton) c;
			if (b.isSelected()) {
				paintSelectedIndicator(g, p, b, r);
			}
		}
	}

	protected void paintSelectedIndicator(Graphics2D g, JPalette p,
			JToggleButton b, Rectangle r) {
		int j = Math.max(1, Math.min(4, r.width / 10));
		int k = Math.max(1, Math.min(4, r.height / 10));
		Insets i = new Insets(k, j, k, j);
		float[] hsb = new float[3];
		Color.RGBtoHSB(b.getForeground().getRed(),
				b.getForeground().getGreen(), b.getForeground().getBlue(), hsb);

		float d = Math.min(r.width, r.height);
		float z;
		if (d < 20) {
			z = Math.max(1, (d - 10f) / 10f + 1);
		} else {
			z = 2;
		}

		g.setStroke(new BasicStroke(z));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);

		Color color = hsb[2] > .5 ? Color.black : Color.white;
		g.setColor(color);
		g.draw(new Ellipse2D.Float(r.x + i.left, r.y + i.top, r.width - i.right
				- i.left, r.height - i.top - i.bottom));

		color = hsb[2] > .5 ? Color.white : Color.black;
		g.setColor(color);
		g.draw(new Ellipse2D.Float(r.x + i.left + z, r.y + i.top + z, r.width
				- i.right - i.left - 2 * z, r.height - i.top - i.bottom - 2 * z));

		// CheckIcon icon = new CheckIcon(r.width - i.left - i.right, r.height
		// - i.top - i.bottom, color);
		// icon.paintIcon(p, g, r.x + i.left, r.y + i.top);
	}

	protected void paintHighlights(Graphics2D g, JPalette p) {
		boolean highlightScribble = VALUE_HIGHLIGHT_SCRIBBLE.equals(p
				.getClientProperty(PROPERTY_HIGHLIGHT));
		boolean highlightGrid = VALUE_HIGHLIGHT_BEVEL.equals(p
				.getClientProperty(PROPERTY_HIGHLIGHT));
		if (highlightScribble) {
			g.setColor(new Color(255, 255, 255, 50));
			g.setStroke(new BasicStroke(.5f));
			int ctr = 0;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			for (Component c : p.getComponents()) {
				GeneralPath path = Scribbler.create(c.getBounds(), 1, 1, ctr++);
				g.draw(path);
			}
		} else if (highlightGrid) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			Color dark1 = new Color(0, 0, 0, 50);
			Color light1 = new Color(255, 255, 255, 60);
			for (Component c : p.getComponents()) {
				Graphics2D g2 = (Graphics2D) g.create();
				Rectangle r = c.getBounds();
				g2.clipRect(r.x, r.y, r.width, r.height);
				g2.setColor(dark1);
				g2.drawLine(r.x + 1, r.y + r.height - 1, r.x + r.width - 2, r.y
						+ r.height - 1);
				g2.drawLine(r.x + r.width - 1, r.y + 1, r.x + r.width - 1, r.y
						+ r.height - 1);
				g2.setColor(light1);
				g2.drawLine(r.x + 1, r.y, r.x + r.width - 2, r.y);
				g2.drawLine(r.x, r.y, r.x, r.y + r.height - 2);

				g2.dispose();
			}

		}
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return getMinimumSize(c);
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		JPalette p = (JPalette) c;
		int cellHeight = p.getCellHeight();
		int cellWidth = p.getCellWidth();
		Color[][] colors = p.getColors();
		int rowCount = colors.length;
		int columnCount = colors[0].length;
		Insets i = c.getInsets();
		return new Dimension(columnCount * cellWidth + i.left + i.right,
				rowCount * cellHeight + i.top + i.bottom);
	}

	@Override
	public Dimension getMaximumSize(JComponent c) {
		return getMinimumSize(c);
	}

	public void nudge(JPalette palette, int dx, int dy) {
		JToggleButton b = getSelectedButton(palette);
		if (b == null) {
			if (palette.getComponentCount() > 0) {
				b = (JToggleButton) palette.getComponent(0);
				b.doClick();
			}
			return;
		}

		JToggleButton next = (JToggleButton) FocusArrowListener.getComponent(
				dx, dy, b, Arrays.asList(palette.getComponents()));
		if (next != null) {
			next.doClick();
		}
	}

	protected JToggleButton getSelectedButton(JPalette palette) {
		for (Component c : palette.getComponents()) {
			if (c instanceof JToggleButton) {
				JToggleButton b = (JToggleButton) c;
				if (b.isSelected())
					return b;
			}
		}
		return null;
	}
}
