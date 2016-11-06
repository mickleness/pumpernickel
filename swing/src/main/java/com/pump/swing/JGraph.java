/*
 * @(#)JGraph.java
 *
 * $Date: 2016-01-30 19:07:08 -0500 (Sat, 30 Jan 2016) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
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
package com.pump.swing;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;

import com.pump.blog.Blurb;
import com.pump.geom.TransformUtils;

/** A JComponent that renders a simple graph.
 * 
 */
@Blurb (
filename = "JGraph",
title = "Data: Simple Graphs",
releaseDate = "",
summary = "This helps render graph of data points, including different colors for different data.",
scrapped = "You should probably use <a href=\"http://www.jfree.org/jfreechart/\">JFreeChart</a> "+
"unless you're looking for a tiny class with minuscule overhead.",
sandboxDemo = true
)
public class JGraph extends JComponent {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JGraph graph = new JGraph();
				
				for(int x = 50; x<200; x++) {
					graph.add("Line", x, .5*x+20);
				}
				for(double t = 0; t<4*Math.PI; t += .1) {
					graph.add("Sine Wave", t*50, 30+10*Math.sin(t));
				}
				
				JFrame frame = new JFrame();
				frame.getContentPane().add(graph);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

	static class Data implements Comparable<Data> {
		final double x;
		double[] y = new double[] {};
		
		Data(double x) {
			this.x = x;
		}

		Data(double x,double y) {
			this(x);
			add(y);
		}
		
		public double getX() {
			return x;
		}
		
		public double[] getY() {
			double[] t = new double[y.length];
			System.arraycopy(y, 0, t, 0, y.length);
			return t;
		}
		
		void add(double y) {
			double[] t = new double[this.y.length+1];
			System.arraycopy(this.y, 0, t, 0, this.y.length);
			t[t.length-1] = y;
			Arrays.sort(t);
			this.y = t;
		}

		public int compareTo(Data d) {
			if(x<d.x) return -1;
			if(x>d.x) return 1;
			return 0;
		}
	}
	
	class XAxis extends JComponent {
		private static final long serialVersionUID = 1L;

		XAxis() {
			setMinimumSize(new Dimension(20, 60));
			setPreferredSize(new Dimension(20, 60));
			setOpaque(false);
		}
		
		@Override
		protected void paintComponent(Graphics g0) {
			super.paintComponent(g0);
			if(domainMin==null) return;
			
			Graphics2D g = (Graphics2D)g0;
			g.setFont( JGraph.this.getFont() );
			
			AffineTransform transform = representation.getTransform();
			
			point.setLocation(domainMin.doubleValue(), 0);
			transform.transform(point, point);
			int x1 = (int)point.getX();
			point.setLocation(domainMax.doubleValue(), 0);
			transform.transform(point, point);
			int x2 = (int)point.getX();
			
			TickMarkInfo info = new TickMarkInfo(domainMin.doubleValue(), domainMax.doubleValue(), x2-x1);
			paintTicks(g, info.minorTicks, 3);
			paintTicks(g, info.majorTicks, 6);
			paintLabels(g, info.labels, 8);
		}
		
		void paintLabels(Graphics2D g,Map<Number, String> labels,int distance) {
			AffineTransform transform = representation.getTransform();
			Iterator<Number> iter = labels.keySet().iterator();
			Font font = g.getFont();
			while(iter.hasNext()) {
				Number n = iter.next();
				point.setLocation(n.doubleValue(), 0);
				transform.transform(point, point);
				int x = (int)(point.getX()+.5);
				String s = labels.get(n);
				LineMetrics metrics = font.getLineMetrics(s, g.getFontRenderContext());
				Graphics2D g2 = (Graphics2D)g.create();
				g2.rotate(Math.PI/2, x, distance+metrics.getAscent());
				g2.drawString(s, x-distance, (distance+metrics.getAscent()+metrics.getDescent()) );
				g2.dispose();
			}
		}
		
		void paintTicks(Graphics2D g,Set<Number> ticks,int size) {
			AffineTransform transform = representation.getTransform();
			
			Iterator<Number> iter = ticks.iterator();
			g.setColor(Color.gray);
			while(iter.hasNext()) {
				point.setLocation(iter.next().doubleValue(), 0);
				transform.transform(point, point);
				int x = (int)(point.getX()+.5);
				g.drawLine( x, 0, x, size);
			}
		}
	}
	
	class YAxis extends JComponent {
		private static final long serialVersionUID = 1L;

		YAxis() {
			setMinimumSize(new Dimension(60, 20));
			setPreferredSize(new Dimension(60, 20));
			setOpaque(false);
		}
		
