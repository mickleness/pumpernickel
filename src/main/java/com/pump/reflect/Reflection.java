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
package com.pump.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * A set of static methods relating to reflection.
 * 
 */
public class Reflection {

	/**
	 * Uses reflection to retrieve a static field from a class.
	 * 
	 * @return null if an error occurred retrieving this value
	 */
	public static Object getFieldValue(String className, String fieldName) {
		try {
			Class<?> c = Class.forName(className);
			Field f = c.getField(fieldName);
			return f.get(null);
		} catch (Throwable t) {
			return null;
		}
	}

	/**
	 * Return an array of objects based on a comma-separated description of its
	 * contents.
	 */
	protected static Object[] parseCommaSeparatedList(String arguments) {
		if (arguments.trim().length() == 0)
			return new Object[] {};
		List<String> listElements = new ArrayList<String>();
		Stack<Character> constructs = new Stack<Character>();
		int startingIndex = 0;
		for (int i = 0; i < arguments.length(); i++) {
			char ch = arguments.charAt(i);
			if (ch == ',' && constructs.size() == 0) {
				listElements.add(arguments.substring(startingIndex, i));
				startingIndex = i + 1;
			} else if (ch == '('
					&& (constructs.size() == 0 || constructs.peek() != '"')) {
				constructs.add('(');
			} else if (ch == ')' && constructs.size() > 0
					&& constructs.peek() == '(') {
				constructs.pop();
			} else if (ch == '"') {
				if (constructs.size() > 0 && constructs.peek() == '"') {
					constructs.pop();
				} else {
					constructs.add('"');
				}
			} else if (ch == '\\' && constructs.size() > 0
					&& constructs.peek() == '"') {
				i++;
			}
		}
		listElements.add(arguments.substring(startingIndex));

		Object[] returnValue = new Object[listElements.size()];
		for (int a = 0; a < returnValue.length; a++) {
			returnValue[a] = parse(listElements.get(a).trim());
		}
		return returnValue;
	}

	static Pattern longPattern = Pattern.compile("\\d+L");
	static Pattern intPattern = Pattern.compile("\\d+");
	static Pattern floatPattern = Pattern.compile("[0-9]*\\.?[0-9]+");
	static Pattern fieldPattern = Pattern
			.compile("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*");
	static Pattern newPattern = Pattern
			.compile("new\\s+([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*\\(\\s*.*\\s*\\)");
	static Pattern methodPattern = Pattern
			.compile("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*\\(\\s*.*\\s*\\)");
	static Pattern stringPattern = Pattern.compile("\"[\\.|[^\"]]*\"");

