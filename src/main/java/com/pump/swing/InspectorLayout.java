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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicButtonUI;

import com.pump.awt.RowLayout;
import com.pump.awt.RowLayout.Cell;
import com.pump.awt.RowLayout.ComponentCluster;
import com.pump.awt.RowLayout.ComponentConstraints;
import com.pump.icon.EmptyIcon;
import com.pump.icon.TriangleIcon;
import com.pump.plaf.UIEffect;

public class InspectorLayout {

	private static final String ROTATION = InspectorLayout.class.getName()
			+ ".rotation";
	private static final String EFFECT = InspectorLayout.class.getName()
			+ ".effect";

	private static class AnimateTriangleEffect extends UIEffect {
		boolean expanded;

		AnimateTriangleEffect(AbstractButton button, boolean expanded) {
			super(button, 200, 20);
			this.expanded = expanded;

			addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					float progress = ((UIEffect) e.getSource()).getProgress();

					if (AnimateTriangleEffect.this.expanded) {
						getComponent().putClientProperty(ROTATION,
								new Double(progress * (Math.PI / 2)));
					} else {
						getComponent().putClientProperty(ROTATION,
								new Double((1 - progress) * (Math.PI / 2)));
					}
				}
			});
		}
	}

	private class HeaderUI extends BasicButtonUI {

		boolean clickable;
		TriangleIcon triangleIcon = new TriangleIcon(SwingConstants.EAST, 10,
				10);

		HeaderUI(boolean clickable) {
			this.clickable = clickable;
		}

		@Override
		public void paint(Graphics g0, JComponent c) {
			Graphics2D g = (Graphics2D) g0;
			if (c.isOpaque()) {
				if (clickable && c instanceof AbstractButton
						&& ((AbstractButton) c).getModel().isArmed()) {
					g.setPaint(new GradientPaint(new Point(0, 0), new Color(
							230, 230, 230), new Point(0, c.getHeight()),
							new Color(180, 180, 180)));
				} else {
					g.setPaint(new GradientPaint(new Point(0, 0), Color.white,
							new Point(0, c.getHeight()), new Color(216, 216,
									216)));
				}
				g.fillRect(0, 0, c.getWidth(), c.getHeight());

				Rectangle border = new Rectangle(0, 0, c.getWidth() - 1,
						c.getHeight() - 1);
				if (!isHorizontalEdgePainted()) {
					border.x--;
					border.width += 2;
				}

				if (c.hasFocus()) {
					Shape rect = new RoundRectangle2D.Float(border.x, border.y,
							border.width, border.height, 8, 8);
					com.pump.plaf.PlafPaintUtils.paintFocus(g, rect, 2);
				}

				g.setColor(Color.gray);
				g.setStroke(new BasicStroke(1));
				g.drawRect(border.x, border.y, border.width, border.height);
			}

			super.paint(g, c);

			if (clickable) {
				Graphics2D g2 = (Graphics2D) g.create();
				Number angle = (Number) c.getClientProperty(ROTATION);
				if (angle != null) {
					g2.rotate(angle.doubleValue(), 15, c.getHeight() / 2);
				}
				triangleIcon.paintIcon(c, g2,
						15 - triangleIcon.getIconWidth() / 2, c.getHeight() / 2
								- triangleIcon.getIconHeight() / 2);
				g2.dispose();
			}
		}

	}

	public class Gap extends JComponent {
		private static final long serialVersionUID = 1L;

		Gap() {
			setOpaque(false);
		}
	}

	public class Header extends JButton {
		private static final long serialVersionUID = 1L;
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean isExpanded = !isExpanded();

				setExpanded(isExpanded);
			}
		};

		public Header(String text, boolean collapsible) {
			super(text);
			setFont(UIManager.getFont("Label.font"));
			if (collapsible) {
				setIcon(new EmptyIcon(10, 10));
				addActionListener(actionListener);
				putClientProperty(ROTATION, new Double(Math.PI / 2.0));
			} else {
				setRequestFocusEnabled(false);
				setFocusable(false);
			}
			setBorderPainted(false);
			setContentAreaFilled(false);
			setUI(new HeaderUI(collapsible));
			setHorizontalAlignment(SwingConstants.LEFT);
			setIconTextGap(10);
			setBorder(new EmptyBorder(5, 10, 5, 5));
			setMargin(new Insets(0, 0, 0, 0));
			addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_LEFT
							|| e.getKeyCode() == KeyEvent.VK_UP) {
						setExpanded(false);
						e.consume();
					} else if (e.getKeyCode() == KeyEvent.VK_RIGHT
							|| e.getKeyCode() == KeyEvent.VK_DOWN) {
						setExpanded(true);
						e.consume();
					}
				}
			});
			setOpaque(true);
		}

		public boolean isExpanded() {
			int[] range = getRange();

			boolean visible = false;
			for (int a = range[0]; a < range[1]; a++) {
				if (rowLayout.isRowVisible(a))
					visible = true;
			}
			return visible;
		}

		public void setExpanded(boolean b) {
			if (isExpanded() == b)
				return;

			int[] range = getRange();

			for (int a = range[0]; a < range[1]; a++) {
				rowLayout.setRowVisible(a, b);
			}

			AnimateTriangleEffect effect = (AnimateTriangleEffect) getClientProperty(EFFECT);
			if (effect != null) {
				effect.stop();
			}
			effect = new AnimateTriangleEffect(Header.this, b);
			putClientProperty(EFFECT, effect);
		}

		private int[] getRange() {
			int startIndex = -1;
			for (int a = 0; a < rowLayout.getRowCount() && startIndex == -1; a++) {
				JComponent[] jc = rowLayout.getRow(a);
				if (jc.length == 1 && jc[0] == Header.this) {
					startIndex = a + 1;
				}
			}
			int endIndex = -1;
			for (int a = startIndex; a < rowLayout.getRowCount()
					&& endIndex == -1; a++) {
				JComponent[] jc = rowLayout.getRow(a);
				if ((jc.length == 1 && jc[0] instanceof Header)
						|| (jc.length == 1 && jc[0] instanceof Gap)) {
					endIndex = a;
				}
			}
			if (endIndex == -1)
				endIndex = rowLayout.getRowCount();
			if (startIndex == -1) {
				throw new IllegalStateException("this header (" + getText()
						+ ") was not found");
			}
			return new int[] { startIndex, endIndex };
		}
	}

	public enum HorizontalAlignment {
		LEFT, RIGHT, CENTER, STRETCH
	}

	public static String TWO_COLUMN_INSPECTOR = "twoColumnInspector";
	public static String SINGLE_COLUMN_NO_INSETS = "singleColumn";

	String currentSectionID = TWO_COLUMN_INSPECTOR;
	int sectionCtr = 0;
	boolean horizontalEdgePainted = true;

	RowLayout rowLayout = new RowLayout() {
		private static final long serialVersionUID = 1L;

		public boolean isAutoAlign(JComponent jc) {
			if (jc instanceof Header)
				return false;
			return super.isAutoAlign(jc);
		}
	};

	Insets columnInsets = new Insets(1, 5, 1, 5);

	public InspectorLayout() {
		rowLayout.addRowType(TWO_COLUMN_INSPECTOR,
				new Cell[] { new Cell(0, .5f, columnInsets),
						new Cell(1, .5f, columnInsets) });
		rowLayout.addRowType(SINGLE_COLUMN_NO_INSETS, new Cell[] { new Cell(1,
				.5f, new Insets(0, 0, 0, 0)) });
	}

	public void setColumnInsets(Insets i) {
		columnInsets.set(i.top, i.left, i.bottom, i.right);
		rowLayout.addRowType(TWO_COLUMN_INSPECTOR,
				new Cell[] { new Cell(0, .5f, columnInsets),
						new Cell(1, .5f, columnInsets) });

		for (int a = 0; a <= sectionCtr; a++) {
			currentSectionID = TWO_COLUMN_INSPECTOR + "." + a;
			rowLayout.addRowType(currentSectionID, new Cell[] {
					new Cell(0, .5f, columnInsets),
					new Cell(1, .5f, columnInsets) });
		}
	}

	public JPanel getPanel() {
		return rowLayout.getPanel();
	}

	public Header addHeader(String text, boolean collapsible) {
		Header header = new Header(text, collapsible);
		Insets insets = new Insets(0, 0, 0, 0);
		if (rowLayout.getRowCount() != 0) {
			insets.top = 5;
		}
		insets.bottom = 3;
		addRow(header, HorizontalAlignment.STRETCH, insets, 0);
		sectionCtr++;
		currentSectionID = TWO_COLUMN_INSPECTOR + "." + sectionCtr;
		rowLayout.addRowType(currentSectionID,
				new Cell[] { new Cell(0, .5f, columnInsets),
						new Cell(1, .5f, columnInsets) });
		return header;
	}

	/**
	 * Appends a new row containing only 1 object to this inspector. Note this
	 * component is stretched to fill the entire row; so the insets parameter
	 * can be used to help provide a more balanced appearance.
	 * 
	 * @param component
	 *            the component to add.
	 * @param alignment
	 *            one of the alignment enums: LEFT, CENTER, RIGHT, or STRETCH.
	 * @param insets
	 *            and optional set of additional insets to apply to the
	 *            component.
	 */
	public void addRow(JComponent component, HorizontalAlignment alignment,
			Insets insets, int verticalPriority) {
		ComponentCluster cluster = createAlignedCluster(component, alignment,
				insets, verticalPriority);
		rowLayout.addRow(cluster, SINGLE_COLUMN_NO_INSETS);
	}

	private ComponentCluster createAlignedCluster(JComponent component,
			HorizontalAlignment alignment, Insets insets, int verticalPriority) {
		if (HorizontalAlignment.STRETCH.equals(alignment)) {
			return new ComponentCluster(new JComponent[] { component },
					new ComponentConstraints[] { new ComponentConstraints(1,
							ComponentConstraints.HorizontalAlignment.LEFT,
							insets, verticalPriority) });
		} else if (HorizontalAlignment.LEFT.equals(alignment)) {
			return new ComponentCluster(
					new JComponent[] { component, null },
					new ComponentConstraints[] {
							new ComponentConstraints(
									0,
									ComponentConstraints.HorizontalAlignment.LEFT,
									insets, verticalPriority),
							new ComponentConstraints(
									1,
									ComponentConstraints.HorizontalAlignment.LEFT,
									null, 0) });
		} else if (HorizontalAlignment.RIGHT.equals(alignment)) {
			return new ComponentCluster(
					new JComponent[] { null, component },
					new ComponentConstraints[] {
							new ComponentConstraints(
									1,
									ComponentConstraints.HorizontalAlignment.RIGHT,
									null, 0),
							new ComponentConstraints(
									0,
									ComponentConstraints.HorizontalAlignment.RIGHT,
									insets, verticalPriority) });
		} else if (HorizontalAlignment.CENTER.equals(alignment)) {
			return new ComponentCluster(new JComponent[] { null, component,
					null },
					new ComponentConstraints[] {
							new ComponentConstraints(1, null, null, 0),
							new ComponentConstraints(0, null, insets,
									verticalPriority),
							new ComponentConstraints(1, null, null, 0) });
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Appends a new row containing 2 objects to this inspector.
	 * 
	 * The identifier is right-aligned, and the control's alignment is defined
	 * by the <code>alignment</code> argument.
	 * 
	 * @param identifier
	 *            the control on the left. This should usually contain text. A
	 *            <code>JLabel</code> or a <code>JCheckBox</code> is
	 *            recommended.
	 * @param control
	 *            any more complex control on the right.
	 * @param alignment
	 *            how to position the control.
	 * @param insets
	 *            an optional set of insets for the control.
	 */
	public void addRow(JComponent identifier, JComponent control,
			HorizontalAlignment alignment, Insets insets) {
		rowLayout.addRow(ComponentCluster.createRightAligned(identifier),
				createAlignedCluster(control, alignment, insets, 0),
				currentSectionID);
	}

	/**
	 * Appends a new row to this inspector.
	 * 
	 * @param identifier
	 *            the control on the left. This should usually contain text. A
	 *            <code>JLabel</code> or a <code>JCheckBox</code> is
	 *            recommended.
	 * @param controls
	 *            the controls to add to this row.
	 */
	public void addRow(JComponent identifier, ComponentCluster controls) {
		rowLayout.addRow(ComponentCluster.createRightAligned(identifier),
				controls, currentSectionID);
	}

	/**
	 * Appends a new row containing 3 objects to this inspector.
	 * 
	 * The identifier is right-aligned. The leftControl is left-aligned and
	 * occupies the remaining width, and the rightControl is right-aligned
	 * against the far right margin and is as small as possible.
	 * 
	 * @param identifier
	 *            the control on the left. This should usually contain text. A
	 *            <code>JLabel</code> or a <code>JCheckBox</code> is
	 *            recommended.
	 * @param leftControl
	 *            any other control.
	 * @param stretchToFill
	 *            whether the <code>leftControl</code> should stretch to fit the
	 *            remaining width.
	 * @param rightControl
	 *            the element to add on the right.
	 */
	public void addRow(JComponent identifier, JComponent leftControl,
			boolean stretchToFill, JComponent rightControl) {
		ComponentCluster rightCluster = null;

		JComponent[] rightClusterComps = new JComponent[] { leftControl,
				rightControl };
		if (stretchToFill) {
			rightCluster = new ComponentCluster(
					rightClusterComps,
					new ComponentConstraints[] {
							new ComponentConstraints(
									1,
									ComponentConstraints.HorizontalAlignment.LEFT,
									new Insets(0, 0, 0, 2), 0),
							new ComponentConstraints(
									0,
									ComponentConstraints.HorizontalAlignment.RIGHT,
									null, 0) });
		} else {
			rightCluster = new ComponentCluster(
					rightClusterComps,
					new ComponentConstraints[] {
							new ComponentConstraints(
									0,
									ComponentConstraints.HorizontalAlignment.LEFT,
									new Insets(0, 0, 0, 2), 0),
							new ComponentConstraints(
									0,
									ComponentConstraints.HorizontalAlignment.RIGHT,
									null, 0) });
		}

		rowLayout.addRow(ComponentCluster.createRightAligned(identifier),
				rightCluster, currentSectionID);

	}

	/**
	 * Appends a new separator to this inspector.
	 * 
	 */
	public JSeparator addSeparator() {
		JSeparator separator = new JSeparator();
		addRow(separator, HorizontalAlignment.STRETCH, null, 0);
		return separator;
	}

	/**
	 * Appends a gap to this inspector. All the rows should be their
	 * preferred/minimum height, but all vertical gaps will distribute the
	 * remaining vertical space evenly.
	 * 
	 */
	public void addGap() {
		Gap gap = new Gap();
		addRow(gap, HorizontalAlignment.STRETCH, null, 1);
	}

	public RowLayout getRowLayout() {
		return rowLayout;
	}

	/**
	 * Removes all elements from this inspector, usually so elements can be
	 * re-added.
	 */
	public void clear() {
		rowLayout.clear();
	}

	public boolean isHorizontalEdgePainted() {
		return horizontalEdgePainted;
	}

	public void setHorizontalEdgePainted(boolean b) {
		horizontalEdgePainted = b;
	}
}