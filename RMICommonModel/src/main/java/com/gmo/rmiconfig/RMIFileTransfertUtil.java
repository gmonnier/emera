package com.gmo.rmiconfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.gmo.network.rmiutil.RMIInputStream;
import com.gmo.network.rmiutil.RMIInputStreamImpl;
import com.gmo.network.rmiutil.RMIOutputStream;
import com.gmo.network.rmiutil.RMIOutputStreamImpl;

public class RMIFileTransfertUtil {
	
	final public static int BUF_SIZE = 1024 * 64;

	// --------- File transfert --------

	public OutputStream getOutputStream(File f) throws IOException {
		return new RMIOutputStream(new RMIOutputStreamImpl(new FileOutputStream(f)));
	}

	public InputStream getInputStream(File f) throws IOException {
		return new RMIInputStream(new RMIInputStreamImpl(new FileInputStream(f)));
	}

	public void upload(File src, File dest) throws IOException {
		copy(new FileInputStream(src), getOutputStream(dest));
	}
	
	public void upload(InputStream src, File dest) throws IOException {
		copy(src, getOutputStream(dest));
	}

	public void download(File src, File dest) throws IOException {
		copy(getInputStream(src), new FileOutputStream(dest));
	}
	
	public void download(File src, OutputStream dest) throws IOException {
		copy(getInputStream(src), dest);
	}

	public void copy(InputStream in, OutputStream out) throws IOException {
		byte[] b = new byte[BUF_SIZE];
		int len;
		while ((len = in.read(b)) >= 0) {
			out.write(b, 0, len);
		}
		in.close();
		out.close();
	}
	
}
