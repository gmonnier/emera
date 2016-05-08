/**
 * Copyright 2013 Illumina
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.illumina.basespace;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.illumina.basespace.auth.ResourceForbiddenException;
import com.illumina.basespace.entity.ApiResource;
import com.illumina.basespace.entity.AppResultCompact;
import com.illumina.basespace.entity.AppSessionCompact;
import com.illumina.basespace.entity.AppSessionStatus;
import com.illumina.basespace.entity.CreateAppResult;
import com.illumina.basespace.entity.CreatePurchase;
import com.illumina.basespace.entity.File;
import com.illumina.basespace.entity.FileCompact;
import com.illumina.basespace.entity.FileRedirectMetaData;
import com.illumina.basespace.entity.ProductCompact;
import com.illumina.basespace.entity.ProjectCompact;
import com.illumina.basespace.entity.ReferenceCompact;
import com.illumina.basespace.entity.RunCompact;
import com.illumina.basespace.entity.SampleCompact;
import com.illumina.basespace.entity.UploadStatus;
import com.illumina.basespace.file.DownloadEvent;
import com.illumina.basespace.file.DownloadListener;
import com.illumina.basespace.infrastructure.BaseSpaceException;
import com.illumina.basespace.infrastructure.ClientConnectionProvider;
import com.illumina.basespace.infrastructure.ResourceNotFoundException;
import com.illumina.basespace.param.CoverageParams;
import com.illumina.basespace.param.FileParams;
import com.illumina.basespace.param.Mappable;
import com.illumina.basespace.param.PositionalQueryParams;
import com.illumina.basespace.param.QueryParams;
import com.illumina.basespace.property.CreatePropertiesHolder;
import com.illumina.basespace.property.Property;
import com.illumina.basespace.response.CreatePurchaseResponse;
import com.illumina.basespace.response.GetAppResultResponse;
import com.illumina.basespace.response.GetAppSessionResponse;
import com.illumina.basespace.response.GetCoverageMetadataResponse;
import com.illumina.basespace.response.GetCoverageResponse;
import com.illumina.basespace.response.GetFileRedirectMetaDataResponse;
import com.illumina.basespace.response.GetFileResponse;
import com.illumina.basespace.response.GetFileUploadResponse;
import com.illumina.basespace.response.GetGenomeResponse;
import com.illumina.basespace.response.GetProjectResponse;
import com.illumina.basespace.response.GetPropertyResponse;
import com.illumina.basespace.response.GetPurchaseResponse;
import com.illumina.basespace.response.GetRefundResponse;
import com.illumina.basespace.response.GetRunResponse;
import com.illumina.basespace.response.GetSampleResponse;
import com.illumina.basespace.response.GetUserResponse;
import com.illumina.basespace.response.GetVariantSetResponse;
import com.illumina.basespace.response.ListAppResultsResponse;
import com.illumina.basespace.response.ListAppSessionsResponse;
import com.illumina.basespace.response.ListFilesResponse;
import com.illumina.basespace.response.ListGenomesResponse;
import com.illumina.basespace.response.ListProductsResponse;
import com.illumina.basespace.response.ListProjectsResponse;
import com.illumina.basespace.response.ListPropertiesResponse;
import com.illumina.basespace.response.ListRunsResponse;
import com.illumina.basespace.response.ListSamplesResponse;
import com.illumina.basespace.response.ListVariantsResponse;
import com.illumina.basespace.util.ProgressInputStream;
import com.illumina.basespace.util.TypeHelper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Default implementation of a BaseSpace client
 * 
 * @author bking
 */
class DefaultApiClient implements ApiClient {
	private final Logger logger = Logger.getLogger(DefaultApiClient.class.getPackage().getName());
	private ClientConnectionProvider connectionProvider;
	private Map<String, FileRedirectMetaData> downloadMetaDataCache = new Hashtable<String, FileRedirectMetaData>();

	private boolean cancelAllDownloads;

	/**
	 * Create a BaseSpace session
	 * 
	 * @param config
	 *            the configuration to use to establish the session
	 */
	DefaultApiClient(final ClientConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
		logger.setLevel(Level.ALL);
		cancelAllDownloads = false;
	}

