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
package com.pump.release;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.pump.blog.Blurb;
import com.pump.image.ImageSize;
import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;
import com.pump.release.Workspace.BlurbInfo;

public class BlurbIndexBuilder {

	
	class BlurbWriteup implements Comparable<BlurbWriteup> {
		BlurbInfo blurb;
		
		public BlurbWriteup(BlurbInfo blurb) {
			this.blurb = blurb;
		}

		@Override
		public int compareTo(BlurbWriteup o) {
			Blurb b = blurb.getBlurb();
			return b.title().compareTo(o.blurb.getBlurb().title());
		}
		
		/** Express this blurb as a snippet of HTML. */
		public String getHTML() {
			Blurb b = blurb.getBlurb();
			
			StringWriter stringWriter = new StringWriter();
			stringWriter.append("<table border=\"0\" cellspacing=\"0px\" cellpadding=\"10px\" padding=\"0px\" width=\"650px\">\n");
			stringWriter.append("\t<tr>\n");
			stringWriter.append("\t\t<td style=\"background: linear-gradient( rgb(255, 255, 255), rgb(241, 241, 241));border:1px solid rgb(220,220,220);width=100%;height=100%;box-shadow: 0px 1px 3px #888888;\">\n");
			stringWriter.append("\t\t\t<table width=\"100%\">\n");
			stringWriter.append("\t\t\t\t<tr>\n");
			
			String article = b.article(); 
			String title = b.title();
			String releaseDate = b.releaseDate();
			if(article==null || article.length()==0) {
				stringWriter.append("\t\t\t\t\t<td><span style=\"font-weight: bold;font-size:110%;}\">"+title+"</span>");
			} else {
				stringWriter.append("\t\t\t\t\t<td><span style=\"font-weight: bold;font-size:110%;}\"><a href=\""+article+"\">"+title+"</a></span>");
			}
			if(releaseDate!=null && releaseDate.length()>0) {
				stringWriter.append("<br>");
				stringWriter.append(releaseDate);
			}
			stringWriter.append("</td>\n");
			
			//this block writes the upper-right corner of the table which may include
			//the jar link and/or the jnlp link
			{
				//this preserves the order the links are presented in (this stores the
				//human-readable link name)
				List<String> linkText = new ArrayList<String>();
				
				//this maps the human-readable link name to the hyperlink
				
				stringWriter.append("\t\t\t\t\t<td style=\"text-align:right;vertical-align:text-top;\"><span style=\"font-weight: bold;}\">");
				Class blurbClass = blurb.getBlurbClass();
				String javadocUrl = "https://mickleness.github.io/pumpernickel/javadoc/" + blurbClass.getName().replace(".", "/") + ".html";
				stringWriter.append("<a href=\"" + javadocUrl + "\">Javadoc</a>");
				stringWriter.append("</span></td>\n");
			}
			
			stringWriter.append("\t\t\t\t</tr>\n");
			stringWriter.append("\t\t\t</table>\n");
			stringWriter.append("\t\t</td>\n");
			stringWriter.append("\t</tr>\n");
			stringWriter.append("</table>\n");
			
			if(!b.imageName().isEmpty()) {
				File blurbGraphicFile = new File(blurbDirectory, b.imageName());
				if(!blurbGraphicFile.exists())
					throw new RuntimeException( blurbGraphicFile.getAbsolutePath() + " does not exist." );
				
				Dimension imageSize = null;
				if(blurbGraphicFile!=null)
					imageSize = ImageSize.get(blurbGraphicFile);
				
				if(blurbGraphicFile!=null && blurbGraphicFile.exists() && imageSize!=null) {
					stringWriter.append("<img align=\"right\" style=\"padding:8px\" src=\"blurbs/"+blurbGraphicFile.getName()+"\" width=\""+imageSize.width+"\" height=\""+imageSize.height+"\" alt=\"Demo Graphic\">");
				}
			}
			
			stringWriter.append("<p>"+b.summary()+"\n");
			
			stringWriter.append("<br clear=right/>\n");
			return stringWriter.toString();
		}
	}
	
	protected File blurbDirectory;

	public void run(Workspace workspace) throws IOException {
		SortedSet<BlurbWriteup> writeups = new TreeSet<BlurbWriteup>();
		File releaseDir = new File(workspace.getDirectory(), "docs");
		blurbDirectory = new File(releaseDir, "blurbs");
		
		if(!blurbDirectory.exists()) {
			throw new RuntimeException(blurbDirectory.getAbsolutePath()+" does not exist.");
		}
		
		FileTreeIterator iter = new FileTreeIterator(workspace.getDirectory(), "java");
		while(iter. hasNext()) {
			File javaFile = iter.next();
			BlurbInfo blurb = workspace.getBlurb(javaFile);
			if(blurb!=null) {
				writeups.add(new BlurbWriteup(blurb));
			}
		}
		
		File templateFile = new File(releaseDir, "template.html");
		String templateString = IOUtils.read(templateFile);
		
		StringBuilder sb = new StringBuilder();
		for(BlurbWriteup w : writeups) {
			sb.append(w.getHTML()+"\n");
		}
		String indexText = templateString.replace("<!insertTable>", sb.toString());

		File indexFile = new File(releaseDir, "index.html");
		if(IOUtils.write(indexFile, indexText, true))
			System.out.println("Updated "+indexFile.getAbsolutePath());
	}

}