package com.pump.xray;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import com.pump.io.NullOutputStream;

public class JarBuilder {
	
	Map<Class, Collection<Class>> classMap = new HashMap<>();
	
	Manifest manifest;
	
	public JarBuilder(Class... classes) {
		manifest = createManifest();
		addClasses(classes);
	}
	
	protected Manifest createManifest() {
		Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        return manifest;
	}
	
	public Manifest getManifest() {
		return manifest;
	}
	
	public void setManifest(Manifest manifest) {
		if(manifest==null)
			throw new NullPointerException();
		this.manifest = manifest;
	}
	
	public void addClasses(Class... classes) {
		for(Class t : classes) {
			if(t==null)
				throw new NullPointerException();
			catalogClass(t);
		}
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
	
	public void run(OutputStream out) throws IOException {
		int startingSize, endingSize;
		do {
			startingSize = classMap.size();
			write(new NullOutputStream());
			endingSize = classMap.size();
		} while(startingSize != endingSize);
		
		write(out);
	}
	
	protected void write(OutputStream out) throws IOException {
		try(JarOutputStream jarOut = new JarOutputStream(out, manifest)) {
			
		}
	}

	public boolean isSupported(Class t) {
        if (t.getPackage() == null)
            return false;
        if (t.getPackage().getName().startsWith("java"))
            return false;

        return true;
	}
}