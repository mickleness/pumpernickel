/*
 * @(#)TextEffectComponent.java
 *
 * $Date: 2014-11-27 01:55:25 -0500 (Thu, 27 Nov 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.Timer;

import com.pump.awt.text.TextEffect;

public class TextEffectComponent extends JComponent {
	private static final long serialVersionUID = 1L;

	public static final String PROGRESS_KEY = TextEffectComponent.class.getName()+".progress";
	public static final String TEXT_EFFECT_KEY = TextEffectComponent.class.getName()+".text-effect";
	public static final String DURATION_KEY = TextEffectComponent.class.getName()+".duration";
	
	long startTime = System.currentTimeMillis();
	Timer timer;
	
	public TextEffectComponent(TextEffect e,int duration) {
		this(e, duration, 15);
	}
	
	public TextEffectComponent(TextEffect e,final int duration,final int updateInterval) {
		timer = new Timer(updateInterval, null);
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				float elapsed = System.currentTimeMillis() - startTime;
				float progress = elapsed / ((float)getDuration());
				if(progress>1) {
					progress = 1;
					timer.stop();
				}
				setProgress(progress);
			}
		});
		
		setDuration(duration);
		setTextEffect(e);
		setOpaque(false);
		reset();
	}
	
	public void reset() {
		startTime = System.currentTimeMillis();
		setProgress(0);
		timer.start();
	}
	
	public void setProgress(float f) {
		putClientProperty(PROGRESS_KEY, f);
		repaint();
	}
	
	public void setTextEffect(TextEffect e) {
		putClientProperty(TEXT_EFFECT_KEY, e);
		if(e!=null)
			setPreferredSize(e.getPreferredSize());
		repaint();
		reset();
	}
	
	public void setDuration(int i) {
		putClientProperty(DURATION_KEY, i);
	}
	
	public int getDuration() {
		return ((Number)getClientProperty(DURATION_KEY)).intValue();
	}
	
	public float getProgress() {
		return ((Number)getClientProperty(PROGRESS_KEY)).floatValue();
	}

	public TextEffect getTextEffect() {
		return ((TextEffect)getClientProperty(TEXT_EFFECT_KEY));
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		TextEffect e = getTextEffect();
		if(e!=null) {
			float progress = getProgress();
			Graphics2D g2 = (Graphics2D)g.create();
			e.paint(g2, progress);
			g2.dispose();
		}
	}
}