	/**
	 * Parse an object as null, an int, a long, a float, a String, a static
	 * field, or "new xyz(..)" (where ".." looks recursively for a
	 * comma-separated list of arguments)).
	 * <p>
	 * For example, you can call: <br>
	 * <code>parse( "new Color( 255, 0, 128 )" );</code> <br>
	 * <code>parse( "new com.pump.swing.resources.ArrowIcon( javax.swing.SwingConstants.EAST, 24, 24 )" );</code>
	 * <br>
	 * <code>parse( "new com.pump.swing.resources.TriangleIcon( javax.swing.SwingConstants.EAST, 24, 24, new Color(0)" );</code>
	 * <p>
	 * The class name/constants must be fully qualified.
	 */
	public static Object parse(final String input) {
		String string = input.trim();
		if ("true".equalsIgnoreCase(string)) {
			return Boolean.TRUE;
		} else if ("false".equalsIgnoreCase(string)) {
			return Boolean.FALSE;
		} else if ("null".equalsIgnoreCase(string)) {
			return null;
		} else if (intPattern.matcher(string).matches()) {
			return Integer.parseInt(string);
		} else if (longPattern.matcher(string).matches()) {
			return Long.parseLong(string);
		} else if (floatPattern.matcher(string).matches()) {
			return Float.parseFloat(string);
		} else if (stringPattern.matcher(string).matches()) {
			return string.substring(1, string.length() - 1);
		} else if (newPattern.matcher(string).matches()) {
			string = string.substring(3).trim();
			int i1 = string.indexOf('(');
			int i2 = string.lastIndexOf(')');
			String className = string.substring(0, i1);
			String arguments = string.substring(i1 + 1, i2);

			try {
				Class<?> t = Class.forName(className);
				return construct(t, arguments);
			} catch (ClassNotFoundException | SecurityException
					| IllegalArgumentException e) {
				throw new RuntimeException("An error occurred parsing \""
						+ input + "\" as a field.", e);
			}
		} else if (methodPattern.matcher(string).matches()) {
			int i1 = string.indexOf('(');
			int i2 = string.lastIndexOf(')');
			String lhs = string.substring(0, i1);
			String arguments = string.substring(i1 + 1, i2);
			i1 = lhs.lastIndexOf('.');
			String className = lhs.substring(0, i1);
			String methodName = lhs.substring(i1 + 1);

			Object obj = null;
			try {
				Class<?> t = null;
				List<String> fieldNames = new ArrayList<String>();
				while (t == null) {
					try {
						t = Class.forName(className);
					} catch (ClassNotFoundException e) {
						int i = className.lastIndexOf('.');
						if (i == -1) {
							break;
						}
						String fieldName = className.substring(i + 1);
						className = className.substring(0, i);
						fieldNames.add(fieldName);
					}
				}

				for (int a = 0; a < fieldNames.size(); a++) {
					if (obj != null)
						t = obj.getClass();

					obj = t.getField(fieldNames.get(0)).get(obj);
				}

				if (obj != null)
					t = obj.getClass();

				Method[] m = t.getMethods();
				for (int a = 0; a < m.length; a++) {
					// TODO: consider overloaded method names, similar to how
					// we look for the best-fit constructor
					boolean isStatic = (m[a].getModifiers() | Modifier.STATIC) > 0;
					if (m[a].getName().equals(methodName) && isStatic) {
						return m[a].invoke(obj,
								parseCommaSeparatedList(arguments));
					}
				}
			} catch (SecurityException | IllegalArgumentException
					| IllegalAccessException | InvocationTargetException
					| NoSuchFieldException e) {
				throw new RuntimeException("An error occurred parsing \""
						+ input + "\" as a method.", e);
			}
			throw new RuntimeException("Unrecognized method for " + input
					+ "\"");
		} else if (fieldPattern.matcher(string).matches()) {
			int i = string.lastIndexOf('.');
			String className = string.substring(0, i);
			if (className.length() == 0)
				throw new RuntimeException(
						"Unrecognized argument \""
								+ input
								+ "\". An attempt was made to parse as a field name, but no identifying class name was found.");

			String fieldName = string.substring(i + 1);
			try {
				Class<?> t = Class.forName(className);
				if (fieldName.equals("class"))
					return t;
				Field f = t.getDeclaredField(fieldName);
				if ((f.getModifiers() & Modifier.STATIC) == 0)
					throw new RuntimeException(
							"Unrecognized argument \""
									+ input
									+ "\". An attempt was made to parse as a field name, the field used was not static.");

				return f.get(null);
			} catch (ClassNotFoundException | NoSuchFieldException
					| SecurityException | IllegalArgumentException
					| IllegalAccessException e) {
				throw new RuntimeException("An error occurred parsing \""
						+ input + "\" as a field.", e);
			}
		}
		throw new RuntimeException("Unrecognized argument \"" + input + "\"");
	}

