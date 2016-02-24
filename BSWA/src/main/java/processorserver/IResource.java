package processorserver;

import com.gmo.network.location.ClientLocation;

public interface IResource {

	public String getID();
	
	public String getName();
	
	// Retrieve the location of this client
	public ClientLocation getLocation();
}
