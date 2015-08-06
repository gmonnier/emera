package application;

import java.io.File;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import logger.JavaStyleLogger;
import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import processorserver.ProcessorServerManager;
import reports.extraction.AnalysisExtractor;
import systemUtil.SystemCommand;
import ws.WSAdditionalAnalysis;
import ws.WSAnalysisConfiguration;
import ws.WSAnalysisManagement;
import ws.WSBaseSpace;
import ws.WSConnection;
import ws.WSDataStorage;
import ws.WSNetworkConfiguration;
import application.connectionsMonitor.ConnectionsMonitor;
import application.filters.ConnectionFilter;
import application.mappers.EOFExceptionMapper;
import application.mappers.WebExceptionMapper;
import applicationconfig.ApplicationContextManager;
import applicationconfig.StorageConfigurationManager;
import awsinterfaceManager.AWSInterfaceManager;
import externalInterfaces.basespace.BaseSpaceModelManager;
import externalInterfaces.basespace.rmiserver.BaseSpaceRMIDownloadServer;

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
		Log4JLogger.setup(LOG4J_LOGGING, LOG4J_LOGGING_CONSOLE_ON, "conf/logging", "BSWA");

		JavaStyleLogger.setup(JAVA_LOGGING_ON, "conf/logging");

	}

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	public static void main(String[] args) throws Exception {

		LOG.info("---------------------------------------------");
		LOG.info("--------START BSWebApp APPLICATION----------");
		LOG.info("---------------------------------------------");

		LOG.info("Application root : " + new File("").getAbsolutePath());

		initConfigs();
		
		initConnectionMonitor();

		initRMIServer();

		initReportExtraction();

		initProcessorServer();

		initBaseSpaceModel();

		initAWSModel();

		initJettyServer();

	}

	private static void initConnectionMonitor() {
		LOG.debug("Init connections monitor");
		ConnectionsMonitor.getInstance();
	}

	private static void initAWSModel() {
		LOG.debug("Init Amazon Web Services Interface");
		AWSInterfaceManager.getInstance();
	}

	private static void initReportExtraction() {
		LOG.debug("Extract stored analyses results");
		new AnalysisExtractor();
	}

	private static void initProcessorServer() {
		ProcessorServerManager.getInstance();
	}

	private static void initRMIServer() {
		LOG.info("Rmi Module --> request to start baseSpaceModel rmi interface server");
		new BaseSpaceRMIDownloadServer();
	}

	private static void initBaseSpaceModel() {
		BaseSpaceModelManager.getInstance();
	}

	private static void initConfigs() {
		ApplicationContextManager.getInstance().getConfig();
		StorageConfigurationManager.getInstance().getConfig();
	}

	private static void initJettyServer() throws Exception {

		Server jettyServer = new Server(80);

		ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletHandler.setContextPath("/");

		ServletHolder rootServletHolder = new ServletHolder(ServletContainer.class);
		rootServletHolder.setInitOrder(0);
		LOG.debug("Register providers ");
		rootServletHolder.setInitParameter(
				"jersey.config.server.provider.classnames",
				"org.glassfish.jersey.media.multipart.MultiPartFeature;" + WSBaseSpace.class.getCanonicalName() + ";" + WSDataStorage.class.getCanonicalName() + ";"
						+ WSConnection.class.getCanonicalName() + ";" + WSAnalysisConfiguration.class.getCanonicalName() + ";" + WSAnalysisManagement.class.getCanonicalName() + ";"
						+ WebExceptionMapper.class.getCanonicalName() + ";" + WSNetworkConfiguration.class.getCanonicalName() + ";" + WSAdditionalAnalysis.class.getCanonicalName() + ";"
						+ EOFExceptionMapper.class.getCanonicalName());
		servletHandler.addServlet(rootServletHolder, "/*");
		servletHandler.addFilter(new ConnectionFilter(), "/*", EnumSet.of(DispatcherType.REQUEST));

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[] { "index.html" });
		resource_handler.setResourceBase("src/main/webapp");
		
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