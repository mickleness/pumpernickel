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
package com.pump.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pump.util.TreeIterator;

/** This iterates through a file tree structure.
 */
public class FileTreeIterator extends TreeIterator<File> {
	
	/** This finds the first file in the directory that matches
	 * the name provided.
	 * @param dir
	 * @param fileName
	 * @return a file nested inside dir that matches the file name provided,
	 * or null if no such file is found.
	 */
	public static File find(File dir,String fileName) {
		if(dir==null) throw new NullPointerException();
		
		FileTreeIterator iter = new FileTreeIterator(dir);
		while(iter.hasNext()) {
			File f = iter.next();
			if(f.getName().equals(fileName))
				return f;
		}
		return null;
	}
	
	/** This finds all files in the directory that match
	 * the exact name provided.
	 * @param dir the directories to search in. Null elements
	 * in this array are skipped, but if the array itself is null then
	 * a <code>NullPointerException</code> is thrown.
	 * @param fileName the exact file name to search for.
	 * @return files nested inside dir that satisfy this search.
	 * This may return an empty array but will not return null.
	 */
	public static File[] findAll(File[] dir,String fileName) {
		if(dir==null) throw new NullPointerException();
		if(fileName==null) throw new NullPointerException();
		
		List<File> results = new ArrayList<File>();
		for(File d : dir) {
			if(d!=null) {
				FileTreeIterator iter = new FileTreeIterator(d);
				while(iter.hasNext()) {
					File f = iter.next();
					if(f.getName().equals(fileName)) {
						results.add(f);
					}
				}
			}
		}
		return results.toArray(new File[results.size()]);
	}
	
	/** This finds all files in the directory that match
	 * the name provided.
	 * @param dir the directories to search in. Null elements
	 * in this array are skipped, but if the array itself is null then
	 * a <code>NullPointerException</code> is thrown.
	 * @param fileBaseName the name of the file without the file extension
	 * @param fileExtensions the file extensions search check against.
	 * @return files nested inside dir that satisfy this search.
	 * This may return an empty array but will not return null.
	 */
	public static File[] findAll(File[] dir,String fileBaseName,String[] fileExtensions) {
		if(dir==null) throw new NullPointerException();
		
		List<File> results = new ArrayList<File>();
		for(File d : dir) {
			if(d!=null) {
				FileTreeIterator iter = new FileTreeIterator(d, fileExtensions);
				while(iter.hasNext()) {
					File f = iter.next();
					String name = f.getName();
					int i = name.lastIndexOf('.');
					if(i!=-1) {
						name = name.substring(0, i);
					}
					if(name.equals(fileBaseName)) {
						results.add(f);
					}
				}
			}
		}
		return results.toArray(new File[results.size()]);
	}
	
	/** This finds all files in the directory that match
	 * the name and filter provided.
	 * @param dir the directories to search in. Null elements
	 * in this array are skipped, but if the array itself is null then
	 * a <code>NullPointerException</code> is thrown.
	 * @param fileBaseName the name of the file without the file extension
	 * @param fileFilter the optional FileFilter
	 * @return files nested inside dir that satisfy this search.
	 * This may return an empty array but will not return null.
	 */
	public static File[] findAll(File[] dir,String fileBaseName,FileFilter fileFilter) {
		if(dir==null) throw new NullPointerException();
		
		List<File> results = new ArrayList<File>();
		for(File d : dir) {
			if(d!=null) {
				FileTreeIterator iter = new FileTreeIterator(d, fileFilter);
				while(iter.hasNext()) {
					File f = iter.next();
					String name = f.getName();
					int i = name.lastIndexOf('.');
					if(i!=-1) {
						name = name.substring(0, i);
					}
					if(name.equals(fileBaseName)) {
						results.add(f);
					}
				}
			}
		}
		return results.toArray(new File[results.size()]);
	}
	
	public final FileFilter fileFilter;
	
	/** Creates a new <code>FileTreeIterator</code>
	 * 
	 * @param parent the root file to begin searching in.
	 * For best results this should be parent.getCanonicalFile(). Otherwise
	 * case sensitivity might accidentally result in some files being flagged
	 * as aliases. (That is: if the user inputs a file path in a case insensitive
	 * manner, and then we call isAlias(myFile), it may return a false positive.)
	 * @param filter the filter determining what files to list.
	 */
	public FileTreeIterator(File parent,FileFilter filter) {
		super(parent, true);
		this.fileFilter = filter;
	}

	/** Creates a new <code>FileTreeIterator</code>.
	 * 
	 * @param parent the root file to begin searching in.
	 * For best results this should be parent.getCanonicalFile(). Otherwise
	 * case sensitivity might accidentally result in some files being flagged
	 * as aliases. (That is: if the user inputs a file path in a case insensitive
	 * manner, and then we call isAlias(myFile), it may return a false positive.)
	 * @param extensions the file name extensions to search for.
	 */
	public FileTreeIterator(File parent,String... extensions) {
		this(parent,new SuffixFilenameFilter(extensions));
	}

	/** Creates a new <code>FileTreeIterator</code> that
	 * iterates over every non-directory file in this tree.
	 * 
	 * @param parent the root file to begin searching in.
	 */
	public FileTreeIterator(File parent) {
		this(parent,(FileFilter)null);
	}
	
	
	@Override
	protected File[] listChildren(File parent) {
		File[] f = parent==null ? null : parent.listFiles();
		if(f==null) return null;
		
		for(int a = 0; a<f.length; a++) {
			if(isAlias(f[a]))
				f[a] = null;
		}
		return f;
	}

	@Override
	protected boolean isReturnValue(File node) {
		if(fileFilter==null) return true;
		return fileFilter.accept(node);
	}

	/** This checks to see if a <code>File</code> is
	 * a symlink/alias.  Unfortunately this is not
	 * officially supported in the <code>File</code> method
	 * signatures, so this uses a potentially awkward approach.
	 */
	public static boolean isAlias(File file)
	{
		try {
			if (!file.exists())
				return false;
			else
			{
				String cnnpath = file.getCanonicalPath();
				String abspath = file.getAbsolutePath();
				
				if(cnnpath.startsWith("/private") && (!abspath.startsWith("/private"))) {
					cnnpath = cnnpath.substring("/private".length());
				}
				
				//this is just a verbose path, but it's probably not an alias:
				if(abspath.endsWith(cnnpath) && abspath.startsWith("/Volumes/"))
					return false;
				
				boolean returnValue = !abspath.equals(cnnpath);
				return returnValue;
			}
		}
		catch(IOException ex) {
			return false;
		}
	}
}