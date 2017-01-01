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
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.plaf.ButtonUI;

public class ShimmerPaintUIEffect extends PaintUIEffect {
	public static final MouseListener mouseListener = new MouseAdapter() {
		/** A new shimmer to the effects list. */
		@Override
		public void mouseEntered(MouseEvent e) {
			AbstractButton button = (AbstractButton)e.getSource();
			ButtonUI ui = button.getUI();
			if(ui instanceof FilledButtonUI) {
				FilledButtonUI fui = (FilledButtonUI)ui;
				fui.getEffects(button).add(new ShimmerPaintUIEffect(button));
			}
		}
		
		/** Kill the effects if the user moused over the component
		 * really quickly.
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			AbstractButton button = (AbstractButton)e.getSource();
			ButtonUI ui = button.getUI();
			if(ui instanceof FilledButtonUI) {
				FilledButtonUI fui = (FilledButtonUI)ui;
				List<PaintUIEffect> list = fui.getEffects(button);
				int ctr = 0;
				while(ctr<list.size()) {
					PaintUIEffect effect = list.get(ctr);
					if(effect instanceof ShimmerPaintUIEffect &&
							effect.getProgress()<.2f &&
							effect.getComponent()==button) {
						list.remove(ctr);
					} else {
						ctr++;
					}
				}
			}
		}
	};
	
	final AbstractButton button;
	final GeneralPath p = new GeneralPath();
	
	float slantWidth = 10;
	float shimmerWidth = 20;
	
	public ShimmerPaintUIEffect(AbstractButton button) {
		super(button, 500, 40);
		this.button = button;
	}

	@Override
	public void paint(Graphics2D g) {
		float p2 = (progress - .2f) / .8f;
		FilledButtonUI ui = (FilledButtonUI)button.getUI();
		float dx = p2 * (button.getWidth() + slantWidth + shimmerWidth);
		p.reset();
		p.moveTo(dx, 0);
		p.lineTo(dx - shimmerWidth, 0);
		p.lineTo(dx - shimmerWidth - slantWidth, button.getHeight());
		p.lineTo(dx - slantWidth, button.getHeight());

		g.clip(ui.getShape(button));
		g.setColor(new Color(255, 255, 255, 100));
		g.fill(p);
	}
	
	@Override
	public boolean isBackground() {
		return false;
	}

}