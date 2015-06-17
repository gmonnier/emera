package ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ws-resources/connection")
public class WSConnection {

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getTestConnectionHTML() {
		return "<b>Connection OK</b>";
	}

}
