package com.pump.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

/**
 * Create an InputStream from a URL.
 */
public class URLInputStreamSource implements InputStreamSource {

	protected URL url;

	public URLInputStreamSource(URL url) {
		this.url = Objects.requireNonNull(url);
	}

	@Override
	public InputStream createInputStream() throws IOException {
		return url.openStream();
	}

	public URL getURL() {
		return url;
	}
}