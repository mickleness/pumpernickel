package com.pump.xray;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MethodWriter extends ConstructorOrMethodWriter {

	public MethodWriter(SourceCodeManager sourceCodeManager,Method method) {
		super(sourceCodeManager, method.getModifiers(), method.getTypeParameters(), method.getGenericReturnType(), method.getName(), method.getGenericParameterTypes(), method.getGenericExceptionTypes());	
		
		if(method.getDeclaringClass().isInterface()) {
			if( Modifier.isAbstract(modifiers) ) {
				modifiers = modifiers - Modifier.ABSTRACT;
				writeBody = false;
			}
		} else if(method.getDeclaringClass().isEnum()) {
			// Don't acknowledge anything inside an enum is abstract.
			// The point of x-ray is to build jars to compile against, and
			// the only time knowing when something is abstract or not will
			// matter to the consumer is when they can subclass it. Nobody
			// can subclass an enum, so it's not worth addressing here.
			if( Modifier.isAbstract(modifiers) ) {
				modifiers = modifiers - Modifier.ABSTRACT;
				writeBody = true;
			}
		}
	}
}