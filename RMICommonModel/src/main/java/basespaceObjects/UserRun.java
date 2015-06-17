package basespaceObjects;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserRun implements Serializable {
	
	private String name;
	private String id;
	private String status;
	private Date dateCreated;
	private String dateCreatedFormat;
	private long totalSize;
	
	private List<FastQFile> listFilesData;
	
	private final static DateFormat df = new SimpleDateFormat("dd-MM-yyyy  HH:mm");
	
	// Needed for JAXB JSON encoder
	public UserRun() {
		listFilesData = new ArrayList<FastQFile>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
		this.dateCreatedFormat = df.format(dateCreated);
	}

	public String getDateCreatedFormat() {
		return dateCreatedFormat;
	}

	public void setDateCreatedFormat(String dateCreatedFormat) {
		this.dateCreatedFormat = dateCreatedFormat;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	
	public long getTotalSize() {
		return totalSize;
	}

	public List<FastQFile> getListFilesData() {
		return listFilesData;
	}

	public void setListFilesData(List<FastQFile> listFilesData) {
		this.listFilesData = listFilesData;
	}

}
