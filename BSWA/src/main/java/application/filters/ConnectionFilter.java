package application.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import logger.Log4JLogger;

import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlet.FilterHolder;

import application.connectionsMonitor.ConnectionsMonitor;

public class ConnectionFilter extends FilterHolder {
	
	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.connectionlogger;
	
	public ConnectionFilter(){
		
		super(new Filter() {
			
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				ConnectionsMonitor.getInstance().monitorRequest(request);
				chain.doFilter(request, response);
			}
			
			@Override
			public void destroy() {
				// TODO Auto-generated method stub
				
			}
		});
		
		LOG.debug("ConnectionFilter instantiated");
	}
}
