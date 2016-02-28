package com.gmo.results.extractor;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

public class AnalysisS3Extractor extends AnalysisExtractor {

	private static Logger LOG = Log4JLogger.logger;

	@Override
	public boolean isRootValid() {
		return true;
	}

}
