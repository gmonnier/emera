package com.gmo.externalInterfaces.rmiserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.Logger;

import com.amazonaws.services.ec2.model.Instance;
import com.gmo.basespaceService.model.FastQFile;
import com.gmo.commonconfiguration.NetworkTopologyManager;
import com.gmo.configuration.StorageConfigurationManager;
import com.gmo.coreprocessing.Analysis;
import com.gmo.coreprocessing.AnalysisManager;
import com.gmo.coreprocessing.AnalysisOccurence;
import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.generated.configuration.networktopology.RmiInterface;
import com.gmo.logger.Log4JLogger;
import com.gmo.modelconverters.AnalysisConverter;
import com.gmo.modelconverters.FileStoredConverter;
import com.gmo.network.location.ClientLocation;
import com.gmo.network.rmiutil.RMIInputStream;
import com.gmo.network.rmiutil.RMIInputStreamImpl;
import com.gmo.network.rmiutil.RMIOutputStream;
import com.gmo.network.rmiutil.RMIOutputStreamImpl;
import com.gmo.processorNode.interfaces.INodeServerControl;
import com.gmo.processorNode.viewmodel.BSDownloadInfo;
import com.gmo.processorNode.viewmodel.BSSingleDownloadInfo;
import com.gmo.processorNode.viewmodel.OutputFileType;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.ViewNodePollingInfo;
import com.gmo.processorNode.viewmodel.analyses.preprocessing.ViewPreProcessingConfiguration;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.processorNode.viewmodel.network.ViewAWSInstance;
import com.gmo.processorNode.viewmodel.network.ViewDistantResource;
import com.gmo.processorNode.viewmodel.network.ViewNodeNetworkConfig;
import com.gmo.processorserver.IDistantResource;
import com.gmo.processorserver.IResource;
import com.gmo.processorserver.ProcessorServerManager;
import com.gmo.reports.additionnalAnalyses.occurenceIncrease.OccurencesIncreaseAnalysis;
import com.gmo.sharedobjects.client.ClientStatus;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.analysis.NoSuchAnalysisException;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.inputs.ModelFileStored;
import com.gmo.sharedobjects.model.reports.Report;

import awsinterfaceManager.AWSEC2InterfaceManager;

public class NodeServerImpl implements INodeServerControl {

	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Log4JLogger.logger;

	public NodeServerImpl() throws RemoteException {
		super();
	}

	@Override
	public void requestNodeProcessorClientRemove(String clientID) throws RemoteException {
		LOG.info("[RMI-MODULE] Request to remove distant resource with ID " + clientID);
		ProcessorServerManager.getInstance().requestClientRemove(clientID);
	}

	@Override
	public List<ViewFile> getListStoredData() throws RemoteException {
		List<ModelFileStored> input = StorageConfigurationManager.getInstance().getListStoredData();
		List<ViewFile> listView = new ArrayList<ViewFile>();
		FileStoredConverter viewBuilder = new FileStoredConverter();

		for (Iterator<ModelFileStored> iterator = input.iterator(); iterator.hasNext();) {
			ModelFileStored inputFile = (ModelFileStored) iterator.next();
			listView.add(viewBuilder.buildViewModelObject(inputFile));
		}
		return listView;
	}

	@Override
	public List<ViewFile> getListStoredLibraries() throws RemoteException {
		List<ModelFileStored> input = StorageConfigurationManager.getInstance().getListStoredLibraries();
		List<ViewFile> listView = new ArrayList<ViewFile>();
		FileStoredConverter viewBuilder = new FileStoredConverter();

		for (Iterator<ModelFileStored> iterator = input.iterator(); iterator.hasNext();) {
			ModelFileStored inputFile = (ModelFileStored) iterator.next();
			listView.add(viewBuilder.buildViewModelObject(inputFile));
		}
		return listView;
	}

