package com.pump.io;

import com.pump.io.parser.java.JavaClassSummary;
import com.pump.release.Project;
import com.pump.swing.TextEditorApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * TODO: find a place to store the resulting jar, comment here https://stackoverflow.com/questions/12678104/text-editor-with-syntax-highlighting-and-line-numbers/78987723#78987723
 */
public class JarBuilder implements AutoCloseable {

    public static void main(String[] args) throws Exception {
        try (JarBuilder builder = new JarBuilder()) {
            builder.addAll(new File("/Users/jeremy/IdeaProjects/pumpernickel_2025/ui-text-editor/target/classes"));
            builder.add("com/pump/util/JVM.class", new File("/Users/jeremy/IdeaProjects/pumpernickel_2025/common/target/classes/com/pump/util/JVM.class"));
            builder.add("com/pump/util/BasicConsumer.class", new File("/Users/jeremy/IdeaProjects/pumpernickel_2025/common/target/classes/com/pump/util/BasicConsumer.class"));
            builder.add("com/pump/io/parser/java/JavaEncoding.class", new File("/Users/jeremy/IdeaProjects/pumpernickel_2025/common/target/classes/com/pump/io/parser/java/JavaEncoding.class"));
            builder.addJavaSourceFiles(new File("/Users/jeremy/IdeaProjects/pumpernickel_2025/"));
            File jarFile = new File("pump-ui-text-editor.jar");
            builder.writeJar(jarFile, TextEditorApp.class);
            System.out.println("wrote: " + jarFile.getPath());
        }
    }

    private void addJavaSourceFiles(File dir) throws IOException {
        Iterator<File> iter = new FileTreeIterator(dir, "java");
        while (iter.hasNext()) {
            File javaFile = iter.next();
            JavaClassSummary classSummary = new JavaClassSummary(javaFile);
            String className = classSummary.getCanonicalName();
            String classFileEntryName = className.replace('.', '/') + ".class";
            if (jarContents.containsKey(classFileEntryName)) {
                jarContents.put(className.replace('.', '/') + ".java", javaFile);
            }
            System.currentTimeMillis();
        }

    }

    Map<String, File> jarContents = new LinkedHashMap<>();

    // TODO: remove?
//    final File dir;

    public JarBuilder() {
//        dir = createTempDir();
    }

    public void addAll(File dir) {
        for (File child : dir.listFiles()) {
            add(dir, child);
        }
    }

    private void add(File relativeRoot, File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                add(relativeRoot, child);
            }
        } else if (!file.isHidden()) {
            String path = file.getPath().substring(relativeRoot.getPath().length());
            if (path.startsWith(File.separator))
                path = path.substring(1);
            path = path.replace(File.separatorChar, '/');
            add(path, file);
        }
    }

    public void add(String jarEntry, File file) {
        if (jarContents.put(jarEntry, file) != null)
            throw new IllegalArgumentException("Multiple entries for entry \"" + jarEntry);
    }

    public void writeJar(File dest, Class mainClass) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(dest)) {
            try (JarOutputStream jarOut = new JarOutputStream(fileOut, createManifest(mainClass))) {
                for (Map.Entry<String, File> jarEntry : jarContents.entrySet()) {
                    JarEntry e = new JarEntry(jarEntry.getKey());
                    jarOut.putNextEntry(e);
                    try (FileInputStream fileIn = new FileInputStream(jarEntry.getValue())) {
                        IOUtils.write(fileIn, jarOut);
                    }
                }
            }
        }
    }

    private Manifest createManifest(Class mainClass) {
        Manifest manifest = new Manifest();
        Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.put(new Attributes.Name("Created-By"),
                "https://github.com/mickleness/pumpernickel/");
        if (mainClass != null) {
            attributes.put(Attributes.Name.MAIN_CLASS, mainClass.getName());
        }
        return manifest;
    }

    private File createTempDir() {
        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        Random r = new Random(System.currentTimeMillis());
        while (true) {
            File newDir = new File(tmpdir, r.nextLong() + "-dir");
            if (!newDir.exists() && newDir.mkdirs())
                return newDir;
        }
    }

    @Override
    public void close() throws Exception {
//        IOUtils.delete(dir);
    }
}
