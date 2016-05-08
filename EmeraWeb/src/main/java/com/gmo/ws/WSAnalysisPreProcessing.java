package com.gmo.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.BaseSpaceContextManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.nodes.NodeManager;
import com.gmo.processorNode.viewmodel.analyses.preprocessing.ViewPreProcessingConfiguration;

@Path("/ws-resources/preprocess")
public class WSAnalysisPreProcessing {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	@Path("enqueue")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postNewProcessJSON(ViewPreProcessingConfiguration jsonConfig, @QueryParam("user_id") String userID) {
		LOG.debug("enqueue pre-processing analysis : " + jsonConfig + "   User ID = : " + userID);

		// Get users's bs connection credentials
		String bsClientID = BaseSpaceContextManager.getInstance().getConfig().getBsClientID();
		String bsClientSecret = BaseSpaceContextManager.getInstance().getConfig().getBsClientSecret();
		String bsAccessToken = BaseSpaceContextManager.getInstance().getConfig().getBsAccessToken();

		
		String id = NodeManager.getInstance().getNodeRMIClient().enqueueNewPreprocessingAnalysis(jsonConfig, userID, bsClientID, bsClientSecret, bsAccessToken);

		LOG.info("Analysis enqueue request performed : id = " + id);
		if(id == null) {
			return Response.status(500).build();
		}
		return Response.ok().entity(id).build();
	}

	@Path("confinit")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ViewPreProcessingConfiguration retrieveDefaultConfigurationJSON() {
		return new ViewPreProcessingConfiguration();
	}
}
