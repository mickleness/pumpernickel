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

import java.awt.CardLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.PanelUI;

import com.pump.image.transition.Transition;

/** This panel renders some refreshes through transitions.
 * <p>The {@link #startTransition(Transition, Runnable, float)} method
 * takes a snapshot of the current body of this panel, then executes the
 * <code>Runnable</code> argument, then takes a second snapshot, and then
 * executes the {@link com.bric.image.transition.Transition2D}.
 * <p>Internally the <code>TransitionPanel</code> uses a <code>CardLayout</code>
 * with two panels:
 * <ul><li>The body: this panel is accessible via {@link #getBody()}, and is at
 * your disposal to configure however you'd like. The only thing to keep in mind is:
 * setting it to invisible won't actually free up screen space like it normally would.
 * Instead you need to set this <code>TransitionPanel</code> to invisible.</li>
 * <li>The transition panel: this is only made visible during a transition. It is important
 * to completely obscure/remove the body during a transition, because otherwise more
 * complex UI components (like <code>JSpinners</code>) may flicker and render over
 * the transition for a split second. Also because this replaces the body: nothing in
 * the body is capable of receiving user input during the transition. (However this
 * shouldn't matter too much, because ideally a transition should be about a quarter
 * of a second.)</li></ul>
 * 
 * @see com.bric.image.transition.Transition2DDemo
 * @see com.bric.swing.TransitionPanelDemo
 * @see com.bric.swing.TransitionTabbedPane
 */
public class TransitionPanel extends JComponent {
	private static final long serialVersionUID = 1L;
	
	/** The client property that maps to a Transition2D. */
	public static final String TRANSITION_KEY = TransitionPanel.class.getName()+".transition";
	
	JPanel contentPane = new JPanel();
	JPanel transitionPane = new JPanel();
	CardLayout cardLayout = new CardLayout();

	BufferedImage startImage, endImage;

	private Animation animation;

	private Timer animationTimer = new Timer(20, new ActionListener() {
	
		public void actionPerformed(ActionEvent e) {
			if(animation==null && pendingAnimations.size()>0) {
				animation = pendingAnimations.remove(0);
				animation.start();
			}
			if(animation!=null) {
				animation.iterate();
			} else {
				animationTimer.stop();
			}
		}
	});

	private class Animation {
		Runnable runnable;
		Transition transition;
		float duration;
		long startTime;
		
		Animation(Transition transition,Runnable runnable,float duration) {
			this.runnable = runnable;
			this.transition = transition;
			this.duration = duration;
		}
		
		void iterate() {
			long elapsed = System.currentTimeMillis() - startTime;
			if(elapsed>duration*1000) {
				//we're done here
				animation = null;
				startImage = null;
				endImage = null;
				cardLayout.show(TransitionPanel.this, "default");
			} else {
				transitionPane.repaint();
			}
		}
		
		void paint(Graphics g) {
			float elapsed = System.currentTimeMillis() - startTime;
			elapsed = elapsed/1000f;
			float fraction = elapsed / duration;
			if(fraction>1)
				fraction = 1;
			Graphics2D g2 = (Graphics2D)g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			transition.paint( g2, startImage, endImage, fraction);
			g2.dispose();
		}
		
		void start() {
			startTime = System.currentTimeMillis();
			startImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			Graphics2D g = (Graphics2D)startImage.createGraphics();
			contentPane.paint(g);
			g.dispose();
			
			runnable.run();
			contentPane.getLayout().layoutContainer(contentPane);
			
			endImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
			g = (Graphics2D)endImage.createGraphics();
			contentPane.paint(g);
			g.dispose();
			
			cardLayout.show(TransitionPanel.this, "transition");
		}
	}

	private List<Animation> pendingAnimations = new LinkedList<Animation>();
	
	public TransitionPanel() {
		setLayout(cardLayout);
		add(contentPane, "default");
		add(transitionPane, "transition");
		
		transitionPane.setUI(new PanelUI() {
			@Override
			public void paint(Graphics g, JComponent c) {
				super.paint(g, c);
				
				if(animation!=null) {
					animation.paint(g);
				}
			}
		});
	}
	
	/** Begin a transition. While a transition is in the progress:
	 * the body panel is not technically visible, so components in that
	 * panel can't be interacted.
	 * <p>This manages animations in a FIFO queue, so no animations will overlap
	 * but each call will be respected.
	 * 
	 * @param transition the transition to invoke.
	 * @param runnable a runnable to be invoked on the EDT that will change the appearance
	 * of the body panel. A snapshot is taken before and after this runnable is executed, and
	 * those snapshots are what is animated.
	 * @param duration The duration (in seconds) of the animation.
	 */
	public void startTransition(Transition transition,Runnable runnable,float duration) {
		pendingAnimations.add(new Animation(transition, runnable, duration));
		animationTimer.start();
	}
	
	/** Return true if an animation is in progress. If several animations queued up,
	 * then this may take several seconds to return false.
	 */
	public boolean isAnimating() {
		return animationTimer.isRunning();
	}

	/** Return the body of this panel that you should modify.
	 * <p>You should not add components directly to this <code>TransitionPanel</code>,
	 * but you can do whatever you want to the panel this method returns.
	 */
	public JPanel getBody() {
		return contentPane;
	}
}