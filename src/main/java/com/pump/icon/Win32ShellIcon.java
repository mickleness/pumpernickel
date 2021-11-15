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
package com.pump.icon;

import java.awt.Image;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * This identifies icons accessible via the <code>sun.awt.shell.Win32ShellFolder2</code> class.
 * 
 * @see com.pump.icon.AquaIcon
 */
public class Win32ShellIcon {
	
	public enum Size {
		/**
		 * An icon that is 16x16 pixels
		 */
		WIDTH_16,
		/**
		 * An icon that is 32x32 pixels
		 */
		WIDTH_32
	}
	
	static Map<String, Win32ShellIcon> allIcons = new LinkedHashMap<>();

	private static boolean initialized = false;
	private static Class<?> win32ShellFolder2Class;
	private static Method getShell32IconMethod, getSystemIconMethod;

	private static void initialize() {
		if(initialized)
			return;
		initialized = true;
		try {
			win32ShellFolder2Class = Class.forName("sun.awt.shell.Win32ShellFolder2");

			getShell32IconMethod = Class.forName("sun.awt.shell.Win32ShellFolder2").getDeclaredMethod("getShell32Icon", Integer.TYPE, Boolean.TYPE);
			getShell32IconMethod.setAccessible(true);
			
			Field[] fields = win32ShellFolder2Class.getFields();
			for(Field field : fields) {
				if((field.getModifiers() & Modifier.STATIC) > 0 && field.getType().equals(Integer.TYPE)) {
					String name =field.getName();
					if(name.startsWith("ATTRIB_"))
						continue;
					if(name.startsWith("SHGDN_"))
						continue;
					
					// When I try to access the "DESKTOP" icon I get a null image?
					if(name.startsWith("DESKTOP"))
						continue;
					
					field.setAccessible(true);
					int id = (Integer) field.get(null);
					allIcons.put(name, new Win32ShellIcon(name,id));
				}
			}
			
			Class<?> systemIconClass = Class.forName("sun.awt.shell.Win32ShellFolder2$SystemIcon");
			for(Object systemIconEnum : systemIconClass.getEnumConstants()) {
				String name = systemIconEnum.toString();
				allIcons.put(name, new Win32ShellIcon(name,systemIconEnum));
			}

			getSystemIconMethod = Class.forName("sun.awt.shell.Win32ShellFolder2").getDeclaredMethod("getSystemIcon", systemIconClass);
			getSystemIconMethod.setAccessible(true);
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * Return all the IDs this runtime session has referred to, including all
	 * the static fields listed in this class.
	 */
	public static Collection<String> getIDs() {
		initialize();
		return Collections.unmodifiableSet(allIcons.keySet());
	}

	public static Win32ShellIcon get(String id) {
		return allIcons.get(id);
	}
	
	String name;
	
	//these are mutually exclusive: only one will be used.
	int id;
	Object enumObject;
	
	public Win32ShellIcon(String name,int id) {
		this.name = name;
		this.id = id;
	}

	public Win32ShellIcon(String name,Object enumObject) {
		this.name = name;
		this.enumObject = enumObject;
	}
	
	public String getName() {
		return name;
	}

	public Icon getIcon(Size size) {
		Objects.requireNonNull(size);
		
		try {
			if(enumObject!=null) {
				Image img = (Image) getSystemIconMethod.invoke(null, enumObject);
				if(img==null)
					return null;
				Icon returnValue = new ImageIcon(img);
				if(size==Size.WIDTH_16)
					returnValue = IconUtils.createScaledIcon(returnValue, 16, 16);
				return returnValue;
			}
			
			Image img = (Image) getShell32IconMethod.invoke(null, id, size==Size.WIDTH_32);
			if(img==null)
				return null;
			
			return new ImageIcon(img);
		} catch(Throwable t) {
			throw new RuntimeException(t);
		}
	}
}