package com.pump.icon;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Objects;

import javax.swing.Icon;

import com.pump.geom.TransformUtils;

public class IconUtils {

	private static class PaddedIconInvocationHandler implements
			InvocationHandler {
		Icon icon;
		Insets insets;

		public PaddedIconInvocationHandler(Icon icon, Insets insets) {
			Objects.requireNonNull(icon);
			Objects.requireNonNull(insets);
			this.icon = icon;
			this.insets = new Insets(insets.top, insets.left, insets.bottom,
					insets.right);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			Parameter[] params = method.getParameters();
			if ("getPaddedIcon".equals(method.getName()) && params.length == 0) {
				return icon;
			} else if ("getIconInsets".equals(method.getName())
					&& params.length == 0) {
				return new Insets(insets.top, insets.left, insets.bottom,
						insets.right);
			} else if ("getIconHeight".equals(method.getName())
					&& params.length == 0) {
				return icon.getIconHeight() + insets.top + insets.bottom;
			} else if ("getIconWidth".equals(method.getName())
					&& params.length == 0) {
				return icon.getIconWidth() + insets.left + insets.right;
			} else if ("paintIcon".equals(method.getName())
					&& params.length == 4
					&& params[0].getType().equals(Component.class)
					&& params[1].getType().equals(Graphics.class)
					&& params[2].getType().equals(Integer.TYPE)
					&& params[3].getType().equals(Integer.TYPE)) {
				Component c = (Component) args[0];
				Graphics g = (Graphics) args[1];
				int x = (Integer) args[2];
				int y = (Integer) args[3];
				x += insets.left;
				y += insets.top;
				icon.paintIcon(c, g, x, y);
				return Void.TYPE;
			} else if ("hashCode".equals(method.getName())
					&& params.length == 0) {
				return insets.hashCode() + icon.hashCode();
			} else if ("equals".equals(method.getName()) && params.length == 1
					&& params[0].getType().equals(Object.class)) {
				Object obj = (Object) args[0];
				if (!(obj instanceof PaddedIcon))
					return false;
				PaddedIcon p = (PaddedIcon) obj;
				if (!p.getIconInsets().equals(insets))
					return false;
				return p.getPaddedIcon().equals(icon);
			} else {
				return method.invoke(icon, args);
			}
		}
	}

	private static class ScaledIconInvocationHandler implements
			InvocationHandler {
		Icon icon;
		Dimension size;

		public ScaledIconInvocationHandler(Icon icon, Dimension size) {
			Objects.requireNonNull(icon);
			Objects.requireNonNull(size);
			this.icon = icon;
			this.size = new Dimension(size.width, size.height);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			Parameter[] params = method.getParameters();
			if ("getScaledIcon".equals(method.getName()) && params.length == 0) {
				return icon;
			} else if ("getIconHeight".equals(method.getName())
					&& params.length == 0) {
				return size.height;
			} else if ("getIconWidth".equals(method.getName())
					&& params.length == 0) {
				return size.width;
			} else if ("paintIcon".equals(method.getName())
					&& params.length == 4
					&& params[0].getType().equals(Component.class)
					&& params[1].getType().equals(Graphics.class)
					&& params[2].getType().equals(Integer.TYPE)
					&& params[3].getType().equals(Integer.TYPE)) {
				Component c = (Component) args[0];
				Graphics g = (Graphics) args[1];
				int x = (Integer) args[2];
				int y = (Integer) args[3];

				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
						RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.transform(TransformUtils.createAffineTransform(x, y, x
						+ icon.getIconWidth(), y, x, y + icon.getIconHeight(),
						x, y, x + size.width, y, x, y + size.height));
				icon.paintIcon(c, g2, x, y);
				g2.dispose();

				return Void.TYPE;
			} else if ("hashCode".equals(method.getName())
					&& params.length == 0) {
				return size.hashCode() + icon.hashCode();
			} else if ("equals".equals(method.getName()) && params.length == 1
					&& params[0].getType().equals(Object.class)) {
				Object obj = (Object) args[0];
				if (!(obj instanceof ScaledIcon))
					return false;
				ScaledIcon s = (ScaledIcon) obj;
				if (size.width != s.getIconWidth())
					return false;
				if (size.height != s.getIconHeight())
					return false;
				return icon.equals(s.getScaledIcon());
			} else {
				return method.invoke(icon, args);
			}
		}
	}

	public static PaddedIcon createPaddedIcon(Icon icon, Dimension targetSize) {
		if (icon == null)
			icon = new EmptyIcon(0, 0);

		int x = targetSize.width - icon.getIconWidth();
		int y = targetSize.height - icon.getIconHeight();

		if (x < 0)
			throw new IllegalArgumentException(
					"Icon too wide to fit in target size. " + targetSize.width
							+ "<" + icon.getIconWidth());
		if (y < 0)
			throw new IllegalArgumentException(
					"Icon too tall to fit in target size. " + targetSize.height
							+ "<" + icon.getIconHeight());

		int top = y / 2;
		int bottom = y - top;
		int left = x / 2;
		int right = x - left;
		Insets insets = new Insets(top, left, bottom, right);

		return createPaddedIcon(icon, insets);
	}

	public static PaddedIcon createPaddedIcon(Icon icon, int padding) {
		Insets insets = new Insets(padding, padding, padding, padding);
		return createPaddedIcon(icon, insets);
	}

	public static PaddedIcon createPaddedIcon(Icon icon, Insets insets) {
		if (icon instanceof PaddedIcon) {
			PaddedIcon p = (PaddedIcon) icon;
			Icon originalIcon = p.getPaddedIcon();
			Insets origInsets = p.getIconInsets();
			insets.left += origInsets.left;
			insets.top += origInsets.top;
			insets.bottom += origInsets.bottom;
			insets.right += origInsets.right;
			return createPaddedIcon(originalIcon, insets);
		}
		Class<?>[] interfaces = getInterfaces(icon, PaddedIcon.class);
		InvocationHandler handler = new PaddedIconInvocationHandler(icon,
				insets);
		return (PaddedIcon) Proxy.newProxyInstance(
				IconUtils.class.getClassLoader(), interfaces, handler);
	}

	public static ScaledIcon createScaledIcon(Icon icon, int width, int height) {
		return createScaledIcon(icon, new Dimension(width, height));
	}

	public static ScaledIcon createScaledIcon(Icon icon, Dimension iconSize) {
		if (icon instanceof ScaledIcon) {
			ScaledIcon s = (ScaledIcon) icon;
			Icon originalIcon = s.getScaledIcon();
			return createScaledIcon(originalIcon, iconSize);
		}
		Class<?>[] interfaces = getInterfaces(icon, ScaledIcon.class);
		InvocationHandler handler = new ScaledIconInvocationHandler(icon,
				iconSize);
		return (ScaledIcon) Proxy.newProxyInstance(
				IconUtils.class.getClassLoader(), interfaces, handler);

	}

	/**
	 * Return an array of all the interfaces an object current implements plus
	 * the additional class.
	 */
	private static Class<?>[] getInterfaces(Object obj, Class<?> additionalClass) {
		Class<?>[] interfaces = obj.getClass().getInterfaces();
		Class<?>[] returnValue = new Class[interfaces.length + 1];
		System.arraycopy(interfaces, 0, returnValue, 0, interfaces.length);
		returnValue[returnValue.length - 1] = additionalClass;
		return returnValue;
	}
}
