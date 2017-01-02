package com.pump.xray;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

import com.pump.io.IndentedPrintStream;

public class ConstructorWriter extends ConstructorOrMethodWriter {

	Constructor constructor;
	
	public ConstructorWriter(JarBuilder builder,Constructor constructor) {
		super(builder, constructor.getModifiers(), null, constructor.getName(), constructor.getParameterTypes(), constructor.getExceptionTypes());	
		this.constructor = constructor;
	}
	
	@Override
	protected void writeBody(IndentedPrintStream ips) {
		Class type = constructor.getDeclaringClass();
		type = type.getSuperclass();
		if(type!=null) {
			Constructor[] superConstructors = type.getDeclaredConstructors();
			Comparator<Constructor> constructorComparator = new Comparator<Constructor>() {

				@Override
				public int compare(Constructor o1, Constructor o2) {
					Class[] throws1 = o1.getExceptionTypes();
					Class[] throws2 = o2.getExceptionTypes();
					boolean compatibleThrows1 = isCompatibleThrows(throws1);
					boolean compatibleThrows2 = isCompatibleThrows(throws2);
					if( (compatibleThrows1) && (!compatibleThrows2))
						return -1;
					if( (!compatibleThrows1) && (compatibleThrows2))
						return 1;
					
					boolean public1 = Modifier.isPublic(o1.getModifiers());
					boolean public2 = Modifier.isPublic(o2.getModifiers());
					if( (public1) && (!public2))
						return -1;
					if( (!public1) && (public2))
						return 1;
					
					boolean protected1 = Modifier.isProtected(o1.getModifiers());
					boolean protected2 = Modifier.isProtected(o2.getModifiers());
					if( (protected1) && (!protected2))
						return -1;
					if( (!protected1) && (protected2))
						return 1;

					boolean private1 = Modifier.isPrivate(o1.getModifiers());
					boolean private2 = Modifier.isPrivate(o2.getModifiers());
					if( (private1) && (!private2))
						return 1;
					if( (!private1) && (private2))
						return -1;
					
					return o1.toString().compareTo(o2.toString());
				}

				private boolean isCompatibleThrows(Class[] throwTypes) {
					for(Class throwType : throwTypes) {
						if(!isCompatible(throwType))
							return false;
					}
					return true;
				}

				private boolean isCompatible(Class throwType) {
					for(Class t : ConstructorWriter.this.throwsTypes) {
						if(t.isAssignableFrom(throwType))
							return true;
					}
					return false;
				}
				
			};
			
			Arrays.sort(superConstructors, constructorComparator);
			ips.print("super(");
			Class[] paramTypes = superConstructors[0].getParameterTypes();
			for(int a = 0; a<paramTypes.length; a++) {
				if(a>0)
					ips.print(", ");;
				ips.print(getValue(paramTypes[a], true));
			}
			ips.println(");");
		}
	}
}