package processorNode.model;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import basespaceService.model.FastQFile;

@XmlRootElement
public class ViewFile implements Serializable {

	private ViewFileOrigin origin;

	private OutputFileType fileType;

	private String name;

	private String id;

	private long dateCreated;

	private long size;

	private final static DateFormat df = new SimpleDateFormat("dd-MM-yyyy  HH:mm");

	private ViewFile() {
		fileType = OutputFileType.UNKNOWN;
	}
	
	public ViewFile(ViewFileOrigin origin, String name, String id, long dateCreated, long size) {
		super();
		this.origin = origin;
		this.name = name;
		this.id = id;
		this.dateCreated = dateCreated;
		this.size = size;
		
		extractFileTypeFromName();
	}

	/**
	 * Create a View file from an input FastQ file retrieved from BaseSpace.
	 * 
	 * @param inputFile
	 *            input sample file object.
	 */
	public ViewFile(FastQFile inputFile) {
		this.name = inputFile.getName();
		this.origin = ViewFileOrigin.BASESPACE;
		this.dateCreated = inputFile.getDateCreated().getTime();
		this.id = inputFile.getId();
		this.size = inputFile.getSize();
		
		extractFileTypeFromName();
	}

	/**
	 * Create a View file from a system file (basically an additional report
	 * file).
	 * 
	 * @param inputFile
	 *            input sample file object.
	 */
	public ViewFile(File inputFile) {
		this.name = inputFile.getName();
		this.origin = ViewFileOrigin.STORED;
		this.dateCreated = inputFile.lastModified();
		this.id = inputFile.getAbsolutePath();
		this.size = inputFile.length();
		
		extractFileTypeFromName();
	}

	private void extractFileTypeFromName() {
		if (name.toLowerCase().endsWith(".pdf")) {
			this.fileType = OutputFileType.PDF;
		} else if (name.toLowerCase().endsWith(".csv")) {
			this.fileType = OutputFileType.CSV;
		} else if (name.toLowerCase().endsWith(".fastq")) {
			this.fileType = OutputFileType.FASTQ;
		} else {
			this.fileType = OutputFileType.UNKNOWN;
		}
	}

	public long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getDateCreatedFormat() {
		return df.format(new Date(dateCreated));
	}

	public void setDateCreatedFormat(String dateCreated) {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ViewFileOrigin getOrigin() {
		return origin;
	}

	public void setOrigin(ViewFileOrigin type) {
		this.origin = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getReadableSize() {
		return readableFileSize(size);
	}

	public void setReadableSize(String readSize) {
		// only for JAXB
	}

	public OutputFileType getFileType() {
		return fileType;
	}

	public void setFileType(OutputFileType fileType) {
		this.fileType = fileType;
	}

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	@Override
	public String toString() {
		return "ViewFile [origin=" + origin + ", name=" + name + ", id=" + id + ", dateCreated=" + dateCreated + "]";
	}
}
