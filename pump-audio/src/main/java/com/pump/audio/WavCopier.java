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
package com.pump.audio;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;

import com.pump.io.SuffixFilenameFilter;

public class WavCopier extends WavReader {
	WavFileWriter writer = new WavFileWriter(new File("copyXYZ.wav"));
	boolean started = false;
	public WavCopier(InputStream in) throws IOException {
		super(in);
		read();
		writer.close();
	}

	@Override
	protected void processSamples(byte[] sample,int offset,int length, int numberOfSamples) throws IOException {
		if(started==false) {
			if(lastFormatChunk==null) throw new NullPointerException();
			writer.writeFormat( lastFormatChunk );
			started = true;
		}
		//TODO: use "offset" here? (Not changing because T4L is near release candidate)
		writer.writeSample(sample, 0, length);
	}

	//made private so BlogUpdater doesn't roll out a jar
	@SuppressWarnings("unused")
	private static void main(String[] s) {
		try {
			JFrame frame = new JFrame();
			FileDialog fd = new FileDialog(frame);
			fd.setFilenameFilter(new SuffixFilenameFilter("wav", "wave"));
			fd.pack();
			fd.setVisible(true);
			if(fd.getFile()==null) return;
			File wavFile = new File(fd.getDirectory()+fd.getFile());
			FileInputStream in = null;
			try {
				in = new FileInputStream(wavFile);
				System.err.println("length: " + wavFile.length());
				WavCopier r = new WavCopier(in);
			} finally {
				in.close();
			}
			System.exit(0);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}