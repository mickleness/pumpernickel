/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

import javax.accessibility.AccessibleIcon;
import javax.swing.Icon;

import com.pump.geom.TransformUtils;

/**
 * This is a set of static methods related to help with
 * <code>javax.swing.Icons</code>.
 * <p>
 * Many of these methods focus on creating new Icon objects that extend the
 * functionality of an Icon but still support all the original interfaces that
 * Icon supported. (This is implemented using the Proxy reflection class.) For
 * example if you want to scale or pad an Icon that is also a UIResource: the
 * helper method will create a new PaddedIcon or ScaledIcon that also identifies
 * as a UIResource.
 */
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

	private static class AccessibleIconInvocationHandler implements
			InvocationHandler {

		Icon icon;
		String description;

		public AccessibleIconInvocationHandler(Icon icon) {
			Objects.requireNonNull(icon);
			this.icon = icon;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			Parameter[] params = method.getParameters();
			if ("getAccessibleIconDescription".equals(method.getName())
					&& params.length == 0) {
				return description;
			} else if ("setAccessibleIconDescription".equals(method.getName())
					&& String.class.equals(params[0].getType())) {
				description = (String) args[0];
				return Void.TYPE;
			} else if ("getAccessibleIconWidth".equals(method.getName())
					&& params.length == 0) {
				return icon.getIconWidth();
			} else if ("getAccessibleIconHeight".equals(method.getName())
					&& params.length == 0) {
				return icon.getIconHeight();
			}
			return method.invoke(icon, args);
		}
	}

	/**
	 * Create a new PaddedIcon.
	 * 
	 * @param icon
	 *            the icon to pad. If this is already a PaddedIcon then a new
	 *            PaddedIcon is created that adds additional padding as
	 *            necessary.
	 * @param targetSize
	 *            the size to stretch the icon to fill. The new PaddedIcon will
	 *            be centered within this space. This method throws an exception
	 *            if the targetSize is smaller than the original icon.
	 * @return a new PaddedIcon.
	 */
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

	/**
	 * Create a new PaddedIcon.
	 * 
	 * @param icon
	 *            the icon to pad. If this is already a PaddedIcon then a new
	 *            PaddedIcon is created that adds additional padding.
	 * @param padding
	 *            the padding to uniformly apply to all sides of the icon.
	 * @return a new PaddedIcon.
	 */
	public static PaddedIcon createPaddedIcon(Icon icon, int padding) {
		Insets insets = new Insets(padding, padding, padding, padding);
		return createPaddedIcon(icon, insets);
	}

	/**
	 * Create a new PaddedIcon.
	 * 
	 * @param icon
	 *            the icon to pad. If this is already a PaddedIcon then a new
	 *            PaddedIcon is created that adds additional padding.
	 * @param insets
	 *            the padding to apply to the icon.
	 * @return a new PaddedIcon.
	 */
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

	/**
	 * Create a new ScaledIcon.
	 * 
	 * @param icon
	 *            the icon to scale. If this is already a ScaledIcon then a new
	 *            ScaledIcon is created that references the underlying icon.
	 * @param width
	 *            the width of the new icon.
	 * @param height
	 *            the height of the new icon.
	 * @return a new ScaledIcon.
	 */
	public static ScaledIcon createScaledIcon(Icon icon, int width, int height) {
		return createScaledIcon(icon, new Dimension(width, height));
	}

	/**
	 * Create a new ScaledIcon.
	 * 
	 * @param icon
	 *            the icon to scale. If this is already a ScaledIcon then a new
	 *            ScaledIcon is created that references the underlying icon.
	 * @param iconSize
	 *            the dimensions of the new icon.
	 * @return a new ScaledIcon.
	 */
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
	 * Create an AccessibleIcon that is also an Icon.
	 * 
	 * @param icon
	 *            the icon to convert to an AccessibleIcon. If this is already
	 *            an AccessibleIcon then a new AccessibleIcon is still created
	 *            that will store a unique description. (So if you change one
	 *            AccessibleIcon's description, the other AccessibleIcon will be
	 *            unaffected.)
	 * @return
	 */
	public static AccessibleIcon createAccessibleIcon(Icon icon) {
		Class<?>[] interfaces = getInterfaces(icon, AccessibleIcon.class);
		InvocationHandler handler = new AccessibleIconInvocationHandler(icon);
		AccessibleIcon returnValue = (AccessibleIcon) Proxy.newProxyInstance(
				IconUtils.class.getClassLoader(), interfaces, handler);

		if (icon instanceof AccessibleIcon) {
			// initialize our new AccessibleIcon's description if possible:
			AccessibleIcon original = (AccessibleIcon) icon;
			returnValue.setAccessibleIconDescription(original
					.getAccessibleIconDescription());
		}

		return returnValue;
	}

	/**
	 * Return an array of all the interfaces an object current implements plus
	 * the additional class.
	 */
	private static Class<?>[] getInterfaces(Object obj,
			Class<?> additionalInterface) {
		Collection<Class<?>> interfaces = new HashSet<>();
		Class<?> z = obj.getClass();
		while (z != null) {
			interfaces.addAll(Arrays.asList(z.getInterfaces()));
			z = z.getSuperclass();
		}
		interfaces.add(additionalInterface);
		return interfaces.toArray(new Class[interfaces.size()]);
	}
}