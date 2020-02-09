package com.pump.showcase;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.BytePixelIterator;
import com.pump.image.pixel.IntPixelIterator;
import com.pump.plaf.CircularProgressBarUI;
import com.pump.plaf.LabelCellRenderer;
import com.pump.swing.popover.JPopover;
import com.pump.swing.popover.ListSelectionVisibility;
import com.pump.util.list.ObservableList;

public abstract class ShowcaseIconDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	protected static class ShowcaseIcon {
		BufferedImage img;
		Collection<String> ids = new HashSet<>();

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

		ImageIcon imgIcon;

		public Icon getImageIcon() {
			if (imgIcon == null)
				imgIcon = new ImageIcon(img);
			return imgIcon;
		}
	}

	protected ObservableList<ShowcaseIcon> icons = new ObservableList<>();
	protected JList<ShowcaseIcon> list = new JList<>(icons.createUIMirror(null));
	protected Dimension maxConstrainingSize = new Dimension(48, 48);
	CardLayout cardLayout = new CardLayout();
	JPanel cardPanel = new JPanel(cardLayout);
	JProgressBar progressBar = new JProgressBar();
	boolean isShowing = false;

	public ShowcaseIconDemo() {
		progressBar.setUI(new CircularProgressBarUI());
		progressBar.setPreferredSize(new Dimension(90, 90));
		JPanel progressBarPanel = new JPanel();
		progressBarPanel.add(progressBar);
		cardPanel.add(progressBarPanel, "loading");
		cardPanel.add(new JScrollPane(list), "icons");
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(cardPanel, c);
		progressBar.setIndeterminate(true);

		Thread thread = new Thread("loading " + getClass().getSimpleName()) {

			class UpdateProgressRunnable implements Runnable {
				int value, max;

				public UpdateProgressRunnable(int value, int max) {
					this.value = value;
					this.max = max;
				}

				public void run() {
					progressBar.setIndeterminate(false);
					progressBar.getModel().setRangeProperties(value, 1, 0, max,
							true);
				}
			}

			public void run() {
				String[] ids = null;
				int i = 0;
				try {
					while (true) {
						waitForShowing();

						if (ids == null)
							ids = getImageIDs();
						if (i == ids.length)
							return;

						BufferedImage img = getImage(ids[i]);
						img = padImage(img);
						add(ids[i], img);
						i++;

						SwingUtilities.invokeLater(new UpdateProgressRunnable(
								i, ids.length));
					}
				} finally {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							cardLayout.show(cardPanel, "icons");
						}
					});
				}
			}

			private BufferedImage padImage(BufferedImage img) {
				if (img.getWidth() < maxConstrainingSize.width
						|| img.getHeight() < maxConstrainingSize.height) {
					BufferedImage bi = new BufferedImage(
							maxConstrainingSize.width,
							maxConstrainingSize.height,
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

			private void waitForShowing() {
				while (true) {
					synchronized (ShowcaseIconDemo.this) {
						if (isShowing)
							return;
						try {
							ShowcaseIconDemo.this.wait();
						} catch (InterruptedException e) {
							// do nothing
						}
					}
					Thread.yield();
				}
			}
		};
		thread.start();

		addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				synchronized (ShowcaseIconDemo.this) {
					boolean newIsShowing = isShowing();
					if (newIsShowing != isShowing) {
						synchronized (ShowcaseIconDemo.this) {
							isShowing = newIsShowing;
							ShowcaseIconDemo.this.notifyAll();
						}
					}
				}
			}

		});
		list.setFixedCellHeight(maxConstrainingSize.width);
		list.setFixedCellHeight(maxConstrainingSize.height);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(0);

		list.setCellRenderer(new LabelCellRenderer<ShowcaseIcon>() {

			@Override
			protected void formatLabel(ShowcaseIcon icon) {
				label.setIcon(icon.getImageIcon());
			}

		});

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					JPopover popover;

					@Override
					public void valueChanged(ListSelectionEvent e) {
						ShowcaseIcon icon = list.getSelectedValue();
						if (icon != null) {
							popover = new JPopover(list,
									createPopupContents(icon), true);
							// TODO: set target to cell bounds
							popover.setVisibility(new ListSelectionVisibility(
									list, icon));
						}
					}

					private JComponent createPopupContents(ShowcaseIcon icon) {
						return new JLabel(icon.ids.toString());
					}

				});
	}

	protected abstract BufferedImage getImage(String string);

	protected abstract String[] getImageIDs();
}