	@Override
	public GetUserResponse getCurrentUser() {
		return getConnectionProvider().getResponse(GetUserResponse.class, "users/current", null, null);
	}

	@Override
	public ListProjectsResponse getProjects(QueryParams params) {
		return getConnectionProvider().getResponse(ListProjectsResponse.class, "users/current/projects", params, null);
	}

	@Override
	public GetProjectResponse getProject(String id) {
		return getConnectionProvider().getResponse(GetProjectResponse.class, "projects/" + id, null, null);
	}

	@Override
	public GetProjectResponse createProject(String projectName) {
		Form form = new Form();
		form.add("name", projectName);
		return getConnectionProvider().postForm(GetProjectResponse.class, "projects", null, form);
	}

	@Override
	public ListSamplesResponse getSamples(ProjectCompact project, QueryParams params) {
		return getConnectionProvider().getResponse(ListSamplesResponse.class, "projects/" + project.getId() + "/samples", params, null);

	}

	@Override
	public ListSamplesResponse getSamples(RunCompact run, QueryParams params) {
		return getConnectionProvider().getResponse(ListSamplesResponse.class, "runs/" + run.getId() + "/samples", params, null);

	}

	@Override
	public GetSampleResponse getSample(String id) {
		return getConnectionProvider().getResponse(GetSampleResponse.class, "samples/" + id, null, null);
	}

	@Override
	public GetAppResultResponse getAppResult(String id) {
		return getConnectionProvider().getResponse(GetAppResultResponse.class, "appresults/" + id, null, null);
	}

	@Override
	public ListAppResultsResponse getAppResults(ProjectCompact project, QueryParams params) {
		return getConnectionProvider().getResponse(ListAppResultsResponse.class, "projects/" + project.getId() + "/appresults", params, null);
	}

	/**
	 * Updated API call to use appsessionS/... instead of appsession/
	 */
	@Override
	public ListAppResultsResponse getAppResults(AppSessionCompact appSession, QueryParams params) {
		return getConnectionProvider().getResponse(ListAppResultsResponse.class, "appsessions/" + appSession.getId() + "/appresults", params, null);
	}

	@Override
	public GetAppSessionResponse getAppSession(String id) {
		return getConnectionProvider().getResponse(GetAppSessionResponse.class, "appsessions/" + id, null, null);
	}

	@Override
	public ListAppSessionsResponse getAppSessions(String appId, String status, QueryParams params) {
		if (params == null)
			params = new QueryParams();
		params.addParam("AppId", appId);
		params.addParam("Status", status);
		return getConnectionProvider().getResponse(ListAppSessionsResponse.class, "users/current/appsessions", params, null);

	}

	@Override
	public ListRunsResponse getRuns(QueryParams params) {
		return getConnectionProvider().getResponse(ListRunsResponse.class, "users/current/runs", params, null);
	}

	@Override
	public GetRunResponse getRun(String id) {
		return getConnectionProvider().getResponse(GetRunResponse.class, "runs/" + id, null, null);
	}

	@Override
	public ListFilesResponse getFiles(SampleCompact sample, FileParams params) {
		return getConnectionProvider().getResponse(ListFilesResponse.class, "samples/" + sample.getId() + "/files", params, null);

	}

	@Override
	public ListFilesResponse getFiles(RunCompact run, FileParams params) {
		return getConnectionProvider().getResponse(ListFilesResponse.class, "runs/" + run.getId() + "/files", params, null);
	}

	@Override
	public ListFilesResponse getFiles(AppResultCompact appresult, FileParams params) {
		return getConnectionProvider().getResponse(ListFilesResponse.class, "appresults/" + appresult.getId() + "/files", params, null);
	}

	@Override
	public GetFileResponse getFile(String id) {
		return getConnectionProvider().getResponse(GetFileResponse.class, "files/" + id, null, null);
	}

	@Override
	public URI getDownloadURI(FileCompact file) {
		WebResource resource = connectionProvider.getClient()
				.resource(UriBuilder.fromUri(connectionProvider.getConfiguration().getApiRootUri()).path(connectionProvider.getConfiguration().getVersion()).build()).path("files").path(file.getId())
				.path("content");
		return resource.getURI();
	}

	@Override
	public InputStream getFileInputStream(FileCompact file) {
		return getFileInputStream(file, 0, file.getSize());
	}

