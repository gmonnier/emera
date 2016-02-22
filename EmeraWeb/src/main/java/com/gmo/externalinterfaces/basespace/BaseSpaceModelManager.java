package com.gmo.externalinterfaces.basespace;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.Logger;

import basespaceObjects.FastQFile;
import basespaceObjects.UserInfo;
import basespaceObjects.UserRun;

import com.gmo.externalinterfaces.basespace.rmiclient.BaseSpaceRMIClient;
import com.gmo.logger.Log4JLogger;

public class BaseSpaceModelManager {

	private List<UserRun> listRuns;

	private UserInfo userInfo;

	private BaseSpaceRMIClient rmiCLient;

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	private BaseSpaceModelManager() {
		listRuns = new ArrayList<UserRun>();
		userInfo = new UserInfo();
		updateModel();
	}

	public void updateModel() {
		LOG.info("Update baseSpace model");
		rmiCLient = new BaseSpaceRMIClient();
		if (rmiCLient.isConnectionOk()) {
			LOG.info("Request list of users runs from RMI Client.");
			listRuns = rmiCLient.requestListCurrentUserRuns();
			userInfo = rmiCLient.requestUserInfo();
		}
	}

	/**
	 * 
	 * @param id
	 * @return the fastQ file object with the given ID in the current model.
	 */
	public FastQFile getWithID(String id) {
		for (Iterator<UserRun> iterator = listRuns.iterator(); iterator.hasNext();) {
			UserRun userRun = (UserRun) iterator.next();
			List<FastQFile> listFiles = userRun.getListFilesData();
			for (Iterator<FastQFile> iterator2 = listFiles.iterator(); iterator2.hasNext();) {
				FastQFile fastQFile = (FastQFile) iterator2.next();
				if (fastQFile.getId().equals(id)) {
					return fastQFile;
				}
			}
		}
		throw new NoSuchElementException();
	}

	public void requestDownload(String analyseID, String uploadDirectory, FastQFile fastqRequest) {
		LOG.info("Request new download to basespace server");
		rmiCLient = new BaseSpaceRMIClient();
		if (rmiCLient.isConnectionOk()) {
			rmiCLient.requestStartNewDownload(analyseID, uploadDirectory, fastqRequest);
		}
	}

	public boolean isConnectionOK() {
		updateModel();
		return rmiCLient != null && rmiCLient.isConnectionOk();
	}

	public static synchronized BaseSpaceModelManager getInstance() {
		return BaseSpaceModelManagerHolder.instance;
	}

	private static class BaseSpaceModelManagerHolder {
		public final static BaseSpaceModelManager instance = new BaseSpaceModelManager();
	}

	public List<UserRun> getListRuns() {
		return listRuns;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}
}
