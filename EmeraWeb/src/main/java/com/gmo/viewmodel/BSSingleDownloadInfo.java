package com.gmo.viewmodel;

import javax.xml.bind.annotation.XmlRootElement;

import basespaceObjects.FastQFile;

@XmlRootElement
public class BSSingleDownloadInfo {

	private FastQFile file;
	
	private int progress;
	
	public BSSingleDownloadInfo() {
		file = null;
		progress = 0;
	}

	public BSSingleDownloadInfo(FastQFile file, int progress) {
		super();
		this.file = file;
		this.progress = progress;
	}
	
	public BSSingleDownloadInfo(FastQFile file) {
		super();
		this.file = file;
		this.progress = 0;
	}

	public FastQFile getFile() {
		return file;
	}

	public void setFile(FastQFile file) {
		this.file = file;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BSSingleDownloadInfo other = (BSSingleDownloadInfo) obj;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
}
