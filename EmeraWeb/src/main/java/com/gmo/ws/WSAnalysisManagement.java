package com.gmo.ws;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.Logger;

import com.gmo.configuration.ApplicationContextManager;
import com.gmo.generated.configuration.applicationcontext.LocationType;
import com.gmo.logger.Log4JLogger;
import com.gmo.nodes.NodeManager;
import com.gmo.processorNode.viewmodel.StatusChangeRequest;
import com.gmo.processorNode.viewmodel.ViewNodePollingInfo;
import com.gmo.processorNode.viewmodel.ViewPollingInfo;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.processorNode.viewmodel.network.ViewDistantResource;
import com.gmo.processorNode.viewmodel.network.ViewNetworkConfig;
import com.gmo.processorNode.viewmodel.report.ViewReportGraphData;
import com.gmo.results.ResultsManager;
import com.gmo.sharedobjects.model.analysis.NoSuchAnalysisException;
import com.gmo.sharedobjects.model.genelibrary.ReferenceGene;
import com.gmo.sharedobjects.model.reports.Report;

@Path("/ws-resources/analysis")
public class WSAnalysisManagement {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public final static String TEXT_CSV = "text/csv";

	public final static String APP_PDF = "application/pdf";

	@Path("appinfos/{user_id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ViewPollingInfo retrieveApplicationsInfoJSON(@PathParam("user_id") String userID) {
		// Local Data
		List<ViewAnalysis> listCompleted = ResultsManager.getInstance().getUserProcessedAnalysis(userID);
		ViewDistantResource frontEndServer = NodeManager.getInstance().getFrontEndServer();

		// Remote Data
		ViewNodePollingInfo nodeInfo = NodeManager.getInstance().getNodeRMIClient().getViewNodePollingInfo(userID);

		// Final data objects
		ViewNetworkConfig networkConfig = new ViewNetworkConfig(frontEndServer, nodeInfo.getNetworkConfig());

		ViewPollingInfo pollingData = new ViewPollingInfo(networkConfig, nodeInfo.getRunningAnalysis(), listCompleted);
		return pollingData;
	}

	@Path("report/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ViewReportGraphData retrieveReportInfoJSON(@PathParam("id") String analyseID) {
		LOG.debug("Request for ReportGraphData for analyse : " + analyseID);
		ViewReportGraphData graphData = new ViewReportGraphData();

		try {
			Report report = ResultsManager.getInstance().getProcessedAnalysis(analyseID).getReport();
			List<ReferenceGene> lib = report.getLibrary().getGenes();
			for (int i = 0; i < lib.size(); i++) {
				ReferenceGene grna = lib.get(i);
				graphData.addDataToGraph(i, report.getOccurencePercent(grna.getAssociatedSequence()), grna.getName());
			}
		} catch (NoSuchAnalysisException e) {
			LOG.debug("No analyse found with ID (in processed list): " + analyseID);
		}

		return graphData;
	}

	@GET
	@Path("report/{id}/csv")
	@Produces(TEXT_CSV)
	public Response getCSVAnalysisResultFile(@PathParam("id") String analyseID, @QueryParam("user_ID") String userID) {
		return getResultFileResponse(analyseID, userID, "csv_report.csv");
	}

	@GET
	@Path("report/{id}/pdf")
	@Produces(APP_PDF)
	public Response getPDFAnalyseResultFile(@PathParam("id") String analyseID, @QueryParam("user_ID") String userID) {
		return getResultFileResponse(analyseID, userID, "pdf_report.pdf");
	}

	private Response getResultFileResponse(String analyseID, String userID, String fileName) {
		String resultLoc = ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocation().getAnalysisResultsLocationRoot();
		LocationType locType = ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocation().getAnalysisResultsLocationType();

		switch (locType) {
		case LOCAL: {
			String filePath = resultLoc + File.separator + userID + File.separator + analyseID + File.separator + fileName;
			File result = new File(filePath);
			if (!result.exists()) {
				LOG.warn("No analysis result file found for " + filePath);
			} else {
				ResponseBuilder response = Response.ok((Object) result);
				response.header("Content-Disposition", "attachment; filename=" + result.getName());
				return response.build();
			}
			break;
		}
		case S_3: {
			String filePath = "https://s3.amazonaws.com/" + resultLoc + "/" + userID + "/" + analyseID + "/" + fileName;

			URI targetURIForRedirection;
			try {
				targetURIForRedirection = new URI(filePath);
				return Response.temporaryRedirect(targetURIForRedirection).build();
			} catch (URISyntaxException e) {
				LOG.warn("URISyntaxException :" + filePath);
				return Response.status(404).build();
			}
		}
		}
		return Response.status(404).build();
	}

	@Path("stopall")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response requestStopAllAnalysesJSON(@QueryParam("user_id") String userID) {
		LOG.info("Request all analysis to be stopped for user : " + userID);
		NodeManager.getInstance().getNodeRMIClient().stopAllAnalyses(userID);
		return Response.status(200).build();
	}

	@Path("statusrequest")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response requestStatusChanged(StatusChangeRequest jsonStatusReq) {
		String id = jsonStatusReq.getAnalyseId();
		LOG.info("Request to change status for analyse ID " + id + " to " + jsonStatusReq);
		NodeManager.getInstance().getNodeRMIClient().requestRunningAnalysisChangeStatus(id, jsonStatusReq.getNewStatus());
		return Response.status(200).build();
	}

}
