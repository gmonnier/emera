package com.gmo.viewmodel;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ViewModelFileStored implements Serializable {

	private String id;

	private File systemFile;

	private Date lastModified;

	private ViewModelFileStored() {
	}

	public ViewModelFileStored(File systemFile) {
		this.id = systemFile.getAbsolutePath();
		lastModified = new Date(systemFile.lastModified());
		this.systemFile = systemFile;
	}

	public String getName() {
		return systemFile.getName();
	}

	public void setName(String name) {
		// for serialization only
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public File getSystemFile() {
		return systemFile;
	}

	public void setSystemFile(File systemFile) {
		this.systemFile = systemFile;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public long getSize() {
		return systemFile.length();
	}

	@Override
	public String toString() {
		return "FileStored [systemFile=" + systemFile.getAbsolutePath() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ViewModelFileStored other = (ViewModelFileStored) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}