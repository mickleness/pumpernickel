package com.pump.xray;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

public class ClassWriter extends StreamWriter {
	
	Class type;
	
	Collection<StreamWriter> members = new TreeSet<>();
	Collection<FieldWriter> fields = new TreeSet<>();
	Collection<ConstructorWriter> constructors = new TreeSet<>();
	Collection<MethodWriter> methods = new TreeSet<>();
	

	public ClassWriter(Class type,boolean autopopulate) {
		this(null, type, autopopulate);
	}
	
	public ClassWriter(SourceCodeManager sourceCodeManager,Class type,boolean autopopulate) {
		super(sourceCodeManager);
		this.type = type;
		
		if(autopopulate) {
			Field[] fields = type.getDeclaredFields();
			for(Field field : fields) {
				int m = field.getModifiers();
				boolean synthetic = field.isSynthetic();
				if( (!synthetic) && (!Modifier.isPrivate(m)) )
					this.fields.add(new FieldWriter(sourceCodeManager, field));
			}
			Constructor[] constructors = type.getDeclaredConstructors();
			for(Constructor constructor : constructors) {
				int m = constructor.getModifiers();
				if(!Modifier.isPrivate(m))
					this.constructors.add(new ConstructorWriter(sourceCodeManager, constructor));
			}
			
			//well... if we couldn't get any public/protected/package level constructors
			//to make visible, then we may HAVE to include private constructors to avoid compiler
			//errors:
			if(this.constructors.size()==0) {
				for(Constructor constructor : constructors) {
					this.constructors.add(new ConstructorWriter(sourceCodeManager, constructor));
				}
			}
			
			
			Method[] methods = type.getDeclaredMethods();
			for(Method method : methods) {
				int m = method.getModifiers();
				boolean include = true;
				if(Modifier.isPrivate(m) || method.getName().startsWith("access$"))
					include = false;
				if(type.isEnum() && method.getName().equals("values") && 
						method.getParameterTypes().length==0)
					include = false;
				if(type.isEnum() && method.getName().equals("valueOf") && 
						method.getParameterTypes().length==1 &&
						method.getParameterTypes()[0].equals(String.class))
					include = false;
				if(method.isSynthetic())
					include = false;
				
				if(include)
					this.methods.add(new MethodWriter(sourceCodeManager, method));
			}
		}
	}

	@Override
	public void write(ClassWriterStream cws, boolean emptyFile) throws Exception {
		Map<String, String> nameToSimpleName = cws.getNameMap();
		if(emptyFile) {
			nameToSimpleName.clear();
			populateNameMap(nameToSimpleName, this);
			
			Package p = type.getPackage();
			cws.println("package " + p.getName() + ";");
			cws.println();
		}
		
		int modifiers = type.getModifiers();
		
		if(type.isInterface() && Modifier.isAbstract(modifiers)) {
			//remove things that are implied
			//this is a minor readability nuisance:
			modifiers = modifiers - Modifier.ABSTRACT;
		} else if(type.isEnum()) {
			//these are compiler errors:
			if(Modifier.isFinal(modifiers))
				modifiers = modifiers - Modifier.FINAL;
			if(Modifier.isAbstract(modifiers))
				modifiers = modifiers - Modifier.ABSTRACT;
		}
		cws.print(toString(modifiers));
		
		if(type.isEnum()) {
			cws.print(" enum ");
			cws.print(type.getSimpleName());
			cws.println(" {");
    		try(AutoCloseable c = cws.indent()) {
    			writeEnumBody(cws);
    		}
    		cws.println("}");
		} else {
			if(type.isInterface()) {
				cws.print(" interface ");
			} else {
				cws.print(" class ");
			}
			cws.print(type.getSimpleName());
			TypeVariable[] typeParameters = type.getTypeParameters();
			if(typeParameters.length>0) {
				cws.print("<");
				for(int a = 0; a<typeParameters.length; a++) {
					if(a>0)
						cws.print(", ");
					cws.print( toString(nameToSimpleName, typeParameters[a], true) );
				}
				cws.print(">");
			}

        	Type genericSuperclass = type.getGenericSuperclass();
			if (genericSuperclass != null)
            {
				cws.print(" extends " + toString(nameToSimpleName, genericSuperclass, true));
            }

        	Type[] genericInterfaces = type.getGenericInterfaces();
        	
            int classesLen = genericInterfaces.length;
            if(genericInterfaces != null && genericInterfaces.length > 0)
            {
                if(type.isInterface())
                	cws.print(" extends ");
                else
                	cws.print(" implements ");
                for(int x=0; x<classesLen; x++)
                {
                	cws.print(toString(nameToSimpleName, genericInterfaces[x], true));
                    if(x < classesLen - 1)
                    {
                    	cws.print(", ");
                    }
                }
            }
            cws.println(" {");
    		try(AutoCloseable c = cws.indent()) {
    			writeClassOrInterfaceBody(cws);
    		}
    		cws.println("}");
		}
	}

	private void populateNameMap(Map<String, String> nameToSimpleName,ClassWriter writer) {
		String newValue = writer.getType().getSimpleName();
		String oldValue = nameToSimpleName.put(writer.getType().getName().replace('$', '.'), newValue);
		if(oldValue!=null && (!oldValue.equals(newValue)))
			throw new IllegalStateException("\""+oldValue+"\", \""+newValue+"\" "+writer.getType().getName());
		for(StreamWriter member : writer.members) {
			if(member instanceof ClassWriter) {
				populateNameMap( nameToSimpleName, (ClassWriter)member );
			}
		}
	}

	protected void writeClassOrInterfaceBody(ClassWriterStream cws) throws Exception {
		for(StreamWriter w : members) {
			w.write(cws, false);
			cws.println();
		}
		for(StreamWriter f : fields) {
			f.write(cws, false);
		}
		for(ConstructorWriter c : constructors) {
			cws.println();
			c.write(cws, false);
		}
		for(MethodWriter m : methods) {
			cws.println();
			m.write(cws, false);
		}
	}
	
	protected void writeEnumBody(ClassWriterStream cws) throws Exception {
		for(StreamWriter w : members) {
			w.write(cws, false);
			cws.println();
		}
		boolean started = false;
		for(FieldWriter f : fields) {
			if(f.getField().isEnumConstant()) {
				if(started)
					cws.print(", ");
				cws.print(f.getField().getName());
				started = true;
			}
		}
		if(started)
			cws.println(";");
		
		for(FieldWriter f : fields) {
			if(!f.getField().isEnumConstant()) {
				f.write(cws, false);
			}
		}
		for(MethodWriter m : methods) {
			cws.println();
			m.write(cws, false);
		}
	}

	/** Return the class this ClassWriter writes. */
	public Class getType() {
		return type;
	}

	/** Add a declared/nested class inside this class. */
	public void addDeclaredClass(ClassWriter writer) {
		members.add(writer);
	}

	public ClassWriter getDeclaredType(Class declaredType) {
		for(StreamWriter member : members) {
			if(member instanceof ClassWriter && ((ClassWriter)member).getType().equals(declaredType)) {
				return ((ClassWriter)member);
			}
		}
		return null;
	}
	
	
}
