package com.gmo.coreprocessing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.processorserver.IDistantResource;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;

public abstract class Analysis {
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
	
	protected String id;

	protected String userid;
	
	private AnalysisStatus status;
	
	private long launchDate;

	private long completionDate;
	
	private int progress;

	private List<IDistantResource> assignedResources;
	
	public Analysis(String userID) {
		this.userid = userID;
		this.id = UUID.randomUUID().toString();
		this.progress = 0;
		this.completionDate = -1;
		this.status = AnalysisStatus.IDLE;
		this.assignedResources = new ArrayList<IDistantResource>();
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
	
	/*
	 * <----- Resources management
	 */

	public abstract void init(String bsuserID, String bsuserSecret, String bsuserToken);

	public abstract void stopAnalyse();
	
}
