package ws;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

import basespaceService.model.UserInfo;
import basespaceService.model.UserRun;
import externalInterfaces.basespace.BaseSpaceModelManager;
import viewModel.ViewUserRun;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/ws-resources/bs-model")
public class WSBaseSpace extends Application {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	@GET
	@Path("userinfo")
	@Produces(MediaType.APPLICATION_JSON)
	public UserInfo getUserInfoJSON() {
		LOG.info("Retrieve current user info");
		return BaseSpaceModelManager.getInstance().getUserInfo();
	}

	@GET
	@Path("connectionstatus")
	@Produces(MediaType.TEXT_PLAIN)
	public boolean getConnectionStatusJSON() {
		LOG.info("Retrieve connection status to BS");
		return BaseSpaceModelManager.getInstance().isConnectionOK();
	}

	@GET
	@Path("userruns")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewUserRun> getUserRunsJSON() {
		LOG.info("Retrieve current user runs");

		List<UserRun> input = BaseSpaceModelManager.getInstance().getListRuns();
		List<ViewUserRun> listrunView = new ArrayList<ViewUserRun>();

		// Sort from the last run (most recent) to the last one
		for (int i = input.size() - 1 ; i >= 0 ; i--) {
			UserRun inputRun = input.get(i);
			listrunView.add(new ViewUserRun(inputRun));
		}
		return listrunView;
	}

}