	@Override
	public InputStream getFileInputStream(FileCompact file, long start, long end) {
		try {
			return getInputStreamInternal(file, start, end);
		} catch (RuntimeException t) {
			throw t;
		}
	}

	@Override
	public void cancelDownloads() {
		logger.info("Request to cancel all downloads");
		cancelAllDownloads = true;
	}


	@Override
	public void download(final com.illumina.basespace.entity.File file, java.io.File target, DownloadListener listener) {

		long timeinit = System.currentTimeMillis();
		FileOutputStream fos = null;
		// InputStream in = null;
		GZIPInputStream gzipin = null;
		long progress = 0;
		try {
			if (target.isDirectory()) {
				if (!target.exists() && !target.mkdirs()) {
					throw new IllegalArgumentException("Unable to create local folder " + target.toString());
				}
				String fileName = file.getName();
				if (fileName.endsWith(".gz")) {
					fileName = fileName.substring(0, fileName.length() - 3);
				}
				target = new java.io.File(target, fileName);
			}

			final int CHUNK_SIZE = 4096;
			ProgressInputStream unzipStr = new ProgressInputStream(getFileInputStream(file));
			gzipin = new GZIPInputStream(unzipStr);
			fos = new FileOutputStream(target);

			byte[] outputByte = new byte[CHUNK_SIZE];
			int bytesRead = 0;
			while (!cancelAllDownloads && (bytesRead = gzipin.read(outputByte, 0, CHUNK_SIZE)) != -1) {
				fos.write(outputByte, 0, bytesRead);
				progress = unzipStr.getProgress();
				if (listener != null) {
					DownloadEvent evt = new DownloadEvent(file, target, progress, file.getSize());
					listener.progress(evt);
				}
			}
			fos.close();
			gzipin.close();
			fos = null;
			gzipin = null;
			if (listener != null)
				listener.complete(new DownloadEvent(file, target, progress, file.getSize()));
		} catch (BaseSpaceException bs) {
			throw bs;
		} catch (Throwable t) {
			throw new RuntimeException("Error during file download", t);
		} finally {
			try {
				if (gzipin != null)
					gzipin.close();
			} catch (Throwable t) {
			}
			try {
				if (fos != null)
					fos.close();
			} catch (Throwable t) {
			}
			if (cancelAllDownloads) {
				if (target != null) {
					target.delete();
					if (listener != null)
						listener.canceled(new DownloadEvent(file, target, progress, file.getSize()));
					// reinit cancel value
					cancelAllDownloads = false;
				}
			}
		}
		System.out.println((System.currentTimeMillis() - timeinit) / 1000);

	}

	@Override
	public GetFileRedirectMetaDataResponse getFileRedirectMetaData(FileCompact file) {
		final MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("redirect", "meta");
		Mappable mappable = new Mappable() {
			@Override
			public MultivaluedMap<String, String> toMap() {
				return queryParams;
			}
		};
		return getConnectionProvider().getResponse(GetFileRedirectMetaDataResponse.class, getDownloadURI(file).toString(), mappable, null);
	}

	@Override
	public GetCoverageResponse getCoverage(File file, String chromosome, CoverageParams params) {
		return getConnectionProvider().getResponse(GetCoverageResponse.class, file.getHrefCoverage() + "/" + chromosome, params, null);
	}

	@Override
	public GetCoverageMetadataResponse getCoverageMetaData(File file, String chromosome) {
		return getConnectionProvider().getResponse(GetCoverageMetadataResponse.class, file.getHrefCoverage() + "/" + chromosome + "/meta", null, null);
	}

	@Override
	public GetVariantSetResponse getVariantSet(File file) {
		return getConnectionProvider().getResponse(GetVariantSetResponse.class, "/variantset/" + file.getId(), null, null);
	}

	@Override
	public ListVariantsResponse getVariants(File file, String chromosome, PositionalQueryParams params) {
		params.addParam("Format", "json");
		return getConnectionProvider().getResponse(ListVariantsResponse.class, file.getHrefVariants() + "/variants/" + chromosome, params, null);
	}

	@Override
	public String getVariantRawRecord(FileCompact file, String chromosome, PositionalQueryParams params) {
		params.addParam("Format", "vcf");
		return getConnectionProvider().getStringResponse("variantset/" + file.getId() + "/variants/" + chromosome, params, null);
	}