		@Override
		protected void paintComponent(Graphics g0) {
			super.paintComponent(g0);
			if(rangeMin==null) return;
			
			Graphics2D g = (Graphics2D)g0;
			g.setFont( JGraph.this.getFont() );
			
			AffineTransform transform = representation.getTransform();
			
			point.setLocation(0, rangeMin.doubleValue());
			transform.transform(point, point);
			int y1 = (int)point.getY();
			point.setLocation(0, rangeMax.doubleValue());
			transform.transform(point, point);
			int y2 = (int)point.getY();
			
			TickMarkInfo info = new TickMarkInfo(rangeMin.doubleValue(), rangeMax.doubleValue(), y1-y2);
			paintTicks(g, info.minorTicks, 3);
			paintTicks(g, info.majorTicks, 6);
			paintLabels(g, info.labels, 8);
		}
		
		void paintLabels(Graphics2D g,Map<Number, String> labels,int distance) {
			AffineTransform transform = representation.getTransform();
			Iterator<Number> iter = labels.keySet().iterator();
			Font font = g.getFont();
			while(iter.hasNext()) {
				Number n = iter.next();
				point.setLocation(0, n.doubleValue());
				transform.transform(point, point);
				int y = (int)(point.getY()+.5);
				String s = labels.get(n);
				LineMetrics metrics = font.getLineMetrics(s, g.getFontRenderContext());
				Rectangle2D r = g.getFontMetrics().getStringBounds(s, g);
				g.drawString(s, (float)(getWidth()-distance-r.getWidth()), (y+metrics.getDescent()) );
			}
		}
		
		void paintTicks(Graphics2D g,Set<Number> ticks,int size) {
			AffineTransform transform = representation.getTransform();
			
			Iterator<Number> iter = ticks.iterator();
			g.setColor(Color.gray);
			while(iter.hasNext()) {
				point.setLocation(0, iter.next().doubleValue());
				transform.transform(point, point);
				int y = (int)(point.getY()+.5);
				g.drawLine( getWidth()-size, y, getWidth(), y);
			}
		}
	}
	
	class GraphRepresentation extends JComponent {
		private static final long serialVersionUID = 1L;

		/** Dot radius */
		private int r = 2;
		
		MouseInputAdapter mouseListener = new MouseInputAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) {
					showContextualMenu(e);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(e.isPopupTrigger()) {
					showContextualMenu(e);
				}
			}
			
