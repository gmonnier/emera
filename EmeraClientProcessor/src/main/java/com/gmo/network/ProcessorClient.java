package com.gmo.network;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.ClientContextManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.network.network_Client.BaseClient;
import com.gmo.network.network_Client.NetworkEvent;
import com.gmo.network.network_Client.NetworkListener;
import com.gmo.process.ProcessManager;
import com.gmo.sharedobjects.client.ClientStatus;
import com.gmo.sharedobjects.model.data.ChunkResult;
import com.gmo.sharedobjects.model.data.DataChunk;
import com.gmo.sharedobjects.model.genelibrary.GeneLibrary;
import com.gmo.sharedobjects.model.processconfiguration.PartialProcessConfiguration;
import com.gmo.sharedobjects.util.StringSerializationException;
import com.gmo.ui.IUIInterface;
import com.gmo.ui.Info;

public class ProcessorClient implements NetworkListener {

	private BaseClient client;

	private boolean connected = false;

	private IUIInterface uiInterface;

	// Use to attempt connection again when server shutdown
	private CheckConnectionThread threadConnexion;

	private static Logger LOG = Log4JLogger.logger;

	public ProcessorClient() {

	}

	public void launchConnexionThread() {
		LOG.debug("Start connection check thread to server");
		if (uiInterface != null) {
			uiInterface.clearDisplayedInfoChanged();
			uiInterface.addDisplayedInfoChanged(new Info("Not connected", "connection thread running..."));
		}
		if (threadConnexion != null && threadConnexion.isAlive()) {
			threadConnexion.stop();
		}
		threadConnexion = new CheckConnectionThread(this);
		threadConnexion.start();
	}

	/**
	 * Initialize connection with server
	 * 
	 * @return true if connection initialized successfully, false otherwise.
	 */
	public boolean initializeConnexion(boolean silencious) {

		String ipServer = ClientContextManager.getInstance().getConfig().getServerAdress();
		int connectionPort = ClientContextManager.getInstance().getConfig().getConnectionPort();

		// Start listening on socket
		try {

			LOG.debug("Try to init connection with " + ipServer + "   on port " + connectionPort);
			client = new BaseClient(ipServer, connectionPort);
			client.addNetworkListener(this);
			ProcessManager.getInstance().setNetworkInterface(this);
			if (uiInterface != null) {
				uiInterface.clearDisplayedInfoChanged();
				uiInterface.clientConnectionChanged(true, new Info("Server", ipServer));
			}
			connected = true;
			LOG.debug("Connection established successfully");
		} catch (IOException e) {
			LOG.debug("Unable to connect to server.");
			connected = false;
			if (uiInterface != null) {
				uiInterface.clientConnectionChanged(false, null);
			}
		}

		return connected;
	}

	/**
	 * If connexion has been disconnected abnormaly
	 */
	@Override
	public void networkDisconnected(String string) {
		LOG.debug("Connection to server ended abnormally");
		connected = false;
		if (uiInterface != null) {
			uiInterface.clientConnectionChanged(false, null);
		}
		ProcessManager.getInstance().initParameters();
		ProcessManager.getInstance().setNetworkInterface(null);
		launchConnexionThread();
	}

	public void notifyStatusChanged(ClientStatus clientStatus) {
		LOG.debug("Notify server status updated to  " + clientStatus);
		sendToServer("STATUS<#>" + clientStatus);
	}

	public void notifyNewResults(String analyseID, ChunkResult results) {
		StringBuilder sb = new StringBuilder();
		sb.append("DATA_CHUNK_RESULTS<#>");
		sb.append(analyseID);
		sb.append("<#>");
		sb.append(results.getObjectAsString());
		String message = sb.toString();
		
		LOG.debug(message);
		sendToServer(message);
	}