	private String accessToken;

	protected String getAccessToken() {
		return accessToken;
	}

	protected WebResource getRootApiWebResource() {
		return getClient().resource(UriBuilder.fromUri(connectionProvider.getConfiguration().getApiRootUri()).path(connectionProvider.getConfiguration().getVersion()).build());
	}

	private Client getClient() {
		return connectionProvider.getClient();
	}

	public ClientConnectionProvider getConnectionProvider() {
		return this.connectionProvider;
	}

	@Override
	public GetAppSessionResponse updateAppSession(AppSessionCompact appSession, AppSessionStatus status, String summary) {
		Form form = new Form();
		form.add("status", status.toString());
		if (summary != null)
			form.add("statussummary", summary);
		return getConnectionProvider().postForm(GetAppSessionResponse.class, "appsessions/" + appSession.getId(), null, form);
	}

	@Override
	public GetAppResultResponse createAppResult(ProjectCompact project, String name, String description, URI hrefAppSession, ReferenceCompact[] references) {
		CreateAppResult appResult = new CreateAppResult();
		appResult.setName(name);
		appResult.setDescription(description);
		appResult.setHrefAppSession(hrefAppSession);
		appResult.setReferences(references);
		return getConnectionProvider().postJson(GetAppResultResponse.class, "projects/" + project.getId() + "/appresults", null, appResult);
	}

	@Override
	public ListGenomesResponse getGenomes(QueryParams params) {
		return getConnectionProvider().getResponse(ListGenomesResponse.class, "genomes", params, null);
	}

	@Override
	public GetGenomeResponse getGenome(String id) {
		return getConnectionProvider().getResponse(GetGenomeResponse.class, "genomes/" + id, null, null);

	}

