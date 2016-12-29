/*
 * @(#)RowLayout.java
 *
 * $Date: 2016-01-30 19:07:08 -0500 (Sat, 30 Jan 2016) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Area;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.math.MathG;
import com.pump.plaf.MoveUIEffect;
import com.pump.plaf.UIEffect;
import com.pump.util.JVM;

public class RowLayout implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final String EFFECT = RowLayout.class.getName()+".Effect";
	private static final String CLIPPING = RowLayout.class.getName()+".Clipping";
	private static final String PAINT = RowLayout.class.getName()+".Paint";
	
	public static class ComponentConstraints {
		public static enum HorizontalAlignment {LEFT, RIGHT};
		float horizontalWeight = 0;
		Insets insets = new Insets(2, 2, 2, 2);
		HorizontalAlignment horizontalAlignment;
		int verticalPriority = 0;
		
		public ComponentConstraints(float horizontalWeight) {
			this(horizontalWeight, null, new Insets(2,2,2,2), 0);
		}
		
		/**
		 * 
		 * @param horizontalWeight the horizontal weight determines the relative width a component
		 * will receive.
		 * @param horizontalAlignment this optional alignment is used only to nudge components when
		 * the autoalign property is active.
		 * @param insets the padding for a constraint.
		 * @param verticalPriority the vertical priority of this component. All remaining vertical space
		 * in a container is equally divided among elements with the highest vertical priority.
		 */
		public ComponentConstraints(float horizontalWeight,HorizontalAlignment horizontalAlignment,Insets insets,int verticalPriority) {
			if(verticalPriority<0) throw new IllegalArgumentException("vertical priority ("+verticalPriority+") must not be negative");
			if(horizontalWeight<0) throw new IllegalArgumentException("horizontal weight ("+horizontalWeight+") must not be negative");
			
			this.horizontalWeight = horizontalWeight;
			this.horizontalAlignment = horizontalAlignment;
			this.verticalPriority = verticalPriority;
			
			if(insets==null) {
				this.insets = new Insets(0,0,0,0);
			} else {
				this.insets.set(insets.top, insets.left, insets.bottom, insets.right);
			}
		}
		
		public float getHorizontalWeight() {
			return horizontalWeight;
		}
		
		public Insets getInsets() {
			return new Insets(insets.top, insets.left, insets.bottom, insets.right);
		}
		
		public HorizontalAlignment getHorizontalAlignment() {
			return horizontalAlignment;
		}
		
		public int getVerticalPriority() {
			return verticalPriority;
		}
	}
	
	/** TODO: this is a failed experiment at supporting animation on my
	 * Windows 7 machine. This technically works, but the performance
	 * (frame rate) is unacceptably slow; so for the time being I don't
	 * see a compelling reason to even use this.
	 */
	private class Wrapper extends JComponent {
		private static final long serialVersionUID = 1L;

		Wrapper(JComponent jc) {
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
			c.fill = GridBagConstraints.BOTH;
			add(jc, c);
			
			addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent e) {
					if(getComponent(0).isVisible()==false)
						getComponent(0).setVisible(true);
				}

				@Override
				public void componentHidden(ComponentEvent e) {
					if(getComponent(0).isVisible()==true)
						getComponent(0).setVisible(false);
				}
			});
			
			jc.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentShown(ComponentEvent e) {
					if(Wrapper.this.isVisible()==false)
						Wrapper.this.setVisible(true);
				}

				@Override
				public void componentHidden(ComponentEvent e) {
					if(Wrapper.this.isVisible()==true)
						Wrapper.this.setVisible(false);
				}
			});
		}
		
		@Override
		public void paint(Graphics g) {
			g = g.create();
			Rectangle clipping = (Rectangle)getClientProperty(CLIPPING);
			if(clipping!=null) {
				clipping = SwingUtilities.convertRectangle( RowLayout.this.panel, clipping, this);
				g.clipRect(clipping.x, clipping.y, clipping.width, clipping.height);
			}
			super.paint(g);
			g.dispose();
		}
	}
	
	public static class ComponentCluster {
		protected JComponent[] components;
		protected ComponentConstraints[] constraints;

		public ComponentCluster(JComponent[] array) {
			this(array, (ComponentConstraints[])null);
		}
		
		public ComponentCluster(JComponent[] components,float[] horizontalWeights) {
			this.components = new JComponent[components.length];
			System.arraycopy(components, 0, this.components, 0, components.length);
			this.constraints = new ComponentConstraints[horizontalWeights.length];
			for(int a = 0; a<horizontalWeights.length; a++) {
				this.constraints[a] = new ComponentConstraints(horizontalWeights[a]);
			}
		}
		
		public ComponentCluster(JComponent[] components,ComponentConstraints[] constraints) {
			this.components = new JComponent[components.length];
			System.arraycopy(components, 0, this.components, 0, components.length);
			this.constraints = new ComponentConstraints[constraints.length];
			System.arraycopy(constraints, 0, this.constraints, 0, constraints.length);
		}
		
		public static ComponentCluster createRightAligned(JComponent[] c) {
			JComponent[] array = new JComponent[c.length + 1];
			System.arraycopy(c, 0, array, 1, c.length);
			ComponentConstraints[] constraints = new ComponentConstraints[c.length+1];
			constraints[0] = new ComponentConstraints(1);
			for(int a = 1; a<constraints.length; a++) {
				constraints[a] = new ComponentConstraints(0);
			}
			constraints[constraints.length-1].horizontalAlignment = ComponentConstraints.HorizontalAlignment.RIGHT;
			return new ComponentCluster(array, constraints);
		}
		
		public static ComponentCluster createRightAligned(JComponent c) {
			return createRightAligned(c, null);
		}
		
		public static ComponentCluster createRightAligned(JComponent c,Insets insets) {
			ComponentCluster cluster = createRightAligned(new JComponent[] { c });
			if(insets!=null)
				cluster.constraints[1].insets.set( insets.top, insets.left, insets.bottom, insets.right);
			return cluster;
		}
		
		public static ComponentCluster createLeftAligned(JComponent[] c) {
			JComponent[] array = new JComponent[c.length + 1];
			System.arraycopy(c, 0, array, 0, c.length);
			ComponentConstraints[] constraints = new ComponentConstraints[c.length+1];
			constraints[constraints.length-1] = new ComponentConstraints(1f);
			for(int a = 0; a<constraints.length - 1; a++) {
				constraints[a] = new ComponentConstraints(0);
			}
			constraints[0].horizontalAlignment = ComponentConstraints.HorizontalAlignment.LEFT;
			return new ComponentCluster(array, constraints);
		}
		
		public static ComponentCluster createLeftAligned(JComponent c) {
			return createLeftAligned(c, null);
		}
		
		public static ComponentCluster createLeftAligned(JComponent c, Insets insets) {
			ComponentCluster cluster = createRightAligned(new JComponent[] { c });
			if(insets!=null)
				cluster.constraints[0].insets.set( insets.top, insets.left, insets.bottom, insets.right);
			return cluster;
		}
	}
	
	static Method getBaselineMethod = getMethod();
	
	private static Method getMethod() {
		try {
			return Component.class.getMethod("getBaseline", new Class<?>[] { Integer.TYPE, Integer.TYPE });
		} catch(Throwable t) {
			return null;
		}
	}
	
	private class ComponentSize {
	
		int width, height, baseline;
		
		ComponentSize(Component c) {
			Dimension d = getPreferredSize(c);
			Dimension d2 = getMinimumSize(c);

			width = Math.max(d.width, d2.width);
			height = Math.max(d.height, d2.height);
			
			if(getBaselineMethod==null) {
				baseline = -1;
			} else {
				try {
					baseline = ((Integer)getBaselineMethod.invoke(c, new Object[] { width, height })).intValue();
				} catch(Throwable t) {
					baseline = -1;
				}
			}
		}
		
		int getAscent() {
			if(baseline<0) return height;
			return baseline;
		}
		
		int getDescent() {
			if(baseline<0) return 0;
			return height-baseline;
		}
	}

	public static class Cell implements Serializable {
		private static final long serialVersionUID = 1L;

		public float horizontalWeight = 1;
		public float verticalAlignment = .5f;
		public Insets insets = new Insets(3,3,3,3);
		
		public Cell() {}
		
		public Cell(float horizontalWeight, float verticalAlignment) {
			this(horizontalWeight, verticalAlignment, null);
		}
		
		public Cell(float horizontalWeight, float verticalAlignment,Insets insets) {
			super();
			this.horizontalWeight = horizontalWeight;
			this.verticalAlignment = verticalAlignment;
			setInsets(insets);
		}
		
		public void setInsets(Insets i) {
			if(i!=null) {
				insets.left = i.left;
				insets.right = i.right;
				insets.top = i.top;
				insets.bottom = i.bottom;
			} else {
				insets.left = 0;
				insets.right = 0;
				insets.top = 0;
				insets.bottom = 0;
			}
		}
	}
	
	private class Profile {
		/** Maps a column ID to its minimum width. */
		Map<String, int[]> columnWidthsTable = new HashMap<String, int[]>();
		int totalMinimumHeight = 0;
		int totalMinimumWidth = 0;
		int maxVisiblePriority = 0;
		/** Maps a row index to its vertical priority. */
		Map<Integer, Integer> rowToPriority = new HashMap<Integer, Integer>();
		
		SortedSet<Integer> getRows(int verticalPriority) {
			SortedSet<Integer> rows = new TreeSet<Integer>();
			for(int rowIndex = 0; rowIndex<rowToPriority.size(); rowIndex++) {
				Integer p = rowToPriority.get(rowIndex);
				if(p==verticalPriority)
					rows.add(rowIndex);
			}
			return rows;
		}
		
		void validate(boolean onlyIfInvalid) {
			if(columnWidthsTable.size()>0 && onlyIfInvalid) {
				return;
			}
			invalidate();

			
			{ //process vertical priorities
				rowToPriority.clear();
				for(int rowIndex = 0; rowIndex<rows.size(); rowIndex++) {
					Row row = rows.get(rowIndex);
					int maxPriorityForRow = -1;
					for(int columnIndex = 0; columnIndex<row.columns.length; columnIndex++) {
						ComponentCluster cluster = row.columns[columnIndex];
						if(cluster!=null) {
							for(int b = 0; b<cluster.components.length; b++) {
								JComponent clusterElement = cluster.components[b];
								if(clusterElement!=null && clusterElement.isVisible()) {
									maxVisiblePriority = Math.max(cluster.constraints[b].verticalPriority, maxVisiblePriority);
									maxPriorityForRow = Math.max(maxPriorityForRow, cluster.constraints[b].verticalPriority);
								}
							}
						}
					}
					rowToPriority.put(rowIndex, maxPriorityForRow);
				}
			}
			
			for(int rowIndex = 0; rowIndex<rows.size(); rowIndex++) {
				Row row = rows.get(rowIndex);
				int[] columnWidths = columnWidthsTable.get(row.rowTypeID);
				if(columnWidths==null) {
					columnWidths = new int[ row.columns.length ];
					columnWidthsTable.put(row.rowTypeID, columnWidths);
				}

				int maxAscent = 0;
				int maxDescent = 0;
				int maxRowHeight = 0;
				boolean baselineUsed = false;
				
				for(int columnIndex = 0; columnIndex<row.columns.length; columnIndex++) {
					ComponentCluster cluster = row.columns[columnIndex];
					if(cluster!=null) {
						Cell cell = rowTypes.get(row.rowTypeID)[columnIndex];
						int width = 0;
						for(int b = 0; b<cluster.components.length; b++) {
							JComponent clusterElement = cluster.components[b];
							if(clusterElement!=null && clusterElement.isVisible()) {
								ComponentConstraints constraints = cluster.constraints[b];
								ComponentSize preferredSize = new ComponentSize(clusterElement);
								
								if(preferredSize.baseline<0) {
									maxRowHeight = Math.max(maxRowHeight, preferredSize.height + 
											cell.insets.top + cell.insets.bottom + 
											constraints.insets.top + constraints.insets.bottom);
								} else {
									baselineUsed = true;
									maxAscent = Math.max( maxAscent, preferredSize.getAscent() + 
											cell.insets.top + constraints.insets.top);
									maxDescent = Math.max( maxDescent, preferredSize.getDescent() + 
											cell.insets.bottom + constraints.insets.bottom);
								}
								
								width += preferredSize.width + constraints.insets.left + constraints.insets.right;
							}
						}
						
						width += cell.insets.left + cell.insets.right;
						columnWidths[columnIndex] = Math.max( columnWidths[columnIndex], width );
					}
				}
				
				if(baselineUsed) {
					int textHeight = maxAscent + maxDescent;
					maxRowHeight = Math.max( maxRowHeight, textHeight);
				}

				totalMinimumHeight += maxRowHeight;
			}
			Iterator<int[]> columnWidthsIter = columnWidthsTable.values().iterator();
			while(columnWidthsIter.hasNext()) {
				int widthSum = 0;
				int[] columnWidths = columnWidthsIter.next();
				for(int t : columnWidths) {
					widthSum += t;
				}
				totalMinimumWidth = Math.max(totalMinimumWidth, widthSum);
			}
		}
		
		void invalidate() {
			totalMinimumHeight = 0;
			totalMinimumWidth = 0;
			maxVisiblePriority = 0;
			rowToPriority.clear();
			columnWidthsTable.clear();
		}
		
		int getMinimumWidth() {
			validate(true);
			return totalMinimumWidth;
		}
		
		int getMinimumHeight() {
			validate(true);
			return totalMinimumHeight;
		}
	}
	
	private class RowGeometry {
		/** These rectangles use relative y-positioning, but have absolute x, width and height values. */
		WeakHashMap<JComponent, Rectangle> theoreticalBoundsTable = new WeakHashMap<JComponent, Rectangle>();
		
		/** Based on theoreticalBoundsTable: these are nudged/stretched to follow the constraints each
		 * component should follow.
		 * These rectangles use relative y-positioning, but have absolute x, width and height values. */
		WeakHashMap<JComponent, Rectangle> actualBoundsTable = new WeakHashMap<JComponent, Rectangle>();
		
		/** A simple map of JComponents to ComponentConstraints. */
		WeakHashMap<JComponent, ComponentConstraints> constraintTable = new WeakHashMap<JComponent, ComponentConstraints>();
		
		int height, yOffset;
		final int rowIndex;
		int maxAscent = 0;
		int maxHeight = 0;
		int maxDescent = 0;

		/** Maps components to ComponentSize objects. */
		Map<JComponent, ComponentSize> sizeTable = new WeakHashMap<JComponent, ComponentSize>();
		
		void addHeight(int extraHeight) {
			this.height += extraHeight;
			this.maxHeight += extraHeight;
			this.maxAscent += extraHeight/2;
			this.maxDescent += extraHeight - extraHeight/2;
			
			Iterator<JComponent> iter = theoreticalBoundsTable.keySet().iterator();
			while(iter.hasNext()) {
				JComponent jc = iter.next();
				Rectangle r = theoreticalBoundsTable.get(jc);
				if(r!=null)
					r.height += extraHeight;
				ComponentSize size = sizeTable.get(jc);
				size.height += extraHeight;
				if(size.baseline>0)
					size.baseline += extraHeight/2;
			}
			
			calculateActualBounds();
		}
		
		Rectangle getBounds(JComponent jc, boolean theoretical,boolean includeYOffset) {
			Rectangle r = theoretical ? theoreticalBoundsTable.get(jc) : actualBoundsTable.get(jc);
			if(r==null) return null;
			r = new Rectangle(r);
			if(includeYOffset)
				r.y += yOffset;
			return r;
		}
		
		int getMaximumHeight() {
			Row row = rows.get(rowIndex);
			int maxHeight = 0;
			for(int a = 0; a<row.componentListUnwrapped.size(); a++) {
				JComponent jc = row.componentListUnwrapped.get(a);
				if(jc!=null && theoreticalBoundsTable.get(jc)!=null) {
					int width = theoreticalBoundsTable.get(jc).width;
					Dimension d = getMaximumSize(jc, width);
					if(d==null)
						d = jc.getMaximumSize();
					if(d!=null)
						maxHeight = Math.max(maxHeight, d.height);
				}
			}
			return maxHeight;
		}
		
		RowGeometry(int rowIndex,int[] columnWidths) {
			this.rowIndex = rowIndex;
			Row row = rows.get(rowIndex);
			Cell[] cells = rowTypes.get(row.rowTypeID);
			
			for(int columnIndex = 0; columnIndex<row.columns.length; columnIndex++) {
				int remainingWidth = columnWidths[columnIndex];
				ComponentCluster componentCluster = row.columns[columnIndex];
				float totalWeight = 0;
				for(int componentIndex = 0; componentIndex<componentCluster.components.length; componentIndex++) {
					JComponent component = componentCluster.components[componentIndex];
					ComponentConstraints constraints = componentCluster.constraints[componentIndex];
					constraintTable.put(component, constraints);
					if(component!=null && component.isVisible()) {
						ComponentSize size = new ComponentSize(component);
						sizeTable.put(component, size);
						remainingWidth -= size.width;
					}
					remainingWidth -= constraints.insets.left + constraints.insets.right;
					totalWeight += constraints.horizontalWeight;
				}

				int x = 0;
				for(int z = 0; z<columnIndex; z++) {
					x += columnWidths[z];
				}
				
				x += cells[columnIndex].insets.left;
				remainingWidth -= cells[columnIndex].insets.right + cells[columnIndex].insets.left;
				
				for(int componentIndex = 0; componentIndex<componentCluster.components.length; componentIndex++) {
					JComponent component = componentCluster.components[componentIndex];
					ComponentConstraints constraints = constraintTable.get( component );
					ComponentSize size = component==null ? null : sizeTable.get(component);
					x += constraints.insets.left;

					int extraWidth;
					if(totalWeight==0) {
						extraWidth = 0;
					} else {
						extraWidth = (int)( constraints.horizontalWeight / totalWeight * remainingWidth );
						remainingWidth -= extraWidth;
						totalWeight -= constraints.horizontalWeight;
					}
					
					if(component!=null && component.isVisible()) {
						Rectangle r = new Rectangle(x, 
								0,
								size.width + extraWidth, 
								size.height);
						if(size.baseline>0) {
							maxAscent = Math.max( maxAscent, size.baseline + constraints.insets.top + cells[columnIndex].insets.top);
							maxDescent = Math.max( maxDescent, size.height - size.baseline + constraints.insets.bottom + cells[columnIndex].insets.bottom);
						} else {
							maxHeight = Math.max( maxHeight, size.height + constraints.insets.top + constraints.insets.bottom
									+ cells[columnIndex].insets.top + cells[columnIndex].insets.bottom);
						}
						theoreticalBoundsTable.put(component, r);
						x += r.width;
					} else {
						x += extraWidth;
					}
					x += constraints.insets.right;
				}
			}
			
			height = Math.max( maxAscent + maxDescent, maxHeight);
			if(maxAscent + maxDescent < height) {
				//stretch the maxAscent & maxDescent to fill height:
				int extraTextHeight = height - maxAscent - maxDescent;
				int extraAscent = extraTextHeight/2;
				int extraDescent = extraTextHeight - extraAscent;
				maxAscent += extraAscent;
				maxDescent += extraDescent;
			}
			if(maxHeight < height)
				maxHeight = height;
			
			calculateActualBounds();
		}
		
		void calculateActualBounds() {
			actualBoundsTable.clear();
			
			Iterator<JComponent> iter = theoreticalBoundsTable.keySet().iterator();
			while(iter.hasNext()) {
				JComponent comp = iter.next();
				Rectangle bounds = theoreticalBoundsTable.get(comp);
				ComponentSize size = sizeTable.get(comp);
				ComponentConstraints constraints = constraintTable.get(comp);
				
				int incr;
				if(size.baseline>0) {
					incr = maxAscent - size.baseline;
				} else {
					incr = height/2 - size.height/2;
				}
				
				Rectangle actualBounds = new Rectangle(bounds);
				actualBounds.y += incr;
				JComponent alignComp = comp;
				if(alignComp instanceof Wrapper)
					alignComp = (JComponent)alignComp.getComponent(0);
				if(isAutoAlign(alignComp)) {
					if(constraints.horizontalAlignment==ComponentConstraints.HorizontalAlignment.LEFT) {
						Insets i = paddingInfo.get(alignComp);
						actualBounds.x -= i.left;
					} else if(constraints.horizontalAlignment==ComponentConstraints.HorizontalAlignment.RIGHT) {
						Insets i = paddingInfo.get(alignComp);
						actualBounds.x += i.right;
					}
				}
				
				if(comp instanceof JCheckBox) {
					int d1 = actualBounds.y;
					int d2 = height - actualBounds.y - actualBounds.height;
					int d = Math.min(d1,d2);
					actualBounds.y -= d;
					actualBounds.height += 2*d;
				}
				
				actualBoundsTable.put(comp, actualBounds);
			}
		}
		
		/** @return true if these two geometries lay out the same objects.
		 * (It may be possible for two geometries to layout the same
		 * number of objects with the same height but still refer to 
		 * different objects.)
		 * @param other
		 */
		protected boolean componentsMatch(RowGeometry other) {
			return actualBoundsTable.keySet().equals(other.actualBoundsTable.keySet());
		}
	}
	
	private class RowLayoutManager implements LayoutManager2 {
		
		ChangeListener stripPropertiesWhenFinishedListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				MoveUIEffect effect = (MoveUIEffect)e.getSource();
				if(effect.getState()==UIEffect.State.FINISHED) {
					JComponent jc = effect.getComponent();
					jc.putClientProperty(PAINT, null);
					jc.putClientProperty(EFFECT, null);
					jc.putClientProperty(CLIPPING, null);
					Container parent = jc.getParent();
					if(parent!=null) {
						Rectangle r = jc.getBounds();
						parent.repaint(r.x, r.y, r.width, r.height);
					}
				}
			}
		};
		
		/** The profile calculates the minimum column widths, but this
		 * table tracks the actual widths (which may be mucher larger,
		 * based on the size of container).
		 */  
		Map<String, int[]> realColumnWidthsTable = new HashMap<String, int[]>();
		
		/** Maps a row index to its previous layout. */
		Map<Integer, RowGeometry> lastGeometryTable = new HashMap<Integer, RowGeometry>();

		public void addLayoutComponent(String arg0, Component arg1) {}

		private void checkContainer(Container parent) {
			if(parent!=panel) throw new IllegalArgumentException("This layout should only be used with the RowLayout object that created it.");
		}
		
		public void layoutContainer(Container parent) {
			checkContainer(parent);
			
			for(int a = 0; a<parent.getComponentCount(); a++) {
				Component c = parent.getComponent(a);
				if(c instanceof JComponent) {
					JComponent jc = (JComponent)c;
					MoveUIEffect effect = (MoveUIEffect)jc.getClientProperty(EFFECT);
					if(effect!=null)
						effect.stop();
				}
			}

			profile.validate(true);
			int containerWidth = Math.max(parent.getWidth(), profile.totalMinimumWidth);
			int containerHeight = Math.max(parent.getHeight(), profile.totalMinimumHeight);
			
			realColumnWidthsTable.clear();
			/** Maps a row index to its current layout. */
			Map<Integer, RowGeometry> geometryTable = new HashMap<Integer, RowGeometry>();
			
			{ //fill in realColumnWidthsTable:
				Iterator<String> iter = profile.columnWidthsTable.keySet().iterator();
				while(iter.hasNext()) {
					String rowID = iter.next();
					int[] minimumWidths = profile.columnWidthsTable.get(rowID);
					int[] realWidths = new int[minimumWidths.length];
					Cell[] cells = rowTypes.get(rowID);
					float totalWeight = 0;
					int totalWidth = 0;
					for(int columnIndex = 0; columnIndex<cells.length; columnIndex++) {
						totalWeight += cells[columnIndex].horizontalWeight;
						totalWidth += minimumWidths[columnIndex];
					}
					int remainingWidth = containerWidth - totalWidth;
					for(int columnIndex = 0; columnIndex<cells.length; columnIndex++) {
						if(totalWeight>0) {
							int myExtraWidth = (int)(cells[columnIndex].horizontalWeight * remainingWidth / totalWeight);
							realWidths[columnIndex] += minimumWidths[columnIndex] + myExtraWidth;
							remainingWidth -= myExtraWidth;
						} else {
							realWidths[columnIndex] = minimumWidths[columnIndex];
						}
						totalWeight -= cells[columnIndex].horizontalWeight;
					}
					
					realColumnWidthsTable.put(rowID, realWidths);
				}
			}
			
			{ //calculate all the vertical placement
				int currentHeight = 0;
				for(int rowIndex = 0; rowIndex<rows.size(); rowIndex++) {
					Row row = rows.get(rowIndex);
					int[] columnWidths = realColumnWidthsTable.get(row.rowTypeID);
					RowGeometry geometry = new RowGeometry(rowIndex, columnWidths);
					currentHeight += geometry.height;
					geometryTable.put(rowIndex, geometry);
				}
				
				int remainingHeight = containerHeight - currentHeight; //or "containerHeight - profile.totalMinimumHeight"
				int currentMaxPriority = profile.maxVisiblePriority;
				while(remainingHeight>0 && currentMaxPriority>=0) {
					SortedSet<Integer> rows = profile.getRows(currentMaxPriority);
					if(rows!=null) {
						int rowsRemaining = rows.size();
						for(Integer rowIndex : rows) {
							int extraHeightPerRow = remainingHeight/rowsRemaining;
							RowGeometry geometry = geometryTable.get(rowIndex);
							int maxHeight = geometry.getMaximumHeight();
							int extraHeight = Math.min(extraHeightPerRow, Math.max(0, maxHeight - geometry.height) );
							geometry.addHeight( extraHeight );
							remainingHeight -= extraHeight;
							rowsRemaining--;
						}
					}
					currentMaxPriority--;
				}
				
				int y = 0;
				for(int rowIndex = 0; rowIndex<rows.size(); rowIndex++) {
					RowGeometry geometry = geometryTable.get(rowIndex);
					geometry.yOffset = y;
					y += geometry.height;
				}
			}

			{ // actually install all the RowGeometries:
				int max = Math.max( rows.size(), lastGeometryTable.size());
				
				boolean animate = isAnimationEnabled();
				Set<Integer> rowsToShow = new HashSet<Integer>();
				Set<Integer> rowsToHide = new HashSet<Integer>();
				for(int rowIndex = 0; rowIndex<max && animate; rowIndex++) {
					RowGeometry current = geometryTable.get(rowIndex);
					RowGeometry previous = lastGeometryTable.get(rowIndex);
					if(current!=null && current.height!=0 && (previous==null || previous.height==0)) {
						rowsToShow.add( rowIndex );
					} else if( (current==null || current.height==0) && previous!=null && previous.height!=0) {
						rowsToHide.add( rowIndex );
					} else if(current!=null && previous!=null) {
						if(!current.componentsMatch(previous)) {
							//if components with a row were added/removed: don't try to animate that.
							animate = false;
						} //else if(current.height==previous.height && current.height>0 && 
						//		current.width!=previous.width &&
						//		current.componentsMatch(previous)) {
							//only the width is changing? don't animate; that's a weird effect
						//	animate = false;
						//}
					}
				}
				
				if(!animate) {
					for(int rowIndex = 0; rowIndex<max; rowIndex++) {
						RowGeometry current = geometryTable.get(rowIndex);
						if(current!=null) {
							Iterator<JComponent> iter = current.actualBoundsTable.keySet().iterator();
							while(iter.hasNext()) {
								JComponent comp = iter.next();
								Rectangle bounds = current.getBounds(comp, false, true);
								comp.setBounds(bounds);
								comp.putClientProperty(EFFECT, null);
								comp.putClientProperty(PAINT, null);
								comp.putClientProperty(CLIPPING, null);
							}
						}
					}
					parent.repaint();
				} else {
					for(int rowIndex = 0; rowIndex<max; rowIndex++) {
						if(rowsToHide.contains(rowIndex)) {
							int rowMin = rowIndex;
							int rowMax = rowIndex;
							int displacement = lastGeometryTable.get(rowMin).height;
							Rectangle clipping = new Rectangle(0, lastGeometryTable.get(rowMin).yOffset, containerWidth, 0);
							while( rowsToHide.contains(rowMax+1) ) {
								rowMax++;
								displacement += lastGeometryTable.get(rowMax).height;
							}
							clipping.height += displacement;
							for(int k = rowMin; k<=rowMax; k++) {
								RowGeometry previous = lastGeometryTable.get(k);
								Iterator<JComponent> iter = previous.actualBoundsTable.keySet().iterator();
								while(iter.hasNext()) {
									JComponent comp = iter.next();
									Rectangle boundsBefore = previous.getBounds(comp, false, true);
									Rectangle boundsAfter = previous.getBounds(comp, false, true);
									boundsAfter.y -= displacement;
									MoveUIEffect effect = new MoveUIEffect(comp, boundsBefore, boundsAfter);
									effect.addChangeListener( stripPropertiesWhenFinishedListener );
									comp.putClientProperty(EFFECT, effect);
									comp.putClientProperty(PAINT, Boolean.TRUE);
									comp.putClientProperty(CLIPPING, clipping);
									comp.setBounds(boundsBefore);
								}
							}
							rowIndex = rowMax;
						} else if(rowsToShow.contains(rowIndex)) {
							int rowMin = rowIndex;
							int rowMax = rowIndex;
							int displacement = geometryTable.get(rowMin).height;
							Rectangle clipping = new Rectangle(0, geometryTable.get(rowMin).yOffset, containerWidth, 0);
							while( rowsToShow.contains(rowMax+1) ) {
								rowMax++;
								displacement += geometryTable.get(rowMax).height;
							}
							clipping.height += displacement;
							for(int k = rowMin; k<=rowMax; k++) {
								RowGeometry current = geometryTable.get(k);
								Iterator<JComponent> iter = current.actualBoundsTable.keySet().iterator();
								while(iter.hasNext()) {
									JComponent comp = iter.next();
									Rectangle boundsBefore = current.getBounds(comp, false, true);
									Rectangle boundsAfter = current.getBounds(comp, false, true);
									boundsBefore.y -= displacement;
									MoveUIEffect effect = new MoveUIEffect(comp, boundsBefore, boundsAfter);
									effect.addChangeListener( stripPropertiesWhenFinishedListener );
									comp.putClientProperty(EFFECT, effect);
									comp.putClientProperty(PAINT, null);
									comp.putClientProperty(CLIPPING, clipping);
									comp.setBounds(boundsBefore);
								}
							}
							rowIndex = rowMax;
						} else {
							//the easiest case:
							//just create a MoveUIEffect to move this already-visible
							//row somewhere else:
							RowGeometry current = geometryTable.get(rowIndex);
							Iterator<JComponent> iter = current.actualBoundsTable.keySet().iterator();
							while(iter.hasNext()) {
								JComponent comp = iter.next();
								Rectangle bounds = current.getBounds(comp, false, true);
								comp.putClientProperty(EFFECT, new MoveUIEffect(comp, bounds));
								comp.putClientProperty(PAINT, null);
								comp.putClientProperty(CLIPPING, null);
							}
						}
					}
				}
			}
			
			lastGeometryTable.clear();
			lastGeometryTable.putAll(geometryTable);
		}
		
		private int getMaximumExtraHeight(Row row,int[] columnWidths) {
			int maxExtraHeight = 0;
			for(int a = 0; a<row.columns.length; a++) {
				if(row.columns[a]!=null) {
					int columnWidth = columnWidths[a];
					for(int b = 0; b<row.columns[a].components.length; b++) {
						JComponent jc = row.columns[a].components[b];
						ComponentConstraints c = row.columns[a].constraints[b];
						if(jc!=null && jc.isVisible() && c.verticalPriority==profile.maxVisiblePriority) {
							Dimension maxSize = getMaximumSize(jc, columnWidth);
							
							if(maxSize==null) 
								maxSize = jc.getMaximumSize();
							Dimension preferredSize = getPreferredSize(jc);
							
							maxExtraHeight = Math.max( maxExtraHeight, maxSize.height - preferredSize.height);
						}
					}
				}
			}
			return maxExtraHeight;
		}

		public Dimension minimumLayoutSize(Container parent) {
			checkContainer(parent);
			return new Dimension( profile.getMinimumWidth(), profile.getMinimumHeight());
		}

		public Dimension preferredLayoutSize(Container parent) {
			checkContainer(parent);
			return new Dimension( profile.getMinimumWidth(), profile.getMinimumHeight());
		}

		public Dimension maximumLayoutSize(Container parent) {
			checkContainer(parent);
			return new Dimension( profile.getMinimumWidth(), profile.getMinimumHeight());
		}

		public void removeLayoutComponent(Component comp) {}

		public void addLayoutComponent(Component component, Object constraints) {}

		public float getLayoutAlignmentX(Container parent) {
			checkContainer(parent);
			return 0;
		}

		public float getLayoutAlignmentY(Container parent) {
			checkContainer(parent);
			return 0;
		}

		public void invalidateLayout(Container parent) {
			checkContainer(parent);
			profile.invalidate();
		}
	}
	
	private static class Row implements Serializable {
		private static final long serialVersionUID = 1L;
		
		ComponentCluster[] columns;
		String rowTypeID;
		
		ArrayList<JComponent> componentListUnwrapped = new ArrayList<JComponent>();
		
		Row(ComponentCluster[] columns, String rowTypeID) {
			this.columns = Arrays.copyOf(columns, columns.length);
			this.rowTypeID = rowTypeID;
			
			for(int a = 0; a<columns.length; a++) {
				if(columns[a]!=null) {
					for(int b = 0; b<columns[a].components.length; b++) {
						if(columns[a].components[b]!=null) {
							JComponent jc = columns[a].components[b];
							componentListUnwrapped.add(jc);
						}
					}
				}
			}
		}
		
		Iterable<JComponent> componentIterator() {
			return componentListUnwrapped;
		}
		
		JComponent[] components() {
			return componentListUnwrapped.toArray(new JComponent[componentListUnwrapped.size()]);
		}
	}
	
	final JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;
		
		@Override
		protected void paintChildren(Graphics g) {
			Set<Rectangle> clippings = new HashSet<Rectangle>();
			
			if(!isAnimationEnabled()) {
				super.paintChildren(g);
			} else {
				for(int a = getComponentCount()-1; a>=0; a--) {
					Component child = getComponent(a);
					Rectangle clipping = null;
					boolean paintAnyway = false;
					
					if(child instanceof JComponent) {
						JComponent jc = (JComponent)child;
						clipping = (Rectangle)jc.getClientProperty(CLIPPING);
						if(jc.getClientProperty(PAINT)!=null) {
							paintAnyway = (Boolean)jc.getClientProperty(PAINT);
						} else {
							paintAnyway = false;
						}
						if(clipping!=null)
							clippings.add(new Rectangle(clipping));
					}
	
					if(child.isVisible() || paintAnyway) {
						Graphics2D g2 = (Graphics2D)g.create();
						if(clipping!=null)
							g2.clipRect(clipping.x, clipping.y, clipping.width, clipping.height);
						Rectangle r = child.getBounds();
						g2.translate(r.x, r.y);
						g2.clipRect(0, 0, r.width, r.height);
						getComponent(a).paint(g2);
						g2.dispose();
					}
				}
			}
			
			if(debug) {
				Graphics2D g2 = (Graphics2D)g.create();
				for(int rowIndex = 0; rowIndex<rows.size(); rowIndex++) {
					Row row = rows.get(rowIndex);
					RowGeometry geometry = layout.lastGeometryTable.get(rowIndex);
					Cell[] cells = rowTypes.get(row.rowTypeID);
					int[] widths = layout.realColumnWidthsTable.get(row.rowTypeID);
					int x = 0;
					for(int columnIndex = 0; columnIndex<widths.length; columnIndex++) {
						drawInsets(g2, x, geometry.yOffset, widths[columnIndex], geometry.height, cells[columnIndex].insets, Color.blue);
						x += widths[columnIndex];
					}
					for(JComponent jc : row.componentIterator()) {
						if(geometry.theoreticalBoundsTable.get(jc)!=null) {
							Rectangle r = new Rectangle(geometry.theoreticalBoundsTable.get(jc));
							Insets i = geometry.constraintTable.get(jc).insets;
							r.y += geometry.yOffset;
							r.x -= i.left;
							r.y -= i.top;
							r.width += i.left + i.right;
							r.height += i.top + i.bottom;
							drawInsets(g2, r.x, r.y, r.width, r.height, i, Color.red);
						}
					}
				}
				
				Iterator<Rectangle> iter = clippings.iterator();
				while(iter.hasNext()) {
					Rectangle r = iter.next();
					g.setColor(Color.green);
					g.drawRect(r.x, r.y, r.width, r.height);
				}
				
				g2.dispose();
			}
		}
		
		private void drawInsets(Graphics2D g, int x, int y, int width, int height, Insets insets, Color color) {
			g.setColor(color);
			g.drawRect(x, y, width, height);
			
			if(insets!=null) {
				Area area = new Area(new Rectangle(x, y, width, height));
				area.subtract(new Area(new Rectangle(x + insets.left, y + insets.top, width - insets.left - insets.right, height - insets.top - insets.bottom)));
				g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()/8));
				g.fill(area);
			}
		}
	};
	
	static final PaddingInfo paddingInfo = new StoredPaddingInfo();
	final Map<String, Cell[]> rowTypes = new HashMap<String, Cell[]>();
	final List<Row> rows = new ArrayList<Row>();
	final Profile profile = new Profile();
	boolean autoAlign = true;
	boolean animationEnabled = JVM.isMac;
	boolean wrapperEnabled = false;
	private RowLayoutManager layout = new RowLayoutManager();
	public boolean debug = false;
	
	public RowLayout() {
		panel.setLayout(layout);
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public boolean isAnimationEnabled() {
		return animationEnabled;
	}
	
	/** Controls whether animations are enabled.
	 * Note by default these are enabled by default on Mac, but
	 * disabled on other platforms because of rendering
	 * bugs/performance problems.
	 * @param b whether animations are enabled.
	 */
	public void setAnimationEnabled(boolean b) {
		animationEnabled = b;
	}
	
	/** Add a new row type (or overwrite the definition of
	 * an existing row type).
	 * @param rowTypeID the type ID of the new row
	 * @param cell the new cells to add
	 */
	public void addRowType(String rowTypeID,Cell[] cell) {
		Cell[] oldValue = rowTypes.put(rowTypeID, cell);
		if(oldValue!=null) {
			profile.invalidate();
			panel.invalidate();
		}
	}
	
	/** Returns the row type associated with a row index.
	 * @param rowIndex the row index to get the type of.
	 * @return the row type of a given row.
	 */
	public String getRowType(int rowIndex) {
		Row row = rows.get(rowIndex);
		return row.rowTypeID;
	}
	
	public ComponentConstraints getConstraints(Component c) {
		if(c==null) throw new NullPointerException();
		for(Row row : rows) {
			for(ComponentCluster cluster : row.columns) {
				for(int a = 0; a<cluster.components.length; a++) {
					if(c==cluster.components[a]) {
						return cluster.constraints[a];
					}
				}
			}
		}
		throw new IllegalArgumentException("this component was not found");
	}
	
	/** Change the row type associated with a row index.
	 * <p>This is generally not recommended for normal use.
	 * 
	 * @param rowIndex the row index to modify
	 * @param rowTypeID the new row type ID
	 * @return whether a change occurred
	 */
	public boolean setRowType(int rowIndex,String rowTypeID) {
		if(rowTypeID==null) throw new NullPointerException();
		Row row = rows.get(rowIndex);
		boolean returnValue = !rowTypeID.equals(row.rowTypeID);
		row.rowTypeID = rowTypeID;

		if(returnValue) {
			profile.invalidate();
			panel.invalidate();
			panel.revalidate();
		}
		
		return returnValue;
	}
	
	public int addRow(ComponentCluster[] components,String rowTypeID) {
		if(wrapperEnabled) {
			for(int a = 0; a<components.length; a++) {
				if(components[a]!=null) {
					for(int b = 0; b<components[a].components.length; b++) {
						if(components[a].components[b]!=null)
							components[a].components[b] = new Wrapper( components[a].components[b] );
					}
				}
			}
		}
		Row row = new Row(components, rowTypeID);

		Cell[] cells = rowTypes.get(row.rowTypeID);
		if(cells==null)
			throw new IllegalArgumentException("The row type \""+row.rowTypeID+"\" is not defined. Call addRowType() to define a row before calling addRow(..)");
		if(cells.length!=row.columns.length)
			throw new IllegalArgumentException("The row type \""+row.rowTypeID+"\" is defined to accept "+cells.length+" columns, but "+row.columns.length+" were provided.");
		rows.add(row);
		for(JComponent jc : row.componentIterator()) {
			if(jc.getParent() instanceof Wrapper)
				jc = (JComponent)jc.getParent();
			panel.add(jc);
		}
		return rows.size()-1;
	}
	
	public boolean isRowVisible(int rowIndex) {
		Row row = rows.get(rowIndex);
		int componentsVisible = 0;
		for(JComponent jc : row.componentIterator()) {
			if(jc.isVisible()) {
				componentsVisible++;
			} else {
				return false;
			}
		}
		return componentsVisible>0;
	}
	
	public int getRowCount() {
		return rows.size();
	}
	
	public JComponent[] getRow(int rowIndex) {
		Row row = rows.get(rowIndex);
		return row.components();
	}
	
	public void setRowVisible(int rowIndex,boolean visible) {
		Row row = rows.get(rowIndex);
		for(JComponent jc : row.componentIterator()) {
			jc.setVisible(visible);
		}
	}
	
	public int addRow(ComponentCluster column1,ComponentCluster column2,ComponentCluster column3,String rowTypeID) {
		return addRow(new ComponentCluster[] {column1, column2, column3}, rowTypeID);
	}
	
	public int addRow(ComponentCluster column1,ComponentCluster column2,String rowTypeID) {
		return addRow(new ComponentCluster[] {column1, column2}, rowTypeID);
	}

	public int addRow(ComponentCluster singleColumn,String rowTypeID) {
		return addRow(new ComponentCluster[] {singleColumn}, rowTypeID);
	}
	
	public void clear() {
		rows.clear();
		panel.removeAll();
	}
	
	/** Return whether a component should autoalign.
	 * 
	 * @param jc the component to check
	 * @return whether autoalign is active for the argument
	 */
	public boolean isAutoAlign(JComponent jc) {
		return autoAlign;
	}
	
	/** Returns the preferred size of a component.
	 * Subclasses are encouraged to override this.
	 * 
	 * @param c the component to retrieve the maximum size.
	 * @return the maximum size of the argument.
	 */
	protected Dimension getPreferredSize(Component c) {
		return c.getPreferredSize();
	}
	
	protected Dimension getMaximumSize(JComponent jc,int columnWidth) {
		Dimension maxSize = null;
		if(jc instanceof JScrollPane) {
			JScrollPane scrollPane = (JScrollPane)jc;
			Component child = scrollPane.getViewport().getComponent(0);
			if(child instanceof JList) {
				JList list = (JList)child;
				int cellWidth = list.getFixedCellWidth();
				int cellHeight = list.getFixedCellHeight();
				if(cellWidth>0 && cellHeight>0) {
					int elementCount = list.getModel().getSize();
					int columns = (columnWidth - scrollPane.getVerticalScrollBar().getWidth()) / cellWidth;
					int rows = MathG.ceilInt( ((double)elementCount)/((double)columns) );
					maxSize = new Dimension( columns*cellWidth, rows*cellHeight);
				}
			} else if(child instanceof JTree) {
				JTree tree = (JTree)child;
				int rowHeight = tree.getRowHeight();
				int rows = Math.max(tree.getVisibleRowCount(), 1);
				int height = rows*rowHeight;
				maxSize = new Dimension( 100, height);
			}
			
			if(maxSize!=null) {
				Insets i = scrollPane.getBorder().getBorderInsets(scrollPane);
				maxSize.height += i.top + i.bottom;
			}
		}
		return maxSize;
	}

	/** Returns the minimum size of a component.
	 * Subclasses are encouraged to override this.
	 * 
	 * @param c the component to retrieve the minimum size.
	 * @return the minimum size of the argument.
	 */
	protected Dimension getMinimumSize(Component c) {
		return c.getMinimumSize();
	}
	
	/** Set the default autoalign property.
	 * @param b the new autoalign value.
	 */
	public void setDefaultAutoAlign(boolean b) {
		autoAlign = b;
		panel.invalidate();
		panel.validate();
	}
}