	@Override
	public ViewNodePollingInfo getViewNodePollingInfo(String userID) throws RemoteException {

		IResource server = ProcessorServerManager.getInstance().getServerResource();
		ViewDistantResource nodeServer = new ViewDistantResource(server.getID(), server.getName(), null, server.getLocation());

		Map<String, IDistantResource> listRes = ProcessorServerManager.getInstance().getMapResourcesConnected();
		List<ViewDistantResource> resources = new ArrayList<>(listRes.size());

		List<Instance> listInstances = AWSEC2InterfaceManager.getInstance().getAllInstances();
		List<ViewAWSInstance> awsInstances = new ArrayList<>(listInstances.size());

		if (userID.equals("guest")) {
			// Stub resources
			ViewDistantResource res = new ViewDistantResource("ID 1", "Resource name", ClientStatus.IDLE.toString(), ClientLocation.stubLocation("163.168.80.10", "Irvine", 33.6694, -117.8231));
			resources.add(res);
			ViewDistantResource res2 = new ViewDistantResource("ID 2", "Resource name 2", ClientStatus.IDLE.toString(), ClientLocation.stubLocation("184.168.85.3", "Zurich", 47.3667, 8.5500));
			resources.add(res2);
			ViewDistantResource res3 = new ViewDistantResource("ID 3", "Resource name 3", ClientStatus.PROCESSING.toString(), ClientLocation.stubLocation("183.143.7.31", "Farnay", 45.4942, 4.5986));
			resources.add(res3);
			ViewDistantResource res4 = new ViewDistantResource("ID 4", "Resource name 4", ClientStatus.PROCESSING.toString(),
					ClientLocation.stubLocation("145.143.7.31", "Baltimore", 39.2833, -76.6167));
			resources.add(res4);
			ViewDistantResource res5 = new ViewDistantResource("ID 5", "Resource name 5", ClientStatus.RETRIEVING_DATA.toString(),
					ClientLocation.stubLocation("145.143.7.31", "San Francisco", 37.7833, -122.4167));
			resources.add(res5);
		}

		Iterator<Map.Entry<String, IDistantResource>> it = listRes.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, IDistantResource> pair = (Map.Entry<String, IDistantResource>) it.next();
			IDistantResource distResource = pair.getValue();
			ViewDistantResource res = new ViewDistantResource(distResource.getID(), distResource.getName(), distResource.getClientStatus().toString(), distResource.getLocation());
			resources.add(res);
		}

		for (int i = 0; i < listInstances.size(); i++) {
			Instance inst = listInstances.get(i);
			awsInstances.add(new ViewAWSInstance(inst.getInstanceId(), inst.getPrivateIpAddress(), inst.getPublicIpAddress(), inst.getState().getName(), inst.getInstanceType()));
		}

		ViewNodeNetworkConfig viewNetConfig = new ViewNodeNetworkConfig(nodeServer, resources, awsInstances);

		List<Analysis> usersAnalysis = AnalysisManager.getInstance().getUserRunningAnalysis(userID);
		List<ViewAnalysis> usersViewAnalysis = new ArrayList<ViewAnalysis>();

		AnalysisConverter analysisConverter = new AnalysisConverter();
		for (Iterator<Analysis> iterator = usersAnalysis.iterator(); iterator.hasNext();) {
			Analysis analysis = iterator.next();
			usersViewAnalysis.add(analysisConverter.buildViewModelObject(analysis));
		}
		
		if (userID.equals("guest")) {
			ViewAnalysis analysisTest = new ViewAnalysis();
			analysisTest.setId(UUID.randomUUID().toString());
			analysisTest.setProgress(32);
			analysisTest.setStatus(AnalysisStatus.RETRIEVE_FILES);
			analysisTest.setUserid("guest");
			analysisTest.setLaunchDate(new Date().getTime());
			
			BSDownloadInfo downloadInfo = new BSDownloadInfo();
			FastQFile testDown = new FastQFile("fatsq-test");
			testDown.setName("Test stub File");
			testDown.setPath("path to file");
			testDown.setSize(1234567l);
			testDown.setDateCreated(new Date());
			downloadInfo.getDownloadingFiles().add(new BSSingleDownloadInfo(testDown, 46));
			downloadInfo.setIsdownloading(true);
			analysisTest.setDownloadInfo(downloadInfo);
			usersViewAnalysis.add(analysisTest);
		}

