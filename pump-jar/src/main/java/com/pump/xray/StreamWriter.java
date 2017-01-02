package com.pump.xray;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Modifier;

import com.pump.io.IndentedPrintStream;
import com.pump.io.java.JavaEncoding;

public abstract class StreamWriter implements Comparable<StreamWriter> {

	/**
	 * Convert a constant representing modifiers to a String.
	 * 
	 * @param modifiers modifiers, such as <code>Modifier.ABSTRACT + Modifier.PUBLIC</code>
	 * @return a Java-formatted string, such as "public abstract"
	 */
	public static String toString(int modifiers) {
        StringBuilder sb = new StringBuilder();
        boolean isAbstract = Modifier.isAbstract(modifiers);
        boolean isFinal = Modifier.isFinal(modifiers);
        boolean isNative = Modifier.isNative(modifiers);
        boolean isPrivate = Modifier.isPrivate(modifiers);
        boolean isProtected = Modifier.isProtected(modifiers);
        boolean isPublic = Modifier.isPublic(modifiers);
        boolean isStatic = Modifier.isStatic(modifiers);
        boolean isSynchronized = Modifier.isSynchronized(modifiers);

        //TODO: some errors were observed with these, and I'm not sure they're necessary for mocked code
        boolean isStrict = false; //Modifier.isStrict(modifiers);
        boolean isVolatile = false; //Modifier.isVolatile(modifiers);
        boolean isTransient = false; //Modifier.isTransient(modifiers);

        if (isPublic) {
            sb.append("public ");
        }
        if (isPrivate) {
            sb.append("private ");
        }
        if (isAbstract) {
            sb.append("abstract ");
        }
        if (isFinal) {
            sb.append("final ");
        }
        if (isNative) {
            sb.append("native ");
        }
        if (isProtected) {
            sb.append("protected ");
        }
        if (isStatic)  {
            sb.append("static ");
        }
        if (isStrict)  {
            sb.append("strictfp ");
        }
        if (isSynchronized) {
            sb.append("synchronized ");
        }
        if (isTransient) {
            sb.append("transient ");
        }
        if (isVolatile) {
            sb.append("volatile ");
        }

        return sb.toString().trim();
	}

	protected JarBuilder builder;
	
	public StreamWriter(JarBuilder builder) {
		this.builder = builder;
	}

	protected String toString(Object value) {

		if(value instanceof String) {
			String str = (String)value;
			return "\""+JavaEncoding.encode(str)+"\"";
		} else if(value instanceof Character) {
			Character ch = (Character)value;
			return "\'"+JavaEncoding.encode(ch.toString())+"\'";
		} else if(value instanceof Float) {
			return value.toString()+"f";
		} else if(value instanceof Long) {
			return value.toString()+"L";
		}
		return null;
	}
	
	protected String toString(Class t,boolean catalog) {
        String name = null;

        if (catalog && builder!=null && builder.isSupported(t)) {
        	builder.addClasses(t);
        }

        if(t.isArray()) {
            return toString(t.getComponentType(), catalog)+"[]";
        }
        name = t.getName();

        return name.replace("$", ".");
	}

	@Override
	public int compareTo(StreamWriter other) {
		return toString().compareTo(other.toString());
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof StreamWriter) {
			return compareTo( (StreamWriter)other )==0;
		}
		return false;
	}
	
	@Override
	public String toString() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(IndentedPrintStream ips = new IndentedPrintStream(out, true, "UTF-8")) {
			write(ips, true);
			return new String(out.toByteArray(), "UTF-8");
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public abstract void write(IndentedPrintStream ips, boolean emptyFile) throws Exception;
}