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

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * This writes a java.lang.reflect.Method or java.lang.reflect.Constructor.
 */
public class ConstructorOrMethodWriter extends StreamWriter {

	protected int modifiers;
	protected Type returnType;
	protected Type[] paramTypes;
	protected Type[] throwsTypes;
	protected String name;
	protected boolean writeBody;
	protected TypeVariable[] typeVariables;
	protected boolean isVarArgs;

	/**
	 * Create a new ConstructorOrMethodWriter.
	 * 
	 * @param sourceCodeManager
	 *            the optional SourceCodeManager.
	 * @param modifiers
	 *            the modifiers of this method/constructor.
	 * @param typeVariables
	 *            the type variables (if any) of this method/constructor.
	 * @param returnType
	 *            the optional return type; may be null.
	 * @param name
	 *            the name of this method/constructor
	 * @param paramTypes
	 *            the parameter types of this method/constructor
	 * @param throwsTypes
	 *            the throw types of this method/constructor.
	 * @param isVarArgs
	 *            true if the last parameter is a varargs parameter.
	 */
	public ConstructorOrMethodWriter(SourceCodeManager sourceCodeManager,
			int modifiers, TypeVariable[] typeVariables, Type returnType,
			String name, Type[] paramTypes, Type[] throwsTypes,
			boolean isVarArgs) {
		super(sourceCodeManager);
		this.modifiers = modifiers;
		this.returnType = returnType;
		this.name = name;
		this.isVarArgs = isVarArgs;
		this.paramTypes = paramTypes;
		this.throwsTypes = throwsTypes;
		this.typeVariables = typeVariables;
		writeBody = !(Modifier.isAbstract(modifiers) || Modifier
				.isNative(modifiers));
	}

	@Override
	public void write(ClassWriterStream cws) throws Exception {
		cws.print(toString(modifiers));

		Map<String, String> nameToSimpleName = cws.getNameMap();

		if (typeVariables != null && typeVariables.length > 0) {
			cws.print(" <");
			for (int a = 0; a < typeVariables.length; a++) {
				if (a > 0)
					cws.print(", ");
				cws.print(toString(nameToSimpleName, typeVariables[a], true));
			}
			cws.print(">");
		}

		if (returnType != null) {
			cws.print(" " + toString(nameToSimpleName, returnType, false));
		}
		cws.print(" " + name + "(");
		for (int a = 0; a < paramTypes.length; a++) {
			if (a > 0)
				cws.print(", ");
			String s = toString(nameToSimpleName, paramTypes[a], false)
					+ " arg" + a;
			if (isVarArgs && a == paramTypes.length - 1) {
				int i = s.lastIndexOf("[]");
				s = s.substring(0, i) + "..." + s.substring(i + 2);
			}
			cws.print(s);
		}
		cws.print(")");
		if (throwsTypes.length > 0) {
			cws.print(" throws ");
			for (int a = 0; a < throwsTypes.length; a++) {
				if (a > 0)
					cws.print(", ");
				cws.print(toString(nameToSimpleName, throwsTypes[a], false));
			}
		}
		if (!writeBody) {
			cws.println(";");
		} else {
			cws.println(" {");
			try (AutoCloseable c = cws.indent()) {
				writeBody(cws);
			}
			cws.println("}");
		}
	}

	/**
	 * Write the body of this method/constructor. The default implementation
	 * supplies "return [..];" if necessary.
	 */
	protected void writeBody(ClassWriterStream cws) {
		if (!Void.TYPE.equals(returnType)) {
			if (returnType instanceof Class) {
				cws.println("return "
						+ getValue(cws.getNameMap(), (Class) returnType, false)
						+ ";");
			} else {
				cws.println("return null;");
			}
		}
	}
}