			void showContextualMenu(MouseEvent e) {
				JPopupMenu popup = new JPopupMenu();
				JMenuItem item = new JMenuItem("Save...");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							saveToFile(null);
						} catch(IOException e2) {
							e2.printStackTrace();
						}
					}
				});
				popup.add(item);
				popup.show(GraphRepresentation.this, e.getX(), e.getY());
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				try {
					AffineTransform transform = getTransform();
					AffineTransform inverse = transform.createInverse();
					Point2D dataPoint = inverse.transform(e.getPoint(), null);

					Set<String> sortedNames = data.keySet();
					Iterator<String> keyIter = sortedNames.iterator();
					double minDistance = Double.MAX_VALUE;
					String closestTip = null;
					while(keyIter.hasNext()) {
						String name = keyIter.next();
						SortedSet<Data> d = data.get(name);
						Iterator<Data> dataIter = d.iterator();
						while(dataIter.hasNext()) {
							Data z = dataIter.next();
							for(int a = 0; a<z.y.length; a++) {
								point.setLocation(z.x, z.y[a]);
								transform.transform(point, point);
								ellipse.setFrame(point.getX()-3*r,point.getY()-3*r,6*r,6*r);
								if(ellipse.contains(e.getX(), e.getY())) {
									double distance = Point2D.distance(dataPoint.getX(), dataPoint.getY(), z.x, z.y[a]);
									if(distance<minDistance) {
										minDistance = distance;
										closestTip = (name+" ( "+z.x+", "+z.y[a]+" )");
									} else if(distance==minDistance) {
										closestTip += "<br>"+(name+" ( "+z.x+", "+z.y[a]+" )");
									}
								}
							}
						}
					}
					if(closestTip==null) {
						GraphRepresentation.this.setToolTipText(null);
					} else {
						GraphRepresentation.this.setToolTipText("<html>"+closestTip+"</html>");
					}
					
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
				}
			}
		};
		
		GraphRepresentation() {
			setMinimumSize(new Dimension(80, 80));
			setPreferredSize(new Dimension(300, 300));
			setOpaque(false);
			addMouseListener( mouseListener );
			addMouseMotionListener( mouseListener );
		}
		
		AffineTransform getTransform() {
			if(rangeMin==null || domainMin==null ||
					rangeMin.equals(rangeMax) || 
					domainMin.equals(domainMax))
				return new AffineTransform();
			
			return TransformUtils.createAffineTransform(
					domainMin.doubleValue(), rangeMin.doubleValue(), 
					domainMax.doubleValue(), rangeMin.doubleValue(), 
					domainMin.doubleValue(), rangeMax.doubleValue(),
					r+1, getHeight()-r-1, 
					getWidth()-r-1, getHeight()-r-1, 
					r+1, r+1 );
		}
		
		@Override
		protected void paintComponent(Graphics g0) {
			super.paintComponent(g0);
			Graphics2D g = (Graphics2D)g0;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			if(domainMin==null) return;
			
			AffineTransform transform = getTransform();
			
			Set<String> sortedNames = data.keySet();
			Iterator<String> keyIter = sortedNames.iterator();
			while(keyIter.hasNext()) {
				String name = keyIter.next();
				SortedSet<Data> d = data.get(name);
				Iterator<Data> dataIter = d.iterator();
				g.setColor( getColor(name) );
				while(dataIter.hasNext()) {
					Data t = dataIter.next();
					for(int a = 0; a<t.y.length; a++) {
						point.setLocation(t.x, t.y[a]);
						transform.transform(point, point);
						ellipse.setFrame(point.getX()-r,point.getY()-r,2*r,2*r);
						g.fill( ellipse );
					}
				}
			}
			
			
		}
	}
	
	static class TickMarkInfo {
		private static double[] increments = new double[] {
			.0001, .00025, .0005, .001, .0025, .005, .01, .025, .05, .1, .25, .5, 1, 2.5, 5, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000, 10000, 25000, 50000
		};
		
		SortedSet<Number> majorTicks = new TreeSet<Number>();
		SortedSet<Number> minorTicks = new TreeSet<Number>();
		SortedMap<Number, String> labels = new TreeMap<Number, String>();
		final double min, max;
		final int pixels;
		
		final double minorIncr;
		final double majorIncr;
		
		TickMarkInfo(double min,double max,int pixels) {
			this.min = min;
			this.max = max;
			this.pixels = pixels;

			//TODO: this should be improved to more abstractly handle other sizes
			double amount = max-min;
			double scale = (pixels) / amount;
			int majorIndex = increments.length-1;
			for(int i = increments.length-2; i>=0; i--) {
				if(increments[i]*scale<50) {
					majorIndex = i+1;
					break;
				}
			}
			minorIncr = increments[majorIndex-1];
			majorIncr = increments[majorIndex];
			
			defineMajorTicks();
			defineMinorTicks();
			defineLabels();
		}
		
		void defineMajorTicks() {
			int k = (int)(min/majorIncr);
			while(k*majorIncr<max) {
				majorTicks.add( k*majorIncr );
				k++;
			}
		}
		
		void defineMinorTicks() {
			int k = (int)(min/minorIncr);
			while(k*minorIncr<max) {
				minorTicks.add( k*minorIncr );
				k++;
			}
			//TODO: account for rounding
			minorTicks.removeAll(majorTicks);
		}
		
		void defineLabels() {
			Iterator<Number> iter = majorTicks.iterator();
			while(iter.hasNext()) {
				Number n = iter.next();
				String s = Double.toString(n.doubleValue());
				if(s.endsWith(".0")) {
					s = s.substring(0, s.length()-2);
				}
				labels.put(n, s );
			}
		}
	}
	
	SortedMap< String, SortedSet<Data> > data = new TreeMap<String, SortedSet<Data> >();
	Number domainMin = null;
	Number domainMax = null;
	Number rangeMin = null;
	Number rangeMax = null;
	Map< String, Color> colors = new HashMap<>();
	Point2D point = new Point2D.Double();
	Ellipse2D ellipse = new Ellipse2D.Float();
	GraphRepresentation representation = new GraphRepresentation();
	
	public JGraph() {
		Font font = UIManager.getFont("TableHeader.font");
		if(font!=null)
			setFont(font);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 0; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add( new YAxis(), c );
		c.gridx++; c.weightx = 1;
		add( representation, c);
		c.gridy++; c.weighty = 0;
		add( new XAxis(), c );
		
		setOpaque(false);
	}
	
	protected Color getColor(String name) {
		Color color = colors.get(name);
		if(color==null) return Color.black;
		return color;
	}
	

	/** Writes the contents of this graph to a tab-separated text file.
	 * 
	 * @param file the file to write to. If this is null, then a FileDialog
	 * will be invoked to choose a file.
	 * @throws IOException if an IO problem occurs.
	 */
	public void saveToFile(File file) throws IOException {
		if(file==null) {
			file = showSaveDialog();
			if(file==null) return;
		}

		//reorganize the data to a form we can iterate through by ascending x-values:
		SortedMap<Number, Map<String, double[]>> table = new TreeMap<Number, Map<String, double[]>>();
		
		Iterator<String> nameIter = data.keySet().iterator();
		SortedSet<String> allNames = new TreeSet<String>();
		while(nameIter.hasNext()) {
			String name = nameIter.next();
			allNames.add(name);
			SortedSet<Data> d = data.get(name);
			Iterator<Data> dataIter = d.iterator();
			while(dataIter.hasNext()) {
				Data d2 = dataIter.next();
				Number x = new Double(d2.getX());
				Map<String, double[]> tableValue = table.get(x);
				if(tableValue==null) {
					tableValue = new HashMap<String, double[]>();
					table.put(x, tableValue);
				}
				tableValue.put(name, d2.getY());
			}
		}
		
		FileOutputStream out = null;
		try {
			if(!file.exists()) file.createNewFile();
			out = new FileOutputStream(file, true);
			PrintStream ps = new PrintStream( out );
			StringBuffer sb = new StringBuffer();
			
			//the header row:
			sb.append("x");
			nameIter = allNames.iterator();
			while(nameIter.hasNext()) {
				sb.append("\t"+nameIter.next());
			}
			ps.println(sb.toString());
			
			//the data, sorted by ascending x values:
			Iterator<Number> rows = table.keySet().iterator();
			while(rows.hasNext()) {
				sb.delete(0, sb.length());
				Number x = rows.next();
				sb.append(x);
				Map<String, double[]> columns = table.get(x);
				nameIter = allNames.iterator();
				while(nameIter.hasNext()) {
					double[] values = columns.get(nameIter.next());
					if(values==null) {
						sb.append("\t#");
					} else {
						sb.append("\t"+toString(values));
					}
				}
				ps.println(sb.toString());
			}
			
			ps.close();
		} finally {
			if(out!=null) {
				try {
					out.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	static class TableValue implements Comparable<TableValue> {
		String name;
		double[] values;
		
		TableValue(String name,double[] values) {
			this.name = name;
			this.values = values;
		}

		public int compareTo(TableValue o) {
			return name.compareTo(o.name);
		}
	}
	
	static String toString(double[] array) {
		StringBuffer sb = new StringBuffer();
		sb.append( Double.toString(array[0]) );
		for(int a = 1; a<array.length; a++) {
			sb.append(", ");
			sb.append(array[a]);
		}
		return sb.toString();
	}
	
	File showSaveDialog() {
		FileDialog fd = null;
		Window w = SwingUtilities.getWindowAncestor(this);
		if(w instanceof Frame) {
			fd = new FileDialog( (Frame)w );
		} else if(w instanceof Dialog) {
			fd = new FileDialog( (Dialog)w );
		} else {
			throw new RuntimeException();
		}
		
		fd.setMode( FileDialog.SAVE );
		fd.setFile("Graph.txt");
		fd.setVisible(true);
		if(fd.getFile()==null) {
			return null;
		}
		String path = fd.getDirectory()+fd.getFile();
		if(path.toLowerCase().endsWith(".txt")==false)
			path = path + ".txt";
		File file = new File(path);
		return file;
	}
	
	public void add(String name,double x,double y) {
		if( Double.isInfinite( x ) || Double.isNaN( x ))
			throw new IllegalArgumentException("x = "+x);
		if( Double.isInfinite( y ) || Double.isNaN( y ))
			throw new IllegalArgumentException("y = "+y);
		
		SortedSet<Data> t = data.get(name);
		if(t==null) {
			t = new TreeSet<Data>();
			data.put(name, t);
			Data d = new Data(x, y);
			t.add(d);
		} else {
			Iterator<Data> iter = t.iterator();
			boolean added = false;
			while(iter.hasNext() && (!added)) {
				Data d = iter.next();
				if(d.x==x) {
					d.add(y);
					added = true;
				}
			}
			if(!added) {
				Data d = new Data(x, y);
				t.add(d);
			}
		}
		
		if(domainMin==null || domainMin.doubleValue()>x) {
			domainMin = x;
		}
		if(domainMax==null || domainMax.floatValue()<x) {
			domainMax = x;
		}
		if(rangeMin==null || rangeMin.doubleValue()>y) {
			rangeMin = y;
		}
		if(rangeMax==null || rangeMax.floatValue()<y) {
			rangeMax = y;
		}
		
		if(colors.containsKey(name)==false) {
			colors.put(name, generateColor(data.size()));
		}
		
		repaint();
	}
	
	Color generateColor(int randomSeed) {
		Random random = new Random(randomSeed*1000);
		float[] hsb = new float[3];
		while(true) {
			hsb[0] = random.nextFloat();
			hsb[1] = random.nextFloat();
			hsb[2] = random.nextFloat();
			if(hsb[2]>.4f && hsb[1]>.5f) {
				return new Color( Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]) );
			}
		}
	}
}
