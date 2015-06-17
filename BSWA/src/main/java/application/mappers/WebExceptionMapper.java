package application.mappers;

import javax.ws.rs.WebApplicationException;
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

	public Response toResponse(WebApplicationException ex) {
		LOG.error("Bad request --> ");
		LOG.error(ex.getMessage(), ex);
		return Response.status(400).entity("Bad request").type(MediaType.APPLICATION_JSON).build();
	}
	

}