	/**
	 * This constructs an instance of t if a constructor is found that matches
	 * the arguments provided. This will not identify a varargs constructor. If
	 * multiple constructors match the arguments provided, this method will
	 * choose one to use
	 * 
	 * @param t
	 *            the type of class to construct.
	 * @param arguments
	 *            a comma separated list of simple arguments, such as
	 *            primitives, strings, or static fields.
	 * @return a new instance of t created using the arguments provided, or an
	 *         exception will be thrown.
	 */
	protected static <T> T construct(Class<T> t, String arguments) {
		Object[] argumentObjects = parseCommaSeparatedList(arguments);
		Constructor<?>[] constructors = t.getDeclaredConstructors();

		class IncompatibleArgumentsException extends Exception {
			private static final long serialVersionUID = 1L;
		}

		class Match implements Comparable<Match> {
			Constructor<?> constructor;
			Object[] literalArguments;
			Object[] convertedArguments;
			double score = 0;

			Match(Constructor<?> constructor, Class<?>[] idealArgTypes,
					Object[] arguments) throws IncompatibleArgumentsException {
				this.constructor = constructor;
				this.literalArguments = arguments;
				this.convertedArguments = new Object[literalArguments.length];

				for (int a = 0; a < idealArgTypes.length; a++) {
					Class<?> currentClass = arguments[a] == null ? null
							: arguments[a].getClass();

					if (idealArgTypes[a].equals(currentClass)) {
						score += 1;
						convertedArguments[a] = arguments[a];
					} else if (isInteger(idealArgTypes[a])
							&& isInteger(currentClass)) {
						score += 1;
						convertedArguments[a] = arguments[a];
					} else if (isLong(idealArgTypes[a]) && isLong(currentClass)) {
						score += 1;
						convertedArguments[a] = arguments[a];
					} else if (isFloat(idealArgTypes[a])
							&& isFloat(currentClass)) {
						score += 1;
						convertedArguments[a] = arguments[a];
					} else if (isShort(idealArgTypes[a])
							&& isShort(currentClass)) {
						score += 1;
						convertedArguments[a] = arguments[a];
					} else if (isChar(idealArgTypes[a]) && isChar(currentClass)) {
						score += 1;
						convertedArguments[a] = arguments[a];
					} else if (isBoolean(idealArgTypes[a])
							&& isBoolean(currentClass)) {
						score += 1;
						convertedArguments[a] = arguments[a];
					} else if ((!idealArgTypes[a].isPrimitive())
							&& currentClass == null) {
						score += .95;
						convertedArguments[a] = null;
					} else if (idealArgTypes[a].isAssignableFrom(currentClass)) {
						score += .5;
						convertedArguments[a] = arguments[a];
					} else if (isLong(idealArgTypes[a])
							&& isInteger(currentClass)) {
						score += .25;
						convertedArguments[a] = new Long(
								((Integer) arguments[a]).longValue());
					} else if (isDouble(idealArgTypes[a])
							&& isInteger(currentClass)) {
						score += .25;
						convertedArguments[a] = new Double(
								((Integer) arguments[a]).doubleValue());
					} else if (isFloat(idealArgTypes[a])
							&& isInteger(currentClass)) {
						score += .25;
						convertedArguments[a] = new Float(
								((Integer) arguments[a]).floatValue());
					} else if (isShort(idealArgTypes[a])
							&& isInteger(currentClass)) {
						score += .25;
						convertedArguments[a] = new Short(
								((Integer) arguments[a]).shortValue());
					} else if (isDouble(idealArgTypes[a])
							&& isFloat(currentClass)) {
						score += .15;
						convertedArguments[a] = new Double(
								((Float) arguments[a]).doubleValue());
					} else {
						throw new IncompatibleArgumentsException();
					}
				}
				score = score / ((double) idealArgTypes.length);
			}

			private boolean isInteger(Class<?> c) {
				return Integer.TYPE.equals(c) || Integer.class.equals(c);
			}

			private boolean isLong(Class<?> c) {
				return Long.TYPE.equals(c) || Long.class.equals(c);
			}

			private boolean isDouble(Class<?> c) {
				return Double.TYPE.equals(c) || Double.class.equals(c);
			}

			private boolean isFloat(Class<?> c) {
				return Float.TYPE.equals(c) || Float.class.equals(c);
			}

			private boolean isShort(Class<?> c) {
				return Short.TYPE.equals(c) || Short.class.equals(c);
			}

			private boolean isChar(Class<?> c) {
				return Character.TYPE.equals(c) || Character.class.equals(c);
			}

			private boolean isBoolean(Class<?> c) {
				return Boolean.TYPE.equals(c) || Boolean.class.equals(c);
			}

			@Override
			public int compareTo(Match o) {
				if (score < o.score) {
					return -1;
				}
				if (score > o.score) {
					return 1;
				}
				return 0;
			}

		}

		SortedSet<Match> matches = new TreeSet<Match>();
		for (Constructor<?> constructor : constructors) {
			Class<?>[] argT = constructor.getParameterTypes();
			if (argT.length == argumentObjects.length) {
				try {
					matches.add(new Match(constructor, argT, argumentObjects));
				} catch (IncompatibleArgumentsException e) {
					// keep searching
				}
			}
		}
		if (matches.size() > 0) {
			try {
				Match match = matches.last();
				match.constructor.setAccessible(true);
				return (T) match.constructor
						.newInstance(match.convertedArguments);
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(
						"an error occurred invoking a constructor for "
								+ t.getName() + " using \"" + arguments + "\"",
						e);
			}
		}

		throw new RuntimeException("no constructor for " + t.getName()
				+ " was found that matched \"" + arguments + "\"");
	}

