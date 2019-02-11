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
package com.pump.diagram.plaf;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.diagram.BinaryRelationship;
import com.pump.diagram.Box;
import com.pump.diagram.BoxContainer;
import com.pump.diagram.Connector;
import com.pump.diagram.swing.BoxContainerPanel;
import com.pump.geom.ShapeUtils;
import com.pump.math.MutableInteger;
import com.pump.swing.JThrobber;
import com.pump.util.CombinationIterator;
import com.pump.util.ObservableList;
import com.pump.util.ObservableProperties.Key;

public class RightAngleBoxContainerPanelUI extends BoxContainerPanelUI {
	public static final Key<Point> KEY_JOIN1_CONTROL_POINT = new Key<>(
			"join-control-1", Point.class);
	public static final Key<Point> KEY_JOIN2_CONTROL_POINT = new Key<>(
			"join-control-2", Point.class);
	public static final Key<Boolean> KEY_STAGING_DIRTY = new Key<>(
			"staging-dirty", Boolean.class);

	private static final Comparator<Join> JOIN_COMPARATOR = new Comparator<Join>() {

		@Override
		public int compare(Join o1, Join o2) {
			if (!o1.boxEdge.equals(o2.boxEdge))
				throw new RuntimeException(
						"this comparator requires joins share the same box edge");
			// what box does this join have to connect to:
			Box b1 = o1.first ? o1.connector.getBox2() : o1.connector.getBox1();
			Box b2 = o2.first ? o2.connector.getBox2() : o2.connector.getBox1();
			Point p1 = b1.getCenter();
			Point p2 = b2.getCenter();
			if (o1.boxEdge.side.isLeftOrRight()) {
				if (p1.y < p2.y) {
					return -1;
				} else if (p1.y > p2.y) {
					return 1;
				}
			} else {
				if (p1.x < p2.x) {
					return -1;
				} else if (p1.x > p2.x) {
					return 1;
				}
			}
			long id1 = b1.getID();
			long id2 = b2.getID();
			if (id1 < id2) {
				return -1;
			} else if (id1 > id2) {
				return 1;
			}
			return 0;
		}

	};

	static class BoxEdge {
		Box box;
		Side side;
		transient Join[] orderedJoins;
		transient Point[] anchors;

		public BoxEdge(Box box, Side side) {
			if (box == null)
				throw new NullPointerException();
			if (side == null)
				throw new NullPointerException();
			this.box = box;
			this.side = side;
		}

		public BoxEdge(BoxEdge edge) {
			this.box = edge.box;
			this.side = edge.side;
			this.orderedJoins = edge.orderedJoins;
			this.anchors = edge.anchors;
		}

		public Point[] getAnchors(boolean forceRegeneration) {
			if (anchors == null || forceRegeneration) {
				anchors = new Point[orderedJoins.length];
				Rectangle rect = box.getBounds();
				if (side.isLeftOrRight) {
					int span = rect.height;
					int x = side == Side.LEFT ? rect.x : rect.x + rect.width;
					for (int i = 0; i < orderedJoins.length; i++) {
						int y = rect.y + (i + 1) * span
								/ (orderedJoins.length + 1);
						anchors[i] = new Point(x, y);
					}
				} else {
					int span = rect.width;
					int y = side == Side.DOWN ? rect.y + rect.height : rect.y;
					for (int i = 0; i < orderedJoins.length; i++) {
						int x = rect.x + (i + 1) * span
								/ (orderedJoins.length + 1);
						anchors[i] = new Point(x, y);
					}
				}
			}
			return anchors;
		}

		@Override
		public int hashCode() {
			return box.hashCode() + side.hashCode();
		}

