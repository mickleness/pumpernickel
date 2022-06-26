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
package com.pump.plaf;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.pump.data.Key;
import com.pump.io.location.IOLocation;
import com.pump.job.JobManager;
import com.pump.job.JobRunnable;
import com.pump.plaf.decorate.DecoratedTreeUI;
import com.pump.swing.JThrobber;
import com.pump.swing.NavigationListener;
import com.pump.swing.ThrobberManager;
import com.pump.swing.io.LocationBreadCrumbs;
import com.pump.swing.io.LocationTreePane;

public class LocationTreePaneUI extends ComponentUI {

	static class LocationTreePaneTreeUI extends DecoratedTreeUI {

	}

	static class LocationCellRenderer extends
			LabelCellRenderer<DefaultMutableTreeNode> {

		@Override
		protected void formatLabel(DefaultMutableTreeNode value) {
			if (value.getUserObject() instanceof IOLocation) {
				IOLocation loc = (IOLocation) value.getUserObject();
				getLabel().setText(loc.getName());
				// TODO: preferably request the icon in another thread, use
				// default icons until it's ready
				Icon icon = loc.getIcon(null);
				getLabel().setIcon(icon);
			} else {
				getLabel().setText(String.valueOf(value.getUserObject()));
				getLabel().setIcon(null);
			}
		}
	}

