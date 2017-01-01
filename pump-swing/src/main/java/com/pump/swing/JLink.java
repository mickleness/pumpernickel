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
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/** This JLabel resembles a hyperlink, and ActionListeners
 * can be attached so it acts like a button.
 * <P>This has 3 colors:
 * <ul><LI>Default</li>
 * <LI>Indicated: used when the mouse is over this component</li>
 * <LI>Selected: used when the mouse is pressed over this component,
 * or when the space bar is pressed when this link has the keyboard focus.</LI></ul>
 * <P>You can configure these colors yourself, but by default they come in
 * "standard" blue-ish shades.  (On Vista the default color is designed to
 * match existing screenshots I found in the Vista interface guidelines, but
 * otherwise the default color is <code>Color.blue</code>.)
 * <P>A dotted border is displayed around this component when it has the
 * keyboard focus.
 *
 */
public class JLink extends JLabel {
	private static final long serialVersionUID = 1L;

	/** This is a 1-pixel dotted border.
	 */
	static class DottedLineBorder implements Border {
		Color color;
		
		public DottedLineBorder(Color c) {
			this.color = c;
		}
		
		public Insets getBorderInsets(Component c)
		{
			return new Insets(1,1,1,1);
		}
	
		public boolean isBorderOpaque()
		{
			return false;
		}
	
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
		{
			((Graphics2D)g).setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,10,new float[] {1,1},0));
			g.drawRect(x,y,width-1,height-1);
		}
	}
	
	public static ActionListener defaultActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if(src instanceof JLink) {
				JLink link = (JLink)src;
				String text = link.getText();
				try {
					Desktop.getDesktop().browse(new URI(text));
				} catch(Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	};
	
	boolean drawLine = true;
	static Border unfocusedBorder = new EmptyBorder(3,3,3,3);
	static Border focusedBorder = new CompoundBorder(
			new DottedLineBorder(new Color(0,0,150)),
			new EmptyBorder(2,2,2,2));
	
	MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			if(isEnabled()) {
				setForeground(selectedColor);
				requestFocus();
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			if(isEnabled()) {
				fireActionListeners();
				if(contains(e.getPoint())) {
					setForeground(indicatedColor);
				} else {
					setForeground(defaultColor);
				}
			}
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			if(isEnabled()) {
				setForeground(indicatedColor);
			}
		}
		@Override
		public void mouseExited(MouseEvent e) {
			setForeground(defaultColor);
		}
	};
	
	ActionListener resetColor = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			setForeground(defaultColor);
		}
	};
	
	KeyListener keyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			int i = e.getKeyCode();
			if(i==KeyEvent.VK_SPACE) {
				setForeground(selectedColor);
				fireActionListeners();
				Timer timer = new Timer(500,resetColor);
				timer.setRepeats(false);
				timer.start();
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			int i = e.getKeyCode();
			if(i==KeyEvent.VK_SPACE) {
				setForeground(defaultColor);
			}
		}
	};
	
	List<ActionListener> listeners = new ArrayList<ActionListener>();
	
	/** This listener will be notifier when the user activates this link. */
	public void addActionListener(ActionListener l) {
		if(listeners.contains(l))
			return;
		listeners.add(l);
	}
	
	public void removeActionListener(ActionListener l) {
		listeners.remove(l);
	}
	
	protected void fireActionListeners() {
		for(int a = 0; a<listeners.size(); a++) {
			ActionListener l = listeners.get(a);
			try {
				l.actionPerformed(new ActionEvent(this,0,getText()));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
    
    private static boolean isVista() {
    	String osName = System.getProperty("os.name").toLowerCase();
    	return (osName.indexOf("vista")!=-1);
    }
	
	private Color defaultColor = isVista() ? new Color(0,102,204) : Color.blue;
	private Color indicatedColor = new Color(0,0,100);
	private Color selectedColor = Color.black;
	
	/** Create a new, empty JLink. */
	public JLink() {
		super();
		initialize();
	}
	
	/** Create a JLink presenting the text provided. */
	public JLink(String text) {
		super(text);
		initialize();
	}
	
	/** Create a JLink presenting the text provided. */
	public JLink(String text,final URL url) {
		super(text);
		initialize();
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(url.toURI());
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	/** Adds the necessary properties/listeners. */
	private void initialize() {
		setRequestFocusEnabled(true);
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				setBorder(focusedBorder);
			}
			
			public void focusLost(FocusEvent e) {
				setBorder(unfocusedBorder);
			}
		});
		setFocusable(true);
		setBorder(unfocusedBorder);
		setForeground(defaultColor);
		
		addMouseListener(mouseListener);
		addKeyListener(keyListener);
	}
	
	/** Controls whether the line is painted under the link text. */
	public void setDrawLine(boolean b) {
		drawLine = b;
	}
	
	FontRenderContext frc = new FontRenderContext(new AffineTransform(),true,true);
	@Override
	public void paint(Graphics g) {
	  super.paint(g);
		
		if(drawLine) {
			LineMetrics m = getFont().getLineMetrics(getText(),frc);
			Insets i = getInsets();
			int descent = (int)m.getDescent()-4;
			if(isEnabled()) {
				g.setColor(getForeground());
			} else {
				g.setColor(SystemColor.textInactiveText);
			}
			g.drawLine(i.left,getHeight()-i.bottom-descent,getWidth()-i.right-1,getHeight()-i.bottom-descent);
		}
	}
	
	public void setDefaultColor(Color c) {
		defaultColor = c;
		repaint();
	}
	public void setIndicatedColor(Color c) {
		indicatedColor = c;
		repaint();
	}
	public void setSelectedColor(Color c) {
		selectedColor = c;
		repaint();
	}
	public Color getDefaultColor() {
		return defaultColor;
	}
	public Color getIndicatedColor() {
		return indicatedColor;
	}
	public Color getSelectedColor() {
		return selectedColor;
	}
}