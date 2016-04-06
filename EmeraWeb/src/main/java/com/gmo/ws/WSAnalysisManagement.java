package com.gmo.ws;

import java.io.File;
import java.util.ArrayList;
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
import com.gmo.logger.Log4JLogger;
import com.gmo.nodes.NodeManager;
import com.gmo.processorNode.viewmodel.StatusChangeRequest;
import com.gmo.processorNode.viewmodel.ViewPollingInfo;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
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
		return NodeManager.getInstance().getNodeRMIClient().getViewPollingInfo(userID);
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
	public Response getCSVAnalyseResultFile(@PathParam("id") String analyseID, @QueryParam("user_ID") String userID) {

		String resultLoc = ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocation();

		String filePath = resultLoc + File.separator + userID + File.separator + analyseID + File.separator + "csv_report.csv";
		File result = new File(filePath);
		if (!result.exists()) {
			LOG.debug("No CSV analysis found for ID " + analyseID);
		} else {
			ResponseBuilder response = Response.ok((Object) result);
			response.header("Content-Disposition", "attachment; filename=" + result.getName());
			return response.build();
		}

		return Response.status(404).build();
	}

	@GET
	@Path("report/{id}/pdf")
	@Produces(APP_PDF)
	public Response getPDFAnalyseResultFile(@PathParam("id") String analyseID, @QueryParam("user_ID") String userID) {

		String resultLoc = ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocation();

		String filePath = resultLoc + File.separator + userID + File.separator + analyseID + File.separator + "pdf_report.pdf";
		File result = new File(filePath);
		if (!result.exists()) {
			LOG.debug("No PDF analysis found for path " + filePath);
		} else {
			ResponseBuilder response = Response.ok((Object) result);
			response.header("Content-Disposition", "attachment; filename=" + result.getName());
			return response.build();
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
