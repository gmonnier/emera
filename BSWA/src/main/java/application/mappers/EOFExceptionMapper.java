package application.mappers;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import model.analyses.AnalysisStatus;
import model.analyses.NoSuchAnalysisException;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;

import coreprocessing.AnalysisManager;

@Provider
public class EOFExceptionMapper implements ExceptionMapper<IOException> {

	@Context
	private HttpServletRequest request;

	@Context
	private ServletContext servletContext;

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public Response toResponse(IOException ex) {
		
		LOG.error("EOF exception catch on " + request.getContentType());
		String analyseID = request.getParameter("analyseid");

		if(analyseID != null) {
			try {
				LOG.debug("Request to change status to UPLOAD_ERROR for " + analyseID);
				AnalysisManager.getInstance().getRunningAnalysis(analyseID).setStatus(AnalysisStatus.UPLOAD_ERROR);
			} catch(NoSuchAnalysisException e) {
				LOG.error("No analysis with given ID -> " + analyseID);
			}
		}
		
		LOG.error("EOF exception catch while uploading file! --> Cancel uploading");
		return Response.status(500).build();
	}
}