package com.gmo.coreprocessing;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IDownloadListener;
import com.gmo.basespaceService.model.FastQFile;
import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.modelconverters.ProcessConfigurationConverter;
import com.gmo.processorNode.viewmodel.BSDownloadInfo;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewFileOrigin;
import com.gmo.processorserver.IDistantResource;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;

import main.BaseSpacePlatformManager;

public abstract class Analysis implements IDownloadListener {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	protected String id;

	protected String userid;

	protected AnalysisStatus status;

	protected long launchDate;

	protected long completionDate;

	protected int progress;

	private List<IDistantResource> assignedResources;

	private BSDownloadInfo downloadInfo;

	public Analysis(String userID) {
		this.userid = userID;
		this.id = UUID.randomUUID().toString();
		this.progress = 0;
		this.completionDate = -1;
		this.status = AnalysisStatus.IDLE;
		this.assignedResources = new ArrayList<IDistantResource>();
		this.downloadInfo = new BSDownloadInfo();
	}

	public void init(String bsuserID, String bsuserSecret, String bsuserToken) {

		LOG.debug("Init analyse " + id);
		setLaunchDate(new Date().getTime());

		// Ask for BaseSPace download if basespaces files are required (Data
		// files)
		List<ViewFile> listdataSelected = viewConfiguration.getSelectedDataFiles();
		for (Iterator<ViewFile> iterator = listdataSelected.iterator(); iterator.hasNext();) {
			ViewFile dataFile = (ViewFile) iterator.next();
			if (dataFile.getOrigin() == ViewFileOrigin.BASESPACE) {
				FastQFile fastQFile = BaseSpacePlatformManager.getInstance(bsUserID, bsUserSecret, bsToken).getWithID(dataFile.getId());
				LOG.debug("Request download for fastQFile " + fastQFile.getName());
				downloadInfo.update(fastQFile, 0);
				setStatus(AnalysisStatus.RETRIEVE_FILES);
				BaseSpacePlatformManager.getInstance(bsUserID, bsUserSecret, bsToken).requestNewDownload(StorageConfigurationManager.getInstance().getConfig().getDataFilesRoot(), fastQFile, this);
			}
		}
	}

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public long getLaunchDate() {
		return launchDate;
	}

	public void setLaunchDate(long laundDate) {
		this.launchDate = laundDate;
	}

	public long getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(long endDate) {
		this.completionDate = endDate;
	}

	public AnalysisStatus getStatus() {
		return status;
	}

	public List<IDistantResource> getAssignedResources() {
		return assignedResources;
	}

	public void setAssignedResources(List<IDistantResource> assignedResources) {
		this.assignedResources = assignedResources;
	}

	public BSDownloadInfo getDownloadInfo() {
		return downloadInfo;
	}

	public void setDownloadInfo(BSDownloadInfo downloadInfo) {
		this.downloadInfo = downloadInfo;
	}

	/*
	 * Resources management ----->
	 */
	public synchronized void assignDistantResource(IDistantResource resource) {
		boolean found = false;
		LOG.info("Assign distant resource : " + resource.getName() + "  to " + this.id);
		for (int i = 0; i < assignedResources.size(); i++) {
			if (assignedResources.get(i).getID().equals(resource.getID())) {
				// Resource already assigned to this campaign. Replace.
				assignedResources.set(i, resource);
				found = true;
			}
		}

		if (!found) {
			assignedResources.add(resource);
		}
	}

	public synchronized void removeDistantResource(String resourceID) {
		for (int i = assignedResources.size() - 1; i >= 0; i--) {
			if (resourceID.equals(assignedResources.get(i).getID())) {
				// Release current resources (especially in the buffer if
				// exists)
				if (buffer != null) {
					buffer.releaseChunks(resourceID);
				}
				assignedResources.remove(i);
				LOG.debug(resourceID + " removed from analysis");
				return;
			}
		}
		LOG.warn(resourceID + " not found into assignedResources list : current list = " + printAssignedResourcesID());
	}

	public synchronized void removeAllDistantResource() {
		for (int i = assignedResources.size() - 1; i >= 0; i--) {
			if (buffer != null) {
				buffer.releaseChunks(assignedResources.get(i).getID());
			}
			// Notify client to stop current action
			assignedResources.get(i).requestStopCurrent();
		}
		assignedResources.clear();
	}

	private String printAssignedResourcesID() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<IDistantResource> iterator = assignedResources.iterator(); iterator.hasNext();) {
			IDistantResource resource = (IDistantResource) iterator.next();
			sb.append(resource.getID());
			if (iterator.hasNext()) {
				sb.append(" , ");
			}
		}
		return sb.toString();
	}

	/*
	 * <----- Resources management
	 */

	@Override
	public void downloadFailed(FastQFile inputFile) {
		LOG.debug("Server side Download failed received for " + inputFile.getName());
		setStatus(AnalysisStatus.UPLOAD_ERROR);
		downloadInfo.downloadDone(inputFile);
	}

	@Override
	public void downloadSuccess(FastQFile inputFile, String outputPath) {
		StorageConfigurationManager.getInstance().updateModel();
		try {
			ModelFileStored mfs = StorageConfigurationManager.getInstance().getWithPath(InputType.DATA, outputPath);
			processConfiguration.addToData(mfs);
			downloadInfo.downloadDone(inputFile);
		} catch (NoSuchElementException nse) {
			LOG.error("No Model File stored element found with path " + outputPath + " of type : " + InputType.DATA);
			setStatus(AnalysisStatus.UPLOAD_ERROR);
		}
	}

	@Override
	public void downloadProgress(int percentage, FastQFile inputFile) {
		downloadInfo.update(inputFile, percentage);
	}

	public abstract void stopAnalyse();

	public abstract void setStatus(AnalysisStatus status);

}
