package com.gmo.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.gmo.configuration.ApplicationContextManager;
import com.gmo.logger.Log4JLogger;
import com.gmo.nodes.NodeManager;
import com.gmo.processorNode.viewmodel.ViewFile;
import com.gmo.processorNode.viewmodel.analyses.preprocessing.ViewDataSplitterModel;
import com.gmo.sharedobjects.model.inputs.InputType;
import com.gmo.sharedobjects.model.processconfiguration.ExtractionPattern;
import com.gmo.ws.exceptions.NodeStorageException;

@Path("/ws-resources/datastorage")
public class WSDataStorage extends Application {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	@GET
	@Path("dataFiles")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewFile> getdataStoredJSON() {
		LOG.info("Retrieve stored input data files");
		return NodeManager.getInstance().getNodeRMIClient().getListStoredData();

	}

	@GET
	@Path("libFiles")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewFile> getLibrariesStoredJSON() {
		LOG.info("Retrieve stored input libraries files");
		return NodeManager.getInstance().getNodeRMIClient().getListStoredLibraries();
	}

	@GET
	@Path("extractionPatterns")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ExtractionPattern> getEXtractionPatternsJSON() {
		LOG.info("Retrieve stored patterns");
		return ApplicationContextManager.getInstance().getListPatterns();
	}

	@GET
	@Path("splitPatterns")
	@Produces(MediaType.APPLICATION_JSON)
	public List<ViewDataSplitterModel> getSplitPatternsJSON() {
		LOG.info("Retrieve stored split patterns");
		List<ViewDataSplitterModel> listViewSplitPattern = ApplicationContextManager.getInstance()
				.getListViewSplitPatterns();
		return listViewSplitPattern;
	}

	@POST
	@Path("/uploadDataFile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadDataFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("analyseID") String analyseid) {
		LOG.info("Request to upload files on server param found ->" + analyseid);
		try {
			NodeManager.getInstance().getNodeRMIClient().uploadFileToNodeServer(InputType.DATA,
					fileDetail.getFileName(), uploadedInputStream, analyseid);
			LOG.info("File successfully uploaded to Node server storage location : " + fileDetail.getFileName());
			return Response.status(200).build();
		} catch (NodeStorageException nse) {
			LOG.info("File already exists in db - Abort " + fileDetail.getFileName());
			return Response.status(409).entity("File already exists in database").build();
		} catch (IOException ioe) {
			LOG.info("Error while uploading file to node server : " + fileDetail.getFileName());
			return Response.status(500).build();
		}
	}

	@POST
	@Path("/uploadLibFile")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadLibFile(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("analyseID") String analyseid) {
		LOG.info("Request to upload files on server param found ->" + analyseid);
		try {
			NodeManager.getInstance().getNodeRMIClient().uploadFileToNodeServer(InputType.LIBRARY,
					fileDetail.getFileName(), uploadedInputStream, analyseid);
			LOG.info("File successfully uploaded to Node server storage location : " + fileDetail.getFileName());
			return Response.status(200).build();
		} catch (NodeStorageException nse) {
			LOG.info("File already exists in db - Abort " + fileDetail.getFileName());
			return Response.status(409).entity("File already exists in database").build();
		} catch (IOException ioe) {
			LOG.info("Error while uploading file to node server : " + fileDetail.getFileName());
			return Response.status(500).build();
		}
	}

}
