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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

public class FieldWriter extends StreamWriter {

	protected Field field;

	public FieldWriter(SourceCodeManager sourceCodeManager,Field field) {
		super(sourceCodeManager);
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	@Override
	public void write(ClassWriterStream cws, boolean emptyFile) throws Exception {
		cws.print( toString( field.getModifiers() ));
		cws.print( ' ' + toString(cws.getNameMap(), field.getGenericType(), true)+" "+field.getName());
		Object value = getSupportedConstantValue(cws.getNameMap());
		if(value!=null) {
			cws.println(" = "+value+";");
		} else {
			cws.println(";");
		}
	}

	private String getSupportedConstantValue(Map<String, String> nameToSimpleName) throws IllegalArgumentException, IllegalAccessException {
		boolean isFinal = field!=null && Modifier.isFinal(field.getModifiers());
		if (isFinal)
		{	
			String returnValue = null;
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
				boolean isStatic = Modifier.isStatic(field.getModifiers());
				if (isStatic) {
					field.setAccessible(true);
					Object v = field.get(null);
					returnValue = toString(v);
				} else {
					returnValue = getValue(nameToSimpleName, valueType, false);
				}
			}
			if(returnValue==null)
				return "null";
			return returnValue;
		}
		return null;
	}

}