package com.pump.xray;

import java.lang.reflect.Modifier;

import com.pump.io.IndentedPrintStream;

public class ConstructorOrMethodWriter extends StreamWriter {

	protected int modifiers;
	protected Class returnType;
	protected Class[] paramTypes;
	protected Class[] throwsTypes;
	protected String name;
	protected boolean writeBody;
	
	public ConstructorOrMethodWriter(JarBuilder builder,int modifiers,Class returnType,String name,Class[] paramTypes,Class[] throwsTypes) {
		super(builder);
		this.modifiers = modifiers;
		this.returnType = returnType;
		this.name = name;
		this.paramTypes = paramTypes;
		this.throwsTypes = throwsTypes;
		writeBody = !(Modifier.isAbstract(modifiers) || Modifier.isNative(modifiers));
	}

	@Override
	public void write(IndentedPrintStream ips, boolean emptyFile) throws Exception {
		ips.print( toString(modifiers) );
		if(returnType!=null) {
			ips.print(" "+toString( returnType, true) );
		}
		ips.print(" "+name+"(" );
		for(int a = 0; a<paramTypes.length; a++) {
			if(a>0)
				ips.print(", ");
			ips.print( toString(paramTypes[a], true)+" arg"+a);
		}
		ips.print(")");
		if(throwsTypes.length>0) {
			ips.print(" throws ");
			for(int a = 0; a<throwsTypes.length; a++) {
				if(a>0)
					ips.print(", ");
				ips.print( toString(throwsTypes[a], true) );
			}
		}
		if(!writeBody) {
			ips.println(";");
		} else {
			ips.println(" {");
			try(AutoCloseable c = ips.indent()) {
				writeBody(ips);
			}
			ips.println("}");
		}
	}

	protected void writeBody(IndentedPrintStream ips) {
		if(!Void.TYPE.equals(returnType)) {
			ips.println("return "+getValue(returnType, false)+";");
		}
	}

	protected String getValue(Class type,boolean cast) {
		String value;
		if(Character.TYPE.equals(returnType)) {
			value = "\'?\'";
		} else if(Long.TYPE.equals(returnType)) {
			value = "0L";
		} else if(Double.TYPE.equals(returnType)) {
			value = "0.0";
		} else if(Float.TYPE.equals(returnType)) {
			value = "0f";
		} else if(Integer.TYPE.equals(returnType)) {
			value = "0";
		} else if(Byte.TYPE.equals(returnType)) {
			value = "(byte)0";
		} else if(Short.TYPE.equals(returnType)) {
			value = "(short)0";
		} else if(Boolean.TYPE.equals(returnType)) {
			value = Boolean.FALSE.toString();
		} else if(cast) {
			value = "("+toString(type, true)+")";
		} else {
			value = "null";
		}
		return value;
	}
	
}