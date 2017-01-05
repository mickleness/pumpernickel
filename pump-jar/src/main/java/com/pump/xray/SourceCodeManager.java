package com.pump.xray;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.pump.io.IOUtils;
import com.pump.io.NullOutputStream;

public class SourceCodeManager {

	/**
	 * A map of every class to the classes it contains.
	 * This is effectively a tree structure compressed into a map,
	 * where the parent is the key and the children are the values.
	 */
	protected Map<Class, Collection<Class>> classMap = new HashMap<>();

	public void addClasses(Class... classes) {
		for(Class t : classes) {
			if(t==null)
				throw new NullPointerException();
			catalogClass(t);
		}
	}
	
	/**
	 * This explores required classes and adds to the classes this manager identifies.
	 */
	public Map<Class, ClassWriter> build() throws Exception {
		int startingSize, endingSize;
		do {
			startingSize = classMap.size();
			for(Class t : classMap.keySet().toArray(new Class[classMap.size()])) {
				ClassWriter w = new ClassWriter(this, t, true);
				w.write(new ClassWriterStream(new NullOutputStream(), true, "UTF-8"), true);
			}
			endingSize = classMap.size();
		} while(startingSize != endingSize);
		

		Map<Class, Collection<Class>> allClasses = getClassMap();
		Map<Class, ClassWriter> writers = new HashMap<>();
		for(Entry<Class, Collection<Class>> entry : allClasses.entrySet()) {
			if(entry.getKey().getDeclaringClass()==null) {
				ClassWriter writer = new ClassWriter(entry.getKey(), true);
				populateInnerClasses(writer, allClasses);
				writers.put(writer.getType(), writer);
			}
		}
		return writers;
	}
	
	protected void populateInnerClasses(ClassWriter writer,Map<Class, Collection<Class>> allClasses) {
		Class z = writer.getType();
		Collection<Class> declaredClasses = allClasses.get(z);
		if(declaredClasses!=null) {
			for(Class declaredClass : declaredClasses) {
				ClassWriter declaredWriter = new ClassWriter(declaredClass, true);
				writer.addDeclaredClass(declaredWriter);
				populateInnerClasses(declaredWriter, allClasses);
			}
		}
	}
	
	/**
	 * Return a map of every class to the classes it contains.
	 * This is effectively a tree structure compressed into a map,
	 * where the parent is the key and the children are the values.
	 */
	public Map<Class, Collection<Class>> getClassMap() {
		return new HashMap<>( classMap );
	}
	
	protected void catalogClass(Class z) {
        if(!classMap.containsKey(z)) {
            classMap.put(z, new HashSet<Class>());
            Class child = z;
            Class parent = child.getDeclaringClass();
            while(parent!=null) {
                Collection<Class> t = classMap.get(parent);
                if(t==null) {
                    t = new HashSet<>();
                    classMap.put(parent, t);
                }
                t.add(child);
                child = parent;
                parent = parent.getDeclaringClass();
            }
        }
	}

	public boolean isSupported(Class t) {
        if (t.getPackage() == null)
            return false;
        if (t.getPackage().getName().startsWith("java"))
            return false;

        return true;
	}
	
	public void write(File destDir,Collection<ClassWriter> writers) throws IOException {
		for(ClassWriter writer : writers) {
			String classname = writer.getType().getName();
			File dest = new File(destDir.getAbsolutePath() + File.separator + classname.replace(".", File.separator)+".java");
			IOUtils.write(dest, writer.toString(), false);
		}
	}
}
