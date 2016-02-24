package viewModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.Logger;

import basespaceService.model.FastQFile;

import com.gmo.logger.Log4JLogger;

@XmlRootElement
public class BSDownloadInfo {

	private List<BSSingleDownloadInfo> downloadingFiles;
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public BSDownloadInfo() {
		this.downloadingFiles = new ArrayList<BSSingleDownloadInfo>();
	}

	public void update(FastQFile downloadingFile, int progress) {
		boolean isInside = false;
		for (Iterator<BSSingleDownloadInfo> iterator = downloadingFiles.iterator(); iterator.hasNext();) {
			BSSingleDownloadInfo bsSingleDownloadInfo = (BSSingleDownloadInfo) iterator.next();
			if (bsSingleDownloadInfo.getFile().equals(downloadingFile)) {
				isInside = true;
				bsSingleDownloadInfo.setProgress(progress);
				return;
			}
		}

		if (!isInside) {
			downloadingFiles.add(new BSSingleDownloadInfo(downloadingFile, progress));
		}
	}

	public List<BSSingleDownloadInfo> getDownloadingFiles() {
		return downloadingFiles;
	}
	
	public void setDownloadingFiles( List<BSSingleDownloadInfo> listInfo) {
		// for serialization
	}

	public void downloadDone(FastQFile fastQf) {
		for (int i = downloadingFiles.size() - 1; i >= 0; i--) {
			if (downloadingFiles.get(i).getFile().equals(fastQf)) {
				downloadingFiles.remove(i);
			}
		}
	}

	public boolean isIsdownloading() {
		return !downloadingFiles.isEmpty();
	}

	public void setIsdownloading(boolean isdownloading) {
		// For JAXB
	}

}
