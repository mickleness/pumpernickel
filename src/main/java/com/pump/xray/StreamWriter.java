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
package com.pump.xray;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

import com.pump.io.parser.java.JavaEncoding;

/**
 * This writes java code to a {@link ClassWriterStream}.
 */
public abstract class StreamWriter implements Comparable<StreamWriter> {

	/**
	 * Convert a constant representing modifiers to a String.
	 * 
	 * @param modifiers
	 *            modifiers, such as
	 *            <code>Modifier.ABSTRACT + Modifier.PUBLIC</code>
	 * @return a Java-formatted string, such as "public abstract"
	 */
	public static String toString(int modifiers) {
		StringBuilder sb = new StringBuilder();
		boolean isAbstract = Modifier.isAbstract(modifiers);
		boolean isFinal = Modifier.isFinal(modifiers);
		boolean isNative = Modifier.isNative(modifiers);
		boolean isPrivate = Modifier.isPrivate(modifiers);
		boolean isProtected = Modifier.isProtected(modifiers);
		boolean isPublic = Modifier.isPublic(modifiers);
		boolean isStatic = Modifier.isStatic(modifiers);
		boolean isSynchronized = Modifier.isSynchronized(modifiers);

		// TODO: some errors were observed with these, and I'm not sure they're
		// necessary for mocked code
		boolean isStrict = false; // Modifier.isStrict(modifiers);
		boolean isVolatile = false; // Modifier.isVolatile(modifiers);
		boolean isTransient = false; // Modifier.isTransient(modifiers);

		if (isPublic) {
			sb.append("public ");
		}
		if (isPrivate) {
			sb.append("private ");
		}
		if (isAbstract) {
			sb.append("abstract ");
		}
		if (isFinal) {
			sb.append("final ");
		}
		if (isNative) {
			sb.append("native ");
		}
		if (isProtected) {
			sb.append("protected ");
		}
		if (isStatic) {
			sb.append("static ");
		}
		if (isStrict) {
			sb.append("strictfp ");
		}
		if (isSynchronized) {
			sb.append("synchronized ");
		}
		if (isTransient) {
			sb.append("transient ");
		}
		if (isVolatile) {
			sb.append("volatile ");
		}

		return sb.toString().trim();
	}

	/** An optional SourceCodeManager. */
	protected SourceCodeManager sourceCodeManager;

	public StreamWriter(SourceCodeManager sourceCodeManager) {
		this.sourceCodeManager = sourceCodeManager;
	}

	/**
	 * Convert a value to java code, or return null.
	 * 
	 * @param value
	 *            a value, such as a String, Character, Float, Long, Short,
	 *            Byte, Integer, Boolean or Double.
	 * 
	 * @return a java code representation of this object, or null if the
	 *         argument isn't supported.
	 */
	protected String toString(Object value) {
		if (value instanceof String) {
			String str = (String) value;
			return "\"" + JavaEncoding.encode(str) + "\"";
		} else if (value instanceof Character) {
			Character ch = (Character) value;
			return "\'" + JavaEncoding.encode(ch.toString()) + "\'";
		} else if (value instanceof Float) {
			return value.toString() + "f";
		} else if (value instanceof Long) {
			return value.toString() + "L";
		} else if (value instanceof Short) {
			return "(short)" + value.toString();
		} else if (value instanceof Byte) {
			return "(byte)" + value.toString();
		} else if (value instanceof Integer || value instanceof Boolean
				|| value instanceof Double) {
			return value.toString();
		}
		return null;
	}

