package application.mappers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;

@Provider
public class WebExceptionMapper implements ExceptionMapper<WebApplicationException> {
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;
	
    @Context
    HttpServletRequest request;

	public Response toResponse(WebApplicationException ex) {
		LOG.error("Error Accessing " + request.getRequestURI() + " : " + ex.getMessage());
		
		if(ex.getResponse().getStatus() == 500) {
			// Print stack trace only if 500
			LOG.error(ex);
		}
		
		return Response.status(400).entity("Bad request").type(MediaType.APPLICATION_JSON).build();
	}
	

}
