package main;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import model.BaseSpaceUserModel;

import org.apache.logging.log4j.Logger;

import basespaceService.model.FastQFile;

import com.gmo.logger.Log4JLogger;
import com.illumina.basespace.ApiClient;
import com.illumina.basespace.ApiClientManager;

import connection.BaseSpaceInitConnection;
import download.SampleDownloader;


public class BaseSpacePlatformManager {

	private static class ManagerHolder {
		public final static BaseSpacePlatformManager instance = new BaseSpacePlatformManager();
	}

	private ApiClient clientBS;

	private BaseSpaceUserModel bsUserModel;

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	/**
	 * Instantiate unique Executor service of the application. This service is
	 * used to save and download files to this server from BaseSpace servers.
	 */
	private static final Executor fileWriterService = Executors.newFixedThreadPool(1);

	private BaseSpacePlatformManager() {
		init();
		bsUserModel = new BaseSpaceUserModel();
	}

	private void init() {
		LOG.info("Init baseSpace connection");

		BaseSpaceInitConnection bsConnectionConfig = new BaseSpaceInitConnection();
		try {
			clientBS = ApiClientManager.instance().createClient(bsConnectionConfig);
			LOG.info("baseSpace connection instantiated");
		} catch (Throwable e) {
			for(int i=0; i<e.getCause().getStackTrace().length; i++) {
				LOG.error(e.getCause().getStackTrace()[i]);
            }
			LOG.error("Error while creating BS client", e);
		}

	}

	public static synchronized BaseSpacePlatformManager getInstance() {
		return ManagerHolder.instance;
	}

	public ApiClient getClientBS() {
		return clientBS;
	}

	public BaseSpaceUserModel getBSUserModel() {
		return bsUserModel;
	}

	public void requestNewDownload(String path, FastQFile file, String analyseID) {
		SampleDownloader downloader = new SampleDownloader(file, path, clientBS, analyseID);
		fileWriterService.execute(downloader);
	}

}
