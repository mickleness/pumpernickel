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
	
	Manifest manifest;
	
	public JarBuilder() {
		manifest = createManifest();
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
	
	protected void write(OutputStream out) throws IOException {
		try(JarOutputStream jarOut = new JarOutputStream(out, manifest)) {
			
		}
	}
}