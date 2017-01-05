package com.pump.xray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;
import com.pump.io.java.JavaClassSummary;

public class JarBuilder {
	
	Manifest manifest;
	SourceCodeManager sourceCodeManager;
	
	public JarBuilder(SourceCodeManager sourceCodeManager) {
		manifest = createManifest();
		this.sourceCodeManager = sourceCodeManager;
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
	
	protected void write(OutputStream out) throws Exception {

		class MyJavaFileObject extends SimpleJavaFileObject {
			File file;
			
			protected MyJavaFileObject(File file, Kind kind) {
				super(file.toURI(), kind);
				this.file = file;
			}

		    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
		    	return IOUtils.read(file);
		    }

			@Override
			public OutputStream openOutputStream() throws IOException {
				if(!file.exists())
					file.createNewFile();
				return new FileOutputStream(file);
			}
		}
		
		File dir = new File(System.getProperty("java.io.tmpdir"));
		final File tmpDir = IOUtils.getUniqueFile(dir, "srcpath", false, true);
		if(!tmpDir.mkdir())
			throw new IOException("File.mkdir failed for "+tmpDir.getAbsolutePath());
		try {
			Map<Class, ClassWriter> classWriterMap = sourceCodeManager.build();
			sourceCodeManager.write(tmpDir, classWriterMap.values());

			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
	        if (compiler == null) {
	            throw new RuntimeException("Compiler not found.");
	        }
	        
	        StandardJavaFileManager defaultManager = compiler.getStandardFileManager(null, null, null);

	        final Map<String, MyJavaFileObject> srcMap = new HashMap<>();
	        final Map<String, MyJavaFileObject> classMap = new HashMap<>();
	        FileTreeIterator iter = new FileTreeIterator(tmpDir, "java");
	        while(iter.hasNext()) {
	        	File f = iter.next();
	        	String className = JavaClassSummary.getClassName(f);
	        	srcMap.put(className, new MyJavaFileObject(f, Kind.SOURCE));
	        }

	        JavaFileManager manager = new ForwardingJavaFileManager<StandardJavaFileManager>(defaultManager) {
	            @Override
	            public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
    				if(kind==Kind.SOURCE) {
    					if(className.contains("$"))
        					className = className.substring(0, className.indexOf("$"));
    	               	return srcMap.get( className);
                	} else if(kind==Kind.CLASS) {
                		if(!classMap.containsKey(className)) {
                    		File classFile = new File(tmpDir.getAbsolutePath() + File.separator + className.replace(".", File.separator)+".class");
                    		classMap.put(className, new MyJavaFileObject(classFile, Kind.CLASS));
                		}
        	           	return classMap.get( className );
                	}
            		return null;
	            }
	        };
	        
	        // javac options
	        List<String> options = new ArrayList<String>();
	        options.add("-deprecation");
	        options.add("-nowarn");

	        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
	        
	        // create a compilation task
	        try(Writer err = new StringWriter()) {
	        	javax.tools.JavaCompiler.CompilationTask task = compiler.getTask(err, manager, diagnostics, options, null, srcMap.values());
	        	System.out.println(err.toString());
	        
			    if (!task.call()) {
			        StringBuilder errors = new StringBuilder();
			        for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
			            if(diagnostic.getCode().startsWith("compiler.err")) {
			                errors.append("Line " + diagnostic.getLineNumber() + ": [" + diagnostic.getCode() + "] " + diagnostic.getMessage(null) + "\n");
			            }
			        }
			        System.err.println(errors.toString());
			    }

			    iter = new FileTreeIterator(tmpDir, "java", "class");
			    try(JarOutputStream jarOut = new JarOutputStream(out, manifest)) {
					while(iter.hasNext()) {
			        	File f = iter.next();
			        	String name = f.getAbsolutePath().substring(tmpDir.getAbsolutePath().length()+1);
						jarOut.putNextEntry(new JarEntry(name));
						try(FileInputStream fileIn = new FileInputStream(f)) {
							IOUtils.write(fileIn, jarOut);
						}
			        }
				}
	        }
			
		} finally {
			IOUtils.delete(tmpDir);
		}
	}
}