	@Override
	public GetFileResponse startMultipartUpload(AppResultCompact appResult, String fileName, String contentType, String directory) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", contentType);
		Form form = new Form();
		form.add("name", fileName);
		form.add("multipart", String.valueOf(true));
		if (directory != null)
			form.add("directory", directory);
		return getConnectionProvider().postForm(GetFileResponse.class, "appresults/" + appResult.getId() + "/files", headers, form);
	}

	@Override
	public ListProductsResponse getProducts(String[] tags, String[] productIds, QueryParams params) {
		if (params == null)
			params = new QueryParams();
		params.addParam("Tags", tags, ",");
		params.addParam("ProductId", productIds, ",");
		return getConnectionProvider().getResponse(ListProductsResponse.class, connectionProvider.getConfiguration().getStoreRootUri(), "users/current/products", params, null);
	}

	@Override
	public CreatePurchaseResponse createPurchase(AppSessionCompact appSession, ProductCompact[] products) {
		CreatePurchase purchase = new CreatePurchase();
		purchase.setProducts(products);
		purchase.setAppSessionId(appSession.getId());
		return getConnectionProvider().postJson(CreatePurchaseResponse.class, connectionProvider.getConfiguration().getStoreRootUri(), "purchases", null, purchase);
	}

	@Override
	public GetPurchaseResponse getPurchase(String id) throws ResourceNotFoundException, ResourceForbiddenException {
		return getConnectionProvider().getResponse(GetPurchaseResponse.class, connectionProvider.getConfiguration().getStoreRootUri(), "purchases/" + id, null, null);

	}

	@Override
	public GetRefundResponse createRefund(String purchaseId, String refundSecret, String comment) {
		Form form = new Form();
		form.add("RefundSecret", refundSecret);
		form.add("Comment", comment);
		return getConnectionProvider().postForm(GetRefundResponse.class, connectionProvider.getConfiguration().getStoreRootUri(), "purchases/" + purchaseId + "/refund", null, form);
	}

	@Override
	public GetFileUploadResponse uploadFilePart(FileCompact file, int partNumber, String MD5Hash, InputStream part) {
		Map<String, String> headers = null;
		if (MD5Hash != null) {
			headers = new HashMap<String, String>();
			headers.put("Content-MD5", MD5Hash);
		}
		return getConnectionProvider().putFile(GetFileUploadResponse.class, "files/" + file.getId() + "/parts/" + String.valueOf(partNumber), headers, part);
	}

	@Override
	public GetFileUploadResponse uploadFilePart(FileCompact file, int partNumber, String MD5Hash, byte[] part) {
		Map<String, String> headers = null;
		if (MD5Hash != null) {
			headers = new HashMap<String, String>();
			headers.put("Content-MD5", MD5Hash);
		}
		return getConnectionProvider().putFile(GetFileUploadResponse.class, "files/" + file.getId() + "/parts/" + String.valueOf(partNumber), headers, part);
	}

	@Override
	public GetFileResponse setMultipartUploadStatus(FileCompact file, UploadStatus status) {
		Form form = new Form();
		form.add("uploadstatus", status.toString().toLowerCase());
		return getConnectionProvider().postForm(GetFileResponse.class, "files/" + file.getId(), null, form);
	}

	protected InputStream getInputStreamInternal(FileCompact file, long start, long end) {
		try {
			FileRedirectMetaData metaData = getRedirectMetaData(file);
			InputStream in = connectionProvider.getClient().resource(metaData.getHrefContent()).accept(MediaType.APPLICATION_OCTET_STREAM).accept(MediaType.TEXT_HTML)
					.accept(MediaType.APPLICATION_XHTML_XML).header("Range", "bytes=" + start + "-" + end).get(InputStream.class);
			return in;
		} catch (ResourceForbiddenException forbidden) {
			downloadMetaDataCache.remove(file.getId());
			throw forbidden;
		} catch (BaseSpaceException bs) {
			throw bs;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	protected FileRedirectMetaData getRedirectMetaData(FileCompact file) {
		FileRedirectMetaData metaData = downloadMetaDataCache.get(file.getId());
		if (metaData == null || new Date().after(metaData.getExpires())) {
			logger.warning("Redirect metaData for file id#" + file.getId() + "(" + file.getName() + ") does not exist or expired...fetching...");
			metaData = getFileRedirectMetaData(file).get();
			downloadMetaDataCache.put(file.getId(), metaData);
		}
		return metaData;
	}

	protected Mappable merge(Mappable mappable, Map<String, String> more) {
		final MultivaluedMap<String, String> map = new MultivaluedMapImpl();
		if (mappable != null)
			map.putAll(mappable.toMap());
		if (more != null) {
			for (String key : more.keySet()) {
				map.putSingle(key, more.get(key));
			}
		}
		Mappable newMappable = new Mappable() {
			@Override
			public MultivaluedMap<String, String> toMap() {
				return map;
			}
		};
		return newMappable;
	}

	@Override
	public ListPropertiesResponse putProperties(ApiResource resource, Property[] properties) {
		CreatePropertiesHolder props = new CreatePropertiesHolder(properties);
		return getConnectionProvider().postJson(ListPropertiesResponse.class, TypeHelper.INSTANCE.getResourcePath(resource.getClass(), true) + "/" + resource.getId() + "/properties", null, props);

	}

	@Override
	public ListPropertiesResponse getProperties(ApiResource resource) {
		return getConnectionProvider().getResponse(ListPropertiesResponse.class, TypeHelper.INSTANCE.getResourcePath(resource.getClass(), true) + "/" + resource.getId() + "/properties", null, null);
	}

	@Override
	public GetPropertyResponse getProperty(ApiResource resource, String propertyName) {
		return getConnectionProvider().getResponse(GetPropertyResponse.class,
				TypeHelper.INSTANCE.getResourcePath(resource.getClass(), true) + "/" + resource.getId() + "/properties/" + propertyName + "/items", null, null);
	}

	@Override
	public void deleteProperty(ApiResource resource, String propertyName) {
		getConnectionProvider().delete(TypeHelper.INSTANCE.getResourcePath(resource.getClass(), true) + "/" + resource.getId() + "/properties/" + propertyName, null);
	}

	/**
	 * GET: users/current/appsessions?OutputProjects={1}
	 */
	@Override
	public ListAppSessionsResponse getAppSessions(String projectId, QueryParams params) {
		if (params == null)
			params = new QueryParams();
		params.addParam("OutputProjects", projectId);
		return getConnectionProvider().getResponse(ListAppSessionsResponse.class, "users/current/appsessions", params, null);

	}

}