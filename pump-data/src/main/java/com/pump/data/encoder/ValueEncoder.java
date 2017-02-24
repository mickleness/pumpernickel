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
package com.pump.data.encoder;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.pump.io.java.JavaEncoding;

/**
 * This converts an object to/from a serialized String representation of that object.
 * 
 * This is similar to the {@link java.text.Format} class.
 */
public abstract class ValueEncoder<T> {
	
	/** The word "null" */
	protected static final String NULL = "null";
	
	public static ValueEncoder<File> FILE = new ValueEncoder<File>(File.class) {

		@Override
		public File parse(String str) {
			if(NULL.equals(str) || str==null)
				return null;
			return new File(str);
		}

		@Override
		public String encode(File value) {
			if(value==null)
				return NULL;
			return value.getAbsolutePath();
		}
	};
	
	public static ValueEncoder<String> STRING = new ValueEncoder<String>(String.class) {

		@Override
		public String parse(String str) {
			if(NULL.equals(str) || str==null)
				return null;
			if(str.charAt(0)=='\"' && str.charAt(str.length()-1)=='\"') {
				return JavaEncoding.decode(str.substring(1, str.length()-1));
			}
			throw new IllegalArgumentException("This string was not recognized: "+str);
		}

		@Override
		public String encode(String value) {
			if(value==null)
				return NULL;
			StringBuilder sb = new StringBuilder();
			sb.append("\"");
			sb.append(JavaEncoding.encode(value));
			sb.append("\"");
			return sb.toString();
		}
	};
	
	public static ValueEncoder<Character> CHAR = new ValueEncoder<Character>(Character.class) {

		@Override
		public Character parse(String str) {
			if(NULL.equals(str) || str==null)
				return null;
			if(str.charAt(0)=='\'' && str.charAt(str.length()-1)=='\'') {
				String decoded = JavaEncoding.decode(str.substring(1, str.length()-1));
				if(decoded.length()==1) {
					return decoded.charAt(0);
				}
				throw new IllegalArgumentException("This decoded String was more than 1-character long");
			}
			throw new IllegalArgumentException("This character was not recognized: "+str);
		}

		@Override
		public String encode(Character value) {
			if(value==null)
				return NULL;
			StringBuilder sb = new StringBuilder();
			sb.append("\'");
			sb.append(JavaEncoding.encode(value.toString()));
			sb.append("\'");
			return sb.toString();
		}
		
	};
	
	protected abstract static class DirectEncoder<T> extends ValueEncoder<T> {

		public DirectEncoder(Class<T> type) {
			super(type);
		}

		@Override
		public T parse(String str) {
			if(NULL.equals(str) || str==null)
				return null;
			return parseNonNull(str);
		}
		
		protected abstract T parseNonNull(String str);

		@Override
		public String encode(T value) {
			if(value==null)
				return NULL;
			return value.toString();
		}
	}
	
	public static ValueEncoder<Integer> INT = new DirectEncoder<Integer>(Integer.class) {

		@Override
		public Integer parseNonNull(String str) {
			return Integer.parseInt(str);
		}
	};
	
	public static ValueEncoder<Short> SHORT = new DirectEncoder<Short>(Short.class) {

		@Override
		public Short parseNonNull(String str) {
			return Short.parseShort(str);
		}
	};
	
	public static ValueEncoder<Long> LONG = new DirectEncoder<Long>(Long.class) {

		@Override
		public Long parseNonNull(String str) {
			return Long.parseLong(str);
		}
	};
	
	public static ValueEncoder<Float> FLOAT = new DirectEncoder<Float>(Float.class) {

		@Override
		public Float parseNonNull(String str) {
			return Float.parseFloat(str);
		}
	};
	
	public static ValueEncoder<Double> DOUBLE = new DirectEncoder<Double>(Double.class) {

		@Override
		public Double parseNonNull(String str) {
			return Double.valueOf(str);
		}
	};
	
	public static ValueEncoder<Boolean> BOOLEAN = new DirectEncoder<Boolean>(Boolean.class) {

		@Override
		public Boolean parseNonNull(String str) {
			return Boolean.parseBoolean(str);
		}
	};
	
	public static ValueEncoder<Byte> BYTE = new DirectEncoder<Byte>(Byte.class) {

		@Override
		public Byte parseNonNull(String str) {
			return Byte.parseByte(str);
		}
	};
	
	public static ValueEncoder<Rectangle> RECTANGLE = new ValueEncoder<Rectangle>(Rectangle.class) {

		@Override
		public Rectangle parse(String str) {
			Number[] numbers = parseNumbers(str);
			return new java.awt.Rectangle(numbers[0].intValue(), numbers[1].intValue(), numbers[2].intValue(), numbers[3].intValue());
		}

		@Override
		public String encode(Rectangle value) {
			return value.x+" "+value.y+" "+value.width+" "+value.height;
		}
		
	};
	
	/** A convenience method to parse an array of Numbers
	 * 
	 * @param text a list of space-separated or comma-separated numbers.
	 * @return an array of Numbers reflecting the argument
	 */
	public static Number[] parseNumbers(java.lang.String text) {
		java.util.List<Number> list = new ArrayList<Number>();
		StringBuffer sb = new StringBuffer();
		boolean hex = false;
		for(int a = 0; a<text.length(); a++) {
			char ch = text.charAt(a);
			if(java.lang.Character.isWhitespace(ch) || ch==',') {
				if(sb.length()>0) {
					if(hex) {
						list.add( java.lang.Long.parseLong(sb.toString(), 16) );
					} else {
						list.add( java.lang.Double.parseDouble(sb.toString()) );
					}
					sb.delete(0, sb.length());
					hex = false;
				}
			} else {
				if(ch=='x' && sb.toString().equals("0")) {
					hex = true;
				} else {
					sb = sb.append(ch);
				}
			}
		}
		if(sb.length()>0) {
			if(hex) {
				list.add( java.lang.Long.parseLong(sb.toString(), 16) );
			} else {
				list.add( java.lang.Double.parseDouble(sb.toString()) );
			}
		}
		Number[] returnValue = new Number[list.size()];
		list.toArray(returnValue);
		return returnValue;
	}

	static Map<Class, ValueEncoder> defaultEncoders = new HashMap();
	
	static {
		defaultEncoders.put(File.class, ValueEncoder.FILE);
		defaultEncoders.put(String.class, ValueEncoder.STRING);
		defaultEncoders.put(Character.class, ValueEncoder.CHAR);
		defaultEncoders.put(Integer.class, ValueEncoder.INT);
		defaultEncoders.put(Short.class, ValueEncoder.SHORT);
		defaultEncoders.put(Long.class, ValueEncoder.LONG);
		defaultEncoders.put(Float.class, ValueEncoder.FLOAT);
		defaultEncoders.put(Double.class, ValueEncoder.DOUBLE);
		defaultEncoders.put(Boolean.class, ValueEncoder.BOOLEAN);
		defaultEncoders.put(Byte.class, ValueEncoder.BYTE);
		defaultEncoders.put(Rectangle.class, ValueEncoder.RECTANGLE);
	}
	
	public static <T> ValueEncoder<T> getDefaultEncoder(Class<T> type) {
		return defaultEncoders.get(type);
	}

	protected Class<T> type;
	
	public ValueEncoder(Class<T> type) {
		if(type==null)
			throw new NullPointerException();
		
		this.type = type;
	}
	
	public Class<T> getType() {
		return type;
	}
	
	public abstract T parse(String str);
	
	public abstract String encode(T value);
}