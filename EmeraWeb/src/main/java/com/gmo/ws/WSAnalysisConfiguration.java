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
import com.gmo.logger.Log4JLogger;
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
		String id = AnalysisManager.getInstance().enqueueNewAnalysis(jsonConfig, userID);
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
		return new ViewCreateProcessConfiguration();
	}
}
