package download;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IDownloadListener;
import com.gmo.basespaceService.model.FastQFile;
import com.gmo.logger.Log4JLogger;
import com.illumina.basespace.ApiClient;
import com.illumina.basespace.auth.ResourceForbiddenException;
import com.illumina.basespace.entity.File;
import com.illumina.basespace.file.DownloadEvent;
import com.illumina.basespace.file.DownloadListener;
import com.illumina.basespace.infrastructure.ResourceNotFoundException;

public class SampleDownloader implements Runnable, DownloadListener {

	private String destinationDirectory;
	private FastQFile fastqfile;
	private ApiClient clientBS;
	private IDownloadListener listener;

	private int currentProgress;

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public SampleDownloader(FastQFile fastqfile, String destinationDirectory, ApiClient clientBS, IDownloadListener listener) {
		this.destinationDirectory = destinationDirectory;
		this.clientBS = clientBS;
		this.fastqfile = fastqfile;
		this.currentProgress = 0;
		this.listener = listener;
	}

	@Override
	public void run() {

		// For a download we need the full file entity

		// Start download
		try {
			
			LOG.debug("Download executor running");
			final File file = clientBS.getFile(fastqfile.getId()).get();

			final java.io.File localFolder = new java.io.File(destinationDirectory);
			if(!localFolder.exists()) {
				localFolder.mkdirs();
			}
			
			LOG.debug("Remote BaseSPace API call");
			clientBS.download(file, localFolder, this);

		} catch (Throwable t) {
			LOG.debug("Download failed ", t);
			
			if (t instanceof Exception) {
				if (t instanceof ResourceNotFoundException) {
					LOG.error("Resource not found in baseSpace database : ID = " + fastqfile.getId());
				} else if (t instanceof ResourceForbiddenException) {
					LOG.error("Access to this resource is forbidden by BaseSpace");
				}
			}

			// Notify Listeners that download has failed
			if (listener != null) {
				listener.downloadFailed(fastqfile);
			}
		}

	}

	@Override
	public void progress(DownloadEvent evt) {
		// Send notification that progress change to server if percentage
		// changed.
		int newPercentage = (int) ((double) evt.getCurrentBytes() / evt.getTotalBytes() * 100);
		if (currentProgress != newPercentage) {
			LOG.debug("Download progress changed to " + newPercentage);
			if (listener != null) {
				listener.downloadProgress(newPercentage, fastqfile);
			}
			currentProgress = newPercentage;
		}
	}

	@Override
	public void complete(DownloadEvent evt) {
		if (!new java.io.File(destinationDirectory).exists()) {
			if (listener != null) {
				listener.downloadFailed(fastqfile);
			}
		} else {
			LOG.error("Download complete " + fastqfile);
			if (listener != null) {
				listener.downloadSuccess(fastqfile, destinationDirectory);
			}
		}
	}

	@Override
	public void canceled(DownloadEvent evt) {
		LOG.info("Download canceld");
		if (listener != null) {
			listener.downloadFailed(fastqfile);
		}
	}

}
