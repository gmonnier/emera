package com.gmo.coreprocessing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;

import com.gmo.basespaceService.interfaces.IDownloadListener;
import com.gmo.basespaceService.model.FastQFile;
import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.coreprocessing.analysismerger.AnalysisMerger;
import com.gmo.coreprocessing.fastQReaderDispatcher.ChunkQueueBuffer;
import com.gmo.coreprocessing.fastQReaderDispatcher.DataReaderDispatcher;
import com.gmo.coreprocessing.fastQReaderDispatcher.IReaderDispatcherListener;
import com.gmo.logger.Log4JLogger;
import com.gmo.modelconverters.ProcessConfigurationConverter;
import com.gmo.processorNode.viewmodel.BSDownloadInfo;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewFileOrigin;
import com.gmo.processorserver.IDistantResource;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.data.ChunkResult;
import com.gmo.sharedobjects.model.genelibrary.GeneLibrary;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.model.processconfiguration.ProcessConfiguration;
import com.gmo.sharedobjects.model.reports.Report;
import com.gmo.sharedobjects.util.FileUploadListener;

import main.BaseSpacePlatformManager;

public class Analysis implements FileUploadListener, IAnalysisProcessingListener, IReaderDispatcherListener, IDownloadListener {

	/**
	 * InstantiateExecutor service for this . This service is used to start
	 * worker threads on data analysis.
	 */
	private final ExecutorService dataAnalysisService = Executors.newSingleThreadExecutor();

	private int progress;

	private List<IDistantResource> assignedResources;

	private BSDownloadInfo downloadInfo;

	// Configuration generated by the view
	private ViewCreateProcessConfiguration viewConfiguration;

	// Configuration of the processing
	private ProcessConfiguration processConfiguration;

	private List<ViewFile> additionalAnalyses;

	private long launchDate;

	private long completionDate;

	private GeneLibrary geneLibrary;

	private AnalysisStatus status;

	private String id;

	private String userid;

	private final static DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	// Start new thread to merge results
	private AnalysisMerger merger;

	// Associated report
	private Report report;

	// Start new thread to merge results
	private final ChunkQueueBuffer buffer = new ChunkQueueBuffer();

	// Mandatory for jaxb
	public Analysis() {
		this.additionalAnalyses = new ArrayList<>();
	}

	public Analysis(ViewCreateProcessConfiguration viewconfiguration, String userID) {
		progress = 0;
		completionDate = -1;
		geneLibrary = null;
		this.userid = userID;
		status = AnalysisStatus.IDLE;
		this.viewConfiguration = viewconfiguration;
		downloadInfo = new BSDownloadInfo();
		setLaunchDate(new Date().getTime());
		id = UUID.randomUUID().toString();
		this.assignedResources = new ArrayList<IDistantResource>();
		this.additionalAnalyses = new ArrayList<>();
	}

