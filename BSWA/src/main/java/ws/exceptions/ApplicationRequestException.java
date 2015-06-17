package ws.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ApplicationRequestException extends WebApplicationException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
      * Create a HTTP 401 (Unauthorized) exception.
     */
     public ApplicationRequestException() {
         super(Response.status(Status.BAD_REQUEST).build());
     }

     /**
      * Create a HTTP 404 (Not Found) exception.
      * @param message the String that is the entity of the 404 response.
      */
     public ApplicationRequestException(String message) {
         super(Response.status(Status.BAD_REQUEST).entity(message).type("text/plain").build());
     }

}