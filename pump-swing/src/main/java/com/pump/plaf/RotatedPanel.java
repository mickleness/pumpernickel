package com.pump.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class RotatedPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public enum Rotation {
		NONE(false) {

			@Override
			public AffineTransform createTransform(int width, int height) {
				return new AffineTransform();
			}

		},
		COUNTER_CLOCKWISE(true) {

			@Override
			public AffineTransform createTransform(int width, int height) {
				AffineTransform tx = new AffineTransform();
				tx.translate(width / 2, height / 2);
				tx.rotate(-Math.PI / 2);
				tx.translate(-height / 2, -width / 2);
				return tx;
			}

		},
		CLOCKWISE(true) {

			@Override
			public AffineTransform createTransform(int width, int height) {
				AffineTransform tx = new AffineTransform();
				tx.translate(width / 2, height / 2);
				tx.rotate(Math.PI / 2);
				tx.translate(-height / 2, -width / 2);
				return tx;
			}

		},
		ROTATE_180(false) {

			@Override
			public AffineTransform createTransform(int width, int height) {
				return AffineTransform.getRotateInstance(Math.PI, width / 2,
						height / 2);
			}
		};

		boolean invert;

		Rotation(boolean invert) {
			this.invert = invert;
		}

		public abstract AffineTransform createTransform(int width, int height);

		public Dimension transform(Dimension d) {
			if (invert)
				return new Dimension(d.height, d.width);
			return new Dimension(d.width, d.height);
		}
	}

	private class RotatedComponentLayoutManager implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			Component child = parent.getComponent(0);
			Dimension d = child.getPreferredSize();
			return getRotation().transform(d);
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			Component child = parent.getComponent(0);
			Dimension d = child.getMinimumSize();
			return getRotation().transform(d);
		}

		@Override
		public void layoutContainer(Container parent) {
			Component child = parent.getComponent(0);
			Dimension d = new Dimension(parent.getWidth(), parent.getHeight());
			d = getRotation().transform(d);
			child.setBounds(new Rectangle(0, 0, d.width, d.height));
		}

	}

	public static final String PROPERTY_ROTATION = RotatedPanel.class.getName()
			+ "#rotation";

	public RotatedPanel(JComponent jc, Rotation rotation) {
		setOpaque(false);
		setRotation(rotation);
		setLayout(new RotatedComponentLayoutManager());
		add(jc);

		addPropertyChangeListener(PROPERTY_ROTATION,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						revalidate();
						repaint();
					}

				});
	}

	@Override
	public Dimension getPreferredSize() {
		Component child = getComponent(0);
		Dimension d = child.getPreferredSize();
		return getRotation().transform(d);
	}

	@Override
	public Dimension getMaximumSize() {
		Component child = getComponent(0);
		Dimension d = child.getMaximumSize();
		return getRotation().transform(d);
	}

	@Override
	public Dimension getMinimumSize() {
		Component child = getComponent(0);
		Dimension d = child.getMinimumSize();
		return getRotation().transform(d);
	}

	@Override
	protected void paintChildren(Graphics g0) {
		Graphics2D g = (Graphics2D) g0.create();
		g.setClip(null);
		g.transform(getRotation().createTransform(getWidth(), getHeight()));
		g.clipRect(0, 0, getHeight(), getWidth());
		super.paintChildren(g);
		g.dispose();
	}

	public void setRotation(Rotation r) {
		Objects.requireNonNull(r);
		putClientProperty(PROPERTY_ROTATION, r);
	}

	public Rotation getRotation() {
		return (Rotation) getClientProperty(PROPERTY_ROTATION);
	}

}
