package viewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.gmo.basespaceService.model.FastQFile;
import com.gmo.basespaceService.model.UserRun;

public class ViewUserRun {

	private String name;
	private String id;
	private String status;
	private Date dateCreated;
	private long totalSize;
	
	private List<ViewFile> listFilesData;
	
	private final static DateFormat df = new SimpleDateFormat("dd-MM-yyyy  HH:mm");
	
	private ViewUserRun() {
	}
			
	// Needed for JAXB JSON encoder
	public ViewUserRun(UserRun inputRun) {
		name = inputRun.getName();
		id = inputRun.getId();
		status = inputRun.getStatus();
		dateCreated = inputRun.getDateCreated();
		totalSize = inputRun.getTotalSize();
		
		listFilesData = new ArrayList<ViewFile>();
		for (Iterator<FastQFile> iterator = inputRun.getListFilesData().iterator(); iterator.hasNext();) {
			FastQFile fastQFile = (FastQFile) iterator.next();
			listFilesData.add(new ViewFile(fastQFile));
		}
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
	}

	public String getDateCreatedFormat() {
		return df.format(dateCreated);
	}
	
	public void setDateCreatedFormat(String dateFormatted) {
		// JAXB Requirement
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}
	
	public long getTotalSize() {
		return totalSize;
	}

	public List<ViewFile> getListFilesData() {
		return listFilesData;
	}

	public void setListFilesData(List<ViewFile> listFilesData) {
		this.listFilesData = listFilesData;
	}
}
