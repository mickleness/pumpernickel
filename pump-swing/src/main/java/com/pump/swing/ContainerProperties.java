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
package com.pump.swing;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import com.pump.io.HTMLEncoding;

/** This helps serialize and deserialize simple UIs based on the names of components.
 * For example, if you have a selected JCheckBox with the name "is-preloaded", then after
 * calling {@link #catalog(Container)} on any parent of that JCheckBox this ContainerProperties
 * will include "is-preloaded=true". Similarly when you call {@link #install(Container, UncaughtExceptionHandler)}
 * it will parse that value and set the UI accordingly.
 * <p>This only supports a small set of data types in JToggleButtons, JTextComponents, JComboBoxes and JLists:
 * primitives, Strings, BigDecimals, BigIntegers, and Files. It's possible this object
 * can serialize values but it won't be able to deserialize them later.
 * <p>This is a quick and light tool intended to help with settings, but it is not
 * a good solution for complex (or crucial) saved data.
 *
 */
public class ContainerProperties extends Properties {
	private static final long serialVersionUID = 1L;
	
	/** If you would prefer not to use Component's name property, then you can
	 * use this client property for Swing components instead.
	 */
	public static final String KEY_PROPERTY_NAME = ContainerProperties.class+"#property-name";
	
	private static final String TYPE_SUFFIX = "*component-type";

	/** Create an empty ContainerProperties.
	 * 
	 */
	public ContainerProperties() {
		
	}
	
	/** Create a ContainerProperties that catalogs all descendants of the argument.
	 * 
	 * @param container a Container with textfields, buttons, comboboxes, etc. that you
	 * want to catalog with this ContainerProperties.
	 */
	public ContainerProperties(Container container) {
		this();
		catalog(container);
	}
	
	/** Catalog all descendants of the argument.
	 * 
	 * @param container a Container with textfields, buttons, comboboxes, etc. that you
	 * want to catalog with this ContainerProperties.
	 */
	public void catalog(Container container) {
		scanChildren : for(int a = 0; a<container.getComponentCount(); a++) {
			Component comp = container.getComponent(a);
			String name = getName(comp);
			if(name!=null) {
				if(comp instanceof JTextComponent) {
					JTextComponent jtc = (JTextComponent)comp;
					put(name, jtc.getText());
					continue scanChildren;
				} else if(comp instanceof JToggleButton) {
					boolean b = ((JToggleButton)comp).isSelected();
					put(name, Boolean.toString(b) );
					continue scanChildren;
				} else if(comp instanceof JComboBox<?>) {
					JComboBox<?> jcb = (JComboBox<?>)comp;
					Object s = jcb.getModel().getSelectedItem();
					if(s==null) {
						put(name, "null");
						remove(name+TYPE_SUFFIX);
					} else {
						put(name, toString(s));
						put(name+TYPE_SUFFIX, s.getClass().getName());
					}
					continue scanChildren;
				} else if(comp instanceof JList<?>) {
					JList<?> list = (JList<?>)comp;
					List<?> s = list.getSelectedValuesList();
					StringBuffer sb = new StringBuffer();
					for(Object e : s) {
						if(sb.length()>0)
							sb.append(", ");
						sb.append("\"");
						sb.append(HTMLEncoding.encode(toString(e)));
						sb.append("\"");
					}
					put(name, sb.toString());
					if(s.size()>0)
						put(name+TYPE_SUFFIX, s.get(0).getClass().getName());
					continue scanChildren;
				} else {
					System.err.println("unsupported component: "+comp.getClass().getName());
				}
			}
			if(comp instanceof Container)
				catalog( (Container)comp );
		}
	}
	
	/** Convert an object to a String.
	 * 
	 * @param obj the object to convert.
	 * @return the serialized String representation of the object.
	 */
	protected String toString(Object obj) {
		if(obj instanceof File) {
			return ((File)obj).getAbsolutePath();
		} else if(obj==null) {
			return null;
		}
		return obj.toString();
	}

	/** Convert a String representation to an Object
	 * 
	 * @param toStringValue the serialized String value
	 * @param componentType the type of object that is returned.
	 * @param handler a handler to notify if something goes wrong
	 * @return the Object represented by the argument String.
	 */
	protected Object convert(String toStringValue,Class<?> componentType, UncaughtExceptionHandler handler) {
		if(String.class.equals(componentType))
			return toStringValue;
		if(Boolean.class.equals(componentType) || Boolean.TYPE.equals(componentType))
			return Boolean.parseBoolean(toStringValue);
		if(Integer.class.equals(componentType) || Integer.TYPE.equals(componentType))
			return Integer.parseInt(toStringValue);
		if(Short.class.equals(componentType) || Short.TYPE.equals(componentType))
			return Short.parseShort(toStringValue);
		if(Long.class.equals(componentType) || Long.TYPE.equals(componentType))
			return Long.parseLong(toStringValue);
		if(Character.class.equals(componentType) || Character.TYPE.equals(componentType))
			return toStringValue.charAt(0);
		if(Byte.class.equals(componentType) || Byte.TYPE.equals(componentType))
			return Byte.parseByte(toStringValue);
		if(Float.class.equals(componentType) || Float.TYPE.equals(componentType))
			return Float.parseFloat(toStringValue);
		if(Double.class.equals(componentType) || Double.TYPE.equals(componentType))
			return Double.parseDouble(toStringValue);
		if(BigInteger.class.equals(componentType))
			return new BigInteger(toStringValue);
		if(BigDecimal.class.equals(componentType))
			return new BigDecimal(toStringValue);
		if(File.class.equals(componentType))
			return new File(toStringValue);
		handler.uncaughtException(Thread.currentThread(), new UnsupportedEncodingException("ContainerProperties cannot parse "+componentType.getCanonicalName()));
		return null;
	}
	
