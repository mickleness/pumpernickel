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

/**
 * This manages a catalog of classes. If you call {@link #addClasses(Class...)}
 * with exactly one Class object and then call {@link #build()}, this will reach
 * around and identify all the dependencies required to fully represent the
 * class in question. (And the dependencies of those dependencies, and so on.)
 * For example: you can't represent all the public method signatures of a
 * java.util.HashMap without a java.util.Map, a java.util.Iterator, a
 * java.util.Map$Entry, etc.
 * <p>
 * By passing a SourceCodeManager to all <code>StreamWriter</code> subclasses:
 * we give them a callback to constantly declare a need for more classes as they
 * write data.
 */
public class SourceCodeManager {

	/**
	 * A map of every class to the classes it contains. This is effectively a
	 * tree structure compressed into a map, where the parent is the key and the
	 * children are the values.
	 */
	protected Map<Class, Collection<Class>> classMap = new HashMap<>();

	/**
	 * Add one or more classes to this SourceCodeManager.
	 */
	public synchronized void addClasses(Class... classes) {
		for (Class t : classes) {
			if (t == null)
				throw new NullPointerException();
			catalogClass(t);
		}
	}

	/**
	 * This explores required classes and adds to the classes this manager
	 * identifies.
	 */
	public synchronized Map<Class, ClassWriter> build() throws Exception {
		int startingSize, endingSize;
		do {
			startingSize = classMap.size();
			for (Class t : classMap.keySet()
					.toArray(new Class[classMap.size()])) {
				ClassWriter w = new ClassWriter(this, t, true);
				w.write(new ClassWriterStream(new NullOutputStream(), true,
						"UTF-8"));
			}
			endingSize = classMap.size();
		} while (startingSize != endingSize);

		Map<Class, Collection<Class>> allClasses = getClassMap();
		Map<Class, ClassWriter> writers = new HashMap<>();
		for (Entry<Class, Collection<Class>> entry : allClasses.entrySet()) {
			if (entry.getKey().getDeclaringClass() == null) {
				ClassWriter writer = new ClassWriter(entry.getKey(), true);
				populateInnerClasses(writer, allClasses);
				writers.put(writer.getType(), writer);
			}
		}
		return writers;
	}

	/**
	 * Given a parent class's ClassWriter this populates all the necessary inner
	 * classes (if any).
	 * 
	 * @param writer
	 *            the parent ClassWriter to possibly add declared/nested classes
	 *            to.
	 * @param allClasses
	 *            a map of all classes, where the key is the parent and the
	 *            value is a collection of its children.
	 */
	protected void populateInnerClasses(ClassWriter writer,
			Map<Class, Collection<Class>> allClasses) {
		Class z = writer.getType();
		Collection<Class> declaredClasses = allClasses.get(z);
		if (declaredClasses != null) {
			for (Class declaredClass : declaredClasses) {
				ClassWriter declaredWriter = new ClassWriter(declaredClass,
						true);
				writer.addDeclaredClass(declaredWriter);
				populateInnerClasses(declaredWriter, allClasses);
			}
		}
	}

	/**
	 * Return a map of every class to the classes it contains. This is
	 * effectively a tree structure compressed into a map, where the parent is
	 * the key and the children are the values.
	 */
	public Map<Class, Collection<Class>> getClassMap() {
		return new HashMap<>(classMap);
	}

	/**
	 * Catalog a Class as being required to emulate/represent in the x-ray'd
	 * jar.
	 */
	protected void catalogClass(Class z) {
		if (!classMap.containsKey(z)) {
			classMap.put(z, new HashSet<Class>());
			Class child = z;
			Class parent = child.getDeclaringClass();
			while (parent != null) {
				Collection<Class> t = classMap.get(parent);
				if (t == null) {
					t = new HashSet<>();
					classMap.put(parent, t);
				}
				t.add(child);
				child = parent;
				parent = parent.getDeclaringClass();
			}
		}
	}

	/**
	 * Return true if the argument should be supported/represented in the
	 * x-ray'd jar.
	 * <p>
	 * Subclasses may override this to impose their own rules about what is and
	 * isn't appropriate to inlude.
	 * 
	 * @param t
	 *            the class to evaluate.
	 * @return true if the argument should be included in the x-ray'd jar.
	 */
	public boolean isSupported(Class t) {
		if (t.getPackage() == null)
			return false;
		if (t.getPackage().getName().startsWith("java"))
			return false;

		return true;
	}

	/**
	 * Writer a series of ClassWriters to a destination directory.
	 * 
	 * @param destDir
	 *            the directory to write to.
	 * @param writers
	 *            the ClassWriters to write into this directory.
	 */
	public void write(File destDir, Collection<ClassWriter> writers)
			throws IOException {
		if (!destDir.exists()) {
			if (!destDir.mkdirs())
				throw new IOException("File.mkdirs failed for "
						+ destDir.getAbsolutePath());
		} else if (!destDir.isDirectory()) {
			throw new IOException("This destination is not a directory: "
					+ destDir.getAbsolutePath());
		}
		for (ClassWriter writer : writers) {
			String classname = writer.getType().getName();
			File dest = new File(destDir.getAbsolutePath() + File.separator
					+ classname.replace(".", File.separator) + ".java");
			IOUtils.write(dest, writer.toString(), false);
		}
	}
}