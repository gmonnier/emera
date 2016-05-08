package com.gmo.network.network_Server;

import java.io.IOException;

public interface INetworkServerInfoListener {

	// Server unable to initialize
	public void initialisationError(String string, IOException ioe);
	
	// Client removed from connected clients list
	public void clientRemoved(ExtendedSocket client);

	// Client added to connected clients list
	public void clientAdded(ExtendedSocket client);

}
