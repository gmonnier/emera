package com.gmo.modelconverters;

import org.apache.logging.log4j.Logger;

import com.gmo.logger.Log4JLogger;
import com.gmo.processorNode.viewmodel.network.ViewDistantResource;
import com.gmo.processorserver.IDistantResource;

public class DistantResourceConverter implements IViewModelConverter<ViewDistantResource, IDistantResource> {

	// log4j logger - Main logger
	private static Logger LOG = Log4JLogger.logger;

	@Override
	public ViewDistantResource buildViewModelObject(IDistantResource input) {
		ViewDistantResource resource = new ViewDistantResource(input.getID(), input.getName(), input.getClientStatus().toString(), input.getLocation());
		return resource;
	}

	@Override
	public IDistantResource buildDataModelObject(ViewDistantResource input) {
		// Should not occurs this way
		LOG.error("buildDataModelObject not supported for ViewDistantResource object");
		throw new UnsupportedOperationException();
	}

}
