package coreprocessing.analysismerger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import reports.Report;

import com.gmo.logger.Log4JLogger;
import com.gmo.model.data.ChunkResult;

public class AnalysisMergerWorker implements Runnable {

	private ChunkResult chunkresult;

	private static Logger LOG = Log4JLogger.logger;

	private IMergerInfo imerge;

	private Report report;

	public AnalysisMergerWorker(Report report, ChunkResult chunkresult, IMergerInfo imerge) {
		this.chunkresult = chunkresult;
		this.imerge = imerge;
		this.report = report;
	}

	@Override
	public void run() {
		LOG.debug("Merge chunk result " + chunkresult.getChunkId() + " report=" + report);

		HashMap<String, Integer> resultOcc = report.getOccurencesFound();

		Iterator<Map.Entry<String, Integer>> it = chunkresult.getOccurences().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) it.next();

			Integer resultVal = resultOcc.get(pair.getKey());
			if (resultVal != null) {
				resultOcc.put(pair.getKey(), resultVal + pair.getValue());
			} else {
				resultOcc.put(pair.getKey(), pair.getValue());
			}
			report.incrementTotalOccurencesFound(pair.getValue());
		}
		
		report.incrementTotalLineProcessed(chunkresult.getLinesProcessed());
		report.incrementTotalChunksProcessed();
		
		LOG.debug("Notify merge performed - total chunks processed = " + report.getTotalChunksProcessed() + "\t\t\t\t linesProcessed : " + report.getTotalLineProcessed());
		imerge.mergeDone(chunkresult.getChunkId());
	}

}
