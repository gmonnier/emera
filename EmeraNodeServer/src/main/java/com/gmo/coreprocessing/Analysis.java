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
import com.gmo.processorNode.viewmodel.BSDownloadInfo;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewFileOrigin;
import com.gmo.processorserver.IDistantResource;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.data.ChunkResult;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.util.FileCollectorListener;

import main.BaseSpacePlatformManager;

public abstract class Analysis implements FileCollectorListener {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	protected String id;

	protected String userid;

	protected AnalysisStatus status;

	protected long launchDate;

	protected long completionDate;

	protected int progress;

	private List<IDistantResource> assignedResources;

	private FileCollector fileCollector;

	public Analysis(String userID) {
		this.userid = userID;
		this.id = UUID.randomUUID().toString();
		this.progress = 0;
		this.completionDate = -1;
		this.status = AnalysisStatus.IDLE;
		this.assignedResources = new ArrayList<IDistantResource>();
	}

	public void init(String bsuserID, String bsuserSecret, String bsuserToken, List<ViewFile> requestedFiles) {
		LOG.debug("Init analyse " + id);
		setLaunchDate(new Date().getTime());

		fileCollector = new FileCollector(requestedFiles, this, bsuserID, bsuserSecret, bsuserToken);
		if (fileCollector.needsCollection()) {
			setStatus(AnalysisStatus.RETRIEVE_FILES);
		}

	}
	
	public synchronized void setStatus(AnalysisStatus newstatus) {
		if (newstatus == status) {
			return;
		}

		LOG.debug("Request to change analysis status from " + status + " to " + newstatus);

		if (this.status == AnalysisStatus.UPLOAD_ERROR || this.status == AnalysisStatus.RUNNING_ERROR) {
			LOG.debug("Analysis is in Upload error status. Cannot be updated");
			return;
		}

		else if (newstatus == AnalysisStatus.READY_FOR_PROCESSING) {
			LOG.debug("Analysis " + id + " switch to Ready for processing. Start buffer and dispatcher");
			startProcessing();
		}

		this.status = newstatus;
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

	public FileCollector getFileCollector() {
		return fileCollector;
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
				cleanupAfterResourceRelease(resourceID);
				assignedResources.remove(i);
				LOG.debug(resourceID + " removed from analysis");
				return;
			}
		}
		LOG.warn(resourceID + " not found into assignedResources list : current list = " + printAssignedResourcesID());
	}

	public synchronized void removeAllDistantResource() {
		for (int i = assignedResources.size() - 1; i >= 0; i--) {
			cleanupAfterResourceRelease(assignedResources.get(i).getID());
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
	public void fileCollectionDone() {
		LOG.debug("All files successfully collected, analysis is ready to start.");
		setStatus(AnalysisStatus.READY_FOR_PROCESSING);
	}
	
	@Override
	public void collectionFailed() {
		setStatus(AnalysisStatus.UPLOAD_ERROR);
	}

	protected abstract void cleanupAfterResourceRelease(String resourceID);
	
	protected abstract void startProcessing();

	public abstract void stopAnalyse();

	public abstract void analysisResultsReceived(ChunkResult result);

}
