package ui;

import client.ClientStatus;

public interface IUIInterface {
	
	public void clientConnectionChanged(boolean isConnectedToServer, Info serverInfo);

	public void clientStatusChanged(ClientStatus status);
	
	public void clearDisplayedInfoChanged();
	
	public void addDisplayedInfoChanged(Info info);

	public void replaceDisplayInfo(Info info);

}