		return new ViewNodePollingInfo(viewNetConfig, usersViewAnalysis);
	}

	@Override
	public void uploadToNodeServerDone(InputType inputType, String analyseid, String fileName) throws RemoteException {

		LOG.info("[RMI-MODULE] Upload to Node Server completed for " + fileName + " of type " + inputType);
		StorageConfigurationManager.getInstance().updateModel();
		ModelFileStored modelUploaded = StorageConfigurationManager.getInstance().getWithPath(inputType, fileName);

		try {
			AnalysisManager.getInstance().getRunningAnalysis(analyseid).getFileCollector().fileCollected(modelUploaded, inputType);
		} catch (NoSuchAnalysisException e) {
			LOG.error("Unable to add " + modelUploaded + " to data of " + analyseid);
		}

	}

	@Override
	public void stopAllAnalyses(String userID) throws RemoteException {
		LOG.debug("[RMI-MODULE] Request to stop all currently running analyses belonging to userID : " + userID);
		AnalysisManager.getInstance().stopAllAnalyses(userID);
	}

	@Override
	public void requestRunningAnalysisChangeStatus(String id, AnalysisStatus newStatus) throws RemoteException {
		LOG.error("[RMI-MODULE] Request analysis " + id + " status change to " + newStatus);
		try {
			AnalysisManager.getInstance().getRunningAnalysis(id).setStatus(newStatus);
		} catch (NoSuchAnalysisException e) {
			LOG.error("Unable to change analysis staus - analysis not found - id = " + id);
		}

	}

	@Override
	public String enqueueNewAnalysis(ViewCreateProcessConfiguration viewProcessConfig, String userID, String bsuserID, String bsuserSecret, String bsuserToken, LocationType resultLocType,
			String resultLocation) throws RemoteException {
		LOG.info("[RMI-MODULE] Request to enqueue new occurence analysis");
		return AnalysisManager.getInstance().enqueueNewOccurenceAnalysis(viewProcessConfig, userID, bsuserID, bsuserSecret, bsuserToken, resultLocType, resultLocation);
	}
	
	@Override
	public String enqueueNewPreprocessingAnalysis(ViewPreProcessingConfiguration viewProcessConfig, String userID, String bsuserID, String bsuserSecret, String bsuserToken) throws RemoteException {
		LOG.info("[RMI-MODULE] Request to enqueue new split analysis");
		return AnalysisManager.getInstance().enqueueNewSplitAnalysis(viewProcessConfig, userID, bsuserID, bsuserSecret, bsuserToken);
	}

	// ---------- File Transfert ---------

	@Override
	public OutputStream getOutputStream(String fileName, InputType inputType) throws IOException {
		LOG.warn("Request outputStream for file upload with type " + inputType + "  and fileName=" + fileName);
		String uploadedFileLocationRoot;
		switch (inputType) {
		case DATA: {
			uploadedFileLocationRoot = StorageConfigurationManager.getInstance().getConfig().getDataFilesRoot();
			break;
		}
		case LIBRARY: {
			uploadedFileLocationRoot = StorageConfigurationManager.getInstance().getConfig().getLibrariesFilesRoot();
			break;
		}
		default: {
			uploadedFileLocationRoot = StorageConfigurationManager.getInstance().getConfig().getDataFilesRoot();
		}
		}

		File uploadDest = new File(uploadedFileLocationRoot, fileName);
		if (uploadDest.exists()) {
			LOG.warn(uploadDest.getAbsolutePath() + "  already exists. Abort");
			return null;
		} else {
			// Create parent directory if not existing yet
			if (!uploadDest.getParentFile().isDirectory()) {
				uploadDest.getParentFile().mkdirs();
			}
			uploadDest.createNewFile();
		}
		LOG.warn("Create RMI OutputStream for " + uploadDest.getAbsolutePath());
		RmiInterface transfertPort = NetworkTopologyManager.getInstance().getByRmiInterfaceName("IFileTransfer");
		return new RMIOutputStream(new RMIOutputStreamImpl(new FileOutputStream(uploadDest), transfertPort.getExportPort()));
	}

	@Override
	public InputStream getInputStream(File f) throws IOException {
		RmiInterface transfertPort = NetworkTopologyManager.getInstance().getByRmiInterfaceName("IFileTransfer");
		return new RMIInputStream(new RMIInputStreamImpl(new FileInputStream(f), transfertPort.getExportPort()));
	}

	@Override
	public void requestOccurencesIncreaseAnalysis(Report refReport, Report compReport, OutputFileType outputFileType) throws RemoteException {
		LOG.warn("TODO: Pass S3/Files Reference, not the complete report!");
		new OccurencesIncreaseAnalysis(refReport, compReport, outputFileType);
	}

}
