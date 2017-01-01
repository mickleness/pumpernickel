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

import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/** This provides a context menu for a component that lets
 * the user adjust the RenderingHints being used.
 * <P>This is intended as a tool for developers; not for
 * general use.
 * <P>Note this menu cannot actually change the <code>RenderingHints</code>
 * of a component unless you override the <code>paint()</code> method.
 * <P>For example, a possible demo of this component might look like this:
 * <BR>
 * <BR><code>RenderingHintsContextMenu myContextMenu = new RenderingHintsContextMenu(myComponent)</code>
 * <BR><code>JComponent myComponent = new JComponent() {</code>
 * <BR><code> &nbsp; public void paint(Graphics g) {</code>
 * <br><code> &nbsp; &nbsp; ((Graphics2D)g).setRenderingHints(myContextMenu.getRenderingHints());</code>
 * <br><code> &nbsp; &nbsp; ... paint something</code>
 * <br><code> &nbsp; }</code>
 * <br><code>};</code>
 * <P>The component that this menu is created for will receive
 * a call to <code>repaint()</code> whenever a hint is changed.
 *
 */
public class RenderingHintsContextMenu extends JPopupMenu {
	private static final long serialVersionUID = 1L;
	
	/** The component this context menu is associated with.
	 */
	JComponent jc;
	
	/** A list of ChangeListeners */
	List<ChangeListener> listeners = new ArrayList<ChangeListener>();

	/** Constructs a new <code>RenderingHintsContextMenu</code> that
	 * includes every available hint.
	 * 
	 * @param jc the component to repaint when a hint is changed.
	 * Note the <code>paint()</code> must be designed to consult this
	 * object for the correct rendering hints.
	 */
	public RenderingHintsContextMenu(JComponent jc) {
		this(jc, null);
	}