		@Override
		public String toString() {
			return side.toString() + " of " + box.getBounds() + " ("
					+ box.getID() + ")";
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof BoxEdge))
				return false;
			BoxEdge other = (BoxEdge) obj;
			return equals(other, true);
		}

		protected boolean equals(BoxEdge other, boolean compareOrderedJoins) {
			if (other.box != box)
				return false;
			if (other.side != side)
				return false;
			if (compareOrderedJoins) {
				if (orderedJoins != null && other.orderedJoins != null) {
					if (orderedJoins.length != other.orderedJoins.length) {
						return false;
					} else {
						for (int a = 0; a < orderedJoins.length; a++) {
							if (orderedJoins[a] != other.orderedJoins[a]) {
								return false;
							}
						}
					}
				} else if (orderedJoins != null || other.orderedJoins != null) {
					return false;
				}
			}
			return true;
		}

		public Join[] getJoins() {
			return orderedJoins;
		}
	}

	protected int scenarioIndex = 0;

	@Override
	public void paint(Graphics g0, JComponent c) {
		BoxContainerPanel bcp = (BoxContainerPanel) c;
		Boolean dirty = getContext(bcp).get(KEY_STAGING_DIRTY);
		if (dirty == null || dirty.booleanValue())
			restage(bcp);

		super.paint(g0, c);

		Graphics2D g = (Graphics2D) g0.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Staging staging = getContext(bcp).get(KEY_STAGING);
		if (staging == null || staging.allScenarios.size() == 0)
			return;

		Scenario scenario;
		int i;
		synchronized (staging) {
			i = scenarioIndex % staging.allScenarios.size();
			Iterator<Scenario> iter = staging.allScenarios.iterator();
			scenario = iter.next();
			for (int a = 0; a < i; a++) {
				scenario = iter.next();
			}
		}

		g.setColor(Color.DARK_GRAY);
		g.setStroke(new BasicStroke(1));
		for (Shape shape : scenario.getShapes(false)) {
			GeneralPath[] subpaths = ShapeUtils.getSubPaths(shape);
			for (GeneralPath subpath : subpaths) {
				if (ShapeUtils.isClosed(subpath)) {
					g.fill(subpath);
				} else {
					g.draw(subpath);
				}
			}
		}

		long current = System.currentTimeMillis();
		synchronized (previousScenarios) {
			previousScenarios.put(scenario, current);
			Iterator<Scenario> iter = previousScenarios.keySet().iterator();
			while (iter.hasNext()) {
				Scenario oldScenario = iter.next();
				if (oldScenario != scenario) {
					long elapsed = current - previousScenarios.get(oldScenario);
					float opacity = 1f - ((float) elapsed) / 200f;
					if (opacity < 0) {
						iter.remove();
					} else {
						g.setComposite(AlphaComposite.getInstance(
								AlphaComposite.SRC_OVER, opacity));

						g.setStroke(new BasicStroke(3 * (1 - opacity * opacity
								* opacity * opacity)));
						for (Shape shape : oldScenario.getShapes(true)) {
							g.draw(shape);
						}
					}
				}
			}
			if (previousScenarios.size() > 1)
				bcp.repaint();
		}

		if (staging.debugging) {
			g.setColor(Color.red);
			g.drawString("" + i + " / " + staging.allScenarios.size() + " ("
					+ scenarioIndex + ")", 5, 20);
			g.drawString(scenario.toString(), 5, 40);
			int y = 60;
			for (BoxEdge edge : scenario.allEdges) {
				Join[] joins = edge.getJoins();
				for (int a = 0; a < joins.length; a++) {
					g.drawString(joins[a].boxEdge.box.getID() + ": "
							+ joins[a].boxEdge.side, 5, y);
					y += 14;
				}
			}
			g.dispose();
		}
	}

	Map<Scenario, Long> previousScenarios = new HashMap<>();

	@Override
	protected void paintConnector(Graphics2D g, BoxContainerPanel bcp,
			Connector connector) {
		// do nothing here
	}

	boolean debugScenarios = false;

	KeyListener keyListener = new KeyAdapter() {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_D && e.isAltDown()) {
				debugScenarios = !debugScenarios;
				e.getComponent().repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				scenarioIndex--;
				e.getComponent().repaint();
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				scenarioIndex++;
				e.getComponent().repaint();
			}
		}
	};

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setFocusable(true);
		c.addKeyListener(keyListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removeKeyListener(keyListener);
	}

	enum Side {
		DOWN(false) {
			@Override
			public Point[] getLine(Rectangle rect) {
				return new Point[] { new Point(rect.x, rect.y + rect.height),
						new Point(rect.x + rect.width, rect.y + rect.height) };
			}
		},
		LEFT(true) {
			@Override
			public Point[] getLine(Rectangle rect) {
				return new Point[] { new Point(rect.x, rect.y),
						new Point(rect.x, rect.y + rect.height) };
			}

		},
		UP(false) {
			@Override
			public Point[] getLine(Rectangle rect) {
				return new Point[] { new Point(rect.x, rect.y),
						new Point(rect.x + rect.width, rect.y) };
			}
		},
		RIGHT(true) {

			@Override
			public Point[] getLine(Rectangle rect) {
				return new Point[] { new Point(rect.x + rect.width, rect.y),
						new Point(rect.x + rect.width, rect.y + rect.height) };
			}
		};

		boolean isLeftOrRight;

		Side(boolean isLeftOrRight) {
			this.isLeftOrRight = isLeftOrRight;
		}

		public boolean isLeftOrRight() {
			return isLeftOrRight;
		}

		public abstract Point[] getLine(Rectangle rect);
	}

	static class Join {
		BoxEdge boxEdge;
		Connector connector;
		boolean first;

		transient int index = -1;

		public Join(Box box, Side side, Connector connector, boolean first) {
			this(new BoxEdge(box, side), connector, first);
		}

		/**
		 * @param boxEdge
		 *            the side of the box this join originates from.
		 * @param connector
		 * @param first
		 *            if true this refers to box1 of a Connector, if false it
		 *            refers to box2
		 */
		public Join(BoxEdge boxEdge, Connector connector, boolean first) {
			this.boxEdge = boxEdge;
			this.connector = connector;
			this.first = first;
		}

		public Join(Join join) {
			this.boxEdge = join.boxEdge;
			this.connector = join.connector;
			this.first = join.first;
			this.index = join.index;
		}

		@Override
		public String toString() {
			return "Join[ edge=" + boxEdge + ", first=" + first
					+ ", connector=" + connector + "]";
		}
	}

	static final Key<ExecutorService> KEY_EXECUTOR_SERVICE = new Key<>(
			"executorService", ExecutorService.class);

	static class Staging implements Runnable {
		static int THREAD_COUNT = 4;
		CombinationIterator<Join> joinComboIter;
		BoxContainer boxContainer;
		Scenario bestScenario;
		Collection<Scenario> allScenarios = new TreeSet<>();
		BoxContainerPanel boxContainerPanel;
		boolean debugging;
		List<ChangeListener> changeListeners = new ArrayList<>();
		boolean dirty = true;
		boolean cancelled = false;
		boolean finished = false;
		BoxContainerPanelUI ui;
		int finishedThreads = 0;
		Runnable fireChangeListeners = new Runnable() {
			public void run() {
				synchronized (changeListeners) {
					synchronized (Staging.this) {
						if (!dirty)
							return;
						dirty = false;
					}
					for (ChangeListener changeListener : changeListeners) {
						changeListener.stateChanged(new ChangeEvent(
								Staging.this));
					}
				}
			}
		};

		public Staging(BoxContainerPanelUI ui, PlafContext context,
				BoxContainerPanel bcp, boolean debugging) {
			this.debugging = debugging;
			this.boxContainerPanel = bcp;
			this.ui = ui;
			boxContainer = bcp.getBoxContainer();
			if (boxContainer != null) {
				ObservableList<Connector> connectors = boxContainer
						.getConnectors();

				List<Join[]> possibleJoins = new ArrayList<>();
				for (Connector connector : connectors) {
					boolean b1 = boxContainer.getBoxes().contains(
							connector.getBox1());
					boolean b2 = boxContainer.getBoxes().contains(
							connector.getBox2());
					if (b1 && b2) {
						Join[] join1 = calculatePossibleJoins(connector, true);
						Join[] join2 = calculatePossibleJoins(connector, false);
						if (join1.length > 0 && join2.length > 0) {
							possibleJoins.add(join1);
							possibleJoins.add(join2);
						}
					}
				}
				joinComboIter = new CombinationIterator<>(possibleJoins);
			}

			ExecutorService service;
			synchronized (bcp) {
				service = context.get(KEY_EXECUTOR_SERVICE);
				if (service == null) {
					service = Executors.newFixedThreadPool(THREAD_COUNT);
					context.set(KEY_EXECUTOR_SERVICE, service);
				}
			}
			for (int a = 0; a < THREAD_COUNT; a++) {
				service.submit(this);
			}
		}

		private List<Join> nextJoins() {
			if (!cancelled) {
				synchronized (this) {
					if (joinComboIter.hasNext())
						return joinComboIter.next();
				}
			}
			return null;
		}

		@Override
		public void run() {
			try {
				if (cancelled)
					return;
				while (true) {
					List<Join> joins = nextJoins();
					if (joins == null)
						return;

					Map<BoxEdge, SortedSet<Join>> edgesToJoins = new HashMap<>();
					for (Join join : joins) {
						SortedSet<Join> allJoinsForEdge = edgesToJoins
								.get(join.boxEdge);
						if (allJoinsForEdge == null) {
							allJoinsForEdge = new TreeSet<>(JOIN_COMPARATOR);
							edgesToJoins.put(join.boxEdge, allJoinsForEdge);
						}
						allJoinsForEdge.add(join);
					}

					List<BoxEdge> boxEdges = new ArrayList<>();
					for (BoxEdge edge : edgesToJoins.keySet()) {
						Set<Join> localJoins = edgesToJoins.get(edge);
						Join[] joinArray = localJoins
								.toArray(new Join[localJoins.size()]);
						BoxEdge copy = new BoxEdge(edge);
						copy.orderedJoins = joinArray;
						copy.anchors = null;
						boxEdges.add(copy);
					}

					Scenario thisScenario = new Scenario(ui,
							boxContainer.getBoxes(), boxEdges);
					submitScenario(thisScenario);
				}
			} finally {
				synchronized (this) {
					finishedThreads++;
					if (finishedThreads == THREAD_COUNT) {
						finished = true;
						dirty = true;
						SwingUtilities.invokeLater(fireChangeListeners);
					}
				}
			}
		}

		public void addChangeListener(ChangeListener l) {
			synchronized (changeListeners) {
				changeListeners.add(l);
			}
		}

		public void removeChangeListener(ChangeListener l) {
			synchronized (changeListeners) {
				changeListeners.remove(l);
			}
		}

		private void submitScenario(Scenario newScenario) {
			boolean change = false;
			synchronized (this) {
				if (bestScenario == null
						|| newScenario.compareTo(bestScenario) < 0) {
					bestScenario = newScenario;
					allScenarios.clear();
					allScenarios.add(bestScenario);
					change = true;
				} else if (debugging) {
					allScenarios.add(newScenario);
					change = true;
				}
				if (change) {
					dirty = true;
					SwingUtilities.invokeLater(fireChangeListeners);
				}
			}
		}

		public void cancel() {
			cancelled = true;
		}
	}

	private static final Key<Staging> KEY_STAGING = new Key<>("staging",
			Staging.class);
	JThrobber throbber = new JThrobber();

	ChangeListener stagingChangeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			Staging staging = (Staging) e.getSource();
			synchronized (staging.boxContainerPanel) {
				staging.boxContainerPanel.repaint();

				PlafContext currentContext = getContext(staging.boxContainerPanel);
				Staging currentStaging = currentContext.get(KEY_STAGING);
				if (currentStaging == staging && staging.finished) {
					staging.boxContainerPanel.remove(throbber);
				}
			}
		}
	};

	protected void restage(BoxContainerPanel bcp) {
		try {
			synchronized (bcp) {
				PlafContext context = getContext(bcp);
				Staging oldStaging = context.get(KEY_STAGING);
				if (oldStaging != null) {
					oldStaging.removeChangeListener(stagingChangeListener);
					oldStaging.cancel();
				}
				Staging newStaging = new Staging(this, context, bcp,
						debugScenarios);
				newStaging.addChangeListener(stagingChangeListener);
				context.set(KEY_STAGING, newStaging);
				bcp.repaint();

				/*
				 * bcp.setLayout(new GridBagLayout()); GridBagConstraints c =
				 * new GridBagConstraints(); c.insets = new Insets(5,5,5,5);
				 * c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
				 * c.anchor = GridBagConstraints.SOUTHEAST; bcp.add(throbber,
				 * c);
				 */
			}
		} finally {
			getContext(bcp).set(KEY_STAGING_DIRTY, false);
		}
	}

	static class Scenario implements Comparable<Scenario> {
		static long idCtr = 0;
		long id = idCtr++;
		List<BoxEdge> allEdges;

		/**
		 * The first criteria: how many connectors overlap boxes? (measured in
		 * pixels)
		 */
		static final int SCORE_INDEX_PIXELS_CONNECTORS_OVERLAPPING_BOXES = 0;

		/**
		 * The second criteria: how many connectors overlap other connectors?
		 * (measured in pixels)
		 */
		static final int SCORE_INDEX_CONNECTORS_OVERLAPPING_CONNECTORS = 1;

		/** The third criteria: how many connector intersections are there? */
		static final int SCORE_INDEX_CONNECTOR_INTERSECTION_COUNT = 2;

		/** The fourth criteria: emphasizes edges with fewer joins. */
		static final int SCORE_INDEX_CROWDED_JOINS = 3;

		/** The fifth criteria: the total distance of all connectors */
		static final int SCORE_INDEX_TOTAL_DISTANCE = 4;

		MutableInteger[] score = new MutableInteger[5];

		Map<Connector, GeneralPath> connectorShapes = new HashMap<>();
		List<Box> allBoxes = null;

		private final static Point scratch1 = new Point();
		private final static Point scratch2 = new Point();
		private final static Point scratch3 = new Point();
		private final static Point scratch4 = new Point();

		private BoxContainerPanelUI ui;

		Scenario(BoxContainerPanelUI ui, List<Box> allBoxes,
				List<BoxEdge> allEdges) {
			this.allBoxes = allBoxes;
			this.allEdges = allEdges;
			this.ui = ui;
		}

		private int getScore(int index) {
			if (score[index] != null)
				return score[index].value;

			MutableInteger newScore = new MutableInteger();
			synchronized (this) {
				score[index] = newScore;

				switch (index) {
				case SCORE_INDEX_PIXELS_CONNECTORS_OVERLAPPING_BOXES:
					for (int a = 0; a < allEdges.size(); a++) {
						BoxEdge edge = allEdges.get(a);
						Join[] joins = edge.getJoins();
						Point[] anchors = edge.getAnchors(false);
						for (int i = 0; i < joins.length; i++) {
							Point p1 = anchors[i];
							Point p3 = joins[i].connector
									.getControlPoint(scratch1);
							Point p2 = scratch2;
							if (edge.side.isLeftOrRight) {
								p2.x = p3.x;
								p2.y = p1.y;
							} else {
								p2.x = p1.x;
								p2.y = p3.y;
							}

							for (Box box : allBoxes) {
								Rectangle rect = box.getBounds();
								newScore.value += getOverlap(rect, p1, p2);
								newScore.value += getOverlap(rect, p2, p3);
							}
						}
					}
					break;
				case SCORE_INDEX_CONNECTORS_OVERLAPPING_CONNECTORS:
					for (int a = 0; a < allEdges.size(); a++) {
						BoxEdge edge = allEdges.get(a);
						Join[] joins = edge.getJoins();
						Point[] anchors = edge.getAnchors(false);
						for (int i = 0; i < joins.length; i++) {
							Point p1 = anchors[i];
							Point p3 = joins[i].connector
									.getControlPoint(scratch1);
							Point p2 = scratch2;
							if (edge.side.isLeftOrRight) {
								p2.x = p3.x;
								p2.y = p1.y;
							} else {
								p2.x = p1.x;
								p2.y = p3.y;
							}

							for (int b = a; b < allEdges.size(); b++) {
								BoxEdge otherEdge = allEdges.get(b);
								Join[] otherJoins = otherEdge.getJoins();
								Point[] otherAnchors = otherEdge
										.getAnchors(false);
								for (int j = 0; j < otherJoins.length; j++) {
									if (otherJoins[j].connector != joins[i].connector) {
										Point p1b = otherAnchors[j];
										Point p3b = otherJoins[j].connector
												.getControlPoint(scratch3);
										Point p2b = scratch4;
										if (otherJoins[j].boxEdge.side.isLeftOrRight) {
											p2b.x = p3b.x;
											p2b.y = p1b.y;
										} else {
											p2b.x = p1b.x;
											p2b.y = p3b.y;
										}

										newScore.value += getOverlap(p1, p2,
												p1b, p2b);
										newScore.value += getOverlap(p1, p2,
												p2b, p3b);
										newScore.value += getOverlap(p2, p3,
												p1b, p2b);
										newScore.value += getOverlap(p2, p3,
												p2b, p3b);
									}
								}
							}
						}
					}
					break;
				case SCORE_INDEX_CONNECTOR_INTERSECTION_COUNT:
					for (int a = 0; a < allEdges.size(); a++) {
						BoxEdge edge = allEdges.get(a);
						Join[] joins = edge.getJoins();
						Point[] anchors = edge.getAnchors(false);
						for (int i = 0; i < joins.length; i++) {
							Point p1 = anchors[i];
							Point p3 = joins[i].connector
									.getControlPoint(scratch1);
							Point p2 = scratch2;
							if (edge.side.isLeftOrRight) {
								p2.x = p3.x;
								p2.y = p1.y;
							} else {
								p2.x = p1.x;
								p2.y = p3.y;
							}

							for (int b = a; b < allEdges.size(); b++) {
								BoxEdge otherEdge = allEdges.get(b);
								Join[] otherJoins = otherEdge.getJoins();
								Point[] otherAnchors = otherEdge
										.getAnchors(false);
								for (int j = 0; j < otherJoins.length; j++) {
									if (otherJoins[j].connector != joins[i].connector) {
										Point p1b = otherAnchors[j];
										Point p3b = otherJoins[j].connector
												.getControlPoint(scratch3);
										Point p2b = scratch4;
										if (otherJoins[j].boxEdge.side.isLeftOrRight) {
											p2b.x = p3b.x;
											p2b.y = p1b.y;
										} else {
											p2b.x = p1b.x;
											p2b.y = p3b.y;
										}

										newScore.value += isIntersection(p1,
												p2, p1b, p2b) ? 1 : 0;
										newScore.value += isIntersection(p1,
												p2, p2b, p3b) ? 1 : 0;
										newScore.value += isIntersection(p2,
												p3, p1b, p2b) ? 1 : 0;
										newScore.value += isIntersection(p2,
												p3, p2b, p3b) ? 1 : 0;
									}
								}
							}
						}
					}
					break;
				case SCORE_INDEX_CROWDED_JOINS:
					for (int a = 0; a < allEdges.size(); a++) {
						BoxEdge edge = allEdges.get(a);
						Join[] joins = edge.getJoins();
						newScore.value += joins.length - 1;
					}
					break;
				case SCORE_INDEX_TOTAL_DISTANCE:
					for (int a = 0; a < allEdges.size(); a++) {
						BoxEdge edge = allEdges.get(a);
						Join[] joins = edge.getJoins();
						Point[] anchors = edge.getAnchors(false);
						for (int i = 0; i < joins.length; i++) {
							Point p1 = anchors[i];
							Point p3 = joins[i].connector
									.getControlPoint(scratch1);
							Point p2 = scratch2;
							if (edge.side.isLeftOrRight) {
								p2.x = p3.x;
								p2.y = p1.y;
							} else {
								p2.x = p1.x;
								p2.y = p3.y;
							}

							newScore.value += distance(p1, p2);
							newScore.value += distance(p2, p3);
						}
					}
					break;
				}
			}
			return newScore.value;
		}

		private static int distance(Point p1, Point p2) {
			boolean horiz = p1.y == p2.y;
			int v;
			if (horiz) {
				v = p2.x - p1.x;
			} else {
				v = p2.y - p1.y;
			}
			if (v < 0)
				v = -v;
			return v;
		}

		private static boolean isIntersection(Point p1, Point p2, Point p1b,
				Point p2b) {

			boolean h1 = p1.y == p2.y;
			boolean h2 = p1b.y == p2b.y;

			if (h1 == h2)
				return false;

			int minX, maxX, minY, maxY, otherX, otherY;
			if (h1) {
				minY = Math.min(p1b.y, p2b.y);
				maxY = Math.max(p1b.y, p2b.y);
				otherX = p1b.x;
				minX = Math.min(p1.x, p2.x);
				maxX = Math.max(p1.x, p2.x);
				otherY = p1.y;
			} else {
				minX = Math.min(p1b.x, p2b.x);
				maxX = Math.max(p1b.x, p2b.x);
				otherY = p1b.y;
				minY = Math.min(p1.y, p2.y);
				maxY = Math.max(p1.y, p2.y);
				otherX = p1.x;
			}

			if (minX <= otherX && otherX <= maxX && minY <= otherY
					&& otherY <= maxY)
				return true;
			return false;
		}

		private static int getOverlap(Point p1, Point p2, Point p1b, Point p2b) {
			// TODO Auto-generated method stub
			return 0;
		}

		/**
		 * Calculate how many pixels overlap between the Rectangle and line
		 * segment.
		 * 
		 */
		private static int getOverlap(Rectangle rect, Point p1, Point p2) {
			boolean horiz = p1.y == p2.y;
			int pMin, pMax, rMin, rMax;
			if (horiz) {
				if (p1.y < rect.y || p1.y > rect.y + rect.height) {
					return 0;
				}
				pMin = Math.min(p1.x, p2.x);
				pMax = Math.max(p1.x, p2.x);
				rMin = Math.min(rect.x, rect.x + rect.width);
				rMax = Math.max(rect.x, rect.x + rect.width);
			} else {
				if (p1.x < rect.x || p1.x > rect.x + rect.height) {
					return 0;
				}
				pMin = Math.min(p1.y, p2.y);
				pMax = Math.max(p1.y, p2.y);
				rMin = Math.min(rect.y, rect.y + rect.height);
				rMax = Math.max(rect.y, rect.y + rect.height);
			}
			if (pMax < rMin || pMin > rMax)
				return 0;
			if (pMin < rMin && pMax <= rMax)
				return pMax - rMin;
			if (rMin <= pMin && pMax <= rMax)
				return pMax - pMin;
			return rMax - pMin;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for (int a = 0; a < score.length; a++) {
				if (a != 0)
					sb.append(".");
				sb.append(score[a]);
			}
			return sb.toString();
		}

		@Override
		public int compareTo(Scenario other) {
			for (int a = 0; a < score.length; a++) {
				int myValue = getScore(a);
				int otherValue = other.getScore(a);
				if (myValue < otherValue) {
					return -1;
				} else if (myValue > otherValue) {
					return 1;
				}
			}
			if (id < other.id)
				return -1;
			if (id > other.id)
				return 1;
			return 0;
		}

		public Collection<? extends Shape> getShapes(boolean forceRegeneration) {
			if (connectorShapes.size() == 0 || forceRegeneration) {
				connectorShapes.clear();
				Map<Connector, List<Point>> pointMap = new HashMap<>();
				for (BoxEdge edge : allEdges) {
					Join[] joins = edge.getJoins();
					Point[] anchors = edge.getAnchors(forceRegeneration);
					for (int a = 0; a < joins.length; a++) {
						Point center = joins[a].connector
								.getControlPoint(false);
						Point joinPoint;
						if (!edge.side.isLeftOrRight()) {
							joinPoint = new Point(anchors[a].x, center.y);
						} else {
							joinPoint = new Point(center.x, anchors[a].y);
						}
						List<Point> points = pointMap.get(joins[a].connector);
						if (points == null) {
							points = new ArrayList<>();
							pointMap.put(joins[a].connector, points);
							points.add(anchors[a]);
							points.add(joinPoint);
						} else {
							Point last = points.get(points.size() - 1);
							if (last.x != joinPoint.x && last.y != joinPoint.y) {
								points.add(center);
							}
							points.add(joinPoint);
							points.add(anchors[a]);

							// make sure they're ordered so we get the
							// decoration correct
							if (!joins[a].first)
								Collections.reverse(points);
						}
					}
				}

				int curveRadius = 15;
				for (Connector connector : pointMap.keySet()) {
					GeneralPath path = new GeneralPath();
					List<Point> points = pointMap.get(connector);
					path.moveTo(points.get(0).x, points.get(0).y);
					for (int a = 1; a < points.size(); a++) {
						Point current = points.get(a);
						if (a != points.size() - 1) {
							Point prev = points.get(a - 1);
							Point next = points.get(a + 1);
							double d1 = prev.distance(current);
							double d2 = next.distance(current);

							double myRadius1 = Math.min(d1 / 2, curveRadius);
							double myRadius2 = Math.min(d2 / 2, curveRadius);

							Point2D p1 = midway(prev, current,
									prev.distance(current) - myRadius1);
							Point2D p1c = midway(prev, current,
									prev.distance(current) - myRadius1 * 1 / 4);
							Point2D p2c = midway(current, next,
									myRadius2 * 1 / 4);
							Point2D p2 = midway(current, next, myRadius2);
							path.lineTo(p1.getX(), p1.getY());
							path.curveTo(p1c.getX(), p1c.getY(), p2c.getX(),
									p2c.getY(), p2.getX(), p2.getY());
						} else {
							path.lineTo(current.x, current.y);
						}
					}

					BinaryRelationship relationship = connector
							.getRelationship();
					if (ui != null) {
						ui.addConnectorDecoration(path,
								relationship.relationshipSideA, points.get(0),
								points.get(1));
						ui.addConnectorDecoration(path,
								relationship.relationshipSideB,
								points.get(points.size() - 1),
								points.get(points.size() - 2));
					} else {
						relationship.relationshipSideA.appendDecoration(path,
								points.get(0), points.get(1));
						relationship.relationshipSideB.appendDecoration(path,
								points.get(points.size() - 1),
								points.get(points.size() - 2));
					}

					connectorShapes.put(connector, path);
				}
			}
			return connectorShapes.values();
		}

		private Point2D midway(Point2D p1, Point2D p2, double distance) {
			double theta = Math.atan2(p2.getY() - p1.getY(),
					p2.getX() - p1.getX());
			double maxDistance = p1.distance(p2);
			if (distance < 0)
				distance = 0;
			if (distance > maxDistance)
				distance = maxDistance;
			return new Point2D.Double(p1.getX() + distance * Math.cos(theta),
					p1.getY() + distance * Math.sin(theta));
		}
	}

	/**
	 * 
	 * @param connector
	 * @param first
	 *            if true this refers to box1 of a Connector, if false it refers
	 *            to box2
	 * @return
	 */
	private static Join[] calculatePossibleJoins(Connector connector,
			boolean first) {
		List<Join> options = new ArrayList<>();
		Point p = connector.getControlPoint(false);
		Box box = first ? connector.getBox1() : connector.getBox2();
		Rectangle r = box.getBounds();
		if (r.contains(p)) {
			// ugh, let's ignore the control point
			double topDistance = (new Point2D.Double(r.getCenterX(),
					r.getMinY())).distance(p);
			double bottomDistance = (new Point2D.Double(r.getCenterX(),
					r.getMaxY())).distance(p);
			double leftDistance = (new Point2D.Double(r.getMinX(),
					r.getCenterY())).distance(p);
			double rightDistance = (new Point2D.Double(r.getMaxX(),
					r.getCenterY())).distance(p);
			double min = Math.min(Math.min(topDistance, bottomDistance),
					Math.min(leftDistance, rightDistance));
			if (min == topDistance) {
				options.add(new Join(box, Side.UP, connector, first));
			} else if (min == bottomDistance) {
				options.add(new Join(box, Side.DOWN, connector, first));
			} else if (min == leftDistance) {
				options.add(new Join(box, Side.LEFT, connector, first));
			} else if (min == rightDistance) {
				options.add(new Join(box, Side.RIGHT, connector, first));
			}
		} else if (r.x + r.width < p.x) {
			// the point is on the right
			if (p.y < r.y) {
				// the point is above
				options.add(new Join(box, Side.UP, connector, first));
				options.add(new Join(box, Side.RIGHT, connector, first));
			} else if (p.y < r.y + r.height) {
				// the point is in the middle
				options.add(new Join(box, Side.RIGHT, connector, first));
			} else {
				// the point is below
				options.add(new Join(box, Side.DOWN, connector, first));
				options.add(new Join(box, Side.RIGHT, connector, first));
			}
		} else if (r.x < p.x) {
			// the point is in the middle
			if (p.y < r.y) {
				// the point is above
				options.add(new Join(box, Side.UP, connector, first));
			} else if (p.y < r.y + r.height) {
				// the point is in the middle
				// huh? do nothing.
			} else {
				// the point is below
				options.add(new Join(box, Side.DOWN, connector, first));
			}
		} else {
			// the point is on the left
			if (p.y < r.y) {
				// the point is above
				options.add(new Join(box, Side.UP, connector, first));
				options.add(new Join(box, Side.LEFT, connector, first));
			} else if (p.y < r.y + r.height) {
				// the point is in the middle
				options.add(new Join(box, Side.LEFT, connector, first));
			} else {
				// the point is below
				options.add(new Join(box, Side.DOWN, connector, first));
				options.add(new Join(box, Side.LEFT, connector, first));
			}
		}
		return options.toArray(new Join[options.size()]);
	}

	@Override
	protected void paintHandles(Graphics2D g, BoxContainerPanel bcp,
			List<Connector> connectors, List<Box> boxes, boolean dashStroke) {
		g = (Graphics2D) g.create();
		super.paintHandles(g, bcp, connectors, boxes, dashStroke);
		for (Connector connector : connectors) {
			Point p1 = connector.get(KEY_JOIN1_CONTROL_POINT);
			if (p1 != null)
				paintEllipseHandle(g, p1, dashStroke);
			Point p2 = connector.get(KEY_JOIN2_CONTROL_POINT);
			if (p2 != null)
				paintEllipseHandle(g, p2, dashStroke);
		}
	}

	@Override
	public void refreshConnectors(BoxContainerPanel bcp) {
		super.refreshConnectors(bcp);
		getContext(bcp).set(KEY_STAGING_DIRTY, true);
	}

}