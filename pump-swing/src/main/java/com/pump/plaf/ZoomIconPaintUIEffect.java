/*
 * @(#)ZoomIconUIEffect.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
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
package com.pump.plaf;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.plaf.ButtonUI;

import com.pump.geom.TransformUtils;

public class ZoomIconPaintUIEffect extends PaintUIEffect {
	public static final ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			AbstractButton button = (AbstractButton)e.getSource();
			ButtonUI ui = button.getUI();
			if(ui instanceof FilledButtonUI) {
				FilledButtonUI fui = (FilledButtonUI)ui;
				List<PaintUIEffect> effects = fui.getEffects(button);
				boolean hasZoom = false;
				for(int a = 0; a<effects.size(); a++) {
					PaintUIEffect effect = effects.get(a);
					if(effect instanceof ZoomIconPaintUIEffect && effect.getComponent()==button)
						hasZoom = true;
				}
				if(!hasZoom)
					effects.add(new ZoomIconPaintUIEffect(button));
			}
		}
	};
	
	final AbstractButton button;
	
	public ZoomIconPaintUIEffect(AbstractButton b) {
		super(b, 750, 40);
		button = b;
	}

	@Override
	public void paint(Graphics2D g) {
		FilledButtonUI ui = (FilledButtonUI)button.getUI();
		
		Rectangle r = ui.getIconBounds(button);
		if(r.width==0 || r.height==0)
			return;
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				progress));
		float z = (1 - progress) * button.getHeight() / 4;
		g.transform(TransformUtils.createAffineTransform(r.x, r.y, r.x
				+ r.width, r.y, r.x, r.y + r.height, r.x - z, r.y - z, r.x
				+ r.width + z, r.y - z, r.x - z, r.y + r.height + z));
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		button.getIcon().paintIcon(button, g, r.x, r.y);
	}
	
	@Override
	public boolean isBackground() {
		return false;
	}
}
