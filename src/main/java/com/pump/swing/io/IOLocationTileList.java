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
package com.pump.swing.io;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JToolTip;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.pump.io.location.IOLocation;
import com.pump.io.location.IOLocationFilter;
import com.pump.plaf.ThumbnailLabelUI;
import com.pump.swing.NavigationListener;

public class IOLocationTileList extends JList<IOLocation> {

	private static final long serialVersionUID = 1L;

	protected int TYPING_THRESHOLD = 750;
	protected KeyListener typingListener = new KeyAdapter() {
		StringBuffer sb = new StringBuffer();
		long lastType = -1;

		Timer purgeText = new Timer(50, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (System.currentTimeMillis() - lastType > TYPING_THRESHOLD) {
					sb.delete(0, sb.length());
					purgeText.stop();
				}
			}
		});

		@Override
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();
			if (Character.isDefined(c)) {
				sb.append(c);

				stringTyped(sb.toString());

				lastType = System.currentTimeMillis();
				purgeText.start();
				e.consume();
			}
		}
	};

	protected KeyListener commitKeyListener = new KeyAdapter() {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER
					|| e.getKeyCode() == KeyEvent.VK_ACCEPT) {
				fireIOLocationListListeners(
						NavigationListener.ListSelectionType.KEY,
						getSelectedLocations());
				e.consume();
			}
		}
	};

	protected void stringTyped(String s) {
		s = s.toLowerCase();

		ListModel m = getModel();
		int index = 0;
		synchronized (m) {
			while (index < m.getSize()) {
				IOLocation loc = (IOLocation) m.getElementAt(index);
				if (loc.getName().toLowerCase().startsWith(s)) {
					setSelectedIndex(index);
					return;
				}
				index++;
			}
		}
	}

	private class RepaintLocationRunnable implements Runnable {
		IOLocation loc;
		boolean isThumbnail;

		RepaintLocationRunnable(IOLocation l, boolean t) {
			loc = l;
			isThumbnail = t;
		}

		public void run() {
			repaint(loc, isThumbnail);
		}
	}

	protected void repaint(IOLocation loc, boolean thumbnail) {
		int size = getModel().getSize();
		for (int index = 0; index < size; index++) {
			if (getModel().getElementAt(index) == loc) {
				Rectangle bounds = getUI().getCellBounds(this, index, index);
				repaint(bounds);
				return;
			}
		}
	}

	// TODO: can this logic (copied and pasted from LocationBrowserUI be neatly
	// wrapped in one interfaced/abstract model?
	private PropertyChangeListener graphicListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(GraphicCache.THUMBNAIL_PROPERTY)) {
				IOLocation loc = (IOLocation) evt.getSource();
				SwingUtilities.invokeLater(new RepaintLocationRunnable(loc,
						true));
			} else if (evt.getPropertyName().equals(GraphicCache.ICON_PROPERTY)) {
				IOLocation loc = (IOLocation) evt.getSource();
				SwingUtilities.invokeLater(new RepaintLocationRunnable(loc,
						false));
			}
		}
	};

	IOLocationFilter filter = null;
	List<NavigationListener<IOLocation>> listeners = new ArrayList<NavigationListener<IOLocation>>();

	protected MouseListener commitMouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			/*
			 * Give other listeners a chance to react to this mouse event by
			 * making this an invoke-later runnable:
			 */
			class ClickRunnable implements Runnable {
				MouseEvent mouseEvent;

				ClickRunnable(MouseEvent e) {
					mouseEvent = e;
				}

				public void run() {
					setSelectedIndex(-1);
					if (mouseEvent.getClickCount() == 1) {
						fireIOLocationListListeners(
								NavigationListener.ListSelectionType.SINGLE_CLICK,
								getSelectedLocations());
					} else if (mouseEvent.getClickCount() == 2) {
						fireIOLocationListListeners(
								NavigationListener.ListSelectionType.DOUBLE_CLICK,
								getSelectedLocations());
					}
				}
			}

			SwingUtilities.invokeLater(new ClickRunnable(e));
		}
	};

	public IOLocationTileList() {
		super();
		initialize();
	}

	public IOLocationTileList(ListModel<IOLocation> dataModel) {
		super(dataModel);
		initialize();
	}

	public IOLocationTileList(IOLocation[] listData) {
		super(listData);
		initialize();
	}

	private void initialize() {
		JLabel label = new JLabel();
		label.setUI(new ThumbnailLabelUI());
		setCellRenderer(new BasicTileCellRenderer(new GraphicCache(), label));

		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setFixedCellWidth(100);
		setFixedCellHeight(100);
		setVisibleRowCount(-1);
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		addKeyListener(commitKeyListener);
		addKeyListener(typingListener);
		addMouseListener(commitMouseListener);
	}

	public IOLocationFilter getFilter() {
		return filter;
	}

	/**
	 * This filter may be used by renderers to show unsupported locations as
	 * disabled. (For example, see the BasicTileCellRenderer.)
	 * <p>
	 * Note this does not actually filter elements out of this list, for that
	 * you need to call: <code>IOLocationFilter.filter(srcList)</code>
	 * 
	 * @param filter
	 */
	public void setFilter(IOLocationFilter filter) {
		if (this.filter == filter)
			return;

		this.filter = filter;
		repaint();
	}

	public void addIOLocationListListener(
			NavigationListener<IOLocation> listener) {
		if (listeners.contains(listener))
			return;
		listeners.add(listener);
	}

	public void removeIOLocationListListener(
			NavigationListener<IOLocation> listener) {
		listeners.remove(listener);
	}

	protected void fireIOLocationListListeners(
			NavigationListener.ListSelectionType type, IOLocation... locations) {
		for (NavigationListener<IOLocation> listener : listeners) {
			try {
				if (listener.elementsSelected(type,
						Arrays.copyOf(locations, locations.length)))
					return;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setCellRenderer(ListCellRenderer cellRenderer) {
		ListCellRenderer oldRenderer = this.getCellRenderer();
		if (oldRenderer instanceof BasicTileCellRenderer) {
			BasicTileCellRenderer b = (BasicTileCellRenderer) oldRenderer;
			b.graphicCache.removePropertyChangeListener(graphicListener);
		}
		super.setCellRenderer(cellRenderer);
		if (cellRenderer instanceof BasicTileCellRenderer) {
			BasicTileCellRenderer b = (BasicTileCellRenderer) cellRenderer;
			b.graphicCache.addPropertyChangeListener(graphicListener);
		}
	}

	public IOLocation[] getSelectedLocations() {
		List<IOLocation> obj = getSelectedValuesList();
		IOLocation[] selection = new IOLocation[obj.size()];
		for (int a = 0; a < obj.size(); a++) {
			selection[a] = (IOLocation) obj.get(a);
		}
		return selection;
	}

	public static class BasicTileCellRenderer implements
			ListCellRenderer<IOLocation> {
		protected GraphicCache graphicCache;
		protected IOLocationFilter filter;
		protected JLabel thumbnail;

		public BasicTileCellRenderer(GraphicCache graphicCache, JLabel thumbnail) {
			if (graphicCache == null)
				graphicCache = new GraphicCache();
			this.graphicCache = graphicCache;
			this.thumbnail = thumbnail;
		}

		public GraphicCache getGraphicCache() {
			return graphicCache;
		}

		public JLabel getThumbnail() {
			return thumbnail;
		}

		public Component getListCellRendererComponent(JList list, IOLocation l,
				int index, boolean isSelected, boolean cellHasFocus) {
			String text = "";
			BufferedImage image;

			text = l.getName();
			image = getGraphicCache().requestThumbnail(l);
			if (image == null) {
				if (l.isDirectory()) {
					image = LocationPane.FOLDER_THUMBNAIL;
				} else {
					image = LocationPane.FILE_THUMBNAIL;
				}
			}

			IOLocationFilter f = null;
			if (list instanceof IOLocationTileList) {
				IOLocationTileList t = (IOLocationTileList) list;
				f = t.getFilter();
			}
			boolean enabled = f == null ? true : f.filter(l) != null;
			JLabel label = getThumbnail();
			label.setEnabled(enabled);
			label.setText(text);
			label.setIcon(new ImageIcon(image));
			label.putClientProperty("selected", new Boolean(isSelected));

			format(l, label);

			return label;
		}

		/**
		 * This lets subclasses format this label. The default implementation
		 * does nothing, but subclasses can override this to meddle with the
		 * text/icon/etc before the renderer returns this label.
		 */
		protected void format(IOLocation loc, JLabel label) {
		}
	}

	@Override
	public String getToolTipText(MouseEvent event) {
		if (event != null) {
			Point p = event.getPoint();
			int index = locationToIndex(p);
			if (index != -1) {
				IOLocation loc = (IOLocation) getModel().getElementAt(index);
				return loc.getName();
			}
		}
		return super.getToolTipText(event);
	}

	@Override
	public Point getToolTipLocation(MouseEvent event) {
		if (event != null) {
			Point p = event.getPoint();
			int index = locationToIndex(p);
			ListCellRenderer r = getCellRenderer();
			Rectangle cellBounds;

			if (index != -1 && (r instanceof BasicTileCellRenderer)
					&& (cellBounds = getCellBounds(index, index)) != null
					&& cellBounds.contains(p.x, p.y)) {
				String text = getToolTipText(event);
				JToolTip tip = new JToolTip();
				tip.setTipText(text);
				Dimension tipSize = tip.getPreferredSize();
				BasicTileCellRenderer btcr = (BasicTileCellRenderer) r;

				int yOffset = cellBounds.height / 2;
				if (btcr.thumbnail.getUI() instanceof ThumbnailLabelUI) {
					ThumbnailLabelUI ui = (ThumbnailLabelUI) btcr.thumbnail
							.getUI();
					yOffset = ui.getTextRect().y;
				}
				return new Point(cellBounds.x + cellBounds.width / 2
						- tipSize.width / 2, cellBounds.y + yOffset);
			}
		}
		return super.getToolTipLocation(event);
	}

	private int lastRows = -1;
	private int lastColumns = -1;

	/**
	 * This calculates the number of required rows based on a width and the
	 * fixed cell width. Then this calls <code>setVisibleRowCount(rows)</code>
	 * 
	 * @param width
	 */
	public void updateVisibleRowCount(int width) {
		int size = getModel().getSize();
		int columns = Math.max(1, width / getFixedCellWidth());
		int rows = (size - 1) / columns + 1;
		if (lastRows != rows || lastColumns != columns) {
			setPreferredSize(new Dimension(columns * getFixedCellWidth(), rows
					* getFixedCellHeight()));
			setVisibleRowCount(-1);
		}
		lastRows = rows;
		lastColumns = columns;
	}
}