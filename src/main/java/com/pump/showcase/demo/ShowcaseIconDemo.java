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
package com.pump.showcase.demo;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pump.icon.IconUtils;
import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.BytePixelIterator;
import com.pump.image.pixel.IntPixelIterator;
import com.pump.inspector.Inspector;
import com.pump.plaf.LabelCellRenderer;
import com.pump.swing.ContextualMenuHelper;
import com.pump.swing.ImageTransferable;
import com.pump.swing.popover.JPopover;
import com.pump.swing.popover.ListSelectionVisibility;
import com.pump.swing.popup.ListCellPopupTarget;
import com.pump.util.list.ObservableList;

/**
 * This shows a scrolling JList of icons. This includes a slider to resize the
 * icons.
 * <p>
 * Subclasses are responsible for generating a list of image IDs, and can
 * convert each image ID into a BufferedImage. This class will automatically
 * consolidate IDs that share identical BufferedImages into a single
 * ShowcaseIcon instance.
 */
public abstract class ShowcaseIconDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	private static final String ACTION_CLEAR_SELECTION = "clear-selection";

	/**
	 * The number of pixels around each icon in the list.
	 */
	private int ICON_PADDING = 3;

	static class ClearSelectionAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JList<?> list = (JList<?>) e.getSource();
			list.setSelectedIndices(new int[] {});
		}
	}

	static class CopyIconRunnable implements Runnable {
		BufferedImage img;

		public CopyIconRunnable(Icon icon) {
			img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = img.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
		}

		@Override
		public void run() {
			Transferable contents = new ImageTransferable(img);
			Toolkit.getDefaultToolkit().getSystemClipboard()
					.setContents(contents, null);
		}
	}

	class LoadImageRunnable implements Runnable {
		String id;
		Dimension defaultImageSize;

		public LoadImageRunnable(String id) {
			this.id = id;
			int k = getCellSize();
			defaultImageSize = new Dimension(k, k);
		}

		public void run() {
			BufferedImage img = getImage(id, defaultImageSize);
			img = padImage(img);
			add(id, img);
		}

		private BufferedImage padImage(BufferedImage img) {
			int z = getCellSize();
			if (img.getWidth() < z || img.getHeight() < z) {
				BufferedImage bi = new BufferedImage(z, z,
						BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = bi.createGraphics();
				g.drawImage(img, bi.getWidth() / 2 - img.getWidth() / 2,
						bi.getHeight() / 2 - img.getHeight() / 2, null);
				g.dispose();
				return bi;
			}
			return img;
		}

		private void add(String id, BufferedImage img) {
			for (ShowcaseIcon s : icons) {
				if (s.matchesImage(img)) {
					s.ids.add(id);
					return;
				}
			}
			icons.add(new ShowcaseIcon(img, id));
		}
	}

	/**
	 * One image and all the IDs that produce that image.
	 */
	protected class ShowcaseIcon {
		BufferedImage img;
		Collection<String> ids = new HashSet<>();
		Icon imgIcon;

		public ShowcaseIcon(BufferedImage img, String id) {
			Objects.requireNonNull(img);
			Objects.requireNonNull(id);
			this.img = img;
			ids.add(id);
		}

		public boolean matchesImage(BufferedImage other) {
			if (img.getType() != other.getType()
					|| img.getWidth() != other.getWidth()
					|| img.getHeight() != other.getHeight())
				return false;
			BufferedImageIterator i1 = BufferedImageIterator.get(img, true);
			BufferedImageIterator i2 = BufferedImageIterator.get(other, true);
			if (i1 instanceof BytePixelIterator) {
				BytePixelIterator b1 = (BytePixelIterator) i1;
				BytePixelIterator b2 = (BytePixelIterator) i2;
				byte[] row1 = new byte[b1.getMinimumArrayLength()];
				byte[] row2 = new byte[b2.getMinimumArrayLength()];
				while (!b1.isDone()) {
					b1.next(row1);
					b2.next(row2);
					if (!Arrays.equals(row1, row2))
						return false;
				}
			} else {
				IntPixelIterator b1 = (IntPixelIterator) i1;
				IntPixelIterator b2 = (IntPixelIterator) i2;
				int[] row1 = new int[b1.getMinimumArrayLength()];
				int[] row2 = new int[b2.getMinimumArrayLength()];
				while (!b1.isDone()) {
					b1.next(row1);
					b2.next(row2);
					if (!Arrays.equals(row1, row2))
						return false;
				}
			}
			return true;
		}

		public Icon getImageIcon(Dimension maxConstrainingSize) {
			if (imgIcon == null || img == null) {
				if (img == null)
					img = getImage(ids.iterator().next(), maxConstrainingSize);
				imgIcon = new ImageIcon(img);
				int j = maxConstrainingSize.width - imgIcon.getIconWidth();
				int k = maxConstrainingSize.height - imgIcon.getIconHeight();
				if (j >= 0 && k >= 0 && (j + k) > 0) {
					imgIcon = IconUtils.createPaddedIcon(imgIcon,
							maxConstrainingSize);
				}
			}
			return imgIcon;
		}
	}

	protected ObservableList<ShowcaseIcon> icons = new ObservableList<>();
	protected JList<ShowcaseIcon> list = new JList<>(
			icons.createUIMirror(null));
	boolean isShowing = false;
	Inspector inspector = new Inspector();

	public ShowcaseIconDemo() {
		setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		add(inspector.getPanel(), c);
		c.gridy++;
		c.weighty = 1;
		JScrollPane scrollPane = new JScrollPane(list);
		add(scrollPane, c);

		// use a small preferred size so the separate at the top
		// of the header is never pushed aside. The GridBagLayout
		// will make sure this scrollpane is alway stretched-to-fit
		scrollPane.setPreferredSize(new Dimension(10, 10));

		inspector.addRow(new JLabel("Icon Size:"), getSizeControl());

		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(0);

		list.setCellRenderer(new LabelCellRenderer<ShowcaseIcon>() {

			@Override
			protected void formatLabel(ShowcaseIcon showcaseIcon) {
				int z = getCellSize();
				Dimension maxConstrainingSize = new Dimension(z, z);
				Icon icon = showcaseIcon.getImageIcon(maxConstrainingSize);
				icon = IconUtils.createPaddedIcon(icon, new Dimension(
						z + ICON_PADDING * 2, z + ICON_PADDING * 2));
				label.setIcon(icon);
			}

		});

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.getSelectionModel()
				.addListSelectionListener(new ListSelectionListener() {
					JPopover popover;
					int currentIndex = -1;

					@Override
					public void valueChanged(ListSelectionEvent e) {
						int i = list.getSelectedIndex();
						if (i == currentIndex)
							return;

						currentIndex = i;
						if (popover != null)
							popover.dispose();

						ShowcaseIcon icon = list.getSelectedValue();
						if (icon != null) {
							popover = new JPopover(list,
									createPopupContents(icon), true);
							popover.setTarget(new ListCellPopupTarget(list, i));
							popover.setVisibility(
									new ListSelectionVisibility(list, icon));
						}
					}

				});

		KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		list.getInputMap().put(escapeKey, ACTION_CLEAR_SELECTION);
		list.getActionMap().put(ACTION_CLEAR_SELECTION,
				new ClearSelectionAction());

		new ContextualMenuHelper(list) {

			@Override
			protected void showPopup(Component c, int x, int y) {
				clear();
				int index = list.getUI().locationToIndex(list, new Point(x, y));
				if (index == -1)
					return;

				Rectangle r = list.getUI().getCellBounds(list, index, index);
				if (!r.contains(new Point(x, y)))
					return;

				ShowcaseIcon si = icons.get(index);
				int z = getCellSize();
				Icon icon = si.getImageIcon(new Dimension(z, z));
				add("Copy Image", new CopyIconRunnable(icon));
				super.showPopup(c, x, y);
			}

		};

		refreshCellSize();
	}

	/**
	 * The JComponent that controls {@link #getCellSize()}.
	 */
	protected abstract JComponent getSizeControl();

	protected void refreshCellSize() {
		int z = getCellSize();
		list.setFixedCellHeight(z + ICON_PADDING * 2);
		list.setFixedCellWidth(z + ICON_PADDING * 2);
		for (ShowcaseIcon i : icons) {
			i.img = null;
		}
		list.repaint();
	}

	/**
	 * Return the current dimensions of the icons in this demo.
	 */
	protected abstract int getCellSize();

	/**
	 * Create the JComponent that describes an icon. This will appear when the
	 * user clicks an icon in the JList. This is similar to a tooltip, but it
	 * will not disappear until the selection changes.
	 */
	protected abstract JComponent createPopupContents(ShowcaseIcon icon);

	/**
	 * Convert an ID into a BufferedImage.
	 * <p>
	 * It's OK if this method is expensive; the user will see a loading
	 * indicator until all images and loaded.
	 * 
	 * @param id
	 *            the ID of the image to produce.
	 * @param maxConstrainingSize
	 *            the maximum dimensions. Ideally the image should be scaled
	 *            proportionally to fit inside these dimensions.
	 */
	protected abstract BufferedImage getImage(String id,
			Dimension maxConstrainingSize);

	/**
	 * Return all the supported image IDs.
	 */
	protected abstract String[] getImageIDs();

	@SuppressWarnings("unchecked")
	public List<Runnable> getInitializationRunnables() {
		String[] allIDs = getImageIDs();

		List<Runnable> returnValue = new ArrayList<>(allIDs.length);
		for (String id : allIDs) {
			returnValue.add(new LoadImageRunnable(id));
		}
		return returnValue;
	}
}