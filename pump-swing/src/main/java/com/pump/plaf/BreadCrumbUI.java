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
package com.pump.plaf;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;
import javax.swing.plaf.basic.BasicLabelUI;

import com.pump.icon.PaddedIcon;
import com.pump.icon.TriangleIcon;
import com.pump.swing.JBreadCrumb;
import com.pump.swing.JBreadCrumb.BreadCrumbFormatter;
import com.pump.swing.NavigationListener;
import com.pump.swing.NavigationListener.ListSelectionType;

/** The ComponentUI for {@link com.pump.swing.JBreadCrumb}.
 */
public class BreadCrumbUI extends ComponentUI {
	protected static final String PATH_NODE_KEY = BreadCrumbUI.class.getName()+".pathNode";
	protected static final String PATH_NODE_INDEX_KEY = BreadCrumbUI.class.getName()+".pathNodeIndex";
	protected static final String INDICATED_KEY = BreadCrumbUI.class.getName()+".indicated";
	
	/** This client property of a <code>JBreadCrumb</code> defines the icon used
	 * as a separator.
	 */
	public static final String SEPARATOR_ICON_KEY = BreadCrumbUI.class.getName()+".separatorIcon";
	
	private static final String TARGET_WIDTH_KEY = BreadCrumbUI.class.getName()+".targetWidth";
	private static final String TIMER_KEY = BreadCrumbUI.class.getName()+".timer";
	
	protected Icon defaultSeparatorIcon = new PaddedIcon(new TriangleIcon( SwingConstants.EAST, 6, 6), new Insets(2,4,2,6));
	
	/** This is an option for debugging and demonstration. */
	boolean artificiallySlow = false;
	
	/** Create a new BreadCrumbUI. */
	public BreadCrumbUI() {
		this(false);
	}
	
	/** Create a new BreadCrumbUI.
	 * 
	 * @param artificiallySlow this is an option for debugging and demonstration.
	 */
	public BreadCrumbUI(boolean artificiallySlow) {
		this.artificiallySlow = artificiallySlow;
	}
	
	private class CrumbLayout implements LayoutManager {
		
		Icon separatorIcon;
		
		CrumbLayout(Icon separatorIcon) {
			this.separatorIcon = separatorIcon;
		}
		
		public void addLayoutComponent(String name, Component comp) {}
	
		public void removeLayoutComponent(Component comp) {}
	
		public Dimension preferredLayoutSize(Container parent) {
			JBreadCrumb<?> bc = (JBreadCrumb<?>)parent;
			JLabel[] crumbs = getCrumbs(bc);
			
			Dimension d = new Dimension(crumbs.length*separatorIcon.getIconWidth(), 0);
			for(JLabel c : crumbs) {
				Dimension preferred = c.getPreferredSize();
				d.width += preferred.width;
				d.height = Math.max(d.height, preferred.height);
			}
			d.height = Math.max(d.height, separatorIcon.getIconHeight());
			
			Border border = bc.getBorder();
			if(border!=null) {
				Insets i = bc.getBorder().getBorderInsets(parent);
				d.width += i.left + i.right;
				d.height += i.top + i.bottom;
			}
			return d;
		}
	
		public Dimension minimumLayoutSize(Container parent) {
			JBreadCrumb<?> bc = (JBreadCrumb<?>)parent;
			JLabel[] crumbs = getCrumbs(bc);
			Dimension d = new Dimension(crumbs.length*separatorIcon.getIconWidth(), 0);
			for(int a = 0; a<crumbs.length; a++) {
				Dimension minimum;
				if(a==crumbs.length-1) {
					Icon icon = crumbs[a].getIcon();
					if(icon!=null) {
						minimum = new Dimension(icon.getIconWidth(), icon.getIconHeight());
					} else {
						minimum = crumbs[a].getMinimumSize();
					}
				} else {
					minimum = crumbs[a].getMinimumSize();
				}
				d.width += minimum.width;
				d.height = Math.max(d.height, minimum.height);
			}

			d.height = Math.max(d.height, separatorIcon.getIconHeight());

			Border border = bc.getBorder();
			if(border!=null) {
				Insets i = bc.getBorder().getBorderInsets(parent);
				d.width += i.left + i.right;
				d.height += i.top + i.bottom;
			}
			
			return d;
		}
		
