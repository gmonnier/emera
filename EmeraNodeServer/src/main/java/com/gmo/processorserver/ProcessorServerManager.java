package com.gmo.processorserver;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.gmo.commonconfiguration.NetworkTopologyManager;
import com.gmo.coreprocessing.Analysis;
import com.gmo.coreprocessing.AnalysisManager;
import com.gmo.coreprocessing.AnalysisOccurence;
import com.gmo.logger.Log4JLogger;
import com.gmo.network.network_Server.BaseServer;
import com.gmo.network.network_Server.ExtendedSocket;
import com.gmo.network.network_Server.INetworkServerInfoListener;
import com.gmo.network.network_Server.ServerEvent;
import com.gmo.network.network_Server.ServerListener;
import com.gmo.sharedobjects.client.ClientStatus;
import com.gmo.sharedobjects.model.analysis.AnalysisStatus;
import com.gmo.sharedobjects.model.data.ChunkResult;
import com.gmo.sharedobjects.util.StringSerializationException;

public class ProcessorServerManager implements ServerListener, INetworkServerInfoListener {

	private static ProcessorServerManager instance;

	// Lower level connection server
	private BaseServer server;

	private ErrorDispatcher errDispatcher;

	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	// List of resources connected to the server and available for processing
	private Map<String, IDistantResource> mapResourcesConnected;
	private Map<String, IDistantResource> mapResourcesAvailable;
	
	private final IResource serverResource;

	private ProcessorServerManager() {
		
		int listeningPort = NetworkTopologyManager.getInstance().getConfig().getTcpNodeServerPort();

		LOG.info("Create Processor server - Listening port on " + listeningPort);

		mapResourcesConnected = Collections.synchronizedMap(new HashMap<String, IDistantResource>());
		mapResourcesAvailable = Collections.synchronizedMap(new HashMap<String, IDistantResource>());

		server = new BaseServer(listeningPort, this);
		server.addServerListener(this);
		serverResource = new NodeServerResource(server.getServerSocket());

		errDispatcher = new ErrorDispatcher();

	}

	public static synchronized ProcessorServerManager getInstance() {
		if (instance == null) {
			instance = new ProcessorServerManager();
		}
		return instance;
	}

	public void sendToAllClient(String msg) {
		for (int i = 0; i < server.getListeClients().size(); i++) {
			ExtendedSocket client = server.getListeClients().get(i);
			client.printOutput(msg);
		}
	}

	@Override
	public void initialisationError(String string, IOException ioe) {
		LOG.error("Error while initializing the server", ioe);
	}

	public void requestClientRemove(String clientID) {
		LOG.info("Request to remove client with ID : " + clientID);
		server.killClient(clientID);
	}

	@Override
	public void clientRemoved(ExtendedSocket clientSock) {
		LOG.info("Client removed : " + clientSock.getIP());
		mapResourcesConnected.remove(clientSock.getIP());
		mapResourcesAvailable.remove(clientSock.getIP());

		List<Analysis> listAnalyses = AnalysisManager.getInstance().getAllRunningAnalysis();
		for (Analysis runningAnalysis : listAnalyses) {
			runningAnalysis.removeDistantResource(clientSock.getIP());
		}
	}

	@Override
	public void clientAdded(ExtendedSocket newSock) {
		IDistantResource ires = new DistantResourceImpl(newSock);
		mapResourcesConnected.put(ires.getID(), ires);
		mapResourcesAvailable.put(ires.getID(), ires);
		ires.requestStatus();

		List<Analysis> analysisList = AnalysisManager.getInstance().getAllRunningAnalysis();
		for (Iterator<Analysis> iterator = analysisList.iterator(); iterator.hasNext();) {
			AnalysisOccurence analysis = (AnalysisOccurence) iterator.next();
			if (analysis.getStatus() == AnalysisStatus.RUNNING) {
				ires.requestAssignmentToAnalysis(analysis.getId());
				mapResourcesAvailable.remove(ires.getID());
			}
		}

	}

	public void requestAllAvailableResources(Analysis runningAnalysis) {
		Iterator<Map.Entry<String, IDistantResource>> it = mapResourcesConnected.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, IDistantResource> pair = (Map.Entry<String, IDistantResource>) it.next();
			IDistantResource ires = pair.getValue();
			ires.requestAssignmentToAnalysis(runningAnalysis.getId());
			mapResourcesAvailable.remove(pair.getKey());
		}
	}

	@Override
	public void serverEvent(ServerEvent evt) {

		IDistantResource distRes = mapResourcesConnected.get(evt.getSender().getIP());
		if (distRes == null) {
			LOG.error("No resource connectedcorresponding to IP " + evt.getSender().getIP() + "  for message " + evt.getString().substring(0, Math.min(100, evt.getString().length())));
			return;
		}

		if (evt.getString().startsWith("REQ_PARAMETERS")) {
			distRes.sendAnalysisParameters();
		}

		else if (evt.getString().startsWith("REQ_LIBRARIES")) {
			distRes.sendLibraries();
		}

		// DATA_CHUNK_RESULTS<#>analyseID<#>ChunkResult
		else if (evt.getString().startsWith("DATA_CHUNK_RESULTS")) {
			String analyseID = "";
			try {
				String[] splitted = evt.getString().split("<#>");
				analyseID = splitted[1];
				ChunkResult result = new ChunkResult();
				result.convertStringToObject(splitted[2]);

				LOG.info("Received new data chunk result for chunkID = " + result.getChunkId() + " from " + distRes.getID());

				Analysis analyse = AnalysisManager.getInstance().getRunningAnalysis(analyseID);
				analyse.analysisResultsReceived(result);

			} catch (StringSerializationException e) {
				errDispatcher.unableToRetrieveDataChunkResults(evt.getSender().getIP(), analyseID, e);
				LOG.warn("Full data string received from socket : " + evt.getString());
			} catch (Throwable t) {
				errDispatcher.unableToRetrieveDataChunkResults(evt.getSender().getIP(), t);
			}

		}

		else if (evt.getString().startsWith("STATUS")) {
			try {
				String[] splitted = evt.getString().split("<#>");
				ClientStatus status = ClientStatus.valueOf(splitted[1]);
				distRes.setClientStatus(status);
				if (status == ClientStatus.IDLE) {
					mapResourcesAvailable.put(distRes.getID(), distRes);
				}
				if (status == ClientStatus.ERROR) {
					mapResourcesAvailable.remove(distRes.getID());
				}
			} catch (Throwable t) {
				errDispatcher.unableToRetrieveClientStatus(evt.getSender().getIP());
			}
		}

	}

	public Map<String, IDistantResource> getMapResourcesConnected() {
		return mapResourcesConnected;
	}

	public Map<String, IDistantResource> getListResourcesAvailable() {
		return mapResourcesAvailable;
	}

	public IResource getServerResource() {
		return serverResource;
	}

}
