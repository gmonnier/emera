package com.gmo.application;

import java.rmi.RemoteException;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.DispatcherType;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import com.gmo.application.connectionsMonitor.ConnectionsMonitor;
import com.gmo.application.filters.ConnectionFilter;
import com.gmo.application.mappers.EOFExceptionMapper;
import com.gmo.application.mappers.WebExceptionMapper;
import com.gmo.basespaceService.interfaces.IBaseSpaceModel;
import com.gmo.commonconfiguration.NetworkTopologyManager;
import com.gmo.configuration.ApplicationContextManager;
import com.gmo.externalinterfaces.nodes.rmiserver.NodeNotificationsRMIServer;
import com.gmo.logger.JavaStyleLogger;
import com.gmo.logger.Log4JLogger;
import com.gmo.results.extractor.AnalysisFileExtractor;
import com.gmo.results.extractor.AnalysisS3Extractor;
import com.gmo.systemUtil.SystemCommand;

import configuration.AWSContextManager;

public class WebServerApp {

	// Application logging.
	public final static boolean LOG4J_LOGGING = true;
	public final static boolean LOG4J_LOGGING_CONSOLE_ON = true;
	public final static boolean JAVA_LOGGING_ON = true;

	// Set up logging properties.
	static {
		// Clear logs directory
		new SystemCommand().removeAllINDirectory("logs");

		// Set up the log4j logger
		Log4JLogger.setup(LOG4J_LOGGING, LOG4J_LOGGING_CONSOLE_ON, "conf/logging", "EmeraWeb");

		JavaStyleLogger.setup(JAVA_LOGGING_ON, "conf/logging");

	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public static void main(String[] args) throws Exception {

		LOG.info("---------------------------------------------");
		LOG.info("--------START Emera Web APPLICATION----------");
		LOG.info("---------------------------------------------");

		logSystemProperties();

		initConfigs();

		initConnectionMonitor();

		// initReportExtraction();

		initRMIServers();

		initJettyServer();

	}

	private static void initRMIServers() {

		try {
			int registryPort = NetworkTopologyManager.getInstance().getConfig().getRmiNetworkConfig().getRmiRegistryParameters().getRmiRegistryPort();
			java.rmi.registry.LocateRegistry.createRegistry(registryPort);
			System.out.println("RMI registry ready.");
		} catch (Exception e) {
			LOG.error("Exception starting RMI registry:", e);
		}

		try {
			new NodeNotificationsRMIServer().initConnection();
		} catch (RemoteException e) {
			LOG.error("Unable to initiate NodeNotificationsRMIServer, exit applictation");
		}
	}

	private static void logSystemProperties() {
		Properties p = System.getProperties();
		Enumeration<Object> keys = p.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			String value = (String) p.get(key);
			LOG.info(key + ": " + value);
		}
	}

	private static void initConfigs() {
		ApplicationContextManager.getInstance().getConfig();
		AWSContextManager.getInstance().getConfig();
	}

	private static void initConnectionMonitor() {
		LOG.debug("Init connections monitor");
		ConnectionsMonitor.getInstance();
	}

	private static void initReportExtraction() {
		LOG.debug("Extract stored analyses results");
		switch (ApplicationContextManager.getInstance().getConfig().getAnalysisResultsLocationType()) {
		case LOCAL: {
			LOG.error("Local File Analysis Extractor defined -> Start extraction");
			new AnalysisFileExtractor();
			break;
		}
		case S_3: {
			LOG.error("Amazon S3 Analysis Extractor defined -> Start extraction");
			new AnalysisS3Extractor();
			break;
		}
		default: {
			LOG.error("Unknown results location type");
		}
		}
	}

	private static void initJettyServer() throws Exception {

		Server jettyServer = new Server(ApplicationContextManager.getInstance().getConfig().getWebServerPort());

		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletHandler.setContextPath("/");

		ServletHolder rootServletHolder = new ServletHolder(ServletContainer.class);
		rootServletHolder.setInitOrder(0);
		LOG.debug("Register providers ");
		rootServletHolder.setInitParameter("jersey.config.server.provider.classnames", "org.glassfish.jersey.media.multipart.MultiPartFeature;" + WebExceptionMapper.class.getCanonicalName() + ";" + EOFExceptionMapper.class.getCanonicalName());
		rootServletHolder.setInitParameter("jersey.config.server.provider.packages", "com.gmo.ws");

		servletHandler.addServlet(rootServletHolder, "/*");
		servletHandler.addFilter(new ConnectionFilter(), "/*", EnumSet.of(DispatcherType.REQUEST));

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "index.html" });
		resource_handler.setResourceBase("src/main/webapp/resources");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler, servletHandler });
		jettyServer.setHandler(handlers);
		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
			jettyServer.destroy();
		}

	}
}