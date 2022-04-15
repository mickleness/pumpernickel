package com.pump.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Create an InputStream from a File.
 */
public class FileInputStreamSource implements InputStreamSource {

	protected File file;

	public FileInputStreamSource(File file) {
		this.file = Objects.requireNonNull(file);
	}

	@Override
	public InputStream createInputStream() throws IOException {
		return new FileInputStream(file);
	}

	public File getFile() {
		return file;
	}
}