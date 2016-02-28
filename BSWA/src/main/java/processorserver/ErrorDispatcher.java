package processorserver;

import java.util.List;

import model.analyses.NoSuchAnalysisException;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.sharedobjects.util.StringSerializationException;

import coreprocessing.Analysis;
import coreprocessing.AnalysisManager;

public class ErrorDispatcher {

	// The logger.
	private static Logger LOG = Log4JLogger.logger;

	public void unableToRetrieveDataChunkResults(String ip, String analyseID, StringSerializationException t) {
		LOG.error("unableToRetrieveDataChunkResults from " + ip, t);
		LOG.error("Reason : " + t.getMessage());

		try {
			Analysis analysis = AnalysisManager.getInstance().getRunningAnalysis(analyseID);
			// remove from analysis and release potential chunks
			LOG.warn("Ask analysis to remove " + ip + " from its distant resource list");
			analysis.removeDistantResource(ip);
		} catch (NoSuchAnalysisException e) {
			LOG.error("No analysis found with id " + analyseID);
		}
	}

	public void unableToRetrieveDataChunkResults(String ip, Throwable t) {
		LOG.error("unableToRetrieveDataChunkResults from " + ip, t);
		List<Analysis> listAnalysis = AnalysisManager.getInstance().getAllRunningAnalysis();
		for (Analysis analysis : listAnalysis) {
			// remove from analysis and release potential chunks
			analysis.removeDistantResource(ip);
		}
	}

	public void unableToRetrieveClientStatus(String ip) {
		LOG.error("unableToRetrieveClientStatus from " + ip);
	}

}