		/** Return the thumb that should be given the most horizontal weight. */
		private JLabel getPrioritizedCrumb(JLabel[] labels) {
			for(int a = 0; a<labels.length; a++) {
				Boolean b = (Boolean)labels[a].getClientProperty(INDICATED_KEY);
				if(Boolean.TRUE.equals(b))
					return labels[a];
			}
			return labels[labels.length-1];
		}
	
		public void layoutContainer(Container parent) {
			JBreadCrumb<?> jbc = (JBreadCrumb<?>)parent;
			JLabel[] crumbs = getCrumbs(jbc);
			Border border = jbc.getBorder();
			Insets i = border==null ? new Insets(0,0,0,0) : border.getBorderInsets(jbc);

			if(crumbs.length==0) {
				return;
			}
			
			Map<JLabel, Dimension> preferredSize = new HashMap<JLabel, Dimension>();
			for(JLabel l : crumbs) {
				preferredSize.put(l, l.getPreferredSize());
			}
			
			JLabel prioritizedCrumb = getPrioritizedCrumb(crumbs);
			Dimension prioritizedSize = preferredSize.get(prioritizedCrumb);
		
			if(crumbs.length==1) {
				prioritizedCrumb.setBounds(i.left,i.top,prioritizedSize.width,prioritizedSize.height);
				return;
			}

			int availableWidth = jbc.getWidth() - prioritizedSize.width - i.left - i.right;
			
			int expectedWidth = 0;
			int maxWidth = 0;
			for(int a = 0; a<crumbs.length; a++) {
				if(crumbs[a]!=prioritizedCrumb) {
					Dimension d = preferredSize.get(crumbs[a]);
					maxWidth = Math.max(maxWidth, d.width);
					expectedWidth += d.width + separatorIcon.getIconWidth();
				}
			}

			if(expectedWidth>availableWidth) {
				/* Our first line of defense:
				 * 
				 * If we're deep in a nested tree with a variation
				 * of object types, collapse as many as possible
				 */
				Class<?> k = crumbs[crumbs.length-1].getClientProperty(PATH_NODE_KEY).getClass();
				int firstAbnormality = -1;
				identifyFirstAbnormality : for(int j = crumbs.length-2; j>=0; j--) {
					Class<?> k2 = crumbs[j].getClientProperty(PATH_NODE_KEY).getClass();
					if(!k.equals(k2)) {
						firstAbnormality = j;
						break identifyFirstAbnormality;
					}
				}
				if(firstAbnormality!=-1) {
					for(int j = 0; j<=firstAbnormality; j++) {
						Icon icon = crumbs[j].getIcon();
						if(icon!=null && crumbs[j]!=prioritizedCrumb) {
							Dimension d = preferredSize.get(crumbs[j]);
							d.width = icon.getIconWidth();
							preferredSize.put(crumbs[j], d);
						}
					}
					
					//now re-tally everything:
					expectedWidth = 0;
					for(int a = 0; a<crumbs.length; a++) {
						if(crumbs[a]!=prioritizedCrumb) {
							Dimension d = preferredSize.get(crumbs[a]);
							expectedWidth += d.width + separatorIcon.getIconWidth();
						}
					}
				}
			}
			
			/* Our next (more rational) approach:
			 * just trim everything except the indicated crumb back
			 * until everything fits.
			 */
			
			while(expectedWidth>availableWidth) {
				expectedWidth = 0;
				for(int a = 0; a<crumbs.length; a++) {
					if(crumbs[a]!=prioritizedCrumb) {
						Dimension d = preferredSize.get(crumbs[a]);
						int labelWidth = Math.min(d.width, maxWidth);
						expectedWidth += labelWidth + separatorIcon.getIconWidth();
					}
				}
				maxWidth--;
			}
			
			int x = i.left;
			int middleHeight = jbc.getHeight() - i.top - i.bottom;
			for(int a = 0; a<crumbs.length; a++) {
				Dimension d = preferredSize.get(crumbs[a]);
				int width = d.width;
				int height = d.height;
				if(crumbs[a]!=prioritizedCrumb) {
					width = Math.min(maxWidth, width);
				}
				//label.setBounds(x, 0, width, height);
				setBounds(jbc, crumbs[a], x, i.top + (middleHeight-height)/2, width, height);
				
				x += width;
				x += separatorIcon.getIconWidth();
			}
		}
		
