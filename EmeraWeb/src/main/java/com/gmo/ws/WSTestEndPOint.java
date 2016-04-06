package com.gmo.ws;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.results.ResultsManager;

@Path("/ws-resources/test")
public class WSTestEndPOint {

	@Path("all-processed")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewAnalysis> retrieveApplicationsInfoJSON(@PathParam("user_id") String userID) {
		return ResultsManager.getInstance().getAllProcessedAnalysis();
	}
	
}
