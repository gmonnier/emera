package com.gmo.sharedobjects.model.reports;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.sharedobjects.model.genelibrary.GeneLibrary;
import com.gmo.sharedobjects.model.processconfiguration.ProcessConfiguration;
import com.gmo.sharedobjects.model.reports.comparators.OccurenceComparator;

public class Report implements Serializable {

	private GeneLibrary library;

	private HashMap<String, Integer> occurencesFound;

	private Map<String, UnfoundStartSeqMap> uncorrespondingEntry;

	private ViewCreateProcessConfiguration analyseConfig;

	private long totalLineProcessed = 0;
	// Number of input lines containing one gRNA
	private long totalOccurencesFound = 0;

	private int totalChunksProcessed = 0;

	private long startDate;
	private long endDate;

	private int chunkSize;

	private String analyseID;

	private String userID;

	private Report() {
	}

	public Report(ViewCreateProcessConfiguration analyseConfig, long startDate, String analyseID, String userID) {
		this.analyseConfig = analyseConfig;
		this.occurencesFound = new HashMap<String, Integer>();
		this.startDate = startDate;
		this.userID = userID;
		this.analyseID = analyseID;
		this.uncorrespondingEntry = new HashMap<String, UnfoundStartSeqMap>();
		this.library = new GeneLibrary();
	}

	public Report(ViewCreateProcessConfiguration analyseConfig, long startDate, String analyseID, String userID, int chunksize) {
		this.chunkSize = chunksize;
		new Report(analyseConfig, startDate, analyseID, userID);
	}

	public void finalizeReport() {
		// Sort the library by occurences
		Collections.sort(library.getGenes(), new OccurenceComparator(this));
	}

	public ViewCreateProcessConfiguration getAnalyseConfig() {
		return analyseConfig;
	}

	public void setAnalyseConfig(ViewCreateProcessConfiguration analyseConfig) {
		this.analyseConfig = analyseConfig;
	}

	public GeneLibrary getLibrary() {
		return library;
	}

	public void setLibrary(GeneLibrary library) {
		this.library = library;
	}

	public HashMap<String, Integer> getOccurencesFound() {
		return occurencesFound;
	}

	public Map<String, UnfoundStartSeqMap> getUncorrespondingEntry() {
		return uncorrespondingEntry;
	}

	public void setUncorrespondingEntry(Map<String, UnfoundStartSeqMap> uncorrespondingEntry) {
		this.uncorrespondingEntry = uncorrespondingEntry;
	}

	public long getTotalLineProcessed() {
		return totalLineProcessed;
	}

	public long getTotalOccurencesFound() {
		return totalOccurencesFound;
	}

	public int getTotalChunksProcessed() {
		return totalChunksProcessed;
	}

	public void setTotalLineProcessed(long totalLineProcessed) {
		this.totalLineProcessed = totalLineProcessed;
	}

	public void incrementTotalLineProcessed(long increment) {
		this.totalLineProcessed += increment;
	}

	public void setTotalOccurencesFound(long totalOccurencesFound) {
		this.totalOccurencesFound = totalOccurencesFound;
	}

	public void incrementTotalOccurencesFound(long increment) {
		this.totalOccurencesFound += increment;
	}

	public synchronized void setTotalChunksProcessed(int totalChunksProcessed) {
		this.totalChunksProcessed = totalChunksProcessed;
	}

	public synchronized void incrementTotalChunksProcessed() {
		this.totalChunksProcessed++;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public int getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(int chunkSize) {
		this.chunkSize = chunkSize;
	}

	public String getAnalyseID() {
		return analyseID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public double getOccurencePercent(String geneKey) {
		Integer occ = occurencesFound.get(geneKey);
		if (occ != null) {
			return ((double) occ * 100.0) / (double) totalOccurencesFound;
		} else {
			return 0;
		}
	}

	public int getOccurenceCount(String geneKey) {
		Integer occ = occurencesFound.get(geneKey);
		if (occ != null) {
			return occ;
		} else {
			return 0;
		}
	}

	public String getProcessingTimeFormatted() {
		if (startDate != 0 && endDate != 0) {
			long duration = endDate - startDate;
			int seconds = (int) (duration / 1000) % 60;
			int minutes = (int) ((duration / (1000 * 60)) % 60);
			int hours = (int) ((duration / (1000 * 60 * 60)) % 24);
			String st = hours + " hours " + minutes + " min " + seconds + " s";
			return st;
		} else {
			return "Unknown";
		}
	}

	public void setProcessingTimeFormatted(String processTimeFormetted) {
		// Only for serialization
	}

}