	/**
	 * Convert a Type object to java code.
	 * 
	 * @param nameToSimpleName
	 *            a map of fully qualified java names to simple names, such as
	 *            "java.lang.Thread" to "Thread". This method takes this map
	 *            into account to simplify names. This is not just a nicety to
	 *            make code more readable: in rare cases this is a requirement
	 *            to avoid compiler errors.
	 * @param t
	 *            the Type to represent.
	 * @return java code representing the Type provided.
	 */
	protected String toString(Map<String, String> nameToSimpleName, Type t,
			boolean declaration) {
		if (t instanceof Class) {
			return toString(nameToSimpleName, (Class) t);
		}
		if (t instanceof ParameterizedType) {
			ParameterizedType p = (ParameterizedType) t;
			Type[] args = p.getActualTypeArguments();
			Type owner = p.getOwnerType();
			Type raw = p.getRawType();
			StringBuilder sb = new StringBuilder();
			sb.append(toString(nameToSimpleName, raw, declaration));
			if (args.length > 0) {
				sb.append("<");
				for (int a = 0; a < args.length; a++) {
					if (a > 0)
						sb.append(", ");
					sb.append(toString(nameToSimpleName, args[a], false));
				}
				sb.append(">");
			}
			return sb.toString();
		} else if (t instanceof TypeVariable) {
			TypeVariable v = (TypeVariable) t;
			String name = v.getName();
			GenericDeclaration dec = v.getGenericDeclaration();
			Type[] bounds = v.getBounds();
			if (declaration && bounds.length == 1
					&& (bounds[0] instanceof Class)
					&& (!bounds[0].equals(Object.class))) {
				return name + " extends " + ((Class) bounds[0]).getName();
			} else if (declaration && bounds.length == 1
					&& (bounds[0] instanceof ParameterizedType)) {
				return name + " extends "
						+ toString(nameToSimpleName, bounds[0], false);
			}
			return name;
		} else if (t instanceof GenericArrayType) {
			GenericArrayType g = (GenericArrayType) t;
			return toString(nameToSimpleName, g.getGenericComponentType(),
					declaration) + "[]";
		} else if (t instanceof WildcardType) {
			WildcardType w = (WildcardType) t;
			Type[] lowerBounds = w.getLowerBounds();
			Type[] upperBounds = w.getUpperBounds();
			if (upperBounds.length == 1 && upperBounds[0].equals(Object.class)) {
				return "?";
			} else if (upperBounds.length == 1) {
				return "? extends "
						+ toString(nameToSimpleName, upperBounds[0],
								declaration);
			}
		}
		return t.toString();
	}

	/**
	 * Convert a Class object to java code.
	 * 
	 * @param nameToSimpleName
	 *            a map of fully qualified java names to simple names, such as
	 *            "java.lang.Thread" to "Thread". This method takes this map
	 *            into account to simplify names. This is not just a nicety to
	 *            make code more readable: in rare cases this is a requirement
	 *            to avoid compiler errors.
	 * @param t
	 *            the Class to write.
	 * @return java code representing the Class provided.
	 */
	protected String toString(Map<String, String> nameToSimpleName, Class t) {
		String name = null;

		if (sourceCodeManager != null && sourceCodeManager.isSupported(t)) {
			sourceCodeManager.addClasses(t);
		}

		if (t.isArray()) {
			return toString(nameToSimpleName, t.getComponentType()) + "[]";
		}
		name = t.getName();

		String str = name.replace("$", ".");
		if (nameToSimpleName != null && nameToSimpleName.containsKey(str)) {
			return nameToSimpleName.get(str);
		}

		return str;
	}

	/**
	 * Return the default value of the type provided.
	 * 
	 * @param nameToSimpleName
	 *            a map of fully qualified java names to simple names, such as
	 *            "java.lang.Thread" to "Thread". This method takes this map
	 *            into account to simplify names. This is not just a nicety to
	 *            make code more readable: in rare cases this is a requirement
	 *            to avoid compiler errors.
	 * @param type
	 *            the Class to represent
	 * @param cast
	 *            true if this should preface the value with a cast (if
	 *            appropriate). For example: when writing a constructor we
	 *            shouldn't say "super(null)", because the compiler may flag
	 *            that as ambiguous. Instead we should say
	 *            "super( (java.lang.Thread) null)".
	 * @return a java representation of a default value for the type provided,
	 *         such as "0L" (for longs), "'?'" (for chars), "(short)0" (for
	 *         shorts), or "null" for objects.
	 * 
	 *         This method will not return null. (But it may return "null").
	 */
	protected String getValue(Map<String, String> nameToSimpleName, Class type,
			boolean cast) {
		String value;
		if (Character.TYPE.equals(type)) {
			value = "\'?\'";
		} else if (Long.TYPE.equals(type)) {
			value = "0L";
		} else if (Double.TYPE.equals(type)) {
			value = "0.0";
		} else if (Float.TYPE.equals(type)) {
			value = "0f";
		} else if (Integer.TYPE.equals(type)) {
			value = "0";
		} else if (Byte.TYPE.equals(type)) {
			value = "(byte)0";
		} else if (Short.TYPE.equals(type)) {
			value = "(short)0";
		} else if (Boolean.TYPE.equals(type)) {
			value = Boolean.FALSE.toString();
		} else if (cast) {
			value = "(" + toString(nameToSimpleName, type) + ") null";
		} else {
			value = "null";
		}
		return value;
	}

	@Override
	public int compareTo(StreamWriter other) {
		return toString().compareTo(other.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof StreamWriter) {
			return compareTo((StreamWriter) other) == 0;
		}
		return false;
	}

	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (ClassWriterStream ips = new ClassWriterStream(out, true, "UTF-8")) {
			write(ips);
			return new String(out.toByteArray(), "UTF-8");
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Write this java source code to a stream.
	 * 
	 * @param cps
	 *            the stream to write to.
	 */
	public abstract void write(ClassWriterStream cps) throws Exception;
}