	/** Constructs a new <code>RenderingHintsContextMenu</code> that
	 * includes only the specified hints.
	 * 
	 * @param jc the component to repaint when a hint is changed.
	 * Note the <code>paint()</code> must be designed to consult this
	 * object for the correct rendering hints.
	 * @param keys the keys this menu should include.
	 */
	public RenderingHintsContextMenu(JComponent jc,RenderingHints.Key[] keys) {
		this.jc = jc;

		Class<?> c = RenderingHints.class;
		Field[] f = c.getFields();
		
		//identify the keys:
		for(int a = 0; a<f.length; a++) {
			String name = f[a].getName();
			if(name.startsWith("KEY_")) {
				try {
					RenderingHints.Key key = (RenderingHints.Key)f[a].get(null);
					
					boolean include = false;
					if(keys==null) {
						include = true; //include everything by default
					} else {
						for(int b = 0; b<keys.length; b++) {
							if(keys[b]==key)
								include = true;
						}
					}
					
					if(include) {
						HintInfo hintInfo = new HintInfo(key, name, rename(name,4));
						add(hintInfo);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		
		//identify the values for each key
		for(int a = 0; a<f.length; a++) {
			String name = f[a].getName();
			if(name.startsWith("VALUE_")) {
				String tag = name.substring(6);
				int i = tag.indexOf('_');
				if(i!=-1)
					tag = tag.substring(0,i);
				
				HintInfo hi = null;
				
				for(int b = 0; b<getComponentCount(); b++) {
					HintInfo t = (HintInfo)getComponent(b);
					if(t.keyName.indexOf(tag)==4) { //should start after "KEY_"
						hi = t;
					}
				}
				
				if(hi!=null) {
					try {
						hi.addOption(f[a].get(null), name, rename(name,6));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}

		//install:

		MouseListener mouseListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.isPopupTrigger()) {
					JComponent jc = (JComponent)e.getSource();
					RenderingHintsContextMenu.this.show(jc,e.getX(),e.getY());
					e.consume();
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				mousePressed(e);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				mousePressed(e);
			}
		};
		jc.addMouseListener(mouseListener);
	}
	
	public void addChangeListener(ChangeListener l) {
		if(listeners.contains(l))
			return;
		listeners.add(l);
	}
	
	public void removeChangeListener(ChangeListener l) {
		listeners.remove(l);
	}
	
	protected void fireChangeListeners() {
		for(int a = 0; a<listeners.size(); a++) {
			ChangeListener l = listeners.get(a);
			try {
				l.stateChanged(new ChangeEvent(this));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** Assigns the rendering hints this menu represents. */
	public void setRenderingHint(RenderingHints.Key key,Object value) {
		for(int a = 0; a<getComponentCount(); a++) {
			if(getComponent(a) instanceof HintInfo) {
				HintInfo hintInfo = (HintInfo)getComponent(a);
				if(hintInfo.key.equals(key)) {
					hintInfo.setSelectedValue(value);
					return;
				}
			}
		}
		throw new IllegalArgumentException("the key \""+key+"\" is not present in this menu.");
	}
	
	/** Returns the keys this contextual menu current
	 * represents.
	 */
	public RenderingHints getRenderingHints() {
		Map<RenderingHints.Key, Object> table = new HashMap<>();
		for(int a = 0; a<this.getComponentCount(); a++) {
			if(getComponent(a) instanceof HintInfo) {
				HintInfo hintInfo = (HintInfo)getComponent(a);
				Object hintValue = hintInfo.getSelectedValue();
				if(hintValue!=null) {
					table.put(hintInfo.key, hintValue);
				}
			}
		}
		return new RenderingHints(table);
	}
	
	class HintInfo extends JMenu {
		private static final long serialVersionUID = 1L;
		
		RenderingHints.Key key;
		String keyName;
		String keyUserName;
		
		JCheckBoxMenuItem undefined = new JCheckBoxMenuItem("Undefined",true);
		
		ActionListener actionListener = new ActionListener() {
			boolean adjusting = false;
			public void actionPerformed(ActionEvent e) {
				if(adjusting)
					return;
				
				JCheckBoxMenuItem i = (JCheckBoxMenuItem)e.getSource();
				adjusting = true;
				for(int a = 0; a<getItemCount(); a++) {
					if(getItem(a) instanceof JCheckBoxMenuItem) {
						JCheckBoxMenuItem i2 = (JCheckBoxMenuItem)getItem(a);
						i2.setSelected(i==i2);
					}
				}
				adjusting = false;
				jc.repaint();
				fireChangeListeners();
			}
		};
		
		public HintInfo(RenderingHints.Key key,String keyName,String keyUserName) {
			super(keyUserName);
			this.key = key;
			this.keyName = keyName;
			this.keyUserName = keyUserName;
			add(undefined);
			addSeparator();
			undefined.addActionListener(actionListener);
		}
		
		public void setSelectedValue(Object value) {
			for(int a = 0; a<getItemCount(); a++) {
				if(getItem(a) instanceof JCheckBoxMenuItem) {
					JCheckBoxMenuItem item = (JCheckBoxMenuItem)getItem(a);
					Object itemValue = item.getClientProperty("RenderingHint.value");
					if(itemValue!=null &&
							itemValue.equals(value) && 
							item.isSelected()==false) {
						item.doClick();
						return;
					}
				}
			}
			throw new IllegalArgumentException("the value \""+value+"\" was not found");
		}
		
		public Object getSelectedValue() {
			if(undefined.isSelected())
				return null;
			
			for(int a = 0; a<getItemCount(); a++) {
				if(getItem(a) instanceof JCheckBoxMenuItem) {
					JCheckBoxMenuItem item = (JCheckBoxMenuItem)getItem(a);
					if(item.isSelected())
						return item.getClientProperty("RenderingHint.value");
				}
			}
			return null;
		}
		
		public void addOption(Object value,String valueName,String valueUserName) {
			JCheckBoxMenuItem item = new JCheckBoxMenuItem(valueUserName);
			item.addActionListener(actionListener);
			add(item);
			item.putClientProperty("RenderingHint.value", value);
		}
		
		@Override
		public String toString() {
			return "Option[ keyName = "+keyName+", keyUserName = "+keyUserName+" ]";
		}
	}
	
	/** Takes strings like "KEY_ANTIALIAS_ON" and converts them to "Antialias On".
	 * This makes a more human-readable string.
	 */
	private static String rename(String s,int startingPosition) {
		StringBuffer sb = new StringBuffer();
		boolean start = true;
		for(int a = startingPosition; a<s.length(); a++) {
			char c = s.charAt(a);
			if(c=='_') {
				sb.append(' ');
				start = true;
			} else {
				if(start) {
					sb.append(Character.toUpperCase(c));
				} else {
					sb.append(Character.toLowerCase(c));
				}
				start = false;
			}
		}
		return sb.toString();
	}
}