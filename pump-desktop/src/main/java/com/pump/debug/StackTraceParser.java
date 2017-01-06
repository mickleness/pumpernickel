package com.pump.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.pump.debug.StackTrace.Element;

public class StackTraceParser {
	
	private static class StackTraceParserIterator implements Iterator<StackTrace> {
		
		BufferedReader br;
		StackTrace next;

		public StackTraceParserIterator(Reader reader) throws IOException {
			br = new BufferedReader(reader);
			queueNext();
		}
		
		private void queueNext() throws IOException {
			next = null;
			
			String s = br.readLine();
			while(s!=null && s.trim().length()==0) {
				s = br.readLine();
			}
			
			if(s==null)
				return;
			
			int i1 = s.lastIndexOf("(");
			int i2 = s.indexOf(")", i1);
			String stateClause = s.substring(i1+1, i2).trim();
			String str = stateClause;
			if(str.startsWith("state")) {
				str = str.substring(5).trim();
			} else {
				throw new IOException("The clause \""+stateClause+"\" was expected to read: \"state = x\"");
			}
			if(str.startsWith("=")) {
				str = str.substring(1).trim();
			} else {
				throw new IOException("The clause \""+stateClause+"\" was expected to read: \"state = x\"");
			}
			String state = str;
			
			String name;
			str = s.substring(0, i1).trim();
			if(str.endsWith(":")) {
				name = str.substring(0, str.length()-1);
			} else {
				throw new IOException("The top of this stack trace was expected to read \"thread id: (state = x)\"");
			}
			
			List<Element> elements = new ArrayList<>();
			s = br.readLine();
			while(s!=null && s.length()>0) {
				s = s.trim();
				elements.add(parseElement(s));
				s = br.readLine();
			}
			
			
			next = new StackTrace();
			next.set(StackTrace.KEY_NAME, name);
			next.set(StackTrace.KEY_STATE, state);
			next.set(StackTrace.KEY_ELEMENTS, elements);
		}

		private Element parseElement(String s) throws IOException {
			Element e = new Element();
			if(s.startsWith("-")) {
				s = s.substring(1).trim();
			} else {
				throw new IOException("The stack trace element \""+s+"\" was expected to start with a hyphen.");
			}
			
			int i1 = s.indexOf("(");
			int i2 = s.indexOf(")", i1);
			if(i1==-1)
				throw new IOException("The stack trace element \""+s+"\" did not contain parantheses.");
			
			String params = s.substring(i1+1, i2);
			String rhs = s.substring(i2+1).trim();
			String lhs = s.substring(0, i1);
			int i3 = lhs.lastIndexOf(".");
			String className = lhs.substring(0,i3);
			String methodName = lhs.substring(i3+1);
			
			Integer lineNumber = parseInt(rhs, "line=");
			Integer bci = parseInt(rhs, "@bci=");
			
			int i4 = rhs.lastIndexOf("(");
			int i5 = rhs.indexOf(")", i4+1);
			String frame = rhs.substring(i4+1, i5);
			
			StackTraceElement el = new StackTraceElement(className, methodName, null, lineNumber==null ? -1 : lineNumber);
			
			e.set(Element.KEY_ELEMENT, el);
			e.set(Element.KEY_BCI, bci);
			e.set(Element.KEY_FRAME, frame);
			e.set(Element.KEY_PARAMS, params);
			
			return e;
		}
		
		private Integer parseInt(String s,String prefix) {
			int i4 = s.indexOf(prefix);
			StringBuilder sb = new StringBuilder();
			for(int z = i4 + prefix.length(); z<s.length(); z++) {
				if(Character.isDigit(s.charAt(z))) {
					sb.append(s.charAt(z));
				} else {
					break;
				}
			}
			if(sb.length()==0)
				return null;
			return Integer.parseInt(sb.toString());
		}

		@Override
		public boolean hasNext() {
			return next!=null;
		}

		@Override
		public StackTrace next() {
			StackTrace returnValue = next;
			try {
				queueNext();
			} catch(IOException e) {
				throw new RuntimeException(e);
			}
			return returnValue;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	public Iterator<StackTrace> parse(Reader reader) throws IOException {
		return new StackTraceParserIterator(reader);
	}
}