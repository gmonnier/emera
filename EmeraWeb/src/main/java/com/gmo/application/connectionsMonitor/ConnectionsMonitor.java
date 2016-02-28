package com.gmo.application.connectionsMonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.network.location.LocationLookup;

public class ConnectionsMonitor {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.connectionlogger;

	private Map<String, ConnectionInfo> connectionsInfo;

	private List<Pattern> noMonitoringPathsPatterns;

	private static class ConnectionsMonitorHolder {
		public final static ConnectionsMonitor instance = new ConnectionsMonitor();
	}

	public static synchronized ConnectionsMonitor getInstance() {
		return ConnectionsMonitorHolder.instance;
	}

	// Constructor
	private ConnectionsMonitor() {
		connectionsInfo = new HashMap<String, ConnectionInfo>();
		noMonitoringPathsPatterns = new ArrayList<Pattern>();
		noMonitoringPathsPatterns.add(Pattern.compile("^(/ws-resources/connection).*$"));
		noMonitoringPathsPatterns.add(Pattern.compile("^(/ws-resources/analysis/appinfos).*$"));
	}

	public synchronized void monitorRequest(ServletRequest request) {
		String distantID = request.getRemoteAddr();
		HttpServletRequest httpreq = (HttpServletRequest) request;

		ConnectionInfo info = connectionsInfo.get(distantID);
		boolean newClient = false;
		if (info == null) {
			info = new ConnectionInfo(System.currentTimeMillis(), distantID, LocationLookup.getLocation(distantID));
			connectionsInfo.put(distantID, info);
			newClient = true;
			LOG.debug("Add new client to the monitoring map : " + info);
		} else {
			info.setLastConnectionTime(System.currentTimeMillis());
		}
		
		boolean nolog = false;
		for (Pattern pathPattern : noMonitoringPathsPatterns) {

			if (pathPattern.matcher(httpreq.getPathInfo()).matches()) {
				nolog = true;
				break;
			}
		}

		if (!nolog || newClient) {
			LOG.debug(info + " accessing resource : " + httpreq.getPathInfo());
		}
	}

}
