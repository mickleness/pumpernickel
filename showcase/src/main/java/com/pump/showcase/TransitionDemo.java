/*
 * @(#)Transition2DDemo.java
 *
 * $Date: 2014-05-09 09:15:15 -0400 (Fri, 09 May 2014) $
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
package com.pump.showcase;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.animation.BufferedAnimationPanel;
import com.pump.image.transition.AbstractTransition;
import com.pump.image.transition.Transition;
import com.pump.swing.AnimationController;

/** An abstract UI to demo a set of transitions.
 */
public abstract class TransitionDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	
	BufferedImage img1;
	BufferedImage img2;
	JComboBox<Transition> transitionComboBox = new JComboBox<Transition>();
	JComboBox<Object> interpolationComboBox = new JComboBox<Object>();
	AnimationController controller = new AnimationController();
	JSpinner duration = new JSpinner(new SpinnerNumberModel(2,.1,100,.1));
	JLabel interpolationLabel = new JLabel("Interpolation Hint:");

	/**
	 * 
	 * @param includeInterpolationControls true if a combobox for the interpolation hint should
	 * be visible.
	 */
	public TransitionDemo(Transition[] transitions,boolean includeInterpolationControls) {
		this(AbstractTransition.createImage("A",true),
				AbstractTransition.createImage("B",false),
				transitions, includeInterpolationControls);
	}
    
	/**
	 * 
	 * @param includeInterpolationControls true if a combobox for the interpolation hint should
	 * be visible.
	 */
	public TransitionDemo(BufferedImage bi1,BufferedImage bi2,Transition[] transitions,boolean includeInterpolationControls) {
		img1 = bi1;
		img2 = bi2;

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 0;
		c.fill = GridBagConstraints.NONE; c.anchor = GridBagConstraints.SOUTHWEST;
		JPanel optionsPanel = new JPanel(new GridBagLayout());
		add(optionsPanel,c);
		final TransitionPanel panel = new TransitionPanel((Transition)transitionComboBox.getItemAt(0));
		c.weighty = 1; c.gridy++; c.fill = GridBagConstraints.NONE;
		add(panel,c);
		c.weightx = 0;
		c.gridy++; c.anchor = GridBagConstraints.NORTHWEST;
		add(controller,c);
		
		Dimension d = controller.getPreferredSize();
		d.width = panel.getPreferredSize().width;
		controller.setPreferredSize(d);
		
        c.gridy++;

		c.gridx = 0; c.gridy = 0; c.weightx = 0; c.weighty = 0;
		c.anchor = GridBagConstraints.EAST; c.insets = new Insets(3,3,3,3);
		optionsPanel.add(new JLabel("Transition:"),c);
		c.gridy++;
		optionsPanel.add(new JLabel("Duration (s):"),c);
		c.gridy++;
		optionsPanel.add(interpolationLabel,c);
		c.gridx++; c.gridy = 0; c.anchor = GridBagConstraints.WEST;
		optionsPanel.add(transitionComboBox,c);
		c.gridy++;
		optionsPanel.add(duration,c);
		c.gridy++;
		optionsPanel.add(interpolationComboBox,c);

		interpolationLabel.setVisible(includeInterpolationControls);
		interpolationComboBox.setVisible(includeInterpolationControls);
		
		optionsPanel.setOpaque(false);

		controller.addPropertyChangeListener(AnimationController.TIME_PROPERTY,
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					panel.refresh();
				}
			}
		);
		controller.setLooping(true);
		
		addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if(isShowing()) {
					if(!controller.isPlaying()) {
						controller.play();
					}
				} else {
					if(controller.isPlaying()) {
						controller.pause();
					}
				}
			}
			
		});
		
		ChangeListener durationListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				float d = ((Number)duration.getValue()).floatValue();
				controller.setDuration(2*d); //once for A->B, once for B->A
			}
		};
		duration.addChangeListener(durationListener);
		durationListener.stateChanged(null);

		transitionComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.refresh();
			}
		});
        
		transitionComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                Transition t = (Transition)transitionComboBox.getSelectedItem();
                if(t==null) return;
                
                panel.setTransition( t );
			}
		});

		transitionComboBox.removeAllItems();
		for(int a = 0; a<transitions.length; a++) {
			transitionComboBox.addItem(transitions[a]);
			//make Scribble the default. I like it, and it performs well
			if(transitions[a].toString().indexOf("Scribble")!=-1)
				transitionComboBox.setSelectedIndex(a);
		}
		
		interpolationComboBox.addItem(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		interpolationComboBox.addItem(RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		interpolationComboBox.addItem(RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		interpolationComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.refresh();
			}
		});
	}
    
    public RenderingHints getQualityHints() {
    	return createQualityHints();
    }
    
    public static RenderingHints createQualityHints() {
    	RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		// WARNING: set this to bicubic interpolation brings Windows Vista to its knees.
		hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		return hints;
    }

	class TransitionPanel extends BufferedAnimationPanel {
		private static final long serialVersionUID = 1L;

		Transition transition;

		public TransitionPanel(Transition transition) {
			setTransition(transition);
			setPreferredSize(new Dimension(img1.getWidth(),img1.getHeight()));
		}

		public void setTransition(Transition transition) {
			this.transition = transition;
		}
		
		Font font = new Font("Mono",0,12);
		DecimalFormat format = new DecimalFormat("#.##");

		@Override
		protected void paintAnimation(Graphics2D g,int width,int height) {
			g.setColor(Color.black);
			g.fillRect(0,0,width,height);
			float t = controller.getTime()/controller.getDuration()*2;
			BufferedImage frameA, frameB;
			if(t>=2) { //for the very last frame
				t = 0;
				frameA = img1;
				frameB = img2;
			} else if(t>=1) {
				t = t%1;
				frameA = img2;
				frameB = img1;
			} else {
				frameA = img1;
				frameB = img2;
			}
			((Graphics2D)g).setRenderingHints(getQualityHints());
			if(interpolationComboBox.isVisible() && interpolationComboBox.getSelectedItem()!=null) {
				((Graphics2D)g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolationComboBox.getSelectedItem());
			}
			transition.paint((Graphics2D)g, frameA, frameB, t);
			Graphics2D g2 = (Graphics2D)g;
			TextLayout tl = new TextLayout(format.format((t*100))+"%",font, g2.getFontRenderContext());
			Shape outline = tl.getOutline(AffineTransform.getTranslateInstance(5, 18));
			g2.setColor(Color.black);
			g2.setStroke(new BasicStroke(2));
			g2.draw(outline);
			g2.setColor(Color.white);
			g2.fill(outline);
		}
	}
}