	public void init(String bsUserID, String bsUserSecret, String bsToken) {

		LOG.debug("Init analyse " + id);

		// Init process configuration
		processConfiguration = new ProcessConfigurationConverter().buildDataModelObject(viewConfiguration);
		processConfiguration.setUploadListener(this);

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

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public ViewCreateProcessConfiguration getConfiguration() {
		return viewConfiguration;
	}

	public void setConfiguration(ViewCreateProcessConfiguration configuration) {
		this.viewConfiguration = configuration;
	}

	public BSDownloadInfo getDownloadInfo() {
		return downloadInfo;
	}

	public void setDownloadInfo(BSDownloadInfo downloadInfo) {
		this.downloadInfo = downloadInfo;
	}

	public long getLaunchDate() {
		return launchDate;
	}

	public void setLaunchDate(long laundDate) {
		this.launchDate = laundDate;
	}

	public String getLaunchDateFormat() {
		return df.format(new Date(launchDate));
	}

	public void setLaunchDateFormat(String laundDateFormat) {
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
			report = new Report(processConfiguration, launchDate, id, userid, DataReaderDispatcher.CHUNK_SIZE);

			merger = new AnalysisMerger(report, buffer, this);
			// Start new reader in separate pool thread (manage by thread pool
			// executor)
			AnalysisWorker worker = new AnalysisWorker(processConfiguration, this, this, buffer);

			dataAnalysisService.execute(worker);

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

	public GeneLibrary getGeneLibrary() {
		return geneLibrary;
	}

	public ProcessConfiguration getProcessConfiguration() {
		return processConfiguration;
	}

	public void fileUploaded(ModelFileStored modelFile) {

		LOG.debug("New resource assigned to this analysis : " + modelFile.getName());

		LOG.debug("viewConfigurations : " + viewConfiguration + "  processConfiguration : " + processConfiguration);
		boolean allFilesUploaded = viewConfiguration.getSelectedDataFiles().size() == processConfiguration.getSelectedDataFiles().size() && viewConfiguration.getSelectedLibraries().size() == processConfiguration.getSelectedLibraries().size();

		if (allFilesUploaded) {
			LOG.debug("All files successfully uploads/downloads, request to change status");
			setStatus(AnalysisStatus.READY_FOR_PROCESSING);
		}

	}

	@Override
	public void analysisStarted() {
		synchronized (this) {
			setStatus(AnalysisStatus.RUNNING);
		}
	}

	@Override
	public void analysisError() {
		synchronized (this) {
			setStatus(AnalysisStatus.RUNNING_ERROR);
		}
	}

	@Override
	public void analysisPreProcessStarted() {
		synchronized (this) {
			setStatus(AnalysisStatus.PREPROCESSING);
		}
	}

	@Override
	public void readProgress(int lineRead, int percent) {
		synchronized (this) {
			progress = percent;
		}
	}

	@Override
	public void readDone(int totalCount) {
		LOG.debug("Reading done on associated data file. Number of lines read : " + totalCount);
		synchronized (this) {
			progress = 100;
		}
	}

	@Override
	public void analysisDone(final long dateFinished) {
		synchronized (this) {
			LOG.debug("Analysis done request to change status accordingly");
			progress = 100;
			completionDate = dateFinished;
			setStatus(AnalysisStatus.DONE);
			AnalysisManager.getInstance().analyseFinished(this);
		}
	}

	@Override
	public String getAnalysisID() {
		return this.id;
	}

	@Override
	public void librariesExtracted(GeneLibrary library) {
		LOG.warn("Lib extracted, add to report");
		report.setLibrary(library);
		this.geneLibrary = library;
	}

	@Override
	public void analysisResultsReceived(ChunkResult result) {
		synchronized (this) {
			if (merger == null) {
				LOG.warn("Merger is null, it seems results were received for analysis ID " + id + "  but merger has not been initialized yet, or has been destroyed");
				return;
			}
			// Start processing in a new thread
			merger.processChunckResult(result);
		}
	}

	/*
	 * Resources management
	 */
	public synchronized void assignDistantResource(IDistantResource resource) {
		boolean found = false;
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

	/*
	 * Resources management
	 */
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

	public ChunkQueueBuffer getBuffer() {
		return buffer;
	}

	public void stopAnalyse() {
		merger = null;
		// Shutdown worker thread.
		dataAnalysisService.shutdownNow();
		setStatus(AnalysisStatus.DONE);
		for (int i = assignedResources.size() - 1; i >= 0; i--) {
			if (buffer != null) {
				buffer.releaseChunks(assignedResources.get(i).getID());
			}
			// Notify client to stop current action
			assignedResources.get(i).requestStopCurrent();
		}
		assignedResources.clear();

	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public List<IDistantResource> getAssignedResources() {
		return assignedResources;
	}

	public void setAssignedResources(List<IDistantResource> assignedResources) {
		this.assignedResources = assignedResources;
	}

	public List<ViewFile> getAdditionalAnalyses() {
		return additionalAnalyses;
	}

	public void setAdditionalAnalyses(List<ViewFile> additionalAnalyses) {
		this.additionalAnalyses = additionalAnalyses;
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
}
