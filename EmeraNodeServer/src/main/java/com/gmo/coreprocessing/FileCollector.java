package com.gmo.coreprocessing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IDownloadListener;
import com.gmo.basespaceService.model.FastQFile;
import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.modelconverters.FileStoredConverter;
import com.gmo.processorNode.viewmodel.BSDownloadInfo;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewFileOrigin;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.util.FileCollectorListener;

import main.BaseSpacePlatformManager;

public class FileCollector implements IDownloadListener {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private List<ViewFile> collectedFiles;

	private List<ViewFile> requestedFiles;

	private BSDownloadInfo downloadInfo;

	// Listener. Notified each time a file is added to this configuration.
	private transient FileCollectorListener collectorListener;

	public FileCollector(List<ViewFile> requestedFiles, FileCollectorListener collectorListener, String bsuserID, String bsuserSecret, String bsuserToken) {
		super();
		this.collectedFiles = new ArrayList<>();
		this.requestedFiles = requestedFiles;
		this.collectorListener = collectorListener;
		this.downloadInfo = new BSDownloadInfo();

		for (Iterator<ViewFile> iterator = requestedFiles.iterator(); iterator.hasNext();) {
			ViewFile dataFile = (ViewFile) iterator.next();
			if (dataFile.getOrigin() == ViewFileOrigin.BASESPACE) {
				FastQFile fastQFile = BaseSpacePlatformManager.getInstance(bsuserID, bsuserSecret, bsuserToken).getWithID(dataFile.getId());
				LOG.debug("Request download for fastQFile " + fastQFile.getName());
				downloadInfo.update(fastQFile, 0);
				BaseSpacePlatformManager.getInstance(bsuserID, bsuserSecret, bsuserToken).requestNewDownload(StorageConfigurationManager.getInstance().getConfig().getDataFilesRoot(), fastQFile, this);
			} else {
				// Do not used fileCollected method here since those files are already populated in the analysis config
				collectedFiles.add(dataFile);
			}
		}
	}

	private void checkCollectedFiles() {
		boolean allFilesUploaded = requestedFiles.size() == collectedFiles.size();

		if (allFilesUploaded) {
			collectorListener.fileCollectionDone();
		}
	}

	public boolean needsCollection() {
		for (Iterator<ViewFile> iterator = requestedFiles.iterator(); iterator.hasNext();) {
			ViewFile dataFile = (ViewFile) iterator.next();
			if (dataFile.getOrigin() == ViewFileOrigin.BASESPACE) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void downloadFailed(FastQFile inputFile) {
		LOG.debug("Server side Download failed received for " + inputFile.getName());
		collectorListener.collectionFailed();
		downloadInfo.downloadDone(inputFile);
	}

	@Override
	public void downloadSuccess(FastQFile inputFile, String outputPath) {
		StorageConfigurationManager.getInstance().updateModel();
		try {
			ModelFileStored mfs = StorageConfigurationManager.getInstance().getWithPath(InputType.DATA, outputPath);
			downloadInfo.downloadDone(inputFile);
			fileCollected(mfs, InputType.DATA);
			
		} catch (NoSuchElementException nse) {
			LOG.error("No Model File stored element found with path " + outputPath + " of type : " + InputType.DATA);
			collectorListener.collectionFailed();
		}
	}
	
	public void fileCollected(ModelFileStored mfs, InputType type) {
		collectorListener.fileCollected(type, mfs);
		collectedFiles.add(new FileStoredConverter().buildViewModelObject(mfs));
		checkCollectedFiles();
	}

	@Override
	public void downloadProgress(int percentage, FastQFile inputFile) {
		downloadInfo.update(inputFile, percentage);
	}

	public BSDownloadInfo getDownloadInfo() {
		return downloadInfo;
	}

}
