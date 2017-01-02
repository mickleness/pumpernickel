package com.pump.xray;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.pump.io.IndentedPrintStream;
import com.pump.io.java.JavaEncoding;

public class FieldWriter extends StreamWriter {

	protected Field field;
	
	public FieldWriter(JarBuilder builder,Field field) {
		super(builder);
		this.field = field;
	}
	
	public Field getField() {
		return field;
	}

	@Override
	public void write(IndentedPrintStream ips, boolean emptyFile) throws Exception {
		ips.print( toString( field.getModifiers() ));
		ips.print( ' ' + toString(field.getType(), true)+" "+field.getName());
		Object value = getSupportedConstantValue();
		if(value!=null) {
			ips.println(" = "+toString(value)+";");
		} else {
			ips.println(";");
		}
	}
	
	private Object getSupportedConstantValue() throws IllegalArgumentException, IllegalAccessException {
		boolean isConstant = field!=null && Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers());
        if (isConstant)
        {
    		Class valueType = field.getType();
            if (String.class.equals(valueType) || 
            		Character.TYPE.equals(valueType) || 
            		Long.TYPE.equals(valueType) ||
            		Float.TYPE.equals(valueType) ||
            		Integer.TYPE.equals(valueType) ||
                    Short.TYPE.equals(valueType)  ||
                    Float.TYPE.equals(valueType)  ||
                    Boolean.TYPE.equals(valueType)  ||
                    Double.TYPE.equals(valueType)   ||
                    Byte.TYPE.equals(valueType)  )
            {
            	return field.get(null);
            }
        }
        return null;
	}
	
}