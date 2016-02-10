package com.gmo.application.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.FilterHolder;

import com.gmo.application.connectionsMonitor.ConnectionsMonitor;
import com.gmo.logger.Log4JLogger;

public class ConnectionFilter extends FilterHolder {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.connectionlogger;

	public ConnectionFilter() {

		super(new Filter() {

			public void destroy() {
				// TODO Auto-generated method stub
				
			}

			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				ConnectionsMonitor.getInstance().monitorRequest(request);
				chain.doFilter(request, response);
			}

			public void init(FilterConfig arg0) throws ServletException {
				// TODO Auto-generated method stub
				
			}

			
		});

		LOG.debug("ConnectionFilter instantiated");
	}
}
