package com.pump.xray;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.TreeSet;

import com.pump.io.IndentedPrintStream;

public class ClassWriter extends StreamWriter {
	
	Class type;
	
	Collection<StreamWriter> members = new TreeSet<>();
	Collection<FieldWriter> fields = new TreeSet<>();
	Collection<ConstructorWriter> constructors = new TreeSet<>();
	Collection<MethodWriter> methods = new TreeSet<>();
	
	public ClassWriter(JarBuilder builder,Class type,boolean autopopulate) {
		super(builder);
		this.type = type;
		
		if(autopopulate) {
			Field[] fields = type.getDeclaredFields();
			for(Field field : fields) {
				int m = field.getModifiers();
				if(!Modifier.isPrivate(m))
					this.fields.add(new FieldWriter(builder, field));
			}
			Constructor[] constructors = type.getDeclaredConstructors();
			for(Constructor constructor : constructors) {
				int m = constructor.getModifiers();
				if(!Modifier.isPrivate(m))
					this.constructors.add(new ConstructorWriter(builder, constructor));
			}
			Method[] methods = type.getDeclaredMethods();
			for(Method method : methods) {
				int m = method.getModifiers();
				if(!Modifier.isPrivate(m))
					this.methods.add(new MethodWriter(builder, method));
			}
		}
	}

	@Override
	public void write(IndentedPrintStream ips, boolean emptyFile) throws Exception {
		if(emptyFile) {
			Package p = type.getPackage();
            ips.println("package " + p.getName() + ";");
            ips.println();
		}
		
		int modifiers = type.getModifiers();
		if(type.isInterface() && Modifier.isAbstract(modifiers)) {
			modifiers = modifiers - Modifier.ABSTRACT;
		}
		ips.print(toString(modifiers));
		
		if(type.isEnum()) {
			ips.print(" enum ");
			ips.print(type.getSimpleName());
            ips.println(" {");
    		try(AutoCloseable c = ips.indent()) {
    			writeEnumBody(ips);
    		}
            ips.println("}");
		} else {
			if(type.isInterface()) {
				ips.print(" interface ");
			} else {
				ips.print(" class ");
			}
			ips.print(type.getSimpleName());
			
			if (type.getSuperclass() != null)
            {
                ips.print(" extends " + toString(type.getSuperclass(), true));
            }

            Class[] classes = type.getInterfaces();
            int classesLen = classes.length;
            if(classes != null && classes.length > 0)
            {
                if(type.isInterface())
                    ips.print(" extends ");
                else
                    ips.print(" implements ");
                for(int x=0; x<classesLen; x++)
                {
                    ips.print(toString(classes[x], true));
                    if(x < classesLen - 1)
                    {
                        ips.print(", ");
                    }
                }
            }
            ips.println(" {");
    		try(AutoCloseable c = ips.indent()) {
    			writeClassOrInterfaceBody(ips);
    		}
            ips.println("}");
		}
	}

	protected void writeClassOrInterfaceBody(IndentedPrintStream ips) throws Exception {
		for(StreamWriter w : members) {
			w.write(ips, false);
			ips.println();
		}
		for(StreamWriter f : fields) {
			f.write(ips, false);
		}
		for(ConstructorWriter c : constructors) {
			ips.println();
			c.write(ips, false);
		}
		for(MethodWriter m : methods) {
			ips.println();
			m.write(ips, false);
		}
	}
	
	protected void writeEnumBody(IndentedPrintStream ips) throws Exception {
		for(StreamWriter w : members) {
			w.write(ips, false);
			ips.println();
		}
		for(FieldWriter f : fields) {
			boolean started = false;
			if(f.getField().isEnumConstant()) {
				if(started)
					ips.print(", ");
				ips.print(f.getField().getName());
				started = true;
			}
			if(started)
				ips.println(";");
		}
		for(FieldWriter f : fields) {
			if(!f.getField().isEnumConstant()) {
				f.write(ips, false);
			}
		}
		for(MethodWriter m : methods) {
			ips.println();
			m.write(ips, false);
		}
	}
	
	
}