	private static UncaughtExceptionHandler DEFAULT_HANDLER = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			StringBuffer msg = new StringBuffer();
			Throwable k = e;
			while(k!=null) {
				msg.append( e.getClass().getCanonicalName() );
				if(e.getMessage()!=null)
					msg.append(e.getMessage()+"\n");
				for(StackTraceElement t2 : e.getStackTrace()) {
					msg.append("\t"+t2.toString()+"\n");
				}
				k = k.getCause();
				if(k!=null) 
					msg.append("\n... caused by:\n");
			}
			System.err.println("An error occurred installing this ContainerProperties:");
			System.err.println(msg.toString().trim());
		}
	};
	
	/** Install these properties on a Container's descendants.
	 * 
	 * @param container Configure all the components in the argument that match properties stored in this ContainerProperties.
	 * @param handler this will be notified as exceptions come up. If this is null, then a default exception handler is used
	 * that quietly prints messages to System.err but continues to iterate over all possible components.
	 */
	public void install(Container container,UncaughtExceptionHandler handler) {
		if(handler==null) {
			handler = DEFAULT_HANDLER;
		}
		
		scanChildren : for(int a = 0; a<container.getComponentCount(); a++) {
			Component comp = container.getComponent(a);
			String name = getName(comp);
			if(name!=null) {
				String value = getProperty(name);
				
				Class<?> componentType = null;
				String typeKey = name+TYPE_SUFFIX;
				String typeValue = getProperty(typeKey);
				if(typeValue!=null) {
					try {
						componentType = Class.forName(typeValue);
					} catch(ClassNotFoundException e) {
						handler.uncaughtException(Thread.currentThread(), e);
						continue scanChildren;
					}
				}
				
				if(name!=null && value!=null) {
					if(comp instanceof JTextComponent) {
						JTextComponent jtc = (JTextComponent)comp;
						jtc.setText(value);
						continue scanChildren;
					} else if(comp instanceof JToggleButton) {
						((JToggleButton)comp).setSelected( Boolean.parseBoolean(value) );
						continue scanChildren;
					} else if(comp instanceof JComboBox<?>) {
						JComboBox<?> jcb = (JComboBox<?>)comp;
						if(componentType==null && "null".equals(value)) {
							jcb.setSelectedIndex(-1);
						} else {
							Object v = convert(value, componentType, handler);
							if(v!=null)
								jcb.setSelectedItem( v );
						}
						continue scanChildren;
					} else if(comp instanceof JList<?>) {
						JList<?> list = (JList<?>)comp;
						List<Integer> selectionIndices = new ArrayList<>();
						if(value.length()!=0) {
							int i = 0;
							while(i<value.length()) {
								int i1 = value.indexOf('"', i);
								int i2 = value.indexOf('"', i1+1);
								i = i2+1;
								
								String el = value.substring(i1+1, i2);
								el = HTMLEncoding.decode(el);
								Object v = convert(el, componentType, handler);
								int j = getIndexOf(list, v);
								if(j==-1) {
									handler.uncaughtException( Thread.currentThread(), new IllegalStateException("the list \""+name+"\" doesn't appear to contain the value \""+v+"\""));
								} else {
									selectionIndices.add(j);
								}
							}
						}
						int[] array = new int[selectionIndices.size()];
						for(int j = 0; j<array.length; j++) {
							array[j] = selectionIndices.get(j);
						}
						list.setSelectedIndices(array);

						continue scanChildren;
					}
				}
			}
			if(comp instanceof Container)
				install( (Container)comp, handler );
		}
	}

	private int getIndexOf(JList<?> list, Object v) {
		for(int j = 0; j<list.getModel().getSize(); j++) {
			if(v.equals(list.getModel().getElementAt(j)) ) {
				return j;
			}
		}
		return -1;
	}

	protected String getName(Component comp) {
		String name = null;
		if(comp instanceof JComponent)
			name = (String)((JComponent)comp).getClientProperty(KEY_PROPERTY_NAME);
		if(name==null)
			name = comp.getName();
		return name;
	}
}