	private static TreeNode createRootNode(IOLocation[] rootLocations) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		for (IOLocation loc : rootLocations) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(loc);
			root.add(node);
		}
		return root;
	}

	class LocationTreePaneTreeModel extends DefaultTreeModel {
		private static final long serialVersionUID = 1L;

		public LocationTreePaneTreeModel(IOLocation[] rootLocations) {
			super(createRootNode(rootLocations));
		}

	}

	/*
	 * enum LocationAttribute { LAST_MODIFIED() {
	 * 
	 * @Override public String get(IOLocation loc) { try { long date =
	 * loc.getModificationDate(); return
	 * DateFormat.getDateTimeInstance(DateFormat.SHORT,
	 * DateFormat.SHORT).format(new Date(date)); } catch (IOException e) {
	 * return ""; } } }, SIZE() {
	 * 
	 * @Override public String get(IOLocation loc) { try { return
	 * IOUtils.formatFileSize(loc.length()); } catch (IOException e) { return
	 * ""; } } };
	 * 
	 * public abstract String get(IOLocation loc); }
	 * 
	 * class Header extends JPanel { private static final long serialVersionUID
	 * = 1L;
	 * 
	 * public Header(LocationTreePane ltp) { // TODO: add other attributes
	 * add(new JLabel("Name")); }
	 * 
	 * }
	 */

	class Footer extends JPanel {
		private static final long serialVersionUID = 1L;

		LocationBreadCrumbs crumbs = new LocationBreadCrumbs();
		LocationTreePane ltp;
		JThrobber throbber = throbberManager.createThrobber();
		NavigationListener<IOLocation> navListener = new NavigationListener<IOLocation>() {

			public boolean elementsSelected(ListSelectionType type,
					IOLocation... elements) {
				if (type == ListSelectionType.DOUBLE_CLICK
						&& elements.length > 0) {
					for (int a = 0; a < ltp.getTree().getRowCount(); a++) {
						TreePath path = ltp.getTree().getPathForRow(a);
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
								.getLastPathComponent();
						if (elements[0].equals(node.getUserObject())) {
							ltp.getTree().setSelectionPath(path);
							return true;
						}
					}
				}
				return false;
			}
		};

		public Footer(LocationTreePane ltp) {
			setLayout(new GridBagLayout());
			this.ltp = ltp;
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = 0;
			c.weightx = 1;
			c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			c.insets = new Insets(2, 2, 2, 2);
			add(crumbs, c);
			c.gridx++;
			c.weightx = 0;
			add(throbber, c);

			crumbs.addNavigationListener(navListener);

			ltp.getTree().addTreeSelectionListener(new TreeSelectionListener() {

				@Override
				public void valueChanged(TreeSelectionEvent e) {
					TreePath path = e.getNewLeadSelectionPath();

					crumbs.removeNavigationListener(navListener);
					try {
						if (path != null) {
							crumbs.setVisible(true);
							List<IOLocation> locs = new LinkedList<>();
							for (int a = 0; a < path.getPathCount(); a++) {
								DefaultMutableTreeNode n = (DefaultMutableTreeNode) path
										.getPathComponent(a);
								if (n.getUserObject() instanceof IOLocation) {
									locs.add((IOLocation) n.getUserObject());
								}
							}

							crumbs.setPath(locs.toArray(new IOLocation[locs
									.size()]));
						} else {
							crumbs.setVisible(false);
							crumbs.setPath(new IOLocation[] {});
						}
					} finally {
						crumbs.addNavigationListener(navListener);
					}
				}

			});

		}

	}

	// public static final Key<Header> KEY_HEADER = new Key<>(Header.class,
	// LocationTreePaneUI.class.getName() + "#header");

	public static final Key<Footer> KEY_FOOTER = new Key<>(Footer.class,
			LocationTreePaneUI.class.getName() + "#footer");

	public static ComponentUI createUI(JComponent c) {
		return new LocationTreePaneUI();
	}

	Map<IOLocation, IOLocation[]> parentToChildren = new ConcurrentHashMap<>();
	JobManager jobManager = new JobManager(1);
	ThrobberManager throbberManager = new ThrobberManager(jobManager);

	class ListChildrenRunnable implements Runnable {

		DefaultMutableTreeNode node;
		LocationTreePaneTreeModel treeModel;
		boolean autoExpand;
		LocationTreePane ltp;
		Comparator<IOLocation> locComparator = new Comparator<IOLocation>() {

			@Override
			public int compare(IOLocation o1, IOLocation o2) {
				int i = o1.getName().compareToIgnoreCase(o2.getName());
				if (i != 0)
					return i;
				i = o1.getPath().compareToIgnoreCase(o2.getPath());
				if (i != 0)
					return i;
				return o1.getPath().compareTo(o2.getPath());
			}

		};

		public ListChildrenRunnable(LocationTreePane ltp,
				LocationTreePaneTreeModel treeModel,
				DefaultMutableTreeNode node, boolean autoExpand) {
			this.node = node;
			this.ltp = ltp;
			this.treeModel = treeModel;
			this.autoExpand = autoExpand;
		}

		@Override
		public void run() {
			IOLocation loc = (IOLocation) node.getUserObject();
			IOLocation[] children = parentToChildren.get(loc);
			if (children == null) {
				children = loc.listChildren(null, null);
				Arrays.sort(children, locComparator);
				parentToChildren.put(loc, children);

				final IOLocation[] c = children;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						for (int index = 0; index < c.length; index++) {
							DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(
									c[index]);
							treeModel.insertNodeInto(childNode,
									ListChildrenRunnable.this.node, index);
						}
						if (autoExpand) {
							TreePath path = getTreePath(node);
							ltp.getTree().expandPath(path);
						}
					}

					private TreePath getTreePath(TreeNode node) {
						List<TreeNode> nodes = new LinkedList<>();
						while (node != null) {
							nodes.add(0, node);
							node = node.getParent();
						}
						return new TreePath(nodes.toArray());
					}
				});
			}
		}

	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		final LocationTreePane ltp = (LocationTreePane) c;
		ltp.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.BOTH;
		// c.add(getHeader(ltp), gbc);

		JScrollPane scrollPane = new JScrollPane(ltp.getTree());
		gbc.gridy++;
		gbc.weighty = 1;
		c.add(scrollPane, gbc);

		gbc.gridy++;
		gbc.weighty = 0;
		c.add(getFooter(ltp), gbc);

		ltp.addPropertyChangeListener(LocationTreePane.KEY_ROOTS.getName(),
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						refreshTreeModel(ltp);
					}
				});

		ltp.getTree().setRootVisible(false);
		ltp.getTree().setCellRenderer(new LocationCellRenderer());
		ltp.getTree().setUI(new LocationTreePaneTreeUI());
		refreshTreeModel(ltp);
		ltp.getTree().addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				for (TreePath path : e.getPaths()) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
							.getLastPathComponent();
					Runnable r = new ListChildrenRunnable(ltp,
							(LocationTreePaneTreeModel) ltp.getTree()
									.getModel(), node, false);
					jobManager.addJob(new JobRunnable(r));
				}
			}

		});
	}

	private void refreshTreeModel(LocationTreePane ltp) {
		parentToChildren.clear();
		ltp.getTree().setModel(new LocationTreePaneTreeModel(ltp.getRoots()));
	}

	// private Component getHeader(LocationTreePane ltp) {
	// Header header = KEY_HEADER.getClientProperty(ltp);
	// if (header == null) {
	// header = new Header(ltp);
	// KEY_HEADER.putClientProperty(ltp, header);
	// }
	// return header;
	// }

	private Component getFooter(LocationTreePane ltp) {
		Footer footer = KEY_FOOTER.getClientProperty(ltp);
		if (footer == null) {
			footer = new Footer(ltp);
			KEY_FOOTER.putClientProperty(ltp, footer);
		}
		return footer;
	}

}