	/**
	 * Listening message from server
	 */
	@Override
	public void networkEvent(NetworkEvent evt) {

		String msg = evt.getString();
		LOG.debug("RCV->" + msg);
		/*
		 * LIBRARIES<#>AnalyseID<#>DataChunk
		 */
		if (msg.startsWith("LIBRARIES")) {

			ProcessManager.getInstance().setClientStatus(ClientStatus.RETRIEVING_LIBS);
			String[] splitted = msg.split("<#>");

			try {
				LOG.debug("Server sent libraries for analysis calculations -> analyseID : " + splitted[1]);
				GeneLibrary lib = new GeneLibrary();
				lib.convertStringToObject(splitted[2]);
				ProcessManager.getInstance().setCurrentLibrary(lib);

				ProcessManager.getInstance().setClientStatus(ClientStatus.WAITING_FOR_DATA);
			} catch (StringSerializationException e) {
				LOG.error("Unable to deserialize the String into Library");
				ProcessManager.getInstance().setClientStatus(ClientStatus.ERROR);
			} catch (Throwable t) {
				LOG.error("Unable to deserialize the String into Library ", t);
				ProcessManager.getInstance().setClientStatus(ClientStatus.ERROR);
			}
		}

		else if (msg.startsWith("ANALYSE_ASSIGN")) {
			LOG.debug("Request to be assigned to a new analysis");
			String[] splitted = msg.split("<#>");
			try {
				ProcessManager.getInstance().setCurrentAnalysisID(splitted[1]);
				sendToServer("REQ_PARAMETERS");
			} catch (Throwable t) {
				LOG.error("Unable to deserializerequest to assign to new analysis ", t);
				ProcessManager.getInstance().setClientStatus(ClientStatus.ERROR);
			}
		}

		else if (msg.startsWith("ANALYSE_UNASSIGN")) {
			LOG.debug("Request to clear and init analysis properties");
			try {
				ProcessManager.getInstance().initParameters();
			} catch (Throwable t) {
				LOG.error("Unable to deserializerequest to unasign the resource to current analysis ", t);
				ProcessManager.getInstance().setClientStatus(ClientStatus.ERROR);
			}
		}
		/*
		 * PARAMETERS<#>AnalyseID<#>PartialProcessConfiguration
		 */
		else if (msg.startsWith("PARAMETERS")) {
			LOG.debug("Server sent parameters for current analysis calculations");
			ProcessManager.getInstance().setClientStatus(ClientStatus.RETRIEVING_PARAMETERS);
			PartialProcessConfiguration processConfig = new PartialProcessConfiguration();
			String[] splitted = msg.split("<#>");
			try {
				processConfig.convertStringToObject(splitted[2]);
				ProcessManager.getInstance().setProcessConfiguration(processConfig);
				sendToServer("REQ_LIBRARIES");
			} catch (StringSerializationException e) {
				LOG.error("Unable to deserialize the String into processConfig ", e);
				ProcessManager.getInstance().setClientStatus(ClientStatus.ERROR);
			} catch (Throwable t) {
				LOG.error("Unable to deserialize the String into processConfig ", t);
				ProcessManager.getInstance().setClientStatus(ClientStatus.ERROR);
			}
		}
		/*
		 * DATACHUNK<#>DataChunk
		 */
		else if (msg.startsWith("DATACHUNK")) {
			LOG.debug("New Data Chunk received");
			ProcessManager.getInstance().setClientStatus(ClientStatus.RETRIEVING_DATA);
			String[] splitted = msg.split("<#>");
			try {
				DataChunk chunk = new DataChunk();
				chunk.convertStringToObject(splitted[1]);
				ProcessManager.getInstance().requestProcessingChunk(chunk);
			} catch (StringSerializationException e) {
				LOG.error("Unable to deserialize the String into Chunk object ", e);
				ProcessManager.getInstance().setClientStatus(ClientStatus.ERROR);
			} catch (Throwable t) {
				LOG.error("Unable to deserialize the String into Chunk ", t);
				ProcessManager.getInstance().setClientStatus(ClientStatus.ERROR);
			}
		}

		else if (msg.startsWith("STOP_ACTION<#>")) {
			ProcessManager.getInstance().stopCurrentAction();
		}
		
		/*
		 * Server explicitely required the current status.
		 */
		else if (msg.startsWith("REQ_STATUS<#>")) {
			notifyStatusChanged(ProcessManager.getInstance().getClientStatus());
		}

		else if (msg.equals("/kill")) {
			LOG.error("kill message received.");
			connected = false;
			if (uiInterface != null) {
				uiInterface.clientConnectionChanged(false, null);
			}
			ProcessManager.getInstance().setNetworkInterface(null);
			ProcessManager.getInstance().initParameters();
			
			// stop the client Thread.
			shutdown();
			launchConnexionThread();
		}

		else if (msg.equals("/refused")) {
			LOG.error("Connection refused by the server - exit application");
			System.exit(0);
		}
	}

	/**
	 * Request to send a message to the server
	 * 
	 * @param request
	 *            command pour le serveur
	 */
	public void sendToServer(String request) {
		if (connected) {
			client.sendString(request);
		}
	}

	public void shutdown() {
		if (client != null) {
			client.stop();
		}
	}

	public void setUiInterface(IUIInterface uiInterface) {
		this.uiInterface = uiInterface;
	}

}
