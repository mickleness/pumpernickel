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
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.LabelUI;
import javax.swing.tree.TreeCellRenderer;

import com.pump.io.location.IOLocation;
import com.pump.plaf.AquaLocationSourceList.LocationNode;
import com.pump.swing.io.GraphicCache;
import com.pump.util.JVM;


/** A <code>TreeCellRenderer</code> and <code>ListCellRenderer</code> that supports <code>IOLocations</code> and
 * complements the <code>SourceListTreeUI</code>.
 */
public class SourceListLocationRenderer implements TreeCellRenderer, ListCellRenderer {
	JLabel label = new JLabel();
	Font normalFont;
	Font categoryFont;
	Font selectedFont;
	LabelUI selectedUI;
	LabelUI normalUI;
	LabelUI categoryUI;
	GraphicCache graphicCache;
	
	private SourceListLocationRenderer(GraphicCache graphicCache) {
		Font font = UIManager.getFont("TableHeader.font");
		if(font!=null) {
			normalFont = font;
		} else {
			normalFont = label.getFont();
		}
		selectedFont = normalFont.deriveFont(Font.BOLD);
		categoryFont = selectedFont;
		this.graphicCache = graphicCache;

		if(JVM.isMac) {
			selectedUI = new EmphasizedLabelUI(new Color(0xffffff),new Color(0xffffff),new Color(0x99000000,true));
			normalUI = new EmphasizedLabelUI();
			categoryUI = new EmphasizedLabelUI(new Color(0x606060),new Color(0x606060), EmphasizedLabelUI.DEFAULT_EMPHASIS_COLOR);
		} else {
			selectedUI = new EmphasizedLabelUI(new Color(0x1E508B),new Color(0x1E508B),null);
			normalUI = selectedUI;
			categoryUI = selectedUI;
			selectedFont = normalFont;
		}
	}
	
	public SourceListLocationRenderer(GraphicCache graphicCache,JTree tree) {
		this(graphicCache);
	}
	
	public SourceListLocationRenderer(GraphicCache graphicCache,final JList list) {
		this(graphicCache);
		graphicCache.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				Object src = evt.getSource();
				if(evt.getPropertyName().equals(GraphicCache.ICON_PROPERTY) && src instanceof IOLocation) {
					for(int a = 0; a<list.getModel().getSize(); a++) {
						if(list.getModel().getElementAt(a).equals(src)) {
							list.repaint(list.getCellBounds(a, a));
							return;
						}
					}
				}
			}
			
		});
	}
	
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		return getRenderer(value, isSelected);
	}
	
	protected boolean isCategory(Object value) {
		return !(value instanceof LocationNode || value instanceof IOLocation);
	}
	
	private Map<IOLocation, Icon> iconTable = new HashMap<IOLocation, Icon>();
	protected JLabel getRenderer(Object value,boolean selected) {
		//set UI first, since that changes the font
		if(selected) {
			label.setUI(selectedUI);
			label.setFont(selectedFont);
		} else if(isCategory(value)==false) {
			label.setUI(normalUI);
			label.setFont(normalFont);
		} else {
			label.setUI(categoryUI);
			label.setFont(categoryFont);
		}

		if(value instanceof IOLocation) {
			IOLocation loc = (IOLocation)value;
			Icon icon = iconTable.get(loc);
			if(icon==null) {
				icon = graphicCache.requestIcon(loc);
				if(icon!=null) {
					iconTable.put(loc, icon);
				}
			}
			label.setIcon(icon);
			label.setText(loc.getName());
		} else if(value instanceof LocationNode) {
			LocationNode n = (LocationNode)value;
			label.setIcon(n.getIcon());
			label.setText(n.getLocation().getName());
		} else {
			label.setIcon(null);
			label.setText(value.toString());
		}
		label.setOpaque(false);
		return label;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		return getRenderer(value, selected);
	}
	
}