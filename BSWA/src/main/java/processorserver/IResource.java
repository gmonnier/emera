package processorserver;

import processorserver.util.ClientLocation;

public interface IResource {

	public String getID();
	
	public String getName();
	
	// Retrieve the location of this client
	public ClientLocation getLocation();
}
