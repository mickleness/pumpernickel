/*
 * @(#)EclipseClasspathFile.java
 *
 * $Date$
 *
 * Copyright (c) 2015 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.jar;

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pump.swing.DialogFooter;
import com.pump.swing.FileDialogUtils;
import com.pump.swing.QDialog;
import com.pump.window.WindowList;

/** A simple parser for Eclipse's .classpath XML file.
 */
public class EclipseClasspathFile {
	public final List<File> srcpaths = new ArrayList<File>();
	public final List<File> libs = new ArrayList<File>();
	public static Preferences prefs = Preferences.userNodeForPackage(EclipseClasspathFile.class);
	
	/** Create a new EclipseClasspathFile.
	 * 
	 * @param xmlFile the .classpath file to parse.
	 * @param output if true then updates will be printed to System.out as parsing progressing.
	 */
	public EclipseClasspathFile(File xmlFile,boolean output) throws IOException, ParserConfigurationException, SAXException {
		if(output)
			System.out.println("Parsing "+xmlFile.getAbsolutePath()+":");
		Element root = parseXML(xmlFile);

		NodeList nl = root.getChildNodes();
		for (int i=0; nl!=null && i<nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n instanceof Element) {
				Element el = (Element)nl.item(i);
				if("classpathentry".equals(el.getTagName())) {
					String k = el.getAttribute("kind");
					String p = el.getAttribute("path");
					if("src".equals(k)) {
						String filepath = prefs.get(p, null);
						File f = filepath==null ? null : new File(filepath);
						if(f!=null && !f.exists())
							f = null;
						if(f==null) {
							try {
								f = identifyFile(xmlFile, p);
							} catch(MissingFileException e) {
								Frame[] frames = WindowList.getFrames(true, false, false);
								QDialog.showDialog(frames[0], "Missing \""+p+"\"", QDialog.ERROR_MESSAGE, 
										"The file \""+p+"\" wasn't found.", 
										"Please navigate to this file, or a child of it.", 
										null, //innerComponent, 
										null, //lowerLeftComponent, 
										DialogFooter.OK_OPTION, //options, 
										DialogFooter.OK_OPTION, //defaultOption, 
										null, //dontShowKey, 
										null, //alwaysApplyKey, 
										DialogFooter.EscapeKeyBehavior.TRIGGERS_DEFAULT);
								f = FileDialogUtils.showOpenDialog(frames[0], "Find \""+p+"\"");
								while(f!=null && f.getName().contains(p)) {
									f = f.getParentFile();
								}
								if(f==null)
									throw e;
								prefs.put(p, f.getAbsolutePath());
								try {
									prefs.sync();
								} catch (BackingStoreException e1) {
									e1.printStackTrace();
								}
							}
						}
						srcpaths.add(f);
						if(output)
							System.out.println("\tIdentified classpathentry src: "+f.getAbsolutePath());
					} else if("lib".equals(k)) {
						File f = identifyFile(xmlFile, p);
						libs.add(f);
						if(output)
							System.out.println("\tIdentified classpathentry lib: "+f.getAbsolutePath());
					} else if("con".equals(k) || "output".equals(k)) {
						//do nothing
					} else {
						System.err.println("EclipseClasspathFile: unrecognized classpathentry kind: "+k);
					}
				}
			}
		}
	}

	private File identifyFile(final File adjacentFile,final String path) {
		String p = path;
		
		File file = new File(adjacentFile.getParentFile(), p);
		if(file.exists())
			return file;
		
		File t = adjacentFile;
		while(p.startsWith(File.separator)) {
			t = t.getParentFile();
			p = p.substring(1);
		}
		
		while(t.getParentFile()!=null) {
			String newPath = t.getParentFile().getAbsolutePath();
			if(!newPath.endsWith(File.separator))
				newPath = newPath+File.separator;
			newPath = newPath+p;
			File f = new File(newPath);
			if(f.exists())
				return f;
			t = t.getParentFile();
		}
		
		throw new MissingFileException("file not found: "+path+" (near "+adjacentFile.getAbsolutePath()+")", path);
	}
	
	static class MissingFileException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		String filename;
		
		public MissingFileException(String msg,String filename) {
			super(msg);
			this.filename = filename;
		}
		
		public String getFilename() {
			return filename;
		}
	}

	static DocumentBuilder builder;
	protected Element parseXML(File xmlFile) throws IOException, ParserConfigurationException, SAXException {
		if(builder==null) {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		try(InputStream in = new FileInputStream(xmlFile)) {
			//parse using builder to get DOM representation of the XML file
			org.w3c.dom.Document dom = builder.parse(in);
			Element root = dom.getDocumentElement();
			return root;
		}
	}
}
