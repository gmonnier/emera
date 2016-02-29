package com.gmo.ws;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.ViewCompareAnalysisParam;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.analyses.standard.ViewAnalysis;
import com.gmo.results.ResultsManager;
import com.gmo.sharedobjects.model.analysis.NoSuchAnalysisException;

@Path("/ws-resources/additional")
public class WSAdditionalAnalysis {
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public final static String TEXT_CSV = "text/csv";

	public final static String APP_PDF = "application/pdf";

	@GET
	@Path("{analyseId}/{name}")
	@Produces({TEXT_CSV, APP_PDF})
	public Response getAdditionalReport(@PathParam("analyseId") String analyseId, @PathParam("name") String name) {

		LOG.debug("Request for additional analysis download for analyse " + analyseId + " on file index " + name);
		List<ViewFile> viewFiles;
		try {
			viewFiles = ResultsManager.getInstance().getProcessedAnalysis(analyseId).getAdditionalAnalyses();

			for (Iterator<ViewFile> iterator = viewFiles.iterator(); iterator.hasNext();) {
				ViewFile viewFile = (ViewFile) iterator.next();
				if (viewFile.getName().equals(name)) {

					// the ID is the absolute path
					File result = new File(viewFile.getId());
					if (!result.exists()) {
						LOG.debug("No additional analysis found for path " + result.getAbsolutePath());
					} else {
						ResponseBuilder response = Response.ok((Object) result);
						String contentType = result.getName().toLowerCase().endsWith(".pdf") ? APP_PDF : TEXT_CSV;
						response.type(contentType);
						response.header("Content-Disposition", "attachment; filename=" + result.getName());
						return response.build();
					}
				}
			}
		} catch (NoSuchAnalysisException e) {
			LOG.error("No analysis found with ID " + analyseId);
		}

		return Response.status(404).build();
	}
	
	@DELETE
	@Path("{analyseId}/{name}")
	public Response deleteAdditionalReport(@PathParam("analyseId") String analyseId, @PathParam("name") String filename) {

		LOG.debug("Request for additional analysis to be deleted " + analyseId + " - file name : " + filename);
		try {
			ResultsManager.getInstance().getProcessedAnalysis(analyseId).deleteAdditionalReport(filename);
			return Response.status(200).build();
		} catch (Throwable e) {
			LOG.error("No analysis found with ID " + analyseId, e);
		}

		return Response.status(404).build();
	}
	
	@Path("analysisCompare")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response requestAnalysisComparison(ViewCompareAnalysisParam compareParam) {

		LOG.info("Request to perform analysis comparison between " + compareParam.getAnalysisID1() + " and " + compareParam.getAnalysisID2() + " to output type :" + compareParam.getOutputFileType());
		try {
			ViewAnalysis anRef = ResultsManager.getInstance().getProcessedAnalysis(compareParam.getAnalysisID1());
			ViewAnalysis anComp = ResultsManager.getInstance().getProcessedAnalysis(compareParam.getAnalysisID2());
			new OccurencesIncreaseAnalysis(anRef, anComp, compareParam.getOutputFileType());
			return Response.status(200).build();
		} catch (NoSuchAnalysisException e) {
			LOG.error("Request to change status on unexisting analysis ID");
			return Response.status(500).build();
		}

	}
	
}
