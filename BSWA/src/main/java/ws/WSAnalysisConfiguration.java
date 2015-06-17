package ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import logger.Log4JLogger;
import model.parameters.ExtractionPattern;
import model.processconfig.ProcessConfiguration;

import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataParam;

import applicationconfig.ApplicationContextManager;
import coreprocessing.AnalysisManager;
import viewModel.ViewCreateProcessConfiguration;
import ws.exceptions.ApplicationRequestException;

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
		ExtractionPattern newPat = new ExtractionPattern(jsonPattern);
		boolean success = ApplicationContextManager.getInstance().requestAddPattern(newPat);
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
