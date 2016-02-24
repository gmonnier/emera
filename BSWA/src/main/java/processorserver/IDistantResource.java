package processorserver;

import com.gmo.client.ClientStatus;
import com.gmo.model.data.DataChunk;

public interface IDistantResource extends IResource {

	public ClientStatus getClientStatus();

	public void setClientStatus(ClientStatus status);

	// Request the distant resource to process the given chunk
	public void sendDataChunk(DataChunk chunk);

	// Send analysis parameters before starting any process operation
	public void sendAnalysisParameters();

	// Send libraries to be used to processing to the distant resources
	public void sendLibraries();

	// Send libraries to be used to processing to the distant resources
	public void requestAssignmentToAnalysis(String id);

	// Request to stop current action
	public void requestStopCurrent();

	// Request the current status of the client processor
	public void requestStatus();

}