	/**
	 * This object is returned by <code>invokeMethod(..)</code> when an error
	 * occurs.
	 */
	public static final Object INVOCATION_ERROR = new Object();

	/**
	 * This uses reflection to call a method that may not exist in the compiling
	 * JVM, or is otherwise inaccessible.
	 * 
	 * @return INVOCATION_ERROR if an error occurs (details are printed to the
	 *         console), or the return value of the invocation.
	 */
	public static Object invokeMethod(Class<?> c, Object obj,
			String methodName, Object... arguments) {
		if (c == null && obj != null)
			c = obj.getClass();

		try {
			while (c != null) {
				Method[] methods = c.getDeclaredMethods();
				for (int a = 0; a < methods.length; a++) {
					if (methods[a].getName().equals(methodName)) {
						methods[a].setAccessible(true);
						try {
							return methods[a].invoke(obj, arguments);
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				}
				c = c.getSuperclass();
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return INVOCATION_ERROR;
	}

	/**
	 * This debugging tool combs through a class and tells you what public
	 * static field has the value you've provided.
	 * <P>
	 * For example, you can call: <BR>
	 * <code>nameStaticField(BufferedImage.class,new Integer(BufferedImage.TYPE_INT_ARGB))</code>
	 * <BR>
	 * And this method will return "TYPE_INT_ARGB".
	 * 
	 * @param c
	 *            the class of interest
	 * @param value
	 *            the value. Primitives must be wrapped.
	 * @return the string of the field with that value, or <code>null</code> if
	 *         not hits were found. (Or if multiple hits were found, this
	 *         returns a list of possible matches.)
	 */
	public static String nameStaticField(Class<?> c, Object value) {
		Field[] f = c.getDeclaredFields();
		List<Field> v = new ArrayList<>();
		for (int a = 0; a < f.length; a++) {
			if ((f[a].getModifiers() & Modifier.STATIC) > 0) {
				try {
					f[a].setAccessible(true);
					Object obj = f[a].get(null);
					if (obj != null && obj.equals(value)) {
						v.add(f[a]);
					}
				} catch (IllegalAccessException e) {
				}
			}
		}

		if (v.size() == 0)
			return null;

		if (v.size() == 1) {
			return (v.get(0)).getName();
		}

		// uh-oh, more than 1 field equalled the desired value...
		// could be the case a static float and a static int both
		// point to the number 1?

		int a = 0;
		while (a < v.size()) {
			try {
				Object obj = (v.get(a)).get(null);
				if (obj.getClass().equals(value.getClass()) == false) {
					v.remove(a);
				} else {
					a++;
				}
			} catch (IllegalAccessException e) {
				return "An unexpected error occurred the second time I tried to access a field.";
			}
		}

		if (v.size() == 1) {
			return (v.get(0)).getName();
		} else if (v.size() > 1) {
			return describe(v);
		}

		// last attempt:

		for (a = 0; a < v.size(); a++) {
			try {
				Object obj = (v.get(a)).get(null);
				if (obj.getClass().isInstance(value)) {
					return (v.get(a)).getName();
				}
			} catch (IllegalAccessException e) {
				return "An unexpected error occurred the second time I tried to access a field.";
			}
		}

		return describe(v);
	}

	private static String describe(List<Field> v) {
		// we failed. try to give helpful info:
		String s = "[ " + (v.get(0)).getName();
		for (int a = 1; a < v.size(); a++) {
			s += ", " + (v.get(a)).getName();
		}
		s += " ]";
		return s;
	}
}