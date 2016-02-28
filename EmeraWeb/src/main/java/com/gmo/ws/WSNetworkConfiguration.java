package com.gmo.ws;

import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.Logger;

import awsinterfaceManager.AWSEC2InterfaceManager;

import com.gmo.logger.Log4JLogger;
import com.gmo.nodes.NodeManager;

@Path("/ws-resources/netconfig")
public class WSNetworkConfiguration {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
	
	@DELETE
	@Path("{clientID}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestClientShutdownJSON(@PathParam("clientID") String clientID) {
		LOG.debug("Request to remove distant resource with ID " + clientID);
		NodeManager.getInstance().getNodeRMIClient().requestNodeProcessorClientRemove(clientID);
		return Response.status(200).build();
	}
	
	@POST
	@Path("startAllAWS")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestClientStartAllAWSResourcesJSON() {
		LOG.debug("Request to start all AWS resources");
		AWSEC2InterfaceManager.getInstance().startAllInstances();
		return Response.status(200).build();
	}
	
	@POST
	@Path("stopAllAWS")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestClientStopAllAWSResourcesJSON() {
		LOG.debug("Request to stop all AWS resources");
		AWSEC2InterfaceManager.getInstance().stopAllInstances();
		return Response.status(200).build();
	}
	
	@POST
	@Path("deployClients")
	@Produces(MediaType.APPLICATION_JSON)
	public Response requestClientDeployProcessor() {
		LOG.debug("Request to deploy client processor on all active resources");
		return Response.status(200).build();
	}

}