		/** Use a small animation to gradually change the bounds of a label.
		 * 
		 */
		private void setBounds(final JBreadCrumb<?> container, JLabel label,int x,int y,int width,int height) {
			if(!container.isShowing()) {
				label.setBounds(x,y,width,height);
				return;
			}
			Rectangle bounds = label.getBounds();
			bounds.y = y;
			bounds.height = height;
			label.setBounds(bounds);
			
			if(bounds.x==x && bounds.width==width) {
				label.putClientProperty(TARGET_WIDTH_KEY, null);
			} else {
				label.putClientProperty(TARGET_WIDTH_KEY, width);
			}
			Timer timer = (Timer)container.getClientProperty(TIMER_KEY);
			if(timer==null) {
				ActionListener actionListener = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JLabel[] labels = getCrumbs(container);

						Border border = container.getBorder();
						Insets i = border==null ? new Insets(0,0,0,0) : border.getBorderInsets(container);
						
						boolean changed = false;
						int x = i.left;
						for(int a = 0; a<labels.length; a++) {
							Rectangle bounds = labels[a].getBounds();
							
							Integer targetWidth = (Integer)labels[a].getClientProperty(TARGET_WIDTH_KEY);
							if(targetWidth==null) {
								targetWidth = bounds.width;
							}
							if(bounds.x!=x) {
								bounds.x = x;
								changed = true;
							}
							int newWidth;
							
							int k1 = artificiallySlow ? 1 : 10;
							int k2 = artificiallySlow ? 1 : 3;
							
							if(targetWidth<bounds.width) {
								if(Math.abs(bounds.width - targetWidth)>20) {
									newWidth = Math.max(targetWidth, bounds.width - k1);
								} else {
									newWidth = Math.max(targetWidth, bounds.width - k2);
								}
							} else if(targetWidth>bounds.width) {
								if(Math.abs(bounds.width - targetWidth)>20) {
									newWidth = Math.min(targetWidth, bounds.width + k1);
								} else {
									newWidth = Math.min(targetWidth, bounds.width + k2);
								}
							} else {
								newWidth = bounds.width;
							}
							
							if(newWidth!=bounds.width) {
								bounds.width = newWidth;
								changed = true;
							}
							
							labels[a].setBounds(bounds);

							x += bounds.width;
							x += separatorIcon.getIconWidth();
						}
						if(!changed) {
							Timer timer = (Timer)container.getClientProperty(TIMER_KEY);
							timer.stop();
						} else {
							container.repaint();
						}
					}
				};
				timer = new Timer( artificiallySlow ? 50 : 25, actionListener);
				container.putClientProperty(TIMER_KEY, timer);
			}
			timer.start();
		}
	
	}
	
	/** Return the labels that exactly correspond to <code>jbc.getPath()</code>
	 * in the analogous order.
	 */
	protected static JLabel[] getCrumbs(JBreadCrumb<?> jbc) {
		TreeMap<Integer, JLabel> map = new TreeMap<Integer, JLabel>();
		for(int a = 0; a<jbc.getComponentCount(); a++) {
			Component c = jbc.getComponent(a);
			if(c instanceof JLabel) {
				JLabel label = (JLabel)c;
				Integer i = (Integer)label.getClientProperty(PATH_NODE_INDEX_KEY);
				if(i!=null) {
					map.put(i, label);
				}
			}
		}
		JLabel[] array = new JLabel[map.size()];
		int ctr = 0;
		for(Integer key : map.keySet() ) {
			array[ctr++] = map.get(key);
		}
		return array;
	}
	
	PropertyChangeListener refreshUIListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			JBreadCrumb<?> comp = (JBreadCrumb<?>)evt.getSource();
			refreshUI(comp);
		}
	};
	
	class LabelMouseListener<T> extends MouseAdapter {

		private JLabel getLabel(Container parent,Point p) {
			for(int a = 0; a<parent.getComponentCount(); a++) {
				Component comp = parent.getComponent(a);
				if(comp.getBounds().contains(p) && comp instanceof JLabel)
					return (JLabel)comp;
			}
			return null;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			JBreadCrumb jbc = (JBreadCrumb)e.getComponent();
			JLabel label = getLabel(jbc, e.getPoint());
			if(label==null) return;
			
			List<NavigationListener<T>> listeners = jbc.getNavigationListeners();
			for(int a = 0; a<listeners.size(); a++) {
				NavigationListener<T> listener = listeners.get(a);
				ListSelectionType type = e.getClickCount()==2 ? ListSelectionType.DOUBLE_CLICK : ListSelectionType.SINGLE_CLICK;
				T element = (T)label.getClientProperty(PATH_NODE_KEY);

				//stupid ClassCastException and varargs require a little fancy footwork here:
				T[] array = (T[])Array.newInstance(element.getClass(), 1);
				array[0] = element;
				
				listener.elementsSelected(type, array);
			}
			
		}
	};
	
	class ContainerMouseListener extends MouseAdapter {
		JComponent container;
		
		public ContainerMouseListener(JComponent container) {
			this.container = container;
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			JComponent label = getClosestChild(e.getX(), e.getY());
			setIndicatedComponent(label);
		}
		
		private JComponent getClosestChild(int x,int y) {
			int minDistance = Integer.MAX_VALUE;
			JComponent minComp = null;
			for(int a = 0; a<container.getComponentCount(); a++) {
				if(container.getComponent(a) instanceof JComponent) {
					JComponent child = (JComponent)container.getComponent(a);
					Rectangle bounds = child.getBounds();
					if(bounds.x <= x && x<= bounds.x+bounds.width) {
						return child;
					} else if(x < bounds.x) {
						int distance = bounds.x - x;
						if(minComp == null || distance < minDistance) {
							minComp = child;
							minDistance = distance;
						}
					} else {
						int distance = x - (bounds.x + bounds.width);
						if(minComp == null || distance < minDistance) {
							minComp = child;
							minDistance = distance;
						}
					}
				}
			}
			return minComp;
		}
		
		private void setIndicatedComponent(JComponent label) {
			boolean changed = false;
			for(int a = 0; a<container.getComponentCount(); a++) {
				if(container.getComponent(a) instanceof JComponent) {
					JComponent child = (JComponent)container.getComponent(a);
					Boolean b = (Boolean)child.getClientProperty(INDICATED_KEY);
					if(b==null) b = false;
					Boolean indicated = label==child;
					if( !b.equals(indicated) ) {
						changed = true;
						child.putClientProperty(INDICATED_KEY, indicated);
					}
				}
			}
			if(changed) {
				container.invalidate();
				container.revalidate();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			setIndicatedComponent(null);
		}
	};
	
	public void setDefaultSeparatorIcon(Icon icon) {
		if(icon==null)
			throw new NullPointerException();
		defaultSeparatorIcon = icon;
	}
	
	/** Return the icon the argument wants to use for separators. */
	protected Icon getSeparatorIcon(JBreadCrumb<?> comp) {
		Icon icon = (Icon)comp.getClientProperty(SEPARATOR_ICON_KEY);
		if(icon==null) return defaultSeparatorIcon;
		return icon;
	}
	

	/** Return the crumb at a given location.
	 * 
	 * @param comp the JBreadCrumb
	 * @param p a point relative to the JBreadCrumb
	 * @return the crumb at the given point, or null.
	 */
	public <E> E getCrumb(JBreadCrumb<E> comp,Point p) {
		for(int a = 0; a<comp.getComponentCount(); a++) {
			Component c = comp.getComponent(a);
			if(c.getBounds().contains(p) &&
					c instanceof JLabel && 
					((JLabel)c).getClientProperty(PATH_NODE_KEY)!=null ) {
				JLabel l = (JLabel)c;
				return (E)l.getClientProperty(PATH_NODE_KEY);
			}
		}
		return null;
	}
	
	protected <E> void refreshUI(final JBreadCrumb<E> comp) {
		E[] path = comp.getPath();
		Icon separatorIcon = getSeparatorIcon(comp);
		comp.setLayout( new CrumbLayout(separatorIcon) );
		
		BreadCrumbFormatter<E> formatter = comp.getFormatter();
		
		int ctr = 0;
		Set<JLabel> componentsToRemove = new HashSet<JLabel>();
		for(int a = 0; a<comp.getComponentCount(); a++) {
			Component c = comp.getComponent(a);
			if(c instanceof JLabel && ((JLabel)c).getClientProperty(PATH_NODE_KEY)!=null ) {
				JLabel l = (JLabel)c;
				if(path!=null && ctr<path.length) {
					l.putClientProperty(PATH_NODE_KEY, path[ctr]);
					l.putClientProperty(PATH_NODE_INDEX_KEY, ctr);
					formatter.format(comp, l, path[ctr], ctr);
					ctr++;
				} else {
					l.setSize(l.getPreferredSize());
					componentsToRemove.add(l);
				}
			}
		}
		
		for(JLabel l : componentsToRemove) {
			comp.remove(l);
		}
		while(path!=null && ctr<path.length) {
			JLabel newLabel = new JLabel();
			LabelUI ui = getLabelUI();
			if(ui!=null)
				newLabel.setUI(ui);
			newLabel.putClientProperty(PATH_NODE_KEY, path[ctr]);
			newLabel.putClientProperty(PATH_NODE_INDEX_KEY, ctr);
			formatter.format(comp, newLabel, path[ctr], ctr);
			comp.add(newLabel);
			newLabel.setSize(newLabel.getPreferredSize());
			ctr++;
		}
		
		comp.invalidate();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				comp.revalidate();
				comp.repaint();
			}
		});
	}
	
	static class FadingLabelUI extends BasicLabelUI {
		protected static final String FADE_OUT = FadingLabelUI.class.getName()+".fade-out";

		@Override
		public void paint(Graphics g0, JComponent c) {
			Boolean b = (Boolean)c.getClientProperty(FADE_OUT);
			if(b==null) b = Boolean.FALSE;

			BufferedImage bi = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics2D g2 = bi.createGraphics();
			g2.setRenderingHints( ((Graphics2D)g0).getRenderingHints() );
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			super.paint(g2, c);
			if(b) {
				g2.setComposite(AlphaComposite.DstOut);
				g2.setPaint(new GradientPaint(Math.max(0, c.getWidth()-10),0,new Color(0,0,0,0), c.getWidth(),0,new Color(0,0,0)));
				g2.fillRect(0,0,c.getWidth(),c.getHeight());
			}
			g2.dispose();
		
			g0.drawImage(bi, 0, 0, null);
		}

		protected String layoutCL(
	        JLabel label,
	        FontMetrics fontMetrics,
	        String text,
	        Icon icon,
	        Rectangle viewR,
	        Rectangle iconR,
	        Rectangle textR)
	    {
	    	String returnValue = super.layoutCL(label, fontMetrics, text, icon, viewR, iconR, textR);
	    	//disregard "returnValue" and just return the text:
	    	//this avoids forcing ellipses
	    	label.putClientProperty(FADE_OUT, !returnValue.equals(text));
	    	
	    	return text;
	    }
		
	}
	
	/** Return the LabelUI each crumb should use. */
	protected LabelUI getLabelUI() {
		return null; //return new FadingLabelUI();
	}

    public static ComponentUI createUI(JComponent c) {
        return new BreadCrumbUI();
    }

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		ContainerMouseListener cml = new ContainerMouseListener(c);
		c.addMouseMotionListener(cml);
		c.addMouseListener(new LabelMouseListener());
		c.putClientProperty(ContainerMouseListener.class.getName(), cml);
		
		c.addPropertyChangeListener(JBreadCrumb.PATH_KEY, refreshUIListener);
		c.addPropertyChangeListener(JBreadCrumb.FORMATTER_KEY, refreshUIListener);
		c.addPropertyChangeListener(SEPARATOR_ICON_KEY, refreshUIListener);
		refreshUI( (JBreadCrumb<?>)c );
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		ContainerMouseListener cml = (ContainerMouseListener)c.getClientProperty(ContainerMouseListener.class.getName());
		if(cml!=null) {
			c.removeMouseMotionListener(cml);
			c.putClientProperty(ContainerMouseListener.class.getName(), null);
		}
		c.putClientProperty(TIMER_KEY, null);
		c.removePropertyChangeListener(JBreadCrumb.PATH_KEY, refreshUIListener);
		c.removePropertyChangeListener(JBreadCrumb.FORMATTER_KEY, refreshUIListener);
		c.removePropertyChangeListener(SEPARATOR_ICON_KEY, refreshUIListener);
	}

	/** Paint a triangle between every label.
	 * 
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		JBreadCrumb<?> jbc = (JBreadCrumb<?>)c;
		Icon separatorIcon = getSeparatorIcon(jbc);
		JLabel[] crumbs = getCrumbs( jbc );
		Insets insets = new Insets(0,0,0,0);
		Border b = c.getBorder();
		if(b!=null)
			insets = b.getBorderInsets(c);
		for(int a = 0; a<crumbs.length-1; a++) {
			Rectangle r = crumbs[a].getBounds();
			int x = r.x + r.width;
			int y = (c.getHeight() - insets.bottom - insets.top)/2 + insets.top - separatorIcon.getIconHeight()/2;
			separatorIcon.paintIcon(c, g, x, y );
		}
	}
}