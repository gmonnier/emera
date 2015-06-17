package network.network_Server;

public interface INetworkServerInfoListener {

	// Server unable to initialize
	public void initialisationError(String string);
	
	// Client removed from connected clients list
	public void clientRemoved(ExtendedSocket client);

	// Client added to connected clients list
	public void clientAdded(ExtendedSocket client);

}
