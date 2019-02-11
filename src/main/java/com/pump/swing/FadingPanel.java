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

import java.awt.AlphaComposite;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * This panel can fade its contents as you change elements.
 * <P>
 * It's non-public because it's a little tricky. When an animation is in
 * progress, the <code>paint()</code> method directly paints BufferedImages
 * composited with fading opacity.
 * <P>
 * It's implemented by actually placing a CardLayout in this panel. The
 * following calls delegate to the "real" inner content of this panel:
 * <ul>
 * <LI>setLayout()</li>
 * <LI>add()</li>
 * <LI>removeAll()</LI>
 * </ul>
 * <P>
 * However other methods will be misleading, unless you recognize this panel is
 * really a CardLayout with two panels:
 * <ul>
 * <LI>getComponentCount()</li>
 * <LI>getComponent()</li>
 * <LI>getLayout()</LI>
 * </ul>
 * <P>
 * The CardLayout is a strange step, but I found it necessary because even
 * though the <code>paint()</code> method was being intercepted during an
 * animation: sometimes on XP some components would insist on repainting
 * themselves, which can lead to really awkward flickering.
 * <P>
 * It is very strongly recommended that this component be opaque. See <a href=
 * "https://javagraphics.blogspot.com/2008/06/crossfades-what-is-and-isnt-possible.html"
 * >this link</a> for a discussion of why.
 */
class FadingPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final boolean isMac = System.getProperty("os.name")
			.toLowerCase().indexOf("mac") != -1;

	public static final String PROPERTY_PROGRESS = "FadingPanel.fadeProgress";

	public static void main(String[] args) {
		JFrame f = new JFrame();
		final FadingPanel fadingPanel = new FadingPanel();
		fadingPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		final JCheckBox checkBox = new JCheckBox("Toggle Me");
		final JSlider slider = new JSlider();
		fadingPanel.add(checkBox, c);
		c.gridy++;
		fadingPanel.add(slider, c);

		f.setResizable(false);

		f.getContentPane().add(fadingPanel);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.pack();
		f.setVisible(true);

		checkBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkBox.isSelected()) {
					fadingPanel.animateStates(new Runnable() {
						public void run() {
							slider.setValue(0);
						}
					}, 250);
				} else {
					fadingPanel.animateStates(new Runnable() {
						public void run() {
							slider.setValue(100);
						}
					}, 250);
				}
			}
		});
	}

	private final CardLayout cardLayout = new CardLayout();
	private BufferedImage image1, image2;
	private float progress = 1;
	private boolean animating = false;
	private final JPanel mainPanel = new JPanel();
	private final JPanel animationPanel = new JPanel() {

		private static final long serialVersionUID = 1L;
		private BufferedImage scrap;

		@Override
		public void paint(Graphics g0) {
			Graphics2D g = (Graphics2D) g0;
			int w = getWidth();
			int h = getHeight();
			if (isOpaque()) {
				Color bkgnd = getBackground();
				g.setColor(bkgnd);
				g.fillRect(0, 0, w, h);
				g.drawImage(image1, 0, 0, null);
				g.setComposite(AlphaComposite.getInstance(
						AlphaComposite.SRC_OVER, progress));
				g.drawImage(image2, 0, 0, null);
				return;
			}

			int mWidth = Math.max(image1.getWidth(), image2.getWidth());
			int mHeight = Math.max(image1.getHeight(), image2.getHeight());
			if (scrap == null || scrap.getWidth() < mWidth
					|| scrap.getHeight() < mHeight) {
				if (mWidth > 0 && mHeight > 0) {
					if (scrap != null)
						scrap.flush();
					scrap = new BufferedImage(mWidth, mHeight, image1.getType());
				}
			}
			if (scrap != null) {
				Graphics2D g2 = scrap.createGraphics();
				g2.setComposite(AlphaComposite.Clear);
				g2.fillRect(0, 0, scrap.getWidth(), scrap.getHeight());
				paintCrossFade(g2, image1, image2, progress);
				g2.dispose();
				g.drawImage(scrap, 0, 0, null);
			}
		}
	};

	/**
	 * This paints a combination of bi1 and bi2 fading as f ranges from [0,1].
	 * See <a href=
	 * "https://javagraphics.blogspot.com/2008/06/crossfades-what-is-and-isnt-possible.html"
	 * >this link</a> for more details.
	 * 
	 * @param g
	 * @param bi1
	 * @param bi2
	 * @param f
	 */
	static void paintCrossFade(Graphics2D g, BufferedImage bi1,
			BufferedImage bi2, float f) {

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				1 - f));
		g.drawImage(bi1, 0, 0, null);

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f));
		g.drawImage(bi2, 0, 0, null);

		float total = 1 - f + f * f;

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN,
				(float) (Math.pow(total, .2))));
		g.drawImage(bi1, 0, 0, null);
		g.drawImage(bi2, 0, 0, null);

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				1 - f));
		g.drawImage(bi1, 0, 0, null);

		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, f));
		g.drawImage(bi2, 0, 0, null);
		return;
	}

	/** Creates a new FadingPanel */
	public FadingPanel() {
		super();
		mainPanel.setOpaque(isOpaque());
		super.setLayout(cardLayout);
		super.add(mainPanel, "main");
		super.add(animationPanel, "animate");
	}

	/** Creates a new FadingPanel */
	public FadingPanel(LayoutManager layoutManager) {
		this();
		setLayout(layoutManager);
	}

	/** Adds a new component to the contents of this FadingPanel. */
	@Override
	public Component add(Component comp, int index) {
		return mainPanel.add(comp, index);
	}

	/** Adds a new component to the contents of this FadingPanel. */
	@Override
	public void add(Component comp, Object constraints, int index) {
		mainPanel.add(comp, constraints, index);
	}

	/** Adds a new component to the contents of this FadingPanel. */
	@Override
	public void add(Component comp, Object constraints) {
		mainPanel.add(comp, constraints);
	}

	/** Adds a new component to the contents of this FadingPanel. */
	@Override
	public Component add(Component comp) {
		return mainPanel.add(comp);
	}

	/** Adds a new component to the contents of this FadingPanel. */
	@Override
	public Component add(String name, Component comp) {
		return mainPanel.add(name, comp);
	}

	/** Removes a component from the contents of this FadingPanel. */
	@Override
	public void remove(Component comp) {
		mainPanel.remove(comp);
	}

	/** Removes a component from the contents of this FadingPanel. */
	@Override
	public void remove(int index) {
		mainPanel.remove(index);
	}

	/** Removes all components from the contents of this FadingPanel. */
	@Override
	public void removeAll() {
		mainPanel.removeAll();
	}

	/** Sets the LayoutManager of the contents of this FadingPanel. */
	@Override
	public void setLayout(LayoutManager mgr) {
		if (mainPanel == null) {
			// this happens from calls high up in the constructor:
			// ignore it and we'll change the layoutManager in a second
			super.setLayout(mgr);
			return;
		}
		mainPanel.setLayout(mgr);
	}

	/** Captures an image of this panel before/after running the argument. */
	private void captureStates(Runnable r) {
		if (image1 != null) {
			image1.flush();
		}
		if (image2 != null) {
			image2.flush();
		}

		image1 = captureImage();
		r.run();
		image2 = captureImage();

		// make sure the images are the same size
		// if one is larger in any dimension: then enlarge it,
		// and paint what the *other* image shows in those
		// empty areas.
		int mWidth = Math.max(image1.getWidth(), image2.getWidth());
		int mHeight = Math.max(image1.getHeight(), image2.getHeight());
		if (image1.getWidth() < mWidth || image1.getHeight() < mHeight) {
			BufferedImage tmp = new BufferedImage(mWidth, mHeight,
					image1.getType());
			Graphics2D g = tmp.createGraphics();
			if (image1.getWidth() < mWidth) {
				g.drawImage(image2, image1.getWidth(), 0, image2.getWidth(),
						image2.getHeight(), image1.getWidth(), 0,
						image2.getWidth(), image2.getHeight(), null);
			}
			if (image1.getHeight() < mHeight) {
				g.drawImage(image2, 0, image1.getHeight(), image1.getWidth(),
						image2.getHeight(), 0, image1.getHeight(),
						image1.getWidth(), image2.getHeight(), null);
			}
			g.drawImage(image1, 0, 0, null);
			g.dispose();
			image1.flush();
			image1 = tmp;
		}
		if (image2.getWidth() < mWidth || image2.getHeight() < mHeight) {
			BufferedImage tmp = new BufferedImage(mWidth, mHeight,
					image2.getType());
			Graphics2D g = tmp.createGraphics();
			if (image2.getWidth() < mWidth) {
				g.drawImage(image1, image2.getWidth(), 0, image1.getWidth(),
						image1.getHeight(), image2.getWidth(), 0,
						image1.getWidth(), image1.getHeight(), null);
			}
			if (image2.getHeight() < mHeight) {
				g.drawImage(image1, 0, image2.getHeight(), image2.getWidth(),
						image1.getHeight(), 0, image2.getHeight(),
						image2.getWidth(), image1.getHeight(), null);
			}
			g.drawImage(image2, 0, 0, null);
			g.dispose();
			image2.flush();
			image2 = tmp;
		}
	}

	/**
	 * Animate this panel. This fades from its current state to the state after
	 * the argument runnable has been performed. The fade will last 250
	 * milliseconds.
	 * <P>
	 * This method should be called from the event dispatch thread.
	 * 
	 * @param r
	 *            the changes to make to this panel.
	 */
	public void animateStates(Runnable r) {
		if (getWidth() == 0 || getHeight() == 0) {
			// this is probably the result of an incomplete GUI,
			// let it go for now:
			r.run();
			return;
		}
		animateStates(r, 250);
	}

	/**
	 * Animate this panel. This fades from its current state to the state after
	 * the argument runnable has been performed.
	 * <P>
	 * This method should be called from the event dispatch thread.
	 * 
	 * @param r
	 *            the changes to make to this panel.
	 * @param duration
	 *            the duration the fade should last, in milliseconds. Usually
	 *            250 is a reasonable number.
	 */
	public void animateStates(Runnable r, final long duration) {
		captureStates(r);
		setProgress(0);
		final long start = System.currentTimeMillis();
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				long current = System.currentTimeMillis() - start;
				float f = ((float) current) / ((float) duration);
				if (f > 1)
					f = 1;
				setProgress(f);
				if (f == 1) {
					((Timer) e.getSource()).stop();
				}
			}
		};
		Timer timer = new Timer(40, actionListener);
		timer.start();
	}

	/** Sets the opacity of both this panel and the inner content panel. */
	@Override
	public void setOpaque(boolean b) {
		super.setOpaque(b);
		if (mainPanel != null) // mainPanel is null high up in construction
			mainPanel.setOpaque(b);
	}

	/**
	 * Captures an image of this panel.
	 * 
	 */
	private BufferedImage captureImage() {
		BufferedImage bi = new BufferedImage(mainPanel.getWidth(),
				mainPanel.getHeight(), isMac ? BufferedImage.TYPE_INT_ARGB
						: BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = bi.createGraphics();
		g.setComposite(AlphaComposite.Clear);
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		g.setComposite(AlphaComposite.SrcOver);
		mainPanel.paint(g);
		g.dispose();
		return bi;
	}

	// TODO: ?
	public void refresh() {
		Dimension d = mainPanel.getPreferredSize();
		mainPanel.setSize(d);
		mainPanel.validate();
		mainPanel.doLayout();
	}

	/**
	 * Returns the current progress. When this is 1, no animation is occurring.
	 */
	public float getProgress() {
		return progress;
	}

	private Component focusedComponent = null;

	/**
	 * Sets the progress. External classes probably should not call this, as it
	 * will interfere with the animation process (if any).
	 * 
	 * @param f
	 *            a float from [0,1]
	 */
	public void setProgress(float f) {
		if (f < 0 || f > 1)
			throw new IllegalArgumentException("The progress (" + f
					+ ") must be between 0 and 1.");
		if (progress == f)
			return;

		float oldValue = progress;

		try {
			this.fireVetoableChange(PROPERTY_PROGRESS, new Float(oldValue),
					new Float(f));
		} catch (PropertyVetoException e) {
			return;
		}

		progress = f;

		if (progress < 1) {
			if (animating == false) {
				focusedComponent = findFocusedComponent(mainPanel);
				cardLayout.show(this, "animate");
				animating = true;
			}
		} else {
			if (animating == true) {
				cardLayout.show(this, "main");

				if (focusedComponent != null
						&& mainPanel.isAncestorOf(focusedComponent)) {
					focusedComponent.requestFocus();
					if (focusedComponent instanceof JComponent) {
						((JComponent) focusedComponent).grabFocus();
					}
				}

				animating = false;
				mainPanel.doLayout();

				if (image1 != null)
					image1.flush();
				if (image2 != null)
					image2.flush();
				image1 = null;
				image2 = null;
			}
		}

		repaint();

		this.firePropertyChange(PROPERTY_PROGRESS, oldValue, f);
	}

	private static Component findFocusedComponent(Container c) {
		for (int a = 0; a < c.getComponentCount(); a++) {
			if (c.getComponent(a).hasFocus())
				return c.getComponent(a);

			if (c.getComponent(a) instanceof Container) {
				Component returnValue = findFocusedComponent((Container) c
						.getComponent(a));
				if (returnValue != null)
					return returnValue;
			}
		}
		return null;
	}

	/**
	 * EXPERIMENTAL: DON'T USE THIS METHOD WITHOUT MORE TESTING.
	 * 
	 * This resizes this panel's parent window in sync with the fade from t =
	 * [0,1]. But it doesn't work well in my testing environment, and I'm
	 * leaving it be for now.
	 */
	void resizeWindow() {
		final Window window = SwingUtilities.getWindowAncestor(this);
		if (window == null)
			return;
		final Dimension startingSize = window.getSize();
		final Dimension finalSize = window.getPreferredSize();
		addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				FadingPanel p = (FadingPanel) evt.getSource();
				float f = p.getProgress();
				if (f > .999f) {
					window.setSize(finalSize);
					p.removePropertyChangeListener(this);
					return;
				}
				Dimension currentSize = new Dimension((int) (startingSize.width
						* (1 - f) + finalSize.width * f),
						(int) (startingSize.height * (1 - f) + finalSize.height
								* f));
				window.setSize(currentSize);
			}
		});
	}
}