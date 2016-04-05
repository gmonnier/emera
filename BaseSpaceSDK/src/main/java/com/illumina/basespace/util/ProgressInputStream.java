package com.illumina.basespace.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * InputStream that notifies listeners of its progress.
 */
public class ProgressInputStream extends InputStream {
	
	private InputStream wrappedInputStream;
	private long progress;

	public ProgressInputStream(InputStream in) {
		wrappedInputStream = in;
		progress = 0;
	}

	@Override
	public int read() throws IOException {
		progress += 1;
		return wrappedInputStream.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		int retVal = wrappedInputStream.read(b);
		progress += retVal;
		return retVal;
	}

	@Override
	public int read(byte[] b, int offset, int length) throws IOException {
		int retVal = wrappedInputStream.read(b, offset, length);
		progress += retVal;
		return retVal;
	}

	@Override
	public void close() throws IOException {
		wrappedInputStream.close();
	}

	@Override
	public int available() throws IOException {
		return wrappedInputStream.available();
	}

	@Override
	public void mark(int readlimit) {
		wrappedInputStream.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		wrappedInputStream.reset();
	}

	@Override
	public boolean markSupported() {
		return wrappedInputStream.markSupported();
	}

	@Override
	public long skip(long n) throws IOException {
		return wrappedInputStream.skip(n);
	}

	public long getProgress() {
		return progress;
	}

}