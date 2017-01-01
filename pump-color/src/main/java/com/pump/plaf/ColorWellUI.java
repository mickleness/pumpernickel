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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.TexturePaint;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.ComponentUI;

import com.pump.swing.ColorPalette;
import com.pump.swing.ColorPicker;
import com.pump.swing.ColorWell;

public class ColorWellUI extends ComponentUI {
	
	/** This client property maps to an ActionListener that is notified when the down arrow key is pressed. */
	public static final String DOWN_KEY_ACTION_PROPERTY = "downKeyAction";
	
	/** This client property maps to an ActionListener that is notified when the space key is pressed. */
	public static final String SPACE_KEY_ACTION_PROPERTY = "spaceKeyAction";
	
	/** This client property maps to an ActionListener that is notified when user clicks on a ColorWell exactly once. */
	public static final String SINGLE_CLICK_ACTION_PROPERTY = "singleClickAction";
	
	/** This client property maps to an ActionListener that is notified when user double-clicks on a ColorWell. */
	public static final String DOUBLE_CLICK_ACTION_PROPERTY = "doubleClickAction";
	
	/** This client property maps to a JPopupMenu.
	 */
	public static final String POPUP_PALETTE_PROPERTY = "palettePopup";
	
	static int DOUBLE_CLICK_THRESHOLD = 500;
	
	
	static ActionListener colorPickerActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			Window owner = SwingUtilities.getWindowAncestor(well);
			Color newValue = ColorPicker.showDialog(owner, well.getColor());
			if(newValue!=null) {
				well.setColor(newValue);
			}
		}
	};

	static ActionListener colorPaletteActionListener = new ActionListener() {
		KeyListener popupKeyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ESCAPE || 
						e.getKeyCode()==KeyEvent.VK_ENTER || 
						e.getKeyCode()==KeyEvent.VK_TAB) {
					ColorPalette palette = (ColorPalette)e.getSource();
					JPopupMenu popup = (JPopupMenu)palette.getParent();
					popup.setVisible(false);
				}
			}
		};
		public void actionPerformed(ActionEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			JPopupMenu popup = (JPopupMenu)well.getClientProperty(POPUP_PALETTE_PROPERTY);
			ColorPalette colorPalette;
			if(popup==null) {
				popup = new JPopupMenu();
				colorPalette = new ColorPalette();
				colorPalette.addKeyListener(popupKeyListener);
				colorPalette.setBorder(null);
				colorPalette.putClientProperty(ColorSet.PALETTE_STYLE_PROPERTY, ColorSet.PALETTE_STYLE_GRADIENT);
				colorPalette.putClientProperty(ColorSet.PALETTE_CELL_STYLE_PROPERTY, ColorSet.PALETTE_CELL_STYLE_SHADOW);
				colorPalette.putClientProperty(HSBColorPaletteUI.PALETTE_PADDING_PROPERTY, "nonsaturated");
				colorPalette.putClientProperty(ColorPaletteUI.PREFERRED_CELL_WIDTH_PROPERTY, new Integer(20));
				colorPalette.putClientProperty(ColorPaletteUI.PREFERRED_CELL_HEIGHT_PROPERTY, new Integer(20));
				popup.add(colorPalette);
				well.putClientProperty(POPUP_PALETTE_PROPERTY, popup);
				well.bind(colorPalette);
				
				ColorPaletteUI ui = colorPalette.getUI();
				if(ui instanceof ScrollableColorPaletteUI) {
					ScrollableColorPaletteUI sui = (ScrollableColorPaletteUI)ui;
					JScrollBar scrollBar = sui.getHorizontalScrollBar(colorPalette);
					scrollBar.setEnabled(!well.isOpaqueColors());
				}
				
				
				final JPopupMenu popupRef = popup;
				well.addPropertyChangeListener("Frame.active",new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						popupRef.setVisible(false);
					}
				});
			} else {
				colorPalette = (ColorPalette)popup.getComponent(0);
			}
			popup.show(well, 1, well.getHeight()-1);

			final ColorPalette colorPaletteRef = colorPalette;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					colorPaletteRef.requestFocus();
				}
			});
		}
	};
	
	static {
		int uiThreshold = UIManager.getInt("doubleClickThreshold");
		if(uiThreshold!=0)
			DOUBLE_CLICK_THRESHOLD = uiThreshold;
	}
	
	protected static final ChangeListener colorChangeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			JComponent jc = (JComponent)e.getSource();
			jc.repaint();
		}
	};
	protected static final FocusListener repaintFocusListener = new FocusListener() {
		public void focusGained(FocusEvent e) {
			JComponent jc = (JComponent)e.getSource();
			jc.repaint();
		}
		public void focusLost(FocusEvent e) {
			JComponent jc = (JComponent)e.getSource();
			jc.repaint();
		}
	};
	
	protected static final MouseInputAdapter mouseListener = new MouseInputAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mousePressed(MouseEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processMouseEvent(e);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processMouseEvent(e);
		}
	};
	protected static final KeyListener keyListener = new KeyListener() {

		public void keyPressed(KeyEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processKeyEvent(e);
		}

		public void keyReleased(KeyEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processKeyEvent(e);
		}

		public void keyTyped(KeyEvent e) {
			ColorWell well = (ColorWell)e.getSource();
			well.getUI().processKeyEvent(e);
		}
	};
	
	protected void processKeyEvent(KeyEvent e) {
		int code = e.getKeyCode();
		ColorWell well = (ColorWell)e.getSource();
		if(code==KeyEvent.VK_SPACE) {
			ActionListener actionListener = (ActionListener)well.getClientProperty(SPACE_KEY_ACTION_PROPERTY);
			if(actionListener!=null)
				actionListener.actionPerformed(new ActionEvent(well,e.getID(),"space",e.getWhen(),e.getModifiers()));
		} else if(code==KeyEvent.VK_DOWN) {
			ActionListener actionListener = (ActionListener)well.getClientProperty(DOWN_KEY_ACTION_PROPERTY);
			if(actionListener!=null)
				actionListener.actionPerformed(new ActionEvent(well,e.getID(),"down",e.getWhen(),e.getModifiers()));
		}
	}
	
	protected void processMouseEvent(MouseEvent e) {
		ColorWell well = (ColorWell)e.getSource();
		
		ActionListener singleClickListener = (ActionListener)well.getClientProperty(SINGLE_CLICK_ACTION_PROPERTY);
		ActionListener doubleClickListener = (ActionListener)well.getClientProperty(DOUBLE_CLICK_ACTION_PROPERTY);
	
		if(e.getID()==MouseEvent.MOUSE_PRESSED) {
			well.requestFocus();
		}
		
		if(e.getID()==MouseEvent.MOUSE_PRESSED && e.getButton()==MouseEvent.BUTTON1) {
			Long lastPress = (Long)well.getClientProperty("lastMousePress");
			Long currentPress = new Long(e.getWhen());
			well.putClientProperty("lastMousePress", currentPress);
			
			if(doubleClickListener==null) {
				if(singleClickListener!=null)
					singleClickListener.actionPerformed(new ActionEvent(well,e.getID(),"single click",e.getWhen(),e.getModifiers()));
			} else {
				if(lastPress==null || currentPress.longValue()-lastPress.longValue()>DOUBLE_CLICK_THRESHOLD) {
					if(singleClickListener!=null) {
						SingleClickTimer singleClickTimer = new SingleClickTimer(well,DOUBLE_CLICK_THRESHOLD,currentPress,singleClickListener,e);
						singleClickTimer.start();
					}
				} else {
					doubleClickListener.actionPerformed(new ActionEvent(well,e.getID(),"double click",e.getWhen(),e.getModifiers()));
				}
			}
		}
	}
	
	
	private static ActionListener timerListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SingleClickTimer timer = (SingleClickTimer)e.getSource();
			timer.checkTime();
		}
	};
	
	static class SingleClickTimer extends Timer {
		private static final long serialVersionUID = 1L;
		
		ColorWell well;
		Long timeStamp;
		MouseEvent trigger;
		ActionListener actionListener;
		public SingleClickTimer(ColorWell well,int doubleClickThreshold,Long timeStamp,ActionListener actionListener,MouseEvent trigger) {
			super(doubleClickThreshold, timerListener);
			this.well = well;
			this.timeStamp = timeStamp;
			this.actionListener = actionListener;
			this.trigger = trigger;
			setRepeats(false);
		}
		
		protected void checkTime() {
			Long currentStamp = (Long)well.getClientProperty("lastMousePress");
			if(currentStamp!=null && currentStamp.equals(timeStamp)) {
				actionListener.actionPerformed(new ActionEvent(well,trigger.getID(),"single click",trigger.getWhen(),trigger.getModifiers()));
			}
			stop();
		}
	}

	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D)g0;
		ColorWell well = (ColorWell)c;
		Color color = well.getColor();
		Border border = c.getBorder();
		Insets borderInsets = border.getBorderInsets(c);
		if(color.getAlpha()<255) {
			TexturePaint checkers = PlafPaintUtils.getCheckerBoard(8);
			g.setPaint(checkers);
			g.fillRect(borderInsets.left, borderInsets.top, 
					c.getWidth()-borderInsets.left-borderInsets.right, 
					c.getHeight()-borderInsets.top-borderInsets.bottom);
		}
		g.setColor(color);
		g.fillRect(borderInsets.left, borderInsets.top, 
				c.getWidth()-borderInsets.left-borderInsets.right, 
				c.getHeight()-borderInsets.top-borderInsets.bottom);
	}

	@Override
	public void installUI(JComponent c) {
		c.addFocusListener(repaintFocusListener);
		c.addMouseListener(mouseListener);
		c.addMouseMotionListener(mouseListener);
		c.addKeyListener(keyListener);

		ColorWell well = (ColorWell)c;
		well.addChangeListener(colorChangeListener);
		
		c.putClientProperty(DOUBLE_CLICK_ACTION_PROPERTY, colorPickerActionListener);
		c.putClientProperty(SPACE_KEY_ACTION_PROPERTY, colorPickerActionListener);
		c.putClientProperty(SINGLE_CLICK_ACTION_PROPERTY, colorPaletteActionListener);
		c.putClientProperty(DOWN_KEY_ACTION_PROPERTY, colorPaletteActionListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		c.removeFocusListener(repaintFocusListener);
		c.removeMouseListener(mouseListener);
		c.removeMouseMotionListener(mouseListener);
		c.removeKeyListener(keyListener);

		ColorWell well = (ColorWell)c;
		well.removeChangeListener(colorChangeListener);
	}
}