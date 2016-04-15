package com.gmo.ws;

import java.io.IOException;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmo.configuration.ApplicationContextManager;
import com.gmo.configuration.BaseSpaceContextManager;
import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.logger.Log4JLogger;
import com.gmo.nodes.NodeManager;
import com.gmo.processorNode.viewmodel.ViewCreateProcessConfiguration;
import com.gmo.sharedobjects.model.processconfiguration.ExtractionPattern;
import com.gmo.ws.exceptions.ApplicationRequestException;

@Path("/ws-resources/process")
public class WSAnalysisConfiguration {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	@Path("enqueue")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String postNewProcessJSON(ViewCreateProcessConfiguration jsonConfig, @QueryParam("user_id") String userID) {
		LOG.debug("POST: enqueue requested for new analysis : " + jsonConfig);
		LOG.debug("POST: User ID = : " + userID);
		
		// Get users's bs connection credentials
		String bsClientID = BaseSpaceContextManager.getInstance().getConfig().getBsClientID();
		String bsClientSecret = BaseSpaceContextManager.getInstance().getConfig().getBsClientSecret();
		String bsAccessToken = BaseSpaceContextManager.getInstance().getConfig().getBsAccessToken();
		
		LocationType resultLocType = ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocationType();
		String id = NodeManager.getInstance().getNodeRMIClient().enqueueNewAnalysis(jsonConfig, userID, bsClientID, bsClientSecret, bsAccessToken, resultLocType);
		
		// Update storage of default values for next analysisconfigurations
		ApplicationContextManager.getInstance().getConfig().setAllowCharacterError(jsonConfig.getPatternAttributes().isAllowOneMismatch());
		ApplicationContextManager.getInstance().getConfig().setAllowShifting(jsonConfig.getPatternAttributes().isCheckForShifted());
		ApplicationContextManager.getInstance().getConfig().setCheckForUnfoundEntries(jsonConfig.getOutputAttributes().isGenerateStatisticsOnUnfoundgRNA());
		ApplicationContextManager.getInstance().getConfig().setGenerateCSVOutput(jsonConfig.getOutputAttributes().isGenerateCSV());
		ApplicationContextManager.getInstance().getConfig().setGeneratePDFOutput(jsonConfig.getOutputAttributes().isGeneratePDF());
		ApplicationContextManager.getInstance().updateDefaultPatternIndex(jsonConfig.getPattern());
		ApplicationContextManager.getInstance().getWriter().marshalXMLFileExternalThread();
		
		LOG.info("new Analysis enqueued with ID " + id);
		return id;
	}

	@Path("addPattern")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public ExtractionPattern postNewPatternJSON(String jsonPattern) {
		LOG.debug("POST: Request to create new pattern with string " + jsonPattern);
		boolean success;
		ExtractionPattern newPat = null;
		JsonFactory factory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);
		TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
		HashMap<String, String> parsed;
		try {
			parsed = mapper.readValue(jsonPattern, typeRef);
			
			newPat = new ExtractionPattern(parsed.get("value"),parsed.get("alias"));
			success = ApplicationContextManager.getInstance().requestAddPattern(newPat);
		} catch (IOException e) {
			LOG.error("Unable to parse JSON in postNewPatternJSON : jsonPattern = " + jsonPattern);
			success = false;
		}
		
		
		if (success) {
			LOG.info("new Pattern added successfully ");
			ApplicationContextManager.getInstance().requestSetDefaultPattern(jsonPattern);
			return newPat;
		} else {
			LOG.error("Pattern is invalid or an error occured while adding it to storage module. Send back http 500 error.");
			throw new ApplicationRequestException("Pattern is invalid or already exists");
		}
	}

	@Path("confinit")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ViewCreateProcessConfiguration retrieveDefaultConfigurationJSON() {
		return new ViewCreateProcessConfiguration(ApplicationContextManager.getInstance());
	}
}
