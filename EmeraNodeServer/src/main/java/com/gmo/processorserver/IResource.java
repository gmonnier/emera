package com.gmo.processorserver;

import com.gmo.network.location.ClientLocation;

public interface IResource {

	public String getID();

	public String getName();

	public ClientLocation getLocation();
	
}
