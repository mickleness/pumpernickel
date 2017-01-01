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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LabelUI;

public class ThumbnailLabelUI extends LabelUI {
	protected Rectangle iconRect = new Rectangle();
	protected Rectangle textRect = new Rectangle();
	
	public Rectangle getTextRect() {
		return new Rectangle(textRect);
	}
	
	public Rectangle getIconRect() {
		return new Rectangle(iconRect);
	}
	
	ComponentListener repaintComponentListener = new ComponentListener() {

		public void componentHidden(ComponentEvent e) {}

		public void componentMoved(ComponentEvent e) {}

		public void componentResized(ComponentEvent e) {
			((Component)e.getSource()).repaint();
		}

		public void componentShown(ComponentEvent e) {}
		
	};
	
	protected int getViewWidth(JLabel label) {
		return label.getWidth();
	}
	
	private Rectangle viewR = new Rectangle();
	protected void calculateGeometry(JLabel label) {
		FontMetrics fm = label.getFontMetrics(label.getFont());
		String text = label.getText();
		Icon icon = label.getIcon();
		int verticalAlignment = label.getVerticalAlignment();
		int horizontalAlignment = label.getHorizontalAlignment();
		int verticalTextPosition = label.getVerticalTextPosition();
		int horizontalTextPosition = label.getHorizontalTextPosition();
		int textIconGap = label.getIconTextGap();
		viewR.setFrame(0,0,getViewWidth(label),label.getHeight());
		SwingUtilities.layoutCompoundLabel(fm, text, icon, verticalAlignment, horizontalAlignment, verticalTextPosition, horizontalTextPosition, viewR, iconRect, textRect, textIconGap);
	
		textRect.x = label.getWidth()/2-textRect.width/2;
		iconRect.x = label.getWidth()/2-iconRect.width/2;
		iconRect.y = 0;
		textRect.y = iconRect.height+label.getIconTextGap();
	}
	
	@Override
	public Dimension getPreferredSize(JComponent c) {
		JLabel label = (JLabel)c;
		
		calculateGeometry( label );
		Rectangle sum;
		if(iconRect.width>0 && iconRect.height>0) {
			sum = new Rectangle(iconRect);
			if(textRect.width>0 && textRect.height>0) {
				sum.add(textRect);
			}
		} else if(textRect.width>0 && textRect.height>0) {
			sum = new Rectangle(textRect);
		} else {
			return new Dimension(0,0);
		}
		return new Dimension(sum.width, sum.height);
	}



	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D)g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(c.isOpaque()) {
			g.setColor(c.getBackground());
			g.fillRect(0,0,c.getWidth(),c.getHeight());
		}
		
		JLabel label = (JLabel)c;
		
		Object selectedValue = label.getClientProperty("selected");
		if(selectedValue==null) selectedValue = Boolean.FALSE;
		boolean isSelected = selectedValue.toString().equals("true");

		Object indicatedValue = label.getClientProperty("selected");
		if(indicatedValue==null) indicatedValue = Boolean.FALSE;
		boolean isIndicated = indicatedValue.toString().equals("true");
		
		calculateGeometry(label);
		paintIcon(g,label,isSelected,isIndicated);

		g.setColor(label.getForeground());
		paintText(g, label, label.getText(), isSelected, isIndicated);
	}
	
	protected void paintIcon(Graphics2D g,JLabel label,boolean isSelected,boolean isIndicated) {
		Icon icon = label.getIcon();
		if(icon==null) return;
		
		icon.paintIcon(label, g, iconRect.x+iconRect.width/2-icon.getIconWidth()/2, 
				iconRect.y+iconRect.height/2-icon.getIconHeight()/2);
	}

	protected void paintText(Graphics2D g,JLabel label,String text,boolean isSelected,boolean isIndicated) {
		Font font = label.getFont();
		g.setFont(font);
		FontMetrics metrics = g.getFontMetrics(font);
		int limit  = text.length();
		Rectangle2D r = g.getFontMetrics().getStringBounds(text, g);
		String finalText = text;
		while(limit>0 && r.getWidth()>textRect.width) {
			limit--;
			finalText = text.substring(0,limit)+"...";
			r = g.getFontMetrics().getStringBounds(finalText, g);
		}
		g.drawString(finalText, textRect.x, textRect.y+metrics.getAscent());
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.addComponentListener(repaintComponentListener);
		JLabel label = (JLabel)c;
		label.setHorizontalTextPosition(SwingConstants.CENTER);
		label.setIconTextGap(3);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removeComponentListener(repaintComponentListener);
	}
	
	
}