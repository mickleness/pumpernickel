package com.pump.xray;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodWriter extends ConstructorOrMethodWriter {

	public MethodWriter(JarBuilder builder,Method method) {
		super(builder, method.getModifiers(), method.getReturnType(), method.getName(), method.getParameterTypes(), method.getExceptionTypes());	
		
		boolean isInterface = method.getDeclaringClass().isInterface();
		if(isInterface) {
			if( Modifier.isAbstract(modifiers) ) {
				modifiers = modifiers - Modifier.ABSTRACT;
				writeBody = false;
			}
		}
	}
}