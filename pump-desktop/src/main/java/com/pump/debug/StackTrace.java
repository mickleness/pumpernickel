package com.pump.debug;

import java.util.List;
import java.util.Map;

import com.pump.data.BeanMap;
import com.pump.data.Key;

/**
 * This is a simple representation of a complete stack trace.
 * <p>
 * This was originally created just to help us sort complex dumps of threads.
 */
public class StackTrace extends BeanMap {
	
	/**
	 * This is effectively an extension of a StackTraceElement. But that class is declared final,
	 * so this object stores a StackTraceElement plus several other attributes.
	 */
	static class Element extends BeanMap {
		public final static Key<StackTraceElement> KEY_ELEMENT = new Key<>(StackTraceElement.class, "element");
		public final static Key<Integer> KEY_BCI = new Key<>(Integer.class, "bci");
		public final static Key<String> KEY_FRAME = new Key<>(String.class, "frame");
		public final static Key<String> KEY_PARAMS = new Key<>(String.class, "params");
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			StackTraceElement e = get(KEY_ELEMENT);
			if(e!=null) {
				sb.append(e.getClassName().toString());
				sb.append("."+e.getMethodName());
				sb.append("("+get(KEY_PARAMS)+")");
				if(e.getLineNumber()>=0)
					sb.append(" line="+e.getLineNumber());
				if(e.getFileName()!=null)
					sb.append(" file="+e.getFileName());
			}
			
			int bci = get(KEY_BCI);
			String frame = get(KEY_FRAME);
			if(bci>=0)
				sb.append(" @bci="+bci);
			if(frame!=null)
				sb.append(" frame="+frame);
			
			return sb.toString();
		}
		
	}
	
	public final static Key<String> KEY_STATE = new Key<>(String.class, "state");
	public final static Key<String> KEY_NAME = new Key<>(String.class, "name");
	public final static Key<List<Element>> KEY_ELEMENTS = new Key(List.class, "elements");
	
	public StackTrace() {
		
	}

	public StackTrace(Map<String, Object> attributes) {
		super(attributes);
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof StackTrace))
			return false;
		StackTrace other = (StackTrace)obj;
		return toString().equals(other.toString());
	}

	@Override
	public String toString() {
		String name = get(KEY_NAME);
		String state = get(KEY_STATE);
		StringBuilder sb = new StringBuilder();
		if(name!=null && state!=null) {
			sb.append(name+": (state = "+state+")");
		} else if(name==null && state!=null) {
			sb.append("(state = "+state+")");
		} else if(name!=null && state==null) {
			sb.append(name);
		}
		
		List<Element> elements = get(KEY_ELEMENTS);
		if(elements!=null) {
			for(Element e : elements) {
				if(sb.length()>0) {
					sb.append("\n - "+e.toString());
				} else {
					sb.append(" - "+e.toString());
				}
			}
		}
		return sb.toString();
	}
	
}
