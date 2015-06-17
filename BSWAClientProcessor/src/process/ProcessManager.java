package process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import logger.Log4JLogger;
import model.data.ChunkResult;
import model.data.DataChunk;
import model.genelibrary.GeneLibrary;
import model.parameters.PartialProcessConfiguration;
import network.ProcessorClient;

import org.apache.logging.log4j.Logger;

import ui.IUIInterface;
import ui.Info;
import client.ClientStatus;

public class ProcessManager implements IWorkerProgress {
	
	private static class ProcessManagerHolder {
	    private static final ProcessManager instance = new ProcessManager();
	}

	private final static int SUBCHUNK_SIZE = 1000;
	
	public static synchronized ProcessManager getInstance() {
		return ProcessManagerHolder.instance;
	}

	// Use all logical threads assigned to the jvm, minus one (to let some
	// resources to the computer
	private final static int CORES = Runtime.getRuntime().availableProcessors() > 1 ? Runtime.getRuntime().availableProcessors() - 1 : 1;

	private static final ExecutorService chunkAnalyserService = Executors.newFixedThreadPool(CORES);

	// The current state of the client
	private ClientStatus clientStatus;

	private GeneLibrary currentLibrary;

	private PartialProcessConfiguration processConfiguration;

	private ProcessorClient networkInterface;

	private String currentAnalysisID;

	private int analysedChuncksCount;

	private long analysedDataCount;

	private IUIInterface uiInterface;

	private int remainingSubChunk;

	private ChunkResult finalResult;

	// private ChunkResultMerger currentMerger;
	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	public void initParameters() {
		processConfiguration = null;
		currentLibrary = null;
		currentAnalysisID = "";
		analysedChuncksCount = 0;
		remainingSubChunk = 0;
		analysedDataCount = 0;
		setClientStatus(ClientStatus.IDLE);
	}

	private ProcessManager() {
		clientStatus = ClientStatus.IDLE;
	}

	public void setUIInterface(IUIInterface uiInterface) {
		this.uiInterface = uiInterface;
		uiInterface.clientStatusChanged(clientStatus);
	}

	public ClientStatus getClientStatus() {
		return clientStatus;
	}

	public void setClientStatus(ClientStatus clientStatus) {

		LOG.debug("Client status request to be changed from " + this.clientStatus + " to " + clientStatus);
		
		if (this.clientStatus == clientStatus) {
			return;
		}

		if (networkInterface != null) {
			networkInterface.notifyStatusChanged(clientStatus);
		}

		if (uiInterface != null) {
			uiInterface.clientStatusChanged(clientStatus);
		}
		
		if (this.clientStatus == ClientStatus.ERROR) {
			LOG.debug("Client is in error mode, unable to change it");
			return;
		}

		this.clientStatus = clientStatus;

	}

	public void requestProcessingChunk(DataChunk chunk) {
		ProcessManager.getInstance().setClientStatus(ClientStatus.PROCESSING);

		// currentMerger = new ChunkResultMerger(this);
		finalResult = new ChunkResult(chunk.getChunkID());

		if (uiInterface != null) {
			uiInterface.replaceDisplayInfo(new Info("Process Chunk", chunk.getChunkID()));
			uiInterface.replaceDisplayInfo(new Info("Cores assigned to this resource", Integer.toString(CORES)));
		}

		remainingSubChunk = 0;
		while ((remainingSubChunk * SUBCHUNK_SIZE) < chunk.getLinesToProcess().size()) {
			int minIndex = remainingSubChunk * SUBCHUNK_SIZE;
			int maxIndex = Math.min((remainingSubChunk + 1) * SUBCHUNK_SIZE, chunk.getLinesToProcess().size());

			remainingSubChunk++;
			chunkAnalyserService.execute(new ChunkWorker(chunk, finalResult, minIndex, maxIndex, this, currentLibrary, processConfiguration));
		}
	}

	/**
	 * Warning: need to be synchronized since it is called from an other thread via the interface
	 */
	@Override
	public synchronized void workerDone() {
		
		remainingSubChunk--;
		LOG.debug("SubChunk finished processed, remaining : " + remainingSubChunk);

		if (remainingSubChunk == 0) {
			
			LOG.debug("All subChunks processed - send results to server");
			
			analysedChuncksCount++;
			analysedDataCount += finalResult.getLinesProcessed();
			if (uiInterface != null) {
				uiInterface.replaceDisplayInfo(new Info("analysedChuncksCount", Integer.toString(analysedChuncksCount)));
				uiInterface.replaceDisplayInfo(new Info("analysedDataCount", Long.toString(analysedDataCount)));
			}

			if (networkInterface != null) {
				networkInterface.notifyNewResults(currentAnalysisID, finalResult);
			}
			setClientStatus(ClientStatus.WAITING_FOR_DATA);
		}
	}

	public GeneLibrary getCurrentLibrary() {
		return currentLibrary;
	}

	public void setCurrentLibrary(GeneLibrary currentLibrary) {
		this.currentLibrary = currentLibrary;
	}

	public PartialProcessConfiguration getProcessConfiguration() {
		return processConfiguration;
	}

	public void setProcessConfiguration(PartialProcessConfiguration processConfiguration) {
		this.processConfiguration = processConfiguration;
		if (uiInterface != null) {
			uiInterface.addDisplayedInfoChanged(new Info("Config - gen CSV", Boolean.toString(processConfiguration.getOutputAttributes().isGenerateCSV())));
			uiInterface.addDisplayedInfoChanged(new Info("Config - gen PDF", Boolean.toString(processConfiguration.getOutputAttributes().isGeneratePDF())));
			uiInterface.addDisplayedInfoChanged(new Info("Config - gen unfound stats", Boolean.toString(processConfiguration.getOutputAttributes().isGenerateStatisticsOnUnfoundgRNA())));

			uiInterface.addDisplayedInfoChanged(new Info("Config - allow mismatch", Boolean.toString(processConfiguration.getPatternAttributes().isAllowOneMismatch())));
			uiInterface.addDisplayedInfoChanged(new Info("Config - check for shifted", Boolean.toString(processConfiguration.getPatternAttributes().isCheckForShifted())));

			uiInterface.addDisplayedInfoChanged(new Info("Config - Pattern - grna length ", Integer.toString(processConfiguration.getPattern().getGrnaSubSequenceLength())));
			uiInterface.addDisplayedInfoChanged(new Info("Config - Pattern - skip count  ", Integer.toString(processConfiguration.getPattern().getSkippedCharCount())));
		}
	}

	public String getCurrentAnalysisID() {
		return currentAnalysisID;
	}

	public void setCurrentAnalysisID(String currentAnalysisID) {
		this.currentAnalysisID = currentAnalysisID;
		if (uiInterface != null) {
			// Assign new analyse, clear all previous infos.
			uiInterface.clearDisplayedInfoChanged();
			uiInterface.addDisplayedInfoChanged(new Info("Analyse ID", currentAnalysisID));
		}
	}

	public void setNetworkInterface(ProcessorClient networkInterface) {
		this.networkInterface = networkInterface;
	}

	public void stopCurrentAction() {
		LOG.debug("Request to stop current action");
		chunkAnalyserService.shutdownNow();
		setClientStatus(ClientStatus.IDLE);
	}

}
