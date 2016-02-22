package com.gmo.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import processorNode.viewmodel.analyses.preprocessing.ViewPreProcessingConfiguration;

import com.gmo.logger.Log4JLogger;

@Path("/ws-resources/preprocess")
public class WSAnalysisPreProcessing {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	@Path("enqueue")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String postNewProcessJSON(ViewPreProcessingConfiguration jsonConfig, @QueryParam("user_id") String userID) {
		LOG.debug("POST: enqueue pre-processing analysis : " + jsonConfig);
		LOG.debug("POST: User ID = : " + userID);

		String analyseID = "AnalyseID";
		LOG.info("new Analysis enqueued with ID " + analyseID);
		return analyseID;
	}

	@Path("confinit")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ViewPreProcessingConfiguration retrieveDefaultConfigurationJSON() {
		return new ViewPreProcessingConfiguration();
	}
}
