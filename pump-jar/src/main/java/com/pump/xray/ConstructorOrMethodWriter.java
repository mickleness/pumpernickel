package com.pump.xray;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public class ConstructorOrMethodWriter extends StreamWriter {

	protected int modifiers;
	protected Type returnType;
	protected Type[] paramTypes;
	protected Type[] throwsTypes;
	protected String name;
	protected boolean writeBody;
	protected TypeVariable[] typeVariables;
	
	public ConstructorOrMethodWriter(SourceCodeManager sourceCodeManager,int modifiers,TypeVariable[] typeVariables,Type returnType,String name,Type[] paramTypes,Type[] throwsTypes) {
		super(sourceCodeManager);
		this.modifiers = modifiers;
		this.returnType = returnType;
		this.name = name;
		this.paramTypes = paramTypes;
		this.throwsTypes = throwsTypes;
		this.typeVariables = typeVariables;
		writeBody = !(Modifier.isAbstract(modifiers) || Modifier.isNative(modifiers));
	}

	@Override
	public void write(ClassWriterStream cws, boolean emptyFile) throws Exception {
		cws.print( toString(modifiers) );
		
		Map<String, String> nameToSimpleName = cws.getNameMap();
		
		if(typeVariables!=null && typeVariables.length>0) {
			cws.print(" <");
			for(int a = 0; a<typeVariables.length; a++) {
				if(a>0)
					cws.print(", ");
				cws.print( toString(nameToSimpleName, typeVariables[a], true) );
			}
			cws.print(">");
		}
		
		if(returnType!=null) {
			cws.print(" "+toString(nameToSimpleName, returnType, true) );
		}
		cws.print(" "+name+"(" );
		for(int a = 0; a<paramTypes.length; a++) {
			if(a>0)
				cws.print(", ");
			cws.print( toString(nameToSimpleName, paramTypes[a], true)+" arg"+a);
		}
		cws.print(")");
		if(throwsTypes.length>0) {
			cws.print(" throws ");
			for(int a = 0; a<throwsTypes.length; a++) {
				if(a>0)
					cws.print(", ");
				cws.print( toString(nameToSimpleName, throwsTypes[a], true) );
			}
		}
		if(!writeBody) {
			cws.println(";");
		} else {
			cws.println(" {");
			try(AutoCloseable c = cws.indent()) {
				writeBody(cws);
			}
			cws.println("}");
		}
	}

	protected void writeBody(ClassWriterStream cws) {
		if(!Void.TYPE.equals(returnType)) {
			if(returnType instanceof Class) {
				cws.println("return "+getValue( cws.getNameMap(), (Class)returnType, false)+";");
			} else {
				cws.println("return null;");
			}
		}
	}
}