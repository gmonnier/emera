package download;

import java.rmi.RemoteException;

import org.apache.logging.log4j.Logger;

import rmiImplementations.clients.RMIDownloadNotifClient;

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

	private String outputPath;
	private FastQFile fastqfile;
	private ApiClient clientBS;
	private IDownloadListener listener;

	private int currentProgress;

	private String analyseID;

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public SampleDownloader(FastQFile fastqfile, String outputPath, ApiClient clientBS, String analyseID) {
		this.outputPath = outputPath;
		this.clientBS = clientBS;
		this.fastqfile = fastqfile;
		this.analyseID = analyseID;
		this.currentProgress = 0;

		RMIDownloadNotifClient clientRMI = new RMIDownloadNotifClient();
		if (clientRMI.isConnectionOk()) {
			listener = clientRMI.getRmiDownloadListener();
		}
	}

	@Override
	public void run() {

		// For a download we need the full file entity

		// Start download
		try {
			final File file = clientBS.getFile(fastqfile.getId()).get();

			final java.io.File localFolder = new java.io.File(outputPath);
			clientBS.download(file, localFolder, this);

		} catch (Throwable t) {
			if (t instanceof Exception) {
				if (t instanceof ResourceNotFoundException) {
					LOG.error("Resource not found in baseSpace database : ID = " + fastqfile.getId());
				} else if (t instanceof ResourceForbiddenException) {
					LOG.error("Access to this resource is forbidden by BaseSpace");
				}
			}

			// Notify Listeners that download has failed
			try {
				listener.downloadFailed(analyseID, fastqfile);
			} catch (RemoteException e) {
				LOG.error("Unable to send to server that download failed for file " + fastqfile.getName());
			}
		}

	}

	@Override
	public void progress(DownloadEvent evt) {
		// Send notification that progress change to server if percentage changed.
		int newPercentage = (int) ((double) evt.getCurrentBytes() / evt.getTotalBytes() * 100);
		if (currentProgress != newPercentage) {
			LOG.debug("Download progress changed to " + newPercentage);
			try {
				listener.downloadProgress(newPercentage, analyseID, fastqfile);
			} catch (RemoteException e) {
				LOG.error("Unable to send to server that download progressed - Abort all downloads ");
				clientBS.cancelDownloads();
			}
			currentProgress = newPercentage;
		}
	}

	@Override
	public void complete(DownloadEvent evt) {
		if (!new java.io.File(outputPath).exists()) {
			LOG.error("Output file not written successfully");
			try {
				listener.downloadFailed(analyseID, fastqfile);
			} catch (RemoteException e) {
				LOG.error("Unable to send to server that download failed : ", e);
			}
		} else {
			try {
				LOG.error("Download complete " + fastqfile + " for analyse " + analyseID);
				listener.downloadSuccess(analyseID, fastqfile, outputPath);
			} catch (RemoteException e) {
				LOG.error("Unable to send to server that download failed : ", e);
			}
		}
	}

	@Override
	public void canceled(DownloadEvent evt) {
		LOG.info("Download canceld");
		try {
			listener.downloadFailed(analyseID, fastqfile);
		} catch (RemoteException e) {
			LOG.error("Unable to send to server that download failed : ", e);
		}
	}

}
