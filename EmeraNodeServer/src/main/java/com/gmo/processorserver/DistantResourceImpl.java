package com.gmo.processorserver;

import org.apache.logging.log4j.Logger;

import com.gmo.client.ClientStatus;
import com.gmo.coreprocessing.Analysis;
import com.gmo.coreprocessing.AnalysisManager;
import com.gmo.coreprocessing.fastQReaderDispatcher.ChunkQueueBuffer;
import com.gmo.logger.Log4JLogger;
import com.gmo.model.analysis.NoSuchAnalysisException;
import com.gmo.model.data.DataChunk;
import com.gmo.model.genelibrary.GeneLibrary;
import com.gmo.model.processconfig.ProcessConfiguration;
import com.gmo.network.location.ClientLocation;
import com.gmo.network.location.LocationLookup;
import com.gmo.network.network_Server.ExtendedSocket;

public class DistantResourceImpl implements IDistantResource {

	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	private ExtendedSocket clientSock;

	private ClientStatus clientStatus;

	private String analyseID;

	private ClientLocation clientLocation;
	
	public DistantResourceImpl(ExtendedSocket clientSock) {
		this.clientSock = clientSock;
		this.clientStatus = ClientStatus.IDLE;
		this.analyseID = "";
		
		clientLocation = LocationLookup.getLocation(clientSock.getInetAdress());
		
	}

	@Override
	public String getID() {
		return clientSock.getIP();
	}

	@Override
	public ClientStatus getClientStatus() {
		return clientStatus;
	}

	@Override
	public String getName() {
		return clientSock.getName();
	}

	@Override
	public void setClientStatus(ClientStatus clientStatus) {
		if (this.clientStatus == clientStatus) {
			return;
		}

		this.clientStatus = clientStatus;
		
		if(clientStatus == ClientStatus.ERROR) {
			LOG.debug("Distant reosurce put in error state id = " + getID());
		}

		if (clientStatus == ClientStatus.WAITING_FOR_DATA) {
			Analysis analysis;
			try {
				analysis = AnalysisManager.getInstance().getRunningAnalysis(analyseID);
				ChunkQueueBuffer buffer = analysis.getBuffer();

				// Wait to get next available chunk in the buffer
				DataChunk chunk = buffer.takeNextChunckForProcess(getID());
				if (chunk != null) {
					sendDataChunk(chunk);
				} else {
					LOG.debug("Unassign Distant reource " + getID() + " from analyse " + analyseID);
					clientSock.printOutput("ANALYSE_UNASSIGN<#>" + analyseID);
				}
			} catch (NoSuchAnalysisException e) {
				LOG.error("No analysis found with ID : " + analyseID);
			}
		}
		
		else if (clientStatus == ClientStatus.IDLE) {
			Analysis analysis;
			try {
				analysis = AnalysisManager.getInstance().getRunningAnalysis(analyseID);
				analysis.removeDistantResource(this.getID());
			} catch (NoSuchAnalysisException e) {
				LOG.error("No analysis found with ID " + analyseID + " , unable to unassign current resource with ID " + getID());
			}
		}

	}

	@Override
	public void sendDataChunk(DataChunk chunk) {
		LOG.debug("Sending chunck to " + getID() + "   chunckID " + chunk.getChunkID());
		clientSock.printOutput("DATACHUNK<#>" + chunk.getObjectAsString());
		LOG.debug("chunck sent to " + getID());
	}

	@Override
	public void sendAnalysisParameters() {
		if (!analyseID.isEmpty()) {
			ProcessConfiguration config;
			try {
				LOG.debug("Sending parameters to " + getID());
				config = AnalysisManager.getInstance().getRunningAnalysis(analyseID).getProcessConfiguration();
				clientSock.printOutput("PARAMETERS<#>" + analyseID + "<#>" + config.getObjectAsString());
				LOG.debug("Parameters sent to " + getID());
			} catch (NoSuchAnalysisException e) {
				LOG.error("Unable to retrieve analysis with ID = " + analyseID + "  , abort sending parameters");
			}
		} else {
			LOG.error("Empty analyseID associated with distant resource. Abort sending parameters.");
		}
	}

	@Override
	public void sendLibraries() {
		if (!analyseID.isEmpty()) {
			GeneLibrary libraries;
			try {
				LOG.debug("Sending libraries to " + getID());
				libraries = AnalysisManager.getInstance().getRunningAnalysis(analyseID).getGeneLibrary();
				if (libraries == null) {
					LOG.error("No libraries yet initialized for this analysis. Abort sending libraries.");
					return;
				}
				clientSock.printOutput("LIBRARIES<#>" + analyseID + "<#>" + libraries.getObjectAsString());
				LOG.debug("Libraries sent to " + getID());
			} catch (NoSuchAnalysisException e) {
				LOG.error("Unable to retrieve analysis with ID = " + analyseID + "  , abort sending libraries");
			}
		} else {
			LOG.error("Empty analyseID associated with distant resource. Abort sending libraries.");
		}
	}

	@Override
	public void requestAssignmentToAnalysis(String analyseId) {
		this.analyseID = analyseId;
		Analysis analysis;
		try {
			analysis = AnalysisManager.getInstance().getRunningAnalysis(analyseId);
			analysis.assignDistantResource(this);
			clientSock.printOutput("ANALYSE_ASSIGN<#>" + analyseId);
		} catch (NoSuchAnalysisException e) {
			LOG.error("No analysis found with ID " + analyseId + " , unable to assign resource to given analysis");
		}
	}

	public String getAnalyseID() {
		return analyseID;
	}

	public void setAnalyseID(String analyseID) {
		this.analyseID = analyseID;
	}

	@Override
	public void requestStopCurrent() {
		clientSock.printOutput("STOP_ACTION<#>");
	}

	@Override
	public ClientLocation getLocation() {
		return clientLocation;
	}

	@Override
	public void requestStatus() {
		clientSock.printOutput("REQ_STATUS<#>");
	}

}
