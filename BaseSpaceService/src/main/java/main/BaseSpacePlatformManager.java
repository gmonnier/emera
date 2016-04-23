package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IDownloadListener;
import com.gmo.basespaceService.model.FastQFile;
import com.gmo.basespaceService.model.UserInfo;
import com.gmo.basespaceService.model.UserRun;
import com.gmo.logger.Log4JLogger;
import com.illumina.basespace.ApiClient;
import com.illumina.basespace.ApiClientManager;
import com.illumina.basespace.entity.FileCompact;
import com.illumina.basespace.entity.RunCompact;
import com.illumina.basespace.entity.SampleCompact;
import com.illumina.basespace.entity.User;
import com.illumina.basespace.param.QueryParams;
import com.illumina.basespace.response.GetUserResponse;

import connection.BaseSpaceConfiguration;
import download.SampleDownloader;

public class BaseSpacePlatformManager {

	private final static Map<String, BaseSpacePlatformManager> instances = new HashMap<String, BaseSpacePlatformManager>();

	private ApiClient clientBS;

	private List<UserRun> listUserRuns;

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	/**
	 * Instantiate unique Executor service of the application. This service is
	 * used to save and download files to this server from BaseSpace servers.
	 */
	private static final Executor fileWriterService = Executors.newFixedThreadPool(1);

	private BaseSpacePlatformManager(String clientID, String clientSecret, String accessToken) {
		LOG.info("Init baseSpace connection");

		BaseSpaceConfiguration bsConnectionConfig = new BaseSpaceConfiguration(clientID, clientSecret, accessToken);

		try {
			clientBS = ApiClientManager.instance().createClient(bsConnectionConfig);
			LOG.info("baseSpace connection instantiated");
		} catch (Throwable e) {
			for (int i = 0; i < e.getCause().getStackTrace().length; i++) {
				LOG.error(e.getCause().getStackTrace()[i]);
			}
			LOG.error("Error while creating BS client", e);
		}

		listUserRuns = new ArrayList<>();
		updateListRunsCurrentUser();
	}

	public static synchronized BaseSpacePlatformManager getInstance(String clientID, String clientSecret, String accessToken) {
		if (instances.get(clientID) == null) {
			instances.put(clientID, new BaseSpacePlatformManager(clientID, clientSecret, accessToken));
		}
		return instances.get(clientID);
	}

	public void requestNewDownload(String fileName, FastQFile file, IDownloadListener downloadListener) {
		LOG.debug("Request download to " + fileName + "  from FQFile = " + file);
		SampleDownloader downloader = new SampleDownloader(file, fileName, clientBS, downloadListener);
		fileWriterService.execute(downloader);
	}

	public List<UserRun> getListRunsCurrentUser() {
		return listUserRuns;
	}

	public void updateListRunsCurrentUser() {

		LOG.info("Retrieve list of users runs");

		List<UserRun> listRuns = new ArrayList<UserRun>();

		RunCompact[] list = clientBS.getRuns(new QueryParams(20)).items();
		LOG.info(list.length + "runs extracted");
		for (int i = 0; i < list.length; i++) {

			LOG.debug("RUN ----> " + list[i].getName());

			UserRun currRun = new UserRun();
			currRun.setName(list[i].getName());
			currRun.setId(list[i].getId());
			currRun.setDateCreated(list[i].getDateCreated());
			currRun.setStatus(list[i].getStatus());
			currRun.setTotalSize(list[i].getTotalSize());

			SampleCompact[] sc = clientBS.getSamples(list[i], null).items();

			for (int sci = 0; sci < sc.length; sci++) {

				SampleCompact scc = sc[sci];
				LOG.debug("  SAMPLE ----> " + scc);
				if (!scc.getName().startsWith("Unindexed")) {
					FileCompact[] fcs = clientBS.getFiles(scc, null).items();
					for (int j = 0; j < fcs.length; j++) {
						FileCompact fileComp = fcs[j];
						FastQFile newFile = new FastQFile(fileComp.getId());
						newFile.setContentType(fileComp.getContentType());
						newFile.setDateCreated(fileComp.getDateCreated());
						newFile.setPath(fileComp.getPath());
						newFile.setName(fileComp.getName());
						newFile.setSize(fileComp.getSize());

						currRun.getListFilesData().add(newFile);
						LOG.debug("         FILE --> " + fileComp.getName() + " size = " + fileComp.getSize());
					}
				} else {
					LOG.debug("Unindexed ingored sample.");
				}
			}

			listRuns.add(currRun);
		}

		listUserRuns = listRuns;
	}

	/**
	 * 
	 * @param id
	 * @return the fastQ file object with the given ID in the current model.
	 */
	public FastQFile getWithID(String id) {
		for (Iterator<UserRun> iterator = listUserRuns.iterator(); iterator.hasNext();) {
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

	public UserInfo getCurrentUserInfo() {
		LOG.info("Retrieve current user info");

		GetUserResponse ur = clientBS.getCurrentUser();
		User user = ur.get();
		return new UserInfo(user.getName(), user.getId());
	}

}
